package com.sina.video.tools;

import java.io.*;
import java.sql.* ;


public class PdpsTableInsert
{
    String driver = "com.mysql.jdbc.Driver" ;
    String url = "jdbc:mysql://10.39.0.100:3306/video_pv" ;
    String user = "optimus" ;
    String passwd = "optimus" ;
    private Connection conn ;
    private static AdsPdpsConfParser configParser = AdsPdpsConfParser.getInstance();
    public PdpsTableInsert()
    {
        try
            {
                Class.forName(driver) ;
            }
        catch (ClassNotFoundException e)
            {
                System.err.println("jdbc driver cannot found;") ;
                e.printStackTrace() ;
                System.exit(1) ;
            }
        try
            {
                conn = DriverManager.getConnection(url,user,passwd);
                if( !conn.isClosed() )
                    System.out.println("Succeeded connection to the Database!") ;
            }
        catch( Exception e)
            {
                System.err.println("database cannot connection!") ;
                e.printStackTrace() ;
                System.exit(1) ;
            }
    }
    
    public void close() throws SQLException
    {
        if( conn != null )
            conn.close() ;
    }
    
    public void doExecute(String dataFile) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile))) ;
        String line = br.readLine() ;
        Statement st = conn.createStatement() ;
        int lineNum = 0 ;
        while( line != null )
            {
                String[] items = line.trim().split("\t");
                String pdpsId = items[1].trim() ;
                String pdpsName = configParser.getStringOfPdps(pdpsId) ;
                long count = Long.parseLong(items[2].trim()) ;
                if( pdpsName != configParser.UNKNOWN ) 
                    {
                        String sql = "insert into pdps2_pv (dt, pdps, flow) values ('" + items[0].trim() + "','"+ pdpsName + "'," + count +") on duplicate key update flow=" + count ;
                        System.out.println("sql"+sql) ;
                        st.execute(sql) ;
                        ++ lineNum ;
                    }
                line = br.readLine() ;
            }
        System.out.println("insert " + lineNum + " lines into table pdps2_pv") ;
    }
    public static void main(String[] args)
    {
        if( args.length != 1 )
            {
                System.err.println("need 1 parameter: pdps_pv.data");
                System.exit(1) ;
            }
        PdpsTableInsert ip = new PdpsTableInsert() ;
        String pdps_file = args[0] ;
        System.out.println("[INFO]: pdps prediction file: "+pdps_file) ;
        try 
            {
                ip.doExecute(pdps_file) ;
                System.exit(0) ;
            }
        catch( Exception e)
            {
                e.printStackTrace() ;
                System.exit(1) ;
            }
    }
}
