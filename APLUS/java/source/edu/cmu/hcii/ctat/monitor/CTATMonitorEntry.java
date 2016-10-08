/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMonitorEntry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATMonitorEntry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.4  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.3  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.2  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.1  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 $RCSfile: CTATMonitorEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMonitorEntry.java,v $ 
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

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATMonitorEntry extends CTATBase
{		
	private int socketID=-1;
	private String monitorType="MONITOR"; // MONITOR or LOADTEST
	private CTATTutorMonitorServerThread aThread=null;
	private ArrayList <String> msRequestList=null;
	
	/**
	*
	*/	
	public CTATMonitorEntry ()
	{  
    	setClassName ("CTATMonitorEntry");
    	debug ("CTATMonitorEntry ()");
    	
    	msRequestList=new ArrayList<String> ();
	}
	/**
	*
	*/
	public Boolean getMachineMonitorRequest (String anID)
	{
		for (int i=0;i<msRequestList.size();i++)
		{
			String msID=msRequestList.get(i);
			if (msID.equals(anID)==true)
				return (true);
		}
		
		return (false);
	}
	/**
	*
	*/
	public void addMachineMonitorRequest (String anID)
	{
		if (getMachineMonitorRequest (anID)==false)
			msRequestList.add(anID);
	}
	/**
	*
	*/
	public void removeMachineMonitorRequest (String anID)
	{
		msRequestList.remove(anID);
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
	public int getSocketID() 
	{
		return socketID;
	}
	/**
	*
	*/
	public void setSocketID(int socketID) 
	{
		this.socketID = socketID;
	}
	/**
	*
	*/	
	public String getMonitorType() 
	{
		return monitorType;
	}
	/**
	*
	*/	
	public void setMonitorType(String monitorType) 
	{
		this.monitorType = monitorType;
	}
}
