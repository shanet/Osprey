#ifndef SD_H
#define SD_H

#include <fcntl.h>
#include <stdint.h>

#define FILE_READ O_RDONLY
#define FILE_WRITE O_WRONLY

class File {
  public:
    File(void);

    size_t write(const char *buffer);
    void flush();
    void close();
    operator bool();
};

// ----------------------------------------------------------------------------------

class SDClass {
  public:
    bool begin(uint8_t csPin = 0);
    File open(const char *filename, uint8_t mode = O_WRONLY);
    bool exists(char *filepath);
};

extern SDClass SD;

#endif
