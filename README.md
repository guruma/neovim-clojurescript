# neovim-clojurescript

## prerequisites

### install msgpack-lite

	npm install -g msgpack-lite

## compile

	$ lein cljsbuild once prod

## run

### tty
to check host is ready

	$ node out/host-plugin.js

### nvim
run nvim

	$ nvim

and enter ex mode in nvim to type ":NodeCmdArg0" and you can see "Hello, NVIM" message.

## test

	$ lein cljsbuild once test
	$ node out/integration-test.js
	$ cat log.txt


