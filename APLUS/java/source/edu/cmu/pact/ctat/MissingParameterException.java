package edu.cmu.pact.ctat;
import java.util.Vector;

public class MissingParameterException extends CommException {
	private Vector parametersParsed=null;
	
	public MissingParameterException(){super();};

	public MissingParameterException(String s){super(s);};

	public MissingParameterException(String parm,Vector alreadyParsed) {
		super(parm);
		parametersParsed = alreadyParsed;
	}
	
	public String getMessage() {
		if (parametersParsed == null)
			return "Required parameter '"+super.getMessage()+"' is missing";
		else
			return "Required parameter '"+super.getMessage()+"' is missing, parameters parsed: "+parametersParsed;
	}
}
