/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATClientEntry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATClientEntry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.17  2012/08/15 21:15:22  akilbo
 Added "lastTimestamp" variable to help keep track of time between events in logging

 Revision 1.16  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.15  2012/04/18 19:20:17  vvelsen
 Bunch of fixes to the logic. The code should also be a tiny bit faster.

 Revision 1.14  2012/04/06 17:53:41  vvelsen
 Fixed a bug in the TS where it wouldn't track inactive sessions properly

 Revision 1.13  2012/03/29 18:58:15  vvelsen
 Lots of bug fixes in the data representation and serialization of services.

 Revision 1.12  2012/03/27 17:13:30  vvelsen
 Added more safety features in case either the deamon or the monitor server goes down. Added redundency and failover mechanisms. Everything can now be configured either through command line arguments or a configuration file.

 Revision 1.11  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.10  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.

 Revision 1.9  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.8  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.7  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.6  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 Revision 1.5  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.4  2012/02/09 19:32:04  vvelsen
 Added some new tools and cleaned up the current code to the point that most of our testing and checking mechanisms are in place or at least stubbed.

 Revision 1.3  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.2  2012/02/07 21:36:24  vvelsen
 We now pretty much have xml representations finished of services and machine specs. These can be stored on disk as a configuration and send around over the network.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATClientEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATClientEntry.java,v $ 
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
import java.util.UUID;

import org.w3c.dom.Element;

/**
 * 
 */
public class CTATClientEntry extends CTATXMLBase
{		
	private int socketID=-1;
	private int port=-1;	
	private String guid="undefined";
	private String machineGuid="undefined";
	private String clientType="undefined";
	private String address="127.0.0.1";
	private String access="NONE";
	private String status="START";
	private String report="";	
	private Boolean updated=false;
	private Boolean shouldLog=true;
	private Long lastPing=(long) 0;
	private Integer updateDelta=1;
	private Integer lastTimestamp=0 ;

	private CTATTutorMonitorServerThread aThread=null;
	protected CTATStreamedTableDiskLogger logger=null;
	private CTATBerkeleyDB dbDriver=null;
	
	/**
	*
	*/	
	public CTATClientEntry ()
	{  
    	setClassName ("CTATClientEntry");
    	debug ("CTATClientEntry ()");    	
    	
    	// If we don't have one, we will now
    	guid=UUID.randomUUID().toString();
    	
    	// If we don't have one, we will now
    	getHostname();
    	
		logger=new CTATStreamedTableDiskLogger ();
		report=new String ();
	}
	/**
	*
	*/
	public Integer getUpdateDelta()
	{
		return updateDelta;
	}
	/**
	*
	*/
	public void setUpdateDelta(Integer updateDelta)
	{
		this.updateDelta = updateDelta;
	}	
	/**
	*
	*/
	public Long getLastPing()
	{
		return lastPing;
	}
	/**
	*
	*/
	public void setLastPing(Long lastPing) 
	{
		this.lastPing = lastPing;
	}	
	/**
	*
	*/	
	public CTATBerkeleyDB getDbDriver() 
	{
		return dbDriver;
	}
	/**
	*
	*/	
	public void setDbDriver(CTATBerkeleyDB dbDriver) 
	{
		this.dbDriver = dbDriver;
	}	
	/**
	 * 
	 */
	public CTATStreamedTableDiskLogger getLogger ()
	{
		return (logger);
	}
	/**
	*
	*/	
	public CTATTutorMonitorServerThread getaThread() 
	{
		return aThread;
	}
	/**
	*
	*/	
	public void setaThread(CTATTutorMonitorServerThread aThread) 
	{
		this.aThread = aThread;
	}
	/**
	*
	*/
	public Boolean checkService ()
	{
		debug ("checkService ()");
		
		// Implement in client!!!
		
		return (false);
	}
	/**
	*
	*/
	public void resetReport ()
	{
		report="";
	}
	/**
	*
	*/
	public void appendReport (String aMessage)
	{
		report=aMessage;
	}
	/**
	*
	*/
	public String getReport ()
	{
		return (report);
	}
	/**
	*
	*/
	public void startLogging ()
	{
		debug ("startLogging ()");
		
		// Implement in client!!!
	}	
	/**
	*
	*/	
	public String getStatus() 
	{
		return status;
	}
	/**
	*
	*/	
	public void setStatus(String status) 
	{	
		debug ("setStatus ("+status+")");
		
		if (status.isEmpty()==true)
		{
			debug ("Error: status argument is empty");
			return;
		}
		
		String oldStatus=this.status;
				
		this.status=status;
		
		// We should make sure we've set our internal status first
		// before marking the service as changed since the checker
		// might be faster than our code here
		
		if (oldStatus.equals (status)==false)
		{
			// Something changed, better be safe than sorry
			this.setUpdated(true);
		}
		
	}	
	/**
	*
	*/	
	public Boolean getUpdated() 
	{
		return updated;
	}
	/**
	*
	*/	
	public void setUpdated(Boolean updated) 
	{
		this.updated = updated;
	}	
	/**
	*
	*/	
	public String getMachineGuid() 
	{
		return machineGuid;
	}
	/**
	*
	*/	
	public void setMachineGuid(String aMachineGuid) 
	{
		if (aMachineGuid==null)
			return;
		
		if (aMachineGuid.isEmpty()==true)
			return;
		
		if (aMachineGuid.equals("")==true)
			return;
		
		this.machineGuid = aMachineGuid;
	}	
	/**
	*
	*/	
	public String getAccess() 
	{
		return access;
	}
	/**
	*
	*/	
	public void setAccess(String access) 
	{
		this.access = access;
	}	
	/**
	*
	*/	
	public int getID() 
	{
		return socketID;
	}
	/**
	*
	*/	
	public void setID(int iD) 
	{
		socketID = iD;
	}
	/**
	*
	*/	
	public String getGuid() 
	{
		return guid;
	}
	/**
	*
	*/	
	public void setGuid(String uuid) 
	{
		if ((uuid.equals("-1")==true) || (uuid.isEmpty()==true))
			return;
		
		guid=uuid;
	}	
	/**
	*
	*/	
	public String getClientType() 
	{
		return clientType;
	}
	/**
	*
	*/	
	public void setClientType(String clientType) 
	{
		this.clientType = clientType;
	}
	/**
	*
	*/	
	public int getPort() 
	{
		return port;
	}
	/**
	*
	*/	
	public void setPort(int port) 
	{
		this.port = port;
	}
	/**
	 * 
	 */
	public String getHostname ()
	{
		debug ("getHostname ("+this.address+")");
		
		CTATNetworkTools ntools=new CTATNetworkTools ();
		
		String better=ntools.getHostname();
		
		if ((this.address.equals("127.0.0.1")==true) || (this.address.equals("localhost")==true))
		{
			debug ("Assigning better hostname: " + better);
			this.address=better;
		}
		
		return (this.address);
	}	
	/**
	*
	*/	
	public void setHostname (String aHostname) 
	{
		debug ("setHostname ("+aHostname+")");
		
		if (aHostname==null)
		{
			debug ("Null argument provided");
			return;
		}
		
		if ((aHostname.equals("")==true) || (aHostname.isEmpty()==true))		
		{
			debug ("No valid hostname provided as argument");
			return;
		}
		
		if ((aHostname.equals("127.0.0.1")==false) && (aHostname.toLowerCase().equals("localhost")==false))
		{
			debug ("Valid hostname provided, assigning: " + aHostname);
			this.address=aHostname;
			return;
		}
		
		debug ("Error unable to verify provided hostname to be valid, not assinging");
	}	
	/**
	*
	*/	
	public Boolean parseHostConfig (Element root) 
	{
		debug ("parseHostConfig ()");
				
		this.setGuid(root.getAttribute("ID"));		
		setHostname (root.getAttribute("host"));
		setPort (Integer.parseInt(root.getAttribute("port")));
		setAccess (root.getAttribute("access"));
		setStatus (root.getAttribute("status"));
		setMachineGuid(root.getAttribute("machineGUID"));
		
		return (true);
	}
	/**
	*
	*/	
	public Boolean fromXML (Element root) 
	{
		debug ("fromXML ()");
						
		if (root.getNodeName().equals(getClientType())==true)
		{
			parseHostConfig (root);				
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
	public String toXMLOpen() 
	{
		return("<"+clientType+" ID=\""+getGuid()+"\" host=\""+address+"\" port=\""+getPort()+"\" access=\""+getAccess ()+"\" status=\""+getStatus ()+"\" machineGUID=\""+getMachineGuid()+"\" delta=\""+updateDelta.toString()+"\" >");		
	}
	/**
	*
	*/	
	public String toXMLOpen(String attrName,String attrValue) 
	{
		return("<"+clientType+" ID=\""+getGuid()+"\" host=\""+address+"\" port=\""+getPort()+"\" access=\""+getAccess ()+"\" status=\""+getStatus ()+"\" machineGUID=\""+getMachineGuid()+"\" delta=\""+updateDelta.toString()+"\" "+attrName+"=\""+attrValue+"\" >");		
	}	
	/**
	*
	*/	
	public String toXMLClose() 
	{
		return("</"+clientType+">");		
	}	
	/**
	*
	*/	
	public String toXML() 
	{
		debug ("toXML ()");
		
		StringBuffer buffer=new StringBuffer ();
		buffer.append(toXMLOpen());
		buffer.append(toXMLClose());
		
		return (buffer.toString());
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
	public String toXMLUpdated() 
	{
		debug ("toXMLUpdated ()");
		
		StringBuffer buffer=new StringBuffer ();
		buffer.append(toXMLOpen());
		buffer.append(toXMLClose());
		
		return (buffer.toString());
	}
	/**
	*
	*/
	public void cleanup ()
	{
		debug ("cleanup ()");
		
		// Implement in child class
	}
	
	public Integer getLastTimestamp(){
		
		return lastTimestamp;
		
	}
	
	public void setLastTimestamp(Integer timeStamp){
		
		this.lastTimestamp= timeStamp;
		
	}
}
