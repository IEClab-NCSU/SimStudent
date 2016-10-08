package edu.cmu.old_pact.cl.tools.problemstatement;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MenuBar;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;
public class ProblemStatement extends DorminToolFrame {
  	
  	StatementProxy pv_obj; 

  	HtmlPanel htmlViewer;	
  	// four actual font sizes to correspond to 
  	// "small, normal, big, bigger" respectively in the preferences setting
  	private int[] fontSizes = {10, 12, 14, 18};
  	private int width = 400;
	private int height = 350;
	private String myName = "ProblemStatement";  
	
  	public ProblemStatement() {
  		super("ProblemStatement");
  		setTitle("Scenario");
		setBackground(Color.white);
		curFontSizeIndex = 1;  // defaults to "normal"

		setLayout(new BorderLayout());
    	setBackground(Color.white);
    	htmlViewer = new HtmlPanel(this, width, height);
    	htmlViewer.setTopMargin(12);
        validate();
		
		add("West",m_ToolBarPanel);
		setupToolBar(m_ToolBarPanel);
		setModeLine("");
		add("Center", htmlViewer);
    	MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());    	
		setMenuBar(menuBar);
		pack();
		setCurrentWidth(width);
		setCurrentHeight(height);
		setSize(width, height);	
		// update font size if different from the stored or global value
		setFontSize(ObjectRegistry.getWindowFontSize(myName));
		updateSizeAndLocation(myName);	
  	}
 /* 	
  	public void windowOpened(){
		Dimension cur_size = getSize();
		Dimension tb_size = m_ToolBarPanel.getSize();
		int tb_h = tb_size.height;
		int cur_h = cur_size.height;
		if(tb_h < cur_h)
			tb_h = cur_h;
		this.setSize(cur_size.width, tb_h);
	}
*/	
	private void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.statementToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		/*
		tb.addButton(Settings.help,"Help", true);
		tb.addSeparator();
		*/
		tb.addToolBarImage(Settings.statementLabel,Settings.statementLabelSize);
	}
  
    // this method is used to blank out the scenario window
    public void resetProblem()
    {
        htmlViewer.resetProblem("");
    }
    
  	public void resetProblem(String problemName, Vector statementV){
  		setTitle("Problem "+problemName);
  		pv_obj.setName("ProblemStatement"+problemName);
  		htmlViewer.resetProblem(problemName);	
  		splitText(statementV);
  		this.toFront();
  	}
 	
  	public void setFontSize(int sizeIndex) {
  		if(sizeIndex != curFontSizeIndex) {
  			curFontSizeIndex = sizeIndex;
  			htmlViewer.setFontSize(fontSizes[sizeIndex]);
  		}
  	}
  	  	
  	public ObjectProxy getObjectProxy() {
  		return pv_obj;
  	}

	public void setProxyInRealObject(ObjectProxy op) {
		pv_obj = (StatementProxy)op;
		setToolFrameProxy(pv_obj);

	}

	
	public void setProperty(String propertyName, 
						    Object propertyValue) throws DorminException{
            getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
		
		if(propertyName.equalsIgnoreCase("HIGHLIGHT")) {
			Vector tags;
			if(	propertyValue.toString().equalsIgnoreCase("FALSE") ||
				propertyValue.toString().equals("[0, 0]")) // empty tags
				tags = new Vector();
			else
				tags = DataConverter.getListValue(propertyName,propertyValue);
			showtxt(tags);
		}
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			int newFontSize = DataConverter.getIntValue(propertyName,propertyValue);
			htmlViewer.setFontSize(newFontSize);
			setCurFontSizeIndex(getClosestCurFontSizeIndex(newFontSize,fontSizes));
		}
		else if(propertyName.equalsIgnoreCase("PROBLEMSTATEMENT")) {
			setStatement((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("PROBLEMNAME")) 
			//setTitle("Problem "+(String)propertyValue);
			setModeLine("Problem "+propertyValue.toString());
		
		else if(propertyName.equalsIgnoreCase("URLBASE")) {
			htmlViewer.setImageBase((String)propertyValue);		
			htmlViewer.displayHtmlIfExists();
		}
		else {
			super.setProperty(propertyName, propertyValue);
		}
		} catch (NoSuchPropertyException e){
			throw new NoSuchPropertyException("ProblemStatement : "+e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		}
  	}
  	
  	protected void setStatement(String strState){
  		if(strState.indexOf("\n") == -1)
  			strState = strState+"\n";
  		Vector vState = new Vector();
  		StringTokenizer st = new StringTokenizer(strState, "\n",true);
  		
  		while(st.hasMoreElements()) {
  			vState.addElement(st.nextToken());
  		}	
  		splitText(vState);
  	}
  	
  	public Dimension preferredSize() {
  		//htmlViewer.setHtmlWidth(width);
  		return new Dimension(width+25,htmlViewer.getHtmlHeight()+40);
  	}

  	public void showtxt(Vector tag) {
  		htmlViewer.showtxt( tag);
  	}

	public void splitText(Vector readInV) {
  		int s = readInV.size();
  		String problemText = "";
  		for(int i=0; i<s; i++)
  			problemText = problemText + (String)readInV.elementAt(i);
  		htmlViewer.displayHtml(problemText);
  		
  		readInV.removeAllElements();
  		readInV = null;
	}
	
	public void delete(){
		//base = null;
		htmlViewer.removeAll();
		htmlViewer = null;
		super.delete();
		pv_obj = null;
	}

    public void reset()
    {
        resetProblem();
    }
}
