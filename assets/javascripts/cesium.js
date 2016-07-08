var LINE_COLOR = [255, 0, 255, 255];
var CAMERA_ANGLE = -20;

var flightPath;
var viewer;

function initCesium() {
  initViewer();

  // Load the flight path and model into Cesium
  viewer.dataSources.add(Cesium.CzmlDataSource.load(getFlightPath())).then(function(dataSource) {
    flightPath = dataSource;
    viewer.entities.add(flightPath.entities.getById('path'));
  });

  resetCamera(true);
  document.getElementById('track_rocket').addEventListener('change', onTrackRocketChange);
}

function initViewer() {
  var imageryProvider = new Cesium.MapboxImageryProvider({
    'mapId': 'mapbox.streets-satellite',
    'accessToken': 'pk.eyJ1Ijoic2hhbmV0IiwiYSI6ImNpbXZnbnBhMjAydDl3a2x1ejNoNWoydHMifQ.WIi_Jv4TO3hOzj-E120rYg',
  });

  viewer = new Cesium.Viewer('cesium', {
    'baseLayerPicker': false,
    'creditContainer': 'credits',
    'fullscreenButton': false,
    'geocoder': false,
    'homeButton': false,
    'imageryProvider': imageryProvider,
    'navigationHelpButton': false,
    'infoBox': false,
    'sceneModePicker': false,
  });

  // Show shadows
  viewer.scene.globe.enableLighting = true;
}

function getFlightPath() {
  return [
    {
      'id': 'document',
      'name': 'CZML Path',
      'version': '1.0',
    },
    {
      'id': 'path',
      'name': 'Rocket flight path',
      'availability': launchTime + '/' + flightEnd,
      'path': {
        'material': {
          'polylineOutline': {
            'color': {'rgba': LINE_COLOR},
          },
        },
        'width': 8,
        'leadTime': 0,
        'resolution': 100,
      },
      'model': {
        'gltf': model,
        'minimumPixelSize': 50,
        'maximumPixelSize': 100,
      },
      'position': {
        'epoch': launchTime,
        'cartographicDegrees': coordinates,
        'interpolationAlgorithm': 'LAGRANGE',
        'interpolationDegree': 5,
      },
      'orientation': {
        'epoch': launchTime,
        'unitQuaternion': getOrientation(),
      },
    },
  ];
}

function getOrientation() {
  var points = getCartographicCoordinates();

  // Convert each data point's Euler angles to a quaternion
  for(var i=0; i<orientation.length; i+=5) {
    var origin = Cesium.Ellipsoid.WGS84.cartographicToCartesian(points[i/5]);
    var roll = Cesium.Math.toRadians(orientation[i+1]);
    var pitch = Cesium.Math.toRadians(orientation[i+2]);
    var heading = Cesium.Math.toRadians(orientation[i+3]);

    var quaternion = Cesium.Transforms.headingPitchRollQuaternion(origin, heading, roll, pitch);

    orientation[i+1] = quaternion.x;
    orientation[i+2] = quaternion.y;
    orientation[i+3] = quaternion.z;
    orientation[i+4] = quaternion.w;
  }

  return orientation;
}

function onTrackRocketChange(event) {
  // Don't do anything unless Cesium is initialized already
  if(!viewer) return;

  if(event.target.checked && flightPath) {
    trackRocket();
  } else {
    resetCamera();
  }
}

function resetCamera(pauseClock) {
  pauseClock = pauseClock || false;
  viewer.trackedEntity = undefined;

  if(pauseClock) {
    var wasAnimated = viewer.clock.shouldAnimate;
    viewer.clock.shouldAnimate = false;
  }

  var boundingSphere = Cesium.BoundingSphere.fromPoints(getCartesianCoordinates());

  viewer.camera.flyToBoundingSphere(boundingSphere, {
    'orientation': {
      'heading': 0,
      'pitch': Cesium.Math.toRadians(CAMERA_ANGLE),
      'roll': 0,
    },
    'complete': function() {
      if(pauseClock && wasAnimated) {
        viewer.clock.shouldAnimate = true;
      }
    },
  });
}

function trackRocket() {
  var wasAnimated = viewer.clock.shouldAnimate;
  viewer.clock.shouldAnimate = false;

  // Fly to the bounding sphere of the rocket to make a nice animation for changing it to be the tracked entity
  var boundingSphere = new Cesium.BoundingSphere();
  viewer.dataSourceDisplay.getBoundingSphere(flightPath.entities.getById('path'), false, boundingSphere);

  viewer.camera.flyToBoundingSphere(boundingSphere, {
    'complete': function() {
      viewer.trackedEntity = flightPath.entities.getById('path');

      // Only start the clock again if it was already running before moving the camera
      if(wasAnimated) {
        viewer.clock.shouldAnimate = true;
      }
    },
  });
}

function getCartographicCoordinates() {
  var points = [];

  for(var i=0; i<coordinates.length; i+=4) {
    points.push(Cesium.Cartographic.fromDegrees(coordinates[i+1], coordinates[i+2], coordinates[i+3]));
  }

  return points;
}

function getCartesianCoordinates() {
  var points = [];

  for(var i=0; i<coordinates.length; i+=4) {
    points.push(Cesium.Cartesian3.fromDegrees(coordinates[i+1], coordinates[i+2], coordinates[i+3]));
  }

  return points;
}
