#ifndef ARDUINO_H
#define ARDUINO_H

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define A7 0
#define HIGH 0
#define LOW 0
#define OUTPUT 0

float analogRead(int pin);
void delay(int time);
void pinMode(int pin, int mode);
void digitalWrite(int pin, int mode);
void pinPeripheral(int pin, int mode);

#endif
