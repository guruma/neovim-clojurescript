# neovim-clojurescript

## prerequisites

### install msgpack-lite

	npm install -g msgpack-lite

## compile

	$ lein cljsbuild once prod

## test

	$ lein cljsbuild once test
	$ node out/integration-test.js
	$ cat log.txt


