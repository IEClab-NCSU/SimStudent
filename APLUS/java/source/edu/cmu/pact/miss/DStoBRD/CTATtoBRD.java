/*
 * Carnegie Mellon Univerity, Human Computer Interaction Institute
 * Copyright 2005
 * All Rights Reserved
 */

package edu.cmu.pact.miss.DStoBRD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Sung-Joo, Noboru 2006, awlee
 * 
 * Given a tab-separated plain text file in "CTAT" log format, 
 * convertCTATtoBRD converts it into "BRD" log format that contains
 * Selection, Action, Input, and Skill name among other things
 * 
 */
public class CTATtoBRD{

    private int numCtatTransactions = 0;
    private void incNumCtatTransactions() { numCtatTransactions++; }
    private void resetNumCtatTransactions() { numCtatTransactions = 0; }
    private int getNumCtatTransactions() { return numCtatTransactions; }
    
    private int numBrdTransactions = 0;
    private void incNumBrdTransactions() { numBrdTransactions++; }
    private void resetNumBrdTransactions() { numBrdTransactions = 0; }
    private int getNumBrdTransactions() { return numBrdTransactions; }
    
    private String[] skillsRequiringInput = { "add", "subtract", "multiply", "divide"
    		, "clt", "distribute", "mt", "rf"};
    // add skills here if brd node should show their input
        
    private static class CtatLogTransaction{
        String id;
        String studentName;
        String problem;
        String skill;
        String selection;
        String input;
        String subgoal;
        String outcome;

        public CtatLogTransaction(String id, String studentName, String problem, 
                String skill, String selection, String input, 
                String subgoal, String outcome){

            this.id = id;
            this.studentName = studentName;
            this.problem = problem;
            this.skill = skill;
            this.selection = selection;
            this.input = input;
            this.subgoal = subgoal;
            this.outcome = outcome;
        }
    }

    private ArrayList /* CtatLogTransaction */ transactionList(String inputFile) throws IOException{

        ArrayList transList = new ArrayList();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));

        String fileLine = in.readLine(); //header of the file
        while((fileLine = in.readLine()) != null){

            incNumCtatTransactions();
            
            /*
            0. id
            1. student 
            2. problem 
            3. skill   
            4. selection       
            5. input   
            6. step    
            7. outcome
            */
            
            String[] tokens = fileLine.split("\t");
            String id = tokens[0];
            String student = tokens[1];
            String problem = tokens[2];
            String skill = tokens[3];
            String selection = tokens[4];
            String input = tokens[5];
            String subgoal = tokens[6];
            String outcome = tokens[7];
            
            /*
            System.out.println(id);
            System.out.println(student);
            System.out.println(problem);
            System.out.println(skill);
            System.out.println(selection);
            System.out.println(input);
            System.out.println(subgoal);
            System.out.println(outcome);
			*/
                        
            if (skillRequiresInput(skill)) {
                input = skill.concat(" " + input);
            } else if (!skill.equalsIgnoreCase("typein") && ! skill.equalsIgnoreCase("auto-typein")) {
                input = skill;
            }
            	
            CtatLogTransaction item = 
                new CtatLogTransaction(id, student, problem, skill, selection, input, subgoal, outcome);
            transList.add(item);
        }

        System.out.println(transList.size() + " transactions found.");

        return transList;
    }

    // Row position for a selection.  Reset to the default values on a new problem name
    int rowLHS, rowRHS, rowSKL;
    void resetRowPositions() {
        rowLHS = 2;
        rowRHS = 2;
        rowSKL = 1;
    }

    void convertCTATtoBRD(String inputFile, String outputFile) throws IOException{

        resetNumCtatTransactions();
        resetNumBrdTransactions();
        
        ArrayList /* CtatLogTransaction */ transactionList = transactionList(inputFile);
        // ArrayList /* String */ probNames = problemList(transactionList);
        FileWriter fout = new FileWriter(new File(outputFile));    	
        BufferedWriter out = new BufferedWriter(fout);
        //String student = "STU_Hrje";

        String header = "id\tstudent_name\tproblem_name\tselection\taction\tinput\tskill\toutcome\n";
        out.write(header);
        
        String oldProblemName = "";
        String skillTemplate = "";
        String selectionTemplate = "commTable1_";
        String action = "UpdateTable";

        for(int i = 0; i < transactionList.size(); i++){
            
            // ArrayList oneProbTransaction = modifyCLActions(transactionList, (String)probNames.get(i));
            //modifyCLActions(transList, student, (String)probNames.get(i));
            
            CtatLogTransaction logTransaction = (CtatLogTransaction)transactionList.get(i);
            String id = logTransaction.id;
            String problemName = logTransaction.problem;
            String student = logTransaction.studentName;
            String selection = selectionTemplate;
            String input = logTransaction.input;
            String outcome = logTransaction.outcome;
            String skill = logTransaction.skill;

            if (!problemName.equalsIgnoreCase(oldProblemName)) {
                resetRowPositions();
                oldProblemName = problemName;
            }
            
            if(logTransaction.selection.equalsIgnoreCase("SKILL") ){
                selection = selection + "C3R" + rowSKL;
                skillTemplate = logTransaction.skill;
                if(outcome.equalsIgnoreCase("OK")){
                    rowSKL++;
                }
                
            } else if(logTransaction.selection.equalsIgnoreCase("LHS")){
                selection = selection + "C1R" + rowLHS;
                skill = skillTemplate + "-" + skill;
                if(outcome.equalsIgnoreCase("OK")){
                    rowLHS++;
                }

            }else if(logTransaction.selection.equalsIgnoreCase("RHS")){
                selection = selection + "C2R" + rowRHS;
                skill = skillTemplate + "-" + skill;
                if(outcome.equalsIgnoreCase("OK")){
                    rowRHS++;
                }
            }
            
            writeToFile(out, id, student, problemName, selection, action, input, skill, outcome );
        }

        out.close();
        System.out.println(getNumCtatTransactions() + " CTAT transactions read.");
        System.out.println(getNumBrdTransactions() + " BRD transactions wrote.");
    }
    
    private void writeToFile(BufferedWriter out, String id, 
            String student, String problem, String selection, String action,
            String input, String skill, String outcome ) throws IOException {
        
        String log = id + "\t"; 
        log += student + "\t";
        log += problem + "\t";
        log += selection + "\t";
        log += action + "\t";
        log += input + "\t";
        log += skill + "\t";
        log += outcome + "\n";

        out.write(log);
        incNumBrdTransactions();
    }
    
    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("Please specify input file and output file names.");
            System.out.println("CTATtoBRD <input_file> <output_rule>");
            System.exit(-1);
        }

        CTATtoBRD converter = new CTATtoBRD();
        try {
            converter.convertCTATtoBRD(args[0], args[1]);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //returns whether this is a type of skill which requires an input
	private boolean skillRequiresInput(String skillName)
	{
		for(int i = 0; i < skillsRequiringInput.length; i++)
		{
			if(skillsRequiringInput[i].equalsIgnoreCase(skillName))
			{
				return true;
			}
		}
		
		return false;
	}
}
//private static class BrdTransaction{
//String studentID;
//String probName;
//String selection;
//String action;
//String input;
//String subskillName;
//String outcome;
//
//public BrdTransaction(String studentID, String probName, String selection,
//      String action, String input, String subskillName, String outcome){
//  this.studentID = studentID;
//  this.probName = probName;
//  this.selection = selection;
//  this.action = action;
//  this.input = input;
//  this.subskillName = subskillName;
//  this.outcome = outcome;
//}
//}


//ArrayList /* String */ problemList(ArrayList /* CtatLogTransaction */ transactionList){
//
//ArrayList /* Sring */ problemList = new ArrayList();
//
//String oldProblemName = "";
//for(int i=0; i < transactionList.size(); i++){
//  CtatLogTransaction item = (CtatLogTransaction)transactionList.get(i);
//  String problemName = item.problem;
//  if( !problemName.equalsIgnoreCase(oldProblemName) ){
//      problemList.add(problemName);
//      oldProblemName= problemName;
//  }
//}
//return problemList;
//}

//public ArrayList /* BrdTransaction */ modifyCLActions(ArrayList logTransactions, String problemName){
//ArrayList /* BrdTransaction */ newList = new ArrayList();
//int rowLHS =2;
//int rowRHS =2;
//int rowSK = 1;
////flag to distinguish skill2 group is in use.
////boolean skill2Group = false;
//// a number to keep track down for LHS and RHS after skill2
////int numSkill2Typein = 0; 
//
//String skill = "skill_init";
//for(int i=0; i< logTransactions.size(); i++){
//  CtatLogTransaction item = (CtatLogTransaction)logTransactions.get(i);
//  BrdTransaction newItem;
//
//  if(item.problem.equalsIgnoreCase(problemName)){
//      //assume the file is in the order of the sequence
//      String selection = "commTable1_C";
//      String action = "UpdateTable";
//
//      if(item.selection.equalsIgnoreCase("LHS")){
//          selection = selection.concat("1R" + rowLHS);
//          item.skill = skill.concat("-" + item.skill);
//          if(item.outcome.equalsIgnoreCase("OK")){
//
//              rowLHS++;
//          }
//
//      }else if(item.selection.equalsIgnoreCase("RHS")){
//          selection = selection.concat("2R" + rowRHS);
//          item.skill = skill.concat("-" + item.skill);
//          if(item.outcome.equalsIgnoreCase("OK")){
//
//              rowRHS++;
//          }
//      }else if(item.selection.equalsIgnoreCase("SKILL") ){
//          selection = selection.concat("3R" + rowSK);
//          if(item.outcome.equalsIgnoreCase("OK")){
//              rowSK++;
//              skill = item.skill;
//          }
//      }else
//          continue;
//
//      //add arbitrary student number
//      newItem = new BrdTransaction(item.studentName , problemName, selection,
//              action, item.input, item.skill, item.outcome);
//
//      //System.out.println(probName + " " + selection + " " + action + " "
//      //              + item.skill);
//      newList.add(newItem);
//  }else
//      continue;
//}
//
//return newList;
//}
