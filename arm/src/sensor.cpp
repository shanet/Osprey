#include "sensor.h"

Sensor::Sensor() {}

Sensor::Sensor(float processNoise, float measurementNoise, float error) {
  this->processNoise = processNoise;
  this->measurementNoise = measurementNoise;
  this->error = error;
}

kalman_t Sensor::kalmanInit(float initialValue) {
  kalman_t kalman;

  kalman.processNoise = processNoise;
  kalman.measurementNoise = measurementNoise;
  kalman.error = error;
  kalman.value = initialValue;

  return kalman;
}

void Sensor::kalmanUpdate(kalman_t* state, float measurement) {
  // Prediction update
  state->error = state->error + state->processNoise;

  // Measurement update
  state->gain = state->error / (state->error + state->measurementNoise);
  state->value = state->value + state->gain * (measurement - state->value);
  state->error = (1 - state->gain) * state->error;
}
