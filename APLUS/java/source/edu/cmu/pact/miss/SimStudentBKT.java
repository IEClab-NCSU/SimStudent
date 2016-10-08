package edu.cmu.pact.miss;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimStudentBKT {

	
	
	public SimStudentBKT(SimSt simst){
			
	
		
	}

	
	public boolean areSkillsMastered(){
		return false;
	}
	
	public void updateSkills(){
		
	}
	
	public Map<String, Integer> getSkillMastery(){

   		Map<String, Integer> skills = new LinkedHashMap<String, Integer>();
	
   		skills.put("Know how to add", 34);
   		skills.put("Know when to add", 37);

   		skills.put("Know how to subtract", 83);
   		skills.put("Know when to subtract", 12);
	
   		skills.put("Know how to multiply", 43);
   		skills.put("Know when to multiply", 62);
		
   		skills.put("Know how to divide", 33);
   		skills.put("Know when to divide", 92);
	
   		skills.put("Know when a problem is done", 92);
	
   		return skills;
	}
	
}
