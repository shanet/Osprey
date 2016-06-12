require 'json'

EPSILON = 5
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

    start_index = -1
    end_index = -1

    # Find each launch in the log by looking for altitude increases
    @points.each_with_index do |point, index|
      # If the altitude is greater than the epsilon value, we found a new launch
      if start_index == -1 && point[:agl] > EPSILON
        start_index = (index - BUFFER > 0 ? index - BUFFER : index)
      elsif start_index != -1 && point[:agl] < EPSILON
        # If we're already in a launch and the altitude is less than the epsilon value again, we're at the end
        # TODO: This assumes the landing altitude is very similar to the launch altitude

        end_index = (index + BUFFER < @points.count ? index + BUFFER : index)

        points_in_launch = @points.slice start_index, end_index - start_index
        @launches << Launch.new(points_in_launch, @output_path)

        start_index = -1
      end
    end
  end
end
