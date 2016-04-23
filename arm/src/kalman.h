#ifndef KALMAN_H
#define KALMAN_H

typedef struct {
  float processNoise;     // process noise covariance
  float measurementNoise; // measurement noise covariance
  float value;            // value
  float error;            // estimation error covariance
  float gain;             // kalman gain
} kalman_t;

#endif
