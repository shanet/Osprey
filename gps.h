#ifndef GPS_H
#define GPS_H

#include <libraries/Adafruit-GPS-Library/Adafruit_GPS.h>
#include <SoftwareSerial.h>
#include "sensor.h"

#define ISO_8601_LENGTH 32

using namespace std;

class GPS : public virtual Sensor {
  public:
    GPS();
    int init();

    char* getIso8601();
    float getLatitude();
    float getLongitude();
    float getSpeed();
    float getAltitude();
    int getQuality();

    Adafruit_GPS* getRawGPS();
    void updateBuffers();

    static Adafruit_GPS gps;

  protected:
    void setInterrupt(bool useInterrupt);
    void updateIso8601();

    volatile char iso8601[];
    static SoftwareSerial gpsSerial;
};

#endif
