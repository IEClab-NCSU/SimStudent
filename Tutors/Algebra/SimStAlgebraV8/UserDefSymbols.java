/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package SimStAlgebraV8;

import jess.*;
import SimStAlgebraV8.LucyWeakPK.*;
import edu.cmu.pact.miss.userDef.algebra.*;
import edu.cmu.pact.miss.userDef.algebra.weak.*;
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

	// trace.out("UserDefSymbols for Equation Tutor......");

	// Feature predicates
	//
	// rete.addUserfunction( new CanBeSimplified() );
	rete.addUserfunction( new IsLastConstTermNegative() );
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
        rete.addUserfunction( new IsAVarTerm() );
        rete.addUserfunction( new IsSkillAdd() );
        rete.addUserfunction( new IsSkillSubtract() );
        rete.addUserfunction( new IsSkillDivide() );
        rete.addUserfunction( new IsSkillMultiply() );
		rete.addUserfunction( new HasParentheses() );
	//rete.addUserfunction( new IsPositive() );
	//
	// Operators
	//
	rete.addUserfunction( new GetVarTerm() );
	rete.addUserfunction( new GetConstTerm() );
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
	rete.addUserfunction( new SkillCltOperand() );
	rete.addUserfunction( new SkillDistribute() );
	//rete.addUserfunction( new AddParentheses() );
	rete.addUserfunction( new SkillDone() );
	rete.addUserfunction( new SkillDivide() );
	rete.addUserfunction( new SkillMultiply() );
	rete.addUserfunction( new SkillRfOperand() );
	rete.addUserfunction( new SkillMt() );
	rete.addUserfunction( new SkillSubtract() );
	rete.addUserfunction( new SubTerm() );
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
	// Weak operators for the Error analysis study
	// 
	// rete.addUserfunction( new GetFirstNumberString() );
	// rete.addUserfunction( new GetFirstVarString() );
	// rete.addUserfunction( new CalcNumbers() );

	rete.addUserfunction( new GetFirstIntegerBeforeLetterWithoutSign() );
	rete.addUserfunction( new GetSecondIntegerBeforeLetterWithoutSign() );
	rete.addUserfunction( new GetSecondIntegerWithoutSign() );
	rete.addUserfunction( new GetFirstIntegerWithoutSign() );
	rete.addUserfunction( new GetSecondNearestIntegerWithoutSign() );
	rete.addUserfunction( new GetFirstNearestIntegerWithoutSign() );
	rete.addUserfunction( new GetSecondNearestInteger() );
	rete.addUserfunction( new GetFirstNearestInteger() );
	rete.addUserfunction( new GetFirstIntegerBeforeLetter() );
	rete.addUserfunction( new GetSecondIntegerBeforeLetter() );
	rete.addUserfunction( new GetSecondInteger() );
	rete.addUserfunction( new GetFirstInteger() );

	rete.addUserfunction( new FirstNumber() );
	rete.addUserfunction( new GetFirstNumberWithSign() );
	rete.addUserfunction( new GetFirstVarString() );
	rete.addUserfunction( new Difference() );
	rete.addUserfunction( new GetNumbers() );
	rete.addUserfunction( new GetNumbersAndSigns() );
	rete.addUserfunction( new NumOpNum() );
	rete.addUserfunction( new Sum() );

	
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
