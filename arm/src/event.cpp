#include "event.h"

Event::Event(Accelerometer accelerometer, Barometer barometer) {
  phase = PAD;

  events[EVENT_APOGEE] = {APOGEE_PIN, APOGEE, 0};
  events[EVENT_MAIN] = {MAIN_PIN, DEFAULT_ALTITUDE, 0};

  this->accelerometer = accelerometer;
  this->barometer = barometer;
}

int Event::init() {
  for(int i=0; i<numEvents(); i++) {
    pinMode(events[i].pin, OUTPUT);
  }

  return 1;
}

void Event::check() {
  float acceleration = accelerometer.getAcceleration();
  float altitude = barometer.getAltitudeAboveGround();

  for(int i=0; i<numEvents(); i++) {
    switch(phase) {
      case PAD:
        checkPad(acceleration);
        break;
      case BOOST:
        checkBoost(acceleration);
        break;
      case COAST:
        checkCoast(acceleration, i);
        break;
      case DROGUE:
        checkDrogue(acceleration, altitude, i);
        break;
      case MAIN:
        checkMain(acceleration, i);
        break;
      case LANDED:
        // nop
        break;
      default:
        break;
    }
  }
}

void Event::checkPad(float acceleration) {
  // Move to boost after motor ignition
  if(acceleration >= BOOST_ACCELERATION) {
    phase = BOOST;
    return;
  }

  // If we missed the boost acceleration for some reason, jump to coast
  if(acceleration <= COAST_ACCELERATION) {
    phase = COAST;
    return;
  }
}

void Event::checkBoost(float acceleration) {
  if(acceleration <= COAST_ACCELERATION) {
    phase = COAST;
  }
}

void Event::checkCoast(float acceleration, int eventNum) {
  static int countdown = APOGEE_COUNTDOWN * CYCLES_PER_SECOND;
  static int countdownRunning = 0;

  if(countdownRunning) {
    countdown--;
  }

  // Anything less than .25g can be considered at apogee
  if(acceleration < APOGEE_IDEAL) {
    atApogee(eventNum);
    phase = DROGUE;
    return;
  }

  // Anything less than .5g is /probably/ apogee, but wait to see if we
  // can get closer and if not, the timer will expire causing an apogee event
  if(acceleration < APOGEE_OKAY) {
    countdownRunning = 1;
    return;
  }

  // If the apogee countdown is finished, fire it
  if(countdownRunning && countdown <= 0) {
    atApogee(eventNum);
    phase = DROGUE;
    countdownRunning = 0;
    return;
  }

  // If the acceleration is back to 1, we're falling but without a drogue chute (uh oh)
  if(acceleration > 1) {
    atApogee(eventNum);
    phase = DROGUE;
    return;
  }
}

void Event::checkDrogue(float acceleration, float altitude, int eventNum) {
  event_t *event = &events[eventNum];

  // If the event altitude is reached (or we're falling too fast), fire it
  if((event->altitude > 0 && altitude < event->altitude) || acceleration > BOOST_ACCELERATION) {
    fire(eventNum);
    phase = MAIN;
  }
}

void Event::checkMain(float altitude, int eventNum) {
  if(altitude < LANDED_ALTITUDE) {
    phase = LANDED;
  }
}

void Event::atApogee(int eventNum) {
  event_t *event = &events[eventNum];

  // Only fire the given event if it's set to fire at apogee
  if(event->altitude == APOGEE) {
    fire(eventNum);
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

int Event::getPhase() {
  return phase;
}
