#include "uart.h"

SERCOM sercom1;
Uart Serial1;

Uart::Uart() {
  bufferReadPosition = 0;
  bufferWritePosition = 0;
  echo = 0;
}

Uart::Uart(SERCOM *sercom, int a, int b, int c, int d) {}
void Uart::begin(int pin) {}

void Uart::write(char c) {
  if(echo == 1) {
    putchar(c);
  }
}

int Uart::available() {
  return (bufferWritePosition - bufferReadPosition > 0);
}

char Uart::read() {
  char c = buffer[bufferReadPosition];
  bufferReadPosition++;

  if(bufferReadPosition >= BUFFER) {
    bufferReadPosition = 0;
  }

  return c;
}

void Uart::insert(const char *buffer) {
  for(int i=0; buffer[i] != '\0'; i++) {
    this->buffer[bufferWritePosition] = buffer[i];
    bufferWritePosition++;

    if(bufferWritePosition >= BUFFER) {
      bufferWritePosition = 0;
    }
  }
}

void Uart::IrqHandler() {}

void Uart::enableEcho() {
  echo = 1;
}

void Uart::disableEcho() {
  echo = 0;
}
