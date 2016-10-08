package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

/**
 * Function callable from Jess code (see {@link #getName()} to
 * send a CommMessage from {@link BR_Controller#handleCommMessageUTP(MessageObject)
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class PerformTutorAction implements Userfunction, Serializable {
	
	/** Function name, as known to Jess. */
	private static final String FUNCTION_NAME = "perform-tutor-action";

	/** Model tracer instance with student values. */
	protected transient JessModelTracing jmt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	/**
	 * No-argument constructor for use from (load-function).
	 */
	public PerformTutorAction() {
		if (trace.getDebugCode("ui")) trace.printStack("ui", "PerformTutorAction()");
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public PerformTutorAction(JessModelTracing jmt) {
		if (trace.getDebugCode("ui")) trace.printStack("ui", "PerformTutorAction(JessModelTracing "+jmt+")");
		this.jmt = jmt;
	}	

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@link #FUNCTION_NAME}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return FUNCTION_NAME;
	}

	/**
	 * Test the given arguments against the student values stored in
	 * the attached model tracer {@link #jmt}.  <b>This function halts
	 * the Rete when the match fails.</b>
	 * @param vv argument list: order is<ol>
	 *        <li>[functionName=={@link #FUNCTION_NAME}, supplied by Jess]</li> 
	 *        <li>messageType--use "InterfaceAction" for msgs to student interface</li>
	 *        <li>selection--widget name, e.g. "commLabel1"</li>
	 *        <li>action--e.g. "UpdateText"</li>
	 *        <li>input--new text to write</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} on failure
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if (getJmt() == null) // to avoid problems with startup
			return Funcall.TRUE;

		if (getJmt().isHintTrace())
			return Funcall.TRUE;  // no-op if called on a hint request
		
		Value result = Funcall.FALSE;
		
		if(!vv.get(0).stringValue(context).equals(FUNCTION_NAME))
			throw new JessException(FUNCTION_NAME, "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		String messageType = "";
		String selection = "";
		String action = "";
		String input = "";

		
		if(vv.size() > 1) {
			messageType = vv.get(1).resolveValue(context).stringValue(context);
			if(vv.size() > 2) {
				selection = vv.get(2).resolveValue(context).stringValue(context);
				if(vv.size() > 3) {
					action = vv.get(3).resolveValue(context).stringValue(context);
					if(vv.size() > 4) {
						input = vv.get(4).resolveValue(context).stringValue(context);
					}
				}
			}
		}
		if (messageType.length() < 1)
			return result;

		MTRete rete = null;
        MT mt = null;
        BR_Controller controller = null;
        if (jmt != null 
        		&& (rete = jmt.getRete()) != null
        		&& (mt = rete.getMT()) != null
        		&& (controller = (BR_Controller) mt.getController()) != null) {

        	String transactionId = controller.getMessageTank().enqueueMessageToStudent(messageType,
        			selection, action, input, PseudoTutorMessageBuilder.TUTOR_PERFORMED);
        	if (trace.getDebugCode("ui"))
        		trace.out("ui", String.format("(%s %s \"%s\" \"%s\" \"%s\") transactionId %s",
        				FUNCTION_NAME, messageType, selection, action, input, transactionId));
    		return Funcall.TRUE;
        }
		return Funcall.FALSE;
	}

	/**
	 * Get a reference to the model tracer. If the field {@link #jmt} is null,
	 * tries to reestablish the connection via
	 * {@link MTRete#getJmt()}.
	 * 
	 * @return Returns the jmt.
	 */
	protected JessModelTracing getJmt() {
		if (jmt == null) {
			if (context != null) {
				if (context.getEngine() instanceof MTRete)
					jmt = ((MTRete) context.getEngine()).getJmt();
			}
		}
		return jmt;
	}

	/**
	 * @param jmt The jmt to set.
	 */
	protected void setJmt(JessModelTracing jmt) {
		this.jmt = jmt;
	}
}
