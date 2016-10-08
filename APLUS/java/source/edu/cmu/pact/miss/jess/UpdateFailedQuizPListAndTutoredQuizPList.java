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
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class UpdateFailedQuizPListAndTutoredQuizPList implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/** Constant to denote the name of this user-defined function */
	private static final String UPDATE_FAILED_QUIZP_LIST_AND_TUTORED_QUIZP_LIST = "update-failedQuizP-list-tutoredQuizP-list";

	/**	 */
	protected transient ModelTracer amt;
	
	/**	Link to current variable context, and, thence, to the Rete */
	protected transient Context context;
	
	public UpdateFailedQuizPListAndTutoredQuizPList(){
		this(null);
	}
	
	public UpdateFailedQuizPListAndTutoredQuizPList(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_FAILED_QUIZP_LIST_AND_TUTORED_QUIZP_LIST)) {

			throw new JessException(UPDATE_FAILED_QUIZP_LIST_AND_TUTORED_QUIZP_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}

		Fact f = null;
		String slotNameForFailedQPList = SimStRete.NOT_SPECIFIED; // failed quiz problem list
		String slotNameForTutoredQPList = SimStRete.NOT_SPECIFIED; // solved quiz problem list
		String slotNameForTutoredQPListAllSections = SimStRete.NOT_SPECIFIED; // solved quiz problem list for all quiz sections
		String problemToRemove = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				f = vv.get(1).resolveValue(context).factValue(context);
				
				if(vv.size() >  2) {
					
					slotNameForFailedQPList = vv.get(2).resolveValue(context).stringValue(context);
					
					if(vv.size() > 3) {
						
						slotNameForTutoredQPList = vv.get(3).resolveValue(context).stringValue(context);
						
						if(vv.size() > 4) {
							
							//slotNameForTutoredQPListAllSections = vv.get(4).resolveValue(context).stringValue(context);
							problemToRemove = vv.get(4).resolveValue(context).stringValue(context);
								
							String[] sidesP = problemToRemove.split("=");
							if (sidesP.length == 2) {
								
								problemToRemove = " " + sidesP[0] + "=" + sidesP[1] + " ";
							} else {
								// Trouble don't go ahead as this problem can't
								// be abstracted
								return Funcall.FALSE;
							}
						}
					}
				}
			}
			
			if(f != null && slotNameForFailedQPList != SimStRete.NOT_SPECIFIED && slotNameForTutoredQPList != SimStRete.NOT_SPECIFIED
					&& problemToRemove != SimStRete.NOT_SPECIFIED) {
			
				String problemList = f.getSlotValue(slotNameForFailedQPList).stringValue(context);
				String list[] = problemList.split(":");
				String listP = "";
				String newSuggestedProblemsList = "";
				
				String tutoredProblemList = f.getSlotValue(slotNameForTutoredQPList).stringValue(context);
				
				Problem studentP = new Problem(problemToRemove);
				String abstractedStudentP = studentP.getSignedAbstraction();
				
				Integer count = ModelTraceWorkingMemory.quizProblemsTutoredListAllSections.get(abstractedStudentP);
				if(count == null) {
					
					ModelTraceWorkingMemory.quizProblemsTutoredListAllSections.put(abstractedStudentP, new Integer(1));
				} else {
					
					int value = count.intValue();
					++value;
					ModelTraceWorkingMemory.quizProblemsTutoredListAllSections.put(abstractedStudentP, new Integer(value));
				}

				if(list.length > 0) {
					
					for(int i=0; i < list.length; i++) {
						
						String prob = list[i];
						String token[] = prob.split("=");
						if(token.length == 2) {
							listP = " " + token[0] + "=" + token[1] + " "; // minerva bug
						} else {
							continue;
						}
						
						Problem suggestedP = new Problem(listP);
						String abstractedSuggestedP = suggestedP.getSignedAbstraction();
						
						if(abstractedStudentP.equalsIgnoreCase(abstractedSuggestedP)) {
							
							if(!tutoredProblemList.equalsIgnoreCase("nil")) {
								
								tutoredProblemList = prob + ":" + tutoredProblemList;
							} else {
								
								tutoredProblemList = prob + ":";
							}
							
						} else {
							
							newSuggestedProblemsList += prob + ":";
						}
					}
					
					if(newSuggestedProblemsList.length() > 0) {
			
						((SimStRete)context.getEngine()).modify(f, slotNameForFailedQPList, new Value(newSuggestedProblemsList, RU.STRING));
						
						if(tutoredProblemList.length() > 0) {
							
							((SimStRete)context.getEngine()).modify(f, slotNameForTutoredQPList, new Value(tutoredProblemList, RU.STRING));
						} else {
							
							((SimStRete)context.getEngine()).modify(f, slotNameForTutoredQPList, Funcall.NIL);
						}
						return Funcall.TRUE;
					} else {
						
						((SimStRete)context.getEngine()).modify(f, slotNameForFailedQPList, Funcall.NIL);
						
						if(tutoredProblemList.length() > 0) {
							
							((SimStRete)context.getEngine()).modify(f, slotNameForTutoredQPList, new Value(tutoredProblemList, RU.STRING));
						} else {
							
							((SimStRete)context.getEngine()).modify(f, slotNameForTutoredQPList, Funcall.NIL);							
						}
					}
				} else {
					
					return Funcall.FALSE;
				}
			}
		}
		
		return Funcall.FALSE; 
	}

	@Override
	public String getName() {
		
		return UPDATE_FAILED_QUIZP_LIST_AND_TUTORED_QUIZP_LIST;
	}

}
