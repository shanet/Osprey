import datetime
import dateutil.parser
import json
import serial

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

      self.log.write("%s\n" % json.dumps(parsedData))
      return parsedData
    except Exception as exception:
      message = 'Invalid JSON: %s:\n%s' % (exception, rawData)
      self.log.write(message)
      raise Exception(message)

  def parseTimestamp(self, data):
    try:
      data['timestamp'] = dateutil.parser.parse(data['iso8601'])
      data['timestamp'] = data['timestamp'].strftime('%b %d %Y %H.%M.%S:%f')[:-3]
    except:
      data['timestamp'] = data['iso8601']
