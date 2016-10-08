package edu.cmu.old_pact.toolframe;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import edu.cmu.pact.Utilities.trace;

 
public class ToolFrame extends Frame implements ActionListener, ComponentListener,
												WindowListener, PropertyChangeListener{
	String m_FrameID=null;
	
	Object m_Selection=null;
	public ToolBarPanel m_ToolBarPanel;
	MainMenuBar m_MenuBar=null;
	Rectangle m_Placement;
	Rectangle m_CurrentWindowPos;
	boolean m_bToolTip=false;
	ToolTip m_ToolTip=null;
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	//if true, menus for this tool are accessible through the MergedToolMenuBar
	private boolean publishedMenuBar=false;
	
	protected boolean isMovable = true;
	protected boolean isUserAction = true;

	public ToolFrame (String title) {
		super(title);
		m_ToolBarPanel=new ToolBarPanel(ToolBarPanel.VERTICAL);
		m_ToolBarPanel.addActionListener(this);
		
		m_ToolTip=new ToolTip(this);
		addComponentListener(this);	
		addWindowListener(this);
	}
	
	public ToolFrame(String title,boolean publishMenu) {
		this(title);
		publishedMenuBar=publishMenu;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void firePropertyChange(String name, Object oldValue, Object newValue){
		if (changes == null)
			changes = new PropertyChangeSupport (this);
			
		changes.firePropertyChange(name, oldValue, newValue);
	}
	
	public void propertyChange(PropertyChangeEvent evt){
	}
	
	public boolean getIsMovable(){
		return isMovable;
	}
	public void setIsMovable(boolean v){
	   	isMovable = v;
	}
	
	public boolean getIsUserAction(){
		return isUserAction;
	}
	
	public void setIsUserAction(boolean v){
		isUserAction = v;
	}
	
	public void addToolBar(ToolBarPanel ToolBar)
	{
		m_ToolBarPanel.add(ToolBar);
		validate();
	}
	
	public void setToolBarPanel(ToolBarPanel tp){
		m_ToolBarPanel = tp;
		m_ToolBarPanel.addActionListener(this);
		validate();
	}
	
	public void removeToolBarPanel(){
		if(m_ToolBarPanel != null){
			m_ToolBarPanel.removeActionListener(this);
			remove(m_ToolBarPanel);
			m_ToolBarPanel.clear();
			m_ToolBarPanel = null;
		}
		validate();
	} 
	
	public void delete(){
		trace.out (5, "ToolFrame", "delete tool frame");
		
		removeToolBarPanel();
		removeAll();
		changes = null; 
		m_ToolTip = null;
		m_MenuBar = null;
		setVisible(false);
		removeComponentListener(this);
		removeWindowListener(this);
		trace.out (5, "ToolFrame.java", "DEFINITELY NOT calling garbage collector");
//		System.gc();
		trace.out (5, "ToolFrame.java", "NOT disposing this window");
//		dispose();
		trace.out (5, "ToolFrame", "done deleting tool frame");
	}
	
	public void windowOpened(WindowEvent e) { 
	}

    public void windowClosing(WindowEvent e) { }

    public void windowClosed(WindowEvent e) { }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e){ }
    	
    public void windowActivated(WindowEvent e){ 
    }

    public void windowDeactivated(WindowEvent e){ 
    }		
	
	public void sendNoteFontsizeSet(int fontsizeIndex){ }
	
	public void sendTextFieldValue(){ }

	public void refresh()
	{
		Dimension Size=size();
		//resize(Size.width+1,Size.height+1);
		//resize(Size);
		setSize(Size.width+1,Size.height+1);
		setSize(Size);
	}
	// workaround deiconify window in java 1.1
	// doesn't work : removes the menuBar, so I can't restore it.
	/*
	public void setVisible(boolean b){
		if(b){
			//Works for jView:
			Point location = getLocation();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

			if(iconified)
				location = new Point(200, 200);
		
			removeNotify(); // removes menubar!!!
			addNotify();
			setLocation(location);
		}
		super.setVisible(b);
	}
	*/	

	public void savePlacement()
	{
		m_Placement=bounds();
	}
	/*
	synchronized public void reshape(int x,int y,int width,int height)
	{
		super.reshape(x,y,width,height);
		validate();
	}
	*/	
	
	public void restorePlacement()
	{
		Rectangle current = bounds();
		reshape(m_Placement.x, m_Placement.y, current.width, current.height);
	}	

	public boolean isWindowMaximized()
	{
		Dimension screenD=Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screen = new Rectangle(0, 0, screenD.width, screenD.height);
		Rectangle window=bounds();
		
		if (
			((window.x<screen.x) &&	((window.x+window.width)>(screen.x+screen.width))) ||
			((window.y<screen.y) &&	((window.y+window.height)>(screen.y+screen.height)))
		   )
			return true;
		else	
			return false;		
	}			
		
	public void adjustMaximumSize()
	{
		Rectangle bounds=bounds();
		Rectangle mainframe=new Rectangle(0, 0, 1, 1);
		Dimension screenD=Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screen = new Rectangle(0, 0, screenD.width, screenD.height);
		
		int left=Math.max(bounds.x,0);
		int top=Math.max(bounds.y,mainframe.y+mainframe.height);
		int right=Math.min(bounds.x+bounds.width,screen.width);
		int bottom=Math.min(bounds.y+bounds.height,screen.height);
		reshape(left,top,right-left,bottom-top);
	}	
	
	public void showToolTip(ImageButton button)
	{
		Dimension Size=button.size();
		Point Location=button.getLocationInFrame();
		Point mousePosition=button.getMousePosition();
		Location.x+=location().x+mousePosition.x;
		Location.y+=location().y+mousePosition.y;
		if(m_ToolTip != null){
		m_ToolTip.setText(button.getToolTipText());
		//m_ToolTip.move(Location.x,Location.y+Size.height);
			m_ToolTip.setLocation(Location.x,Location.y+Size.height);
			m_ToolTip.setVisible(true);
		}
	}	
			
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if(command.equalsIgnoreCase("Show ToolTip")){
			showToolTip((ImageButton)e.getSource());
		}
		else if (command.equalsIgnoreCase("Hide ToolTip")) {
			m_bToolTip=false;
			repaint();
			if(m_ToolTip !=null)
				m_ToolTip.setVisible(false);
		}
	}
	
	public void setFontSize(int sizeIndex) {}
	
	public int getCurrentFontIndex() {
	  return -1;
	}
	
	public void componentResized(ComponentEvent e){ }

	public void componentMoved(ComponentEvent e) {

		if(getIsUserAction() && !getIsMovable()) 
			restorePlacement();

		savePlacement();
		setIsUserAction(true);
		
	/*
    	if (isWindowMaximized())
			adjustMaximumSize();
	*/
    }
    
    public void componentShown(ComponentEvent e) { }
    
    public void componentHidden(ComponentEvent e){ }
/*		
	public void setMenuBar(MainMenuBar menuBar)
	{
		m_MenuBar=menuBar;
		if (publishedMenuBar)
			MergedToolMenuBar.updateMergedMenus(menuBar,this);
	}
*/	
	public MenuBar getMenuBar()
	{
		//return m_MenuBar;
		return super.getMenuBar();
	}
}
