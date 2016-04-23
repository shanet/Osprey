#include "accelerometer.h"

Adafruit_10DOF Accelerometer::dof = Adafruit_10DOF();
Adafruit_LSM303_Accel_Unified Accelerometer::accelerometer = Adafruit_LSM303_Accel_Unified(30301);
Adafruit_LSM303_Mag_Unified Accelerometer::magnetometer = Adafruit_LSM303_Mag_Unified(30302);

Accelerometer::Accelerometer() : Sensor(KALMAN_PROCESS_NOISE, KALMAN_MEASUREMENT_NOISE, KALMAN_ERROR) {
  roll = kalmanInit(0);
  pitch = kalmanInit(90);
  heading  = kalmanInit(0);
  acceleration  = kalmanInit(1);
}

int Accelerometer::init() {
  return accelerometer.begin() & magnetometer.begin();
}

float Accelerometer::getRoll() {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.roll) {
    return NO_DATA;
  }

  kalmanUpdate(&roll, orientation.roll);
  return roll.value;
}

float Accelerometer::getPitch() {
  sensors_vec_t orientation;
  getAccelOrientation(&orientation);

  if(!orientation.pitch) {
    return NO_DATA;
  }

  kalmanUpdate(&pitch, orientation.pitch);
  return pitch.value;
}

float Accelerometer::getHeading() {
  sensors_vec_t orientation;
  getMagOrientation(&orientation);

  if(!orientation.heading) {
    return NO_DATA;
  }

  kalmanUpdate(&heading, orientation.heading);
  return heading.value;
}

float Accelerometer::getAcceleration() {
  kalmanUpdate(&acceleration, getRawAcceleration());
  return acceleration.value;
}

float Accelerometer::getRawAcceleration() {
  sensors_event_t event;
  accelerometer.getEvent(&event);

  return sqrt(pow(event.acceleration.x, 2) + pow(event.acceleration.y, 2) + pow(event.acceleration.z, 2)) * MS2_TO_G;
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
