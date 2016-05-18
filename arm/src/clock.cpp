#include "clock.h"

Osprey::Clock::Clock() {}

int Osprey::Clock::init() {
  rtc.begin();
  reset();

  return 1;
}

void Osprey::Clock::reset() {
  rtc.setTime(0, 0, 0);
  rtc.setDate(1, 1, 16);
}

int Osprey::Clock::getSeconds() {
  // If your flight is longer than 24 hours I want to talk with you
  int seconds = rtc.getSeconds();
  seconds += rtc.getMinutes() * 60;
  seconds += rtc.getHours() * 3600;

  return seconds;
}
