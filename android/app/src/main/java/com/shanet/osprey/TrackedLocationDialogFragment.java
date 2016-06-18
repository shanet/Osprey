package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;

class TrackedLocationDialogFragment extends DialogFragment {
  private TrackedLocationDialogListener listener;
  private Double latitude;
  private Double longitude;

  public TrackedLocationDialogFragment(TrackedLocationDialogListener listener, Double latitude, Double longitude) {
    this.listener = listener;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public interface TrackedLocationDialogListener {
    public void onTrackedLocationChanged(float latitude, float longitude);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View layout = getActivity().getLayoutInflater().inflate(R.layout.tracked_location_dialog, null);

    final EditText latitudeEdit = (EditText)layout.findViewById(R.id.latitude_edit);
    final EditText longitudeEdit = (EditText)layout.findViewById(R.id.longitude_edit);

    if(latitude != null && longitude != null) {
      latitudeEdit.setText(String.format("%f", latitude.floatValue()));
      longitudeEdit.setText(String.format("%f", longitude.floatValue()));
    }

    return new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dialog_title_tracked_location)
      .setMessage(R.string.tracked_location_info)
      .setView(layout)
      .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          float latitude = new Double(latitudeEdit.getText().toString()).floatValue();
          float longitude = new Double(longitudeEdit.getText().toString()).floatValue();

          dialog.dismiss();
          listener.onTrackedLocationChanged(latitude, longitude);
        }
      })
      .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      })
      .create();
  }
}
