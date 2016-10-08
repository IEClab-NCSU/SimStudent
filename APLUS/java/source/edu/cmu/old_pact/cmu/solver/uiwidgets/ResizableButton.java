package edu.cmu.old_pact.cmu.solver.uiwidgets;
/*ResizeableButton overrides setLabel() so that it can grow & shrink
  when its label text changes.*/

import java.awt.Button;

public class ResizableButton extends Button {
	public ResizableButton(String label){
		super(label);
	}

	public void setLabel(String l){
		super.setLabel(l);
		invalidate();
		getParent().layout();
	}
}
