package edu.cmu.old_pact.dormin;

/**
* TutorToolProxy supports messages, designed for tutors and tools.
**/


public abstract class TutorToolProxy extends ToolProxy {

	public TutorToolProxy(){
		super();
	}
	
	public TutorToolProxy(	String type, 
						String name, 
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	public TutorToolProxy(	String type, 
						ObjectProxy parent, 
						String id) {				
		this(type, null, parent, id, -9999);
	}
	
	public TutorToolProxy(	String type, 
						ObjectProxy parent,
						int position) {			
		this(type, null, parent, null, position);
	}
	
	public TutorToolProxy(	ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	public TutorToolProxy(	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super(type, name, parent, id, position);
	} 

	public void treatMessage(MessageObject mo, String inVerb) throws DorminException{
		inVerb = inVerb.toUpperCase();
		boolean completed = false;
		
		if(inVerb.equalsIgnoreCase("NOTECREATION")){
			noteCreation(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("ACTIONREQUEST")){
			actionRequest(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEDELETe")){
			noteDeletion(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEPROPERTYSET")){
			notePropertySet(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEINSERT")){
			noteInsert(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEREMOVE")){
			noteRemove(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEOPEN")){
			noteOpen(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEPRINT")){
			notePrint(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEQUIT")){
			noteQuit(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTESAVE")){
			noteSave(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("GETHINT")){
			getHint(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("GETNEXTSTEP")){
			nextStep(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("UPDATEASSESSMENT")){
			updateAssessment(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("STARTPROBLEM")) {
			startProblem(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTELOGIN")){
			noteLogin(mo);
			completed = true;
		}else if(inVerb.equalsIgnoreCase("DONE")) {
			noteDone(mo);
			completed = true;
		}
		else
			super.treatMessage(mo, inVerb);
	}

	// tutor messages
	public  void noteCreation(MessageObject mo){ }
	public  void noteDeletion(MessageObject mo){ }
	public  void notePropertySet(MessageObject mo){ }
	public  void noteInsert(MessageObject mo){ }
	public  void noteRemove(MessageObject mo){ }
	public  void noteOpen(MessageObject mo){ }
	public  void notePrint(MessageObject mo){ }
	public  void noteQuit(MessageObject mo){ }
	public  void noteSave(MessageObject mo){ }
	public  void getHint(MessageObject mo){ }
	public  void getNextStep(MessageObject mo){ }
	public  void updateAssessment(MessageObject mo){ }
	public  void startProblem(MessageObject mo) { }
	public  void actionRequest(MessageObject mo) { }
	public  void noteLogin(MessageObject mo) { }
	public  void noteDone(MessageObject mo) { }
	
}