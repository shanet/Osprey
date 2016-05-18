#ifndef COMMANDS_H
#define COMMANDS_H

#include "accelerometer.h"
#include "barometer.h"
#include "clock.h"
#include "constants.h"
#include "event.h"
#include "gps.h"
#include "radio.h"
#include "thermometer.h"

namespace Osprey {
  extern Accelerometer accelerometer;
  extern Barometer barometer;
  extern Osprey::Clock clock;
  extern Event event;
  extern GPS gps;
  extern Radio radio;
  extern Thermometer thermometer;

  int commandStatus;

  void processCommand();
  int startFlight(char *arg);
  int endFlight(char *arg);
  int zeroSensors(char *arg);
  int setPressure(char *arg);
  int enableLogging(char *arg);
  int disableLogging(char *arg);
  int setEvent(char *arg);
  int fireEvent(char *arg);
  int armIgniter(char *arg);
  int disarmIgniter(char *arg);
}

#endif
