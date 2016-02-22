import curses
import curses.ascii
import curses.textpad
import signal
import threading

import constants
import exceptions

from command import Command
from radio import Radio

class OspreyNcurses(object):
  freezeDisplay = False

  def __init__(self, blockDevice, baudRate):
    signal.signal(signal.SIGALRM, ncursesSignalHandler)
    self.radio = Radio(blockDevice, baudRate)

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
    self.displayLines(['Waiting for data...'])

    while True:
      try:
        curData = self.radio.read()

        lines = []

        lines.append('Time: %s' % curData['timestamp'])
        lines.append('Coordinates: %s' % (curData['coordinates']))

        lines.append('Roll: %.2f\xb0' % curData['roll'])
        lines.append('Pitch: %.2f\xb0' % curData['pitch'])
        lines.append('Heading: %.2f\xb0' % curData['heading'])

        lines.append('Pressure Altitude: %.2fm' % curData['pressure_altitude'])
        lines.append('GPS Altitude: %.2fm' % curData['gps_altitude'])
        lines.append('Above Ground Level: %.2fm' % curData['agl'])
        lines.append('Pressure Setting: %.2f\" Hg' % curData['pressure_setting'])

        lines.append('Temperature: %.2f\xb0C' % curData['temp'])
        lines.append('GPS Speed: %.2fkt' % curData['speed'])
        lines.append('GPS Quality: %d' % curData['gps_quality'])

        lines.append('Previous Command: %s' % curData['previous_command'])
        lines.append('Command Status: %s' % ('ACK' if curData['command_status'] == constants.COMMAND_ACK else 'ERR'))

        lines.append('Logging: %d' % curData['logging'])
        lines.append('Battery: %.2fV' % curData['battery'])

        self.displayLines(lines)
      except exceptions.RadioReceiveError as exception:
        self.displayLines([str(exception)])
        continue

  def drawUI(self):
    self.setColors()
    self.screen.clear()
    self.screen.border(0)

    self.makeOutputWindow()
    self.makeInputWindow()
    self.makeStatusWindow()
    self.screen.refresh()

  def displayLines(self, lines, minTime=None):
    # Don't display anything while the freeze display flag is set
    if OspreyNcurses.freezeDisplay:
      return

    self.outputWindow.erase()

    for index, line in enumerate(lines):
      self.outputWindow.addstr(index, 1, line, 1)

    self.outputWindow.refresh()
    self.inputWindow.refresh()

    # Set the freeze display flag and set an alarm to disable it
    if minTime:
      OspreyNcurses.freezeDisplay = True
      signal.alarm(minTime)

  def setColors(self):
    if not curses.has_colors(): return

    curses.init_pair(1, curses.COLOR_GREEN, curses.COLOR_BLACK)
    self.screen.bkgd(curses.color_pair(1))

  def makeOutputWindow(self):
    self.outputWindow = self.screen.subwin(self.height-4, self.width-2, 1, 1)
    self.outputWindow.scrollok(True)

  def makeInputWindow(self):
    self.inputWindow = self.screen.subwin(1, self.width-26, self.height-2, 1)

    self.input = curses.textpad.Textbox(self.inputWindow, insert_mode=True)
    curses.textpad.rectangle(self.screen, self.height-3, 0, self.height-1, self.width-25)
    self.inputWindow.move(0, 0)

  def makeStatusWindow(self):
    status = 'Osprey Debug Console'

    self.statusWindow = self.screen.subwin(self.height-3, self.width-24)
    self.statusWindow.border(0)
    self.statusWindow.addstr(1, 2, status)

class CursesSendThread(threading.Thread):
  def __init__(self, curses):
    threading.Thread.__init__(self)
    self.daemon = True
    self.curses = curses

  def run(self):
    self.curses.inputWindow.move(0, 0)

    while True:
      message = self.curses.input.edit(self.inputValidator)[:-1]
      self.sendCommandToClient(message)
      self.curses.screen.refresh()
      self.clearCommandInput()

  def inputValidator(self, char):
    if char == curses.KEY_HOME:
      return curses.ascii.SOH
    elif char == curses.KEY_END:
      return curses.ascii.ENQ
    elif char == curses.KEY_ENTER or char == ord('\n'):
      return curses.ascii.BEL
    elif char == curses.ascii.DEL:
      return curses.ascii.BS
    else:
      return char

  def clearCommandInput(self):
    self.curses.inputWindow.deleteln()
    self.curses.inputWindow.move(0, 0)
    self.curses.inputWindow.deleteln()

  def sendCommandToClient(self, input):
    try:
      self.curses.radio.send(Command(input))
    except exceptions.InvalidCommandError as exception:
      self.curses.displayLines([str(exception)], minTime=3)

def ncursesSignalHandler(signalNum, frame):
  if signalNum == int(signal.SIGALRM):
    OspreyNcurses.freezeDisplay = False
