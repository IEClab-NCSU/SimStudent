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
import edu.cmu.pact.miss.userDef.oldpredicates.AddTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.AddTermBy;
import edu.cmu.pact.miss.userDef.oldpredicates.CanBeSimplified;
import edu.cmu.pact.miss.userDef.oldpredicates.Coefficient;
import edu.cmu.pact.miss.userDef.oldpredicates.CopyTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.Denominator;
import edu.cmu.pact.miss.userDef.oldpredicates.DivTen;
import edu.cmu.pact.miss.userDef.oldpredicates.DivTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.DivTermBy;
import edu.cmu.pact.miss.userDef.oldpredicates.EvalArithmetic;
import edu.cmu.pact.miss.userDef.oldpredicates.FirstVarTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.GCD;
import edu.cmu.pact.miss.userDef.oldpredicates.HasCoefficient;
import edu.cmu.pact.miss.userDef.oldpredicates.HasConstTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.HasVarTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.Homogeneous;
import edu.cmu.pact.miss.userDef.oldpredicates.InverseTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.IsFractionTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.LCM;
import edu.cmu.pact.miss.userDef.oldpredicates.LastConstTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.LastTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.ModTen;
import edu.cmu.pact.miss.userDef.oldpredicates.Monomial;
import edu.cmu.pact.miss.userDef.oldpredicates.MulTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.MulTermBy;
import edu.cmu.pact.miss.userDef.oldpredicates.NotNull;
import edu.cmu.pact.miss.userDef.oldpredicates.Numerator;
import edu.cmu.pact.miss.userDef.oldpredicates.Polynomial;
import edu.cmu.pact.miss.userDef.oldpredicates.RemoveFirstVarTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.RemoveLastConstTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.RemoveLastTerm;
import edu.cmu.pact.miss.userDef.oldpredicates.ReverseSign;
import edu.cmu.pact.miss.userDef.oldpredicates.RipCoefficient;
import edu.cmu.pact.miss.userDef.oldpredicates.VarTerm;
import edu.cmu.pact.miss.userDef.topological.Distinctive;
import edu.cmu.pact.miss.userDef.topological.table.ConsecutiveColumn;
import edu.cmu.pact.miss.userDef.topological.table.ConsecutiveRow;
import edu.cmu.pact.miss.userDef.topological.table.SameColumn;
import edu.cmu.pact.miss.userDef.topological.table.SameRow;
import edu.cmu.pact.miss.userDef.topological.table.SameTable;
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

	// trace.out("UserDefSymbols for Equation Tutor......");

	// Feature predicates
	rete.addUserfunction( new HasCoefficient() );
	rete.addUserfunction( new VarTerm() );
	rete.addUserfunction( new Monomial() );
	rete.addUserfunction( new Polynomial() );
	rete.addUserfunction( new HasVarTerm() );
	rete.addUserfunction( new HasConstTerm() );
	rete.addUserfunction( new Homogeneous() );
	rete.addUserfunction( new NotNull() );
	rete.addUserfunction( new CanBeSimplified() );
	rete.addUserfunction( new IsFractionTerm() );
	// Operators
	rete.addUserfunction( new CopyTerm() );
	rete.addUserfunction( new Coefficient() );
	rete.addUserfunction( new InverseTerm() );
	rete.addUserfunction( new ReverseSign() );
	rete.addUserfunction( new EvalArithmetic() );
	rete.addUserfunction( new RipCoefficient() );
	rete.addUserfunction( new FirstVarTerm() );
	rete.addUserfunction( new LastTerm() );
	rete.addUserfunction( new LastConstTerm() );
	rete.addUserfunction( new RemoveFirstVarTerm() );
	rete.addUserfunction( new RemoveLastTerm() );
	rete.addUserfunction( new RemoveLastConstTerm() );
	rete.addUserfunction( new Denominator() );
	rete.addUserfunction( new Numerator() );
	rete.addUserfunction( new AddTerm() );
	rete.addUserfunction( new DivTerm() );
	rete.addUserfunction( new MulTerm() );
	rete.addUserfunction( new DivTen() );
	rete.addUserfunction( new ModTen() );
	rete.addUserfunction( new AddTermBy() );
	rete.addUserfunction( new DivTermBy() );
	rete.addUserfunction( new MulTermBy() );
	rete.addUserfunction( new GCD() );
	rete.addUserfunction( new LCM() );
	
	
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
