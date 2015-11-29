#include "thermometer.h"

Adafruit_BMP085_Unified thermometer;

int initThermometer(void) {
  thermometer = Adafruit_BMP085_Unified(18001);
  return thermometer.begin();
}

float getTemperature(void) {
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
