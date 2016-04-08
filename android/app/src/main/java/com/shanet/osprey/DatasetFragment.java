package com.shanet.osprey;

import android.content.Context;
import android.support.v4.app.Fragment;

import android.widget.TextView;

public abstract class DatasetFragment extends Fragment {
  public abstract void updateDataset(Dataset dataset);
  public abstract String getTitle(Context context);

  protected void updateDisplay(TextView textView, Double value, int defaultString) {
    textView.setText(value != null ? value.toString() : getString(defaultString));
  }
}
