## Overall Usage (ie. actually flying the thing)

Once everything is built, soldered, compiled, and running, the hard part is over. Usage is relatively straightforward, but there are some points of interest to be aware of:

* The battery used is a 3.7V lipo. When fully charged, it will run at slightly above 4V, quickly dropping down to ~3.7V, holding that voltage for a while, then dropping again before dying at around 3.3V. **It is critical to understand that the igniter circuit has not been tested successfully below 3.6V**. While the processor and sensors will work at or below this voltage, it is highly recommended to never launch on a battery that is below 3.7V. Fully charged batteries are clearly ideal simply for maximum battery life in the event of a difficult recovery.
* The microSD card should be an SDHC card formatted as FAT32. Some cards (especially old ones) may require too much current during writes than the microcontroller is capable of providing resulting in board resets.
* Issuing the "start flight" command will arm the igniter. Never do this until the rocket is on the pad and pointed up, but before the motor igniter is inserted.
* The "end flight" command will stop logging and flush all data to the SD card. It is highly recommended to issue this command upon rocket recovery to avoid loss of a log file.
  * In the event that a log file is lost from the filesystem, a data recovery program may be able to recover it. [PhotoRec](http://www.cgsecurity.org/wiki/PhotoRec) is a good option for this.

In short, the process for usage is as follows:

0. Connect microcontroller and sensors to the igniter circuit
0. Connect e-matches to igniter circuit
0. Connect battery to switch
0. With rocket on the pad, use switch to turn on the boards
0. From the Android app, confirm data is being received
0. Issue the "start flight" command and verify the following:
  * Above ground level is 0m or nearly 0m
  * Flight phase is "pad"
  * Igniter is armed
  * Logging is enabled
  * Battery is at least 3.7V
  * GPS has valid coordinates

And you're ready to fly!

### Commands

* Start flight: Shortcut for setting phase to pad, zeroing sensors, arming igniter, and enable logging.
* End flight: Shortcut for disable logging and disarm igniter. Note: In order to prevent data loss from not closing the log file, it's important to remember to issue the end flight command immediately following rocket recovery.
* Set pressure setting: Change the pressure setting from the default of 29.92"Hg to the current reading. This is only necessary for a correct pressure altitude, but will not affect the above ground level value (provided that the pressure setting is not changed mid-flight). The pressure setting will continuously vary, but the current reading may be obtained from the [METAR of your local airport](https://aviationweather.gov/metar).
* Zero: Zero the above ground level and relative timestamp counter.
* Start logging: Begin logging data points to a new log file on the SD card.
* End logging: Stop logging data points to the log file on the SD card.
* Set event: Set the altitude at which the given event will fire.
* Fire event: Immediately fire the e-match connected to the igniter for the given event. Note: The igniter must be armed in order for it to fire.
* Arm igniter: Arm the igniter to allow it to be fired
* Disarm igniter: Disarm the igniter to not allow it to be fired
