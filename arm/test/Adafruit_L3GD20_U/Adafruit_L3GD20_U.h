#ifndef LSM3GD20_H
#define LSM3GD20_H

#include <Adafruit_Sensor/Adafruit_Sensor.h>

class Adafruit_L3GD20_Unified : public Adafruit_Sensor {
  public:
    Adafruit_L3GD20_Unified(int32_t sensorID = -1);

    bool begin();
    void enableAutoRange(bool enabled);
    bool getEvent(sensors_event_t*);
    void getSensor(sensor_t*);
};

#endif
