#ifndef EVENT_H
#define EVENT_H

#include "sensor.h"

#define NUM_EVENTS 2
#define DURATION 100 // ms
#define DEFAULT_ALTITUDE 30000 // m

#define EVENT_0_PIN 5
#define EVENT_1_PIN 6

typedef struct event_t {
  int pin;
  int altitude;
  int fired;
} event_t;

using namespace std;

class Event : public virtual Sensor {
  public:
    Event();
    int init();
    void check();
    void fire(int eventNum);
    void set(int eventNum, int altitude);
    int didFire(int eventNum);
    int altitude(int eventNum);
    int numEvents();

  protected:
    event_t events[NUM_EVENTS];
};

#endif
