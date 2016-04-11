#ifndef RADIO_H
#define RADIO_H

#include <Arduino.h>
#include <avr/dtostrf.h>

#include "sensor.h"
#include "logger.h"

#define RADIO_BAUD 115200
#define RADIO_MAX_LINE_LENGTH 64

using namespace std;

class Radio : public virtual Sensor {
  public:
    Radio();
    int init();
    void send(const char* const message);
    void send(float message, int precision=2);
    void send(int message);
    char* recv();
    void clear();
    int enableLogging();
    int disableLogging();
    int isLogging();
    char* getMostRecentMessage();

    static void floatToString(float num, int precision, char *buffer);
    static char read();

  protected:
    bool logging;
    Logger logger;

    static Uart *RadioSerial;

    static char message1[];
    static char message2[];
    static char mostRecentMessage[];

    static char *currentMessage;
    static char *previousMessage;
    static int messagePosition;
};

#endif
