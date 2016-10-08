package edu.cmu.pact.miss.userDef.algebra.expression.test;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class AlgExpParseTest 
{
	private static String[]tests=
	{   "2/3",
		"36*(90/(36x)+1)",
		"1/-2","(-1)","2xy","x/y/z","3x-2y","9","5.32","x","(x+3)","x+3+x","1/3","x","5x","-5x","x+3","x*y","x/y","1/3/1/4","xy","wxyz","5(x+3)","5*(x+3)","3x+4",
		"(x+2)+(3x+4)","5(x+3)+2(x+3)","x+3x+4","(x+2)(x+4)(x+3)","-(x+3)","-x","-3x+4x","3x+2y","(x-3)","x-y","x-3y","-(x+3)(x+3)-(x+4)",
		"3+x/5","3x-3-2x","x/7","2x/7","2/7x","2/(7x)","(57x+50)/6","1/x","-6x-12x-18","(2x+4)/(3x)","-(2x+4)/(3x)","5x+7y+4z+2x+2z+4y+3x","x+3(3x+x+1)","1/2*(2x+2)+4(3x+8)","5.532x",
		"5.3x+7x+3","5.5x/5.5","3.6+5.5x-2.2y","x/6","2x/6","(2x)/6","2(x+3)/6","(-x)/(-1)", "(x+3)+(x+2)","(-x)/(-1)","2x+3x+6"
		
        
	};
	public static void main(String[] args)
	{
		int  numTests=0;
		 int numFailed=0;
		 System.out.println("Algebra Exp Parse Test");
		 for(int testnum=0; testnum<tests.length; testnum++)
		 {
			
			 try
			 {
			 System.out.print(tests[testnum]);
			 AlgExp result=AlgExp.parseExp(tests[testnum]);
			 System.out.println(" "+result.getClass()+" "+result.parseRep());
			 }
			 catch(ExpParseException e)
			 {
				 e.printStackTrace();
				 numFailed++;
			 }
			 
			 numTests++;
			 }
				 
				 
			 
			 System.out.println("Num failed: "+numFailed);
			 System.out.println("Num tests: "+numTests);
	}
	
	
	
}
