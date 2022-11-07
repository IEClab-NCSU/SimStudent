

package edu.cmu.old_pact.dormin;

/**
* Version 3
*
* May 5, 1999. 
*	- Changed parameter type "O" - now it is Object description, with real type String.
*/

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import edu.cmu.pact.CommManager.CommManager;
import edu.cmu.pact.Utilities.trace;


///////////////////////////////////////////////////////////////////////////////////////
/**

	The MessageObject Class serves as a way of storing data for messageObjects

*/
///////////////////////////////////////////////////////////////////////////////////////
public class MessageObject{

	private static String DirectObjectRep="DOBJ";
	private static String IndirectObjectRep="IOBJ";
	
	/** Property name or element type for {@link #getTransactionId()}. */
	public static final String TRANSACTION_ID = "transaction_id";

	public static int MsgCounter=1;
	public static boolean showMessage = false;
	public int MessageID;
	private Vector Names,Values,Types;
	public static final float version = (float) 1.2;
	private String myVerb=null;
	private String Initializer=null;
	private ObjectProxy topProxy=null; //top-level proxy receiving message
	private DorminException parsingException=null; //store parsing exception
	private int currentPos=0; //current parsing position (kept in case error throws out of parse)

	/** Semantic event identifier. Prefix "T" for transaction. */
	private String transactionId = makeTransactionId();
	private String convertInstructions;
	
	/** See {@link #isLoggingSuppressed()}: whether this msg should be logged. */
	private boolean doNotLog = false;
	
	/** See {@link #lockTransactionId(String)}, {@link #setTransactionId(String)}. */
	private boolean isTransactionIdLocked = false;
	
	///////////////////////////////////////////////////////////////////////////////////////
	/**
		constructor
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public MessageObject(int ID){
		MsgCounter = ID;
		MessageID = MsgCounter;
		Names = new Vector();
		Values = new Vector();
		Types =new Vector();
	}

	/**
	 * Create a unique string suitable for a {@link #getTransactionId()} value.
	 * @return "T" + {@link UUID#randomUUID()}.toString()
	 */
	public static String makeTransactionId() {
		return "T" + UUID.randomUUID().toString();
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/**
		constructor
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public MessageObject(String Initializer,ObjectProxy top) {
//		trace.out("mo", "creating message object: string = " + Initializer +
//				  ", top proxy = " + top);
		MsgCounter++;
		MessageID = MsgCounter;
		Names = new Vector();
		Values = new Vector();
		Types =new Vector();
		topProxy = top;
		//trace.out (5, this, "top proxy = " + top);
		this.Initializer = Initializer;
		try {
			parseMessage(Initializer);
			Object transactionId = getProperty(TRANSACTION_ID);
			if (transactionId instanceof String)
				setTransactionId((String) transactionId);
		}
		//If we get a DorminException parsing the message, store the exception and
		//try to parse IGNOREERRORMESSAGES
		catch (DorminException e) {
			trace.out (5, this, "dormin exception = " + e);
			parsingException = e;
			parseIgnorableErrors(Initializer,currentPos);
		}
		//trace.out (5, this, "done");
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/**
		constructor
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public MessageObject(String Verb){	
		MsgCounter++;
		MessageID = MsgCounter;
		Names = new Vector();
		Values = new Vector();
		Types =new Vector();
		myVerb = Verb;
	}

    public static MessageObject messageObjectWithNamesAndValues(String verb, String[] names, Object[] values) {
        if (verb==null || names==null || values==null || names.length!=values.length)
            throw new IllegalStateException("Invalid arguments to messageObjectWithNamesAndValues");

        MessageObject mo = new MessageObject(verb);

        for (int i=0; i<names.length; i++)
            mo.setProperty(names[i], values[i]);

        return mo;
    }

    public MessageObject copy() {
        MessageObject mo = new MessageObject(myVerb);

        for (String name: propertyNames())
            mo.setProperty(name, getProperty(name));

        return mo;
    }

	///////////////////////////////////////////////////////////////////////
	/**
	 * Constructor for subclasses. Sets {@link #myVerb}, {@link #Initializer},
	 * empty.
	 *
	 * @param  top sender?
	 */
	///////////////////////////////////////////////////////////////////////
	protected MessageObject(ObjectProxy top) {
//		trace.out("mo", "creating message object: top proxy = " + top);
		MsgCounter++;
		MessageID = MsgCounter;
		Names = new Vector();
		Values = new Vector();
		Types = new Vector();
		topProxy = top;
		myVerb = "";
		Initializer = null;
	}

	/** Semantic event identifier.
		@param  id new value for {@link #semanticEventId}
		@deprecated use {@link #setTransactionId(String)} */
	public void setSemanticEventId(String id) {
		setTransactionId(id);
	}

	/** Semantic event identifier.
		@return value of {@link #semanticEventId}
		@deprecated use {@link #getTransactionId()} */
	public String getSemanticEventId() {
		return getTransactionId();
	}

	/** Semantic event identifier of linked event.
		@param id new value for {@link #setTransactionId(String)}
		@deprecated use {@link #setTransactionId(String)} */
	public void setLinkedSemanticEventId(String id) {
		setTransactionId(id);
	}

	/** Semantic event identifier of linked event.
		@return value of {@link #getTransactionId()}
		@deprecated use {@link #getTransactionId()} */
	public String getLinkedSemanticEventId() {
		return getTransactionId();
	}

	/**
	 * Semantic event identifier of linked event. This call prevents
	 * {@link #setTransactionId(String)} from changing the value later.
	 * @param id new value for {@link #getTransactionId()} 
	 */
	public void lockTransactionId(String id) {
		if (id == null || id.length() < 1)
			throw new IllegalArgumentException("lockTranactionId() argument \"+id+\" must be a valid id");
		transactionId = id;
		isTransactionIdLocked = true;
	}

	/** Semantic event identifier of linked event.
		@param id new value for {@link #getTransactionId()} */
	public void setTransactionId(String id) {
		if (isTransactionIdLocked)
			return;
		transactionId = (id == null ? "" : id);
	}
	
	/** Semantic event identifier of linked event.
		@return value of {@link #transactionId} */
	public String getTransactionId() {
		return transactionId;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Accessor for subclasses.  !!!Access should be protected, but this
	 * throws compilation error in subclass {@link edu.cmu.pact.Utilities.OLIMessageObject}.
	 *
	 * @return value of {@link #topProxy}
	 */
	///////////////////////////////////////////////////////////////////////
	public ObjectProxy getTopProxy() {
		return topProxy;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Accessor for subclasses.
	 *
	 * @param  parsingException new value for {@link #parsingException}
	 */
	///////////////////////////////////////////////////////////////////////
	protected void setParsingException(DorminException parsingException) {
		this.parsingException = parsingException;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Accessor for subclasses.
	 *
	 * @param  initializer new String for {@link #Initializer}
	 */
	///////////////////////////////////////////////////////////////////////
	protected void setInitializer(String initializer) {
		Initializer = "";
		if (initializer != null)
			Initializer = initializer;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/**
		If there was an error parsing the message, getParseError will return it
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public DorminException getParseError () {
		return parsingException;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public void setVerb(String VerbName){
		myVerb = VerbName;
	}

	public String getVerb(){
		return myVerb;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public void addParameter(String ParameterName,String Value){
		addParameterToVectors(ParameterName,Value,"S");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public void addParameter(String ParameterName,int Value){
		Integer tempInt = Integer.valueOf(String.valueOf(Value));
		addParameterToVectors(ParameterName,tempInt,"I");
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/**
	
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	public void addParameter(String ParameterName,boolean Value){
		Boolean tempBool = Boolean.valueOf(String.valueOf(Value));
		addParameterToVectors(ParameterName,tempBool,"B");
	}

	public void addParameter(String ParameterName,float Value){
		Float tempFloat = Float.valueOf(String.valueOf(Value));
		addParameterToVectors(ParameterName,tempFloat,"F");
	}
	
	public void addParameter(String ParameterName,double Value){
		Double tempDouble = Double.valueOf(String.valueOf(Value));
		addParameterToVectors(ParameterName,tempDouble,"D");
	}

	public void addParameter(String ParameterName,Vector Value){
//		trace.out("mo", "add ParameterName " + ParameterName +
//				  ", Value.size() " +
//				  (Value == null ? "null" : (new Integer(Value.size())).toString()));
		addParameterToVectors(ParameterName,Value,"L");
	}
	
	public void addParameter(String ParameterName, ObjectProxy Value){
		addParameterToVectors(ParameterName,Value,"O");
	}
	
	public void addParameter(String ParameterName, Range Value){
		addParameterToVectors(ParameterName,Value,"R");
	}
	
	public void addUnknownParameter(String ParameterName,String ParameterRep){
		addParameterToVectors(ParameterName,ParameterRep,"U");
	}
	
	public void addParameter(String ParameterName, char Value) { 
		Character tempC = new Character(Value);
		addParameterToVectors(ParameterName,tempC,"C");
	}
	
	public void addObjectParameter(String ParameterName, ObjectProxy Value){
		addParameterToVectors(ParameterName,Value,"O");
	}
	public void addObjectParameter(String ParameterName, String Value){
		addParameterToVectors(ParameterName,Value,"O");
	}
/*The following routines are very very boring.  They're all shortcuts for AddParameter, allowing the
user to add Indirect & Direct Objects without caring very much about exactly what the heck
the codes for them are.  Another level of independence, I hope.*/

	public void addDirectObject(String Value){
		addParameterToVectors(DirectObjectRep,Value,"S");
	}
	
	public void addDirectObject(int Value){
		Integer tempInt = Integer.valueOf(String.valueOf(Value));
		addParameterToVectors(DirectObjectRep,tempInt,"I");
	}

	public void addDirectObject(boolean Value){
		Boolean tempBool = Boolean.valueOf(String.valueOf(Value));
		addParameterToVectors(DirectObjectRep,tempBool,"B");
	}

	public void addDirectObject(float Value){
		Float tempFloat = Float.valueOf(String.valueOf(Value));
		addParameterToVectors(DirectObjectRep,tempFloat,"F");
	}
	
	public void addDirectObject(double Value){
		Double tempDouble = Double.valueOf(String.valueOf(Value));
		addParameterToVectors(DirectObjectRep,tempDouble,"D");
	}

	public void addDirectObject(Vector Value){
		addParameterToVectors(DirectObjectRep,Value,"L");
	}
	
	public void addDirectObject(ObjectProxy Value){
		addParameterToVectors(DirectObjectRep,Value,"O");
	}
	
	public void addDirectObject(Range Value){
		addParameterToVectors(DirectObjectRep,Value,"R");
	}
	
	public void addUnknownDirectObject(String DirectObjectRep,String ParameterRep){
		addParameterToVectors(DirectObjectRep,ParameterRep,"U");
	}
	
	public void addIndirectObject(String Value){
		addParameterToVectors(IndirectObjectRep,Value,"S");
	}
	
	public void addIndirectObject(int Value){
		Integer tempInt = Integer.valueOf(String.valueOf(Value));
		addParameterToVectors(IndirectObjectRep,tempInt,"I");
	}

	public void addIndirectObject(boolean Value){
		Boolean tempBool = Boolean.valueOf(String.valueOf(Value));
		addParameterToVectors(IndirectObjectRep,tempBool,"B");
	}

	public void addIndirectObject(float Value){
		Float tempFloat = Float.valueOf(String.valueOf(Value));
		addParameterToVectors(IndirectObjectRep,tempFloat,"F");
	}
	
	public void addIndirectObject(double Value){
		Double tempDouble = Double.valueOf(String.valueOf(Value));
		addParameterToVectors(IndirectObjectRep,tempDouble,"D");
	}

	public void addIndirectObject(Vector Value){
		addParameterToVectors(IndirectObjectRep,Value,"L");
	}
	
	public void addIndirectObject(ObjectSpecifier Value){
		addParameterToVectors(IndirectObjectRep,Value,"O");
	}
	
	public void addIndirectObject(Range Value){
		addParameterToVectors(IndirectObjectRep,Value,"R");
	}
	
	public void addUnknownIndirectObject(String IndirectObjectRep,String ParameterRep){
		addParameterToVectors(IndirectObjectRep,ParameterRep,"U");
	}
	
	public String extractUnknownValue(String Name) throws DorminException{
		return extractStrValue(Name);
	}

	public String extractStrValue(String Name) throws DorminException{
		int index = ParameterSearch(Name);
		String returnee = new String();
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'B':if(getBoolVal(index)) returnee = "TRUE";
						 else returnee = "FALSE";
						 break;
	
				case 'U':
				case 'S':returnee = getStrVal(index);
						 break;
				case 'F':returnee = String.valueOf(getFloatVal(index));
						 break;
				case 'D':returnee = String.valueOf(getDoubleVal(index));
						 break;
				case 'R':returnee = (getRangeVal(index)).toString();
						 break;				
				case 'O'://returnee = (getSpecVal(index)).toString();
						 returnee = getStrVal(index);
						 break;
				case 'I':returnee = String.valueOf(getIntVal(index));
						 break;
				case 'L':returnee = (getListVal(index)).toString();
						 break;

			}
		} catch (ArrayIndexOutOfBoundsException a) {
			throw new MissingParameterException(Name);
		}
		catch(DorminException ex){
			throw ex;
		}
		return returnee;
	}
	
	public char getObjectType(String Name) throws MissingParameterException {
		int index = ParameterSearch(Name);
		char value = ' ';
		try{
			value = ((String) Types.elementAt(index)).charAt(0);
		}catch (ArrayIndexOutOfBoundsException a){
			throw new MissingParameterException(Name);
		}
		return value;
	}
	
    public void setParameter (String name, String value) throws MissingParameterException {
        int index = ParameterSearch(name); 
        trace.err ("Names = " + Arrays.asList (Names.toArray()));
        trace.err ("Values = " + Arrays.asList (Values.toArray()));
        try{
            Values.setElementAt(value, index);
        } catch (ArrayIndexOutOfBoundsException a) {
            throw new MissingParameterException(name);
        }
        
    }
    
	//getParameter gets a parameter of any type, specified by name
	public Object getParameter(String Name) throws MissingParameterException {
		int index = ParameterSearch(Name);
		Object value;
		try{
			value = Values.elementAt(index);
		} catch (ArrayIndexOutOfBoundsException a) {
			throw new MissingParameterException(Name);
		}
		return value;
	}
	
	//getOptionalParameter returns null if the parameter is not found
	public Object getOptionalParameter(String Name) {
		Object value = null;
		try {
			value = getParameter(Name);
		}
		catch (MissingParameterException ex) {
		} //ignore missing parameter
		return value;
	}	//////////////////////////////////////////////////////
	/**
		Add the given cell to the comm listener to recieve dormin messages
	*/
	//////////////////////////////////////////////////////

	
	//getParameterNames should only be used for error handling (so the error message can list the parameters we parsed)
	public Vector getParameterNames() {
		return Names;
	}
	
	//getParsedParameterString is used for debugging. It prints out the parameters parsed and their
	//values
	public String getParsedParameterString() {
		String outstring = "";
		for (int i=0;i<Names.size();++i)
			outstring = outstring + Names.elementAt(i)+"="+Values.elementAt(i)+"|";
		return outstring;
	}

	/**
	* extracts Object description as a String
	*/	
	public String extractObjectValue(String Name) throws DorminException{
		int index = ParameterSearch(Name);
		String returnee = new String();
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'B':if(getBoolVal(index)) returnee = "TRUE";
						 else returnee = "FALSE";
						 break;
	
				case 'U':
				case 'S':returnee = getStrVal(index);
						 break;
				case 'F':returnee = ""+getFloatVal(index);
						 break;
				case 'D':returnee = ""+getDoubleVal(index);
						 break;
				case 'R':Range range = getRangeVal(index);
						 returnee = range.getParent().toString(); //This isn't right -- we're returning an object, but only the parent of the range
						 break;				
				case 'O'://returnee = (getSpecVal(index)).toString();
						 returnee = getStrVal(index);
						 break;
				case 'I':returnee = ""+getIntVal(index);
						 break;
				case 'L':returnee = (getListVal(index)).toString();
						 break;

			}
		} catch (ArrayIndexOutOfBoundsException a){
			throw new MissingParameterException(Name);
		}
		catch (DorminException ex){
			throw ex;
		}
		return returnee;
	}

	public char extractCharValue(String Name) throws MissingParameterException,BadCoerceException{
		int index = ParameterSearch(Name);
		char returnee = 'A';
		try {
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'B':if(getBoolVal(index)) returnee = 'T';
						 else returnee = 'F';
						 break;
				case 'U':
				case 'S':returnee = (getStrVal(index).toCharArray())[0];						 
						 break;
				case 'C':returnee = getCharVal(index);
						 break;
				default: throw new BadCoerceException("The value named "+Name+ " is a "+StrFromType(value)+", and cannot be coerced into an integer");
			}
		}catch (ArrayIndexOutOfBoundsException a) {throw new MissingParameterException(Name);};
		return returnee;
	}

	public int extractIntValue(String Name) throws MissingParameterException,BadCoerceException{
		int index = ParameterSearch(Name);
		int returnee=0;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'F':returnee = (int) getFloatVal(index);
						 break;
				case 'D':returnee = (int) getDoubleVal(index);
						 break;
				case 'I':returnee = getIntVal(index);
						 break;
				case 'B':if(getBoolVal(index)) returnee = 1;
						 else returnee =0;
						 break;
				case 'S':String tString = getStrVal(index);
						 try {
						 	Integer tInt = Integer.valueOf(tString);
						  	returnee = tInt.intValue();
						 } catch (NumberFormatException n){ throw new BadCoerceException("The String " + tString + " cannot be parsed as an integer");};
						 break;	
				default :throw new BadCoerceException("The value named " + Name + " is a"+StrFromType(value)+", and cannot be coerced into an integer");		 					 
			}
		}catch (ArrayIndexOutOfBoundsException a){ throw new MissingParameterException(Name);};
		return returnee;
	}
	
	public boolean extractBoolValue(String Name) throws MissingParameterException,BadCoerceException{
		int index = ParameterSearch(Name);
		boolean returnee=true;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'F':if(getFloatVal(index)== (float) 0.0) returnee = false;
						 else returnee = true;
						 break;
				case 'D':if(getDoubleVal(index)== (double) 0.0) returnee = false;
						 else returnee = true;
						 break;
				case 'I':if(getIntVal(index) ==0 ) returnee = false;
						 else returnee = true;
						 break;
				case 'B':returnee = getBoolVal(index);
						 break;
				case 'S':String tString = getStrVal(index);
						 if(tString.equals("TRUE")) returnee = true;
						 else {
						 	if(tString.equals("FALSE")) returnee = false;
						 	else throw new BadCoerceException("The String " + tString + " cannot be coerced into a boolean value");
						 }
						 break;	
				default :throw new BadCoerceException("The value named " + Name + " is a"+StrFromType(value)+", and cannot be coerced into a boolean");			 					 
			}
		}catch (ArrayIndexOutOfBoundsException a){throw new MissingParameterException(Name);};
		return returnee;
	}
	
	public float extractFloatValue(String Name) throws MissingParameterException,BadCoerceException{
		int index = ParameterSearch(Name);
	 	float returnee = (float) 0.0;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'B':if(getBoolVal(index)) returnee = (float) 1.0;
						 else returnee =(float) 0.0;
						 break;
				case 'F':returnee = getFloatVal(index);
						 break;
				case 'D':returnee = (new Double(getDoubleVal(index))).floatValue();
						 break;
				case 'I':returnee = (float) getIntVal(index);
						 break;
				case 'S':String tString = getStrVal(index);
						 try {
						 	Float tFloat = new Float(tString);
						  	returnee = tFloat.floatValue();
						 } catch (NumberFormatException n){throw new BadCoerceException("The String " + tString + " cannot be parsed as a float");};
						 break;	
				default :throw new BadCoerceException("The value named " + Name + " is a"+StrFromType(value)+", and cannot be coerced into a flaot");
								 					 
			}
		}catch (ArrayIndexOutOfBoundsException a) {throw new MissingParameterException(Name);};
		return returnee;
	}

	public double extractDoubleValue(String Name) throws MissingParameterException,BadCoerceException{
		int index = ParameterSearch(Name);
	 	double returnee = (double) 0.0;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'B':if(getBoolVal(index)) returnee = (double) 1.0;
						 else returnee =(double) 0.0;
						 break;
				case 'F':returnee = (new Float(getFloatVal(index))).doubleValue();
						 break;
				case 'D':returnee = getDoubleVal(index);
						 break;
				case 'I':returnee = (double) getIntVal(index);
						 break;
				case 'S':String tString = getStrVal(index);
						 try {
						 	Double tDouble = new Double(tString);
						  	returnee = tDouble.doubleValue();
						 } catch (NumberFormatException n){throw new BadCoerceException("The String " + tString + " cannot be parsed as a double");};
						 break;	
				default :throw new BadCoerceException("The value named " + Name + " is a"+StrFromType(value)+", and cannot be coerced into a double");
								 					 
			}
		}catch (ArrayIndexOutOfBoundsException a) {throw new MissingParameterException(Name);};
		return returnee;
	}


	public Vector extractListValue(String Name) throws DorminException{
		int index = ParameterSearch(Name);
		Vector returnee = new Vector();
		if (index == -1) return returnee;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
//			trace.out("mo", "Name " + Name + ", index " + index +
//					  ", value '" + value + "'");
			switch(value){
				case 'F': Float tFloat = Float.valueOf(String.valueOf(getFloatVal(index)));
						  returnee.addElement(tFloat);
						  break;
				case 'D': Double tDouble = Double.valueOf(String.valueOf(getDoubleVal(index)));
						  returnee.addElement(tDouble);
						  break;
				case 'O': //returnee.addElement(getSpecVal(index));
						  returnee.addElement(extractObjectValue(Name));
						  break;
				case 'U': returnee.addElement(getStrVal(index));
						  break;
				case 'L': returnee = getListVal(index);
						  break;
				case 'B': Boolean tBool = Boolean.valueOf(String.valueOf(getBoolVal(index)));
						  returnee.addElement(tBool);
						  break;
				case 'R': returnee.addElement(getRangeVal(index));
						  break;
				case 'I': Integer tInt = Integer.valueOf(String.valueOf(getIntVal(index)));
						  returnee.addElement(tInt);
						  break;
				case 'S': returnee.addElement(getStrVal(index));
						
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			throw new MissingParameterException(Name);
		}
		catch (DorminException ex){
			throw ex;
		}
		return returnee;
	}
	
	public Range extractRangeValue(String Name) throws DorminException{
		int index = ParameterSearch(Name);
		//Range returnee = new Range();
		Range returnee = null;
		try{
			char value = ((String) Types.elementAt(index)).charAt(0);
			switch(value){
				case 'R': returnee = getRangeVal(index);
						 break;
				default:throw new BadCoerceException("The value named " + Name + " is a"+StrFromType(value)+", and cannot be coerced into a Range");
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			throw new MissingParameterException(Name);
		}
		catch (DorminException ex){
			throw ex;
		}
		return returnee;
	}

	public String extractVerb(){
		return myVerb;
	}
	// my synch
	public synchronized void send(Target destinationTarget){
		
		Integer messageInt = new Integer (0);
		messageInt = (Integer)(getOptionalParameter("MESSAGENUMBER"));
		
		//trace.out (5, this, "sending message. messageInt = " + messageInt);
		if (messageInt == null) {
			this.addParameter("MESSAGENUMBER", Communicator.messageNumber);
			Communicator.addMessage(Communicator.messageNumber, this); 
			Communicator.messageNumber++;
		}
		
			
		try {
			CommManager.instance().sendJavaMessage(this);
		} catch (NullPointerException e) {	
			trace.err("Can't find CommManager: null pointer exception");
		} catch (java.lang.NoClassDefFoundError e) {
			trace.err ("Can't find CommManager: no class def found");
		} catch (Exception e) { 
			trace.err ("CommManager: exception = " + e);
		}

		
		if(showMessage && destinationTarget != null)
		  destinationTarget.transmitEvent(this);

		
	}

	/**
	 * Set a single PROPERTYVALUES value given the matching PROPERTYNAMES name.
	 * @param name name to find in PROPERTYNAMES; stops on the first matching name
	 * @param value value to set
	 */
	public void setProperty(String name, Object value) {
		boolean toAddParams = false;
		List pNames;
		List pValues;
		try {
			pNames = (List) getParameter("PROPERTYNAMES");
			pValues = (List) getParameter("PROPERTYVALUES");
		} catch (MissingParameterException mpe) {
			pNames = new Vector();
			pValues = new Vector();
			toAddParams = true;
		}
		int i = 0;
		boolean setValue = false;
		if (value == null)              // prevent trouble w/ null values in vector
			value = "null";
		for (Iterator it = pNames.iterator(); !setValue && it.hasNext(); ++i) {
			String pName = (String) it.next();
			if (name == null) {
				if (pName == null) {
					pValues.set(i, value);
					setValue = true;
				}
			} else if (name.equals(pName)) {
				pValues.set(i, value);
				setValue = true;
			}				
		}
		if (!setValue) {      // found no existing property by this name
			pNames.add(name);
			pValues.add(value);
			setValue = true;
		}
		if (toAddParams) {
			addParameter("PROPERTYNAMES", (Vector) pNames);
			addParameter("PROPERTYVALUES", (Vector) pValues);
		}
	}

    public void setProperty(String name, int i) {
        setProperty(name, new Integer(i));
    }

    public void addPropertyElement(String name, Object value) {
        Object prev = getProperty(name);
        trace.out("sp", "addPropertyElement(" + name + ", " + value + ") before: " + prev);
        Vector v;

        if (prev instanceof Vector) {
            v = (Vector)prev;
            v.add(value);
        } else if (prev == null) {
        	v = new Vector();
        	v.add(value);
            setProperty(name, value);
        } else
        	throw new IllegalStateException("addPropertyElement(" + name + ", " + value +
        			"): property already exists with value " + prev);
        trace.out("sp", "addPropertyElement(" + name + ", " + value + ") after: " + getProperty(name));
    }
    public void addPropertyElement(String name, int value) {
        addPropertyElement(name, new Integer(value));
    }

    public Vector<String> propertyNames() {
        try {
            return (Vector<String>)getParameter("PROPERTYNAMES");
		} catch (MissingParameterException mpe) {
            Vector v = new Vector();
            addParameter("PROPERTYNAMES", v);
            return v;
        }
    }

	/**
	 * Get a single PROPERTYVALUES value given the matching PROPERTYNAMES name.
	 * @param name to find in PROPERTYNAMES; stops on the first matching name
	 * @param ignoreCase true means make the name lookup case-insensitive
	 * @return value from PROPERTYVALUES whose index matches the given name;
	 *         null if not found
	 * @throw IllegalStateException if PROPERTYNAMES or PROPERTYVALUES missing 
	 */
	public Object getProperty(String name, boolean ignoreCase) {
		String param = null;
		List pNames;
		List pValues;
		
		pNames = (List) getOptionalParameter(param = "PROPERTYNAMES");
		
		if (pNames == (null)) return null;
		
		try {
			pValues = (List) getParameter(param = "PROPERTYVALUES");
		} catch (MissingParameterException mpe) {
			throw new IllegalStateException("missing "+param+": "+
						mpe.getMessage());
		}
		int i = 0;
		for (Iterator it = pNames.iterator(); it.hasNext(); ++i) {
			String pName = (String) it.next();
			if (name == null) {
				if (pName == null)
					return pValues.get(i);
			} else {
				if (name.equals(pName) || (ignoreCase && name.equalsIgnoreCase(pName)))
					return pValues.get(i);
			}				
		}
		return null;
	}


	/**
	 * Get a single PROPERTYVALUES value given the matching PROPERTYNAMES name.
	 * @param name to find in PROPERTYNAMES; stops on the first matching name (case-insensitive)
	 * @return value from PROPERTYVALUES whose index matches the given name;
	 *         null if not found
	 * @throw IllegalStateException if PROPERTYNAMES or PROPERTYVALUES missing 
	 */
    public Object getProperty(String name) {
        return getProperty(name, true);
    }

	/**
	 * Convenience method to get the "MessageType" property.
	 * @return value of MessageType property, as String 
	 */
	public String getMessageTypeProperty() {
		return (String)getProperty("MessageType", true);
	}

    private boolean cmp(Object o1, Object o2) {
        if (o1==null)
            return o2==null;
        if (o2==null)
            return o1==null;
        if (o1 instanceof String && o2 instanceof String)
            return ((String)o1).equalsIgnoreCase((String)o2);
        return o1.equals(o2);
    }
    
    public boolean matchProperty(String name, Object value) {
        Object property = getProperty(name);

        trace.out("sp", "matchProperty(" + name + ", " + value + ", (" + property.getClass() + ")" + property + ")");
        if (property instanceof Vector) {
            for (Object o: (Vector)property) {
                if (cmp(value, o))
                    return true;
            }
            return false;
        } else
            return cmp(value, property);
    }
	
	/**
	 * Convenience method to test whether the given type matches this message's
	 * {@link #getMessageTypeProperty()}.
	 * @param type
	 * @return true if types match (case-insensitive)
	 */
    public boolean isMessageType(String type) {
        return type.equalsIgnoreCase(getMessageTypeProperty());
    }

	/**
	 * Convenience method to test whether one of the given types matches this message's
	 * {@link #getMessageTypeProperty()}.
	 * @param types
	 * @return true if any element of types[] matches (case-insensitive)
	 */
    public boolean isMessageType(String[] types) {
        for (String msgType: types) {
            if (isMessageType(msgType))
                return true;
        }
        return false;
    }
    	
	public Vector getProperties(){
		return Names;	
	}
	
	public char getPropertyType(String PropName) throws MissingParameterException{
		int index;
		char returnee = 'A';
		index = ParameterSearch(PropName);
		try{
			String tString = (String) Types.elementAt(index);
			returnee = tString.charAt(0);
		} catch (ArrayIndexOutOfBoundsException a){
			throw new MissingParameterException(PropName);
		}
		return returnee;
	}
	
	public float getVersion(){
		return version;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/**
		toString()
      */
	/////////////////////////////////////////////////////////////////////////////
	public String toString(){
		
            
            if(Initializer != null)
			return Initializer;
	
		try{
			int messNum = this.extractIntValue("MESSAGENUMBER");
		} catch (DorminException e) {
			this.addParameter("MESSAGENUMBER", Communicator.messageNumber);
			Communicator.addMessage(Communicator.messageNumber, this); 
			Communicator.messageNumber++;
		}	
	
		String returnee=new String();
		int length = 0;
        if (myVerb != null)
            length = myVerb.length();
        returnee = "SE/"+version +"&VERB=S:"+length+":"+myVerb+"&";
		int i;
		try{
			char dataType;
			Object tempObj;
			String appendee,result;

			for( i=0;  i<Types.size();  i++){
				dataType = ((String)Types.elementAt(i)).charAt(0);
				tempObj = Values.elementAt(i);
				appendee = stringFromObject(dataType,tempObj);
				result = (String) Names.elementAt(i) +"="+appendee;
				returnee = returnee + result + "&";
			}				
		} catch (ArrayIndexOutOfBoundsException a){
			a.printStackTrace();
		}
	
		return returnee;
	}

	/////////////////////////////////////////////////////////////////////////////
	/**
		
      */
	/////////////////////////////////////////////////////////////////////////////
	public String toPrettyString(){
		if(Initializer != null)
			return Initializer;
        String returnee = null;
		try{
			int messNum = this.extractIntValue("MESSAGENUMBER");
	
            returnee=new String();
    		returnee = "\n    SE/"+version +"&VERB=S:"+myVerb.length()+":"+myVerb+"&";
    		int i;
			char dataType;
			Object tempObj;
			String appendee,result;

			for( i=0;  i<Types.size();  i++){
				dataType = ((String)Types.elementAt(i)).charAt(0);
				tempObj = Values.elementAt(i);
				//		"MO toString dataType = "+dataType+" tempObj = "+tempObj);
				appendee = stringFromObject(dataType,tempObj);
				result = "\n    " + (String) Names.elementAt(i) +"="+appendee;
				returnee = returnee + result + "&";
			}				
		} catch (ArrayIndexOutOfBoundsException a){
			a.printStackTrace();
        } catch (DorminException e) {
//            e.printStackTrace();
            this.addParameter("MESSAGENUMBER", Communicator.messageNumber);
            Communicator.addMessage(Communicator.messageNumber, this); 
            Communicator.messageNumber++;
        }  catch (NullPointerException e) {
            return returnee + "null";
        } 
	
		return returnee + "\n";
	}

	/////////////////////////////////////////////////////////////////////////////
	/**
		These functions are protected utilities.  
      	Should never be used by the developers

	//In most cases, these objects have to be recorded as a special form of string, for
	//DORMIN's encoding scheme.  This method takes care of that by converting the object 
	//into the string.


      */
	/////////////////////////////////////////////////////////////////////////////
	protected String stringFromObject(char type,Object tempObj){
		String returnee = "";
		try {
			switch(type){
				case 'F':returnee = ((Float) tempObj).toString();
						 returnee = "F"+":"+String.valueOf(returnee.length())+":"+returnee;
						 break;
				case 'D':returnee = ((Double) tempObj).toString();
						 returnee = "D"+":"+String.valueOf(returnee.length())+":"+returnee;
						 break;
				case 'O':if(tempObj instanceof ObjectProxy)
						 	returnee = stringFromSpec((ObjectProxy)tempObj);
						 else {
						 	returnee = (String)tempObj;
						 }
						 returnee = "O"+":"+returnee;
					 	 break;
				case 'L':int s = ((Vector)tempObj).size();
						 returnee = "L"+":"+String.valueOf(s)+":"+stringFromList((Vector)tempObj);
				         break;
				case 'B':if (((Boolean)tempObj).booleanValue()) returnee = "T";
					     else returnee = "F";
					     returnee = "B"+":"+"1"+":"+returnee;
					     break;
				case 'R':returnee = ((Range) tempObj).toString();
						 returnee = "R"+":"+"["+returnee+"]";
						 break;
				case 'I':returnee = ((Integer) tempObj).toString();
						 returnee = "I"+":"+String.valueOf(returnee.length())+":"+returnee;
						 break;
				case 'S':returnee = (String) tempObj;
						 returnee = "S"+":"+String.valueOf(returnee.length())+":"+returnee;
						 break;
				case 'U':returnee = (tempObj == null ? "null" : tempObj.toString());
				 		 returnee = "S"+":"+String.valueOf(returnee.length())+":"+returnee;
				 		 break;
				default:returnee = "ERROR";
			}
		} catch (NullPointerException e) {
			return null;
		}
		//trace.out (10, this, "object is "+returnee);
		return returnee;
	}

	/////////////////////////////////////////////////////////////////////////////
	/**

      */
	/////////////////////////////////////////////////////////////////////////////
	private String stringFromSpec(ObjectProxy Spec){
		return Spec.getStrDescription();
	}
	
	private char getObjectType(Object obj){
		char objType = 'U';
		if(obj instanceof Float)
			return 'F';
		if(obj instanceof Double)
			return 'D';
		if(obj instanceof ObjectSpecifier)
			return 'O';
		if(obj instanceof Vector)
			return 'L';
		if(obj instanceof Boolean)
			return 'B';
		if(obj instanceof Range)
			return 'R';
		if(obj instanceof Integer)
			return 'I';
		if(obj instanceof String)
			return 'S';
		return objType;
	}
		
	protected String stringFromList(Vector tempObj){
                String Tempstring,returnee;
		returnee = "[";
		int i;
		
		try{
			Object temp;
			for(i=0;i<tempObj.size();i++){
				temp = tempObj.elementAt(i);
				char objType = getObjectType(temp);
				Tempstring = stringFromObject(objType,temp);
				if(i>0) returnee = returnee + ",";
				returnee = returnee+Tempstring;
			}
		}catch (ArrayIndexOutOfBoundsException a){};
		returnee = returnee + "]";
		return returnee;
	}
	
	//parseIgnorableErrors is a little strange. If we encounter an error parsing the message
	//(like NoSuchObjectException), we still need to try and parse the "ignoreErrorClasses" parameter
	//so that we know whether to report such an error. This method does that.
	public void parseIgnorableErrors(String messageString,int start) {
		int pos = messageString.indexOf("IGNOREERRORCLASSES=L:",start);
		if (pos != -1) {
			try {
				pos += 21; //length of "IGNOREERRORCLASSES=L:"
				ParseResult classes = parseListResult(messageString,pos);
				addParameterToVectors("IGNOREERRORCLASSES",classes.getParsedValue(),"L");
//				trace.out("parseIgnorableErrors found: "+classes.getParsedValue());
			}
			catch (DorminException e) {
				trace.out("DorminException trying to interpret IGNOREERRORCLASSES");
			} //ignore DorminException -- this means we can't interpret IGNOREERRORCLASSES
		}
	}			
	
	private void parseMessage(String messageString) throws DorminException {
	// ParseMessage is a rather complicated method which takes a String object (inString)
	// and extracts the DORMIN parameter/value pairs, which are then recorded in the correct
	// vectors (Names,Values & Types).  
	//
	 	int pos;
	 	int maxPos = messageString.length();
	 	String name;
	 	int equalPlace;
	 	char dataType;
	 	ParseResult result;
	 	
	 	pos = messageString.indexOf("&"); //find the first ampersand. Everything before this is system info
	 	while (pos < maxPos-1) { //subtract 1, so we can ignore last ampersand
	 		equalPlace = messageString.indexOf("=",pos);
			name = messageString.substring(pos+1,equalPlace);
	 		pos = equalPlace+1;
	 		result = parseParameter(messageString,pos);
	 		pos = result.getNewPosition();
	 		addParameterToVectors(name,result.getParsedValue(),new Character(result.getParsedType()).toString());
	 		if (name.equalsIgnoreCase("Verb"))
	 			myVerb = (String)(result.getParsedValue());
	 		
		}
	}
	
	private ParseResult parseParameter (String message,int pos) throws DorminException {
//		trace.out("parsing parameter starting at "+message.substring(pos,pos+5)+"...");
		currentPos = pos; //increment stored position, in case a parse error occurs
		char parameterType = message.charAt(pos);
		switch (parameterType) {
			case 'F':
			case 'D':
				return parseFloatResult(message,pos+2);
			case 'I':
				return parseIntegerResult(message,pos+2);
			case 'B':
				return parseBooleanResult(message,pos+2);
			case 'S':
				return parseStringResult(message,pos+2);
			case 'L':
				return parseListResult(message,pos+2);
			case 'O':
				return parseObjectResult(message,pos+2);
			case 'R':
				return parseRangeResult(message,pos+2);
			default:
				throw new DataFormatException(parameterType+" is not a valid data type code");
		}
	}
	
	private ParseResult parseFloatResult(String message,int pos) throws DataFormatException {
		int floatLength = -1;
		String floatString="???";
		try {
			int lengthEnd = message.indexOf(':',pos);
			floatLength = parseInt(message,pos,lengthEnd);
			floatString = message.substring(lengthEnd+1,lengthEnd+1+floatLength); //wish I could do this without creating a string
			Float val = Float.valueOf(floatString);
			return new ParseResult(val,lengthEnd+1+floatLength,'F');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing float in string "+message.substring(pos));
		}
		catch (NumberFormatException e) {
			if (floatLength == -1)
				throw new DataFormatException(e.toString()+" is not an integer specifying the float length");
			else
				throw new DataFormatException(floatString+" is not a floating point number");
		}
	}
		
	private ParseResult parseIntegerResult(String message,int pos) throws DataFormatException {
		int intLength = -1;
		try {
			int lengthEnd = message.indexOf(':',pos);
//			trace.out("about to parse int in "+message.substring(pos,lengthEnd));
			intLength = parseInt(message,pos,lengthEnd);
//			trace.out("intlength is "+intLength);
//			trace.out("second int in "+message.substring(lengthEnd+1,lengthEnd+1+intLength));
			Integer val = Integer.valueOf(String.valueOf(parseInt(message,lengthEnd+1,lengthEnd+1+intLength)));
//			trace.out("Integer parameter is "+val);
			return new ParseResult(val,lengthEnd+1+intLength,'I');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing int in string "+message.substring(pos));
		}
		catch (NumberFormatException e) {
			if (intLength == -1)
				throw new DataFormatException(e.toString()+" is not an integer specifying the integer length");
			else
				throw new DataFormatException(e.toString()+" is not an integer");
		}
	}
	
	//for Booleans, we only care about the first character of the value
	//It can be T or 1 for true, or F or 0 for false. This allows "True" or "False" as values
	//(as well as T or F)
	private ParseResult parseBooleanResult(String message,int pos) throws DataFormatException {
		Boolean value;
		try {
			int lengthEnd = message.indexOf(':',pos);
			int boolLength = -1;
			boolLength = parseInt(message,pos,lengthEnd);
			char firstChar = message.charAt(lengthEnd+1);
//			trace.out("firstchar is "+firstChar);
			if (firstChar == 'T' || firstChar == 't' || firstChar == '1')
				value = Boolean.valueOf("true");
			else if (firstChar == 'F' || firstChar == 'f' || firstChar == '0')
				value = Boolean.valueOf("false");
			else	
				throw new DataFormatException("Illegal Boolean value: "+message.substring(lengthEnd,boolLength));
//			trace.out("Got boolean: "+" will start parsing at "+message.substring(lengthEnd+1+boolLength,lengthEnd+boolLength+10));
			return new ParseResult(value,lengthEnd+1+boolLength,'B');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing boolean in string "+message.substring(pos));
		}
		catch (NumberFormatException e) {
			throw new DataFormatException(e.toString()+" is not an integer specifying the boolean length");
		}
	}
	
	private ParseResult parseStringResult(String message,int pos) throws DataFormatException {
		String value;
		try {
			int lengthEnd = message.indexOf(':',pos);
			int stringLength = -1;
			stringLength = parseInt(message,pos,lengthEnd);
//			trace.out("in parseStringResult, string length is "+stringLength);
			value = message.substring(lengthEnd+1,lengthEnd+1+stringLength);
//			trace.out("string value is "+value);
			return new ParseResult(value,lengthEnd+1+stringLength,'S');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing string in "+message.substring(pos));
		}
		catch (NumberFormatException e) {
			throw new DataFormatException(e.toString()+" is not an integer specifying the string length");
		}
	}
	
	private ParseResult parseListResult(String message,int pos) throws DataFormatException, DorminException {
		try {
			Vector values = new Vector();
			int lengthEnd = message.indexOf(':',pos);
			int listLength = -1;
			listLength = parseInt(message,pos,lengthEnd);
			int parameterStart = lengthEnd+2; //skip : and [
			for (int i=0;i<listLength;++i) {
				ParseResult thisResult = parseParameter(message,parameterStart);
				values.addElement(thisResult.getParsedValue());
				parameterStart = thisResult.getNewPosition()+1;
			}
			return new ParseResult(values,parameterStart,'L');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing list in "+message.substring(pos));
		}
		catch (NumberFormatException e) {
			throw new DataFormatException(e.toString()+" is not an integer specifying the list length");
		}
	}
	
	private ParseResult parseObjectResult(String message,int pos) throws DorminException {
		ObjectProxy currentObject = topProxy;
		ParseResult typeResult=null,formResult=null,dataResult=null;
		try {
			int lengthEnd = message.indexOf(':',pos);
			int numObjects = -1;
			
			numObjects = parseInt(message,pos,lengthEnd);
			int objectPos = lengthEnd+1; //skip :
			for (int i=0;i<numObjects;++i) {
				typeResult=null;
				formResult=null;
				dataResult=null;
//				trace.out("current object is "+currentObject);
				typeResult = parseParameter(message,objectPos);
				String type = (String)(typeResult.getParsedValue());
				objectPos = typeResult.getNewPosition()+1;
				
				formResult = parseParameter(message,objectPos);
				String form = (String)(formResult.getParsedValue());
				objectPos = formResult.getNewPosition()+1;
				
				dataResult = parseParameter(message,objectPos);
				Object data = dataResult.getParsedValue();
				objectPos = dataResult.getNewPosition()+1;

//				trace.out("type: "+type+" form: "+form+" data: "+data);
				
				//don't bother with getContainedObjectBy for the application object
				//(normally, this is explicitly specified, but it doesn't have to be)
				// need to be overwritten: there might be a lot of top objects
				
				ObjectProxy nextObject = null;
				
				//just set the top proxy to this by default
				if(i == 0)
					nextObject = topProxy;
				else {
					nextObject = currentObject.getContainedObjectBy(type,form,data.toString());
				}
				// try to ignore the topObject
				// This shows the weekness of Dormin message structure implementation:
				// We have only "Object" which is in fact a "Sender", so on the "Receiver" side
				// it needs to have the same sender's hierarhy of "Object"s except the top one:
				// For example : 	sender -   "Application,Position,1,Grapher,Position,1"
				//					receiver -  "Tutor,Position,1,Grapher,Position,1"
				// Ideas?
				if (nextObject == null)
					throw new NoSuchObjectException("Can't find object '"+type+","+form+","+data+"' in container '"+currentObject+"'");
				currentObject = nextObject;
			}
//			trace.out("parsed object: "+currentObject);
			return new ParseResult(currentObject,objectPos-1,'O');
		}
		catch (ClassCastException e) {
			throw new DataFormatException("Bad type specifier parsing object contained in "+currentObject+": "+e);
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing object parameter: contained object specified as '"+message.substring(pos)+"'"+";container is "+currentObject);
		}
		catch (NumberFormatException e) {
			throw new DataFormatException(e.toString()+" is not an integer specifying the number of objects");
		}
		catch (DorminException e) { //exception parsing some component of object
			if (typeResult == null)
				throw new DataFormatException("Bad type specifier parsing object contained in "+currentObject+": "+e);
			else if (formResult == null)
				throw new DataFormatException("Bad form specifier parsing object contained in "+currentObject+": "+e+"[type was "+typeResult.getParsedValue()+"]");
			else if (dataResult == null)
				throw new DataFormatException("Bad data specifier parsing object contained in "+currentObject+": "+e+"[type was "+typeResult.getParsedValue()+"]"+"[form was "+formResult.getParsedValue()+"]");
			else
				throw e;
		}
			
//		catch (NumberFormatException e) {
//			throw new DataFormatException(e.toString()+" is not an integer specifying the list length");
//}
	
	}



		
/*			boolean foundLastObject = false;
			while (!foundLastObject) {
//				trace.out("current object is "+currentObject);
				int endOfType = message.indexOf(',',pos);
				String type = message.substring(pos,endOfType);
				int endOfForm = message.indexOf(',',endOfType+1);
				String form = message.substring(endOfType+1,endOfForm);
				//If this is the last object in the chain, it ends with an ampersand; otherwise, it ends with
				//a comma.
				int dataEndPos = endOfForm+1;
				boolean foundEndOfObject=false;
				for (dataEndPos = endOfForm+1;!foundEndOfObject;++dataEndPos) {
					if (message.charAt(dataEndPos) == ',')
						foundEndOfObject=true;
					else if (message.charAt(dataEndPos) == '&') {
						foundEndOfObject=true;
						foundLastObject=true;
					}
					else if (message.charAt(dataEndPos) == ';') { //semicolon used at end of parent object in range
						foundEndOfObject=true;
						foundLastObject=true;
					}
					else if (message.charAt(dataEndPos) == ']') { //close bracket if object is last item in a list
						foundEndOfObject=true;
						foundLastObject=true;
					}
				}
				String data = message.substring(endOfForm+1,dataEndPos-1);
//				trace.out("type: "+type+" form: "+form+" data: "+data+" found last: "+foundLastObject);
				//don't bother with getContainedObjectBy for the application object
				//(normally, this is explicitly specified, but it doesn't have to be)
				ObjectProxy nextObject = null;
				if (type.equalsIgnoreCase("Application"))
					nextObject = topProxy;
				else
					nextObject = currentObject.getContainedObjectBy(type,form,data);
				if (nextObject == null)
					throw new NoSuchObjectException("Can't find object '"+type+","+form+","+data+"' in container '"+currentObject+"'");
				currentObject = nextObject;
				pos = dataEndPos;
			}
//			trace.out("parsed object: "+currentObject);
			return new ParseResult(currentObject,pos-1,'O');
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new DataFormatException("Parsing object parameter: contained object specified as '"+message.substring(pos)+"'"+";container is "+currentObject);
		}
//		catch (NumberFormatException e) {
//			throw new DataFormatException(e.toString()+" is not an integer specifying the list length");
//		}*/
	
	private ParseResult parseRangeResult(String message,int pos) throws DataFormatException, NoSuchObjectException, DorminException {
		int curPos = pos+3; //skip [O:
		ParseResult parent = parseObjectResult(message,curPos);
		curPos = parent.getNewPosition()+3; //skip ;S:
		ParseResult type = parseStringResult(message,curPos);
		curPos = type.getNewPosition()+3; //skip ;L:
		ParseResult list = parseListResult(message,curPos);
		Range newRange = new Range((ObjectProxy)(parent.getParsedValue()),(String)(type.getParsedValue()),(Vector)(list.getParsedValue()));
		return new ParseResult(newRange,list.getNewPosition()+1,'R'); //add 1 to skip close bracket
	}
	
    /**
     * parseInt parses the string argument as a signed decimal integer 
     * starting at the specified place and ending at the specified place.
     * This method is mostly copied from java.lang.Integer.
     * The characters in the string 
     * must all be digits of the specified radix (as determined by 
     * whether <code>Character.digit</code> returns a 
     * nonnegative value), except that the first character may be an 
     * ASCII minus sign <code>'-'</code> to indicate a negative value. 
     * The resulting integer value is returned. 
     *
     * @param      s   the <code>String</code> containing the integer.
     * @param      start   the starting point in the string.
     * @param      length  the length of the string to parse
     * @return     the integer represented by the string argument
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable integer.

     * @since      JDK1.0
     */
    public static int parseInt(String s, int start, int end) 
		throws NumberFormatException 
    {
        if (s == null) {
            throw new NumberFormatException("null");
        }

	int result = 0;
	boolean negative = false;
	int i = start, max = end;
	int limit;
	int multmin;
	int digit;

	if (max > 0) {
	    if (s.charAt(start) == '-') {
		negative = true;
		limit = Integer.MIN_VALUE;
		i++;
	    } else {
		limit = -Integer.MAX_VALUE;
	    }
	    multmin = limit / 10;
	    if (i < max) {
		digit = Character.digit(s.charAt(i++),10);
		if (digit < 0) {
		    throw new NumberFormatException(s.substring(start,end));
		} else {
		    result = -digit;
		}
	    }
	    while (i < max) {
		// Accumulating negatively avoids surprises near MAX_VALUE
		digit = Character.digit(s.charAt(i++),10);
		if (digit < 0) {
		    throw new NumberFormatException(s.substring(start,end));
		}
		if (result < multmin) {
		    throw new NumberFormatException(s.substring(start,end));
		}
		result *= 10;
		if (result < limit + digit) {
		    throw new NumberFormatException(s.substring(start,end));
		}
		result -= digit;
	    }
	} else {
	    throw new NumberFormatException(s);
	}
	if (negative) {
	    if (i > 1) {
		return result;
	    } else {	/* Only got "-" */
		throw new NumberFormatException(s.substring(start,end));
	    }
	} else {
	    return -result;
	}
    }

	private void parseSubstring(String inSub) throws DorminException{
		//First we must identify the name and type, and then extract that rich nugget of data
		//that lies within
		int colon,nextcolon;
		String name;
		char type;
		String value;
		int equals = inSub.indexOf("=");
		name = inSub.substring(0,equals);
		type = inSub.substring(equals+1,equals+2).charAt(0);
		colon = inSub.indexOf(":");
		switch (type) {
			case 'F':
			case 'D':
			case 'B':
			case 'I':
			case 'U':
			case 'S':	nextcolon = inSub.indexOf(":",colon+1);
						String sLength = inSub.substring(colon+1,nextcolon);
						colon = nextcolon;		
						try{
							int intLength = Integer.parseInt(sLength);
							int realLen = (inSub.length()-colon-1);
							if(intLength != realLen){
								throw new DataFormatException("Specified length of "+inSub.substring(colon+1)+" is "+intLength+" real length = "+realLen);
							}
							nextcolon = colon+intLength;
						} catch (NumberFormatException e) {
							throw new DataFormatException("MessageObject parseSubstring: String length "+ sLength+" is not a number for "+inSub);
						}
						catch (IndexOutOfBoundsException ex){
							throw new DataFormatException("MessageObject parseSubstring: Bad object length "+sLength+" for "+inSub);
						}
						value = inSub.substring(colon+1);
						break;
			default:
						value = inSub.substring(colon+1);
		}
		try{
			addStringForm(name,type,value);	
		} catch (DorminException e){
			throw e;
		}
	}	
	
	private synchronized void addStringForm(String val_name,char val_type, String val_rep) throws DorminException{
	//Interprets the String val_rep as the appropriate string form, and pushes the
	//right values onto the stack

//trace.out("in MO addStringForm "+val_name + " of type " + val_type +" and value " + val_rep);
	if(val_name.equals("VERB")){
			myVerb = val_rep;
		} 
	else {
		try{
		switch(val_type){
				case 'F':	Float tfloat = Float.valueOf(val_rep);
							addParameter(val_name,tfloat.floatValue());
						 	break;
				case 'D':	Double tdouble = Double.valueOf(val_rep);
							addParameter(val_name,tdouble.doubleValue());
						 	break;
				case 'O':	addParameterToVectors(val_name, val_rep, "O");
							break;
				case 'U':	addUnknownParameter(val_name,val_rep);
							break;
				case 'L':	Vector tVec = parseList(val_rep);
							addParameter(val_name,tVec);
							break;
				case 'B':	if(val_rep.equals("T"))
								 addParameter(val_name,true);
							else 
								addParameter(val_name,false);
							break;
				case 'R':	//Range tRange = parseRange(val_rep);
							//addParameter(val_name,tRange);
							addParameterToVectors(val_name, val_rep, "R");		
							break;
				case 'I':	Integer tInt = Integer.valueOf(val_rep);
							addParameter(val_name,tInt.intValue());
							break;
				case 'S':	addParameter(val_name,val_rep);
							break;	
			}	
		}catch (DorminException e){
			throw e;
		}
		}
	}
	
	private Object parseObject(char val_type, String val_rep) throws DorminException{
		Object objValue = null;
//trace.out("in parseObject val_type = "+val_type+" val_rep = *"+val_rep+"*"+" len = "+val_rep.length());
		try{
		switch(val_type){
			case 'F':	Float tfloat = Float.valueOf(val_rep);
						objValue = tfloat;
						 break;
			case 'D':	Double tdouble = Double.valueOf(val_rep);
						objValue = tdouble;
						break;
			case 'O':
			case 'U':
			case 'S':	if(val_rep.length() == 0)
							objValue = "";
						else
							objValue = val_rep;
						break;
			case 'L':	Vector tVec = parseList(val_rep);
						objValue = tVec;
						break;
			case 'B':	Boolean bool = Boolean.valueOf(val_rep);
						objValue = bool;
						break;
//			case 'R':	Range tRange = parseRange(val_rep);
//						objValue = (Object)tRange;	
//						break;
			case 'I':	Integer tInt = Integer.valueOf(val_rep);
						objValue = tInt;
						break;
			default:		
		}
		} catch(DorminException e){
			throw e;
		}
		return objValue;
	}

	public Vector parseList(String inList) throws DorminException{
	// Example: LIST=L:3:[I:2:44,O:App,POSITION,1,Worksheet,NAME,foobar,S:5:hello]
//trace.out("in parlseList inList = "+inList);
		boolean listAdded = false;
		if(inList.startsWith("L:"))
			inList = inList.substring(2);
		Vector returnee = new Vector();
		int colonPos = inList.indexOf(":");
		int listSize = Integer.parseInt(inList.substring(0, colonPos));
		int currSize = 0;
		String listString = inList.substring(colonPos+2,inList.length()-1);
//trace.out("in parlseList listString = "+listString);
		int maxPos = listString.length();
		int i=0,colon=0;
		int nextcolon = 0;
		String value;
		String strType;
		char charType;
		try{
			while(i<maxPos){
				listAdded = false;
				colon = listString.indexOf(":",i);
				strType = listString.substring(i,colon);
				nextcolon = listString.indexOf(":",colon+1);
				charType = strType.charAt(0);
				switch(charType) {
					case 'L':	int lastBracket = getClosingBracket(listString.substring(colon),0);
								String newListStr = listString.substring(i,(lastBracket+1+colon));
								Vector newList = parseList(newListStr);
								returnee.addElement(newList);
								currSize++;
								if(currSize == listSize)
									return returnee;
								nextcolon = newListStr.length()+2;
								listAdded = true;
								break;
					case 'F':
					case 'D':
					case 'B':
					case 'I':
					case 'U':
					case 'S': 	
								String sLength = listString.substring(colon+1, nextcolon);
								int intLength = 0;
								try{
									intLength = Integer.parseInt(sLength);
								} catch (NumberFormatException e) {
									throw new DataFormatException("Can't parseList: String length "+ sLength+" is not a number for "+listString);
								}
								colon = nextcolon;
								if(intLength == 0)
									nextcolon = listString.indexOf(":",colon+intLength+1);
								else
									nextcolon = listString.indexOf(":",colon+intLength);
								int realnextcolon = nextcolon-2;
								if(realnextcolon <0)
									realnextcolon = maxPos;
								if(intLength != (realnextcolon-colon-1)) {
									String ext = listString.substring(colon+1, realnextcolon);
									throw new DataFormatException("Bad length of \""+ext+"\" in a List. Actual length = "+(realnextcolon-colon-1)+" specified lenght = "+intLength);
								}	
								
								break;
					case 'O':	nextcolon = findNextObjInList(listString, (colon+1), charType)+2;
								break;
					case 'R':	nextcolon = findNextObjInList(listString, (colon+1), charType)+1;
								break;
				}
				nextcolon = nextcolon - 2; // Don't include ",S"
				if(nextcolon < 0)
					nextcolon = maxPos;
				if(!listAdded){
					value = listString.substring(colon+1,nextcolon);
					Object objValue = parseObject(charType,value);
					currSize++; 
					returnee.addElement(objValue);
					if(currSize == listSize)
						return returnee;
				}
		else {
		nextcolon = nextcolon+i;
		colon = nextcolon;
		}
				i = nextcolon+1; // Don't include comma
			}
		} catch (StringIndexOutOfBoundsException a){
			throw new DataFormatException("Can't parseList "+inList);
		}
		return returnee;
	}
	
	// List of Objects can contain only types 'O' and 'R'
	private int findNextObjInList(String objStr, int st, char objType) {
		switch (objType){
			case 'R': 	st = st+2;
						int toret = objStr.indexOf("]]", st)+3;
						return toret;
		}
		
		int toret = objStr.indexOf("R:", st);
		if(toret != -1) {
			return toret;
		}
		toret = objStr.indexOf("O:", st);		
		return toret;
		
	}
	
	private int getClosingBracket(String str,int endPos){
		int toret = str.indexOf("]", endPos);
		String subStr = str.substring(0,toret);
		StringTokenizer st = new StringTokenizer(subStr, "[");
		int numOfTokens = st.countTokens();
		if(numOfTokens > 2){
			for(int i=0; i<(numOfTokens-2); i++) 
				toret = str.indexOf("]", toret+1);
		}
		return toret;
	}

	private ObjectSpecifier parseSpec(String inSpec){
		ObjectSpecifier returnee = new ObjectSpecifier();
		String value,type,len,parent;
		int i=0;
		inSpec = inSpec+":";//And if I explained my dark reasons for doing this, it would take
						   //all day.
		int colon = inSpec.indexOf(":");

		if(colon > 0){
			i = colon+1;
			value = inSpec.substring(0,colon);
			returnee = new ObjectSpecifier(value);
	//		//trace.out(value);
			try{
				while(i<(inSpec.length()-1)){
					int typecolon = inSpec.indexOf(":",i+1);
					int lencolon = inSpec.indexOf(":",typecolon+1);
					int parcolon = inSpec.indexOf(":",lencolon+1);
					type = inSpec.substring(i,typecolon);
					len = inSpec.substring(typecolon+1,lencolon);
					parent = inSpec.substring(lencolon+1,parcolon);
					returnee.contain(parent,len);
					i = parcolon+1;
				}
			} catch (StringIndexOutOfBoundsException a){};
		}
		else returnee = new ObjectSpecifier(inSpec);	
	//	//trace.out(returnee.toString());	
		return returnee;
	}
	
/*	private Range parseRange(String inRange) throws DorminException{
		Range returnee = null;
		if(inRange.endsWith("]]"))
			inRange = inRange.substring(0, inRange.length()-1);
		try{
			int curPos = inRange.indexOf("O:");
			int nextPos = inRange.indexOf(",S:");
			String parentDesc = inRange.substring(curPos+2, nextPos);
			curPos = nextPos;
			nextPos = inRange.indexOf(",L:");
			String rangeType = inRange.substring(curPos+5, nextPos);
			Vector startEndPairs = (Vector)parseObject('L',inRange.substring(nextPos+1));
			returnee = new Range(parentDesc, rangeType,startEndPairs);
		} catch (ArrayIndexOutOfBoundsException e){
			throw new DataFormatException("MessageObject parseRange: can't parse range :"+inRange+" "+ e);
		}
		catch (DorminException ex){
			throw ex;
		}
		return returnee;	
	}
*/	
	public synchronized void addParameterToVectors(String ParameterName, Object Value,String Type){
		//int index = ParameterSearch(ParameterName);
		ParameterName = ParameterName.toUpperCase();
		int index = Names.indexOf(ParameterName);
		if(index>=0){
			try {
				Names.setElementAt(ParameterName,index);
				Types.setElementAt(Type,index);	
				char charType = Type.charAt(0);	
				switch(charType){
					case 'L':
						Object curV = Values.elementAt(index);
						if(curV instanceof Vector){
							((Vector)curV).addElement(Value);
							break;
						}
					default:
						Values.setElementAt(Value,index);
						break;
				}
						
			} catch (ArrayIndexOutOfBoundsException e){
			//	//trace.out("Search Error encountered in MessageOBject:addParameterToVectors");
			};
		}
		else {
			Names.addElement(ParameterName);
			Values.addElement(Value);
			Types.addElement(Type);
		}
	}
	
	public void resetParameter(String ParameterName, Object Value, String Type){
		int ind = ParameterSearch(ParameterName);
		if (ind == -1) return;
		Names.removeElementAt(ind);
		Values.removeElementAt(ind);
		Types.removeElementAt(ind);
		addParameterToVectors(ParameterName, Value, Type);
		Initializer = null;
	}	
	
	private int ParameterSearch(String ParameterName) {
	/*The ParameterSearch method goes through the available parameters in a linear search,
	 *looking for a Parameter whose name matches ParameterName.  Finiding this parameter, it returns the
	 *index as int.  Failing to find it, it returns -1
	 */
	 	int i=0;
		try{
			 for(i=0;i<=Names.size();i++){
	 			String t_str = (String) Names.elementAt(i);
	 			if(t_str.equals(ParameterName.toUpperCase())) break;
			}
		}
	 	catch (ArrayIndexOutOfBoundsException a) {
	 		i = -1;
	 	}
		return i;
	}	
	
	private int getIntVal(int Index){
	/*The various get*Val method sassume that we have been guaranteed the datatype*/
		Integer temp = Integer.valueOf("0");
		try{
			temp=(Integer) Values.elementAt(Index);
		} catch (ArrayIndexOutOfBoundsException a){};
		return temp.intValue();
	}
			 
	private float getFloatVal(int Index){
		Float temp = Float.valueOf("0.0");
		try{
			temp = (Float) Values.elementAt(Index);
		} catch(ArrayIndexOutOfBoundsException a){};
		return temp.floatValue();
	}
	
	private double getDoubleVal(int Index){
		Double temp = Double.valueOf("0.0");
		try{
			temp = (Double) Values.elementAt(Index);
		} catch(ArrayIndexOutOfBoundsException a){};
		return temp.doubleValue();
	}
	
	private boolean getBoolVal(int Index){
		Boolean temp = Boolean.valueOf("false");
		try {
			temp = (Boolean) Values.elementAt(Index);
		} catch (ArrayIndexOutOfBoundsException a){};
		return temp.booleanValue();
	}
	
	private String getStrVal(int Index){
		String temp=new String();
		try {
			Object obj = Values.elementAt(Index);
			if (obj instanceof String)
				temp =(String) obj;
			else if (obj == null)
				temp = "null";
			else
				temp = obj.toString();
		} catch (ArrayIndexOutOfBoundsException a){};
		return temp;
	}
	
	private char getCharVal(int Index){
		Character temp = new Character('A');
		try {
			temp = (Character) Values.elementAt(Index);
		} catch (ArrayIndexOutOfBoundsException a){};
		return temp.charValue();
	}
	
	private Vector getListVal(int Index){
		Vector temp=new Vector();
		try {
			temp =(Vector) Values.elementAt(Index);
		} catch (ArrayIndexOutOfBoundsException a){
			//trace.out("in DORMIN.MessageObject can't getListValue "+a.toString());
		}
		return temp;
	}

	// new type Object is extracted as a String
	private Vector getSpecVal(int Index){
		return getListVal(Index);
	}	
		
	private Range getRangeVal(int Index) throws DorminException{
		Range temp = null;
		String tempStr="";
		try{
			Object tempObj = Values.elementAt(Index);
			if(tempObj instanceof Range)
				return (Range)tempObj;
			else {
				throw new NoSuchObjectException("Object '"+tempObj+"' doesn't exist");
			}
		} catch (DorminException a){
			throw a;
		}
	}	


	private String StrFromType(char value){
		String returnee;
		switch(value){
			case 'F':returnee = new String(" floating-point number");break;
			case 'D':returnee = new String(" double-point number");break;
			case 'O':returnee = new String("n ObjectSpecifier object");break;
			case 'U':returnee = new String("n Unknown Object");break;
			case 'L':returnee = new String(" List");break;
			case 'B':returnee = new String(" Boolean");break;
			case 'R':returnee = new String(" Range");break;
			case 'I':returnee = new String("n Integer");break;
			case 'S':returnee = new String(" String");break;
			default:returnee = new String(" Flaming Bucket O'Cheeze Whiz(tm)");
		}
		return returnee;
	}
	
	/**
	 * Edit a property name in this message.
	 * @param name property name; no-op if null or empty
	 * @param value property's new value; empty strin
	 * @param add whether to add the property if it's not found
	 */
	public void changePropertyValue(String name, Object value, boolean add) {
		if (name == null || name.length() < 1)
			return;
		Vector pNames = null;
		Vector pValues = null;
		try {
			pNames = (Vector) getParameter("PROPERTYNAMES");
			if (pNames == null && !add)
				return;
		} catch (MissingParameterException mpe) {
			if (!add)
				return;
			pNames = new Vector();
			addParameter("PROPERTYNAMES", pNames);
		}
		try {
			pValues = (Vector) getParameter("PROPERTYVALUES");
			if (pValues == null && !add)
				return;
		} catch (MissingParameterException mpe) {
			if (!add)
				return;
			pValues = new Vector();
			addParameter("PROPERTYVALUES", pNames);
		}
		int i;
		for (i = 0; i < pNames.size(); ++i)
			if (name.equalsIgnoreCase((String) pNames.get(i)))
				break;
		if (i >= pNames.size()) {
			if (!add)
				return;
			pNames.add(name);
			pValues.add(value);
		} else {
			pValues.set(i, value);
		}
	}

	/**
	 * @return the {@link #convertInstructions}
	 */
	public String getConvertInstructions() {
		return convertInstructions;
	}
	/**
	 * @param convertInstructions new value for {@link #convertInstructions}
	 */
	public void setConvertInstructions(String convertInstructions) {
		this.convertInstructions = convertInstructions;
	}

	/**
	 * Extract the desired value from propertyValues and return it
	 * 
	 * @param propertyNames
	 *            Property name vector from Dormin message
	 * @param propertyValues
	 *            Property value vector from Dormin message
	 * @param propertyName
	 *            The property name of the value being sought
	 * 
	 * The property value requested, or null if not found
	 */
	static public Object getValue(Vector propertyNames, Vector propertyValues,
	        String propertyName) {
	    int pos = fieldPosition(propertyNames, propertyName);
	
	    if (pos != -1)
	        return propertyValues.elementAt(pos);
	
	    return null;
	}

	/**
	 * Extracts a field position from a dormin message vector
	 */
	public static int fieldPosition(Vector from, String fieldName) {
	    int toret = -1;
	    int s = from.size();
	    for (int i = 0; i < s; i++) {
	        Object o = from.elementAt(i);
	        if (((String) o).equalsIgnoreCase(fieldName))
	            return i;
	    }
	
	    return toret;
	}
	
	/**
	 * @param b new value for {@link #doNotLog}
	 */
	public void suppressLogging(boolean b) {
		this.doNotLog = b;
	}
	
	/**
	 * @return {@link #doNotLog}
	 */
	public boolean isLoggingSuppressed() {
		return doNotLog;
	}
}
