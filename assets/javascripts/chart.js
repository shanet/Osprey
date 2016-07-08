var chart;

function initChart() {
  chart = c3.generate({
    bindto: '#chart',
    data: {
      columns: [
        agl,
        pressureAltitude,
        gpsAltitude,
        acceleration,
        groundSpeed,
      ],
      axes: {
        Acceleration: 'y2',
      },
      types: {
        Acceleration: 'line',
      },
    },
    axis: {
      y: {
        label: {
          text: 'Altitude (m)',
          position: 'outer-middle',
        },
      },
      y2: {
        show: true,
        label: {
          text: 'Acceleration (g)',
          position: 'outer-middle',
        },
      },
    },
    grid: {
      x: {
        lines: [
          {value: index_pad, text: 'Pad'},
          {value: index_boost, text: 'Boost'},
          {value: index_coast, text: 'Coast'},
          {value: index_drogue, text: 'Drogue'},
          {value: index_main, text: 'Main'},
          {value: index_landed, text: 'Landed'},
        ],
      },
    },
    zoom: {
      enabled: true,
    },
  });
}

function initCheckboxes() {
  var checkboxes = document.getElementById('chart_controls').querySelectorAll('input[type="checkbox"]');

  for(var i=0; i<checkboxes.length; i++) {
    checkboxes[i].addEventListener('change', onCheckboxChange);
    onCheckboxChange({srcElement: checkboxes[i]});
  }
}

function onCheckboxChange(event) {
  var series = event.srcElement.dataset.series;

  if(event.srcElement.checked) {
    chart.show([series]);
  } else {
    chart.hide([series]);
  }
}
