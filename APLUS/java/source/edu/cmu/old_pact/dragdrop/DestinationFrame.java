package edu.cmu.old_pact.dragdrop;

/**
* Class contains DragDestination object - DestinationTextArea
**/

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;

public class DestinationFrame extends Frame{
	DestinationTextArea dta;
	
	public DestinationFrame(){
		dta = new DestinationTextArea();
		setTitle("Destination");
		setLayout(new BorderLayout());
		Panel panel= new Panel();
		panel.setLayout(new FlowLayout());
		panel.add(dta);
		add("Center", panel);
		pack();
		setLocation(10, 300);
		setVisible(true);
	}
}