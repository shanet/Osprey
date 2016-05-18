#include "Adafruit_10DOF/Adafruit_10DOF.h"

Adafruit_10DOF::Adafruit_10DOF(void) {}

bool Adafruit_10DOF::begin(void) {
  return true;
}

bool Adafruit_10DOF::accelGetOrientation(sensors_event_t *event, sensors_vec_t *orientation) {
  orientation->roll = getField("roll").floatVal;
  orientation->pitch = getField("pitch").floatVal;

  return true;
}

bool Adafruit_10DOF::magTiltCompensation(sensors_axis_t axis, sensors_event_t *mag_event, sensors_event_t *accel_event) {
  return true;
}

bool Adafruit_10DOF::magGetOrientation(sensors_axis_t axis, sensors_event_t *event, sensors_vec_t *mag_orientation) {
  mag_orientation->heading = getField("heading").floatVal;

  return true;
}

bool Adafruit_10DOF::fusionGetOrientation(sensors_event_t *accel_event, sensors_event_t *mag_event, sensors_vec_t *orientation) {
  return true;
}
