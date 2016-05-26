package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.widget.EditText;

class DownloadMapDialogFragment extends DialogFragment {
  private DownloadMapDialogListener listener;

  public DownloadMapDialogFragment(DownloadMapDialogListener listener) {
    this.listener = listener;
  }

  public interface DownloadMapDialogListener {
    public void onMapDownloadRequest(String regionName);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final EditText regionNameEdit = new EditText(getActivity());
    regionNameEdit.setHint("Name");

    return new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dialog_title_download_map)
      .setMessage(R.string.download_map_info)
      .setView(regionNameEdit)
      .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          listener.onMapDownloadRequest(regionNameEdit.getText().toString());
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
