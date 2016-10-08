package edu.cmu.pact.miss.PeerLearning;

import java.util.ArrayList;

/**
 * Class that will be replaced by Sanjay's Tutor
 * @author simstudent
 *
 */
public class SimStudentLMS {

	ArrayList<String> problems;
	int problemGivenCnt=0;
	
	
	public SimStudentLMS(){
		
		problems = new ArrayList<String>();
		
		problems.add("3x=4");
		problems.add("4x=6");
		problems.add("3x+4=7");
		
		
	}
	
	
	
	public String getNextProblem(){
		
		return problems.get(problemGivenCnt++);
	}	
	
}
