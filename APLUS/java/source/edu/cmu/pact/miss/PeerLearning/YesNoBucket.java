package edu.cmu.pact.miss.PeerLearning;

import edu.cmu.pact.Utilities.trace;

public class YesNoBucket
{
	//Message sent from producer to consumer.
    private String yesNo = null;
    
    public boolean isEmpty(){
        return yesNo==null;
    }

	 public synchronized String waitForYesNo() {
	        while (isEmpty()) {
	            try { 
	            	if(trace.getDebugCode("ss"))trace.out("ss", "waitForYesNo: yesNo is empty. Waiting for a message...");
	                wait();
	            } catch (InterruptedException e) {}
	        }
	        String result = yesNo;
	        yesNo = null;
	        //notifyAll();
	        return result;
	    }

	        
	    public synchronized void put(String yesNoIn) {

	    	if(trace.getDebugCode("ss"))trace.out("ss", "put: put message " + yesNoIn);
	        this.yesNo = yesNoIn;
	        //Notify consumer that status has changed.
	        notifyAll();
	    }
}
