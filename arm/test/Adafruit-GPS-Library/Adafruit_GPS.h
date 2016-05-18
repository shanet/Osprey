#ifndef ADAFRUIT_GPS_H
#define ADAFRUIT_GPS_H

#include <stdbool.h>
#include <stdint.h>
#include <string>

#include "HardwareSerial.h"
#include "stub.h"

#define PMTK_SET_NMEA_OUTPUT_RMCGGA "$PMTK314,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*28"
#define PMTK_SET_NMEA_UPDATE_5HZ  "$PMTK220,200*2C"
#define PMTK_API_SET_FIX_CTL_5HZ  "$PMTK300,200,0,0,0,0*2F"

class Adafruit_GPS : public Stub {
  public:
    Adafruit_GPS(HardwareSerial *ser);

    void begin(uint16_t baud);

    char* lastNMEA();
    bool newNMEAreceived();
    void common_init();

    void sendCommand(const char *command);

    char read();
    bool parse(char *message);

    uint8_t year;
    uint8_t month;
    uint8_t day;
    uint8_t hour;
    uint8_t minute;
    uint8_t seconds;
    uint16_t milliseconds;

    float latitudeDegrees;
    float longitudeDegrees;

    float altitude;
    float speed;
    uint8_t fixquality;
};

#endif
