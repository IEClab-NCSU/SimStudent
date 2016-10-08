/**
 * Describe class MissMouseHandler here.
 *
 *
 * Created: Thu May 12 15:58:07 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.console.controller;

// import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.cmu.pact.Utilities.trace;

public class MissMouseHandler
    implements ListSelectionListener, ActionListener, MouseListener {

    // - 
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // - 

    // The Miss Controller that is in charge
    private MissController missController;
    private MissController getMissController() { return this.missController; }
    private void setMissController( MissController missController ) {
	this.missController = missController;
    }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>MissMouseHandler</code> instance.
     *
     */
    public MissMouseHandler( MissController controller ) {

	setMissController( controller );
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    //////////////////////////////////////////////////////////////////////
    // Implementation of java.awt.event.MouseListener

    /**
     * Describe <code>mouseClicked</code> method here.
     *
     * @param mouseEvent a <code>MouseEvent</code> value
     */
    public final void mouseClicked(final MouseEvent mouseEvent) {

    }

    /**
     * Describe <code>mouseEntered</code> method here.
     *
     * @param mouseEvent a <code>MouseEvent</code> value
     */
    public final void mouseEntered(final MouseEvent mouseEvent) {

    }

    /**
     * Describe <code>mouseExited</code> method here.
     *
     * @param mouseEvent a <code>MouseEvent</code> value
     */
    public final void mouseExited(final MouseEvent mouseEvent) {

    }

    /**
     * Describe <code>mousePressed</code> method here.
     *
     * @param mouseEvent a <code>MouseEvent</code> value
     */
    public final void mousePressed(final MouseEvent mouseEvent) {

    }

    /**
     * Describe <code>mouseReleased</code> method here.
     *
     * @param mouseEvent a <code>MouseEvent</code> value
     */
    public final void mouseReleased(final MouseEvent mouseEvent) {

    }

    //////////////////////////////////////////////////////////////////////
    // Implementation of java.awt.event.ActionListener

    /**
     * Describe <code>actionPerformed</code> method here.
     *
     * @param actionEvent an <code>ActionEvent</code> value
     */
    public final void actionPerformed(final ActionEvent actionEvent) {

	MissController controller = getMissController();

	String arg = actionEvent.getActionCommand();
	Object source = actionEvent.getSource();

	if ( arg.equals( MissController.NEW_PROBLEM ) ) {

	    trace.out("New Problem...");

	} else if ( arg.equals( MissController.LOAD_WME_TYPE ) ) {

	    controller.setSimStWmeTypeFile();
	    
	} else if ( arg.equals( MissController.INIT_WME ) ) {

	    controller.setSimStInitStateFile();

	} else if ( arg.equals( MissController.LOAD_PREDICATE ) ) {

	    controller.readSimStPredicateSymbols();

	} else if ( arg.equals( MissController.LOAD_OPERATOR ) ) {

	    controller.readSimStOperators();

	} else if ( arg.equals( MissController.TEST_MODEL_ON ) ) {
	    
	    controller.testProductionModelOn();

	} else if ( arg.equals( MissController.HIBERNATE_SS ) ) {

	    controller.hibernateSimSt( true );

	} else if ( arg.equals( MissController.WAKEUP_SS ) ) {

	    controller.hibernateSimSt( false );

	}
	// Added 6/7/06 - Reid Van Lehn <rvanlehn@mit.edu>
	else if ( arg.equals(MissController.LOAD_INSTRUCTIONS)) {
		controller.loadInstructions();
	} else if (arg.equals(MissController.SAVE_INSTRUCTIONS)) {
		controller.saveInstructions();
	} 

    }

    //////////////////////////////////////////////////////////////////////
    // Implementation of javax.swing.event.ListSelectionListener

    /**
     * Describe <code>valueChanged</code> method here.
     *
     * @param listSelectionEvent a <code>ListSelectionEvent</code> value
     */
    public void valueChanged( ListSelectionEvent listSelectionEvent ) {

	
    }

}

//
// end of MissMouseHandler.java
//
