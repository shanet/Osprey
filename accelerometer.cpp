#include "accelerometer.h"

Adafruit_10DOF dof;
Adafruit_LSM303_Accel_Unified accelerometer;
Adafruit_LSM303_Mag_Unified magnetometer;

int initAccelerometer(void) {
  dof = Adafruit_10DOF();
  accelerometer = Adafruit_LSM303_Accel_Unified(30301);
  magnetometer = Adafruit_LSM303_Mag_Unified(30302);

  return accelerometer.begin() & magnetometer.begin();
}

float getRoll(void) {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.roll) {
    return NO_DATA;
  }

  return orientation.roll;
}

float getPitch(void) {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.pitch) {
    return NO_DATA;
  }

  return orientation.pitch;
}

float getHeading(void) {
  sensors_vec_t orientation;
  getMagOrientation(&orientation);

  if(!orientation.heading) {
    return NO_DATA;
  }

  return orientation.heading;
}

void getAccelOrientation(sensors_vec_t *orientation) {
  sensors_event_t event;
  accelerometer.getEvent(&event);

  dof.accelGetOrientation(&event, orientation);
}

void getMagOrientation(sensors_vec_t *orientation) {
  sensors_event_t event;
  magnetometer.getEvent(&event);

  dof.magGetOrientation(SENSOR_AXIS_Z, &event, orientation);
}
