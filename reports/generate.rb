#! /usr/bin/env ruby

require_relative 'launch'
require_relative 'log'

def main
  case ARGV.count
    when 0
      puts('No input log file specified')
      exit
    when 1
      puts('No output directory specified')
      exit
  end

  log = Log.new ARGV[0], ARGV[1]

  log.launches.each do |launch|
    launch.render
  end
end

def help
  # TODO
end

main()
