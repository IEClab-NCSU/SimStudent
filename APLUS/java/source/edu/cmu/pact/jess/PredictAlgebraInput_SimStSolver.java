package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;

public class PredictAlgebraInput_SimStSolver implements Userfunction, Serializable {

	
	/** Function name, as known to Jess. */
	private static final String PREDICT_ALGEBRA_INPUT_SIMSTSOLVER = "predict-algebra-input";


	public PredictAlgebraInput_SimStSolver() {
		super();
	}
	
	protected transient Context context;
	
	/**
	 * Return the name of this function as registered with Jess.
	 * @return "predict-algebra-input"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return PREDICT_ALGEBRA_INPUT_SIMSTSOLVER;
	}
	
	public Value call(ValueVector vv, Context context) throws JessException {

		//       System.out.println("entered PredictAlgebraInput.call()");

		Value result = Funcall.TRUE;
		
		this.context = context;

		if(!vv.get(0).stringValue(context).equals(PREDICT_ALGEBRA_INPUT_SIMSTSOLVER))
			throw new JessException(PREDICT_ALGEBRA_INPUT_SIMSTSOLVER, "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		String predictedSelection = MTRete.NOT_SPECIFIED;
		String predictedAction = MTRete.NOT_SPECIFIED;
		String predictedInput = MTRete.NOT_SPECIFIED;

			if(vv.size() > 1) {
				predictedSelection = vv.get(1).resolveValue(context).stringValue(context);
				if(vv.size() > 2) {
					predictedAction = vv.get(2).resolveValue(context).stringValue(context);
					if(vv.size() > 3) {
						predictedInput = vv.get(3).resolveValue(context).stringValue(context);
					}
				}
			}
			if(predictedSelection != MTRete.NOT_SPECIFIED && predictedAction != MTRete.NOT_SPECIFIED && predictedInput != MTRete.NOT_SPECIFIED) {
			
						if (!predictedInput.equals("FALSE")){
							//update the global variable *sInput* so that working memory can be updated (RHS of rule uses *sInput* to update wm)
							context.getEngine().eval("(defglobal ?*sInput* = " + edu.cmu.pact.jess.Utils.escapeString(predictedInput, true) + ")");
						}
						
						 Sai sai=new Sai(predictedSelection,predictedAction,predictedInput);
						 ValueVector sai_vv = new ValueVector();
						 sai_vv.add(sai);
					
						 context.getEngine().getGlobalContext().setVariable("*sSai*",new Value(sai));
							

					result=Funcall.TRUE;
			} else {
				result=Funcall.FALSE;
			}
	
		return result;
	}
}
