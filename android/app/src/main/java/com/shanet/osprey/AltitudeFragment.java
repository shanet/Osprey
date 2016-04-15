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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class AltitudeFragment extends GraphFragment {
  // Default Y axis scale range
  private static final float Y_MAX = 100f;
  private static final float Y_MIN = -10f;

  private ILineDataSet aglDataset;
  private ILineDataSet pressureAltitudeDataset;
  private ILineDataSet gpsAltitudeDataset;

  private LineChart graph;

  private TextView aglDisplay;
  private TextView pressureAltitudeDisplay;
  private TextView gpsAltitudeDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.altitude_fragment, null);

    aglDisplay = (TextView)layout.findViewById(R.id.agl_display);
    pressureAltitudeDisplay = (TextView)layout.findViewById(R.id.pressure_altitude_display);
    gpsAltitudeDisplay = (TextView)layout.findViewById(R.id.gps_altitude_display);

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
    Double agl = (Double)dataset.getField("agl");
    Double pressureAltitude = (Double)dataset.getField("pressure_altitude");
    Double gpsAltitude = (Double)dataset.getField("gps_altitude");
    Integer delta = (Integer)dataset.getField("delta");

    updateDisplay(aglDisplay, agl, R.string.default_altitude, 0, R.string.meters);
    updateDisplay(pressureAltitudeDisplay, pressureAltitude, R.string.default_altitude, R.string.pressure_altitude, R.string.meters);
    updateDisplay(gpsAltitudeDisplay, gpsAltitude, R.string.default_altitude, R.string.gps_altitude, R.string.meters);

    updateGraphDataset(aglDataset, agl.floatValue(), delta.intValue());
    updateGraphDataset(pressureAltitudeDataset, pressureAltitude.floatValue(), -1);
    updateGraphDataset(gpsAltitudeDataset, gpsAltitude.floatValue(), -1);
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_altitude);
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

    // If the Y value is outside of the default scale, switch to autoscaling
    if(y >= Y_MAX || y <= Y_MIN) {
      YAxis yAxis = graph.getAxisLeft();
      yAxis.resetAxisMaxValue();
      yAxis.resetAxisMinValue();

      graph.setAutoScaleMinMaxEnabled(true);
    }
  }

  private void initGraphDatasets() {
    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);
    graph.setData(data);

    aglDataset = createGraphDataSet(android.R.color.holo_red_light);
    pressureAltitudeDataset = createGraphDataSet(android.R.color.holo_green_light);
    gpsAltitudeDataset = createGraphDataSet(android.R.color.holo_blue_light);

    graph.getData().addDataSet(aglDataset);
    graph.getData().addDataSet(pressureAltitudeDataset);
    graph.getData().addDataSet(gpsAltitudeDataset);
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
