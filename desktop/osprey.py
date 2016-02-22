#! /usr/bin/env python3

import sys
import signal
import argparse

import constants

from ncurses import OspreyNcurses
from qt import OspreyQt

ui = None

def main():
  signal.signal(signal.SIGINT, signalHandler)
  args = parseCmdLineArgs()

  if args.ncurses:
    ui = OspreyNcurses(args.device, args.baud)
  else:
    ui = OspreyQt(args.device, args.baud)

  ui.start()
  sys.exit(0)

def parseCmdLineArgs():
    argvParser = argparse.ArgumentParser()

    argvParser.add_argument('-d', '--device', dest='device', nargs='?', type=str, default=constants.DEFAULT_BLOCK_DEVICE, help='Block device to read from')
    argvParser.add_argument('-b', '--baud', dest='baud', nargs='?', type=int, default=constants.DEFAULT_BAUD_RATE, help='Baud rate to read from block device with')
    argvParser.add_argument('-n', '--ncurses', dest='ncurses', default=False, action='store_true', help='Use the Ncurses UI')

    args = argvParser.parse_args()

    return args

def signalHandler(signal, frame):
  if ui is not None:
    ui.stop()

  sys.exit(0)

if __name__ == '__main__':
  main()
