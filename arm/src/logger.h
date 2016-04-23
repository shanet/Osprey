#ifndef LOGGER_H
#define LOGGER_H

#define SD_CHIP_SELECT 4
#define FILENAME_FORMAT "%d.log" // https://en.wikipedia.org/wiki/8.3_filename

#include <SPI.h>
#include <SD.h>

#include "constants.h"
#include "sensor.h"

using namespace std;

class Logger : public virtual Sensor {
  public:
    Logger();
    int init();
    int open();
    void close();
    void flush();
    void log(const char* message);

  protected:
    File file;
};

#endif
