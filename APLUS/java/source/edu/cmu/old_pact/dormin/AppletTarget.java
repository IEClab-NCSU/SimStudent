package edu.cmu.old_pact.dormin;
import java.util.Vector;

public class AppletTarget extends Target {
	String myName;
	public static Vector targetList;
	public static Vector targetNames;
	/*This class is, to put it mildly, something of a cheat*/
	
	public AppletTarget(String targetName){
		myName = targetName;
	}
	
	public AppletTarget(String TargetName,ExternalObject representation) {
	//Add a collision check on the addElements
		try{
			int i = targetNames.indexOf(TargetName);
			if(i==-1) {
				targetList.addElement(representation);
				targetNames.addElement(TargetName);	
			}
		} catch (NullPointerException n) {
			targetList = new Vector();
			targetNames = new Vector();
			targetList.addElement(representation);
			targetNames.addElement(TargetName);
		}
		myName = TargetName;
	}
	
	public void transmitEvent(MessageObject inEvent){
		try {
			int i = targetNames.indexOf(myName);
			ExternalObject temp =(ExternalObject) targetList.elementAt(i);
			temp.handleEvent(inEvent);
		} catch (ArrayIndexOutOfBoundsException a){}
	}
}