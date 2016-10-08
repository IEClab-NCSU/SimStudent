package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;

public class MessageBox extends Dialog {   
	public static final int NO = 0;   
	public static final int YES = 1;   
	public static final int CANCEL = -1;   
	protected Button yes = null, no = null, cancel = null;   
	protected MultiLineLabel label;  
	
	public MessageBox( Frame parent, String title, String message,
	                      String yes_label, String no_label, String cancel_label)  {     
		super(parent, title, true);    
		this.setForeground(parent.getForeground());     
		this.setBackground(parent.getBackground());     
		this.setLayout(new BorderLayout());     
		label = new MultiLineLabel(message, 40, 10);     
		this.add("Center", label);     
		Panel p = new Panel();     
		p.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));     
		if (yes_label != null) p.add(yes = new Button(yes_label));     
		if (no_label != null) p.add(no = new Button(no_label));     
		if (cancel_label != null) p.add(cancel = new Button(cancel_label));     
		this.add("South", p);     
		this.pack();
		setResizable(false);
	}  
	
	public boolean action(Event e, Object arg)  {
	    if (e.target instanceof Button) {       
	    	this.hide();       
	    	this.dispose();       
	    	if (e.target == yes) 
	    		answer(YES);       
	    	else if (e.target == no) 
	    			answer(NO);       
	    		else answer(CANCEL);       
	    	return true;    
	    } else 
	    	return false;  
	}  
	
	protected void answer(int answer)  {    
		switch(answer) {    
			case YES:       
				yes(); break;    
			case NO:       
				no(); break;    
			case CANCEL:       
				cancel(); break;    
		}  
	}  
	
	public void show()
	{
		super.show();
	}	
	
	protected void yes() {}  
	protected void no() {} 
	protected void cancel() {}
}// YesNoDialog