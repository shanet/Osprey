#include "barometer.h"

Adafruit_BMP085_Unified barometer;
float groundLevel;

int initBarometer(void) {
  barometer = Adafruit_BMP085_Unified(18001);
  int initResult = barometer.begin();

  if(initResult) {
    setGroundLevel();
  }

  return initResult;
}

void setGroundLevel(void) {
  groundLevel = getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, getPressure(), getTemperature());
}

float getPressure(void) {
  sensors_event_t event;
  barometer.getEvent(&event);

  if(!event.pressure) {
    return NO_DATA;
  }

  return event.pressure;
}

float getAltitudeAboveSeaLevel(void) {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(PRESSURE * MERCURY_TO_HPA_CONVERSION, pressure, getTemperature());
}

float getAltitudeAboveGround(void) {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, pressure, getTemperature()) - groundLevel;
}

float getPressureAltitude(float setting, float pressure, float temperature) {
  // Convert atmospheric pressure, SLP and temp to altitude
  return barometer.pressureToAltitude(setting, pressure, temperature);
}
