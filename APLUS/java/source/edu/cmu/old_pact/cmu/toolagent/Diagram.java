//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/Diagram.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.doublebufferedpanel.BackgroundImagePanel;
import edu.cmu.old_pact.doublebufferedpanel.DoubleBufferedPanel;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.htmlPanel.AdjustableHtmlPanel;
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.scrollpanel.LightComponentScroller;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;


public class Diagram extends DorminToolFrame {

	private DiagramProxy  dProxy;
	private BackgroundImagePanel backImagePanel;
	private AdjustableHtmlPanel htmlPanel = null;
	private DoubleBufferedPanel dbp;
	protected LinkVector links;
	private LightComponentScroller m_ScrollPanel;
	Panel centerPanel;
	private String urlBase = null;
	
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public Diagram(){
		super("Diagram");
		links = new LinkVector();
		links.addPropertyChangeListener(this);
		setBackground(Settings.diagramBackground);
		setLayout(new BorderLayout());
		
		dbp = new DoubleBufferedPanel();
		backImagePanel = new BackgroundImagePanel();
		addPropertyChangeListener(backImagePanel);
		dbp.add(backImagePanel);
		backImagePanel.addKeyListener(this);
		dbp.addKeyListener(this);
		
		centerPanel = new Panel();
		centerPanel.setLayout(new GridBagLayout());
		GridbagCon.viewset(centerPanel,dbp,0,1,1,1,0,0,0,0);
		m_ScrollPanel = new LightComponentScroller(centerPanel);
		m_ScrollPanel.setScrollbarWidth(17);
		add("Center", m_ScrollPanel);
		setupToolBar(m_ToolBarPanel);
		add("West",m_ToolBarPanel);
		
		setModeLine("");		
		MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());
		setMenuBar(menuBar);
		pack();
		
		setCurrentWidth(370);
		setCurrentHeight(350);
		setSize(370, 350);
		updateSizeAndLocation("Diagram");
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public ObjectProxy getObjectProxy() {
		return dProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		dProxy = (DiagramProxy)op;
		setToolFrameProxy(dProxy);
	}
	
    public void addObject(Object p){
    	if(p instanceof AdjustableHtmlPanel){
    		htmlPanel = (AdjustableHtmlPanel)p;
    		htmlPanel.setImageBase(urlBase);
    		htmlPanel.setWidth(350);
    		htmlPanel.setAdjustment(AdjustableHtmlPanel.ADJUST_BOTH);
    		GridbagCon.viewset(centerPanel,htmlPanel,0,0,1,1,0,0,0,0);
    		validate();
    	}
    }
    
    public void componentResized(ComponentEvent e){
		super.componentResized(e);
		redraw();
	}		
	
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b)
		  redraw();
	}
		
	/**
	* HtmlPanel should always be visible, so it should use the ScrollPanel size instead of size of its real
	* parent centerPanel. 
	* This is not so obvious in temrs of java LayoutManager.
	**/
	public void redraw(){
		if(htmlPanel != null){
			Dimension dim = m_ScrollPanel.getSize();
			Dimension html_dim = htmlPanel.preferredSize();
			htmlPanel.setHtmlWidth(dim.width-20);
			htmlPanel.setWidth(dim.width-20);
			htmlPanel.setHeight(htmlPanel.getHtmlHeight());
			htmlPanel.layout();
			
			validate();
			htmlPanel.repaint();
		}
	}
	
	public void delete(){
		this.removePropertyChangeListener(backImagePanel);
		Component[] comps = backImagePanel.getComponents();
		int s = comps.length;
		for(int i=0; i<s; i++){
			if(comps[i] instanceof SingleTextField) 
				((SingleTextField)comps[i]).removePropertyChangeListener(links);
		}
		dbp.removeKeyListener(this);
		dbp.removeAll();
		backImagePanel.removeKeyListener(this);
		backImagePanel.delete();
		remove(m_ScrollPanel);
		m_ScrollPanel.removeAll();
		m_ScrollPanel = null;
		backImagePanel = null;
		dbp = null;
		links.removePropertyChangeListener(this);
		links.delete();
		links = null;
		changes = null;
		super.delete();
		dProxy = null;
	}
	
	public void askForHint() {
		if(!links.currAskedForHelp())
			super.askForHint();
		//else
			//links.focusCurrentCell();
	}
	
	public void addField(Component com){
		backImagePanel.add(com);
		validate();
		if(com instanceof SingleTextField){
			SingleTextField stf = (SingleTextField)com;
//			links.addVecticalLink(stf);
//			links.addHorisontalLink(stf);
			stf.addPropertyChangeListener(links);
		}
	}
		
	private void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.diagramToolBarColor);
	
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
		tb.addToolBarImage(Settings.diagramLabel,Settings.diagramLabelSize);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
			if(	propertyName.equalsIgnoreCase("DIRECTORY"))
				changes.firePropertyChange(propertyName, "",(String)propertyValue); 
			else if(propertyName.equalsIgnoreCase("DIAGRAMNAME")){
				changes.firePropertyChange(propertyName, "",(String)propertyValue);  
			}
			else if(propertyName.equalsIgnoreCase("URLBASE")) {
				urlBase = (String)propertyValue;
				if(htmlPanel != null)
					htmlPanel.setImageBase(urlBase);
			}
			else 
				super.setProperty(propertyName, propertyValue);
		} catch(NoSuchPropertyException e) {
			throw new NoSuchPropertyException("Diagram : "+e.getMessage());
		}
	}
}