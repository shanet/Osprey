from PySide.QtGui import QLabel
from PySide.QtGui import QLineEdit
from PySide.QtGui import QVBoxLayout
from PySide.QtGui import QWidget

from qDataDisplay import QDataDisplay

import constants

class QSmallDataDisplay(QDataDisplay):
  def __init__(self, parent, label, units):
    QDataDisplay.__init__(self, parent)
    self.units = units

    self.label = QLabel('%s:' % label)
    self.data = QLineEdit(constants.DEFAULT_LABEL)
    self.data.setReadOnly(True)

    vbox = QVBoxLayout()
    vbox.addStretch(1)
    vbox.addWidget(self.label)
    vbox.addWidget(self.data)
    vbox.addStretch(1)
    self.setLayout(vbox)

  def setData(self, data):
    # Show floats with two decimals
    text = '%.2f' % data if isinstance(data, float) else str(data)

    self.data.setText('%s%s' % (text, self.units))
