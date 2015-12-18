#include "radio.h"

volatile char Radio::message1[RADIO_MAX_LINE_LENGTH];
volatile char Radio::message2[RADIO_MAX_LINE_LENGTH];

volatile char* Radio::previousMessage = message1;
volatile char* Radio::currentMessage = message2;
volatile int Radio::messagePosition = 0;

Radio::Radio() {}

int Radio::init() {
  Serial.begin(115200);

  // Set an interrupt to read the radio data once every millisecond
  setInterrupt(true);

  return 1;
}

void Radio::setInterrupt(bool enable) {
  if(enable) {
    // Set the Output Compare Register B to fire an interrupt when the value is equal to below
    OCR0B = 0x00;

    // Enable the COMPB interrupt by flipping the proper bit on the TIMSK0 mask
    TIMSK0 |= _BV(OCIE0B);
  } else {
    // Disable the COMPB interrupt
    TIMSK0 &= ~_BV(OCIE0B);
  }
}

SIGNAL(TIMER0_COMPB_vect) {
  // This interrupt is called whenever the Output Compare Register B equals the
  // value set above in the setInterrupt() function (roughly once per millisecond)
  Radio::read();
}

void Radio::send(const char* const message) {
  //radioSerial.write(message);
  Serial.print(message);
}

void Radio::send(float message, int precision) {
  char buffer[RADIO_MAX_LINE_LENGTH];
  dtostrf(message, 1, precision, buffer);

  send(buffer);
}

void Radio::send(int message) {
  char buffer[RADIO_MAX_LINE_LENGTH];
  sprintf(buffer, "%d", message);

  send(buffer);
}

char* Radio::recv() {
  return (char*)previousMessage;
}

char Radio::read() {
  if(!Serial.available()) {
    return 0;
  }

  char c = Serial.read();

  // If the end of the line, set a NUL terminator and swap the message buffers
  if(c == '\n') {
    currentMessage[messagePosition] = '\0';

    if(currentMessage == message1) {
      currentMessage = message2;
      previousMessage = message1;
    } else {
      currentMessage = message1;
      previousMessage = message2;
    }

    messagePosition = 0;
  } else {
    currentMessage[messagePosition] = c;
    messagePosition++;
  }

  // Keep putting new chars in the last index of the message if we hit the limit
  // Hopefully a newline will come through and properly terminate the string
  if(messagePosition >= RADIO_MAX_LINE_LENGTH) {
    messagePosition--;
  }

  return c;
}
