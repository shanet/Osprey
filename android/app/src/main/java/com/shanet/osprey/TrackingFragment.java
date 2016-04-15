package com.shanet.osprey;

import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.graphics.drawable.Drawable;
import android.graphics.Matrix;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

public class TrackingFragment extends DatasetFragment implements LocationListener, SensorEventListener {
  private static final int EARTH_RADIUS = 6371009;

  private double rocketLatitude;
  private double rocketLongitude;
  private double userLatitude;
  private double userLongitude;
  private float heading;

  private Sensor orientationSensor;
  private SensorManager sensorManager;

  private ImageView arrowDisplay;
  private TextView rocketCoordinatesDisplay;
  private TextView userCoordinatesDisplay;
  private TextView distanceDisplay;
  private TextView relativeBearingDisplay;
  private TextView headingDisplay;
  private TextView magneticBearingDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.tracking_fragment, null);

    heading = 0;

    arrowDisplay = (ImageView)layout.findViewById(R.id.arrow);
    rocketCoordinatesDisplay = (TextView)layout.findViewById(R.id.rocket_coordinates_display);
    userCoordinatesDisplay = (TextView)layout.findViewById(R.id.user_coordinates_display);
    distanceDisplay = (TextView)layout.findViewById(R.id.distance_display);

    relativeBearingDisplay = (TextView)layout.findViewById(R.id.relative_bearing_display);
    headingDisplay = (TextView)layout.findViewById(R.id.heading_display);
    magneticBearingDisplay = (TextView)layout.findViewById(R.id.magnetic_bearing_display);

    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
    orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    return layout;
  }

  public void onResume() {
    super.onResume();
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  public void onPause() {
    super.onPause();
    sensorManager.unregisterListener(this);
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    // Update the rocket coordinates label
    String coordinates = dataset.getCoordinates();
    rocketCoordinatesDisplay.setText(coordinates != null ? coordinates : getString(R.string.default_coordinates));

    Double latitude = (Double)dataset.getField("latitude");
    Double longitude = (Double)dataset.getField("longitude");

    // Only update the rocket position if coordinates exist
    if(latitude != null && longitude != null) {
      rocketLatitude = latitude.doubleValue();
      rocketLongitude = longitude.doubleValue();

      updateDistance();
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_tracking);
  }

  // Location listener methods
  // ---------------------------------------------------------------------------------------------------
  public void onLocationChanged(Location location) {
    userLatitude = location.getLatitude();
    userLongitude = location.getLongitude();

    // Update the user coordinates label
    userCoordinatesDisplay.setText(String.format("%f, %f", userLatitude, userLongitude));

    updateDistance();
    updateBearing();
  }

  public void onProviderEnabled(String provider) {}
  public void onProviderDisabled(String provider) {}
  public void onStatusChanged(String provider, int status, Bundle extras) {}
  // ---------------------------------------------------------------------------------------------------

  // Sensor listener methods
  // ---------------------------------------------------------------------------------------------------
  public void onSensorChanged(SensorEvent event) {
    heading = event.values[0];
    updateBearing();
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  // ---------------------------------------------------------------------------------------------------

  private void updateDistance() {
    // https://en.wikipedia.org/wiki/Haversine_formula
    double deltaLatitude = Math.toRadians(userLatitude - rocketLatitude);
    double deltaLongitude = Math.toRadians(userLongitude - rocketLongitude);

    double sinDeltaLatitude = Math.sin(deltaLatitude / 2);
    double sinDeltaLongitude = Math.sin(deltaLongitude / 2);

    double intermediate = Math.pow(sinDeltaLatitude, 2)
      + Math.cos(Math.toRadians(rocketLatitude))
      * Math.cos(Math.toRadians(userLatitude))
      * Math.pow(sinDeltaLongitude, 2);

    double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(intermediate));

    distanceDisplay.setText(String.format("%.2fm", distance));
  }

  private void updateBearing() {
    // Calculate the magnetic bearing from the rocket's location to magnetic north
    double deltaLongitude = Math.toRadians(rocketLongitude - userLongitude);

    double y = Math.sin(deltaLongitude) * Math.cos(Math.toRadians(rocketLatitude));
    double x = Math.cos(Math.toRadians(userLatitude)) * Math.sin(Math.toRadians(rocketLatitude)) -
      Math.sin(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(rocketLatitude)) * Math.cos(deltaLongitude);

    double magneticBearing = Math.toDegrees(Math.atan2(y, x));
    magneticBearing = (magneticBearing + 360) % 360;

    // RB = MB - MH
    double relativeBearing = magneticBearing - heading;

    relativeBearingDisplay.setText(String.format("Relative bearing: %.0f\u00b0", relativeBearing));
    headingDisplay.setText(String.format("Heading: %.0f\u00b0", heading));
    magneticBearingDisplay.setText(String.format("Magnetic bearing: %.0f\u00b0", magneticBearing));

    // Rotate the arrow
    Matrix matrix = new Matrix();
    matrix.setRotate((float)relativeBearing, arrowDisplay.getDrawable().getBounds().width()/2, arrowDisplay.getDrawable().getBounds().height()/2);

    arrowDisplay.setScaleType(ImageView.ScaleType.MATRIX);
    arrowDisplay.setImageMatrix(matrix);
  }
}
