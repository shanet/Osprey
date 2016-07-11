![](/images/logo_readme.png?raw=true)

Osprey
======

Osprey is a free and open source tracking, telemetry, and dual deployment system for amateur rocketry utilizing only off-the-shelf components. It consists of five major components:

0. [The microcontroller and associated sensors](docs/arm.md)
0. [The e-match igniter circuit](docs/igniter.md)
0. [The Android application for receiving data during a flight](docs/android.md)
0. [The desktop application for laptop-based analysis and debugging](docs/desktop.md)
0. [The launch report generation script for post-flight analysis](docs/reports.md) ([sample launch report](https://shanet.github.io/Osprey/))

The final assembled product, corresponding Android application, and launch report:

![](/images/header.png?raw=true)

The following data is collected:

|                     |                     |                    |                  |
|---------------------|---------------------|--------------------|------------------|
|Latitude & longitude |Above ground level   |ISO 8601 timestamp  |Battery voltage   |
|Roll, pitch, heading |Pressure altitude    |Integer timestamp   |Temperature       |
|Acceleration         |GPS altitude & speed |Flight phase        |Event fired flags |

All data is logged to an on-board microSD card and transmitted via radio to the Android application.

## High Level Build Overview

Osprey, by nature, is extremely DIY. This is necessary to achieve its goal of not using any custom printed hardware or non-free software. If you are not comfortable soldering your own circuits together or compiling software from source, this is not the project for you. In fact, it's quite likely, even inevitable, that something won't go as intended during a build. Those looking for a more plug'n'play solution should look elsewhere. For the adventurous out there, to fully construct everything follow these steps:

0. [Assemble the microcontroller and associated sensors](docs/arm.md)
0. [Assemble the igniter board](docs/igniter.md)
0. [Compile and upload the microcontroller software](docs/arm.md#software)
0. [Set the required radio configuration](docs/radio.md)
0. [Install the completed microcontroller, sensors, igniter, and switch to your e-bay sled](docs/assembly.md)
0. [Compile and install the Android application](docs/android.md)
0. [Review the usage section](docs/usage.md)
0. Do some ground tests, then stick it in a rocket and cross your fingers.

For everything else, see the documentation in the `docs` directory.

A write-up of some of Osprey's internals (Kalman filter, apogee detection, testing, etc.) is [available on my blog](https://shanetully.com/2016/07/inside-the-construction-of-an-amateur-rocketry-flight-computer).

## Roadmap

Some potential future additions:

* "Micro version" with only a processor, IMU, and battery for data logging purposes only.
* The 3D flight path playback in the launch report could be made more rich with the addition of altitude and flight phase data.
* Support for multiple flight stages.
* Reworking the igniter circuit to only need a single capacitor.
* Support for offline maps in the generated launch report webpage.

## Contributing

Pull requests, bug reports, and general questions are welcome. Additionally, suggestions on where the documentation can be made more clear are extremely useful. Log files and generated launch reports are appreciated as well for helping to improve the project and as a showcase.

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
