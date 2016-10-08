package edu.cmu.pact.miss;

import edu.cmu.pact.Utilities.trace;


//get() will wait until it's not empty, and then return the object itself,


public class MessageDrop {
    
    //Message sent from producer to consumer.
    private String message = "";
    //True if consumer should wait for producer to send message, false
    //if producer should wait for consumer to retrieve message.
//    private boolean empty = true;

    public boolean isEmpty(){
        return message.equals("");
    }

    //only return if it's not empty
    public synchronized String getMessage() {
        while (isEmpty()) {
            try { 
            	if(trace.getDebugCode("miss"))trace.out("miss", "getMessage: message is empty. Waiting for a message...");
                wait();
            } catch (InterruptedException e) {}
        }
        if(trace.getDebugCode("miss"))trace.out("miss", "getMessage: message received!   : " + message);
        String result = message;
        message = "";
//        notifyAll();
        return result;
    }

    public synchronized void waitTime(long timeout) {
        try { 
            wait(timeout);
        }
        catch (InterruptedException e) {}

    }

    
    
    public synchronized void put(String message) {
        //Wait until message has been retrieved.

//        while (!isEmpty()) {
//            try { 
//                System.out.println("put: message is not empty. Waiting for a space to put " + message);
//                wait();
//            } catch (InterruptedException e) {}
//        }
        //Toggle status.
        //Store message.
    	if(trace.getDebugCode("miss"))trace.out("miss", "put: put message " + message);
        this.message = message;
        //Notify consumer that status has changed.
        notifyAll();
    }
}
