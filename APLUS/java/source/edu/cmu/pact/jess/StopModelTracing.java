/*
 * Created on Oct 19, 2003
 *
 */
package edu.cmu.pact.jess;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author sanket
 *
 */
public class StopModelTracing extends Thread{

	public StopModelTracing(){
		this.constructFrame();
	}


	/**
	 * 
	 */
	private void constructFrame() {
		JFrame frame = new JFrame("Stop");
		JButton cancelBtn = new JButton("Stop Model Tracing");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("STOP Clicked.");
				MTRete.stopModelTracing = true;
				
			}
		});
		frame.getContentPane().add(panel);
		frame.pack();
		frame.show();		
	}

	public static void main(String[] args) {
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
//		this.constructFrame();
	}

}
