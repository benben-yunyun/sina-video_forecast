#!/bin/bash

if [ $# -ne 0 ] ; then
	Date=`date -d "$1" +"%Y%m%d"`
else	
	Date=`date -d "-1days" +"%Y%m%d"`
fi
echo "Date:" $Date
echo "PWD:" `pwd`

jarArch="/data1/temp/optimus/qixiang/video_forecast/java/videoTools.jar"



hive_table="
	add jar $jarArch ;
	create temporary function calString as 'com.sina.video.tools.CalculateStringOfPdpsUDF' ;
	insert overwrite directory '/user/optimus/qixiang/video_forecast'
	select pdps , calString(pdps)
	from vide_forecast_qixiang_pv where dt='$Date'
"
echo ${hive_table}
hive -e "$hive_table"
#vide_forecast_qixiang_pv partition(dt='$Date') 
