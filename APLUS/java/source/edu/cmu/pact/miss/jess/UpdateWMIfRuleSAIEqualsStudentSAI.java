package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;

public class UpdateWMIfRuleSAIEqualsStudentSAI /*extends jess.NVPairOperation*/ implements Userfunction,
		Serializable {

	/** Default serial version UID */
	private static final long serialVersionUID = 1L;

	private static final String UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI = "update-wm-if-ruleSAI-equals-studentSAI";

	private static final int NOT_SPEC = 3;
	private static final int NO_MATCH = 2;
	private static final int MATCH = 1;
	private static final int ANY_MATCH = 0;

	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;

	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public UpdateWMIfRuleSAIEqualsStudentSAI(){
		this(null);
	}
	
	public UpdateWMIfRuleSAIEqualsStudentSAI(ModelTracer amt /*SimSt ss*/) {
		this.amt = amt;
		//this.simSt = ss;
	}
	
	/*public Value call(ValueVector vv, Context context) throws JessException {
			
		if(!vv.get(0).stringValue(context).equals(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI)) {
			throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String hintRequest = SimStRete.NOT_SPECIFIED; // ?*hintRequest* is a global variable in this Rete's context
		Fact f = null;
		//String[] slotName = null;
		//Value[] slotValue = null;
		//String wmSlot = SimStRete.NOT_SPECIFIED;
		//String slotValue = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			// If hintRequest is true then exit out of the function as user made a hint request
			hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
			if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {
				return Funcall.TRUE;
			}
			
			Rete engine = context.getEngine();
			Fact fact = getFactArgument(vv, context, engine, UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI);
			Deftemplate dt = fact.getDeftemplate();
			
			int size = vv.size() - 2;
			String[] slotNames = new String[size];
			Value[] slotValues = new Value[size];
			for(int i=2,j=0; i < vv.size(); i++, j++) {
				// fetch the slot, value subexp, stored as a list
				ValueVector svp = vv.get(i).listValue(context);
				
				String slotName = getSlotName(svp, context);
				int type = dt.getSlotType(slotName);
				slotNames[j] = slotName;
				slotValues[j] = getSlotValue(svp, context, type);
			}
			
			// Test if the ruleSAI equals studentSAI AND if it does then modify the wmSlot to the slotValue
			if(hintRequest != SimStRete.NOT_SPECIFIED && fact != null && 
					slotNames != null && (slotNames.length > 0) && slotValues != null && (slotValues.length > 0)) {
				APlusModelTracing amt = null;
				if(simSt != null && simSt.getBrController() != null)
					amt = simSt.getBrController().getAmt();
				if(amt != null && amt.getNodeNowFiring() != null) {
					String studentSelection = amt.getStudentSelection();
					String studentAction = amt.getStudentAction();
					String studentInput = amt.getStudentInput();
					
					String ruleSelection = amt.getNodeNowFiring().getActualSelection();
					String ruleAction = amt.getNodeNowFiring().getActualAction();
					String ruleInput = amt.getNodeNowFiring().getActualInput();
					
					boolean result = compareSAI(studentSelection, studentAction, studentInput, ruleSelection, ruleAction, ruleInput);
					if(result) {
						// If the SAI match then assign the slot (the second argument) the value which we got as the third argument
						((SimStRete)context.getEngine()).modify(fact, slotNames, slotValues);
						return Funcall.TRUE;
					} else {
						// RuleSAI != StudentSAI
						return Funcall.FALSE;
						//throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "terminating the rete as student SAI does not match rule SAI",
						//		studentSelection+":"+studentAction+":"+studentInput + "   " + ruleSelection+":"+ruleAction+":"+ruleInput);
					}
				}
			}
		}
		
		return null;
	}*/
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI)) {

			throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String hintRequest = SimStRete.NOT_SPECIFIED; // ?*hintRequest* is a global variable in this Rete's context
		Fact f = null;
		String[] slotNames = null;
		Value[] slotValues = null;
		//String wmSlot = SimStRete.NOT_SPECIFIED;
		//String slotValue = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			// If hintRequest is true then exit out of the function as user made a hint request
			hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
			//trace.out(" Hint request : "+hintRequest);
			if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {

				return Funcall.TRUE;
			}

			if(vv.size() > 1) {
			
				//wmSlot = vv.get(1).resolveValue(context).stringValue(context);
				f = vv.get(1).resolveValue(context).factValue(context);
				slotNames = new String[(vv.size()) - 2];
				slotValues = new Value[(vv.size()) - 2];

				if(vv.size() > 2) {
					for(int i=2; i < vv.size(); i++) {
						String arg = vv.get(i).resolveValue(context).stringValue(context);

						int openParenIndex = arg.indexOf('(');
						int closeParenIndex = arg.indexOf(')', openParenIndex+1);
						if(openParenIndex != -1 && closeParenIndex != -1) {
							arg = arg.substring(openParenIndex+1, closeParenIndex);
							String nameValuePair [] = arg.split(" ");
							if(nameValuePair.length == 2) {
								
								slotNames[i-2] = nameValuePair[0];
								
								if(nameValuePair[1].equalsIgnoreCase("nil")) {
									slotValues[i-2] = Funcall.NIL;
								}
								else {
									slotValues[i-2] = new Value(nameValuePair[1]);									
								}
							
								
							} else {
								throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "slotName and slotValue pairs are not defined properly", arg);							
							}
						} else {
							throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "slotName and slotValue pairs are not defined properly", arg);
						}
					}
				}
			}
		
			//if(vv.size() > 2) {
			//	slotName = vv.get(2).resolveValue(context).stringValue(context);
			//	if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI arg[" + 2 + "] slotName: " + slotName);
			//	if(vv.size() > 3) {
			//		slotValue = vv.get(3).resolveValue(context);
			//		if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI arg[" + 3 + "] slotValue: " + slotValue);						
			//	}
			//}			
		}
			
			// Test if the ruleSAI equals studentSAI AND if it does then modify the wmSlot to the slotValue
			if(hintRequest != SimStRete.NOT_SPECIFIED && f != null && 
					slotNames != null && (slotNames.length > 0) && slotValues != null && (slotValues.length > 0)) {
				//APlusModelTracing amt = null;
				//if(simSt != null && simSt.getBrController() != null)
				//	amt = simSt.getBrController().getAmt();
				
				if(amt == null) {
					if(context.getEngine() instanceof SimStRete) {
						amt = ((SimStRete)context.getEngine()).getAmt();
					}
				}

				if(amt != null && amt.getNodeNowFiring() != null) {
					String studentSelection = amt.getStudentSelection();
					String studentAction = amt.getStudentAction();
					String studentInput = amt.getStudentInput();
					
					String ruleSelection = amt.getNodeNowFiring().getActualSelection();
					String ruleAction = amt.getNodeNowFiring().getActualAction();
					String ruleInput = amt.getNodeNowFiring().getActualInput();
					
					boolean result = compareSAI(studentSelection, studentAction, studentInput, ruleSelection, ruleAction, ruleInput);
					//trace.out(" Comparing : "+studentSelection+","+studentAction+","+studentInput);
					//trace.out(" With : "+ruleSelection+","+ruleAction+","+ruleInput);
					//trace.out("Compare the SAI results : "+result);
					if(result) {
						// If the SAI match then assign the slot (the second argument) the value which we got as the third argument
						int count = slotNames.length;
						for(int i = 0; i < count; i++) {
							//trace.out("Modifying : "+f+" Names : "+slotNames[i]+" Values : "+slotValues[i].toStringWithParens());
							((SimStRete)context.getEngine()).modify(f, slotNames[i], slotValues[i]);
						}
						//ssRete.eval("(bind ?*studentInput* " + studentInput + ")");
						return Funcall.TRUE;
					} else {
						// RuleSAI != StudentSAI
						return Funcall.FALSE;
						//throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI, "terminating the rete as student SAI does not match rule SAI",
						//		studentSelection+":"+studentAction+":"+studentInput + "   " + ruleSelection+":"+ruleAction+":"+ruleInput);
					}
				}
			}
		
		return Funcall.NIL; // to return "no value" to Jess instead of using null for java
	}

	public static boolean compareSAI(String studentSelection, String studentAction,String studentInput,
			String ruleSelection, String ruleAction,String ruleInput) {
		
		final int S = 0x1;
		final int A = 0x2;
		final int I = 0x4;
		int result = 0, temp;
		
		temp = testSingleValue(studentSelection, ruleSelection);
		if(temp == NO_MATCH)
			return false;
		else if(temp == MATCH)
			result |= S;
		
		temp = testSingleValue(studentAction, ruleAction);
		if(temp == NO_MATCH)
			return false;
		else if(temp == MATCH)
			result |= A;
		
		temp = testSingleValue(studentInput, ruleInput);
		if(temp == NO_MATCH)
			return false;
		else if(temp == MATCH)
			result |= I;

		if((result & (S|A|I)) == (S|A|I))
			return true;
		else 
			return false;
	}

	private static int testSingleValue(String sv, String rv) {

		int result = -1;
		
		if(rv.equals(SimStRete.NOT_SPECIFIED))
			result = NOT_SPEC;
		else if(rv.equals(SimStRete.DONT_CARE))
			result = MATCH;
		else if(rv.equals(sv))
			result = MATCH;
		else 
			result = NO_MATCH;
		
		return result;
	}

	@Override
	public String getName() {
		return UPDATE_WM_IF_RULESAI_EQUALS_STUDENTSAI;
	}

}
