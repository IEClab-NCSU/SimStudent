package edu.cmu.pact.BehaviorRecorder.ProblemModel;
import java.awt.Color;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
public class EdgeColorEvent extends ProblemModelEvent
/* NOTE: EdgeColorEvents should be thrown at the END of any other events.
 * JGraphController will often repaint edges due to different events,
 * and if the EdgeColorEvent is thrown first, it might be repainted to 
 * its default black color afterwards.
 */
implements EdgeEvent {
    

    /* ---------------------------------------
     * Internal Storage.
     * ------------------------------------ */

    /** The Edge event deals with a single edge. */
    private ProblemEdge Edge = null;    

    /** (Collinl)  Not sure why this is here. */
    private static final long serialVersionUID = 1L;

    private Color newColor;

    /* ----------------------------------------------
     * Constructors.
     * -----------------------------------------*/
    public EdgeColorEvent(Object source, ProblemEdge edge, Color NewColor) {
    	super(source, "EdgeUpdated", null, edge);
    	this.Edge = edge;
    	this.newColor = NewColor;
    }

    public Color getColor(){
    	return newColor;
    }

    /**
     * @return {@link #edge}
     */
    public ProblemEdge getEdge() {
        return this.Edge;
    }


}
