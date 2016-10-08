package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReaderJDom;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.ViewUtils;

/**
 * Generate and display a table showing the number of skill opportunities
 * in a subtree of Behavior Graph files. Herein, skills are called "rules."
 * Public access to this class is via {@link #doDialog(BR_Controller)}. 
 */
public class SkillMatrixDialog {
    private String reportDialogTitle = "Skill Matrix Report";
    private String dirName = "";
    private String dirFullName = "";

    /**
     * Prompt the user for a directory, and if that directory has .brd files
     * scan it for skill opportunities and build the skill matrix.
     * @return instance of {@link SkillMatrixDialog} if directory has .brd files
     */
    public static SkillMatrixDialog doDialog(BR_Controller ctlr) {
    	String projDir = SaveFileDialog.getProjectsDirectory(ctlr);
    	File dir = DialogUtilities.chooseFile(projDir, null, new BrdFilter(),
    			"Skill Matrix: Please choose a directory to scan", "Open",
    			DialogUtilities.FILES_AND_DIRECTORIES, ctlr.getActiveWindow());
    	if (dir == null)
    		return null;   // user cancelled file choice

    	List<File> brdFiles = Utils.findFiles(dir, new BrdFilter());
    	if (brdFiles.size() < 1) {
            JOptionPane.showMessageDialog(ctlr.getActiveWindow(),
                    "There are no .brd files under your selection.",
                    "Skill Matrix: Warning", JOptionPane.WARNING_MESSAGE);
    		return null;
    	}
    	return new SkillMatrixDialog(dir, brdFiles, ctlr);
    }

    /** The master list of skills found in the files scanned. */
    private List<String> ruleProductionList = null;
	private Font subdirFont = new java.awt.Font("Dialog", Font.BOLD, 12);
	private Font problemFont = new java.awt.Font("SansSerif", Font.PLAIN, 11);

    
    /**
     * Create the skills matrix for the given list of files.
     * @param dir top-level directory (root of subtree)
     * @param brdFiles list of .brd files in subtree to scan
     * @param ctlr just for dialog parent?
     */
    public SkillMatrixDialog(File dir, List<File> brdFiles, BR_Controller ctlr) {

    	dirFullName = dir.getPath();
    	dirName = dir.getName();
    	
    	List<List<File>> subdirsWithFiles = splitBySubdir(brdFiles);

        ruleProductionList = new ArrayList<String>();
        List<Object> tempVector = skillsMatrix(dirFullName,
                subdirsWithFiles, ruleProductionList);

        List<Object> meterFrequency = new ArrayList<Object>();
        meterFrequency.add(dirName);

        if (trace.getDebugCode("skills")) trace.out("skills", "size of top-level list: "+tempVector.size());
        meterFrequency.addAll(tempVector);
        
        generateSkillsMatrixReport(meterFrequency, ruleProductionList, ctlr);
	}

    /**
     * Create sublists of {@link File}s having a common {@link File#getParent()}.
     * @param brdFiles will sort this list with {@link Collections#sort(List)}
     * @return list of sublists, where all files in each sublist share a parent
     */
    static List<List<File>> splitBySubdir(List<File> brdFiles) {
    	List<List<File>> result = new ArrayList<List<File>>();
    	for (int i = 0; i < brdFiles.size(); ) {
    		String parent = brdFiles.get(i).getParent();
    		int j = i; 
    		while (j < brdFiles.size() && parent.equals(brdFiles.get(j).getParent()))
    			++j; 
    		result.add(brdFiles.subList(i, j));
    		i = j;
    	}
    	return result;
	}

    /**
     * Create and display the skills matrix dialog in
     * {@link SkillsMatrixDialog.UnitSkillsMatrixReportPanel}.
     * @param skillsMatrix
     * @param ruleProductionList
     */
    void generateSkillsMatrixReport(List<Object> skillsMatrix,
    		List<String> ruleProductionList, BR_Controller ctlr) {
    	new UnitSkillsMatrixReportPanel(skillsMatrix, ruleProductionList, ctlr);
    }

    /**
     * Returns a matrix vector of Problem vectors; each problem vector
     * has a problemName as first element, followed by integer values for
     * skill (KC) counts, the ruleProductionList null vector passed in
     * is now an ordered vector with the corresponding skill (KC) names    
     * @param dirFullName
     * @param subdirsWithFiles
     * @param ruleProductionList
     * @return
     */
    private List<Object> skillsMatrix(String dirFullName,
            List<List<File>> subdirsWithFiles, List<String> ruleProductionList) {

    	List<Object> skillsFrequency = new ArrayList<Object>();
    	for (List<File> subdirList : subdirsWithFiles) {
    		String subdir = subdirList.get(0).getParent();
        	if (trace.getDebugCode("skills"))
        		trace.out("skills", "Calling subdirSkillsMatrix("+subdir+");\n    dirFullName "+dirFullName);

        	if (subdir.startsWith(dirFullName)) {
        		if (subdir.length() == dirFullName.length())
        			subdir = ".";
        		else
        			subdir = subdir.substring(dirFullName.length()+1);
        	}
        	if (!subdir.endsWith(File.separator))
        		subdir = subdir+File.separator;

        	List<Object> subdirResult = subdirSkillsMatrix(subdirList, ruleProductionList);

        	List<Object> subdirSkillsFrequency = new ArrayList<Object>();
        	subdirSkillsFrequency.add(subdir);
        	subdirSkillsFrequency.addAll(subdirResult);
        	
        	skillsFrequency.add(subdirSkillsFrequency);  // add to return
    	}

        return skillsFrequency;
    }

    //returns a matrix Vector of Problem vectors; each problem vector
    //has a problemName as first element, followed by integer values for
    //skill (KC) counts, the ruleProductionList null vector passed in
    //is now an ordered vector with the corresponding skill (KC) names
    private List<Object> subdirSkillsMatrix(List<File> brdFiles,
    		List<String> ruleProductionList) {

    	List<Object> subdirSkillsFrequency = new ArrayList<Object>();

        for (File problemFile : brdFiles) {
            if (problemFile.isFile()) {
                RuleProduction.Catalog rpc = new RuleProduction.Catalog(); 
                ProblemStateReaderJDom psrj = new ProblemStateReaderJDom(null);
                ProblemModel pm = psrj.loadBRDFileIntoProblemModel(problemFile.toString(), rpc);
                if (trace.getDebugCode("skills"))
                	trace.out("skills", "subdirSkillsMatrix() pm "+pm+" from "+problemFile.getName());
                if (pm != null) {

                    List<Integer> tempVector = problemSkillsMatrix(pm, ruleProductionList, rpc);

                    List<Object> problemFrequency = new ArrayList<Object>();
                    // 1st element is the problem name
                    problemFrequency.add(problemFile.getName());
                    problemFrequency.addAll(tempVector);

                    subdirSkillsFrequency.add(problemFrequency);
                }
            }
        }
        return subdirSkillsFrequency;
    }

    //returns a matrix Vector of Problem vectors; each problem vector
    //has a problemName as first element, followed by integer values for
    //skill (KC) counts, the ruleProductionList null vector passed in
    //is now an ordered vector with the corresponding skill (KC) names
    /**
     * Create a list of opportunity counts for the skills in a single problem.
     * @param pm where the problem has been loaded 
     * @param ruleProductionList accumulates skill names found in this and other problems
     * @param rpc map skillName=>opportunity count
     * @return list of opportunity counts in this problem
     */
    public static List<Integer> problemSkillsMatrix(ProblemModel pm,
            List<String> ruleProductionList, RuleProduction.Catalog rpc) {
    	List<Integer> problemSkillFrequency = new ArrayList<Integer>();

    	int sizeOfRules = ruleProductionList.size();
        if (trace.getDebugCode("skills"))
        	trace.out("skills", "problem name " +  pm.getProblemName() +
        		"; ruleProductionList["+sizeOfRules+"] "+ruleProductionList);

        for (int j = 0; j < sizeOfRules; j++)
            problemSkillFrequency.add(new Integer(0));

        // upon Vincent's request 11/24/03: only consider Preferred Path
        // get the preferred path from the start state to the end

        List<ProblemEdge> pathEdges = pm.findPathForProblemSkillsMatrix(pm.getStudentBeginsHereState());
        rpc.updateOpportunityCounts(pathEdges);
        if (trace.getDebugCode("skills"))
        	trace.out("skills", "pathEdges "+pathEdges+"; rpc "+rpc);

        Set<String> usedRPs = new HashSet<String>();  // to record rpc members found in ruleProductionList
        for (int i = 0; i < sizeOfRules; ++i) {
        	RuleProduction rp = rpc.get(ruleProductionList.get(i));
        	if (rp != null) {
        		problemSkillFrequency.set(i, rp.getOpportunityCount());
        		usedRPs.add(rp.getKey());
        	}
        }
        for (String key : rpc.keySet()) {
        	if (usedRPs.contains(key))        // this rpc member found in list above, so skip here 
        		continue;
        	RuleProduction rp = rpc.get(key);
        	if (rp.getOpportunityCount() > 0) {
        		ruleProductionList.add(key);
        		problemSkillFrequency.add(new Integer(rp.getOpportunityCount()));
        	}
        }
        return problemSkillFrequency;
    }

    /**
     * This is the dialog that actually shows the skill matrix.
     */
    private class UnitSkillsMatrixReportPanel extends JDialog implements
            ActionListener {
        JLabel reportTitle = new JLabel();
        JPanel reportJPanel = new JPanel();
        JScrollPane reportScrollPanel;
        JPanel ruleNamesJPanel = new JPanel();
        JPanel metersJPanel = new JPanel();
        JPanel problemNamesJPanel = new JPanel();
        JPanel rulesPanel = new JPanel();
        JButton closeJButton = new JButton("Close");
        JPanel closeJPanel = new JPanel();
        JPanel contentPanel = new JPanel();

        private String dirName;

        int numberOfSubdirs;
        int numberOfProblems;
        int numberOfRules;
        final int cellSideLength = 25;
        final int maxLength = 750;
        final int maxHeight = 600;
        final int rulesPanelMargin = 20;
        int problemNamesJPanelWidth = 20;
        final int ruleNameHeight = 20;
        final int topMargin = 20;
        final int leftMargin = 10;
        final int rightMargin = 10;

        public UnitSkillsMatrixReportPanel(List<Object> skillsMatrix,
                List<String> ruleProductionList, BR_Controller ctlr) {
        	super(ctlr.getActiveWindow());

        	this.setTitle(reportDialogTitle);
            dirName = (String) skillsMatrix.get(0);

            if (trace.getDebugCode("skills")) trace.out("skills", "dirName = " + dirName);
            reportTitle.setText(dirName);
            reportTitle.setName("reportTitle");

            numberOfRules = ruleProductionList.size();
            if (trace.getDebugCode("skills")) trace.out("skills", "numberOfRules = " + numberOfRules);

            numberOfSubdirs = skillsMatrix.size() - 1;
            if (trace.getDebugCode("skills")) trace.out("skills", "numberOfSubdirs = " + numberOfSubdirs);

            numberOfProblems = 0;
            List<Object> tempVector;
            for (int i = 1; i <= numberOfSubdirs; i++) {
                tempVector = (List<Object>) skillsMatrix.get(i);
                numberOfProblems = numberOfProblems + tempVector.size() - 1;
            }
            if (trace.getDebugCode("skills")) trace.out("skills", "total problems = " + numberOfProblems);

            problemNamesJPanel.setLayout(new GridLayout(numberOfProblems
                    + numberOfSubdirs + 1, 1));

            // set first row
            JLabel emptyJLabel = new JLabel(" ");
            emptyJLabel.setSize(new Dimension(problemNamesJPanelWidth,
                    cellSideLength));
            problemNamesJPanel.add(emptyJLabel);

            metersJPanel.setLayout(new GridLayout(numberOfProblems
                    + numberOfSubdirs + 1, numberOfRules));

            for (int i = 1; i <= numberOfRules; i++) {
                JLabel ruleJLabel = new JLabel("S" + i);
                ruleJLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                ruleJLabel
                        .setSize(new Dimension(cellSideLength, cellSideLength));
                ruleJLabel.setBackground(java.awt.Color.white);
                metersJPanel.add(ruleJLabel);
            }

            // set subdir/problem rows
            List<Object> subdirVector;
            List<Object> tempProblemVector;
            int subdirProblemNumbers;
            int problemRulesNumber;

            for (int i = 1; i <= numberOfSubdirs; i++) {
                // subdir problem cell
                subdirVector = (List<Object>) skillsMatrix.get(i);
                String subdirName = (String) subdirVector.get(0);
                JLabel subdirJLabel = new JLabel(subdirName);
                subdirJLabel.setFont(subdirFont);
                FontMetrics fm = subdirJLabel.getFontMetrics(subdirFont);
                problemNamesJPanelWidth = Math.max(problemNamesJPanelWidth, fm.stringWidth(subdirName));
                subdirJLabel.setSize(new Dimension(problemNamesJPanelWidth,
                        cellSideLength));
                subdirJLabel.setBackground(java.awt.Color.white);
                subdirJLabel.setName("subdirName" + i);
                problemNamesJPanel.add(subdirJLabel);

                // empty row
                for (int j = 1; j <= numberOfRules; j++) {
                    JLabel ruleJLabel = new JLabel("");
                    ruleJLabel.setFont(problemFont);
                    ruleJLabel.setSize(new Dimension(cellSideLength,
                            cellSideLength));
                    ruleJLabel.setBackground(java.awt.Color.white);
                    metersJPanel.add(ruleJLabel);
                }

                subdirProblemNumbers = subdirVector.size() - 1;
                for (int j = 1; j <= subdirProblemNumbers; j++) {
                    tempProblemVector = (List<Object>) subdirVector.get(j);
                    StringBuilder sb = new StringBuilder((String) tempProblemVector.get(0));
                    sb.insert(0, "   ");  // indent
                    int dotOffset = Math.max(sb.lastIndexOf("."), sb.length());
                    sb.delete(dotOffset, sb.length());
                    if (trace.getDebugCode("skills")) trace.out("skills", "tempProblemName = "+sb);

                    JLabel problemJLabel = new JLabel(sb.toString());
                    problemJLabel.setFont(problemFont);
                    fm = problemJLabel.getFontMetrics(problemFont);
                    problemNamesJPanelWidth = Math.max(problemNamesJPanelWidth,
                    		fm.stringWidth(sb.toString()));
                    problemJLabel.setSize(new Dimension(
                            problemNamesJPanelWidth, cellSideLength));

                    problemJLabel.setBackground(java.awt.Color.white);
                    problemJLabel.setName("problemName" + j);
                    problemNamesJPanel.add(problemJLabel);

                    if (trace.getDebugCode("skills")) trace.out("skills", "tempProblemVector.size() = "
                            + tempProblemVector.size());

                    problemRulesNumber = tempProblemVector.size() - 1;
                    for (int k = 0; k < problemRulesNumber; k++) {
                        Integer tempMeter = (Integer) tempProblemVector.get(k + 1);
                        JLabel meterJLabel = new JLabel(tempMeter.toString());
                        meterJLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                        if (trace.getDebugCode("skills")) trace.out("skills", "tempMeter["+k+"] = "+tempMeter);

                        meterJLabel.setFont(problemFont);
                        meterJLabel.setSize(new Dimension(cellSideLength, cellSideLength));
                        metersJPanel.add(meterJLabel);
                    }

                    for (int k = problemRulesNumber; k < numberOfRules; k++) {
                        JLabel meterJLabel = new JLabel("0");
                        meterJLabel.setHorizontalAlignment(SwingConstants.TRAILING);

                        meterJLabel.setSize(new Dimension(cellSideLength,
                                cellSideLength));
                        metersJPanel.add(meterJLabel);
                    }
                }
            }

            ruleNamesJPanel.setLayout(null);

            problemNamesJPanel.setLocation(leftMargin, topMargin);
            problemNamesJPanel.setBackground(java.awt.Color.white);
            problemNamesJPanel.setSize(new java.awt.Dimension(
                    problemNamesJPanelWidth, (numberOfProblems
                            + numberOfSubdirs + 1)
                            * cellSideLength));
            problemNamesJPanel.setVisible(true);
            ruleNamesJPanel.add(problemNamesJPanel);

            metersJPanel.setLocation(leftMargin + problemNamesJPanelWidth,
                    topMargin);
            metersJPanel.setBackground(java.awt.Color.white);
            metersJPanel.setSize(new java.awt.Dimension(numberOfRules
                    * cellSideLength, (numberOfProblems + numberOfSubdirs + 1)
                    * cellSideLength));
            metersJPanel.setVisible(true);
            ruleNamesJPanel.add(metersJPanel);

            contentPanel.setLayout(new BorderLayout());
            ViewUtils.setStandardBorder(contentPanel);

            reportJPanel.setLayout(new FlowLayout());
            reportTitle.setFont(new java.awt.Font("Dialog", 1, 14));
            reportJPanel.add(reportTitle);

            contentPanel.add(reportJPanel, BorderLayout.NORTH);

            rulesPanel.setLayout(new GridLayout(numberOfRules, 1));
            for (int i = 1; i <= numberOfRules; i++) {
                JLabel ruleLabel = new JLabel("S" + i + ": "
                        + (String) ruleProductionList.get(i - 1));
                ruleLabel.setSize(numberOfRules * cellSideLength,
                        ruleNameHeight);
                ruleLabel.setName("ruleName" + i);
                rulesPanel.add(ruleLabel);
            }

            rulesPanel.setLocation(leftMargin, (numberOfProblems
                    + numberOfSubdirs + 1)
                    * cellSideLength + rulesPanelMargin + topMargin);
            rulesPanel.setSize(new java.awt.Dimension(problemNamesJPanel
                    .getSize().width
                    + metersJPanel.getSize().width, numberOfRules
                    * ruleNameHeight + 10));
            rulesPanel.setVisible(true);
            ruleNamesJPanel.add(rulesPanel);

            int tempWidth = leftMargin + problemNamesJPanelWidth
                    + numberOfRules * cellSideLength + rightMargin;
            int tempHeight = (numberOfProblems + numberOfSubdirs + 1)
                    * cellSideLength + numberOfRules * ruleNameHeight
                    + rulesPanelMargin + topMargin + 10;

            ruleNamesJPanel.setPreferredSize(new java.awt.Dimension(tempWidth,
                    tempHeight));

            reportScrollPanel = new JScrollPane(ruleNamesJPanel);

            contentPanel.add(reportScrollPanel, BorderLayout.CENTER);
            Insets insets = contentPanel.getBorder().getBorderInsets(contentPanel);
            tempWidth += insets.left + insets.right;
            tempHeight += insets.top + insets.bottom;

            closeJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            closeJPanel.add(closeJButton);

            closeJButton.addActionListener(this);

            contentPanel.add(closeJPanel, BorderLayout.SOUTH);
            getContentPane().add(contentPanel);

            setSize(new java.awt.Dimension(Math.min(maxLength, tempWidth) + 20,
                    Math.min(maxHeight, tempHeight) + 60));
            setLocationRelativeTo(ctlr.getActiveWindow());
            setVisible(true);
        }

        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == closeJButton) {
                setVisible(false);
                dispose();
            }
        }
    }
}
