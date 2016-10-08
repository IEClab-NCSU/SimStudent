/**
 * AmlRete.java
 *
 *
 * Created: Sat Jan 01 22:32:06 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.server.UID;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;

import jess.Deftemplate;
import jess.Fact;
import jess.Jesp;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

public class AmlRete extends Rete implements Cloneable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    // Stores paths for each WME:: The key for each entry is a String
    // "<wmeType>/<wmeName>"
    //
    Hashtable wmePath = null;
    /** 
     * a Vector(of WMEPath) of branches in the path which are waiting to be processed
     */
   
    // Enable for topological chunking
    //private Vector branches=new Vector();
    /**
     * A Vector (of WmePathNodes) coresponding to the last node added to each of the 
     * wmePaths in branches. Hence, the order of lastNodes corresponds to the order of branches.
     * Note that each wmePathNode in lastNodes is not actually a part of the wmePaths in branches,
     * but will be immediately added when searchWmePaths is called.
     */
   
    // Enable for topological chunking
    // private Vector lastNodes=new Vector();
    
    private Hashtable getWmePath() { return this.wmePath; }
    private void setWmePath( Hashtable wmePath ) { this.wmePath = wmePath; }

    void addWmePath( Fact fact, WmePath path, String parentSlotName ) {
    	
    	//path.add( fact,parentSlotName );
	
    	try {
    		String type = fact.getName();
    		String name = fact.getSlotValue( "name" ).toString();
    		String key = type + "|" + name;

    		Vector v=(Vector)wmePath.get(key);
    		if(v==null)
    		{
    			v=new Vector();
    			wmePath.put(key,v);
    		}
	
    		v.add(path);
        	
       

    	} catch (JessException e) {
    		e.printStackTrace();
    	}
 
    	
    }
    
    /*
     * Reimplement for topological chunking
     * 
    //Add this wmePath to the wmePath hash table. 
    void addWmePath_chunking_version( Fact fact, WmePath path, 
    		         String parentSlotName, String branchName ) {

	path.add( fact,parentSlotName );
	try {
	    String type = fact.getName();
	    String name = fact.getSlotValue( "name" ).toString();
	    String key;
	    if (branchName != null)
	    	key = type + "|" + name + "|" + branchName;
	    else
	    	key = type + "|" + name;

	    Vector v=(Vector)wmePath.get(key);
	    if(v==null)
	    {
	    	v=new Vector();
	    	wmePath.put(key,v);
	    }
	    v.add(path);

	} catch (JessException e) {
	    e.printStackTrace();
	}
    }
*/ 
  
    public Vector getWmePath( String wmeType, String wmeName, String letter) {
    	//wmePath is the hashmap that keeps the wmepaths for all 
    	//terminal elements. 	
       	if ( wmePath == null ) { initializeWmePath(letter); }
    	String wmeKey = wmeType + "|" + wmeName;    	
    	
    	  // System.out.println("wmeFacts is " + wmePath);
    	return (Vector)wmePath.get( wmeKey );
        }
    
    
    /*
     * Reimplement for topological chunking
     * 
     * Assuming that all WME have "name" slot, getWmePath returns a
     * list of WMEPaths WME-path for WME with a specified type and name
     * 
     * If branchName is non-null, see if the branches vector contains a 
     * path with a child slot equal to branch name. If so, resume search from that
     * path instead. If branchName is null, proceed from beginning of search.
     * @param wmeType
     * @param wmeName
     * @return
     
    public Vector getWmePath_chunking_version( String wmeType, String wmeName, String branchName ) {

	if ( wmePath == null ) { 
		initializeWmePath(); 
	}
	//No specific branch requested, so take results of search always using first path
	if (branchName == null) {
		String wmeKey = wmeType + "|" + wmeName;
		return (Vector)wmePath.get( wmeKey );
	}
	//Otherwise, try starting search at each node stored in branches until 
	// one succeeds
	String wmeKey = wmeType + "|" + wmeName + "|" + branchName;
	for (int i=0; i<branches.size(); i++) {
		WmePath branchedPath = (WmePath)branches.get(i);
		//WmePathNode node = branchedPath.getLastNode();
		//WmePathNode node = branchedPath.removeLastNode(); //removes and returns last node
		Fact wmeFact = (Fact)lastNodes.get(i);
		//search paths on last branch 
		searchWmePath(new Value(wmeFact), branchedPath, branchedPath.getLastNode().getWme().getName(), branchName);
		if (wmePath.containsKey(wmeKey)) 
			return (Vector)wmePath.get(wmeKey);
	}
	return null; // no vector found
    }
    */

    // WME child slot :: The slot name that must be followed to find a
    // wme-path
    //
    HashMap wmeChildSlots = new edu.cmu.pact.miss.HashMap();
    Vector getWmeChildSlots( String wmeType ) throws Exception {
    	Vector childSlots = (Vector)wmeChildSlots.get( wmeType );
    	return childSlots;
    }

    /*
     * Reimplement for topological chunking. Did not load names correctly
     * 
     * 
    // wmeBranches - a HashMap containing wmeChild names as keys,
    // and a vector of Strings as a value. 
    // Each string is the name of a direct parent, so cell might have
    // a vector of <"row", "column"> while interface-elements might have
    // <"problem">
    HashMap wmeBranches = new edu.cmu.pact.miss.HashMap();
    //Returns the vector of parents from the wmeBranches hash
    Vector getWmeBranchParents(String wmeType) {
    	//Remove MAIN:: from incoming string
    	wmeType = wmeType.substring(wmeType.lastIndexOf(":")+1, wmeType.indexOf("|"));
    	return ((Vector)wmeBranches.get(wmeType));
    }
    */

    // WME that has no further link to other WMEs 
    private Vector terminalWmeType = new Vector();
    private Vector getTerminalWmeType() { return terminalWmeType; }

    // WME that must be ignored
    private Vector /* String */ systemWmeType = new Vector();
    private Vector /* String */ getSystemWmeType() {
    	return systemWmeType;
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    /**
     * Returns WME-type (i.e., the "Name") of a WME with the specified
     * name. This is for CTAT integration, because CommWidgets does
     * not tell anything about WME-type
     *
     * @param name a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String wmeType( String name ) {

    	Iterator facts = listFacts();
    	while ( facts.hasNext() ) {

    		Fact fact = (Fact)facts.next();

    		try {
    			if ( hasSlot( fact, "name" ) &&
    					fact.getSlotValue("name").equals( name ) ) {
    				return fact.getName();
    			}
    		} catch (JessException e) {
    			e.printStackTrace();
    		}
    	}
    	return null;
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
    		Iterator facts = listFacts();
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

    // A cache for lookupWme()
    private HashMap lookupWmeCache = new HashMap();
    private boolean lookupWmeCacheContains( String key ) {
    	return lookupWmeCache.containsKey( key );
    }
    private Fact getLookupWmeCache( String key ) {
    	return (Fact)lookupWmeCache.get( key );
    }
    
    private void putLookupWmeCache( String key, Fact fact ) {
    	lookupWmeCache.put( key, fact );
    }

    /**
     * Return a WME of the type "MAIN::problem"
     *
     * @return a MAIN::problem WME
     **/
    public Fact lookupProblemWme() {

    	Iterator facts = listFacts();
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

    	Vector constraint = new Vector();
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
    public Value lookupWmeValue( String type, String slot, 
    		Vector /* of String */ constraint ) {

    	Value value = null;

    	Iterator slotVals = constraint.iterator();

    	Iterator facts = listFacts();
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

    private boolean constHold( Fact fact, Vector /* of String */ constraint ) {

    	boolean result = true;

    	Iterator slotValues = constraint.iterator();
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
    
    // WME-path / / / / / / / / / / / / / / / / / / / / 
    // 
    
    // Find all WME-paths
    private void initializeWmePath(String letter) {
	
    	setWmePath( new Hashtable() );

    	// Get a interface-elements slot value of the MAIN::problem WME
    	Value problem = new Value( lookupProblemWme() );
    	
    	// Launching the search with the problem WME and an empty list
    	// name is misleading, searchWmePath actually DOESN'T search
    	// for a specific wme path but fills the wmePath hashMap.
    	searchWmePath( problem, new WmePath( this ), null, letter );

    
    	// Different version to use with Topological Chunking
    	// search for a a path taking the FIRST branch every time
    	//searchWmePath( problem, new WmePath( this ), null, null );
    }
    

    
    private void searchWmePath( Value wme, WmePath path,String parentSlotName,String letter ) {
    	   	   
    	   
        // if the WME is a list, then ...
        if ( wme.type() == RU.LIST ) {
            // repeatedly call searchWmePath on each of the elements
            // with an individual copy of path
            try {
                ValueVector listValue = wme.listValue( getGlobalContext() );
                for ( int i = 0; i < listValue.size(); i++ ) {
                    searchWmePath( listValue.get(i), (WmePath)path.clone(), parentSlotName,letter );
                }
            } catch (JessException e) {
                e.printStackTrace();
            }

            // If the WME has no link to another WME, then ...
        } /* nbarba 08/27/2014 : Previously only wme paths for terminal elements was pre-computed.
           * This has been commented to pre-compute wme paths for all elements (addWmePath 
           * as transfered to the following if).
        	else if ( isTerminalWme( wme )) {
            try {
                // Add a new wme-path to the wmePath database
                Fact fact = wme.factValue( getGlobalContext() );
                addWmePath( fact, path, parentSlotName );
            } catch (JessException e) {
                e.printStackTrace();
            }

            // Follow further WME link
        }*/ else if ( isNotSystemWme( wme ) ) {
            try {
            
                Fact wmeFact=wme.factValue(getGlobalContext());
               // addWmePath( wmeFact, (WmePath) path.clone(), parentSlotName );
               // path.add( wmeFact, parentSlotName );
                path.add( wmeFact, parentSlotName,letter );
                
               
                addWmePath( wmeFact, (WmePath) path.clone(), parentSlotName );
               
                //Children stuff is left over from t-chunking
                Vector<String> children = getWmeChildSlots(wmeFact.getName());
     
                if (children != null) {
                	              	
                	for (String  child : children){    	
                			String childSlotName=(String)child;  
                			
                        	searchWmePath( wmeFact.getSlotValue(childSlotName), path, childSlotName,letter );//search the first branch               			     			
                	}
                	//String childSlotName=(String)children.get(0);
                	//searchWmePath( wmeFact.getSlotValue(childSlotName), path, childSlotName );//search the first branch
                	
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 
     * Reimplement for topological chunking
     * 
     * Generates all possible wmePaths, filling the wmePath vector. 
     * Called recursively to search through all possibilities.
     * If there is a case when a parent has multiple possible
     * children (other than a list of multiple children), then
     * only searches the branch corresponding to the wme named by 'branchWmeName'
     * and places this node in a stack for later calls to resume the search.
     * 
     * If null, takes first branch and places rest on stack.  
     * 
     * @param wme
     * @param path
     * @param parentSlotName
     * @param branchWmeName
    private void searchWmePath_topological_version( Value wme, WmePath path, 
    		                    String parentSlotName, String branchWmeName ) {
	// if the WME is a list, then ...
	if ( wme.type() == RU.LIST ) {
	    // repeatedly call searchWmePath on each of the elements
	    // with an individual copy of path
	    try {
		ValueVector listValue = wme.listValue( getGlobalContext() );
		for ( int i = 0; i < listValue.size(); i++ ) {
		    searchWmePath( listValue.get(i), (WmePath)path.clone(),parentSlotName, branchWmeName );
		}
	    } catch (JessException e) {
		e.printStackTrace();
	    }
	    
	// If the WME has no link to another WME, then ...
	} else if ( isTerminalWme( wme ) ) {

	    try {
		// Add a new wme-path to the wmePath database
		Fact fact = wme.factValue( getGlobalContext() );
		//include the branch name as part of the key
		//Assumes that if a real branch name is specified, 
		// that branch will be found
		addWmePath( fact, path,parentSlotName, branchWmeName );
	    } catch (JessException e) {
		e.printStackTrace();
	    }

	// Follow further WME link
	} else if ( isNotSystemWme( wme ) ) {
	    try {
	    //First add this link to the path
		//Clone prior to adding this wmeFact to path, in case path
	    // needs to be saved for branching
	    	
	    WmePath branchClone = (WmePath)path.clone();
	    	
	    Fact wmeFact=wme.factValue(getGlobalContext());
		path.add( wmeFact,parentSlotName );
		
	    //Now get the children from the wme.
		Vector children=getWmeChildSlots(wmeFact.getName());
		String firstChildSlot=(String)children.get(0);
		if (children.size() == 1) {
			//Only single branch, so continue search
			searchWmePath( wmeFact.getSlotValue(firstChildSlot), (WmePath)path.clone(),
					       firstChildSlot, branchWmeName );
		}
		else {
			//Check to see if children match branchWmeName
			//Note that if none of the children match and branchWmeName is non-null,
			//search will end here. This indicates that an incorrect node was tried
			for (int i=0; i<children.size(); i++) {
				if (branchWmeName == null)
					break;
				String curChild = (String)children.get(i);
				if (curChild.equals(branchWmeName)) {
					//Strip off MAIN:: before searching
					searchWmePath(wmeFact.getSlotValue(curChild), (WmePath)path.clone(), 
							      curChild, branchWmeName);
				}	
			}
			//Save this node for future searches (to save time)
			//branches.add((WmePath)path.clone());
			branches.add(branchClone);
			lastNodes.add((Fact)wmeFact.clone());
		}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	   
		}
    } // end function searchWmePath
    */


    private boolean isNotSystemWme( Value wme ) {
    
    	boolean test = true;
    	Fact fact = null;
    	try {
    		fact = wme.factValue( getGlobalContext() );
    	} catch (JessException e) {
    		e.printStackTrace();
    	}

    	int index = getSystemWmeType().indexOf( fact.getName() );
    	if ( index != -1 ) {
    		test = false;
    	}
    	return test;
    }

    private boolean isTerminalWme( Value wme ) {

    	boolean test = false;

    	// If the WME is a fact, and ...
    	if ( wme.type() == RU.FACT ) {

    		Fact fact = null;
    		try {
    			fact = wme.factValue( getGlobalContext() );
    		} catch (JessException e) {
    			e.printStackTrace();
    		}

    		if ( isTerminalWmeType( fact.getName() ) ) {
    			test = true;
    		}
    	}
    	return test;
    }

    boolean isTerminalWmeType( String type ) {

    	int index = getTerminalWmeType().indexOf( type );
    	return index != -1;
    }

    /**
     * 
     * @param fact a <code>jess.Fact</code>
     * @return   wme-path that contains give fact as a node.
     */  
    public WmePath lookupWmePath( Fact fact ) {

    	WmePath targetPath = null;
    	Enumeration vectors=getWmePath().elements();
    	while(vectors.hasMoreElements())
    	{
    		Vector v=(Vector)vectors.nextElement();
    		Enumeration paths =v.elements();
    		while ( paths.hasMoreElements() ) 
    		{

    			WmePath wmePath = (WmePath)paths.nextElement();

    			if ( wmePath.hasNode( fact ) ) {
    				targetPath = wmePath;
    				break;
    			}
    		}
    	}
    	return targetPath;
    }

    // File I/O / / / / / / / / / / / / / / / / / / / /
    // 

    /**
     * Read Jess code from a file specified
     *
     * @param fileName a name of the file to be read
     */
    public void readFile( String fileName ) {

    	// Sat Jun 04 11:09:58 2005: Mac Jess does need them get quoted 
    	fileName = "\"" + fileName + "\"";
    	try {
    		Value val = executeCommand( "(batch " + fileName + ")" );
    	} catch (JessException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Make a copy of this object with EQUAL but not IDENTICAL rete
     * instance
     *
     * @return an <code>Object</code> value
     */
    public Object clone() {

    	// Make a clone of this object
    	AmlRete amlRete = null;
    	try {
    		amlRete = (AmlRete)super.clone();
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
    
	/**
	 * Parse an input file. Uses {@link jess.Jesp}. Presumes that input
	 * stream is from a file, not a console.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @param  removeBuggyRules if true, will remove buggy rules after the parse;
	 *         see {@link #extractBuggyRules()}
	 * @return result of the last parsed entity
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	public Value parse(Reader rdr, boolean removeBuggyRules) throws JessException {

	    UID uid = new UID();                         // for unique router name
	    if (!(rdr instanceof BufferedReader || rdr instanceof StringReader))
	        rdr = new BufferedReader(rdr);
//	    addInputRouter(uid.toString(), rdr, false);  // false=>not consoleLike
	    Jesp jesp = new Jesp(rdr, this);

            Value result = jesp.parse(false, this.getGlobalContext());
	    // removeInputRouter(uid.toString());
	    //if (removeBuggyRules)
	    //    unloadBuggyRules();
	    return result;
	}

    public boolean loadWMEStructureFromReader(BufferedReader reader) 
    {
    	/*
    	 * ;; is a commented line
    	 * 
    	 * 
    	 * ;;WME Structure
    	 (parent child+)+(each parent gets it's own line

		---------------(seperator)
		;;terminal WMEs
		WME+
		--------------
		;;Ignored WMEs
		WME*

    	 */
    	Vector sensSlots = new Vector(); //sensitive slots for WmePathNode
    	String curLine;
    	boolean loadStructure=true;
    	boolean loadTerminals=false;
    	boolean loadIgnore=false;
    	Pattern whitespace=Pattern.compile("\\s+");
    	do
    	{
    		try
    		{
    			curLine=reader.readLine();
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    			return false;
    		}
    		if(curLine==null)
    			break;
    		if(curLine.startsWith(";;") || whitespace.matcher(curLine).matches()||curLine.equals("") )
    			continue;
    		
    		curLine=curLine.trim();
    			if(curLine.startsWith("-"))
    		{
    			if(loadStructure)
    			{
    				loadStructure=false;
    				loadTerminals=true;
    			}
    			
    			else if(loadTerminals)
    			{
    				loadTerminals=false;
    				loadIgnore=true;
    			}
    			continue;
    		}
    		if(loadStructure)
    		{
    			//While reading in contents, also fill in the wmeBranches hashMap
    			String[] contents=whitespace.split(curLine);
    			String parent=contents[0];
    			Vector children=new Vector();
    			
    	
    			/*nbarba 07/31/2014: wme may now have many children in the form e.g. "fraction,table", separted by comma.
    			 * Hierarchy structure is defined in wmeStructure.txt file*/
    			if (contents[1].contains(",")){ 				
    				String[] tmp=contents[1].split(",");
            		Collections.addAll(children, tmp);
    			}
    			else children.add(contents[1]);
	
    			//Add parent to this child's wmeBranch hash value
    			//Re implement for t-chunking
    			/*
    			if (wmeBranches.containsKey(contents[1])) {
    				Vector parents = (Vector)wmeBranches.get(contents[1]);
    				if (!parents.contains(parent))
    					parents.add(parent);
    			}
    			else {
    				Vector newParents = new Vector();
    				newParents.add(parent);
    				wmeBranches.put(contents[1], newParents);
    			}
    			*/
    			//also store in sensitive slots
    			
    			/*nbarba 07/31/2014:wme may now have many children. (see comment above)*/
    			if (contents[1].contains(",")){ 				
    				String[] tmp=contents[1].split(",");
            		Collections.addAll(sensSlots, tmp);
    			}
    			else sensSlots.add(contents[1]);
    			
			
    			wmeChildSlots.put(parent,children);
    			
    			for(int childnum=2; childnum<contents.length; childnum++)
    			{
    				String child=contents[childnum];
    				/*
    				 * reimplement for chunking
    				if (wmeBranches.containsKey(contents[childnum])) {
        				Vector parents = (Vector)wmeBranches.get(contents[childnum]);
        				if (!parents.contains(parent))
        					parents.add(parent);
        			}
        			else {
        				Vector newParents = new Vector();
        				newParents.add(parent);
        				wmeBranches.put(contents[1], newParents);
        			}
        			*/
    				children=(Vector)wmeChildSlots.get(parent);
    				children.add(child);
    				sensSlots.add(child);
    			}
    		}
    		if(loadTerminals)
    			terminalWmeType.add(curLine);
    		if(loadIgnore)
    		{
    			systemWmeType.add(curLine);
    		}
    	} while(curLine!=null);
//    	Set sensitive slots in WmePathNode
		WmePathNode.setSensitiveSlots((String[])sensSlots.toArray(new String[1]));
		
		
		//System.out.println(" fianl wmeChildSlots: " + wmeChildSlots);
		return true;
    }

    /**
     * loads a WME structure into this rete from a file(see method for file structure) 
     * @param structureFilePath the path to the WMEstructureFile
     * 
     */
    public boolean loadWMEStructureFromFile(String structureFilePath) 
    {
    	/*
    	 * ;; is a commented line
    	 * 
    	 * 
    	 * ;;WME Structure
    	 (parent child+)+(each parent gets it's own line

		---------------(seperator)
		;;terminal WMEs
		WME+
		--------------
		;;Ignored WMEs
		WME*

    	 */
    	File file=new File(structureFilePath);
    	BufferedReader reader;
    	
    	try
    	{
    		reader=new BufferedReader(new FileReader(file));
    	}
    	catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    	Vector sensSlots = new Vector(); //sensitive slots for WmePathNode
    	String curLine;
    	boolean loadStructure=true;
    	boolean loadTerminals=false;
    	boolean loadIgnore=false;
    	Pattern whitespace=Pattern.compile("\\s+");
    	do
    	{
    		try
    		{
    			curLine=reader.readLine();
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    			return false;
    		}
    		if(curLine==null)
    			break;
    		if(curLine.startsWith(";;") || whitespace.matcher(curLine).matches()||curLine.equals("") )
    			continue;
    		
    		curLine=curLine.trim();
    			if(curLine.startsWith("-"))
    		{
    			if(loadStructure)
    			{
    				loadStructure=false;
    				loadTerminals=true;
    			}
    			
    			else if(loadTerminals)
    			{
    				loadTerminals=false;
    				loadIgnore=true;
    			}
    			continue;
    		}
    		if(loadStructure)
    		{
    			//While reading in contents, also fill in the wmeBranches hashMap
    			String[] contents=whitespace.split(curLine);
    			String parent=contents[0];
    			Vector children=new Vector();
    			children.add(contents[1]);
    			//Add parent to this child's wmeBranch hash value
    			//Re implement for t-chunking
    			/*
    			if (wmeBranches.containsKey(contents[1])) {
    				Vector parents = (Vector)wmeBranches.get(contents[1]);
    				if (!parents.contains(parent))
    					parents.add(parent);
    			}
    			else {
    				Vector newParents = new Vector();
    				newParents.add(parent);
    				wmeBranches.put(contents[1], newParents);
    			}
    			*/
    			//also store in sensitive slots
    			sensSlots.add(contents[1]);
    			wmeChildSlots.put(parent,children);
    			for(int childnum=2; childnum<contents.length; childnum++)
    			{
    				String child=contents[childnum];
    				/*
    				 * reimplement for chunking
    				if (wmeBranches.containsKey(contents[childnum])) {
        				Vector parents = (Vector)wmeBranches.get(contents[childnum]);
        				if (!parents.contains(parent))
        					parents.add(parent);
        			}
        			else {
        				Vector newParents = new Vector();
        				newParents.add(parent);
        				wmeBranches.put(contents[1], newParents);
        			}
        			*/
    				children=(Vector)wmeChildSlots.get(parent);
    				children.add(child);
    				sensSlots.add(child);
    			}
    		}
    		if(loadTerminals)
    			terminalWmeType.add(curLine);
    		if(loadIgnore)
    		{
    			systemWmeType.add(curLine);
    		}
    	} while(curLine!=null);
//    	Set sensitive slots in WmePathNode
		WmePathNode.setSensitiveSlots((String[])sensSlots.toArray(new String[1]));
		return true;
    }
 
    public static void main(String[] args) {
    	
    	AmlRete amlRete = new AmlRete();

    }

    
}


//
// end of AmlRete.java
// 
