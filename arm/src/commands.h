#ifndef COMMANDS_H
#define COMMANDS_H

#include "constants.h"
#include "accelerometer.h"
#include "barometer.h"
#include "gps.h"
#include "radio.h"
#include "thermometer.h"

extern Accelerometer accelerometer;
extern Barometer barometer;
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

#endif
