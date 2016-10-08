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

public class UpdateMTWorkingMemory implements Userfunction, Serializable {

	/** Default serial version UID */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String UPDATE_MT_WORKING_MEMORY = "update-mt-working-memory";
	
	/**	 */
	//private static final String EVENTS_LIST = "modelTracedEvents";
	
	private static final int NOT_SPEC = 3;
	private static final int NO_MATCH = 2;
	private static final int MATCH = 1;
	private static final int ANY_MATCH = 0;
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public UpdateMTWorkingMemory(){
		this(null);
	}
	
	public UpdateMTWorkingMemory(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {

		//if(trace.getDebugCode("rr"))trace.out("rr", "Enter in call in UpdateMTWorkingMemory");
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_MT_WORKING_MEMORY)) {
			throw new JessException(UPDATE_MT_WORKING_MEMORY, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}

		String hintRequest = SimStRete.NOT_SPECIFIED;
		
		String studentSelection = SimStRete.NOT_SPECIFIED;
		String studentAction = SimStRete.NOT_SPECIFIED;
		String studentInput = SimStRete.NOT_SPECIFIED;
		
		String ruleSelection = SimStRete.NOT_SPECIFIED;
		String ruleAction = SimStRete.NOT_SPECIFIED;
		String ruleInput = SimStRete.NOT_SPECIFIED;
		
		String slot = SimStRete.NOT_SPECIFIED;
		String value = SimStRete.NOT_SPECIFIED;
		
		Fact f = null;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() >  1) {
				f = vv.get(1).factValue(context);
				if(f != null) {
					//events = (LinkedList) f.getSlotValue(EVENTS_LIST).javaObjectValue(context);
					hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
					studentSelection = ((SimStRete)context.getEngine()).eval("?*studentSelection*").stringValue(context);
					studentAction = ((SimStRete)context.getEngine()).eval("?*studentAction*").stringValue(context);
					studentInput = ((SimStRete)context.getEngine()).eval("?*studentInput*").stringValue(context);
					ruleSelection = ((SimStRete)context.getEngine()).eval("?*ruleSelection*").stringValue(context);
					ruleAction = ((SimStRete)context.getEngine()).eval("?*ruleAction*").stringValue(context);
					ruleInput = ((SimStRete)context.getEngine()).eval("?*ruleInput*").stringValue(context);
				} else {
					//if(trace.getDebugCode("rr")) trace.err("*****Error: Model Trace Working Memory Corrupted*****");
					return null;
				}
				//if(trace.getDebugCode("rr"))trace.out("rr", "UPDATE-MT-WORKING-MEMORY arg[" + 1 + "] wm: " + f);
				if(vv.size() > 2) {
					String slotName = vv.get(2).resolveValue(context).stringValue(context);
					//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE-MT-WORKING-MEMORY arg[" + 2 + "] slotName: " + slotName);
					slot = (String) f.getSlotValue(slotName).javaObjectValue(context);
					if(vv.size() >  3) {
						value = vv.get(3).resolveValue(context).stringValue(context);
						//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE-MT-WORKING-MEMORY arg[" + 3 + "] value: " + value);						
					}
				}
			}
			
			if(hintRequest != SimStRete.NOT_SPECIFIED && studentSelection != SimStRete.NOT_SPECIFIED && studentAction != SimStRete.NOT_SPECIFIED
					&& studentInput != SimStRete.NOT_SPECIFIED && ruleSelection != SimStRete.NOT_SPECIFIED && ruleAction != SimStRete.NOT_SPECIFIED
					&& ruleInput != SimStRete.NOT_SPECIFIED) {
				if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.FALSE)) {
					boolean result = compareSAI(studentSelection, studentAction, studentInput, ruleSelection, ruleAction, ruleInput);
					if(result) {
						// If the SAI match then assign the slot (the second argument) the value which we got as the third argument
						slot = value;
						return Funcall.TRUE;
					} else {
						// Rule SAI != Student SAI
						return Funcall.FALSE;
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}
	
	/*public Value call(ValueVector vv, Context context) throws JessException {
		
		//if(trace.getDebugCode("rr"))trace.out("rr", "Enter in call in UpdateMTWorkingMemory");
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_MT_WORKING_MEMORY)) {
			throw new JessException(UPDATE_MT_WORKING_MEMORY, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		LinkedList events = null;
		String hintRequest = SimStRete.NOT_SPECIFIED;
		String studentSelection = SimStRete.NOT_SPECIFIED;
		String ruleSelection = SimStRete.NOT_SPECIFIED;
		String eventInfo = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() >  1) {
				Fact f = vv.get(1).factValue(context);
				if(f != null) {
					events = (LinkedList) f.getSlotValue(EVENTS_LIST).javaObjectValue(context);
					hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
					studentSelection = ((SimStRete)context.getEngine()).eval("?*studentSelection*").stringValue(context);
				} else {
					//if(trace.getDebugCode("rr"))
						trace.err("*****Error: Model Trace Working Memory Corrupted*****");
					return null;
				}
				//if(trace.getDebugCode("rr"))trace.out("rr", "UPDATE-MT-WORKING-MEMORY arg[" + 1 + "] wm: " + f);
				if(vv.size() > 2) {
					ruleSelection = vv.get(2).resolveValue(context).stringValue(context);
					ruleSelection = ruleSelection.replaceAll("SimStName", SimSt.SimStName);
					//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE-MT-WORKING-MEMORY arg[" + 2 + "] ruleSelection: " + ruleSelection);
					if(vv.size() >  3) {
						eventInfo = vv.get(3).resolveValue(context).stringValue(context);
						eventInfo = eventInfo.replaceAll("SimStName", SimSt.SimStName);
						//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE-MT-WORKING-MEMORY arg[" + 3 + "] eventInfo: " + eventInfo);						
					}
				}
			}
			
			//if(trace.getDebugCode("rr"))
				trace.out("rr", "studentSelection: " + studentSelection + "  ruleSelection: " + ruleSelection + "  eventInfo: " + eventInfo);
			
			if(events != null && hintRequest != SimStRete.NOT_SPECIFIED && studentSelection != SimStRete.NOT_SPECIFIED &&
					ruleSelection != SimStRete.NOT_SPECIFIED) {
				if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.FALSE) && studentSelection.equals(ruleSelection)) {
					//if(trace.getDebugCode("rr"))
						trace.out("rr", "Adding the ruleSelection: " + ruleSelection + "  to the modelTracedEvents list");
					events.addFirst(ruleSelection);
					if(eventInfo != SimStRete.NOT_SPECIFIED) {
						events.addFirst(eventInfo);
					}
					return Funcall.TRUE;
				} else {
					return null;
				}
			}
		}
		
		return null;
	}*/

	private boolean compareSAI(String studentSelection, String studentAction,String studentInput,
			String ruleSelection, String ruleAction,String ruleInput) {
		
		//if(trace.getDebugCode("rr"))trace.out("rr", "Enter in compareSAI");
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

	private int testSingleValue(String sv, String rv) {

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
		return UPDATE_MT_WORKING_MEMORY;
	}

}
