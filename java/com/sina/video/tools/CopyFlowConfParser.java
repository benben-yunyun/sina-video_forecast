package com.sina.video.tools ;

import java.io.* ;
import java.util.* ;



public class CopyFlowConfParser
{
	private static CopyFlowConfParser instance = new CopyFlowConfParser();
	
	public class Task
	{
		public String dstPos,dstLength,dstRotation ;
		public double decayFactory ;
		public List<String> srcPos ;
		public List<String> srcLength ;
		public List<String> srcRotation ;
		public Task()
		{
			dstPos = "" ;
			dstLength = "" ;
			dstRotation = "" ;
			decayFactory = 0.0 ;
			srcPos = new ArrayList<String>() ;
			srcLength = new ArrayList<String>() ;
			srcRotation = new ArrayList<String>() ;
		}
	};
	public List<Task> task ;
	public static CopyFlowConfParser getInstance()
	{
		return instance ;
	}
	
	private CopyFlowConfParser()
	{
		try
		{
			task = new ArrayList<Task>() ;
			BufferedReader inReader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("com/sina/video/tools/conf/copyFlow.conf")));
			parserConf(inReader) ;
		}
		catch( FileNotFoundException e)
		{
			System.err.println("cannot find file: copyFlow.conf");
			e.printStackTrace() ;
		}
		catch( IOException e)
		{
			System.err.println("cannot parse file: copyFlow.conf") ;
			e.printStackTrace() ;
		}
	}
	private void parserConf(BufferedReader inReader) throws IOException 
	{
		if( inReader != null )
		{
			String line ;
			while( (line = inReader.readLine()) != null )
			{
				System.out.println(line) ;
				Task item = new Task() ;
				String[] keyValue = line.split("=") ;
				if( keyValue.length != 2 )
				{
					System.err.println("parser task ERROR:"+line) ;
					continue ;
				}
				String[] dstStr = keyValue[0].split("#") ;
				if( dstStr.length != 3 )
				{
					System.err.println("dstStr ERROR:" + keyValue[0]) ;
					continue ;
				}
				item.dstPos = dstStr[0] ;
				item.dstLength = dstStr[1] ;
				item.dstRotation = dstStr[2] ;
				
				String[] srcStr = keyValue[1].split("#") ;
				if( srcStr.length != 4 )
				{
					System.err.println("srcStr ERROR:" + keyValue[1]) ;
					continue ;
				}
				item.decayFactory = Double.parseDouble(srcStr[0]) ;
				
				String[] srcPos = srcStr[1].split(",") ;
				if( srcPos.length <= 0 )
				{
					System.err.println("parse dstPos ERROR:"+srcStr[1]) ;
					continue ;
				}
				for( String pos : srcPos ) 
					item.srcPos.add(pos) ;

				String[] srcLength = srcStr[2].split(",") ;
				if( srcLength.length <= 0 )
				{
					System.err.println("parse dstLength ERROR:"+srcStr[2]) ;
					continue ;
				}
				for( String length : srcLength )
					item.srcLength.add(length) ;

				String[] srcRotation = srcStr[3].split(",") ;
				if( srcRotation.length <= 0 )
				{
					System.err.println("parse dstRotation ERROR:" + srcStr[3]) ;
					continue ;
				}
				for( String rotation : srcRotation )
					item.srcRotation.add(rotation) ;
				task.add(item) ;
			}
		}
	}

	public static void main(String[] args)
	{
		CopyFlowConfParser cf = CopyFlowConfParser.getInstance();
		for( int i = 0 ; i < cf.task.size() ; ++ i )
		{
			Task item = cf.task.get(i) ;
			System.out.println("[INFO] task:" + i) ;
			System.out.println("dstPos:"+item.dstPos+"\tdstLength:"+item.dstLength+"\tdstRotation:"+item.dstRotation) ;
			System.out.println("decayFactory:"+item.decayFactory) ;
			for( int j = 0 ; j < item.srcPos.size(); ++ j)
				System.out.print(j+" srcPos:" + item.srcPos.get(j)+"\t") ;
			System.out.println();
			for( int j = 0 ; j < item.srcLength.size(); ++ j )
				System.out.print(j+" srcLength:"+item.srcLength.get(j)+"\t") ;
			System.out.println() ;
			for( int j = 0 ; j < item.srcRotation.size() ; ++ j )
				System.out.print(j+" srcRotation:" +item.srcRotation.get(j)+"\t" ) ;
		}
	}
} 
