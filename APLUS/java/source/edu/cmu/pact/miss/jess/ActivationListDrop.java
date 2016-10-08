package edu.cmu.pact.miss.jess;

import java.util.ArrayList;

public class ActivationListDrop {

	private ArrayList activations = null;
	
	private boolean empty = true;
	
	public synchronized ArrayList take() {
		
		while(empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		empty = true;
		notifyAll();
		return activations;
	}
	
	public synchronized void put(ArrayList activations) {
		
		while(!empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		empty = false;
		this.activations = activations;
		notifyAll();
	}

}
