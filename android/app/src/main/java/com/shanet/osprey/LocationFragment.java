package com.shanet.osprey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public abstract class LocationFragment extends DatasetFragment implements LocationListener {
  // Location listener methods
  // ---------------------------------------------------------------------------------------------------
  public abstract void onLocationChanged(Location location);

  public void onProviderDisabled(String provider) {}
  public void onProviderEnabled(String provider) {}
  public void onStatusChanged(String provider, int status, Bundle extras) {}
  // ---------------------------------------------------------------------------------------------------

  // Last known location methods
  // ---------------------------------------------------------------------------------------------------
  protected abstract void showLastKnownLocation();

  protected void updateLastKnownLocation(float latitude, float longitude) {
    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    editor.putFloat(getString(R.string.last_known_latitude), latitude);
    editor.putFloat(getString(R.string.last_known_longitude), longitude);

    editor.commit();
  }

  protected Map<String, Double> getLastKnownLocation() {
    Map<String, Double> lastKnownLocation = new HashMap<String, Double>();

    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    double latitude = preferences.getFloat(getString(R.string.last_known_latitude), 0);
    double longitude = preferences.getFloat(getString(R.string.last_known_longitude), 0);

    // Treat (0, 0) as no location
    if(latitude == 0 && longitude == 0) {
      return null;
    }

    lastKnownLocation.put("latitude", new Double(latitude));
    lastKnownLocation.put("longitude", new Double(longitude));

    return lastKnownLocation;
  }

  protected void showNoLastKnownLocationToast() {
    Toast.makeText(getActivity(), R.string.no_last_known_location, Toast.LENGTH_SHORT).show();
  }
  // ---------------------------------------------------------------------------------------------------
}
