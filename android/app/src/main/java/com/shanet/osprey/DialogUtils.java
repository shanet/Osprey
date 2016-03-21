package com.shanet.osprey;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

public abstract class DialogUtils {
  public static void displayErrorDialog(Context context, int title, int message) {
    displayErrorDialog(context, context.getString(title), context.getString(message));
  }

  public static void displayErrorDialog(Context context, String title, String message) {
    new AlertDialog.Builder(context)
    .setTitle(title)
    .setMessage(message)
    .setIcon(R.drawable.error_icon)
    .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {}
    })
    .show();
  }

  public static void displayInfoDialog(Context context, int title, int message) {
    displayInfoDialog(context, context.getString(title), context.getString(message));
  }

  public static void displayInfoDialog(Context context, String title, String message) {
    new AlertDialog.Builder(context)
    .setTitle(title)
    .setMessage(message)
    .setIcon(R.drawable.about_icon)
    .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {}
    })
    .show();
  }
}
