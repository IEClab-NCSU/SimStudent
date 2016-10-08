package tracer;

import jess.*;

/**
 * Userfunction that SimStudent automatically creates in production rules.
 * For us, we don't need it, so we add this userfunction so Jess won't complain
 * @author Alex Xiao
 *
 */
public class FoaFunction implements Userfunction {

	@Override
	public Value call(ValueVector arg0, Context arg1) throws JessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "here-is-the-list-of-foas";
	}

}
