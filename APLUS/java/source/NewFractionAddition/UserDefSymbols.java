/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package NewFractionAddition;

import jess.*;
import edu.cmu.pact.miss.userDef.algebra.*;
import edu.cmu.pact.miss.userDef.topological.*;
import edu.cmu.pact.miss.userDef.topological.table.*;

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

	// System.out.println("UserDefSymbols for Equation Tutor......");

	// Feature predicates
	//
	// rete.addUserfunction( new CanBeSimplified() );
	rete.addUserfunction( new HasCoefficient() );
	rete.addUserfunction( new HasConstTerm() );
	rete.addUserfunction( new HasVarTerm() );
	rete.addUserfunction( new Homogeneous() );
	rete.addUserfunction( new IsFractionTerm() );
	rete.addUserfunction( new IsConstant() );
	rete.addUserfunction( new IsDenominatorOf() );
	rete.addUserfunction( new IsNumeratorOf() );
	rete.addUserfunction( new IsPolynomial() );
	rete.addUserfunction( new IsVariable() );
	rete.addUserfunction( new Monomial() );
	rete.addUserfunction( new NotNull() );
	rete.addUserfunction( new IsSkillAdd() );
        rete.addUserfunction( new IsSkillSubtract() );
        rete.addUserfunction( new IsSkillDivide() );
        rete.addUserfunction( new IsSkillMultiply() );
	//
	// Operators
	//
	rete.addUserfunction( new AddTerm() );
	rete.addUserfunction( new AddTermBy() );
	rete.addUserfunction( new Coefficient() );
	rete.addUserfunction( new CopyTerm() );
	rete.addUserfunction( new Denominator() );
	rete.addUserfunction( new DivTen() );
	rete.addUserfunction( new DivTerm() );
	rete.addUserfunction( new DivTermBy() );
	rete.addUserfunction( new EvalArithmetic() );
	rete.addUserfunction( new FirstTerm() );
	rete.addUserfunction( new FirstVarTerm() );
	rete.addUserfunction( new GCD() );
	rete.addUserfunction( new GetOperand() );
	rete.addUserfunction( new InverseTerm() );
	rete.addUserfunction( new LastConstTerm() );
	rete.addUserfunction( new LastTerm() );
	rete.addUserfunction( new LastVarTerm() );
	rete.addUserfunction( new LCM() );
	rete.addUserfunction( new ModTen() );
	rete.addUserfunction( new MulTerm() );
	rete.addUserfunction( new MulTermBy() );
	rete.addUserfunction( new Numerator() );
	// rete.addUserfunction( new RemoveFirstVarTerm() );
	// rete.addUserfunction( new RemoveLastConstTerm() );
	// rete.addUserfunction( new RemoveLastTerm() );
	rete.addUserfunction( new ReverseSign() );
	rete.addUserfunction( new RipCoefficient() );
	rete.addUserfunction( new SkillAdd() );
	rete.addUserfunction( new SkillClt() );
	rete.addUserfunction( new SkillDivide() );
	rete.addUserfunction( new SkillMultiply() );
	rete.addUserfunction( new SkillRf() );
	rete.addUserfunction( new SkillMt() );
	rete.addUserfunction( new SkillSubtract() );
	rete.addUserfunction( new VarName() );
	// 
	// Tue Oct 04 10:57:59 2005
	// They are too domain specific and ad-hoc
	// 
	// rete.addUserfunction( new CancelDenominator() );
	// rete.addUserfunction( new CancelCoefficient() );
	// rete.addUserfunction( new CancelLastConstTerm() );
	//
	// Buggy rules from operators-bug.txt
	//
	rete.addUserfunction( new AppendVarSymbol() );
	rete.addUserfunction( new ButLastTerm() );
	rete.addUserfunction( new DropSimpleVarSymbol() );
	rete.addUserfunction( new GetConstTermSymbols() );
	rete.addUserfunction( new GetVariableSymbol() );
	rete.addUserfunction( new GetNumSymbolsList() );
	rete.addUserfunction( new GetVarTermSymbols() );
	rete.addUserfunction( new ListAddSymbols() );
	rete.addUserfunction( new ListFirstNegativeFunnyAdd() );
	// 
	//topological constraints
	//
	rete.addUserfunction(new SameTable());
	rete.addUserfunction(new SameColumn());
	rete.addUserfunction(new SameRow());
	rete.addUserfunction(new ConsecutiveColumn());
	rete.addUserfunction(new ConsecutiveRow());
	rete.addUserfunction(new Distinctive());
    }
}
