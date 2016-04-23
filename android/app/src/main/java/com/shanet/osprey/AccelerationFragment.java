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

public class AccelerationFragment extends GraphFragment {
  private static final float Y_MAX = 3f;
  private static final float Y_MIN = -1f;

  private ILineDataSet accelerationDataset;

  private LineChart graph;

  private TextView accelerationDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.acceleration_fragment, null);

    accelerationDisplay = (TextView)layout.findViewById(R.id.acceleration_display);

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
    Double acceleration = (Double)dataset.getField("acceleration");
    Integer delta = (Integer)dataset.getField("delta");

    updateDisplay(accelerationDisplay, acceleration, R.string.default_acceleration, 0, R.string.g);

    updateGraphDataset(accelerationDataset, acceleration.floatValue(), delta.intValue());
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_acceleration);
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

    accelerationDataset = createGraphDataSet(android.R.color.holo_red_light);
    graph.getData().addDataSet(accelerationDataset);
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
