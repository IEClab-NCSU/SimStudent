package edu.cmu.old_pact.dormin;

public class ExternalObject{
	//Properties
	protected String Type,Name;	
	protected int Position;
	protected String UniqueID;
	protected ExternalObject Parent;
	

	public ExternalObject(String type,ExternalObject parent,String name,int position,String uniqueID){
		Parent = parent;
		Type =type;
		Name = name;
		Position = position;
		UniqueID = uniqueID;
	}
	
	public ExternalObject(){
		Type = "OBJECT";
		Name = null;
		Parent = null;
		Position = 1;
		UniqueID = null;
	}
	/*
	public ExternalObject(String inType){
		Type = inType;
		Name = null;
		Parent = null;
		Position = 1;
		UniqueID = null;
	}
	*/
	public ExternalObject(String inName){
		Type = "OBJECT";
		Name = inName;
		Parent = null;
		Position = 1;
		UniqueID = null;
	}

	public String getName(){
		return Name;
	}

	public int getPosition(){ 
		return Position;
	}
	
	public String getUniqueID(){
		return UniqueID;
	}
	
	public String getType(){
		return Type;
	}
	
	public void handleEvent(MessageObject inEvent) {
		handleMessage(inEvent);
	}
	
	public void handleMessage(MessageObject inEvent) {
	}
	
	public void handleEvent(String inStr) {
		handleMessage(inStr);
	}
	
	public void handleMessage(String inStr) {
	}
	
	public void handleSet(MessageObject inEvent) throws NoSuchPropertyException{
		throw new NoSuchPropertyException("That Property doesn't exist");
	}

	public void handleGet(MessageObject inEvent) throws NoSuchPropertyException{
		throw new NoSuchPropertyException("That Property doesn't exist");
	}
	public void handleCreate(MessageObject inEvent) throws NoSuchPropertyException{
		throw new NoSuchPropertyException("That Property doesn't exist");
	}
	public void handleDelete(MessageObject inEvent) throws NoSuchPropertyException{
		throw new NoSuchPropertyException("That Property doesn't exist");
	}
	public void handleInsert(MessageObject inEvent) throws NoSuchPropertyException{
		throw new NoSuchPropertyException("That Property doesn't exist");
	}

	public ExternalObject Resolve(ObjectSpecifier toFind){	
		return new ExternalObject("FOO");
	}
	
	public ExternalObject getContainedObjectByID(String ModelType,int ID){
		return new ExternalObject("FOO");
	}
	
	public ExternalObject getContainedObjectByName(String ModelType,String name){
		return new ExternalObject("FOO");
	}
}