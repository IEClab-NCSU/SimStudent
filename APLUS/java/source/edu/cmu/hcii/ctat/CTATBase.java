/**
 -
 License:
 -
 ChangeLog:
 $Log: CTATBase.java,v $
 Revision 1.15  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.14  2012/08/30 15:25:33  sewall
 Fix-ups after Alvaro's 2012/08/17 merge.

 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UnknownFormatConversionException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Node;

import edu.cmu.pact.Utilities.trace;

public class CTATBase
{
	/** The output stream for {@link #debug(String)}. Default value {@link System#out}. */
	public static PrintStream outStream = System.out;

	/** The error output stream. Default value {@link System#err}. */
	public static PrintStream errStream = System.err;

	private static final SimpleDateFormat df = new SimpleDateFormat ("HH:mm:ss.SSS");
	private String className=getClass().getSimpleName();
	
	private String classType=getClass().getSimpleName();
	private String instanceName="";	
	private String name="";

	/**
	 *
	 */
    public CTATBase () 
    {
    	setClassName ("CTATBase");
    	//debug ("CTATBase ()");       	
    }
	/**
	 * 
	 */	
	protected String removeGarbage (String in)
	{
		return (in.replace("\\n",""));
	}
	/**
	 * 
	 */	
	public byte[] inputStreamToByteArray(InputStream inStream)
	{
	    InputStreamReader in = new InputStreamReader(inStream);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    int next=0;
	    
		try 
		{
			next = inStream.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    while (next > -1)
	    {
	        baos.write(next);
	        try {
				next = in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    byte[] result = baos.toByteArray();
	    try {
			baos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return result;
	} 	
	/**
	 *
	 */
    public void setName (String aName)
    {
    	name=aName;
    }
	/**
	 *
	 */
    public String getName ()
    {
    	return (name);
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
    public void setClassName (String aName)
    {
    	className=aName;
    }
	/**
	 *
	 */
    public String getClassName ()
    {
    	return (className);
    }
	/**
	 *
	 */
    public void setClassType (String aName)
    {
    	classType=aName;
    }
	/**
	 *
	 */
    public String getClassType ()
    {
    	return (classType);
    }    
	/**
	 *
	 */    
    public String getCurrentDate ()
    {
    	return (df.format(new Date()));
    }
	/**
	*	
	*/		
	protected String getClassOpen ()
	{
		return ("<"+getClassType ()+">");
	}
	/**
	*	
	*/		
	protected String getClassOpen (String aName)
	{
		return ("<"+getClassType ()+" name=\""+aName+"\">");
	}	
	/**
	*	
	*/		
	protected String getClassClose ()
	{
		return ("</"+getClassType ()+">");
	}
	/**
	*	
	*/	
	protected Element getClassElement ()
	{
		//return ("<"+getClassName ()+">");
		return (new Element (getClassType ()));
	}
	/**
	*	
	*/		
	protected Element getClassElement (String aName)
	{
		Element newElement=new Element (getClassType ());
		newElement.setAttribute("name",aName);
		//return ("<"+getClassName ()+" name=\""+aName+"\">");
		return (newElement);
	}		
	/**
	*	
	*/		
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(getClassOpen ()+"<name>"+getName ()+"</name>"+getClassClose ());
		return (buffer.toString ());
	}
	/**
	*	
	*/		
	public Element toElement()
	{
		Element newElement=getClassElement ();
		Element nameElement=new Element ("name");
		nameElement.setText(getName());
		newElement.addContent(nameElement);
		return(newElement);
	}	
	/**
	*	
	*/  
	public void showNodeType (Node a_node)
	{
		if (a_node==null)
		{
			debug ("Error: provided node is null");
			return;
		}
	  
		switch (a_node.getNodeType ())
		{
			case Node.ATTRIBUTE_NODE:         
										debug ("Node type: The node is an Attr.");
	                                    break;  
			case Node.CDATA_SECTION_NODE: 
	                                     debug ("Node type: The node is a CDATASection.");
	                                     break;  
			case Node.COMMENT_NODE:           
	                                     debug ("Node type: The node is a Comment.");
	                                     break;  
			case Node.DOCUMENT_FRAGMENT_NODE: 
	                                     debug ("Node type: The node is a DocumentFragment.");
	                                     break;  
			case Node.DOCUMENT_NODE: 
	                                     debug ("Node type: The node is a Document.");
	                                     break;  
			case Node.DOCUMENT_TYPE_NODE: 
	                                     debug ("Node type: The node is a DocumentType.");
	                                     break;  
			case Node.ELEMENT_NODE: 
	                                     debug ("Node type: The node is an Element.");
	                                     break;  
			case Node.ENTITY_NODE: 
	                                     debug ("Node type: The node is an Entity.");
	                                     break;  
			case Node.ENTITY_REFERENCE_NODE: 
	                                     debug ("Node type: The node is an EntityReference.");
	                                     break;  
			case Node.NOTATION_NODE: 
	                                     debug ("Node type: The node is a Notation.");
	                                     break;  
			case Node.PROCESSING_INSTRUCTION_NODE: 
	                                     debug ("Node type: The node is a ProcessingInstruction.");
	                                     break;  
			case Node.TEXT_NODE:                   
	                                     debug ("Node type: The node is a Text node.");
	                                     break;   
		}
	} 	
	/**
	 *
	 */
    public void fromString (String xml)
    {
    	debug ("fromString ()");
    	
        SAXBuilder builder = new SAXBuilder();
        Document doc=null;
		try {
			doc = builder.build(new StringReader(xml));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		  
		Element root=doc.getRootElement();    
		
		fromXML (root);
    }
	/**
	 *
	 */
    public void fromXML (Element root)
    {
    	debug ("fromXML ()");	
    }		
	/**
	 *
	 */		
	public static void debug(String aClass,String s) 
	{
		/*
		if(!CTATLink.printDebugMessages)
			return;
		*/	
		
		if (!trace.getDebugCode("ll"))
			return;
		
		if (outStream == null)
			return;
		
		String aString=String.format("(%b) [%s] [%d] <%s> %s\n", trace.getDebugCode("ll"),df.format(new Date()), ++CTATLink.debugLine, aClass, s);

		outStream.print(aString);
		outStream.flush();
		
		if (CTATLink.logSnooper!=null)
			CTATLink.logSnooper.addLine(aString);
	}     	
	/**
	 *
	 */		
	public void debug(String s) 
	{
		/*
		if(!CTATLink.printDebugMessages)
			return;
		*/

		if (trace.getDebugCode("ll")==false)
			return;
		
		if (outStream == null)
			return;
		
		String aString="";
		String aDateString=df.format(new Date());
		
		try
		{
			aString=String.format("("+trace.getDebugCode("ll")+") [%s] [%d] <"+className+"> %s\n",	aDateString, ++CTATLink.debugLine, s);
		}
		catch (UnknownFormatConversionException fe)
		{
			trace.out("Unknown format conversion exception for: " + className + ", " + aDateString + ", " + CTATLink.debugLine + ", " + s);
		}
				
		//outStream.printf (aString);
		outStream.print(aString);
		outStream.flush();
		
		if (CTATLink.logSnooper!=null)
			CTATLink.logSnooper.addLine(aString);		
	}
	/**
	 *
	 */		
	public static void error(String aClass,String s) 
	{		
		if (outStream == null)
			return;
		
		String aString=String.format("[%s] [%d] <%s> %s\n", df.format(new Date()), ++CTATLink.debugLine, aClass, s);

		errStream.print(aString);
		errStream.flush();
		
		if (CTATLink.logSnooper!=null)
			CTATLink.logSnooper.addLine(aString);
	}   	
	/**
	 *
	 */		
	public void error(String s)
	{	
		/*
		if(!CTATLink.printDebugMessages)
			return;
		*/	
		
		if (outStream == null)
			return;
		
		String aString="";
		String aDateString=df.format(new Date());
		
		try
		{
			aString=String.format("[%s] [%d] <"+className+"> %s\n",	aDateString, ++CTATLink.debugLine, s);
		}
		catch (UnknownFormatConversionException fe)
		{
			trace.out("Unknown format conversion exception for: " + className + ", " + aDateString + ", " + CTATLink.debugLine + ", " + s);
		}
				
		errStream.print(aString);
		errStream.flush();
		
		if (CTATLink.logSnooper!=null)
			CTATLink.logSnooper.addLine(aString);		
	}	
	/**
	 * Write a string and a stack backtrace.
	 * @param s
	 * @param e
	 */
	public void debugStack(String s, Throwable e) {
		if(!CTATLink.printDebugMessages)
			return;
		
		if (outStream == null)
			return;
		debug(s);
		e.printStackTrace(outStream);
		outStream.flush();
	}
	
	/**
	 * @return the outStream
	 */
	public static PrintStream getOutStream() {
		return outStream;
	}

	/**
	 * @return the errStream
	 */
	public static PrintStream getErrStream() {
		return errStream;
	}

	/**
	 * @param outStream the outStream to set
	 */
	public static void setOutStream(PrintStream outStream) {
		CTATBase.outStream = outStream;
	}

	/**
	 * @param errStream the errStream to set
	 */
	public static void setErrStream(PrintStream errStream) {
		CTATBase.errStream = errStream;
	}
}
