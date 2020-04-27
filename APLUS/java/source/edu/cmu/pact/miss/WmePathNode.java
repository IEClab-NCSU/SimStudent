/**
 * WmePathNode.java
 *
 *
 * Created: Sat Jan 15 21:21:54 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Value;
import jess.ValueVector;
import jess.Variable;

public class WmePathNode implements Cloneable {

    // -
    // - Field - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    private static int varSymNum = 0;
    private static String genVarSym() { return "?var" + varSymNum++; }
    private static String genMvSym() { return "m" + varSymNum++; }
    private static String genVarSym(String letter) { 
    	varSymNum++;
    	return "?var" + varSymNum+letter; 
    }
    /**
     * a pointer to the direct child of this node in its particular WMEPath
     */
    private WmePathNode child;
    // A Rete net held in the WME-path 
    private AmlRete rete;
    private AmlRete getRete() { return this.rete; }
    private void setRete( AmlRete rete ) { this.rete = rete; }

    // Variable that binds this WME-path node
    // 
    private String variable;
    /**
     * Returns a variable that binds this WME-path node
     *
     * @return a <code>String</code> value
     */
    String getVariable() { return this.variable; }
    private void setVariable( String var ) { this.variable = var; }

    // Fact
    //
    private Fact wme;
    /**
     * Returns the WME 
     *
     * @return a <code>Fact</code> value
     */
    public Fact getWme() { return this.wme; }
    private void setWme( Fact wme ) { this.wme = wme; }

    // Slots that must be taken care of to identify unifiable WMEs
    //
    
    //String[] sensitiveSlots = {"interface-elements", "columns", "cells" };
    static String[] sensitiveSlots;
    //Set as the sensitive slots are loaded in AmlRete
    static void setSensitiveSlots(String[] slots) {
    	sensitiveSlots = slots;
    }
    
	private String parentSlotName;
    private String[] getSensitiveSlots() { return this.sensitiveSlots; }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>WmePathNode</code> instance.
     *
     */
    public WmePathNode( Fact wme, AmlRete rete ) {
	this( genVarSym(), wme, rete );
    }

    public WmePathNode( Fact wme, AmlRete rete, String letter ) {
    	this( genVarSym(letter), wme, rete );
        }
    
    
    public WmePathNode( String var, Fact wme, AmlRete rete ) {
	// setRete() must come first for setWme calls getRete()
	setRete( rete );
	setVariable( var );
	setWme( wme );
    }

   
    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -
    
    /**
     * set the child of this node
     * @param myChild
     */ 
    void setChild(WmePathNode myChild)
    {
    	child=myChild;
    }
    /**
     * @return  child of this node in its WmePath
     */
    
    public WmePathNode getChild()
    {
    	return child;
    }
    /**
     * Replace the slot value, which should be a list, with a list of
     * '?' followed by a variable symbol at the place where the
     * specified wme fact is located, then followed by '$?'  For
     * example, (cell <Fact-1> <Fact-2> ... <Fact-n>) would be (cell ?
     * ? ?varX $?) when <Fact-3> is given as the argument.  Return the
     * variable symbol. 
     *
     * @param wme a <code>Fact</code> value
     * @return a <code>String</code> value */
    String replaceVar( Fact targetWme ) {
    
	String var = genVarSym();
	Fact wme = getWme();
	try {
	    Context c = getRete().getGlobalContext();

	    Vector childSlots = getRete().getWmeChildSlots( wme.getName() );
	    Iterator iter=childSlots.iterator();
	    while(iter.hasNext())
	    {
	    String childSlot=(String)iter.next();
	    ValueVector children = wme.getSlotValue( childSlot ).listValue(c);

	    ValueVector vVector = new ValueVector();
	    for ( int i = 0; i < children.size(); i++ ) {
		Fact fact = children.get(i).factValue(c);
		if ( fact.equals( targetWme ) ) {
		    vVector.add( new Variable(var.substring(1), RU.VARIABLE) );
		} else {
		    vVector.add( new Variable( "", RU.VARIABLE ) );
		}
	    }
	    wme.setSlotValue( childSlot, new Value( vVector, RU.LIST ) );
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return var;
    }
    
    /**
     * Return TRUE if a specified WmePathNode is unifiable with this
     * instance.
     *
     * @param node a <code>WmePathNode</code> value
     * @return a <code>boolean</code> value
     **/
    boolean isUnifiable( WmePathNode node ) {

	Fact thisWme = this.getWme();
	Fact thatWme = node.getWme();
	return thisWme.getName().equals( thatWme.getName() )
	    && isUnifiable( thisWme, thatWme );
    }

    // f1 could be more general than f2
    private boolean isUnifiable( Fact f1, Fact f2 ) {
	boolean test = true;

	Deftemplate deftemplate = f1.getDeftemplate();
	int nSlots = deftemplate.getNSlots();

	try {
	    for (int i = 0; i < nSlots; i++) {

		String slotName = deftemplate.getSlotName(i);
		if ( isSensitiveSlot( slotName ) ) {
		    Value v1 = f1.getSlotValue( slotName );
		    Value v2 = f2.getSlotValue( slotName );
		    
		    if ( !isUnifiable( v1, v2 ) ) {
			
			test = false;
			break;
		    }
		}
	    }
	} catch (JessException e) {
	    e.printStackTrace();
	}
	return test;
    }

    private boolean isSensitiveSlot( String slotName ) {

	boolean test = false;

	String[] sensitiveSlots = getSensitiveSlots();

	for (int i = 0; i < sensitiveSlots.length; i++) {

	    if ( slotName.equals( sensitiveSlots[i] ) ) {
		test = true;
		break;
	    }
	}
	return test;
    }

    private boolean isUnifiable( Value v1, Value v2 ) {

	boolean test = false;

	try {
	    switch ( v1.type() ) {

	    case RU.LIST:
		Context c = getRete().getGlobalContext();
		test = isUnifiable( v1.listValue(c), v2.listValue(c) );
		break;
	    default:
		test = v1.toString().equals( v2.toString() );
		break;
	    }
	} catch (JessException e) {
	    e.printStackTrace();
	}
	return test;
    }

    private boolean isUnifiable( ValueVector v1, ValueVector v2 ) {

	boolean test = true;

	StringTokenizer st1 = new StringTokenizer( v1.toString() );
	StringTokenizer st2 = new StringTokenizer( v2.toString() );

	Vector /* of String */ vs1 = new Vector();
	while ( st1.hasMoreTokens() ) {
	    vs1.add( (String)st1.nextToken() );
	}
	Vector /* of String */ vs2 = new Vector();
	while ( st2.hasMoreTokens() ) {
	    vs2.add( (String)st2.nextToken() );
	}

	try {
	    test = isUnifiable( car(vs1), cdr(vs1), car(vs2), cdr(vs2) );
	} catch (Exception e) {
	    e.printStackTrace();
	}

        return test;
    }

    private boolean isUnifiable( String s1, Vector /* of String */ vs1,
				 String s2, Vector /* of String */ vs2 )
	throws Exception {
	
	// if both s1 and s2 are the final element of the value vector, ...
	if ( vs1.isEmpty() && vs2.isEmpty() ) {
	    // then see if s1 is unifiable with s2
	    return isUnifiable( s1, s2 );
	}
	
	// First, see if s1 is unifiable with s2, 
	if ( isUnifiable( s1, s2 ) ) {
	    // if s1 is a multivariable '$?', ...
	    if ( isMultiVariable( s1 ) ) {

		if ( isVariable( s2 ) ) {
		    return isUnifiable( car(vs1), cdr(vs1), s2, vs2 );
		}

		// then, if s2 is not '$?', skip until the next variable
		if ( !isMultiVariable( s2 ) ) {
		    while ( !vs2.isEmpty() && !isVariable( car(vs2) ) ) {
			vs2 = cdr(vs2);
		    }
		}
	    }
	    // See if the rest of vs1 and vs2 are unifiable
	    return isUnifiable( car(vs1), cdr(vs1), car(vs2), cdr(vs2) );

	// if s1 is not unifiable with s2, return false
	} else {

	    return false;
	}
    }

    private boolean isUnifiable( String s1, String s2 ) {

	boolean test = false;

	if ( s1 == s2 ||
	     s1.equals( s2 ) ||
	     isMultiVariable( s1 ) ||
	     (isVariable(s1) && isVariable(s2)) ||
	     (isAnonymousVar(s1) && isAnonymousVar(s2)) ) {
	    test = true;
	}
	return test;
    }

    private boolean isMultiVariable( String s ) {

	// return s.equals( "$?" );
	return s.startsWith( "$?" );
    }

    private boolean isVariable( String s ) {

	return (s.charAt(0) == '?') && (s.length() > 1);
    }

    private boolean isAnonymousVar( String s ) {

	return s.equals( "?" );
    }

    private String car( Vector v ) {
	return v.isEmpty() ? null : (String)v.get(0);
    }

    private Vector cdr( Vector v ) {
	return v.isEmpty() ?
	    new Vector() :
	    new Vector( v.subList( 1, v.size() ) );
    }

    /**
     * Return TRUE if there is a slot with 
     *
     * @return a <code>boolean</code> value
     */
    boolean isMostSpecific() {

	boolean test = false;
	
	if ( rete.isTerminalWmeType( getWme().getName() ) ) {

	    test = true;

	} else {

	    try {
		Context c = getRete().getGlobalContext();
		String slot = getChild().getParentSlotName();
		Value value = getWme().getSlotValue( slot );
		ValueVector children = value.listValue(c);
		String firstElement = children.get(0).toString();
		if ( isAnonymousVar( firstElement ) ||
		     isVariable( firstElement ) ) {
		    test = true;
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return test;
    }

    /**
     * Transrate (slot ? ? ?var ? ? ?) into (slot $? ?var $?)
     *
     * @return a <code>WmePathNode</code> value
     */
    WmePathNode generalize() {

	WmePathNode generalizedNode = (WmePathNode)clone();

	try {
	    Context c = getRete().getGlobalContext();
	    // Identify the name of the slot to be generalized.  
	    String childSlot = getChild().getParentSlotName();
	    
	    
	    
	    
		    // Get the value of the target slot
		    Value value = getWme().getSlotValue( childSlot );
		    ValueVector children = value.listValue(c);
	
		    if ( children.size() == 1 ) {
			return null;
		    }
	
		    String var = null; 
		    for (int i = 0; i < children.size(); i++) {
	
			String element = children.get(i).toString();
			if ( isVariable( element ) ) {
			    var = element;
			    break;
			}
		    }
		    
		    ValueVector generalizedValue = new ValueVector();
		    // generalizedValue.add(new Variable("", RU.MULTIVARIABLE));
		    generalizedValue.add(new Variable(genMvSym(), RU.MULTIVARIABLE));
		    generalizedValue.add(new Variable(var.substring(1), RU.VARIABLE));
		    generalizedValue.add(new Variable("", RU.MULTIVARIABLE));
	
		    Value gValue = new Value( generalizedValue, RU.LIST );
		    generalizedNode.getWme().setSlotValue( childSlot, gValue );
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return generalizedNode;
    }
    /**
     * 
     * @return the name of the slot used to access this node in the parent (usually a multislot name, like cells)
     */
   public String getParentSlotName() {

		return parentSlotName;
	}
	/**
     * Returns TRUE is this object refers to a WME with the give type.
     *
     * @param type a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    boolean isWmeType( String type ) {

	return getWme().getName().equals( type );
    }

    /**
     * Returns TRUE if this WmePathNode refers to the fact that is
     * equivalent to the given fact
     *
     * @param fact a <code>Fact</code> value
     * @return a <code>boolean</code> value
     **/
    boolean hasSameFact( Fact fact ) {

	boolean test = false;

	if ( getWme().getName().equals( fact.getName() ) ) {

	    try {
		String thisName = getWme().getSlotValue( "name" ).toString();
		String thatName = fact.getSlotValue( "name" ).toString();

		if ( thisName.equals( thatName) ) {
		    test = true;
		}
	    } catch (JessException e) {
		e.printStackTrace();
	    }
	}
	return test;
    }

    /**
     * Return TRUE if this WmePathNode referes to a WME that is the
     * same type as the given WME in the given WmePathNode
     *
     * @param fact a <code>Fact</code> value
     * @return a <code>boolean</code> value
     **/
    boolean hasSameWmeType( WmePathNode node ) {
	return getWme().getName().equals( node.getWme().getName() );
    }

    /**
     * Return TRUE if this WmePathNode has the same variable name as
     * the given node
     *
     * @param node a <code>WmePathNode</code> value
     * @return a <code>boolean</code> value
     **/
    boolean hasSameSymbol( WmePathNode node ) {
	return getVariable().equals( node.getVariable() );
    }

    /**
     * Returns TRUE if this wme-path node contains a multivarable
     *
     * @return a <code>boolean</code> value
     */
    boolean hasMultivariable() {

	boolean test = false;

	Fact wme = getWme();
	Deftemplate deftemplate = wme.getDeftemplate();
	int nSlots = deftemplate.getNSlots();

	try {
	    for (int i = 0; i < nSlots && test == false; i++) {

		String slotName = deftemplate.getSlotName(i);
		if ( isSensitiveSlot( slotName ) ) {

		    Value value = wme.getSlotValue( slotName );
		    if ( value.type() == RU.LIST ) {

			Context c = getRete().getGlobalContext();
			ValueVector vv = value.listValue(c);
			String vvStr = vv.toString();
			StringTokenizer tokens = new StringTokenizer( vvStr );
			while ( tokens.hasMoreTokens() ) {
			    String var = (String)tokens.nextToken();
			    if ( isMultiVariable( var ) ) {
				test = true;
				break;
			    }
			}
		    }
		}
	    }
	} catch (JessException e) {
	    e.printStackTrace();
	}
	return test;
    }

    /**
     * Describe <code>clone</code> method here.
     *
     * @return an <code>Object</code> value
     */
    public Object clone() {

	WmePathNode cloneNode = null;
	try {
	    cloneNode = (WmePathNode)super.clone();
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}

	Fact cloneWme = (Fact)getWme().clone();
	cloneNode.setWme( cloneWme );

	Context c = getRete().getGlobalContext();

	try {
	    Deftemplate deftemplate = wme.getDeftemplate();
	    for (int i = 0; i < deftemplate.getNSlots(); i++) {
		String slotName = deftemplate.getSlotName(i);
		Value originalValue = wme.getSlotValue( slotName );

		if ( originalValue.type() == RU.LIST ) {
		    ValueVector originalVector = originalValue.listValue(c);
		    ValueVector cloneVec = (ValueVector)originalVector.clone();
		    Value cloneValue = new Value( cloneVec, RU.LIST );
		    cloneWme.setSlotValue( slotName, cloneValue );
		}
	    }
	} catch (JessException e) {
	    e.printStackTrace();
	}

	return cloneNode;
    }

    /**
     * Describe <code>toString</code> method here.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return getVariable() + " <- " + getWme();
    }
	public void setParentSlot(String slotName) 
	{
	
		parentSlotName=slotName;
	}

    /*
    public String toString( int valID ) {

	Fact wme = (Fact)getWme().clone();

	String[] slotNames = wme.getDeftemplate().getSlotNames();
	for (int i = 0; i < slotNames.length; i++) {
	    if ( slotNames[i].equals( "value" ) ) {
		try {
		    Variable var = new Variable("val" + valID, RU.VARIABLE);
		    wme.setSlotValue( "value", var );
		} catch (JessException e) {
		    e.printStackTrace();
		}
		break;
	    }
	}
	
	return getVariable() + " <- " + wme;
    }
    */
}

//
// WmePathNode.java
// 
