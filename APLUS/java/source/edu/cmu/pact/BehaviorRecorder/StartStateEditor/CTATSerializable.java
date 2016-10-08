/**------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2013-05-19 14:58:35 -0400 (Sun, 19 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.7  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.6  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.5  2011/06/11 14:12:17  vvelsen
 First version of start state editor that fully processes and displays the start state coming from an AS3 based tutor.

 Revision 1.4  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.3  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.

 Revision 1.2  2011/05/27 20:58:25  vvelsen
 Many more pieces of functionality. The serialization is much better, according to spec and in sync with the AS3 version.

 Revision 1.1  2011/05/26 16:12:06  vvelsen
 Added first version of the start state editor. There's a test rig under the test directory.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;


import java.util.Iterator;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.Utilities.trace;

/**

*/
public class CTATSerializable extends CTATBase
{
	public static enum IncludeIn {
		minimal,  // send the minimum set of elements from Tutoring Service to student interface
		sparse,   // send this element from Tutoring Service under normal conditions 
		full,     // send this element only when TS should override all parameters (old default)
		never     // don't send this element even if asked to send all parameters
	}
	
	/** For debugging. */
	protected static final XMLOutputter xmlOutp = new XMLOutputter(Format.getPrettyFormat()); 
	
	protected String value="";
	protected String type="";
	protected String format=""; // Either one of Boolean, Number, String
	
	protected IncludeIn includeIn = IncludeIn.minimal;  // default for backward compatibility
	
	private Boolean touched=false;
	
	/**
	 *
	 */
    public CTATSerializable () 
    {
    	setClassName ("CTATSerializable");
    	debug ("CTATSerializable ()");
    }
	/**
	 *
	 */    
	public void setTouched(Boolean touched) 
	{
		this.touched = touched;
	}
	/**
	 *
	 */	
	public Boolean getTouched() 
	{
		return touched;
	}    
	/**
	 *
	 */
    public void setValue (String aValue)
    {
    	value=aValue;
    }
	/**
	 *
	 */
    public String getValue ()
    {
    	return (value);
    }  
	/**
	 *
	 */	
	public void setType(String aType) 
	{
		this.type = aType;
	}
	/**
	 *
	 */	
	public String getType() 
	{
		return type;
	}  
	/**
	 *
	 */	
	public void setFormat(String aFormat) 
	{
		this.format = aFormat;
	}
	/**
	 *
	 */	
	public String getFormat() 
	{
		return format;
	} 	
	/**
	 * 
	 */
	public void setIncludeIn (CTATSerializable.IncludeIn aValue)
	{
		includeIn=aValue;
	}
	/**
	 * 
	 */
	public IncludeIn getIncludeIn ()
	{
		return (includeIn);
	}
	/**
	 * 
	 */
	protected String getIncludeInString ()
	{
		StringBuffer formatter=new StringBuffer ();
		
		formatter.append(" includein=\"");
		formatter.append(includeIn.toString());
		formatter.append("\" ");
		
		return (formatter.toString());
	}
	/**
	*	
	*/		
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(getClassOpen ()+"<name>"+getName ()+"</name><value fmt=\"text\" type=\""+getType ()+"\" "+getIncludeInString ()+">"+getValue ()+"</value>"+getClassClose ());
		return (buffer.toString ());
	}
	/**
	*	
	*/		
	public Element toStringElement()
	{
		Element newElement=getClassElement ();

		Element nameElement=new Element ("name");
		nameElement.setText(getName());						
		newElement.addContent(nameElement);
		
		Element valueElement=new Element ("value");
		valueElement.setAttribute("fmt","text");
		valueElement.setAttribute("type",getType());
		if(includeIn != CTATSerializable.IncludeIn.minimal)
			valueElement.setAttribute("includein",includeIn.toString());
		
		valueElement.setText(value);		
				
		newElement.addContent(valueElement);		
		
		if(trace.getDebugCode("ll"))
			trace.outNT("ll", getClass().getSimpleName()+".toStringElement() result:\n"+xmlOutp.outputString(newElement));		
		return(newElement);
	}	
	/**
	 *
	 */
	public void fromXML (Element node)
	{
		debug ("fromXML ()");
 
		Iterator<Element> itr = (node.getChildren()).iterator();
		
		while (itr.hasNext()) 
		{
           Element elem=(Element) itr.next();
           
           debug ("Parsing text node ("+elem.getName()+")...");
           
			if (elem.getName().equals ("name")==true)
			{
				debug ("Parsing selection: " + ((Element) elem.getChildren().get(0)).getValue());
				setName (((Element) elem.getChildren().get(0)).getValue());
			}   
			
			if (elem.getName().equals ("value")==true)
			{
				setValue (((Element) elem.getChildren().get(0)).getValue());				
				setType (elem.getAttributeValue("type"));
				
				if (elem.getAttributeValue("includein")!=null)
				{
					try {
						includeIn = IncludeIn.valueOf(elem.getAttributeValue("includein").toLowerCase());
					} catch (Exception e) {
						trace.err("Invalid includeIn attribute \""+elem.getAttributeValue("includein")+
								"\" for element "+node.getName()+"; default "+IncludeIn.full);
						includeIn = IncludeIn.minimal;  // default for backward compatibility
					}
				}
			}
		}     				
	}
}
