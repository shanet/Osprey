from PySide.QtCore import Qt

from PySide.QtGui import QFont
from PySide.QtGui import QLabel
from PySide.QtGui import QLineEdit
from PySide.QtGui import QVBoxLayout
from PySide.QtGui import QWidget

from qDataDisplay import QDataDisplay

import constants

class QBigDataDisplay(QDataDisplay):
  def __init__(self, parent, label, units):
    QDataDisplay.__init__(self, parent)
    self.units = units

    self.buildLabels(label)

    vbox = QVBoxLayout()
    vbox.addStretch(1)
    vbox.addWidget(self.label)
    vbox.addWidget(self.data)
    vbox.addStretch(1)
    self.setLayout(vbox)

  def buildLabels(self, label):
    self.label = QLabel(label)
    self.data = QLabel(constants.DEFAULT_LABEL)

    # Center the text in the labels
    self.label.setAlignment(Qt.AlignCenter)
    self.data.setAlignment(Qt.AlignCenter)

    # Bold the label label
    labelFont = QFont()
    labelFont.setBold(True)

    # Make the data label font obnoxiously large
    dataFont = QFont()
    dataFont.setPixelSize(20)

    self.label.setFont(labelFont)
    self.data.setFont(dataFont)

  def setData(self, data):
    # Show floats with two decimals
    text = '%.2f' % data if isinstance(data, float) else str(data)

    self.data.setText('%s%s' % (text, self.units))
