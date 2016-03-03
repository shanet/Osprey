package com.shanet.osprey;

public class Exceptions extends Exception {
  public Exceptions(String message) {
    super(message);
  }

  public static class NoUsbDriversException extends Exceptions {
    public NoUsbDriversException() {
      super("No USB drivers found");
    }
  }

  public static class UnableToOpenUsbDeviceException extends Exceptions {
    public UnableToOpenUsbDeviceException() {
      super("Unable to open USB device");
    }
  }
}
