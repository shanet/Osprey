## Microcontroller & Sensors

All assembled, the total size is 9.5cm x 3.3cm x 2.8cm and weighs in at 50g.

### Hardware

The following hardware is used:

* [Adafruit Feather M0 Adalogger](https://www.adafruit.com/products/2796)
* [Adafruit Ultimate GPS](https://www.adafruit.com/products/746)
* [Adafruit 10-DOF IMU](https://www.adafruit.com/products/1604)
* [XBee Pro 900 Wire Antenna](http://www.mouser.com/ProductDetail/Digi-International/XBP9B-DMWT-002)
* [XBee Pro 900 RP-SMA Connector](http://www.mouser.com/ProductDetail/Digi-International/XBP9B-DMST-002)
* [Adafruit XBee Adapter Kit](https://www.adafruit.com/product/126)
* [SparkFun XBee Explorer Dongle](https://www.sparkfun.com/products/11697)
* [Adafruit 500mAh LiPo battery](https://www.adafruit.com/product/1578)
* [Adafruit Perma-Proto Half-size Breadboard PCB](https://www.adafruit.com/products/1609) (cut in half for use as a mounting board)
* Either an omnidirectional antenna or a Yagi antenna with an RP-SMA connector
* [3 position terminal block](https://www.adafruit.com/products/725)
* USB OTG if connecting to an Android device
* [Foam mounting tape to hold everything to the mounting board](https://www.amazon.com/3M-Scotch-Mounting-125-Inch-314/dp/B0007P5G8Y/ref=sr_1_1?ie=UTF8&qid=1486328647&sr=8-1&keywords=Scotch+Mounting+Tape)

**Note:** Adafruit is occasionally out of stock with their components. I have had good luck in the past finding them in stock with [Mouser](http://www.mouser.com).

### Wiring

![](/images/sensors_bb.png?raw=true)

|Radio       | GPS          | IMU         | Igniter terminal |
|------------|--------------|-------------|------------------|
|Pin 1 -> RX | Pin 10 -> RX | SDA -> SDA  | Left -> Pin 6    |
|Pin 0 -> TX | Pin 11 -> TX | SCL -> SCL  | Center -> GND    |
|3.3V -> Vin | 3.3V -> Vin  | 3.3V -> Vin | Right -> Pin 5   |
|GND -> GND  | GND -> GND   | GND -> GND  |                  |

### Software

#### Dependencies

These instructions are for Linux (x86_64). See below for where to find instructions for other platforms. Do not ask for Windows support.

* Download and extract the [Adafruit SAMD library](https://github.com/adafruit/arduino-board-index/raw/gh-pages/boards/adafruit-samd-1.0.9.tar.bz2) to `~/.arduino15/packages/adafruit/hardware/samd/1.0.9`
* Download and extract the [ARM compiler](http://downloads.arduino.cc/gcc-arm-none-eabi-4.8.3-2014q1-linux64.tar.gz) to `~/.arduino15/packages/adafruit/tools/arm-none-eabi-gcc/4.8.3-2014q1`
* Download and extract [Bossac](http://downloads.arduino.cc/bossac-1.6.1-arduino-x86_64-linux-gnu.tar.gz) to `~/.arduino15/packages/adafruit/tools/bossac/1.6.1-arduino`.
* Download and extract [CMSIS](http://downloads.arduino.cc/CMSIS-4.0.0.tar.bz2) to `~/.arduino15/packages/adafruit/tools/CMSIS/4.0.0-atmel`.

Note: The archives above can be extracted whenever you'd like, but the paths at the top of the Makefile must be adjusted accordingly.

Alternatively, Adafruit has good instructions for getting the above set up with the help of the Arduino software. If not using Linux, this is most likely the easiest way to gather the dependencies.

#### Compiling & Uploading

```
$ cd arm
$ make
$ make upload
```

### Tests

```
$ cd arm
$ make test
$ make test_run # Don't re-compile, only run tests
```

#### Replaying flight from log

In some cases, it is useful to "replay" a flight from its associated log file. For example, when working on logic for apogee detection it is much easier and faster to replay the flight from an existing log file instead of doing actual test launches. To use this, simply place a log file at
```
arm/test/fixtures/replay.json
```
and then run the tests as normal. The output from the replayed flight will be printed to stdout in the usual log format.
