package com.shanet.osprey;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

public class AltitudeFragment extends DatasetFragment {
  private TextView aglDisplay;
  private TextView pressureAltitudeDisplay;
  private TextView gpsAltitudeDisplay;
  private TextView pressureSettingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.altitude_fragment, null);

    aglDisplay = (TextView)layout.findViewById(R.id.agl_display);
    pressureAltitudeDisplay = (TextView)layout.findViewById(R.id.pressure_altitude_display);
    gpsAltitudeDisplay = (TextView)layout.findViewById(R.id.gps_altitude_display);
    pressureSettingDisplay = (TextView)layout.findViewById(R.id.pressure_setting_display);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if the view has not been initalized yet
    if(aglDisplay == null) return;

    // Update the data displays
    Double agl = (Double)dataset.getField("agl");
    Double pressureAltitude = (Double)dataset.getField("pressure_altitude");
    Double gpsAltitude = (Double)dataset.getField("gps_altitude");
    Double pressureSetting = (Double)dataset.getField("pressure_setting");

    updateDisplay(aglDisplay, agl, R.string.default_altitude);
    updateDisplay(pressureAltitudeDisplay, pressureAltitude, R.string.default_altitude);
    updateDisplay(gpsAltitudeDisplay, gpsAltitude, R.string.default_altitude);
    updateDisplay(pressureSettingDisplay, pressureSetting, R.string.default_altitude);
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_altitude);
  }
}
