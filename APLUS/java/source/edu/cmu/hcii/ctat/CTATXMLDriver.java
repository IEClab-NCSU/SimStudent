/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATXMLDriver.java,v 1.2 2012/09/18 15:21:55 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATXMLDriver.java,v $
 Revision 1.2  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 Revision 1.1  2012/09/14 13:58:29  vvelsen
 Forgot to add two source files

 $RCSfile: CTATXMLDriver.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATXMLDriver.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 
 */
public class CTATXMLDriver extends CTATBase
{			
	private String xmlid ="hoopxml";
	private Document doc = null;
	
	/**
	*
	*/	
	public CTATXMLDriver ()
	{  
    	setClassName ("CTATXMLDriver");
    	//debug ("CTATXMLDriver ()");
	}
	/**
	 * 
	 */	
	public String getXMLID() 
	{
		return xmlid;
	}
	/**
	 * 
	 */	
	public void setXMLID(String anID) 
	{
		this.xmlid = anID;
	}		
	/**
	 * 
	 */	
	public Document getDoc() 
	{
		return doc;
	}
	/**
	 * 
	 */	
	public void setDoc(Document doc) 
	{
		this.doc = doc;
	}	
	/**
	*
	*/	
    public Element loadXMLFromString (String xml)
    {
    	debug ("loadXMLFromString ()");
    	
    	//debug (xml);
    	
    	xml=xml.trim();
    	
    	if (xml.indexOf ("<?xml")==-1)
    	{
    		xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+xml;
    	}
    	
    	SAXBuilder builder = new SAXBuilder();
    	Reader in = new StringReader(xml);
    	Element root = null;
  
    	try
    	{
    		doc = builder.build(in);
    		root = doc.getRootElement();

    	} 
    	catch (JDOMException e)
    	{
    		// do what you want
    		return (null);
    	} 
    	catch (IOException e)
    	{
    		// do what yo want
    		return (null);
    	} 
    	catch (Exception e)
    	{
    		// do what you want
    		return (null);
    	}
    	
    	return (root);
    }		
	/**
	*	
	*/	    
    protected String getAttributeValue (Element aNode,String anAttribute)
    {    	    	
    	return (aNode.getAttributeValue(anAttribute));
    }
}
