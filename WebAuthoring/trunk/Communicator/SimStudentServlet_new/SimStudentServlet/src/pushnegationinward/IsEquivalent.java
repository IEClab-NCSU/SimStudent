package pushnegationinward;

import java.util.Vector;

import edu.cmu.pact.miss.FeaturePredicate;

public class IsEquivalent extends FeaturePredicate {

	public IsEquivalent() {
		setArity(2);
		setName("is-equivalent");
	}
	
	@Override
	public String inputMatcher( String exp1, String exp2 ) {
		return (PLParserWrapper.match(exp1, exp2) == true ? "T" : null);
	}

	@Override
	public String apply(Vector arg0) {
		return null;
	}	
}
