#include "Adafruit-GPS-Library/Adafruit_GPS.h"

Adafruit_GPS::Adafruit_GPS(HardwareSerial *ser) {}

void Adafruit_GPS::begin(uint16_t baud) {}

char* Adafruit_GPS::lastNMEA() {
  return (char*)"";
}

bool Adafruit_GPS::newNMEAreceived() {
  return true;
}

void Adafruit_GPS::common_init() {}

void Adafruit_GPS::sendCommand(const char *command) {}

char Adafruit_GPS::read() {
  return 0;
}

bool Adafruit_GPS::parse(char *message) {
  //std::string iso8601 = getField("iso8601").;
  //sscanf(iso8601.c_str(), "%hhu-%hhu-%hhuT%hhu:%hhu:%hhu.%huZ", &year, &month, &day, &hour, &minute, &seconds, &milliseconds);

  latitudeDegrees = getField("latitude").floatVal;
  longitudeDegrees = getField("longitude").floatVal;

  altitude = getField("gps_altitude").floatVal;
  speed = getField("speed").floatVal;
  fixquality = getField("quality").intVal;

  return true;
}
