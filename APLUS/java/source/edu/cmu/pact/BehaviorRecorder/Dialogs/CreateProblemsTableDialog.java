package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.XMLSpecialCharsTransform;
import edu.cmu.pact.Utilities.trace;

/////////////////////////////////////////////////////////////////////////
/**
 * The class supports creating a problem tables for Mass Production
 */
/////////////////////////////////////////////////////////////////////////

public class CreateProblemsTableDialog extends JDialog {
    private ArrayList variables = new ArrayList();
    
    private JLabel     brdTemplateLabel = new JLabel("Select the BRD template:");
    private JTextField brdTemplateTextField = new JTextField();
    { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(brdTemplateTextField); }
    private JButton    brdTemplateBrowseButton = new JButton("Browse...");
    
    private JButton createButton = new JButton("Create");
    private JButton cancelButton = new JButton("Cancel");
    
    private Container contentPane = getContentPane();
    
    private String instructions = "<html>" +
    "Use this window to create a problems table for mass production.  " +
    "Mass production allows you to easily produce many Behavior Recorder graphs that use a single student interface." +
    " The BRD template is a normal BRD file with variable names in place of constant values in the student input, hints, etc. ";
    
    private BR_Controller controller;
    
    private File problemTableFile;

	public static final String PROBLEMS_TABLE_LOCATION = "Problems Table Location";
    
    static final String BRD_TEMPLATE_PATH = "BRD Template Path";
    
    public CreateProblemsTableDialog (BR_Controller controller) {
        super(controller.getActiveWindow(), true);
        this.controller = controller;
        this.setTitle("Create Problems Table for Mass Production");
        
        initUI();
    }
    
    /**
     * 
     */
    private void initUI() {
        
        int headerHeight = 100;
        
        int leftAlign = 20;
        
        int rowSpace = 30;
        int broweSpace = 15;
        
        int labelWidth = 400;
        int textFiledWidth = 400;
        int buttonWidth = 100;
        int fieldHeight = 20;
        
        int buttonLeft = 320;
        
        Font labelFont = new java.awt.Font("", 1, 12);
        
        int dialogWidth = 555;
        int dialogHeight = 230;
        
        contentPane.setLayout(null);
        
        
        JLabel instructionslabel = new JLabel (instructions);
        instructionslabel.setBounds(leftAlign, 0, 450, 90);
        contentPane.add(instructionslabel);
        
        // format selectProblemTemplateJLabel
        brdTemplateLabel.setLocation(leftAlign, headerHeight);
        brdTemplateLabel.setFont(labelFont);
        brdTemplateLabel.setSize(labelWidth, fieldHeight);
        contentPane.add(brdTemplateLabel);
        
        // format selectProblemTemplateJTextField
        brdTemplateTextField.setLocation(leftAlign, headerHeight + rowSpace);
        brdTemplateTextField.setSize(textFiledWidth, fieldHeight);
        //selectProblemTemplateJTextField.setEditable(false);
        brdTemplateTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectTemplateFileTextFieldActionPerformed();
            }
        });
        contentPane.add(brdTemplateTextField);
        brdTemplateTextField.setText(controller.getProblemModel().getProblemFullName());
        
        // format selectProblemTemplateJButton
        brdTemplateBrowseButton.setLocation(leftAlign + brdTemplateTextField.getWidth() + broweSpace, 
                headerHeight + rowSpace);
        brdTemplateBrowseButton.setSize(buttonWidth, fieldHeight);
        contentPane.add(brdTemplateBrowseButton);
        brdTemplateBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectProblemTemplateActionPerformed();
            }
        });
        
        // format createJButton
        createButton.setLocation(buttonLeft, headerHeight + 2 * rowSpace + 10);
        createButton.setSize(buttonWidth, fieldHeight);
        contentPane.add(createButton);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createButtonActionPerformed();
            }
        });
        
        // format cancelJButton
        cancelButton.setLocation(leftAlign + brdTemplateTextField.getWidth() + broweSpace, headerHeight + 2 * rowSpace + 10);
        cancelButton.setSize(buttonWidth, fieldHeight);
        contentPane.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        setSize(dialogWidth, dialogHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        
        setVisible(true);
    }
    
    /**
     * 
     */
    private void createButtonActionPerformed() {
        
        final String brdTemplateFilename = brdTemplateTextField.getText();
        
        boolean keepGoing = promptToSaveBRDSilently(brdTemplateFilename, controller, this, "Next please specify the name of your new problems table file.");

        if (!keepGoing)
            return;

        if (!processProblemTemplateData(brdTemplateFilename))
            return;
        
        // invalid variables data
        if (!checkVariablesNotEmpty())
            return;
          
        String targetDir = controller.getPreferredBRDLocation();
        String DefaultSelectedTextFileName = brdTemplateFilename.replaceAll(".brd", ".txt");
        File f = DialogUtilities.chooseFile(targetDir, DefaultSelectedTextFileName, null,
        		"Select Problem Table Filename", "Save", controller);
        if (f == null)
        	return;
        problemTableFile = f;
        
        String name = problemTableFile.getAbsolutePath();
        if (! name.endsWith(".txt")) {
            name += ".txt";
            problemTableFile = new File (name);
        }
        trace.out ("name = " + problemTableFile);
        
        // create a .txt file with all variables delimited by '\t'
        try {
            writeProblemsTableFile();
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
            String title = "Error reading file";
            String message = "<html>The system was unable to write to the selected file." +
                    "<br>" +
                    "Please check to make sure it is not open in another program.";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
            close();
            return;
        }		
        controller.getPreferencesModel().setStringValue(PROBLEMS_TABLE_LOCATION, problemTableFile.getAbsolutePath());
        close();
    }

	private void writeProblemsTableFile() throws FileNotFoundException, IOException {
		OutputStream fout = new FileOutputStream(problemTableFile);
		
		String tempStr = "Problem Name";
		fout.write(tempStr.getBytes());
		
		for (int i=0; i<variables.size(); i++) {
		    tempStr = (String) variables.get(i);
		    
		    tempStr = XMLSpecialCharsTransform.transformBackSpecialChars(tempStr);
		    
		    // put variables in col or row
		    tempStr = "\n" + tempStr;
		    
		    
		    fout.write(tempStr.getBytes());
		}
		
		fout.close();
	}

    /**
     * Check to see if the current file in the behavior recorder is the same as the file being
     * loaded in the dialog.  If so then prompt the user to save the file.
     * 
     * @param brdTemplateFilename
     * @param controller2
     * 
     * @return True if the user wants to save, false if the user wants to cancel or an error occurs
     */
    public static boolean promptToSaveBRDSilently(final String brdTemplateFilename,
    		final BR_Controller controller2, Component parent, String userMessage) {
        File testTemplate = new File (brdTemplateFilename);
        File testCurrentBRD = new File (controller2.getProblemModel().getProblemFullName());
        
        
        if (! testTemplate.equals(testCurrentBRD))
            return true;
        

        String message = "Save the current BRD file?";
        String title = "Save File";
        int i = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION);
        
        if (i != JOptionPane.OK_OPTION)
            return false;
        
        String filename = controller2.saveBRDSilently();
        if (filename == null)
        	return false;

        JOptionPane.showMessageDialog(parent, "<html>The BRD file has been saved.<br><br>" + userMessage);
        return true;
    }
    
    /**
     * 
     */
    private void close() {
        setVisible(false);
        dispose();
    }
    
    
    /**
     * 
     */
    private void selectProblemTemplateActionPerformed() {
        
        File selectedFile = DialogUtilities.chooseFile(controller.getPreferredBRDLocation(),
        		new BrdFilter(), "Select BRD Template File", "Select", controller); 
        
        if (selectedFile != null) {
            brdTemplateTextField.setText(selectedFile.getPath());
            
            processProblemTemplateData(selectedFile.getPath());
            
            final String folder = selectedFile.getParent();
            controller.getPreferencesModel().setStringValue(BRD_TEMPLATE_PATH, selectedFile.getAbsolutePath());
            controller.setPreferredBRDLocation(folder);
        }
    }
    
    /**
     * 	Build variables from the selected 
     * 	Mass Production file selection.
     * 	
     * 	@param: templatFilePath -- selected Mass Production file path.
     */
    
    private boolean processProblemTemplateData(String templatFilePath) {
        
        // no file path
        if (templatFilePath == null
                || templatFilePath.length() == 0) {
            String message [] = {"You must select the BRD template file."};
            
            displayWarningMsg(message);
            
            return false;
        }
        
        // invalid mass production file
        if (!templatFilePath.endsWith(".brd")) {
            trace.out(5, this, "You selected file:\n" + templatFilePath);
            
            String message[] = {"The BRD template file type should be \".brd\".",
            "Please reselect your file."};
            
            displayWarningMsg(message);
            
            return false;
        }
        
        File templateFile = new File (templatFilePath);
        
        // selected file doesn't exist.
        if (!templateFile.exists()) {
            trace.out(5, this, "You selected file:\n" + templatFilePath);
            
            String message[] = {"Your selected BRD template file does not exist.",
            "Please reselect your file."};
            
            displayWarningMsg(message);
            
            return false;
        }
        
        // selected file is really a folder
        if (templateFile.isDirectory()) {
            trace.out(5, this, "You selected folder:\n" + templatFilePath);
            String message[] = {"You selected a folder not a file.",
            "Please reselect your file."};
            
            displayWarningMsg(message);
            
            return false;
        }
        
        // clear variables
        variables.clear();
        
        parsingAllVariables(templatFilePath);
        
        return checkVariablesNotEmpty();
        
    }
    
    /**
     * 	Check whether variables has real data
     * 
     * 	@reurn true if variables has real data.
     */
    private boolean checkVariablesNotEmpty() {
        
        // no variables in the selected mass production file 
        if (variables.size() == 0) {
            String message[] = {"No valid variables were found in your",
                    "selected mass production file.",
            "Please select a new file."};
            
            displayWarningMsg(message);
            
            return false;
        }
        
        return true;
    }
    
    
    /**
     *	Display the warning message.
     *
     *	@param messageText: message text to be displaied.
     */
    
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
    
    /**
     *	Parsing out all variables from the selected Mass Production file.
     *
     *	@param filePath: selected Mass Production file full path.
     */
    
    private void parsingAllVariables (String filePath) {
        
        FileReader fr;
        
        BufferedReader br;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String strLine = br.readLine();
            while (strLine != null) {
                addToVariables(fetchVariabls(strLine));
                strLine = br.readLine();
            }
            
            br.close();
            fr.close();
            
            for (int i=0; i<variables.size(); i++) 
                trace.out(5, this, "variable = " + variables.get(i));
            
        } catch (FileNotFoundException fe) {
            trace.out(5, this, "FileNotFoundException: " + fe.toString());
        } catch (IOException ie) {
            // clear incomplete variables data
            variables = new ArrayList();
            trace.out(5, this, "IOException: " + ie.toString());
        } 
        
        return;
    }
    
    
    /**
     *	fetch out variables from the read string text
     *	
     *	@param strLine: the read line string text.
     *
     *	@return: ArrayList for variables from the passing string.
     */
    
    public ArrayList fetchVariabls(String strLine) {
        ArrayList variableList = new ArrayList();
        
        Pattern varPattern = Pattern.compile("%([^ ]*?)%");
        Matcher varMatcher = varPattern.matcher(strLine);
        
        String tempStr;
        
        while (varMatcher.find()) {
            if (varMatcher.group(0) != null) {
                tempStr = varMatcher.group(0);
                // filter out the composite variable
                int index = tempStr.lastIndexOf("%(");
                if (index == 0) {
                    index = tempStr.indexOf(")%");
                    if (index == tempStr.length() - 2)
                        variableList.add(tempStr);
                }
                strLine = varMatcher.replaceFirst(" ");
                varMatcher = varPattern.matcher(strLine);
            }
        }	
        
        return variableList;
    }
    
    
    /**
     *	Add the new variables list into the total variables.
     *	
     *	@param variableList: the new fetched variables list.
     */
    
    private void addToVariables(ArrayList variableList) {
        if (variableList == null)
            return;
        
        String tempStr;
        for (int i=0; i<variableList.size(); i++) {
            tempStr = (String) variableList.get(i);
            if (!isInArrayList(variables, tempStr))
                variables.add(tempStr);
        }
        
        return;
    }
    
    /**
     *	Test whether a string is an element in an ArrayList
     *	
     *	@param strList -- an ArrayList.
     *	@param str -- a string text.
     *
     *	@return -- true if str matches an element in strList.
     */
    
    public boolean isInArrayList(ArrayList strList, String str) {
        if (strList == null)
            return false;
        
        String tempStr;
        for (int i=0; i<strList.size(); i++) {
            tempStr = (String) strList.get(i);
            if (tempStr.equals(str))
                return true;
        }
        
        return false;
    }
    
    /**
     * 
     */
    private void selectTemplateFileTextFieldActionPerformed() {
        processProblemTemplateData(brdTemplateTextField.getText().trim());
    }
    
}
