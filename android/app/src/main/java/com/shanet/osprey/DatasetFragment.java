package com.shanet.osprey;

import android.content.Context;
import android.support.v4.app.Fragment;

import android.widget.TextView;

public abstract class DatasetFragment extends Fragment {
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
}
