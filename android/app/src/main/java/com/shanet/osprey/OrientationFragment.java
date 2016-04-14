package com.shanet.osprey;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrientationFragment extends DatasetFragment {
  private ILineDataSet rollDataset;
  private ILineDataSet pitchDataset;
  private ILineDataSet headingDataset;

  private LineChart graph;

  private TextView rollDisplay;
  private TextView pitchDisplay;
  private TextView headingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.orientation_fragment, null);

    rollDisplay = (TextView)layout.findViewById(R.id.roll_display);
    pitchDisplay = (TextView)layout.findViewById(R.id.pitch_display);
    headingDisplay = (TextView)layout.findViewById(R.id.heading_display);

    graph = (LineChart)layout.findViewById(R.id.graph);
    configureGraph(graph);

    rollDataset = createGraphDataSet(android.R.color.holo_red_light);
    pitchDataset = createGraphDataSet(android.R.color.holo_green_light);
    headingDataset = createGraphDataSet(android.R.color.holo_blue_light);

    graph.getData().addDataSet(rollDataset);
    graph.getData().addDataSet(pitchDataset);
    graph.getData().addDataSet(headingDataset);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    // Update the data displays
    Double roll = (Double)dataset.getField("roll");
    Double pitch = (Double)dataset.getField("pitch");
    Double heading = (Double)dataset.getField("heading");
    Integer delta = (Integer)dataset.getField("delta");

    updateDisplay(rollDisplay, roll, R.string.default_orientation, R.string.roll, R.string.degrees);
    updateDisplay(pitchDisplay, pitch, R.string.default_orientation, R.string.pitch, R.string.degrees);
    updateDisplay(headingDisplay, heading, R.string.default_orientation, R.string.heading, R.string.degrees);

    updateGraphDataset(rollDataset, roll.floatValue(), delta.intValue());
    updateGraphDataset(pitchDataset, pitch.floatValue(), -1);
    updateGraphDataset(headingDataset, heading.floatValue(), -1);
  }

  private void updateGraphDataset(ILineDataSet dataset, float y, int x) {
    LineData data = graph.getData();

    if(data == null) {
      return;
    }

    // Prevent multiple x values for each y value in a dataset
    if(x >= 0) {
      data.addXValue(Integer.toString(x));
    }

    dataset.addEntry(new Entry(y, dataset.getEntryCount()));

    graph.notifyDataSetChanged();
    graph.setVisibleXRangeMinimum(dataset.getEntryCount());
    graph.moveViewToX(data.getXValCount());
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_orientation);
  }
}
