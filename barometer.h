#ifndef BAROMETER_H
#define BAROMETER_H

#include <libraries/Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>

#include "constants.h"
#include "thermometer.h"

int initBarometer(void);
float getAltitudeAboveSeaLevel(void);
float getAltitudeAboveGround(void);
float getPressureAltitude(float setting, float pressure, float temperature);
float getPressure(void);
void setGroundLevel(void);

#endif
