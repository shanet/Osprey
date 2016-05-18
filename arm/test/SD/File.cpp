#include "SD/SD.h"

File::File(void) {}

size_t File::write(const char *buffer) {
  return 0;
}

void File::flush() {}
void File::close() {}

File::operator bool() {
  return true;
}
