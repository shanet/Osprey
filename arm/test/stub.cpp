#include "stub.h"

using string = std::string;
using map = std::map<std::string, stub_t>;

FILE* Stub::file;
map Stub::current;

field_t Stub::fields[] = {
  {"acceleration", FIELD_FLOAT},
  {"pressure_altitude", FIELD_FLOAT},
  {"expected_phase", FIELD_INT},
  {"expected_apogee_cause", FIELD_INT},
};

int Stub::open(const char *input) {
  file = fopen(input, "r");
  return (file != NULL);
}

int Stub::read() {
  if(file == NULL) return 0;

  char buffer[BUFFER];

  if(fgets(buffer, BUFFER, file) == NULL) {
    return 0;
  }

  // Go around again if a blank line or comment
  if(*buffer == '\n' || *buffer == '/') return read();

  json current = json::parse(buffer);
  updateMap(current);

  return 1;
}

void Stub::updateMap(json current) {
  // Update each of the defined fields
  for(unsigned int i=0; i<sizeof(fields)/sizeof(fields[0]); i++) {
    string field = fields[i].field;

    // Only update the field if it exists in the json object
    if(current.find(field) != current.end()) {
      stub_t data;

      if(fields[i].type == FIELD_FLOAT) {
        data.floatVal = current[field];
      } else {
        data.intVal = current[field];
      }

      this->current[field] = data;
    }
  }
}

void Stub::close() {
  fclose(file);
  file = NULL;
}

stub_t Stub::getField(string field) {
  return current[field];
}

void Stub::setField(std::string field, stub_t data) {
  current[field] = data;
}
