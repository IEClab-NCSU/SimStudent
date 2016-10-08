package edu.cmu.pact.miss.userDef.algebra.expression.decomposers;

import java.util.Vector;

import edu.cmu.pact.miss.Decomposer;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexTerm;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
import edu.cmu.pact.miss.userDef.algebra.expression.SimpleTerm;

public class AlgExpFactorDecomposer extends Decomposer {

	

	public Vector decompose(String foa) 
	{
		try
		{
			Vector factors=new Vector();
			AlgExp exp=AlgExp.parseExp(foa);
			Vector v=new Vector();
			if(!exp.isTerm())
				return null;
			if(exp.isSimpleTerm())
			{
				SimpleTerm t=(SimpleTerm)exp;
				factors.add(t.getConstant());
				factors.add(t.getVariable());
				
			}
			else
			{
				factors=((ComplexTerm)exp).getFactors();
				
			}
			for(int factor=0; factor<factors.size(); factor++)
				v.add(factors.get(factor).toString());
			return v;
		}
		catch (ExpParseException e)
		{
			e.printStackTrace();
		}
		return null;
		
		
	}

}
