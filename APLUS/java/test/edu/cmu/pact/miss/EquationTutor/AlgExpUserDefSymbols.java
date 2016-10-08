/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.EquationTutor;

import jess.Rete;
import jess.Userpackage;
import edu.cmu.pact.miss.userDef.topological.Distinctive;
import edu.cmu.pact.miss.userDef.topological.table.ConsecutiveColumn;
import edu.cmu.pact.miss.userDef.topological.table.ConsecutiveRow;
import edu.cmu.pact.miss.userDef.topological.table.SameColumn;
import edu.cmu.pact.miss.userDef.topological.table.SameRow;
import edu.cmu.pact.miss.userDef.topological.table.SameTable;

public class AlgExpUserDefSymbols implements Userpackage {

    /**
     * Creates a new <code>UserDefSymbols</code> instance.
     *
     */
    public AlgExpUserDefSymbols() {
    }

    // Implementation of jess.Userpackage

    /**
     * Describe <code>add</code> method here.
     *
     * @param rete a <code>Rete</code> value
     */
    public final void add(final Rete rete) {

	// System.out.println("UserDefSymbols for Equation Tutor......");

	// Feature predicates
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.HasCoefficient() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.HasVarTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.Monomial() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.IsPolynomial() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.HasVarTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.HasConstTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.Homogeneous() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.NotNull() );
	// rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.CanBeSimplified() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.IsFractionTerm() );
	// Operators
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.CopyTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.Coefficient() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.InverseTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.ReverseSign() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.EvalArithmetic() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.RipCoefficient() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.FirstVarTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.LastTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.LastConstTerm() );
	/*rete.addUserfunction( new RemoveFirstVarTerm() );
	rete.addUserfunction( new RemoveLastTerm() );
	rete.addUserfunction( new RemoveLastConstTerm() );*/
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.Denominator() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.Numerator() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.AddTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.DivTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.MulTerm() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.DivTen() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.ModTen() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.AddTermBy() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.DivTermBy() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.MulTermBy() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.GCD() );
	rete.addUserfunction( new edu.cmu.pact.miss.userDef.algebra.LCM() );
	
	// 
	// Tue Oct 04 10:57:59 2005
	// They are too domain specific and ad-hoc
	// 
	// rete.addUserfunction( new CancelDenominator() );
	// rete.addUserfunction( new CancelCoefficient() );
	// rete.addUserfunction( new CancelLastConstTerm() );
	//topological constraints
	rete.addUserfunction(new SameTable());
	rete.addUserfunction(new SameColumn());
	rete.addUserfunction(new SameRow());
	rete.addUserfunction(new ConsecutiveColumn());
	rete.addUserfunction(new ConsecutiveRow());
	rete.addUserfunction(new Distinctive());
    }
}
