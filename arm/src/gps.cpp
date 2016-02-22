#include "gps.h"

Uart GPS::GPSSerial(&sercom1, GPS_RX_PIN, GPS_TX_PIN, SERCOM_RX_PAD_0, UART_TX_PAD_2);
Adafruit_GPS GPS::gps = Adafruit_GPS(&GPS::GPSSerial);

GPS::GPS() {
  char iso8601[ISO_8601_LENGTH];
}

int GPS::init() {
  //gps.begin(GPS_BAUD);
  GPSSerial.begin(GPS_BAUD);

  pinPeripheral(GPS_RX_PIN, PIO_SERCOM);
  pinPeripheral(GPS_TX_PIN, PIO_SERCOM);

  // Get RMC (recommended minimum) and GGA (fix data) data
  gps.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);

  // Refresh data 5 times per second
  gps.sendCommand(PMTK_SET_NMEA_UPDATE_5HZ);
  gps.sendCommand(PMTK_API_SET_FIX_CTL_5HZ);

  delay(1000);

  return 1;
}

void SERCOM1_Handler() {
  // Call the interrupt handler for the serial object before trying to read from it
  GPS::GPSSerial.IrqHandler();
  GPS::gps.read();

  // After reading available data, if a new NMEA sentece is available, parse it
  if(GPS::gps.newNMEAreceived()) {
    GPS::gps.parse(GPS::gps.lastNMEA());
  }
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

char* GPS::getIso8601() {
  sprintf(iso8601, "20%d-%02d-%02dT%02d:%02d:%02d.%02dZ", gps.year, gps.month, gps.day, gps.hour, gps.minute, gps.seconds, gps.milliseconds);
  return iso8601;
}
