#!/usr/bin/bash -

##########################
# insert PV into database
# author panwei1@staff.sina.com.cn
##########################

cd $(dirname `ls -l $0 | awk '{print $NF;}'`)/../..
WORK_DIR=`pwd`
echo "PWD="$WORK_DIR 
source ${WORK_DIR}/conf/default.conf

pdps_pv="$WORK_DIR/data/$RESULT_DATA"

if [ $# -ne 1 ]
then
	echo "need 1 parmaters: pdps.pv"
	exit 1
fi

pdps_pv=$1

java -cp java/mysql-connector-java-5.1.2.jar:$jarArch com.sina.video.tools.PdpsTableInsert data/$pdps_pv

if [ $? -ne 0 ]
then
	echo "error inserting psid pv data into DB"
	exit 1
fi

exit 0
