package edu.cmu.pact.jess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;

import jess.Context;
import jess.Defquery;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Funcall;
import jess.HasLHS;
import jess.JessException;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

public class GetCustomFieldsFact implements Userfunction, Serializable {
	
	/** For {@link Serializable}, a long with digits yyyymmddHHMM from the time this class was last edited. **/
	private static final long serialVersionUID = 201310051610L;

	/** String for {@link #getName()}. */
	private static final String NAME = "get-custom-fields-fact";

	/** Deftemplate name for fact bearing custom fields. */
	public static final String CUSTOM_FIELDS = "custom-fields";

	/** Defquery name. */
	public static final String GET_CUSTOM_FIELDS = "get-custom-fields";
	
	/** Defquery text. */
	private static final String DEFQUERY =
			"(defquery "+GET_CUSTOM_FIELDS+
			"  \"Retrieve any facts of type "+CUSTOM_FIELDS+"\""+
			"  ?cf <- ("+CUSTOM_FIELDS+"))";

	/**
	 * @return {@value #NAME}
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * If there's a deftemplate with the name {@value #CUSTOM_FIELDS}, then return an
	 * existing {@link Fact} of that type if any; if none, assert one and return it.
	 * @param vv ignored
	 * @param context
	 * @return result of {@link #get(Rete)}; null (no-op) if no deftemplate
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		Fact fact = get(context.getEngine());
		if(fact == null)
			return Funcall.NIL;
		else
			return new FactIDValue(fact);
	}

	/**
	 * If there's a deftemplate with the name {@value #CUSTOM_FIELDS}, then execute the defquery
	 * {@value #GET_CUSTOM_FIELDS} and return the first fact returned. If no fact is found,
	 * assert one with default values in each slot.
	 * @param rete
	 * @return first fact returned from defquery {@link #GET_CUSTOM_FIELDS}; nil if none 
	 */
	public static Fact get(Rete rete) {
		return get(rete, true);
	}

	/**
	 * If there's a deftemplate with the name {@value #CUSTOM_FIELDS}, then execute the defquery
	 * {@value #GET_CUSTOM_FIELDS} and return the first fact returned.
	 * @param rete
	 * @param create if true, assert a fact if one isn't found
	 * @return first fact returned from defquery {@link #GET_CUSTOM_FIELDS}; null if no deftemplate
	 *         
	 */
	public static Fact get(Rete rete, boolean create) {
		Deftemplate dt = null;
		try {
			HasLHS hasLHS = rete.findDefrule(GET_CUSTOM_FIELDS);
			if(trace.getDebugCode("mt"))
				trace.out("mt", "GetCustomFieldsFact() found "+hasLHS+": deftemplate "+
						rete.findDeftemplate(CUSTOM_FIELDS));
			if(null == (dt = rete.findDeftemplate(CUSTOM_FIELDS)))
				return null;                        // author didn't define our deftemplate
			else if(hasLHS instanceof Defquery)
				;                                   // done: reuse existing defquery
			else if(null != hasLHS)
				throw new IllegalStateException("GetCustomFieldsFact: construct of type "+
						hasLHS.getClass().getSimpleName()+" has same name as defquery "+GET_CUSTOM_FIELDS);
			else {
				Value v = rete.eval(DEFQUERY);
				if(!(Funcall.TRUE.equals(v)))
					throw new IllegalStateException("GetCustomFieldsFact: failed to parse defquery "+
							GET_CUSTOM_FIELDS);
			}
		} catch (Exception e) {
	        String errorMessage = "Error finding deftemplate or defquery for "+GET_CUSTOM_FIELDS+": "+e;
			trace.errStack(errorMessage, e);
			if(rete instanceof MTRete)
				((MTRete)rete).getTextOutput().append("\n").append(errorMessage).append("\n");
		}
		QueryResult qr = null;
		Fact result = null;
		try {
			qr = rete.runQueryStar(GET_CUSTOM_FIELDS, new ValueVector(0));
			while(qr.next()) {
				result = qr.get("cf").factValue(rete.getGlobalContext());  // return 1st fact found
				if(trace.getDebugCode("mt"))
					trace.out("mt", "GetCustomFieldsFact() returning existing fact:\n  "+result);
				return result;
			}
			if(!create)                             // no fact found; doesn't want to create
				return null;
			result = rete.assertFact(new Fact(dt)); // new fact with default slot values 
			if(trace.getDebugCode("mt"))
				trace.out("mt", "GetCustomFieldsFact() returning new fact:\n  "+result);
			return result;
		} catch (JessException je) {
			String errorMessage = "Error finding running defquery "+GET_CUSTOM_FIELDS+" at line "+
	    	        je.getLineNumber()+":\n"+
	    	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	    	        (je.getData() == null ? "" : je.getData());
	        trace.errStack(errorMessage, je);
			if(rete instanceof MTRete)
				((MTRete)rete).getTextOutput().append("\n").append(errorMessage).append("\n");
			return null;
		} finally {
			if(qr != null) qr.close();
		}
	}

	/**
	 * Format a fact into XML. Each slot name will be in its own &lt;custom_field&gt; element.
	 * @param f fact to format
	 * @param ctx
	 * @return string in XML format
	 */
	public static List<Element> toXML(Fact f, Context ctx) {
		if(f == null)
			return null;
		List<Element> result = new ArrayList<Element>();
		Deftemplate dt = f.getDeftemplate();
		for(int i = 0; i < dt.getNSlots(); ++i) {
			String slotName = null;
			Object vObj = null;
			try {
				slotName = dt.getSlotName(i);
				Value v = f.getSlotValue(slotName);
				vObj = Utils.jessValueToJava(v, ctx); 
			} catch(Exception e) {
				trace.errStack(String.format("Error in toXML() on slot[%d] name %s: %s",
						i, slotName, e.toString()), e);
			}

			Element nameElt = (new Element("name"));
			nameElt.addContent(slotName);

			Element valueElt = new Element("value");
			if(vObj == null)
				;  // empty element for null value
			else if(!(vObj instanceof List))
				valueElt.addContent(vObj.toString());
			else {
				List<Object> vList = (List<Object>) vObj;
				int j = 0;
				if(j < vList.size())
					valueElt.addContent(vList.get(j).toString());
				while(++j < vList.size())
					valueElt.addContent(";").addContent(vList.get(j).toString());  // semicolon delimiter
			}

			Element elt = new Element("custom_field");
			elt.addContent(nameElt).addContent(valueElt);
			result.add(elt);
		}
		return result;
	}

	/**
	 * If a fact of type {@value #CUSTOM_FIELDS} is present, retract it. Then, regardless,
	 * assert a new fact.
	 * @param rete
	 * @return new fact asserted; null (no-op) if feature is not in use
	 */
	public static Fact clear(Rete rete) {
		Deftemplate dt = null;
		try {
			dt = rete.findDeftemplate(CUSTOM_FIELDS);
			if(dt == null)
				return null;      // feature not in use
		} catch(JessException je) {
	        String errorMessage = "Error finding deftemplate for "+GET_CUSTOM_FIELDS+": "+je;
			trace.errStack(errorMessage, je);
			if(rete instanceof MTRete)
				((MTRete)rete).getTextOutput().append("\n").append(errorMessage).append("\n");
			return null; 
		}
		try {
			Fact f = get(rete, false);
			if(f != null)
				rete.retract(f);
		} catch(JessException je) {
	        String errorMessage = "Error retracting "+CUSTOM_FIELDS+" fact: "+je;
			trace.errStack(errorMessage, je);
			if(rete instanceof MTRete)
				((MTRete)rete).getTextOutput().append("\n").append(errorMessage).append("\n");
			// fall through to try to assert anyway
		}
		try {
			Fact result = rete.assertFact(new Fact(dt));
			if(trace.getDebugCode("mt"))
				trace.out("mt", "GetCustomFieldsFact.clear() returns new fact:\n  "+result);
			return result;
		} catch(JessException je) {
	        String errorMessage = "Error asserting "+CUSTOM_FIELDS+" fact: "+je;
			trace.errStack(errorMessage, je);
			if(rete instanceof MTRete)
				((MTRete)rete).getTextOutput().append("\n").append(errorMessage).append("\n");
			return null;
		}
	}
}
