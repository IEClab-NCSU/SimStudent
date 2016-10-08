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
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class UpdateWMIfStudentProblemMatchesList implements Userfunction,
		Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST = "update-wm-if-student-problem-matches-list";
	
	/**	 */
	private static final String DORMIN_STEM = "dorminTable";
	
	/**	 */
	private static final int CONST_ARG_SIZE = 5;
	
	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;

	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public UpdateWMIfStudentProblemMatchesList(){
		this(null);
	}
	
	public UpdateWMIfStudentProblemMatchesList(ModelTracer amt /*SimSt ss*/) {
		this.amt = amt;
		//this.simSt = ss;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {

		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST)) {

			throw new JessException(UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String suggestedProblemsList = SimStRete.NOT_SPECIFIED;
		String sel1= SimStRete.NOT_SPECIFIED;
		String sel2 = SimStRete.NOT_SPECIFIED;
		Fact f = null;
		String[] slotName = null;
		Value[] slotValue = null;

		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				suggestedProblemsList = vv.get(1).resolveValue(context).stringValue(context);
				if(vv.size() > 2) {
					
					sel1 = vv.get(2).resolveValue(context).stringValue(context);
					if(vv.size() > 3) {

						sel2 = vv.get(3).resolveValue(context).stringValue(context);
						if(vv.size() > 4) {
							
							f = vv.get(4).resolveValue(context).factValue(context);
							if(vv.size() > 5) {
								
								int startIndex = CONST_ARG_SIZE;
								int nameValuePair = vv.size() - CONST_ARG_SIZE;
								
								// Check if the nameValuePair is a multiple of 2 or otherwise
								if(!(nameValuePair % 2 == 0)) {

									throw new JessException(UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST, "nameValuePair not specified properly for the function", nameValuePair);
								}
								
								int arraySize = nameValuePair / 2;
								slotName = new String[arraySize];
								slotValue = new Value[arraySize];
								
								for(int i=0, j = 0; i < arraySize; i++, j++) {
									if(vv.size() > startIndex) {
										
										slotName[j] = vv.get(startIndex).resolveValue(context).stringValue(context);
										++startIndex;
										
										if(vv.size() > startIndex) {
											String value1 = vv.get(startIndex).resolveValue(context).stringValue(context);
											if(value1.equalsIgnoreCase("nil")) {
												slotValue[j] = new Value(Funcall.NIL);
											} else {
												slotValue[j] = new Value(value1, RU.STRING);
											}
											++startIndex;
										}
									}
								}
								
								/*slotName[0] = vv.get(5).resolveValue(context).stringValue(context);
								//if(trace.getDebugCode("rr"))trace.out("rr", "Update-WM-If-Student-Problem-Matches-List arg[" + 5 +"] slotName[0]: " + slotName[0] + ";");
								if(vv.size() > 6) {
									
									String value1 = vv.get(6).resolveValue(context).stringValue(context);
									if(value1.equalsIgnoreCase("nil")) {
										slotValue[0] = new Value(Funcall.NIL);
									} else {
										slotValue[0] = new Value(value1, RU.STRING);
									}
									//if(trace.getDebugCode("rr"))trace.out("rr", "Update-WM-If-Student-Problem-Matches-List arg[" + 6 +"] slotValue[0]: " + value1 + ";");
									if(vv.size() > 7) {
										
										slotName[1] = vv.get(7).resolveValue(context).stringValue(context);
										//if(trace.getDebugCode("rr"))trace.out("rr", "Update-WM-If-Student-Problem-Matches-List arg[" + 7 +"] slotName[1]: " + slotName[1] + ";");
										if(vv.size() > 8) {
											
											String value2 = vv.get(8).resolveValue(context).stringValue(context);
											if(value2.equalsIgnoreCase("nil")) {
												slotValue[1] = new Value(Funcall.NIL);
											} else {
												slotValue[1] = new Value(value2, RU.STRING);												
											}
											//if(trace.getDebugCode("rr"))trace.out("rr",  "Update-WM-If-Student-Problem-Matches-List arg[" + 8 +"] slotValue[1]: " + value2 + ";");
										}
									}
								}*/
							}
						}
					}
				}
			}
			
			if(amt == null) {
				if(context.getEngine() instanceof SimStRete) {
					amt = ((SimStRete)context.getEngine()).getAmt();
				}
			}

			if(amt != null && amt.getController() != null /*simSt.getBrController() != null*/ && sel1 != SimStRete.NOT_SPECIFIED && sel2 != SimStRete.NOT_SPECIFIED
					&& suggestedProblemsList != SimStRete.NOT_SPECIFIED) {
				
				String problem = "";
				// TODO : Remove the hard coded start state elements
				if(sel1.equalsIgnoreCase(CompProblemWithHindsight.START_STATE_ELEMENTS[0]) || sel1.equalsIgnoreCase(CompProblemWithHindsight.START_STATE_ELEMENTS[1]) &&
						sel2.equalsIgnoreCase(CompProblemWithHindsight.START_STATE_ELEMENTS[0]) || sel2.equalsIgnoreCase(CompProblemWithHindsight.START_STATE_ELEMENTS[1])) {
			
					String[] startStateElements = {sel1, sel2};
					if(sel1.contains(DORMIN_STEM) && sel2.contains(DORMIN_STEM) && !(sel1.equalsIgnoreCase(sel2))) {
	
						for(int i=0; i < startStateElements.length; i++) {
							
							Object widget = ((BR_Controller)amt.getController()).lookupWidgetByName(startStateElements[i]);
							//Object widget = simSt.getBrController().lookupWidgetByName(startStateElements[i]);
							if(widget != null && widget instanceof TableExpressionCell) {
								
								TableExpressionCell cell = (TableExpressionCell)widget;
								String value = cell.getText().toLowerCase();
							
								String table = startStateElements[i].substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
								int tableVal = Integer.parseInt(table);
								switch(tableVal) {
								case 1:
									problem += " " + value + "=";
									break;
								case 2:
									problem += " " + value;
									break;
								default:
									break;
								}
							}
						}
					}
				}
				
				// Got the student entered problem
				boolean matched = false;
				
				if(problem.length() > 0 && problem.split("=").length > 1 && problem.split("=")[0].trim().length() > 0
						&& problem.split("=")[1].trim().length() > 0) {
					
					Problem studentP = new Problem(problem);
					String abstractedStudentP = studentP.getSignedAbstraction();
					
					String[] list = suggestedProblemsList.split(":");
					String listP = "";
					for(int i=0; i < list.length; i++) {
						
						String prob = list[i];
						String token[] = prob.split("=");
						if(token.length == 2) {
							listP = " " + token[0] + "=" + token[1] + " "; // minerva bug 
						} else {
							return Funcall.FALSE;
						}
						
						Problem suggestedP = new Problem(listP);
						String abstractedSuggestedP = suggestedP.getSignedAbstraction();
						if(abstractedStudentP.equalsIgnoreCase(abstractedSuggestedP)) {
							
							matched = true;
							break;
						}
					}
					
					if(matched && f != null) {
						
						// Student has entered a same type of problem as those he failed on the list
						for(int i=0; i < slotName.length; i++) {
							if(slotName[i] != null && slotValue[i] != null) {
								
								((SimStRete)context.getEngine()).modify(f, slotName[i], slotValue[i]);
							}
						}
						return Funcall.TRUE;
					} else {
						
						// Student deviating
						return Funcall.FALSE;
					}
				}

				
			}
		}
		
		return Funcall.FALSE;
	}
	
	/*public Value call(ValueVector vv, Context context) throws JessException {
		
		//if(trace.getDebugCode("rr"))trace.out("rr", "Enter in call in UpdateWMIfStudentProblemMatchesList");
		
		if(!vv.get(0).stringValue(context).equals(UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST)) {
			throw new JessException(UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String hintRequest = SimStRete.NOT_SPECIFIED; // ?*hintRequest* is a global variable in this Rete's context
		String celllhs = SimStRete.NOT_SPECIFIED;
		String cellrhs = SimStRete.NOT_SPECIFIED;
		String suggestedProblems = SimStRete.NOT_SPECIFIED;
		Fact f = null;
		String slotName = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			// If hintRequest is true then exit out of the function as user made a hint request
			hintRequest = ((SimStRete)context.getEngine()).eval("?*hintRequest*").stringValue(context);
			if(hintRequest.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {
				//if(trace.getDebugCode("rr"))trace.out("rr", "HintRequest : Exit from call in UpdateWMIfRuleSAIEqualsStudentSAI");
				return Funcall.TRUE;
			}

			if(vv.size() > 1) {
				celllhs = vv.get(1).resolveValue(context).stringValue(context);
				//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST arg[" + 1 + "] celllhs: " + celllhs);
				if(vv.size() > 2) {
					cellrhs = vv.get(2).resolveValue(context).stringValue(context);
					//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST arg[" + 2 + "] cellrhs: " + cellrhs);
					if(vv.size() > 3) {
						suggestedProblems = vv.get(3).resolveValue(context).stringValue(context);
						//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST arg[" + 3 + "] suggestedProblems: " + suggestedProblems);
						if(vv.size() > 4) {
							f = vv.get(4).resolveValue(context).factValue(context);
							//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST arg[" + 4 + "] f: " + f);
							if(vv.size() > 5) {
								slotName = vv.get(5).resolveValue(context).stringValue(context);
								//if(trace.getDebugCode("rr"))trace.out("rr",  "UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST arg[" + 5 + "] slotName: " + slotName);
							}
						}
					}
				}
			}
			
			String problem = createProblem(celllhs, cellrhs);
			boolean matched = false;

			if(problem.length() > 0) {
				
				//if(trace.getDebugCode("rr"))trace.out("rr", "Got problem: " + problem);
				Problem studentP = new Problem(problem);
				String abstractedStudentP = studentP.getSignedAbstraction();
				//if(trace.getDebugCode("rr"))trace.out("rr", "abstractedStudentP: " + abstractedStudentP);
				
				String[] list = suggestedProblems.split(":");
				String listP = "";
				for(int i=0; i < list.length; i++) {
					
					String prob = list[i];
					String token[] = prob.split("=");
					if(token.length == 2) {
						listP = " " + token[0] + "=" + token[1] + " "; // minerva bug 
					} else {
						return Funcall.FALSE;
					}
					//if(trace.getDebugCode("rr"))trace.out("rr", "Got list problem: " + listP);
					Problem suggestedP = new Problem(listP);
					String abstractedSuggestedP = suggestedP.getSignedAbstraction();
					//if(trace.getDebugCode("rr"))trace.out("rr", "abstractedSuggestedP: " + abstractedSuggestedP);
					if(abstractedStudentP.equalsIgnoreCase(abstractedSuggestedP)) {
						
						//if(trace.getDebugCode("rr"))trace.out("rr", "YAYYYYYY MATCH");
						matched = true;
						break;
					}
				}
			}
			
			if(matched) {
				
				//if(trace.getDebugCode("rr"))trace.out("rr", "Update the slotName:" + slotName + " in fact: " + f + "  to slotValue: " + problem);
				((SimStRete)context.getEngine()).modify(f, slotName, new Value(problem, RU.STRING));
				((SimStRete)context.getEngine()).modify(f, "quizTaken", new Value("false", RU.STRING));
			}
		}
		
		return Funcall.NIL;
	}
	*/

	@SuppressWarnings("unused")
	private String createProblem(String celllhs, String cellrhs) {
		
		String[] startStateElements = {celllhs, cellrhs};
		String problem = "";
		
		if(amt != null && amt.getController() == null /*simSt != null && simSt.getBrController() == null*/) // DANGER: Don't go futher
			return "";
		
		for(int i=0; i < startStateElements.length; i++) {
			
			Object widget = ((BR_Controller)amt.getController()).lookupWidgetByName(startStateElements[i]);
			//Object widget = simSt.getBrController().lookupWidgetByName(startStateElements[i]);
			if(widget != null && widget instanceof TableExpressionCell) {
				
				TableExpressionCell cell = (TableExpressionCell)widget;
				String value = cell.getText().toLowerCase();
				
				if(value.length() > 0 && value.trim().length() > 0) {
					
					String table = startStateElements[i].substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
					int tableVal = Integer.parseInt(table);
					
					switch(tableVal) {
					case 1:
						problem += " " + value;
						problem += "=";
						break;
					case 2:
						problem += value + " ";
						break;
					default:
						break;
					}
				} else {
					// Cell is empty
					return "";
				}
			}
		}
		
		return problem;
	}

	@Override
	public String getName() {
		
		return UPDATE_WM_IF_STUDENT_PROBLEM_MATCHES_LIST;
	}

}
