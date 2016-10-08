//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/Glossary.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.scrollpanel.BevelPanel;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;



public class Glossary extends DorminToolFrame implements Sharable {
	
	GlossaryProxy gProxy;
	private String[] language = {"English"};
	private Label resultLabel;
	private SearchTextField searchField;
	private SearchList resultList;
	private HtmlPanel imageDisplay;
	private int left_off = 18;
	private int right_off = 18;
	private int top_off = 10;
	private int bottom_off = 10;
	private Panel forButtons;
	private Panel centerPanel;
	private boolean sendButtonIncluded = false;
	private Button sendButton;
	private int minWidth = 360;
	private int minHeight = 500;
  	private int[] fontSizes = {10, 12, 14, 18};
	
	Font labelFont; 
	Font buttonFont; 
	private String myName = "Glossary";
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public Glossary(){
		super("Glossary");
		curFontSizeIndex = 1;  // defaults to "normal"
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			labelFont = new Font("geneva", Font.BOLD, 12);
			buttonFont = new Font("geneva", Font.PLAIN, 12); 
		}
		else {
			labelFont = new Font("arial", Font.BOLD, 12);
			buttonFont = new Font("arial", Font.PLAIN, 12);		
		} 
			
		setBackground(Settings.glossaryBackground);
		setLayout(new BorderLayout());
		centerPanel = new Panel();
		centerPanel.setLayout(new GridBagLayout());
		add("Center", centerPanel);
		Label searchLabel = new Label("Search for");
		searchLabel.setFont(labelFont);
		GridbagCon.viewset(centerPanel,searchLabel, 0, 0, 1, 1, top_off, left_off, 0 ,0);
		
		searchField = new SearchTextField(16);
		searchField.addKeyListener(this);
		searchField.setBackground(Color.white);
		GridbagCon.viewset(centerPanel,searchField, 1,0,1,1,top_off,0,0,right_off);
		
		resultLabel = new Label("No items to show");
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			resultLabel.setFont(new Font("geneva", Font.PLAIN, 10));
		else
			resultLabel.setFont(new Font("arial", Font.PLAIN, 10));
			
		GridbagCon.viewset(centerPanel,resultLabel, 1, 1, 1, 1, 0, 0, 0 ,0);
		
		resultList = new SearchList(6);
		searchField.addPropertyChangeListener(resultList);
		GridbagCon.viewset(centerPanel,resultList, 0, 2, 2, 1, top_off, left_off, 0 ,right_off);
		
		resultList.addPropertyChangeListener(this);
		resultList.addKeyListener(this);
		
		imageDisplay = new HtmlPanel(250, 180, true);
		//imageDisplay.setCanDeleteDoc(false);
		resultList.addPropertyChangeListener(imageDisplay);
		Container imageP = borderedHtmlPanel(imageDisplay);
		imageDisplay.setHtmlViewerParent(imageP);
//		GridbagCon.viewset(centerPanel,imageP, 0, 3, 2, 1, top_off, left_off, 0 ,right_off,1,1,1); 
		
		forButtons = new Panel();
		forButtons.setLayout(new FlowLayout());
		sendButton = new Button("Send");
		sendButton.setFont(buttonFont);
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendReason();
			}
		});
		forButtons.add(new Label("     "));
		
		Button showAllButton = new Button("Show All");
		showAllButton.setFont(buttonFont);
		showAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAllItems();
			}
		});
		forButtons.add(showAllButton);
		GridbagCon.viewset(centerPanel,forButtons,0,4,2,1,top_off,left_off,bottom_off,right_off);
		
		setupToolBar(m_ToolBarPanel);
		add("West",m_ToolBarPanel);
		
		setModeLine("");
		getAllProperties().put("INCLUDESENDBUTTON", new Boolean(false));
		
		MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());
		setMenuBar(menuBar);
		
		this.addPropertyChangeListener(resultList);
		this.addPropertyChangeListener(imageDisplay);
		
		pack();
		setSize(400, 570);
		
		// update font size if different from the stored or global value
		setFontSize(ObjectRegistry.getWindowFontSize(myName));
		updateSizeAndLocation(myName);
	}
	
	public void requestFocus(){
		super.requestFocus();
		searchField.requestFocus();
	}
	
	public void setFontSize(int sizeIndex) {
  		if(sizeIndex != curFontSizeIndex) {
  			curFontSizeIndex = sizeIndex;
  			imageDisplay.setFontSize(fontSizes[sizeIndex]);
  		}
  	}
  	  	
	private void includeSendButton(boolean include){
		if(include){
			if(!sendButtonIncluded) {
				forButtons.add(sendButton,0);
				validate(); 
			}
		}
		else{
			if(sendButtonIncluded){
				forButtons.remove(sendButton);
				validate();
			}
		}
		sendButtonIncluded = include;
	}
	
	private void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.glossaryToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
		tb.addToolBarImage(Settings.glossaryLabel,Settings.glossaryLabelSize);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
		
	protected void showAllItems(){
		searchField.setText("");
	}
	
	private Container borderedHtmlPanel(Panel p) {
		BevelPanel bp = new BevelPanel();
		bp.setStyle(BevelPanel.LOWERED);
		bp.setBackground(Color.white);
		bp.setLayout(new FlowLayout(0,0,0));
		bp.add(p);
		return bp;
    }
    
    private void sendReason(){
    	String reason = resultList.getSelectedItem();
    	if(reason != null){
    		MessageObject mo = new MessageObject("NotePropertySet");
			mo.addParameter("Object",gProxy);
			mo.addParameter("PROPERTY", "REASON");
			mo.addParameter("VALUE", reason);
			gProxy.send(mo);
		}
	}
/*	
	public void setCurrentWidth(int w){
		w = Math.max(w,minWidth);
		super.setCurrentWidth(w);
	}
	
	public void setCurrentHeight(int h){
		h = Math.max(h,minHeight);
		super.setCurrentHeight(h); 
	}

	public Dimension preferredSize(){
		return new Dimension(minWidth, minHeight);
	}
*/
	public ObjectProxy getObjectProxy() {
		return gProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		gProxy = (GlossaryProxy)op;
		setToolFrameProxy(gProxy);
	}
	
	public void delete(){
		searchField.removePropertyChangeListener(resultList);
		searchField.removeKeyListener(this);
		resultList.removePropertyChangeListener(imageDisplay);
		resultList.removePropertyChangeListener(this);
		this.removePropertyChangeListener(resultList);
		this.removePropertyChangeListener(imageDisplay);
		searchField.delete();
		searchField = null;
		resultList.removeKeyListener(this);
		resultList.delete();
		resultList = null;
		changes = null;
		imageDisplay.removeAll();
		forButtons.removeAll();
		centerPanel.removeAll();
		forButtons = null;
		imageDisplay = null;
		centerPanel = null;
		sendButton = null;
		super.delete();
		gProxy = null;
	}
/*	
	public void keyPressed(KeyEvent e){
		if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1 && e.isControlDown())
    		openTeacherWindow();
    }
    public void keyReleased(KeyEvent e){ }
    public void keyTyped(KeyEvent e) { }	
	
*/	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
		if(	propertyName.equalsIgnoreCase("DIRECTORY")){
			changes.firePropertyChange(propertyName, "",(String)propertyValue); 
		}
		else if(propertyName.equalsIgnoreCase("URLBASE")){
			changes.firePropertyChange(propertyName, "",(String)propertyValue); 
		}
		else if(propertyName.equalsIgnoreCase("ITEMNAMES") ||
				propertyName.equalsIgnoreCase("FILENAMES")) {
			changes.firePropertyChange(propertyName, (new Vector()),DataConverter.getListValue(propertyName,propertyValue));  
		}
		else if(propertyName.equalsIgnoreCase("SEARCHWORD")) {
			searchField.setText((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("RESET")) {
				imageDisplay.displayHtml("");
				resultList.deselect(resultList.getSelectedIndex());
		}
		else if(propertyName.equalsIgnoreCase("INCLUDESENDBUTTON")) {
			includeSendButton(DataConverter.getBooleanValue(propertyName,propertyValue));
		}
		else 
			super.setProperty(propertyName, propertyValue);
		} catch(NoSuchPropertyException e) {
			throw new NoSuchPropertyException("Glossary : "+e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		}
		
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("RESULTLABEL")) {
			resultLabel.setText((String)evt.getNewValue());
		}
		else
			super.propertyChange(evt);
	}
	
	public void redraw(){
		imageDisplay.setHtmlSize(imageDisplay.getParent().getSize());
		imageDisplay.layoutHtmlViewer();
		invalidate();
		validate();
	}

	public void componentResized(ComponentEvent e){
		super.componentResized(e);
		redraw();
	}
}	
		
		
		
		
		
	