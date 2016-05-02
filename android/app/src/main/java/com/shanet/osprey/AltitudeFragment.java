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

public class AltitudeFragment extends GraphFragment implements NumberInputDialogFragment.NumberInputDialogListener {
  private static final double DEFAULT_PRESSURE_SETTING = 29.92;

  // Default Y axis scale range
  private static final float Y_MAX = 100f;
  private static final float Y_MIN = -10f;

  private Double currentPressureSetting;

  private ILineDataSet aglDataset;
  private ILineDataSet pressureAltitudeDataset;
  private ILineDataSet gpsAltitudeDataset;

  private LineChart graph;

  private TextView aglDisplay;
  private TextView gpsAltitudeDisplay;
  private TextView pressureAltitudeDisplay;
  private TextView pressureSettingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.altitude_fragment, null);

    currentPressureSetting = new Double(DEFAULT_PRESSURE_SETTING);

    aglDisplay = (TextView)layout.findViewById(R.id.agl_display);
    gpsAltitudeDisplay = (TextView)layout.findViewById(R.id.gps_altitude_display);
    pressureAltitudeDisplay = (TextView)layout.findViewById(R.id.pressure_altitude_display);
    pressureSettingDisplay = (TextView)layout.findViewById(R.id.pressure_setting_display);

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
    Integer delta = (Integer)dataset.getField("delta");
    Double gpsAltitude = (Double)dataset.getField("gps_altitude");
    Double pressureAltitude = (Double)dataset.getField("pressure_altitude");
    currentPressureSetting = (Double)dataset.getField("pressure_setting");

    updateDisplay(aglDisplay, agl, R.string.default_altitude, 0, R.string.meters);
    updateDisplay(gpsAltitudeDisplay, gpsAltitude, R.string.default_altitude, R.string.gps_altitude, R.string.meters);
    updateDisplay(pressureAltitudeDisplay, pressureAltitude, R.string.default_altitude, R.string.pressure_altitude, R.string.meters);
    updateDisplay(pressureSettingDisplay, currentPressureSetting, 0, 0, R.string.inches_mercury);

    updateGraphDataset(aglDataset, agl.floatValue(), delta.intValue());
    updateGraphDataset(gpsAltitudeDataset, gpsAltitude.floatValue(), -1);
    updateGraphDataset(pressureAltitudeDataset, pressureAltitude.floatValue(), -1);
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
    inflater.inflate(R.menu.altitude_option_menu, menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.clear_graph_option:
        clearGraph();
        return true;
      case R.id.set_pressure_setting_option:
        // Show a dialog to get a new pressure setting from the user
        NumberInputDialogFragment dialog = new NumberInputDialogFragment(this, R.string.dialog_title_pressure_setting, currentPressureSetting.floatValue(), 0);
        dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
        return true;
    }

    return false;
  }

  public void onNumberReceived(double number, int which) {
    // Once the pressure setting is retrived from the user, send the set pressure command
    sendCommand(String.format("1%s", Double.toString(number)));
  }
  // ---------------------------------------------------------------------------------------------------
}
