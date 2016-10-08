package tracer;

import tracer.MTException.MTExceptionType;
import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * Parent class of Jess userfunctions used by the model tracer. Provides
 * activation and automatically throw exception capabilities for every
 * userfunction (e.g. when searching for a hint, buggy userfunctions
 * should automatically throw an exception, since there can't be hints
 * for wrong production rules)
 * @author Alex Xiao
 *
 */


public abstract class MTUserFunction implements Userfunction {
	protected boolean on = false;	
	private boolean autoThrowException = false;

	
	public boolean isAutoThrowException() {
		return autoThrowException;
	}

	public void setAutoThrowException(boolean autoThrowException) {
		this.autoThrowException = autoThrowException;
	}

	public void activate(boolean on) {
		this.on = on;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException, MTException {
		if (this.autoThrowException) {
			throw new MTException(MTExceptionType.AUTO, "");
		}
		return null;
	}
	
	protected String getStringFromValue(Value v, Context c) throws JessException {
		v = v.resolveValue(c);
		switch(v.type()) {
		case RU.FACT:
			return v.factValue(c).getSlotValue("name").stringValue(c);
		case RU.STRING:
			return v.stringValue(c);
		case RU.SYMBOL:
			return v.symbolValue(c);
		case RU.INTEGER:
			return v.intValue(c) + "";
		case RU.FLOAT:
			return v.floatValue(c) + "";
		case RU.LONG:
			return v.longValue(c) + "";
		case RU.VARIABLE:
			return v.variableValue(c) + "";
		default:
			return "";
		}
	}
}
