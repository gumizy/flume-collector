#!/bin/bash
ps -ef | grep dcsec-collector-engine-1.0.0.jar | grep -v grep | cut -c 9-15 | xargs kill -9