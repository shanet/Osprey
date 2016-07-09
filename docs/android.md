## Android Application

The Android app is the main method of interfacing with the rocket during flight and during recovery. It contains a display of all data from the rocket as well as the ability to issue commands to the rocket. Moreover, it tracks the rocket's location and continuously calculates the distance and direction from the device's location to the rocket.

A pre-built APK is not available in order to preserve the author's sanity that would surely be lost from publishing an app on the Play store.

### Dependencies

Install Android Studio or at least the Android command line tools from https://developer.android.com/studio/index.html#downloads.

In Osprey's directory, you may have to create a file, `android/local.properties`, with the following content:
```
sdk.dir=/path/to/android/sdk
```

### Compiling

```
$ cd android
$ ./gradlew assembleDebug
```

### Installing

ADB must be enabled on the target device before installing. See https://developer.android.com/studio/command-line/adb.html#Enabling for instructions.

```
$ cd android
$ ./gradlew installDebug
```

### Usage

With the radio plugged in and GPS enabled, start the app. There are eight main views:

* Location: A map with the current rocket location, device location, flight path of rocket, and path from current device location to the rocket. The options menu contains options to change the map style, show the last known location of the rocket, save maps for offline use, and track a given pair of coordinates.
* Tracking: The distance and relative bearing to the rocket's current location as well a compass and unnecessarily large arrow that points towards the rocket. The options menu contains options to show the last known location of the rocket and track a given pair of coordinates.
* Altitude: A graph of the above ground level, pressure altitude, and GPS altitude along with the pressure setting and an option to set it.
* Acceleration: A graph of the combined X, Y, and Z axes acceleration.
* Orientation: A graph of the roll, pitch, and heading angles.
* Events: A display of which events have fired, if the igniter is armed, and the altitude the main chute is set to deploy at. Also here are the override buttons to manually fire the apogee and main chutes. The options menu contains options to arm the igniter and set the main chute deployment altitude.
* Status: The phase the rocket is in, the most recent command and battery voltage. The buttons for the start and end flight commands are also found here.
* Raw: The last 100 lines of the raw radio log. This is useful if the rocket goes out of range of the receiver and sends corrupted JSON strings. It may be possible to manually extract some useful data from these.

The full log file displayed in the Raw view can be found through a file manager at `/sdcard/Android/data/com.shanet.osprey/files/osprey.log`.
