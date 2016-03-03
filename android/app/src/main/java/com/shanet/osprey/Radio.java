package com.shanet.osprey;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

import android.util.Log;

public class Radio {
  private static final int BAUD_RATE = 115200;

  private static UsbSerialPort port;
  private final ExecutorService executor;

  private BufferedReader reader;
  private PipedInputStream input;
  private PipedOutputStream output;

  private Context context;
  private SerialInputOutputManager serialIoManager;
  private UsbManager usbManager;


  public Radio(Context context) throws IOException {
    output = new PipedOutputStream();
    input = new PipedInputStream(output);
    reader = new BufferedReader(new InputStreamReader(input));

    port = null;
    executor = Executors.newSingleThreadExecutor();
    usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
  }

  public void open() throws Exceptions.NoUsbDriversException, Exceptions.UnableToOpenUsbDeviceException {
    // Find all available drivers from attached devices
    List<UsbSerialDriver> drivers = getAvailableDrivers();

    if(drivers.isEmpty()) {
      throw new Exceptions.NoUsbDriversException();
    }

    // Open a connection to the first available driver
    UsbSerialDriver driver = drivers.get(0);
    UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());

    if(connection == null) {
      //PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent("com.shanet.osprey.USB_PERMISSION"), 0);
      //usbManager.requestPermission(driver.getDevice(), intent);
      throw new Exceptions.UnableToOpenUsbDeviceException();
    }

    port = driver.getPorts().get(0);

    try {
      port.open(connection);
      port.setParameters(BAUD_RATE, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
    } catch(IOException err1) {
      try {
        close();
      } catch(IOException err2) {}

      throw new Exceptions.UnableToOpenUsbDeviceException();
    }

    stopIoManager();
    startIoManager();
  }

  public void close() throws IOException {
    stopIoManager();

    if(port != null) {
      port.close();
      port = null;
    }
  }

  public String readLine() throws IOException {
    return reader.readLine();
  }

  private List<UsbSerialDriver> getAvailableDrivers() {
    return UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
  }

  private void stopIoManager() {
    if(serialIoManager != null) {
      Log.i("osprey", "stopping io manager");
      serialIoManager.stop();
      serialIoManager = null;
    }
  }

  private void startIoManager() {
    if(port != null) {
      serialIoManager = new SerialInputOutputManager(port, listener);
      executor.submit(serialIoManager);
      Log.i("osprey", "starting io manager");
    }
  }

  private final SerialInputOutputManager.Listener listener = new SerialInputOutputManager.Listener() {
    public void onRunError(Exception e) {
      Log.i("osprey", "listener run error");
    }

    public void onNewData(final byte[] data) {
      try {
        Log.i("osprey", new String(data));
        output.write(data);
      } catch(IOException err) {}
    }
  };
}
