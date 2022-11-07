package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.jess.WMEEditor;
import edu.cmu.pact.miss.SimSt;

public class GetMTProblemSuggestion implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_MT_PROBLEM_SUGGESTION = "get-mt-problem-suggestion";
	
	/** Model tracer instance with student values namely selection, action and input */
	protected transient ModelTracer amt;
	
	/** Link to the APlus Environment */
	//protected SimSt simSt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	/**	 */
	public GetMTProblemSuggestion() {
		this(null);
	}
	
	/**
	 * @param amt
	 */
	public GetMTProblemSuggestion(ModelTracer amt /*SimSt ss*/) {
		this.amt = amt;
		//this.simSt = ss;
	}
	
	/**
	 * Store the given arguments for logging purposses the student values stored in
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
		
		
		if(!vv.get(0).stringValue(context).equals(GET_MT_PROBLEM_SUGGESTION))
			throw new JessException(GET_MT_PROBLEM_SUGGESTION, "called but ValueVector head differs", vv.get(0).stringValue(context));
		
		String suggestedSelection = SimStRete.NOT_SPECIFIED;
		String suggestedAction = SimStRete.NOT_SPECIFIED;
		String suggestedInput = SimStRete.NOT_SPECIFIED;
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
					suggestedSelection = sai[0];
				} else {
					suggestedSelection = vv.get(1).resolveValue(context).stringValue(context);
					suggestedSelection = suggestedSelection.replaceAll("SimStName", SimSt.SimStName);
				}
				if(vv.size() > 2) {
					suggestedAction = vv.get(2).resolveValue(context).stringValue(context);

					if(vv.size() > 3) {
						if(sai != null && sai.length >= 2
								&& ((vv.get(3).resolveValue(context).stringValue(context).split(":")).length == 2)) {
							suggestedInput = sai[2];						
						} else {
							suggestedInput = vv.get(3).resolveValue(context).stringValue(context);
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
			
			

			if(amt == null) {
				if(context.getEngine() instanceof SimStRete) {
					amt = ((SimStRete)context.getEngine()).getAmt();
				}
			}
			
					
			//trace.out(" Cause of error : "+amt+ "   Log "+amt.getLogger().getHintLogAgent());
			/*Inform the log controller about the metatutor suggestion. We are sure that amt is not null, as it is initialized above*/
			if (suggestedInput!="nil"){	
					amt.getLogger().getHintLogAgent().updateMtStartStateElementsValues(suggestedSelection, suggestedInput);
			}
			
		}
		
		return Funcall.TRUE;
	}
	
	@Override
	public String getName() {
		return GET_MT_PROBLEM_SUGGESTION;
	}

}