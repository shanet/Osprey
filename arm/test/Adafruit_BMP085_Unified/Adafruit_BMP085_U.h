#ifndef BMP085_H
#define BMP085_H

#include <Adafruit_Sensor/Adafruit_Sensor.h>
#include "stub.h"

class Adafruit_BMP085_Unified : public Adafruit_Sensor, public Stub {
  public:
    Adafruit_BMP085_Unified(int32_t sensorID = -1);

    bool begin();
    void getTemperature(float *temp);
    float pressureToAltitude(float seaLevel, float atmospheric, float temp);
    bool getEvent(sensors_event_t*);
    void getSensor(sensor_t*);
};

#endif
