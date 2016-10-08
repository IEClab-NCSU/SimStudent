package edu.cmu.hcii.ctat.monitor;


import java.util.Date;


/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATWSEntry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATWSEntry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.6  2012/08/15 21:10:13  akilbo
 Changed logging to be exclusively in setStatus( ) function. We now log the time from last major event and logging chars received is now integrated with status updates.

 Revision 1.5  2012/07/30 17:34:14  akilbo
 Fixed bug where the log would continously print the amount recieved, even if it were the same as the previous amount. Now only logs amount of characters recieved if it is a different amount.

 Revision 1.4  2012/07/30 17:03:26  akilbo
 Modified log entries to properly display a date, also date and entry information are seperated by a comma now instead of a tab

 Revision 1.3  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.

 Revision 1.2  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATWSEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATWSEntry.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

/**
 * 
 */
public class CTATWSEntry extends CTATClientEntry
{	
	private Integer lastAmountReceived=0;
	
	/**
	*
	*/	
	public CTATWSEntry ()
	{  
    	setClassName ("CTATWSEntry");
    	debug ("CTATWSEntry ()");
    	this.setClientType("webservice");
    	this.setPort(80);
    	this.setAccess("HTTP");    				
	}
	/**
	*
	*/	
	public Integer getLastAmountReceived() 
	{
		return lastAmountReceived;
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
			formatter.append(",");
			formatter.append(String.format("%d", (getLastAmountReceived())));
			
			setLastTimestamp(currentTimeStamp);
		
			if (logger!=null)
			{
				logger.addLine(formatter.toString ());
			}
		}		
	}	
	/**
	*
	*/	
	public void setLastAmountReceived(Integer lastAmountReceived) 
	{
		
		//checks to see if the amount received is different from the previous amount.
		if (!(this.lastAmountReceived.equals(lastAmountReceived)))
		{
			
			this.lastAmountReceived = lastAmountReceived;
			

		}	
	}	
	/**
	*
	*/
	public void startLogging ()
	{
		debug ("startLogging ()");
		
		if (this.getShouldLog()==true)	
			logger.setFileID("WS-"+this.getGuid()+"-"+this.getHostname());
		else
			debug ("Logging is disabled for this entry");		
	}	
}
