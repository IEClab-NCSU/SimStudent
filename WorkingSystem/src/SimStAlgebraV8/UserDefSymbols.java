/**
 * Created: Dec 16, 2013
 * @author Noboru Matsuda
 * 
 * (c) Noboru Matsuda 2013-2014
 */
package SimStAlgebraV8;

import jess.Rete;
import jess.Userpackage;
import SimStAlgebraV8.userDefSymbols.*;
import TabularPerception.*;

/**
 * @author mazda
 *
 */
public class UserDefSymbols implements Userpackage {

	/* (non-Javadoc)
	 * @see jess.Userpackage#add(jess.Rete)
	 */
	@Override
	public void add(Rete rete) {
		
		// 
		// Topological constraints used for perceptual learning in WME retrieval
		// They must inherit UserDefWmeRetrievalJessSymbol
		//
		rete.addUserfunction(new SameTable());
		rete.addUserfunction(new SameColumn());
		rete.addUserfunction(new SameRow());
		rete.addUserfunction(new ConsecutiveColumn());
		rete.addUserfunction(new ConsecutiveRow());
		rete.addUserfunction(new Distinctive());

		//
		// feature predicates (LHS) 
		// They must inherit UserDefJessSymbol
		// 
		rete.addUserfunction(new HasVarTerm());
		rete.addUserfunction(new HasCoefficient());
		rete.addUserfunction(new IsLastConstTermNegative());
		rete.addUserfunction(new IsHomogeneous());

		rete.addUserfunction(new IsSkillAdd());
		rete.addUserfunction(new IsSkillSubtract());
		rete.addUserfunction(new IsSkillMultiply());
		rete.addUserfunction(new IsSkillDivide());
		
		//
		// Operators (RHS)
		// They must inherit UserDefJessSymbol
		// 
		rete.addUserfunction(new SkillAdd());
		rete.addUserfunction(new SkillSubtract());
		rete.addUserfunction(new SkillMultiply());
		rete.addUserfunction(new SkillDivide());
		
		rete.addUserfunction(new AddTerm());
		rete.addUserfunction(new SubtractTerm());
		rete.addUserfunction(new MultiplyTerm());
		rete.addUserfunction(new DivTerm());

		rete.addUserfunction(new GetConstTerm());
		rete.addUserfunction(new GetFirstIntegerWithoutSign());
		rete.addUserfunction(new GetOperand());
	}

}
