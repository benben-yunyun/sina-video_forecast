#!/bin/bash 


cd $(dirname `ls -l $0 | awk '{print $NF;}'`)/../..

WORK_DIR=`pwd`


echo "[INFO] process start $0"
echo "[INFO] ......."
echo "[INFO] predict_main PWD: $WORK_DIR"


if [ $# -ne 0 ]; then
	Date=`date -d "$1" +"%Y%m%d"`
	if [ $? -ne 0 -o `expr length $1` -ne 8]; then
		echo "[INFO] useage: $0 [DATE (format: YYYYmmdd)]" ;
		exit 1 
	fi
else
	Date=`date +"%Y%m%d" -d "-1days"` 
fi

echo "[INFO] predict_main Date:" $Date

source $WORK_DIR/conf/default.conf

doForecast()
{
	Data_dir=$WORK_DIR/data
	Model_file=$WORK_DIR/model/$MODEL_FILE
	
	echo "[INFO] data_fir $Data_dir" 
	echo "[INFO] Model_file $Model_file"
	

	train_begin_date=`date -d "$Date $(($TRAIN_DAYS-1)) days ago" +%Y%m%d`
	reserve_date=`date -d "$Date $RESERVED_DAYS days ago" +%Y%m%d`
	
	echo "[INFO] train_begin_date $train_begin_date" 
	echo "[INFO] reserve_date $reserve_date" 

	echo "[INFO] delete old data:" 
	echo "[INFO] ......"
	
	hive_drop_data="
		alter table $TABLE_FLOW_NAME drop partition(dt='$reserve_date') ;
	"
	echo "$hive_drop_data"
#hive -e "$hive_drop_data"


	echo "[INFO] download data from $TABLE_FLOW_NAME"
	echo "[INFO] ......"

	hive_download_data="
		insert overwrite local directory '$Data_dir/pdps'
		select pdps, pv, dt from $TABLE_FLOW_NAME
		where dt<='$Date' and dt>='$train_begin_date';
	"
	echo "$hive_down_data"
        hive -e "$hive_download_data"

	cat $Data_dir/pdps/* | tr '\001' '\t' > $Data_dir/pdps_history.data


	echo "[INFO] transform format:" 
	echo "[INFO] ......"
	sort -k 1,1 -k 3,3 $Data_dir/pdps_history.data > $Data_dir/pdps_history_sort.data
	python $WORK_DIR/src/prediction_flow/transform/transform_normal_2_r.py $Data_dir/pdps_history_sort.data $TRAIN_DAYS $Data_dir/pdps_rscript.data $Data_dir/pdps_rscript_wrong.data

	echo "[INFO] predict flow:"
	echo "[INFO] ......"
	echo "$RSCRIPT $Model_file $Data_dir/pdps_rscript.data $PREDICTION_DAYS > $Data_dir/pdps_rscript_prediction.data"
	$RSCRIPT $Model_file $Data_dir/pdps_rscript.data $PREDICTION_DAYS > $Data_dir/pdps_rscript_prediction.data


	echo "[INFO] parse result format:"
	echo "......"

	echo ""
	python $WORK_DIR/src/prediction_flow/transform/transform_r_2_normal.py $Data_dir/pdps_rscript_prediction.data $Date $TOTAL_PREDICTION_DAYS $Data_dir/pdps_prediction.data
	

	echo "[INFO] copyFlow"
	echo "[INFO] ......"
	sort -k 1,1 $Data_dir/pdps_prediction.data > $Data_dir/pdps_prediction_sort.data
	java -cp $jarArch com.sina.video.tools.CalculateCopyFlow $Data_dir/$RESULT_DATA $Data_dir/pdps_prediction_sort.data 
	echo "[INFO] end"
}
doForecast 
