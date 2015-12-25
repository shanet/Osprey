#include "radio.h"

volatile char Radio::message1[RADIO_MAX_LINE_LENGTH];
volatile char Radio::message2[RADIO_MAX_LINE_LENGTH];

volatile char* Radio::previousMessage = message1;
volatile char* Radio::currentMessage = message2;
volatile int Radio::messagePosition = 0;

Radio::Radio() {}

int Radio::init() {
  UBRR0H = UBRRH_VALUE;
  UBRR0L = UBRRL_VALUE;

  #if USE_2X
    UCSR0A |= _BV(U2X0);
  #else
    UCSR0A &= ~(_BV(U2X0));
  #endif

  // Use 8-bit data
  UCSR0C = _BV(UCSZ01) | _BV(UCSZ00);

  // Enable RX and TX
  UCSR0B = _BV(RXEN0) | _BV(TXEN0);

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
  for(int i=0; message[i] != '\0'; i++) {
    // Only write data to the URD0 register when the USART data register empty bit is set. Only when this
    // is empty can we write new data to be transmitted to the register.
    loop_until_bit_is_set(UCSR0A, UDRE0);
    UDR0 = message[i];
  }
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
  // Only read data from the URD) register if the receive complete bit (RXC0) is set on the UCSR0A register
  if(bit_is_clear(UCSR0A, RXC0)) {
    return 0;
  }

  char c = UDR0;

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
