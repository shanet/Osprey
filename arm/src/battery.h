#ifndef BATTERY_H
#define BATTERY_H

#include "sensor.h"

#define BATTERY_PIN A7
#define REFERENCE_VOLTAGE 3.3

using namespace std;

class Battery : public virtual Sensor {
  public:
    Battery();
    int init();
    float getVoltage();
};

#endif
