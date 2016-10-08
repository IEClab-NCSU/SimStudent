package servlet;

import interaction.SAI;
/**
 * Class to wrap up the associated rules response for the tutor.
 * @author Patrick Nguyen
 *
 */
public class AssociatedRulesTutorResponse extends AssociatedRulesResponse{

	private SAI studentSAI;
	private String toolSelection;
	private boolean endOfTransaction;
	
	
	public boolean isEndOfTransaction() {
		return endOfTransaction;
	}


	public void setEndOfTransaction(boolean endOfTransaction) {
		this.endOfTransaction = endOfTransaction;
	}


	public SAI getStudentSAI() {
		return studentSAI;
	}


	public void setStudentSAI(SAI studentSAI) {
		this.studentSAI = studentSAI;
	}


	public String getToolSelection() {
		return toolSelection;
	}


	public void setToolSelection(String toolSelection) {
		this.toolSelection = toolSelection;
	}


	public String toXML(){
		String xml = "<message>";
		xml+=wrapInXML("verb",getVerb());
		xml+="<properties>";
		
		xml+=wrapInXML("MessageType",getMessageType());
		xml+=wrapInXML("Indicator",getIndicator());
		
		SAI sai1 = getSai();
		xml+=wrapInXML("Selection",sai1.getSelection());
		xml+=wrapInXML("Action",sai1.getAction());
		xml+=wrapInXML("Input",sai1.getInput());
		
		SAI sai2 = getStudentSAI();
		xml+=wrapInXML("StudentSelection",sai2.getSelection());
		xml+=wrapInXML("StudentAction",sai2.getAction());
		xml+=wrapInXML("StudentInput",sai2.getInput());
		
		xml+=wrapInXML("Actor",getActor());
		xml+=wrapInXML("Rules",getRules());
		xml+=wrapInXML("skillBarDelimiter",getSkillBarDelimiter());
		xml+=wrapInXML("StepID",getStepID());
		xml+=wrapInXML("tool_selection",getToolSelection());
		xml+=wrapInXML("transaction_id",getTransactionID());
		xml+=wrapInXML("LogAsResult",""+isLogAsResult());
		xml+=wrapInXML("end_of_transaction",""+isEndOfTransaction());
		
		xml+="</properties>";
		xml+= "</message>";
		return xml;
	}
}
