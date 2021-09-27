package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommTextField;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.userDef.algebra.Denominator;
import edu.cmu.pact.miss.userDef.algebra.Numerator;

public class ConstructCLHintMessage implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String CONSTRUCT_CL_HINT_MESSAGE = "construct-cl-hint-message";
	
	/**	 */
	private static final String HINT_REQUEST = "hint-request";
	
	private static final String FEEDBACK_REQUEST = "feedback-request";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public ConstructCLHintMessage() { 
		this(null);
	}
	
	public ConstructCLHintMessage(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * 
	 */
	public Value call(ValueVector vv, Context context) throws JessException {

		Value v;
		Value returnValue;
		String returnString = "";
		String tempString;
		char  c, e;
		ValueVector returnVV = new ValueVector();
		
		// vv.get(0) is function name "construct-cl-hint-message"
		if(!vv.get(0).stringValue(context).equals(CONSTRUCT_CL_HINT_MESSAGE))
			throw new JessException(CONSTRUCT_CL_HINT_MESSAGE, "called but ValueVector head differs", vv.get(0).stringValue(context));
	
		for(int i = 1; i < vv.size(); i++){
			v = vv.get(i);
			
			try {
				tempString = v.resolveValue(context).stringValue(context);
			}catch (JessException ex) {
				trace.out("Exception handled: " + ex.getMessage());
				trace.out(ex.getProgramText());
				trace.out(ex.getLineNumber());
				tempString = v.toString();
				if(tempString.endsWith("]")){
					tempString = tempString.substring(0,tempString.length() - 1);
					returnString += tempString;
					returnValue = new Value(returnString,RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
					continue;
				}
			}
			
			// remove any newline character from the string
			tempString = tempString.replaceAll("\n" , " ");
			tempString = tempString.replaceAll("\t" , " ");
			tempString = tempString.replaceAll("\\s+" , " "); 

			if(tempString.length() > 0){
				c = tempString.charAt(0);
				e = tempString.charAt(tempString.length() - 1);
			}else{
				continue;
			}

			if(c == '['){
				if(!returnString.trim().equals("")){
					returnValue = new Value(returnString,RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
				}
				tempString = tempString.substring(1);
				if(tempString.length() > 0){
					c = tempString.charAt(0);  
				}else{
					continue;
				}
			}
			
			if(e == ']'){
				tempString = tempString.substring(0,tempString.length() - 1);					
				returnString += tempString;
				returnValue = new Value(returnString,RU.STRING);
				returnVV.add(returnValue);
				returnString = "";
				continue;
			}
			
			if(c == '?' || c == '$'){
				try{
					v = v.resolveValue(context);
					returnString += v.stringValue(context) + " ";
				}catch(JessException je){
					returnString += v.stringValue(context) + " ";
				}
			}else {
				returnString += tempString + " ";
			}
		}

		returnValue = new Value(returnString,RU.STRING);
		returnVV.add(returnValue);
	
	
		
		// When the rule that fired is MAIN::simst-help-request ask Carnegie Learning to provide the hint message.
		if(context.getEngine().getThisRuleName().contains(HINT_REQUEST)) {
			
			String msg = returnVV.get(0).stringValue(context);
			returnVV = constructHintMessage(msg);
		} else if(context.getEngine().getThisRuleName().contains(FEEDBACK_REQUEST)) {
			
			String msg = returnVV.get(0).stringValue(context);
			//returnVV = constructFeedBackMessage(msg);					
			if (amt!=null){
				ValueVector feedbackVV = new ValueVector();
				String feedbackMessage=amt.getController().getMissController().getSimSt().getFoaGetter().getMTHintMessageOnFeedback(msg, amt.getController().getMissController().getSimSt().getBrController()); 
				feedbackVV.add(feedbackMessage);
				returnVV=feedbackVV;
			}
			
			
		}
		
		//if (jmt == null) {
		//	if (ctx.getEngine() instanceof MTRete)
		//		jmt = ((MTRete) ctx.getEngine()).getJessModelTracing(); 
		//}
		
		if (amt != null && returnVV != null) {
			amt.setFiringNodeCLHintMessages(returnVV, context);
			//amt.setFiringNodeMessages(returnVV, context);
		} else
			return null;
		
		return new Value(returnVV, RU.LIST);
	}

	
	
	/**
	 * 
	 * @param msg
	 * @return
	 * @throws JessException
	 */
	private ValueVector constructFeedBackMessage(String msg) throws JessException {
	
	
		ValueVector returnVV = new ValueVector();
		StringBuilder sb = new StringBuilder();
		
		if(amt != null) {
			
			ProblemNode currentNode = ((BR_Controller)amt.getController()).getCurrentNode();
			List<ProblemEdge> edges = currentNode.getIncomingEdges();
			if(edges.size() >= 1) {

				ProblemEdge edge = edges.get(0);
				Sai sai = edge.getSai();
				
				if(msg.trim().equals("TRUE")) {
					
					if(sai.getS().equalsIgnoreCase(WorkingMemoryConstants.DONE_BUTTON_SELECTION))
						returnVV.add("Saying the problem is solved is correct here.");
					else
						returnVV.add("Yes, " + sai.getI() + " would be correct here.");
				} else if(msg.trim().equals("FALSE")) {
					
					if(sai.getS().equalsIgnoreCase(WorkingMemoryConstants.DONE_BUTTON_SELECTION))
						returnVV.add("Saying the problem is solved would not be correct here.");
					else
						returnVV.add("No, " + sai.getI() + " would not be the right thing to do.");				
				}
				
				return returnVV;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Helper method to query for the next step hint messages.
	 * @param currentNodeName
	 * @throws JessException 
	 */
	private ValueVector constructHintMessage(String msg) throws JessException {
		
		// Suggested Hint message
		// What do you get when you ?transformation? to ?foa/
		// You need to enter ?input? on the left side/right side
		ValueVector returnVV = new ValueVector();
		
		msg = applyTransformationFilter(msg);
		String[] token = msg.split(":");

		if(token.length == 2) {
			String[] hintMsg = token[1].split(";");
			for(int i=0; i< hintMsg.length; i++) {
				returnVV.add(hintMsg[i]);
			}
		} else {
			String[] hintMsg = msg.split(",");
			Vector focusOfAttn = null;
			
			if(hintMsg.length == 3) { // Hint message is of the form dorminTable1_C1R2,UpdateTable,3x
				if(amt != null)
					focusOfAttn = ((BR_Controller)amt.getController()).getMissController().getSimSt().
						getFoaGetter().foaGetter((BR_Controller)amt.getController(), hintMsg[0].trim(), hintMsg[1].trim(), hintMsg[2], null);
				String side = "";
				String message = "";
				String runType = ((BR_Controller)amt.getController()).getRunType();
				if(hintMsg[0].contains("dorminTable1")) {

					if(focusOfAttn != null) {
						if(!runType.equals("springBoot")) {
							message = "What do you get when you apply the transformation " + ((TableExpressionCell)focusOfAttn.elementAt(1)).getText() + " to " + ((TableExpressionCell)focusOfAttn.elementAt(0)).getText() + "?";
						} else {
							message = "What do you get when you apply the transformation " + focusOfAttn.elementAt(1).toString() + " to " + focusOfAttn.elementAt(0).toString() + "?";
							
						}
						returnVV.add(message);
					}
					side = "left";
					message = "You need to enter " + hintMsg[2] + " on the " + side + " side.";
					returnVV.add(message);

				} else if(hintMsg[0].contains("dorminTable2")) {

					if(focusOfAttn != null) {
						if(!runType.equals("springBoot")) {
							message = "What do you get when you apply the transformation " + ((TableExpressionCell)focusOfAttn.elementAt(1)).getText()+ " to " + ((TableExpressionCell)focusOfAttn.elementAt(0)).getText() + "?";
						} else {
							message = "What do you get when you apply the transformation " + focusOfAttn.elementAt(1).toString()+ " to " + focusOfAttn.elementAt(0).toString() + "?";
						}
						returnVV.add(message);
					}
					side = "right";
					message = "You need to enter " + hintMsg[2] + " on the " + side + " side.";
					returnVV.add(message);

				} else if(hintMsg[0].contains("done")) {
					message = "There is no more work left on the problem. Click on the Problem is Solved button.";
					returnVV.add(message);
				}
			}
		}
		return returnVV;
	}

	/**
	 * Intercepts the message from the CL Oracle and modifies the hint message to be
	 * relevant to the transformation skills that the SimStudent knows about.
	 * Example : Hint message telling to use Cross Multiply is substituted by multiply
	 * @param msg
	 * @return
	 */
	private String applyTransformationFilter(String msg) {
		
		StringBuilder sb = new StringBuilder();
		String[] token = msg.split(":");
		if(token.length == 2) {
			
			String[] sai = token[0].split(",");
			sb.append(sai[0]+",");
			sb.append(sai[1]+",");
			String runType = ((BR_Controller)amt.getController()).getMissController().getSimSt().getSsRete().getRunType();
			if(sai.length == 3) {
				
				String skill = sai[2], foa1 = "", foa2 = "";
				Vector focusOfAttn = null;
				if(amt != null)
					focusOfAttn = ((BR_Controller)amt.getController()).getMissController().getSimSt().
						getFoaGetter().foaGetter((BR_Controller)amt.getController(), sai[0].trim(), sai[1].trim(), sai[2], null);
				if(focusOfAttn != null) {
					
				//	foa1 = ((TableExpressionCell)focusOfAttn.elementAt(0)).getText();
				//	foa2 = ((TableExpressionCell)focusOfAttn.elementAt(1)).getText();
				
					if (focusOfAttn.elementAt(0) instanceof JCommComboBox )
						foa1 = (String) ((JCommComboBox)focusOfAttn.elementAt(0)).getValue();
					else if (focusOfAttn.elementAt(0) instanceof JCommTextField  )
						foa1 = ((JCommTextField )focusOfAttn.elementAt(0)).getText();
					else if(runType.equals("springBoot"))
						foa1 = focusOfAttn.elementAt(0).toString();
					else 
						foa1 = ((TableExpressionCell)focusOfAttn.elementAt(0)).getText();
				
					
					if (focusOfAttn.elementAt(1) instanceof JCommComboBox )
						foa2 = (String) ((JCommComboBox)focusOfAttn.elementAt(1)).getValue();
					else if (focusOfAttn.elementAt(1) instanceof JCommTextField  )
						foa2 = ((JCommTextField )focusOfAttn.elementAt(1)).getText();
					else if(runType.equals("springBoot"))
						foa2 = focusOfAttn.elementAt(1).toString();
					else 	
						foa2 = ((TableExpressionCell)focusOfAttn.elementAt(1)).getText();
							
				
				}
				
				// Add other skills for which message needs to be modified here using if-else
				if(skill.contains("cm") && foa1.length() > 0 && foa2.length() > 0) {
					
					Numerator n = new Numerator();
					String num = n.numerator(foa1);
					Denominator d = new Denominator();
					String denom = d.denominator(foa1);
					
					// Create the modified hint message
					sb.append("multiply " + denom + ":");
					sb.append("What can you do to both sides to get " + num + " by itself?;");
					sb.append("In " + foa1 + " , " + num + " is divided by " + denom + ". How do you undo division?;");
					sb.append("Multiply both sides by " + denom + ".");
					
					return sb.toString();
				} else if(skill.contains("rf") && foa1.length() > 0 && foa2.length() > 0) {
					
					if(trace.getDebugCode("rr"))
						trace.out("rr", sai[0] + " " + sai[1] + " " + sai[2]);
					sb.append(sai[2]+":");
					sb.append(token[1]+";");
					sb.append("Type in " + sai[2] + ".");
					return sb.toString();
				}
			}
		}
		
		return msg;
	}
	
	@Override
	public String getName() {
		return CONSTRUCT_CL_HINT_MESSAGE;
	}

}
