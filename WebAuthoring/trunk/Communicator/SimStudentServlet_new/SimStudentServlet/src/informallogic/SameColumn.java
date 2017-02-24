package informallogic;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.Rete;

public class SameColumn extends WMEConstraintPredicate {

	public SameColumn() {
		setArity(2);
		setName("same-column");
	}

	@Override
	public String apply(Vector args, Rete rete) {
		Fact f1 = (Fact) args.get(0);
		Fact f2 = (Fact) args.get(1);
		String name1 = "";
		String name2 = "";
		try {
			name1 = f1.getSlotValue("name").toString();
			name2 = f2.getSlotValue("name").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (  (name1.equals("prob_table_c2") & name2.equals("ans_table_c2")) //for biimplication/and/or structure
		    | (name1.equals("prob_table_c3") & name2.equals("ans_table_c3"))
			| (name1.equals("prob_table_c4") & name2.equals("ans_table_c4"))
			| (name1.equals("prob_table_c5") & name2.equals("ans_table_c5"))
			| (name1.equals("prob_table_c6") & name2.equals("ans_table_c6"))
		   )
			return "T";
		return null;
	}

}
