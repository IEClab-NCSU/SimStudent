package edu.cmu.old_pact.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.spreadsheet.OrderedTextField;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.foldertabpanel.FolderTabPanel;
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.scrollpanel.LightComponentScroller;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.old_pact.settings.Settings;

public class WizardFrame extends DorminToolFrame implements ItemListener, KeyListener{
	private ObjectProxy f_proxy;
	public LinkVector links;
	private NullLayoutPanel centerPanel;
	LightComponentScroller m_ScrollPanel;
	private int delta = 1;
	private boolean canResetToolBar = true;
	// it's used in a GrapherSetUpFrame
	public Object currFocusedObject = null;
	private static final String FONT_CHANGED = "font changed";
		// four actual font sizes to correspond to 
  		// "small, normal, big, bigger" respectively in the preferences setting
  	private int[] fontSizes = {10, 12, 14, 18};	
	
	public WizardFrame(ObjectProxy f_proxy){
		super("Frame");
		
		curFontSizeIndex = 1;  // defaults to "normal"
		setBackground(Color.white);
		this.f_proxy = f_proxy;
		setToolFrameProxy(f_proxy);
		links = new LinkVector();
		setLayout(new BorderLayout());
				
		centerPanel = new NullLayoutPanel();
		centerPanel.setLayout(null);
		m_ScrollPanel=new LightComponentScroller(centerPanel);
		add("Center", m_ScrollPanel);
		
		add("West",m_ToolBarPanel);
		
		pack();
		setCurrentWidth(350);
		setCurrentHeight(300);
		setSize(350, 300);
	}
	
	public void delete(){
		f_proxy = null;
		links.delete();
		links = null;
		
		centerPanel.removeAll();
		removeAll();
		centerPanel = null;
		remove(m_ScrollPanel);
		m_ScrollPanel.removeAll();
		m_ScrollPanel = null;
		super.delete();
	}
	
	public void setCanResetToolBar(boolean c){
		canResetToolBar = c;
	}

	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		Hashtable Properties = getAllProperties();
		Properties.put(propertyName.toUpperCase(), propertyValue);
		try{
			if(propertyName.equalsIgnoreCase("NAME"))
				setName((String)propertyValue);
			else if (propertyName.equalsIgnoreCase("FONT")) {
				Font f = ParameterSettings.getFont( propertyValue);
				if(f != null){
					if(f.getSize() == 0)
		  				f =new Font(f.getName(), f.getStyle(), getFont().getSize());
					setFont(f);
				}
			}
			else if(propertyName.equalsIgnoreCase("FONTSTYLE")){
				int intStyle = ParameterSettings.getFontStyle((String)propertyValue);
				Font f = getFont();
				if(f != null) {				
					Font newF = new Font(f.getName(), intStyle, f.getSize());
					setFont(newF);
				}
			}
			else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
				try {
					int newSize = DataConverter.getIntValue(propertyName, propertyValue);
					setWizardFontSize(newSize);
					setCurFontSizeIndex(getClosestCurFontSizeIndex(newSize,fontSizes)); 
				} catch(DataFormattingException ex){
  					String st = ex.getMessage()+" for Object of type "+f_proxy.type;
  					throw new DataFormatException(st);
  				}
			}
			else
				super.setProperty(propertyName, propertyValue);
		} catch(NoSuchPropertyException e) {
			throw new NoSuchPropertyException("WizardFrame : "+e.getMessage());
		} 
		Properties = null;
	}
	
	public void keyTyped(KeyEvent e){ }
	
    public void keyReleased(KeyEvent e){ }
	
	public void keyPressed(KeyEvent evt){
		if(evt.isActionKey() && evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_F1 )
			openTeacherWindow();
	}
	
	public void setName(String name){
		super.setName(name);
		setTitle(name);
		// if not from constructor
		// for some reason it triples the "Help" button for the GraphingSetup tool, 
		// so set canResetToolBar to false for GraphingSetup tool
		if(f_proxy != null && canResetToolBar){
			f_proxy.setName(name);
			f_proxy.defaultNameDescription();
			ObjectRegistry.unregisterObject("Frame");
			ObjectRegistry.registerObject(name, this);
			MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());
			setMenuBar(menuBar);
			resetToolBar(name);
		}
	}
	
	public void focusGained(FocusEvent evt){
		currFocusedObject = this;
		super.focusGained(evt);
	}
	

	/**
	* ToolBar gets its properties from Settings
	* It needs some changes in Settings library
	**/
	public void resetToolBar(String name){
		m_ToolBarPanel.setBackground(Settings.factoringToolBarColor);
		m_ToolBarPanel.setInsets(new Insets(0,0,0,0));
		m_ToolBarPanel.addSeparator();
		m_ToolBarPanel.addButton(Settings.help,"Help", false);
		m_ToolBarPanel.addSeparator();
		//tb.addToolBarImage(Settings.factoringLabel,Settings.factoringLabelSize);
	}
	
	public void addObject(Object comp){
		if(comp instanceof DorminChoice )
			((DorminChoice)comp).addPropertyChangeListener(this);
			
		else if(comp instanceof OrderedTextField )
			((OrderedTextField)comp).addPropertyChangeListener(this);
			
		else if(comp instanceof DorminPanel )
			((DorminPanel)comp).addPropertyChangeListener(this);
		
		centerPanel.addObject(comp);
		
		if(comp instanceof OrderedTextField){
			OrderedTextField otf = (OrderedTextField)comp;
			links.addVecticalLink(otf);
			links.addHorisontalLink(otf);
			otf.addPropertyChangeListener(links);
			otf.addPropertyChangeListener(this);
		}
		else if(!(comp instanceof FolderTabPanel) && (comp instanceof Component))
			((Component)comp).addKeyListener(this);
		refresh();
	}
	

	public void refresh(){
		Dimension dim = getSize();
		setSize(dim.width+delta, dim.height);
		delta = (-1)*delta;
	// Steve M. thinks this might work too, and might not require components to be layed out again.
	//	setVisible(false);
	//	setVisible(true);
	}
	
	public void askForHint() {
		if(currFocusedObject == null || currFocusedObject == this)
			super.askForHint();
		else{
			if(currFocusedObject instanceof OrderedTextField && 
				((OrderedTextField)currFocusedObject).isEditable()){
				((OrderedTextField)currFocusedObject).askHint();
				return;
			}
			else if(currFocusedObject instanceof CanAskForHelp){
				if(((CanAskForHelp)currFocusedObject).askedForHelp())
					return;
			}
			super.askForHint();
		}
	}
	
	public void componentResized(ComponentEvent e){
		super.componentResized(e);
		if ( centerPanel != null)
			centerPanel.repaint();
	}

	public void setFont(Font f){
	
		if(f.getSize() == 0)
		  f =new Font(f.getName(), f.getStyle(), getFont().getSize());
		  
		super.setFont(f);
		Component[] children;		
		children = centerPanel.getComponents();
		
		for(int i=0;i<children.length;i++) {
			children[i].setFont(f);
			children[i].invalidate();
			children[i].validate();
		}
		
		centerPanel.repaint();
		centerPanel.setVisible(false);
		centerPanel.setVisible(true);	
	}

	// this method is called from the Preferences window
	public void setFontSize(int sizeIndex) {
  		if(sizeIndex != curFontSizeIndex) {
  			curFontSizeIndex = sizeIndex;
  			setWizardFontSize(fontSizes[sizeIndex]);
  		}
  	}
	
	private void setWizardFontSize(int size) {
		Integer fS = new Integer(size);
		setContainerProperty(this,"FONTSIZE", fS);
		fS = null;
		refresh();
	} 
	
	protected void setContainerProperty(Container con, String pName, Object pValue){
		Component[] children = con.getComponents();
		int s = children.length;
		if(s==0) return;
	
		for(int i=0;i<s;i++) {
		  	if(children[i] instanceof Sharable){
		  		try{
					((Sharable)children[i]).setProperty(pName, pValue);
				} 
				catch (DorminException e) { }
		  	}
		   	else if(pName.equalsIgnoreCase("FONTSIZE")){
				Font currentFont = children[i].getFont();
				Font f = new Font(currentFont.getName(), currentFont.getStyle(), ((Integer)pValue).intValue());				
				children[i].setFont(f);
			}
				
			if(children[i] instanceof Container) 	
				setContainerProperty((Container)children[i], pName, pValue);
		}
	}
	
	

// This method should be in its own inner member class.
// However, there is a bug in the Java 1.1 compiler that prevents
// inner classes from accessing private methods of their enclosing
// classes correctly. This should be changed if we move to 1.2
	public void itemStateChanged(ItemEvent e) {
		if (f_proxy != null) {
			int fontSize = (Integer.valueOf((String)e.getItem())).intValue();
			setWizardFontSize(fontSize);
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT", f_proxy);
			mo.addParameter("PROPERTY", "FONTSIZE");
			mo.addParameter("FONTSIZE",	fontSize);
			f_proxy.send(mo);
		}
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("REFRESH")) 
			refresh();
		else if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINED")) {
			currFocusedObject = evt.getNewValue();
			super.propertyChange(evt);
		}
		else
			super.propertyChange(evt);
	}
}