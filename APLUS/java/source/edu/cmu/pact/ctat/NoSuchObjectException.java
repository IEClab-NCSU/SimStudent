package edu.cmu.pact.ctat;

public class NoSuchObjectException extends CommException{
	public NoSuchObjectException(){super();};
	public NoSuchObjectException(String s){super(s);};
		
	public synchronized static String getObjectDesc(MessageObject mo){
		String objStr = "Object can't be read :"+mo.toString();
//		try{
			mo.getProperty("OBJECT");
//		}catch (CommException exc) { }
		return "Object '"+objStr+"' doesn't exist";
	}	
}
