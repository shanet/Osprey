package com.shanet.osprey;

import android.app.Activity;

import android.os.Bundle;

import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import android.util.Log;

public class Main extends Activity {
  private TextView titleText;
  private TextView dataDisplay;
  private ScrollView scrollView;

  private Radio radio;
  private Thread recvThread;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    titleText = (TextView)findViewById(R.id.demoTitle);
    dataDisplay = (TextView)findViewById(R.id.consoleText);
    scrollView = (ScrollView)findViewById(R.id.demoScroller);

    try {
      radio = new Radio(this);
    } catch(IOException err) {
      titleText.setText("Failed to initalize radio.");
    }
  }

  @Override
  protected void onPause() {
    super.onPause();

    try {
      radio.close();
    } catch(IOException err) {}

    finish();
  }

  @Override
  protected void onResume() {
    super.onResume();

    try {
      radio.open();
    } catch(Exceptions.NoUsbDriversException err) {
      titleText.setText(err.getMessage());
      return;
    } catch(Exceptions.UnableToOpenUsbDeviceException err) {
      titleText.setText(err.getMessage());
      return;
    }

    recvThread = new Thread(new Runnable() {
      public void run() {
        while(true) {
          try {
            final String line = radio.readLine();

            runOnUiThread(new Runnable() {
              public void run() {
                updateReceivedData(line);
              }
            });
          } catch(IOException err) {}
        }
      }
    });

    recvThread.start();
  }

  private void updateReceivedData(String line) {
    dataDisplay.setText(line);
    scrollView.smoothScrollTo(0, dataDisplay.getBottom());
  }
}
