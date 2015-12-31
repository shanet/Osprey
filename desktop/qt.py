import sys

from PySide.QtCore import QTimer

from PySide.QtGui import QApplication
from PySide.QtGui import QLabel
from PySide.QtGui import QMainWindow
from PySide.QtGui import QVBoxLayout
from PySide.QtGui import QWidget

class OspreyQt(QApplication):
  def __init__(self, blockDevice, baudRate):
    QApplication.__init__(self, sys.argv)

  def start(self):
    # Start a timer to allow for ctrl+c handling
    self.timer = QTimer()
    self.timer.start(500)
    self.timer.timeout.connect(lambda: None)

    self.chatWindow = QPrimaryWindow()
    self.chatWindow.show()

    self.exec_()

  def stop(self):
    self.quit()

class QPrimaryWindow(QMainWindow):
  def __init__(self):
    QMainWindow.__init__(self)

    vbox = QVBoxLayout()
    vbox.addWidget(QLabel('Hello, World!'))

    # Add the completeted layout to the window
    self.centralWidget = QWidget()
    self.centralWidget.setLayout(vbox)
    self.setCentralWidget(self.centralWidget)
