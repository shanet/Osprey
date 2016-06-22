#ifndef EVENT_H
#define EVENT_H

#include "accelerometer.h"
#include "barometer.h"
#include "clock.h"
#include "constants.h"
#include "radio.h"
#include "sensor.h"

#define NUM_EVENTS 2
#define FIRE_DURATION 100 // ms
#define DEFAULT_MAIN_ALTITUDE 152.4f // m
#define APOGEE -1

#define APOGEE_PIN 5
#define MAIN_PIN 6

#define EVENT_APOGEE 0
#define EVENT_MAIN 1

#define APOGEE_CAUSE_NONE 0
#define APOGEE_CAUSE_ALTITUDE 1
#define APOGEE_CAUSE_COUNTDOWN 2
#define APOGEE_CAUSE_SAFETY_COUNTDOWN 3
#define APOGEE_CAUSE_FREE_FALL 4
#define APOGEE_CAUSE_MANUAL 5

#define APOGEE_COUNTDOWN 6 // seconds
#define SAFETY_APOGEE_COUNTDOWN 12 // seconds
#define BOOST_ACCELERATION 1.25 // g
#define COAST_ACCELERATION 0.75 // g
#define APOGEE_IDEAL 0.15 // g
#define APOGEE_OKAY 0.3 // g
#define APOGEE_ALTITUDE_DELTA 1 // meters
#define LANDED_ALTITUDE_DELTA 1.5 // meters
#define LANDED_ALTITUDE_LIMIT 5

typedef struct event_t {
  int pin;
  float altitude;
  int fired;
} event_t;

namespace Osprey {
  extern Accelerometer accelerometer;
  extern Barometer barometer;
  extern Osprey::Clock clock;
  extern Radio radio;
}

class Event : public virtual Sensor {
  public:
    Event();
    int init();
    void check();
    void fire(int eventNum);
    int didFire(int eventNum);
    float getAltitude(int eventNum);
    int setAltitude(int eventNum, float altitude);
    int numEvents();
    int getPhase();
    int getApogeeCause();
    void setApogeeCause(int cause);
    void arm();
    void disarm();
    int isArmed();
    void reset();

  protected:
    void phasePad(float acceleration);
    void phaseBoost(float acceleration);
    void phaseCoast(float acceleration, float altitude);
    void phaseDrogue(float acceleration, float altitude);
    void phaseMain(float acceleration);
    void phaseLanded();
    void atApogee(int apogeeCause);

    int checkApogeeCountdowns();
    void disableApogeeCountdowns();

    int armed;
    int phase;
    event_t events[NUM_EVENTS];

    int apogeeCountdownStart;
    int safetyApogeeCountdownStart;

    int pendingApogee;
    int apogeeCause;
    float previousAltitude;
    int landedAltitudeInRange;
};

#endif
