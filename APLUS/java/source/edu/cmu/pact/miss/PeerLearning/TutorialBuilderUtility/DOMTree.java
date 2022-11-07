package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.integerInRange;
import edu.cmu.pact.Utilities.trace;

//Document Object Model Tree
public class DOMTree implements TreeSelectionListener, ComponentListener
{
	public enum OUTPUT_TYPE
	{
		CONSOLE, FILE, RETURN, ARRAY;
	}

	/**
	 * Top of the tree
	 */
	private DOMNode head;
	
	/**
	 * Makes sure the current ID can only be accessed via get method
	 */
	public class IDManager
	{
		private int currentID = 0;
		
		public int getCurrentID()
		{			
			return ++currentID;
		}
	}
	
	private IDManager manager;
	
	protected DOMNode getHead() {
		return head;
	}
	
	protected DOMTree(Object contents, ArrayList<DOMNode> children) //creates a new tree with the data for the head
	{
		manager = new IDManager();
		
		head = new DOMNode(contents, null);

		if(children == null)
			head.setChildren(new ArrayList<DOMNode>());
		else 
			head.setChildren(children);
	}

	protected DOMTree(Container c) //constructor for new tree from a graphical container object
	{
		manager = new IDManager();
		
		head = new DOMNode(c, null);
		createTreeFromComponent(c, head);
		
		removeDuplicates();
		addTags();
	}

	/**
	 * Creates a new tree from a java.awt.Container or a subclass
	 * @param c Container to get components from
	 * @param currentParent Current node accepting children
	 */
	protected void createTreeFromComponent(Container c, DOMNode currentParent) //logic for new from a graphical container object
	{
		if(currentParent == null)
			return;

		addChildren(c.getComponents(), currentParent);

		for(int x = 0; x < currentParent.getChildren().size(); x++)
		{
			try{
				createTreeFromComponent((Container)currentParent.getChildren().get(x).getContents(), currentParent.getChildren().get(x));
			}
			catch(ClassCastException e)
			{
				//don't add to tree
			}
		}
	}



	// ------ Adding Children ------ //
	protected void addChildren(Object[] add, DOMNode addTo)
	{
		addChildren(new ArrayList<Object>(Arrays.asList(add)), addTo);
	}

	protected void addChildren(ArrayList<Object> add, DOMNode addTo)
	{
		addTo.addChildren(add);
	}

	protected void addChild(Object add, DOMNode addTo)
	{
		ArrayList<Object> l = new ArrayList<Object>();
		l.add(add);
		addTo.addChildren(l);
	}

	/**
	 * Gateway for recursive search
	 * @param toFind Object to find in the tree
	 * @param current Node to start at
	 * @return Node containing the given object, or null if it's not found
	 */
	protected DOMNode find(Object toFind, DOMNode current)
	{
		if(head.contains(toFind)) //check the head
			return head;
		else {
			return searchFor(toFind, current); //check the rest of the tree
		}
	}

	/**
	 * Searches for a node with the given contents
	 * @param toFind Contents being looked for
	 * @param current Current node in the traversal
	 * @return Node containing the search target object, or null if the object wasn't found
	 */
	private DOMNode searchFor(Object toFind, DOMNode current)
	{	
		for(DOMNode c : current.getChildren()) //check its children
			if(c.contains(toFind))
				return c;

		for(DOMNode c : current.getChildren()) //then tell the children to check their children
			return searchFor(toFind, c);

		return null;
	}

	/**
	 * Single parameter find method, starting at the head
	 * @param toFind See searchFor
	 * @return See searchFor
	 */
	protected DOMNode find(Object toFind) //single object find to start at top
	{
		return searchFor(toFind, head);
	}

	//being output block
	/**
	 * Outputs the tree in a variety of different ways
	 * @param t Type of output
	 * @return String if it is requested, null otherwise
	 */
	protected Object output(OUTPUT_TYPE t)
	{
		switch(t)
		{
		case CONSOLE: //output the tree to the console
			output(head, 0);
			break;
		case FILE: //output the tree to a file
			try {
				PrintWriter p = new PrintWriter("domtree.txt");
				output(head, 0, p);
				p.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			break;
		case RETURN: //returns a string containing a formatted tree
			return output(head, 0, new StringBuilder());
		case ARRAY: //returns a 1-D array containing all the elements
			return output(head, new ArrayList<DOMNode>());
		}

		return null;
	}

	protected ArrayList<DOMNode> output(DOMNode current, ArrayList<DOMNode> arrayList)  //Array return
	{
		if(current == null)
			return null;
		else {
			arrayList.add(current);
		}

		for(DOMNode d : current.getChildren())
		{
			output(d, arrayList);
		}

		return arrayList;

	}

	protected String output(DOMNode current, int tabCount, StringBuilder ret) //"toString" equivalent
	{
		for(int x = 0; x < tabCount; x++)
			ret.append("\t");
		ret.append(current + "\n");

		++tabCount;

		for(DOMNode d : current.getChildren())
		{
			output(d, tabCount, ret);
		}

		return ret.toString();
	}

	protected void output(DOMNode current, int tabCount) //straight to out stream
	{
		for(int x = 0; x < tabCount; x++)
			trace.out("\t");
		trace.out(current.toString());

		++tabCount;

		for(DOMNode d : current.getChildren())
		{
			output(d, tabCount);
		}
	}

	protected void output(DOMNode current, int tabCount, PrintWriter p) //writes to file
	{
		for(int x = 0; x < tabCount; x++)
			p.write("\t");
		p.write(current.toString() + "\n");

		++tabCount;

		for(DOMNode d : current.getChildren())
		{
			output(d, tabCount, p);
		}
	}
	//end output block

	//begin tree data type <-> tree graphical element interaction methods
	private JTree jtree;

	/**
	 * Returns the graphical tree representing the underlying data structure, creating it if it's not present
	 * @return Graphical tree
	 */
	protected JTree getJTree()
	{
		if(jtree == null)
			convertToJTree();
		return jtree;
	}

	/**
	 * Gateway method to remove duplicates
	 */
	protected void removeDuplicates()
	{
		DOMNode rootPane = findInstanceOf(head, new JLayeredPane().getClass());

		removeGlassPane();
		removeDuplicates(rootPane, ((Component) rootPane.getContents()).getSize());
	}

	/**
	 * Removes all objects with the same dimensions
	 * @param current Current node in the traversal
	 * @param currentDim Current dimension for which duplicates are being looked
	 */
	private void removeDuplicates(DOMNode current, Dimension currentDim)
	{				
		ArrayList<DOMNode> temp = current.getChildren();

		for(int x = 0; x < temp.size(); x++) //goes through all the children of the current node
		{
			removeDuplicates(current.getChildren().get(x), currentDim);
		}

		if(((Component) current.getContents()).getSize().equals(currentDim) && current.getChildren().size() > 0) //removes the identical elements, and remakes the parent-child connections
		{			
			for(DOMNode d : current.getChildren())
			{
				current.getParent().addChildren(d);
			}
			current.getParent().remove(current);
			current = current.getParent();
		}
		else
			currentDim = ((Component) current.getContents()).getSize();
	}

	/**
	 * Gateway to recursive remove
	 */
	private void removeGlassPane()
	{
		removeGlassPane(head);
	}

	/**
	 * Removes the glass pane from the tree, if there is one
	 * @param current Current node in the traversal
	 */
	private void removeGlassPane(DOMNode current)
	{
		if(current.verboseToString().indexOf("glassPane") > 0)
		{
			for(DOMNode d : current.getChildren())
			{
				current.getParent().addChildren(d);
			}
			current.getParent().remove(current);
			return;
		}
		else {
			for(DOMNode d : current.getChildren())
			{
				removeGlassPane(d);
			}
		}
	}
	
	/**
	 * No argument way to adding tags
	 */
	private void addTags()
	{
		addTags(head);
	}
	
	
	private void addTags(DOMNode current)
	{
		current.setIDTag(manager.getCurrentID());
		
		for(DOMNode childDomNode : current.getChildren())
		{
			addTags(childDomNode);
		}
	}

	/**
	 * Creates a graphical representation of the underlying tree data structure
	 */
	protected void convertToJTree()
	{		
		DOMNode first = head;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(first);	

		buildTree(first.getChildren().get(0), top, 0);
		jtree = new JTree(top);
		jtree.addTreeSelectionListener(this);
		
		//action for displaying the location
		Action locationAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{				
				DOMNode node = (DOMNode)((DefaultMutableTreeNode)jtree.getLastSelectedPathComponent()).getUserObject();
				Container c = (Container)(node.getContents());
				JOptionPane.showMessageDialog(null, "Location: (" + c.getX() + ", " + c.getY() + ")" + "\nDimensions: (" + (c.getWidth()) + ", " + c.getHeight() + ")");
			}
		};
		jtree.getInputMap().put(KeyStroke.getKeyStroke('l'), "location");
		jtree.getActionMap().put("location", locationAction);
	}
	
	/**
	 * Updates tree with node change
	 */
	public void updateTree()
	{
		((DefaultTreeModel) jtree.getModel()).nodeChanged(head);
	}

	/**
	 * Returns the class name of a Swing object sans the javax.swing
	 * @param o Object to remove javax.swing from
	 * @return Cleaned class name
	 */
	protected String getClassName(Object o)
	{
		return o.getClass().getName().replace("javax.swing.", "");
	}

	/**
	 * Builds a JTree from the tree data model
	 * @param current Current node in the traversal
	 * @param category Current category children are being added to
	 * @param index Current index to move through all nodes in a given level
	 */
	protected void buildTree(DOMNode current, DefaultMutableTreeNode category, int index)
	{
		if(current.getChildren().size() > 0)
		{
			DefaultMutableTreeNode temp = category;
			category = new DefaultMutableTreeNode(current);
			temp.add(category);
		}
		else
			category.add(new DefaultMutableTreeNode(current));

		for(DOMNode d : current.getChildren())
		{
			buildTree(d, category, index);
		}
	}

	/**
	 * Finds an instance of an object in the tree
	 * @param current Current node in the traversal
	 * @param c Object of type to find
	 * @return Object of given type, or null if not found
	 */
	protected DOMNode findInstanceOf(DOMNode current, Object c)
	{
		if(current == null)
			return null;
		if(current.getContents().getClass().equals(c))
			return current;

		for(DOMNode d : current.getChildren())
		{
			current = findInstanceOf(d, c);
		}

		return current;
	}
	
	protected DOMNode findElementByID(String ID)
	{
		int id = Integer.parseInt(ID.replaceAll("[a-zA-z]", ""));		
		return findElementByID(head, id);
	}
	
	/**
	 * Finds an element with a given ID tag # in the tree
	 * @param current The current node in the traversal
	 * @param ID The object ID being searched for
	 * @return The object with the correct tag, or null if the object wasn't found
	 */
	private DOMNode findElementByID(DOMNode current, int ID)
	{			
		ArrayList<DOMNode> children = current.getChildren();
		
		for(DOMNode domNode : children)
		{
			if(domNode.getIDTag() == ID)
			{
				return domNode;
			}
			current = findElementByID(domNode, ID);
			
			if(current != null)
				if(current.getIDTag() == ID)
					return current;
		}
		
		return null;
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {		
		
		try {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				jtree.getLastSelectedPathComponent();
		Object o = ((DOMNode)node.getUserObject()).getContents();		
		JComponent comp = (JComponent)o;

		GUIUtility.clearAndDraw(comp, Color.RED);
		
		}
		catch(Exception e)
		{
			//don't attempt to highlight
		}
	}

	/**
	 * Returns a string representation of the tree
	 */
	public String toString()
	{
		return (String)this.output(OUTPUT_TYPE.RETURN);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	//updates the tree once the interface is shown and true width/heights can be pulled
	@Override
	public void componentResized(ComponentEvent e) {
		updateTree();
	    jtree.revalidate();
		jtree.repaint();
	}

	//updates the tree once the interface is shown and true width/heights can be pulled
	@Override
	public void componentShown(ComponentEvent e) {				
		updateTree();
	    jtree.revalidate();
		jtree.repaint();
	}
}