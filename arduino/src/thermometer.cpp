#include "thermometer.h"

Adafruit_BMP085_Unified Thermometer::thermometer = Adafruit_BMP085_Unified(18001);

Thermometer::Thermometer() {}

int Thermometer::init() {
  return thermometer.begin();
}

float Thermometer::getTemperature() {
  sensors_event_t event;
  thermometer.getEvent(&event);

  if(!event.pressure) {
    return NO_DATA;
  }

  // Get ambient temperature (in celsius)
  float temperature;
  thermometer.getTemperature(&temperature);

  return temperature;
}
