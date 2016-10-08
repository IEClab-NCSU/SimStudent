//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/RatioFrame.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.Point;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.cmu.spreadsheet.AltTextField;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.CommonObjectProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.scrollpanel.BevelPanel;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;

public class RatioFrame extends DorminToolFrame {
	private SingleTextField triangle_1, triangle_2;
	private RatioProxy rProxy;
	private FractionsPanel fractionsPanel;
	private TableViewer scrollPanel;
	Font labelFont; 
	private QuestionPanel questionPanel;
	protected LinkVector links;
	private int minWidth = 300;
	private int minHeight = 435;
	
	public RatioFrame(RatioProxy p){
		super("RatioTool");
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			labelFont = new Font("geneva", Font.BOLD, 11);
		else
			labelFont = new Font("arial", Font.BOLD, 11);
			
		links = new LinkVector();
		links.addPropertyChangeListener(this);
		setProxyInRealObject(p);
		setTitle("Ratio Tool");
		setBackground(Settings.ratioBackground);
		setLayout(new BorderLayout());
		Panel centerPanel = new Panel();
		centerPanel.setBackground(getBackground());
		centerPanel.setLayout(new GridBagLayout());
		
		Panel trianglePanel = new Panel();
		trianglePanel.setLayout(new GridBagLayout());
		Label lab = createLabel("Triangle similarity statement", labelFont);
		GridbagCon.viewset(centerPanel,lab, 0, 0, 1, 1, 10, 5, 0 ,0);
		Panel trPanel = new Panel();
		trPanel.setLayout(new FlowLayout(1));
		trPanel.add(new TriangleCanvas());
		
		triangle_1 = createSingleField(35, rProxy, "Triangle 1");
//		links.addVecticalLink(triangle_1);
//		links.addHorisontalLink(triangle_1);
		triangle_1.addPropertyChangeListener(links);
		trPanel.add(triangle_1);
		trPanel.add(new Label(" "));
		trPanel.add(createLabel("~", (new Font("dialog", Font.BOLD, 16))));
		trPanel.add(new TriangleCanvas());
		triangle_2 = createSingleField(35, rProxy, "Triangle 2");
		trPanel.add(triangle_2);
//		links.addVecticalLink(triangle_2);
//		links.addHorisontalLink(triangle_2);
		triangle_2.addPropertyChangeListener(links);
		
		GridbagCon.viewset(trianglePanel,trPanel, 0, 0, 1, 1, 0, 0, 0 ,0);
		GridbagCon.viewset(centerPanel,trianglePanel, 0, 1, 1, 1, 0, 0, 10 ,0);
		
		Label lab1 = createLabel("Corresponding sides ratios", labelFont);
		GridbagCon.viewset(centerPanel,lab1, 0, 2, 1, 1, 0, 5, 0 ,0);
		fractionsPanel = new FractionsPanel(rProxy, links);
		GridbagCon.viewset(centerPanel,fractionsPanel, 0, 3, 1, 1, 0, 0, 10 ,0);
		
		Label header = createLabel("Substitute values", labelFont);
		GridbagCon.viewset(centerPanel,header, 0, 4, 1, 1, 0, 5, 0 ,0);
		questionPanel = new QuestionPanel();
		questionPanel.setBackground(Settings.ratioBackgroundLight);
		scrollPanel = new TableViewer(questionPanel);
		scrollPanel.setSize(235, 170);
		scrollPanel.setScrollbarWidth(17);
		scrollPanel.setBackground(Settings.ratioBackgroundLight);
		Component bounded_sp = borderedPanel(scrollPanel);
		bounded_sp.setBackground(Settings.ratioBackgroundLight);
		GridbagCon.viewset(centerPanel,bounded_sp, 0, 5, 1, 1, 0, 0, 10 ,0);
		
		add("Center",centerPanel);
		
		setupToolBar(m_ToolBarPanel);
		add("West",m_ToolBarPanel);
		setModeLine("");
		
		pack();
		setCurrentWidth(minWidth);
		setCurrentHeight(minHeight);
		setCurrentLocation(new Point(0,0));
				    	
    	MenuBar menuBar = MenuFactory.getGeneralMenuBar(this, getName());
		setMenuBar(menuBar);
		
		updateSizeAndLocation("RatioTool");
	}
	
	private Component borderedPanel(Container p) {
		BevelPanel bp = new BevelPanel();
		bp.setStyle(BevelPanel.LOWERED);   //PLAIN);
		bp.setLayout(new FlowLayout());
		bp.add(p);
		return bp;
    }
	
	private void addQuestions(int numQuestions){
		for(int i=0;i<numQuestions; i++){
			Label lab = createLabel("Question "+String.valueOf(i+1), labelFont);
			questionPanel.addComponent(lab);
			// "Question" is an absolutely virtual object without any properties
			CommonObjectProxy tp = new CommonObjectProxy(rProxy,"Question");
			FractionsPanel fp = new FractionsPanel(tp, links);
			questionPanel.addComponent(fp);
		}
	}
	
	public void requestFocus(){
		super.requestFocus();
		fractionsPanel.requestFocus();
	}
	
	private void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.ratioToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
		tb.addToolBarImage(Settings.ratioLabel,Settings.ratioLabelSize);
	}
	
	private Label createLabel(String labText, Font font){
		Label toret = new Label(labText);
		toret.setFont(font);
		return toret;
	}
	
	static SingleTextField createSingleField(int w, ObjectProxy parent, String name){
		SingleTextField stf = new SingleTextField(w,parent,name);
		stf.setGrow(AltTextField.NO_GROW);
		return stf;
	}
/*	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if(	command.equalsIgnoreCase("HINT") ||
			command.equalsIgnoreCase("HELP")) 
			askForHint();
		else
			super.actionPerformed(e);
	}
	
	public void askForHint() {
 		if(triangle_1.canAskForHelp()){
 			triangle_1.askHint();
 			return;
 		}
 		if(triangle_2.canAskForHelp()){
 			triangle_2.askHint();
 			return;
 		}
 		if(	!fractionsPanel.asksForHint() &&
 			!questionPanel.asksForHint())
 			super.askForHint();	
	}
*/
	public void askForHint() {
		if(!links.currAskedForHelp())
			super.askForHint();
		//else
		//	links.focusCurrentCell();
	}
		
	public ObjectProxy getObjectProxy() {
		return rProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		rProxy = (RatioProxy)op;
		setToolFrameProxy(rProxy);
	}
	
	public void delete(){
		triangle_1.removePropertyChangeListener(links);
		triangle_2.removePropertyChangeListener(links);
		triangle_1 = null;
		triangle_2 = null;
		fractionsPanel.removeAll();
		fractionsPanel = null;
		questionPanel.removeAll();
		questionPanel = null;
		links.removePropertyChangeListener(this);
		links.delete();
		links = null;
		super.delete();
		rProxy = null;
	}
	
	public void removeAll(){
		scrollPanel.removeAll();
		scrollPanel = null;
		super.removeAll();
		//removeNotify();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
			if(propertyName.equalsIgnoreCase("NUMBEROFQUESTIONS")) {
				int numQuestions = DataConverter.getIntValue(propertyName, propertyValue);
				addQuestions(numQuestions);
				updateScrollPanel(numQuestions);
			}
			else 
				super.setProperty(propertyName, propertyValue);
		} catch(NoSuchPropertyException e) {
			throw new NoSuchPropertyException("RatioTool : "+e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		}
	}
	
	private void updateScrollPanel(int numQuestions){
		//setting for vertical scroll bar
		scrollPanel.setComponent(questionPanel, numQuestions);
		if(numQuestions == 1) {
			scrollPanel.setSize(235,90);
			minHeight = 380;
			setSize(minWidth,380);
		}	
	}
				
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
	
	public void windowActivated(){
		links.focusCurrentCell();
	}
}