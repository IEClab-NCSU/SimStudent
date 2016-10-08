/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMachineProperty.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATMachineProperty.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.2  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATMachineProperty.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMachineProperty.java,v $ 
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

import org.w3c.dom.Element;

/**
 * 
 */
public class CTATMachineProperty extends CTATXMLBase
{		
	private String key=null;
	private String value=null;
	
	/**
	*
	*/	
	public CTATMachineProperty ()
	{  
    	setClassName ("CTATMachineProperty");
    	//debug ("CTATMachineProperty ()");
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
	public Boolean fromXML(Element root) 
	{
		//debug ("fromXML ()");
		
		if (root.getNodeName().equals("property")==true)
		{
			this.setKey(root.getAttribute("key"));
			
			//this.setValue(root.getAttribute("value"));
			this.setValue(root.getTextContent());
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
	public String toXML() 
	{
		//debug ("toXML ()");
		
		StringBuffer buffer=new StringBuffer ();
						
		buffer.append("<property key=\""+getKey()+"\"><![CDATA[");
		buffer.append(getValue());
		buffer.append("]]></property>");
				
		return (buffer.toString());
	}
}
