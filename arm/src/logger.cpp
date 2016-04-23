#include "logger.h"

Logger::Logger() {}

int Logger::init() {
  if(!SD.begin(SD_CHIP_SELECT)) {
    return 0;
  }

  return 1;
}

int Logger::open() {
  char filename[20];
  int logNumber = 0;

  // Find the first unused log number
  do {
    sprintf(filename, FILENAME_FORMAT, logNumber);
    logNumber++;
  } while(SD.exists(filename));

  file = SD.open(filename, FILE_WRITE);

  return (file ? 1 : 0);
}

void Logger::close() {
  file.close();
}

void Logger::flush() {
  file.flush();
}

void Logger::log(const char* message) {
  file.write(message);
}
