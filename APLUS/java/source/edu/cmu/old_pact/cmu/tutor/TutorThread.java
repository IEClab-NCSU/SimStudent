package edu.cmu.old_pact.cmu.tutor;

public class TutorThread extends Thread {
	String selection;
	String action;
	String input;
	Tutor tutor;
	int currentEvent;
	
	TutorThread(Tutor tut,int eventNum,String sel,String act,String inp) {
		super();
		tutor = tut;
		selection = sel;
		action = act;
		input = inp;
		currentEvent = eventNum;
	}
	
	public void run() {
		//trace.out("in threadRUN: "+selection+" "+action+" "+input);
		tutor.checkStudentAction(selection,action,input);
		tutor.getTranslator().responseCompleted(currentEvent);
	}
}
