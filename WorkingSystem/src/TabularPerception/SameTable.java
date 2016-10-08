/**
 * Created: Dec 17, 2013
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
public class SameTable extends TabularPerceptionUserDefJessSymbol {

	public SameTable() {
		setArity(2);
		setName("same-table");
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	@Override
	public String apply(Vector<Fact> args, Rete rete) {

		return sameTable((Fact)args.get(0),(Fact)args.get(1),rete);
	}

}
