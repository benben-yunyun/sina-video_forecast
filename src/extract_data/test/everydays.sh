#!/bin/bash


Date=`date +%Y%m%d -d "-1days" `

echo $Date 

DateIndex=`date +%Y%m%d -d "$1" `

echo $DateIndex

while [[ $DateIndex -le  $Date ]]
do
	sh ../extract_data.sh $DateIndex
	DateIndex=`date -d "$DateIndex 1 days" +%Y%m%d `

	echo $DateIndex 


done 
