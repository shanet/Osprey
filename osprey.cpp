#include "osprey.h"

Accelerometer accelerometer;
Barometer barometer;
GPS gps;
Thermometer thermometer;

void setup(void) {
  Serial.begin(115200);
  initSensors();
  Serial.println("Ready to roll.\n");
}

void loop(void) {
  Serial.print("Roll: ");
  Serial.print(accelerometer.getRoll());

  Serial.print(" | Pitch: ");
  Serial.print(accelerometer.getPitch());

  Serial.print(" | Heading: ");
  Serial.print(accelerometer.getHeading());

  Serial.print(" | Press. Alt: ");
  Serial.print(barometer.getAltitudeAboveSeaLevel());

  Serial.print(" | AGL: ");
  Serial.print(barometer.getAltitudeAboveGround());

  Serial.print(" | Temp: ");
  Serial.print(thermometer.getTemperature());

  Adafruit_GPS *gps_data = gps.getGPS();

  if(gps_data) {
    if(gps_data->fix) {
      Serial.print(" | Time: ");
      Serial.print(gps_data->hour, DEC);
      Serial.print(':');
      Serial.print(gps_data->minute, DEC);
      Serial.print(':');
      Serial.print(gps_data->seconds, DEC);
      Serial.print('.');
      Serial.print(gps_data->milliseconds);

      Serial.print(" | Date: ");
      Serial.print(gps_data->day, DEC);
      Serial.print('/');
      Serial.print(gps_data->month, DEC);
      Serial.print("/20");
      Serial.print(gps_data->year, DEC);

      Serial.print(" | Fix: ");
      Serial.print((int)gps_data->fix);

      Serial.print(" | Quality: ");
      Serial.print((int)gps_data->fixquality);

      Serial.print(" | Location: ");
      Serial.print(gps_data->latitude, 4);
      Serial.print(gps_data->lat);
      Serial.print(", ");
      Serial.print(gps_data->longitude, 4);
      Serial.print(gps_data->lon);

      Serial.print(" | Location (degrees): ");
      Serial.print(gps_data->latitudeDegrees, 4);
      Serial.print(", ");
      Serial.print(gps_data->longitudeDegrees, 4);

      Serial.print(" | Speed (kt): ");
      Serial.print(gps_data->speed);

      Serial.print(" | Angle: ");
      Serial.print(gps_data->angle);

      Serial.print(" | Altitude: ");
      Serial.print(gps_data->altitude);

      Serial.print(" | Satellites: ");
      Serial.print((int)gps_data->satellites);
    } else {
      Serial.print(" | No GPS Fix");
    }
  }

  Serial.println("");
  delay(1000);
}

void initSensors(void) {
  if(!accelerometer.init()) {
    printInitError("Failed to intialize accelerometer");
  }

  if(!barometer.init()) {
    printInitError("Failed to intialize barometer");
  }

  if(!thermometer.init()) {
    printInitError("Failed to intialize thermometer");
  }

  if(!gps.init()) {
    printInitError("Failed to intialize GPS");
  }
}

void printInitError(const char* const message) {
  while(1) {
    Serial.println(message);
    delay(1000);
  }
}
