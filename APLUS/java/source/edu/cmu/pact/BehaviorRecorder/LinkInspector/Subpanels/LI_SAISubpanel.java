/**
 * LI_SIASubpanel.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 * This file defines the LI_Subpanel abstract class.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditStudentInputDialog;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;


//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * LI_SAIsubpanel.java
 *
 * This subpanel displays the student's selection/action/input
 * data and permits editing.  Like the others it has a top label
 * and permits editing on a click.
 */
public class LI_SAISubpanel extends LI_Subpanel {

    /* --------------------------------------------
     * Local Storage. 
     * ------------------------------------------*/
    
    /* The subpanel label as a whole. */
    private JLabel PanelLabel = null;

    /* The Contents Label. */
    private JLabel ContentsLabel = null;

    
    /* Preferred height for this item. */
    private static int PREF_HEIGHT = 120;


    /* --------------------------------------------
     * Constructor
     * ------------------------------------------*/

    /**
     * Construct a new SAISubpanel that displays
     * the relevant Student Input data.
     * A click on the label will cause this to load
     * the editor panel.
     *
     * @param Edge: The problem Edge. 
     */
    public LI_SAISubpanel(BR_Controller Controller, 
			  ProblemEdge Edge, int CurrWidth,LinkInspectorPanel Panel) 
	throws LinkInspectorException {
    	this.Panel = Panel;
	if (trace.getDebugCode("LI_SAISubpanel")) trace.outNT("LI_SAISubpanel", "Generating Panel");
	/* ------------------------------------------
	 * Set the underlying elements. */
	this.setController(Controller);
	this.setEdge(Edge);

	/* ------------------------------------------
	 * Set the Looks. */
	this.setName("LI_SAISubpanel");
	this.setPreferredSize(new Dimension(CurrWidth, this.PREF_HEIGHT));
	//this.setMaximumSize(new Dimension(CurrWidth, LI_Subpanel.MAX_HEIGHT));
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBackground(Color.WHITE);
	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
	/* ------------------------------------------
	 * Set the individual labels. */
	String LabelText = "<html><u>SAI:</u></html>";
	this.PanelLabel = new JLabel(LabelText, JLabel.CENTER);
	this.add(this.PanelLabel);
	this.PanelLabel.addMouseListener(this);

	/* ------------------------------------------
	 * Set the contents label separately to permit updates. */
	this.setContents();
	
	//this.repaint();
    }
	

    /* -------------------------------------------------
     * Contents.
     * -----------------------------------------------*/
    /**
     * Set the contents label text for display.
     * This will reset the text directly as needed. 
     */
    private void setContents() {
	
	StringBuffer DBuffer = new StringBuffer();
	Matcher EdgeMatcher = this.CurrEdgeData.getMatcher();

	if (this.ContentsLabel != null) { this.remove(this.ContentsLabel); }
	
	DBuffer.append("<HTML><TABLE cellpadding=\"1\" cellspacing=\"1\">");
	//change default cell padding..
	DBuffer.append("<COL align=\"center\" style=\"font-style: normal \">");
	DBuffer.append("<COL align=\"center\" style=\"font-style: normal\">");
	
	DBuffer.append("<TR><TD>Selection:</TD><TD> &nbsp; &nbsp;"); 
	DBuffer.append(EdgeMatcher.getSelectionLabelText());
	DBuffer.append("</TD></TR>");

	DBuffer.append("<TR><TD>Action:</TD><TD> &nbsp; &nbsp;"); 
	DBuffer.append(EdgeMatcher.getActionLabelText());
	DBuffer.append("</TD></TR>");

	DBuffer.append("<TR><TD>Input:</TD><TD> &nbsp; &nbsp;"); 
	DBuffer.append(EdgeMatcher.getInputLabelText());
	DBuffer.append("</TD></TR>");

	DBuffer.append("<TR><TD>Actor:</TD><TD> &nbsp; &nbsp;"); 
	DBuffer.append(this.CurrEdgeData.getActor());
	DBuffer.append("</TD></TR>");

	DBuffer.append("</TABLE></HTML>");


	//this.ContentsLabel.setText(DBuffer.toString());
	this.ContentsLabel = new JLabel(DBuffer.toString());
	this.ContentsLabel.setName("LI_SAILabel");
	this.add(this.ContentsLabel);
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
      


    /* -------------------------------------------------
     * MouseListener interface. 
     * -----------------------------------------------*/

    /**
     * In the event that the label is clicked on this 
     * code will bring up a dialogue for editing the 
     * link traversals.  This method is defined here but
     * will only be added as a listener to the JLabel.
     *
     * NOTE:: this code should be replaced with local 
     * editing in the future.
     *
     * @param E: The mouse click event itself.
     */
    public void mouseClicked(MouseEvent E) {

	// Pop up the edit dialogue.
	EditStudentInputDialog.show(this.CurrEdgeData, this.Controller);

	// Reset the contents.
	//this.setContents();

	// Signal the update.
	//this.fireUpdateEvent(false, true);
    }


    /* ----------------------------------------------
     * Dimension Updates.
     * ------------------------------------------- */
    
    /**
     * Return the desired height of this panel given
     * the minimum widh.  This will just return the
     * static PREF_HEIGHT value at all times.
     *
     * @param WidthLimit:  The Min Width value.
     */
    protected int getMinHeight(int WidthLimit) { 
	return this.PREF_HEIGHT;
    }



}
