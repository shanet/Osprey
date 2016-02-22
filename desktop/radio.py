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

      self.transformData(parsedData)
      self.writeLog('%s\n' % rawData)
      return parsedData
    except json.decoder.JSONDecodeError as exception:
      message = 'Invalid JSON: %s:\n%s' % (exception, rawData)
      self.writeLog(message)
      raise exceptions.RadioReceiveError(message)

  def parseTimestamp(self, dataset):
    try:
      dataset['datetime'] = dateutil.parser.parse(dataset['iso8601'])
      dataset['timestamp'] = dataset['datetime'].strftime('%b %d %Y %H.%M.%S:%f')[:-3]
    except:
      dataset['datetime'] = None
      dataset['timestamp'] = dataset['iso8601']

  def transformData(self, dataset):
    self.parseTimestamp(dataset)
    self.addCoordinatesString(dataset)

  def addCoordinatesString(self, dataset):
    dataset['coordinates'] = '%f, %f' % (dataset['latitude'], dataset['longitude'])

  def send(self, command):
    self.serial.write(command.encode())

  def writeLog(self, message):
    self.log.write(message)
    self.log.flush()
