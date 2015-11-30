#include "barometer.h"

Adafruit_BMP085_Unified Barometer::barometer = Adafruit_BMP085_Unified(18001);

Barometer::Barometer() {
  thermometer = Thermometer();
}

int Barometer::init() {
  int initResult = barometer.begin();

  if(initResult) {
    setGroundLevel();
  }

  return initResult;
}

float Barometer::getPressure() {
  sensors_event_t event;
  barometer.getEvent(&event);

  if(!event.pressure) {
    return NO_DATA;
  }

  return event.pressure;
}

float Barometer::getAltitudeAboveSeaLevel() {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(PRESSURE * MERCURY_TO_HPA_CONVERSION, pressure, thermometer.getTemperature());
}

float Barometer::getAltitudeAboveGround() {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, pressure, thermometer.getTemperature()) - groundLevel;
}

float Barometer::getPressureAltitude(float setting, float pressure, float temperature) {
  // Convert atmospheric pressure, SLP and temp to altitude
  return barometer.pressureToAltitude(setting, pressure, temperature);
}

void Barometer::setGroundLevel() {
  groundLevel = getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, getPressure(), thermometer.getTemperature());
}
