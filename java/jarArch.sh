#!/bin/bash


cd $(dirname `ls -l $0 | awk '{print $NF;}'`)

WORK_DIR=`pwd`

rm $WORK_DIR/videoTools.jar
jar -cvf videoTools.jar com/sina/video/tools/*.class com/sina/video/tools/conf/*.conf
