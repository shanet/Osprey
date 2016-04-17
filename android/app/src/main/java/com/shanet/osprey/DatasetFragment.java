package com.shanet.osprey;

import android.content.Context;
import android.support.v4.app.Fragment;

import android.widget.TextView;

public abstract class DatasetFragment extends Fragment {
  private static final double FEET_TO_METERS = 3.2808399;

  public abstract void updateDataset(Dataset dataset);
  public abstract String getTitle(Context context);

  protected void updateDisplay(TextView textView, Object value, int defaultString) {
    updateDisplay(textView, value, defaultString, 0, 0);
  }

  protected void updateDisplay(TextView textView, Object value, int defaultString, int label, int units) {
    String text;

    if(value == null) {
      text = getString(defaultString);
    } else if(label != 0 && units != 0) {
      text = String.format("%s: %s%s", getString(label), value.toString(), getString(units));
    } else if(label != 0) {
      text = String.format("%s: %s", getString(label), value.toString());
    } else if(units != 0) {
      text = String.format("%s%s", value.toString(), getString(units));
    } else {
      text = value.toString();
    }

    textView.setText(text);
  }

  protected void sendCommand(String command) {
    ((Main)getActivity()).write(command + "\n");
  }

  protected int ftToM(int feet) {
    return (int)(feet * FEET_TO_METERS);
  }

  protected int mToFt(int meters) {
    return (int)(meters / FEET_TO_METERS);
  }

  protected Integer mToFt(Integer meters) {
    return new Integer(mToFt(meters.intValue()));
  }
}
