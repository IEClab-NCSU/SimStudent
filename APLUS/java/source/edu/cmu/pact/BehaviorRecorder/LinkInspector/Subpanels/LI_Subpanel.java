/**
 * LI_SubpanelInterface.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 * This file defines the LI_Subpanel abstract class.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;


//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * LI_Subpanel
 *
 * The LI_Subpanel is an abstract class that implements
 * some of the requisite interfaces for the LI Subpanels
 * as well as implementing some of the more standard
 * and often unneeded methods.  
 *
 * <br>
 * Despite the existence of the setEdge methods the edges
 * should always be set by the constructor for each subpanel
 * as they will only be generated when a panel is needed and
 * removed when they are not.
 * <br>
 * 
 * The panels follow a basic framework.  All panels and gui items
 * are centered on a particular edge.  When instatiated they will 
 * be instantiated with that edge.  They are added as listeners to
 * the problem model when instantiated and listen for changes to the 
 * edges.  In the event of an EdgeUpdatedEvent they will be refreshed
 * from the contents of the Edge itself.
 * <br>
 * 
 * Subclasses of this class need not implement the ProblemModelEventListener
 * interface, that is done here.  They need only:
 *   1) Implement An appropriate Constructor that calls setEdge as part of
 *      its call.
 *   2) Implement a clearEdge method if any for explicit clearing when 
 *      the panel is purged.
 *   3) Implement the abstract refreshData method.  This method will be 
 *      called when the edge data changes and updates occur.
 *   4) Implement getMinHeight(int).  
 *   *) Other parts of this code such as the updateDimensions may be 
 *      subclassed as needed but are not always.
 * <br>
 *
 * Primarily the panels when present should implement display and editing for
 * a small subset of the EdgeData.  They will be constructed when a new edge
 * is added to a LinkInspectorPanel, will be notified of necessary updates 
 * via the refreshData method and will be cleared by the clearEdge method 
 * before they are closed.  
 */
public abstract class LI_Subpanel extends JPanel
    implements MouseListener {
    
    /* --------------------------------------------
     * Shared Storage.
     * ----------------------------------------- */

    /** BR Controller storage. */
    protected BR_Controller Controller = null;

    /** Storage for the problem edge. */
    protected ProblemEdge CurrEdge = null;

    /** Edge data used for menu construction. */
    protected EdgeData CurrEdgeData = null;

    /** Storage for the current dimensions. */
    protected Dimension CurrDimensions = null;

    /** 
     * This flag will be used as part of the update code
     * to flag when this subpanel caused a change and thus
     * does not need to update.  */
    protected boolean IUpdated = false;

    protected LinkInspectorPanel Panel;
    /* Maximum HEIGHT for this item. */
    public static int MAX_HEIGHT = 65536;
   // protected boolean collapsible;
  //  protected JPanel titleBar;
    /* Static label text. */
    //private static int PREF_HEIGHT = 10;    
    
    /* This is used to store the preferred dimensions
     * for later use. */
    //protected Dimension PreferredSizeDimen = null;

    /* --------------------------------------------
     * Edge storage and manipulation.
     * ------------------------------------------*/

    /**
     * clearEdge
     *
     * Make the panel blank clearing all data in it.
     */
    public void clearEdge() { 
	this.CurrEdge = null; 
	this.CurrEdgeData = null; 
    }

    /**
     * Set the edge for the present item supporting it
     * as a usable element.
     *
     * @param Edge: The edge being set.
     */
    protected void setEdge(ProblemEdge Edge) 
	throws LinkInspectorException {
	
	this.CurrEdge = Edge;
	this.CurrEdgeData = Edge.getEdgeData();
	if (trace.getDebugCode("LI_Subpanel")) trace.outNT("LI_Subpanel", "Done Setting Edge.");
    }


    /**
     * On an update call this method will be called
     * for all final classes.  It should not be 
     * overridden.  Rather the abstract updateEdgerContents
     * below should be changed.
     *
     * NOTE:: It is up to the descendant panels to se the 
     *  IUpdated flag appropriately.  If they fail to do
     *  so then this will just cause an update each time. 
     *
     * @param Ev:  The problem Model event being dealt with.
     */
    public final void updateContents(EdgeEvent Ev) {
	
		/* check the IUpdated flag.  If true set to false
		 * and ignore this as this panel originated the 
		 * change else call the inner update. */
		if (this.IUpdated) { this.IUpdated = false; }
		else {
			if(Ev instanceof EdgeRewiredEvent){
				this.CurrEdge = ((EdgeRewiredEvent)Ev).getEdgeCreatedEvent().getEdge();;
				this.CurrEdgeData = CurrEdge.getEdgeData();
			}
			this.refreshData(Ev); 
		}
    }
	

    /**
     * @param PEvent: The problem model event signalled.
     *
     * On changes to the problem model trap these changes and
     * refresh the edge data.
     */
    public abstract void refreshData(EdgeEvent Ev);
       
	

    /* -------------------------------------------
     * Controller Storage.
     * -----------------------------------------*/

/**
     * Set the edge for the present item supporting it
     * as a usable element.
     *
     * @param Edge: The edge being set.
     */
    protected void setController(BR_Controller Controller) 
	throws LinkInspectorException {
	
	this.Controller = Controller;
	if (trace.getDebugCode("LI_Subpanel")) trace.outNT("LI_Subpanel", "Set Controller.");
    }

    /* -------------------------------------------
     * Dimensional changes.
     * ---------------------------------------- */

    /**
     * Accept changes to the dimensions from the 
     * LinkInspectorPanel, Keep the width constrained 
     * but not the height.  The height will be set 
     * based upon a call to getMinHeight which must
     * specify the minimum height of this panel 
     * given the supplied desired width. 
     *
     * @param WidthLim:  The Max Width.
     */
    public void updateDimensions(int WidthLim) { 
    	//this.v
    	int Width = WidthLim - 10;
	int Height = this.getMinHeight(Width);
    	Dimension Pref = new Dimension(Width, Height);
	//this.PreferredSizeDimen = Pref;
    	//this.setPreferredSize(Pref);
    	//this.setMaximumSize(Pref);
    }

    /*getPrefferredSize(){
     * return 
     * 
     */
    //public Dimension getPreferredSize() { return this.PreferredSizeDimen; }


    /**
     * getMinHeight
     *
     * Return the minimum desired height given the 
     * Supplied width minimum.  This may use a static
     * minimum value or some other feature but must
     * be overridden. 
     *
     * @param WidthLimit: The minimum width. 
     */
    protected abstract int getMinHeight(int WidthLimit);





    /* -----------------------------------------------
     * Event firing.
     * ---------------------------------------------*/

    /**
     * When signalled, fire an EdgeUpdated Event from this
     * item.  This will cause the signal to be propagated 
     * it also sets the IUpdated flag as well.
     */
    protected void fireUpdateEvent(boolean LinkChanged, 
				 boolean DataChanged) {

	this.IUpdated = true;
	//For simplicity updated events from this subpanel have set as the source
	//the containing linkinspectorpanel. When an instanceof the linkinspectorpanel
	//gets an event, it will ignore it if it is the source of the update.
	EdgeUpdatedEvent U 
	    = new EdgeUpdatedEvent(this.Panel, this.CurrEdge,  DataChanged);
	ProblemModel P = this.Controller.getProblemModel();
       	P.fireProblemModelEvent(U);
    }


    /* -----------------------------------------------
     * MouseListener Methods.
     * -------------------------------------------- */
    
    /** Called when the mouse is selected. 
     * Must be implemented.  */
    public void mouseClicked(MouseEvent E) { }

    /** Empty method for when the Mouse enters the component. */
    public void mouseEntered(MouseEvent E) { }

    /** Empty method for when the Mouse exits the component. */
    public void mouseExited(MouseEvent E) { }

    /** Empty method for when the Mouse is pressed in the component. */
    public void mousePressed(MouseEvent E) { }

    /** Empty method for when the Mouse release occurrs the component. */
    public void mouseReleased(MouseEvent E) { }    
}
