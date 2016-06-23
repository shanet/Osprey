#include "radio.h"

Uart *Radio::RadioSerial = &Serial1;

char Radio::message1[RADIO_MAX_LINE_LENGTH];
char Radio::message2[RADIO_MAX_LINE_LENGTH];
char Radio::mostRecentMessage[RADIO_MAX_LINE_LENGTH];

char* Radio::previousMessage = message1;
char* Radio::currentMessage = message2;
int Radio::messagePosition = 0;

int Radio::init() {
  RadioSerial->begin(RADIO_BAUD);

  if(!logger.init()) {
   return 0;
  }

  logging = false;

  return 1;
}

void Radio::send(const char* const message) {
  for(int i=0; message[i] != '\0'; i++) {
    RadioSerial->write(message[i]);
  }

  if(isLogging()) {
    logger.log(message);
  }
}

void Radio::send(float message, int precision) {
  char buffer[RADIO_MAX_LINE_LENGTH];
  floatToString(message, precision, buffer);

  send(buffer);
}

void Radio::send(int message) {
  char buffer[RADIO_MAX_LINE_LENGTH];
  sprintf(buffer, "%d", message);

  send(buffer);
}

char* Radio::recv() {
  read();
  return (char*)previousMessage;
}

void Radio::clear() {
  // Copy the previous message before clearing it so we can report the most recently processed message
  strncpy(mostRecentMessage, previousMessage, RADIO_MAX_LINE_LENGTH);

  previousMessage[0] = '\0';
}

void Radio::read() {
  if(!RadioSerial->available()) {
    return;
  }

  char c = RadioSerial->read();

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

  // Keep reading until there is no more data to read
  return read();
}

int Radio::enableLogging() {
  // If already logging, do nothing
  if(isLogging()) return 1;

  if(logger.open()) {
    logging = true;
    return 1;
  }

  return 0;
}

int Radio::disableLogging() {
  // If not already logging, do nothing
  if(!isLogging()) return 1;

  logger.close();
  logging = false;
  return 1;
}

int Radio::isLogging() {
  return (logging ? 1 : 0);
}

void Radio::flushLog() {
  logger.flush();
}

void Radio::floatToString(float num, int precision, char *buffer) {
  // Modified from https://raw.githubusercontent.com/arduino/Arduino/master/hardware/arduino/sam/cores/arduino/avr/dtostrf.c
  char format[20];
  sprintf(format, "%%%d.%df", 3, precision);
  sprintf(buffer, format, num);
}

char* Radio::getMostRecentMessage() {
  return mostRecentMessage;
}
