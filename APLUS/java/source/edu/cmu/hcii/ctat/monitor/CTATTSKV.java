/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSKV.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTSKV.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.1  2012/05/03 15:32:51  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 $RCSfile: CTATTSKV.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSKV.java,v $ 
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

import edu.cmu.hcii.ctat.CTATBase;

/**
 * Important! Do not derive classes like these from our CTAT class:
 * import edu.cmu.hcii.ctat.CTATBase. Using such base classes will
 * create instances with a large memory footprint 
 */
public class CTATTSKV implements Serializable
{	
	private static final long serialVersionUID = -1L;
		
	private String key="";
	private String value="";
	
	/**
	*
	*/	
	public CTATTSKV ()
	{  
    	
	}	
	/**
	 *
	 */
	protected void debug (String aMessage)
	{
		CTATBase.debug ("CTATTSKV",aMessage);	
	}
	/**
	*
	*/
	public String getKey() 
	{
		return key;
	}
	/**
	*
	*/
	public void setKey(String key) 
	{
		this.key = key;
	}
	/**
	*
	*/
	public String getValue() 
	{
		return value;
	}
	/**
	*
	*/
	public void setValue(String value) 
	{
		this.value = value;
	}	
	/**
	*
	*/	
    public String toString() 
    {       	
    	return "[CTATTSKV: "+key+"="+value+"]";
    }
	/**
	*
	*/	
    public String toCSVHeader() 
    {
    	return (key);
    }
	/**
	*
	*/	
    public String toCSV() 
    {       	
    	return (value);
    }    	
}
