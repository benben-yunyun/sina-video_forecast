#!/bin/bash
sql="CREATE TABLE IF NOT EXISTS pdps2_pv
(
 dt VARCHAR(64) NOT NULL COMMENT 'date',
 pdps VARCHAR(128) NOT NULL COMMENT 'pdps',
 flow INT NOT NULL COMMENT 'flow',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'insert/update time',
 INDEX(dt),
 INDEX(pdps),
 PRIMARY KEY(dt,pdps)
)
 DEFAULT CHARACTER SET utf8;
"

echo $sql

mysql -h localhost -P3306 -u optimus --password=optimus -Dvideo_pv --default-character-set=utf8 -e "$sql"
