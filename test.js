var fs = require("fs");
var msgpack = require("/usr/local/lib/node_modules/msgpack-lite");
var cp = require('child_process');


var cljs = cp.spawn('node', ['out/test.js']);

cljs.stdout.on('data', (data) => {
          var d = msgpack.decode(data);
	  console.log(`stdout: ${d}`);
});

cljs.stderr.on('data', (data) => {
	  console.log(`stderr: ${data}`);
});

cljs.on('close', (code) => {
	  console.log(`child process exited with code ${code}`);
});

//var buffer = msgpack.encode({"foo": "bar"});

var buffer = msgpack.encode([0, 1, 'poll', null]);
cljs.stdin.write(buffer);

//var buffer = msgpack.encode([0, 1, 'specs', null]);
//cljs.stdin.write(buffer);
