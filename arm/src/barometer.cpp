#include "barometer.h"

Adafruit_BMP085_Unified Barometer::barometer = Adafruit_BMP085_Unified(18001);
float Barometer::pressureSetting = DEFAULT_PRESSURE_SETTING;

Barometer::Barometer() : Sensor(KALMAN_PROCESS_NOISE, KALMAN_MEASUREMENT_NOISE, KALMAN_ERROR) {
  thermometer = Thermometer();
  altitude = kalmanInit(0);
}

int Barometer::init() {
  return barometer.begin();
}

float Barometer::getPressure() {
  sensors_event_t event;
  barometer.getEvent(&event);

  if(!event.pressure) {
    return NO_DATA;
  }

  kalmanUpdate(&altitude, event.pressure);
  return altitude.value;
}

float Barometer::getAltitudeAboveSeaLevel() {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(pressureSetting * MERCURY_TO_HPA_CONVERSION, pressure, thermometer.getTemperature());
}

float Barometer::getAltitudeAboveGround() {
  float pressure = getPressure();

  if(pressure == NO_DATA) {
    return NO_DATA;
  }

  return getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, pressure, thermometer.getTemperature()) - groundLevel;
}

void Barometer::setPressureSetting(float pressureSetting) {
  Barometer::pressureSetting = pressureSetting;
}

float Barometer::getPressureSetting() {
  return pressureSetting;
}

void Barometer::zero() {
  setGroundLevel();
}

float Barometer::getPressureAltitude(float setting, float pressure, float temperature) {
  // Convert atmospheric pressure, SLP, and temp to altitude
  return barometer.pressureToAltitude(setting, pressure, temperature);
}

void Barometer::setGroundLevel() {
  groundLevel = getPressureAltitude(SENSORS_PRESSURE_SEALEVELHPA, getPressure(), thermometer.getTemperature());
}
