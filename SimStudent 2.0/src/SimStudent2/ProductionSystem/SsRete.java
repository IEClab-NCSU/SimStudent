/**
 * AmlRete.java
 *
 *
 * Created: Sat Jan 01 22:32:06 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package SimStudent2.ProductionSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;
import SimStudent2.HashMap;

public class SsRete extends Rete {

	private static final long serialVersionUID = 1364631357879375472L;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// var symbol generation is done by Binding.class
	// A variable number used for genVar();
	// int varNumber = 0;
	
	// A cache for lookupWme()
	// 
	private HashMap lookupWmeCache = new HashMap();
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public SsRete() {}
	
	public SsRete(String wmeTypeFile) {
	
		loadBatchFile(wmeTypeFile);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	/**
	 * @return A string "?varXX" with XX a unique number.
	 */
	/*
	public String genVar() {

		return genVar("var");
	}
	
	public String genVar(String prefix) {
		
		return "?" + prefix + this.varNumber++;
	}
	*/
	
	
	// - 
	// - File IO . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// - 

	public void loadBatchFile(String fileName) {
		
		try {
			
			batch(fileName);
			
		} catch (JessException e) {
			
			e.printStackTrace();
		}
	}
	
	// - 
	// - WME lookup . . . . . . . . . . . . . . . . . . . . . . . . .
	// - 
	
	private boolean lookupWmeCacheContains( String key ) {
		return getLookupWmeCache().containsKey( key );
	}
	
	private Fact getLookupWmeCache( String key ) {
		return (Fact)getLookupWmeCache().get( key );
	}

	private void putLookupWmeCache( String key, Fact fact ) {
		getLookupWmeCache().put( key, fact );
	}

	public Fact lookupFactByName(String name) {

		Fact fact = null;

		try {
			@SuppressWarnings("unchecked")
			Iterator<Fact> facts = listFacts();
			while (facts.hasNext()) {
				
				fact = (Fact) facts.next();

				Value factNameValue = null;
				try {
					factNameValue = fact.getSlotValue("name");
				} catch (JessException e) {
					;
				}
				
				if (factNameValue != null) {
					
					String factName = factNameValue.stringValue(getGlobalContext());
					if (name.equals(factName))
						break;
				}
				fact = null;
			}
		
		} catch (JessException e) {

			e.printStackTrace();
		}
		
		return fact;
	}

	/**
	 * Return a fact of a specified type with a specified name as the
	 * value of "name" slot
	 *
	 * @param type a <code>String</code> value
	 * @param name a <code>String</code> value
	 * @return a <code>Fact</code> value
	 **/
	public Fact lookupWme( String type, String name ) {

		String lookupKey = type + name;

		Fact fact = null;
		// If this lookup has been done before, ...
		if ( lookupWmeCacheContains( lookupKey ) ) {

			// then retrieve the fact from a cache
			fact = getLookupWmeCache( lookupKey );

		} else {

			// otherwise, really lookup the fact from the Rete net
			@SuppressWarnings("unchecked")
			Iterator<Fact> facts = listFacts();
			while ( facts.hasNext() ) {

				fact = (Fact)facts.next();
				try {
					if ( fact.getName().equals( type ) &&
							fact.getSlotValue("name").equals( name ) ) {
						break;
					}
				} catch (JessException e) {
					e.printStackTrace();
				}
			}
			// put the fact into the 
			putLookupWmeCache( lookupKey, fact );
		}
		return fact;
	}

	/**
	 * Return a WME of the type "MAIN::problem"
	 *
	 * @return a MAIN::problem WME
	 **/
	public Fact lookupProblemWme() {

		@SuppressWarnings("unchecked")
		Iterator<Fact> facts = listFacts();
		while ( facts.hasNext() ) {

			Fact fact = (Fact)facts.next();
			if ( fact.getName().equals( "MAIN::problem" )) {
				return fact;
			}
		}
		return null;
	}

	/**
	 * Return a value of the "value" slot for a Fact with the type of
	 * <wmeType> and has a value of <wmeName> in the "name" slot.
	 *
	 * @param wmeType a type of the Fact sought
	 * @param wmeName a name of the Fact sought
	 * @return a <code>String</code> value
	 **/
	public Value lookupWmeValue( String wmeType, String wmeName ) {

		Vector<String> constraint = new Vector<String>();
		constraint.add( "name" );
		constraint.add( wmeName );

		return lookupWmeValue( wmeType, "value", constraint );
	}


	/**
	 * Return a slot valur of the specified slot for a WME with the
	 * specified type and the slots with specified slot values.  The
	 * slot values are specified as a Vector that lists slot names and
	 * their values alternatively (i.e., {slot1 value1 slot2 value2
	 * ...}.
	 *
	 * @param type a <code>String</code> value
	 * @param slot a <code>String</code> value
	 * @param constraint a <code>Vector</code> value
	 * @return a <code>Value</code> value
	 **/
	public Value lookupWmeValue( String type, String slot, Vector<String> constraint ) {

		Value value = null;

		@SuppressWarnings("unchecked")
		Iterator<Fact> facts = listFacts();
		while ( facts.hasNext() ) {

			Fact fact = (Fact)facts.next();
			String wmeType = null;
			try {
				wmeType = fact.getName();
				if ( wmeType.equals( type ) && constHold(fact, constraint) ) {
					value = fact.getSlotValue( slot );
					break;
				}
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	private boolean constHold( Fact fact, Vector<String> constraint ) {

		boolean result = true;

		Iterator<String> slotValues = constraint.iterator();
		while ( slotValues.hasNext() ) {

			String slotName = (String)slotValues.next();
			String slotValue = (String)slotValues.next();

			try {
				if ( !fact.getSlotValue( slotName ).equals( slotValue ) ) {
					result = false;
					break;
				}
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * (MAIN::cell (name dorminTable1_C1R2) (value nil) (description nil) (row-number 2) (column-number 1))
	 * Fact.getName() returns "MAIN::cell"
	 * We often needs to read "dorminTable1_C1R2"
	 * 
	 * @param fact
	 * @return
	 */
	public String getWmeLabelName(Fact fact) {
		
		String wmeLabelName = null;
		
		try {
			
			Value factNameSlot = fact.getSlotValue("name");
			wmeLabelName = factNameSlot.stringValue(getGlobalContext());
			
		} catch (JessException e) {

			e.printStackTrace();
		}
		
		return wmeLabelName;
	}

	/**
	 * Returns WME-type (i.e., getName()) of a WME fact with the specified "name" slot value. 
	 *
	 * @param name a <code>String</code> value
	 * @return a <code>String</code> value
	 **/
	public String wmeType( String name ) {

		String wmeType = null;
		
		@SuppressWarnings("unchecked")
		Iterator<Fact> facts = listFacts();
		while ( facts.hasNext() ) {

			Fact fact = (Fact)facts.next();
			
			try {
				if ( hasSlot( fact, "name" ) &&	fact.getSlotValue("name").equals( name ) ) {
					wmeType = fact.getName();
				}
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
		return wmeType;
	}

	private boolean hasSlot( Fact fact, String name ) {

		boolean result = false;

		Deftemplate template = fact.getDeftemplate();
		String[] slotNames = template.getSlotNames();
		for (int i = 0; i < slotNames.length; i++) {

			if ( slotNames[i].equals( name ) ) {
				result = true;
				break;
			}
		}
		return result;
	}

	// - 
	// - Cloning . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// -
	
	/**
	 * Make a copy of this object with EQUAL but not IDENTICAL rete
	 * instance
	 *
	 * @return an <code>Object</code> value
	 */
	public Object clone() {

		// Make a clone of this object
		SsRete amlRete = null;
		try {
			amlRete = (SsRete)super.clone();
			
		} catch (CloneNotSupportedException e) {
			
			e.printStackTrace();
		}

		// Copy the state of this object
		//
		// Open a byte array output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			bsave( baos );

		} catch (IOException e) {
		
			e.printStackTrace();
		}

		// Now, redirect the byte array stream for input...
		byte[] byteArray = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream( byteArray );

		// ... and feed the state to the clone
		try {
			
			amlRete.bload( bais );
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}

		return amlRete;
	}

	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private HashMap getLookupWmeCache() { return lookupWmeCache; }

	
}
