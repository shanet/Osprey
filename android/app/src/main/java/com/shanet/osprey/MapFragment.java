package com.shanet.osprey;

import android.app.NotificationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends LocationFragment implements
  DownloadMapDialogFragment.DownloadMapDialogListener,
  OfflineMapsDialogFragment.OfflineMapsDialogListener,
  MapboxMap.OnScrollListener,
  MapStyleDialogFragment.MapStyleDialogListener {

  private static final int DEFAULT_ZOOM = 15;
  private static final int ORANGE = 0xFFFFAA00; // wtf? android has a constant for magenta but not orange?

  public static final String JSON_CHARSET = "UTF-8";
  public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

  private boolean mapFollow;
  private LinkedList<LatLng> mapPathPoints;

  private MapboxMap map;
  private MapView mapView;
  private Marker mapRocketMarker;

  private NotificationManager notificationManager;
  private NotificationCompat.Builder notificationBuilder;

  private OfflineManager offlineMapManager;

  private Polyline mapRocketPath;
  private Polyline mapUserPath;

  private String mapStyle;
  private TextView coordinatesDisplay;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.map_fragment, null);

    coordinatesDisplay = (TextView)layout.findViewById(R.id.coordinates_display);
    mapView = ((MapView)layout.findViewById(R.id.map));
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(onMapReady);

    mapFollow = true;
    mapStyle = getMapStyle();
    mapPathPoints = new LinkedList<LatLng>();

    offlineMapManager = OfflineManager.getInstance(getActivity());
    offlineMapManager.setAccessToken(getString(R.string.mapbox_api_key));

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

      updateMapMarker(latitude.doubleValue(), longitude.doubleValue());
      mapRocketPath = updateMapPath(mapRocketPath, mapPathPoints, ORANGE);

      updateLastKnownLocation((float)latitude.doubleValue(), (float)longitude.doubleValue());
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_map);
  }

  // Map methods
  // ---------------------------------------------------------------------------------------------------
  private OnMapReadyCallback onMapReady = new OnMapReadyCallback() {
    public void onMapReady(MapboxMap mapboxMap) {
      map = mapboxMap;

      map.setStyleUrl(mapStyle);
      map.setMyLocationEnabled(true);
      map.setOnScrollListener(MapFragment.this);
    };
  };

  private void updateMapCamera(double latitude, double longitude, int zoom) {
    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
      .zoom(DEFAULT_ZOOM)
      .build();

    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
    map.easeCamera(cameraUpdate, 1000);
  }

  private void updateMapMarker(double latitude, double longitude) {
    LatLng position = new LatLng(latitude, longitude);

    // Make a new marker if one doesn't exist yet. Otherwise, update the position of the existing one
    if(mapRocketMarker == null) {
      mapRocketMarker = map.addMarker(new MarkerOptions().position(position));
    } else {
      mapRocketMarker.setPosition(position);
    }
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
  // ---------------------------------------------------------------------------------------------------

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.map_option_menu, menu);

    // Set the map follow checkbox accordingly
    MenuItem item = menu.findItem(R.id.map_follow_option);
    item.setChecked(mapFollow);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    DialogFragment dialog;

    switch(item.getItemId()) {
      case R.id.map_style_option:
        dialog = new MapStyleDialogFragment(this, mapStyle);
        dialog.show(getActivity().getSupportFragmentManager(), "MapStyleDialog");
        return true;
      case R.id.map_follow_option:
        item.setChecked(!item.isChecked());
        mapFollow = item.isChecked();
        return true;
      case R.id.clear_ground_track_option:
        mapPathPoints.clear();
        mapRocketPath.remove();
        return true;
      case R.id.show_last_known_location_option:
        showLastKnownLocation();
        return true;
      case R.id.list_offline_maps_option:
        getOfflineMapsList(new OfflineMapsListListener() {
          public void onOfflineMapsRetrieved(OfflineRegion[] regions) {
            // Pass the regions to the offline map dialog
            DialogFragment dialog = new OfflineMapsDialogFragment(MapFragment.this, regions);
            dialog.show(getActivity().getSupportFragmentManager(), "OfflineMapsDialog");
          }
        });
        return true;
      case R.id.download_map_option:
        dialog = new DownloadMapDialogFragment(this);
        dialog.show(getActivity().getSupportFragmentManager(), "DownloadMapDialog");
        return true;
    }

    return false;
  }

  public void onMapStyleChanged(String style) {
    mapStyle = style;
    map.setStyleUrl(style);
    updateMapStyle();
  }

  protected void showLastKnownLocation() {
    Map<String, Double> lastKnownLocation = getLastKnownLocation();

    if(lastKnownLocation == null) {
      showNoLastKnownLocationToast();
      return;
    }

    double latitude = lastKnownLocation.get("latitude").doubleValue();
    double longitude = lastKnownLocation.get("longitude").doubleValue();

    updateMapCamera(latitude, longitude, DEFAULT_ZOOM);
    updateMapMarker(latitude, longitude);
  }

  public void onMapDownloadRequest(String regionName) {
    downloadOfflineMap(regionName);
    Toast.makeText(getActivity(), R.string.download_started, Toast.LENGTH_SHORT).show();
  }

  public void onOfflineMapSelected(final int region) {
    getOfflineMapsList(new OfflineMapsListListener() {
      public void onOfflineMapsRetrieved(OfflineRegion[] regions) {
        // Show a toast if no regions exist
        if(regions == null || regions.length == 0) {
          Toast.makeText(getActivity(), R.string.no_regions, Toast.LENGTH_SHORT).show();
          return;
        }

        OfflineTilePyramidRegionDefinition regionDefinition = (OfflineTilePyramidRegionDefinition)regions[region].getDefinition();

        // Move the map to the given region
        LatLngBounds bounds = regionDefinition.getBounds();
        double regionZoom = regionDefinition.getMinZoom();

        CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(bounds.getCenter())
          .zoom(regionZoom)
          .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        onMapStyleChanged(regionDefinition.getStyleURL());
      }
    });
  }

  public void onOfflineMapDeleted() {
    Toast.makeText(getActivity(), R.string.map_deleted, Toast.LENGTH_SHORT).show();
  }

  // Shared prefs
  // ---------------------------------------------------------------------------------------------------
  protected void updateMapStyle() {
    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    editor.putString(getString(R.string.map_style), mapStyle);
    editor.commit();
  }

  protected String getMapStyle() {
    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    return preferences.getString(getString(R.string.map_style), "mapbox://styles/mapbox/outdoors-v9");
  }
  // ---------------------------------------------------------------------------------------------------

  // Offline maps
  // ---------------------------------------------------------------------------------------------------
  private void downloadOfflineMap(String regionName) {
    OfflineTilePyramidRegionDefinition regionDefinition = getCurrentMapDefinition();
    byte[] metadata = createOfflineMapMetadata(regionName);
    createMapDownloadNotification();

    // Download the region asynchronously
    offlineMapManager.createOfflineRegion(regionDefinition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
      public void onCreate(OfflineRegion offlineRegion) {
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
          public void onStatusChanged(OfflineRegionStatus status) {
            // Calculate the download percentage and update the download notification
            int percentage = (status.getRequiredResourceCount() >= 0 ? (int)(100 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0);

            if(status.isComplete()) {
              notificationBuilder.setContentText(getString(R.string.map_download_complete)).setProgress(0, 0, false);
            } else if(status.isRequiredResourceCountPrecise()) {
              notificationBuilder.setProgress(100, percentage, false);
            }

            notificationManager.notify(1, notificationBuilder.build());
          }

          public void onError(OfflineRegionError error) {
            notificationBuilder.setContentText(getString(R.string.map_download_error)).setProgress(0, 0, false);
            notificationManager.notify(1, notificationBuilder.build());
          }

          public void mapboxTileCountLimitExceeded(long limit) {
            notificationBuilder.setContentText(getString(R.string.map_download_too_large)).setProgress(0, 0, false);
            notificationManager.notify(1, notificationBuilder.build());
          }
        });
      }

      public void onError(String error) {
        notificationBuilder.setContentText(getString(R.string.map_download_error)).setProgress(0, 0, false);
        notificationManager.notify(1, notificationBuilder.build());
      }
    });
  }

  private void getOfflineMapsList(final OfflineMapsListListener listener) {
    offlineMapManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
      public void onList(final OfflineRegion[] offlineRegions) {
        // Pass the list of regions to the listener
        listener.onOfflineMapsRetrieved(offlineRegions);
      }

      public void onError(String error) {
        Toast.makeText(getActivity(), getString(R.string.offline_map_error), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private OfflineTilePyramidRegionDefinition getCurrentMapDefinition() {
    LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
    double minZoom = map.getCameraPosition().zoom;
    double maxZoom = map.getMaxZoom();
    float pixelRatio = this.getResources().getDisplayMetrics().density;

    return new OfflineTilePyramidRegionDefinition(mapStyle, bounds, minZoom, maxZoom, pixelRatio);
  }

  private byte[] createOfflineMapMetadata(String regionName) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
      return jsonObject.toString().getBytes(JSON_CHARSET);
    } catch(Exception e) {
      return null;
    }
  }

  private void createMapDownloadNotification() {
    notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    notificationBuilder = new NotificationCompat.Builder(getActivity());
    notificationBuilder.setContentTitle(getString(R.string.map_downloading_title))
      .setContentText(getString(R.string.map_downloading))
      .setSmallIcon(R.drawable.ic_launcher);
  }

  private interface OfflineMapsListListener {
    public void onOfflineMapsRetrieved(OfflineRegion[] regions);
  }
  // ---------------------------------------------------------------------------------------------------
}
