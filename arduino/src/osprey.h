#include <lib/Adafruit_Sensor/Adafruit_Sensor.h>
#include <Wire.h>

#include "constants.h"
#include "accelerometer.h"
#include "barometer.h"
#include "gps.h"
#include "radio.h"
#include "thermometer.h"

Accelerometer accelerometer;
Barometer barometer;
GPS gps;
Radio radio;
Thermometer thermometer;

char commandStatus;

void printJSON();
void processCommand();
void initSensors();
void printInitError(const char* const message);
