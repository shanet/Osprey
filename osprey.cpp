#include "osprey.h"

Accelerometer accelerometer;
Barometer barometer;
GPS gps;
Radio radio;
Thermometer thermometer;

void setup(void) {
  initSensors();
  radio.send("Ready to roll.\r\n");
}

void loop(void) {
  radio.send("Roll: ");
  radio.send(accelerometer.getRoll());

  radio.send(" | Pitch: ");
  radio.send(accelerometer.getPitch());

  radio.send(" | Heading: ");
  radio.send(accelerometer.getHeading());

  radio.send(" | Press. Alt: ");
  radio.send(barometer.getAltitudeAboveSeaLevel());

  radio.send(" | AGL: ");
  radio.send(barometer.getAltitudeAboveGround());

  radio.send(" | Temp: ");
  radio.send(thermometer.getTemperature());

  radio.send(" | ISO8601: ");
  radio.send(gps.getIso8601());

  radio.send(" | Location: ");
  radio.send(gps.getLatitude(), 6);
  radio.send(", ");
  radio.send(gps.getLongitude(), 6);

  radio.send(" | Speed (kt): ");
  radio.send(gps.getSpeed());

  radio.send(" | Altitude: ");
  radio.send(gps.getAltitude());

  radio.send(" | Quality: ");
  radio.send(gps.getQuality());

  radio.send("\r\n");
  delay(1000);
}

void initSensors(void) {
  if(!accelerometer.init()) {
    printInitError("Failed to intialize accelerometer");
  }

  if(!barometer.init()) {
    printInitError("Failed to intialize barometer");
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

void printInitError(const char* const message) {
  while(1) {
    radio.send(message);
    radio.send("\r\n");
    delay(1000);
  }
}
