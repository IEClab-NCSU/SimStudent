/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServiceRegistry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATServiceRegistry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.14  2012/09/06 17:33:18  akilbo
 Fixed an infinite loop that would appear in the remove entry method

 Revision 1.13  2012/08/13 20:58:32  akilbo
 Created new "START" status on creating new entries in the sRegistry to note the beginning of a logging entry.

 Revision 1.12  2012/04/18 19:20:17  vvelsen
 Bunch of fixes to the logic. The code should also be a tiny bit faster.

 Revision 1.11  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.10  2012/03/30 18:26:21  vvelsen
 A bunch of changes to the way the server checks and verifies if services are down. There is still a tweak that needs to be made to TS testing since it doesn't always accurately detect if a TS is down

 Revision 1.9  2012/03/27 17:13:30  vvelsen
 Added more safety features in case either the deamon or the monitor server goes down. Added redundency and failover mechanisms. Everything can now be configured either through command line arguments or a configuration file.

 Revision 1.8  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.7  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.

 Revision 1.6  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.5  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.4  2012/02/09 19:32:04  vvelsen
 Added some new tools and cleaned up the current code to the point that most of our testing and checking mechanisms are in place or at least stubbed.

 Revision 1.3  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.2  2012/02/07 21:36:24  vvelsen
 We now pretty much have xml representations finished of services and machine specs. These can be stored on disk as a configuration and send around over the network.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATServiceRegistry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServiceRegistry.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat.monitor;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class CTATServiceRegistry extends CTATXMLBase
{		
	private ArrayList <CTATClientEntry> entries=null;
	private ArrayList <CTATClientEntry> foundEntries=null;
	private Boolean shouldLog=true;
	
	// DB Support
	private CTATBerkeleyDB dbService=null;	
	
	/**
	*
	*/	
	public CTATServiceRegistry ()
	{  
    	setClassName ("CTATServiceRegistry");
    	debug ("CTATServiceRegistry ()");
    	
    	entries=new ArrayList<CTATClientEntry> ();
	}
	/**
	*
	*/	
	public CTATBerkeleyDB getDbService() 
	{
		return dbService;
	}
	/**
	*
	*/	
	public void setDbService(CTATBerkeleyDB dbService) 
	{
		this.dbService = dbService;
	}	
	/**
	*
	*/	
	public Boolean getShouldLog() 
	{
		return shouldLog;
	}
	/**
	*
	*/	
	public void setShouldLog(Boolean shouldLog) 
	{
		this.shouldLog = shouldLog;
	}	
	/**
	*
	*/
	public ArrayList <CTATClientEntry> getEntries() 
	{
		return entries;
	}
	/**
	*
	*/
	public void resetUpdates ()
	{
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry test=entries.get(i);
			test.setUpdated(false);
		}		
	}
	/**
	*
	*/
	public CTATTSEntry addTS ()
	{
		debug ("addTS ()");
		
		CTATTSEntry newEntry=new CTATTSEntry ();
		newEntry.setDbDriver(dbService);
		newEntry.setShouldLog(this.getShouldLog());
		
		entries.add(newEntry);
				
		return (newEntry);
	}
	/**
	*
	*/
	public CTATMachineEntry addMS ()
	{
		debug ("addMS ()");
		
		CTATMachineEntry newEntry=new CTATMachineEntry ();
		newEntry.setDbDriver(dbService);
		newEntry.setShouldLog(this.getShouldLog());
		
		entries.add(newEntry);
		
		return (newEntry);
	}	
	/**
	*
	*/
	public CTATWSEntry addWS ()
	{
		debug ("addWS ()");
		
		CTATWSEntry newEntry=new CTATWSEntry ();
		newEntry.setDbDriver(dbService);
		newEntry.setShouldLog(this.getShouldLog());
		
		entries.add(newEntry);
		
		return (newEntry);
	}
	/**
	 * 
	 */
	public CTATTSEntry findTS (int ID)
	{
		return ((CTATTSEntry) getEntry (ID));
	}
	/**
	 * 
	 */
	public CTATWSEntry findWS (int ID)
	{
		return ((CTATWSEntry) getEntry (ID));
	}
	/**
	 * 
	 */
	public CTATMachineEntry findMachine (int ID)
	{
		return ((CTATMachineEntry) getEntry (ID));
	}	
	/**
	*
	*/
	public CTATClientEntry addEntry (String type)
	{
		debug ("addEntry ("+type+")");
		
		CTATClientEntry newEntry=null;
		
		if (type.equals("WebService"))
		{
			newEntry=new CTATWSEntry ();
			newEntry.setShouldLog(this.getShouldLog());
		}
		
		if (type.equals("TutoringService"))
		{
			newEntry=new CTATTSEntry ();
			newEntry.setShouldLog(this.getShouldLog());
		}		
		
		newEntry.setDbDriver(dbService);
		
		entries.add(newEntry);
		
		return (newEntry);
	}
	/**
	*
	*/
	public CTATClientEntry getEntry (int ID)
	{
		debug ("getEntry ()");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry test=entries.get(i);
			if (test.getID()==ID)
				return (test);
		}
		
		return (null);
	}	
	/**
	*
	*/
	public CTATClientEntry getEntry (String uuid)
	{
		debug ("getEntry ()");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry test=entries.get(i);
			if (test.getGuid().equals(uuid)==true)
				return (test);
		}
		
		return (null);
	}
	/**
	*
	*/
	public Boolean removeEntry (String uuid)
	{
		debug ("removeEntry ("+uuid+")");
		
		CTATClientEntry test=getEntry (uuid);
		if (test==null)
		{
			debug ("Unable to find entry: " + uuid);
			return (false);
		}
		
		entries.remove(test);
		
		if (test.getClientType().equals ("machine")==true)
		{
			// Remove all services associated with this machine ...
						
			Boolean found=true;
			int entriesFound=0;
						
			while (found==true)
			{			
				debug ("while: " + found);
				
				if (entries.size()==0)
				{
					found=false;
				}
				
				for (int i=0;((i<entries.size()) && (found==true));i++)
				{
					debug ("for: " + found);
					
					found=false;
					
					CTATClientEntry target=entries.get(i);
					if (target.getMachineGuid().equals (test.getGuid())==true)
					{					
						found=removeEntry (target.getGuid());
						entriesFound++;
					}
				}
			}	
			
			debug ("Removed " + entriesFound + " service entries");			
		}
		
		
		
		return (true);
	}
	/**
	*
	*Note:
	*I changed the logic from revision 1.13 to a simple linear search. I was not convinced that the old algorithm would
	*properly remove IDs if they were not the first entries in the list. Since it is a somewhat unorganized list, I just created
	*a simple linear search to remove items from the list. -akilbo
	*
	*/
	public Boolean removeEntries (int ID)
	{
		debug ("removeEntries ("+ID+")");

		Boolean found=true;
		int entriesFound=0;
		
		if (entries.size()==0)
		{
			debug ("No entries in database");
			return (false);
		}
					
		
			for (int i=0;i<entries.size();i++)
			{
				
				debug ("for: " + found);
				debug ("i = " + i);
				
				CTATClientEntry test=entries.get(i);
				if (test.getID()==ID)
				{	
					
					debug(test.getClientType());
					
					found=removeEntry (test.getGuid());
					entriesFound++;
					i--;		
					
				}
			}
		
		debug ("Removed " + entriesFound + " service entries");
		
		return (true);
	}
	/**
	*
	*/
	public CTATClientEntry getMachine (String ID)
	{
		debug ("getMachine ("+ID+")");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			if (entry.getGuid().equals(ID)==true)
			{
				return (entry);
			}
		}
		
		return (null);
	}		
	/**
	*
	*/
	public CTATMachineEntry getFirstMachineEntry ()
	{
		debug ("getFirstMachineEntry ()");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			
			if (entry.getClientType().equals ("machine")==true)
			{
				return (CTATMachineEntry) (entry);
			}
		}
		
		return (null);
	}	
	/**
	*
	*/
	public CTATClientEntry getMachineEntry (int ID)
	{
		debug ("getMachineEntry ("+ID+")");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			if ((entry.getID()==ID) && (entry.getClientType().equals ("machine")==true))
			{
				return (entry);
			}
		}
		
		return (null);
	}	
	/**
	*
	*/
	public ArrayList <CTATClientEntry> getMachineServices (String aMachine)
	{
		debug ("getMachineServices (String)");
		
		foundEntries=new ArrayList<CTATClientEntry> ();
				
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			if (entry.getMachineGuid().equals(aMachine)==true)
			{
				foundEntries.add(entry);
			}
		}
		
		return (foundEntries);
	}	
	/**
	*
	*/
	public ArrayList <CTATClientEntry> getMachineServices (CTATClientEntry aMachine)
	{
		debug ("getMachineServices (CTATClientEntry)");
		
		foundEntries=new ArrayList<CTATClientEntry> ();
		
		if (aMachine.getClientType().equals("machine")==false)
		{
			debug ("The client entry does not represent a machine");
			return foundEntries;
		}
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			if (entry.getMachineGuid().equals(aMachine.getMachineGuid())==true)
			{
				foundEntries.add(entry);
			}
		}
		
		return (foundEntries);
	}
	/**
	*
	*/	
	public Boolean markMachineDown (int ID)
	{
		CTATClientEntry test=getMachineEntry (ID);
		
		if (test!=null)
		{	
			debug ("Marking machine as down ...");
			
			test.setStatus("DOWN");
			
			/*
			debug ("Mark services in machine as potentially down");
				
			markServicesDown (ID);
			*/
									
			return (true);
		}
		
		return (false);
	}
	/**
	*
	*/	
	public void markServicesDown (int ID)
	{
		debug ("markServicesDown ("+ID+")");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			if ((entry.getID()==ID) && (entry.getClientType().equals("machine")==false))
			{
				entry.setStatus("DOWN");
			}
		}		
	}
	/**
	*
	*/	
	public Boolean fromXMLList (CTATTutorMonitorServerThread aThread,
								Element root) 
	{
		debug ("fromXMLList ("+root.getNodeName()+")");
		
		int ID=-1;
		
		if (aThread!=null)
			ID=aThread.getID();
						
		if (root.getNodeName().equals("services")==true)
		{
			NodeList children=root.getChildNodes ();
		      						
			if (children==null)
			{
				debug ("Internal error: children list is null");
				return (false);
			}
			      
			if (children.getLength()>0)
			{ 
				ArrayList<CTATClientEntry> tempList=new ArrayList<CTATClientEntry> ();
				String machineGUID="undefined";
				
			    for (int i=0;i<children.getLength();i++) 
			    {
			    	Node node=children.item (i);
			    	
			    	//showNodeType (node);
			    	
			    	if (node.getNodeType ()==Node.ELEMENT_NODE)
			    	{
			    		debug ("Parsing: " + node.getNodeName());
			    		
			    		if (node.getNodeName().equals("tutoringservice")==true)
			    		{
			    			debug ("Creating tutoringservice entry ...");
			    						    			
			    			CTATTSEntry ts=new CTATTSEntry ();
			    			ts.setDbDriver(dbService);
			    			ts.setShouldLog(this.getShouldLog());
			    			ts.setID(ID);
			    			ts.fromXML((Element) node);
			    			ts.setUpdated(true);
			    			
			    			CTATClientEntry fixedTSEntry=getEntry (ts.getGuid());
			    			if (fixedTSEntry==null)
			    			{
			    				ts.setDbDriver(dbService);
			    				ts.startLogging();
			    				ts.setaThread(aThread);
				    			tempList.add(ts);	    				
				    			entries.add(ts);
				    			
				    			//Logs "START" as the very first thing to denote the beginning of a new log.
				    			
				    			ts.setStatus("START");
			    			}
			    			else
			    			{
			    				fixedTSEntry.fromXML((Element) node);
			    				fixedTSEntry.setUpdated(true);
			    			}
			    		}
			    		
			    		if (node.getNodeName().equals("webservice")==true)
			    		{
			    			debug ("Creating webservice entry ...");
			    			
			    			CTATWSEntry ws=new CTATWSEntry ();
			    			ws.setDbDriver(dbService);
			    			ws.setShouldLog(this.getShouldLog());
			    			ws.setID(ID);
			    			ws.fromXML((Element) node);
			    			ws.setUpdated(true);
			    			
			    			CTATClientEntry fixedWSEntry=getEntry (ws.getGuid());
			    			if (fixedWSEntry==null)
			    			{
			    				ws.setDbDriver(dbService);
			    				ws.startLogging();
			    				ws.setaThread(aThread);
				    			tempList.add(ws);			    				
				    			entries.add(ws);
				    			
				    			//Logs "START" as the very first thing to denote the beginning of a new log.
				    			
				    			ws.setStatus("START");
			    			}
			    			else
			    			{			    				
			    				fixedWSEntry.fromXML((Element) node);
			    				fixedWSEntry.setUpdated(true);
			    			}
			    		}
			    		
			    		if (node.getNodeName().equals("machine")==true)
			    		{
			    			debug ("Creating machine entry ...");
			    			
			    			CTATMachineEntry ms=new CTATMachineEntry ();
			    			ms.setDbDriver(dbService);
			    			ms.setShouldLog(this.getShouldLog());
			    			ms.setID(ID);
			    			ms.fromXML((Element) node);
			    			ms.setUpdated(true);
			    			machineGUID=ms.getMachineGuid();
			    			
			    			//tempList.add(ms);

			    			CTATClientEntry fixedMSEntry=getEntry (ms.getGuid());
			    			if (fixedMSEntry==null)
			    			{
			    				ms.setDbDriver(dbService);
			    				ms.startLogging();
			    				ms.setaThread(aThread);
				    			entries.add(ms);
				    			
				    			//Logs "START" as the very first thing to denote the beginning of a new log.
				    			
				    			ms.setStatus("START");
			    			}
			    			else
			    			{
			    				fixedMSEntry.fromXML((Element) node);
			    				fixedMSEntry.setUpdated(true);
			    			}
			    		}			    					    		
			    	} 
			    }          
			    
			    // Post process to check and make sure that every WS and TS has the proper machine GUID
			    
			    if (machineGUID.equals("undefined")==false)
			    {
			    	for (int j=0;j<tempList.size();j++)
			    	{
			    		CTATClientEntry tester=tempList.get(j);
			    		if (tester.getClientType().equals("machine")==false)
			    		{
			    			tester.setMachineGuid(machineGUID);
			    		}			    		
			    	}
			    }	
			}							
		}
		else		
		{
			debug ("Client type tag not found in node, instead got: " + root.getNodeName());
			return (false);
		}
		
		return (true);
	}	
	/**
	*
	*/	
	public String toXML() 
	{
		debug ("toXML ()");
		
		StringBuffer buffer=new StringBuffer ();

		buffer.append("<services update=\"all\">");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			buffer.append(entry.toXML());
		}
		
		buffer.append("</services>");
		
		return (buffer.toString());
	}
	/**
	*
	*/	
	public String toXMLUpdated() 
	{
		debug ("toXMLUpdated ()");
		
		StringBuffer buffer=new StringBuffer ();
		
		buffer.append("<services update=\"changed\">");		

		debug ("Serializing : " + entries.size() + " entries");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATClientEntry entry=entries.get(i);
			
			buffer.append(entry.toXMLUpdated());
		}
		
		buffer.append("</services>");		
		
		return (buffer.toString());
	}	
}
