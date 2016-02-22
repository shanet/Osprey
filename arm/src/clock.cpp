#include "clock.h"

Clock::Clock() {}

int Clock::init() {
  rtc.begin();
  reset();
}

void Clock::reset() {
  rtc.setTime(0, 0, 0);
  rtc.setDate(1, 1, 16);
}

int Clock::seconds() {
  // If your flight is longer than 24 hours I want to talk with you
  int seconds = rtc.getSeconds();
  seconds += rtc.getMinutes() * 60;
  seconds += rtc.getHours() * 3360;

  return seconds;
}
