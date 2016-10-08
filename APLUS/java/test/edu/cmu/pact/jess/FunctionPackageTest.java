/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Context;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class FunctionPackageTest extends TestCase {

    public static Test suite() {
        return new TestSuite(FunctionPackageTest.class);
    }
    
    public void testFunctions() {
    	String thisPkg = getClass().getPackage().getName();
    	String[] userfunctions = {
    			thisPkg+".ConstructMessage",	
    			thisPkg+".PredictObservableAction",
    			thisPkg+".TestSAI",
    			"java.util.ArrayList",          // not a Userfunction
    			thisPkg+".SomeUnknownFunction"	// not even a class
    	};
    	MTRete rete = new MTRete();
    	JessModelTracing jmt = new JessModelTracing(rete, null);
    	FunctionPackage fp = new FunctionPackage(userfunctions, jmt);
    	assertEquals("size() shows all but 2 functions loaded", userfunctions.length-2, fp.size()); 
    	rete.addUserpackage(fp);
    	String[] msgs = { "1 one", "2 two", "3 three" };
    	String program = "(construct-message ["+msgs[0]+"] ["+msgs[1]+"] ["+msgs[2]+"])";
    	Context ctx = rete.getGlobalContext();
    	try {
    		Value v = rete.eval(program);
    		ValueVector vv = v.listValue(ctx);
    		assertEquals(program+" result size", msgs.length+1, vv.size()); // +1: empty msg at end
    		for (int i = 0; i < vv.size(); ++i) {
    			String msg = vv.get(i).resolveValue(ctx).stringValue(ctx);
    			if (msg == null || msg.length() < 1)   // empty msg marks end
    				break;
    			assertEquals("construct-message result["+i+"]", msgs[i], msg);
    		}
    	} catch (JessException je) {
    		fail("error on "+program+": "+je);
    	}
    }
}
