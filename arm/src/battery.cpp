#include "battery.h"

Battery::Battery() {}
int Battery::init() {}

float Battery::voltage() {
  // https://learn.adafruit.com/adafruit-feather-m0-adalogger/power-management
  float voltage = analogRead(BATTERY_PIN);

  voltage *= 2;
  voltage *= REFERENCE_VOLTAGE;
  voltage /= 1024;

  return voltage;
}
