/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.1  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.hcii.ctat.CTATBase;
//import edu.cmu.pact.Utilities.trace;

public class CTATJDialog extends JDialog
{	
	private static final long serialVersionUID = 1L;
	
	/** The output stream for {@link #debug(String)}. Default value {@link System#out}. */
	//protected static PrintStream outStream = System.out;

	/** The error output stream. Default value {@link System#err}. */
	//protected static PrintStream errStream = System.err;

	//private SimpleDateFormat df;
	//private String className=getClass().getSimpleName();
	
	private String name="";
	
    protected ImageIcon defaultIcon=null;	
	
	/**
	 *
	 */
    public CTATJDialog () 
    {
    	//df=new SimpleDateFormat ("HH:mm:ss.SSS");
    }
	/**
	 *
	 */
    public CTATJDialog (JFrame parent, String title, Boolean modal) 
    {
    	super(parent, title, true);
   		//df=new SimpleDateFormat ("HH:mm:ss.SSS");
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
    /*
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(getClassOpen ()+"<name>"+getName ()+"</name>"+getClassClose ());
		return (buffer.toString ());
	} 
	*/   
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
	public void debug(String s) 
	{
		CTATBase.debug("CTATJDialog", s);		
	}	
	/**
	 * Convenience for creating vector from string
	 * @param s
	 * @return Vector with s as first element; null if s is null 
	 */
	protected Vector<String> s2v(String s) 
	{
		if (s == null)
			return null;
		Vector<String> result = new Vector<String>();
		result.add(s);
		return result;
	}		
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 */
	protected ImageIcon createImageIcon (String aFile,String description) 
	{				
		debug ("createImageIcon ("+aFile+")");
		
		ClassLoader loader=getClass ().getClassLoader();
		if (loader==null)
		{
			debug ("Error: no class loader object available");
			return (defaultIcon);
		}
		
		URL resource=loader.getResource("pact/CommWidgets/"+aFile);
		if (resource==null)
		{
			debug ("Error: unable to find image resource in jar file");
			return (defaultIcon);
		}			
		
		return (new ImageIcon (resource,description));
	}
	/**
	 * 
	 */	
	public Box addInHorizontalLayout (Component comp,int maxX,int maxY) 
	{
		Box dynamicBox=new Box (BoxLayout.X_AXIS);
		dynamicBox.setMinimumSize(new Dimension (20,20));
		dynamicBox.setMaximumSize(new Dimension (maxX,maxY));
		dynamicBox.add(comp);
		return (dynamicBox);
	}
	/**
	 * 
	 */	
	public Box addInVerticalLayout (Component comp) 
	{
		Box dynamicBox=new Box (BoxLayout.Y_AXIS);
		dynamicBox.add(comp);
		return (dynamicBox);
	}	
}
