package edu.cmu.pact.miss.PKLearning;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.FoilData;
import edu.cmu.pact.miss.Instruction;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.SimSt;

public class PKLearner {
	private static Set<String> skills = new HashSet<String>();
	
	/*
	 *	args[0] = Operators File;
	 */
	public static void main(String[] args) throws IOException {
		
        SimSt student = initStudent(args[0], args[1], args[2], args[3], args[4]);
        trace.addDebugCode("miss");
        
        learn(student, args[5]);
  	}
	
	public static SimSt initStudent(String opFile, String fpFile, String wtFile, String wsFile, String isFile) {
		SimSt s = new SimSt();
		s.setOperatorFile(opFile);
		s.setWmeTypeFile(wtFile);
		s.setWmeStructureFile(wsFile);
		s.setInitStateFile(isFile);
				
		s.setStudentInterfaceClass(""); //specifies load package in output?
		
		return s;
	}
	
	 //	Each line of file assumed to have structure: [ skillName, foa, ..., foa, input ]
	public static void learn(SimSt student, String instructionsFile) throws IOException {		
		Scanner infile = new Scanner(new FileReader(instructionsFile));
		
		while(infile.hasNextLine()) {
			String[] tokens = infile.nextLine().split("\t");
				if(tokens[0].charAt(0) != '#') {
				Vector<String> foa = populateFoaVector(tokens);
				
				//skillName = tokens[0]
				Instruction i = new Instruction(tokens[0], foa);
				i.setAction("UpdateTable"); //modified to be public
				
				student.initRhsSearch(i, true);
				student.addInstruction(i);
		        student.generateRulesWithUnorderdFoA(tokens[0]); //modified to be public
	       
		        skills.add(tokens[0]);
		        
		        student.saveProductionRules(2); //saves after each step;
			}
		}
	}
	
	public static Vector<String> populateFoaVector(String[] tokens) {
		Vector<String> v = new Vector<String>();
		String head = "MAIN::cell|commTable1_C", tail = "R1|";
		int lastCol = tokens.length - 1;
		
		v.add(head + lastCol + tail + tokens[lastCol]);
		
		for(int i = 1; i < lastCol; i++) {
			v.add(head + i + tail + tokens[i]);
		}
		
		return v;
	}
}
