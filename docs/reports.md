## Launch Reports

![](/images/cesium.png?raw=true)

A Ruby script to generate a self-contained, static HTML report of a launch from its log file is included. This allows for quick analysis of a flight including the following information:

|                  |                      |                           |
|------------------|----------------------|---------------------------|
|Actual apogee     |Calculated apogee     |Apogee cause               |
|Main chute target |Main chute actual     |Average temperature        |
|Flight time       |Ascent time           |Ascent rate                |
|Descent time      |Descent rate - drogue |Descent rate - main        |

And:
* Graph of altitude, acceleration, and ground speed vs. time
* 3D view of flight path and rocket orientation (roll, pitch, heading)

[See here for a sample launch report](https://shanet.github.io/Osprey/)

### Dependencies

* Ruby (any recent version should do; no gems are required)

### Usage

```
$ reports/generate.rb
Usage: reports/generate.rb [input log] [output directory]
```

Then open `output_directory/report.html` in any web browser that supports WebGL. Chrome users: Chrome must be started with the `--allow-file-access-from-files` flag for the 3D playback to work.
