/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServerConfig.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATServerConfig.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.1  2012/03/27 17:13:30  vvelsen
 Added more safety features in case either the deamon or the monitor server goes down. Added redundency and failover mechanisms. Everything can now be configured either through command line arguments or a configuration file.

 $RCSfile: CTATServerConfig.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServerConfig.java,v $ 
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
public class CTATServerConfig extends CTATXMLBase
{
	private ArrayList <String> smtpContacts=null;
	private ArrayList <String> emailContacts=null;
	
	/**
	*
	*/	
	public CTATServerConfig ()
	{  
    	setClassName ("CTATServerConfig");
    	debug ("CTATServerConfig ()");
    	
    	smtpContacts=new ArrayList<String> ();    	
    	emailContacts=new ArrayList<String> ();
	}
	/**
	*
	*/
	public ArrayList <String> getSMTPContacts ()
	{
		return (smtpContacts);
	}
	/**
	*
	*/
	public ArrayList <String> getEmailContacts ()
	{
		return (emailContacts);
	}
	/**
	*
	*/	
	public Boolean fromXML(Element root) 
	{
		debug ("fromXML ()");
		
		if (root.getNodeName().equals("config")==true)
		{
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
			    				    	
			    	if (node.getNodeType ()==Node.ELEMENT_NODE)
			    	{				
			    		debug ("Parsing: " + node.getNodeName());
			    		
			    		if (node.getNodeName().equals("contactServers")==true)
			    		{		
			    			NodeList servers=node.getChildNodes ();
			    			
			    			for (int j=0;j<servers.getLength();j++)
			    			{
						    	Node aServerNode=servers.item (j);
						    	
							    if (aServerNode.getNodeType ()==Node.ELEMENT_NODE)
							    {
							    	debug ("Parsing: " + aServerNode.getNodeName());
							    	
							    	Element aServer=(Element) aServerNode;
							    	
							    	smtpContacts.add(aServer.getAttribute("url"));
						    	}	
			    			}			    			
			    		}
			    					    		
			    		if (node.getNodeName().equals("contacts")==true)
			    		{		
			    			NodeList emails=node.getChildNodes ();
			    			
			    			for (int j=0;j<emails.getLength();j++)
			    			{
						    	Node email=emails.item (j);
						    	
							    if (email.getNodeType ()==Node.ELEMENT_NODE)
							    {
							    	debug ("Parsing: " + email.getNodeName());
							    	
							    	Element anEmail=(Element) email;
							    	
							    	emailContacts.add(anEmail.getAttribute("email"));
						    	}	
			    			}			    						    			
			    		}
			    	}
			    }
			}
		}	
		
		return (true);
	}	
}	