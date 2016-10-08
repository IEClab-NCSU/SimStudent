/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATBerkeleyDBDataGenerator.java,v 1.3 2012/10/08 14:21:00 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATBerkeleyDBDataGenerator.java,v $
 Revision 1.3  2012/10/08 14:21:00  akilbo
 Changed the CTATlink() to include a new filemanager object as the old constructer has been deprecated.

 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.12  2012/10/04 15:31:09  akilbo
 Added Alternate Generation Technique to use methods similar to the TS Entry

 Revision 1.11  2012/09/27 19:25:04  akilbo
 Added plenty of changes to the TS memory and TS session creation as a way to test the changes to building them in the actual TS Class, currently there is a bug with the time not incrementing as wanted.

 Revision 1.10  2012/09/25 16:35:16  akilbo
 added some more changes, next one will have full algorithm that will take place in the TSsession to make sure it performs in a reasonable amount of time

 Revision 1.9  2012/09/24 20:04:39  akilbo
 Added JodaTime package and most of structure for db averaging that will hopefully be used in the TSEntry class

 Revision 1.8  2012/09/20 22:06:15  akilbo
 fixed several small bugs, works like a charm now

 Revision 1.7  2012/09/20 15:52:23  akilbo
 Created a more reliable method of building databases greater than the /all entry that doesn't require them to be a multiple of nrTimeDelta

 Revision 1.6  2012/09/17 20:05:49  akilbo
 Now generates several databases for each entry with different time frames. weeks and months are being troublesome at the moment however and need to be fixed

 Revision 1.5  2012/09/06 18:03:22  akilbo
 Fixed timing on generated entries from 5 miliseconds to 5 seconds.

 Revision 1.4  2012/05/07 19:09:12  vvelsen
 Added some visual tools to better manage our performance database

 Revision 1.3  2012/05/03 15:32:50  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 Revision 1.2  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.1  2012/04/27 18:19:51  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 $RCSfile: CTATBerkeleyDBDataGenerator.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATBerkeleyDBDataGenerator.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/

package edu.cmu.hcii.ctat.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.table.DefaultTableModel;

import org.joda.time.DateTime;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
//import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATDesktopFileManager;
import edu.cmu.hcii.ctat.CTATLink;

/**
 * 
 */
public class CTATBerkeleyDBDataGenerator extends CTATBase 
{
	private CTATBerkeleyDB driver=null;
	private Long nrEntries=(long) 100000;
	private Long nrDatabases=(long) 3;
	private Long nrTimeDelta=(long) 1000 * 60 * 5; // 3 minutes (1000 ms/s * 60 s/min * 5 min = 200000 ms
	private String startDate="Undefined";
	
	//the db instance fields
	
	private CTATBerkeleyDBInstance sessionDB=null;
	private CTATBerkeleyDBInstance memoryDB=null;
	
	//private CTATBerkeleyDBInstance dummyDB=null;
	
	private CTATBerkeleyDBInstance dailySessionDB = null;
	private CTATBerkeleyDBInstance hourlySessionDB = null;
	private CTATBerkeleyDBInstance weeklySessionDB = null;
	private CTATBerkeleyDBInstance monthlySessionDB = null;
	
	private CTATBerkeleyDBInstance hourlyMemoryDB = null;
	private CTATBerkeleyDBInstance dailyMemoryDB = null;
	private CTATBerkeleyDBInstance weeklyMemoryDB = null;
	private CTATBerkeleyDBInstance monthlyMemoryDB = null;
	
	// the the StoredSortedMap Fields 
	
	private StoredSortedMap<Long, CTATTSSession> sessionMap=null;
	private StoredSortedMap<Long, CTATTSMemory> memoryMap=null;
	
	//private StoredMap<Long, String> dummyMap=null;
	
	private StoredSortedMap<Long, CTATTSSession> hourlySessionMap  = null;
	private StoredSortedMap<Long, CTATTSSession> dailySessionMap  = null;
	private StoredSortedMap<Long, CTATTSSession> weeklySessionMap = null;
	private StoredSortedMap<Long, CTATTSSession> monthlySessionMap = null;
	
	private StoredSortedMap<Long, CTATTSMemory> hourlyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> dailyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> weeklyMemoryMap = null;
	private StoredSortedMap<Long, CTATTSMemory> monthlyMemoryMap = null;
	
	Long hourStart= 0L;
	Long hourEnd = 0L;
	Long dayStart =0L;
	Long dayEnd= 0L;
	Long weekStart = 0L;
	Long weekEnd= 0L;
	Long monthStart= 0L;
	Long monthEnd = 0L;
	
	/**
	 * 
	 */
	public CTATBerkeleyDBDataGenerator ()
	{
    	setClassName ("CTATBerkeleyDBDataGenerator");
    	debug ("CTATBerkeleyDBDataGenerator ()");
    	
	    driver=new CTATBerkeleyDB ();
	    driver.startDBService ();		
	}
	/**
	 * 
	 */
	public void execute ()
	{
		debug ("execute (entries: "+nrEntries+", databases: " + nrDatabases+", delta: " + nrTimeDelta +")");
		
		for (int i=0;i<nrDatabases;i++)
		{		
			//>-------------------------------------------------
			
			try 
			{
				UUID seessionUUID = java.util.UUID.randomUUID();
				sessionDB=driver.accessDB(seessionUUID+"-TSSession/All");
				hourlySessionDB = driver.accessDB(seessionUUID+"-TSSession/Hour");
				dailySessionDB = driver.accessDB(seessionUUID+"-TSSession/Day");
				weeklySessionDB = driver.accessDB(seessionUUID+"-TSSession/Week");
				monthlySessionDB = driver.accessDB(seessionUUID+"-TSSession/Month");
			} 
			catch (Exception e) 
			{
				debug ("Error: unable to access or create db: " + java.util.UUID.randomUUID()+"-TSSession");
				e.printStackTrace();
			}
					
			if (sessionDB!=null)
			{
				LongBinding keyBinding = new LongBinding();
		        EntryBinding<CTATTSSession> sessionBinding = new SerialBinding<CTATTSSession>(driver.getClassCatalog(), CTATTSSession.class);

		        sessionMap = new StoredSortedMap<Long, CTATTSSession> (sessionDB.getDB (), keyBinding, sessionBinding, true);	
		        hourlySessionMap = new StoredSortedMap<Long, CTATTSSession> (hourlySessionDB.getDB (), keyBinding, sessionBinding, true);
		        dailySessionMap = new StoredSortedMap<Long, CTATTSSession> (dailySessionDB.getDB (), keyBinding, sessionBinding, true);
		        weeklySessionMap = new StoredSortedMap<Long, CTATTSSession> (weeklySessionDB.getDB (), keyBinding, sessionBinding, true);
		        monthlySessionMap = new StoredSortedMap<Long, CTATTSSession> (monthlySessionDB.getDB (), keyBinding, sessionBinding, true);
			}
				
			//>-------------------------------------------------
			     
			try 
			{
				UUID memoryUUID = java.util.UUID.randomUUID();
				memoryDB=driver.accessDB(memoryUUID +"-TSMemory/All");
				hourlyMemoryDB = driver.accessDB(memoryUUID + "-TSMemory/Hour");
				dailyMemoryDB = driver.accessDB(memoryUUID + "-TSMemory/Day");
				weeklyMemoryDB = driver.accessDB(memoryUUID + "-TSMemory/Week");
				monthlyMemoryDB = driver.accessDB(memoryUUID + "-TSMemory/Month");
			} 
			catch (Exception e) 
			{
				debug ("Error: unable to access or create db: " + java.util.UUID.randomUUID()+"-TSMemory");
				e.printStackTrace();
			}
				
			if (memoryDB!=null)
			{
				LongBinding keyBinding = new LongBinding();
				EntryBinding<CTATTSMemory> memoryBinding = new SerialBinding<CTATTSMemory>(driver.getClassCatalog(), CTATTSMemory.class);

				memoryMap = new StoredSortedMap<Long, CTATTSMemory> (memoryDB.getDB (), keyBinding, memoryBinding, true);
				hourlyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (hourlyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				dailyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (dailyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				weeklyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (weeklyMemoryDB.getDB (), keyBinding, memoryBinding, true);
				monthlyMemoryMap = new StoredSortedMap<Long, CTATTSMemory> (monthlyMemoryDB.getDB (), keyBinding, memoryBinding, true);
			}
				
			//>-------------------------------------------------
			     
			/*
			try 
			{
				dummyDB=driver.accessDB(java.util.UUID.randomUUID()+"-TSDummy");
			} 
			catch (Exception e) 
			{
				debug ("Error: unable to access or create db: " + java.util.UUID.randomUUID()+"-TSDummy");
				e.printStackTrace();
			}
				
			if (dummyDB!=null)
			{
				LongBinding keyBinding = new LongBinding();
				StringBinding valueBinding = new StringBinding();

				dummyMap=new SortedStoredMap<Long, String> (dummyDB.getDB (), keyBinding, valueBinding, true);
			}
			*/				
				
			//>-------------------------------------------------
				
			
			/*
			Date dateGenerator=new Date ();
			Long converter=dateGenerator.getTime();
			
		

			Date thedate=null;
			
			
			if (startDate.equals("Undefined")==false)
			{
				try 
				{
					thedate=new SimpleDateFormat ("MM dd yyyy", Locale.ENGLISH).parse(startDate);
				} 
				catch (ParseException e) 
				{
					debug ("Error: the supplied date is not in the proper format. Please enter dates in: MM DD YYYY");
					e.printStackTrace();
					System.exit(0);
				}
			
				converter=thedate.getTime();
			}
			*/
			DateTime currentTime = new DateTime();
			Long converter=currentTime.getMillis();
			debug("converter is " + currentTime);
			
			
			int year = currentTime.getYear();
			int month = currentTime.getMonthOfYear();
			int day = currentTime.getDayOfMonth();
			int hour = currentTime.getHourOfDay();
			
			DateTime hourTime = new DateTime(year,month,day, hour, 0, 0);
			debug("hourStart is:" + hourTime);
			hourStart = hourTime.toInstant().getMillis();
			debug("hourStart is " + hourStart);
			hourEnd = hourTime.plusHours(1).getMillis();
			debug("hourEnd is: " + hourEnd);
			debug("hourStart is:" + hourStart);
			debug("the difference is: " + (hourStart-hourEnd));
			
			DateTime dayTime= new DateTime(year,month,day,0,0,0);
			dayStart = dayTime.getMillis();
			dayEnd = dayTime.plusDays(1).getMillis();
			
			
			DateTime weekTime= new DateTime(year,month,day,0,0,0);
			weekStart = weekTime.getMillis();
			weekEnd = weekTime.plusWeeks(1).getMillis();

			DateTime monthTime= new DateTime(year,month,1,0,0,0);
			monthStart = monthTime.getMillis();
			monthEnd =  monthTime.plusMonths(1).getMillis();
			
			
			int seconds = (int) (nrTimeDelta/1000);
			
			debug ("Simulating " + nrEntries + " with a time delta of " + seconds + " seconds, spanning a total of " + (nrEntries*seconds) + " seconds, or " + ((nrEntries*seconds)/60) + " minutes, or " + ((nrEntries*seconds)/(60*60)) + " hours , or " + ((nrEntries*seconds)/(60*60*24)) + " days");
				
			debug ("Adding entries, this might take a while ...");
				
			debug("converter is:" + converter);
			generate(converter);	
			//generateData(converter, sessionMap, memoryMap, nrTimeDelta);
			//generateData(hourStart, hourlySessionMap, hourlyMemoryMap, 3600000L);
			//generateData(dayStart, dailySessionMap, dailyMemoryMap, 86400000L);
			//generateData(weekStart, weeklySessionMap, weeklyMemoryMap, 604800000L);
			//generateData(monthStart, monthlySessionMap, monthlyMemoryMap, 2629740000L);
		
				
				
				//dummyMap.put(converter,"Test String: " + java.util.UUID.randomUUID());
							
				
			debug ("Checking integrity ...");
				
			//dummyDB.assignMap(dummyMap);
			//dummyDB.checkDB();
							
			sessionDB.close();
			memoryDB.close();
			//dummyDB.close ();
				
			debug ("Add done");
		}
		
		try 
		{
			driver.close();
		} 
		catch (Exception e) {
			debug ("Error closing databases");
			e.printStackTrace();
		}
	}




	private void generateData (Long converter, StoredSortedMap<Long, CTATTSSession> dataSessionMap, StoredSortedMap<Long, CTATTSMemory> dataMemoryMap, Long msper){
		
		
		Long counter= 0L;
		Long total= 0L;
		
		Long totalentries = (nrEntries*nrTimeDelta) / msper ;
		
		debug("now creating "  + totalentries +" entries");
		
		for (Long j= 0L ;j<totalentries;j++)
		{
			CTATTSSession sessionProbe=new CTATTSSession ();
			dataSessionMap.put(converter,sessionProbe);					
			
			CTATTSMemory memoryProbe=new CTATTSMemory ();
			dataMemoryMap.put(converter,memoryProbe);	
		
			converter+=msper;// 5 min
			
		//	debug("the converter is currently at " + converter);
		
			counter++;
			
			if (counter>100)
			{
				counter=(long) 0;						
				total+=100;
				
				debug ("Generated "+total+" entries out of " + totalentries);						
		
			}	
		}
	}

	/**
	 * 
	 */
	private void usage ()
	{
		System.out.println("Usage: ");
		System.out.println(" java -cp \".;..;lib\\je-5.0.34.jar;TutorMonitor.jar;..\\java\\lib\\ctat.jar\\\" CTATBerkeleyDBDataGenerator <options>");
		System.out.println("");
		System.out.println("Example:");
		System.out.println(" java -cp \".;..;lib\\je-5.0.34.jar;TutorMonitor.jar;..\\java\\lib\\ctat.jar\\\" CTATBerkeleyDBDataGenerator -entries 100000 -databases 3 -delta 5");
		System.out.println("");
		System.out.println("Options:");
		System.out.println(" -entries <value>");
		System.out.println(" -databases <value>");
		System.out.println(" -delta <value>");
		System.out.println(" -startdate <value> (Please enter a date in the format: MM dd yyyy)");
	}
	/**
	 * 
	 */
	public void parseArgs (String [] args)
	{
		debug ("parseArgs ()");
				
		for (int i=0;i<args.length;i++)
		{
			if (args [i].equals("-help")==true)
			{
				usage ();
				System.exit(0);
			}
			
			if (args [i].equals("-entries")==true)
			{
				nrEntries=Long.parseLong(args [i+1]);
			}
			
			if (args [i].equals("-databases")==true)
			{
				nrDatabases=Long.parseLong(args [i+1]);
			}
			
			if (args [i].equals("-delta")==true)
			{
				nrTimeDelta=Long.parseLong(args [i+1]);
			}
			
			if (args [i].equals("-startdate")==true)
			{
				startDate=args [i+1];
			}
		}
	}
	
	
	private void generate(Long startTime){
		int i = 0;
		while (i<nrEntries){
			
			
			CTATTSSession sessionProbe=new CTATTSSession ();
			sessionMap.put(startTime,sessionProbe);					
			
			CTATTSMemory memoryProbe=new CTATTSMemory ();
			memoryMap.put(startTime,memoryProbe);
			
			
			//build Hour db's
			debug("Start Time is: " + startTime);
			if( startTime >= hourEnd){
				
				debug("the difference is: " + (hourStart-hourEnd));
				hourlyMemoryMap.put(hourStart,avgMemoryMap(memoryMap.subMap(hourStart, hourEnd)));
				hourlySessionMap.put(hourStart,avgSessionMap(sessionMap.subMap(hourStart, hourEnd)));
				hourStart=hourEnd;
				DateTime dtHourEnd = new DateTime(hourEnd);
				hourEnd = dtHourEnd.plusHours(1).toInstant().getMillis();
			}
			
			if( startTime >=dayEnd){
				dailyMemoryMap.put(dayStart,avgMemoryMap(hourlyMemoryMap.subMap(dayStart,dayEnd)));
				dailySessionMap.put(dayStart,avgSessionMap(hourlySessionMap.subMap(dayStart,dayEnd)));				hourlyMemoryMap.put(hourStart,avgMemoryMap(memoryMap.subMap(hourStart, hourEnd)));
				dayStart=dayEnd;
				DateTime dtDayEnd= new DateTime(dayEnd);
				dayEnd = dtDayEnd.plusDays(1).toInstant().getMillis();
				
			}
			
			if( startTime >=weekEnd){
				
				weeklyMemoryMap.put(weekStart,avgMemoryMap(weeklyMemoryMap.subMap(weekStart,weekEnd)));
				weeklySessionMap.put(weekStart,avgSessionMap(weeklySessionMap.subMap(weekStart,weekEnd)));
				weekStart=weekEnd;
				DateTime dtWeekEnd= new DateTime(weekEnd);
				weekEnd = dtWeekEnd.plusWeeks(1).toInstant().getMillis();
				
			}
			
			if( startTime >=monthEnd){
				monthlyMemoryMap.put(monthStart,avgMemoryMap(monthlyMemoryMap.subMap(monthStart, monthEnd)));
				monthlySessionMap.put(monthStart,avgSessionMap(monthlySessionMap.subMap(monthStart, monthEnd)));
				monthStart = monthEnd;
				DateTime dtMonthEnd= new DateTime(monthEnd);
				dayEnd = dtMonthEnd.plusMonths(1).toInstant().getMillis();
				
			}
			
			
			startTime = startTime + nrTimeDelta;
			
			i++;
			
			
		}
			
	}
	
	private CTATTSMemory avgMemoryMap (Map<Long, CTATTSMemory> aMap)
	{
		debug ("avgMemoryMap ("+aMap.entrySet().size()+")");
		
        Iterator<Map.Entry<Long, CTATTSMemory>> iter=aMap.entrySet().iterator();
        
        Long counter=0L;

        Long memUsed = 0L;
        Long memMax = 0L;
        Long memTotal = 0L;
        
        
        debug ("Transforming data ...");
        
        while ((iter.hasNext())) 
        {
            Map.Entry<Long, CTATTSMemory> entry = iter.next();
            
            if (entry!=null)
            {            
            memUsed = memUsed+ entry.getValue().getMemUsed();
            memMax = memMax + entry.getValue().getMemMax();
            memTotal = memTotal + entry.getValue().getMemTotal();
            	
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
        debug ("memoryMapToTable () Done");
        
     
       
        CTATTSMemory thisMemory = new CTATTSMemory(); 
       if(counter>0){
        	thisMemory.setMemUsed(memUsed/counter);
        	thisMemory.setMemMax(memMax/counter);
        	thisMemory.setMemTotal(memTotal/counter);
        }
   
        
       
        return thisMemory;
	}
	
	private CTATTSSession avgSessionMap (Map<Long, CTATTSSession> aMap)
	{
		debug ("avgMemoryMap ("+aMap.entrySet().size()+")");
		
        Iterator<Map.Entry<Long, CTATTSSession>> iter=aMap.entrySet().iterator();
        
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

        
        
        debug ("Transforming data ...");
        
        while ((iter.hasNext())) 
        {
            Map.Entry<Long, CTATTSSession> entry = iter.next();
            
            if (entry!=null)
            {            
            	totalTransactionMs=totalTransactionMs + entry.getValue().getTotalTransactionMs();
            	transactionCount= transactionCount + entry.getValue().getTransactionCount();
            	firstTransactionTime = firstTransactionTime + entry.getValue().getFirstTransactionTime();
            	longestTransactionMs= longestTransactionMs + entry.getValue().getLongestTransactionMs();
            	longestTransactionStartTime = longestTransactionStartTime + entry.getValue().getLongestTransactionStartTime();
            	diskLogEntries= diskLogEntries + entry.getValue().getDiskLogEntries();
            	forwardLogEntries = forwardLogEntries + entry.getValue().getForwardLogEntries();
            	diskLogErrors = diskLogErrors + entry.getValue().getDiskLogErrors();
            	forwardLogErrors= forwardLogErrors + entry.getValue().getForwardLogErrors();
            	
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
        debug ("memoryMapToTable () Done");
        
     
        CTATTSSession thisSession = new CTATTSSession();
        //thisSession.setInstanceName();
        thisSession.setActive(true);		
        thisSession.setTotalTransactionMs(totalTransactionMs);
        thisSession.setTransactionCount(transactionCount);
        thisSession.setFirstTransactionTime(firstTransactionTime);
        thisSession.setLongestTransactionMs(longestTransactionMs);
        thisSession.setLongestTransactionStartTime(longestTransactionStartTime);
        
        
        return thisSession;
	}
	
	/**
	 * 
	 */	
	public static void main(String[] args) 
	{
    	@SuppressWarnings("unused")
		CTATLink link=new CTATLink(new CTATDesktopFileManager ()); // Need at least one instance, might as well be the first object made
    	CTATLink.printDebugMessages=true;
    			
		CTATBerkeleyDBDataGenerator runner=new CTATBerkeleyDBDataGenerator();
		runner.parseArgs(args);
		runner.execute ();
	}
	
}
