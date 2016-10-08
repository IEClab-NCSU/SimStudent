/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSMemory.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTSMemory.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.2  2012/05/03 15:32:51  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 Revision 1.1  2012/04/27 18:19:51  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.3  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.2  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.1  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 $RCSfile: CTATTSMemory.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSMemory.java,v $ 
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
 * We present here a wrapper or container for one data point that measures
 * the Java VM memory stats. Please see the page at:
 * 
 * http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Runtime.html
 * 
 * Note: Important! Do not derive classes like these from our CTAT class:
 * import edu.cmu.hcii.ctat.CTATBase. Using such base classes will
 * create instances with a large memory footprint 
 */
public class CTATTSMemory extends CTATTSSerializable implements Serializable
{		
	private static final long serialVersionUID = -1L;
	
	public Long memUsed=(long) 0; 
	public Long memTotal=(long) 0;
	public Long memMax=(long) 0;
	
	/**
	*
	*/	
	public CTATTSMemory (Long aMemUsed,Long aMemTotal,Long aMemMax)
	{  
		memUsed=aMemUsed;
		memTotal=aMemTotal;
		memMax=aMemMax;
	}	
	/**
	*
	*/	
	public CTATTSMemory ()
	{  
		Runtime runtime = Runtime.getRuntime();
		
		memUsed= runtime.totalMemory ()-runtime.freeMemory();
		memTotal= runtime.totalMemory ();
		memMax= runtime.maxMemory();
	}
	/**
	*
	*/    
	public Long getMemUsed() 
	{
		return memUsed;
	}
	/**
	*
	*/	
	public void setMemUsed(Long memUsed) 
	{
		this.memUsed = memUsed;
	}
	/**
	*
	*/	
	public Long getMemTotal() 
	{
		return memTotal;
	}
	/**
	*
	*/	
	public void setMemTotal(Long memTotal) 
	{
		this.memTotal = memTotal;
	}
	/**
	*
	*/	
	public Long getMemMax() 
	{
		return memMax;
	}
	/**
	*
	*/	
	public void setMemMax(Long memMax) 
	{
		this.memMax = memMax;
	}
	/**
	*
	*/
	private void updateInternalData ()
	{
    	setKV ("memUsed",memUsed.toString());
    	setKV ("memTotal",memTotal.toString());
    	setKV ("memMax",memMax.toString());		
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
