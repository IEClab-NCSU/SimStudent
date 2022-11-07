package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.messageInterface.UserMessageWindow;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.menu.DorminListeningMenu;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.scrollpanel.LightComponentScroller;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;
import edu.cmu.pact.Utilities.trace;

// here SkillsManager class is connected to WS;

// Properties:
//-------------------------------------------
//		NAME(String)			TYPE(Object)
//-------------------------------------------
//		numOfColumnsMatched		int
//		numOfColumns			int
//		numOfColumnsMatched		int

public class WorkSheet extends DorminToolFrame{

	WorksheetProxy ws_obj = null;
	private String codeBase; //not in use
	SpreadsheetPanel sp;
	LightComponentScroller m_ScrollPanel;
	private int delta = 1;
	private MenuBar menuBar;
	private Menu worksheetMenu, editMenu; 
	private boolean firstShow = true;
	private Gridable currentCell;

	// four actual font sizes to correspond to 
  	// "small, normal, big, bigger" respectively in the preferences setting
  	private int[] fontSizes ={10, 12, 14, 18}; // {10, 11, 12, 14};
  	
	public WorkSheet(int numOfRows, int numOfCols, WorksheetProxy ws_obj) {
		this(numOfRows, numOfCols, ws_obj, "Worksheet");
	}
	
	public WorkSheet(int numOfRows, int numOfCols, 
					WorksheetProxy ws_obj, String myName) {
		super(myName);
		trace.out (5, this, "creating works sheet " + myName);
		setCurrentWidth(500);
		setCurrentHeight(350);
		curFontSizeIndex = 1;  // defaults to "normal"

		//resize(500, 350);
		//move (5, 300);
		
		setLayout(new BorderLayout());	 
		setBackground(Color.white);
		try{
			setProperty("numberOfColumns",Integer.valueOf(String.valueOf(numOfCols)));
			setProperty("numberOfRows", Integer.valueOf(String.valueOf(numOfRows)));
			setProperty("Name", "");
		} catch(DorminException e) { }
			
			//setProxyInRealObject(ws_obj);
		// use -1 for numOfRows, numOfCols, because in Geom Worksheet headers are included into the count.
		CellMatrix cellMatrix = new CellMatrix(numOfRows-1, numOfCols-1);
		sp = createSpreadsheetPanel(cellMatrix, ws_obj);
		sp.addKeyListener(this);
		
		//addKeyListener(this);
		Panel centerPanel = new Panel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add("North",(new Label("   ")));
		centerPanel.add("Center", sp);

		m_ScrollPanel=new LightComponentScroller(centerPanel);		
		m_ScrollPanel.setScrollbarWidth(17);
		
		setupToolBar(m_ToolBarPanel);
		add("Center",m_ScrollPanel);
		setModeLine("Spreadsheet Calculation ON");
		
//		setSize(500, 350);
		pack();
		
		// update font size if different from the stored or global value
		setFontSize(ObjectRegistry.getWindowFontSize(myName));
		firstShow = updateSizeAndLocation(myName);
	}
	
	public void setWorksheetMenuBar(){
		menuBar = MenuFactory.getGeneralMenuBar(this, getName(), true);
		// It's known that the editMenu is #1 in the menuBar.
		editMenu = menuBar.getMenu(1);
		worksheetMenu = getWorksheetMenu();
			// add worksheetMenu to the menu bar if its not empty
		if(worksheetMenu.getItemCount() > 0)
			menuBar.add(worksheetMenu);
			
		setMenuBar(menuBar);
	}
	
	public Dimension preferredSize(){
		if(m_ToolBarPanel == null && sp == null)
			return super.preferredSize();
		/*
		Dimension d = m_ToolBarPanel.preferredSize();
		Dimension sp_d = sp.preferredSize();
		return new Dimension (d.width+sp_d.width + 120, sp_d.height+170);
		*/
		Dimension tb_size = m_ToolBarPanel.getSize();
		Dimension sp_size = sp.preferredSize();
		int tb_h = tb_size.height;
		int sp_h = sp_size.height+100;
		//if(tb_h < sp_h)
		//	tb_h = sp_h;
		tb_h = Math.max(tb_h,sp_h);
		tb_h = Math.max(tb_h,225);
		
		return new Dimension (sp_size.width+14+tb_size.width, tb_h);
	}
/*	
	public void keyPressed(KeyEvent e){
		if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 && e.isControlDown())
    		openTeacherWindow();
    }
    public void keyReleased(KeyEvent e){ }
    public void keyTyped(KeyEvent e) { }
*/	
	public SpreadsheetPanel createSpreadsheetPanel(CellMatrix cellMatrix, ObjectProxy obj){
		return  new SpreadsheetPanel(cellMatrix, this, obj);
	}
	
	public Menu getWorksheetMenu(){

		DorminListeningMenu wMenu = new DorminListeningMenu(getName(),ws_obj,this);		
		String[] legalActions = new String[]{"Add Row","Add Column","Delete Row", "Delete Column"};
		wMenu.setLegalActions(legalActions);
		return wMenu;
	}
	
	//setupToolBar adds the buttons and images to the toolbar
	public void setupToolBar(ToolBarPanel tb) {
		add("West",m_ToolBarPanel);
		tb.setBackground(Settings.ssToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
		tb.addToolBarImage(Settings.wsLabel,Settings.wsLabelSize);
	}
  /* 
   	public void focusGained(FocusEvent evt){
  trace.out("WS focusGained");
   		try{
   		sp.requestFocus();
   		} catch (NullPointerException e) { }
   	}


	//doen't work with two WS!!

	public void windowActivated(WindowEvent evt){
		super.windowActivated(evt);
			try{
   				sp.requestFocus();
   			} catch (NullPointerException e) { }
   	}
  */
  	public void requestFocus(){
  		super.requestFocus();
  		try{
   			sp.requestFocus();
   		} catch (NullPointerException e) { }
   	}
   	
  			
   	public void windowOpened(WindowEvent evt){
   		super.windowOpened(evt);
   		try{
   			sp.requestFocus();
   		} catch (NullPointerException e) { }
   	}
   	
   	public void windowDeiconified(WindowEvent evt){
   		super.windowDeiconified(evt);
   		try{
   			sp.requestFocus();
   		} catch (NullPointerException e) { }
   	}
   	
	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("COMPONENTRESIZED")){
			Dimension curSize = getSize();
			setSize( curSize.width+delta, curSize.height+delta);
			delta = (-1)*delta;
		}
		
		else if(evt.getPropertyName().equalsIgnoreCase("GOTFOCUS")) {
			Focusable fs = sp.findFocusedGrid();
			boolean editableStr = fs.isEditable();
			setEditMenuOptions(editableStr);
			boolean canRemoveCol = false;
			boolean canRemoveRow = false;
			if((fs instanceof HeaderGrid) && editableStr){
				int[] pos = fs.getPosition();
				if(pos[0] == -1)
					canRemoveCol = true;
				else if(pos[1] == -1)
					canRemoveRow = true;
			}
			setWorksheetMenuOption("Delete Column",canRemoveCol);
			setWorksheetMenuOption("Delete Row",canRemoveRow);
		}
		
		else super.propertyChange(evt);
	}
	
	private void setEditMenuOptions(boolean editableStr){
	/**
	* String[] commands = new String[]{"CUT","COPY","PASTE"};
	**/
		if(editMenu == null)
			return;
		MenuItem cutItem = editMenu.getItem(0);
		cutItem.setEnabled( editableStr );	
		MenuItem pasteItem = editMenu.getItem(2);
		pasteItem.setEnabled( editableStr );	
	}
	
	private void setWorksheetMenuOption(String label,boolean canRemove){
		if(worksheetMenu == null)
			return;
		int s = worksheetMenu.getItemCount();
		MenuItem targetItem;
		for(int i=0; i<s; i++){
			targetItem = worksheetMenu.getItem(i);
			if((targetItem.getActionCommand()).equalsIgnoreCase(label)){
				targetItem.setEnabled( canRemove );
				break;
			}
		}
	}	
		
	public ObjectProxy getObjectProxy() {
		return ws_obj;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		ws_obj = (WorksheetProxy)op;
		setToolFrameProxy(ws_obj);
	}
	
	public synchronized void delete(){
		trace.out (5, this, "REALLY delete this worksheet");
		//removePropertyChangeListener((PropertyChangeListener)fm);
		sp.clearSpreadsheet(false);
		sp.removeKeyListener(this);
		//removeKeyListener(this);
		sp = null;
		remove(m_ScrollPanel);
		m_ScrollPanel.removeAll();
		m_ScrollPanel = null;
		trace.out (5, this, " delete the super class of this work sheet");
		super.delete();
		ws_obj = null;
		trace.out (5, this, " done deleting work sheet");
	}
	
	public void getHint(String description) {	
		MessageObject mo = new MessageObject("getHint");
		mo.addParameter("Selection", description);
		ws_obj.send(mo);
	}

	public String createCellName(int r, int c) {
		String toret = "R"+String.valueOf(r)+"C"+String.valueOf(c);
		return toret;
	}				

	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
			if(propertyName.equalsIgnoreCase("FONTSIZE")){
				int newFontSize = DataConverter.getIntValue(propertyName,propertyValue);
				sp.setFontSize(newFontSize);
				setCurFontSizeIndex(getClosestCurFontSizeIndex(newFontSize,fontSizes)); 
			}
			else if(propertyName.equalsIgnoreCase("FIRSTSHOW"))
				setFirstShow( DataConverter.getBooleanValue(propertyName,propertyValue));
			else if(propertyName.equalsIgnoreCase("NUMBEROFROWS") ||
				propertyName.equalsIgnoreCase("NUMBEROFCOLUMNS") ){
			}
			else if(propertyName.equalsIgnoreCase("PROBLEMNAME"))
				setTitle(getName()+" for Problem "+(String)propertyValue); 
			else if(propertyName.equalsIgnoreCase("NAME") && ws_obj != null){
				ws_obj.setName(propertyValue.toString());
				//setTitle(propertyValue.toString());
			}
			else if(propertyName.equalsIgnoreCase("FILEDIR"))
				codeBase = (String)propertyValue;
			else if(propertyName.equalsIgnoreCase("ROWADDITIONLOCATION")) {
				try{
					sp.setRowAdditionLocation((String) propertyValue);
				}catch (NoSuchFieldException e){
					throw new InvalidPropertyValueException("Object 'Worksheet' :"+e.getMessage()+"property 'RowAdditionLocation'");
				}
			}
			else if(propertyName.equalsIgnoreCase("COLUMNADDITIONLOCATION")){
				try{
					sp.setColAdditionLocation((String) propertyValue);
				}catch (NoSuchFieldException e){
					throw new InvalidPropertyValueException("Object 'Worksheet' :"+e.getMessage()+"property 'ColumnAdditionLocation'");
				}
			}
			else
				super.setProperty(propertyName, propertyValue);
		} catch (NoSuchPropertyException e){
			throw new NoSuchPropertyException("Worksheet : "+e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		} catch (NoSuchFieldException exc){
			throw new NoSuchPropertyException("Worksheet : "+exc.getMessage());
		}
	}
	
	public void setFontSize(int sizeIndex) {
  		curFontSizeIndex = sizeIndex; 	
  		sp.setFontSize(fontSizes[curFontSizeIndex]);
  	}
  	
  	public void updateFontSize() {
  		sp.setFontSize(fontSizes[curFontSizeIndex]);
  	}
  	  		
	public void actionPerformed(ActionEvent e){
	
		Focusable cell = sp.findFocusedGrid();
		String command = e.getActionCommand();
		if(command.equalsIgnoreCase("CUT"))
			sp.cut();
		else if(command.equalsIgnoreCase("COPY"))
			sp.copy();
		
		else if(command.equalsIgnoreCase("PASTE"))
			sp.paste();
		else if(command.equalsIgnoreCase("HINT") || 
				command.equalsIgnoreCase("HELP")) {
			// added !cell.getGridable().isEditable() upon Chris request
			if(cell == null || !cell.getGridable().isEditable())
				askForHint();
			else{
				sendTextFieldValue();
				sp.askForHint();
			}
		}				
		else if(command.equalsIgnoreCase("DELETE COLUMN")){
			closeUserMessageWindow();
			sp.removeColumn();
		}
		else if(command.equalsIgnoreCase("ADD COLUMN")){
			closeUserMessageWindow();
			sp.addNewCol();
			sp.setFontSize(fontSizes[curFontSizeIndex]);

		}
		else if(command.equalsIgnoreCase("DELETE ROW")){
			closeUserMessageWindow();
			sp.removeRow();			
		}
		else if(command.equalsIgnoreCase("ADD ROW")){
			//sp.addNewRow();
			sp.writeToCell();
			sendActionRequest(command);
		}
		else
			super.actionPerformed(e);
	}
	
	public void closeUserMessageWindow() {
		UserMessageWindow userMessageWindow = (UserMessageWindow)ObjectRegistry.getObject("UserMessageWindow");
		userMessageWindow.closeWindow();
	}
	
	public void setFirstShow(boolean b){
		firstShow = b;
	}
	
	public void setVisible(boolean b){
	
		setSize(preferredSize());
		if(firstShow)
			firstShow = false;
		
		super.setVisible(b);
		if(b){
			this.toFront();
			this.requestFocus();
		}
	}
		
	protected SpreadsheetPanel getSpreadsheetPanel(){
		return sp;
	} 
	
	protected void sendActionRequest(String command){
		Focusable fc = sp.findFocusedGrid();
		if(fc instanceof DorminGridElement){
			MessageObject mo = new MessageObject("ActionRequest");
			mo.addParameter("Action", command);
			Vector pNames = new Vector();
			pNames.addElement("currentSelection");
			Vector pValues = new Vector();
			String desc = (((DorminGridElement)fc).getObjectProxy()).getStrDescription();
			pValues.addElement(desc);
			mo.addParameter("PropertyNames", pNames);
			mo.addParameter("PropertyValues", pValues);
			mo.addParameter("Object", ws_obj);
			ws_obj.send(mo);
		}
	}
		
}
		  
		
			
		
		

		
		
		
		
	