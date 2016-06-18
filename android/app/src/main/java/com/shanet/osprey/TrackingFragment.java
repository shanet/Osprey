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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class TrackingFragment extends LocationFragment implements SensorEventListener {
  private static final int EARTH_RADIUS = 6371009;

  private Double rocketLatitude;
  private Double rocketLongitude;
  private double deviceLatitude;
  private double deviceLongitude;

  private float distance;
  private float heading;
  private float relativeBearing;

  private Sensor orientationSensor;
  private SensorManager sensorManager;

  private ImageView arrowDisplay;
  private ImageView compassDisplay;
  private TextView vectorDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.tracking_fragment, null);

    rocketLatitude = null;
    rocketLongitude = null;

    distance = 0;
    heading = 0;
    relativeBearing = 0;

    arrowDisplay = (ImageView)layout.findViewById(R.id.arrow);
    compassDisplay = (ImageView)layout.findViewById(R.id.compass);
    vectorDisplay = (TextView)layout.findViewById(R.id.vector_display);

    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
    orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    // Tell Android this fragment has an options menu
    setHasOptionsMenu(true);

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

    rocketLatitude = (Double)dataset.getField("latitude");
    rocketLongitude = (Double)dataset.getField("longitude");
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_tracking);
  }

  // Location listener methods
  // ---------------------------------------------------------------------------------------------------
  public void onLocationChanged(Location location) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    deviceLatitude = location.getLatitude();
    deviceLongitude = location.getLongitude();

    if(rocketLatitude != null && rocketLongitude != null) {
      updateDistance();
      updateRelativeBearing();
      updateVector();
      updateImage(arrowDisplay, relativeBearing);
    }
  }
  // ---------------------------------------------------------------------------------------------------

  // Sensor listener methods
  // ---------------------------------------------------------------------------------------------------
  public void onSensorChanged(SensorEvent event) {
    heading = event.values[0];

    updateImage(compassDisplay, 360f - (float)heading);

    if(rocketLatitude != null && rocketLongitude != null) {
      updateRelativeBearing();
      updateVector();
      updateImage(arrowDisplay, relativeBearing);
    }
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  // ---------------------------------------------------------------------------------------------------

  private void updateDistance() {
    double rocketLatitude = this.rocketLatitude.doubleValue();
    double rocketLongitude = this.rocketLongitude.doubleValue();

    // https://en.wikipedia.org/wiki/Haversine_formula
    double deltaLatitude = Math.toRadians(deviceLatitude - rocketLatitude);
    double deltaLongitude = Math.toRadians(deviceLongitude - rocketLongitude);

    double sinDeltaLatitude = Math.sin(deltaLatitude / 2);
    double sinDeltaLongitude = Math.sin(deltaLongitude / 2);

    double intermediate = Math.pow(sinDeltaLatitude, 2)
      + Math.cos(Math.toRadians(rocketLatitude))
      * Math.cos(Math.toRadians(deviceLatitude))
      * Math.pow(sinDeltaLongitude, 2);

    distance = (float)(2 * EARTH_RADIUS * Math.asin(Math.sqrt(intermediate)));
  }

  private void updateRelativeBearing() {
    // Calculate the magnetic bearing from the rocket's location to magnetic north
    double deltaLongitude = Math.toRadians(rocketLongitude - deviceLongitude);

    double y = Math.sin(deltaLongitude) * Math.cos(Math.toRadians(rocketLatitude));
    double x = Math.cos(Math.toRadians(deviceLatitude)) * Math.sin(Math.toRadians(rocketLatitude)) -
      Math.sin(Math.toRadians(deviceLatitude)) * Math.cos(Math.toRadians(rocketLatitude)) * Math.cos(deltaLongitude);

    double magneticBearing = Math.toDegrees(Math.atan2(y, x));
    magneticBearing = (magneticBearing + 360) % 360;

    // RB = MB - MH
    relativeBearing = (float)magneticBearing - heading;
  }

  private void updateVector() {
    String vector = String.format("%.2f%s %s %.0f%s", distance, getString(R.string.meters), getString(R.string.at), relativeBearing, getString(R.string.degrees));
    updateDisplay(vectorDisplay, vector, 0);
  }

  private void updateImage(ImageView image, float angle) {
    // Rotate the arrow to the calculated bearing
    Matrix matrix = new Matrix();

    float width = image.getDrawable().getBounds().width();
    float height = image.getDrawable().getBounds().height();
    matrix.setRotate(angle, width/2, height/2);

    image.setScaleType(ImageView.ScaleType.MATRIX);
    image.setImageMatrix(matrix);
  }

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.tracking_option_menu, menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.show_last_known_location_option:
        showLastKnownLocation();
        return true;
      case R.id.show_tracked_location_option_tracking:
        showTrackedLocation();
        return true;
      case R.id.set_tracked_location_option:
        setTrackedLocation();
        return true;
    }

    return false;
  }

  protected void showLocation(Map<String, Double> location) {
    rocketLatitude = location.get("latitude").doubleValue();
    rocketLongitude = location.get("longitude").doubleValue();

    updateDistance();
    updateRelativeBearing();
    updateVector();
    updateImage(arrowDisplay, relativeBearing);
  }
  // ---------------------------------------------------------------------------------------------------
}
