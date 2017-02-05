require 'erb'
require 'fileutils'

class Launch
  PAD = 0
  BOOST = 1
  COAST = 2
  DROGUE = 3
  MAIN = 4
  LANDED = 5

  APOGEE_CAUSES = ['No apogee detected', 'Altitude', 'Countdown', 'Safety countdown', 'Free fall', 'Manual']

  attr_accessor :title

  def initialize(points, output_path)
    @points = points
    @output_path = '%s/launch_report_%s' % [output_path, first_valid_timestamp.iso8601]
  end

  def render
    if File.exist? @output_path
      puts 'Launch report already exists at %s. Skipping.' % @output_path
      return
    end

    @title = 'Launch report for %s' % first_valid_timestamp.getlocal.strftime('%b %e, %Y, %H:%M:%S %Z')

    set_stats
    create_output_directory
    copy_assets_to_output_directory

    File.open('%s/report.html' % @output_path, 'w') do |file|
      template = ERB.new File.read('templates/report_template.html.erb')
      file.write template.result(binding)
    end

    File.open('%s/assets/data.js' % @output_path, 'w') do |file|
      template = ERB.new File.read('templates/data.js.erb')
      file.write template.result(binding)
    end
  end

  def debug
    @points.each do |point|
      # Put debug stuff here...
      puts '%f,%f,%f,%d' % [point[:agl], point[:latitude], point[:longitude], point[:delta]]
    end
  end

private

  def set_stats
    @actual_apogee = @points.max_by {|point| point[:agl]}[:agl]
    @calculated_apogee = apogee[:agl]
    @apogee_cause = APOGEE_CAUSES[apogee[:apogee_cause]]

    @main_chute_altitude = @points.first[:main_alt]
    @main_chute_actual = main[:agl]
    @average_temperature = @points.inject(0) {|sum, point| sum + point[:temp]} / @points.count

    @flight_start = first_valid_timestamp
    @flight_end = last_valid_timestamp
    @launch_time = Time.parse(@points.find {|point| point[:phase] == BOOST}[:iso8601])

    @flight_time = @flight_end - @flight_start
    @flight_time_minutes = (@flight_time / 60).floor
    @flight_time_seconds = @flight_time - (@flight_time / 60).floor * 60

    @ascent_time = Time.parse(apogee[:iso8601]) - @flight_start
    @ascent_rate = @actual_apogee / @ascent_time

    @descent_time = @flight_end - Time.parse(apogee[:iso8601])
    @descent_time_minutes = (@descent_time / 60).floor
    @descent_time_seconds = @descent_time - (@descent_time / 60).floor * 60
    @descent_rate_drogue = (@actual_apogee - @main_chute_actual) / (Time.parse(main[:iso8601]) - Time.parse(apogee[:iso8601]))
    @descent_rate_main = @main_chute_actual / (@flight_end - Time.parse(main[:iso8601]))

    @index_pad = phase_index PAD
    @index_boost = phase_index BOOST
    @index_coast = phase_index COAST
    @index_drogue = phase_index DROGUE
    @index_main = phase_index MAIN
    @index_landed = phase_index LANDED
  end

  def create_output_directory
    Dir.mkdir @output_path
  end

  def copy_assets_to_output_directory
    FileUtils.cp_r 'assets/', @output_path
  end

  def first_valid_timestamp
    return valid_timestamp :each
  end

  def last_valid_timestamp
    return valid_timestamp :reverse_each
  end

  def valid_timestamp(method)
    @points.send(method) do |point|
      begin
        return Time.parse point[:iso8601]
      rescue ArgumentError
        next
      end
    end

    return nil
  end

  def apogee
    return @points.find {|point| point[:phase] == DROGUE} || {}
  end

  def main
    return @points.find {|point| point[:phase] == MAIN} || {agl: 0.0, iso8601: Time.now.to_s}
  end

  def phase_index(phase)
    return @points.find_index {|point| point[:phase] == phase} || -1
  end
end

class Float
  M_TO_FT = 3.28084

  def feet
    return self * M_TO_FT
  end
end
