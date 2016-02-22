#ifndef ACCELEROMETER_H
#define ACCELEROMETER_H

#include <Adafruit_LSM303DLHC/Adafruit_LSM303_U.h>
#include <Adafruit_L3GD20_U/Adafruit_L3GD20_U.h>
#include <Adafruit_10DOF/Adafruit_10DOF.h>

#include "constants.h"
#include "sensor.h"

using namespace std;

class Accelerometer : public virtual Sensor {
  public:
    Accelerometer();
    int init();
    float getRoll();
    float getPitch();
    float getHeading();
    void getAccelOrientation(sensors_vec_t *orientation);
    void getMagOrientation(sensors_vec_t *orientation);

  protected:
    static Adafruit_10DOF dof;
    static Adafruit_LSM303_Accel_Unified accelerometer;
    static Adafruit_LSM303_Mag_Unified magnetometer;
};

#endif
