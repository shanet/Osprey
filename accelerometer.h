#ifndef ACCELEROMETER_H
#define ACCELEROMETER_H

#include "constants.h"

#include <libraries/Adafruit_LSM303DLHC/Adafruit_LSM303_U.h>
#include <libraries/Adafruit_L3GD20_U/Adafruit_L3GD20_U.h>
#include <libraries/Adafruit_10DOF/Adafruit_10DOF.h>

int initAccelerometer(void);
float getRoll(void);
float getPitch(void);
float getYaw(void);
void getAccelOrientation(sensors_vec_t *orientation);
void getMagOrientation(sensors_vec_t *orientation);
float getHeading(void);

#endif
