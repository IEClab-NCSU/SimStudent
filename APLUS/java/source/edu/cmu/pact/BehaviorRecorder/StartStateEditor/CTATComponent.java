/**------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2014-10-06 14:55:50 -0400 (Mon, 06 Oct 2014) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.12  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.11  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.10  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.9  2011/07/01 18:34:57  vvelsen
 Lots of small fixes that make the start stated editor integrate better with the flash code.

 Revision 1.8  2011/06/20 15:05:51  vvelsen
 This should properly process the Commshell and show a first proper preview version of a Flash tutor.

 Revision 1.7  2011/06/16 14:15:43  vvelsen
 Added much better intraspection support. The BR has a much better idea of the state and layout of the tutor.

 Revision 1.6  2011/06/11 14:12:17  vvelsen
 First version of start state editor that fully processes and displays the start state coming from an AS3 based tutor.

 Revision 1.5  2011/06/10 12:44:01  vvelsen
 Added missing files and changed the w3c parser to the jdom parser.

 Revision 1.4  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.3  2011/06/01 19:39:38  vvelsen
 More serialization functionality. We also now have an output console and better cell rendering.

 Revision 1.2  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;

import org.jdom.Attribute;
import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable.IncludeIn;
import edu.cmu.pact.Utilities.EmptyIterator;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
*
*/
public class CTATComponent extends CTATComponentBase
{
	/** All top-level attributes from the component element. */
	private Map<String, String> attrsMap = new HashMap<String, String>();

	private float x=0;
	private float y=0;
	private float width=0;
	private float height=0;
	
	private int previewX=0;
	private int previewY=0;
	private int previewWidth=0;
	private int previewHeight=0;
	
	private Boolean selected=false;
	
	private ArrayList <CTATSAI>SAIs=null;
	private ArrayList <CTATParameter>parameters=null;
	private ArrayList <CTATStyleProperty>styles=null;
	
	private JCheckBox checker=null;
	
	/**
	 *
	 */
    public CTATComponent () 
    {
    	setClassName ("CTATComponent");
    	setClassType ("CTATComponent");
    	debug ("CTATComponent ()");
    	
    	SAIs=new ArrayList<CTATSAI> ();
    	parameters=new ArrayList<CTATParameter> ();
    	styles=new ArrayList<CTATStyleProperty> ();
    }
	/**
	 *
	 */    
    public void setChecker (JCheckBox aChecker)
    {
    	checker=aChecker;
    }
	/**
	 *
	 */    
    public JCheckBox getChecker ()
    {
    	return (checker);
    }    
	/**
	 *
	 */	
	public ArrayList <CTATSAI> getSAIs ()
	{
		return (SAIs);
	}
	/**
	 *
	 */
	public void setSelected(Boolean aValue) 
	{
		this.selected=aValue;
	}
	/**
	 *
	 */
	public Boolean getSelected() 
	{
		return selected;
	}
	/**
	 *
	 */
	public void setPreviewDimensions (int aX,int aY,int aWidth,int aHeight)
	{
		previewX=aX;
		previewY=aY;
		previewWidth=aWidth;
		previewHeight=aHeight;
	}
	/**
	 *
	 */	
	public void setPreviewX(int previewX) 
	{
		this.previewX = previewX;
	}
	/**
	 *
	 */	
	public int getPreviewX() 
	{
		return previewX;
	}
	/**
	 *
	 */	
	public void setPreviewY(int previewY) 
	{
		this.previewY = previewY;
	}
	/**
	 *
	 */	
	public int getPreviewY() 
	{
		return previewY;
	}
	/**
	 *
	 */	
	public void setPreviewWidth(int previewWidth) 
	{
		this.previewWidth = previewWidth;
	}
	/**
	 *
	 */	
	public int getPreviewWidth() 
	{
		return previewWidth;
	}
	/**
	 *
	 */	
	public void setPreviewHeight(int previewHeight) 
	{
		this.previewHeight = previewHeight;
	}
	/**
	 *
	 */	
	public int getPreviewHeight() 
	{
		return previewHeight;
	}	
	/**
	 *
	 */	
	public void setX(String aValue) 
	{
		debug ("setX ("+aValue+")");
		this.x =Float.parseFloat (aValue);
		debug ("this.x: " + this.x);
	}
	/**
	 *
	 */	
	public void setX(float aValue) 
	{
		this.x=aValue;
	}
	/**
	 *
	 */	
	public float getX() 
	{
		return (this.x);
	}	
	/**
	 *
	 */	
	public void setY(String aValue) 
	{
		debug ("setY ("+aValue+")");
		this.y =Float.parseFloat (aValue);
		debug ("this.y: " + this.y);
	}	
	/**
	 *
	 */	
	public void setY(float aValue) 
	{
		this.y=aValue;
	}
	/**
	 *
	 */	
	public float getY() 
	{
		return (this.y);
	}	
	/**
	 *
	 */	
	public void setWidth(String aValue) 
	{
		debug ("setWidth ("+aValue+")");
		this.width =Float.parseFloat (aValue);
		debug ("this.width: " + this.width);
	}
	/**
	 *
	 */	
	public void setWidth(float aValue) 
	{
		this.width=aValue;
	}
	/**
	 *
	 */	
	public float getWidth() 
	{
		return (this.width);
	}	
	/**
	 *
	 */	
	public void setHeight(String aValue) 
	{
		debug ("setHeight ("+aValue+")");
		this.height =Float.parseFloat (aValue);
		debug ("this.height: " + this.height);
	}	
	/**
	 *
	 */	
	public void setHeight(float aValue) 
	{
		this.height=aValue;
	}	
	/**
	 *
	 */	
	public float getHeight() 
	{
		return (this.height);
	}	
	/**
	 *
	 */
	public ArrayList<CTATParameter> getParameters ()
	{
		return (parameters);
	}
	/**
	 *
	 */
	public ArrayList<CTATStyleProperty> getStyleProperties ()
	{
		return (styles);
	}	
	/**
	 *
	 */	
	public String getAllSAIsXML ()
	{
		StringBuffer buffer=new StringBuffer();
				
		for (int i=0;i<SAIs.size();i++)
		{
			CTATSAI SAI=SAIs.get(i);
			
			buffer.append("<selection>");
			buffer.append(SAI.toString());
			buffer.append("</selection>");
		}
		
		return (buffer.toString ());		
	}
	/**
	 *
	 */	
	public String getAllComponentParametersXML (Boolean isTouched)
	{
		StringBuffer buffer=new StringBuffer();
		
		if (isTouched==true)
		{
			// Only send those items that have been changed ...
			
			for (int i=0;i<parameters.size();i++)
			{
				CTATParameter parameter=parameters.get(i);
			
				if (parameter.getTouched()==true)
				{
					buffer.append("<selection>");
					buffer.append(parameter.toString());
					buffer.append("</selection>");
				}	
			}
		}
		else
		{
			for (int i=0;i<parameters.size();i++)
			{
				CTATParameter parameter=parameters.get(i);
			
				buffer.append("<selection>");
				buffer.append(parameter.toString());
				buffer.append("</selection>");
			}			
		}
		
		return (buffer.toString ());		
	}
	/**
	 *
	 */	
	public String getAllStylePropertiesXML (boolean isTouched)
	{
		StringBuffer buffer=new StringBuffer();
		
		if (isTouched==true)
		{
			// Only send those items that have been changed ...
			
			for (int i=0;i<styles.size();i++)
			{
				CTATStyleProperty style=styles.get(i);
				
				if (style.getTouched()==true)
				{
					buffer.append("<selection>");
					buffer.append(style.toString());
					buffer.append("</selection>");
				}	
			}
		}
		else
		{
			for (int i=0;i<styles.size();i++)
			{
				CTATStyleProperty style=styles.get(i);
			
				buffer.append("<selection>");
				buffer.append(style.toString());
				buffer.append("</selection>");
			}			
		}
		
		return (buffer.toString ());		
	}
	/**
	 *
	 */
	public void getAllParameterElements (Element root,Boolean isTouched,CTATSerializable.IncludeIn includeIn)
	{
		if (isTouched==true)
		{
			// Only send those items that have been changed ...
			
			Element paramSelection=new Element ("selection");
			root.addContent (paramSelection);
			
			for (int i=0;i<parameters.size();i++)
			{
				CTATParameter parameter=parameters.get(i);
			
				if ((parameter.getTouched()==true) && (parameter.getIncludeIn().compareTo(includeIn) <= 0))
				{
					paramSelection.addContent(parameter.toStringElement());
				}	
			}
		}
		else
		{
			Element paramSelection=new Element ("selection");
			root.addContent (paramSelection);			
			
			for (int i=0;i<parameters.size();i++)
			{
				CTATParameter parameter=parameters.get(i);
			
				if (parameter.getIncludeIn().compareTo(includeIn) <= 0)
					paramSelection.addContent(parameter.toStringElement());
			}			
		}
	}
	/**
	 *
	 */
	public void getAllStyleElements (Element root,Boolean isTouched,CTATSerializable.IncludeIn includeIn)
	{
		if (isTouched==true)
		{
			// Only send those items that have been changed ...
			
			Element styleSelection=new Element ("selection");
			root.addContent (styleSelection);			
			
			for (int i=0;i<styles.size();i++)
			{
				CTATStyleProperty style=styles.get(i);
				
				if ((style.getTouched()==true) && (style.getIncludeIn().compareTo(includeIn) <= 0))
				{
					styleSelection.addContent(style.toStringElement ());
				}	
			}
		}
		else
		{
			Element styleSelection=new Element ("selection");
			root.addContent (styleSelection);			
			
			for (int i=0;i<styles.size();i++)
			{
				CTATStyleProperty style=styles.get(i);
			
				if ((style.getIncludeIn().compareTo(includeIn) <= 0))
					styleSelection.addContent(style.toStringElement ());
			}			
		}	   	   
	}		
	/**
	*	
	*/		
	public String toString()
	{
		debug ("toString ()");
		
		StringBuffer buffer=new StringBuffer();
				
		buffer.append(getClassOpen (getInstanceName ()));
		// We don't really need to tell the Flash components about all the methods they
		// support
		/*
		buffer.append("<SAIs>");
		buffer.append(getAllSAIsXML ());
		buffer.append("</SAIs>");
		*/
		buffer.append("<Parameters>");
		buffer.append(getAllComponentParametersXML (false));
		buffer.append("</Parameters>");
		buffer.append("<Styles>");
		buffer.append(getAllStylePropertiesXML (false));
		buffer.append("</Styles>");		
		buffer.append(getClassClose ());
		
		return (buffer.toString ());
	} 	
	/**
	*	
	*/		
	public String toStringTouched()
	{
		debug ("toStringTouched ()");
		
		StringBuffer buffer=new StringBuffer();
				
		buffer.append(getClassOpen (getInstanceName ()));
		// We don't really need to tell the Flash components about all the methods they
		// support
		/*
		buffer.append("<SAIs>");
		buffer.append(getAllSAIsXML ());
		buffer.append("</SAIs>");
		*/
		buffer.append("<Parameters>");
		buffer.append(getAllComponentParametersXML (true));
		buffer.append("</Parameters>");
		buffer.append("<Styles>");
		buffer.append(getAllStylePropertiesXML (true));
		buffer.append("</Styles>");
		buffer.append(getClassClose ());
		
		return (buffer.toString ());
	} 		
	/**
	*	
	*/		
	public Element toStringElement(CTATSerializable.IncludeIn includeIn)
	{
		debug ("toStringElement ("+includeIn+")");
		
		Element newElement=getClassElement ();
		
		// We don't really need to tell the Flash components about all 
		// the methods they support, so we can skip that section
		
		if(includeIn == IncludeIn.full) {
			Element parameterElement=new Element ("s");						
			newElement.addContent(parameterElement);
		}		

		Element parameterElement=new Element ("Parameters");						
		newElement.addContent(parameterElement);
		getAllParameterElements (parameterElement,false,includeIn);
		
		Element styleElement=new Element ("Styles");						
		newElement.addContent(styleElement);		
		getAllStyleElements (styleElement,false,includeIn);
				
		return (newElement);
	}

	/**
	 * Generate a top-level element from {@link #getClassElement(String)} and add all attributes.
	 * @return element with all top-level attributes saved in {@link #attrsMap}
	 * @see edu.cmu.hcii.ctat.CTATBase#getClassElement()
	 */
	protected Element getClassElement () {
		Element result = super.getClassElement(getInstanceName());
		for(Iterator<String> it = attrsMap.keySet().iterator(); it.hasNext();) {
			String name = it.next();
			result.setAttribute(name, attrsMap.get(name));			
		}
		return result;
	}

	/**
	*	
	*/		
	public Element toStringElementTouched()
	{
		debug ("toStringElementTouched ()");
		
		Element newElement=getClassElement (getInstanceName ());
		
		// We don't really need to tell the Flash components about all 
		// the methods they support, so we can skip that section
		
		Element parameterElement=new Element ("Parameters");						
		newElement.addContent(parameterElement);
		
		getAllParameterElements (parameterElement,true,CTATSerializable.IncludeIn.full);
		
		Element styleElement=new Element ("Styles");						
		newElement.addContent(styleElement);		
		
		getAllStyleElements (styleElement,true,CTATSerializable.IncludeIn.full);
				
		debug ("Returning fresh element ...");
		
		return (newElement);
	}	
	/**
	 *
	 */
    public void fromXML (Element node)
    {
    	debug ("fromXML ("+(node == null ? null : node.getName())+")");
    	if(node == null)
    		return;
    	
    	setClassType (node.getName ());
    	
    	Iterator<Element> itr = (node.getChildren()).iterator();

    	debug ("Parsing attributes ...");
    	List<Attribute> attrs = (List<Attribute>) node.getAttributes();
		for(Iterator<Attribute> it =
				(attrs == null ? EmptyIterator.instance() : attrs.iterator()); it.hasNext(); ) {
			Attribute attr = it.next();
			debug (String.format("  %-17s = \"%s\"", attr.getName(), attr.getValue()));
			attrsMap.put(attr.getName(), attr.getValue());
		}    	
    	setInstanceName (attrsMap.get("name"));
    	setX (attrsMap.get("x"));
    	setY (attrsMap.get("y"));
    	setWidth (attrsMap.get("width"));
    	setHeight (attrsMap.get("height"));
    	
		while (itr.hasNext()) 
		{
            Element elem=(Element) itr.next();    	

            //>--------------------------------------
            
			if (elem.getName ().equals ("SAIs"))
			{            
				debug ("Parsing SAIs");
				
				Iterator<Element> SAIItr = (((Element) elem.getChildren().get(0)).getChildren()).iterator();
				
				while (SAIItr.hasNext()) 
				{
		            Element SAIElement=(Element) SAIItr.next();		
					CTATSAI SAI=new CTATSAI ();
					SAI.fromXML (SAIElement);
					SAIs.add (SAI);		            
				}    
			}
			
            //>--------------------------------------
            
			if (elem.getName ().equals ("Parameters"))
			{            
				debug ("Parsing Parameters");
				
				Iterator<Element> ParameterItr = (((Element) elem.getChildren().get(0)).getChildren()).iterator();
				
				while (ParameterItr.hasNext()) 
				{
		            Element ParameterElement=(Element) ParameterItr.next();		
		            
					CTATParameter parameter=new CTATParameter ();
					parameter.fromXML (ParameterElement);
					parameters.add (parameter);		            
				}    
			}
			
            //>--------------------------------------
            
			if (elem.getName ().equals ("Styles"))
			{            
				debug ("Parsing Styles");
				
				Iterator<Element> StyleItr = (((Element) elem.getChildren().get(0)).getChildren()).iterator();
				
				while (StyleItr.hasNext()) 
				{
		            Element StyleElement=(Element) StyleItr.next();
		            
					CTATStyleProperty styleProperty=new CTATStyleProperty ();
					styleProperty.fromXML (StyleElement);
					styles.add (styleProperty);		            
				}    
			}			
			
            //>--------------------------------------			
		}
    }
	
	/**
	 * Remove parameter and style elements from an {@link MsgType#INTERFACE_DESCRIPTION} message
	 * that should not be sent to the student interface.
	 * @param mo message to edit
	 * @param includeIn one of {@link CTATSerializable.IncludeIn#sparse},etc. 
	 */
	public static void editInterfaceDescriptionMessage(MessageObject mo,
			CTATSerializable.IncludeIn includeIn) {
		if(!MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(mo.getMessageType()))
			return;
		Element compElt = (Element) mo.getProperty("serialized");
		if(compElt == null)
			return;
		CTATComponent comp = new CTATComponent();
		comp.fromXML(compElt);
		mo.setProperty("serialized", comp.toStringElement(includeIn));
	}
}
