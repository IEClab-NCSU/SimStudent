package edu.cmu.pact.ctat;

/**
* ObjectProxy is a class which reflects object hierarchy. Can be used for  
* virtual object hierarchy or include a pointer to a real object if the object 
* inplements Sharable interface. This class can be used to provide an access to 
* real object by it's description, and to create a description of an object 
* to send message over network;
*
*	Properties :
* 		TYPE         	* must provide
*		NAME			*
*		POSITION	>0	* provide at least one of these {NAME, POSITION, UNIQUEID}
*		UNIQUEID		*
*		Default		-   POSITION is a default descriptor, which is set automatically, 
*						but user can change it
*	
*			User can describe this object using any format {NAME, POSITION, UNIQUEID}
*			The object itself send it's description using "Defaul" stuff
* 
*			
* Example of user's code:
*
* CREATE an object hierarchy for real objects and implement ObjectProxy for it.
* Create an ExampleProxy as a subclass of an abstract ToolProxy class (a subclass of an ObjectProxy).
*
* 	class ExampleProxy extends ToolProxy{
*		public ExampleProxy(String type, String name, ObjectProxy parent){
*			super(type, name, parent);
*		}
* 	}
*
* Create real object hierarchy:
*
* 	class Top implements Sharable{
*		ExampleProxy topProxy; 				
*		Target target;
*		public Top() {
*			topProxy = new ExampleProxy("Top", "TopName", null);	 
*			target = new Target(topProxy);  // initialize target.	
*			topProxy.setTarget(target);		// attach a target in ObjectProxy only for top object;	
*			target.setRealObject(this);		// connect ObjectProxy with real object
*			Child child = new Child(this);	// for example only
*		}
* 	}
* 	class Child implements Sharable{
* 		ExampleProxy childProxy;
*		public Child(Top top) {
*			childProxy = new ExampleProxy("Child", "ChildName", top.getObjectProxy());
*			childProxy.setRealObject(this); 
*		}
*
*	}
*
* SEND a message over network from Child class:
*		MessageObject mo = new MessageObject("EXAMPLEMESSAGE");
*		mo.addParameter("VALUE", "Just test");
*		mo.addParameter("OBJECT",childProxy); 
*		childProxy.send(mo);		
* }
*
**/

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;


public  class ObjectProxy extends ObjectSpecifier {
	Vector parents;
	Object realObject = null;
	protected Hashtable Properties;
	public ProxyHashtable Children; // accessible for any other package
	MultiTarget reply_targets;
	public static int objectID;
	public static ObjectProxy topObjectProxy;
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(){ 
		Properties = new Hashtable();
		parents = new Vector();
		Children = new ProxyHashtable();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(String type, 
						String name,
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(String type, 
						ObjectProxy parent, 
						String id) {	
		this(type, null, parent, id, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(String type, 
						ObjectProxy parent,
						int position) {	
		this(type, null, parent, null, position);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(String type){
		this(type, null, null, null, -9999);
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy(String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) { 
		this();
//		trace.err("ObjectProxy constructor: type = " + type + " name = " + name + " parent = " + parent
//				+ " id = " + id + " position = " + position);
		init( type, name, parent,id, position);
		/*
		super(type);									
		Properties = new Hashtable();
		Properties.put("TYPE", type);
		///parents = new Vector();
		setContainer(parent);
		Children = new ProxyHashtable();
		
		String def = "";
		if(name != null)
			def = "NAME";
		else if(name == null)
			name = type+"0";
		Properties.put("NAME", name); 
		
		if(position != -9999) {
			Properties.put("POSITION", String.valueOf(position));
			def = "POSITION";
		}
		else if(position == -9999) 
			setPosition();
			
		if(id == null) {
			id = String.valueOf(objectID);
			objectID++;
		}
		Properties.put("UNIQUEID", id);
		if(def.equalsIgnoreCase(""))
			def = "POSITION";
		Properties.put("Default", def);
		if(parent == null)
			topObjectProxy = this;
		*/
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init( 	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super.init(type);
		Properties.put("TYPE", type);
		setContainer(parent);
		
		String def = "";
		if(name != null)
			def = "NAME";
		else if(name == null)
			name = type+"0";
		Properties.put("NAME", name); 
		
		if(position != -9999) {
			Properties.put("POSITION", String.valueOf(position));
			def = "POSITION";
		}
		else if(position == -9999) 
			setPosition();
			
		if(id == null) {
			id = String.valueOf(objectID);
			objectID++;
		}
		Properties.put("UNIQUEID", id);
		if(def.equalsIgnoreCase(""))
			def = "POSITION";
		Properties.put("Default", def);
		if(parent == null)
			topObjectProxy = this;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init(String type, String name, ObjectProxy parent) {
		init(type, name, parent, null, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init(String type, ObjectProxy parent, String id) {				
		init(type, null, parent, id, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init(String type, ObjectProxy parent, int position) {			
		init(type, null, parent, null, position);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init(ObjectProxy parent, String type){
		init(type, null, parent, null, -9999);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void init(String type){
		init(type, null, null, null, -9999);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**
		returns the current state of ObjectProxy tree

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getProxyTree(){
		return getTree("");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getTree(String toret){
		toret = "||"+toret+getStrDescription();	
		int s = this.Children.size();
		if(s > 0){
			Enumeration e = Children.keys();
			Vector el;
			while (e.hasMoreElements()){
				el = (Vector)Children.get(e.nextElement());
				int es = el.size();
				if(es > 0) {
					int k = 0;
					while(k < es){
						toret = toret+((ObjectProxy)el.elementAt(k)).getTree("")+"\n";
						k++;
					}
				}
			}
		}
		return toret;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void mailToProxy(MessageObject mo) throws CommException{ 
		mailToProxy(mo, null);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**
	
	MailToProxy does a couple of different things, depending on the "description" argument
	If that argument is an empty vector, then we ignore it and handle the message
	If the description talks about the target of the message, then we handle the message
	Otherwise, we try and construct the object referenced in the description
	 constructChildProxy calls mailToProxy, so this we're really both constructing the proxy and treating the message
	
	size == 0 - message has been arrived
	
	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void mailToProxy(MessageObject mo, Vector description) throws CommException{ 
	// description consists of type, 		format, 	format value 
	//						  "Worksheet", "POSITION", "3"
//		trace.out("top of mailToProxy (objectProxy)");
		int size = 0;
		if(description != null)
			size = description.size();
//		trace.out("Description is *"+description+"*"+description.size());
		if(size == 0) {
			try{
//				trace.out("about to call treatMessage");
				treatMessage(mo, mo.getVerb());
			} catch(CommException e) { 
				throw e;
			}
			return;
		}
		else if(((String)description.elementAt(0)).equalsIgnoreCase(this.type)){
			if(isThisProxy(description)){

				for(int i=0; i<3; i++)
					description.removeElementAt(0);
				this.mailToProxy(mo, description); //couldn't we just do treatMessage here???
			}
			else throw new NoSuchObjectException(NoSuchObjectException.getObjectDesc(mo));
		}
		else if(!((String)description.elementAt(0)).equalsIgnoreCase(this.type)){
			try{
				constructChildProxy(mo, description);
			} catch (NoSuchObjectException ex) {
				throw ex;
			}
		}
		else throw new NoSuchObjectException(NoSuchObjectException.getObjectDesc(mo));
	}
	
	// depricated for now: no senders or receivers are specified
/*
	public String getSenderName(MessageObject inEvent) {
		String toret = "";
		try{ 
			Vector senderDesc = inEvent.extractListValue("Sender");
			toret = (String)senderDesc.lastElement();
			return toret;
		} catch (CommException e) { }
		return toret;
	}	
*/	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void constructChildProxy(MessageObject mo, Vector description) throws CommException {
		Vector childDesc = new Vector(3);
		for(int i=0; i<3; i++) 
			childDesc.addElement((String)description.elementAt(i));
		// try to find child first
		ObjectProxy child = getContainedProxy(childDesc);
		if(child != null) {
			try{
				child.mailToProxy(mo, description);
			} catch (CommException ex){ 
				throw ex;
			}
		}
		else {
			try{
				constructNewChild(mo, description);
			} catch (CommException ex){ 
				throw ex;
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public  void treatMessage(MessageObject mo, String verb)throws CommException{
		trace.out("treatMessage called in ObjectProxy");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public  void constructNewChild(MessageObject mo, Vector description) throws NoSuchObjectException{ 
		throw new NoSuchObjectException(NoSuchObjectException.getObjectDesc(mo));
	}


	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setRealObject(Object realObject) {
		this.realObject = realObject;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	private void setContainer(ObjectProxy parent) {
		parents.addElement(parent);
		if(parent != null) {
			//trace.out (5, this, "setContainer: parent = " + parent);
			parent.contain(this);
			reply_targets = parent.getTarget();
		}
//		trace.err ("set container: parent = " + parent + " reply targets = " + reply_targets);
//		trace.printStack ("m");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy getContainer() {
		return (ObjectProxy)parents.elementAt(0);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public ObjectProxy getTopContainer() {
		ObjectProxy parent = getContainer();
		ObjectProxy root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getContainer();
		}
		return  root;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public boolean isInside(int pos, String type) {
		boolean inside = false;
		ObjectProxy parent = getContainer();
		if(parent != null){
			int childrenSize = parent.Children.size(type);
			if(pos < childrenSize)
				return true;
		}
		return inside;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void resetPosition(ObjectProxy ch_obj, int pos) {
		int movePos = 0, removePos = 0;
		boolean moveIt = false;
		int curPos = Children.indexOf(ch_obj);
		if(curPos < pos) {
			movePos = pos;
			removePos = curPos;
			moveIt = true;
		}
		else if(curPos > pos) {
			movePos = pos;
			removePos = curPos+1;
			moveIt = true;
		}
		if (moveIt) {
			Children.insertElementAt(ch_obj, movePos);
			ch_obj.Properties.put("POSITION", String.valueOf(movePos+1));
			Children.removeElementAt(ch_obj, removePos);
		}
	}
		

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setName(String name) {
		Properties.put("NAME", name);
		//Properties.put("Default","NAME");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	// this doesn't work properly
	public void setType(String t){
		getContainer().Children.removeElement(this);
	    type = t.toUpperCase();
	    Properties.put("TYPE",t);
	    getContainer().contain(this);
	}
	
	// used in constructor to set default position	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setPosition() {
		ObjectProxy parent = getContainer();
		if(parent != null) {
			int pos = parent.Children.indexOf(this) +1;
			Properties.put("POSITION", String.valueOf(pos));
		}
		else {
			Properties.put("POSITION", String.valueOf(1));
		}
		//trace.out (5, "ObjectProxy.java", "SET POSITION: pos = " + Properties.get ("POSITION"));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setPosition(int pos) {
		if(isInside(pos, this.type)){
			resetPosition(this, pos);
			Properties.put("POSITION", String.valueOf(pos));
			//Properties.put("Default","POSITION");
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setPosition(String posS) {
		try{
			int pos = Integer.parseInt(posS);
			setPosition(pos);
		}
		catch (NumberFormatException e) { }
	}
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setID(String id) {
		Properties.put("UNIQUEID", id);
		Properties.put("Default","UNIQUEID");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	// element at stPos is included
	// element at endPos is ALSO included
	public ObjectProxy[] getRange(int stPos, int endPos, String type) {
		if(isInside(stPos, type) && isInside(endPos, type)) {
			int siz = endPos - stPos+1;
			ObjectProxy[] toret = new ObjectProxy[siz];
			for(int i=0; i<siz; i++)
				toret[i] = (ObjectProxy)Children.elementAt(type, stPos+i);
			return toret;
		}
		else return null;
	}	
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void defaultNameDescription() {
		Properties.put("Default","NAME");
	}
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void defaultPosDescription() {
		Properties.put("Default","POSITION");
	}
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void defaultIDDescription(){
		Properties.put("Default","UNIQUEID");
	}

	//////////////////////////////////////////////////////////////////////////////////////
	/**
		getDefaultDescriptor() 
	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getDefaultDescriptor() {
		//trace.out (10, this, " getDefaultDescriptor properties = " + Properties);
		if (Properties == null) {
			trace.out (10, this, "ERROR: properties is null");
			return new String("");
		}
		if (Properties.isEmpty()) {
			trace.out (10, this, "ERROR: properties is empty");
			return new String ("");
		}
		
		String desc = (String)Properties.get("Default");
		if(desc == null){
			trace.out("NullPointer in ObjectProxy getDefaultDescriptor for "+type+" position = "+getPosition());
			Properties.put("DEFAULT", "POSITION");
			desc = "POSITION";
		}
		return desc;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getName() {
		return (String)Properties.get("NAME");
	}

	//////////////////////////////////////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public int getPosition() {
		String pos = (String)Properties.get("POSITION");
		//trace.out (5, this, "pos = " + pos);
		return Integer.parseInt(pos);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getID() {
		return (String)Properties.get("UNIQUEID");
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void setOwnProperty(String name, Object prop) {
		Properties.put(name, prop);
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public Object getOwnProperty(String name) {
		return Properties.get(name.toUpperCase());
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String getDefaultIdentificator(){
		String defDesc = (String)getOwnProperty("Default");
		if(defDesc != null && defDesc.equalsIgnoreCase("NAME"))
			return (String)getOwnProperty("NAME");
		else
			return type;
	}
		
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public Hashtable getOwnProperties() {
		return Properties;
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void contain(ObjectProxy con) {
		if(!Children.contains(con)) {
			//trace.out (5, this, "in method contain: con.type = " + con.type);
			Children.addElement(con);
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public String toString() {
		return getStrDescription();
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**
		Delete this proxy and withdraw the window
	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void deleteProxy() {

		//trace.out (10, "COMM.ObjectProxy", "deleteProxy()");
		synchronized (this) {
		  	// Withdraw (hide) this window
			if(realObject != null && realObject instanceof Sharable){
				try{
					((Sharable)realObject).setProperty("isVisible", Boolean.valueOf("false"));
				} catch (CommException e ) { }
				((Sharable)realObject).delete();
				realObject = null;
			}
	
			// Remove this proxy from the parent proxy
			ObjectProxy parent = getContainer();
			if(parent != null) {
				parent.Children.removeElement(this);
				parent.refreshChildrenPositions(type);
			}
	
			// Delete the children's proxies?
			int s = this.Children.size();
			if(s > 0){
				Enumeration e = Children.keys();
				Vector el;
				while (e.hasMoreElements()){
					el = (Vector)Children.get(e.nextElement());
					int es = el.size();
					if(es > 0) {
						int k = 0;
						while(k < es){
							((ObjectProxy)el.elementAt(0)).deleteProxy();
							k++;
						}
					}
				}
			}
			
			parents = null;
			Properties = null;
			reply_targets = null;
			deleteSpecifier();

	  	 }// end synchronized
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	protected void refreshChildrenPositions(String type){
		Vector childrenByType = (Vector)Children.get(type);
		int s = childrenByType.size();
		if(s > 0){
			for(int i=0; i<s; i++) 
			resetPosition((ObjectProxy)childrenByType.elementAt(i), i);
		}
	}	
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	protected void finalize() throws Throwable{
		this.Children = null;
		super.finalize();
	}


	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void addTarget(Target ot) {
		if(reply_targets == null)
			reply_targets = new MultiTarget("Targets");

		trace.out ("m", "****  add target: " + ot);
//        trace.printStack("m");
		reply_targets.addTarget(ot);
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public MultiTarget getTarget() {
		return reply_targets;
	}


	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	// now this method just a redirection
	public Object getObjectByName( Vector desc) {
		return getDescribedObject(desc);
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public Object getRealParent() {
		ObjectProxy obj_pro = getContainer();
		return obj_pro.getObject();
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public void  changeParent(String parentName){
		ObjectProxy obj_parent = getContainer();
		String parent_type = (String)obj_parent.getOwnProperty("TYPE");
		ObjectProxy parent_parent = obj_parent.getContainer();
		if(parent_parent != null){
			Vector chV = parent_parent.getChildrenByType(parent_type);
			int s = chV.size();
			if(s == 0) {
				obj_parent.setName(parentName);
				return;
			}
			ObjectProxy op;
			boolean found = false;
			for(int i=0; i<s; i++){
				op = (ObjectProxy)chV.elementAt(i);
				if(((String)op.getOwnProperty("NAME")).equalsIgnoreCase(parentName)){
					found = true;
					obj_parent.Children.removeElement(this);
					this.parents.removeElementAt(0);
					this.setContainer(op);
					this.setPosition();
					break;
				}
			}
			if(!found)	
				obj_parent.setName(parentName);
		}
	}
		
		
	

	//////////////////////////////////////////////////////////////////////////////////////
	/**

	 */
	//////////////////////////////////////////////////////////////////////////////////////
	public Object getDescribedObject(Vector description) {
		ObjectProxy top = getTopContainer();
		Vector topDesc = top.getDescription();
		for(int i=0; i<3; i++)
			description.setElementAt((String)topDesc.elementAt(i), i);
		ObjectProxy stop = getObjectProxyBy(description);
		if(stop != null) 
			return stop.getObject();
		else 
			return this.getObject();
	}
	

	public Object getDescribedObject(String description) {
		// I assume that delimeter is comma
		// type,format,value: "Cell,NAME,R1C1"
		Vector desc = new Vector();
		StringTokenizer st = new StringTokenizer(description, ",");
		while (st.hasMoreElements())
			desc.addElement(st.nextToken());
		
		return getDescribedObject(desc);
	}
	
	public Vector getChildrenByType(String typ) {
		Vector toret = null;
		try{
			Vector typeChildren = (Vector)Children.get(typ);
				return typeChildren;
		} catch (NullPointerException e) { 
			trace.out(e.toString());}
		return toret;
	}
	
	public ObjectProxy getChildByType(String typ){
		ObjectProxy toret = null;
		try{
			Vector allChildren = getChildrenByType(typ);
                        if((allChildren != null)&&(allChildren.size() != 0))
                            toret = (ObjectProxy)allChildren.elementAt(0);
		} catch (NullPointerException e) { }
		return toret;
	}
	
	public ObjectProxy getContainedProxyByType(String typ) {
		ObjectProxy toret = null;
		try{
			Vector typeChildren = (Vector)Children.get(typ);
			int chs = typeChildren.size();
			if(chs == 1) 
				return (ObjectProxy)typeChildren.elementAt(0);
		} catch (NullPointerException e) { 
			trace.out(e.toString());
		}
		return toret;
	}
	
	protected boolean isThisProxy(Vector description){
		boolean isThis = false;
		String format = ((String)description.elementAt(1)).toUpperCase();
		try{
			if(((String)Properties.get(format)).equalsIgnoreCase((String)description.elementAt(2)) &&
				((String)Properties.get("TYPE")).equalsIgnoreCase((String)description.elementAt(0))) {
					description.removeAllElements();
					description = null;
					return true;
			}
		}catch (NullPointerException e){
			description.removeAllElements();
			description = null;
			return isThis;
		}
		description.removeAllElements();
		description = null;
		return isThis;
	}
	
	public ObjectProxy getObjectProxyBy(Vector description) {
		// typeformat,value : "Cell,NAME,R1C1"
		ObjectProxy toret = null;
		boolean upperFound = false;
		try{
			int st = description.size()/3 - 1;
			String format = ((String)description.elementAt(1)).toUpperCase();
			if(((String)Properties.get(format)).equalsIgnoreCase((String)description.elementAt(2)) &&
				((String)Properties.get("TYPE")).equalsIgnoreCase((String)description.elementAt(0))) {
				upperFound = true;
				if(st == 0) 
					return this;
			}
			if(st > 0 && upperFound){ 
				description.removeElementAt(0);
				description.removeElementAt(0);
				description.removeElementAt(0);
				ObjectProxy temp = getContainedObjectBy(	(String)description.elementAt(0),
															(String)description.elementAt(1),
															(String)description.elementAt(2)); 	
				if(temp != null) 
					toret = temp.getObjectProxyBy(description);
				
				if(temp == null) 
					return this;
			}
		} catch (NullPointerException e) { 
			trace.out(e.toString());}
		return toret;
		
	}
	public ObjectProxy getContainedProxy(Vector desc) {
		return getContainedObjectBy((String)desc.elementAt(0), 
									(String)desc.elementAt(1),
									(String)desc.elementAt(2));
	}
	
	public ObjectProxy getContainedObjectBy(String type, String  format, String value) {
		ObjectProxy toret = null;
		try{
			Vector typeChildren = (Vector)Children.get(type);
			int chs = typeChildren.size();
			if(chs > 0) {
				for(int i=0; i<chs; i++) {
					if(((String)((ObjectProxy)(typeChildren.elementAt(i))).getOwnProperty(format)).equalsIgnoreCase(value) ) 
						return (ObjectProxy)typeChildren.elementAt(i);
				}
			}
		} catch (NullPointerException e) {
			toret=null; //make sure this is null -- NullPointerException might happen if child isn't found
		}
		return toret;
	}
	
	public Object getObject() {
		return realObject;
	}	
	
	private MessageObject constructMessage(MessageObject outEvent) {
		// depricated for now: no senders or receivers are spesified
	/*
		Vector description = getDescription();
		outEvent.addParameter("Sender", description);
		Vector objV;
		try{
			objV = outEvent.extractListValue("Receiver");
		} catch (CommException e) { 
			objV = new Vector(1);
			outEvent.addParameter("Receiver", objV);
		}
		return outEvent;
	*/
		return outEvent;
	}
	

	public synchronized void send(MessageObject outEvent) {
		outEvent = constructMessage(outEvent);
		outEvent.send(reply_targets);
	}
	
	public synchronized void send(MessageObject outEvent, String targetName) {
		outEvent = constructMessage(outEvent);
		Target sendTo = TargetRegistry.targets.getTarget(targetName);
		
		outEvent.send(sendTo);
	}

	// now this method just a redirection
	public Vector getDescriptionBy(String desc) {
		return getDescription();
	}
	
	public String getLowerObjectType(String desc){
		Vector vec = getDescriptionBy(desc);
		int s = vec.size();
		if(s < 3)
			return null;
		else
			return (String)vec.elementAt(s-3);
		}
	
	public Vector getDescription() {
		String description = getStrDescription();
		return getDescription(description);
	}
/*
	private String internalStrDescription(){
		String describeBy = getDefaultDescriptor();
		
		String toret = (String)Properties.get(describeBy)+",";
		toret = toret+describeBy+",";
		toret = toret+(String)Properties.get("TYPE")+",";
		try{
			ObjectProxy parent = (ObjectProxy)parents.elementAt(0);
			toret = toret + parent.internalStrDescription();
		}catch (NullPointerException e){ }
		return toret;
	}
*/	

	////////////////////////////////////////////////////////////////////////////////////
	/**	
		internalStrDescription
	 */
	////////////////////////////////////////////////////////////////////////////////////
	private String internalStrDescription(){
		//trace.out (10, this, "internalStrDescription");
		String describeBy = getDefaultDescriptor();
		StringBuffer toret = new StringBuffer();
		if(describeBy.equalsIgnoreCase("NAME"))
			toret.append("S:");
		else 
			toret.append("I:");
		toret.append(((String)Properties.get(describeBy)).length());
		toret.append(":");
		toret.append((String)Properties.get(describeBy));
		toret.append(",");
		toret.append("S:");
		toret.append(describeBy.length());
		toret.append(":");
		toret.append(describeBy);
		toret.append(",");
		toret.append("S:");
		toret.append(((String)Properties.get("TYPE")).length());
		toret.append(":");
		toret.append((String)Properties.get("TYPE"));
		toret.append(",");
		try{
			ObjectProxy parent = (ObjectProxy)parents.elementAt(0);
			toret.append(parent.internalStrDescription());
		}catch (NullPointerException e){
			//trace.out (10, this, "internalStrDescription: exception " + e);
		}
		//trace.out (10, this, "returning " + toret);
		return toret.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	/**	

	 */
	////////////////////////////////////////////////////////////////////////////////////
	public String reverseDescription(String description) {
		StringBuffer toret = new StringBuffer();
		Vector buff = getDescription(description);
		int s = buff.size();
		if(s == 0) return "";
		int buffSize = buff.size()-1;
		int i = buffSize;
		toret.append(s/3);
		toret.append(":");
		while( i>0 ) {
			toret.append((String)buff.elementAt(i));
			toret.append(",");
			i--;
		}
		toret.append(buff.elementAt(0));	
		return toret.toString();
	}	

	public Vector getDescription(String description) {
		Vector buff = new Vector();
		StringTokenizer st = new StringTokenizer(description, ",");
		int cou = st.countTokens();
		for(int i=0; i<cou; i++) {
			if(i != (cou-1))
				buff.addElement(st.nextToken());
			else{
			/**
			* if last object name contains "," put all reminded parts in one string
			**/
				String lastToken = st.nextToken();
				int rem = cou%3;
				if(rem == 1){
					String last = (String)buff.lastElement();
					buff.removeElementAt(i-1);
					lastToken = last+","+lastToken;
					
				}
				buff.addElement(lastToken);
			}
		}
		return buff;
	}
	/**
	* returns typeName,describeBy,identifier
	* Example: "Cell,NAME,R1C1"
	*/
	public String getLocalStrDesc(Vector v) {
		String toret = "";
		for(int i=0; i<2; i++)
			toret = toret+(String)v.elementAt(i)+",";
		toret = toret+(String)v.elementAt(2);
		return toret;
	}

	////////////////////////////////////////////////////////////////////////////////////
	/**	
	 an output:parent [type + describeBy + methodName] + type + describeBy + methodName
	 */
	////////////////////////////////////////////////////////////////////////////////////
	public String getStrDescription() {
		String toret = internalStrDescription();
		return reverseDescription(toret);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	/**	

	 */
	////////////////////////////////////////////////////////////////////////////////////
	public void setRealObjectProperties(Sharable realObj, MessageObject inEvent) throws CommException{
		Vector propertyNames,propertyValues;
//                try{
			propertyNames = (Vector) inEvent.getPropertyNames();
			propertyValues = (Vector) inEvent.getPropertyValues();
//		}catch (CommException e){
//			return;
//		}	
		try{
			int s = propertyNames.size();
			if(s == 0 ) return;
			if(s != propertyValues.size()){
				throw new DataFormatException("Not equal sizes of propertyNames : "+s+" and propertyValues : "+propertyValues.size()+"\n"+"propertyNames = "+propertyNames+"\n"+"propertyValues = "+propertyValues);
			}
			String currName;
			for(int i=0; i<s; i++) {
				currName = (String)propertyNames.elementAt(i);
				if(currName.equalsIgnoreCase("NAMINGPREFERENCE") ||currName.equalsIgnoreCase("NAMINGPREFERENCES"))
					Properties.put("Default",((String)propertyValues.elementAt(i)).toUpperCase());
				else{
					//try{
						if(realObj != null)
						{
							realObj.setProperty(currName, propertyValues.elementAt(i));
						}
						// this is wrong, some objects are only virtual and 
						// may be not attached to any real object!
						/*
						else
							trace.out(" NOOOOO! Some programmer forgot to call ObjectProxy.setRealObject() of "+this);
						*/
					//} catch (NoSuchFieldException es) {
						//throw new NoSuchPropertyException("Object '"+type+"' doesn't have property "+es.getMessage()+"'."); 
					//}
				}
			}
			
			if(realObj != null && realObj instanceof PaintSharable)
				((PaintSharable)realObj).repaintObject();
		}catch (CommException e) { 
			throw e;
		}
	}

}
