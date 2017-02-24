package digt_1_3;

import java.util.Vector;

import edu.cmu.pact.miss.WMEConstraintPredicate;
import jess.Fact;
import jess.JessException;
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
		String name1="",name2="";
		try {
			name1 = f1.getSlotValue("name").toString();
			name2 = f2.getSlotValue("name").toString();
		} catch (JessException e) {
			e.printStackTrace();
		}
		try{
		//System.out.println("@@@sameColumn.java:  "+ (name1.charAt(name1.length()-1) == name2.charAt(name2.length()-1))+"  "+name1+"  "+name2);
		if (name1.charAt(name1.length()-1) == name2.charAt(name2.length()-1)) //char that determine the column number
			return "T";
		}catch(Exception e)
		{e.printStackTrace(); };
		return null;
	}

}
