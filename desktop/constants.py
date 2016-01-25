DEFAULT_LABEL = 'N/A'
NUMPY_ARRAY_SIZE = 1000

DATA_DISPLAYS_PER_ROW = 5
GRAPHS_PER_ROW = 3

DISPLAYS = {
  'time': {
    'label': 'Time (UTC)',
    'field': 'timestamp',
  },
  'coordinates': {
    'label': 'Coordinates',
    'field': 'coordinates',
  },
  'roll': {
    'label': 'Roll',
    'field': 'roll',
    'units': '\xb0',
    'color': 'FF0000',
  },
  'pitch': {
    'label': 'Pitch',
    'field': 'pitch',
    'units': '\xb0',
    'color': '00FF00',
  },
  'heading': {
    'label': 'Heading',
    'field': 'heading',
    'units': '\xb0',
    'color': '0000FF',
  },
  'pressure_altitude': {
    'label': 'Pressure Altitude',
    'field': 'pressure_altitude',
    'units': 'm',
    'color': '00FF00',
  },
  'gps_altitude':{
    'label': 'GPS Altitude',
    'field': 'gps_altitude',
    'units': 'm',
    'color': 'FFAA00',
  },
  'agl': {
    'label': 'Above Ground Level',
    'field': 'agl',
    'units': 'm',
  },
  'pressure_setting': {
    'label': 'Pressure Setting',
    'field': 'pressure_setting',
    'units': '" Hg',
  },
  'temp': {
    'label': 'Temperature',
    'field': 'temp',
    'units': '\xb0C',
    'color': '9900FF',
  },
  'speed': {
    'label': 'GPS Speed',
    'field': 'speed',
    'units': 'kt',
  },
  'gps_quality': {
    'label': 'GPS Quality',
    'field': 'gps_quality',
  },
  'previous_command': {
    'label': 'Previous Command',
    'field': 'previous_command',
  },
  'command_status': {
    'label': 'Command Status',
    'field': 'command_status',
  },
}

GRAPHS = {
  'orientation': {
    'title': 'Orientation',
    'displays': [
      DISPLAYS['roll'],
      DISPLAYS['pitch'],
      DISPLAYS['heading'],
    ],
    'labels': {'left': ('Degrees', 'degrees'), 'bottom': ('Time', 'seconds')},
  },
  'altitude': {
    'title': 'Altitude',
    'displays': [
      DISPLAYS['gps_altitude'],
      DISPLAYS['pressure_altitude'],
    ],
    'labels': {'left': ('Meters', 'meters'), 'bottom': ('Time', 'seconds')},
  },
  'temperature': {
    'title': 'Temperature',
    'displays': [
      DISPLAYS['temp'],
    ],
    'labels': {'left': ('Temperature', 'celsius'), 'bottom': ('Time', 'seconds')},
  },
}
