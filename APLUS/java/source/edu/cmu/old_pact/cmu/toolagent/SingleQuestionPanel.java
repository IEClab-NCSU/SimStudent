//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SingleQuestionPanel.java
package edu.cmu.old_pact.cmu.toolagent;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.cmu.spreadsheet.CellMatrix;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.htmlPanel.AdjustableHtmlPanel;
import edu.cmu.old_pact.toolframe.Hintable;


public class SingleQuestionPanel extends Panel implements Sharable,Hintable, PropertyChangeListener{
	ReasonQuestionProxy rqProxy;
	private AdjustableHtmlPanel questionTextPanel;
	private TablePanel tablePanel = null;
	private PropertyChangeListener frameListener;
	private Hashtable Properties = new Hashtable();
	private int delta = 1;
	
	public SingleQuestionPanel(int w){
		super();
		setLayout(new GridBagLayout());
		questionTextPanel = new AdjustableHtmlPanel(w, 100, AdjustableHtmlPanel.ADJUST_NO);
		questionTextPanel.setTopMargin(2);
		//GridbagCon.viewset(this,questionTextPanel, 0, 0, 1, 1, 10, 0, 0 ,0,1);
	}
	
	public ObjectProxy getObjectProxy() {
		return rqProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		rqProxy = (ReasonQuestionProxy)op;
	}
	
	public void requestFocus(){
		tablePanel.requestFocus();
	}

	public int getNumOfRows(){
		if(tablePanel != null)
			return tablePanel.getNumOfRows();
		return -1;
	}
	
	public int getNumOfCols(){
		if(tablePanel != null)
			return tablePanel.getNumOfCols();
		return -1;
	}
	
	public void setFrameListener(PropertyChangeListener l){
		frameListener = l;
	}
	
	public void addRow(){
		if(tablePanel == null){
			CellMatrix cm = new CellMatrix(1, 4);
			tablePanel = new TablePanel(cm, frameListener, rqProxy);
			Panel panel = new Panel();
			panel.setLayout(new FlowLayout(0,5,0));
			panel.add(tablePanel);
			GridbagCon.viewset(this,panel, 0, 1, 1, 1, 0, 0, 15 ,0);
		}
		else {
			synchronized (tablePanel){
				tablePanel.addNewRow();
				tablePanel.sendResizedEvent();
			}
		}		
		//setSize(preferredSize());
		//validate();
		
		Frame frame = getFrame();
		Dimension dim = frame.size();
		frame.setSize(dim.width, dim.height+delta);
		delta = (-1)*delta;
		
	}
	
	private Frame getFrame() {
		Component parent = getParent();
		Component root = null;		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}
	
	public boolean asksForHint(){
		return tablePanel.asksForHint();
	}
	
	public void delete(){
		if(questionTextPanel != null)
			questionTextPanel.removeAll();
		if(tablePanel != null)
			tablePanel.clearSpreadsheet(false);
		if(getParent() != null)
			getParent().remove(this);
		if(Properties != null)
			Properties.clear();
		Properties = null;
		removeAll();
		questionTextPanel = null;
		tablePanel = null;
		rqProxy = null;
	}
	
	public void setImageBase(String b){
		questionTextPanel.setImageBase(b);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws NoSuchPropertyException{
		if(propertyName.equalsIgnoreCase("STATEMENT")){
			displayHtml((String)propertyValue);
			Properties.put(propertyName.toUpperCase(), propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("HIGHLIGHT")) {
			Properties.put(propertyName.toUpperCase(), propertyValue);
			Vector tags;
			if(	propertyValue.toString().equalsIgnoreCase("FALSE") ||
				propertyValue.toString().equals("[0, 0]")) // empty tags
				tags = new Vector();
			else
				tags = (Vector)propertyValue;
			questionTextPanel.showtxt(tags);			
			adjustHtmlHeight();	
			
			((QuestionPanel)getParent()).scrollToQuestion(this);	
		}
		else {
			throw new NoSuchPropertyException("ReasonQuestion: No such property: "+propertyName);
		}
	}
	
	public void displayHtml(String text){
		synchronized (questionTextPanel){
			questionTextPanel.displayHtml(text);
		}
		adjustHtmlHeight();
	}
	
	public void adjustHtmlHeight(){
		int h = questionTextPanel.getHtmlHeight();
		questionTextPanel.setHeight(h);
		questionTextPanel.layout();
		validate();				
	}
	
	
	public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
 		int s = proNames.size();
 		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
 			return Properties;
 		Hashtable toret = new Hashtable();
 		String currName;
 		for(int i=0; i<s; i++){
 			currName = ((String)proNames.elementAt(i)).toUpperCase();
 			Object ob = Properties.get(currName);
 			if(ob == null)
 				throw new NoSuchPropertyException("ReasonQuestion doesn't have property "+currName);
 			toret.put(currName, ob);
 		}
 		return toret;
 	}

	public void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("Width")){
			int w = ((Integer)evt.getNewValue()).intValue();
			questionTextPanel.setWidth(w);
			//((ReasonFrame)getFrame()).setCommonWidth(w);
 
			adjustHtmlHeight();
			setSize(preferredSize());
			validate();
		}
		else if(eventName.equalsIgnoreCase("FONTSIZE")){
			int s = ((Integer)evt.getNewValue()).intValue();
			setFontSize(s);
		}
	}
 	
 	public void setFontSize(int s){
 		questionTextPanel.setFontSize(s);
 		// use smaller font for questions
		if(tablePanel != null) {
		  int questFont = s;
		  if(s >= 18) questFont = s-4;
		  else if(s >= 14) questFont = s-2;
		  else if (s > 10) questFont = s-1; 
		  tablePanel.setFontSize(questFont);
		}
		adjustHtmlHeight();
 	}
 }
	