/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSSession.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTSSession.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.6  2012/09/26 00:24:14  akilbo
 Changed the Storage of many of the variables from Strings to Longs

 Revision 1.5  2012/05/03 15:32:51  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 Revision 1.4  2012/04/27 18:19:51  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.3  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.2  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.1  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 $RCSfile: CTATTSSession.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSSession.java,v $ 
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

import java.io.Serializable;

/**
 * Important! Do not derive classes like these from our CTAT class:
 * import edu.cmu.hcii.ctat.CTATBase. Using such base classes will
 * create instances with a large memory footprint 
 */
public class CTATTSSession extends CTATTSSerializable implements Serializable
{	
	private static final long serialVersionUID = -1L;
		
	public String lastMessage="";
	public String result="";

	public Long totalTransactionMs=0L;
	public Long transactionCount=0L;
	public Long firstTransactionTime=0L;
	public Long longestTransactionMs=0L;
	public Long longestTransactionStartTime=0L;
	public Long diskLogEntries=0L; 
	public Long forwardLogEntries=0L; 
	public Long diskLogErrors=0L;
	public Long forwardLogErrors=0L;
		
	/**
	*
	*/	
	public CTATTSSession ()
	{  
    	
	}	
	/**
	*
	*/
	public String getLastMessage() 
	{
		return lastMessage;
	}
	/**
	*
	*/
	public void setLastMessage(String lastMessage) 
	{
		this.lastMessage=lastMessage;
	}	
	/**
	*
	*/
	public String getResult() 
	{
		return result;
	}
	/**
	*
	*/
	public void setResult(String result) 
	{
		this.result = result;
	}	
	/**
	*
	*/	
	public Long getTotalTransactionMs() 
	{
		return totalTransactionMs;
	}
	/**
	*
	*/	
	public void setTotalTransactionMs(Long totalTransactionMs) 
	{
		if (this.totalTransactionMs.equals(totalTransactionMs)==false)
			setUpdated (true);
		
		this.totalTransactionMs = totalTransactionMs;
	}
	/**
	*
	*/	
	public Long getTransactionCount() 
	{
		return transactionCount;
	}
	/**
	*
	*/	
	public void setTransactionCount(Long transactionCount) 
	{
		if (this.transactionCount.equals(transactionCount)==false)
			setUpdated (true);
		
		this.transactionCount = transactionCount;
	}
	/**
	*
	*/	
	public Long getFirstTransactionTime() 
	{
		return firstTransactionTime;
	}
	/**
	*
	*/	
	public void setFirstTransactionTime(Long firstTransactionTime) 
	{
		if (this.firstTransactionTime.equals(firstTransactionTime)==false)
			setUpdated (true);
		
		this.firstTransactionTime = firstTransactionTime;
	}
	/**
	*
	*/	
	public Long getLongestTransactionMs() 
	{
		return longestTransactionMs;
	}
	/**
	*
	*/	
	public void setLongestTransactionMs(Long longestTransactionMs) 
	{
		if (this.longestTransactionMs.equals(longestTransactionMs)==false)
			setUpdated (true);
		
		this.longestTransactionMs = longestTransactionMs;
	}
	/**
	*
	*/	
	public Long getLongestTransactionStartTime() 
	{
		return longestTransactionStartTime;
	}
	/**
	*
	*/	
	public void setLongestTransactionStartTime(Long longestTransactionStartTime) 
	{
		if (this.longestTransactionStartTime.equals(longestTransactionStartTime)==false)
			setUpdated (true);
		
		this.longestTransactionStartTime = longestTransactionStartTime;
	}
	/**
	*
	*/		
	public Long getDiskLogEntries() 
	{
		return diskLogEntries;
	}
	/**
	*
	*/		
	public void setDiskLogEntries(Long diskLogEntries) 
	{
		if (this.diskLogEntries.equals(diskLogEntries)==false)
			setUpdated (true);		
		
		this.diskLogEntries = diskLogEntries;
	}
	/**
	*
	*/		
	public Long getForwardLogEntries() 
	{
		return forwardLogEntries;
	}
	/**
	*
	*/		
	public void setForwardLogEntries(Long forwardLogEntries) 
	{
		if (this.forwardLogEntries.equals(forwardLogEntries)==false)
			setUpdated (true);		
		
		this.forwardLogEntries = forwardLogEntries;
	}
	/**
	*
	*/		
	public Long getDiskLogErrors() 
	{
		return diskLogErrors;
	}
	/**
	*
	*/		
	public void setDiskLogErrors(Long diskLogErrors) 
	{
		if (this.diskLogErrors.equals(diskLogErrors)==false)
			setUpdated (true);	
		
		this.diskLogErrors = diskLogErrors;
	}
	/**
	*
	*/		
	public Long getForwardLogErrors() 
	{
		return forwardLogErrors;
	}
	/**
	*
	*/		
	public void setForwardLogErrors(Long forwardLogErrors) 
	{
		if (this.forwardLogErrors.equals(forwardLogErrors)==false)
			setUpdated (true);
		
		this.forwardLogErrors = forwardLogErrors;
	}
	/**
	*
	*/
	private void updateInternalData ()
	{    	
    	setKV ("totalTransactionMs",totalTransactionMs.toString());
    	setKV ("transactionCount",transactionCount.toString()); 
    	setKV ("firstTransactionTime",firstTransactionTime.toString()); 
    	setKV ("longestTransactionMs",longestTransactionMs.toString()); 
    	setKV ("longestTransactionStartTime",longestTransactionStartTime.toString()); 
    	setKV ("diskLogEntries",diskLogEntries.toString()); 
    	setKV ("forwardLogEntries",forwardLogEntries.toString()); 
    	setKV ("diskLogErrors",diskLogErrors.toString()); 
    	setKV ("forwardLogErrors",forwardLogErrors.toString());
	}	
	/**
	*
	*/	
    public String toString() 
    {       	
    	updateInternalData ();
    	
    	return (super.toString());
    }
	/**
	*
	*/	
    public String toCSVHeader() 
    {
    	updateInternalData ();
    	
    	return (super.toCSVHeader());
    }
	/**
	*
	*/	
    public String toCSV() 
    {       	
    	updateInternalData ();
    	
    	return (super.toCSV());
    }    
}
