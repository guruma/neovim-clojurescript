#!/bin/bash

sed "s/path.resolve(\".\"),//" out/test.js > out/t.js
