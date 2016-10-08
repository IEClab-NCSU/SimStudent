/**
 * LI_SubpanelInterface.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 * This file defines the LI_Subpanel abstract class.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;


//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * LI_TraversalSubpanel
 *
 * This subpanel displays the number of traversals and 
 * the traversal types for the panel.  When loaded it 
 * will display the Min/Max traversals as well as the 
 * traversal count if defined.  
 *
 * By virtue of the Subpanel superclass this class is 
 * a MouseListener and will listen on the label.  In 
 * the event of a MouseClicked it will bring up the 
 * editor.
 */
public class LI_TraversalSubpanel extends LI_Subpanel {

    /* --------------------------------------------
     * Local Storage. 
     * ------------------------------------------*/
    
    /* Static label text. */
    private static final String REUSEABLE_LINKS = "Set min/max traversals";

    /* The subpanel label as a whole. */
    private JLabel PanelLabel = null;
    private JLabel TraversalCountArea = null;
    private TraversalTextArea MinTextArea  = null;
    private TraversalTextArea MaxTextArea = null;

    /* Preferred height for this item. */
    private static int PREF_HEIGHT = 110;
    

    /* --------------------------------------------
     * Constructor
     * ------------------------------------------*/

    /**
     * Construct a new TraversalSubpanel that displays
     * the relevant LI_Traversal data from the edge.
     * A click on the label will cause this to load
     * the editor panel.
     *
     * @param Edge: The problem Edge. 
     */
    public LI_TraversalSubpanel(BR_Controller Controller, 
				ProblemEdge Edge, int CurrWidth, LinkInspectorPanel Panel) 
	throws LinkInspectorException {
    this.Panel = Panel;
	if (trace.getDebugCode("LI_TraversalSubpanel")) trace.outNT("LI_TraversalSubpanel", "Generating Panel");
	
	/* ------------------------------------------
	 * Set the underlying elements. */
	this.setController(Controller);
	this.setEdge(Edge);
	
	/* ------------------------------------------
	 * Set the Looks. */
	this.setName("LI_TraversalSubpanel");
	this.setPreferredSize(new Dimension(CurrWidth, this.PREF_HEIGHT));
	//this.setMaximumSize(new Dimension(CurrWidth, LI_Subpanel.MAX_HEIGHT));
	//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBackground(Color.WHITE);
	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	GridLayout temp = new GridLayout();
	temp.setColumns(1);
	temp.setRows(4);
	this.setLayout(temp);
	/* ------------------------------------------
	 * Set the individual labels. */
	//String LabelText = "<html><strong><u>Traversals:</u></strong></html>";
	String LabelText = "<html><u>Traversals:</u></html>";
	this.PanelLabel = new JLabel(LabelText, JLabel.CENTER);
	//this.PanelLabel = new JLabel(LabelText);
	
	//this.PanelLabel.addMouseListener(this);

	MaxTextArea = new TraversalTextArea(this, false);
	MinTextArea = new TraversalTextArea(this, true);
	TraversalCountArea = new JLabel("Traversals: " + String.valueOf(Edge.getEdgeData().getTraversalCount()), JLabel.CENTER);
	//TraversalCountArea.setBackground(Color.lightGray);
	this.add(this.PanelLabel);
	this.add(MinTextArea);
	this.add(MaxTextArea);
	this.add(TraversalCountArea);
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
    	if(CurrEdgeData==null)
    		return;
    	MinTextArea.setText(CurrEdgeData.getMinTraversalsStr());
    	MaxTextArea.setText(CurrEdgeData.getMaxTraversalsStr());
    	TraversalCountArea.setText("Traversals: " + String.valueOf(CurrEdgeData.getTraversalCount()));
/*	DBuffer.append("<HTML><TABLE>");
	DBuffer.append("<COL align=\"right\" style=\"font-style: italic\">");
	DBuffer.append("<COL align=\"left\" style=\"font-style: bold\">");
	
	DBuffer.append("<TR><TH>Min/Max:</TH><TH>"); 
	DBuffer.append(this.CurrEdgeData.getMinTraversalsStr() + "-");
	DBuffer.append(this.CurrEdgeData.getMaxTraversalsStr());
	DBuffer.append("</TH></TR>");

	DBuffer.append("<TR><TH>TraversalCount:</TH><TH>"); 
	DBuffer.append(this.CurrEdgeData.getTraversalCount());
	DBuffer.append("</TH></TR>");*/

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
  /*  public void mouseClicked(MouseEvent E) {

	// Pop up the edit dialogue.
	new EditMinMaxLinkTraversals(this.Controller.getActiveWindow(), 
				     REUSEABLE_LINKS, 
				     true, 
				     this.CurrEdgeData);   

	// Reset the contents of this item.
	this.setContents();

	// Fire the updated event.
	this.fireUpdateEvent(false, true);
    }*/


    
    /* ------------------------------------------------
     * Dimension methods. 
     * ----------------------------------------------*/

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

    class TraversalTextArea extends JPanel implements FocusListener {
    	LI_TraversalSubpanel panel;
    	int oldValue;
    	EdgeData edgeData;
    	boolean isMinTraversal;
    	JTextField inputArea;
    	JLabel inputLabel;
    	//JLabel padding;
    	public TraversalTextArea (LI_TraversalSubpanel panel, boolean isMinTraversal){
    		super();
    		this.setBackground(Color.white);
    		this.panel = panel;
    		edgeData = panel.CurrEdgeData;
    		this.isMinTraversal =isMinTraversal;
    		if(isMinTraversal){
    			this.oldValue = edgeData.getMinTraversals();
    			inputLabel = new JLabel("Min Traversals: ");
    		}else{
    			inputLabel = new JLabel("Max Traversals: ");
    			this.oldValue = edgeData.getMaxTraversals();
    		}
    		inputArea = new JTextField();
    		inputArea.setName("LI_TraversalTextField");
    		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputArea); }
    		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    		//this.setSize(this.getWidth()/2, 25);
    		inputArea.addFocusListener(this);
    		this.add(inputLabel);
    		this.add(inputArea);
    		//hack to get nice padding... ideally one could just set the
    		//inputArea textfield to a fixed width that can allow maybe 5 digits width
    		//unfortunately swing is incomprehensible to me...
    		//this.add(new JLabel("         "));
    	}
    	public void setText(String newText){
    		inputArea.setText(newText);
    	}
		public void focusGained(FocusEvent e) {}

		public void focusLost(FocusEvent e) {
			
			String newText = inputArea.getText();
			
			if(newText==null){
				setText(String.valueOf(oldValue));
				return;
			}
			newText = newText.trim();
			int newValue;
			
			try{
				newValue= Integer.parseInt(newText);
			}catch(NumberFormatException exception){
				setText(String.valueOf(oldValue));
				return;
			}
			
			if((newValue >=0) && (newValue!=oldValue)){
				if(isMinTraversal)
					edgeData.setMinTraversals(newValue);
				else
					edgeData.setMaxTraversals(newValue);
				setText(String.valueOf(newValue));
				oldValue = newValue;
				Controller.getProblemModel().fireProblemModelEvent(
						new EdgeUpdatedEvent(panel.Panel, edgeData.getEdge(), true));
			}else{
				setText(String.valueOf(oldValue));
			}
		}
    }
    	

}
