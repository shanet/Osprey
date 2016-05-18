#ifndef ADAFRUIT_10DOF_H
#define ADAFRUIT_10DOF_H

#include <Adafruit_Sensor/Adafruit_Sensor.h>

#include "stub.h"

typedef enum {
  SENSOR_AXIS_X = (1),
  SENSOR_AXIS_Y = (2),
  SENSOR_AXIS_Z = (3),
} sensors_axis_t;

class Adafruit_10DOF : public Stub {
  public:
    Adafruit_10DOF(void);
    bool begin(void);

    bool accelGetOrientation(sensors_event_t *event, sensors_vec_t *orientation);
    bool magTiltCompensation(sensors_axis_t axis, sensors_event_t *mag_event, sensors_event_t *accel_event);
    bool magGetOrientation(sensors_axis_t axis, sensors_event_t *event, sensors_vec_t *mag_orientation);
    bool fusionGetOrientation(sensors_event_t *accel_event, sensors_event_t *mag_event, sensors_vec_t *orientation);
};

#endif
