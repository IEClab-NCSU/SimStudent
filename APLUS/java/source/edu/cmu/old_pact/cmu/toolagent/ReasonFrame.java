package edu.cmu.old_pact.cmu.toolagent;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.spreadsheet.CustomTextField;
import edu.cmu.old_pact.cmu.uiwidgets.LinePanel;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.htmlPanel.AdjustableHtmlPanel;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.scrollpanel.BevelPanel;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;

public class ReasonFrame extends DorminToolFrame{
	
	ReasonProxy rProxy;
	private AdjustableHtmlPanel statementPanel;		
	private Panel forHtml;
	private Panel forQuestions;
	private Container questionContainer;
	private Container statementView; //Component statementView;
	private TableViewer questionViewer;
	private QuestionPanel questionPanel;
	private int commonWidth = 300;
	private int width = 10;
	private CustomTextField currCell;

	private int ht = 620;
	private int wt = 500;
	
	Font labelFont; 
	private String myName = "ReasonTool";

	// four actual font sizes to correspond to 
  	// "small, normal, big, bigger" respectively in the preferences setting
  	private int[] fontSizes = {10, 12, 14, 18};
  		
	public ReasonFrame(){
		super("ReasonTool");
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			labelFont = new Font("geneva", Font.BOLD, 12);
		else
			labelFont = new Font("arial", Font.BOLD, 12);
		setTitle("Reason Tool");
		setBackground(Settings.reasonBackground);
		setLayout(new BorderLayout());
		Panel centerPanel = new Panel();
		centerPanel.setLayout(new GridLayout(2,1,2,2));
		add("Center", centerPanel);
		curFontSizeIndex = 1;  // defaults to "normal"
		
		statementPanel = new AdjustableHtmlPanel(commonWidth, 150,AdjustableHtmlPanel.ADJUST_NO);	
		statementPanel.addPropertyChangeListener(this);		
		statementView = borderedPanel(statementPanel);
		
		forHtml = new Panel();
		forHtml.setLayout(new FlowLayout());
		forHtml.add(statementView);
		centerPanel.add(forHtml);
				
		questionPanel = new QuestionPanel();
		addPropertyChangeListener(questionPanel);
		questionViewer = new TableViewer(questionPanel);
		questionViewer.setScrollbarWidth(17);
		questionViewer.setSize(commonWidth+20, 250);
		questionContainer = borderedPanel(questionViewer);
		
		forQuestions = new Panel();
		forQuestions.setLayout(new FlowLayout());
		forQuestions.add(questionContainer);
		centerPanel.add(forQuestions);
				
		setupToolBar(m_ToolBarPanel);
		add("West",m_ToolBarPanel);
		setModeLine("");
		
    	MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());
		setMenuBar(menuBar);
		
		// MOVED TO SETVISIBLE
		// update font size if different from the stored or global value
		//setFontSize(ObjectRegistry.getWindowFontSize(myName));
		
		pack();
		width = getSize().width;
		ht = (Toolkit.getDefaultToolkit()).getScreenSize().height - 40;
		setSize(width,ht);	
		
		updateSizeAndLocation(myName);	    	
	}
	

	public int getCommonWidth(){
		return commonWidth;
	}
	
	public void setCommonWidth(int w){
		commonWidth = w;
	}
	
	protected String getImageBase(){
		try{
			return getProperty("URLBASE").toString();
		} catch (NoSuchPropertyException e) { 
			return null;
		}
	}
	
	private void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.reasonToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
		tb.addToolBarImage(Settings.statementLabel,Settings.statementLabelSize);
	}

	public int getFontSize(){
		return fontSizes[curFontSizeIndex];
	}
	
	public void setFontSize(int sizeIndex) {
  		if(sizeIndex != curFontSizeIndex) {
  			curFontSizeIndex = sizeIndex;
  			statementPanel.setFontSize(fontSizes[sizeIndex]);
  			firePropertyChange("FONTSIZE", "", Integer.valueOf(String.valueOf(fontSizes[sizeIndex])));
  		}
  	}
  	  		
	public void askForHint() {
		if(!questionPanel.asksForHint())
			super.askForHint();
		else 
			resetFocus();
	}
	
	private Container borderedPanel(Container p) {
		BevelPanel bp = new BevelPanel();
		bp.setStyle(BevelPanel.LOWERED); 
		bp.setLayout(new FlowLayout()); 
		bp.setBackground(Color.white);
		bp.add(p);
		return bp;
    }
	
	public ObjectProxy getObjectProxy() {
		return rProxy;
	}
	
	public Dimension preferredSize(){
  		Dimension d = super.preferredSize();
		return new Dimension(d.width, d.height);
	}
	
	
	public void setProxyInRealObject(ObjectProxy op) {
		rProxy = (ReasonProxy)op;
		setToolFrameProxy(rProxy);
	}
	
	public void delete(){
		synchronized(this){
			setVisible(false);
		}
		statementPanel.removePropertyChangeListener(this);		
		statementPanel.removeAll();
		forHtml.removeAll();
		forQuestions.removeAll();
		removePropertyChangeListener(questionPanel);	
		questionPanel.removeAll();
		statementPanel = null;
		questionPanel = null;
		statementView = null;
		forQuestions = null;
		forHtml = null;
		currCell = null;
		super.delete();
		rProxy = null;
	}
	
	public void removeAll(){
		questionViewer.removeAll();
		questionViewer = null;
		super.removeAll();
		// never ever call it directly!
//		removeNotify();
	}
	
	public void addQuestion(Panel q_panel){
		questionPanel.addComponent(q_panel);
		Color[] colorSet = {Color.lightGray, Color.black};
		LinePanel lp = new LinePanel(1, colorSet);
		questionPanel.addComponent(lp);
		redraw();
	} 
	
/*
	public void windowActivated(){
		resetFocus();
	}
*/	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
		if(propertyName.equalsIgnoreCase("STATEMENT")){
			statementPanel.displayHtml((String)propertyValue);
				// calling setFontSize fixes the problem when only half of the
				// last statement line is visible
			statementPanel.setFontSize(fontSizes[curFontSizeIndex]);
		}
		else if(propertyName.equalsIgnoreCase("HIGHLIGHT")) {
			Vector tags;
			if(	propertyValue.toString().equalsIgnoreCase("FALSE") ||
				propertyValue.toString().equals("[0, 0]")) // empty tags
				tags = new Vector();
			else
				tags = DataConverter.getListValue(propertyName,propertyValue);
			statementPanel.showtxt(tags);
		}
		
		else if (propertyName.equalsIgnoreCase("URLBASE"))
			statementPanel.setImageBase(propertyValue.toString());
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			int newFontSize = DataConverter.getIntValue(propertyName,propertyValue);
			statementPanel.setFontSize(newFontSize);
			setCurFontSizeIndex(getClosestCurFontSizeIndex(newFontSize,fontSizes));
			firePropertyChange("FONTSIZE", "", Integer.valueOf(String.valueOf(fontSizes[curFontSizeIndex])));
		}
		else 
			super.setProperty(propertyName, propertyValue);
			
		} catch (NoSuchPropertyException e){
			throw new NoSuchPropertyException("ReasonTool : "+ e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		}
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(evt.getPropertyName().equalsIgnoreCase("COMPONENTRESIZED")){
			redraw();
		}
		else if(evt.getPropertyName().equalsIgnoreCase("HEIGHT") ||
				evt.getPropertyName().equalsIgnoreCase("WIDTH")){
			//adjustStatementView();
			redraw();
		}
		if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINED"))
			currCell = (CustomTextField)evt.getNewValue();
		else
			super.propertyChange(evt);
	}

	public void componentResized(ComponentEvent event){
		Dimension size = getSize();
		int curHeight = getCurrentHeight();
		if(Math.abs(curHeight-size.height) > 1.0 ||
			Math.abs(width-size.width) > 1.0) {
			width = size.width;
			super.componentResized(event);
			redraw();
		}
	}
	
	public void requestFocus(){
		if(currCell!= null)
			currCell.requestFocus();
		else
			statementPanel.requestFocus();
	}	
	
	public void resetFocus(){
		if(currCell!= null)
			currCell.requestFocus();
	}

	public void setVisible(boolean b){
	 	if(b)
		  setFontSize(ObjectRegistry.getWindowFontSize(myName));
		super.setVisible(b);
	}
	
	
	public void redraw(){
		Dimension dim = forHtml.getSize();   //statementPanel.getParent().getSize();
		Dimension d = new Dimension(dim.width-20, dim.height-20);
		statementPanel.setHtmlSize(d);
		statementPanel.layoutHtmlViewer();
		
		Dimension dimQ = forQuestions.getSize();
		questionViewer.setSize(dimQ.width-20, dimQ.height-20);
		questionPanel.setSize(dimQ.width-20, dimQ.height-30);
		resetFocus();
		invalidate();
		validate();
	}

}	
		
		
		
		
		
	