package com.shanet.osprey;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

public class StatusFragment extends DatasetFragment {
  private TextView batteryDisplay;
  private TextView phaseDisplay;
  private TextView previousCommandDisplay;
  private TextView loggingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.status_fragment, null);

    batteryDisplay = (TextView)layout.findViewById(R.id.battery_display);
    phaseDisplay = (TextView)layout.findViewById(R.id.phase_display);
    previousCommandDisplay = (TextView)layout.findViewById(R.id.previous_command_display);
    loggingDisplay = (TextView)layout.findViewById(R.id.logging_display);

    ((Button)layout.findViewById(R.id.start_flight_button)).setOnClickListener(startFlight);
    ((Button)layout.findViewById(R.id.end_flight_button)).setOnClickListener(endFlight);

    // Tell Android this fragment has an options menu
    setHasOptionsMenu(true);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    Integer phase = (Integer)dataset.getField("phase");
    Double battery = (Double)dataset.getField("battery");
    String previousCommand = (String)dataset.getField("previous_command");

    String phaseString = getResources().obtainTypedArray(R.array.phases).getString(phase.intValue());

    updateDisplay(phaseDisplay, phaseString, R.string.default_raw);
    updateDisplay(batteryDisplay, battery, R.string.default_raw, R.string.battery);
    updateDisplay(loggingDisplay, (dataset.isLogging() ? "Yes" : "No"), 0, R.string.logging);

    if("".equals(previousCommand)) {
      updateDisplay(previousCommandDisplay, "None", 0, R.string.previous_command);
    } else {
      String command = String.format("%s (%s)", previousCommand, (dataset.wasCommandSuccessful() ? "Success" : "Failure"));
      updateDisplay(previousCommandDisplay, previousCommand, R.string.no_previous_command, R.string.previous_command);
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_status);
  }

  // On-click menu methods
  // ---------------------------------------------------------------------------------------------------
  private View.OnClickListener startFlight = new View.OnClickListener() {
    public void onClick(View view) {
      sendCommand("4");
    }
  };

  private View.OnClickListener endFlight = new View.OnClickListener() {
    public void onClick(View view) {
      sendCommand("5");
    }
  };
  // ---------------------------------------------------------------------------------------------------

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.status_option_menu, menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.zero_sensors_option:
        sendCommand("0");
        return true;
      case R.id.enable_logging_option:
        sendCommand("2");
        return true;
      case R.id.disable_logging_option:
        sendCommand("3");
        return true;
    }

    return false;
  }
  // ---------------------------------------------------------------------------------------------------
}
