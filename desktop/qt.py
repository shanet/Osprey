import numpy
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

from pyqtgraph import mkPen
from pyqtgraph import PlotDataItem
from pyqtgraph import PlotWidget

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
  updateDatasetSignal = Signal(dict)

  def __init__(self, radio):
    QMainWindow.__init__(self)
    self.radio = radio
    self.numDataPoints = 0
    self.dataDisplays = {}
    self.updateDatasetSignal.connect(self.updateDatasetSlot)
    self.createDataBuffers()

    dataDisplaysLayout = self.buildDataDisplayGrid()
    graphsLayout = self.buildGraphs()
    commandInputLayout = self.buildCommandInput()

    centralLayout = QVBoxLayout()
    centralLayout.addLayout(dataDisplaysLayout)
    centralLayout.addLayout(graphsLayout)
    centralLayout.addStretch(1)
    centralLayout.addLayout(commandInputLayout)

    # Add the completeted layout to the window
    self.centralWidget = QWidget()
    self.centralWidget.setLayout(centralLayout)

    self.setCentralWidget(self.centralWidget)

  def createDataBuffers(self):
    self.dataBuffers = {}

    for display in constants.DISPLAYS:
      plotData = PlotDataItem()

      if 'color' in display:
        plotData.setPen(mkPen({'color': display['color']}))

      self.dataBuffers[display['field']] = {
        'plotData': plotData,
        'points': numpy.zeros((1000, 2)),
      }

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

  def buildGraphs(self):
    vbox = QVBoxLayout()

    accelerometerGraph = PlotWidget(title='Orientation', labels={'left': ('Degrees', 'degrees'), 'bottom': ('Time', 'seconds')})
    accelerometerGraph.addItem(self.dataBuffers['roll']['plotData'])
    accelerometerGraph.addItem(self.dataBuffers['pitch']['plotData'])
    accelerometerGraph.addItem(self.dataBuffers['heading']['plotData'])

    vbox.addWidget(accelerometerGraph)

    return vbox

  @Slot(dict)
  def updateDatasetSlot(self, dataset):
    self.updateDataBuffers(dataset)
    self.updateDataDisplays(dataset)
    self.updateGraphs(dataset)

  def updateDataBuffers(self, dataset):
    for key, data in dataset.items():
      if key in self.dataBuffers and isinstance(data, float):
        self.dataBuffers[key]['points'][self.numDataPoints] = (self.numDataPoints, data)

    self.numDataPoints += 1

  def updateDataDisplays(self, dataset):
    for key, data in dataset.items():
      if key in self.dataDisplays:
        self.dataDisplays[key].setData(data)

  def updateGraphs(self, dataset):
    for key, data in dataset.items():
      if key in self.dataBuffers:
        points = self.dataBuffers[key]['points']
        self.dataBuffers[key]['plotData'].setData(points[0:self.numDataPoints])

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
        self.window.updateDatasetSignal.emit(data)
      except Exception as exception:
        print(exception)
      finally:
        time.sleep(1)
