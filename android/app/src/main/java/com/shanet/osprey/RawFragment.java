package com.shanet.osprey;

import android.content.Context;

import android.os.Bundle;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.lang.InterruptedException;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.StringBuilder;

public class RawFragment extends DatasetFragment implements Runnable {
  private static final int CHARS_PER_LINE = 100;
  private static final int NUM_LINES = 100;
  private static final String LINE_SEPARATOR = "\n\n---\n\n";
  private static final int EPSILION = 100;

  private boolean scrolledToBottom;
  private RandomAccessFile log;
  private ScrollView scrollView;
  private Thread refreshThread;
  private TextView rawDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.raw_fragment, null);

    scrollView = (ScrollView)layout.findViewById(R.id.scroll_view);
    rawDisplay = (TextView)layout.findViewById(R.id.raw_display);

    openLog();
    scrolledToBottom = false;

    refreshThread = new Thread(this);
    refreshThread.start();

    return layout;
  }

  public void updateDataset(Dataset dataset) {}

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_raw);
  }

  // Log methods
  // ---------------------------------------------------------------------------------------------------
  public void run() {
    while(true) {
      try {
        seekLog();
      } catch(IOException ioe) {
        continue;
      }

      // Append each line to the end of the log to the text view
      final StringBuilder lines = new StringBuilder();

      while(true) {
        try {
          String line = log.readLine();
          if(line == null) break;

          lines.append(line);
          lines.append(LINE_SEPARATOR);
        } catch(IOException ioe) {}
      }

      rawDisplay.post(new Runnable() {
        public void run() {
          int scrollDelta = (rawDisplay.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

          rawDisplay.setText(lines);

          // Only scroll to the bottom if the first display of the log or the scroll view is within an epsilion value of the bottom
          if(!scrolledToBottom || scrollDelta <= EPSILION) {
            scrollToBottom();
            scrolledToBottom = true;
          }
        }
      });

      try {
        // We don't need to be dumping the log file as fast as possible
        Thread.sleep(1000);
      } catch(InterruptedException ie) {}
    }
  }

  private void seekLog() throws IOException {
    // Seek the log to approximately NUM_LINES before the end of the file
    log.seek(0);
    long position = log.length() - (CHARS_PER_LINE * NUM_LINES);
    log.seek(position);
  }

  private void openLog() {
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      try {
        File file = new File(getActivity().getExternalFilesDir(null), getString(R.string.radio_log));
        log = new RandomAccessFile(file, "r");
      } catch(FileNotFoundException fnfe) {}
    }
  }

  private void scrollToBottom() {
    scrollView.post(new Runnable() {
      public void run() {
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
      }
    });
  }
  // ---------------------------------------------------------------------------------------------------
}
