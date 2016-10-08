//translator proxy for SolverTutor (and other tutors?)
package edu.cmu.old_pact.cmu.tutor;

import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cl.tutors.skillometer.SkillsManager;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.cmu.messageInterface.UserMessageWindow;
import edu.cmu.old_pact.cmu.solver.ruleset.RuleMatchInfo;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;

public class TranslatorProxy extends Object {
    // needs to be in a file imported by TRESolverTutor
    static String UPDATE_SKILL = "UpdateSkill";
    // needs to be in a file to be accessed by SolverFrame
    static String SEND_SKILLS = "SendSkills";

	SkillsManager theSkills;
	Tutor tut;
	TutoredTool tool;
	//HelpBugWin help;
	UserMessageWindow help;
	int eventNum = 0;
	Hashtable outstandingEvents = new Hashtable(10);
	private boolean showSkills = false;
	private boolean standalone = false;
	
	public TranslatorProxy() {
		tut = null;
		theSkills = null;
		tool = null;
	}

	public TranslatorProxy(TutoredTool theTool,Tutor theTutor) {
		tut = theTutor;
		theSkills = new SkillsManager();
		tool = theTool;
	}
	
	public void addTutor(Tutor tutor) {
		tut = tutor;
	}
	
	public void addTool(TutoredTool theTool) {
		tool = theTool;
	}

	public void startProblem(String problem) {
		setProblem(problem);
	}
	
	public void setProblem(String problem) {
		if (tut != null)
			tut.startProblem(problem);
	}

	public void recordStep(String selection,String action,String input) {
		RuleMatchInfo tempRuleMatchInfo = null;
		if (tut != null) {
			outstandingEvents.put(Integer.valueOf(String.valueOf(eventNum)),
								  new String[]{selection,action,input});
			if (action.equalsIgnoreCase("typein")) {
				tempRuleMatchInfo = runTutor(selection, action, input);
			}
			responseCompleted(eventNum);
			eventNum++;
		}
	}
	
	public RuleMatchInfo runTutor(String selection,String action,String input) {
		RuleMatchInfo tempRuleMatchInfo = null;
		tempRuleMatchInfo = tut.checkStudentAction(selection,action,input);
		return (tempRuleMatchInfo);			
	}
	
	public void startNextStep(String action) {
		tut.startNextStep(action);
	}
	
	public void setShowSkills(boolean s){
		showSkills = s;
		if(showSkills && theSkills == null)
			theSkills = new SkillsManager();
	}
	
	public void updateSkill(String skillname, String gradient, String currentEquation) throws DorminException {
		if(SolverFrame.debug()){
			System.out.println("in updateSkill for "+skillname);
		}
            if(((Boolean)tool.getProperty(SEND_SKILLS)).booleanValue())
			{
			    Vector skillValues = new Vector();
			 
			    skillValues.addElement(skillname);
			    skillValues.addElement(gradient);
			    
			    tool.setProperty(UPDATE_SKILL, skillValues);
			}
			else
			{
			    if (showSkills && theSkills != null) 
			        theSkills.updateSkill(skillname, gradient, currentEquation);
			        if(help != null && help.isVisible()) 
			            help.requestFocus();
			        else
			            tool.requestFocus();
			}

	}
	
	public void setReferenceProxy(ObjectProxy o){
		if(theSkills != null)
			theSkills.setReferenceProxy(o);
	}
	
	public void setRefTarget(String  t){
		if(theSkills != null)
			theSkills.setRefTarget(t);
	}

	public void setStandalone(boolean standaloneP){
		standalone = standaloneP;
	}

	public boolean getStandalone(){
		return standalone;
	}

	public void suggestNewProblem(){
		tool.suggestNewProblem();
	}
	
	public void displayCompletionMessage(){
		tool.displayCompletionMessage();
	}

	public void setFlag(String selection, boolean on) {
		if (tool != null) {
			if (on)
				tool.flag(selection);
			else
				tool.unflag(selection);
		}
	}
	
	public void showMessages(String selection,String[] messages,String title) {
		if(standalone){
			UserMessage[] msgs = new UserMessage[messages.length];
			for (int i=0;i<messages.length;++i){
				//System.out.println("TP.sM: message " + i + ": " + messages[i]);
				msgs[i] = new UserMessage(messages[i]);
			}
			help = new UserMessageWindow();
			help.setTitle(title);
			help.presentMessages(msgs);
			help.setVisible(true);
		}
		else{
			String receiver = "1:S:11:Application,S:8:Position,I:1:1";
			MessageObject mo = new MessageObject("ShowMessage");
			mo.addObjectParameter("OBJECT", receiver);
			Vector ms = new Vector();
			for(int i=0;i<messages.length;i++){
				ms.addElement(messages[i]);
			}
			mo.addParameter("Message",ms);
			ObjectProxy solverProxy = tool.getObjectProxy();
			solverProxy.send(mo,"Application0");
			mo = null;
		}
	}
	
	public void hideMessage(){
		if(standalone){
			if(help != null && help.isVisible()) {
				help.setVisible(false);
				help.dispose();
			}
		}
		else{
			String receiver = "2:S:11:Application,S:8:Position,I:1:1,S:6:Dialog,S:8:POSITION,I:1:2";
			MessageObject mo = new MessageObject("setProperty");
			mo.addObjectParameter("OBJECT", receiver);
			Vector pn = new Vector();
			Vector pv = new Vector();
			pn.addElement("isVisible");
			pv.addElement(Boolean.valueOf("false"));
			mo.addParameter("propertyNames",pn);
			mo.addParameter("propertyValues",pv);
			ObjectProxy solverProxy = tool.getObjectProxy();
			solverProxy.send(mo,"Application0");
			pn.removeAllElements();
			pn = null;
			pv.removeAllElements();
			pv = null;
			mo = null;
		}
	}

	public boolean messageVisible(){
		return (help != null) && (help.isVisible());
	}
	
	//responseCompleted is called when the tutor has finished responding to an event
	public void responseCompleted(int eventNum) {
		Object storage;
		storage = outstandingEvents.get(Integer.valueOf(String.valueOf(eventNum)));
		outstandingEvents.remove(Integer.valueOf(String.valueOf(eventNum)));
		if (storage instanceof String[]) {
			String[] selactinp = (String[])storage;
			tool.tutorResponseComplete(selactinp[0],selactinp[1],selactinp[2]);
			if(help != null && help.isVisible()) 
				help.requestFocus();
		}
		else
			System.out.println("Error: response completed for event "+eventNum+":"+storage);
	}
	
	public String getProperty(String obj,String property) throws NoSuchPropertyException {
		if (obj.equalsIgnoreCase("Tool"))
			return tool.getProperty(property).toString();
		else
			return tut.getProperty(property).toString();
	}
	
	public void setProperty(String obj,String property, String value) throws DorminException{
		if (obj.equalsIgnoreCase("Tool")){
			tool.setProperty(property, value);
		}
	}
}
