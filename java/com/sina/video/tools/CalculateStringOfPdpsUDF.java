package com.sina.video.tools ;

import org.apache.hadoop.hive.ql.exec.UDF ;
import java.io.* ;
import java.util.* ;


public class CalculateStringOfPdpsUDF extends UDF
{
	private static AdsPdpsConfParser configParser = AdsPdpsConfParser.getInstance() ;
	
	public String evaluate(String pdps)
	{
		return configParser.getStringOfPdps(pdps) ;
	}
	public static void main(String[] args)
	{
		CalculateStringOfPdpsUDF c = new CalculateStringOfPdpsUDF() ;
		System.out.println(c.evaluate("123")) ;
	}

}
