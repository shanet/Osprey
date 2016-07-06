## Desktop Application

The desktop application is primarily used during development and for debugging purposes as it's not usually practical to carry a laptop around at the launch site. Two interfaces are available, a graphical Qt interface with graphing capabilities, and a console curses UI.

![](/images/desktop.png?raw=true)

### Dependencies

* Python 3
* Python packages:
  * PySide
  * Curses

### Usage

```
$ desktop/osprey.py --help
usage: osprey.py [-h] [-d [DEVICE]] [-b [BAUD]] [-n]

optional arguments:
  -h, --help            show this help message and exit
  -d [DEVICE], --device [DEVICE]
                        Block device to read from
  -b [BAUD], --baud [BAUD]
                        Baud rate to read from block device with
  -n, --ncurses         Use the Ncurses UI
```

* Qt: `$ desktop/osprey.py`
* Curses: `$ desktop/osprey.py --ncurses`
