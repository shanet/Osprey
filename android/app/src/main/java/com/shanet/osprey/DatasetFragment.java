package com.shanet.osprey;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class DatasetFragment extends Fragment {
  public abstract void updateDataset(Dataset dataset);
  public abstract String getTitle(Context context);
}
