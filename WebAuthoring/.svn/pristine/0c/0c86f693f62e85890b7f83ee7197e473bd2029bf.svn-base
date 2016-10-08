package informallogic;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.Rete;

public class PreviousColumn extends WMEConstraintPredicate {

	public PreviousColumn() {
		System.out.println("PreviousColumn constructor");
		setArity(2);
		setName("previous-column");
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
			//System.out.println("name1: "+name1+" name2: "+name2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (    //for biimplication/and/or structure
		      (name1.equals("prob_table_c3") & name2.equals("ans_table_c2"))
			| (name1.equals("prob_table_c4") & name2.equals("ans_table_c3"))
			| (name1.equals("prob_table_c5") & name2.equals("ans_table_c4"))
			| (name1.equals("prob_table_c6") & name2.equals("ans_table_c5"))
			|
			  (name1.equals("ans_table_c3") & name2.equals("ans_table_c2"))
			| (name1.equals("ans_table_c4") & name2.equals("ans_table_c3"))
			| (name1.equals("ans_table_c5") & name2.equals("ans_table_c4"))
			| (name1.equals("ans_table_c6") & name2.equals("ans_table_c5")))
		{			
//			System.out.println("#############################################    #####################"+name1+"  "+name2);

		  return "T";
		}
		return null;
	}

}
