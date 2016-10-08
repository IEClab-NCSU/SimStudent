package edu.cmu.old_pact.cmu.messageInterface;
import edu.cmu.old_pact.dormin.DorminException;

/**
* An abstract class which holds Pointable target and 
* a singal Object which gets poined to in a Pointable target. 
*/

public abstract class Pointer {
	/**
	* A Pointable target.
	*/
    private Pointable target;
    /**
	* An Object to be pointed to in a Pointable target.
	*/
    private Object pointTo;
    /**
	* An abstract method used to send pointTo Object to the Pointable target.
	*/   
    public abstract void point() throws DorminException;
    /**
	* An abstract method used to release pointTo Object in the Pointable target.
	*/  
    public abstract void unPoint() throws DorminException;
    /**
	* An abstract method that stores pointTo Object.
	* @param  pointTo - a specified Object to be stored in this Pointer.
	*/ 
    public abstract void setPointTo(Object pointTo);
}