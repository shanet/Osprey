#include "osprey.h"

void setup(void) {
  initSensors();
  radio.send("Ready to roll.\r\n");
}

void loop(void) {
  printJSON();
  processCommand();
  delay(1000);
}

void printJSON() {
  // The JSON structure is simple enough. Rather than bringing in another library to do a bunch
  // of heavylifting, just construct the string manually.

  radio.send("{");

  radio.send("\"roll\": ");
  radio.send(accelerometer.getRoll());

  radio.send(", \"pitch\": ");
  radio.send(accelerometer.getPitch());

  radio.send(", \"heading\": ");
  radio.send(accelerometer.getHeading());

  radio.send(", \"pressure_altitude\": ");
  radio.send(barometer.getAltitudeAboveSeaLevel());

  radio.send(", \"agl\": ");
  radio.send(barometer.getAltitudeAboveGround());

  radio.send(", \"temp\": ");
  radio.send(thermometer.getTemperature());

  radio.send(", \"iso8601\": \"");
  radio.send(gps.getIso8601());
  radio.send("\"");

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
  radio.send(radio.recv());
  radio.send("\"");

  radio.send(", \"pressure_setting\": ");
  radio.send(barometer.getPressureSetting());

  radio.send("}");
  radio.send("\r\n");
}

void processCommand() {
  char *message = radio.recv();

  // Message format: "[command][argument]"
  // Subtract ASCII 0 as the poor man's char to int conversion
  int command = message[0] - '0';
  char *arg = message+1;

  switch(command) {
    case COMMAND_ZERO_SENSORS:
      barometer.zero();
      commandStatus = COMMAND_ACK;
      break;
    case COMMAND_SET_PRESSURE:
      barometer.setPressureSetting(atof(arg));
      commandStatus = COMMAND_ACK;
      break;
    default:
      commandStatus = COMMAND_ERR;
      break;
  }
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
