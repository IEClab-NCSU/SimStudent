/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package pushnegationinward;

import rmconnective.ReplaceAmpersand;
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

    rete.addUserfunction(new IsNegationOnDisjunction());
    rete.addUserfunction(new IsNegationOnConjunction());
    rete.addUserfunction(new CanMoveNotInward());
    rete.addUserfunction(new IsSimplified());
    // Operators
    rete.addUserfunction(new RM_DoubleNegation());
    rete.addUserfunction(new DemorganizationAnd());
    rete.addUserfunction(new DemorganizationOr());
    rete.addUserfunction(new GetAllButFirst());
	rete.addUserfunction( new GetInterfaceName());
	rete.addUserfunction( new ReplaceAmpersand() );
//  rete.addUserfunction(new _GetLhsBinarySentence());
//  rete.addUserfunction(new _GetRhsBinarySentence());
//  rete.addUserfunction(new _NegateSentence());
//  rete.addUserfunction(new _GetNegatedRhsSentence());
//  rete.addUserfunction(new _GetNegatedLhsSentence());
//	rete.addUserfunction(new _Conjunction());
//  rete.addUserfunction(new _Disjunction());

   	//
    //Constraints
	rete.addUserfunction(new Step1());
	rete.addUserfunction(new Step2());
    }
}
