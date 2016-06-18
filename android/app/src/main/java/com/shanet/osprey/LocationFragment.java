package com.shanet.osprey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v4.app.DialogFragment;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public abstract class LocationFragment extends DatasetFragment implements LocationListener, TrackedLocationDialogFragment.TrackedLocationDialogListener {
  // Location listener methods
  // ---------------------------------------------------------------------------------------------------
  public abstract void onLocationChanged(Location location);

  public void onProviderDisabled(String provider) {}
  public void onProviderEnabled(String provider) {}
  public void onStatusChanged(String provider, int status, Bundle extras) {}
  // ---------------------------------------------------------------------------------------------------

  // Last known/tracked location methods
  // ---------------------------------------------------------------------------------------------------
  protected abstract void showLocation(Map<String, Double> location);

  protected void setLastKnownLocation(float latitude, float longitude) {
    setLocationPreference(latitude, longitude, R.string.last_known_latitude, R.string.last_known_longitude);
  }

  protected void setTrackedLocation() {
    Map<String, Double> trackedLocation = getTrackedLocation();

    Double latitude = (trackedLocation != null ? trackedLocation.get("latitude") : null);
    Double longitude = (trackedLocation != null ? trackedLocation.get("longitude") : null);

    DialogFragment dialog = new TrackedLocationDialogFragment(this, latitude, longitude);
    dialog.show(getActivity().getSupportFragmentManager(), "TrackedLocationDialog");
  }

  public void onTrackedLocationChanged(float latitude, float longitude) {
    setLocationPreference(latitude, longitude, R.string.tracked_latitude, R.string.tracked_longitude);
  }

  protected void setLocationPreference(float latitude, float longitude, int latitudeKey, int longitudeKey) {
    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    editor.putFloat(getString(latitudeKey), latitude);
    editor.putFloat(getString(longitudeKey), longitude);

    editor.commit();
  }

  protected Map<String, Double> getLastKnownLocation() {
    return getLocationPreference(R.string.last_known_latitude, R.string.last_known_longitude);
  }

  protected Map<String, Double> getTrackedLocation() {
    return getLocationPreference(R.string.tracked_latitude, R.string.tracked_longitude);
  }

  protected Map<String, Double> getLocationPreference(int latitudeKey, int longitudeKey) {
    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    double latitude = preferences.getFloat(getString(latitudeKey), 0);
    double longitude = preferences.getFloat(getString(longitudeKey), 0);

    // Treat (0, 0) as no location
    if(latitude == 0 && longitude == 0) {
      return null;
    }

    Map<String, Double> location = new HashMap<String, Double>();
    location.put("latitude", new Double(latitude));
    location.put("longitude", new Double(longitude));

    return location;
  }

  protected void showLastKnownLocation() {
    Map<String, Double> lastKnownLocation = getLastKnownLocation();

    if(lastKnownLocation == null) {
      showNoLastKnownLocationToast();
      return;
    }

    showLocation(lastKnownLocation);
  }

  protected void showTrackedLocation() {
    Map<String, Double> trackedLocation = getTrackedLocation();

    if(trackedLocation == null) {
      showNoTrackedLocationToast();
      return;
    }

    showLocation(trackedLocation);
  }

  protected void showNoLastKnownLocationToast() {
    Toast.makeText(getActivity(), R.string.no_last_known_location, Toast.LENGTH_SHORT).show();
  }

  protected void showNoTrackedLocationToast() {
    Toast.makeText(getActivity(), R.string.no_tracked_location, Toast.LENGTH_SHORT).show();
  }
  // ---------------------------------------------------------------------------------------------------
}
