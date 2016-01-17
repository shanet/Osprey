import numpy

from PySide.QtGui import QVBoxLayout
from PySide.QtGui import QWidget

from pyqtgraph import mkPen
from pyqtgraph import PlotDataItem
from pyqtgraph import PlotWidget

import constants

class QGraph(QWidget):
  def __init__(self, config, parent=None, **kwargs):
    QWidget.__init__(self, parent)
    self.startTime = None
    self.numDataPoints = 0

    self.datasets = {}

    for display in config['displays']:
      self.datasets[display['field']] = self.createDatasetForDisplay(display)

    self.graph = PlotWidget(title=config['title'], labels=config['labels'])

    for _, dataset in self.datasets.items():
      self.graph.addItem(dataset['plotData'])

    vbox = QVBoxLayout()
    vbox.addWidget(self.graph)
    self.setLayout(vbox)

  def createDatasetForDisplay(self, display):
    plotData = PlotDataItem()

    if 'color' in display:
      plotData.setPen(mkPen({'color': display['color']}))

    return {
      'plotData': plotData,
      'points': numpy.zeros((constants.NUMPY_ARRAY_SIZE, 2)),
    }

  def updateDataset(self, dataset):
    time = self.getAxisTime(dataset)

    # Skip updating if no time is available
    if not time:
      return

    for field, _ in self.datasets.items():
      self.updatePoints(time, field, dataset)
      self.updateGraphs(field, dataset)

    self.numDataPoints += 1

  def updatePoints(self, time, field, dataset):
    for key, data in dataset.items():
      # Only plot float values
      if field == key and isinstance(data, float):
        self.datasets[field]['points'][self.numDataPoints] = (time, data)
        return


  def getAxisTime(self, dataset):
    # Use the first dataset as the start time
    if not self.startTime and dataset['datetime']:
      self.startTime = dataset['datetime']

    if dataset['datetime']:
      return (dataset['datetime'] - self.startTime).total_seconds()
    else:
      return None

  def updateGraphs(self, field, dataset):
    for data in dataset.items():
      if field in dataset:
        # We don't want to graph the empty values in the points array so only
        # give the plot data the points up to the current number of data points
        points = self.datasets[field]['points']
        self.datasets[field]['plotData'].setData(points[0:self.numDataPoints])
