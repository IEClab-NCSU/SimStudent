/**
 * Created: Dec 18, 2013
 * @author mazda
 * 
 */
package SimStudent2.ProductionSystem;

import java.io.Serializable;
import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * A 
 * 
 * @author mazda
 *
 */
@SuppressWarnings("serial")
public abstract class UserDefWmeRetrievalJessSymbol implements Userfunction, Cloneable, Serializable {

	// The name of the user defined jess function
	private String name;
	public void setName(String name) { this.name = name; }
	@Override
	public String getName() { return this.name; }
	
    // A number of arguments required for the operator:: This is
    // needed even if one could read the arity off the argument list,
    // because at the time when a FeaturePredicate is instantiated by
    // RhsSearchSuccessorFn, no argument is assigned yet but still
    // need to know the arity.
    private int arity;
    public int getArity() { return arity; }
    public void setArity( int arity ) { this.arity = arity; }
	

	@Override
	public Value call(ValueVector jessArgs, Context c) throws JessException {
	
		Value value = null;
		
		Vector<Fact> argv = new Vector<Fact>();
		for (int i = 1; i < jessArgs.size(); i++) {
		    argv.add( jessArgs.get(i).factValue(c) );
		}
		
		String tmpValue = apply(argv, c.getEngine());
		
		if( tmpValue == null )
			value = new Value("FALSE",RU.SYMBOL);
		else
			value = new Value(tmpValue,RU.STRING);
		
		return value;
	}
	
	public abstract String apply(Vector<Fact> args, Rete rete);

}
