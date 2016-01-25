from PySide.QtGui import QLabel
from PySide.QtGui import QLineEdit
from PySide.QtGui import QVBoxLayout
from PySide.QtGui import QWidget

import constants

class QDataDisplay(QWidget):
  def __init__(self, parent):
    QWidget.__init__(self, parent)
