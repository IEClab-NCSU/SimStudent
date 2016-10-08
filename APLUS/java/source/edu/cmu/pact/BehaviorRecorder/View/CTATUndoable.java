package edu.cmu.pact.BehaviorRecorder.View;

import java.io.ByteArrayInputStream;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo.Undoable;
import edu.cmu.pact.Utilities.trace;

/**
 * Serves as an undo interface between JUndo and CTAT.
 * CTAT is completely unaware of this class.
 * @author Kevin Zhang
 *
 */
public class CTATUndoable implements Undoable
{
	private BR_Controller mainController;
	
	public CTATUndoable(BR_Controller ctrl)
	{
		mainController = ctrl;
	}
	
	@Override
	public byte[] saveState() 
	{
		byte[] saveImage = null;
		try {
			//saveImage = mainController.getProblemStateWriter().createBRDDiskImagePublic();
			saveImage = this.mainController.getProblemStateWriter().createBRDDiskImagePublic();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveImage;
	}

	@Override
	public void derive(byte[] b)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		// does this need to be changed?
		//mainController.openBRDFileAndSendStartStateAux(bais, null);
		//trace.out("mg", "CTATUndoable (derive): working with tab " + this.mainController.getTabNumber());
		this.mainController.openBRDFileAndSendStartStateAux(bais, null);

	}

}
