function initMap() {
  mapboxgl.accessToken = 'pk.eyJ1Ijoic2hhbmV0IiwiYSI6ImNpbXZnbnBhMjAydDl3a2x1ejNoNWoydHMifQ.WIi_Jv4TO3hOzj-E120rYg';

  var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/satellite-streets-v9',
  });

  // Add navigation controls
  map.addControl(new mapboxgl.Navigation());

  // Animate movement to initial location
  map.flyTo({
    center: coordinates[0],
    zoom: 14,
  });

  map.on('load', function () {
    map.addSource('rocket', {
        'type': 'geojson',
        'data': {
            'type': 'Feature',
            'properties': {},
            'geometry': {
                'type': 'LineString',
                'coordinates': coordinates,
            }
        }
    });

    map.addLayer({
        'id': 'rocket',
        'type': 'line',
        'source': 'rocket',
        'layout': {
            'line-join': 'round',
            'line-cap': 'round'
        },
        'paint': {
            'line-color': '#4fe',
            'line-width': 3
        }
    });

    map.addSource("markers", {
        "type": "geojson",
        "data": {
            "type": "FeatureCollection",
            "features": [{
                "type": "Feature",
                "geometry": {
                    "type": "Point",
                    "coordinates": coordinates[0],
                },
                "properties": {
                    "title": "Pad",
                    "marker-symbol": "monument"
                }
            }, {
                "type": "Feature",
                "geometry": {
                    "type": "Point",
                    "coordinates": coordinates[coordinates.length-1],
                },
                "properties": {
                    "title": "Landing",
                    "marker-symbol": "harbor"
                }
            }]
        }
    });

    map.addLayer({
        "id": "markers",
        "type": "symbol",
        "source": "markers",
        "layout": {
            "icon-image": "{marker-symbol}-15",
            "text-field": "{title}",
            "text-font": ["Open Sans Semibold", "Arial Unicode MS Bold"],
            "text-offset": [0, 0.6],
            "text-anchor": "top"
        }
    });
  });
}
