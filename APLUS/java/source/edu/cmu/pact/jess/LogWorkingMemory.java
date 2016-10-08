/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;
import java.util.Iterator;

import jess.Context;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * @author sewall
 *
 */
public class LogWorkingMemory implements Userfunction, Serializable {

	private static final String CLOSE_BRACKET = "]";

	private static final String OPEN_BRACKET = " [";

	/** Function name for {@link #getName()}. */
	private static final String LOG_WORKING_MEMORY = "log-working-memory";

	/** Model tracer instance with student values. */
	protected transient JessModelTracing jmt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	/**
	 * No-argument constructor for use from (load-function).
	 */
	public LogWorkingMemory() {
		this(null);
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public LogWorkingMemory(JessModelTracing jmt) {
		super();
		this.jmt = jmt;
		
		
	}	

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@value #LOG_WORKING_MEMORY}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return LOG_WORKING_MEMORY;
	}

	/**
	 * Log arguments or all of WM.
	 * @param vv vv[1] is label for log entry; vv[2]... are facts or strings to log;
	 *        if vv[2]... are absent, then dumps all facts in WM
	 * @param context for resolving vv[] element values
	 * @return {@value Funcall#TRUE} if logs anything; else {@value Funcall#FALSE}
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		
		this.context = context;
		
		if(!vv.get(0).stringValue(context).equals(getName()))
			throw new JessException(getName(), "called but ValueVector head differs",
					vv.get(0).stringValue(context));
	
		if (getJmt() == null) // to avoid problems with startup
			return Funcall.FALSE;
		
		String label = getName();
		if (vv.size() > 1) {
			Value v = vv.get(1);
			if (v == null)
				label = "(null)";
			else {
				v = v.resolveValue(context);
				label = v.stringValue(context);
			}
		}
		StringBuffer sb = new StringBuffer(label);
		sb.append(OPEN_BRACKET);
		if (vv.size() > 2) {
			for (int i = 2; i < vv.size(); ++i) {
				Value v = vv.get(i);
				if (v == null)
					continue;
				v = v.resolveValue(context);
				if (i > 2)
					sb.append('\n');
				if (v.type() == jess.RU.FACT)
					sb.append(v.factValue(context).toStringWithParens());
				else
					sb.append(v.stringValue(context));
			}
		} else {
			Iterator it = jmt.getRete().listFacts();
			for (int i = 0; it.hasNext(); ++i) {
				Fact fact = (Fact)it.next();
				if (i > 0)
					sb.append('\n');
				sb.append(fact.toStringWithParens());
			}
		}
		sb.append(CLOSE_BRACKET);
		jmt.addWMImage(sb.toString());
		return Funcall.TRUE;
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
	 * Parse a working-memory image created by {@link #call(ValueVector, Context)} into its
	 * label and data fields.
	 * @param wmImage string created by {@link #call(ValueVector, Context)}
	 * @return 2-element array with label in element 0, data in element 1
	 */
	public static String[] parseImage(String wmImage) {
		String[] result = new String[2];
		int endLabelIndex = wmImage.indexOf(OPEN_BRACKET);
		if (endLabelIndex > 0) {
			result[0] = wmImage.substring(0, endLabelIndex);
			result[1] = wmImage.substring(endLabelIndex+OPEN_BRACKET.length(),
					wmImage.length()-CLOSE_BRACKET.length());     // remove "[]"
		} else {
			result[0] = "working-memory-elements";
			result[1] = wmImage;
		}
		return result;
	}

}
