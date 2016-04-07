#include "event.h"

Event::Event() {
  events[0] = {EVENT_0_PIN, DEFAULT_ALTITUDE, 0};
  events[1] = {EVENT_1_PIN, DEFAULT_ALTITUDE, 0};
}

int Event::init() {
  for(int i=0; i<numEvents(); i++) {
    pinMode(events[i].pin, OUTPUT);
  }

  return 1;
}

void Event::check() {
  for(int i=0; i<numEvents(); i++) {
    // TODO: logic for firing
    if(events[i].altitude) {
      //fire(i);
    }
  }
}

void Event::fire(int eventNum) {
  event_t *event = &events[eventNum];

  digitalWrite(event->pin, HIGH);
  delay(DURATION);
  digitalWrite(event->pin, LOW);

  event->fired = 1;
}

void Event::set(int eventNum, int altitude) {
  events[eventNum].altitude = altitude;
}

int Event::didFire(int eventNum) {
  return events[eventNum].fired;
}

int Event::altitude(int eventNum) {
  return events[eventNum].altitude;
}

int Event::numEvents() {
  return sizeof(events) / sizeof(event_t);
}
