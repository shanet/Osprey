#include "event.h"

using namespace Osprey;

Event::Event() {
  events[EVENT_APOGEE] = {APOGEE_PIN, APOGEE, 0};
  events[EVENT_MAIN] = {MAIN_PIN, DEFAULT_MAIN_ALTITUDE, 0};

  reset();
}

int Event::init() {
  reset();

  for(int i=0; i<numEvents(); i++) {
    pinMode(events[i].pin, OUTPUT);
  }

  return 1;
}

void Event::check() {
  float acceleration = accelerometer.getAcceleration();
  float altitude = barometer.getAltitudeAboveGround();

  switch(phase) {
    case PAD:
      phasePad(acceleration);
      break;
    case BOOST:
      phaseBoost(acceleration);
      break;
    case COAST:
      phaseCoast(acceleration, altitude);
      break;
    case DROGUE:
      phaseDrogue(acceleration, altitude);
      break;
    case MAIN:
      phaseMain(altitude);
      break;
    case LANDED:
      phaseLanded();
      break;
    default:
      break;
  }

  previousAltitude = altitude;
}

void Event::phasePad(float acceleration) {
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

void Event::phaseBoost(float acceleration) {
  if(acceleration <= COAST_ACCELERATION) {
    phase = COAST;
  }
}

void Event::phaseCoast(float acceleration, float altitude) {
  // If apogee is pending, as soon as the altitude decreases, fire it
  if(pendingApogee) {
    if(previousAltitude - altitude > APOGEE_ALTITUDE_DELTA) {
      atApogee(APOGEE_CAUSE_ALTITUDE);
    }
  }

  // If the apogee countdown is finished, fire it
  int apogeeCountdownCheck = checkApogeeCountdowns();
  if(apogeeCountdownCheck > 0) {
    atApogee(apogeeCountdownCheck);
    return;
  }

  // Anything less than the ideal acceleration means we're basically at apogee, but should start paying attention to altitude to get as close as possible
  if(acceleration < APOGEE_IDEAL) {
    pendingApogee = 1;

    // Only start the countdown if it's not already started
    if(apogeeCountdownStart == 0) {
      apogeeCountdownStart = Osprey::clock.getSeconds();
    }

    return;
  }

  // Anything less than okay acceleration is /probably/ apogee, but wait to see if we
  // can get closer and if not, the timer will expire causing an apogee event
  if(acceleration < APOGEE_OKAY) {
    // Only start the countdown if it's not already started
    if(safetyApogeeCountdownStart == 0) {
      safetyApogeeCountdownStart = Osprey::clock.getSeconds();
    }

    return;
  }

  // If we're now in free fall we must have missed apogee so immediately fire the drogue
  if(isInFreeFall(altitude)) {
    atApogee(APOGEE_CAUSE_FREE_FALL);
    phase = DROGUE;
    return;
  }
}

void Event::phaseDrogue(float acceleration, float altitude) {
  // If we're in the drogue phase and still in free fall fire everything in a desperate attempt to save our ass
  if(isInFreeFall(altitude)) {
    panic();
    phase = MAIN;
    return;
  }

  // If the event altitude is reached fire it
  for(int i=0; i<numEvents(); i++) {
    event_t *event = &events[i];

    if(event->altitude > 0 && altitude < event->altitude) {
      fire(i);
      phase = MAIN;
    }
  }
}

void Event::phaseMain(float altitude) {
  // Increase the landed altitude in range counter for each time the altitude differs less than the set delta
  if(abs(previousAltitude - altitude) < LANDED_ALTITUDE_DELTA) {
    landedAltitudeInRange++;
  }

  // Once the altitude stops changing and is stable, go to landed
  if(landedAltitudeInRange >= LANDED_ALTITUDE_LIMIT) {
    phase = LANDED;
  }
}

void Event::phaseLanded() {
  // Flush the log so that all data is written to disk in case the end
  // flight command is not sent and the log file isn't cleanly closed
  static int landed = 0;

  if(landed == 0) {
    radio.flushLog();
    radio.disableLogging();
    radio.enableLogging();
    landed = 1;
  }
}

void Event::atApogee(int apogeeCause) {
  phase = DROGUE;
  this->apogeeCause = apogeeCause;
  disableApogeeCountdowns();

  // Fire any events configured to fire at apogee
  for(int i=0; i<numEvents(); i++) {
    event_t *event = &events[i];

    if(event->altitude == APOGEE) {
      fire(i);
    }
  }

  // Reset the free fall counter so that the drogue phase can do its own free fall detection
  freeFallAltitudeInRange = 0;
}

void Event::fire(int eventNum) {
  // Don't fire if not armed
  if(armed != 1) return;

  event_t *event = &events[eventNum];

  digitalWrite(event->pin, HIGH);
  delay(FIRE_DURATION);
  digitalWrite(event->pin, LOW);

  event->fired = 1;
}

int Event::checkApogeeCountdowns() {
  // If the apogee countdown is finished, fire it
  if(apogeeCountdownStart > 0 && abs(apogeeCountdownStart - Osprey::clock.getSeconds()) >= APOGEE_COUNTDOWN) {
    return APOGEE_CAUSE_COUNTDOWN;
  }

  // If the safety apogee countdown is finished, fire it
  if(safetyApogeeCountdownStart > 0 && abs(safetyApogeeCountdownStart - Osprey::clock.getSeconds()) >= SAFETY_APOGEE_COUNTDOWN) {
    return APOGEE_CAUSE_SAFETY_COUNTDOWN;
  }

  return 0;
}

void Event::disableApogeeCountdowns() {
  apogeeCountdownStart = 0;
  safetyApogeeCountdownStart = 0;
  pendingApogee = 0;
}

int Event::isInFreeFall(float altitude) {
  if(previousAltitude - altitude > FREE_FALL_ALTITUDE_DELTA) {
    freeFallAltitudeInRange++;
  }

  return (freeFallAltitudeInRange >= FREE_FALL_ALTITUDE_LIMIT);
}

void Event::panic() {
  for(int i=0; i<numEvents(); i++) {
    fire(i);
  }
}

int Event::didFire(int eventNum) {
  return events[eventNum].fired;
}

float Event::getAltitude(int eventNum) {
  return events[eventNum].altitude;
}

int Event::setAltitude(int eventNum, float altitude) {
  if(eventNum >= numEvents()) return 0;

  events[eventNum].altitude = altitude;
  return 1;
}

int Event::numEvents() {
  return sizeof(events) / sizeof(event_t);
}

int Event::getPhase() {
  return phase;
}

int Event::getApogeeCause() {
  return apogeeCause;
}

void Event::setApogeeCause(int cause) {
  apogeeCause = cause;
}

void Event::arm() {
  armed = 1;
}

void Event::disarm() {
  armed = 0;
}

int Event::isArmed() {
  return armed;
}

void Event::reset() {
  phase = PAD;
  armed = 0;
  previousAltitude = 0;
  pendingApogee = 0;
  apogeeCause = APOGEE_CAUSE_NONE;
  landedAltitudeInRange = 0;
  freeFallAltitudeInRange = 0;

  apogeeCountdownStart = 0;
  safetyApogeeCountdownStart = 0;

  for(int i=0; i<numEvents(); i++) {
    events[i].fired = 0;
  }
}
