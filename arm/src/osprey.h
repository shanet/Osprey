#ifndef OSPREY_H
#define OSPREY_H

#include "constants.h"
#include "accelerometer.h"
#include "barometer.h"
#include "gps.h"
#include "radio.h"
#include "thermometer.h"

#define HEARTBEAT_LED 8
#define HEARTBEAT_INTERVAL 25

Accelerometer accelerometer;
Barometer barometer;
GPS gps;
Radio radio;
Thermometer thermometer;

extern int commandStatus;

void printJSON();
void heartbeat();
void initSensors();
void printInitError(const char* const message);

extern void processCommand();

#endif
