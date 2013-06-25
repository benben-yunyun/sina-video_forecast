#!/usr/bin/python
import sys
import time
import datetime

# program.py result_file predict_date_time 
if len(sys.argv) < 4:
  sys.exit(1)

result_file=sys.argv[1]
predict_start_date=sys.argv[2]
Ndays=int(sys.argv[3])
output=sys.argv[4]

fin = open(result_file)
plist = fin.readlines()
fin.close()

table = open(output, "w")

# read PSID and flows
t = 0
psid_num = len(plist) / 2 
while t < psid_num:
  id = 2 * t
  t += 1
  psid = plist[id][:-1]
  psid = psid.strip()
  flows = plist[id + 1].strip().split()
  # expand flows
  while len(flows) < Ndays:
    flows = flows + flows[-7:]
  tmps = flows[0:Ndays]
  flows = [str(max(0, int(x))) for x in tmps] 
  # write results
  fixday = time.strptime(predict_start_date, "%Y%m%d")
  preday = datetime.date(fixday.tm_year, fixday.tm_mon, fixday.tm_mday)
  for x in flows:
    preday = preday + datetime.timedelta(days=1)
    prestr = preday.strftime("%Y%m%d")
    table.write(prestr + "\t" + psid + "\t" + x + "\n")

table.close()
