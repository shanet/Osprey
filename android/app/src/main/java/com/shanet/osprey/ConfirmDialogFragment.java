package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.widget.TextView;

class ConfirmDialogFragment extends DialogFragment {
  ConfirmDialogListener listener;
  int title;
  int id;

  public ConfirmDialogFragment(ConfirmDialogListener listener, int title, int id) {
    this.listener = listener;
    this.title = title;
    this.id = id;
  }

  public interface ConfirmDialogListener {
    public void onConfirmation(int which);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
      .setTitle(title)
      .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          listener.onConfirmation(id);
        }
      })
      .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      })
      .create();
  }
}
