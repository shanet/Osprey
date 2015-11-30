#include "accelerometer.h"

Adafruit_10DOF Accelerometer::dof = Adafruit_10DOF();
Adafruit_LSM303_Accel_Unified Accelerometer::accelerometer = Adafruit_LSM303_Accel_Unified(30301);
Adafruit_LSM303_Mag_Unified Accelerometer::magnetometer = Adafruit_LSM303_Mag_Unified(30302);

Accelerometer::Accelerometer() {}

int Accelerometer::init() {
  return accelerometer.begin() & magnetometer.begin();
}

float Accelerometer::getRoll() {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.roll) {
    return NO_DATA;
  }

  return orientation.roll;
}

float Accelerometer::getPitch() {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.pitch) {
    return NO_DATA;
  }

  return orientation.pitch;
}

float Accelerometer::getHeading() {
  sensors_vec_t orientation;
  getMagOrientation(&orientation);

  if(!orientation.heading) {
    return NO_DATA;
  }

  return orientation.heading;
}

void Accelerometer::getAccelOrientation(sensors_vec_t *orientation) {
  sensors_event_t event;
  accelerometer.getEvent(&event);

  dof.accelGetOrientation(&event, orientation);
}

void Accelerometer::getMagOrientation(sensors_vec_t *orientation) {
  sensors_event_t event;
  magnetometer.getEvent(&event);

  dof.magGetOrientation(SENSOR_AXIS_Z, &event, orientation);
}
