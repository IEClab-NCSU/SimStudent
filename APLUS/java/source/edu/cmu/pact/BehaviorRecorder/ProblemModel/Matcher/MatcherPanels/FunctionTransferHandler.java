/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

import edu.cmu.pact.BehaviorRecorder.Dialogs.PackageEnumerator;
import edu.cmu.pact.Utilities.trace;

/**
 * 
 */
public class FunctionTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 201404011110L;
	
	/** To handle all but drop actions. */
	private final TransferHandler delegate;

	public FunctionTransferHandler(TransferHandler orig) {
		super("text");
		if(trace.getDebugCode("transferhandler"))
			trace.out("transferhandler", "FunctionTransferHandler.<init>() orig is "+trace.nh(orig));
		delegate = orig;
	}
	
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		if(trace.getDebugCode("transferhandler"))
			trace.out("transferhandler", "FunctionTransferHandler.exportAsDrag("+
					trace.nh(comp)+","+e+","+action+")");
		delegate.exportAsDrag(comp, e, action);
	}
	
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
		if(trace.getDebugCode("transferhandler"))
			trace.out("transferhandler", "FunctionTransferHandler.exportToClipboard("+
					trace.nh(comp)+","+trace.nh(clip)+","+action+")");
		delegate.exportToClipboard(comp, clip, action);
	}
	
	/**
	 * When inserting data from a drag-and-drop, strip the function class and return type if it's a
	 * function signature. 
	 * @param comp argument to superclass method
	 * @param ts
	 * @return
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
	 */
	public boolean importData(TransferHandler.TransferSupport ts) {
		if(trace.getDebugCode("transferhandler"))
			trace.out("transferhandler", "FunctionTransferHandler.importData()"+
					" isDrop "+ts.isDrop()+", transferable "+trace.nh(ts.getTransferable()));
		try {
			Component cmp;
			if(ts.isDrop() && (cmp = ts.getComponent()) instanceof JTextComponent) {
				JTextComponent txtCmp = (JTextComponent) cmp;
				Object value = ts.getTransferable().getTransferData(DataFlavor.stringFlavor);
				if(trace.getDebugCode("transferhandler"))
					trace.out("transferhandler", "FunctionTransferHandler.importData() getTransferData() returns"+
							trace.nh(value)+" \""+value+"\"");
				Point cursor = ts.getDropLocation().getDropPoint();
				if(value != null) {
					String s = PackageEnumerator.extractMethodSignature(value.toString());
					int pos = (cursor != null ? txtCmp.viewToModel(cursor) : txtCmp.getDocument().getLength());
					txtCmp.getDocument().insertString(pos, s, null);
					return true;
				}
			}
		} catch(Exception e) {
			trace.errStack("VectorMatcherPanel.createTextArea() transferHandler.importData() error", e);
		}
		return delegate.importData(ts);
	}
}
