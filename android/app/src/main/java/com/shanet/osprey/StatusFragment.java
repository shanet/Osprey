package com.shanet.osprey;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

public class StatusFragment extends DatasetFragment implements NumberInputDialogFragment.NumberInputDialogListener {
  private final static double DEFAULT_PRESSURE_SETTING = 29.92;

  private double currentPressureSetting;

  private TextView batteryDisplay;
  private TextView commandStatusDisplay;
  private TextView previousCommandDisplay;
  private TextView pressureSettingDisplay;
  private TextView loggingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.status_fragment, null);

    currentPressureSetting = DEFAULT_PRESSURE_SETTING;

    batteryDisplay = (TextView)layout.findViewById(R.id.battery_display);
    commandStatusDisplay = (TextView)layout.findViewById(R.id.command_status_display);
    previousCommandDisplay = (TextView)layout.findViewById(R.id.previous_command_display);
    pressureSettingDisplay = (TextView)layout.findViewById(R.id.pressure_setting_display);
    loggingDisplay = (TextView)layout.findViewById(R.id.logging_display);

    ((Button)layout.findViewById(R.id.zero_sensors_button)).setOnClickListener(zeroSensors);
    ((Button)layout.findViewById(R.id.set_pressure_setting_button)).setOnClickListener(setPressureSetting);
    ((Button)layout.findViewById(R.id.enable_logging_button)).setOnClickListener(enableLogging);
    ((Button)layout.findViewById(R.id.disable_logging_button)).setOnClickListener(disableLogging);
    ((Button)layout.findViewById(R.id.start_flight_button)).setOnClickListener(startFlight);
    ((Button)layout.findViewById(R.id.end_flight_button)).setOnClickListener(endFlight);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    Double battery = (Double)dataset.getField("battery");
    String previousCommand = (String)dataset.getField("previous_command");
    currentPressureSetting = ((Double)dataset.getField("pressure_setting")).doubleValue();

    updateDisplay(batteryDisplay, battery, R.string.default_raw);
    updateDisplay(pressureSettingDisplay, currentPressureSetting, R.string.default_raw);
    loggingDisplay.setText(dataset.isLogging() ? "Yes" : "No");

    if("".equals(previousCommand)) {
      previousCommandDisplay.setText("No previous command");
      commandStatusDisplay.setText("No previous command");
    } else {
      previousCommandDisplay.setText(previousCommand);
      commandStatusDisplay.setText(dataset.wasCommandSuccessful() ? "Success" : "Failure");
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_status);
  }

  private View.OnClickListener zeroSensors = new View.OnClickListener() {
    public void onClick(View view) {
      ((Main)getActivity()).write("0\n");
    }
  };

  private View.OnClickListener setPressureSetting = new View.OnClickListener() {
    public void onClick(View view) {
      // Show a dialog to get a new pressure setting from the user
      NumberInputDialogFragment dialog = new NumberInputDialogFragment(StatusFragment.this, R.string.pressure_setting_dialog_title, currentPressureSetting);
      dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
    }
  };

  public void onNumberReceived(double number) {
    // Once the pressure setting is retrived from the user, send the set pressure command
    ((Main)getActivity()).write(String.format("1%s\n", Double.toString(number)));
  }

  private View.OnClickListener enableLogging = new View.OnClickListener() {
    public void onClick(View view) {
      ((Main)getActivity()).write("2\n");
    }
  };

  private View.OnClickListener disableLogging = new View.OnClickListener() {
    public void onClick(View view) {
      ((Main)getActivity()).write("3\n");
    }
  };

  private View.OnClickListener startFlight = new View.OnClickListener() {
    public void onClick(View view) {
      ((Main)getActivity()).write("4\n");
    }
  };

  private View.OnClickListener endFlight = new View.OnClickListener() {
    public void onClick(View view) {
      ((Main)getActivity()).write("5\n");
    }
  };
}
