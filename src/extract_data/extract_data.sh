#!/bin/bash -xv

if [ $# -ne 0 ] ; then
	Date=`date -d "$1" +"%Y%m%d"`
else	
	Date=`date -d "-1days" +"%Y%m%d"`
fi


cd $(dirname `ls -l $0 | awk '{print $NF;}'`)/../..

WORK_DIR=`pwd` 
echo "[INFO] process start: $0" 
echo "[INFO] ......."
echo "extract_data PWD:" $WORK_DIR

echo "extract_data date:" $Date
source $WORK_DIR/conf/default.conf


hive_table="
	add jar $jarArch ;
	create temporary function calPdps as 'com.sina.video.tools.CalculateAdsPdpsUDF' ;
	insert overwrite table $TABLE_FLOW_NAME  partition(dt='$Date')
	select a.pdps, count(*)
	from 
	(
		select calPdps(user_tag) pdps from $TABLE_SAX_NAME
		where dt='$Date' and user_tag['pos']='head' 
	)a
	where a.pdps!='UNKNOWN' 
	group by a.pdps;
"
echo ${hive_table}
hive -e "$hive_table"
#vide_forecast_qixiang_pv partition(dt='$Date') 
