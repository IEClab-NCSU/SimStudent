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
public class SameRow extends TabularPerceptionUserDefJessSymbol {

	public SameRow() {
		
		setName("same-row");
		setArity(2);
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefWmeRetrievalJessSymbol#apply(java.util.Vector, jess.Rete)
	 */
	@Override
	public String apply(Vector<Fact> args, Rete rete) {
		return sameRow((Fact)args.get(0), (Fact)args.get(1), rete);
	}
}
