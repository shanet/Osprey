function initCanvas() {
  var canvas = document.getElementById('canvas');
  var scene = new THREE.Scene();
  var camera = new THREE.PerspectiveCamera(75, canvas.offsetWidth/canvas.offsetHeight, 1, 10000);
  var renderer = new THREE.WebGLRenderer({canvas: canvas});

  renderer.setSize(canvas.offsetWidth, canvas.offsetHeight);
  canvas.parentNode.appendChild(renderer.domElement);

  // var geometry = new THREE.BoxGeometry(700, 700, 700, 10, 10, 10);
  var geometry = new THREE.CylinderGeometry(100, 100, 700, 16);
  var material = new THREE.MeshBasicMaterial({color: 0xfffff, wireframe: true});
  var cube = new THREE.Mesh(geometry, material);

  scene.add(cube);
  camera.position.z = 1000;

  function render() {
    requestAnimationFrame(render);
    cube.rotation.x += 0.01;
    cube.rotation.y += 0.01;
    cube.rotation.z += 0.01;
    renderer.render(scene, camera);
  };

  render();
}
