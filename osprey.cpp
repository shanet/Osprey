#include "osprey.h"

void setup(void) {
  Serial.begin(115200);
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

  Adafruit_GPS *gps = getGPS();

  if(gps) {
    if(gps->fix) {
      Serial.print(" | Time: ");
      Serial.print(gps->hour, DEC);
      Serial.print(':');
      Serial.print(gps->minute, DEC);
      Serial.print(':');
      Serial.print(gps->seconds, DEC);
      Serial.print('.');
      Serial.print(gps->milliseconds);

      Serial.print(" | Date: ");
      Serial.print(gps->day, DEC);
      Serial.print('/');
      Serial.print(gps->month, DEC);
      Serial.print("/20");
      Serial.print(gps->year, DEC);

      Serial.print(" | Fix: ");
      Serial.print((int)gps->fix);

      Serial.print(" | Quality: ");
      Serial.print((int)gps->fixquality);

      Serial.print(" | Location: ");
      Serial.print(gps->latitude, 4);
      Serial.print(gps->lat);
      Serial.print(", ");
      Serial.print(gps->longitude, 4);
      Serial.print(gps->lon);

      Serial.print(" | Location (degrees): ");
      Serial.print(gps->latitudeDegrees, 4);
      Serial.print(", ");
      Serial.print(gps->longitudeDegrees, 4);

      Serial.print(" | Speed (kt): ");
      Serial.print(gps->speed);

      Serial.print(" | Angle: ");
      Serial.print(gps->angle);

      Serial.print(" | Altitude: ");
      Serial.print(gps->altitude);

      Serial.print(" | Satellites: ");
      Serial.print((int)gps->satellites);
    } else {
      Serial.print(" | No GPS Fix");
    }
  }

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

  if(!initGPS()) {
    printInitError("Failed to intialize GPS");
  }
}

void printInitError(const char* const message) {
  while(1) {
    Serial.println(message);
    delay(1000);
  }
}
