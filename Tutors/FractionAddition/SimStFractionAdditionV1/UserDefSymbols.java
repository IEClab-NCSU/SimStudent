/**
 * Describe class UserDefSymbols here.
 *
 *
 * Created: Tue May 31 00:31:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package SimStFractionAdditionV1;

import jess.*;
import edu.cmu.pact.miss.userDef.algebra.*;
import edu.cmu.pact.miss.userDef.topological.*;
import edu.cmu.pact.miss.userDef.topological.table.*;
import edu.cmu.pact.miss.userDef.topological.fractions.*;
import SimStFractionAdditionV1.operators.*;
import SimStFractionAdditionV1.featurePredicates.*;

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
	//rete.addUserfunction( new IsLastConstTermNegative() );
	//rete.addUserfunction( new HasCoefficient() );
	//rete.addUserfunction( new HasConstTerm() );
	//rete.addUserfunction( new HasVarTerm() );
	//rete.addUserfunction( new Homogeneous() );
	//rete.addUserfunction( new IsFractionTerm() );
	//rete.addUserfunction( new IsConstant() );
	//rete.addUserfunction( new IsDenominatorOf() );
	//rete.addUserfunction( new IsNumeratorOf() );
	//rete.addUserfunction( new IsPolynomial() );
	//rete.addUserfunction( new IsVariable() );
	//rete.addUserfunction( new Monomial() );
	rete.addUserfunction( new NotNull() );
	
	//rete.addUserfunction( new IsEmpty() );
	rete.addUserfunction( new IsImproperFraction() );
	rete.addUserfunction( new IsEquivalentFraction() );
	rete.addUserfunction( new IsGoalReduce() );
	rete.addUserfunction( new IsGoalAdd() );
	rete.addUserfunction( new IsGoalSimplify() );
	rete.addUserfunction( new IsGoalComplex() );
	rete.addUserfunction( new IsComplexFractionChunkImproper() );
	rete.addUserfunction( new CanComplexFractionBeSimplified() );
	rete.addUserfunction( new IsComplexFractionChunkProper() );
	rete.addUserfunction( new IsComplexFractionChunkComplex() );
	rete.addUserfunction( new CanBeSimplified() );




	// Operators
	//
	//rete.addUserfunction( new GetVarTerm() );
	//rete.addUserfunction( new GetConstTerm() );
	rete.addUserfunction( new AddTerm() );
	rete.addUserfunction( new AddTermBy() );
	rete.addUserfunction( new Coefficient() );
	rete.addUserfunction( new CopyTerm() );
	rete.addUserfunction( new Denominator() );
	//rete.addUserfunction( new DivTen() );
	rete.addUserfunction( new DivTerm() );
	rete.addUserfunction( new DivTermBy() );
	rete.addUserfunction( new EvalArithmetic() );
	rete.addUserfunction( new FirstTerm() );
	rete.addUserfunction( new FirstVarTerm() );
	rete.addUserfunction( new GCD() );
	//rete.addUserfunction( new GetOperand() );
	//rete.addUserfunction( new InverseTerm() );
	rete.addUserfunction( new LastConstTerm() );
	rete.addUserfunction( new LastTerm() );
	rete.addUserfunction( new LastVarTerm() );
	rete.addUserfunction( new LCM() );
	rete.addUserfunction( new ModTen() );
	rete.addUserfunction( new MulTerm() );
	rete.addUserfunction( new MulTermBy() );
	//rete.addUserfunction( new Numerator() );
	// rete.addUserfunction( new RemoveFirstVarTerm() );
	// rete.addUserfunction( new RemoveLastConstTerm() );
	// rete.addUserfunction( new RemoveLastTerm() );
	//rete.addUserfunction( new ReverseSign() );
	//rete.addUserfunction( new RipCoefficient() );
	//rete.addUserfunction( new SkillAdd() );
	//rete.addUserfunction( new SkillClt() );
	//rete.addUserfunction( new SkillCltOperand() );
	//rete.addUserfunction( new SkillDistribute() );
	//rete.addUserfunction( new AddParentheses() );
	rete.addUserfunction( new SkillDone() );
	//rete.addUserfunction( new SkillDivide() );
	//rete.addUserfunction( new SkillMultiply() );
	//rete.addUserfunction( new SkillRfOperand() );
	//rete.addUserfunction( new SkillMt() );
	//rete.addUserfunction( new SkillSubtract() );
	//rete.addUserfunction( new SubTerm() );
	//rete.addUserfunction( new VarName() );
	// 

	rete.addUserfunction( new DivisionQuotient() );
	rete.addUserfunction( new DivisionRemainder() );
	

	rete.addUserfunction( new SelectReduce() );
	rete.addUserfunction( new SelectAdd() );
	rete.addUserfunction( new SelectSimplify() );
	rete.addUserfunction( new SelectComplex() );

	// 
	//topological constraints
	//

	rete.addUserfunction(new IsConsecutiveRow());
	rete.addUserfunction(new IsSameRow());
	rete.addUserfunction(new AreBothNumerators());
	rete.addUserfunction(new AreBothDenominators());
	rete.addUserfunction(new SameOrder());
	rete.addUserfunction(new SameTable());
	rete.addUserfunction(new SameColumn());
	rete.addUserfunction(new SameRow());
	rete.addUserfunction(new ConsecutiveColumn());
	rete.addUserfunction(new ConsecutiveRow());
	rete.addUserfunction(new Distinctive());

    }
}
