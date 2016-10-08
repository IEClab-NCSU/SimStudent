/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSEntry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTSEntry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.18  2012/10/05 16:27:46  akilbo
 Added some comments on various methods added

 Revision 1.17  2012/09/26 00:23:42  akilbo
 Changed storage of many of the variables from Strings into Longs

 Revision 1.16  2012/08/15 20:59:31  akilbo
 Added more information to the logging in setStatus( ). Now Displays time from last event logged.

 Revision 1.15  2012/08/13 20:37:41  akilbo
 Added some code to the logging of the ts system. Should properly log the status of the TS to a txt file.

 Revision 1.14  2012/05/07 19:09:12  vvelsen
 Added some visual tools to better manage our performance database

 Revision 1.13  2012/04/27 18:19:51  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.12  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.11  2012/04/18 19:20:17  vvelsen
 Bunch of fixes to the logic. The code should also be a tiny bit faster.

 Revision 1.10  2012/04/06 17:53:41  vvelsen
 Fixed a bug in the TS where it wouldn't track inactive sessions properly

 Revision 1.9  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.8  2012/03/30 18:26:21  vvelsen
 A bunch of changes to the way the server checks and verifies if services are down. There is still a tweak that needs to be made to TS testing since it doesn't always accurately detect if a TS is down

 Revision 1.7  2012/03/29 18:58:15  vvelsen
 Lots of bug fixes in the data representation and serialization of services.

 Revision 1.6  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.5  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.4  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.3  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 Revision 1.2  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATTSEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSEntry.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

	TS API/Formats

	$ printf "<service cmd=\"status\"/>\0" | nc ctat11 1503; echo ""

	Example requests (lines beginning w/ "$", but w/o the "$") and responses :

	$ <service cmd="status"/>
	<service>
  		<sessions count="1" />
  		<memory total="63307776" max="939524096" free="55742456" />
	</service>

	$ <service cmd="detail"/>
	<service>
  		<sessions count="1">
    		<session guid="qa-test_xx5" lastMessage="2012-02-03 20:25:44.384 UTC" />
  		</sessions>
	</service>

	$ <session cmd="detail"/>
	<session guid="null" result="error: null or empty guid" />

	$ <session guid="qa-test_xx5" cmd="detail"/>  # I'm about to add this cmd
	<session guid="qa-test_xx5" result="error: unknown cmd detail" />

	$ <session guid="qa-test_xx5" cmd="remove"/>
	<session guid="qa-test_xx5" result="removed" />

	$ <session cmd="detail"/>
	<session guid="null" result="error: null or empty guid" />

	$ <service cmd="detail"/>
	<service>
  		<sessions count="1">
    		<session guid="qa-test_xx6" lastMessage="2012-02-03 20:30:59.355 UTC" />
  		</sessions>
	</service>

	$ <service cmd="detail"/>
	<service>
  		<sessions count="1">
    		<session guid="qa-test_xx6" lastMessage="2012-02-03 20:32:14.851 UTC" />
  		</sessions>
	</service>

	$ <service cmd="shutdown"/>
	<service shutdownTime="Fri Feb 03 15:34:33 EST 2012" />

*/

package edu.cmu.hcii.ctat.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredSortedMap;
//TODO add comments
/**
 * 
 */
public class CTATTSEntry extends CTATClientEntry implements CTATMessageReceiver
{
	private long memtotal=0;
	private long memmax=0;
	private long memfree=0;
	private int sCount=0;
	
	private ArrayList <CTATTSSession>sessions=null;
	
	private String releaseName="";
	private String version="";
	private String buildDate="";
	
	private CTATStreamedSocket checker=null;
	private Boolean shouldUpdate=false;
	private Boolean updating=false;
	
	private CTATBerkeleyDBInstance sessionDB=null;
	private CTATBerkeleyDBInstance memoryDB=null;
	
	private CTATBerkeleyDBInstance dailySessionDB = null;
	private CTATBerkeleyDBInstance hourlySessionDB = null;
	private CTATBerkeleyDBInstance weeklySessionDB = null;
	private CTATBerkeleyDBInstance monthlySessionDB = null;
	
	private CTATBerkeleyDBInstance hourlyMemoryDB = null;
	private CTATBerkeleyDBInstance dailyMemoryDB = null;
	private CTATBerkeleyDBInstance weeklyMemoryDB = null;
	private CTATBerkeleyDBInstance monthlyMemoryDB = null;
	
	private StoredSortedMap<Long, CTATTSSession> sessionMap=null;
	private StoredSortedMap<Long, CTATTSMemory> memoryMap=null;


	//Maps for changing resolution of the database to look at datapoints in terms of hours/days/weeks/months
	private StoredSortedMap<Long, CTATTSSession> hourlySessionMap  = null;
	private StoredSortedMap<Long, CTATTSSession> dailySessionMap  = null;
	private StoredSortedMap<Long, CTATTSSession> weeklySessionMap = null;
	private StoredSortedMap<Long, CTATTSSession> monthlySessionMap = null;
	
	private StoredSortedMap<Long, CTATTSMemory> hourlyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> dailyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> weeklyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> monthlyMemoryMap = null;
	
	
	//Times to keep track of the beginning and end of time periods, these help when we grab a map over a range and iterate over the valuees in it, and average them
	Long hourStart= 0L;
	Long hourEnd = 0L;
	Long dayStart =0L;
	Long dayEnd= 0L;
	Long weekStart = 0L;
	Long weekEnd= 0L;
	Long monthStart= 0L;
	Long monthEnd = 0L;
	
	private SortedMap<Long, CTATTSSession> sessionMapRanged=null;
	private SortedMap<Long, CTATTSMemory> memoryMapRanged=null;	 
	
	private Object[] sessionObjectMap=null;
	private Object[] memoryObjectMap=null;
	
		
	/**
	*
	*/	
	public CTATTSEntry ()
	{  
    	setClassName ("CTATTSEntry");
    	debug ("CTATTSEntry ()");
    	this.setClientType("tutoringservice");
		this.setPort(1503);
		this.setAccess("HTTP");
		//setStatus("UP");
		
		sessions=new ArrayList<CTATTSSession> ();	
		
		//Get info for the current date to use to set boundaries
		DateTime currentTime = new DateTime();
		int year = currentTime.getYear();
		int month = currentTime.getMonthOfYear();
		int day = currentTime.getDayOfMonth();
		int hour = currentTime.getHourOfDay();
		
		
		//each of the next four blocks builds the boundaries for them based on the current time frame 
		DateTime hourTime = new DateTime(year,month,day, hour, 0, 0);
		hourStart = hourTime.toInstant().getMillis();
		hourEnd = hourTime.plusHours(1).getMillis();

		
		DateTime dayTime= new DateTime(year,month,day,0,0,0);
		dayStart = dayTime.getMillis();
		dayEnd = dayTime.plusDays(1).getMillis();
		
		
		DateTime weekTime= new DateTime(year,month,day,0,0,0);
		weekStart = weekTime.getMillis();
		weekEnd = weekTime.plusWeeks(1).getMillis();

		DateTime monthTime= new DateTime(year,month,1,0,0,0);
		monthStart = monthTime.getMillis();
		monthEnd =  monthTime.plusMonths(1).getMillis();
		
	}
	/**
	 * 
	 */
	private void setupDB ()
	{
		debug ("setupDB ()");
		
		CTATBerkeleyDB dbDriver=this.getDbDriver();
		
		if (dbDriver==null)
		{
			debug ("No database driver available, chances are this class is used purely for administrative purposes");
			return;
		}
		
		if (sessionDB==null)
		{
			try 
			{
				//create all the session dbs
				sessionDB=dbDriver.accessDB(this.getGuid()+"-TSSession/All");
				hourlySessionDB = dbDriver.accessDB(this.getGuid()+"-TSSession/Hour");
				dailySessionDB = dbDriver.accessDB(this.getGuid()+"-TSSession/Day");
				weeklySessionDB = dbDriver.accessDB(this.getGuid()+"-TSSession/Week");
				monthlySessionDB = dbDriver.accessDB(this.getGuid()+"-TSSession/Month");
				
			} 
			catch (Exception e) 
			{
				debug ("Error: unable to access or create db: " + this.getGuid ()+"-TSSession");
				e.printStackTrace();
			}
			
			if (sessionDB!=null)
			{
				LongBinding keyBinding = new LongBinding();
		        EntryBinding<CTATTSSession> sessionBinding = new SerialBinding<CTATTSSession>(dbDriver.getClassCatalog(), CTATTSSession.class);

		        sessionMap = new StoredSortedMap <Long, CTATTSSession> (sessionDB.getDB (), keyBinding, sessionBinding, true);	
		        hourlySessionMap = new StoredSortedMap<Long, CTATTSSession> (hourlySessionDB.getDB (), keyBinding, sessionBinding, true);
		        dailySessionMap = new StoredSortedMap<Long, CTATTSSession> (dailySessionDB.getDB (), keyBinding, sessionBinding, true);
		        weeklySessionMap = new StoredSortedMap<Long, CTATTSSession> (weeklySessionDB.getDB (), keyBinding, sessionBinding, true);
		        monthlySessionMap = new StoredSortedMap<Long, CTATTSSession> (monthlySessionDB.getDB (), keyBinding, sessionBinding, true);
				//sessionMapRanged=sessionMap.subMap (minTest,maxTest);
				//sessionObjectMap=sessionMap.entrySet().toArray();
			}
		}	
		     
		if (memoryDB==null)
		{
			try 
			{
				//create all the memory DBs
				memoryDB=dbDriver.accessDB(this.getGuid()+"-TSSession/All");
				hourlyMemoryDB = dbDriver.accessDB(this.getGuid() + "-TSMemory/Hour");
				dailyMemoryDB = dbDriver.accessDB(this.getGuid() + "-TSMemory/Day");
				weeklyMemoryDB = dbDriver.accessDB(this.getGuid() + "-TSMemory/Week");
				monthlyMemoryDB = dbDriver.accessDB(this.getGuid() + "-TSMemory/Month");
			} 
			catch (Exception e) 
			{
				debug ("Error: unable to access or create db: " + this.getGuid ()+"-TSMemory");
				e.printStackTrace();
			}
			
			if (memoryDB!=null)
			{
				LongBinding keyBinding = new LongBinding();
				EntryBinding<CTATTSMemory> memoryBinding = new SerialBinding<CTATTSMemory>(dbDriver.getClassCatalog(), CTATTSMemory.class);
				
				memoryMap = new StoredSortedMap <Long, CTATTSMemory> (memoryDB.getDB (), keyBinding, memoryBinding, true);
				hourlyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (hourlyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				dailyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (dailyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				weeklyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (weeklyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				monthlyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (monthlyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				
				//memoryMapRanged=memoryMap.subMap (minTest,maxTest);
				//memoryObjectMap=memoryMap.entrySet().toArray();
			}	
		}		       	    			
	}
	/**
	* Returns a view of the portion of this sorted map whose keys range from fromKey, inclusive, 
	* to toKey, exclusive. (If fromKey and toKey are equal, the returned sorted map is empty.) 
	* The returned sorted map is backed by this sorted map, so changes in the returned sorted 
	* map are reflected in this sorted map, and vice-versa. The returned Map supports all 
	* optional map operations that this sorted map supports.
	* 
	* The map returned by this method will throw an IllegalArgumentException if the user attempts 
	* to insert a key outside the specified range.
	* 
	* Note: this method always returns a half-open range (which includes its low endpoint but not 
	* its high endpoint). If you need a closed range (which includes both endpoints), and the key 
	* type allows for calculation of the successor a given key, merely request the subrange from 
	* lowEndpoint to successor(highEndpoint). For example, suppose that m is a map whose keys are 
	* strings. The following idiom obtains a view containing all of the key-value mappings in m 
	* whose keys are between low and high, inclusive:
	* 
	* Map sub = m.subMap(low, high+"\0");
	* 
	* A similarly technique can be used to generate an open range (which contains neither endpoint). 
	* The following idiom obtains a view containing all of the key-value mappings in m whose keys 
	* are between low and high, exclusive:
	* 
	* Map sub = m.subMap(low+"\0", high);
	*/
	public String getSessionBlock (Long startDate,Long endDate)
	{
		debug ("getSessionBlock ()");
		//TODO change?
		try
		{
			sessionMapRanged=sessionMap.subMap (startDate,endDate);
		}
		catch (IllegalArgumentException e)
		{
			debug ("One of the range indexes is out of bounds");
			return ("Out of Range");
		}
		
		StringBuffer formatted=new StringBuffer ();
		
		Iterator<Map.Entry<Long, CTATTSSession>> iter=sessionMapRanged.entrySet().iterator();
    	
		while (iter.hasNext()) 
		{
			Map.Entry<Long, CTATTSSession> entry = iter.next();
        
			if (entry!=null)
			{				
				formatted.append(entry.getKey ().toString ()+","+entry.getValue().toCSV()+"\n");
			}
			else
				debug ("Error getting session from map");	            	        
		}		
		
		return (formatted.toString());
	}
	/**
	* Returns a view of the portion of this sorted map whose keys range from fromKey, inclusive, 
	* to toKey, exclusive. (If fromKey and toKey are equal, the returned sorted map is empty.) 
	* The returned sorted map is backed by this sorted map, so changes in the returned sorted 
	* map are reflected in this sorted map, and vice-versa. The returned Map supports all 
	* optional map operations that this sorted map supports.
	* 
	* The map returned by this method will throw an IllegalArgumentException if the user attempts 
	* to insert a key outside the specified range.
	* 
	* Note: this method always returns a half-open range (which includes its low endpoint but not 
	* its high endpoint). If you need a closed range (which includes both endpoints), and the key 
	* type allows for calculation of the successor a given key, merely request the subrange from 
	* lowEndpoint to successor(highEndpoint). For example, suppose that m is a map whose keys are 
	* strings. The following idiom obtains a view containing all of the key-value mappings in m 
	* whose keys are between low and high, inclusive:
	* 
	* Map sub = m.subMap(low, high+"\0");
	* 
	* A similarly technique can be used to generate an open range (which contains neither endpoint). 
	* The following idiom obtains a view containing all of the key-value mappings in m whose keys 
	* are between low and high, exclusive:
	* 
	* Map sub = m.subMap(low+"\0", high);
	*/
	public String getMemoryBlock (Long startDate,Long endDate)
	{
		debug ("getMemoryBlock ()");
		//TODO Change?
		try
		{
			memoryMapRanged=memoryMap.subMap (startDate,endDate);
		}
		catch (IllegalArgumentException e)
		{
			debug ("One of the range indexes is out of bounds");
			return ("Out of Range");
		}		
		
		StringBuffer formatted=new StringBuffer ();
		
		Iterator<Map.Entry<Long, CTATTSMemory>> iter=memoryMapRanged.entrySet().iterator();
    	
		while (iter.hasNext()) 
		{
			Map.Entry<Long, CTATTSMemory> entry = iter.next();
        
			if (entry!=null)
			{				
				formatted.append(entry.getKey ().toString ()+","+entry.getValue().toCSV()+"\n");
			}
			else
				debug ("Error getting session from map");	            	        
		}				
		
		return (formatted.toString());
	}	
	/**
	*
	*/	
	public Boolean isUpdating() 
	{
		return updating;
	}
	/**
	*
	*/	
	public String getReleaseName() 
	{
		return releaseName;
	}
	/**
	*
	*/	
	public void setReleaseName(String releaseName) 
	{
		this.releaseName = releaseName;
	}
	/**
	*
	*/	
	public String getVersion() 
	{
		return version;
	}
	/**
	*
	*/	
	public void setVersion(String version) 
	{
		this.version = version;
	}
	/**
	*
	*/	
	public String getBuildDate() 
	{
		return buildDate;
	}
	/**
	*
	*/	
	public void setBuildDate(String buildDate) 
	{
		this.buildDate = buildDate;
	}	
	/**
	*
	*/	
	public void setStatus(String status) 
	{	
		super.setStatus(status);
		
		if (this.getShouldLog()==true)
		{		
			StringBuffer formatter=new StringBuffer ();

			Date stamp=new Date ();
			
			Integer currentTimeStamp = (int) stamp.getTime();
		
			formatter.append (String.format("%d",stamp.getTime()));
			formatter.append (",");
			formatter.append(status);
			formatter.append (",");
			formatter.append(String.format("%d", (currentTimeStamp-getLastTimestamp()))); //getting the difference between the current time and the time of the previous event
			
			setLastTimestamp(currentTimeStamp);
			
			if (logger!=null)
			{
				logger.addLine(formatter.toString ());
			}
		}	
		
		if ((status.toLowerCase().equals("ok")==false) && (status.toLowerCase().equals("up")==false))
		{
			debug ("Removing all sessions ..");
			setsCount (0);
			sessions.clear();
		}
		else
			resetReport (); // We're good
	}
	/**
	*
	*/
	public Boolean checkService ()
	{
		debug ("checkService ()");
		
		Boolean prevUpdate=shouldUpdate;
		
		debug("prevUpdate is "+ prevUpdate);
		
		if (checker==null){
			
			debug("checker is null");
			
			checker=new CTATStreamedSocket ();
		}
		
		checker.sendAndKeepOpen (this.getHostname(),this.getPort(),"<service cmd=\"all\" keepalive=\"true\" />",this);
		
		shouldUpdate=false;
				
		return (prevUpdate);		
	}
	/**
	*
	*/	
	@Override
	public void handleIncomingData(String text) 
	{
		updating=true;
						
		debug ("handleIncomingData ()");
		
		String oldStatus=this.getStatus();
		
		//this.setStatus("UP");
		
		resetReport ();
				
		shouldUpdate=true;
		
		if (text==null)
		{
			
			//only logs if a new status occurs
			if(!(oldStatus.equals("DOWN")))
				this.setStatus ("DOWN");
			
			if (oldStatus.equals(this.getStatus())==false)
			{
				// This way we make sure we only report a change in status and not the same thing every second
				appendReport ("Unable to receive status from server, marking service tutoring service at "+this.getHostname()+" as down");
			}	
		
			//shouldUpdate=true;
			updating=false;
			return;
		}
		
		else if ((text.isEmpty()==true) || (text.equals("")==true))
		{
			
			//only logs if a new status occurs
			if(!(oldStatus.equals("DOWN")))
				this.setStatus ("DOWN");
			
			if (oldStatus.equals(this.getStatus())==false)
			{
				// This way we make sure we only report a change in status and not the same thing every second
				appendReport ("Unable to receive status from server, marking service tutoring service at "+this.getHostname()+" as down");
			}			
			
			//shouldUpdate=true;
			updating=false;
			return;
		}		
		else
		{
			debug ("Received "+text.length()+" characters");
			debug (text);
			
			CTATXMLBase helper=new CTATXMLBase ();
			
			Document document=helper.loadXMLFromString (text);		
			
			if (document!=null)
			{
				
				debug("ts document is not null");
				
				debug("oldstatus is: " + oldStatus);
				if(!(oldStatus.equals("UP")))
					this.setStatus("UP");
				
				Element root=document.getDocumentElement();
			  
				this.fromTSXML (root);  // This activates logging if changes were found				
				this.setUpdated (true);
				//shouldUpdate=true;
			}		
		}			
		
		//shouldUpdate=true;
		
		updating=false;
	}
	/**
	*
	*/	
	@Override
	public void handleConnectionClosed() 
	{
		debug ("handleConnectionClose ()");
		
		//checkService ();
		//only logs if a new status occurs
		if(!(getStatus().equals("DOWN")))
			this.setStatus ("DOWN");
		
		appendReport ("Connection closed by foreign host for: "+this.getHostname());
		
		shouldUpdate=true;
	}
	/**
	*
	*/
	public void startLogging ()
	{
		debug ("startLogging ()");
		
		if (this.getShouldLog()==true)	
			logger.setFileID("TS-"+this.getGuid()+"-"+this.getHostname());
		else
			debug ("Logging is disabled for this entry");
	}
	/**
	*
	*/	
	public int getsCount() 
	{
		return sCount;
	}
	/**
	*
	*/	
	public void setsCount(int aCount) 
	{
		if (this.sCount!=aCount)
		{
			cleanup ();
		}
		
		this.sCount = aCount;
	}	
	/**
	*
	*/
	public long getMemtotal() 
	{
		return memtotal;
	}
	/**
	*
	*/
	public void setMemtotal(long memtotal) 
	{
		this.memtotal = memtotal;
	}
	/**
	*
	*/
	public long getMemmax() 
	{
		return memmax;
	}
	/**
	*
	*/
	public void setMemmax(long memmax) 
	{
		this.memmax = memmax;
	}
	/**
	*
	*/
	public long getMemfree() 
	{
		return memfree;
	}
	/**
	*
	*/
	public void debugStatus ()
	{
		debug ("Sessions reported: " + getsCount ()+", stored: "+this.sessions.size()+" Mem Total: " + getMemtotal ()+" Mem Free: " + getMemfree () + " Mem Max: " + getMemmax ());
	}
	/**
	*
	*/
	public void setMemfree(long memfree) 
	{
		this.memfree = memfree;
	}
	/**
	 * 
	 */	
	private void addSession (CTATTSSession aSession)
	{
		debug ("addSession (CTATTSSession)");
		sessions.add(aSession);
	}
	/**
	 * 
	 */
	private CTATTSSession getSession (String aSession)
	{
		debug ("getSession ("+aSession+") -> " + sessions.size());
		
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession test=sessions.get(i);
			
			debug ("Comparing " + test.getInstanceName() + " to " + aSession);
			
			if (test.getInstanceName().equals(aSession)==true)
			{
				debug ("Found");
				return (test);
			}
		}	
		
		return (null);
	}
	/**
	 * 
	 */
	private void removeSession (String aSession)
	{
		debug ("removeSession ("+aSession+")");
		
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession test=sessions.get(i);
			if (test.getInstanceName().equals(aSession)==true)
			{
				debug ("Removing: " + test.getInstanceName());
				sessions.remove(test);
			}
		}			
	}				
	/**
	*
	*/	
	public String toXML() 
	{
		debug ("toXML ()");
		
		StringBuffer buffer=new StringBuffer ();
		buffer.append(toXMLOpen());
		
		buffer.append("<version releaseName=\""+this.getReleaseName()+"\" versionNumber=\""+this.getVersion()+"\" buildDate=\""+this.getBuildDate()+"\" ></version>");
		
		buffer.append("<sessions>");
		
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession sess=sessions.get(i);
						
			buffer.append("<session guid=\""+sess.getInstanceName()+"\" lastMessage=\""+sess.getLastMessage()+"\" totalTransactionMs=\""+sess.getTotalTransactionMs()+"\" transactionCount=\""+sess.getTransactionCount()+"\" firstTransactionTime=\""+sess.getFirstTransactionTime()+"\" longestTransactionMs=\""+sess.getLongestTransactionMs()+"\" longestTransactionStartTime=\""+sess.getLongestTransactionStartTime()+"\" />");										
		}
						
		buffer.append("</sessions>");
		
		buffer.append("<memory total=\""+this.getMemtotal()+"\" max=\""+this.getMemmax()+"\" free=\""+this.getMemfree()+"\" />");
				
		buffer.append(toXMLClose());
		
		return (buffer.toString());
	}
	/**
	*
	*/	
	public String toXMLUpdated()
	{
		debug ("toXMLUpdated ()");
		
		StringBuffer buffer=new StringBuffer ();
		buffer.append(toXMLOpen());
				
		// For now we append the version info to every message since we only get this
		// through ping to the TS and not through the initial registration through
		// the CTATServiceModule
		
		buffer.append("<version releaseName=\""+this.getReleaseName()+"\" versionNumber=\""+this.getVersion()+"\" buildDate=\""+this.getBuildDate()+"\" ></version>");
		
		buffer.append("<sessions>");
		
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession sess=sessions.get(i);
			
			/**
			 * I've removed the check that sees if any of the data has changed and instead it will
			 * always send the entire list of sessions. Currently I know no other way to make sure
			 * the monitor clients have an accurate view with only partial lists being sent.
			 */
			
			//if (sess.getActive()==true)
			//{
				buffer.append("<session guid=\""+sess.getInstanceName()+"\" lastMessage=\""+sess.getLastMessage()+"\" totalTransactionMs=\""+sess.getTotalTransactionMs()+"\" transactionCount=\""+sess.getTransactionCount()+"\" firstTransactionTime=\""+sess.getFirstTransactionTime()+"\" longestTransactionMs=\""+sess.getLongestTransactionMs()+"\" longestTransactionStartTime=\""+sess.getLongestTransactionStartTime()+"\" />");
			//}	
		}
					
		buffer.append("</sessions>");
		
		buffer.append("<memory total=\""+this.getMemtotal()+"\" max=\""+this.getMemmax()+"\" free=\""+this.getMemfree()+"\" />");		
		
		buffer.append(toXMLClose());
		
		return (buffer.toString());
	}		
	/**
	*
	*/	
	public Boolean fromTSXML (Element root) 
	{
		debug ("fromTSXML ()");
									
		if (root.getNodeName().equals("service")==true)
		{			
			NodeList children=root.getChildNodes ();
			      
			if (children==null)
			{
				debug ("Internal error: children list is null");
				return (false);
			}
			      
			if (children.getLength()>0)
			{    
			    for (int i=0;i<children.getLength();i++) 
			    {
			    	Node node=children.item (i);
			    	
			    	//showNodeType (node);
			    	
			    	if (node.getNodeType ()==Node.ELEMENT_NODE)
			    	{
			    		Element transformer=(Element) node;
			    		
			    		if (node.getNodeName().equals("version")==true)
			    		{
			    			this.setReleaseName(transformer.getAttribute("releaseName"));	
			    			this.setVersion(transformer.getAttribute("versionNumber"));
			    			this.setBuildDate(transformer.getAttribute("buildDate"));				    			
			    		}
			    		
			    		if (node.getNodeName().equals("memory")==true)
			    		{
			    			this.setMemtotal(Long.parseLong(transformer.getAttribute("total")));	
			    			this.setMemmax(Long.parseLong(transformer.getAttribute("max")));
			    			this.setMemfree(Long.parseLong(transformer.getAttribute("free")));			    			
			    		}
			    		
			    		if (node.getNodeName().equals("sessions")==true)
			    		{
			    			debug ("Found sessions node");
			    						    			
			    			makeAllInactive ();
			    						    			
			    			NodeList sess=node.getChildNodes ();
						      
			    			if (sess!=null)
			    			{
			    			    for (int j=0;j<sess.getLength();j++) 
			    			    {						    		
							    	Node sessNode=sess.item (j);

							    	if (sessNode.getNodeType ()==Node.ELEMENT_NODE)
							    	{						    		
							    		Element sessEntry=(Element) sessNode;
						    		
							    		if (sessEntry.getNodeName().equals("session")==true)
							    		{							    			
							    			//debugStatus ();
							    			
							    			/*
							    			 * <service>
  											 * 	<sessions count="1">
  											 * 		<session guid="TestA" 
  											 * 				 lastMessage="2012-03-13 12:49:21.193 UTC" 
  											 * 				 ip="128.2.176.49" 
  											 * 				 totalTransactionMs="3010" 
  											 * 				 transactionCount="14" 
  											 * 				 firstTransactionTime="2012-03-13 12:48:21.349 UTC" 
  											 * 				 longestTransactionMs="2853" 
  											 * 				 longestTransactionStartTime="2012-03-13 12:48:21.349 UTC" 
  											 * 				 diskLogEntries="0" 
  											 * 				 forwardLogEntries="0" 
  											 * 				 diskLogErrors="0" 
  											 * 				 forwardLogErrors="0" />
  											 * </sessions>
  											 * </service>
							    			 */
							    										    			
							    			CTATTSSession aSession=createSessionInfo (sessEntry.getAttribute("guid"),
							    												  	  sessEntry.getAttribute("lastMessage"),
							    												  	  sessEntry.getAttribute("result"),
							    												  	  sessEntry.getAttribute("totalTransactionMs"),
							    												  	  sessEntry.getAttribute("transactionCount"),
							    												  	  sessEntry.getAttribute("firstTransactionTime"),
							    												  	  sessEntry.getAttribute("longestTransactionMs"),
							    												  	  sessEntry.getAttribute("longestTransactionStartTime"),
							    												  	  sessEntry.getAttribute("diskLogEntries"),
							    												  	  sessEntry.getAttribute("forwardLogEntries"),
							    												  	  sessEntry.getAttribute("diskLogErrors"),
							    												  	  sessEntry.getAttribute("forwardLogErrors"));

							    			// Log newly created session, but only if the data is different
							    			
						    				logState (aSession,
						    						  this.getMemtotal(),
						    						  this.getMemmax(),
						    						  this.getMemfree());
						    				
						    				// Swap old for new ...
						    				
						    				removeSession (aSession.getInstanceName());
						    				aSession.setUpdated(true);
						    				addSession (aSession);
						    				
						    				//debugStatus ();
							    		}
							    		else
							    			debug ("Error: element does not represent a session: " + sessEntry.toString());
						    		}
			    			    }
			    			}			    			
			    		}			    					    		
			    	} 
			    }                        
			}					
		}
		else		
		{
			debug ("Properties tag not found in node, instead got: " + root.getNodeName());
			return (false);
		}
		
		return (true);
	}
	/**
	 * 
	 */
	private CTATTSSession createSessionInfo (String aSession,
								 		 	 String lastMessage,
								 		 	 String result,
								 		 	 String totalTransactionMs,
								 		 	 String transactionCount,
								 		 	 String firstTransactionTime,
								 		 	 String longestTransactionMs,
								 		 	 String longestTransactionStartTime,
								 		 	 String diskLogEntries,
								 		 	 String forwardLogEntries,
								 		 	 String diskLogErrors,
								 		 	 String forwardLogErrors)
	{
		debug ("setSessionInfo ("+aSession+","+lastMessage+",...)");
		
		/*
		CTATTSSession test=getSession (aSession);
		if (test==null)
		{
			test=new CTATTSSession ();
			test.setInstanceName(aSession);
			sessions.add(test);
		}
		*/
		
		CTATTSSession test=new CTATTSSession ();
		
		debug ("Setting basic session data ..");
		
		test.setInstanceName(aSession);
		test.setActive(true);		
		test.setLastMessage(lastMessage);
		test.setResult(result);
		test.setTotalTransactionMs(Long.parseLong(totalTransactionMs));
		test.setTransactionCount(Long.parseLong(transactionCount));
		test.setFirstTransactionTime(Long.parseLong(firstTransactionTime));
		test.setLongestTransactionMs(Long.parseLong(longestTransactionMs));
		test.setLongestTransactionStartTime(Long.parseLong(longestTransactionStartTime));
		
		debug ("Setting extended session data ..");
		
		test.setDiskLogEntries(Long.parseLong(diskLogEntries)); 
		test.setForwardLogEntries(Long.parseLong(forwardLogEntries)); 
		test.setDiskLogErrors(Long.parseLong(diskLogErrors));
		test.setForwardLogErrors(Long.parseLong(forwardLogErrors)); 		
		
		return (test);
	}	
	/**
	*
	*/
	private void logState (CTATTSSession aSession,
						   long aTotal,
						   long aMax,
						   long aFree)
	{
		debug ("logState ()");
		
		if (this.getShouldLog()==false)
			return;
		
		if (aSession.getUpdated()==false)
			return;
		
		if (logger==null)
		{
			debug ("Internal error: no logger available!");
			return;
		}
		
		CTATTSSession prevSession=getSession (aSession.getInstanceName());
		
		if (prevSession!=null)
		{
			debug ("We have a previous session, checking ...");
			
			aSession.setActive(prevSession.getActive());
			
			if (
				(prevSession.getInstanceName().equals(aSession.getInstanceName())==true) &&
				(prevSession.getTransactionCount().equals(aSession.getTransactionCount())==true) && 
				(prevSession.getDiskLogEntries().equals(aSession.getDiskLogEntries())==true) &&
				(prevSession.getForwardLogEntries().equals(aSession.getForwardLogEntries())==true) &&
				(prevSession.getDiskLogErrors().equals (aSession.getDiskLogErrors())==true) &&
				(prevSession.getForwardLogErrors().equals (aSession.getForwardLogErrors())==true)
			   )	
			{
				debug ("No important changes detected");
				prevSession.addrepeats(1);
				return;
			}
			else
				debug ("Previous session is different, logging ...");
		}
		else
			debug ("No previous session found for: " + aSession.getInstanceName());
		
		StringBuffer tabber=new StringBuffer ();
		tabber.append (aSession.getInstanceName());
		tabber.append ("\t");
		
		tabber.append (aSession.getLastMessage());
		tabber.append ("\t");

		tabber.append (aSession.getTotalTransactionMs());
		tabber.append ("\t");
		
		tabber.append (aSession.getTransactionCount());
		tabber.append ("\t");
		
		tabber.append (aSession.getFirstTransactionTime());
		tabber.append ("\t");		

		tabber.append (aSession.getLongestTransactionMs());
		tabber.append ("\t");		
		
		tabber.append (aSession.getLongestTransactionStartTime());
		tabber.append ("\t");
				
		tabber.append (aSession.getDiskLogEntries());
		tabber.append ("\t");
		
		tabber.append (aSession.getForwardLogEntries());
		tabber.append ("\t");
		
		tabber.append (aSession.getDiskLogErrors());
		tabber.append ("\t");
		
		tabber.append (aSession.getForwardLogErrors());
		tabber.append ("\t");
						
		tabber.append (String.format("%d",aTotal));
		tabber.append ("\t");
		tabber.append (String.format("%d",aMax));
		tabber.append ("\t");
		tabber.append (String.format("%d",aFree));
		
		logger.addLine(tabber.toString());
		
		Date generator=new Date ();
		long time = generator.getTime();
		
		setupDB ();
		
		if (sessionDB!=null)
		{
			sessionMap.put(time,aSession);
		}	
							
		CTATTSMemory memCompactor=new CTATTSMemory ();
		memCompactor.setMemUsed(aTotal-aFree);
		memCompactor.setMemTotal(aTotal);
		memCompactor.setMemMax(aMax);
			
		if (memoryDB!=null)
		{
			memoryMap.put(time,memCompactor);
		}
		checkResolutions(time);
		
		aSession.setUpdated(false);		
	}	
	
	
	/**
	 * This 
	 * @param time
	 */
	private void checkResolutions(long time) {
		
		debug("checkResolutions(" + time + ")");
		//TODO add catch up methods
		if( time >= hourEnd){
			
			debug("the difference is: " + (hourStart-hourEnd));
			hourlyMemoryMap.put(hourStart,avgMemory(memoryMap.subMap(hourStart, hourEnd)));
			hourlySessionMap.put(hourStart,avgSession(sessionMap.subMap(hourStart, hourEnd)));
			hourStart=hourEnd;
			DateTime dtHourEnd = new DateTime(hourEnd);
			hourEnd = dtHourEnd.plusHours(1).toInstant().getMillis();
		}
		
		if( time >=dayEnd){
			dailyMemoryMap.put(dayStart,avgMemory(hourlyMemoryMap.subMap(dayStart,dayEnd)));
			dailySessionMap.put(dayStart,avgSession(hourlySessionMap.subMap(dayStart,dayEnd)));				hourlyMemoryMap.put(hourStart,avgMemory(memoryMap.subMap(hourStart, hourEnd)));
			dayStart=dayEnd;
			DateTime dtDayEnd= new DateTime(dayEnd);
			dayEnd = dtDayEnd.plusDays(1).toInstant().getMillis();
			
		}
		
		if( time >=weekEnd){
			
			weeklyMemoryMap.put(weekStart,avgMemory(weeklyMemoryMap.subMap(weekStart,weekEnd)));
			weeklySessionMap.put(weekStart,avgSession(weeklySessionMap.subMap(weekStart,weekEnd)));
			weekStart=weekEnd;
			DateTime dtWeekEnd= new DateTime(weekEnd);
			weekEnd = dtWeekEnd.plusWeeks(1).toInstant().getMillis();
			
		}
		
		if( time >=monthEnd){
			monthlyMemoryMap.put(monthStart,avgMemory(monthlyMemoryMap.subMap(monthStart, monthEnd)));
			monthlySessionMap.put(monthStart,avgSession(monthlySessionMap.subMap(monthStart, monthEnd)));
			monthStart = monthEnd;
			DateTime dtMonthEnd= new DateTime(monthEnd);
			dayEnd = dtMonthEnd.plusMonths(1).toInstant().getMillis();
			
		}
		
	}
	/**
	*
	*/
	private CTATTSSession findFirstInactive ()
	{
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession test=sessions.get(i);
			if (test.getActive()==false)
				return (test);
		}	
		
		return (null);
	}
	/**
	*
	*/
	@SuppressWarnings("unused")
	private CTATTSSession findFirstActive ()
	{
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession test=sessions.get(i);
			if (test.getActive()==false)
				return (test);
		}	
		
		return (null);
	}	
	/**
	*
	*/
	private void makeAllInactive ()
	{
		for (int i=0;i<sessions.size();i++)
		{
			CTATTSSession test=sessions.get(i);
			test.setActive(false);
		}		
	}	
	/**
	*
	*/
	public void cleanup ()
	{
		debug ("cleanup ()");
				
		CTATTSSession activeSession=findFirstInactive ();
		
		while (activeSession!=null)
		{
			removeSession (activeSession.getInstanceName());
			
			activeSession=findFirstInactive ();					
		}
	}	
	/*
	 * this method like the avgMemory() method takes a submap from any existing databases and creates an average of all the entries on that submap, complete with compesation for repeats.
	 * This then returns a TSSession object which can be placed into a databaset one level higher up, for example, you would take a submap of days from monday to sunday, average the values found in them, 
	 * and then return a session to be placed into a higher level map of the week.
	 */
	private CTATTSSession avgSession (Map<Long, CTATTSSession> aMap)
	{
		debug ("avgMemory ("+aMap.entrySet().size()+")");

		
        Iterator<Map.Entry<Long, CTATTSSession>> iter=aMap.entrySet().iterator();
        
        
        //set a baseline for all the statistics
        Long counter=0L;
        
    	Long totalTransactionMs=0L;
    	Long transactionCount=0L;
    	Long firstTransactionTime=0L;
    	Long longestTransactionMs=0L;
    	Long longestTransactionStartTime=0L;
    	Long diskLogEntries=0L; 
    	Long forwardLogEntries=0L; 
    	Long diskLogErrors=0L;
    	Long forwardLogErrors=0L;

        
    
        //iterate through the items in the list
        while ((iter.hasNext())) 
        {
            Map.Entry<Long, CTATTSSession> entry = iter.next();
            
            if (entry!=null)
            {            
            	int repeats = entry.getValue().getRepeats();
            	
            	totalTransactionMs=totalTransactionMs + entry.getValue().getTotalTransactionMs();
            	transactionCount= transactionCount + entry.getValue().getTransactionCount();
            	firstTransactionTime = firstTransactionTime + entry.getValue().getFirstTransactionTime();
            	longestTransactionMs= longestTransactionMs + entry.getValue().getLongestTransactionMs();
            	longestTransactionStartTime = longestTransactionStartTime + entry.getValue().getLongestTransactionStartTime();
            	diskLogEntries= diskLogEntries + entry.getValue().getDiskLogEntries();
            	forwardLogEntries = forwardLogEntries + entry.getValue().getForwardLogEntries();
            	diskLogErrors = diskLogErrors + entry.getValue().getDiskLogErrors();
            	forwardLogErrors= forwardLogErrors + entry.getValue().getForwardLogErrors();
            	counter= counter + repeats;
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
     
        CTATTSSession thisSession = new CTATTSSession();
        //thisSession.setInstanceName();
        
        if(counter>0){
        thisSession.setActive(true);		
        	thisSession.setTotalTransactionMs(totalTransactionMs/counter);
        	thisSession.setTransactionCount(transactionCount/counter);
        	thisSession.setFirstTransactionTime(firstTransactionTime/counter);
        	thisSession.setLongestTransactionMs(longestTransactionMs/counter);
        	thisSession.setLongestTransactionStartTime(longestTransactionStartTime/counter);
        }
        
        return thisSession;
	}
	
	/*
	 * this method like the avgSession() method takes a submap from any existing databases and creates an average of all the entries on that submap, complete with compesation for repeats.
	 * This then returns a TSSession object which can be placed into a databaset one level higher up, for example, you would take a submap of days from monday to sunday, average the values found in them, 
	 * and then return a session to be placed into a higher level map of the week.
	 */
	private CTATTSMemory avgMemory (Map<Long, CTATTSMemory> aMap)
	{
		debug ("avgMemory ("+aMap.entrySet().size()+")");
		
        Iterator<Map.Entry<Long, CTATTSMemory>> iter=aMap.entrySet().iterator();
        
        Long counter=0L;

        Long memUsed = 0L;
        Long memMax = 0L;
        Long memTotal = 0L;
        
        
        while ((iter.hasNext())) 
        {
            Map.Entry<Long, CTATTSMemory> entry = iter.next();
            
            if (entry!=null)
            {          
            int repeats = entry.getValue().getRepeats();	
            memUsed = memUsed+ entry.getValue().getMemUsed()*(repeats+1);
            memMax = memMax + entry.getValue().getMemMax()*(repeats+1);
            memTotal = memTotal + entry.getValue().getMemTotal()*(repeats+1);
            	
            counter= counter + repeats;
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
        
     
       
        CTATTSMemory thisMemory = new CTATTSMemory(); 
       if(counter>0){
        	thisMemory.setMemUsed(memUsed/counter);
        	thisMemory.setMemMax(memMax/counter);
        	thisMemory.setMemTotal(memTotal/counter);
        }
   
        
       
        return thisMemory;
	}
}

	