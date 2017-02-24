package digt_1_3;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.JessException;
import jess.Rete;

public class PreviousRow extends WMEConstraintPredicate {

	public PreviousRow() {
		setArity(2);
		setName("previous-row");
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
		try
		{
			int r1 = Integer.parseInt(""+name1.charAt(name1.length()-3));
			int c1 = Integer.parseInt(""+name1.charAt(name1.length()-1));
					
			int r2 = Integer.parseInt(""+name2.charAt(name2.length()-3));
			int c2 = Integer.parseInt(""+name2.charAt(name2.length()-1));

			if (c1 == 1 & c2 == 1)
				if (r1 - r2 == -1)
				return "T";
			//System.out.println("~~~~~~~~~~~~~~~~~ "+r1+"   "+r2+"  "+name1+"   "+name2);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
