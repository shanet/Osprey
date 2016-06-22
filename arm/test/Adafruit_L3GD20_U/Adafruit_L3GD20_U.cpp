#include "Adafruit_L3GD20_U/Adafruit_L3GD20_U.h"

Adafruit_L3GD20_Unified::Adafruit_L3GD20_Unified(int32_t sensorID) {}

bool Adafruit_L3GD20_Unified::begin() {
  return true;
}

void Adafruit_L3GD20_Unified::enableAutoRange(bool enabled) {}

bool Adafruit_L3GD20_Unified::getEvent(sensors_event_t*) {
  return true;
}

void Adafruit_L3GD20_Unified::getSensor(sensor_t*) {}
