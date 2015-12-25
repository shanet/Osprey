#ifndef THERMOMETER_H
#define THERMOMETER_H

#include <lib/Adafruit_BMP085_Unified/Adafruit_BMP085_U.h>
#include "constants.h"
#include "sensor.h"

using namespace std;

class Thermometer : public virtual Sensor {
  public:
    Thermometer();
    int init();
    float getTemperature(void);

  protected:
    static Adafruit_BMP085_Unified thermometer;
};

#endif
