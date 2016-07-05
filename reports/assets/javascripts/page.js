function ready() {
  testAjaxRequests();
  initChart();
  initCheckboxes();
  initCesium();
}

function testAjaxRequests() {
  // Test for Ajax requests by attemping to get a JS file (this will not work with local files due to browser security)
  var request = new XMLHttpRequest();

  request.addEventListener('error', function(error) {
    showNoAjaxError();
  });

  try {
    request.open('GET', getScriptSource());
    request.send();
  } catch(err) {
    showNoAjaxError();
  }
}

function getScriptSource() {
  var scripts = document.getElementsByTagName('script');
  return scripts[0].src;
}

function showNoAjaxError() {
  document.getElementById('no_ajax_error').style.display = 'block';
}
