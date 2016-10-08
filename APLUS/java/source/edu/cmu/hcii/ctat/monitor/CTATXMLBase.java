/**
 $Author: vvelsen $ 

 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATXMLBase.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATXMLBase.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.2  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATXMLBase.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATXMLBase.java,v $ 
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATXMLBase extends CTATBase
{			
	/**
	*
	*/	
	public CTATXMLBase ()
	{  
    	setClassName ("CTATXMLBase");
    	//debug ("CTATXMLBase ()");
	}
	/**
	 * 
	 */
	public void debugSAXException (SAXParseException spe)
	{
		StringBuffer sb = new StringBuffer( spe.toString() );		
		sb.append("\n Line number: " + spe.getLineNumber());
		sb.append("\n Column number: " + spe.getColumnNumber() );
		sb.append("\n Public ID: " + spe.getPublicId() );
		sb.append("\n System ID: " + spe.getSystemId() + "\n");
		
		debug (sb.toString ());		
	}
	/**
	*
	*/	
    public Document loadXMLFromString (String xml)
    {
    	debug ("loadXMLFromString ()");
    	
    	xml=xml.trim();
    	
    	if (xml.indexOf ("<?xml")==-1)
    	{
    		xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+xml;
    	}
    	
    	//debug ("Parsing: " +xml);
    	
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        
		try 
		{
			builder = factory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);			
		}
		
        InputSource is = new InputSource(new StringReader(xml));
        
        Document result=null;
        
		try 
		{
			result = builder.parse(is);
		} 
		catch (SAXParseException spe) 
		{
			// TODO Auto-generated catch block
			debugSAXException (spe);
			return (null);
		}		
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
        
        return result;
    }			
	/**
	*
	*/	
	public Boolean fromXML(Element root) 
	{
		debug ("fromXML ()");
		
		// Do something in child classes
		
		return (true);
	}
	/**
	*
	*/	
	public String toXML() 
	{
		debug ("toXML ()");
		
		StringBuffer buffer=new StringBuffer ();
		buffer.append("<xml class=\""+this.getClassName()+"\" instance=\""+this.getInstanceName()+"\" />");
				
		return (buffer.toString());
	}
}
