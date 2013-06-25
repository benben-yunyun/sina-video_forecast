package com.sina.video.tools ;

import org.apache.hadoop.hive.ql.exec.UDF ;
import java.io.* ;
import java.util.* ;


public class CalculateAdsPdpsUDF extends UDF
{
	private static AdsPdpsConfParser configParser = AdsPdpsConfParser.getInstance() ;
	
	public String evaluate(Map<String,String> user_tag)
	{
		String v_cha,pos,v_length,rotation ;
		/*
		if( user_tag.containsKey("v_cha") )
			v_cha = user_tag.get("v_cha") ;
		else
			return configParser.UNKNOWN ;
		*/
		if( user_tag.containsKey("pos") )
			pos = user_tag.get("pos") ;
		else
			return configParser.UNKNOWN ;
		if( user_tag.containsKey("v_length") )
			v_length = user_tag.get("v_length") ;
		else
			return configParser.UNKNOWN ;
		if( user_tag.containsKey("rotation") ) 
			rotation = user_tag.get("rotation") ;
		else
			return configParser.UNKNOWN ;
		//return configParser.getPdpsName(v_cha,pos,v_length,rotation) ;
		return configParser.getPdpsName(pos,v_length,rotation) ;
	}
	public static void main(String[] args)
	{
		CalculateAdsPdpsUDF c = new CalculateAdsPdpsUDF() ;
		Map<String,String> data = new HashMap<String,String>() ;
		data.put("v_cha","sports_g") ;
		data.put("pos","head2") ;
		data.put("v_length","long") ;
		data.put("rotation","1") ;
		String ret = c.evaluate(data) ;
		System.out.println(ret) ;

		data.put("rotation","0") ;
		ret = c.evaluate(data) ;
		System.out.println(ret) ;
	}

}
