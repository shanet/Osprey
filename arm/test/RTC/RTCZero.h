#ifndef RTC_ZERO_H
#define RTC_ZERO_H

#include <stdint.h>

#include "stub.h"

class RTCZero : public Stub {
  public:
    RTCZero();
    void begin();

    uint8_t getSeconds();
    uint8_t getMinutes();
    uint8_t getHours();

    void setTime(uint8_t hours, uint8_t minutes, uint8_t seconds);
    void setDate(uint8_t day, uint8_t month, uint8_t year);
};

#endif
