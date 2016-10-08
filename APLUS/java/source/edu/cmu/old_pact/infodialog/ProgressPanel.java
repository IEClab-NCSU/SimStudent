package edu.cmu.old_pact.infodialog;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;

class ProgressPanel extends Panel implements Runnable {
	ProgressBar bar;
	Thread ownThread;
	
	public ProgressPanel(ProgressBar b) {
		super();
		bar = b;
		this.setLayout(new FlowLayout(1));
		this.setBackground(bar.getBackgroundColor());
		add(bar);
		ownThread = new Thread(this);
	}
	
	public ProgressPanel() {
		this(new ProgressBar(150, 15, Color.lightGray));
	}
	public ProgressPanel(int w, int h, Color color) {
		this(new ProgressBar(w, h, color));
	}
	
	public void run() {
		bar.runIt(100);
	}
	
	public void startThread() {
		if(ownThread != null && ownThread.isAlive()) {
			ownThread.stop();
			ownThread = null;
		}
		ownThread = new Thread(this);
		ownThread.start();
	}
	
	public void stopThread() {
		if(ownThread != null && ownThread.isAlive()){
			bar.show((double)1.1);
			ownThread.stop();
			ownThread = null;
		}
	}
}