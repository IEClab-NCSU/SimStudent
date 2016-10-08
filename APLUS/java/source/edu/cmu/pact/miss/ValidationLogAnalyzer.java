/**
 * @author mazda
 * 
 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * @author mazda
 *
 */
public class ValidationLogAnalyzer {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Class Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public final String CT_FILE_NAME = "contingency-table.txt";

    public final String SUCCESS = "SUCCESS";
    
    public final int TRUE_POSITIVE = 1;
    public final int TRUE_NEGATIVE = 2;
    public final int FALSE_POSITIVE = 3;
    public final int FALSE_NEGATIVE = 4;

    // The name of the input file
    private String validationLogFileName;
    
    // The number of training problems that must be considered as pre-learning to 
    // fit real students performance.  Those pre-learning problems would be excluded
    // from the analysis of fitness
    private int cutOff = 0;
    
    // A list of contingency tables each for a different condition
    Vector /* ContingencyTable */ ctList = new Vector();
    void addCT(ContingencyTable ct) {
	ctList.add(ct);
    }
    Vector /* ContingencyTable */ getCT() {
	return ctList;
    }
    
    /**
     * @author mazda
     *
     */
    class ContingencyTable {
	
	String condition;
	int cutOff;
	
	int numTruePositive = 0;
	int numTrueNegative = 0;
	int numFalsePositive = 0;
	int numFalseNegative = 0;
	
	private String getCondition() {
	    return condition;
	}
	private void setCondition(String condition) {
	    this.condition = condition;
	}
	private int getCutOff() {
	    return cutOff;
	}
	private void setCutOff(int cutOff) {
	    this.cutOff = cutOff;
	}
	
	public ContingencyTable(String condition, int cutOff) {
	    setCondition(condition);
	    setCutOff(cutOff);
	}

	// Given a validation (a singe transaction in a validation file),
	// increment a number of {true|false)_{positive|negative} according 
	// to the validation
	public void addTally(Validation validation) {

	    // System.out.println("addTally for " + validation.fitType());
	    
	    switch (validation.fitType()) {
	    case TRUE_POSITIVE:
		numTruePositive++;
		break;
	    case TRUE_NEGATIVE:
		numTrueNegative++;
		break;
	    case FALSE_POSITIVE:
		numFalsePositive++;
		break;
	    case FALSE_NEGATIVE:
		numFalseNegative++;
		break;
	    }
	}
	
	int total() { 
	    return numTruePositive + numTrueNegative + numFalsePositive + numFalseNegative;
	}
	
	float accuracy() {
	    int trueCount = numTruePositive + numTrueNegative;
	    return ((float)trueCount)/((float)total());
	}
	
	float error() {
	    int falseCount = numFalsePositive + numFalseNegative;
	    return ((float)falseCount)/((float)total());
	}
	
	float precision() {
	    return ((float)numTruePositive)/((float)(numTruePositive + numFalsePositive));
	}
	
	float recall() {
	    return ((float)numTruePositive)/((float)(numTruePositive + numFalseNegative));
	}

	float F1() {
	    float precision = precision();
	    float recall = recall();
	    return 2*precision*recall/(precision + recall);
	}
	
	public void writeToFile() throws IOException {
	    
	    if (!new File(CT_FILE_NAME).exists()) {
		createNewContingencyAnalysisLog();
	    }
	    
	    FileWriter fileWriter = new FileWriter(CT_FILE_NAME, true);
	    BufferedWriter out = new BufferedWriter(fileWriter);
	    String log = getCondition() + "\t" + getCutOff() + "\t";
	    log += numTruePositive + "\t";
	    log += numFalseNegative + "\t";
	    log += numFalsePositive + "\t";
	    log += numTrueNegative + "\t";
	    log += accuracy() + "\t";
	    log += error() + "\t";
	    log += precision() + "\t";
	    log += recall() + "\t";
	    log += F1() + "\t";
	    log += "\n";
	    out.write(log);
	    out.close();
	    
	    System.out.println("logged... " + log);
	}

	private void createNewContingencyAnalysisLog() throws IOException {
	    FileWriter fileWriter = new FileWriter(CT_FILE_NAME);
	    BufferedWriter out = new BufferedWriter(fileWriter);
	    out.write("Condition\tCut-off\tTP\tFN\tFP\tTN\tAccuracy\tError\tPrecision\tRecall\tF1\n");
	    out.close(); 
	}
    }

    /**
     * @author mazda
     *
     */
    class Validation {
	
	int numTraining;
	String condition;
	String model;
	String actual;
	String modelRule;

	public Validation(String record) {
	    
	    String[] field = record.split("\t");
	    /*
	     0. Date	
	     1. Condition	
	     2. NumTraining	
	     3. Phase	
	     4. TestName	
	     5. StateName	
	     6. Freq	
	     7. NumRule	
	     8. NumSteps	
	     9. ModelRule	
	    10. ActualRule	
	    11. ModelStatus	
	    12. Model	
	    13. ActualStatus	
	    14. Actual	
	    15. Success
	    16. NumSuccess	
	    17. NumAttempt	
	    18. Ratio	
	    19. Learned
	    */
	    
	    numTraining = Integer.parseInt(field[2]);
	    condition = field[1];
	    model = field[12];
	    actual = field[14];
	    modelRule = field[9];
	}

	public int fitType() {
	    
	    int fitType = -1;
	    if (actual.equals(SUCCESS)) 
		fitType = (model.equals(SUCCESS)) ? TRUE_POSITIVE : FALSE_POSITIVE;
	    else
		fitType = (model.equals(SUCCESS)) ? FALSE_NEGATIVE : TRUE_NEGATIVE;
		
	    return fitType;
	}
	
	public String toString() {
	    return "<" + condition + " " + numTraining + " " + model + " " + actual + ">";
	}
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public ValidationLogAnalyzer(String[] args) {
	setValidationLogFileName( args[0] );
	if ( args.length == 2) {
	    setCutOff( Integer.parseInt(args[1]) );
	}
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // main() 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	
	if (args.length < 1 || 2 < args.length) {
	    
	    System.out.println("Usage: ValidationLogAnalyzer <log_file> {<cut_off>}");
	    System.out.println("<log_file> must be the validation log file generated by SimSt.");
	    System.out.println("An optional <cut_off> specifies # of problems for pre-learning.");
	}
	
	ValidationLogAnalyzer validationLogAnalyzer = new ValidationLogAnalyzer(args);
	validationLogAnalyzer.runAnalysis();
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Class Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    void runAnalysis() {
	
	try {
	    contingencyTableAnalysis( getCutOff() );
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void contingencyTableAnalysis(int cutOff) throws IOException {
	
	String previousCondition = null;
	ContingencyTable contingencyTable = null;
	
	BufferedReader in = openInputFile(getValidationLogFileName());
	
	// Skipping the header line
	String record = in.readLine();
	while ((record = in.readLine()) != null) {

	    Validation validation = new Validation(record);
	    // System.out.println("validation: " + validation);
	    
	    // Skip steps where the Tutor typed-in an equation
	    if (validation.modelRule.indexOf("auto-") > 0)
		continue;
	    
	    // A new condition has been read
	    if (!validation.condition.equals(previousCondition)) {
		contingencyTable = new ContingencyTable(validation.condition, cutOff);
		addCT(contingencyTable);
		previousCondition = validation.condition;
		// System.out.println("CT added. Total " + getCT().size() + " tables.");
	    }
	    
	    // NumTrainig begins from 0
	    if (validation.numTraining +1 > cutOff) {
		contingencyTable.addTally(validation);
	    }
	}
	
	writeCTtoFile();
    }
    
    private void writeCTtoFile() throws IOException {
	for (int i = 0; i < getCT().size(); i++) {
	    ContingencyTable ct = (ContingencyTable)getCT().get(i);
	    ct.writeToFile();
	}
    }

    BufferedReader openInputFile(String fileName) throws IOException {
        return new BufferedReader(new FileReader(fileName));
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Getters and setters
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    private int getCutOff() {
        return cutOff;
    }

    private void setCutOff(int cutOff) {
        this.cutOff = cutOff;
    }

    private String getValidationLogFileName() {
        return validationLogFileName;
    }

    private void setValidationLogFileName(String validationLogFileName) {
        this.validationLogFileName = validationLogFileName;
    }

}
