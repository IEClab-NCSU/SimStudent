package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Dialog;
import java.util.EventListener;

public interface ModalDialogListener extends EventListener {
	public abstract void modalDialogPerformed(ModalDialogEvent e, Dialog eventHolder);
}
