#ifndef LSM303_H
#define LSM303_H

#include <math.h>
#include <Adafruit_Sensor/Adafruit_Sensor.h>

#include "constants.h"
#include "stub.h"

class Adafruit_LSM303_Accel_Unified : public Adafruit_Sensor, public Stub {
  public:
    Adafruit_LSM303_Accel_Unified(int32_t sensorID = -1);

    bool begin(void);
    bool getEvent(sensors_event_t*);
    void getSensor(sensor_t*);
};

// ----------------------------------------------------------------------------------

class Adafruit_LSM303_Mag_Unified : public Adafruit_Sensor {
  public:
    Adafruit_LSM303_Mag_Unified(int32_t sensorID = -1);

    bool begin(void);
    void enableAutoRange(bool enable);
    bool getEvent(sensors_event_t*);
    void getSensor(sensor_t*);
};

#endif
