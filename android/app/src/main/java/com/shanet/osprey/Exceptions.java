package com.shanet.osprey;

public class Exceptions extends Exception {
  public Exceptions(String message) {
    super(message);
  }

  public static class NoUsbDriversException extends Exceptions {
    public NoUsbDriversException() {
      super("A USB device was detected, but no drivers are available for it.");
    }
  }

  public static class UnableToOpenUsbDeviceException extends Exceptions {
    public UnableToOpenUsbDeviceException() {
      super("Unable to open USB device. This may be a permissions problem.");
    }
  }
}
