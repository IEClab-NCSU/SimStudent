package pushnegationinward;

import java.util.Vector;

import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;

public class _ApplyDemorganLawOnOr extends MyFeaturePredicate {
	private static final long serialVersionUID = 1L;
	private static PLParser parser;
	public _ApplyDemorganLawOnOr() {
		setArity(1);
		setName("apply-demorgan-or");
		setReturnValueType(TYPE_COMPLEX_FORMULA);
		setArgValueType(new int[] {TYPE_COMPLEX_FORMULA});
		parser = new PLParser();
	}

	@Override
	public String apply(Vector args) {
		String arg = args.get(0).toString();
		arg = arg.replaceAll("and", "&");
		arg = arg.replaceAll("or", "|");
        
		Sentence sen = parser.parse(arg);
        String result =applyDemorgan(sen);
       
		System.out.println("~~~~~demorgan_Or:"+result);
		return result;
	}


	private static String applyDemorgan(Sentence sen) {
		ComplexSentence a_com, b_com;
		Sentence a, b;
		String result = null;
		a = sen.getSimplerSentence(0).getSimplerSentence(0);
		a_com = new ComplexSentence(Connective.NOT, new Sentence[] { a });
		b = sen.getSimplerSentence(0).getSimplerSentence(1);
		b_com = new ComplexSentence(Connective.NOT, new Sentence[] { b });
		result = a_com.toString() + " & " + b_com.toString();

		// destroy the object
		a = null;b = null;a_com = null;b_com = null;
		return result;
	}
}