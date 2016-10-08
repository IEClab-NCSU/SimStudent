package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
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

public class CompProblemWithHindsight implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/** Constant to denote the name of this user-defined function */
	private static final String COMP_PROBLEM_WITH_HINDSIGHT = "comp-problem-with-hindsight";
	
	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;

	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/**	Link to current variable context and, thence, to the Rete */
	protected transient Context context;
	
	private static final String DORMIN_STEM = "dorminTable";

	public static final String[] START_STATE_ELEMENTS = {"dorminTable1_C1R1", "dorminTable2_C1R1"};
	               
	public CompProblemWithHindsight(){
		this(null);
	}
	
	public CompProblemWithHindsight(ModelTracer amt /*SimSt ss*/) {
		this.amt = amt;
		//this.simSt = ss;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(COMP_PROBLEM_WITH_HINDSIGHT)) {
			throw new JessException(COMP_PROBLEM_WITH_HINDSIGHT, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String input = SimStRete.NOT_SPECIFIED;
		String sel1= SimStRete.NOT_SPECIFIED;
		String sel2 = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				
				problemList = vv.get(1).resolveValue(context).stringValue(context);
				if(vv.size() > 2) {
					
					input = vv.get(2).resolveValue(context).stringValue(context);
					if(vv.size() > 3) {
						
						sel1 = vv.get(3).resolveValue(context).stringValue(context);
						if(vv.size() > 4) {
							
							sel2 = vv.get(4).resolveValue(context).stringValue(context);
						}
					}
				}
			}
			
			if(amt == null) {
				if(context.getEngine() instanceof SimStRete) {
					amt = ((SimStRete)context.getEngine()).getAmt();
				}
			}
			
			if (problemList!=null){
			String[] list1 = problemList.split(":");
			Value rtnValue1 = new Value(formatProblem(list1[0]), RU.STRING);
			return rtnValue1;
			}
			
			
			if(amt != null && amt.getController() != null /*simSt.getBrController() != null*/) {
				
				Object widget = ((BR_Controller)amt.getController()).lookupWidgetByName(sel2);
				//simSt.getBrController().lookupWidgetByName(sel2);
				if(widget != null && widget instanceof TableExpressionCell) {
					
					TableExpressionCell cell = (TableExpressionCell)widget;
					String value = cell.getText().toLowerCase();
					
					
					if(value.length() > 0 && value.trim().length() > 0) {
						
						String problem = "";
						
						// TODO : Remove the hard coded start state elements
						if(sel1.equalsIgnoreCase(START_STATE_ELEMENTS[0]) || sel1.equalsIgnoreCase(START_STATE_ELEMENTS[1])) {
							String table = sel2.substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
							int tableVal = Integer.parseInt(table);
							switch(tableVal) {
							case 1:
								problem += " " + value + "=" + input + " ";
								break;
							case 2:
								problem += " " + input + "=" + value + " ";
								break;
							default:
								break;
							}
						}
				
						boolean matched = false;
					
						if(problem.length() > 0 && problem.split("=").length > 1 && problem.split("=")[0].trim().length() > 0 && 
								problem.split("=")[1].trim().length() > 0) {
							
							Problem studentP = new Problem(problem);
							String abstractedStudentP = studentP.getSignedAbstraction();
							
							String[] list = problemList.split(":");
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
							
							if(matched) {
								
								problem = problem.trim();
								Value rtnValue = new Value(formatProblem(problem), RU.STRING);
							
								return rtnValue;
							} else {
								
								//list[0] = list[0].trim();
								//Value rtnValue = new Value(list[0], RU.STRING);
								Value rtnValue = new Value("", RU.STRING);
								
								return rtnValue;
							}
						} else {

							String[] list = problemList.split(":");
							if(list.length > 0) {
								Value rtnValue = new Value(formatProblem(list[0]), RU.STRING);
							
								return rtnValue;
							}
						}
					} else {
						
						String problem = "";
						String matchedP = "";
						if(sel1.contains(DORMIN_STEM) && !(sel1.equalsIgnoreCase(sel2))) {
							String table = sel1.substring(DORMIN_STEM.length(), DORMIN_STEM.length()+1);
							int tableVal = Integer.parseInt(table);
							if(tableVal == 1) {
								
								boolean matched = false;
								problem += " " + input + "=" + input + " ";
								
								
								if(problem.length() > 0 && problem.split("=").length > 1 && problem.split("=")[0].trim().length() > 0 && 
										problem.split("=")[1].trim().length() > 0) {
									
									Problem studentP = new Problem(problem);
									String abstractedStudentP = studentP.getSignedAbstraction();
									
									String[] list = problemList.split(":");
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
										if(abstractedStudentP.split("=")[0].trim().equalsIgnoreCase(abstractedSuggestedP.split("=")[0].trim())) {
											
											matchedP = listP;
											matched = true;
											break;
										}
									}
									
									if(matched) {
										
										problem = problem.trim();
										Value rtnValue = new Value(formatProblem(problem), RU.STRING);
										
										return rtnValue;
									} else {
										
										list[0] = list[0].trim();
										Value rtnValue = new Value(formatProblem(list[0]), RU.STRING);
										
										return rtnValue;
									}
								}
							} else if(tableVal == 2) {
								
								// TODO: Put the equivalent code for this section as well
							}
						} else {
							
							String[] list = problemList.split(":");
							if(list.length > 0) {
								Value rtnValue = new Value(formatProblem(list[0]), RU.STRING);
							
								return rtnValue;
							}
						}
					}
				}
			}
		}

		return Funcall.FALSE;
	}

	private String formatProblem(String problem) {
		
		String[] token = problem.split("=");
		if(token.length == 2 && token[0].trim().length() > 0 && token[1].trim().length() > 0) {
	
			problem = token[0].trim() + "  =  " +  token[1].trim();
		}
		
		return problem;
	}
	
	@Override
	public String getName() {
		return COMP_PROBLEM_WITH_HINDSIGHT;
	}

}
