/**
 * Created: Dec 19, 2013 6:19:35 AM
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
public class ConsecutiveRow extends TabularPerceptionUserDefJessSymbol {

	public ConsecutiveRow() {
		setArity(2);
		setName("consecutive-row");
	}

	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefWmeRetrievalJessSymbol#apply(java.util.Vector, jess.Rete)
	 */
	@Override
	public String apply(Vector<Fact> args, Rete rete) {
		return consecutiveRow((Fact)args.get(0), (Fact)args.get(1), rete);
	}

}
