#ifndef RADIO_H
#define RADIO_H

#define F_CPU 16000000UL
#define BAUD 9600
#define RADIO_MAX_LINE_LENGTH 128

#include <Arduino.h>
#include <avr/io.h>
#include <util/setbaud.h>

#include "sensor.h"

using namespace std;

class Radio : public virtual Sensor {
  public:
    Radio();
    int init();
    void send(const char* const message);
    void send(float message, int precision=2);
    void send(int message);
    char* recv();

    static char read();

  protected:
    static volatile char message1[];
    static volatile char message2[];

    static volatile char *currentMessage;
    static volatile char *previousMessage;
    static volatile int messagePosition;

    void setInterrupt(bool useInterrupt);
};

#endif
