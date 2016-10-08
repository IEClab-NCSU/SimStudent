/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.8  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.7  2011/07/22 12:00:37  sewall
 Change "Dormin" to "JComm" or "Comm".

 Revision 1.6  2011/07/14 23:36:43  sewall
 De-comm: merge changes from branch CTAT_2_11_Release.

 Revision 1.5  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 Revision 1.4  2011/06/28 12:50:16  vvelsen
 Fixed removal of SAIs from table. Better preview and the interface now properly responds to interfaces being disconnected and re-started.

 Revision 1.3  2011/06/22 13:51:52  vvelsen
 You can now add actions to the start state action table. You can also execute an action directly.

 Revision 1.2  2011/06/20 15:05:51  vvelsen
 This should properly process the Commshell and show a first proper preview version of a Flash tutor.

 Revision 1.1  2011/06/16 14:15:43  vvelsen
 Added much better intraspection support. The BR has a much better idea of the state and layout of the tutor.

 Revision 1.4  2011/06/14 13:52:21  vvelsen
 Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

 Revision 1.3  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.2  2011/06/01 19:39:38  vvelsen
 More serialization functionality. We also now have an output console and better cell rendering.

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

import java.awt.Component;
import java.awt.Dimension;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.io.StringReader;
import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

//import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.input.SAXBuilder;

import edu.cmu.hcii.ctat.CTATBase;
//import edu.cmu.pact.Utilities.trace;

public class CTATJPanel extends JPanel
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
    public CTATJPanel () 
    {
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
    public void fromXML (Element root)
    {
    	debug ("fromXML ()");	
    }	
	/**
	 *
	 */		
	public void debug(String s) 
	{
		CTATBase.debug("CTATJPanel", s);
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
