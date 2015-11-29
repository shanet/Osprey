#include "gps.h"

SoftwareSerial gpsSerial(3, 2);
Adafruit_GPS GPS(&gpsSerial);

int initGPS(void) {
  // 9600 NMEA is the default baud rate for Adafruit MTK GPS's- some use 4800
  GPS.begin(9600);

  // Get RMC (recommended minimum) and GGA (fix data) data
  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);

  // Refresh data 5 times per second
  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_5HZ);
  GPS.sendCommand(PMTK_API_SET_FIX_CTL_5HZ);

  // Set an interrupt to read the GPS data once every millisecond
  setInterrupt(true);

  delay(1000);

  return 1;
}

void setInterrupt(boolean useInterrupt) {
  if(useInterrupt) {
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
  GPS.read();
}

Adafruit_GPS* getGPS(void) {
  // If a new NMEA sentece is available, parse it
  if(GPS.newNMEAreceived()) {
    if(!GPS.parse(GPS.lastNMEA())) {
      // Parsing of the sentence failed. Nothing to return.
      return NULL;
    }
  }

  return &GPS;
}
