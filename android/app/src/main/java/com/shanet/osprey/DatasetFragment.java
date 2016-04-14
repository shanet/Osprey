package com.shanet.osprey;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public abstract class DatasetFragment extends Fragment {
  public abstract void updateDataset(Dataset dataset);
  public abstract String getTitle(Context context);

  protected void updateDisplay(TextView textView, Object value, int defaultString) {
    updateDisplay(textView, value, defaultString, 0, 0);
  }

  protected void updateDisplay(TextView textView, Object value, int defaultString, int label, int units) {
    if(value == null) {
      textView.setText(getString(defaultString));
    } else if(label != 0 && units != 0) {
      textView.setText(String.format("%s: %s%s", getString(label), value.toString(), getString(units)));
    } else {
      textView.setText(value.toString());
    }
  }

  protected void configureGraph(LineChart graph) {
    graph.setDescription("");
    graph.setNoDataTextDescription(getString(R.string.default_raw));

    graph.setTouchEnabled(true);
    graph.setDragEnabled(true);
    graph.setScaleEnabled(true);
    graph.setPinchZoom(true);

    // Add empty data
    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);
    graph.setData(data);

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
    yAxis.setAxisMaxValue(360f);
    yAxis.setAxisMinValue(-180f);
    yAxis.setDrawGridLines(true);
  }

  protected LineDataSet createGraphDataSet(int color) {
    LineDataSet dataset = new LineDataSet(null, "");

    dataset.setAxisDependency(AxisDependency.LEFT);
    dataset.setDrawCircles(false);
    dataset.setColor(ContextCompat.getColor(getActivity(), color));
    dataset.setLineWidth(2f);
    dataset.setDrawValues(false);

    return dataset;
  }
}
