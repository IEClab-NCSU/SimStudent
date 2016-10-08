/**
 * 
 */
package edu.cmu.pact.miss;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JComponent;

import pact.CommWidgets.WrapperSupport;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowPlatform;

/**
 * @author sewall
 *
 */
public class SimStWrapperSupport extends WrapperSupport {

	/**
	 * @param container
	 */
	public SimStWrapperSupport(Container container) {
		super(container);
	}
    
    /**
     * Align GUI components for SimSt Peer Learning Environment
     * @param dimension 
     * @param sp
     */
    private void buildSimStPLE(JComponent tutorPanel) {
        //container.add(new SimStPeerTutoringPlatform(tutorPanel, controller));
    	container.add(new AplusPlatform(tutorPanel, controller));
    }
    
    /**
     * Align GUI components for SimSt Peer Learning Environment
     * @param dimension 
     * @param sp
     */
    private void buildSimStGameShow(JComponent tutorPanel) {
        container.add(new GameShowPlatform(tutorPanel, controller));
    }

    /**
     * Override to create SimSt-specific UI panels.
     */
	protected void setupHintWindow(JComponent tutorPanel, Dimension tutorPanelPreferredSize) {
        if (isRunningSimStPLE()) {
            buildSimStPLE(tutorPanel);
        } else if(isRunningSimStGameShow()) {
            buildSimStGameShow(tutorPanel);
        } else {
            super.setupHintWindow(tutorPanel, tutorPanelPreferredSize);
        }
	}
}
