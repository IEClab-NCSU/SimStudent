//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/DorminToolFrame.java
package edu.cmu.old_pact.dormin.toolframe;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.beanmenu.BeanMenuRegistry;
import edu.cmu.old_pact.beanmenu.DynamicMenu;
import edu.cmu.old_pact.cmu.spreadsheet.CustomTextField;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.toolframe.PreferencesFrame;
import edu.cmu.old_pact.toolframe.ToolBarPanel;
import edu.cmu.old_pact.toolframe.ToolFrame;
import edu.cmu.pact.Utilities.trace;

public abstract class DorminToolFrame extends ToolFrame implements Sharable, FocusListener,
																	PropertyChangeListener,
																	KeyListener{
	private ObjectProxy toolProxy = null;
	private Hashtable Properties;
	private int width = -1;
	private int height = -1;
	private Point location = new Point(-100,-100);
	private MenuBar menuBar = null;
	    //"New property Declaration(Grid Location)"
    private Point gridlocation = new Point(0,0); 
        //"New Property Dec(Num Of X Grids )"
    private int numXgrids = 0;
        //"New Property Dec(Num Of Y Grids )"
    private int numYgrids = 0;
        //"New Property Dec(MinimumSize)"
    private int minimumwidth = 0;
    private int minimumheight = 0;
        //"New Property Dec(Preferred Size )"
    private int preferredwidth = 0;
    private int preferredheight = 0;
	
	private ModeLinePanel modeLinePanel = null;
	private PreferencesFrame prefFrame = null;
  	public int curFontSizeIndex = -1;
  	private int[] fontSizes = {10, 12, 14, 18};
  	
  	private boolean sendSizeChange = false; // when T send msg to Tutor about every size change
  	private boolean initiallyVisible = true;
  	public Object currFocusedObject = null;
  	// used in case the textfield temporary lost focus:
  	// to send or not to send the field value:
  	// not sending it if UserMessageWindow or TeacherOptions is active;
  	private boolean isFocusTravesable = true; 
  	
  	// set it to true if java.version > 1.1.N, that allows to deiconify this window
  	private boolean useNewJava = false;
  	private int delta = 1;
  	private boolean isMac = false;
  	
	public DorminToolFrame(String name) {
		super(name);
		
		setName(name);
		
		Properties = new Hashtable();
		Properties.put("TILEDWINDOW", Boolean.valueOf("false"));
    	Properties.put("USEPREFERREDSIZE", Boolean.valueOf("false"));
		ObjectRegistry.registerObject(name, this);
		setTitle(getName());
		modeLinePanel=new ModeLinePanel();
		
		addKeyListener(this);
		
		addFocusListener(this);
		if(!System.getProperty("java.version").startsWith("1.1"))
			useNewJava = true;
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			isMac = true;
	}
	
	//Only this old code works.
	public boolean keyDown(Event e, int key)  {
    	if(e.id == Event.KEY_ACTION && e.controlDown()) {
  			if(key == 1008) 
				openTeacherWindow();
  		}
  		return super.keyDown(e, key);
  	}
  	
  	public boolean getIsFocusTravesable(){
  		return isFocusTravesable;
  	}
  	
  	public void setIsFocusTravesable(boolean b){
  		isFocusTravesable = b;
  	}
		
    public void openTeacherWindow(){
   		if(toolProxy != null){
			MessageObject mo = new MessageObject("setProperty");
			ObjectProxy thProxy = ObjectProxy.topObjectProxy.getContainedObjectBy("Dialog","Name","TeacherOptions");
			mo.addObjectParameter("OBJECT",thProxy); 
			Vector p_Names= new Vector();
			p_Names.addElement("isVisible");
			Vector p_Values = new Vector();
			p_Values.addElement("true");
			mo.addParameter("PROPERTYNAMES",p_Names);
			mo.addParameter("PROPERTYVALUES",p_Values);	
			toolProxy.send(mo, "Application0");
		
			p_Names.removeAllElements();
			p_Names = null;
			p_Values.removeAllElements();
			p_Values = null;
		}
    }
    
    public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("OPENTEACHERWINDOW")) 
			openTeacherWindow();
		else if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINED")) {
			currFocusedObject = evt.getNewValue();
		}		
	}
	
	public ModeLinePanel getModeLine(){
		return modeLinePanel;
	}
	
	public void processFocusEvent(FocusEvent evt){
    	if(evt.getID() == FocusEvent.FOCUS_GAINED)
    		focusGained(evt);
    	else if(evt.getID() == FocusEvent.FOCUS_LOST)
    		focusLost(evt);
   } 
   
   public void sendTextFieldValue(){
      try {
   		if(currFocusedObject != null &&
   			currFocusedObject instanceof CustomTextField && 
   			((CustomTextField)currFocusedObject).isEditable()){
   			
   			CustomTextField ctf = (CustomTextField)currFocusedObject;
   			if(isMac)
   				ctf.setSendValue(true);
			ctf.sendUserValue("",ctf.getText());
			if(isMac)
   				ctf.setTempoLostFocus(true);
		}
	  }catch (NullPointerException e) {}
	}
   
   	public void focusGained(FocusEvent evt){ 
   		currFocusedObject = this;
   	} 
   	
	public void focusLost(FocusEvent evt){ 
	}

	public void windowIconified(){
		try{
			BeanMenuRegistry.disableMenuItem("Windows", getName());
		} catch (NullPointerException e) {e.printStackTrace();}
	}
	
	public void windowDeiconified(){
		try{
			BeanMenuRegistry.enableMenuItem("Windows", getName());
		} catch (NullPointerException e) {}
	}
	
	public void windowActivated(WindowEvent e){ 
		ObjectRegistry.setActiveWindow(this);
	}
	
	public void setBounds(int x, int y, int w, int h){
		try{
			//trace.out (10, "DorminToolFrame.java", "Set bounds: x = " + x + " y = " + y + 
			//	" width = " + w + " height = " + h);
			super.setBounds(x,y,w,h);
			Properties.put("WIDTH", Integer.valueOf(String.valueOf(w)));
			Properties.put("HEIGHT", Integer.valueOf(String.valueOf(h)));
			Vector loc = new Vector(2);
			loc.addElement(Integer.valueOf(String.valueOf(x)));
			loc.addElement(Integer.valueOf(String.valueOf(y)));
			Properties.put("LOCATION", loc);
		} catch (NullPointerException e) { }
	}
		
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		
		trace.out (10, "DorminToolFrame.java", "Set property: " + propertyName + " " + propertyValue);
		
		Properties.put(propertyName.toUpperCase(), propertyValue);
		try{
		if(propertyName.equalsIgnoreCase("NAME")){
			if(toolProxy != null)
				toolProxy.setName((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("TITLE"))
			setTitle((String)propertyValue);
		else if(propertyName.equalsIgnoreCase("TOFRONT")){
			boolean b = DataConverter.getBooleanValue(propertyName,propertyValue);
			if(b){
				this.setVisible(b);
				this.toFront();
				this.requestFocus();
			}
		}
		else if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+toolProxy.type+"', for Property 'Location' value '"+loc+"' isn't exceptable");  
			int x = ((Integer)loc.elementAt(0)).intValue();
			int y = ((Integer)loc.elementAt(1)).intValue();
			Point p = getLocation();
				if(p.x != x || p.y != y) {
				location.x = x;
				location.y = y;
				setLocation(location);
			}
		}
		else if(propertyName.equalsIgnoreCase("WIDTH")){
			Dimension dim = getSize();
			int w = DataConverter.getIntValue(propertyName,propertyValue);
			if(dim.width != w){	
				trace.out (10, "DorminToolFrame.java", "set width = " + w); 
				setCurrentWidth(w);
				setSize(width, dim.height);
			}
		}
		else if(propertyName.equalsIgnoreCase("HEIGHT")){
			Dimension dim = getSize();
			int h = DataConverter.getIntValue(propertyName,propertyValue);
			if(dim.height != h){	
				trace.out (10, "DorminToolFrame.java", "set height = " + h); 
				setCurrentHeight(h);
				setSize(dim.width,height);
			}
		}
		else if (propertyName.equalsIgnoreCase("ISVISIBLE")){
			boolean b = DataConverter.getBooleanValue(propertyName,propertyValue);
			setVisible(b);
			if(b)
				this.toFront();
		}
		else if (propertyName.equalsIgnoreCase("SENDSIZECHANGE")){
			boolean b = DataConverter.getBooleanValue(propertyName,propertyValue);
			setSendSizeChange(b);
		}		
		else if (propertyName.equalsIgnoreCase("INITIALLYVISIBLE")){
			boolean b = DataConverter.getBooleanValue(propertyName,propertyValue);
			setInitiallyVisible(b);
		}	
		else if (propertyName.equalsIgnoreCase("MODELINE"))
			setModeLine((String)propertyValue);	
		else if(propertyName.equalsIgnoreCase("XGRIDLOCATION") ||
				propertyName.equalsIgnoreCase("YGRIDLOCATION") ||
				propertyName.equalsIgnoreCase("NUMOFXGRIDS")   ||
				propertyName.equalsIgnoreCase("NUMOFYGRIDS")   ||
				propertyName.equalsIgnoreCase("TILEDWINDOW")   ||
				propertyName.equalsIgnoreCase("USEPREFERREDSIZE")   ||
				propertyName.equalsIgnoreCase("FILL")){ 
		}
				
		else if(propertyName.equalsIgnoreCase("MINIMUMWIDTH")){
		}
		else if(propertyName.equalsIgnoreCase("MINIMUMHEIGHT")){
		}
		else if(propertyName.equalsIgnoreCase("PREFERREDWIDTH")){
		}
		else if(propertyName.equalsIgnoreCase("PREFERREDHEIGHT")){
		}
		else if(propertyName.equalsIgnoreCase("RESET")){
		}
		else if(propertyName.equalsIgnoreCase("NAMINGPREFERENCE")){
		}
  		else {
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+toolProxy.type+"'"); 
  		}
  		}catch (DataFormattingException ex){
  			throw getDataFormatException(ex);
  		}
  	}
  	
  	public void setModeLine(String text){
		// if the window has a toolbar then always display modeline 	 		
  		if((text.equalsIgnoreCase("")) && (m_ToolBarPanel == null)){
  			remove(modeLinePanel);
  		}
  		else {
  			add("South",modeLinePanel);
  			
  			modeLinePanel.setModeLineText(text);  		
  			if(m_ToolBarPanel != null){
  				modeLinePanel.setBgColor(m_ToolBarPanel.getBackground());
  			}
		}
		refresh();
  	}
  
   public void refresh(){
		Dimension dim = getSize();
		setSize(dim.width, dim.height+delta);
		delta = (-1)*delta;
	}
  	
  	public void setToolBarPanel(ToolBarPanel tp){
  		super.setToolBarPanel(tp); 		
  	}
  	/////////////////////////////////////////////////////////////////////////////
  	/**
  		set visible
  	*/
  	/////////////////////////////////////////////////////////////////////////////
  	public void setVisible(boolean v){
 		trace.out (10, "DorminToolFrame.java", "set visible = " + v);
  		try{
  			if(v){
  				Dimension dim = getSize();
  				width = dim.width;
  				height = dim.height;
  				location = getLocation();
  			}
  			super.setVisible(v); 
  			if(Properties != null)
  				Properties.put("ISVISIBLE", Boolean.valueOf(String.valueOf(v)));
  		} catch (java.lang.InternalError e) { }
  	}
  	  		
  	/////////////////////////////////////////////////////////////////////////////
  	/**
 
   	*/
  	/////////////////////////////////////////////////////////////////////////////
  	public DataFormatException getDataFormatException(DataFormattingException ex){
  		String st = ex.getMessage()+" for Object of type "+toolProxy.type;
  		DataFormatException exc = new DataFormatException(st); 
  		return exc;
  	}
  	
  	/////////////////////////////////////////////////////////////////////////////
  	/**

  	*/
  	/////////////////////////////////////////////////////////////////////////////
  	public Object getProperty(String key) throws NoSuchPropertyException{
  		Object toret = Properties.get(key.toUpperCase());
  		if (toret != null)
  			return toret;
  		else throw new NoSuchPropertyException("No such property: "+key); 
  	}
  	
  	public Hashtable getAllProperties(){
  		return Properties;
  	}
  	
  	public ObjectProxy getObjectProxy() {
  		return toolProxy;
  	}
  	
	public void setProxyInRealObject(ObjectProxy op) {
	}
	
	public void setToolFrameProxy(ObjectProxy op){
		toolProxy = op;
		modeLinePanel.createProxy(op);
	}
	
	public void delete() {
		removeKeyListener(this);
		if(useNewJava && getState() == Frame.ICONIFIED)
			setState(Frame.NORMAL);
		if(toolProxy != null)
			toolProxy = null;
		Properties.clear();
		Properties = null;
		try{
			BeanMenuRegistry.removeMenuItem("Windows", getName());
			BeanMenuRegistry.removeMenuItem("Tutor", getName());
		} catch (NullPointerException e) {}
		ObjectRegistry.unregisterObject(getName()); 
		clearMenuBar();
		clearModeLinePanel();
		super.delete();
	}
	
	public void keyPressed(KeyEvent e){
		if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 && e.isControlDown()){
    		openTeacherWindow();
    	}
    }
    public void keyReleased(KeyEvent e){ }
    public void keyTyped(KeyEvent e) { }	

	
	public void clearMenuBar(){
		MenuBar m_MenuBar = getMenuBar();
		if(m_MenuBar != null){
			int count = m_MenuBar.getMenuCount();
			if(count == 0) return;
			int i;
			for(i=0; i<count; i++)
				clearMenu(m_MenuBar.getMenu(i));
			m_MenuBar.removeNotify();
		}		
	}
	
	public void clearModeLinePanel(){
		if(modeLinePanel != null){
			remove(modeLinePanel);
			modeLinePanel.clear();
			modeLinePanel = null;
		}
	}
	
	public void clearMenu(Menu menu){
		int s = menu.getItemCount();
		if(s == 0) return;
		MenuItem item;
		for(int i=0; i<s; i++){
			item = menu.getItem(i);
			if(item instanceof DynamicMenu){
				try{
					((DynamicMenu)item).removePropertyChangeListener(this); 
				}catch (NullPointerException e) {}
				((DynamicMenu)item).deleteListeners();
			}
			if(item instanceof Menu)
				clearMenu((Menu)item);
			else 
				item.removeActionListener(this);
		}
	}
	
	public boolean getSendSizeChange(){
		return sendSizeChange;
	}
	
	public void setSendSizeChange(boolean s){
		sendSizeChange = s;
	}
	
	public boolean getInitiallyVisible(){
		return initiallyVisible;
	}
	
	public void setInitiallyVisible(boolean s){
		initiallyVisible = s;
	}
	
	
	public int getCurrentWidth(){
		return width;
	}
	
	public int getCurrentHeight(){
		return height;
	}
	public Point getCurrentLocation(){
		return location;
		
	}
	// New Code here for getting new Properties 
	public Point getGridLocation(){
	     return gridlocation;
	}
	public int getNumXGrids(){
	     return numXgrids;
	}
	public int getNumYGrids(){
	      return numYgrids;
	}
	public int getCurrentPreferredWidth(){
	      return preferredwidth;
	}
	public int getCurrentPreferredHeight(){
	      return preferredheight;
	}
	public int getCurrentMinimumWidth(){
	      return minimumwidth;
	}
	public int getCurrentMinimumHeight(){
	      return minimumheight;
	}
	
	
	public void setCurrentWidth(int w){
		width = w;
	}
	public void setCurrentHeight(int h){	
		height = h;
	}
	public void setCurrentLocation(Point p){
		location = p;
	}
  	//New code here for setting new Properties 
	
  	public void setGridLocation(Point p){
  	          gridlocation = p;
  	}
  	public void setCurrentPreferredWidth(int pw)
  	{
  	     preferredwidth = pw;
  	 }
  	 
	public void setCurrentPreferredHeight(int ph){
	       preferredheight = ph;
	}
	public void setCurrentMinimumWidth(int mw){
	      minimumwidth = mw;
	}
	public void setCurrentMinimumHeight(int mh){
	      minimumheight = mh;
	}
	public void setNumXGrids(int nx){
	      numXgrids = nx;
	}
	public void setNumYGrids(int ny){
	      numYgrids = ny;
	}
	
	
  	public void componentResized(ComponentEvent e){
 		Dimension size = getSize();
		if(Math.abs(width - size.width) > 1 && sendSizeChange) {
			sendNotePropertySet("Width", String.valueOf(size.width));
			width = size.width;
		}
		if(Math.abs(height - size.height) > 1 && sendSizeChange){
			sendNotePropertySet("Height", String.valueOf(size.height));
			height = size.height;
		}
	}
	
	public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
		Hashtable toret = new Hashtable();
		try{
			int s = proNames.size();
			if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
				return Properties;
				String currName;
			for(int i=0; i<s; i++) {
				currName = (String)proNames.elementAt(i);
				toret.put(currName, getProperty(currName));
			}
			return toret;
		} catch (NoSuchPropertyException e){
			throw e;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		
		// "problemstatement" menu item appears as "scenario" on the
		// windows menu, although it is still known as "problemstatement"
		// in all other places (in the registry, as a window name, 
		// in dormin messages, etc.)
		if(command.equalsIgnoreCase("Scenario"))
  		  command = "ProblemStatement";
		
		if(command.equalsIgnoreCase(getName())) 
			performToFront();
		
		else if(command.equalsIgnoreCase("HINT") ||
				command.equalsIgnoreCase("HELP")) 
			   askForHint();
		else if(command.equalsIgnoreCase("DONE")){
				sendTextFieldValue();
			   // hideHintWindow();
			    sendDone();
		}
		else if(command.equalsIgnoreCase("Show ToolTip") ||
				command.equalsIgnoreCase("Hide ToolTip")) 
			   super.actionPerformed(e);
		else if(command.equalsIgnoreCase("Preferences"))
			   showPreferencesWindow();
		else if(command.equalsIgnoreCase("Quit")){
			sendTextFieldValue();
			sendRequest(command);
		}
		else
			sendRequest(command);
	}
	
	public void performToFront(){
		if(useNewJava && getState() == Frame.ICONIFIED)
			setState(Frame.NORMAL);
		this.setVisible(true);
		this.toFront();
		this.requestFocus();
	}
	
	public void showPreferencesWindow(){
		if(prefFrame == null){
			prefFrame = new PreferencesFrame("Set Preferences", this, toolProxy.type);
		}
		else 
		  	prefFrame.setCurrentFontSizeInd(curFontSizeIndex);
		  			
		prefFrame.setVisible(true); 
		prefFrame.toFront();
	}
	
	 public int getCurrentFontIndex() {
  		return curFontSizeIndex;
  	}
  	 	
	public void setCurFontSizeIndex(int ind){ 
		curFontSizeIndex = ind;	
  	}
	 	
  	// return the index of the font size in the fontSizes array
  	//  closest to the new FontSize
  	public int getClosestCurFontSizeIndex(int newFontSize, int[] fontSizes){
  		int ind=0;
		for(int i=0; i<fontSizes.length; i++){
		   	if(fontSizes[i] == newFontSize) { 	
  			 	return i;
  			 }
  			 if(fontSizes[i] < newFontSize) 
  			 	ind = i; 
  		}
  		return ind;	
  	}
  	
	public void askForHint() {
		sendTextFieldValue();
		if(toolProxy != null){
			MessageObject mo = new MessageObject("getHint");
			mo.addParameter("Object",toolProxy); 
			toolProxy.send(mo);
		}
	}
	
	public void sendRequest(String request){
		MessageObject mo = new MessageObject("ActionRequest");
		mo.addParameter("Object", toolProxy);
		mo.addParameter("Action", request);
		toolProxy.send(mo);
	}
	
	public void addField(Component com){
	}
	
	public void sendDone() {
		if(toolProxy != null){
			MessageObject mo = new MessageObject("Done");
			mo.addParameter("OBJECT",toolProxy);
			toolProxy.send(mo);
		}
	}	
	
	public void hideHintWindow(){	
	  ToolFrame hintWin = 
				(ToolFrame)ObjectRegistry.getObject("UserMessageWindow");
	  if(hintWin != null)
	    hintWin.setVisible(false);
	}
		
/*
    public void componentMoved(ComponentEvent e) {
    	super.componentMoved(e);
    	sendLocationChanged();
    }
    
    public void componentShown(ComponentEvent e) {
    	sendLocationChanged(); 
    }
  */  
    public void componentHidden(ComponentEvent e){ }
    
    public boolean updateSizeAndLocation(String myName){
     // change window size and location, if different from those
     // stored in ObjectRegistry from the previous problem
     	boolean firstShow = false;
     	
    	Dimension d = ObjectRegistry.getWindowSize(myName);
    	Point loc = ObjectRegistry.getWindowLocation(myName);
    	
    	if((d != null) &&
    	   (d != getSize()))
    	  setSize(d);
    	  
    	if((loc != null) &&
    	   (loc != getLocation()))
    	  setLocation(loc);
    	
    	if(d == null || loc == null)
    		firstShow = true;
    		
    	return firstShow;
    }
    
    protected void sendLocationChanged(){
    	// work around Iconify/Deiconify for jView, java 1.1
    	
    	Point locP = getLocation();
    	
    	if(	location.x != locP.x || location.y != locP.y){
    		Vector locV = new Vector(2);
    		locV.addElement(Integer.valueOf(String.valueOf(locP.x)));
    		locV.addElement(Integer.valueOf(String.valueOf(locP.y)));
    		sendNotePropertySet("Location", locV);
    		location = new Point(locP.x, locP.y);
    		locV = null;
    	}
    }
    
    public void sendNotePropertySet(String propertyName, Object propertyValue){
    	if(toolProxy != null && toolProxy.getTarget() != null){
    		MessageObject mo = new MessageObject("NotePropertySet");
    		Vector propertyNames = new Vector();
    		Vector propertyValues = new Vector();
    		propertyNames.addElement(propertyName);
    		propertyValues.addElement(propertyValue);
    		mo.addParameter("OBJECT", toolProxy);
    		mo.addParameter("PROPERTYNAMES", propertyNames);
    		mo.addParameter("PROPERTYVALUES", propertyValues);
    		toolProxy.send(mo);
    		propertyNames = null;
    		propertyValues.removeAllElements();
    		propertyValues = null;
    		mo = null;
    	}
    }
    	
	 public void sendNoteFontsizeSet(int fontsizeIndex){
    	if(toolProxy != null && toolProxy.getTarget() != null){
    		MessageObject mo = new MessageObject("NotePropertySet");
    		Vector propertyNames = new Vector();
    		Vector propertyValues = new Vector();
    		propertyNames.addElement("FONTSIZE");
    		propertyValues.addElement(Integer.valueOf(String.valueOf(fontSizes[fontsizeIndex])));
    		mo.addParameter("OBJECT", toolProxy);
    		mo.addParameter("PROPERTYNAMES", propertyNames);
    		mo.addParameter("PROPERTYVALUES", propertyValues);
    		toolProxy.send(mo);
    		propertyNames = null;
    		propertyValues.removeAllElements();
    		propertyValues = null;
    		mo = null;
    	}
    }
    
    public boolean canSendTextFieldValue(){
    	return false;
    }
}
