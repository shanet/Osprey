#include <libraries/Adafruit_Sensor/Adafruit_Sensor.h>
#include <Wire.h>

#include "constants.h"
#include "accelerometer.h"
#include "barometer.h"
#include "thermometer.h"

void initSensors(void);
void printInitError(const char* const message);
