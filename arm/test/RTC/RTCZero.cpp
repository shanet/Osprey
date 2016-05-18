#include "RTC/RTCZero.h"

RTCZero::RTCZero() {}
void RTCZero::begin() {}

uint8_t RTCZero::getSeconds() {
  return getField("delta").intVal - (getHours() * 3600) - (getMinutes() * 60);
}

uint8_t RTCZero::getMinutes() {
  return (getField("delta").intVal - getHours() * 3600) / 60;
}

uint8_t RTCZero::getHours() {
  return getField("delta").intVal / 3600;
}

void RTCZero::setTime(uint8_t hours, uint8_t minutes, uint8_t seconds) {}
void RTCZero::setDate(uint8_t day, uint8_t month, uint8_t year) {}
