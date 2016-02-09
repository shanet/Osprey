#ifndef SENSOR_H
#define SENSOR_H

#include <Adafruit_Sensor/Adafruit_Sensor.h>

using namespace std;

class Sensor {
  public:
    Sensor();
    int init();
};

#endif
