package edu.cmu.pact.miss.DStoBRD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.*;

import edu.cmu.pact.miss.userDef.algebra.IsEquivalent;

/**
 * @author mazda, awlee
 *
 * Given a plain text fine exported from DataShop, this program converts it 
 * into "CTAT" format where student name, problem name, skill name, selection, action,
 * and outcome are shown
 * 
 */
public class DStoCTAT {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Field
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    private final String LHS_SELECTION = "LHS";
    private final String RHS_SELECTION = "RHS";
    private final String OK_OUTCOME = "OK";
    private final String AUTOFILL_SKILL = "auto-typein";
    private final String HINT_SKILL = "hint";

    private String[] SKILL_NAMES = {
            "add", "aproot", "clt", "distribute", "divide", "expon", 
            "fact", "ivm", "mt", "multiply", "rds", "rf", "subtract",
            "left", "right"
    };
    
    private Vector /* String */ skillNames;
    Vector getSkillNames() {
        if (skillNames == null) {
            skillNames = new Vector();
            for (int i = 0; i < SKILL_NAMES.length; i++) {
                skillNames.add(SKILL_NAMES[i]);
            }
        }
        return skillNames;
    }
    
    private int numDsTransactions = 0;
    private void incNumDsTransactions() { numDsTransactions++; }
    private int getNumDsTransactions() { return numDsTransactions; }
    private void resetNumDsTransactions() { numDsTransactions = 0; }
    
    private int numCtatTransactions = 0;
    private void incNumCtatTransactions() { numCtatTransactions++; }
    private int getNumCtatTransactions() { return numCtatTransactions; }
    private void resetNumCtatTransactions() { numCtatTransactions = 0; }
    
    private int typeinWritten = 0;
      
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public DStoCTAT() {}
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Class methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    BufferedWriter openOutputFile(String ctatFileName) throws IOException {
        FileWriter fout = new FileWriter(new File(ctatFileName));
        BufferedWriter out = new BufferedWriter(fout);
        
        String header = "id\tstudent\tproblem\tskill\tselection\tinput\tstep\toutcome\n";
        out.write(header);
        
        return out; 
    }
    
    BufferedReader openInputFile(String dsFileName) throws IOException {
        return new BufferedReader(new FileReader(dsFileName));
    }

    void convertDStoCTAT( String dsFile, String ctatFile ) throws IOException {

        Scanner in = new Scanner(new FileReader(dsFile));
        BufferedWriter out = openOutputFile(ctatFile);
        
        CtatTransaction previousTransaction = null;
        resetFirstTransactionInProblem();
        resetNumCtatTransactions();
        resetNumDsTransactions();
        
        Vector /* CtatTransaction */ incompletes = new Vector();	
        
        // Skip the first line, which is a header
        String dsReadIn = in.nextLine(); 
    	dsReadIn = in.nextLine();
    	incNumDsTransactions();
    	
    	while(in.hasNext()){
            CtatTransaction ctatTransaction = convertDStoCtatTransaction(dsReadIn);

            ArrayList<CtatTransaction> problemList = new ArrayList<CtatTransaction>();
            problemList.add(ctatTransaction);
            String problemName = ctatTransaction.problem;
            
            //imports all steps of the problem into a list
            while(in.hasNext())
            {
            	dsReadIn = in.nextLine();
            	incNumDsTransactions();
            	ctatTransaction = convertDStoCtatTransaction(dsReadIn);
            	
            	if(problemName.equals(ctatTransaction.problem))
            	{
            		problemList.add(ctatTransaction);
            	}
            	else
            	{
            		break;
            	}
            }
            
            if(hasManualTypein(problemList))//if has manual typein steps
            {
            	CtatTransaction manualTransaction = null;
            	boolean first = true;
            	
            	for(int x = 0; x < problemList.size(); x++)
            	{
            		CtatTransaction currentTransaction = problemList.get(x);
            		
            		if (first) {
                    	resetFirstTransactionInProblem();
                        previousTransaction = null;
                        first = false;
                    } else {
                        denyFirstTransactionInProblem();
                    }

                    if (currentTransaction.hasSelection()) {
                        
                        // If the selection was filled (i.e., the transaction is about non-typein"
                        // then use its "step-name", which represents a current equation, and 
                        // identify the selections of the queued transactions...
                        if (updateSelection(currentTransaction, incompletes)) {
                        		writeCtatTransactionToFile(out, incompletes);
                        } else {
                            incompletes.clear();
                        }
                        
                        if(nextIsManualTypein(problemList, x))
                        {
                    		if(previousTransaction != null && !previousWasManualTypein(problemList, x))
                    		{
                    			String equation = getStepEquation(currentTransaction); 
                        		writeAutoTypeinTransactionToFile(out, equation, previousTransaction, incompletes);
                    		}
                    		
                        	manualTransaction = currentTransaction;
                        }
                        else if(currentTransaction.skill.equals("left") || currentTransaction.skill.equals("right"))
                        {
                    		if(manualTransaction != null)
                    		{
                    			writeTypeinTransactionToFile(out, currentTransaction, manualTransaction.skill);
                    		}
                        }
                        else if (previousTransaction != null && !previousTransaction.skill.equals("left") && !previousTransaction.skill.equals("right"))
                        {
                        	if(manualTransaction == null || !manualTransaction.skill.equals(previousTransaction.skill))
                        	{
                        		String equation = getStepEquation(currentTransaction); 
                        		writeAutoTypeinTransactionToFile(out, equation, previousTransaction, incompletes);
                        	}
                        }
                        
                        if(!currentTransaction.skill.equals("left") && !currentTransaction.skill.equals("right"))
                        {
                    		writeCtatTransactionToFile(out, currentTransaction);
                    	}
                    	
                        incompletes.clear();
                    }
                    
                    previousTransaction = currentTransaction;  
            	}
            }
            else //if problem does not contain a manual typein, generate as in previous version
            {
            	boolean first = true;                
            	Iterator<CtatTransaction> itr = problemList.iterator();
            	
            	while(itr.hasNext())
            	{
            		CtatTransaction currentTransaction = itr.next();
            		
            		if (first) {
                    	resetFirstTransactionInProblem();
                        previousTransaction = null;
                        first = false;
                    } else {
                        denyFirstTransactionInProblem();
                    }

                    // System.out.println("==>> " + currentTransaction);
                    if (currentTransaction.hasSelection()) {
                        
                        // If the selection was filled (i.e., the transaction is about non-typein"
                        // then use its "step-name", which represents a current equation, and 
                        // identify the selections of the queued transactions...
                        if (updateSelection(currentTransaction, incompletes)) {
                            // System.out.println("Writing incompletes...");
                        		writeCtatTransactionToFile(out, incompletes);
                        } else {
                            //System.out.println("= = = = = = = = = = = = = = = = = = = = = = = =");
                            //System.out.println("A complete CTAT transaction " + currentTransaction);
                            //System.out.println("couldn't resolve incompletes in the queue:");
                            //for (int i = 0; i < incompletes.size(); i++) {
                            //    System.out.println((CtatTransaction)incompletes.get(i));
                            //}
                            //System.out.println("clearing the incompletes to invoke auto-typein");
                            incompletes.clear();
                        }
                        // If there is a step(s) that was filled automatically by the Tutor, 
                        // we should create a transaction showing that activity as "auto-typein"
                        if (previousTransaction != null) {
                        		String equation = getStepEquation(currentTransaction); 
                               writeAutoTypeinTransactionToFile(out, equation, previousTransaction, incompletes);
                        }
                        
                        // ... then write the transaction in to the file
                        writeCtatTransactionToFile(out, currentTransaction);

                        incompletes.clear();

                    } else if (currentTransaction.hasSkill()){
                        
                        // If the selection was left blank (i.e., the transaction is about "typein"
                        // then set it aside until the next non-typein action is read 
                        // (where the equation can be read hence the selections of the queued typein's 
                        // can be determined).
                        incompletes.add(currentTransaction);
                    } else {
                	// System.out.println("No skill: " + ctatTransaction);
                    }
                    
                    previousTransaction = currentTransaction;  
            	}
            }           
        }
        
        in.close();
        out.close();
        System.out.println(getNumDsTransactions() + " DS transactions read.");
        System.out.println(getNumCtatTransactions() + " CTAT transactions wrote.");
    }

    private boolean hasDifferentProblemName(CtatTransaction previousTransaction, CtatTransaction ctatTransaction) {
        return (previousTransaction == null ||
                !previousTransaction.problem.equals(ctatTransaction.problem));
    }

    // Identify steps that were automatically typed-in by the tutor.  
    // ctatTransaction is already pointing to the next equation step, whereas the 
    // incompletes are steps that must be typedin for the previous equation step
    private void writeAutoTypeinTransactionToFile(BufferedWriter out, String equation,
                                                  CtatTransaction previousTransaction, Vector incompletes) 
    throws IOException {
        if (isFirstTransactionInProblem()) {
            //System.out.println("writeAutoTypeinTransactionToFile: 1st trans. in problem " + previousTransaction);
            return;
        }
        
        if (!isOK(previousTransaction.outcome)) {
            //System.out.println("writeAutoTypeinTransactionToFile: not OK " + previousTransaction);
            return;
        }
        
        if (!isTypedIn(LHS_SELECTION, incompletes)) {
            // System.out.println("writeAutoFillTransactionToFile for " + previousTransaction);
            // System.out.println("... with " + incompletes.size() + " incomplete(s)");
            CtatTransaction autoFillTransaction = 
                createCtatTransactionAutoFill(LHS_SELECTION, previousTransaction, equation);
            writeCtatTransactionToFile(out, autoFillTransaction);
        }
        if (!isTypedIn(RHS_SELECTION, incompletes)) {
            CtatTransaction autoFillTransaction = 
                createCtatTransactionAutoFill(RHS_SELECTION, previousTransaction, equation);
            writeCtatTransactionToFile(out, autoFillTransaction);
        }
    }
    
    private void writeTypeinTransactionToFile(BufferedWriter out,
    		CtatTransaction currentTransaction, String skillAction)
    throws IOException
    {   
    	String id = currentTransaction.id;
    	String student = currentTransaction.student;
    	String problem = currentTransaction.problem;
    	String skill = "typein";
    	
    	String selection = currentTransaction.selection;
    	if(currentTransaction.skill.equals("left"))
    	{ selection = LHS_SELECTION; }
    	else if(currentTransaction.skill.equals("right"))
    	{ selection = RHS_SELECTION; }
    	
    	String input = currentTransaction.input;
    	String step = currentTransaction.step;
    	String outcome = currentTransaction.outcome;
    	
    	/*
    	System.out.println();	
    	System.out.println(id);
    	System.out.println(student);
    	System.out.println(problem);
    	System.out.println(skill);
    	System.out.println(selection);
    	System.out.println(input);
    	System.out.println(step);
    	System.out.println(outcome);
    	*/
    	
    	CtatTransaction typeinTransaction = 
            new CtatTransaction(id, student, problem, skill, selection, input, step, outcome);

    	writeCtatTransactionToFile(out, typeinTransaction);
    }
    
    private boolean firstTransactionInProblem = true;
    private boolean isFirstTransactionInProblem() {
        return firstTransactionInProblem;
    }
    private void denyFirstTransactionInProblem() {
        firstTransactionInProblem = false;
    }
    private void resetFirstTransactionInProblem() {
        firstTransactionInProblem = true;
    }

    /**
     * Given a next "equation step" ctatTransaction and partially typein'd transactions
     * (could be empty), insert auto-typein for missing typein's  
     *  
     * @param ctatTransaction
     * @param incompletes
     */
    CtatTransaction createCtatTransactionAutoFill(String side, CtatTransaction ctatTransaction, String equation) {
        
        String id = ctatTransaction.id;
        String student = ctatTransaction.student;
        String problem = ctatTransaction.problem;
        String skill = AUTOFILL_SKILL;
        String selection = side;

        String[] sides = equation.split("=");
        String input = null;
        
        if (LHS_SELECTION.equalsIgnoreCase(side)) {
        	//System.out.println("LHS " + sides[0]);
            input = sides[0];
            /*
            if (input.charAt(input.length()-1) == ' ') {
                input = input.substring(0, input.length());
            }
            */
        } else if (RHS_SELECTION.equalsIgnoreCase(side)) {
            //System.out.println("RHS " + sides[1]);
        	input = sides[1];
            /*
            if (input.charAt(0) == ' ') {
                input = input.substring(1);
            }
            */
        }
        // Trim all spaces
        if (input != null) {
            input = input.replaceAll(" ", "");
        }
        
        String step = "AutoTypein by DStoCTAT";
        String outcome = OK_OUTCOME;
        
        return new CtatTransaction(id, student, problem, skill, selection, input, step, outcome);
    }
    
    // Check if both sides (LHS & RHS) are typed-in by the student
    private boolean isTypedIn(String side, Vector incompletes) {
        boolean isTypedIn= false;
        for (int i = 0; i < incompletes.size(); i++) {
            CtatTransaction transaction = (CtatTransaction)incompletes.get(i);
            if (isOK(transaction.outcome)) {
                if (side.equals(transaction.selection)) { 
                    isTypedIn = true;
                    break;
                }
            }
        }
        return isTypedIn;
    }

    /**
     * 
     * @param ctatTransaction This must hold the "next" step equation
     * @param incompletes
     * @return
     */
    private boolean updateSelection(CtatTransaction ctatTransaction, Vector /* CtatTransaction */ incompletes) {
        
        String equation = getStepEquation(ctatTransaction);
        String[] sides = equation.split("=");
        String lhsStr = sides[0].replaceFirst(" $", "");
        String rhsStr = sides[1].replaceFirst("^ ", "");
        String currentSelection = null;
        
        boolean updateSelection = true;
        for (int i = incompletes.size() -1; i >= 0; i--) {

            CtatTransaction transaction = (CtatTransaction)incompletes.get(i);

            // The last type-in's from the previous problem... 
            if (!transaction.problem.equals(ctatTransaction.problem)) {
                return updateSelectionLastStep(incompletes);
            }
            
            if (!isOK(transaction.outcome)) {
                // When the transaction is not "OK", then the selection must be 
                // determined by a "OK" transaction that follows 
                // (notice that the outer for loop goes from tail to head).
                if (currentSelection != null) {
                    transaction.selection = currentSelection;
                } else {
                    // A none-OK transaction found that does not have 
                    // an OK transaction that follows
                    // updateSelectionEndNonOk(ctatTransaction, incompletes);
                    // System.exit(-1);
                }
                
            } else { // transaction.outcome == "OK"
                // System.out.print("transaction.input = [" + transaction.input + "] ");
                // System.out.println("LHS = [" + sides[0] + "], RHS = [" + sides[1] + "]");
                String selection = null;
                if (equalsIgnoreSpaceCase(transaction.input, lhsStr)) {
                    selection = "LHS";
                } else if (equalsIgnoreSpaceCase(transaction.input, rhsStr)) {
                    selection = "RHS";
                } else if (isEquivalent(transaction.input, lhsStr)) {
                    selection = "LHS";;
                } else if (isEquivalent(transaction.input, rhsStr)) {
                    selection = "RHS";
                } else if ("typein".equals(transaction.skill)) {
                    // It is likely that the Datashop log is messed up and
                    // Two different problems are recorded with the same "Problem_Name(Id)"
                    //updateSelectionErrorTrouble(ctatTransaction, incompletes, transaction);
                    // break;
                } else {
                    //updateSelectionErrorTrouble(ctatTransaction, incompletes, transaction);
                    // break;
                }
                
                transaction.selection = selection;

                if (selection == null) {
                    // Failed to identify a selection 
                    currentSelection = null;
                } else {
                    // A selection identified
                    if (!selection.equals(currentSelection)) {
                        // Selection switched (this is the expected behavior)
                        currentSelection = selection;
                    } else {
                        // Hit the "OK" transaction twice, but they both have the same selection,
                        // which shouldn't happen
                        updateSelection = false;
                        System.out.println("selection mismatch: " + transaction);
                        break;
                    }
                }
            }
        }
        return updateSelection;
    }

    IsEquivalent isEquivalent = new IsEquivalent();
    private boolean isEquivalent(String exp1, String exp2) {
        String result = isEquivalent.inputMatcher(exp1, exp2);
        // System.out.println("isEquivalent: result = " + result);
        return  result != null;
    }
    
    // For the last step, there is no "next" transaction for SKILL showing an equation.
    // Therefore, we need to make best guesses 
    private boolean updateSelectionLastStep(Vector incompletes) {

        String selection = null;
        
        for (int i = incompletes.size() -1; i >= 0; i--) {
            
            CtatTransaction transaction = (CtatTransaction)incompletes.get(i);

            if (!isOK(transaction.outcome)) {
                if (selection != null) {
                    transaction.selection = selection;
                } else {
                    updateSelectionEndNonOk(null, incompletes);
                }
            } else {
                String equation = getStepEquation(transaction);
                // System.out.println("updateSelectionLastStep @ " + transaction.id + ", equation = " + equation);
                String[] sides = equation.split("=");
                if (sides[0].indexOf(transaction.input) != -1) {
                    selection = "LHS";
                } else if (sides.length > 1 && sides[1].indexOf(transaction.input) != -1) {
                    selection = "RHS";
                } else {
                    selection = "N/A";
                }
                transaction.selection = selection;
            }
        }
        return true;
    }

    private void updateSelectionErrorTrouble(CtatTransaction ctatTransaction, Vector incompletes, 
            CtatTransaction transaction) {
        System.out.println("updateSelection had trouble identify selection for ");
        System.out.println(transaction);
        System.out.println("ctatTransaction:");
        System.out.println(ctatTransaction);
        System.out.println("incompletes:");
        for (int j = 0; j < incompletes.size(); j++) {
            System.out.println((CtatTransaction)incompletes.get(j));
        }
    }

    private void updateSelectionEndNonOk(CtatTransaction ctatTransaction, Vector incompletes) {
        System.out.println("updateSelection gotten transactions ending with non-OK outcome");
        System.out.println("ctatTransaction:");
        System.out.println(ctatTransaction);
        System.out.println("incompletes:");
        for (int j = 0; j < incompletes.size(); j++) {
            System.out.println((CtatTransaction)incompletes.get(j));
        }
    }

    private boolean equalsIgnoreSpaceCase(String input, String string) {
        return input.replaceAll(" ", "").equalsIgnoreCase(string.replaceAll(" ", ""));
    }

    private boolean isOK(String outcome) {
        return OK_OUTCOME.equalsIgnoreCase(outcome);
    }
    
    private void writeCtatTransactionToFile(BufferedWriter out, Vector incompletes) 
    throws IOException {
        for (int i = 0; i < incompletes.size(); i++) {
            writeCtatTransactionToFile(out, (CtatTransaction)incompletes.get(i));
        }
    }

    private void writeCtatTransactionToFile(BufferedWriter out, CtatTransaction ctatTransaction) 
    throws IOException {
        
        String transaction = ctatTransaction.id + "\t";
        transaction += ctatTransaction.student + "\t";
        transaction += ctatTransaction.problem + "\t";
        transaction += ctatTransaction.skill + "\t";
        transaction += ctatTransaction.selection + "\t";
        transaction += ctatTransaction.input + "\t";
        transaction += ctatTransaction.step + "\t";
        transaction += ctatTransaction.outcome + "\n";

        // System.out.println("writing " + transaction);
        
        out.write(transaction);
        incNumCtatTransactions();
    }

    private CtatTransaction convertDStoCtatTransaction(String dsReadIn) {
        
        String[] tokens = dsReadIn.split("\t");

        /* 
         Format of a Datashop transaction in previous version
         0. Transaction Id
         1. Anon Student Id
         2. Session Id
         3. Time
         4. Time Zone
         5. Student Response Type
         6. Student Response Subtype
         7. Tutor Response Type
         8. Tutor Response Subtype
         9. Problem Hierarchy
        10. Problem Name(Id)
        11. Step Name(Id)
        12. Action
        13. Input
        14. Attempt At Step
        15. Outcome
        16. Selection
        17. Feedback Text
        18. Feedback Classification
        19. Help Level
        20. Total # Hints
        21. Condition Name (Type)
        22. Knowledge Component Name (Category)
        23. School
        24. Class
        */
        
        //Format of a Datashop transaction in All_Data_94_export.txt
        /*
        0. Sample Name
        1. Anon Student Id
        2. Session Id
        3. Time
        4. Time Zone
        5. Student Response Type
        6. Student Response Subtype
        7. Tutor Response Type
        8. Tutor Response Subtype
        9. Level(Unit)
        10. Level(Section)
        11. Problem Name
        12. Step Name
        13. Attempt At Step
        14. Outcome
        15. Selection
        16. Action
        17. Input
        18. Feedback Text
        19. Feedback Classification
        20. Help Level
        21. Total # Hints
        22. Condition Name
        23. Condition Type
        24. KC(Default)
        25. KC Category(Default)
        26. KC(Default)
        27. KC Category(Default)
        28. KC(Default)
        29. KC Category(Default)
        30. KC(Default)
        31. KC Category(Default)
        32. KC(Single-KC)
        33. KC Category(Skingle-KC)
        34. School
        35. Class 
        */
        
        String id = tokens[2];
        String student = tokens[1];
        String skill = tokens[16];
        String selection = identifySelection(skill);
        String input = reformInputString(tokens[17]);
        String step = reformInputString(tokens[12]);
        String problem = tokens[11];
        String outcome = tokens[14];
        
        /*
        System.out.println(id);
        System.out.println(student);
        System.out.println(skill);
        System.out.println(selection);
        System.out.println(input);
        System.out.println(step);
        System.out.println(problem);
        System.out.println(outcome);
        */
                
        if (isHint(outcome)) {
            skill = HINT_SKILL;
        }
        
        return new CtatTransaction(id, student, problem, skill, selection, input, step, outcome);
    }

    /**
     * Get rid of "," in the StepName
     * @param stepName
     * @return
     */
    private String reformInputString(String stepName) {
	// System.out.println("stepName = |" + stepName + "|");
	return stepName.replaceAll("\"", "").replaceAll(",", "");
    }
    
    private boolean isHint(String outcome) {
        return (outcome != null && outcome.indexOf("HINT") != -1);
    }

    /**
     * The Selection must be "SKILL" when the skill is one of the known "skills", 
     * otherwise it must be null and set (or, guessed, if you will) later based on the equation
     * on the next step.
     *  
     * @param skill
     * @return
     */
    private String identifySelection(String skill) {
        return getSkillNames().contains(skill) ? "SKILL" : "";
    }

    /**
     * Given a "Problem Name(Id)", which looks like "EG60(715)", 
     * and "Step Name(Id)", like "4x = 2+10x(157591), returns a problem name 
     * taken from the "Step Name(Id)" for new series of transactions with the 
     * same "Problem Name(Id)" 
     * 
     * @param problemId
     * @param stepName
     * @return
     */
    private String lastProblem = "";
    private String getLastProblem() { return lastProblem; }
    private void setLastProblem(String lastProblem) { this.lastProblem = lastProblem; }

    private String lastProblemID = "";
    private String getLastProblemID() { return lastProblemID; }
    private void setLastProblemID(String lastProblemID) { this.lastProblemID = lastProblemID; }
    
    String identifyProblemName(String problemId, String stepName) {
        
        String problemName = getProblemName(stepName);
        
        if (getLastProblemID().equals(problemId) && 
        	couldBeDerivative(getLastProblem(), problemName)) {
            problemName = getLastProblem();
        } else {
            setLastProblemID(problemId);
            setLastProblem(problemName);
        }
        return problemName;
    }
    
    // DS transactions may be messed up and have two different problems coded with the same
    // "Problem Name(Id)"
    private boolean couldBeDerivative(String lastProblemName, String problemName) {

	// If the "prblemName" is not about equation, then let it go
	if (problemName.indexOf('=') == -1) return true;
	
	char var1 = getFirstVariable(lastProblemName);
	char var2 = getFirstVariable(problemName);
	return (!(var1 != -1  && var2 != -1) || var1 == var2);
    }

    private char getFirstVariable(String lastProblemName) {
	char[] letters = lastProblemName.toLowerCase().toCharArray();
	for (int i = 0; i < letters.length; i++) {
	    if ('a' <= letters[i] && letters[i] <= 'z') {
		return letters[i];
	    }
	}
	return (char) -1;
    }

    // stepName looks like "1y-9+9 = 7y-6+9(157594)"
    private String getProblemName(String stepName) {
    	//return stepName.substring(0, stepName.lastIndexOf('('));
    	return stepName.substring(stepName.indexOf(' ') + 1);
    }

    // Return the equation from the "step" field of the given ctatTransaction
    // "step" looks like: "14.73/10.98 = 10.98y/10.98(157543)"
    private String getStepEquation(CtatTransaction ctatTransaction) {
    	return ctatTransaction.step;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("DStoCTAT <input_file> <output_file>");
            System.out.println("You must specify two arguments for the file names.");
            System.exit(-1);
        }
        
        try {
            new DStoCTAT().convertDStoCTAT(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A class that represent a single CTAT transaction, which has the fields student_name,
     * problem_name, skill, selection, actual input, subgoal_name, and outcome
     *
     */
    public class CtatTransaction {
        
        // - - - - - - - - - - - - - - - - - - - - - - - - - 
        // Fields
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        String id;
        String student;
        String problem;
        String skill;
        String selection;
        String input;
        String step;
        String outcome;
        
        // - - - - - - - - - - - - - - - - - - - - - - - - - 
        // Fields
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        public CtatTransaction(String id, String student, String problem, String skill, 
                String selection, String input, String step, String outcome) {
            
            this.id = id;
            this.student = student;
            this.problem = problem;
            this.skill = skill;
            this.selection = selection;
            this.input = input;
            this.step = step;
            this.outcome = outcome;
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - 
        // Methods 
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        
        boolean hasSkill() {
            return !skill.equals("");
	}

	boolean hasSelection() {
	    return !selection.equals("");
        }
        
        public String toString() {
            return "<" + id + " " +
            "[student " + student + "]" +
            "[problem " + problem + "]" +
            "[skill " + skill + "]" +
            "[selection " + selection + "]" +
            "[input " + input + "]" + 
            "[step " + step + "]" +
            "[outcome " + outcome + "]>";
        }
    }
    
    //returns whether a manual typein skill is performed in problem
    private boolean hasManualTypein(ArrayList<CtatTransaction> problemList)
    {
    	for(CtatTransaction c : problemList)
    	{
    		if(c.skill.equals("left") || c.skill.equals("right"))
    		{
    			return true;
    		}
    	}
    
    	return false;
    }
    
    //returns whether the next step is a manual typein
    private boolean nextIsManualTypein(ArrayList<CtatTransaction> problemList, int index)
    {
    	String currentSkill = problemList.get(index).skill;
    	
    	if(currentSkill.equals("left") || currentSkill.equals("right"))
    	{
    		return false;
    	}
    
    	for(int i = index + 1; i < problemList.size(); i++)
    	{
    		CtatTransaction c = problemList.get(i);
    		
    		if(c.hasSelection())
    		{
        		if(c.skill.equals("left") || c.skill.equals("right"))
        		{
        			return true;
        		}
        		else
        		{
        			return false;
        		}
    		}
    	}
    	
    	return false;
    }
    
    //returns whether the previous step was a manual typein
    private boolean previousWasManualTypein(ArrayList<CtatTransaction> problemList, int index)
    {
    	String currentSkill = problemList.get(index).skill;
    	
    	if(currentSkill.equals("left") || currentSkill.equals("right"))
    	{
    		return true;
    	}
    
    	for(int i = index - 1; i >= 0; i++)
    	{
    		CtatTransaction c = problemList.get(i);
    		
    		if(c.hasSelection())
    		{
        		if(c.skill.equals("left") || c.skill.equals("right"))
        		{
        			return true;
        		}
        		else
        		{
        			return false;
        		}
    		}
    	}
    	
    	return false;
    }
}