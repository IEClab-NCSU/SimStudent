package edu.cmu.pact.jess;

import java.io.Serializable;

import edu.cmu.pact.Utilities.trace;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * Author access to {@link JessModelTracing#setDefaultSkillCategory(String)},
 * which specifies a skill category to supply where a skill label omits the category.
 */
public class SetDefaultSkillCategory implements Userfunction, Serializable {
	
	/** Function name, as known to Jess. */
	private static final String SET_DEFAULT_SKILL_CATEGORY = "set-default-skill-category";

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@value #SET_DEFAULT_SKILL_CATEGORY}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return SET_DEFAULT_SKILL_CATEGORY;
	}

	/**
	 * Calls {@link JessModelTracing#setDefaultSkillCategory(String)} and
	 * passes its first argument. No-op if {@link #jmt} is null, 1st argument
	 * is missing or empty.
	 * @return prior value from {@link JessModelTracing#setDefaultSkillCategory(String)};
	 *         NIL if no JessModelTracing instance available
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		JessModelTracing jmt = getJmt(context);
		if(jmt == null)
			return Funcall.NIL;
		Value result = new Value(jmt.getDefaultSkillCategory(), RU.STRING);
		if(vv.size() > 1) {
			String v = vv.get(1).stringValue(context);
			if(v != null && (v = v.trim()).length() > 0)
				jmt.setDefaultSkillCategory(v);
		}
		return result;
	}

	/**
	 * Get a reference to the model tracer via {@link MTRete#getJmt()}.
	 * @return {@link MTRete#getJmt()}
	 */
	protected JessModelTracing getJmt(Context context) {
		if (context == null)
			return null;
		if (context.getEngine() instanceof MTRete)
			return ((MTRete) context.getEngine()).getJmt();
		//if(context.getEngine() instanceof SimStRete) 
			//return ((SimStRete)context.getEngine()).getJmt();
		return null;
	}
}
