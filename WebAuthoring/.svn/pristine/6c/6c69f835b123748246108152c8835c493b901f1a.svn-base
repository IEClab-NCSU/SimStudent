/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package informallogic;

import jess.*;
//import edu.cmu.pact.miss.userDef.algebra.*;
//import edu.cmu.pact.miss.userDef.topological.*;
//import edu.cmu.pact.miss.userDef.topological.table.*;

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
	 * @param rete
	 *            a <code>Rete</code> value
	 */
	public final void add(final Rete rete) {
		// CDM tutor operators
		rete.addUserfunction(new Copy());
		rete.addUserfunction(new BiimplicationConnective());
		rete.addUserfunction(new ConjunctionConnective());
		rete.addUserfunction(new DisjunctionConnective());
		rete.addUserfunction(new ImplicationConnective());
		rete.addUserfunction(new GetInterfaceName());
		// CDM feature predicates
		rete.addUserfunction(new IsAtomicProposition());
		rete.addUserfunction(new IsBiimplicationConnective());
		rete.addUserfunction(new IsConjunctionConnective());
		rete.addUserfunction(new IsDisjunctionConnective());
		rete.addUserfunction(new IsImplicationConnective());		
		// CDM constraints
		rete.addUserfunction(new SameColumn());
		rete.addUserfunction(new PreviousColumn());
		
	}
}
