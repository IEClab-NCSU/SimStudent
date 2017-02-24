/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package digt_1_3;

import jess.*;

public class UserDefSymbols implements Userpackage {

    /**
     * Creates a new <code>UserDefSymbols</code> instance.
     *
     */
    public UserDefSymbols() {
    }

    // Implementation of jess.Userpackage

    /**
     * Describe <code>add</code> method here.
     *
     * @param rete a <code>Rete</code> value
     */
    public final void add(final Rete rete) {

	// Feature predicates
	rete.addUserfunction(new IsDisjunction());
	rete.addUserfunction(new IsConjunction());
	rete.addUserfunction(new IsImplication());
	rete.addUserfunction(new IsTrue());
	rete.addUserfunction(new IsFalse());
	//
	// Operators
	rete.addUserfunction(new TruthAssignment_00());
	rete.addUserfunction(new TruthAssignment_01());
	rete.addUserfunction(new TruthAssignment_10());
	rete.addUserfunction(new TruthAssignment_11());
	rete.addUserfunction(new GetInterfaceName());
	//
    //Constraints
	rete.addUserfunction(new SameRow());
	rete.addUserfunction(new SameColumn());
	rete.addUserfunction(new PreviousRow());
    //
    }
}
