#ifndef GPS_H
#define GPS_H

#include <libraries/Adafruit-GPS-Library/Adafruit_GPS.h>
#include <SoftwareSerial.h>
#include "sensor.h"

using namespace std;

class GPS : public virtual Sensor {
  public:
    GPS();
    int init();
    Adafruit_GPS* getGPS(void);

    static Adafruit_GPS gps;

  protected:
    static SoftwareSerial gpsSerial;

    void setInterrupt(boolean useInterrupt);
};

#endif
