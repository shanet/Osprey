package com.shanet.osprey;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.LineDataSet;

public abstract class GraphFragment extends DatasetFragment {
  public abstract void clearGraph();

  protected void configureGraph(LineChart graph, float yMax, float yMin) {
    graph.setDescription("");
    graph.setNoDataTextDescription(getString(R.string.default_raw));

    graph.setTouchEnabled(true);
    graph.setDragEnabled(true);
    graph.setScaleEnabled(true);
    graph.setPinchZoom(true);

    // Disable some stuff we don't need
    graph.getLegend().setEnabled(false);
    graph.getAxisRight().setEnabled(false);

    // Configure the axes
    XAxis xAxis = graph.getXAxis();
    xAxis.setTextColor(Color.WHITE);
    xAxis.setDrawGridLines(true);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setAvoidFirstLastClipping(true);
    xAxis.setSpaceBetweenLabels(5);
    xAxis.setEnabled(true);

    YAxis yAxis = graph.getAxisLeft();
    yAxis.setTextColor(Color.WHITE);
    yAxis.setAxisMaxValue(yMax);
    yAxis.setAxisMinValue(yMin);
    yAxis.setDrawGridLines(true);
  }

  protected LineDataSet createGraphDataSet(int color) {
    LineDataSet dataset = new LineDataSet(null, "");

    dataset.setAxisDependency(AxisDependency.LEFT);
    dataset.setLineWidth(2f);
    dataset.setDrawCircles(false);
    dataset.setDrawValues(false);

    if(isAdded()) {
      dataset.setColor(ContextCompat.getColor(getActivity(), color));
    }

    return dataset;
  }
}
