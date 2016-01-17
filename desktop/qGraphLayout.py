from PySide.QtGui import QGridLayout

from qGraph import QGraph

class QGraphLayout(QGridLayout):
  def __init__(self, graphs, parent=None):
    QGridLayout.__init__(self, parent)
    self.startTime = None
    self.graphs = []

    for index, (_, graph) in enumerate(graphs.items()):
      print(graph)
      graph = QGraph(graph, parent)

      self.graphs.append(graph)
      self.addWidget(graph, int(index/2), index%2)

  def updateDataset(self, dataset):
    for graph in self.graphs:
      graph.updateDataset(dataset)
