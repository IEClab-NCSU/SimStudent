package pushnegationinward;

import java.util.Arrays;
import java.util.Vector;

import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.Sentence;

public class _IsConnective extends MyFeaturePredicate {
	private static PLParser parser;

	public _IsConnective() {
		setArity(1);
		setName("is-complex-sentence");
		setArgValueType(new int[] {TYPE_OPERATOR});
		parser = new PLParser();
	}

	public String apply(Vector args) {
		String a1 = args.get(0).toString(); 
		a1 = a1.replaceAll("and", "&");
		a1 = a1.replaceAll("or", "|");
		Sentence sen = parser.parse(a1);
//		System.out.println("isLiteral~~~~~~~~~~~"+a1+"  "+a1.matches("[~]*[A-Za-z]"));
		return (Arrays.asList(Constants.VALID_CONNECTIVES).contains(sen)) ? "T": null;
	}
}


