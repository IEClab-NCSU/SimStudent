/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	
 * 	Transforms equations from Datashop files into abstracted form.
 */
package edu.cmu.pact.miss.minerva_3_1;

import edu.cmu.pact.Utilities.trace;

import java.io.*;
import java.util.*;

/*
 * Step SignedNoContext Abs is okay
 * Problem SignedNoContext Abs is buggy
 * stu_ztdot7 
 * 
 * private StepAbstractor s = new StepAbstractor();
 * if(step_Name.indexOf('=') != -1) equationAbstractionAbsolute = s.abstractAbsolute(step_Name);
 * if(step_Name.indexOf('=') != -1) equationAbstractionSimple = s.abstractSimple(step_Name);
 */

public class Minerva3 {
    private Map<String, Problem> problemSet = new HashMap<String, Problem>();

    /*
     * Abstraction Categories: ----------------------- Problem Abstraction
     * Simple Step Abstraction Simple Input Abstraction Simple Problem Absolute
     * Abstraction Problem Context Abstraction Step Absolute Abstraction Step
     * Context Abstraction Input Absolute Abstraction Input Context Abstraction
     * Previous Step Absolute Abstraction Previous Step Context Abstraction
     */
    /*
     * Abstraction Terminology: ------------------------ Absolute --> Assigns
     * numbers to constants from left to right Simple --> Sames as absolute,
     * except without unnecessary negative signs Context --> Takes into account
     * constants throughout whole problem
     */
    String[] headerExtension = { 
            "Problem Signed Abs",
            "Problem Unsigned Abs",
            "Problem Context Abs",
            "Problem Side Signed Abs",
            "Problem Side UnSigned Abs",
            "Problem Side Context Abs",
            "Step Signed Abs",
            "Step Unsigned Abs",
            "Step Signed Context Abs",
            "Step Unsigned Context Abs",
            "Step Side Signed Abs",
            "Step Side Unsigned Abs",
            "Step Side Signed Context Abs",
            "Step Side Unsigned Context Abs"
            // "Step SignedContext Abs",
            // "Step NoSignContext Abs"
    };
    /*
     * "Step SignedNoContext Abs", "Step NoSignNoContext Abs",
     * "Step SignedContext Abs", "Step NoSign Context Abs"
     */
    /*
     * "Problem Abstraction Simple", "Step Abstraction Simple",
     * "Input Abstraction Simple", "Problem Absolute Abstraction",
     * "Problem Context Abstraction", "Step Absolute Abstraction",
     * "Step Context Abstraction", "Input Absolute Abstraction",
     * "Input Context Abstraction", "Previous Step Absolute Abstraction",
     * "Previous Step Context Abstraction"
     */

    /*
     * Header for SE CWCTC 2009 (CL)
     * 
     * Sample Name Anon Student Id Session Id Time Time Zone Duration (sec)
     * Student Response Type Student Response Subtype Tutor Response Type Tutor
     * Response Subtype Level(Unit) Level(Section) Problem Name Step Name
     * Attempt At Step Outcome Selection Action Input Feedback Text Feedback
     * Classification Help Level Total # Hints Condition Name Condition Type
     * KC(Default) KC Type KC Category(Default) KC(Single-KC) School KC
     * Category(Single-KC) Class
     */

    private void processFile(String inputFile, String outputFile)
            throws Exception {
        Scanner infile = new Scanner(new FileReader(inputFile));
        PrintWriter outfile = new PrintWriter(new FileWriter(outputFile));

        // creates heading line, adding categories for abstractions
        String header = infile.nextLine();
        String outputHeader = header;
        
        for (String column : headerExtension) {
            outputHeader += "\t" + column;
        }
        outfile.println(outputHeader);

        // crawls through all steps
        // abstractions are calculated when creating new Problem/Step objects
        while (infile.hasNextLine()) {
            
            String inputLine = infile.nextLine();
            String outputLine = inputLine;
            
            InputCapsule capsule = new InputCapsule(inputLine, header);
            // trace.out("inputLine = " + capsule);

            String problemName = capsule.getValue("Problem Name");
            // trace.out("problemName: " + problemName);
            // stores new problem in map if not previously detected

            if (isValidEquation(problemName)) {
                
                // Create and archive a problem entity
                if (!problemSet.containsKey(problemName)) {
                    problemSet.put(problemName, new Problem(problemName));
                }
                Problem inputProblem = problemSet.get(problemName);

                String stepName = capsule.getValue("Step Name");
                /*
                String attemptAtStep = capsule.getValue("Attempt At Step");
                String outcome = capsule.getValue("Outcome");
                String action = capsule.getValue("Action");
                String input = capsule.getValue("Input");
                // String signContextStepAbs = "";
                // String noSignContextStepAbs = "";
                String feedbackClassification = capsule.getValue("Feedback Classification");
                */
                Step inputStep = new Step(stepName);
                /*
                Step inputStep = new Step(stepName, attemptAtStep, outcome, action, input, feedbackClassification);
                inputStep.abstractionConstantsWithSign(problemName);
                inputStep.abstractionConstantsWithNoSign(problemName);
                inputProblem.addStep(inputStep);

                if (stepName.indexOf('=') != -1)
                    signContextStepAbs = inputStep.signedContextStepAbstraction(stepName);
                if (stepName.indexOf('=') != -1)
                    noSignContextStepAbs = inputStep.noSignContextStepAbstraction(stepName);
                */

                outputLine += "\t"
                    + inputProblem.getSignedAbstraction() + "\t"
                    + inputProblem.getUnsignedAbstraction() + "\t"
                    + inputProblem.getAbstractionContext()+ "\t"
                    + getSideAbstraction(inputProblem.getSignedAbstraction()) + "\t"
                    + getSideAbstraction(inputProblem.getUnsignedAbstraction()) + "\t"
                    //+ getSideAbstraction(inputProblem.getAbstractionContext()) + "\t"
                    + inputProblem.getSideAbstractionContext(reverseSide(inputProblem.getName())) + "\t"
                    + inputStep.getSignedAbstraction() + "\t"
                    + inputStep.getUnsignedAbstraction() + "\t"
                    + inputStep.getSignedInputAbstractionContext() +"\t"
                    + inputStep.getUnsignedInputAbstractionContext()+ "\t"
                    + getSideAbstraction(inputStep.getSignedAbstraction()) + "\t"
                    + getSideAbstraction(inputStep.getUnsignedAbstraction())+"\t"
                    + inputStep.getSignedSideAbstractionContext(reverseSide(inputStep.getStep_Name()))+ "\t"                    
                    + inputStep.getUnsignedSideAbstractionContext(reverseSide(inputStep.getStep_Name()));
                
                //+ getSideAbstraction(inputStep.getUnsignedInputAbstractionContext());
                 
                	
            }

            // prints abstractions to output file; order should map heading
            // line
            outfile.println(outputLine);
        }

        // outfile.println(inputFile);
        outfile.close();
    }

    
    //HashMap<String, String> sideAbstraction = new HashMap<String, String>();

    private String getSideAbstraction(String expression) {
      /*  String abstExp = sideAbstraction.get(expression);
        trace.out(abstExp);
        if (abstExp == null) {
            abstExp = sideAbstraction.get(reverseSide(expression));
        }
        if (abstExp == null) {        	
            sideAbstraction.put(expression, expression);
            abstExp = expression; 
        }
        return abstExp;*/
    	return reverseSide(expression);
    	
    }
    
    private String reverseSide(String expression) {
        
        String reverseSide;
        int idx = 0;
        if (expression != null) {
        	
            idx = expression.indexOf("=");
            //trace.out("assigning index"+ idx);
        }
        /*
        
        if (idx > 0) {
        	
            String lhs = expression.substring(0, idx);
            String rhs = expression.substring(idx + 1);
            reverseSide = rhs + "=" + lhs;
        } else {
        	
            reverseSide = expression;
        }
        */
        if(idx<=0){        	
        	return expression;
        }
        
        String lhs = expression.substring(0, idx);
        String rhs = expression.substring(idx + 1);
        /*if(rhs.contains("v")){
        	if(lhs.contains("v")){
        		return expression;
        	}
        	
        	reverseSide = rhs + "=" + lhs;
        	return reverseSide;
        }*/
        if(lhs.matches("[0-9]+")){
        	return (rhs+"="+lhs);
        }
        
        return expression;
    }

    private boolean isValidEquation(String expression) {
        return expression.indexOf('=') != -1;
    }

    public static void main(String[] Args) throws Exception {
        // processFile([Input File], [Output File]);
        // processFile("exportData94_w.txt", "data94_output.Simplified.txt");
        String inputFile = Args[0];
        String outputFile = Args[1];
        trace.out("Minerva V3: InputFile " + inputFile
                + ", OutputFile: " + outputFile);
        trace.out("Current dir: " + new File(".").getAbsolutePath());

        new Minerva3().processFile(inputFile, outputFile);
    }

}