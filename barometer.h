#ifndef BAROMETER_H
#define BAROMETER_H

#include <libraries/Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>

#include "constants.h"
#include "sensor.h"
#include "thermometer.h"

using namespace std;

class Barometer : public virtual Sensor {
  public:
    Barometer();
    int init();
    float getAltitudeAboveSeaLevel();
    float getAltitudeAboveGround();
    float getPressure();
    float getTemperature();

  protected:
    static Adafruit_BMP085_Unified barometer;
    Thermometer thermometer;
    float groundLevel;

    float getPressureAltitude(float setting, float pressure, float temperature);
    void setGroundLevel();
};

#endif
