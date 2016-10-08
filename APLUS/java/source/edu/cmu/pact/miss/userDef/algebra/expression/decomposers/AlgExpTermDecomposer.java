package edu.cmu.pact.miss.userDef.algebra.expression.decomposers;

import java.util.Vector;

import edu.cmu.pact.miss.Decomposer;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
import edu.cmu.pact.miss.userDef.algebra.expression.Polynomial;

public class AlgExpTermDecomposer extends Decomposer 
{



	public Vector decompose(String foa) 
	{
		Vector v=new Vector();
		AlgExp exp;
		try
		{
			 exp=AlgExp.parseExp(foa);
			
		}
		catch (ExpParseException e) 
		{
			e.printStackTrace();
			return null;
		}
		if(exp.isPolynomial())
		{
			Polynomial p=(Polynomial)exp;
			Vector terms=p.getAllTerms();
			for(int term=0; term<terms.size(); term++)
				v.add(terms.get(term).toString());
			return v;
		}
		return null;
		
		/*	v.add(foa);
			return v;*/
		 
		
  }

}
