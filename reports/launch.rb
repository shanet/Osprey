require 'erb'
require 'fileutils'

class Launch
  attr_accessor :title

  def initialize(points, output_path)
    @points = points
    @output_path = '%s/launch_report_%s' % [output_path, '']#Time.now] # TODO: use the time in the putput path
    @title = 'Launch report for %s' % [points.first[:iso8601]]
  end

  def render
    @actual_apogee = @points.max_by {|point| point[:agl]}[:agl]
    @calculated_apogee = @points.find {|point| point[:phase] == 3}[:agl]
    @apogee_cause = @points.find {|point| point[:phase] == 3}[:apogee_cause]
    @average_temperature = @points.inject(0) {|sum, point| sum + point[:temp]} / @points.count
    @main_chute_altitude = @points.first[:main_alt]

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

private

  def create_output_directory
    # TODO: remove this obviously
    FileUtils.rm_rf @output_path

    Dir.mkdir @output_path
  end

  def copy_assets_to_output_directory
    FileUtils.cp_r 'assets/', @output_path
  end
end
