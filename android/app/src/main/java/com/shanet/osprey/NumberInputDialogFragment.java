package com.shanet.osprey;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.text.InputType;
import android.widget.EditText;

class NumberInputDialogFragment extends DialogFragment {
  NumberInputDialogListener listener;
  double number;
  int title;
  int id;

  public NumberInputDialogFragment(NumberInputDialogListener listener, int title, double currentNumber, int id) {
    this.listener = listener;
    this.title = title;
    this.number = currentNumber;
    this.id = id;
  }

  public interface NumberInputDialogListener {
    public void onNumberReceived(double number, int which);
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final EditText input = new EditText(getActivity());
    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    input.setText(Double.toString(number));
    input.selectAll();

    return new AlertDialog.Builder(getActivity())
      .setTitle(title)
      .setView(input)
      .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          number = Double.parseDouble(input.getText().toString());
          dialog.dismiss();
          listener.onNumberReceived(number, id);
        }
      })
      .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      })
      .create();
  }
}
