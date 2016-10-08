package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.XMLSpecialCharsTransform;
import edu.cmu.pact.Utilities.trace;

/////////////////////////////////////////////////////////////////////////
/**
  *  This class is designed to create mass problem files from
  *  the Excel Configuration and its corresponding Mass Production files.
*/
/////////////////////////////////////////////////////////////////////////

public class MergeMassProductionDialog extends JDialog {
	public static final String VAR_RIGHT_DELIMITER = ")%";
	public static final String VAR_LEFT_DELIMITER = "%(";
	private ArrayList brdVariables = new ArrayList();
	private List<String> extraBRDvars = new ArrayList<String>();
	private ArrayList extraDatavars = new ArrayList();
	private ArrayList badDatavars = new ArrayList();
	private ArrayList emptyVars = new ArrayList();
	
	private static Pattern varPattern = ProblemModel.getMassProductionVarPattern();
	
	private String dataTempData[][];
	
	final int NO_VARIABLES = 0;
	final int NO_CONFIGURE_DATA = 1;
	final int BRD_TEMP_VARS_ABSENT_FROM_DATA_TEMP = 2;
	final int DATA_TEMPLATE_VARIABLES_ABSENT = 3;
	final int DATA_TEMPLATE_MISSING_DATA = 4;
	final int DATA_MATCH = 5;

	// BRD Remplate
	private JLabel brdTemplateLabel = new JLabel("Select the BRD template:");
	private JTextField brdTemplateTextField = new JTextField();
	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(brdTemplateTextField); }
	private JButton brdTemplateBrowseButton = new JButton("Browse...");
	
	// Data Template
	private JLabel problemsTableLabel = new JLabel("Select the problems table:");
	private JTextField problemsTableTextField = new JTextField();
	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(problemsTableTextField); }
	private JButton problemsTableBrowseButton = new JButton("Browse...");
	
	// Buttons
	private JButton saveMergeButton = new JButton("Save & Merge");
	private JButton mergeButton = new JButton("Merge");
	private JButton cancelButton = new JButton("Cancel");
	
	// Excel checkbox
	private JCheckBox excelJCheckbox = new JCheckBox("Table file was edited with Microsoft Excel", true);

	private Container contentPane = getContentPane();
	
	private boolean flagFoundProblemTag;
	private boolean flagFoundProblemTagClose;
	private boolean flagFoundFirstNodeTag;
	private boolean flagFoundFirstNodeText;
	
	private BR_Controller controller;
	
    private String instructions = "<html>" +
            "Use this window to generate BRD files using Mass Production. Mass production allows you to easily " +
            "produce many Behavior Recorder graphs that use a single student interface. Select a " +
            "BRD template (the BRD file with variables in place of constants) and a completed problems table (the " +
            "file containing variable names and values for the graphs you will produce.";
    private static String  prevbrdTemplateFileName = "";
    private static String  prevProblemTableLocation = "";
    private File problemsFolder;

    private static final String ERROR_MESSAGE[] = {
        "The files you selected cannot be merged, because the ",
        "data in the Problems Table is missing or incomplete.",
        "Please check your Problems Table file and try again."};
    
    private static final String NO_DATA_TEMPLATE_VARIABLES_ERROR_MESSAGE[] = {
        "The files you selected cannot be merged, because the ",
        "data in the Problems Table is missing or incomplete.",
        "Please check your Problems Table file and try again."};
   
    private static final String NO_BRD_TEMPLATE_VARIABLES_ERROR_MESSAGE[] = {
        "The files you selected cannot be merged, because there ",
        "are no variables in the BRD Template.",
        "Please use an appropriate BRD Template file."};
    
	// TODO - name the missing variables
	private static final String BRD_TEMPLATE_SHORT_ERROR_MESSAGE[] = {
        "The files you selected cannot be merged, because the ",
        "BRD Template has fewer variables than the Problems Table.",
        "Please compare the two files."};

	// TODO - name the missing variables
	private static final String DATA_TEMPLATE_SHORT_ERROR_MESSAGE[] = {
        "The files you selected cannot be merged, because the ",
        "BRD Template has more variables than the Problems Table.",
        "Please compare the two files."};

//	 TODO - name the missing variables
	private static final String DATA_TEMPLATE_MISSING_DATA_MESSAGE[] = {
        "The files you selected cannot be merged, because the ",
        "Data Template has blank or invalid fields."};
	
	
	// TODO - REPLACE /t/n with /n


    private static final String EMPTY_FILE_MESSAGE[] = {
        "The problem table is empty.  No problems were found."};
    
    private static final String EMPTY_ROW_MESSAGE[] = {
    	"The problem table includes a empty row.  Please check ",
    	"the file and remove empty rows"};

    /**
     * Constructor for unit tests only.
     */
    MergeMassProductionDialog() {
    	super((Frame) null);
    }

	public MergeMassProductionDialog (BR_Controller controller) {
		super(controller.getActiveWindow(), true);
		
		this.controller = controller;
        
		this.setTitle("Merge Problems for Mass Production");

		initUI();
		

	}
	
	/**
	 * 
	 */
	private void initUI() {
		
		int headerHeight = 115;
		
		int leftAlign = 20;
		
		int rowSpace = 30;
		int broweSpace = 15;
		
		int labelWidth = 400;
		int textFiledWidth = 400;
		int buttonWidth = 100;
		int LbuttonWidth = 115;
		int fieldHeight = 20;
		
		int buttonLeft = 190;
		
		Font labelFont = new java.awt.Font("", 1, 12);
		
		int dialogWidth = 555;
		int dialogHeight = 340;
		
		contentPane.setLayout(null);
		
		
        JLabel instructionsLabel = new JLabel (instructions);
        instructionsLabel.setBounds(leftAlign, 0, 450, 90);
        contentPane.add(instructionsLabel);
        
		// format selectProblemTemplateJLabel
		brdTemplateLabel.setLocation(leftAlign, headerHeight);
		brdTemplateLabel.setFont(labelFont);
		brdTemplateLabel.setSize(labelWidth, fieldHeight);
		contentPane.add(brdTemplateLabel);
		
		// format selectProblemTemplateJTextField
		brdTemplateTextField.setLocation(leftAlign, headerHeight + rowSpace);
		brdTemplateTextField.setSize(textFiledWidth, fieldHeight);
		
		if (prevbrdTemplateFileName != "") brdTemplateTextField.setText(prevbrdTemplateFileName);
		else {
			String problemFullName = controller.getProblemModel()
					.getProblemFullName();
			if (problemFullName != null && problemFullName.length() > 0)
				brdTemplateTextField.setText(problemFullName);
			else
				brdTemplateTextField.setText(controller.getPreferencesModel()
						.getStringValue(
								CreateProblemsTableDialog.BRD_TEMPLATE_PATH));
		}
		
		contentPane.add(brdTemplateTextField);
		
		// format selectProblemTemplateJButton
		brdTemplateBrowseButton.setLocation(leftAlign + brdTemplateTextField.getWidth() + broweSpace, 
													headerHeight + rowSpace);

        brdTemplateBrowseButton.setSize(buttonWidth, fieldHeight);
		contentPane.add(brdTemplateBrowseButton);
        brdTemplateBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                brdTemplateBrowseButtonClicked();
            }
        });
        
		problemsTableLabel.setLocation(leftAlign, headerHeight + 2 * rowSpace);
		problemsTableLabel.setFont(labelFont);
		problemsTableLabel.setSize(labelWidth, fieldHeight);
		contentPane.add(problemsTableLabel);
		
		problemsTableTextField.setLocation(leftAlign, headerHeight + 3 * rowSpace);
		problemsTableTextField.setSize(textFiledWidth, fieldHeight);
		String problemTableLocation = controller.getPreferencesModel().getStringValue(CreateProblemsTableDialog.PROBLEMS_TABLE_LOCATION);

		if (problemTableLocation != null)
			problemsTableTextField.setText(problemTableLocation);
		else if (prevProblemTableLocation != "")
			problemsTableTextField.setText(prevProblemTableLocation);	
		else
			problemsTableTextField.setText(brdTemplateTextField.getText().replace(".brd", ".txt"));
		
		contentPane.add(problemsTableTextField);
		
		problemsTableBrowseButton.setLocation(leftAlign + brdTemplateTextField.getWidth() + broweSpace, 
													headerHeight + 3 * rowSpace);
		problemsTableBrowseButton.setSize(buttonWidth, fieldHeight);
		contentPane.add(problemsTableBrowseButton);
		problemsTableBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                problemsTableBrowseButtonClicked();
            }
        });
		
		// Excel checkbox
		excelJCheckbox.setLocation(leftAlign, headerHeight + 4 * rowSpace);
		excelJCheckbox.setFont(labelFont);
		excelJCheckbox.setSize(labelWidth, fieldHeight);
		contentPane.add(excelJCheckbox);
		
		// format createJButton
		saveMergeButton.setLocation(buttonLeft, headerHeight + 5 * rowSpace + 10);
		saveMergeButton.setSize(LbuttonWidth, fieldHeight);
		
		if (!brdTemplateTextField.getText().equals("") && 
				brdTemplateTextField.getText().equalsIgnoreCase(controller.getProblemModel()
				.getProblemFullName()))
			saveMergeButton.setEnabled(true); 
		else saveMergeButton.setEnabled(false);
		
		
		contentPane.add(saveMergeButton);
		saveMergeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveMergeButtonClicked();
			}
		});
		
		// format createJButton
		mergeButton.setLocation(buttonLeft + buttonWidth + rowSpace, headerHeight + 5 * rowSpace + 10);
		mergeButton.setSize(buttonWidth, fieldHeight);
		contentPane.add(mergeButton);
		mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mergeButtonClicked();
            }
        });
		
		// format createJButton 
		cancelButton.setLocation(leftAlign + brdTemplateTextField.getWidth() + broweSpace, headerHeight + 5 * rowSpace + 10);
		cancelButton.setSize(buttonWidth, fieldHeight);
		contentPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
            
        });
		
		setSize(dialogWidth, dialogHeight);
        setResizable(false);
		
		setLocationRelativeTo(controller.getJGraphWindow());
		setVisible(true);
	}
	
    protected void brdTemplateBrowseButtonClicked() {
        String targetDir = controller.getPreferredBRDLocation();
        final String title = "Select the BRD Template File";

        File selectedFile = DialogUtilities.chooseFile(targetDir, new BrdFilter(),
        		title, "Select", controller);
        if (selectedFile != null) {
            brdTemplateTextField.setText(selectedFile.getPath());
            controller.setPreferredBRDLocation(selectedFile.getParent());
    		if (brdTemplateTextField.getText().equalsIgnoreCase(controller.getProblemModel()
					.getProblemFullName()))
    			saveMergeButton.setEnabled(true); 
    		else saveMergeButton.setEnabled(false);
        }
        return;
    }
    
    /**
     *  process the Excel Configuration file selection 
     *  and get the configuration data. 
     */
    
    private void problemsTableBrowseButtonClicked() {

        File selectedFile = DialogUtilities.chooseFile(controller.getPreferredBRDLocation(),
        		new TxtFilter(), "Select the Problems Table File",
        		"Select", controller); 

        if (selectedFile != null) {
            problemsTableTextField.setText(selectedFile.getPath());
            controller.setPreferredBRDLocation(selectedFile.getParent());
        }
        return;
    }

    /**
     * Handler for the button labeled 'Save & Merge'
     */
	private void saveMergeButtonClicked() {
		
		collectBRDData();
		
		Boolean keepGoing = promptToSaveBRD();
		if (!keepGoing)
			return;
		      
		collectProblemData();
	}
	
	   /**
     * Handler for the button labeled 'Merge' in mass production dialog.
     */
	private void mergeButtonClicked() {
		collectBRDData();
        
		collectProblemData();
		return;
	}
	
	/**
	 * Gather the variables form the BRD template.
	 *
	 */
	private void collectBRDData(){
		dataTempData = null;	// John - not sure why we need this?
	    
	    String brdTemplateFileName = brdTemplateTextField.getText();
        String problemsTableFileName = problemsTableTextField.getText();
        
        if (! checkFileExistence(brdTemplateFileName, "BRD Template"))
            return;
        
        if (! checkFileExistence(problemsTableFileName, "problems table"))
            return;
        
        prevbrdTemplateFileName = brdTemplateFileName;
        prevProblemTableLocation = problemsTableFileName;
        
        // Get list of variables in the BRD template.
	    collectBRDTempVars(brdTemplateFileName);
	    
	    
        if (brdVariables.size() == 0) {
            displayWarningMsg(ERROR_MESSAGE);
            return;
        }
	}
	
	private Boolean promptToSaveBRD(){
		String brdTemplateFileName = brdTemplateTextField.getText();
        boolean keepGoing = CreateProblemsTableDialog.promptToSaveBRDSilently(brdTemplateFileName, controller, this,
                "Next you will be prompted for the folder to save the generated problem files in.");
        
        return keepGoing;
	}
	
	/**
	 * Here we collect the data from Data Template and check for errors.
	 *
	 */
	private void collectProblemData(){
		String problemsTableFileName = problemsTableTextField.getText();
		
        Boolean bDataTempValid = collectDataTempVars(problemsTableFileName);
        if (! bDataTempValid)
            return;
        else {
		    problemsFolder = promptForProblemsFolder();
		  
		    if (problemsFolder != null) {
				// John - I commented this because it is redundant with call to collectDataTempVars
		    	// int dataMatchCheck = dataMatch();
		    	// mass variables data don't match configuration data
				//if (dataMatchCheck != DATA_MATCH) {
				//	displayDataMatchErrorMessage(dataMatchCheck);
				//	return;
				//}
				createProblemsFilesWithVariablesReplace();
			}
		    else {
		    	if (trace.getDebugCode("mp")) trace.out("mp","collectProblemData : final BRD files folder is null" );
		    }
        }
        
        setVisible(false);
	    dispose();
	}
	
	private boolean checkFileExistence(String fileName, String fileType) {
        if (fileName == null || fileName.trim().equals("")) {
            String message = "Please select a " + fileType + " file";
            String title = "File Missing";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (! ((new File (fileName)).exists())) {
            String message = "<html>" + fileType + " " + fileName + "<br>" + 
             " could not be found";
            String title = "File Missing";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private File promptForProblemsFolder() {
        
        String startLocation = controller.getPreferredBRDLocation();
        while (true) {
/*
    	File dir = DialogUtilities.chooseFile(projDir, null, brdFilter,
    			"Skill Matrix: Please choose a directory to scan", "Open",
    			DialogUtilities.FILES_AND_DIRECTORIES, ctlr.getActiveWindow());
 * 
 */
			File f = DialogUtilities.chooseFile(startLocation, null, new DirectoryFilter(),
					"Select Output Folder for New Problem Files", "Create Merged Files",
					DialogUtilities.DIRECTORIES_ONLY, controller.getActiveWindow());
			if (f != null) {
				
				
				if (f.isDirectory())
					return f;
				else {
					String message = "<html>" + 
								     "The selected folder: " + "<B>" + f.toString() + "</B>" + " doesn't exist. " + "<br>" +
							         "Please select an existing folder or click " + "<B>" + "New Folder" + "</B>" + 
							         " to create a new folder." + 
							         "</html>";
		            String title = "Error creating folder";
		            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
				}
					
			}
			else return null;
		}
		
    }

    // ----------------------------- Warning Dialogs -------------------------
    /**
     * 	Display the error message based on dataMatchCheck.
     * 
     * 	@param: dataMatchCheck -- dataMatch returned error values:
     * 				NO_VARIABLES,
     * 				NO_CONFIGURE_DATA,
     * 				VARIABLES_NOT_MATCH,
     *				CONFIGURE_DATA_INCOMPLETE.	
			final int NO_VARIABLES = 0;
			final int NO_CONFIGURE_DATA = 1;
			final int VARIABLES_NOT_MATCH = 2;
			final int CONFIGURE_DATA_INCOMPLETE = 3;
			final int DATA_MATCH = 4;
		
     */
	
	private void displayDataMatchErrorMessage(int dataMatchCheck) {
        trace.out ("data match check = " + dataMatchCheck);
		
        if (trace.getDebugCode("mp")) trace.out("mp", "1 so displayDataMatchErrorMessage : variables.size() = " + brdVariables.size());
        displayWarningMsg(ERROR_MESSAGE);
			
		return;
	}


	private void displayNoBRDTemplateVariablesErrorMessage(){
		if (trace.getDebugCode("mp")) trace.out("mp", "1 so displayNoBRDTemplateVariablesErrorMessage: variables.size() = " + brdVariables.size());
        displayWarningMsg(NO_BRD_TEMPLATE_VARIABLES_ERROR_MESSAGE);
			
		return;

	}

	private void displayNoDataTemplateVariablesErrorMessage(){
		if (trace.getDebugCode("mp")) trace.out("mp", "1 so displayNoDataTemplateVariablesErrorMessage: variables.size() = " + brdVariables.size());
        displayWarningMsg(NO_DATA_TEMPLATE_VARIABLES_ERROR_MESSAGE);
			
		return;

	}

	/**
	 * The Data Template lacks some necessary variables.
	 * @param extraVariables	A list of variables that are in the BRD Template but missing from the Data Template
	 */
	private void displayBRDTempVarsMissingFromDataTempErrorMessage(List<String> extraVariables){
		String BRD_TEMPLATE_SHORT_ERROR_MESSAGE[] = new String[3 + extraVariables.size()];
		BRD_TEMPLATE_SHORT_ERROR_MESSAGE[0] = "The BRD Template has variable(s) not defined in the Data Template";
		BRD_TEMPLATE_SHORT_ERROR_MESSAGE[1] = "(problems) table. These will remain untranslated in the output.";
		BRD_TEMPLATE_SHORT_ERROR_MESSAGE[2] = "The missing variable(s) are:";
		for (int i=0; i<extraVariables.size(); i++) {
			BRD_TEMPLATE_SHORT_ERROR_MESSAGE[i+3] = extraVariables.get(i);
		}

		if (trace.getDebugCode("mp")) trace.out("mp", "1 so displayFewerBRDTEmplateVariablesErrorMessage: BRD_TEMPLATE_SHORT_ERROR_MESSAGE = " + BRD_TEMPLATE_SHORT_ERROR_MESSAGE);
        displayWarningMsg(BRD_TEMPLATE_SHORT_ERROR_MESSAGE);
			
		return;

	}

	/**
	 * The BRD Template lacks some variables that do appear in the Data Template.
	 * @param extraVariables	A list of variables that are in the Data Template but missing from the BRD Template
	 */
	private void displayDataTempVarsMissingFromBRDTempErrorMessage(ArrayList extraVariables){
		String DATA_TEMPLATE_SHORT_ERROR_MESSAGE[] = new String[3 + extraVariables.size() + 1];
		DATA_TEMPLATE_SHORT_ERROR_MESSAGE[0] = new String("The BRD Template has more variables than the BRD Template table.");
		DATA_TEMPLATE_SHORT_ERROR_MESSAGE[1] = new String("The merge process will ignore these variables");
		DATA_TEMPLATE_SHORT_ERROR_MESSAGE[2] = new String("The extra variables are:");
		for (int i=0; i<extraVariables.size(); i++) {
			DATA_TEMPLATE_SHORT_ERROR_MESSAGE[i+3] = (String) extraVariables.get(i);
		}
		DATA_TEMPLATE_SHORT_ERROR_MESSAGE[3 + extraVariables.size() ] = new String("The merge will proceed anyway but will ignore the extras.");
		if (trace.getDebugCode("mp")) trace.out("mp", "1 so displayFewerDataTemplateVariablesErrorMessage: DATA_TEMPLATE_SHORT_ERROR_MESSAGE = " + DATA_TEMPLATE_SHORT_ERROR_MESSAGE);
        displayWarningMsg(DATA_TEMPLATE_SHORT_ERROR_MESSAGE);
			
		return;

	}	
	
	/**
	 * 4 The Data Template may have blank data
	 * @param extraVariables
	 */
	private void displayDataTempMissingDataErrorMessage(ArrayList extraVariables){
		String DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE[] = new String[3 + extraVariables.size()];
		DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE[0] = new String("The files you selected cannot be merged, because the ");
		DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE[1] = new String("Data Template (problems) has missing data.");
		DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE[2] = new String("The variables that are missing are:");
		for (int i=0; i<extraVariables.size(); i++) {
			DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE[i+3] = (String) extraVariables.get(i);
		}

		if (trace.getDebugCode("mp")) trace.out("mp", "2 so displayDataTempMissingDataErrorMessage: DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE = " + DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE);
        displayWarningMsg(DATA_TEMPLATE_MISSING_DATA_ERROR_MESSAGE);
			
		return;

	}
	
	private void displayWarningMsg(String [] messageText) {
		
		if (messageText == null)
			return;
		
		JOptionPane.showMessageDialog(
	                        this,
							messageText,
	                        "Warning",
	                        JOptionPane.INFORMATION_MESSAGE);
		
		return;
	}
	// ----------------------------- END Warning Dialogs -------------------------
	
	
	// ----------------------------- Merging Data ----------------------------------
	/**
     * 	Create problems files (BRDs) by merging selected BRD Template
     *  and Data Template (Excel Configuration) Files and 
     *  save themin the selected folder.
     */
	
	private void createProblemsFilesWithVariablesReplace() {	
		if (trace.getDebugCode("mp")) trace.out("mp", "createProblemsFilesWithVariablesReplace : ");
		int problemsCreated = 0;
		
		// create problems .brd files 
		try {
					
			// loop through all problems in the configuration file
			for (int i=1; i<dataTempData.length; i++) {
                writeOneProblemFile(i);
                
                problemsCreated++;
			}
			
		} catch (java.io.IOException ioe) {
			if (trace.getDebugCode("mp")) trace.out("mp",  "IOException: " + ioe.toString());
            String message = "An error occurred while writing the output files.";
            String title = "Error writing files";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
            return;
        }		
		
        String title = "Problem Files Created";
        String message = "<html>"+problemsCreated+" problem file(s) were created in<br>"+
        		problemsFolder.toString()+"</html>";

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		return;
	}

    /**
     * @param problemsCreated
     * @param i
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void writeOneProblemFile(int i) throws FileNotFoundException, IOException {
    	if (trace.getDebugCode("mp")) trace.out("mp", "writeOneProblemFile : " + " i = " + i);
    	
    	FileReader massFileFr;
        BufferedReader massFileBr;
        OutputStream fout;
        String variableName;
        String variableValue;
        String tempStr;
        String problemNameConfig;
        HashMap vars;
        // reset vars
        vars = new HashMap();
        
        // build the variables data hash table
        for (int j=1; j<dataTempData[0].length; j++) {              // for (int j=1; j<=brdVariables.size(); j++) {       
        	// get the xml format variableName
        	variableName = dataTempData[0][j];
        	
        	variableName = XMLSpecialCharsTransform.transformSpecialChars(variableName);
        	
        	// get the variableValue
        	variableValue = dataTempData[i][j];
        	if (trace.getDebugCode("mp")) trace.out("mp", "writeOneProblemFile : " + " variableName = " + variableName  + " variableValue = " + variableValue);
        	
        	if (variableValue == null || variableValue.length() == 0)
        		variableValue = "";
        	
        	// 07-12-2006 chc  Fix CTAT 1518 - & gets converted to &amp; in Mass production
    		variableValue = XMLSpecialCharsTransform.transformSpecialChars(variableValue);
    		
    		variableValue = trimExtraDoubleQuotes (variableValue);
    		
    		vars.put(variableName, variableValue);
        	
        }
        
        // format the problem file full path
        String newProblemFilePath = problemsFolder.toString();				
        	
        int indexSeparator = newProblemFilePath.lastIndexOf(File.separator);
        
        if (indexSeparator != newProblemFilePath.length())
        	newProblemFilePath += File.separator;
        
        problemNameConfig = dataTempData[i][0];		
        
        newProblemFilePath += problemNameConfig;
        
        // make sure the problem file ends with ".brd"
        if (!newProblemFilePath.endsWith(".brd"))
        	newProblemFilePath += ".brd";
        
        if (trace.getDebugCode("mp")) trace.out("mp", "writeOneProblemFile : " + " newProblemFilePath = " + newProblemFilePath);
        
        
        // parsing out the problem name's .brd
        if (problemNameConfig.endsWith(".brd"))
        	problemNameConfig = problemNameConfig.substring(0, problemNameConfig.length() - 4);
        
        // create a problem file writer
        fout = new FileOutputStream(newProblemFilePath);
        
        // reopen the mass file buffer reader
        massFileFr = new FileReader(brdTemplateTextField.getText().trim());
        massFileBr = new BufferedReader(massFileFr);

        flagFoundProblemTag = false;
        flagFoundProblemTagClose = false;
        flagFoundFirstNodeTag = false;
        flagFoundFirstNodeText = false;
        
        // read from massFileBr & write to fout line by line
        while ((tempStr = massFileBr.readLine()) != null) {
        	
        	// process the problem name value for the tag "<ProblemName>"
        	if (!(flagFoundProblemTag && flagFoundProblemTagClose))
        		tempStr = replaceProblemNameTagValue(tempStr, problemNameConfig);
        	
        	if (!(flagFoundFirstNodeTag && flagFoundFirstNodeText))
        		tempStr = replaceStartNodeTextTagValue(tempStr, problemNameConfig);
        	
        	// replace variables in the string with corresponding configue data
        	tempStr = replaceVars(tempStr, vars);
        	tempStr += "\n";
        	
        	fout.write(tempStr.getBytes());
        }
        fout.close();
        
        massFileBr.close();
        massFileFr.close();
    }
	
	/**
     * If str has variables replcae them with 
     * corresponding configue data in HashMap vars.
     * @param str:-- proceesing string.
     * @param vars: -- HashMap with variables & configure data.
     * @return: -- replaced string.
     */
	
	private String replaceVars(String str, HashMap vars) {
//        Pattern varPattern = Pattern.compile("%([^ ]*?)%");
//        Pattern varPattern = Pattern.compile("%\\([^)][^)]*\\)%");
        Matcher varMatcher = varPattern.matcher(str);

        while (varMatcher.find()) {
            String value = null;
            if (varMatcher.group(0) != null) {
                value = (String) vars.get(varMatcher.group(0));
            }
            if (value != null) {
                str = varMatcher.replaceFirst(value);
                varMatcher = varPattern.matcher(str);
            }
        }
        return str;
    }
	
	/**
     * 	Fix the transformation Double qutoes bug when save Excel file as text file.
     *  Also precede dollar signs with backslash to avoid their being interpreted
     *  as backreferences in replacement strings: see {@link Matcher#replaceFirst(java.lang.String)}.
     * 	@param: inString read from text file
     * 
     * 	@return -- trimed text string:
     * 		1. trim the 3 consecutive Double quotes as 1 quote at the beginning and the ending.
     * 		2. trim the 1 Double quote at the beginning and the ending.
     * 		3. for inside 2 consecutive Double quotes trim one & keep one
     */
	
	private String trimExtraDoubleQuotes (String inString) {
		StringBuffer outStringbuf= new StringBuffer("");

		// John - I added the zero length check because we want to allow blank Data Template values.
		if (inString.length() > 0){
			// trim the 3 consecutive Double quotes as 1 quote at the beginning and the ending
			if (testFirst3CharsDoubleQuotes(inString)) {
				inString = inString.substring(2, inString.length() - 2);		
			} // trim the 1 Double quote at the beginning and the ending
			else if (inString.charAt(0) == '"') {
				inString = inString.substring(1, inString.length() - 1);
			}

			boolean firstQutoe = false;
			// for 2 consecutive Double quotes trim one & keep one
			for (int i=0; i<inString.length(); i++) {
				if (inString.charAt(i) == '"') {
					if (firstQutoe) {
						firstQutoe = false;
					}else {
						firstQutoe = true;
						outStringbuf.append(inString.charAt(i));
					}
				} else {
					firstQutoe = false;
					if (inString.charAt(i) == '$')   // avoid backreferences in
						outStringbuf.append('\\');   // values as replacement text
					outStringbuf.append(inString.charAt(i));
				}	
			}
		}
		
		return outStringbuf.toString();
	}
	
	/**
	 * Check if the first three characters are all quotes
	 * @param testString
	 * @return
	 */
	boolean testFirst3CharsDoubleQuotes(String testString) {
		
		if (testString.length() > 0)
		{
			// If any of the first 3 are NOT quotes, then were fine. Return false.
			for (int i=0; i<3; i++) {
				if (testString.charAt(i) != '"')
					return false;
			}
			return true;
		}
		else
			return false;
	}
	
	/**
     * 	Replace the value for the tag "<ProblemName>"
     * 
     * 	@param: strLine -- the current read line text.
     * 	@param: problemNameValue -- the problem name value from the configuration file.
     * 
     * 	@return -- replaced text.
     */
	
	private String replaceProblemNameTagValue(String strLine, String problemNameValue) {
		final String problemNameTag = "<ProblemName>";
		final String problemNameCloseTag = "</ProblemName>";
		
		String replacedStr = strLine;
		
		boolean flagBefore = flagFoundProblemTag;
		
		// process the tag problemNameTag
		if (!flagFoundProblemTag) {	
			int indexStartTag = strLine.indexOf(problemNameTag);
			if (indexStartTag >= 0) {
				// take the problemNameTag and problemNameValue
				replacedStr = strLine.substring(0, indexStartTag + problemNameTag.length());
				replacedStr += problemNameValue;
				
				flagFoundProblemTag = true;
			}
		}
		
		// process the tag problemNameCloseTag
		if (!flagFoundProblemTagClose) {
			int indexEndTag = strLine.indexOf(problemNameCloseTag);
			if (indexEndTag >= 0) {
				// append the tag problemNameCloseTag to the above replaced string
				if (!flagBefore)
					replacedStr += strLine.substring(indexEndTag);
				// take the tag problemNameCloseTag and remaining text
				else 
					replacedStr = strLine.substring(indexEndTag);
				
				flagFoundProblemTagClose = true;
			} 
			// strLine has no problemNameTag or problemNameCloseTag
			// replace with the empty string
			else if (flagBefore)
				replacedStr = "";
		}
		
		return replacedStr;
	}
	
	/**
     * 	Replace the value for the Start node text tag value by the 
     * 	configured problem name.
     * 
     * 	@param: strLine -- the current read line text.
     * 	@param: problemNameValue -- the problem name value from the configuration file.
     * 
     * 	@return -- replaced text.
     */
	
	private String replaceStartNodeTextTagValue(String strLine, String problemNameValue) {
		final String nodeTag = "<node";
		final String textTag = "<text>";
		final String textCloseTag = "</text>";
		
		String replacedStr = strLine;
		
		// check the nodeTag
		if (!flagFoundFirstNodeTag) {
			int indexNodeTag = strLine.indexOf(nodeTag);
			if (indexNodeTag >= 0) 
				flagFoundFirstNodeTag = true;
		} 
		
		// check the textTag
		if (flagFoundFirstNodeTag) {
			if (!flagFoundFirstNodeText) {
				int indexTextTag = strLine.indexOf(textTag);
				if (indexTextTag >= 0) {
					replacedStr = strLine.substring(0, indexTextTag + textTag.length());
					replacedStr += problemNameValue;
					
					// assume the text tag and value in the same line
					int TextTagClose = strLine.indexOf(textCloseTag);
					if (TextTagClose >= 0) {
						replacedStr += strLine.substring(TextTagClose);
					}
					
					flagFoundFirstNodeText = true;
				} 
			}
		}
		
		return replacedStr;
	}
	// ----------------------------- END Merging Data ----------------------------------
	
	
	// ----------------------------- Gathering Variables & Data ----------------------------------
	/**
     * 	process the BRD Template and get the variables.	
     * 
     * 	@param: templatFilePath -- the BRD template file (i.e., BRD file with variables)..
     */
	private void collectBRDTempVars(String templatFilePath) {
		if (trace.getDebugCode("mp")) trace.out("mp", "1 buildVariables : intro : file = " + templatFilePath);
		// clear variables
		brdVariables.clear();
		
		templatFilePath = templatFilePath.trim();
		
		FileReader fr;
        BufferedReader br;
        
        try {
            fr = new FileReader(templatFilePath);
        	br = new BufferedReader(fr);
        	Set<String> brdVarsSet = new LinkedHashSet<String>(); 
        	String strLine = br.readLine();
        	while (strLine != null) {
        		strLine = XMLSpecialCharsTransform.transformBackSpecialChars(strLine);
                ArrayList variableList = fetchVariables(strLine);
        		
        		// Add each mp variable to a list.
                for (int i = 0; i < variableList.size(); i++) {
            		String tempStr = (String) variableList.get(i);               	
                	if (!brdVarsSet.contains(tempStr))
                        brdVarsSet.add(tempStr);
                }
        		strLine = br.readLine();
        	}
        	brdVariables.addAll(brdVarsSet);
        	br.close();
        	fr.close();
        	
 //       	trace.out("mp", "buildVariables : size = " + brdVariables.size() + " 1 = " + brdVariables.get(1));
        	
        } catch (FileNotFoundException fe) {
            if (trace.getDebugCode("mp")) trace.out("mp",  "FileNotFoundException: " + fe.toString());
        } catch (IOException ie) {
        	if (trace.getDebugCode("mp")) trace.out("mp",  "IOException: " + ie.toString());
        }
	
		return;
	}

	/**
     * 	Check whether the Mass Production File 
     * 	selection is valid.
     * 	@reurn true if:
     * 		a. its path is not empty.
     * 		b. its variables list is not empty.
     * 	else return false.
     */
	
	/**
     * 	Read and parse the Data Template (configuration) data from the selected Excel Configuration file.
     *  Compare Data Template variable list with the BRD Template list. 
     * 	@param configFilePath: selected Excel Configuration file full path.
	 *  @return 
     */
	
	private boolean collectDataTempVars(String problemsTableFile) {
		
		// clear configurationData
		dataTempData = null;
		
		try {
            BufferedReader br;
			br = new BufferedReader(new FileReader(problemsTableFile));
			
			// read all text from Excel Configuration file
			String allText = "";
			
			String strLine = br.readLine();
			
			//int index = parsingStr.indexOf("\n\t");
			while (strLine != null) {
//				strLine = strLine.trim(); sewall 2014-09-08, trac #498: don't remove trailing blank
				strLine = removeEndTabs(strLine);
				allText += "\n" + strLine;
				strLine = br.readLine();
			}
			
			// parse Data template (Configuration) file data
			dataTempData = parseConfigData(allText);
			br.close();
		} catch (FileNotFoundException fe) {
            trace.out("FileNotFoundException: " + fe.toString());
            return false;
        } catch (IOException ie) {
			// clear incomplete configurationData
			dataTempData = null;
			trace.out("IOException: " + ie.toString());
            return false;
        }
		
		// Exception: Here is where we trigger the lame message about the files you selectc cannot be merged. 
		if (dataTempData == null) {
			// displayWarningMsg(ERROR_MESSAGE);
			return false;
		}
		
		// Compare BRD Template variabes with Data Template variables.
		int dataMatchCheck = dataMatch(); 
		
		if (trace.getDebugCode("mp")) trace.out("mp","collectDataTempVars :" + " dataMatchCheck = " + dataMatchCheck);
			
		// 0 final int NO_VARIABLES = 0;
		// 1 final int NO_CONFIGURE_DATA = 1;
		// 2 final int BRD_TEMPLATE_VARIABLES_ABSENT = 2;
		// 3 final int DATA_TEMPLATE_VARIABLES_ABSENT = 3;
		// 4 final int DATA_TEMPLATE_MISSING_DATA = 4;
		// 5 final int DATA_MATCH = 5
		if (dataMatchCheck == NO_VARIABLES) {
			displayNoBRDTemplateVariablesErrorMessage();
            return false;
        }
		else if (dataMatchCheck == NO_CONFIGURE_DATA) {
			displayNoDataTemplateVariablesErrorMessage();
            return false;
        }
		else if (dataMatchCheck == BRD_TEMP_VARS_ABSENT_FROM_DATA_TEMP) {
			displayBRDTempVarsMissingFromDataTempErrorMessage(extraBRDvars);
            return true;
        }
		else if (dataMatchCheck == DATA_TEMPLATE_VARIABLES_ABSENT) {
			displayDataTempVarsMissingFromBRDTempErrorMessage(extraDatavars);
            return true;
        }
		else if (dataMatchCheck == DATA_TEMPLATE_MISSING_DATA) {
			displayDataTempMissingDataErrorMessage(badDatavars);
            return false;
        }
		if (dataMatchCheck == DATA_MATCH) {
			return true;
        }
		else {
			return true;
		}
	}
	
	/**
     * 	Parse the data template (e.g., Excel file) data its all text.
     * 
     * 	@param allText: selected Excel Configuration file all text.
     * 
     * 	@return: 2-dimensional String array:
     * 		a. 1st row is the list of all variables
     * 		b. 2nd to the last row: each row has a problem configuration data.
     * 		c. On each row: 
     * 			i). 1st col. for the problem name.
     * 			ii). 2nd col. to the last col.: variables values.
     * 
     * 	return null if no data or data is incomplete.
     */
	
	private String [][] parseConfigData(String allText) {
		// hold configuration data
		String configdata[][];
		
		// 2-dimensional array row & col sizes
		int rowNumber = 0;
		int colNumber = 0;
		
		// get each row data, delimitered by '\n' or '\r'
		StringTokenizer rowTk = new StringTokenizer(allText, "\n\r");
		
		// Quit: the file is empty
		if (!rowTk.hasMoreElements()){
			displayWarningMsg(EMPTY_FILE_MESSAGE);
			return null;
		}
		
		// number of rows
		rowNumber = rowTk.countTokens();
		
		// Cell data: get 1st row delinitered by '\t'
		String rowString = "";
		rowString = (String) rowTk.nextElement();
		rowString.trim();
		rowString = removeEndTabs(rowString);
		StringTokenizer rowCellTk = new StringTokenizer(rowString, "\t");
		
		
		// Alt: use split to get an array.
		String[] rowCellTk2 = rowString.split("\t");
				
		// get 1st row data, delinitered by '\t': this method counts consecutive
		// delimiters as empty tokens
		String[] rowCellTkArr = rowString.split("\t", -2);
		
		// empty
		if (!rowCellTk.hasMoreElements())
			return null;

		
		// number of data cells on the row
		colNumber = rowCellTk.countTokens();
		
		if (rowCellTkArr.length > colNumber)
			colNumber = rowCellTkArr.length;
		
		// Quit: no real configuration data
		if (rowNumber <= 1 || colNumber <= 1){
			displayWarningMsg(EMPTY_FILE_MESSAGE);
			return null;
		}
		
		// Problem Table into an 2D Array
		configdata = new String[rowNumber][colNumber];
		
		// flag: variables on the 1st row or 1st column
		boolean variablesInRow = false;
		
		// Row 1: get the row cell data : headers
		String cellData;
		int j = 0;
		while (rowCellTk.hasMoreElements()) {
			cellData = (String) rowCellTk.nextElement();
			
			// test whether the variables are on the 1st column or row
			if (j == 1) {
				if (cellData.startsWith(VAR_LEFT_DELIMITER) && cellData.endsWith(VAR_RIGHT_DELIMITER)){ 
					variablesInRow = true;
				}
			}
			configdata[0][j++] = cellData;
		}	
		
		
		// Row 2 to N: loop for each row to get data
		emptyVars.clear();
		String pos;
		
		int i = 1;	// row index, base zero
		while (rowTk.hasMoreElements()) {
			// for each row
			rowString = (String) rowTk.nextElement();
//			rowString = rowString.trim(); sewall 2014-09-08 trac #498: don't remove trailing blank
			rowString = removeEndTabs(rowString);
			
			// Values in a Row:
			List<String> rowData = getRowConfigureData(rowString, excelJCheckbox.getModel().isSelected());

			if (trace.getDebugCode("mp")) trace.out("mp", "configdata["+i+"] = "+rowData);
			
			// loop for the row data
			for (j=0; j<rowData.size(); j++) {
				cellData = (String) rowData.get(j);			
				
				if (cellData == null|| cellData.length() == 0){
					int row = i + 1;
					int col = j + 1;
					pos = "row " + row + " column " + col;
					emptyVars.add(new String("row " + row + " column " + col));	// create a list of the position of empty cells. There must be a better way :)
					cellData = "";		// Set default
					if (trace.getDebugCode("mp")) trace.out("mp", "parseConfigData : " + " pos = " + pos  );
				}
				
				// TODO:  Note sure why this block is here.
				if (variablesInRow) { 			// validate the row case data | TODO find out why we care if j or i is <= brdVariables
					if (j <= brdVariables.size() && (cellData == null|| cellData.length() == 0)){
						//createWarningMsg(i,j);
						//return null;
					}
				} else {
					// validate the column case data
					if (i <= brdVariables.size() && (cellData == null || cellData.length() == 0)){ 
						//createWarningMsg(i,j);
						//return null;
					}
				}
				configdata[i][j] = cellData;
			}
			i++;
		}
		if (emptyVars.size() > 0){
			if (trace.getDebugCode("mp")) trace.out("mp", "parseConfigData : TRUE empty cells : emptyVars.size() = " + emptyVars.size() + "  variablesInRow = " + variablesInRow );
			for (int k=0; k<emptyVars.size(); k++) {
				if (trace.getDebugCode("mp")) trace.out("mp","parseConfigData :" + (String) emptyVars.get(k));
			}
			createWarningMsg(emptyVars);
		}
		
		
		if (!variablesInRow) {					// rotate row & col data
			String rotateConfigdata[][] = new String[colNumber][rowNumber];
			
			for (int l=0; l<rotateConfigdata.length; l++)
				for (int m=0; m<rotateConfigdata[l].length; m++)
					rotateConfigdata[l][m] = configdata[m][l];
			configdata = rotateConfigdata;
		}
		
		// CTAT1521: check row w/ problem names
		for (i = 0; i < configdata.length; ++i) {
			if (configdata[i][0] == null || configdata[i][0].length() < 1){
				createMissingProblemNameWarningMsg(i,0);
				return null;
			}
		}
		return configdata;
	}
	
	/**
	 * Look for tabs at the end of a line and strip them.
	 * Similar to the trim method of String but that method gave problems.
	 * @param rowString
	 * @return	The string with trailing tabs removed.
	 */
	private String removeEndTabs(String rowString)
	{
		StringBuffer lcRowString = new StringBuffer(rowString);
		for(int i = lcRowString.length()-1; lcRowString.charAt(i) == '\t'; --i)
		{	
			lcRowString.deleteCharAt(i);
		}
		return lcRowString.toString();
	}
	
	/**
	 * If a Data Template has missing data, generate a warning.
	 * @param emptyVarsList = a list of the positions (row & col) where the data is empty.
	 */
	//private void createWarningMsg(int i, int j){
	private void createWarningMsg(ArrayList emptyVarsList){
		//String si = (new Integer(i+1)).toString();
		//String sj = (new Integer(j)).toString();
			
		String emptyCellMsg[] = new String[2 + emptyVarsList.size()];
		emptyCellMsg[0] = "The problem table includes empty cell(s) at";
		for (int i=0; i<emptyVarsList.size(); i++) {
			if (trace.getDebugCode("mp")) trace.out("mp","createWarningMsg :" + (String) emptyVarsList.get(i));
			emptyCellMsg[i+1] = (String) emptyVarsList.get(i);
		}
		emptyCellMsg[1+emptyVarsList.size()] = "The merge process will insert blank values.";
		displayWarningMsg(emptyCellMsg);
		
		return;
	}
	
	/**
	 * If a problem name is missing from the data, generate a specfic warning.
	 * @param i	= row number
	 * @param j	= column number
	 */
	private void createMissingProblemNameWarningMsg(int i, int j){
		String si = (new Integer(i+1)).toString();
		String sj = (new Integer(j)).toString();
		String emptyCellMsg[] = {
				"The problem table includes a missing problem name at row: " + si,
				"and column : " + sj,
				" Please check the file and complete the problem name."};
		displayWarningMsg(emptyCellMsg);
		
		return;
	}
	
	/**	Tab pattern for {@link #getRowConfigureData(String)}. */
	private static final Pattern Tab = Pattern.compile("\t");

	/**
	 * Parse the cells of a row into a ArrayList.
	 * @param rowDataString
	 * @param removeExcelQuotes if true, remove double-quotes added by Excel for escaping
	 * @return an ArrayList containing a list of values from a row of the Data Template
	 */
	List<String> getRowConfigureData(String rowDataString, boolean removeExcelQuotes) {
		
		String[] cells = Tab.split(rowDataString, 0);  // don't add trailing empty strings
		for (int i = 0; i < cells.length; ++i) {
			if (!removeExcelQuotes || !(cells[i].contains("\"")))
				continue;
			StringBuffer sb = new StringBuffer(cells[i]);
			if (sb.charAt(0) == '\"')
				sb.deleteCharAt(0);
			int end = sb.length() - 1;
			if (end >= 0 && sb.charAt(end) == '\"')
				sb.deleteCharAt(end);
			for (int j = 0; j < sb.length()-1; ++j) {
				if (sb.charAt(j) == '\"' && sb.charAt(j+1) == '\"')
					sb.deleteCharAt(j+1);
			}
			cells[i] = sb.toString();
		}
		return Arrays.asList(cells);
	}
	
	
	
	/**
	 *	Test whether BRD Template variables data matches Data Template (configurationData) variables.
	 *
     * 	@return -- DATA_MATCH if 
     * 					a. each BRD template variable has a match in Data template variables (configurationData)
     * 					b. each Data template variable (configurationData has a match in BRD template variable.
     * 			-- any other error cases. 
     */
	
	private int dataMatch (){

		int result = DATA_MATCH;
		// 0 final int NO_VARIABLES = 0;
		// 1 final int NO_CONFIGURE_DATA = 1;
		// 2 final int BRD_TEMPLATE_VARIABLES_ABSENT = 2;
		// 3 final int BRD_TEM_VARS_ABSENT_FROM_DATA_TEMP = 3;
		// 4 final int DATA_TEMPLATE_MISSING_DATA = 4;
		// 5 final int DATA_MATCH = 5
		
		// 0 no variable data
		if (brdVariables == null)
			return NO_VARIABLES;
		
		// 1
		if (dataTempData == null)
			return NO_CONFIGURE_DATA;

		// test whether each BRD Template variable is in configure variable array.
		String brdVar;
		String dataVar;
		boolean foundFlag;
		
		
		
		// 2 Look for BRD Template variables that are absent from Data Template
		extraBRDvars.clear();
		for (int i=0; i<brdVariables.size(); i++) {
			brdVar = (String)brdVariables.get(i);
			
			foundFlag = false;
			for (int j=1; j < dataTempData[0].length && !foundFlag; j++) {
				dataVar = dataTempData[0][j];
				if (brdVar.equals(dataVar))
					foundFlag = true;				
			}
			
			if (!foundFlag)
				extraBRDvars.add(brdVar);

		}
		if (extraBRDvars.size()!= 0) {
			/* fix this before returning below: see call to extendDataArray() */ 
			result = BRD_TEMP_VARS_ABSENT_FROM_DATA_TEMP; 
		}
		
		
		// 3 Look for Data Template (configuration) variables that are absent from BRD Template
		extraDatavars.clear();
		for (int i=1; i<dataTempData[0].length; i++) {
			dataVar = dataTempData[0][i];

			foundFlag = false;
			for (int j=0; j<brdVariables.size() && !foundFlag; j++) {
				brdVar = (String)brdVariables.get(j);
				if (dataVar.equals(brdVar))
					foundFlag = true;				
			}
			
			if (!foundFlag)
				extraDatavars.add(dataVar);
		}
		if (extraDatavars.size()!= 0){
			return DATA_TEMPLATE_VARIABLES_ABSENT;
		}

		// 4 Look for missing data in Data Template : This is handled in parseConfigData
		
		// Extend data table with vars defined only in brd template
		if (result == BRD_TEMP_VARS_ABSENT_FROM_DATA_TEMP)
			dataTempData = extendDataArray(dataTempData, extraBRDvars);
		// 5 Success !!
		return result;
	}
	
	/**
	 * Extend an array like {@link #dataTempData} with new variables from a List,
	 * setting the value of each new variable to the empty string.
	 * @param dataTable
	 * @param extraVars
	 * @return new dataTable, now lengthened
	 */
	private String[][] extendDataArray(String[][] dataTable, List<String> extraVars) {
		final int oldNVars = dataTable[0].length;
		int nColumns = dataTable.length;
		String[][] result = new String[nColumns][];
		int newNVars = dataTable[0].length+extraVars.size();
		for (int c = 0; c < nColumns; ++c) {
			result[c] = new String[newNVars];
			for (int r = 0; r < dataTable[c].length; ++r)
				result[c][r] = dataTable[c][r];
			for (int r = oldNVars; r < newNVars; ++r)
				result[c][r] = "";
		}
		return result;
	}

	/**
	 *	parse the mass production variables from a string of text
	 *	@param strLine: the read line string text. (from BRD template file).
	 *	@return: ArrayList of mass production variables from the passing string.
     */
	
	private ArrayList fetchVariables(String strLine) {
		ArrayList variableList = new ArrayList();
		
//		Pattern varPattern = Pattern.compile("%([^ ]*)%");
//        Pattern varPattern = Pattern.compile("%\\([^)][^)]*\\)%");
		Matcher varMatcher = varPattern.matcher(strLine);
		
		String tempStr;
		
		// Loop over each variables.
		while (varMatcher.find()) {
			if (varMatcher.group(0) != null) {
				tempStr = varMatcher.group(0);
				
				// filter out the composite variable
				int index = tempStr.lastIndexOf(VAR_LEFT_DELIMITER);
				if (index == 0) {
					index = tempStr.indexOf(VAR_RIGHT_DELIMITER);
					if (index == tempStr.length() - 2)
						variableList.add(tempStr);
				}
				strLine = varMatcher.replaceFirst(" ");
				varMatcher = varPattern.matcher(strLine);
			}
		}
		
		return variableList;
	}
	// ----------------------------- END Gathering Variables & Data ----------------------------------	
}
