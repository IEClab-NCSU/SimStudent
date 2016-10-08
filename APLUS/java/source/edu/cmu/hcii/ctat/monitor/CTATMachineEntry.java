/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMachineEntry.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATMachineEntry.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.8  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.7  2012/03/27 17:13:30  vvelsen
 Added more safety features in case either the deamon or the monitor server goes down. Added redundency and failover mechanisms. Everything can now be configured either through command line arguments or a configuration file.

 Revision 1.6  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.5  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.4  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.3  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 Revision 1.2  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATMachineEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMachineEntry.java,v $ 
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
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class CTATMachineEntry extends CTATClientEntry
{	
	private ArrayList<CTATMachineProperty> properties=null;
	private Boolean autoPS=false;
	private Boolean proxy=false;	
	
	/**
	*
	*/	
	public CTATMachineEntry ()
	{  
    	setClassName ("CTATMachineEntry");
    	debug ("CTATMachineEntry ()");
    	this.setClientType("machine");
    	this.setPort(8080);
    	this.setAccess("STREAM");
    	this.setMachineGuid(this.getGuid ()); // Duh
    	
    	properties=new ArrayList<CTATMachineProperty> ();    	  	
	}
	/**
	*
	*/	
	public Boolean getProxy() 
	{
		return proxy;
	}
	/**
	*
	*/	
	public void setProxy(Boolean proxy) 
	{
		this.proxy = proxy;
	}	
	/**
	*
	*/
	public void startLogging ()
	{
		debug ("startLogging ()");
		
		if (this.getShouldLog()==true)	
			logger.setFileID("MS-"+this.getGuid()+"-"+this.getHostname());
		else
			debug ("Logging is disabled for this entry");		
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
		
			formatter.append (String.format("d",stamp.getTime()));
			formatter.append ("\t");
			formatter.append(status);
		
			if (logger!=null)
			{
				logger.addLine(formatter.toString ());
			}
		}		
	}	
	/**
	 * 
	 */
	public void debugProperties ()
	{
		debug ("debugProperties ()");
		
		for (int i=0;i<properties.size();i++)
		{
			CTATMachineProperty prop=properties.get(i);
			debug ("Key: " + prop.getKey());
		}		
	}
	/**
	 * 
	 */
	public String getProperty (String key)
	{
		//debug ("getProperty ("+key+")");
		
		for (int i=0;i<properties.size();i++)
		{
			CTATMachineProperty prop=properties.get(i);
			if (prop.getKey().equals(key)==true)
				return (prop.getValue());
		}		
		
		return ("null");
	}
	/**
	 * 
	 */
	public CTATMachineProperty findProperty (String key)
	{
		//debug ("findProperty ("+key+")");
		
		for (int i=0;i<properties.size();i++)
		{
			CTATMachineProperty prop=properties.get(i);
			if (prop.getKey().equals(key)==true)
			{
				//debug ("Property found");
				return (prop);
			}
		}		
		
		return (null);
	}	
	/**
	 * 
	 */
	public void addProperty (String key,String value)
	{
		//debug ("addProperty ()");
		
		setProperty (key,value);
	}
	/**
	 * 
	 */
	public CTATMachineProperty setProperty (String key,String value)
	{
		//debug ("setProperty ("+key+")");
		
		CTATMachineProperty newProperty=findProperty (key);
		if (newProperty==null)
		{
			//debug ("Property not found, creating new one ...");
			
			newProperty=new CTATMachineProperty ();
			newProperty.setKey(key);
			properties.add(newProperty);
		}
		
		newProperty.setValue(value);
		
		/*
		if (newProperty.getKey().equals("ps")==true)
				debug ("Property ["+newProperty.getKey()+"] set with: " + newProperty.getValue());
		*/
		
		return (newProperty);
	}	
	/**
	 * 
	 */	
	public ArrayList<CTATMachineProperty> getProperties() 
	{
		return properties;
	}
	/**
	*
	*/		
	public boolean isWindows() 
	{		
		String os = getProperty ("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}
	/**
	*
	*/	
	public boolean isMac() 
	{
		String os = getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}
	/**
	*
	*/	 
	public boolean isUnix() 
	{
		String os = getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
	/**
	*
	*/	 
	public boolean isSolaris() 
	{
		String os = getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}
	/**
	*
	*/	
	public String getLastTop() 
	{
		return getProperty ("ps");
	}
	/**
	*
	*/	
	public void setLastTop(String alastTop) 
	{
		setProperty ("ps",alastTop);
	}
	/**
	*
	*/	
	public Boolean getAutoPS() 
	{
		return autoPS;
	}
	/**
	*
	*/	
	public void setAutoPS(Boolean autoPS) 
	{
		this.autoPS=autoPS;
		
		CTATTutorMonitorServerThread aThread=getaThread();
		
		if (aThread!=null)
		{
			if (this.autoPS==true)
			{
				aThread.send ("<?xml version=\"1.0\" encoding=\"utf-8\"?><request command=\"autops\" value=\"true\" />");
			}
			else
			{
				aThread.send ("<?xml version=\"1.0\" encoding=\"utf-8\"?><request command=\"autops\" value=\"false\" />");
			}
		}	
	}			
	/**
	*
	*/	
	public Boolean fromXML(Element root) 
	{
		//debug ("fromXML ()");
									
		if (root.getNodeName().equals("machine")==true)
		{
			properties=new ArrayList<CTATMachineProperty> ();
			
			parseHostConfig (root);
			
			if ((root.getAttribute("proxy").toLowerCase().equals("true")) || (root.getAttribute("proxy").toLowerCase().equals("yes")))
			{
				debug ("This machine is configured as a proxy!");
				proxy=true;
			}
			
			NodeList children=root.getChildNodes ();
			      
			if (children==null)
			{
				debug ("Internal error: children list is null");
				return (false);
			}
			      
			if (children.getLength()>0)
			{    
			    for (int i=0;i<children.getLength();i++) 
			    {
			    	Node node=children.item (i);
			    	//showNodeType (node);
			    	if (node.getNodeType ()==Node.ELEMENT_NODE)
			    	{
			    		CTATMachineProperty prop=new CTATMachineProperty ();
			    		prop.fromXML((Element) node);

			    		setProperty (prop.getKey(),prop.getValue());
			    	} 
			    }                        
			}				
		}
		else		
		{
			debug ("Properties tag not found in node, instead got: " + root.getNodeName());
			return (false);
		}
		
		//debugProperties ();
		
		return (true);
	}	
	/**
	*
	*/	
	public String toXML() 
	{
		debug ("toXML ()");
				
		StringBuffer buffer=new StringBuffer ();
		
		buffer.append(toXMLOpen("proxy",proxy.toString()));
				
		for (int i=0;i<properties.size();i++)
		{
			CTATMachineProperty prop=properties.get(i);
			buffer.append(prop.toXML ());
		}
				
		buffer.append(toXMLClose());
		
		return (buffer.toString());
	}		
	/**
	*
	*/	
	public String toXMLUpdated() 
	{
		debug ("toXMLUpdated ()");
				
		StringBuffer buffer=new StringBuffer ();
				
		buffer.append(toXMLOpen("proxy",proxy.toString()));
		
		if (this.autoPS==true)
		{			
			CTATMachineProperty psProp=findProperty ("ps");
			if (psProp!=null)
				buffer.append(psProp.toXML ());
			else
				debug ("Internal error: no PS found");
		}
		
		buffer.append(toXMLClose());
		
		return (buffer.toString());
	}		
}
