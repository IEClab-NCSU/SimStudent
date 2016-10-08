package edu.cmu.old_pact.toolframe;


import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.uiwidgets.StackLayout;
import edu.cmu.old_pact.scrollpanel.BevelPanel;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.pact.Utilities.trace;

public class ToolBarPanel extends BevelPanel {

	public final static int HORIZONTAL=0;
	public final static int VERTICAL=1;
	
	private Vector m_ButtonList;
	private Hashtable m_ButtonHash;
	
	int m_Orientation;
	Insets m_Insets=null;
	Dimension m_ButtonSize;
	Dimension m_ImageSize;
	Dimension m_SeparatorSize;
	private Vector actionListeners;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public ToolBarPanel(int orientation)
	{
		super();
		setFont(new Font("Helvetica",0,9));
	//setStyle(BevelPanel.RAISED);
		m_ButtonList=new Vector();
		trace.out (10, "toolframe.ToolBarPanel.java", " constructor");
		m_ButtonHash=new Hashtable();
		
		if (orientation==HORIZONTAL) {
			setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
			m_Insets=new Insets(4,8,4,8);
		} else {
			setLayout(new StackLayout(0));
			m_Insets=new Insets(8,4,8,4);
		}
			
		m_ButtonSize=new Dimension(20,20);
		m_ImageSize = new Dimension(32,32);
		m_SeparatorSize=new Dimension(8,8);
		
		m_Orientation=orientation;
		actionListeners = new Vector();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
		add an action listener to this object	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void addActionListener(ActionListener al){
		trace.out (10, "toolframe.ToolBarPanel", "adding action listener: " + al);
		actionListeners.addElement(al);
		trace.out (10, "toolframe.ToolBarPanel", "action listeners = " + actionListeners);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
		remove an action listener from this object	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void removeActionListener(ActionListener al){
		trace.out (10, "toolframe.ToolBarPanel", "removing action listener: " + al);
		actionListeners.removeElement(al);
		trace.out (10, "toolframe.ToolBarPanel", "action listeners = " + actionListeners);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void clear(){
		trace.out (10, "ToolBarPanel", "clear");
		
		int s = m_ButtonList.size();
		for(int i=0; i<s; i++)
			((ImageButton)m_ButtonList.elementAt(i)).clear();
		removeAll();
		actionListeners.removeAllElements();
		m_ButtonList.removeAllElements();
		trace.out (10, "toolframe.ToolBarPanel", "removing all action listeners. m_ButtonList = "
			+ m_ButtonList);
		m_ButtonHash.clear();
		actionListeners = null;
		//m_ButtonList = null;
		//m_ButtonHash = null;
		
	}
	

	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public ImageButton addButton(String ImageName,String buttonName,boolean showTip, int panelPos)
	{
		trace.out (10, "toolframe.ToolBarPanel", "add button: button name = " + buttonName + " position = " + panelPos);
		//return null;
		
 		trace.out (10, "toolframe.ToolBarPanel", "x");

		ImageButton button = new ImageButton(ImageName,buttonName, showTip);

		trace.out (10, "toolframe.ToolBarPanel", "button = " + button);

		if (actionListeners == null) {
		 	trace.out (10, this, "XXXXX NO ACTION LISTENER: ADDING PAD FRAME");
		 	actionListeners = new Vector();
		 	
		}
		
		trace.out (10, "toolframe.ToolBarPanel", "actionListeners = " + actionListeners + " size = " + 
			actionListeners.size());
		int s;
		
		if (actionListeners != null) {
			s = actionListeners.size();

			
		} else {
			trace.out (10, "toolframe.ToolBarPanel", "action listeners = null  !!!!!!!!!!!!!!!!!!!!");
			s = 0;
		}			
		
		for(int i=0; i<s; i++)
			button.addActionListener((ActionListener)actionListeners.elementAt(i));
		//trace.out (10, "toolframe.ToolBarPanel", "actionListeners = " + actionListeners);
			
		trace.out (10, "toolframe.ToolBarPanel", "1");

		button.setSize(m_ButtonSize);
		trace.out (10, "toolframe.ToolBarPanel", "2");
		button.setBackground(getBackground());
		trace.out (10, "toolframe.ToolBarPanel", "3 -- m_ButtonList = " + m_ButtonList);
		m_ButtonList.addElement(button);
		trace.out (10, "toolframe.ToolBarPanel", "4");
		m_ButtonHash.put(buttonName,button);
		
		trace.out (10, "toolframe.ToolBarPanel", "5");
		
		//create a panel to center the button -- assume toolbar width = logo width
		Panel buttonPanel = containInPanel(button,Settings.panelWidth);
		trace.out (10, "toolframe.ToolBarPanel", "6");
		if(panelPos == -1) {
			trace.out (10, "toolframe.ToolBarPanel", "7");

			add(buttonPanel);
			trace.out (10, "toolframe.ToolBarPanel", "8");
		} else {
			trace.out (10, "toolframe.ToolBarPanel", "9: buttonPanel = " + buttonPanel + " panel position = " + panelPos);
			int numComp = getComponentCount();
			trace.out (10, "toolframe.ToolBarPanel", "num of components = " + numComp);
			/*
			if (numComp == 0) {
				trace.out (10, "toolframe.ToolBarPanel", "removing all components");
				removeAll();
				add(buttonPanel, getComponentCount());	
			} else 
			*/
			//	add(buttonPanel, panelPos);	
				add(buttonPanel, 0);	
			trace.out (10, "toolframe.ToolBarPanel", "10");
		}

		trace.out (10, "toolframe.ToolBarPanel", "11");

		return button;
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public ImageButton addButton(String ImageName,String buttonName,boolean showTip) {
		return addButton(ImageName,buttonName,showTip,-1);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean setButtonState(Container container, String buttonName, boolean press){
		Component[] comps = container.getComponents();
		int s = comps.length;
		if(s == 0) return false;
		ImageButton button = null;
		boolean found = false;
		// find a button on the panel
		for(int i=0; i<s; i++){
			if(comps[i] instanceof Panel) {
				found = setButtonState((Container)comps[i], buttonName, press);
				if(found) return true;
			}
			else if((comps[i].getName()).equalsIgnoreCase(buttonName) && 
				comps[i] instanceof ImageButton){
				button = (ImageButton)comps[i];
				found = true;
				break;
			}
		}
		if(button != null){
			button.setPressed(press);
		}
		return found;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////	
	public void addRealButton(Button button){
		int s = actionListeners.size();
		for(int i=0; i<s; i++)
			button.addActionListener((ActionListener)actionListeners.elementAt(i));
			
		//button.resize(m_ButtonSize);
		button.setSize(m_ButtonSize);
		button.setBackground(getBackground());
		
		//create a panel to center the button -- assume toolbar width = logo width
		Panel buttonPanel = containInPanel(button,Settings.panelWidth);
		add(buttonPanel);
	}
	
	public void addSeparator()
	{
		ToolBarSeparator separator=new ToolBarSeparator();
		//separator.resize(m_SeparatorSize);
		separator.setSize(m_SeparatorSize);
		add(separator);
	}
	
	public void addToolBarImage(String imagefile) {
		addToolBarImage(imagefile,m_ImageSize,false);
	}
	
	public void addToolBarImage(String imagefile,boolean clickable) {
		addToolBarImage(imagefile,m_ImageSize,clickable);
	}

	public void addToolBarImage(String imagefile,Dimension size,boolean clickable) {
		ToolBarImage image;
		//if (clickable)
			//image = new ClickableToolBarImage(imagefile);
		//else
			image = new ToolBarImage(imagefile);
		//image.resize(size);
		image.setSize(size);
		Panel imagePanel = containInPanel(image,Settings.clLogoSize.width);
		add(imagePanel);
	}

	public void addToolBarImage(String imagefile,Dimension size) {
		addToolBarImage(imagefile,size,false);
	}

	public ImageButton getButton(int index)
	{
		return (ImageButton)m_ButtonList.elementAt(index);
	}
	
	public ImageButton getButton(String buttonName)
	{
		return (ImageButton)m_ButtonHash.get(buttonName);
	}
	
	public void paint(Graphics g){
		Dimension dim = getSize();
		if(dim.width != Settings.panelWidth)
			setSize(Settings.panelWidth, dim.height);
		super.paint(g);
	}
		
		
	
	public Insets insets()
	{
		return m_Insets;
	}	
	
	public void setInsets(Insets insets)
	{
		m_Insets=insets;			
	}	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void setButtonSize(Dimension ButtonSize)
	{
		m_ButtonSize=ButtonSize;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void setSeparatorSize(Dimension SeparatorSize)
	{
		m_SeparatorSize=SeparatorSize;
	}
	
	//containInPanel puts the toolBar item in a panel, so that we can center it
	//within the toolbar
	private Panel containInPanel(Component contents,int toolBarWidth) {
		Panel containPanel = new Panel();
		//int widthGap = (toolBarWidth-contents.getSize().width)/2;
		//containPanel.setLayout(new FlowLayout(FlowLayout.CENTER,widthGap,0));
		containPanel.setLayout(new FlowLayout());
		containPanel.add(contents);
		return containPanel;
	}
	

}
