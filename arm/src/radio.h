#ifndef RADIO_H
#define RADIO_H

#include <Arduino.h>
#include <avr/dtostrf.h>

#include "sensor.h"

#define RADIO_BAUD 9600
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

    static void floatToString(float num, int precision, char *buffer);
    static char read();

  protected:
    static Uart *RadioSerial;

    static volatile char message1[];
    static volatile char message2[];

    static volatile char *currentMessage;
    static volatile char *previousMessage;
    static volatile int messagePosition;
};

#endif
