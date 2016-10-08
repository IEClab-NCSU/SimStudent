package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.util.LinkedList;

import jess.Context;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;

public class GetFirstEvent implements Userfunction, Serializable {

	/**	Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static final String GET_FIRST_EVENT = "get-first-event";
	
	private static final String EVENTS_LIST = "modelTracedEvents";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public GetFirstEvent(){
		this(null);
	}
	
	public GetFirstEvent(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(GET_FIRST_EVENT)) {
			throw new JessException(GET_FIRST_EVENT, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		LinkedList events = null;
		String rtnValue = SimStRete.NOT_SPECIFIED;
		
		if(vv.size() > 1) {
			Fact f = vv.get(1).factValue(context);
			if(f != null) {
				events = (LinkedList) f.getSlotValue(EVENTS_LIST).javaObjectValue(context);
			}
		}
		
		if(events != null && events.size() > 0 && context.getEngine() instanceof SimStRete) {
			
			rtnValue = (String) events.getFirst();
			if(rtnValue != SimStRete.NOT_SPECIFIED) {
				Value val = new Value(rtnValue, RU.STRING);
				return val;
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		
		return GET_FIRST_EVENT;
	}

}
