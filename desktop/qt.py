import sys
import threading
import time

from PySide.QtCore import Signal
from PySide.QtCore import Slot
from PySide.QtCore import QTimer

from PySide.QtGui import QApplication
from PySide.QtGui import QGridLayout
from PySide.QtGui import QHBoxLayout
from PySide.QtGui import QLabel
from PySide.QtGui import QLineEdit
from PySide.QtGui import QMainWindow
from PySide.QtGui import QMessageBox
from PySide.QtGui import QPushButton
from PySide.QtGui import QWidget
from PySide.QtGui import QVBoxLayout

import constants

from command import Command, InvalidCommandError
from qDataDisplay import QDataDisplay
from radio import Radio

class OspreyQt(QApplication):
  def __init__(self, blockDevice, baudRate):
    QApplication.__init__(self, sys.argv)
    self.radio = Radio(blockDevice, baudRate)

  def start(self):
    # Start a timer to allow for ctrl+c handling
    self.timer = QTimer()
    self.timer.start(500)
    self.timer.timeout.connect(lambda: None)

    self.primaryWindow = QPrimaryWindow(self.radio)
    self.primaryWindow.show()

    # Start the update thread
    RadioThread(self.primaryWindow, self.radio).start()

    self.exec_()

  def stop(self):
    self.quit()

class QPrimaryWindow(QMainWindow):
  updateDisplaysSignal = Signal(dict)

  def __init__(self, radio):
    QMainWindow.__init__(self)
    self.radio = radio
    self.dataDisplays = {}
    self.updateDisplaysSignal.connect(self.updateDisplaysSlot)

    dataDisplaysLayout = self.buildDataDisplayGrid()
    commandInputLayout = self.buildCommandInput()

    centralLayout = QVBoxLayout()
    centralLayout.addLayout(dataDisplaysLayout)
    centralLayout.addStretch(1)
    centralLayout.addLayout(commandInputLayout)

    # Add the completeted layout to the window
    self.centralWidget = QWidget()
    self.centralWidget.setLayout(centralLayout)

    self.setCentralWidget(self.centralWidget)

  def buildDataDisplayGrid(self):
    grid = QGridLayout()

    for index, display in enumerate(constants.DISPLAYS):
      dataDisplay = QDataDisplay(self, display['label'], display['units'] if 'units' in display else '')

      grid.addWidget(dataDisplay, int(index/5), index%5)
      self.dataDisplays[display['field']] = dataDisplay

    grid.setHorizontalSpacing(1)
    grid.setVerticalSpacing(1)

    return grid

  def buildCommandInput(self):
    self.commandInput = QLineEdit()
    self.commandInput.returnPressed.connect(self.sendCommand)

    sendButton = QPushButton('Send')
    sendButton.clicked.connect(self.sendCommand)

    hbox = QHBoxLayout()
    hbox.addWidget(self.commandInput, 1)
    hbox.addWidget(sendButton)

    return hbox

  @Slot(dict)
  def updateDisplaysSlot(self, dataset):
    for key, data in dataset.items():
      if key in self.dataDisplays:
        self.dataDisplays[key].setData(data)

  def sendCommand(self):
    try:
      command = Command(self.commandInput.text())
      self.radio.send(command)
      self.commandInput.clear()
    except InvalidCommandError as exception:
      QMessageBox.warning(self, 'Invalid Command', str(exception))

class RadioThread(threading.Thread):
  def __init__(self, window, radio):
    threading.Thread.__init__(self)
    self.window = window
    self.radio = radio
    self.daemon = True

  def run(self):
    while True:
      try:
        data = self.radio.read()
        self.window.updateDisplaysSignal.emit(data)
      except Exception as exception:
        print(exception)
      finally:
        time.sleep(1)
