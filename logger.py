#! /usr/bin/env python3

import curses
import curses.ascii
import curses.textpad
import datetime
import dateutil.parser
import json
import signal
import serial
import threading

ARDUINO_BLOCK_DEVICE = '/dev/ttyUSB0'
BAUD_RATE = 9600

COMMAND_ZERO = 0
COMMAND_SET_PRESSURE = 1

COMMAND_ERR = 0
COMMAND_ACK = 1

class Osprey(object):
  freezeDisplay = False

  def __init__(self, block_device, baud_rate):
    self.serial = serial.Serial(block_device, baud_rate)

  def start(self):
    curses.wrapper(self.run)

  def stop(self):
    curses.endwin()

  def run(self, screen):
    self.screen = screen
    (self.height, self.width) = self.screen.getmaxyx()

    self.drawUI()
    self.sendThread = CursesSendThread(self)
    self.sendThread.start()
    self.read()

  def read(self):
    with open('opsrey.log', 'w') as log:
      self.displayLines(['Waiting for data...'])

      while True:
        rawData = str(self.serial.readline())[2:-5]

        try:
          parsedData = json.loads(rawData)

          log.write("%s\n" % json.dumps(parsedData))

          try:
            timestamp = dateutil.parser.parse(parsedData['iso8601'])
            timestamp = timestamp.strftime('%b %d %Y %H.%M.%S:%f')[:-3]
          except:
            timestamp = parsedData['iso8601']

          lines = []

          lines.append('Time: %s' % timestamp)
          lines.append('Coordinates: %f, %f' % (parsedData['latitude'], parsedData['longitude']))

          lines.append('Roll: %.2f\xb0' % parsedData['roll'])
          lines.append('Pitch: %.2f\xb0' % parsedData['pitch'])
          lines.append('Heading: %.2f\xb0' % parsedData['heading'])

          lines.append('Pressure Altitude: %.2fm' % parsedData['pressure_altitude'])
          lines.append('GPS Altitude: %.2fm' % parsedData['gps_altitude'])
          lines.append('Above Ground Level: %.2fm' % parsedData['agl'])
          lines.append('Pressure Setting: %.2f\" Hg' % parsedData['pressure_setting'])

          lines.append('Temperature: %.2f\xb0C' % parsedData['temp'])
          lines.append('GPS Speed: %.2fkt' % parsedData['speed'])
          lines.append('GPS Quality: %d' % parsedData['gps_quality'])

          lines.append('Previous Command: %s' % parsedData['previous_command'])
          lines.append('Command Status: %s' % ('ACK' if parsedData['command_status'] == COMMAND_ACK else 'ERR'))

          self.displayLines(lines)
        except Exception as exception:
          errString = "Invalid JSON: %s:\n%s" % (exception, rawData)
          self.displayLines([errString])
          log.write(errString)
          continue

  def drawUI(self):
    self.setColors()
    self.screen.clear()
    self.screen.border(0)

    self.makeOutputWindow()
    self.makeInputWindow()
    self.screen.refresh()

  def displayLines(self, lines, minTime=None):
    # Don't display anything while the freeze display flag is set
    if Osprey.freezeDisplay:
      return

    self.outputWindow.erase()

    for index, line in enumerate(lines):
      self.outputWindow.addstr(index, 1, line, 1)

    self.outputWindow.refresh()
    self.inputWindow.refresh()

    # Set the freeze display flag and set an alarm to disable it
    if minTime:
      Osprey.freezeDisplay = True
      signal.alarm(minTime)

  def setColors(self):
    if not curses.has_colors(): return

    curses.init_pair(1, curses.COLOR_GREEN, curses.COLOR_BLACK)
    self.screen.bkgd(curses.color_pair(1))

  def makeOutputWindow(self):
    self.outputWindow = self.screen.subwin(self.height-4, self.width-2, 1, 1)
    self.outputWindow.scrollok(True)

  def makeInputWindow(self):
    self.inputWindow = self.screen.subwin(1, self.width-1, self.height-2, 1)

    self.input = curses.textpad.Textbox(self.inputWindow)
    curses.textpad.rectangle(self.screen, self.height-3, 0, self.height-1, self.width-2)
    self.inputWindow.move(0, 0)

class CursesSendThread(threading.Thread):
  def __init__(self, curses):
    threading.Thread.__init__(self)
    self.daemon = True
    self.curses = curses

  def run(self):
    self.curses.inputWindow.move(0, 0)

    while True:
      message = self.curses.input.edit(self.inputValidator)[:-1]
      self.sendCommandToClient(message.strip())
      self.curses.screen.refresh()
      self.clearChatInput()

  def inputValidator(self, char):
    if char == curses.KEY_HOME:
      return curses.ascii.SOH
    elif char == curses.KEY_END:
      return curses.ascii.ENQ
    elif char == curses.KEY_ENTER or char == ord('\n'):
      return curses.ascii.BEL
    else:
      return char

  def clearChatInput(self):
    self.curses.inputWindow.deleteln()
    self.curses.inputWindow.move(0, 0)
    self.curses.inputWindow.deleteln()

  def sendCommandToClient(self, command):
    try:
      message = self.commandToMessage(command)
      self.curses.serial.write(message.encode())
    except Exception as e:
      self.curses.displayLines([str(e)], minTime=3)
      return

  def commandToMessage(self, input):
    parts = input.split(' ')

    # if len(parts) > 2:
      # raise Exception('Invalid Message: Too many arguments: %d' % len(parts))

    command = parts[0]
    if command == 'zero':
      command_num = COMMAND_ZERO
    elif command == 'pressure':
      command_num = COMMAND_SET_PRESSURE
    else:
      raise Exception('Invalid Message: Unknown command')

    if len(parts) > 1:
      arg = parts[1]
    else:
      arg = ''

    return "%d%s\n" % (command_num, str(arg))

def signalHandler(signalNum, frame):
  if signalNum == int(signal.SIGALRM):
    Osprey.freezeDisplay = False

if __name__ == '__main__':
  signal.signal(signal.SIGALRM, signalHandler)

  global osprey
  osprey = Osprey(ARDUINO_BLOCK_DEVICE, BAUD_RATE)
  osprey.start()
