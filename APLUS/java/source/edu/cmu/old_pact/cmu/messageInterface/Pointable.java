package edu.cmu.old_pact.cmu.messageInterface;

/** 
* The Pointable interface for those objects that contain the Objects to be pointed to.
*/ 

public interface Pointable {
	/**
	* Points to a specified toWhat Object in Pointable object.
	* @param toWhat - an Object to be pointed to.
	*/
    void point(Object toWhat);
    /**
	* Releases a specified toWhat Object in Pointable object.
	* @param toWhat - an Object to be released.
	*/
    void unPoint(Object toWhat);
}