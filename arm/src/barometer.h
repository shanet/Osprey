#ifndef BAROMETER_H
#define BAROMETER_H

#include <Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>

#include "constants.h"
#include "sensor.h"
#include "thermometer.h"

using namespace std;

class Barometer : public virtual Sensor {
  public:
    Barometer();
    int init();
    float getPressure();
    float getAltitudeAboveSeaLevel();
    float getAltitudeAboveGround();
    void setPressureSetting(float pressure);
    float getPressureSetting();
    void zero();

  protected:
    static Adafruit_BMP085_Unified barometer;
    static float pressureSetting;

    Thermometer thermometer;
    float groundLevel;

    float getPressureAltitude(float setting, float pressure, float temperature);
    void setGroundLevel();
};

#endif
