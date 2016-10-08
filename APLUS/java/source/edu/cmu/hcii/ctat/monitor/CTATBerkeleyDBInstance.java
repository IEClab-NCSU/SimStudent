/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATBerkeleyDBInstance.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATBerkeleyDBInstance.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.2  2012/04/27 18:19:50  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.1  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.2  2012/03/30 18:26:21  vvelsen
 A bunch of changes to the way the server checks and verifies if services are down. There is still a tweak that needs to be made to TS testing since it doesn't always accurately detect if a TS is down

 Revision 1.1  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/


package edu.cmu.hcii.ctat.monitor;
import java.util.Iterator;
import java.util.Map;
import com.sleepycat.collections.StoredMap;

//import com.sleepycat.bind.serial.StoredClassCatalog;
//import com.sleepycat.bind.tuple.LongBinding;
//import com.sleepycat.bind.tuple.StringBinding;
//import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;

import edu.cmu.hcii.ctat.CTATBase;

/**
*
*/
public class CTATBerkeleyDBInstance extends CTATBase
{
    private Database                db=null;
    private DatabaseConfig          dbConfig=null;
    private StoredMap<Long, String> map=null;
    private Environment             env=null;
    private boolean                 dbDisabled=false;
    private boolean                 create = true;
    private String                  dbDir  = "./db";
	
	/**
	*
	*/	
	public CTATBerkeleyDBInstance ()
	{  
    	setClassName ("CTATBerkeleyDBInstance");
    	debug ("CTATBerkeleyDBInstance ()");    	    	    	
	}
    /**
     * 
     */
	public Database getDB ()
	{
		return (db);
	}
    /**
     * 
     */	
	public void setEnvironment (Environment anEnv)
	{
		env=anEnv;
	}
    /**
     * 
     */
	public Environment getEnvironment ()
	{
		return (env);
	}
    /**
     * 
     */    
	public boolean isDbDisabled() 
	{
		return dbDisabled;
	}
    /**
     * 
     */
	public void setDbDisabled(boolean dbDisabled) 
	{
		this.dbDisabled = dbDisabled;
	}	
	/**
	 * 
	 */	
	public String getDbDir() 
	{
		return dbDir;
	}
	/**
	 * 
	 */	
	public void setDbDir(String dbDir) 
	{
		this.dbDir = dbDir;
	}		
	/**
	 * 
	 */
	public StoredMap<Long, String> getData ()
	{
		return map;
	}
	/**
	 * 
	 */
	public void assignMap (StoredMap<Long, String> aMap)
	{
		map=aMap;
	}	
    /** 
     * Opens the database but does not create or assign the map
     */
    public void openDB (String DBName) throws Exception 
    {
    	debug ("openDB ()");
    	
    	if (env==null)
    	{
    		debug ("Error, no Environment available");
    		return;
    	}
    	
        dbConfig=new DatabaseConfig();
        dbConfig.setTransactional(true);
        
        if (create) 
        {
            dbConfig.setAllowCreate(true);
        }

        this.db=env.openDatabase (null, DBName, dbConfig);

        if (this.db==null)
        {
        	setDbDisabled(true);
        	return;
        }
                                               
        debug ("Database should be open and available for binding");
    }
    /**
     * 
     */
    /*
    public void assignMap (Map aMap)
    {
    	debug ("assignMap ()");
    	
        LongBinding keyBinding = new LongBinding();
        StringBinding dataBinding = new StringBinding();        
        
        // create a map view of the database
        this.map=new StoredMap<Long, String> (db, keyBinding, dataBinding, true);
        
        if (this.map==null)
        {
        	debug ("Error creating StoredMap from database");
        	setDbDisabled (true);
        }        	    	
    }
    */
    /** 
     * Closes the database. 
     */
    public Boolean close()
    {
    	debug ("close ()");
    	
    	dbDisabled=true;
    	       
        debug("Closing database ...");
        
        if (db != null) 
        {
            db.close();
            db = null;
        }
                        
        debug("Resetting map ...");
        
        this.map=null;
        
        debug("Database successfully shutdown");
        
        return (true);
    }
    /** 
     * @param aKey Long
     * @param aValue String
     */
    public boolean writeKV (Long aKey,String aValue)
    {
    	debug ("writeKV (key:"+aKey+", value:"+aValue+")");
    	
    	if (dbDisabled==true)
    	{
    		debug ("Error: database is not open or disabled, aborting");
    		return (false);
    	}	
    	
    	if (map==null)
    	{
    		debug ("No map available to write to, aborting ..");
    		return (false);
    	}
    	
    	map.put (aKey,aValue);
    	    	
    	return (true);
    }
    /**
     * 
     */
    public void dumpDB ()
    {
    	debug ("dumpDB ()");
    	
    	if (map==null)
    	{
    		debug ("No map available to read from, aborting ..");
    		return;
    	}    	
    	
        debug ("Map size: " + map.size() + " entries");
            
        Iterator<Map.Entry<Long, String>> iter=null;
        
		try
		{
			iter=map.entrySet().iterator();
		}
		catch (IndexOutOfBoundsException e)
		{
			debug ("Integrity check failed, IndexOutOfBoundsException");
		}		
                
        debug ("Reading data ...");
                
        while (iter.hasNext()) 
        {
            Map.Entry<Long, String> entry = iter.next();
            debug (entry.getKey().toString() + ' ' +  entry.getValue());
        }    	
    }
    /**
     * 
     */
    public Boolean checkDB ()
    {
    	debug ("checkDB ()");
    	
    	if (map==null)
    	{
    		debug ("No map available to read from, aborting ..");
    		return (false);
    	}    	
    	
        debug ("Checking: " + map.size() + " entries");
            
        Iterator<Map.Entry<Long, String>> iter=null;
        
		try
		{
			iter=map.entrySet().iterator();
		}
		catch (IndexOutOfBoundsException e)
		{
			debug ("Integrity check failed, IndexOutOfBoundsException");
			return (false);
		}		
                                
        while (iter.hasNext()) 
        {
            @SuppressWarnings("unused")
			Map.Entry<Long, String> entry = iter.next();
            //debug (entry.getKey().toString() + ' ' +  entry.getValue());
            System.out.print(".");
        }
        
        System.out.println (" done");
        
        return (true);
    }            
}
