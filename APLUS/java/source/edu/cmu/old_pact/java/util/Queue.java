package edu.cmu.old_pact.java.util;

import java.util.EmptyStackException;
import java.util.Vector;


/**
 * This class abstracts a Queue data structure, that is a 
 * first-in, first-out or FIFO list of objects.
 * @version 1.0 of March 5, 1996 
 * @author Elliotte Rusty Harold 
 */
public class Queue extends Vector {
   
    /**
     * This method puts an object at the back of the Queue.
     * @param o The object to be added to the back of the Queue
     */
    public void push(Object o) {
        addElement(o);
    }

    /**
     * This method takes the object from the front of the Queue.
     * @return The object at the front of the Queue
     */
    public Object pop() {
  
        Object o  = peek();
        removeElementAt(0);
        return o;

    }

    /** 
     * Look at the object at the front of the queue 
     * without removing it
     * @return the object at the front of the Queue
     * @throws EmptyStackException if the Queue is empty
     */
    public Object peek() {

        if (size() == 0) throw new EmptyStackException();
        return elementAt(0);
    
    }

    /** 
     * Tests whether the Queue is empty
     * @return true if the Queue is empty, otherwise false
     */
    public boolean empty() {
        return size() == 0;
    }

    public int search(Object o){
        return indexOf(o);
    }
}
