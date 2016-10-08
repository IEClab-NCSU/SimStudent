package rmconnective;

/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

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
	//
	rete.addUserfunction( new IsBiimplicationConnective() );
	rete.addUserfunction( new IsImplicationConnective() );
	rete.addUserfunction( new IsXorConnective() );
	rete.addUserfunction( new IsSimplified() );
	//
	//topological constraints 
	//
	rete.addUserfunction( new Step1() );
	rete.addUserfunction( new Step2() );
	// 
	// 
	//Operators
	//
	rete.addUserfunction(new RM_DoubleNegation() );
	rete.addUserfunction( new RM_Xor() );
	rete.addUserfunction( new RM_Implication() );
	rete.addUserfunction( new RM_Biimplication() );
	rete.addUserfunction( new GetInterfaceName() );
	rete.addUserfunction( new ReplaceAmpersand() );
    }
}
