import sys
import threading
import time

from PySide.QtCore import QTimer
from PySide.QtGui import QApplication

import exceptions

from qPrimaryWindow import QPrimaryWindow
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
      except exceptions.RadioReceiveError as exception:
        print(str(exception))
      finally:
        time.sleep(1)
