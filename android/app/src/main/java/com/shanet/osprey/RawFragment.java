package com.shanet.osprey;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class RawFragment extends DatasetFragment {
  private TextView rawDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.raw_fragment, null);

    rawDisplay = (TextView)layout.findViewById(R.id.raw_display);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    rawDisplay.setText(dataset.toString() + "\n");
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_raw);
  }
}
