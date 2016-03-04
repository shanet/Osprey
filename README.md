![](/images/logo_readme.png?raw=true)

Osprey
======

Osprey is a free and open source tracking and telemetry system for amateur rocketry. It is currently under heavy development.

Currently the following data is collected:

* Latitude & longitude
* Roll, pitch, heading
* Pressure altitude
* GPS altitude & speed
* Absolute & relative timestamps
* Temperature
* Battery voltage

All data is both logged to an on-board microSD card and transmitted via radio to a desktop client with graphing capabilities or an Android application.

## Hardware

All assembled, the total size is 9.5cm x 3.3cm x 2.8cm and weighs in at 47g.

![](/images/assembled_1.jpg?raw=true)
![](/images/assembled_2.jpg?raw=true)

The following hardware is used:

* [Adafruit Feather M0 Adalogger](https://www.adafruit.com/products/2796)
* [Adafruit Ultimate GPS](https://www.adafruit.com/products/746)
* [Adafruit 10-DOF IMU](https://www.adafruit.com/products/1604)
* [XBee Pro 900 Wire Antenna](https://www.sparkfun.com/products/9097)
* [XBee Pro 900 RP-SMA](https://www.sparkfun.com/products/9099)
* [Adafruit XBee Adapter Kit](https://www.adafruit.com/product/126)
* [SparkFun XBee Explorer Dongle](https://www.sparkfun.com/products/11697)
* [Adafruit 500mAh LiPo battery](https://www.adafruit.com/product/1578)
* [Adafruit Perma-Proto Half-size Breadboard PCB](https://www.adafruit.com/products/1609) (cut in half for use as a mounting board)
* Either an RP-SMA omnidirectional antenna or a Yagi antenna
* Female USB to male microUSB if connecting to an Android device
* Foam tape to hold everything to the mounting board

## Wiring

**NOTE:** The diagram below is a crude approximation because a model for the Adafruit Feather boards are not available yet in Fritzing.

![](/images/wiring.png?raw=true)

Actual wiring:

|Radio       | GPS          | IMU         |
|------------|--------------|-------------|
|Pin 1 -> RX | Pin 10 -> RX | SDA -> SDA  |
|Pin 0 -> TX | Pin 11 -> TX | SCL -> SCL  |
|3.3V -> Vin | 3.3V -> Vin  | 3.3V -> Vin |
|GND -> GND  | GND -> GND   | GND -> GND  |

## Dependencies

TODO

## Compiling

### Embedded

TODO

### Android

TODO

## Usage

TODO

* SDHC microSD card formatted as FAT32

### Commands

* `start_flight`
* `end_flight`
* `pressure`
* `zero`
* `start_logging`
* `end_logging`

## Contributing

TODO

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
