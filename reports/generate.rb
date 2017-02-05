#! /usr/bin/env ruby

require 'time'

require_relative 'launch'
require_relative 'log'

def main
  case ARGV.count
    when 0
      puts('No input log file specified')
      help
      exit
    when 1
      puts('No output directory specified')
      help
      exit
  end

  log = Log.new ARGV[0], ARGV[1]

  if log.launches.any?
    log.launches.each do |launch|
      launch.render
      # launch.debug
    end
  else
    puts 'No launches detected in log'
  end
end

def help
  puts 'Usage: %s [input log] [output directory]' % $PROGRAM_NAME
end

main
