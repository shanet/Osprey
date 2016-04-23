#ifndef BAROMETER_H
#define BAROMETER_H

#include <Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>

#include "constants.h"
#include "sensor.h"
#include "thermometer.h"

#define KALMAN_PROCESS_NOISE 0.01
#define KALMAN_MEASUREMENT_NOISE 0.25
#define KALMAN_ERROR 1

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
    kalman_t altitude;

    float getPressureAltitude(float setting, float pressure, float temperature);
    void setGroundLevel();
};

#endif
