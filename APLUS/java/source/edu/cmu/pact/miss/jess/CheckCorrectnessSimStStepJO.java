package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import pact.CommWidgets.JCommTable.TableExpressionCell;
import cl.utilities.sm.BadExpressionError;
import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.InquiryClSolverTutor;
import edu.cmu.pact.miss.InquiryJessOracle;
import edu.cmu.pact.miss.JessOracle;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.SAIConverter;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;

public class CheckCorrectnessSimStStepJO implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String CHECK_CORRECTNESS_SIMSTSTEP = "check-correctness-simststep-jo";
	
	private static final String NOT_SPECIFIED = "NotSpecified";
	
	/**	Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete */
	protected transient Context context;
	
	/**
	 * No argument constructor
	 */
	public CheckCorrectnessSimStStepJO() {}
	
	/**
	 * Constructor with link to the model tracer
	 * @param amt
	 */
	public CheckCorrectnessSimStStepJO(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * 
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		
		

		ProblemNode currentNode = null;
		InquiryClSolverTutor inBuiltCLSolverTutor;
		String result = "";
		
		this.context = context;
		if(!vv.get(0).stringValue(context).equals(CHECK_CORRECTNESS_SIMSTSTEP))
			throw new JessException(CHECK_CORRECTNESS_SIMSTSTEP, "called but ValueVector head differs", vv.get(0).stringValue(context));

		if(context.getEngine() instanceof SimStRete) {
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			
			if((amt != null) && (amt.getController() != null) && (amt.getController() instanceof BR_Controller)
					&& (amt.getController().getMissController() != null) 
					&& amt.getController().getMissController().getSimSt() != null) {
				
				currentNode = ((BR_Controller)amt.getController()).getCurrentNode();
				
				if(currentNode != null && (currentNode != amt.getController().getProblemModel().getStartNode())) {
					
					SimSt ss = amt.getController().getMissController().getSimSt();
	
					ProblemNode prevNode = (ProblemNode) currentNode.getParents().get(0);
					
					
					if(prevNode != null) {
						
						Enumeration<ProblemEdge> edges = ((BR_Controller)amt.getController()).getProblemModel().getProblemGraph().getIncomingEdges(currentNode);
						while(edges.hasMoreElements()) {
							ProblemEdge edge = edges.nextElement();
							if(edge.source == prevNode && edge.dest == currentNode) {
								
								Sai edgeSAI = edge.getSai();

								// Check if it's a valid selection or is a done step
								if(edgeSAI.getS().equalsIgnoreCase(NOT_SPECIFIED)) {
									return Funcall.FALSE;
								} 
								
								// Ask oracle for the correctness of the step now
								try {
									 
									
							       
								       InquiryJessOracle iJessOracle = new InquiryJessOracle(ss, (BR_Controller)amt.getController());

								   	
								       iJessOracle.init(amt.getController().getProblemName());
								       
								       
								      
								       if(edgeSAI.getS().equalsIgnoreCase("NotSpecified"))
								       {
								           return Funcall.FALSE;
								       }
								     
								       iJessOracle.goToState((BR_Controller)amt.getController(), prevNode);
								   	
								   	
								       try {
								      		
								      			result = iJessOracle.isCorrectStep(edgeSAI.getS(), edgeSAI.getA(), edgeSAI.getI()) ? EdgeData.CORRECT_ACTION : EdgeData.UNTRACEABLE_ERROR;
								      			
								          } catch (Exception e) {
								              e.printStackTrace();
								        }
								       
								       if(result.equals(EdgeData.CLT_ERROR_ACTION))
											result = validateResponse(edgeSAI.getS(), edgeSAI.getA(), edgeSAI.getI());
									
									
									 // if(inBuiltCLSolverTutor.getSAIConverter().validSelection(edgeSAI.getS(), numPrevSteps)) {
									//	result = inBuiltCLSolverTutor.isCorrectStep(edgeSAI.getS(), edgeSAI.getA(), edgeSAI.getI()) ?
									//			EdgeData.CORRECT_ACTION : EdgeData.CLT_ERROR_ACTION;
									//	if(result.equals(EdgeData.CLT_ERROR_ACTION))
									//		result = validateCLResponse(edgeSAI.getS(), edgeSAI.getA(), edgeSAI.getI(), inBuiltCLSolverTutor);
									
									
									
									
									
									
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								if(result.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
									return Funcall.TRUE;
								else if(result.equalsIgnoreCase(EdgeData.CLT_ERROR_ACTION))
									return Funcall.FALSE;
							}
						}
					}
				}
			}
		}
		
		return Funcall.FALSE;
	}

    private String validateResponse(String selection, String action, String input) {
        
        String result= EdgeData.CLT_ERROR_ACTION, exp1= null, exp2 = null;
       
        if(((BR_Controller)amt.getController())!= null && ((BR_Controller)amt.getController()).getMissController() != null
        		&& ((BR_Controller)amt.getController()).getMissController().getSimSt() != null) {
	        
        	if(input.contains(" ") || !((BR_Controller)amt.getController()).getMissController().getSimSt().isFoaGetterDefined()) // Don't override CL Oracle response for transformation step
	            return result;
	       
	        Vector<Object> foas = ((BR_Controller)amt.getController()).getMissController().getSimSt().getFoaGetter().foaGetter(((BR_Controller)amt.getController()), selection, action, input,null);
	       
	        if(foas.size() < 2)
	            return result;
	       
	        TableExpressionCell skillCell = (TableExpressionCell) foas.get(0);
	        TableExpressionCell sideCell = (TableExpressionCell) foas.get(1);
	       
	        if(skillCell != null && (skillCell.getText().startsWith("combine") || skillCell.getText().startsWith("clt") || skillCell.getText().startsWith("distribute"))) {
	            if(sideCell.getText().equals(input)) {
	                result = EdgeData.CORRECT_ACTION;
	                return result;
	            } else {
	                result = EdgeData.CLT_ERROR_ACTION;
	                return result;
	            }
	        } else if(sideCell != null && (sideCell.getText().startsWith("combine") || sideCell.getText().startsWith("clt") || sideCell.getText().startsWith("distribute"))) {
	            if(skillCell.getText().equals(input)) {
	                result = EdgeData.CORRECT_ACTION;
	                return result;
	            } else {
	                result = EdgeData.CLT_ERROR_ACTION;
	                return result;
	            }
	        }
	       
	       /* boolean correctNess = false;
	        try {
	            exp1 = bist.getSm().standardize(input, true);
	            exp2 = bist.getSm().standardize(bist.askNextStep().split(";")[2],true);
	            correctNess = bist.getSm().algebraicEqual(exp1, exp2);
	        } catch(BadExpressionError err) {
	            err.printStackTrace();
	        }
	        if(correctNess) {
	            result = EdgeData.CORRECT_ACTION;
	            return result;
	        }*/
        }
	        return result;
    }
	
	@Override
	public String getName() {
		return CHECK_CORRECTNESS_SIMSTSTEP;
	}
}
