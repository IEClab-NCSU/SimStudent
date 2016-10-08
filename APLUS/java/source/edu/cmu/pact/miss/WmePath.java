/**
 * WmePath.java
 *
 *
 * Created: Thu Jan 13 21:52:59 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.Vector;

import jess.Fact;
import edu.cmu.pact.Utilities.trace;

// import org.jatha.*;
// import org.jatha.read.*;
// import org.jatha.dynatype.*;

public class WmePath implements Cloneable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

//     // A Jatha List interpriter
//     private static Jatha jatha = new Jatha( false, false );
//     Jatha getJatha() { return this.jatha; }
//     void setJatha( Jatha jatha ) { this.jatha = jatha; }
//     // 
//     private static boolean jathaStarted = false;
//     private void setJathaStarted( boolean flag ) { jathaStarted = flag; }

    /*
      s = getJatha().parse( exp, LispParser.PRESERVE );
      op = s.car().toString();
    */

    // The AmlRete
    //
    private AmlRete rete;
    
    private AmlRete getRete() { return this.rete; }
    private void setRete( AmlRete rete ) { this.rete = rete; }
    
    // A list of WME-path node
    //     
    private Vector /* of WmePathNode */ nodes = new Vector();
    /**
     * Return a list of WME Nodes
     *
     * @return a <code>Vector</code> value
     */
    Vector getNodes() { return this.nodes; }
    private void setNodes( Vector /* of WmePathNode */ nodes ) {
	this.nodes = nodes;
    }
    private void addNodes( WmePathNode node ) { nodes.add( node ); }
    WmePathNode getLastNode() {
	return (WmePathNode)nodes.lastElement();
    }
    // Return the i-th wme-path node 
    private WmePathNode getWmePathNode( int i ) {
	return (WmePathNode)this.nodes.get(i);
    }
    /**
     * Describe <code>length</code> method here.
     *
     * @return an <code>int</code> value
     */
    public int length() { return nodes.size(); }

    // A degree of generality: Assign 0 for a WmePathNode that is most
    // specific (i.e., whose ValueVector is a list of ?'s followed by
    // ?var and $?) and assign 1 for others (which should be the most
    // general in the current implementation whose ValueVector is ($?
    // $var $?).  Then, order 0s and 1s from the top WME (i.e.,
    // PROBLEM WME) and convert it into a binary number.  
    //
    private int generality = -1;
    private int getGenerality() {
	if ( this.generality == -1 ) {
	    setGenerality();
	}
	return  this.generality;
    }
    private void setGenerality() {

	int generality = 0;

	Vector /* of WmePathNode */ nodes = getNodes();
	Iterator iterator = nodes.iterator();
	while ( iterator.hasNext() ) {
	    WmePathNode node = (WmePathNode)iterator.next();
	    generality = generality * 2 + (node.isMostSpecific() ? 0 : 1);
	}
	this.generality = generality;
    }
    private void resetGenerality() { this.generality = -1; }

    // -
    // - Constructor  - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>WmePath</code> instance.
     *
     */
    public WmePath( AmlRete rete ) {

	setRete( rete );

	/*
	if ( !jathaStarted ) {
	    jatha.init();
	    jatha.start();
	    setJathaStarted( true );
	}
	*/
    }

    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - -
    // -
  
    /**
     * Ginen a Jess fact, add that into the wme-path as the last
     * element of the path.  If the wme-path is not empty (i.e., it's
     * been on a search process), then the fact must appear the
     * offspring slot of the last wme-path element.  The last element
     * then shoule be replaced with the most-specific wme-path element
     * by substituting the offspring slot with the most-specific slot
     * reference (e.g., (cell ? ? ?var $?) for the 3rd element of the
     * cell slot).  
     *
     * @param fact a <code>Fact</code> value
     **/
    void add( Fact fact, String ParentSlotName,String letter ) {
    	WmePathNode next;
	if ( getNodes().isEmpty() ) {
	    addNodes( new WmePathNode( fact, getRete(),letter ) );
	} else {
		WmePathNode last=getLastNode();
		String var = last.replaceVar( fact );
		next=new WmePathNode( var, fact, getRete());
		next.setParentSlot(ParentSlotName);
		last.setChild(next);
	    addNodes(next) ;
	}
    }

    WmePath generalize() {

	int generality = getGenerality();

	if(trace.getDebugCode("miss"))trace.out("miss", "WmePath.generalize()");
	if(trace.getDebugCode("miss"))trace.out("miss", "  generality = " + generality );
	if(trace.getDebugCode("miss"))trace.out("miss", "  wmtPath = " + this ); 

	if ( (generality + 1) == (1 << length()) - 1 ) {
	    return null;
	}

	WmePath generalizedPath = (WmePath)clone();

	// Identify the WmePathNode that must be generalized next
	// based on the generality.  Let's count nodes in the wmePath
	// from the leaf to the root where the root corresponds with
	// the PROBLEM WME.  The number of the wmePath node, say N, is
	// the first place from the right of a non-zero digit (i.e.,
	// 1) in a binary representation of (generality+1).  For
	// exampample, if generality = 5 (i.e., the 1st and the 3rd
	// nodes from the leaf nodes are alreay "general" nodes),
	// since a binary representation of 6 (N+1) is 0110, the 2nd
	// node from the leaf must be generalized next.
	WmePathNode targetNode = null;
	int index = -1;
	for ( int i = length()-2; i >= 0; i-- ) {

	    trace.out("i = " + i);
	    trace.out( "(generality + 2) % (2 << i) = " +
				(generality + 2) % (2 << i) );
	    
	    if ( ((generality + 2) % (2 << i)) == 0 ) {
		
		index = length() - i - 2;
		targetNode = (WmePathNode)getNodes().get( index );
		trace.out("targetNode: " + targetNode);
		break;
	    }
	}
	WmePathNode generalizedNode = targetNode.generalize();
	trace.out("generalizedNode: " + generalizedNode);

	generalizedPath.getNodes().setElementAt( generalizedNode, index );
	generalizedPath.resetGenerality();

	return generalizedPath;
    }

    /**
     * Return a generalized WmePath.  The argument targetPaths
     * specifies which WmePathNode must be replaced.  Read targetPaths
     * as a binary number where 1's shows the WmePathNode to be
     * repaced.
     *
     * @param targetPaths an <code>int</code> value
     * @return a <code>WmePath</code> value
     **/
    WmePath generalize( int targetPaths ) {
	
	WmePath generalizedPath = (WmePath)clone();

	// For each WmePathNode, see if it is a targetPath, and if so,
	// replace it with a generalized node
	for ( int i = 0; i < length(); i++ ) {
		if ( (1 << i & targetPaths) != 0 ) {
		int index = length() - i -1;
		WmePathNode targetNode = (WmePathNode)getNodes().get(index);
		WmePathNode generalizedNode = targetNode.generalize();
		if ( generalizedNode == null) {
		    return null;
		}
		generalizedPath.getNodes().setElementAt( generalizedNode,
							 index );
	    }
	}
	generalizedPath.resetGenerality();

	return generalizedPath;
    }

    /**
     * Describe <code>isUnifiable</code> method here.
     *
     * @param wmePath a <code>WmePath</code> value
     * @return a <code>boolean</code> value
     */
    boolean isUnifiable( WmePath wmePath ) {

	boolean test = ( this.length() == wmePath.length() ) ? true : false;

	// The unifiable paths must be the same length, and ...
	if  ( test ) {

	    Vector thisNodes = this.getNodes();
	    Vector thatNodes = wmePath.getNodes();
	    int size = thisNodes.size();

	    // corresponding nodes are all unifiable
	    for ( int i = 0; i < size; i++ ) {

		WmePathNode thisNode = (WmePathNode)thisNodes.get(i);
		WmePathNode thatNode = (WmePathNode)thatNodes.get(i);
		if ( !thisNode.isUnifiable( thatNode ) ) {
		    test = false;
		    break;
		}
	    }

	    
	    /*
	    Iterator thisIterator = this.getNodes().iterator();
	    Iterator thatIterator = wmePath.getNodes().iterator();

	    // corresponding nodes are all unifiable
	    while ( thisIterator.hasNext() ) {

		WmePathNode thisNode = (WmePathNode)thisIterator.next();
		WmePathNode thatNode = (WmePathNode)thatIterator.next();

		if ( !thisNode.isUnifiable( thatNode ) ) {

		    test = false;
		    break;
		}
	    }
	    */
	}

	return test;
    }

    /**
     * Returns a list (Vector) of "floating WMEs" which are
     * WmePathNode referred by a variable following a multivariable
     * (i.e., $?var...).
     *
     * @return a <code>Vector</code> value
     **/
    Vector /* WmePathNode */ findFloatingWme() {

	Vector /* WmePathNode */ floatingWmes = new Vector();

	for ( int i = 0; i < this.length() -1; i++ ) {

	    // If the i-th WmePathNode contains a multivariable, then
	    // keep the (i+1)-th WmePathNode, since that's the actual
	    // floating WME
	    if ( getWmePathNode(i).hasMultivariable() ) {
		floatingWmes.add( getWmePathNode(i+1) );
	    }
	}
	return floatingWmes;
    }
    
    /**
     * Returns TRUE if this wme-path contains a given fact as a
     * WmePathNode
     *
     * @param fact a <code>Fact</code> value
     * @return a <code>boolean</code> value
     **/
    boolean hasNode( Fact fact ) {

	boolean test = false;

	for (int i = 0; i < getNodes().size(); i++) {

	    WmePathNode node = (WmePathNode)getNodes().get(i);
	    if ( node.hasSameFact( fact ) ) {
		
		test = true;
		break;
	    }
	}
	return test;
    }

    /**
     * Returns TRUE if this wme-path contains a given WmePathNode
     *
     * @param wmePathNode a <code>WmePathNode</code> value
     * @return a <code>boolean</code> value
     **/
    boolean hasNode( WmePathNode wmePathNode ) {

	return getNodes().contains( wmePathNode );
    }

    
    /**
     * Returns the index of a given wme-path node in this wme-path
     *
     * @param wmePathNode a <code>WmePathNode</code> value
     * @return an <code>int</code> value
     */
    int indexOf( WmePathNode wmePathNode ) {

	return getNodes().indexOf( wmePathNode );
    }

    public WmePathNode lookupNodeOfType( String type ) {

	WmePathNode theNode = null;
	for (int i = 0; i < getNodes().size(); i++) {
	    WmePathNode node = (WmePathNode)getNodes().get(i);
	    if ( node.isWmeType( type ) ) {
		theNode = node;
		break;
	    }
	}
	return theNode;
    }
    /**
     * remove the last node from the path and return it
     * @return the last node in the path or null if the path is empty
     */
    public WmePathNode removeLastNode()
    {
    	WmePathNode last=(WmePathNode)nodes.remove(nodes.size()-1);
    	//update the child pointer of it's parent
    	WmePathNode nextToLast=getLastNode();
    	if(nextToLast!=null)
    		nextToLast.setChild(null);
    	return last;
    }
    // -
    // - Override methods - - - - - - - - - - - - - - - - - - - -
    // - 

    public Object clone() {

	WmePath cloneWmePath = null;
	try {
	    cloneWmePath = (WmePath)super.clone();
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}

	Vector cloneNodes = new Vector();
	Vector nodes = getNodes();
	int size = nodes.size();
	for (int i = 0; i < size; i++) {
	    WmePathNode cloneNode =
		(WmePathNode)((WmePathNode)nodes.get(i)).clone();
	    cloneNodes.add( cloneNode );
	}
	/*
	Iterator nodesIterator = nodes.iterator();
	while ( nodesIterator.hasNext() ) {
	    WmePathNode cloneNode =
		(WmePathNode)((WmePathNode)nodesIterator.next()).clone();
	    cloneNodes.add( cloneNode );
	}
	*/

	cloneWmePath.setNodes( cloneNodes );

	return cloneWmePath;
    }

    public String toString() {

	String str = "";
	
	Iterator paths = getNodes().iterator();
	while ( paths.hasNext() ) {
	    str += ((WmePathNode)paths.next()).toString() + "\n";
	}

	return str;
    }

    /*
    public String toString( int pathID ) {

	String str = "";
	
	Iterator paths = getNodes().iterator();
	while ( paths.hasNext() ) {
	    str += ((WmePathNode)paths.next()).toString( pathID ) + "\n";
	}

	return str;
    }
    */

}

//
// end of WmePath.java
// 
