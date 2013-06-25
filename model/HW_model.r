args<-commandArgs(TRUE)

#######################################################
#		author : wangchao3@staff.sina.com.cn	      #
# predict future flow and write back to table	      #
#######################################################

#args[1] csv filename 
#args[2] predict days
#args[3] if totally update

pred_it<-function(flow_ts)
{
	hw<-HoltWinters(flow_ts,seasonal="add",beta=FALSE,start.periods=3)
	pred<-predict(hw,n.ahead=as.numeric(args[2]))
	cat(as.character(target),"\n")
	str<-paste(as.integer(pred),collapse=" ")
	cat(str,"\n")
}

his<-read.csv(args[1],header=F,sep="#")
nums<-length(his[,1])
cat("")

for (i in 1:nums)
{
	target<-his[i,][[1]]
	#transpose
	tmp_his<-t(his[i,][,-1])
	train_days<-length(tmp_his)
	flow_ts<-ts(tmp_his,freq=7)
	try(pred_it(flow_ts),FALSE)
}
