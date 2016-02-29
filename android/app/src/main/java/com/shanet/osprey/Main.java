package com.shanet.osprey;

import android.app.Activity;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class Main extends Activity {
  private static UsbSerialPort sPort = null;

  private TextView mTitleTextView;
  private TextView mDumpTextView;
  private ScrollView mScrollView;

  private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

  private SerialInputOutputManager mSerialIoManager;

  private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
    @Override
    public void onRunError(Exception e) {}

    @Override
    public void onNewData(final byte[] data) {
      Main.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Main.this.updateReceivedData(data);
        }
      });
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mTitleTextView = (TextView) findViewById(R.id.demoTitle);
    mDumpTextView = (TextView) findViewById(R.id.consoleText);
    mScrollView = (ScrollView) findViewById(R.id.demoScroller);

    // Find all available drivers from attached devices.
    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
    if(availableDrivers.isEmpty()) {
      mTitleTextView.setText("No USB drivers available.");
      return;
    }

    // Open a connection to the first available driver.
    UsbSerialDriver driver = availableDrivers.get(0);
    UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
    if(connection == null) {
      PendingIntent intent = PendingIntent.getBroadcast(this, 0, new Intent("com.shanet.osprey.USB_PERMISSION"), 0);
      manager.requestPermission(driver.getDevice(), intent);
      mTitleTextView.setText("Unable to open USB device.");
      return;
    }

    // Read some data! Most have just one port (port 0).
    sPort = driver.getPorts().get(0);
  }

  @Override
  protected void onPause() {
    super.onPause();

    stopIoManager();

    if(sPort != null) {
      try {
        sPort.close();
      } catch (IOException e) {
      } finally {
        sPort = null;
      }
    }

    finish();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if(sPort == null) {
      mTitleTextView.setText("No serial device.");
    } else {
      final UsbManager usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);

      UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
      if(connection == null) {
        mTitleTextView.setText("Opening device failed");
        return;
      }

      try {
        sPort.open(connection);
        sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
      } catch (IOException e) {
        mTitleTextView.setText("Error opening device: " + e.getMessage());

        try {
            sPort.close();
        } catch (IOException e2) {
        } finally {
          sPort = null;
        }

        return;
      }

      mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
    }

    onDeviceStateChange();
  }

  private void stopIoManager() {
    if(mSerialIoManager != null) {
      mSerialIoManager.stop();
      mSerialIoManager = null;
    }
  }

  private void startIoManager() {
    if(sPort != null) {
      mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
      mExecutor.submit(mSerialIoManager);
    }
  }

  private void onDeviceStateChange() {
    stopIoManager();
    startIoManager();
  }

  private void updateReceivedData(byte[] data) {
    mDumpTextView.append(new String(data));
    mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
  }
}
