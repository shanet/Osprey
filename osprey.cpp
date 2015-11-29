#include "osprey.h"

void setup(void) {
  Serial.begin(9600);
  initSensors();
  Serial.println("Ready to roll.\n");
}

void loop(void) {
  Serial.print("Roll: ");
  Serial.print(getRoll());

  Serial.print(" | Pitch: ");
  Serial.print(getPitch());

  Serial.print(" | Heading: ");
  Serial.print(getHeading());

  Serial.print(" | Press. Alt: ");
  Serial.print(getAltitudeAboveSeaLevel());

  Serial.print(" | AGL: ");
  Serial.print(getAltitudeAboveGround());

  Serial.print(" | Temp: ");
  Serial.print(getTemperature());

  Serial.println("");
  delay(1000);
}

void initSensors(void) {
  if(!initAccelerometer()) {
    printInitError("Failed to intialize accelerometer");
  }

  if(!initBarometer()) {
    printInitError("Failed to intialize barometer");
  }

  if(!initThermometer()) {
    printInitError("Failed to intialize thermometer");
  }
}

void printInitError(const char* const message) {
  while(1) {
    Serial.println(message);
    delay(1000);
  }
}
