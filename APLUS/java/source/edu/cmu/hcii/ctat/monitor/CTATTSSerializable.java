/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSSerializable.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTSSerializable.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/10/05 16:27:24  akilbo
 changed getrepeats() to getRepeats

 Revision 1.2  2012/10/04 15:33:17  akilbo
 Added a Repeats data field so the database information may be compressed

 Revision 1.1  2012/05/03 15:32:51  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 $RCSfile: CTATTSSerializable.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTSSerializable.java,v $ 
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * Important! Do not derive classes like these from our CTAT class:
 * import edu.cmu.hcii.ctat.CTATBase. Using such base classes will
 * create instances with a large memory footprint 
 */
public class CTATTSSerializable implements Serializable
{	
	private static final long serialVersionUID = -1L;
	
	private String instanceName="";	
	
	//because the logging system only logs new changes, we have to know when a value is repeated so when averaging values out we can create a correct average;
	private int repeats =0;
	
	private Boolean updated=false;
	private Boolean active=false;
	
	private HashMap <String,String> kvData=null;
			
	/**
	*
	*/	
	public CTATTSSerializable ()
	{  
		kvData=new HashMap <String,String> ();
	}	
	/**
	 *
	 */
	protected void debug (String aMessage)
	{
		CTATBase.debug ("CTATTSSerializable",aMessage);	
	}
	/**
	 *
	 */	
	public void setKV (String aKey,String aValue)
	{
		kvData.put(aKey, aValue);
	}
	/**
	 *
	 */	
	public void addKV (String aKey,String aValue)
	{
		kvData.put(aKey, aValue);
	}
	/**
	 *
	 */
	public String getKV (String aKey)
	{
		return (kvData.get(aKey));
	}
	/**
	 *
	 */	
	public void setInstanceName(String instanceName) 
	{
		this.instanceName = instanceName;
	}
	/**
	 *
	 */	
	public String getInstanceName() 
	{
		return instanceName;
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
	public Boolean getActive() 
	{
		return active;
	}
	/**
	*
	*/
	
	public void setActive(Boolean active) 
	{
		this.active = active;
	}	
	/**
	*
	*/
	
	//accessor methods for the repeat of certain entries;
	public void addrepeats(int repeats){
		this.repeats = repeats + this.repeats;
	}
	
	public int getRepeats(){
		return repeats;
	}
	
    public String toString() 
    {       	
    	StringBuffer formatted=new StringBuffer ();
    	
    	formatted.append("[CTATTSSerializable: ");
    	    	
        Iterator<Entry<String, String>> it = kvData.entrySet().iterator();
        
        while (it.hasNext()) 
        {
            Map.Entry<String, String> pairs = (Map.Entry <String, String>)it.next();
            
            formatted.append(pairs.getKey() + "=" + pairs.getValue());
            formatted.append("");            
        }
    
        formatted.append(" ]");
        
        return (formatted.toString ());
    }
	/**
	*
	*/	
    public String toCSVHeader() 
    {
    	StringBuffer formatted=new StringBuffer ();
    	
    	Collection<String> c = kvData.keySet();
    	
        Iterator<String> itr = c.iterator();
       
        while(itr.hasNext())
        {               	
        	formatted.append(",");
        	formatted.append(itr.next());
        }
    
    	return (formatted.toString ());
    }
	/**
	*
	*/	
    public String toCSV() 
    {   
    	StringBuffer formatted=new StringBuffer ();
    	
    	Collection<String> c = kvData.values();
    	
        Iterator<String> itr = c.iterator();
       
        while(itr.hasNext())
        {               	
        	formatted.append(",");
        	formatted.append(itr.next());
        }
    
    	return (formatted.toString ());
    }    	
}
