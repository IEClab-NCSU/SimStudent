package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.jess.Utils;
import edu.cmu.pact.miss.SimSt;

/**
 * 
 */
public class PredictedSAI implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String PREDICTED_SAI = "predicted-sai";
	
	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;
	
	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	/**	 */
	public PredictedSAI() {
		this(null);
	}
	
	/**
	 * @param amt
	 */
	public PredictedSAI(ModelTracer amt /*SimSt ss*/) {
		this.amt = amt;
		//this.simSt = ss;
	}
	
	/**
	 * Test the given arguments against the student values stored in
	 * the attached model tracer {@link #amt}.  <b>This function halts
	 * the Rete when the match fails.</b>
	 * @param vv argument list: order is<ol>
	 *        <li>selection</li>
	 *        <li>action</li>
	 *        <li>input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 */ 
	public Value call(ValueVector vv, Context context) throws JessException {

		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(PREDICTED_SAI))
			throw new JessException(PREDICTED_SAI, "called but ValueVector head differs", vv.get(0).stringValue(context));
		
		String predictedSelection = SimStRete.NOT_SPECIFIED;
		String predictedAction = SimStRete.NOT_SPECIFIED;
		String predictedInput = SimStRete.NOT_SPECIFIED;
		String problemListMatcher = SimStRete.NOT_SPECIFIED;
		String problemList = SimStRete.NOT_SPECIFIED;
		
		String token[] = vv.get(1).resolveValue(context).stringValue(context).split(":");
		String sai[] = null;
		if(token.length == 2) {
			sai = token[0].split(",");
		}
		
		if(context.getEngine() instanceof SimStRete) {
			if(vv.size() > 1) {
				if(sai != null && sai.length >= 1 
						&& ((vv.get(1).resolveValue(context).stringValue(context).split(":")).length == 2)) {
					predictedSelection = sai[0];
				} else {
					predictedSelection = vv.get(1).resolveValue(context).stringValue(context);
					predictedSelection = predictedSelection.replaceAll("SimStName", SimSt.SimStName);
				}
				if(vv.size() > 2) {
					predictedAction = vv.get(2).resolveValue(context).stringValue(context);

					if(vv.size() > 3) {
						if(sai != null && sai.length >= 2
								&& ((vv.get(3).resolveValue(context).stringValue(context).split(":")).length == 2)) {
							predictedInput = sai[2];
						} else {
							predictedInput = vv.get(3).resolveValue(context).stringValue(context);
						}

						if(vv.size() > 4) {
							problemListMatcher = vv.get(4).resolveValue(context).stringValue(context); // gets the class name for problem matcher

							if(vv.size() > 5) {
								problemList = vv.get(5).resolveValue(context).stringValue(context);
							}
						}
					}
				}
			}
			
			// If the problem matcher is specified then see if the student started problem type matches any of the failedProblemList type
			// If both the left hand and right hand side match then update the working memory slot studentEnteredProblem to the student
			// started problem
			if(problemListMatcher != SimStRete.NOT_SPECIFIED && problemList != SimStRete.NOT_SPECIFIED) {
				
				String possibleClassName = problemListMatcher;
				String[] clsNames = {possibleClassName, "edu.cmu.pact.miss.jess."+possibleClassName};
				Class cls = null;
				for(int i=0; i < clsNames.length && cls == null; i++) {
					try {
						cls = Class.forName(clsNames[i]);
					} catch (ClassNotFoundException e) {
						if(trace.getDebugCode("rr"))trace.out("rr", "error finding class " + clsNames[i]+" : " + e);
					}
				}
				
				if(cls != null) {
					try {
						
						Class parTypes[] = new Class[2];
						parTypes[0] = edu.cmu.pact.miss.minerva_3_1.Problem.class;
						parTypes[1] = String.class;
						Constructor ctor = cls.getConstructor(parTypes); // get the constructor which accepts 
						if(ctor != null) {
							
							Object arglist[] = new Object[2];
							arglist[0] = parTypes[0].newInstance();
							arglist[1] = parTypes[1].newInstance();
							Object obj = ctor.newInstance(arglist);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(amt == null) {
				if(context.getEngine() instanceof SimStRete) {
					amt = ((SimStRete)context.getEngine()).getAmt();
				}
			}

			if(predictedSelection != SimStRete.NOT_SPECIFIED && predictedAction != SimStRete.NOT_SPECIFIED && 
					predictedInput != SimStRete.NOT_SPECIFIED) {
				if(amt != null /*simSt.getBrController().getAmt() != null*/) {

					//simSt.getBrController().getAmt().setRuleSAI(predictedSelection, predictedAction, predictedInput);
					amt.setRuleSAI(predictedSelection, predictedAction, predictedInput);
					
					// Set the global SAI values for the rule selection,action,input
					SimStRete ssRete = (SimStRete) context.getEngine();
					//System.out.println("Expected SAI "+predictedSelection+","+predictedAction+","+predictedInput);
					ssRete.eval("(bind ?*ruleSelection* " + Utils.escapeString(predictedSelection) + ")");
					ssRete.eval("(bind ?*ruleAction* " + Utils.escapeString(predictedAction) + ")");
					ssRete.eval("(bind ?*ruleInput* " +  Utils.escapeString(predictedInput) + ")");
				}
				return Funcall.TRUE;
			} else {
				return Funcall.FALSE;
			}
		}
		
		return Funcall.FALSE;
	}
	
	@Override
	public String getName() {
		return PREDICTED_SAI;
	}

}
