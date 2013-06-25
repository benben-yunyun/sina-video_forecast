package com.sina.video.tools ;

import java.io.* ;
import java.util.* ;


public class CalculateCopyFlow
{
	private static AdsPdpsConfParser adsConfigParser = AdsPdpsConfParser.getInstance() ;
	private static CopyFlowConfParser flowConfigParser = CopyFlowConfParser.getInstance() ;
	int [][][] flowArray ;
	int posSize ;
	int lengthSize ;
	int rotationSize ;
	BufferedReader inReader ;
	BufferedWriter outReader ;
	public CalculateCopyFlow(String outFile, String inFile)
	{	
		try
		{
			inReader = new BufferedReader( new InputStreamReader(new FileInputStream(inFile))) ;
			outReader = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(outFile))) ;
			if( inReader == null )
				System.err.println("[ERROR] cannot open the in file:" + inFile);
			if( outReader == null )
				System.err.println("[ERROR] cannot oepn the out file:" + outFile) ;
			posSize = adsConfigParser.getPosLength() ;
			lengthSize = adsConfigParser.getLengthLength() ;
			rotationSize = adsConfigParser.getRotationLength() ;
			flowArray = new int[posSize][lengthSize][rotationSize] ;
		}
		catch( FileNotFoundException e)
		{
			e.printStackTrace() ;
		}
		catch( IOException e )
		{
			e.printStackTrace() ;
		}
	}
	public void doCopyFlow(String date)
	{
		if( outReader == null )
		{
			System.err.println("[ERROR] CalculateCopyFlow-doCopyFlow: outReader == null !") ;
			return ;
		}
		List<CopyFlowConfParser.Task> taskList = flowConfigParser.task ;
		if( taskList.size() == 0 )
		{
			System.out.println("[WARNING] CalculateCopyFlow-doCopyFlow: taskList.size() == 0 !") ;
		}
		else
		{
			for( int i = 0 ; i < taskList.size() ; ++ i )
			{
				CopyFlowConfParser.Task item = taskList.get(i) ;
				int dstPos = adsConfigParser.getPosIndex(item.dstPos) ;
				int dstLength = adsConfigParser.getLengthIndex(item.dstLength) ;
				int dstRotation = adsConfigParser.getRotationIndex(item.dstRotation) ;
				double sum = 0 ;
				double decayFactory = item.decayFactory ;
				int srcPos = 0 , srcLength = 0 , srcRotation = 0 ;
				//System.out.println("[DEBUG] task"+i+"\tposStr:"+item.dstPos+"\tdstPos:"+dstPos+"\tlengthStr:"+item.dstLength+"\tdstLength:"+dstLength+"\trotationStr:"+item.dstRotation+"\tdstRotation:"+dstRotation+"\tdecayFactory:"+decayFactory) ;
				for( int posIndex = 0 ; posIndex < item.srcPos.size() ; ++ posIndex )
				{
					srcPos = adsConfigParser.getPosIndex(item.srcPos.get(posIndex)) ;
					//System.out.println("[DEBUG] srcPosStr:"+item.srcPos.get(posIndex)+"\tsrcPos:"+srcPos) ;
					for( int lengthIndex = 0 ; lengthIndex < item.srcLength.size() ; ++ lengthIndex )
					{
						srcLength = adsConfigParser.getLengthIndex(item.srcLength.get(lengthIndex)) ;
						//System.out.println("[DEBUG] srcLengthStr:"+item.srcLength.get(lengthIndex)+"\tsrcLength:"+srcLength) ;
						for( int rotationIndex = 0 ; rotationIndex < item.srcRotation.size() ; ++ rotationIndex )
						{
							srcRotation = adsConfigParser.getRotationIndex(item.srcRotation.get(rotationIndex)) ;
							//System.out.println("[DEBUG] srcRotationStr:"+item.srcRotation.get(rotationIndex)+"srcROtation:"+srcRotation) ;
							sum += flowArray[srcPos][srcLength][srcRotation]; 
						}
					}
				}
				//System.out.println("[DEBUG] sum:"+sum+"\tdecayFactory:"+decayFactory+"\tsum*factory"+(int)(sum*decayFactory)) ;
				flowArray[dstPos][dstLength][dstRotation] = (int)(sum * decayFactory) ; 	
			}
			printToFile(date) ;
		}
	}
	public void printToFile(String date)
	{
		try
		{
			for( int i = 0 ; i < flowArray.length ; ++ i )
				for( int j  = 0 ; j < flowArray[i].length ; ++ j )
					for( int k = 0 ; k < flowArray[i][j].length ; ++ k )
						if( flowArray[i][j][k] > 0 ) 
						{
							int index = i * lengthSize * rotationSize + j * rotationSize + k ;
							outReader.write(date+"\t"+String.valueOf(index)+"\t"+String.valueOf(flowArray[i][j][k])+"\n") ;
							//System.out.println("[DEBUG] printToFile:" + index+"\tpv:"+flowArray[i][j][k]) ;
						}
			outReader.flush() ;
		}
		catch( IOException e)
		{
			e.printStackTrace() ;
		}
	}
	public void run()
	{
		if( inReader == null )	
		{
			System.out.println("[ERROR] CalculateCopyFlow-run: inReader == null!") ;
			return ;
		}
		else
		{
			System.out.println("[INFO] CalculateCopyFlow-run: start") ;
			String ansDate ="" ;
			String line ;
			int errorLine = 0; 
			try
			{
				while( (line = inReader.readLine()) != null )
				{
					String item[] = line.split("\t") ;
					//System.out.println("[DEBUG] run line:"+line+"\tsplit:"+item[0]+"\t"+item[1]+"\t"+item[2]) ;
					if( item.length != 3 )
					{
						errorLine ++ ;
						continue ;
					}
					else
					{
						String nowDate = item[0] ;
						//System.out.println("[DEBUG] ansDate:" + ansDate + "\tnowDate:" + nowDate) ;
						if( nowDate.compareTo(ansDate) != 0 )
						{
							if( ansDate != "" )
							{
								doCopyFlow( ansDate ) ;
							}
							initArray() ;
							ansDate = nowDate ;
						}
						int pdps = Integer.parseInt(item[1]) ;
						int pv = Integer.parseInt(item[2]) ;
						List<Integer> index = getIndex(pdps) ;
						//System.out.println("[DEBUG] run pdps:" + pdps + "\tindex:"+index.toString()+"\tpv:"+pv) ;
						if( index.size() != 3 )
						{
							errorLine ++ ;
							continue ;
						}
						flowArray[index.get(0)][index.get(1)][index.get(2)] += pv ;
					}	
				}
				doCopyFlow(ansDate) ;
			}
			catch( IOException e)
			{
				e.printStackTrace() ;
			}
			System.out.println("[INFO] CalculateCopyFlow-run: errorline\t" + errorLine) ;
		}
	}
	public List<Integer> getIndex(int pdps)
	{
		List<Integer> ret = new ArrayList<Integer>() ;
		int posIndex , lengthIndex, rotationIndex ;
		rotationIndex = pdps % rotationSize ;
		pdps /= rotationSize ;
		lengthIndex = pdps % lengthSize ;
		pdps /= lengthSize ;
		posIndex = pdps ;
		ret.add(posIndex) ;
		ret.add(lengthIndex) ;
		ret.add(rotationIndex) ;
		return ret ;
	}

	
	public void initArray()
	{
		for( int i = 0 ; i < flowArray.length ; ++ i )
			for( int j = 0 ; j < flowArray[i].length ; ++ j )
				for( int k = 0 ; k < flowArray[i][j].length ; ++ k )
					flowArray[i][j][k] = 0 ;
	}
	public static void main(String[] args)
	{
		if( args.length != 2 )
		{
			System.err.println("[ERROR] usage: copyFlow inDataFile outDataFile") ;
			return ;
		}
		CalculateCopyFlow cf = new CalculateCopyFlow(args[0],args[1]) ;
		cf.run() ;
	}

}
