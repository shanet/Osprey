package com.shanet.osprey;

import android.app.Activity;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import android.util.Log;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class Main extends Activity {
  private EditText coordinatesDisplay;
  private ScrollView scrollView;
  private TextView titleText;
  private TextView dataDisplay;

  private Radio radio;
  private Thread recvThread;

  private JSONObject dataset;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    titleText = (TextView)findViewById(R.id.demoTitle);
    dataDisplay = (TextView)findViewById(R.id.consoleText);
    scrollView = (ScrollView)findViewById(R.id.demoScroller);
    coordinatesDisplay = (EditText)findViewById(R.id.coordinates);

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

    try {
      dataset = new JSONObject(line);
      coordinatesDisplay.setText(String.format("%s, %s", dataset.getString("latitude"), dataset.getString("longitude")));
    } catch(JSONException err) {}
  }
}
