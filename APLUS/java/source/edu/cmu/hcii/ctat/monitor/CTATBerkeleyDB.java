/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATBerkeleyDB.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATBerkeleyDB.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.6  2012/05/07 19:09:12  vvelsen
 Added some visual tools to better manage our performance database

 Revision 1.5  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.4  2012/04/27 18:19:50  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.3  2012/04/25 19:35:47  vvelsen
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


import java.io.File;
import java.util.ArrayList;
//import java.util.SortedMap;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
//import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.Utilities.trace;

/**
*
*/
public class CTATBerkeleyDB extends CTATBase implements TransactionWorker
{
    private EnvironmentConfig envConfig=null;	
    private Environment       env=null;

    private boolean dbDisabled=false;
    private boolean create = true;
    private String dbDir   = "./db";
    public boolean dumpDatabase=false;
    private TransactionRunner runner=null;
    private CTATBerkeleyDBInstance mainDB=null;
    private CTATBerkeleyDBInstance catalogDb=null;
    private StoredClassCatalog javaCatalog=null;
    private ArrayList <CTATBerkeleyDBInstance> databases=null;
	    
	/**
	*
	*/	
	public CTATBerkeleyDB ()
	{  
    	setClassName ("CTATBerkeleyDB");
    	debug ("CTATBerkeleyDB ()");
    	
    	databases=new ArrayList<CTATBerkeleyDBInstance> ();
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
	public long getCacheMisses ()
	{
		
		long cacheMisses = env.getStats(null).getNCacheMiss();
		
		return (cacheMisses);
	}	
	/**
	 * 
	 */
	public Boolean startDBService ()
	{
		debug ("startDBService ()");
		
        // environment is transactional
        envConfig=new EnvironmentConfig();
        envConfig.setTransactional(true);
        
        if (create==true) 
        {
        	debug ("EnvironmentConfig.setAllowCreate(true);");
            envConfig.setAllowCreate(true);
        }
        
        env=new Environment(new File(dbDir), envConfig);
                
        try 
        {
			open();
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
			debug ("Caught a database exception whilst opening the db, aborting main server ...");
			env.toString();
			return (false);
		}	        
        
        if (dbDisabled==true)
        {
        	debug ("Error: database is not open or disabled, aborting");
        	return (false);
        }
                        	  
        try 
        {
			catalogDb=this.accessDB("java_class_catalog");
		} 
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (false);
		}        
        
        javaCatalog=new StoredClassCatalog(catalogDb.getDB());        
        
        return (true);
	}
    /**
     * Return the class catalog.
     */
    public final StoredClassCatalog getClassCatalog() 
    {    	
        return javaCatalog;
    }	
	/**
	 * 
	 */
	public boolean startDBThread ()
	{
		debug ("startDBThread ()");
		
		if (runner!=null)
		{
			try 
			{
				runner.run(this);
			} 
			catch (DatabaseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (false);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (false);
			}
		}	
		
		return (false);
	}
    /** 
     * Performs work within a transaction. 
     */
    public void doWork() 
    {
    	debug ("doWork ()");
        	
    	if (dumpDatabase==true)
    	{
    		dumpDB ();
    	}
    }
    /**
     * 
     */
    public CTATBerkeleyDBInstance findDB (String aDB)
    {
    	debug ("findDB ("+aDB+")");
    	
    	for (int i=0;i<databases.size();i++)
    	{
    		CTATBerkeleyDBInstance db=databases.get(i);
    		if (db.getInstanceName().toLowerCase().equals(aDB))
    			return (db);
    	}
    	
    	return (null);
    }
    /** 
     * Opens the main database and creates the Map. 
     */
    public CTATBerkeleyDBInstance open() throws Exception 
    {
    	debug ("open ()");
    	
    	mainDB=findDB ("TSMonitor");
    	
    	if (mainDB==null)
    	{
    		mainDB=new CTATBerkeleyDBInstance ();
    		mainDB.setInstanceName("TSMonitor");
    		mainDB.setEnvironment(env);
    		mainDB.openDB("TSMonitor");
        
    		databases.add(mainDB);
    	}	
                
        debug ("Database should be open and available");      

        return (mainDB);
    }  
    /** 
     * Find a database by name and if one does not exist, create it
     */
    public CTATBerkeleyDBInstance accessDB(String aDB) throws Exception 
    {
    	debug ("openDB ("+aDB+")");
    	        
    	CTATBerkeleyDBInstance db=findDB (aDB);
    	
    	if (db==null)
    	{
    		debug ("Database does not exist yet, creating ...");
    		
    		db=new CTATBerkeleyDBInstance ();
    		db.setInstanceName(aDB);
    		db.setEnvironment(env);
    		db.openDB(aDB);
        
    		databases.add(db);
                
    		debug ("Database should be open and available");
    	}	
        
        return (db);
    }        
    /** 
     * Closes the database. 
     */
    public void close() throws Exception 
    {
    	debug ("close ()");
    	
    	dbDisabled=true;
    	        
        debug("Closing databases ...");
                        
        for (int i=0;i<databases.size();i++)
        {
        	CTATBerkeleyDBInstance db=databases.get(i);
        	if (db!=null)
        	{
        		debug ("Closing: " + db.getInstanceName());
        		db.close();
        	}
        }
        
        debug("Closing class catalog ...");
        
        if (javaCatalog!=null)
        {
        	javaCatalog.close();
        	javaCatalog=null;
        }        
        
        debug("Closing database environment ...");
        
        if (env != null) 
        {
            env.close();
            env = null;
        }
                        
        debug("Resetting map ...");
                
        debug("Database successfully shutdown");
    }
    /**
     * 
     */
    public String getStatus ()
    {
    	if (env==null)
    	{
    		return ("No database environment available");
    	}
    	
    	return (env.getStats(null).toString());
    }
    /**
     * 
     */    
    public ArrayList<String> getDatabases ()
    {
    	if (env==null)
    		return (null);
    	    	
    	return (ArrayList<String>) (env.getDatabaseNames());
    }
    /**
     * 
     */
    public void dumpDB ()
    {
    	debug ("dumpDB ()");
    	
        trace.out(env.getStats(null).toString());
    }
}
