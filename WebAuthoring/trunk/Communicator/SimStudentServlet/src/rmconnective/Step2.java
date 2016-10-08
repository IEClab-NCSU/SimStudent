package rmconnective;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.JessException;
import jess.Rete;

public class Step2 extends WMEConstraintPredicate {

	public Step2() {
		setArity(2);
		setName("is-step-2");
	}

	@Override
	public String apply(Vector args, Rete rete) {
		Fact f1 = (Fact) args.get(0);
		Fact f2 = (Fact) args.get(1);
		String name1="",name2="";
		try {
			name1 = f1.getSlotValue("name").toString();
			name2 = f2.getSlotValue("name").toString();
		} catch (JessException e) {
			e.printStackTrace();
		}
		//System.out.println("~~~~~~~~~step2:"+name1+"  "+name2);

		if (name1.equals("simplified_field") & name2.equals("rm_connective_field"))  
			return "T";
		return null;
	}

}
