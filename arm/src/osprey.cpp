#include "osprey.h"

using namespace Osprey;

void setup(void) {
  initSensors();
  pinMode(HEARTBEAT_LED, OUTPUT);
  counter = 0;
}

void loop(void) {
  event.check();
  printJSON();
  processCommand();
  heartbeat();

  counter++;
}

void Osprey::printJSON() {
  // The JSON structure is simple enough. Rather than bringing in another
  // library to do a bunch of heavylifting, just construct the string manually.

  radio.send("{");

  radio.send("\"roll\": ");
  radio.send(accelerometer.getRoll());

  radio.send(", \"pitch\": ");
  radio.send(accelerometer.getPitch());

  radio.send(", \"heading\": ");
  radio.send(accelerometer.getHeading());

  radio.send(", \"raw_acceleration\": ");
  radio.send(accelerometer.getRawAcceleration());

  radio.send(", \"pressure_altitude\": ");
  radio.send(barometer.getAltitudeAboveSeaLevel());

  radio.send(", \"temp\": ");
  radio.send(thermometer.getTemperature());

  radio.send(", \"id\": ");
  radio.send(counter);

  radio.send(", \"delta\": ");
  radio.send(Osprey::clock.getSeconds());

  radio.send(", \"iso8601\": \"");
  radio.send(gps.getIso8601());
  radio.send("\"");

  radio.send(", \"agl\": ");
  radio.send(barometer.getAltitudeAboveGround());

  radio.send(", \"acceleration\": ");
  radio.send(accelerometer.getAcceleration());

  radio.send(", \"latitude\": ");
  radio.send(gps.getLatitude(), 6);

  radio.send(", \"longitude\": ");
  radio.send(gps.getLongitude(), 6);

  radio.send(", \"speed\": ");
  radio.send(gps.getSpeed());

  radio.send(", \"gps_altitude\": ");
  radio.send(gps.getAltitude());

  radio.send(", \"gps_quality\": ");
  radio.send(gps.getQuality());

  radio.send(", \"command_status\": ");
  radio.send(commandStatus);

  radio.send(", \"previous_command\": \"");
  radio.send(radio.getMostRecentMessage());
  radio.send("\"");

  radio.send(", \"pressure_setting\": ");
  radio.send(barometer.getPressureSetting());

  radio.send(", \"phase\": ");
  radio.send(event.getPhase());

  radio.send(", \"battery\": ");
  radio.send(battery.getVoltage(), 2);

  radio.send(", \"apogee_cause\": ");
  radio.send(event.getApogeeCause());

  radio.send(", \"armed\": ");
  radio.send(event.isArmed());

  radio.send(", \"apogee_fired\": ");
  radio.send(event.didFire(EVENT_APOGEE));

  radio.send(", \"main_fired\": ");
  radio.send(event.didFire(EVENT_MAIN));

  radio.send(", \"main_alt\": ");
  radio.send(event.getAltitude(EVENT_MAIN));

  radio.send(", \"logging\": ");
  radio.send(radio.isLogging());

  radio.send("}");
  radio.send("\r\n");
}

void Osprey::heartbeat() {
  digitalWrite(HEARTBEAT_LED, HIGH);
  delay(HEARTBEAT_INTERVAL);
  digitalWrite(HEARTBEAT_LED, LOW);
}

void Osprey::initSensors() {
  if(!accelerometer.init()) {
    printInitError("Failed to intialize accelerometer");
  }

  if(!barometer.init()) {
    printInitError("Failed to intialize barometer");
  }

  if(!battery.init()) {
    printInitError("Failed to intialize battery");
  }

  if(!Osprey::clock.init()) {
    printInitError("Failed to intialize clock");
  }

  if(!event.init()) {
    printInitError("Failed to intialize events");
  }

  if(!gps.init()) {
    printInitError("Failed to intialize GPS");
  }

  if(!radio.init()) {
    printInitError("Failed to intialize radio");
  }

  if(!thermometer.init()) {
    printInitError("Failed to intialize thermometer");
  }
}

void Osprey::printInitError(const char* const message) {
  while(1) {
    radio.send(message);
    radio.send("\r\n");
    delay(1000);
  }
}
