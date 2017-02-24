package pushnegationinward;

import java.util.Vector;

import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;

public class _CopyOperator extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;
	public _CopyOperator() {
		setArity(1);
		setName("copy-operator");
		setReturnValueType(TYPE_OPERATOR);
		setArgValueType(new int[] {TYPE_OPERATOR});
	}

	@Override
	public String apply(Vector args) {
		return (String) args.get(0);
	}
}