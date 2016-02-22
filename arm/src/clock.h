#ifndef CLOCK_H
#define CLOCK_H

#include "sensor.h"
#include <RTC/RTCZero.h>

using namespace std;

class Clock : public virtual Sensor {
  public:
    Clock();
    int init();
    void reset();
    int seconds();

  protected:
    RTCZero rtc;
};

#endif
