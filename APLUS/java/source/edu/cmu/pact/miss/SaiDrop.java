package edu.cmu.pact.miss;

import edu.cmu.pact.Utilities.trace;



//This class is used to getting the SAI from the Student Interface, or any other process that
//responds by modifying an Sai object
public class SaiDrop {
    
    //Message sent from producer to consumer.
    private Sai sai = null;
    
    public boolean isEmpty(){
        return sai==null;
    }

    //only return when it's not empty
    public synchronized Sai getSai() {
        while (isEmpty()) {
            try { 
            	if(trace.getDebugCode("miss"))trace.out("miss", "get: SAI is empty. Waiting for a message...");
                wait();
            } catch (InterruptedException e) {}
        }
   
        Sai result = sai;
        sai = null;
        //notifyAll();
        if(trace.getDebugCode("miss"))trace.out("miss", "SAI received in getSai() is" + result);
        return result;
    }

        
    public synchronized void put(Sai saiIn) {
        //Wait until message has been retrieved.

//        while (!isEmpty()) {
//            try { 
//                trace.out("put: SAI is not empty. Waiting for a space to put " + saiIn);
//                wait();
//            } catch (InterruptedException e) {}
//        }
        //Toggle status.
        //Store message.
    	if(trace.getDebugCode("miss"))trace.out("miss", "put: put message " + saiIn);
        this.sai = saiIn;
        //Notify consumer that status has changed.
        notifyAll();
    }
}
