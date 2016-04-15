package com.shanet.osprey;

import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LocationFragment extends DatasetFragment implements LocationListener, MapboxMap.OnScrollListener, MapStyleDialogFragment.MapStyleDialogListener {
  private static final int DEFAULT_ZOOM = 15;
  private static final int ORANGE = 0xFFFFAA00; // wtf? android has a constant for magenta but not orange?

  private boolean mapFollow;
  private LinkedList<LatLng> mapPathPoints;

  private MapboxMap map;
  private MapView mapView;
  private Marker mapRocketMarker;
  private Polyline mapRocketPath;
  private Polyline mapUserPath;

  private String mapStyle;
  private TextView coordinatesDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.location_fragment, null);

    coordinatesDisplay = (TextView)layout.findViewById(R.id.coordinates_display);
    mapView = ((MapView)layout.findViewById(R.id.map));
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(onMapReady);

    mapFollow = true;
    mapStyle = Style.SATELLITE_STREETS;
    mapPathPoints = new LinkedList<LatLng>();

    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    // Tell Android this fragment has an options menu
    setHasOptionsMenu(true);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    String coordinates = dataset.getCoordinates();

    // Update the coordinates label
    coordinatesDisplay.setText(coordinates != null ? coordinates : getActivity().getString(R.string.default_coordinates));

    Double latitude = (Double)dataset.getField("latitude");
    Double longitude = (Double)dataset.getField("longitude");

    // Only move the map if coordinates exist and the map has been initialized
    if(map != null && latitude != null && longitude != null) {
      // Add a new point to the list of points and draw a new line
      mapPathPoints.push(new LatLng(latitude, longitude));

      if(mapFollow) {
        updateMapCamera(latitude.doubleValue(), longitude.doubleValue(), DEFAULT_ZOOM);
      }

      mapRocketMarker = updateMapMarker(mapRocketMarker, latitude.doubleValue(), longitude.doubleValue());
      mapRocketPath = updateMapPath(mapRocketPath, mapPathPoints, ORANGE);
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_location);
  }

  // Map methods
  // ---------------------------------------------------------------------------------------------------
  private OnMapReadyCallback onMapReady = new OnMapReadyCallback() {
    public void onMapReady(MapboxMap mapboxMap) {
      map = mapboxMap;

      map.setStyleUrl(mapStyle);
      map.setMyLocationEnabled(true);
      map.setOnScrollListener(LocationFragment.this);
    };
  };

  private void updateMapCamera(double latitude, double longitude, int zoom) {
    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
      .zoom(DEFAULT_ZOOM)
      .build();

    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
    map.easeCamera(cameraUpdate, 1000);
  }

  private Marker updateMapMarker(Marker marker, double latitude, double longitude) {
    if(marker != null) {
      marker.remove();
    }

    return map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
  }

  private Polyline updateMapPath(Polyline line, List<LatLng> points, int color) {
    if(line != null) {
      line.remove();
    }

    return map.addPolyline(new PolylineOptions()
      .width(3)
      .color(color)
      .alpha(.9f)
      .addAll(points)
    );
  }

  public void onScroll() {
    // If the user tries to scroll the map disable map following
    mapFollow = false;

    // Ensure that the map following menu option gets updated
    getActivity().invalidateOptionsMenu();
  }

  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  public void onPause() {
    super.onPause();
    mapView.onPause();
  }
  // ---------------------------------------------------------------------------------------------------

  // Location listener methods
  // ---------------------------------------------------------------------------------------------------
  public void onLocationChanged(Location location) {
    // If we don't know the rocket location yet, there's no line to plot
    if(mapRocketMarker == null) return;

    double latitude = location.getLatitude();
    double longitude = location.getLongitude();

    // Create a new path from the user's current location to the rocket's last known location
    List<LatLng> points = new ArrayList<LatLng>();
    points.add(new LatLng(latitude, longitude));
    points.add(mapRocketMarker.getPosition());

    mapUserPath = updateMapPath(mapUserPath, points, Color.RED);
  }

  public void onProviderDisabled(String provider) {
    DialogUtils.displayErrorDialog(getActivity(), R.string.dialog_title_disabled_gps, R.string.dialog_disabled_gps);
  }

  public void onProviderEnabled(String provider) {}
  public void onStatusChanged(String provider, int status, Bundle extras) {}
  // ---------------------------------------------------------------------------------------------------

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.location_option_menu, menu);

    // Set the map follow checkbox accordingly
    MenuItem item = menu.findItem(R.id.map_follow_option);
    item.setChecked(mapFollow);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.map_style_option:
        MapStyleDialogFragment dialog = new MapStyleDialogFragment(this, mapStyle);
        dialog.show(getActivity().getSupportFragmentManager(), "MapStyleDialog");
        return true;
      case R.id.map_follow_option:
        item.setChecked(!item.isChecked());
        mapFollow = item.isChecked();
        return true;
    }

    return false;
  }

  public void onMapStyleChanged(String style) {
    mapStyle = style;
    map.setStyleUrl(style);
  }
  // ---------------------------------------------------------------------------------------------------
}
