#include "SD/SD.h"

SDClass SD;

bool SDClass::begin(uint8_t csPin) {
  return true;
}

File SDClass::open(const char *filename, uint8_t mode) {
  return File();
}

bool SDClass::exists(char *filepath) {
  return false;
}
