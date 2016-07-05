require 'json'

BUFFER = 10

class Log
  attr_accessor :launches

  def initialize(input_path, output_path)
    @log = File.open input_path, 'r'
    @output_path = output_path
    @points = []

    parse_log
    find_launches
  end

private

  def parse_log
    @points = []

    @log.each do |line|
      begin
        point = JSON.parse line, symbolize_names: true
        @points << point
      rescue JSON::ParserError
        next
      end
    end
  end

  def find_launches
    @launches = []
    start_index = 0

    while start_index
      # Find the first point in the boost phase
      start_index = @points[start_index..-1].index {|point| point[:phase] == Launch::BOOST}
      next unless start_index

      # Find the first point in the landed phase
      end_index = @points[start_index..-1].index {|point| point[:phase] == Launch::LANDED}

      # If an end index wasn't found, go to the end of the list. Otherwise, add the start
      # index to the to the end index so it is not longer relative to the start index
      if !end_index
        end_index = @points.count-1 unless end_index
      else
        end_index += start_index
      end

      # Adjust the indicies to add some buffer data points
      start_index = (start_index - BUFFER >= 0 ? start_index - BUFFER : start_index-1)
      end_index = (end_index + BUFFER < @points.count ? end_index + BUFFER : @points.count-1)

      # Create a new launch for the found indicies
      points_in_launch = @points.slice start_index, end_index - start_index
      @launches << Launch.new(points_in_launch, @output_path)

      # Move to the next pad phase
      start_index = @points[end_index..-1].index {|point| point[:phase] == Launch::PAD}
    end
  end
end
