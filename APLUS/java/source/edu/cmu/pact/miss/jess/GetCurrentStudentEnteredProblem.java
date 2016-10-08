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
import edu.cmu.pact.miss.AlgebraProblemAssessor;

public class GetCurrentStudentEnteredProblem implements Userfunction,
		Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_CURRENT_STUDENT_ENTERED_PROBLEM = "get-current-student-entered-problem";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetCurrentStudentEnteredProblem(){
		this(null);
	}
	
	public GetCurrentStudentEnteredProblem(ModelTracer amt){
		this.amt = amt;
	}

	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_CURRENT_STUDENT_ENTERED_PROBLEM)) {
			throw new JessException(GET_CURRENT_STUDENT_ENTERED_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String startStateElement1 = SimStRete.NOT_SPECIFIED;
		String startStateElement2 = SimStRete.NOT_SPECIFIED;
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				startStateElement1 = vv.get(1).resolveValue(context).stringValue(context);
				
				if(vv.size() > 2) {
					startStateElement2 = vv.get(2).resolveValue(context).stringValue(context);
				}
			}
			
		}
		
		if(amt == null) {
			if(context.getEngine() instanceof SimStRete) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
		}

		
		if(amt != null && amt.getController() != null /*simSt.getBrController() != null*/) {
			
			
			Object widget1 = ((BR_Controller)amt.getController()).lookupWidgetByName(startStateElement1);
			Object widget2 = ((BR_Controller)amt.getController()).lookupWidgetByName(startStateElement2);
			
			//simSt.getBrController().lookupWidgetByName(sel2);
			if(widget1 != null && widget1 instanceof TableExpressionCell && widget2 != null &&
					widget2 instanceof TableExpressionCell) {
				
				TableExpressionCell cell1 = (TableExpressionCell)widget1;
				TableExpressionCell cell2 = (TableExpressionCell)widget2;
				String value1 = cell1.getText().toLowerCase();
				String value2 = cell2.getText().toLowerCase();
				
				if(value1.length() > 0 && value1.trim().length() > 0 && value2.length() > 0
						&& value2.trim().length() > 0) {
					
					problem = value1 + "=" + value2;
					return new Value(problem, RU.STRING);
				}
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return GET_CURRENT_STUDENT_ENTERED_PROBLEM;
	}

}
