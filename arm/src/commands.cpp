#include "commands.h"

void processCommand() {
  char *message = radio.recv();

  // If an empty string, do nothing
  if(*message == '\0') return;

  // Message format: "[command][argument]"
  // Subtract ASCII 0 as the poor man's char to int conversion
  int command = *message - '0';
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
    case COMMAND_SET_EVENT:
      setEvent(arg);
      break;
    case COMMAND_FIRE_EVENT:
      fireEvent(arg);
      break;
    case COMMAND_ARM_IGNITER:
      armIgniter(arg);
      break;
    case COMMAND_DISARM_IGNITER:
      disarmIgniter(arg);
      break;
    default:
      commandStatus = COMMAND_ERR;
      break;
  }

  // Clear the current command after we've processed it
  radio.clear();
}

int startFlight(char *arg) {
  if(zeroSensors(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  if(armIgniter(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  if(enableLogging(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  // Reset the status of if the igniters fired or not for the new flight
  // This is only used for reporting and does not affect the behavior of
  // the firing of igniters once they are armed
  event.resetFiredStatus();

  return commandStatus;
}

int endFlight(char *arg) {
  if(disarmIgniter(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  if(disableLogging(arg) == COMMAND_ERR) {
    return commandStatus;
  }

  return commandStatus;
}

int zeroSensors(char *arg) {
  barometer.zero();
  clock.reset();

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

int setEvent(char *arg) {
  int eventNum = *arg - '0';
  int altitude = atoi(arg+1);

  event.set(eventNum, altitude);

  commandStatus = COMMAND_ACK;
  return commandStatus;
}

int fireEvent(char *arg) {
  // We can't fire if not armed
  if(event.isArmed() == 0) {
    commandStatus = COMMAND_ERR;
    return commandStatus;
  }

  event.fire(*arg - '0');

  commandStatus = COMMAND_ACK;
  return commandStatus;
}

int armIgniter(char *arg) {
  event.arm();

  commandStatus = COMMAND_ACK;
  return commandStatus;
}

int disarmIgniter(char *arg) {
  event.disarm();

  commandStatus = COMMAND_ACK;
  return commandStatus;
}
