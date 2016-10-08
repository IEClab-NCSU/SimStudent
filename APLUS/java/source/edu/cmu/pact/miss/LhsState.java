/**
 * LhsState.java
 *
 *	A state representation for searching LHS of the production
 *	rules.
 *
 * Created: Wed Jan 12 13:17:59 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.Vector;

public class LhsState implements Cloneable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // A list of WME-paths that have been explored
    private Vector /* of WmePath */ wmePathList = new Vector();
    void addWmePath( WmePath path ) { wmePathList.add( path ); }
    Vector /* of WmePath */ getWmePathList() { return this.wmePathList; }
    void setWmePathList( Vector wmePathList ) {this.wmePathList = wmePathList;}
    int numWmePath() { return wmePathList.size(); }
    /**
     * Return i-th WmePath
     *
     * @param i an <code>int</code> value
     * @return a <code>WmePath</code> value
     */
    WmePath getWmePath( int i ) {
	return (WmePath)this.wmePathList.get(i);
    }

    // A number of target WMEs that have been taken care of.  In other
    // words, the number of wme-paths in the wmePath list
    int numWmeProcessed() { return wmePathList.size(); }

    // NOTICE: The order of the targetWme matters!!
    // 
    // The target WMEs whose wme-path must appear in LHS.  They
    // consists of the SEEDS followed by the SELECTION specified in an
    // example.  Each target WME is reprecented as a string of
    // "<wme-type>/<wme-name>"
    //
    private static Vector /* of String */ targetWme;
    private void clearTargetWme() { targetWme = new Vector(); }
    private void addTargetWme( String wmeTypeName ) {
	this.targetWme.add( wmeTypeName );
    }
    private Vector getTargetWme() { return this.targetWme; }
    // Number of the target WME, which automatically determines a
    // depth of the search for LHS
    private int targetWmeSize() { return this.targetWme.size(); }
    //
    String nextTargetWme() {
	if ( numWmeProcessed() < targetWmeSize() ) {
	    return (String)this.targetWme.get( numWmeProcessed() );
	} else {
	    return null;
	}
    }

    // Wme-constraint
    //
    private Vector /* WmeConstraint */ wmeConstraints = new Vector();
    void addWmeConstraint( Vector /* WmeConstraint */ constraints ) {
	for ( int i = 0; i < constraints.size(); i++ ) {
	    WmeConstraint constraint = (WmeConstraint)constraints.get(i);
	    this.wmeConstraints.add( constraint );
	}
    }

    // Represents if the state holds conditions to be a goal state,
    // which in this case means that all wme paths are consistent with
    // all the seeds in examples.
    // 
    // private boolean isGoalState = false;
    boolean isGoalState() {
	return numWmeProcessed() == targetWmeSize();
    }
    // private void setIsGoalState( boolean test ) { this.isGoalState = test; }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * This constructor is called only for creating an initial state.
     * Given an example, it initializes the target WME, which is a
     * static field, so that all successor states can refer to that
     * value.
     *
     **/
    public LhsState( Instruction instruction ) {

	clearTargetWme();

	// Add seed WMEs to the targetWme
	Vector /* of String */ seeds = instruction.getSeeds();
	
	Iterator iterator = seeds.iterator();
	while ( iterator.hasNext() ) {
	    String seed = (String)iterator.next();
	    //Saved as MAIN::(name of component)
	    String wmeTypeName = seed.substring( 0, seed.lastIndexOf('|') );
	    addTargetWme( wmeTypeName );
	}
	// Add selection to the targetWme only when it is not
	// "MAIN::button/done" which is sent when [DONE] button is
	// pressed
	String selection = instruction.getSelection();
	if ( !selection.split("\\|")[1].toUpperCase().equals( "DONE" ) ) {
	    addTargetWme( selection );
	}
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Describe <code>findFloatingWme</code> method here.
     *
     * @return a <code>Vector</code> value
     */
    Vector /* WmePathNode */ findFloatingWme() {

	Vector /* WmePathNode */ wmePathNode = new Vector();

	for (int i = 0; i < numWmePath(); i++) {
	    WmePath wmePath = getWmePath(i);
	    wmePathNode.addAll( wmePath.findFloatingWme() );
	}
	return wmePathNode;
    }

    /**
     * Returns the index of given wmePathNode in the stored wme-paths
     *
     * @param wmePathNode a <code>WmePathNode</code> value
     * @return an <code>int</code> value
     */
    //Gustavo 29March2007:traverses wmePathList until it finds a WmePath that contains wmePathNode
    //
    int wmePathIndexOf( WmePathNode wmePathNode ) {

	int index = -1;

	for (int i = 0; i < numWmePath(); i++) {

	    if ( getWmePath(i).hasNode( wmePathNode ) ) {
		index = i;
		break;
	    }
	}
	return index;
    }

    int wmePathNodeIndexof( WmePathNode wmePathNode ) {

	int index = -1;

	for (int i = 0; i < numWmePath(); i++) {

	    WmePath wmePath = getWmePath(i);
	    if ( wmePath.hasNode( wmePathNode ) ) {

		index = wmePath.indexOf( wmePathNode );
		break;
	    }
	}
	return index;
    }

    /**
     * Duplicate the state
     *
     * @return a <code>LhsState</code> value
     */
    public Object clone() {

	LhsState cloneLhsState = null;

	try {
	    cloneLhsState = (LhsState)super.clone();
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}

	Vector /* of WmePath */ wmePathList = getWmePathList();
	Vector /* of WmePath */ cloneWmePathList = new Vector( wmePathList );
	cloneLhsState.setWmePathList( cloneWmePathList );
	
	return cloneLhsState;
    }

    public String toString() {

	String str = "<LhsState targetWme: " + getTargetWme().toString();
	str += " wmePath:\n" + getWmePathList().toString();

	return str;
    }
}

//
// end of LhsState.java
// 
