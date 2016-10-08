package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Prompt the user for a file or directory from which to read a 
 * set of {@link RuleProduction} instances for the list maintained
 * in {@link BR_Controller#getRuleProductionCatalog()}.  If the user
 * selects a text file, try to read it according to the format in
 * {@link #loadProductionRules(String, BR_Controller)}.  Otherwise
 * the user should choose a single .brd file or a directory, to read
 * read all the .brd files in the subtree rooted at that directory. 
 */
public class LoadProductionRulesDialog {

	private File fi;

    private BR_Controller controller;

    public LoadProductionRulesDialog(BR_Controller controller) {
    	this.controller = controller;
        
        int clearRules = JOptionPane.showConfirmDialog(controller.getActiveWindow(),
        		"Remove all existing production rules before loading?",
        		"Load Production Rules", 
        		JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (clearRules == JOptionPane.CANCEL_OPTION)
        	return;
        if (clearRules == JOptionPane.YES_OPTION)
            controller.getRuleProductionCatalog().clear();

        // Prompt for a text file of rules, a .brd file or a directory of .brd files.
        FileFilter brdOrTxtFilter = new FileFilter() {
        	FileFilter brdFilter = new BrdFilter();
        	FileFilter txtFilter = new TxtFilter();
        	public boolean accept(File f) {
        		return brdFilter.accept(f) || txtFilter.accept(f);
        	}
        	public String getDescription() {
        		return "Behavior Graph files (.brd) or Production Rule lists (.txt)";
        	}
        };
        String initialDirectory = SaveFileDialog.getProjectsDirectory(controller);
        fi = DialogUtilities.chooseFile(initialDirectory, null, brdOrTxtFilter, 
        		"Choose a file or a directory (of .brd files) to scan", "Open",
        		DialogUtilities.FILES_AND_DIRECTORIES, controller.getActiveWindow());
        if (fi == null)
        	return;

        String fileType = ".brd";
        int fileCount = 0;
    	int ctlrRuleCount = getNControllerRules();  // prior count, to calc gain
    	int fileRuleCount = 0;
    	String message = "";

    	if (fi.getPath().endsWith(".txt")) {
    		fileType = ".txt";
    		fileCount = 1;
        	fileRuleCount = loadProductionRules(fi.getPath(), controller);
        	message = "Found "+fileRuleCount+" rules in "+fi.getPath()+".";
        } else {
        	List<File> brdFiles = Utils.findFiles(fi, new BrdFilter());
        	fileCount = brdFiles.size();
        	for (File brdFile : brdFiles) 
        		fileRuleCount += loadProblemRules(brdFile.getPath(), controller);
        	message = "Found "+fileRuleCount+" rules in"+
        			(fi.getPath().endsWith(".brd") ? " " : " "+fileCount+" .brd file(s) in ")+
        			fi.getPath()+".";
        }
    	int ruleGain = getNControllerRules() - ctlrRuleCount;
    	JOptionPane.showMessageDialog(controller.getActiveWindow(),
    			message+"\nLoaded net "+ruleGain+" new rules.", 
    			"Loaded production rules from "+fileType+" file"+(fileCount > 1 ? "s" : ""),
    			JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * @return {@link #controller}.{@link RuleProduction.Catalog}.size()
     */
    private int getNControllerRules() {
    	RuleProduction.Catalog ctlrRules = controller.getRuleProductionCatalog();
    	return (ctlrRules == null ? 0 : ctlrRules.size());
	}

    /**
     * Read {@link RuleProduction} entries from a text file. The file must have the following
     * format:
     * <table>
     *   <tr><td>Line 1:</td><td>&nbsp;</td><td>total number of rules in file</td></tr>
     *   <tr><td>Line 2:</td><td>Rule 1</td><td>rule 1 name (simple name)</td></tr>
     *   <tr><td>Line 3:</td><td>Rule 1</td><td>rule 1 production set (category)</td></tr>
     *   <tr><td>Line 4:</td><td>Rule 1</td><td><tt>save the hints for this RuleProduction</tt></td></tr>
     *   <tr><td>Line 5:</td><td>Rule 1</td><td>number of hints for rule 1</td></tr>
     *   <tr><td>Lines 6 to h1:</td><td>Rule 1</td><td>text of 1st hint for rule 1</td></tr>
     *   <tr><td>Line h1+1:</td><td>Rule 1</td><td><tt>This Hint Message Done</tt></td></tr>
     *   <tr><td>Lines h1+2 to h2:</td><td>Rule 1</td><td>text of 2nd hint for rule 1</td></tr>
     *   <tr><td>Line h2+1:</td><td>Rule 1</td><td><tt>This Hint Message Done</tt></td></tr>
     *   <tr><td>Lines h3 to hN+1:</td><td>Rule 1</td><td>... <i>repeat above pair for more hints</i></td></tr>
     *   <tr><td>Lines hN+2 to r1end-1:</td><td>Rule 1</td><td>text of production rule 1</td></tr>
     *   <tr><td>Line r1end:</td><td>Rule 1</td><td><tt>This Production Rule Done</tt></td></tr>
     *   <tr><td>Lines r1end+1 to r2end:</td><td>Rule 1</td><td>... <i>repeat all after Line 1 above for rule 2</i></td></tr>
     * </table>
     * @param ruleFileFullPath file to read
     * @param controller update this controller's {@link RuleProduction.Catalog}
     * @return number of rules read
     */
    public static int loadProductionRules(String ruleFileFullPath, BR_Controller controller) {
    	int ruleCount = 0;
        // load Unit ruleProductionList
        try {
            if (trace.getDebugCode("pm")) trace.out("pm", "!!Loading file: " + ruleFileFullPath);

            FileReader fr = new FileReader(ruleFileFullPath);

            if (trace.getDebugCode("pm")) trace.out("pm", "FileReader fr: " + fr);

            BufferedReader br = new BufferedReader(fr);

            if (trace.getDebugCode("pm")) trace.out("pm", "BufferedReader br: " + br);

            String s = br.readLine();
            int ruleProductionCount = Integer.valueOf(s).intValue();

            if (trace.getDebugCode("pm")) trace.out("pm", "ruleProductionNumber = "
                            + ruleProductionCount);

            String tempRuleName;
            String tempProductionSet;

            
            RuleProduction tempRuleProduction;
            for (int i = 0; i < ruleProductionCount; i++) {
                tempRuleName = br.readLine();
                tempProductionSet = br.readLine();

                tempRuleProduction = new RuleProduction(tempRuleName,
                        tempProductionSet);

                if (trace.getDebugCode("pm")) trace.out("pm", "tempRuleProduction.ruleName = "
                        + tempRuleProduction.getRuleName());

                s = br.readLine();

                if (s.equals("save the hints for this RuleProduction")) {
                    s = br.readLine();
                    int hintsNumbers = Integer.valueOf(s).intValue();

                    String tempString;

                    for (int j = 0; j < hintsNumbers; j++) {
                        tempString = "";
                        s = br.readLine();
                        while (!s.equals("This Hint Message Done")) {
                            tempString = tempString + s + "\n";
                            s = br.readLine();
                        }

                        tempRuleProduction.addHintItem(tempString);
                    }
                    s = br.readLine();
                }

                StringBuffer pr = new StringBuffer();
                while (!s.equals("This Production Rule Done")) {
                    pr.append(s.trim()).append("\n");
                    s = br.readLine();
                }
                if (trace.getDebugCode("pm")) trace.out("pm", "tempRuleProduction.productionRule = " + pr);
                tempRuleProduction.setProductionRule(pr.toString());

           //     controller.getProblemModel().addRuleProduction(tempRuleProduction);
                // sewall 10/25/09:
                // was .checkAddRuleName(temp.getRuleName(), temp.getProductionSet());
                // now save the work done above
                controller.getRuleProductionCatalog().checkAddRuleProduction(tempRuleProduction);
                ruleCount++;
            }
        } catch (java.io.FileNotFoundException e) {
            trace.errStack("loadProductionRules("+ruleFileFullPath+") error: file not found", e);
        } catch (java.io.IOException e) {
            trace.errStack("loadProductionRules("+ruleFileFullPath+") error reading file", e);
        }
        return ruleCount;
    }

    /**
     * Augment a {@link RuleProduction.Catalog} with the rules from a single .brd file.
     * @param problemFullName .brd filename
     * @param controller update this controller's {@link RuleProduction.Catalog}
     * @return count of rules found
     */
    public static int loadProblemRules(String problemFullName, BR_Controller controller) {
    	int ruleCount = 0;
        Vector<RuleProduction> problemRules = controller.getProblemStateReader()
                .loadProblemRules(problemFullName);
        RuleProduction tempRuleProduction;
        for (int i = 0; i < problemRules.size(); i++) {
            tempRuleProduction = problemRules.elementAt(i);

            if (trace.getDebugCode("pm")) trace.out("pm", "productionSet() = "
                    + tempRuleProduction.getProductionSet()+", ruleName() = "
                    + tempRuleProduction.getRuleName());

            controller.getRuleProductionCatalog().checkAddRuleProduction(tempRuleProduction);
            ruleCount++;
        }
        return ruleCount;
    }
}
