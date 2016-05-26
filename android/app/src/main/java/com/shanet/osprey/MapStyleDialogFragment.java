package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.mapbox.mapboxsdk.constants.Style;

class MapStyleDialogFragment extends DialogFragment {
  private static final String[] MAP_STYLES = {
    Style.DARK,
    Style.EMERALD,
    Style.LIGHT,
    "mapbox://styles/mapbox/outdoors-v9",
    Style.MAPBOX_STREETS,
    Style.SATELLITE,
    Style.SATELLITE_STREETS,
  };

  private MapStyleDialogListener listener;
  private int currentStyle;

  public MapStyleDialogFragment(MapStyleDialogListener listener, String currentStyle) {
    this.listener = listener;

    // We want the index of the current style, not the string
    for(int i=0; i<MAP_STYLES.length; i++) {
      if(MAP_STYLES[i].equals(currentStyle)) {
        this.currentStyle = i;
        break;
      }
    }
  }

  public interface MapStyleDialogListener {
    public void onMapStyleChanged(String style);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dialog_title_map_style)

      .setSingleChoiceItems(R.array.map_styles, currentStyle, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          // When selected, update the style, dismiss the dialog, and fire the style changed callback
          currentStyle = which;
          dialog.dismiss();
          listener.onMapStyleChanged(MAP_STYLES[which]);
        }
       })
      .create();
  }
}
