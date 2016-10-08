package edu.cmu.old_pact.dormin;

/**
* TutorProxy supports messages, designed for tutors.
**/


public abstract class TutorProxy extends CommonObjectProxy {
	
	public TutorProxy(){
		super();
	} 
	
	public TutorProxy(	String type, 
						String name, 
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	public TutorProxy(	String type, 
						ObjectProxy parent, 
						String id) {				
		this(type, null, parent, id, -9999);
	}
	
	public TutorProxy(	String type, 
						ObjectProxy parent,
						int position) {			
		this(type, null, parent, null, position);
	}
	
	public TutorProxy(	ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	public TutorProxy(	String type){
		this(type, null, null, null, -9999);
	}
	
	public TutorProxy(	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super(type, name, parent, id, position);
	} 

	public void treatMessage(MessageObject mo, String inVerb) throws DorminException{
		inVerb = inVerb.toUpperCase();
		boolean completed = false;
//System.out.println("in TutorProxy mo = "+mo.toString());		
		if(inVerb.equalsIgnoreCase("NOTECREATION")){
			noteCreation(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("NOTEDELETE")){
			noteDeletion(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("ACTIONREQUEST")){
			actionRequest(mo);
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
		else if(inVerb.equalsIgnoreCase("NOTELOGIN")){
			noteLogin(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("GETHINT")){
			getHint(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("GETNEXTSTEP")){
			getNextStep(mo);
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
		else if(inVerb.equalsIgnoreCase("DONE")) {
			noteDone(mo);
			completed = true;
		}
		else 
			super.treatMessage(mo, inVerb);
	}
		
	public  void noteCreation(MessageObject mo){ }
	public  void noteDeletion(MessageObject mo){ }
	public  void notePropertySet(MessageObject mo){ }
	public  void noteInsert(MessageObject mo){ }
	public  void noteRemove(MessageObject mo){ }
	public  void noteOpen(MessageObject mo){ }
	public  void notePrint(MessageObject mo){ }
	public  void noteQuit(MessageObject mo){ }
	public  void noteSave(MessageObject mo){ }
	public  void noteLogin(MessageObject mo) { }
	public  void getHint(MessageObject mo){ }
	public  void startProblem(MessageObject mo) { }
	public  void getNextStep(MessageObject mo){ }
	public  void updateAssessment(MessageObject mo){ }
	public  void actionRequest(MessageObject mo) { }
	public  void noteDone(MessageObject mo) { }
	
}