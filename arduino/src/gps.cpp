#include "gps.h"

SoftwareSerial GPS::gpsSerial = SoftwareSerial(3, 2);
Adafruit_GPS GPS::gps = Adafruit_GPS(&gpsSerial);

GPS::GPS() {
  volatile char iso8601[ISO_8601_LENGTH];
}

int GPS::init() {
  // 9600 NMEA is the default baud rate for Adafruit MTK GPS's- some use 4800
  gps.begin(9600);

  // Get RMC (recommended minimum) and GGA (fix data) data
  gps.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);

  // Refresh data 5 times per second
  gps.sendCommand(PMTK_SET_NMEA_UPDATE_5HZ);
  gps.sendCommand(PMTK_API_SET_FIX_CTL_5HZ);

  // Set an interrupt to read the GPS data once every millisecond
  setInterrupt(true);

  delay(1000);

  return 1;
}

void GPS::setInterrupt(boolean enable) {
  if(enable) {
    // Set the Output Compare Register A to fire an interrupt when the value is equal to below
    OCR0A = 0xAF;

    // Enable the COMPA interrupt by flipping the proper bit on the TIMSK0 mask
    TIMSK0 |= _BV(OCIE0A);
  } else {
    // Disable the COMPA interrupt
    TIMSK0 &= ~_BV(OCIE0A);
  }
}

SIGNAL(TIMER0_COMPA_vect) {
  // This interrupt is called whenever the Output Compare Register A equals the
  // value set above in the setInterrupt() function (roughly once per millisecond)
  GPS::gps.read();
}

char* GPS::getIso8601() {
  updateBuffers();
  return (char*)iso8601;
}

float GPS::getLatitude() {
  return gps.latitudeDegrees;
}

float GPS::getLongitude() {
  return gps.longitudeDegrees;
}

float GPS::getSpeed() {
  return gps.speed;
}

float GPS::getAltitude() {
  return gps.altitude;
}

int GPS::getQuality() {
  return gps.fixquality;
}

void GPS::updateBuffers() {
  // If a new NMEA sentece is available, parse it
  if(gps.newNMEAreceived() && !gps.parse(gps.lastNMEA())) {
    return;
  }

  updateIso8601();
}

void GPS::updateIso8601() {
  sprintf((char*)iso8601, "20%d-%02d-%02dT%02d:%02d:%02d.%02dZ", gps.year, gps.month, gps.day, gps.hour, gps.minute, gps.seconds, gps.milliseconds);
}

Adafruit_GPS* GPS::getRawGPS() {
  updateBuffers();
  return &gps;
}
