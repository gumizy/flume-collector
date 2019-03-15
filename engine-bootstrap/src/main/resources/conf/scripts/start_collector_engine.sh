#!/bin/bash
mkdir /tmp/collector_engine_tmp -p
java -server -Xms2G -Xmx2G -Dfile.encoding=utf-8 -Djava.io.tmpdir=/tmp/collector_engine_tmp -jar ./dcsec-collector-engine-1.0.0.jar > /dev/null