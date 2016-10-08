/**
 * Created: Dec 19, 2013
 * @author mazda
 * 
 */
package TabularPerception;

import java.util.Vector;

import jess.Fact;
import jess.Rete;

/**
 * @author mazda
 *
 */
@SuppressWarnings("serial")
public class SameColumn extends TabularPerceptionUserDefJessSymbol {

	public SameColumn() {
		setArity(2);
		setName("same-column");
	}

	@Override
	public String apply(Vector<Fact> args,Rete rete) {
		
		return sameColumn((Fact)args.get(0), (Fact)args.get(1), rete);
	}

}
