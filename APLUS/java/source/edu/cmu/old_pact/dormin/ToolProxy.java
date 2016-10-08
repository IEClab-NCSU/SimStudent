package edu.cmu.old_pact.dormin;

import edu.cmu.pact.Utilities.trace;

/**
* ToolProxy supports messages, designed for tools.
**/


public abstract class ToolProxy extends CommonObjectProxy {

	public ToolProxy(){
		super();
	}
	
	public ToolProxy(	String type, 
						String name, 
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	public ToolProxy(	String type, 
						ObjectProxy parent, 
						String id) {				
		this(type, null, parent, id, -9999);
	}
	
	public ToolProxy(	String type, 
						ObjectProxy parent,
						int position) {			
		this(type, null, parent, null, position);
	}
	
	public ToolProxy(	ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	public ToolProxy(String type){
		this(type, null, null, null, -9999);
	}
	
	public ToolProxy(	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super(type, name, parent, id, position);
	} 

	public void treatMessage(MessageObject mo, String inVerb) throws DorminException{
		inVerb = inVerb.toUpperCase();
		trace.out(10, this, "top of treatMessage (ToolProxy): "+inVerb);
		boolean completed = false;
		
		if(inVerb.equalsIgnoreCase("CREATE")){
			try{
				trace.out (10, this, "CREATING " + mo);
				create(mo);
				completed = true;
				trace.out (10, this, "done creating");
			} catch (DorminException e) {
				throw e;
			}
		}
		else if(inVerb.equalsIgnoreCase("DELETE")){
			trace.out (10, "ToolProxy", "DELETING");
			delete(mo);
			completed = true;
			trace.out (10, "ToolProxy", "done");
		}
		else if(inVerb.equalsIgnoreCase("INSERT")){
			insert(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("REMOVE")){
			remove(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("OPEN")){
			open(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("PRINT")){
			print(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("QUIT")){
			quit(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("SAVE")){
			save(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("UNDO")){
			unDo(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("POINTTO")){
			pointTo(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("SELECT")){
			select(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("STARTACTIVITY")){
			startActivity(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NEXTSTEP")){
			nextStep(mo);
			completed = true;
		}
                //SMILLER Reset message
                else if(inVerb.equalsIgnoreCase("RESET"))
                {
                    reset(mo);
                    completed = true;
                }
		else {
//			System.out.println("about to call super.treatMessage");
			super.treatMessage(mo, inVerb);
		}
	}

	public  void create(MessageObject mo) throws DorminException{ 
		try{
			trace.out (10, this, "creating in toolproxy");
			String childType = mo.extractStrValue("OBJECTTYPE");
			throw new NoSuchObjectException(childType);
		} catch (MissingParameterException e) { 
			throw e;
		}
	}
	
	public  void delete(MessageObject mo){ 
		deleteProxy();
	}
	
	
	public  void insert(MessageObject mo){ };
	public  void remove(MessageObject mo){ };
	public  void open(MessageObject mo){ };
	public  void print(MessageObject mo){ };
	public  void quit(MessageObject mo){ trace.out (5, "ToolProxy", "quit now");};
	public  void save(MessageObject mo){ };
	public  void unDo(MessageObject mo){ };
	public  void pointTo(MessageObject mo){ };
	public  void select(MessageObject mo){ };
	public  void startActivity(MessageObject mo){ };
	public  void nextStep(MessageObject mo){ };
    public void reset(MessageObject mo){};
}

