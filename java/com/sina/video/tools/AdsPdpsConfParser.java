package com.sina.video.tools ;

import java.io.* ;
import java.util.* ;



public class AdsPdpsConfParser
{
	private static AdsPdpsConfParser instance = new AdsPdpsConfParser();
	public static String UNKNOWN = "UNKNOWN" ;

	private Map<String,Integer> channelMapInt;
	private Map<Integer,String> intMapChannel;
	private Map<String,Integer> posMapInt;
	private Map<String,Integer> lengthMapInt;
	private Map<String,Integer> rotationMapInt;
	public int getChannelLength()
	{ return intMapChannel.size() ; }
	public int getPosLength()
	{ return posMapInt.size() ; }
	public int getLengthLength() 
	{ return lengthMapInt.size() ; }
	public int getRotationLength()
	{ return rotationMapInt.size() ; }
	public void display()
	{
		System.out.println("channelMapInt:") ;
		for( Map.Entry<String,Integer> entry : channelMapInt.entrySet() )
			System.out.println("channel: "+entry.getKey() + "\tIndex:"+entry.getValue()) ;
		System.out.println("intMapChannel:") ;
		for( Map.Entry<Integer,String> entry : intMapChannel.entrySet() )
			System.out.println("index: " + entry.getKey() + "\tchannel:"+entry.getValue()) ;
		System.out.println("posMapInt:") ;
		for( Map.Entry<String,Integer> entry : posMapInt.entrySet() )
			System.out.println("pos: " + entry.getKey() + "\tIndex:" + entry.getValue()) ;
		System.out.println("lengthMapInt:") ;
		for( Map.Entry<String,Integer> entry : lengthMapInt.entrySet() )
			System.out.println("lenth: " + entry.getKey() + "\tIndex:" + entry.getValue()) ;
		System.out.println("rotationMapInt:") ;
		for( Map.Entry<String,Integer> entry : rotationMapInt.entrySet() )
			System.out.println("rotation:" + entry.getKey() + "\tIndex:" + entry.getValue()) ;
	}
	public static AdsPdpsConfParser getInstance()
	{
		return instance ;
	}
	
	private AdsPdpsConfParser()
	{
		try
		{
			channelMapInt = new HashMap<String,Integer>() ;
			intMapChannel = new HashMap<Integer,String>() ;
			posMapInt = new HashMap<String,Integer>() ;
			lengthMapInt = new HashMap<String,Integer>() ;
			rotationMapInt = new HashMap<String,Integer>() ;

			Properties prop = new Properties() ;
			prop.load(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("com/sina/video/tools/conf/pdps.conf")));
			parserConf(prop) ;
		}
		catch( FileNotFoundException e)
		{
			System.err.println("cannot find file: pdps.conf");
			e.printStackTrace() ;
		}
		catch( IOException e)
		{
			System.err.println("cannot parse file: pdps.conf") ;
			e.printStackTrace() ;
		}
	}
	private void parserConf( Properties prop) throws IOException 
	{
		if( prop != null )
		{
			String line = prop.getProperty("v_cha") ;
			String[] channel = line.split(",") ;
			int index = 0 ;
			for( String item : channel )
			{
				String[] keyValue = item.split(":") ;
				if( keyValue.length != 2 ) 
					continue ;
				String[] valueArray = keyValue[1].split("\\|") ;
				if( valueArray.length == 0 ) 
					continue ;
				
				intMapChannel.put(new Integer(index),keyValue[0]);
				for( String value : valueArray )
				{
					channelMapInt.put(value,new Integer(index)) ;
				}
				++ index ;
			}
			
			index = 0 ;
			line = prop.getProperty("pos") ;
			String[] posArray = line.split(",") ;
			for( String pos : posArray )
			{
				posMapInt.put(pos,new Integer(index)) ;
				++index ;
			}
			index = 0 ;
			line = prop.getProperty("v_length") ;
			String[] lengthArray = line.split(",") ;
			for( String vLength : lengthArray )
			{
				lengthMapInt.put(vLength,new Integer(index) ) ;
				++ index ;
			}
			index = 0 ;
			line = prop.getProperty("rotation") ;
			String[] rotationArray = line.split(",") ;
			for( String rotation : rotationArray )
			{
				rotationMapInt.put(rotation,new Integer(index)) ;
				++ index ;
			}
		}
	}

	//public String getPdpsName(String channel, String pos, String length, String rotation)
	public String getPdpsName(String pos, String length, String rotation)
	{
		String pdpsName = UNKNOWN;
		Integer index = 0 ;
		/*
		if( channelMapInt.containsKey(channel) ) 
			index = channelMapInt.get(channel) ;
		else 
		{
			if( channelMapInt.containsKey("*") ) 
				index = channelMapInt.get("*") ;
			else
				return pdpsName ;
		} 
		*/
		if( posMapInt.containsKey(pos) )
			index = index * posMapInt.size() + posMapInt.get(pos);
		else 
			return pdpsName ;
		if( lengthMapInt.containsKey(length) )
			index = index * lengthMapInt.size() + lengthMapInt.get(length) ;
		else
			return pdpsName ;
		if( rotationMapInt.containsKey(rotation) )
			index = index * rotationMapInt.size() + rotationMapInt.get(rotation) ;
		else
			return pdpsName ;
		return index.toString() ;
	}
	public String getStringOfPdps(String pdps_string)
	{
		int channelSize = intMapChannel.size() ;
		int posSize = posMapInt.size() ;
		int lengthSize = lengthMapInt.size() ;
		int rotationSize = rotationMapInt.size() ;
		int pdps = Integer.parseInt(pdps_string) ;
		//if( pdps + 1 >= channelSize*posSize*lengthSize*rotationSize )
		if( pdps + 1 >= posSize*lengthSize*rotationSize ) 
			return UNKNOWN ;
		int rotation = pdps % rotationSize ;
		pdps /= rotationSize ;
		int length = pdps % lengthSize ;
		pdps /= lengthSize ;
		int pos = pdps % posSize ;
		pdps /= posSize ;
		//int channel = pdps ;
		String ret = "";
		/*
		if( intMapChannel.containsKey(channel) )
			ret += intMapChannel.get(channel) ;
		else 
			return UNKNOWN ;
		*/
		boolean ok = false ;
		for( Map.Entry<String,Integer> entry : posMapInt.entrySet() )
		{
			if( entry.getValue().compareTo(pos) == 0 )
			{
				ret = entry.getKey() ;
				ok = true ;
				break ;
			}
		}
		if( !ok ) return UNKNOWN ;
		ok = false ;
		for( Map.Entry<String,Integer> entry : lengthMapInt.entrySet() )
		{
			if( entry.getValue().compareTo(length) == 0 )
			{
				ret += "#" + entry.getKey() ;
				ok = true ;
				break ;
			}
		}
		if( !ok ) return UNKNOWN ;
		ok = false ;
		for( Map.Entry<String,Integer> entry : rotationMapInt.entrySet() )
		{
			if( entry.getValue().compareTo(rotation) == 0 )
			{
				ret += "#" + entry.getKey() ;
				ok = true ;
				break ;
			}
		}
		if( !ok ) return UNKNOWN ;
		return ret ;
	}
	public String getChannelName(Integer channelIndex)
	{
		if( intMapChannel.containsKey(channelIndex) )
			return intMapChannel.get(channelIndex) ;
		else 
			return UNKNOWN ;
	}
	public String getPosName(Integer posIndex)
	{
		for( Map.Entry<String,Integer> entry : posMapInt.entrySet() )
		{
			if(  posIndex.compareTo(entry.getValue()) == 0 ) 
				return entry.getKey() ;
		}
		return UNKNOWN;
	}
	public String getLengthName(Integer lengthIndex)
	{
		for( Map.Entry<String,Integer> entry: lengthMapInt.entrySet() )
		{
			if( lengthIndex.compareTo(entry.getValue()) == 0 ) 
				return entry.getKey() ;	
		}
		return UNKNOWN;
	}
	public String getRotationName(Integer rotationIndex)
	{
		for( Map.Entry<String,Integer> entry: rotationMapInt.entrySet() )
		{
			if( rotationIndex.compareTo(entry.getValue()) == 0)
				return entry.getKey() ;
		}
		return UNKNOWN ;
	}
	public int getChannelIndex(String channelStr)
	{
		if( channelMapInt.containsKey(channelStr) ) 
			return channelMapInt.get(channelStr) ;
		else
			return -1 ; 
	}
	public int getPosIndex(String posStr)
	{
		if( posMapInt.containsKey(posStr) )
			return posMapInt.get(posStr) ;
		else
			return - 1;
	}
	public int getLengthIndex(String lengthStr)
	{
		if( lengthMapInt.containsKey(lengthStr) )
			return lengthMapInt.get(lengthStr) ;
		else 
			return -1 ;
	}
	public int getRotationIndex(String rotationStr)
	{
		if( rotationMapInt.containsKey(rotationStr) )
			return rotationMapInt.get(rotationStr) ;
		return -1 ;
	}


	public static void main(String[] args)
	{
		AdsPdpsConfParser instance = AdsPdpsConfParser.getInstance() ;
		instance.display() ;
		System.out.println(instance.getPdpsName("head2","long","1")) ;
		System.out.println(instance.getPdpsName("head","long","1")) ;
		System.out.println( instance.getChannelName(new Integer(2)) + "_" +instance.getPosName(new Integer(2)) + "_" + instance.getLengthName(new Integer(2)) + "_" + instance.getRotationName(new Integer(2)));
		System.out.println( instance.getStringOfPdps("123")) ;
		System.out.println( "-------------------------------------------------");
		for( String s : args ) 
			System.out.println( s + "\t" + instance.getStringOfPdps(s)) ;
	}
} 
