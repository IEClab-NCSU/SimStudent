package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;

public class UpdateWMIfRuleSAIEqualsStudentEnteredSide implements
		Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String UPDATE_WM_IF_RULESAI_EQUALS_STUDENT_ENTERED_SIDE = "update-wm-if-ruleSAI-equals-student-entered-side";

	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;

	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public UpdateWMIfRuleSAIEqualsStudentEnteredSide(){
		this(null);
	}
	
	public UpdateWMIfRuleSAIEqualsStudentEnteredSide(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_WM_IF_RULESAI_EQUALS_STUDENT_ENTERED_SIDE)) {

			throw new JessException(UPDATE_WM_IF_RULESAI_EQUALS_STUDENT_ENTERED_SIDE, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String hintRequest = SimStRete.NOT_SPECIFIED; // ?*hintRequest* is a global variable in this Rete's context
		Fact f = null;
		String[] slotNames = null;
		Value[] slotValues = null;
		
		if(context.getEngine() instanceof SimStRete) {
			
			// If hintRequest is true then exit out of the function as user made a hint request
			hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
			if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {

				return Funcall.TRUE;
			}

			if(vv.size() > 1) {
				
				//wmSlot = vv.get(1).resolveValue(context).stringValue(context);
				f = vv.get(1).resolveValue(context).factValue(context);
				slotNames = new String[((vv.size()) - 2)/2];
				slotValues = new Value[((vv.size()) - 2)/2];
				int nameIndex =0, valueIndex = 0;
				
				if(((vv.size()-2) % 2) == 0) {
					for(int i=2; i < vv.size(); i++) {
						
						String arg = vv.get(i).resolveValue(context).stringValue(context);
						if((i % 2) == 0) {
							slotNames[nameIndex] = arg;
							++nameIndex;
						} else {
							slotValues[valueIndex] = new Value(arg, RU.STRING);
							++valueIndex;
						}
					}
				}
			}
		}
		
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
				
				boolean result = UpdateWMIfRuleSAIEqualsStudentSAI.compareSAI(studentSelection, studentAction, studentInput, ruleSelection, ruleAction, ruleInput);
				
				if(result) {
					// If the SAI match then assign the slot (the second argument) the value which we got as the third argument
					int count = slotNames.length;
					for(int i = 0; i < count; i++) {
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

	@Override
	public String getName() {
		return UPDATE_WM_IF_RULESAI_EQUALS_STUDENT_ENTERED_SIDE;
	}

}
