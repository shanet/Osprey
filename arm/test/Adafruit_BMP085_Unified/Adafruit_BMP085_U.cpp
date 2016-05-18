#include "Adafruit_BMP085_Unified/Adafruit_BMP085_U.h"

Adafruit_BMP085_Unified::Adafruit_BMP085_Unified(int32_t sensorID) {}

bool Adafruit_BMP085_Unified::begin() {
  return true;
}

void Adafruit_BMP085_Unified::getTemperature(float *temp) {
  *temp = getField("temp").floatVal;
}

float Adafruit_BMP085_Unified::pressureToAltitude(float seaLevel, float atmospheric, float temp) {
  return getField("pressure_altitude").floatVal;
}

bool Adafruit_BMP085_Unified::getEvent(sensors_event_t *event) {
  event->pressure = getField("pressure_altitude").floatVal;
  return true;
}

void Adafruit_BMP085_Unified::getSensor(sensor_t*) {}
