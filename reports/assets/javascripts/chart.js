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
          {value: 2, text: 'Pad'},
          {value: 50, text: 'Boost'},
          {value: 100, text: 'Coast'},
          {value: 150, text: 'Apogee'},
          {value: 275, text: 'Main'},
          {value: 300, text: 'Landed'},
        ],
      },
    },
  });
}

function initCheckboxes() {
  var checkboxes = document.querySelectorAll('input[type="checkbox"]');

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
