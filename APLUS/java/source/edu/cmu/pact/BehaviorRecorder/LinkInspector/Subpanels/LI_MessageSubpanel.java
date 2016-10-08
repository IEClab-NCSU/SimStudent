/**
 * LI_MessageSubp.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 * This file defines the LI_Subpanel abstract class.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
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
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.Utilities.trace;

//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * LI_MessagesSubpanel
 *
 * This subpanel displays the student's selection/action/input
 * data and permits editing.  Like the others it has a top label
 * and permits editing on a click.
 */
public class LI_MessageSubpanel extends LI_Subpanel{

    /* ---------------------------------------------
     * Static Parameters.
     * -------------------------------------------*/
    
    /* Maximum width for this item. */
    private static final int MAX_WIDTH = 300;

    /* Static label text. */
    private static int PREF_HEIGHT = 60;


    /* --------------------------------------------
     * Local Storage. 
     * ------------------------------------------*/

    /* The subpanel label as a whole. */
    private JLabel PanelLabel = null;

    /* The Contents Label. */
    private JScrollPane ContentsPane = null;
    private MessageTextArea ContentsPaneMessage =null;
    /** Action Label for the system. */
    private ActionLabel CurrActionLabel  = null;


    /* --------------------------------------------
     * Constructor
     * ------------------------------------------*/

    /**
     * Construct a new Message Panel that displays the 
     * appropriate message to the user.  The Message panel
     * will either display the current Success Message or 
     * the current Buggy Message depending upon the edge
     * type or rather the action type.  As this may change
     * with a type change this code will change the displayed
     * contents.
     *
     * @param Controller: The BRController.
     * @param Edge: The problem Edge. 
     */
    public LI_MessageSubpanel(BR_Controller Controller, 
			      ProblemEdge Edge, int CurrWidth, LinkInspectorPanel Panel) 
	throws LinkInspectorException {
    	this.Panel = Panel;
	if (trace.getDebugCode("LI_MessageSubpanel")) trace.outNT("LI_MessageSubpanel", "Generating Panel");
	/* ------------------------------------------
	 * Set the underlying elements. */
	this.setController(Controller);
	this.setEdge(Edge);
	this.CurrActionLabel = this.CurrEdgeData.getActionLabel();

	/* ------------------------------------------
	 * Set the Looks. */
	this.setName("LI_MessageSubpanel");
	this.setPreferredSize(new Dimension(CurrWidth, this.PREF_HEIGHT));
	//this.setMaximumSize(new Dimension(CurrWidth, LI_Subpanel.MAX_HEIGHT));
	//this.setMaximumSize(new Dimension(CurrWidth, 65536));
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBackground(Color.WHITE);
	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
	/* ------------------------------------------
	 * Set the individual labels. */
	String LabelText;
	if(this.CurrEdge.isBuggy())
		LabelText = "<html><u>Buggy Message:</u></html>";
	else
		LabelText = "<html><u>Message:</u></html>";
	this.PanelLabel = new JLabel(LabelText, JLabel.CENTER);
	this.add(this.PanelLabel);
	this.PanelLabel.addMouseListener(this);
	
	MessageTextArea messageArea;
	if(CurrEdge.isBuggy())
		messageArea = new MessageTextArea(CurrEdgeData.getBuggyMsg(), this);
	else
		messageArea = new MessageTextArea(CurrEdgeData.getSuccessMsg(), this);
	messageArea.setLineWrap(true);
	messageArea.setWrapStyleWord(true);
	//messageArea.addFocusListener(this);
	ContentsPaneMessage = messageArea;
	ContentsPane = new JScrollPane(messageArea);
	ContentsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	ContentsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	this.add(ContentsPane);
	/* ------------------------------------------
	 * Set the contents label separately to permit updates. */
	this.setContents();
	
	//this.repaint();
	if (trace.getDebugCode("LI_MessageSubpanel")) trace.outNT("LI_MessageSubpanel", "L: " + this.getLocation());
	
    }
	
    class MessageTextArea extends JTextArea implements FocusListener {
    	LI_MessageSubpanel panel;
    	String oldText;
    	EdgeData edgeData;
    	public MessageTextArea(String text, LI_MessageSubpanel panel){
    		super();
    		setText(text);
    		this.panel = panel;
    		edgeData = panel.CurrEdgeData;
    		this.oldText = text;
    		addFocusListener(this);
    	}
		public void focusGained(FocusEvent e) {}

		public void focusLost(FocusEvent e) {
			String newText = getText();
			if(newText==null){
				if(oldText ==null || oldText.isEmpty())
					return;
			}else{
				if(newText.equals(oldText)){
					return;
				}
			}
			oldText = newText;
			if(edgeData.getEdge().isBuggy() || 
					edgeData.getActionType().equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
				edgeData.setBuggyMsg(newText);
			else
				edgeData.setSuccessMsg(newText);
			Controller.getProblemModel().fireProblemModelEvent(
					new EdgeUpdatedEvent(panel.Panel, edgeData.getEdge(), true));
		}

    	
    }
    /* -------------------------------------------------
     * Contents.
     * -----------------------------------------------*/
    /**
     * Set the contents label text for display.
     * This will reset the text directly as needed. 
     */
    private void setContents() {
	    String LabelText,text;
	    if(this.CurrEdge.isBuggy()){
	    		text = CurrEdgeData.getBuggyMsg();
	    		LabelText = "<html><u>Buggy Message:</u></html>";
	    }else{
	    		text = CurrEdgeData.getSuccessMsg();
	    		LabelText = "<html><u>Message:</u></html>";
	    }
		this.PanelLabel.setText(LabelText);
		ContentsPaneMessage.setText(text);
		this.setSize(PanelLabel.getHeight() + ContentsPaneMessage.getHeight(), this.getWidth());
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
	    
	    /* Calculate the required dimensions for the 
	     * content label using (h*w)/w' = h'. */
	    int NewHeight = (int) ((ContentWidth * ContentHeight) / WidthLimit);
	
	    /* Add in the height for the label as needed. */
	    NewHeight += (int) LabelDimen.getHeight();

	    /* Add an arbitrary buffer. */
	    NewHeight += 5;  

	    return NewHeight;
	}
    }



    /* -------------------------------------------------
     * MouseListener interface. 
     * -----------------------------------------------*/

    /**
     * When the label is selected this will bring up the 
     * appropriate editor.  This will be either the 
     * edit SuccessMessage dialogue or editBugMessage.
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
	    this.handleUpdate();
	}
	/* On Buggys edit the Bug Message. */
	else if ((ActionType.equalsIgnoreCase(EdgeData.BUGGY_ACTION)) 
		 || (ActionType.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))) {
	    CurrEdge.getLinkEditFunctions().showEditBuggyMsgPanel();
	    this.handleUpdate();
	}
	
    }
    
    
    /**
     * In the event that an update occurrs handle it 
     * here by signaling the update.  
     */
    private void handleUpdate() {
		// Reset the contents.
		this.setContents();
		
		// Signal the update.
		this.fireUpdateEvent(false, true);
    }

}
