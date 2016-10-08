/**
 * LI_HintSubpanel.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.HelpSuccessPanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;

//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * LI_HintSubpanel
 *
 * This subpanel will display the hints on each arc
 * and provides, like others for edits of this in this
 * case through selection of the title. 
 */
public class LI_HintSubpanel extends LI_Subpanel implements ActionListener{

    /* --------------------------------------------
     * Local Storage. 
     * ------------------------------------------*/

    /* The subpanel label as a whole. */
    private JLabel PanelLabel = null;
    //private JPanel hintPanel = null;
    /* The Contents Label. */
    private JButton addButton = null;
    private JScrollPane ContentsPane = null;

    ///** Action Label for the system. */
    //private ActionLabel CurrActionLabel  = null;

    /* Preferred height for this item. */
    private static int PREF_HEIGHT = 150;

    

    /* --------------------------------------------
     * Constructor
     * ------------------------------------------*/

    /**
     * Construct a new hints subpanel that displays the 
     * appropriate hint messages to the user.  This will
     * display hints irrespective of edge type.
     *
     * @param Controller: The BRController.
     * @param Edge: The problem Edge. 
     */
    public LI_HintSubpanel(BR_Controller Controller, 
			   ProblemEdge Edge, int CurrWidth,
			   LinkInspectorPanel Panel) 
	throws LinkInspectorException {

	if (trace.getDebugCode("LI_HintSubpanel")) trace.outNT("LI_HintSubpanel", "Generating Panel");
	//this.functions = functions;
	this.Panel = Panel;


	/* ------------------------------------------
	 * Set the underlying elements. */
	this.setController(Controller);
	this.setEdge(Edge);
	//this.CurrActionLabel = this.CurrEdgeData.getActionLabel();

	/* ------------------------------------------
	 * Set the Looks. */
	this.setName("LI_HintSubpanel");
	int MinHeight = this.getMinHeight(CurrWidth);
	this.setPreferredSize(new Dimension(CurrWidth, MinHeight));
	//this.setMaximumSize(new Dimension(CurrWidth, LI_Subpanel.MAX_HEIGHT));
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBackground(Color.WHITE);
	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	/* ------------------------------------------
	 * Set the individual labels. */
	String LabelText = "<html><u>Hints:</u></html>";
	this.PanelLabel = new JLabel(LabelText, JLabel.CENTER);
	this.add(this.PanelLabel);
	this.PanelLabel.setToolTipText("Click for more options");
	this.PanelLabel.addMouseListener(this);

	/*
	 * Add the addHint Button
	 */
	this.addButton = new JButton("Add New Hint");
	//addButton.setSize(30, 10);
	addButton.setPreferredSize(new Dimension(30,20));
//	this.add
	this.add(addButton);
	addButton.addActionListener(this);
	/* ------------------------------------------
	 * Set the contents label separately to permit updates. */
	this.setContents();
	
	//this.repaint();
    }


    public void setContents(){
    	//if(CurrEdge ==null)
    	//	return;
    	setContents1(false);
    	//this.Panel.repaintLater();
    	//this.repaint();
    	this.revalidate();
    }
    // public Dimension getPreferredSize() {
	
    // 	int Width = this.Panel.getWidth();
    // 	int Height = this.getMinHeight(Width);
    // 	return new Dimension(Width, (Height * 2));
    //     }
	
    private void setContentsOriginal() {
    	
    	String TmpStr = null;
    	StringBuffer DBuffer = new StringBuffer();

    	if (this.ContentsPane != null) { this.remove(this.ContentsPane); }	
    	

    	DBuffer.append("<HTML>");
    	
    	/* With no hints just display the empty set. */
    	/*if (this.CurrEdgeData.defaultHintOnly()) {
    	    DBuffer.append("<i>None Defined.</i>");
    	}
    	else {*/   
    	Vector<String> Hints = this.CurrEdgeData.getAllHints();
	    for (int i = 0; i < Hints.size(); i++) {
			// Extract the specific hint as needed.
			TmpStr = Hints.elementAt(i);
			// Ignore empty hints in the list.
			if (TmpStr.length() > 0) {
	
			    // Truncate overly long items if needed.
			    if (TmpStr.length() > 75) { 
				TmpStr = TmpStr.substring(0, 74);
				TmpStr += "...";
			    }
			    // Add the Label Row for the hint itself.
			    DBuffer.append("<hr><i>Hint#{" + i + "}:</i>");
			    //DBuffer.append("<blockquote>" + TmpStr + "</blockquote>");
			    DBuffer.append(TmpStr);
			}
	    }
    //	}

    	DBuffer.append("</HTML>");

    	/* Finally add the contents. */
    //	this.ContentsLabel = new JPanel(DBuffer.toString());
    	//this.ContentsLabel = new JLabel("");
    	this.add(this.ContentsPane);
    	//this.repaint();
    }
    
    private void setContents1(boolean addTextBox) {
    	Vector<String> Hints = this.CurrEdgeData.getAllHints();
    	if (this.ContentsPane != null) { this.remove(this.ContentsPane); }
    	JPanel contents = new JPanel();
    	int i;
    	String text;
    	HintTextArea top = null;
    	HintTextArea singleHint = null;
    	for(i =0; i < Hints.size(); i++){
    		text = Hints.elementAt(i);
    		if (text.length() > 0) {
			    singleHint = new HintTextArea(text, i, this, false);
			    if(top==null) top = singleHint;
			    contents.add(singleHint);
    		}
    	}
    	if(addTextBox){
    		singleHint = new HintTextArea("", i, this, true);
    		contents.add(singleHint);
    	}
    	BoxLayout box  = new BoxLayout(contents, BoxLayout.Y_AXIS);
    	contents.setLayout(box);
    	ContentsPane = new JScrollPane(contents);
    	ContentsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	ContentsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(ContentsPane, 1);
        if(addTextBox){
    		singleHint.selectAll();
    		singleHint.grabFocus();
        }else{
        	if(top!=null)
        		top.select(0, 0);
        }
    }

    /* -------------------------------------------------
     * Contents.
     * -----------------------------------------------*/
    /**
     * (Re)load the hint messages as needed.
     *
     * Hints may be present on all link types.  This code 
     * extracts the hints, if they are present from the 
     * edge data and adds them in order to the link. 
     *
     * The vector will be a list of strings and will be 
     * read in turn to the table.  No more than 75 
     * characters will be displayed.
     */
    private void setContents2() {
	
	String TmpStr = null;
	StringBuffer DBuffer = new StringBuffer();

	if (this.ContentsPane != null) { this.remove(this.ContentsPane); }	
	

	DBuffer.append("<HTML>");
	
	/* With no hints just display the empty set. */
	/*if (this.CurrEdgeData.defaultHintOnly()) {
	    DBuffer.append("<i>None Defined.</i>");
	}
	else {
	 */   Vector<String> Hints = this.CurrEdgeData.getAllHints();
	    for (int i = 0; i < Hints.size(); i++) {
			// Extract the specific hint as needed.
			TmpStr = Hints.elementAt(i);
			// Ignore empty hints in the list.
			if (TmpStr.length() > 0) {
	
			    // Truncate overly long items if needed.
			    if (TmpStr.length() > 75) { 
			    	TmpStr = TmpStr.substring(0, 74);
			    	TmpStr += "...";
			    }
			    // Add the Label Row for the hint itself.
			    DBuffer.append("<hr><i>Hint#{" + i + "}:</i>");
			    //DBuffer.append("<blockquote>" + TmpStr + "</blockquote>");
			    DBuffer.append(TmpStr);
			}
	  //  }
	    }

	DBuffer.append("</HTML>");

	/* Finally add the contents. */
	//this.ContentsLabel = new JLabel(DBuffer.toString());
	//this.ContentsLabel = new JLabel("");
	this.add(this.ContentsPane);
	//this.repaint();
    }
	


	 
    /* -------------------------------------------------
     * Data Changes.
     * -----------------------------------------------*/


    /**
     * On the refresh this will reset the ContentsLabel
     * by rebuilding it from the system.  
     * 
     * @param Ev: The problem model event signalled.
     */
    public void refreshData(EdgeEvent Ev) { this.setContents(); }


    /* ---------------------------------------------------
     * Location.
     * -------------------------------------------------*/

    /**
     * Return the minimum desired height given the 
     * Supplied width minimum.  This is done by 
     * calculating the volume of the Contents Label
     * and calculating the height necessary to fit
     * that volume in the current width plus the 
     * space necessary for the label.
     *
     * Clearly as this panel moves to entry fields this
     * will need to change.  
     *
     * @param WidthLimit: The maximum width. 
     */
    protected int getMinHeight(int WidthLimit) {
	
	if (this.ContentsPane == null) { return this.PREF_HEIGHT; }
	else {
	    /* Obtain the dimensions themselves for use. */
	    Dimension LabelDimen = this.PanelLabel.getSize();
	    Dimension ContentDimen = this.ContentsPane.getSize();
	    int ContentWidth = (int) ContentDimen.getWidth();
	    int ContentHeight = (int) ContentDimen.getHeight();
	    
	    if (trace.getDebugCode("LI_HintSubpanel")) trace.outNT("LI_HintSubpanel", 
			"Curr Cw:" + ContentWidth 
			+ " Ch:" + ContentHeight
			+ " Wl:" + WidthLimit);
	    

	    /* Calculate the required dimensions for the 
	     * content label using (h*w)/w' = h'. */
	    int NewHeight = (int) ((ContentWidth * ContentHeight) / WidthLimit);
	    
	    /* Add in the height for the label as needed. */
	    NewHeight += (int) LabelDimen.getHeight();
	    
	    /* Add an arbitrary buffer. */
	    NewHeight += 5;  

	    if (trace.getDebugCode("LI_HintSubpanel")) trace.outNT("LI_HintSubpanel", "Generated Height: " + NewHeight);
	    return NewHeight;
	}
    }

	



    /* -------------------------------------------------
     * MouseListener interface. 
     * -----------------------------------------------*/

    /**
     * When the label is selected this code should pop up
     * hint editing.  In the future this will always permit
     * hint editing.  However for the present this will 
     * replicate the ActionLabelHandler behavior and only
     * allow edits on Correct arcs.
     *
     * NOTE:: this code should be replaced with local 
     * editing in the future.
     *
     * @param E: The mouse click event itself.
     */
    public void mouseClicked(MouseEvent E) {

	/* Here we switch based upon the input. */
	String ActionType = this.CurrEdgeData.getActionType();
	
	/* On correct edit the Success Messages. */
	if (ActionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
	    new HelpSuccessPanel(this.Controller, this.CurrEdgeData, false).setVisible(true);
	    // Content updates.
	    this.setContents();
	    
	    // Signal the update.
	    this.fireUpdateEvent(false, true);
	}
    }


	class HintTextArea extends JTextArea implements FocusListener {
		private String oldText;
		private int hintNumber;
		private String title;
		private LI_HintSubpanel hintPanel;
		private boolean newHint;
		private EdgeData edge;
		HintTextArea(String oldText, int hintNumber, LI_HintSubpanel hintPanel, boolean newHint){
			super();
			this.newHint = newHint;
			this.oldText = oldText;
			this.hintPanel = hintPanel;
			this.hintNumber = hintNumber;
			this.edge = hintPanel.CurrEdgeData;
			title = "Hint "+ (hintNumber+1) + ":";
			setText(title + oldText);
			setBorder(BorderFactory.createLineBorder(Color.black));
			setLineWrap(true);
			setWrapStyleWord(true);
			addFocusListener(this);
		}
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(newHint){
				String newText = getText();
				if(newText!=null && newText.length() >= title.length()){
					String newTitle = newText.substring(0, title.length());
					if(newTitle.equalsIgnoreCase(title))
						newText = newText.substring(title.length());
				}
				newText = newText.trim();
				if(newText!=null && newText.length() > 0){
					Vector<String> hints = edge.getAllHints();
					hints.add(hintNumber, newText);
					Controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(hintPanel.Panel, edge.getEdge(), true));
					oldText = newText;
					newHint = false;
				}else{
					if(CurrEdgeData!=null)
						setContents();
				}
			}else{
				String newText = getText();
				if(newText.length() > title.length()){
					String newTitle = newText.substring(0, title.length());
					if(newText==null) return;
					if(newTitle.equalsIgnoreCase(title)){
						newText = newText.substring(title.length());
					}
				}
				newText = newText.trim();
				if(newText.equals(oldText)){
					this.setText(title+newText);
					return; 
				}
				if(newText.isEmpty()){
					Vector<String> hints = edge.getAllHints();
					hints.remove(hintNumber);
					edge.setHints(hints);
					if(CurrEdgeData!=null)
						setContents();
					return;
				}
				Vector<String> hints = edge.getAllHints();
				hints.set(hintNumber, newText);
				this.setText(title+newText);
				Controller.getProblemModel().fireProblemModelEvent(new EdgeUpdatedEvent(hintPanel.Panel, edge.getEdge(), true));
				oldText = newText;
			}
		}
		
	}
    // define buggy message


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		setContents1(true);
		revalidate();
	}
}
