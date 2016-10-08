/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.1  2011/07/01 19:31:40  vvelsen
 Added an XML viewer so we can debug both the entire start state as well as any outgoing messages.

 
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATXMLViewer extends JTree 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DefaultTreeModel treeModel;
	CTATXMLHandler handler=null;
	
	/**
	 * 
	 */			
	public CTATXMLViewer( )
    {
		setup ();
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( java.util.Hashtable<?,?> value )
    {
        super (value);
		setup ();        
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( Object[] value )
    {
        super (value);
		setup ();        
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( javax.swing.tree.TreeModel newModel )
    {
        super (newModel);
		setup ();        
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( javax.swing.tree.TreeNode root )
    {
        super (root);
		setup ();        
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( javax.swing.tree.TreeNode root, boolean asksAllowsChildren )
    {
        super (root,asksAllowsChildren);
		setup ();        
    }
	/**
	 * 
	 */			
	public CTATXMLViewer( java.util.Vector<?> value )
    {
        super (value);
		setup ();        
    }		
	/**
	 * 
	 */			
	public void setup ()
	{
		treeModel=new DefaultTreeModel (null);		
		this.setModel (treeModel);
		handler=new CTATXMLHandler ();
		handler.setTree (this);
	}
    /**
	 * 
	 */
    private void debug (String aMessage)
    {
    	CTATBase.debug ("CTATXMLViewer",aMessage);
    }	
	/**
	 * 
	 */			
	public void setXML (String aStream)
	{		
		debug ("setXML ()");
		
		handler.xmlSetUp(aStream);
		expandAll ();
	}
	/**
	 * 
	 */	
	public void expandAll() 
	{
		int row = 0;
		
		while (row < this.getRowCount()) 
		{
			this.expandRow(row);
			row++;
		}
	}	
}
