#include "Adafruit_LSM303DLHC/Adafruit_LSM303_U.h"

Adafruit_LSM303_Accel_Unified::Adafruit_LSM303_Accel_Unified(int32_t sensorID) {}

bool Adafruit_LSM303_Accel_Unified::begin(void) {
  return true;
}

bool Adafruit_LSM303_Accel_Unified::getEvent(sensors_event_t* event) {
  event->acceleration.x = getField("raw_acceleration").floatVal / MS2_TO_G;
  event->acceleration.y = 0;
  event->acceleration.z = 0;

  return true;
}

void Adafruit_LSM303_Accel_Unified::getSensor(sensor_t*) {}

// ----------------------------------------------------------------------------------

Adafruit_LSM303_Mag_Unified::Adafruit_LSM303_Mag_Unified(int32_t sensorID) {}

bool Adafruit_LSM303_Mag_Unified::begin(void) {
  return true;
}

void Adafruit_LSM303_Mag_Unified::enableAutoRange(bool enable) {}

bool Adafruit_LSM303_Mag_Unified::getEvent(sensors_event_t*) {
  return true;
}

void Adafruit_LSM303_Mag_Unified::getSensor(sensor_t*) {}
