/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.10  2011/09/01 03:02:34  sewall
 Add MessageObject.getPropertiesElement().

 Revision 1.9  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.8  2011/07/22 12:00:37  sewall
 Change "Dormin" to "JComm" or "Comm".

 Revision 1.7  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.6  2011/06/22 14:41:41  vvelsen
 Small fix to SAI processing.

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

import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;

/**
 * Definition:
 * 
 *	<CTATInterfaceAction>
 *     <selection><value>this</value></selection>
 *     <action><value>setVisible</value></action>
 *      <input>
 *     		<value fmt="text" name="instance1" type="String">Some Text</value>
 *     		<value fmt="text" name="instance2" type="Number">5</value>
 *     	</input>
 *  </CTATInterfaceAction>
 *  
*/
public class CTATSAI extends CTATSerializable
{	
	private String action="";
	
	private ArrayList <CTATArgument>arguments=null;
	
	/**
	 *
	 */
    public CTATSAI () 
    {
    	setClassName ("CTATSAI");
    	//debug ("CTATSAI ()");
    	
    	arguments=new ArrayList<CTATArgument> ();
    }
	/**
	 * 
	 */
	public ArrayList<CTATArgument> getArguments ()
	{
		return (arguments);
	}    
	/**
	 * 
	 */
	public int getArgumentSize ()
	{
		return (arguments.size());
	}    	
	/**
	 *
	 */   
	public void setSelection(String selection) 
	{
		setName (selection);
	}
	/**
	 *
	 */	
	public String getSelection() 
	{
		return getName ();
	}
	/**
	 *
	 */	
	public void setAction(String action) 
	{
		this.action=action;
	}
	/**
	 *
	 */	
	public String getAction() 
	{
		return action;
	}	    
	/**
	 *
	 */
    private void resetArguments ()
    {
    	arguments.clear();
    }
	/**
	 * 
	 */
	private CTATArgument getArgument (int anIndex)
	{
		debug ("getArgument ("+anIndex+"->"+arguments.size()+")");
		
		//debugArray (internalArguments);
		
		CTATArgument arg=arguments.get(anIndex);
		if (arg==null)
			debug ("Error: unable to retrieve argument at index " + anIndex);
		
		return (arg);
	}    
	/**
	 * 
	 */
	private CTATArgument checkDefaultArgument ()
	{
		debug ("checkDefaultArgument ()");
		
		if (arguments.size()==0)
		{
			debug ("Adding default argument");
			CTATArgument defaultArgument=new CTATArgument ();
			arguments.add(defaultArgument);
			return (defaultArgument);
		}				
		
		return (null);
	}
	/**
	 * 
	 */
	public CTATArgument setArgument (int anIndex,String aValue)
	{
		debug ("setArgument ()");
		
		checkDefaultArgument ();
		
		CTATArgument tempArgument=arguments.get(anIndex);
		tempArgument.setValue(aValue);
		return (tempArgument);
	}			
	/**
	 * 
	 */
	public CTATArgument addArgument (String aName,String aValue,String aType,String aFormat)
	{
		debug ("addArgument ()");
		CTATArgument tempArgument=new CTATArgument ();
		tempArgument.setName(aName);
		tempArgument.setValue(aValue);
		tempArgument.setType (aType);
		tempArgument.setFormat (aFormat);
		arguments.add(tempArgument);
		return (tempArgument);
	}	
	/**
	*	Changes the current input value
	*/
	public void setInput (String newInput) 
	{
		debug("setInput()");

		checkDefaultArgument ();
		
		CTATArgument arg=getArgument (0);
		arg.setValue(newInput);
	}
	/**
	*	Returns the primary selection value
	*/
	public String getInput() 
	{
		debug("getInput()");
		
		if (arguments.size()==0)
			return ("");			
		
		CTATArgument arg=getArgument (0);
		return arg.getValue();
	}
	/**
	*
	*/
	public void setType(String aType) 
	{
		debug("setType()");
		
		checkDefaultArgument ();
		
		CTATArgument arg=getArgument (0);
		arg.setType(aType);
	}			
	/**
	 *	Returns the primary selection value
	 */
	public String getType() 
	{
		debug("getType()");
		
		if (arguments.size()==0)
			return ("");			
		
		CTATArgument arg=getArgument (0);
		return arg.getType();
		
		//return value;
	}		
	/**
	 *
	 */
	public void setFormat (String aFormat) 
	{
		debug("setFormat()");
		
		checkDefaultArgument ();			
		
		CTATArgument arg=getArgument (0);
		arg.setFormat(aFormat);
	}			
	/**
	 *	Returns the primary selection value
	 */
	public String getFormat() 
	{
		debug("getFormat()");
		
		if (arguments.size()==0)
			return ("");
		
		CTATArgument arg=getArgument (0);
		return arg.getFormat();		
	}			
	/**
	 * 
	 */
	public String toArgumentString ()
	{
		StringBuffer formatted=new StringBuffer();
		
		for (int i=0;i<arguments.size();i++)
		{
			if (i>0)
				formatted.append(", ");
			
			CTATArgument arg=arguments.get(i);
			formatted.append(arg.getName()+":"+arg.getValue ());			
		}
		
		return (formatted.toString());
	}
	/**
	*	Returns the primary SAI in XML string form, should be good for simple components
	*/
	public String toString() 
	{
		StringBuffer formatted=new StringBuffer();
		
		formatted.append (getClassOpen ()+"<selection>"+getName ()+"</selection><action>"+action+"</action>");
		
		for (int i=0;i<arguments.size();i++)
		{
			CTATArgument arg=arguments.get(i);
			formatted.append ("<input name=\""+arg.getName()+"\" fmt=\""+arg.getFormat()+"\" type=\""+arg.getType ()+"\" >"+arg.getValue ()+"</input>");
		}
		
		formatted.append(getClassClose ());
		
		return (formatted.toString());
	}
	/**
	*	Returns the primary SAI in XML string form, should be good for simple components
	*/	
	public String toXMLString()
	{
		//return "<selection>" + getName () + "</selection><action>" + action + "</action><input fmt=\"text\" type=\""+getType ()+"\" >"+getValue ()+"</input>";
		
		return (toString ());
	}
	/**
	*	Returns the primary SAI in XML string form, should be good for simple components
	*/	
	public String toCommXMLString()
	{
		return "<Selection><value>" + getName () + "</value></Selection><Action><value>" + action + "</value></Action><Input><input fmt=\"text\" type=\""+getType ()+"\" >"+getValue ()+"</input></Input>";
	}
	/**
	*	Returns the primary SAI in XML string form, should be good for simple components
	*/
	public Element toStringElement() 
	{
		debug ("toStringElement ()");
		
		Element newElement=getClassElement ();

		Element selectionElement=new Element ("selection");
		selectionElement.setText(getName());						
		newElement.addContent(selectionElement);
		
		Element actionElement=new Element ("action");
		actionElement.setText(action);						
		newElement.addContent(actionElement);		
		
		Element argElement=new Element ("internalArguments");
		newElement.addContent(argElement);		
		
		for (int i=0;i<arguments.size();i++)
		{
			CTATArgument arg=arguments.get(i);
			
			Element inputElement=new Element ("value");
			inputElement.setAttribute("fmt",arg.getFormat());
			inputElement.setAttribute("type",arg.getType ());
			inputElement.setText(arg.getValue ());		
			argElement.addContent(inputElement);			
		}
				
		return(newElement);
	}	
	/**
	 *
	 */
    public void fromXML (Element node)
    {
    	debug ("fromXML ()");
   	
    	resetArguments ();
    	
    	Iterator<Element> itr = (node.getChildren()).iterator();
		
		while (itr.hasNext()) 
		{
            Element elem=(Element) itr.next();
            
            debug ("Parsing text node ("+elem.getName()+")...");
                        
			if (elem.getName().equals ("selection")==true)
			{
				debug ("Parsing selection: " + elem.getValue());
				setName (elem.getValue());
			}            
            
			if (elem.getName().equals ("action")==true)
			{
				debug ("Parsing selection: " + elem.getValue());
				setAction (elem.getValue());
			}   
			
			if (elem.getName().equals ("internalArguments")==true)
			{
				Iterator<Element> itrArgs = (elem.getChildren()).iterator();
				
				while (itrArgs.hasNext()) 
				{
		            Element elemArg=(Element) itrArgs.next();
		          
					if (elemArg.getName().equals ("value")==true)
					{			
						debug ("Parsing argument/value: " + elemArg.getAttributeValue("name")+" - "+elemArg.getAttributeValue("type")+" - " +elemArg.getAttributeValue("fmt"));
						addArgument (elemArg.getAttributeValue("name"),elemArg.getValue(),elemArg.getAttributeValue("type"),elemArg.getAttributeValue("fmt"));
					}			            
				} 
			}						
			
			debug ("Argument string: " + this.toArgumentString());
		}     	    				
    }
	/**
	 *
	 */
   public void fromCommXML (Element node)
   {
   		debug ("fromCommXML ("+node.getName()+")");
  	
   		resetArguments ();
   	
   		Iterator<Element> itr = (node.getChildren()).iterator();
		
		while (itr.hasNext()) 
		{
           Element elem=(Element) itr.next();
           
           debug ("Parsing text node ("+elem.getName()+")...");
                       
			if (elem.getName().equals ("Selection")==true)
			{
				Iterator<Element> selArgs = (elem.getChildren()).iterator();
				
				while (selArgs.hasNext()) 
				{
		            Element elemArg=(Element) selArgs.next();				
				
		            debug ("Parsing Selection: " + elemArg.getValue());
		            setName (elemArg.getValue());
				}    
			}            
           
			if (elem.getName().equals ("Action")==true)
			{
				Iterator<Element> actArgs = (elem.getChildren()).iterator();
				
				while (actArgs.hasNext()) 
				{				
					Element actArg=(Element) actArgs.next();
					
					debug ("Parsing Action: " + actArg.getValue());
					setAction (actArg.getValue());
				}	
			}   
			
			if (elem.getName().equals ("Input")==true)
			{
				Iterator<Element> itrArgs = (elem.getChildren()).iterator();
				
				while (itrArgs.hasNext()) 
				{
		            Element elemArg=(Element) itrArgs.next();
		          
					if (elemArg.getName().equals ("value")==true)
					{			
						debug ("Parsing argument/value: " + elemArg.getAttributeValue("name")+" - "+elemArg.getAttributeValue("type")+" - " +elemArg.getAttributeValue("fmt"));
						addArgument (elemArg.getAttributeValue("name"),elemArg.getValue(),elemArg.getAttributeValue("type"),elemArg.getAttributeValue("fmt"));
					}			            
				} 
			}						
		}     	    				
   }    
}
