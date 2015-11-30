#include "gps.h"

SoftwareSerial GPS::gpsSerial = SoftwareSerial(3, 2);
Adafruit_GPS GPS::gps = Adafruit_GPS(&gpsSerial);

GPS::GPS() {}

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

Adafruit_GPS* GPS::getGPS() {
  // If a new NMEA sentece is available, parse it
  if(gps.newNMEAreceived()) {
    if(!gps.parse(gps.lastNMEA())) {
      // Parsing of the sentence failed. Nothing to return.
      return NULL;
    }
  }

  return &gps;
}
