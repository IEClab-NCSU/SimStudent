package pushnegationinward;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.JessException;
import jess.Rete;

public class Step1 extends WMEConstraintPredicate {

	public Step1() {
		setArity(2);
		setName("is-step-1");
	}

	@Override
	public String apply(Vector args, Rete rete) {
		Fact f1 = (Fact) args.get(0);
		Fact f2 = (Fact) args.get(1);
		String name1="",name2="";
		try {
			name1 = f1.getSlotValue("name").toString();
			name2 = f2.getSlotValue("name").toString();
			System.out.println("~~~~~~~~~step1:"+name1+"  "+name2);
		} catch (JessException e) {
			e.printStackTrace();
		}

		if (name1.equals("formula_field") &
			name2.equals("step1_field"))
		{
			return "T";
		}
		return null;
	}

}
