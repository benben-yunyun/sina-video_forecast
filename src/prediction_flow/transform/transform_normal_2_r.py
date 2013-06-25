#!/usr/bin/python
import sys
import time

his_file=sys.argv[1]
days_num=int(sys.argv[2])
output_file=sys.argv[3]
wrong_file=sys.argv[4]

file_out=open(output_file,'w')
file_wrong=open(wrong_file,'w')

his_list=""
target_old=""
target_now=""
cnt=0
missing_days=0

his_value=[]
prev_date=time.strptime("20130101","%Y%m%d")

for line in file(his_file):
    segs = line.strip().split()
    if len(segs) != 3:
        continue    
    target_now=segs[0]
    if target_now != target_old:
        if target_old != "":
            if cnt == days_num:
                out_line=target_old+'#'+his_list[:-1]+'\n'
                file_out.write(out_line)
            else:
                file_wrong.write(target_old+'\t'+str(cnt)+'\n')
        his_list = ""
        his_list += segs[1]
        his_list += '#'
        target_old=target_now
        cnt=1
        his_value = [segs[1]]
        prev_date=time.strptime(segs[2], "%Y%m%d")
    else:
        dat=time.strptime(segs[2], "%Y%m%d")
        gap = dat.tm_mday - prev_date.tm_mday
        prev_date = dat
        # fill missing data
        while gap > 1:
            n = len(his_value)
            if n > 6:
                gap -= 1
                cnt += 1
                his_list += his_value[n - 7]
                his_list += '#'
                his_value.append(his_value[n - 7])
            else:
                missing_days = 1
                break
        # add current data
        cnt+=1
        his_list += segs[1]
        his_list += '#'
        his_value.append(segs[1])
    

if cnt == days_num:
    out_line=target_old+'#'+his_list[:-1]+'\n'
    file_out.write(out_line)

file_out.close()
file_wrong.close()

if missing_days==1:
    sys.exit(1);
sys.exit(0);
