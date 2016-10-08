/*
 * Created on Mar 18, 2005
 * Updated July 21st 2009
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mpschnei, collinl
 *
 * Created on: Mar 18, 2005
 * Edited July 21st 2009
 * 
 * The ProblemModelEvent signals a change to the problem model in 
 * some form.  It has two primary sublasses, a 
 * {@link #NodeEvent} and an
 * {@link #EdgeEvent} which signal changes to nodes and edges respectively.
 * Each of these events will then be thrown by lower-level items.  I 
 * have considered adding a factory class that will be used to process 
 * the production and maintenance of the elements.  
 * 
 * The ProblemModelEvent is a compund event in that it can have 
 * ProblemModelEvents as subevents.  This is designed to handle
 * cases such as Kevin's edge insertion event.  This code will
 * facilitate the wrapping of that event into a single item 
 * and will also support extraction of relevant subitems such 
 * as the atomic edge events.  
 * 
 * These events will be caught by the @see ProblemModelListener
 * which sould be the item used for all accesses.
 *
 * Update:  CtatMenuBar, linkinspectormanager 

 */
public abstract class ProblemModelEvent extends PropertyChangeEvent {

	/* ------------------------------------------------
     * Option Flags.
     * --------------------------------------------- */
	
	/**The nodeId of the desired current state after the event(s) are processed.
	 *This is currently used by PseudoTutorMessageHandler event handler.
	 *Note: This is NOT checked for in individual subevents, but only in the top level event.
	 */
	private int tryToSetCurrentStateTo = -1;
	
    /* ------------------------------------------------
     * Internal Storage.
     * --------------------------------------------- */

	
    List<ProblemModelEvent> Subevents = null;

    
    /* ------------------------------------------------
     * Constructors.
     * --------------------------------------------- */

    /**
     * Generate a new general ProblemModelEvent that will be used to handle
     * the existence of the event and any subitems.
     *
     * @param source:        The object from which this event originates.
     * @param propertyName:  The Property changed.
     * @param oldValue:      Original value of the item.
     * @param newValue:      New value of the object.
     */
    protected ProblemModelEvent(Object source, String propertyName, 
		      Object oldValue, Object newValue) {

	super(source, propertyName, oldValue, newValue);
        this.Subevents = null;
    } 

    /**
     * Generate a new general ProblemModelEvent that will be used to handle
     * the existence of the event and any subitems.
     *
     * @param source:        The object from which this event originates.
     * @param propertyName:  The Property changed.
     * @param oldValue:      Original value of the item.
     * @param newValue:      New value of the object.
     * @param SubEvents:     A list of subevents used for access.
     */
    protected ProblemModelEvent(Object source, String propertyName, 
		      Object oldValue, Object newValue,
		      List<ProblemModelEvent> Subevents) {

        super(source, propertyName, oldValue, newValue);
        this.Subevents = Subevents;
    } 


    
    /* ------------------------------------------------------
     * Methods dealing with the compund nature of things.
     * --------------------------------------------------- */

    /** Return true if this is a compund event with children. */
    public boolean isCompoundEventP() {
	
	if (this.Subevents == null) { return false; }
	else { return true; }
    }

    /** Retreive the subevents of this event if any. */
    public List<ProblemModelEvent> getSubevents() {
      return this.Subevents;
    }
    
    public void setSubevents(List<ProblemModelEvent> subEvents){
    	this.Subevents =subEvents;
    }

    //     /**
    //      * @param Recursive: True if this should recurse to subitems.
    //      * @param Atomic:    True if w
    //      * Start here.
    //      */
    
    //     /** 
    //      * @param Type:      Type of children desired.
    //      * @param Recursive: True if this should recurse to subitems.
    //      *
    //      * This code will extract children of the specified type 
    //      * from the list and return a list of them.  This will
    //      * generate a type list and then make a recursive call
    //      * to the list search.
    //      */
    //     public getChildrenByType(Class ChildType, boolean Recursive) {
    
    // 	List<Class> TypeList = new ArrayList<Class>(1);
    // 	TypeList.add(ChildType);
    // 	return this.getChildrenByTypeListI(TypeList, Recursive, false);
    //     }
    
    //     /** 
    //      * @param Type: Type of children desired.
    //      * @param Recursive: True if this should recurse to subitems.
    //      *
    //      * This code will extract atomic children of the 
    //      * specified type from the list and return a list of them.  
    //      * This will generate a type list and then make a recursive 
    //      * call to the list search.
    //      */
    //     public getAtomicChildrenByType(Class ChildType, boolean Recursive) {
    
    // 	List<Class> TypeList = new ArrayList<Class>(1);
    // 	TypeList.add(ChildType);
    // 	return this.getChildrenByTypeListI(TypeList, Recursive, true);
    //     }
    
    
    //     /** 
    //      * @param TypeList: List of Type of children desired.
    //      * @param Recursive: True if this should recurse to subitems.
    //      *
    //      * This code will extract children of the specified types 
    //      * from the list and return a list of them.  This will
    //      * generate a type list and then make a recursive call
    //      * to the list search.
    //      */
    //     public getChildrenByType(List<Class> ChildTypes, boolean Recursive) {
    
    // 	return this.getChildrenByTypeListI(TypeList, Recursive, false);
    //     }
    
    //     /** 
    //      * @param TypeList: List of Type of children desired.
    //      * @param Recursive: True if this should recurse to subitems.
    //      *
    //      * This code will extract atomic children of the specified 
    //      * types from the list and return a list of them.  This 
    //      * will generate a type list and then make a recursive call
    //      * to the list search.
    //      */
    //     public getChildrenByType(List<Class> ChildTypes, boolean Recursive) {
    
    // 	return this.getChildrenByTypeListI(TypeList, Recursive, true);
    //     }
    

    /** 
     * Collect all of the nodes for a single type.  This code 
     * uses the collect code below but wraps up the list for 
     * the benefit of the user.
     * <br>
     *
     * @param EventType:  The class of interest.  Must be one
     *         of the descendants of {@link #ProblemModelEvent}
     * @param Recursive:  If true will descend to subevents of
     *            the subevents. 
     * @param ReturnAtomic:  True if we only want atomic items.
     * @param ReturnCompound: True if we want compund items.
     *
     * @return A LinkedList containing the collected events.
     */
    public List<ProblemModelEvent> collectTypeSubevents(Class Type,
							boolean Recursive,
							boolean ReturnAtomic,
							boolean ReturnCompound) {
	ArrayList<Class> TypeList = new ArrayList();
	TypeList.add(Type);
	return this.collectSubevents(null, TypeList, Recursive,
				     ReturnAtomic, ReturnCompound);
    }
	

    /** 
     * Locate all subevents that have as their source the
     * specified item.  This is done to facilitate selection
     * of items relevant to a particular edge or node.
     *
     * @param EventSource:  The item of interest. 
     * @param Recursive:  If true will descend to subevents of
     *            the subevents. 
     * @param ReturnAtomic:  True if we only want atomic items.
     * @param ReturnCompound: True if we want compund items.
     *
     * @return A LinkedList containing the collected events.
     */
    public List<ProblemModelEvent> collectSourceSubevents(Object Source,
							  boolean Recursive,
							  boolean ReturnAtomic,
							  boolean ReturnCompound) {
	ArrayList<Object> SourceList = new ArrayList();
	SourceList.add(Source);
	return this.collectSubevents(SourceList, null, Recursive,
				     ReturnAtomic, ReturnCompound);
    }


    /** 
     * Extract children of the specified type(s) and with the specified
     * source(s) from the list of descendants.  If TypeList is null then 
     * all will be returned.
     * <br>
     *
     * If Recursive is true then this will recurse to the children
     * of any compound events.
     * <br>
     *
     * If ReturnAtomic is true then this will return atomic events.  
     * Similarly if ReturnCompund is true then we will return 
     * compound events.
     * <br>
     *
     * The return process is parent-first thus given a tree of 
     * the form (a(b,c), d, e(f,g(h))) the resulting list will 
     * be flattened to (a, b, c, d, e, f, g, h).
     *
     * @param SourceList:     List of source choices or null.
     * @param TypeList:       List of Type of children desired.
     * @param Recursive:      True if this should recurse to subitems.
     * @param ReturnAtomic:  True if we only want atomic items.
     * @param ReturnCompound: True if we want compund items.
     *
     * @return A LinkedList containing the collected events.
     */
    public List<ProblemModelEvent> collectSubevents(List<Object> SourceList,
						    List<Class> TypeList,
						    boolean Recursive,
						    boolean ReturnAtomic,
						    boolean ReturnCompound) {
	
	boolean Match = false;
	
	// Generate a result list for storing the relevant items.
	List<ProblemModelEvent> Result = new LinkedList<ProblemModelEvent>();

	/* Iterate over the items/ */
	for (ProblemModelEvent E : this.Subevents) {
	
	    /* First check against the sources and if that passes 
	     * check against the types.  If either SourceList
	     * or TypeList is null then this code will accept
	     * all elements for that feature. */
	    if (((SourceList == null) || (this.checkSourceMatch(SourceList, E)))
		&& ((TypeList == null) || (this.checkTypeMatch(TypeList, E)))) {
		    
		/* If this item matches test whether it is 
		 * compound and then behave appropriately. */
		if (E.isCompoundEventP()) {
		    // Add it if the ReturnCompound is true 
		    if (ReturnCompound) { Result.add(E); }
		    // Then recurse if recursion is to be had.
		    if (Recursive) {
			Result.addAll(E.collectSubevents(SourceList, 
							 TypeList, 
							 Recursive,
							 ReturnAtomic, 
							 ReturnCompound));
		    }
		}
		/* Else it is an atomic event and return as needed. */
		else if (ReturnAtomic) { Result.add(E); }
	    }
	}
	// Now return the completed list.
	return Result;
    }


    /**
     * Check whether the event is an instance of any of the
     * classes in the list.
     *
     * @param EventSource:  The item of interest. 
     * @param Recursive:  If true will descend to subevents of
     *            the subevents. 
     * @param ReturnAtomic:  True if we only want atomic items.
     * @param ReturnCompound: True if we want compund items.
     *
     * @return: True if a match is found.
     */
    private boolean checkTypeMatch(List<Class> TypeList, ProblemModelEvent E) {
	// For each of the classes in the list.
	for (Class Type : TypeList) {
	    // If the type matches then set it.  
	    if (Type.isInstance(E)) { return true; }
	}
	return false;
    }
		
     

	

    /**
     * Check whether the event's source is in the list and if
     * so return true.
     *
     * @param SourceList: A nonempty list of sources to check.
     * @param Event:    The event to match against the list.
     *
     * @return: true if the event's source is in the list.
     */
    private boolean checkSourceMatch(List<Object> SourceList, 
				     ProblemModelEvent E) {
	// For each of the classes in the list.
	for (Object Src : SourceList) {
	    // If the type matches then set it.  
	    if (E.getSource() == Src) { return true; }
	}
	return false;
    }


    /* ------------------------------------------------------
     * Output of events. 
     * --------------------------------------------------- */
    
    /** Return the event in the form of a string. */
    public String toString() {
	String Str = getClass().getName();
	if (this.isCompoundEventP()) { Str = Str + "(compound)"; }
	Str = Str + ": " + getPropertyName();
    	return Str;
    }

	public void setTryToSetCurrentStateTo(int tryToSetCurrentStateTo) {
		this.tryToSetCurrentStateTo = tryToSetCurrentStateTo;
	}

	public int getTryToSetCurrentStateTo() {
		return tryToSetCurrentStateTo;
	}

}



// Need static collection mechanisms and selection ones.