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
    document.getElementById('no_ajax_error').style.display = 'block';
  });

  request.open('GET', getScriptSource());
  request.send();
}

function getScriptSource() {
  var scripts = document.getElementsByTagName('script');
  return scripts[0].src;
}
