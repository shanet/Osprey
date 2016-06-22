#ifndef UART_H
#define UART_H

#include <stdio.h>
#include <unistd.h>

#include "HardwareSerial.h"

#define PIO_SERCOM 0
#define SERCOM_RX_PAD_0 0
#define UART_TX_PAD_2 0

#define BUFFER 1024

class SERCOM {};

class Uart : public HardwareSerial {
  public:
    Uart();
    Uart(SERCOM *sercom, int a, int b, int c, int d);
    void begin(int pin);
    void write(char c);
    int available();
    char read();
    void insert(const char *buffer);
    void IrqHandler();
    void enableEcho();
    void disableEcho();
  private:
    char buffer[BUFFER];
    int bufferReadPosition;
    int bufferWritePosition;
    int echo;
};

extern SERCOM sercom1;
extern Uart Serial1;

#endif
