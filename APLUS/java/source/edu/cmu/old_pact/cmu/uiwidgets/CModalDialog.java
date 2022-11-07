package edu.cmu.old_pact.cmu.uiwidgets;
import edu.cmu.pact.Utilities.trace;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Toolkit;

public class CModalDialog /*extends Frame {*/ extends Dialog {
	boolean m_bOk;
	protected ModalDialogListener parent=null;
	Event m_ModalDone;
	String command;
	int width = 300;
	int height = 200;
	private static CModalDialog modal_inst = null;
	
	public CModalDialog(ModalDialogListener pa,String title,boolean modal)
	{
		super((Frame)pa,title,modal);
		//super(title);
		parent=pa;
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width-width)/2, (d.height-height)/2);
		resize(width, height);
		setResizable(false);
	}
	
	public CModalDialog(ModalDialogListener parent,String title,boolean modal, String cmd)
	{
		this(parent,title,modal);
		command = cmd;
	}
	
	public void setVisible(boolean v){
		if(v){
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width-width)/2, (d.height-height)/2);
		}
		super.setVisible(v);
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getArgument() {
		return "";
	}

	public void setCommand(String cmd) {
		command = cmd;
	}
	
	public void finishModalDialog(boolean key)
	{
		//trace.out("starting finishModalDialog...");
		//trace.out("argument is "+getArgument());

		if (parent != null)
			parent.modalDialogPerformed(new ModalDialogEvent(parent,command,getArgument(),key), this);
	}		

	public boolean keyDown(Event e, int key) {
		if (e.id == Event.KEY_PRESS && (key == 10 || key == 3)) {
			//trace.out("Got return or enter");
			finishModalDialog(true);
			return true;
		}
		else
			return false;
	}
		
	public boolean action(Event e,Object arg) {
		if(e.arg instanceof String){
			String str = (String)e.arg;
			if (str.toUpperCase().indexOf("OK") != -1) {
				//trace.out("got OK in modal dialog");
				finishModalDialog(true);
				return true;
			}
			else if (str.toUpperCase().indexOf("CANCEL") != -1) {
				//trace.out("got cancel in modal dialog");
				finishModalDialog(false);
				return true;
			}
		}

		trace.out("got modal dialog action event: "+e.arg);
		return false;
	}
}						
