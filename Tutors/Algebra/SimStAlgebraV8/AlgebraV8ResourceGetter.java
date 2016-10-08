package SimStAlgebraV8;

import java.util.Vector;


import java.util.List;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.ProblemAssessor;
import java.util.Random;
import java.util.Vector;

public class AlgebraV8ResourceGetter {

	public static String EXAMPLES="Examples";
	public static String UNIT_OVERVIEW="Unit Overview";

	String getResource(String problem){
	
	//Vector<String> result=new Vector<String>();
			String resource;
					
			Random randomGenerator = new Random();
			double num1=randomGenerator.nextDouble();
			double num2=randomGenerator.nextDouble();
			    
			if( num1 > num2) 
			    resource=EXAMPLES;
			else
			    resource=UNIT_OVERVIEW;
			    		
			/*
			ProblemAssessor assessor = new AlgebraProblemAssessor();
			String problemType=assessor.classifyProblem(problem);
			result.add(problemType);
			*/
			
							    
		return resource;	    
	}
	
}
