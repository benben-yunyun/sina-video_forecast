#!/bin/bash -xv

####################################
# the main scipt of video flow forecast
# author: qixiang@staff.sina.com.cn
####################################



cd $(dirname `ls -l $0 | awk '{print $NF;}'`)/..

WORK_DIR=`pwd`
DATE=`date +"%Y%m%d"`

echo "video_forecast PWD:" $WORK_DIR

echo "video_forecast DATE:" $DATE

logFile="log/log."$DATE

source ${WORK_DIR}/conf/default.conf

doVideoFlowForecast()
{
	echo "-------------------------------------------"
	echo "          step1: extract data"

	echo "          extract pdps flow from ods_ad_sax_video_impression"
        sh src/extract_data/extract_data.sh
	echo "-------------------------------------------"
	echo "          step2: predict flow"

	sh -x src/prediction_flow/prediction_main.sh 	


	echo "-------------------------------------------"
	echo "          step3: insert result data into hive" 
	
	sh src/db_insert/insert_pdps_table.sh $RESULT_DATA
}>>$logFile 2>&1
doVideoFlowForecast 
