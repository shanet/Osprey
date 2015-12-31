#! /usr/bin/env python3

import sys
import signal
import argparse

from ncurses import OspreyNcurses

DEFAULT_BLOCK_DEVICE = '/dev/ttyUSB0'
DEFAULT_BAUD_RATE = 9600

ncursesUI = None
qtUI = None

def main():
  signal.signal(signal.SIGINT, signalHandler)
  args = parseCmdLineArgs()

  if args.ncurses:
    global ncursesUI
    ncursesUI = OspreyNcurses(args.device, args.baud)
    ncursesUI.start()
  else:
    # TODO
    pass

  sys.exit(0)

def parseCmdLineArgs():
    argvParser = argparse.ArgumentParser()

    argvParser.add_argument('-d', '--device', dest='device', nargs='?', type=str, default=DEFAULT_BLOCK_DEVICE, help='Block device to read from')
    argvParser.add_argument('-b', '--baud', dest='baud', nargs='?', type=int, default=DEFAULT_BAUD_RATE, help='Baud rate to read from block device with')
    argvParser.add_argument('-n', '--ncurses', dest='ncurses', default=False, action='store_true', help='Use the Ncurses UI')

    args = argvParser.parse_args()

    return args

def signalHandler(signal, frame):
  if ncursesUI is not None:
    ncursesUI.stop()
  elif qtUI is not None:
    qtUI.stop()

  sys.exit(0)

if __name__ == '__main__':
  main()
