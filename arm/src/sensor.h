#ifndef SENSOR_H
#define SENSOR_H

#include <Adafruit_Sensor/Adafruit_Sensor.h>
#include <Arduino.h>

#include "kalman.h"

#ifdef TEST
  #include "uart.h"
#endif

class Sensor {
  public:
    Sensor();
    Sensor(float processNoise, float measurementNoise, float error);
    virtual int init() = 0;

  protected:
    kalman_t kalmanInit(float intial_value);
    void kalmanUpdate(kalman_t* state, float measurement);

  private:
    float processNoise;
    float measurementNoise;
    float error;
};

#endif
