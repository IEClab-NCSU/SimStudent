package edu.cmu.pact.jess;

import java.io.Serializable;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

public class HereIsTheListOfFoas_SimStSolver implements Userfunction, Serializable{

	/** Function name, as known to Jess. */
    private static final String HERE_IS_THE_LIST_OF_FOAS = "here-is-the-list-of-foas";


    public HereIsTheListOfFoas_SimStSolver() {
        super();
    }
    
    protected transient Context context;

	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		//void method
		
        return Funcall.TRUE;
    }

	 /**
     * Return the name of this function as registered with Jess.
     * @see jess.Userfunction#getName()
     */
    public String getName() {
        return HERE_IS_THE_LIST_OF_FOAS;
    }

    
}
