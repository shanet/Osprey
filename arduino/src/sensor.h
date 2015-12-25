#ifndef SENSOR_H
#define SENSOR_H

#include <lib/Adafruit_Sensor/Adafruit_Sensor.h>

using namespace std;

class Sensor {
  public:
    Sensor();
    int init();
};

#endif
