#ifndef GPS_H
#define GPS_H

#include <libraries/Adafruit-GPS-Library/Adafruit_GPS.h>
#include <SoftwareSerial.h>

int initGPS(void);
void setInterrupt(boolean useInterrupt);
Adafruit_GPS* getGPS(void);

#endif
