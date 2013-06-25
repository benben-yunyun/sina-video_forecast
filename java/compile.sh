#!/bin/bash


cd $(dirname `ls -l $0 | awk '{print $NF;}'`)

WORK_DIR=`pwd`

javac -cp /usr/local/hive-0.7.1-bin/lib/hive-exec-0.7.1.jar $WORK_DIR/com/sina/video/tools/*.java
