import datetime
import dateutil.parser
import json
import serial

import exceptions

class Radio(object):
  LOG_FILE = 'osprey.log'

  def __init__(self, blockDevice, baudRate):
    self.serial = serial.Serial(blockDevice, baudRate)
    self.log = open(Radio.LOG_FILE, 'w')

  def read(self):
    rawData = str(self.serial.readline())[2:-5]

    try:
      parsedData = json.loads(rawData)
      self.parseTimestamp(parsedData)
      self.addCoordinatesString(parsedData)

      self.log.write('%s\n' % rawData)
      return parsedData
    except json.decoder.JSONDecodeError as exception:
      message = 'Invalid JSON: %s:\n%s' % (exception, rawData)
      self.log.write(message)
      raise exceptions.RadioReceiveError(message)

  def parseTimestamp(self, data):
    try:
      data['datetime'] = dateutil.parser.parse(data['iso8601'])
      data['timestamp'] = data['datetime'].strftime('%b %d %Y %H.%M.%S:%f')[:-3]
    except:
      data['datetime'] = None
      data['timestamp'] = data['iso8601']

  def addCoordinatesString(self, data):
    data['coordinates'] = '%f, %f' % (data['latitude'], data['longitude'])

  def send(self, command):
    self.serial.write(command.encode())
