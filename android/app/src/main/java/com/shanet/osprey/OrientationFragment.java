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

public class OrientationFragment extends DatasetFragment {
  private TextView rollDisplay;
  private TextView pitchDisplay;
  private TextView headingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.orientation_fragment, null);

    rollDisplay = (TextView)layout.findViewById(R.id.roll_display);
    pitchDisplay = (TextView)layout.findViewById(R.id.pitch_display);
    headingDisplay = (TextView)layout.findViewById(R.id.heading_display);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if the view has not been initalized yet
    if(rollDisplay == null) return;

    // Update the data displays
    Double roll = (Double)dataset.getField("roll");
    Double pitch = (Double)dataset.getField("pitch");
    Double heading = (Double)dataset.getField("heading");

    updateDisplay(rollDisplay, roll, R.string.default_orientation);
    updateDisplay(pitchDisplay, pitch, R.string.default_orientation);
    updateDisplay(headingDisplay, heading, R.string.default_orientation);
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_orientation);
  }
}
