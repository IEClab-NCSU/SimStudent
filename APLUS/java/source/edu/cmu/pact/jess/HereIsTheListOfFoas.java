/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.Vector;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;



/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class HereIsTheListOfFoas extends PredictObservableAction {

    /** Function name, as known to Jess. */
    private static final String HERE_IS_THE_LIST_OF_FOAS = "here-is-the-list-of-foas";


    /**
     * No-argument constructor for use from (load-function).
     */
    public HereIsTheListOfFoas() {
        super();
    }

    /**
     * Constructor connects to current model tracer.
     * @param jmt current model tracer
     */
    public HereIsTheListOfFoas(JessModelTracing jmt) {
        super();

    }

    /**
     * Return the name of this function as registered with Jess.
     * @see jess.Userfunction#getName()
     */
    public String getName() {
        return HERE_IS_THE_LIST_OF_FOAS;
    }


    /**
     * calls jmt.addRuleFoas
     */
    public Value call(ValueVector vv, Context context) throws JessException {
        //trace.out("entered HereIsTheListOfFoas.call()");
        
        this.context = context;

        if(!vv.get(0).stringValue(context).equals(HERE_IS_THE_LIST_OF_FOAS))
            throw new JessException(HERE_IS_THE_LIST_OF_FOAS, "called but ValueVector head differs",
                    vv.get(0).stringValue(context));

        // to allow authors to run this function outside of model tracing
        if (getJmt() == null || !getJmt().isModelTracing())
            return Funcall.TRUE;

        if(vv.size()<=1) {
            throw new JessException(HERE_IS_THE_LIST_OF_FOAS, HERE_IS_THE_LIST_OF_FOAS + " requires at least one argument",
                    MTRete.NOT_SPECIFIED);
        }

        Vector /*of String*/ foas = new Vector();
        for (int i=1; i<vv.size(); i++){
            String foa = vv.get(i).resolveValue(context).stringValue(context);
            foas.add(foa);
        }
        if (trace.getDebugCode("add-negative-tuple")) trace.out("add-negative-tuple", "adding FoA off here_is_the_list_of_foas: " + foas);

        jmt.addRuleFoas(foas);

//        trace.out("HereIsTheListOfFoas.call(): returning Funcall.TRUE");
        return Funcall.TRUE;
    }
}

