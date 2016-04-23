#ifndef ACCELEROMETER_H
#define ACCELEROMETER_H

#include <Adafruit_LSM303DLHC/Adafruit_LSM303_U.h>
#include <Adafruit_L3GD20_U/Adafruit_L3GD20_U.h>
#include <Adafruit_10DOF/Adafruit_10DOF.h>
#include <math.h>

#include "constants.h"
#include "kalman.h"
#include "sensor.h"

#define KALMAN_PROCESS_NOISE 0.01
#define KALMAN_MEASUREMENT_NOISE 0.25
#define KALMAN_ERROR 1

using namespace std;

class Accelerometer : public virtual Sensor {
  public:
    Accelerometer();
    int init();
    float getRoll();
    float getPitch();
    float getHeading();
    float getAcceleration();
    float getRawAcceleration();
    void getAccelOrientation(sensors_vec_t *orientation);
    void getMagOrientation(sensors_vec_t *orientation);

  protected:
    static Adafruit_10DOF dof;
    static Adafruit_LSM303_Accel_Unified accelerometer;
    static Adafruit_LSM303_Mag_Unified magnetometer;

    kalman_t roll;
    kalman_t pitch;
    kalman_t heading;
    kalman_t acceleration;
};

#endif
