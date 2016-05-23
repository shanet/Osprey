#ifndef STUB_H
#define STUB_H

#include <map>
#include <json/src/json.hpp>
#include <string>

#define BUFFER 1024
#define FIELD_FLOAT 0
#define FIELD_INT 1

using json = nlohmann::json;

union stub_t {
  int intVal;
  float floatVal;
};

struct field_t {
  std::string field;
  int type;
};

extern void SERCOM1_Handler();

class Stub {
  public:
    int open(const char *input);
    void close();
    int read();
    stub_t getField(std::string field);
    void setField(std::string field, union stub_t);

  private:
    static FILE *file;
    static std::map<std::string, stub_t> current;
    static field_t fields[];

    void updateMap(json current);
};

#endif
