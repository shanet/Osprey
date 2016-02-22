from PySide.QtCore import Signal
from PySide.QtCore import Slot

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

from command import Command
from qGraphLayout import QGraphLayout
from qBigDataDisplay import QBigDataDisplay
from qSmallDataDisplay import QSmallDataDisplay

class QPrimaryWindow(QMainWindow):
  updateDatasetSignal = Signal(dict)

  def __init__(self, radio):
    QMainWindow.__init__(self)
    self.radio = radio
    self.dataDisplays = {}
    self.updateDatasetSignal.connect(self.updateDatasetSlot)

    dataDisplaysLayout = self.buildDataDisplays()
    graphs = self.buildGraphs()
    commandInputLayout = self.buildCommandInput()

    centralLayout = QVBoxLayout()
    centralLayout.addLayout(dataDisplaysLayout)
    centralLayout.addLayout(graphs)
    centralLayout.addStretch(1)
    centralLayout.addLayout(commandInputLayout)

    # Add the completeted layout to the window
    self.centralWidget = QWidget()
    self.centralWidget.setLayout(centralLayout)

    self.setCentralWidget(self.centralWidget)

  def buildDataDisplays(self):
    hbox = QHBoxLayout()
    hbox.addStretch(1)

    for widget in self.buildLeftDataDisplays():
      hbox.addWidget(widget)
      hbox.addStretch(1)

    hbox.addLayout(self.buildRightDataDisplays())

    return hbox

  def buildLeftDataDisplays(self):
    widgets = []

    for display in constants.LEFT_DATA_DISPLAYS:
      units = display['units'] if 'units' in display else ''
      widget = QBigDataDisplay(self, display['label'], units)

      self.dataDisplays[display['field']] = widget
      widgets.append(widget)

    return widgets

  def buildRightDataDisplays(self):
    grid = QGridLayout()

    for index, display in enumerate(constants.RIGHT_DATA_DISPLAYS):
      units = display['units'] if 'units' in display else ''
      widget = QSmallDataDisplay(self, display['label'], units)

      self.dataDisplays[display['field']] = widget
      grid.addWidget(widget, int(index / constants.RIGHT_DATA_DISPLAYS_PER_ROW), index % constants.RIGHT_DATA_DISPLAYS_PER_ROW)

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
    self.graphs = QGraphLayout(constants.GRAPHS, self)
    return self.graphs

  @Slot(dict)
  def updateDatasetSlot(self, dataset):
    self.graphs.updateDataset(dataset)
    self.updateDataDisplays(dataset)

  def updateDataDisplays(self, dataset):
    for key, data in dataset.items():
      if key in self.dataDisplays:
        self.dataDisplays[key].setData(data)

  def sendCommand(self):
    try:
      command = Command(self.commandInput.text())
      self.radio.send(command)
      self.commandInput.clear()
    except exceptions.InvalidCommandError as exception:
      QMessageBox.warning(self, 'Invalid Command', str(exception))
