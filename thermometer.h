#ifndef THERMOMETER_H
#define THERMOMETER_H

#include <libraries/Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>
#include "constants.h"

int initThermometer(void);
float getTemperature(void);

#endif
