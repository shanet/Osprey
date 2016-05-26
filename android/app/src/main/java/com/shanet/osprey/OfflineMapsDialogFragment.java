package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.mapbox.mapboxsdk.offline.OfflineRegion;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

class OfflineMapsDialogFragment extends DialogFragment {
  private CharSequence[] regionNames;
  private int regionSelected;
  private OfflineMapsDialogListener listener;
  private OfflineRegion[] regions;

  public OfflineMapsDialogFragment(OfflineMapsDialogListener listener, OfflineRegion[] regions) {
    this.listener = listener;

    // Add all of the region names to a list
    List<String> offlineRegionsNames = new ArrayList<>();

    for(OfflineRegion offlineRegion : regions) {
      offlineRegionsNames.add(getRegionName(offlineRegion));
    }

    this.regions = regions;
    this.regionNames = offlineRegionsNames.toArray(new CharSequence[offlineRegionsNames.size()]);
  }

  public interface OfflineMapsDialogListener {
    public void onOfflineMapSelected(final int regionSelected);
    public void onOfflineMapDeleted();
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Reset the region selected int to 0
    regionSelected = 0;

    // Build a dialog containing the list of regions
    return new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dialog_title_launch_sites)
      .setSingleChoiceItems(regionNames, 0, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          regionSelected = which;
        }
      })
      .setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          listener.onOfflineMapSelected(regionSelected);
        }
      })
      .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // Deleted the selected region
          regions[regionSelected].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
            public void onDelete() {
              listener.onOfflineMapDeleted();
            }

            public void onError(String error) {}
          });
        }
      })
      .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.dismiss();
        }
      })
    .create();
  }

  private String getRegionName(OfflineRegion offlineRegion) {
    // Get the retion name from the offline region metadata
    String regionName;

    try {
      JSONObject jsonObject = new JSONObject(new String(offlineRegion.getMetadata(), MapFragment.JSON_CHARSET));
      regionName = jsonObject.getString(MapFragment.JSON_FIELD_REGION_NAME);
    } catch(Exception e) {
      regionName = String.format("Region %s", offlineRegion.getID());
    }

    return regionName;
  }
}
