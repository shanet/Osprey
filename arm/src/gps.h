#ifndef GPS_H
#define GPS_H

#include <Adafruit-GPS-Library/Adafruit_GPS.h>
#include <wiring_private.h>
#include <Wire.h>

#include "sensor.h"

#define GPS_RX_PIN 11
#define GPS_TX_PIN 10
#define GPS_BAUD 9600
#define ISO_8601_LENGTH 32

using namespace std;

class GPS : public virtual Sensor {
  public:
    GPS();
    int init();

    float getLatitude();
    float getLongitude();
    float getSpeed();
    float getAltitude();
    int getQuality();
    char* getIso8601();

    static Uart GPSSerial;
    static Adafruit_GPS gps;

  protected:
    char iso8601[];
};

#endif
