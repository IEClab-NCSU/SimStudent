package edu.cmu.old_pact.dormin;

public class NoSuchObjectException extends DorminException{
	public NoSuchObjectException(){super();};
	public NoSuchObjectException(String s){super(s);};
		
	public synchronized static String getObjectDesc(MessageObject mo){
		String objStr = "Object can't be read :"+mo.toString();
		try{
			objStr = mo.extractObjectValue("OBJECT");
		}catch (DorminException exc) { }
		return "Object '"+objStr+"' doesn't exist";
	}	
}