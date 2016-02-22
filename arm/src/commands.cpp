#include "commands.h"

void processCommand() {
  char *message = radio.recv();

  // Message format: "[command][argument]"
  // Subtract ASCII 0 as the poor man's char to int conversion
  int command = message[0] - '0';
  char *arg = message+1;

  switch(command) {
    case COMMAND_START_FLIGHT:
      startFlight(arg);
      break;
    case COMMAND_END_FLIGHT:
      endFlight(arg);
      break;
    case COMMAND_ZERO_SENSORS:
      zeroSensors(arg);
      break;
    case COMMAND_SET_PRESSURE:
      setPressure(arg);
      break;
    case COMMAND_ENABLE_LOGGING:
      enableLogging(arg);
      break;
    case COMMAND_DISABLE_LOGGING:
      disableLogging(arg);
      break;
    default:
      commandStatus = COMMAND_ERR;
      break;
  }
}

int startFlight(char *arg) {
  if(zeroSensors(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  if(enableLogging(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  return commandStatus;
}

int endFlight(char *arg) {
  if(disableLogging(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  return commandStatus;
}

int zeroSensors(char *arg) {
  barometer.zero();
  commandStatus = COMMAND_ACK;
  return commandStatus;
}

int setPressure(char *arg) {
  barometer.setPressureSetting(atof(arg));
  commandStatus = COMMAND_ACK;
  return commandStatus;
}

int enableLogging(char *arg) {
  if(radio.enableLogging()) {
    commandStatus = COMMAND_ACK;
  } else {
    commandStatus = COMMAND_ERR;
  }

  return commandStatus;
}

int disableLogging(char *arg) {
  if(radio.disableLogging()) {
    commandStatus = COMMAND_ACK;
  } else {
    commandStatus = COMMAND_ERR;
  }

  return commandStatus;
}
