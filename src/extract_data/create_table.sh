#!/bin/bash


hive_create="
create table if not exists vide_forecast_qixiang_pv
	(
		pdps string,
		pv string
	)
	partitioned by (dt string) 
	row format delimited fields terminated by '\t' ;
"

hive -e "${hive_create}"

