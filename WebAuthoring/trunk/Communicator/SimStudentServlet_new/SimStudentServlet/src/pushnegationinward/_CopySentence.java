package pushnegationinward;

import java.util.Vector;

import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;

public class _CopySentence extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;
	public _CopySentence() {
		setArity(1);
		setName("copy-sentence");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
	}

	@Override
	public String apply(Vector args) {
		return (String) args.get(0);
	}
}