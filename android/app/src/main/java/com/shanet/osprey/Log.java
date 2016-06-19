package com.shanet.osprey;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class Log {
  private Context context;
  private FileOutputStream file;
  private int filename;

  public Log(Context context, int filename) {
    this.context = context;
    this.filename = filename;

    file = null;
  }

  public boolean open() {
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      try {
        File fd = new File(context.getExternalFilesDir(null), context.getString(filename));
        file = new FileOutputStream(fd, true);
      } catch(FileNotFoundException fnfe) {
        return false;
      }
    }

    return true;
  }

  public boolean close() {
    if(file == null) return true;

    try {
      file.close();
    } catch(IOException ioe) {
      return false;
    } finally {
      file = null;
    }

    return true;
  }

  public boolean write(String message) {
    return write(message.getBytes(), false);
  }

  public boolean write(Exception exception) {
    // Write the stack trace to a string instead of stderr
    StringWriter stackTrace = new StringWriter();
    exception.printStackTrace(new PrintWriter(stackTrace));

    return write(stackTrace.toString().getBytes(), true);
  }

  public boolean write(byte[] message) {
    return write(message, false);
  }

  public boolean write(byte[] message, boolean timestamp) {
    if(file == null) return false;

    try {
      // If requested, put the current timestamp in front of the message
      if(timestamp) {
        file.write(String.format("%s: ", getTimestamp()).getBytes());
      }

      file.write(message);
    } catch(IOException ioe) {
      return false;
    }

    return true;
  }

  private String getTimestamp() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    return dateFormat.format(new Date());
  }
}
