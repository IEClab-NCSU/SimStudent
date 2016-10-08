package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;



//Invidividual Document Object Model
public class DOMNode extends DefaultMutableTreeNode implements TreeNode
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * The object that this node contains
	 */
	private Object contents;
	
	/**
	 * The children of this DOMNode
	 */
	private ArrayList<DOMNode> children;
	
	/**
	 * The parent of this DOMNode
	 */
	private DOMNode parent;
	
	/**
	 * String representing the dimensions of the payload in an x0, y0, x1, y1 format
	 */
	private String dimensionString;
	
	/**
	 * Tag for the object to help differentiate it among other objects
	 */
	private int IDTag = -1;
	
	public int getIDTag() {
		return IDTag;
	}

	public void setIDTag(int iDTag) {
		IDTag = iDTag;
	}
	
	protected String get3DigitIDString()
	{
		String str = "000" + getIDTag();
		return str.substring(str.length() - 3);
	}
	
	/**
	 * Array for storing the constant parts of the dimensions, and to make sure updates occur if a width/height of 0 are given
	 */
	private int[] dimensions = new int[4];
	
	public String getDimensions()
	{
		
		if(dimensions == null || dimensionString == null || dimensions[2] == 0 || dimensions[3] == 0)
		{
			Component c = (Component)getContents();
			
			dimensions[0] = c.getX();
			dimensions[1] = c.getY();
			dimensions[2] = c.getWidth();
			dimensions[3] = c.getHeight();
			
			dimensionString = "(" + Integer.toString(dimensions[0]) + ", " + Integer.toString(dimensions[1]) + ", " + Integer.toString(dimensions[0] + dimensions[2]) + ", " + Integer.toString(dimensions[1] + dimensions[3]) + ")";

			//dimensionString = "(" + Integer.toString(c.getX()) + ", " + Integer.toString(c.getY()) + ", " + Integer.toString(/*c.getX() + */c.getWidth()) + ", " + Integer.toString(/*c.getY() +*/ c.getHeight()) + ")";
		}
		
		return dimensionString;
	}

	public Object getContents() {
		return contents;
	}
	protected void setContents(Object contents) {
		this.contents = contents;
	}
	protected ArrayList<DOMNode> getChildren() {
		return children;
	}
	protected void setChildren(ArrayList<DOMNode> arrayList) {
		this.children = arrayList;
	}
	
	protected void setParent(DOMNode parent) {
		this.parent = parent;
	}

	// ------ Constructors ------ //
	
	protected DOMNode(Object contents, ArrayList<DOMNode> children, DOMNode parent)
	{
		this.contents = contents;
		this.children = children;
		this.parent = parent;
	}

	protected DOMNode(Object contents, DOMNode parent)
	{
		this.contents = contents;
		this.children = new ArrayList<DOMNode>();
		this.parent = parent;
	}
	
	protected DOMNode(DOMNode d)
	{
		super(d);
		IDTag = d.getIDTag();
	}
	
	// ------ Adding Children ------ //
	
	/**
	 * Adds an already created DOMNode to this node
	 * @param d DOMNode to add
	 */
	protected void addChildren(DOMNode d)
	{
		this.children.add(d);
		d.setParent(this);
	}
	
	/**
	 * Adds any object to the children of this DOMNode
	 * @param child Object to package up into a DOMNode and add to this DOMNode's children
	 */
	protected void addChildren(Object child)
	{	
		this.children.add(new DOMNode(child, this));
	}

	/**
	 * Adds a set of childrent to this DOMNode
	 * @param children Child objects to package up into a DOMNode and add to this DOMNode's children
	 */
	protected void addChildren(ArrayList<Object> children)
	{	
		for(Object o : children)
		{
			this.children.add(new DOMNode(o, new ArrayList<DOMNode>(), this));
		}
	}
	
	/**
	 * Removes a DOMNode from the children of this DOMNode
	 * @param d DOMNode to remove
	 */
	protected void remove(DOMNode d)
	{
		for(int x = 0; x < children.size(); x++)
		{
			if(children.get(x).equals(d))
			{
				children.remove(x);
				break;
			}
		}
	}

	/**
	 * Compares the contents of this DOMNode to a given object
	 * @param toFind Object to compare against
	 * @return True if the objects are the same, false otherwise
	 */
	protected boolean contains(Object toFind)
	{
		return contents.equals(toFind);
	}

	/**
	 * Outputs the contents of this DOMNode
	 */
	public String toString()
	{				
		try 
		{
			String ret = contents.toString();
			
			Pattern p = Pattern.compile("\\w+\\[");
			Matcher m = p.matcher(ret);
			
			m.find();
			ret = ret.substring(m.start(), m.end() - 1);
						
			return ret + get3DigitIDString() + " " + getDimensions();
		} 
		catch(IllegalStateException e)
		{
			return contents.toString();
			//e.printStackTrace();
		}
	}

	//for debug purposes
	protected String verboseToString()
	{
		return (super.toString() + " : " + contents + ", Parent: " + parent);
	}

	//begin TreeNode methods
	@Override
	public Enumeration<DOMNode> children() {
		return Collections.enumeration(children);
	}
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	@Override
	public DOMNode getChildAt(int arg0) {
		return children.get(arg0);
	}
	@Override
	public int getChildCount() {
		return children.size();
	}
	@Override
	public int getIndex(TreeNode arg0) {

		for(int x = 0; x < children.size(); x++)
		{
			if(children.get(x).equals(arg0))
				return x;
		}

		return -1;
	}
	@Override
	public DOMNode getParent() {
		return parent;
	}
	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}


}
