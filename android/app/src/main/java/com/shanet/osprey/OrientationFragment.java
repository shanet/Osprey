package com.shanet.osprey;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class OrientationFragment extends GraphFragment {
  private static final float Y_MAX = 360f;
  private static final float Y_MIN = -180f;

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
    configureGraph(graph, Y_MAX, Y_MIN);
    initGraphDatasets();

    // Tell Android this fragment has an options menu
    setHasOptionsMenu(true);

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

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_orientation);
  }

  // Graph methods
  // ---------------------------------------------------------------------------------------------------
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

  private void initGraphDatasets() {
    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);
    graph.setData(data);

    rollDataset = createGraphDataSet(android.R.color.holo_red_light);
    pitchDataset = createGraphDataSet(android.R.color.holo_green_light);
    headingDataset = createGraphDataSet(android.R.color.holo_blue_light);

    graph.getData().addDataSet(rollDataset);
    graph.getData().addDataSet(pitchDataset);
    graph.getData().addDataSet(headingDataset);
  }

  private void clearGraph() {
    graph.clear();
    initGraphDatasets();
  }
  // ---------------------------------------------------------------------------------------------------

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.graph_option_menu, menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.clear_graph_option:
        clearGraph();
        return true;
    }

    return false;
  }
  // ---------------------------------------------------------------------------------------------------
}
