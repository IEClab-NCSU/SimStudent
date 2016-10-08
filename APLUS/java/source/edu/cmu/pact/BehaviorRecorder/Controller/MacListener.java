/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import org.simplericity.macify.eawt.ApplicationAdapter;
import org.simplericity.macify.eawt.ApplicationEvent;

import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog.Load;
import edu.cmu.pact.Utilities.trace;

/**
 * Handler for open file requests on a Mac.
 */
public class MacListener extends ApplicationAdapter {
	
	/** For {@link LoadFileDialog#LoadFileDialog(BR_Controller)}. */
	private final BR_Controller controller;

	/**
	 * @param controller needed by {@link #handleOpenFile(ApplicationEvent)}
	 */
	public MacListener(BR_Controller controller) {
		this.controller = controller;
	}

	/**
	 * Handler for open-application requests on a Mac. Calls
	 * {@link #handleOpenFile(ApplicationEvent) handleOpenFile(ae)}
	 * @param ae event may contain filename.
	 */
    public void handleOpenApplication(ApplicationEvent ae) {
		if (trace.getDebugCode("loadfile"))
			trace.out("loadfile", "MacListener.handleOpenApplication received "+ae);
    	handleOpenFile(ae);
    }

	/**
	 * Handler for open file requests on a Mac.
	 * @param ae event contains filename
	 */
	public void handleOpenFile(ApplicationEvent ae) {
		if (trace.getDebugCode("loadfile"))
			trace.out("loadfile", "MacListener.handleOpenFile received "+ae);
		final String filename = ae.getFilename();
		if (filename != null && filename.length() > 0) {
			LoadFileDialog lfd = new LoadFileDialog(controller.getServer(), controller);
			lfd.new Load(filename).start();
		}
		ae.setHandled(true);
	};
}
