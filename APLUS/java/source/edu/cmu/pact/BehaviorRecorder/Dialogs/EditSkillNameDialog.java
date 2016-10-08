package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import edu.cmu.pact.BehaviorRecorder.Controller.ActionLabelHandler;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.RuleLabelHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Log.AuthorLogListener;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.ViewUtils;
import edu.cmu.pact.miss.AskHintHumanOracle;


/**
 *
 * 
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////

//Noboru says I should make a wrapper around EditSkillNameDialog, which extends AbstractCtatWindow
//TutorWindow does not seem to be a wrapper.
//
//public class EditSkillNameDialog2 extends AbstractCtatWindow implements AbstractCtatWindow, MouseListener {
//
//}


//public class EditSkillNameDialog extends JDialog implements ActionListener, ItemListener {

//AbstractCtatWindow , JDialog, AbstractCtatDialog
public class EditSkillNameDialog extends JDialog implements ActionListener, ItemListener, ComponentListener //, WindowListener
{
    private final RuleLabelHandler handler;

    private String originalRule;

    private JLabel skillNameLabel = new JLabel(
            "Please edit or select the skill name (no spaces):");
    private JLabel skillSetLabel = new JLabel(
            "Please edit or select the skill set name (no spaces):");

    private JPanel contentPanel = new JPanel();    
    private JPanel optionPanel = new JPanel();
    private JPanel okCancelPanel = new JPanel();

    private JComboBox skillSetComboBox = new JComboBox();
    private JComboBox skillNamesComboBox = new JComboBox();

    private JLabel skillLabelLabel = new JLabel("Please enter a label for the skillometer:");    
    private JTextField skillLabelTextField = new JTextField(16);
    { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(skillLabelTextField); }
    private JLabel skillDescriptionLabel = new JLabel("Enter a skill description (optional):");    
    private JTextArea skillDescriptionTextArea = new JTextArea();
    { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(skillDescriptionTextArea); }
    
    private JScrollPane skillDescriptionScrollPane = new JScrollPane(skillDescriptionTextArea); 

    private JCheckBox copyLinkHints = new JCheckBox(
            "Copy this link's hints to the Production Rule corresponding to this link.");
    private JCheckBox copyRuleHints = new JCheckBox(
            "Copy the corresponding Production Rule's hints to this link.");

    JButton okJButton = new JButton("    OK    ");
    JButton cancelJButton = new JButton("Cancel");
    
    /** Edge information before edits. */
    private final String serializedBeforeEdit;
    
    private BR_Controller controller;
    private EdgeData edgeData;

    public void componentHidden(ComponentEvent arg)
    {
    }
    public void componentShown(ComponentEvent arg)
    {
    }
    public void componentResized(ComponentEvent arg)
    {
    }
	public void componentMoved(ComponentEvent e) {
		storeLocation();
	}	    
    
	public void addWindowListener(WindowListener wl) {
       	super.addWindowListener(wl);
	}
	
	/**
	 * For debugging dialog troubles. Created this to see if I could debug the
	 * trouble with transparent borders.
	 * @author sewall
	 */
	private static class LabelDialog extends JDialog {
		private JPanel contentPane = (JPanel)getContentPane();
		private JPanel optionPanel = new JPanel();
	    private JLabel skillLabelLabel = new JLabel("Please enter a label for the skillometer:");
		private JTextField skillLabelTextField = new JTextField(16);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(skillLabelTextField); }
		private JLabel skillNameLabel = new JLabel("Please edit or select the skill name:");
		private JComboBox skillNamesComboBox = new JComboBox();
		private JButton okJButton = new JButton("    OK    ");
		private JButton cancelJButton = new JButton("Cancel");
		private JPanel okCancelPanel = new JPanel();
	    LabelDialog(JFrame frame, List<String> args) {
	    	super(frame);
	        contentPane.setLayout(new BorderLayout());
	        ViewUtils.setStandardBorder(contentPane);
	        BoxLayout boxLayout = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);
	        optionPanel.setLayout(boxLayout);
	    	optionPanel.add(skillLabelLabel);
	    	if (args.contains("textfield")) {
	    		optionPanel.add(skillLabelTextField);
	    	}
	    	if (args.contains("combobox")) {
	            optionPanel.add(skillNameLabel);
	            optionPanel.add(skillNamesComboBox);
	    	}
	    	if (args.contains("buttons")) {
	            okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	            okJButton.setSize(cancelJButton.getSize());
	            okCancelPanel.add(okJButton);
	            okCancelPanel.add(cancelJButton);
	            contentPane.add(okCancelPanel, BorderLayout.SOUTH);
	            getRootPane().setDefaultButton(okJButton);
	    	}
	        contentPane.add(optionPanel, BorderLayout.CENTER);
	        addWindowListener(new java.awt.event.WindowAdapter() {
	            public void windowClosing(java.awt.event.WindowEvent e) {
	                setVisible(false);
	                dispose();
	            }
	        });
	        pack();
	        setVisible(true);
	    }
	}	
	/**
	 * For debugging dialog in isolation to rest of system.
	 * @param args if none, create an {@link EditSkillNameDialog};
	 *             otherwise specify args to create components - one or more of: 
	 *             textfield combobox buttons
	 */
	public static void main(String[] args) {
		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jframe.setSize(150,150);
		jframe.setVisible(true);
		if (args.length > 0) {
			List<String> argList = Arrays.asList(args);
			new LabelDialog(jframe, argList);
		} else {
			EditSkillNameDialog esnd = new EditSkillNameDialog(jframe, "write-carry Addition");
		}
	}
	
	/**
	 * For debugging only.
	 * @param frame parent frame
	 * @param originalRuleP ruleName + productionSet
	 */
	private EditSkillNameDialog(JFrame frame, String originalRuleP) {
		super(frame);
		
		serializedBeforeEdit = "";
		controller = null;
		edgeData = null;
		
		handler = null;
		init(originalRuleP);
	}

	public EditSkillNameDialog(RuleLabelHandler handler, String originalRuleP) {
        super(handler.controller.getActiveWindow());
        
      //Save BRD serialization at start to compare for change upon close (for undo)
        serializedBeforeEdit = handler.edgeData.getEdge().toXMLString();
        controller = handler.controller;
        edgeData = handler.edgeData;
        
        
		this.handler = handler;
		init(originalRuleP);
	}		

	private void init(String originalRuleP) {

		this.addComponentListener(this);
		if(handler != null && handler.controller.getLoggingSupport()!=null)
			this.addWindowListener(new AuthorLogListener(handler.controller.getLoggingSupport()));
	    
        originalRule = originalRuleP;
        int firstSpace = originalRule.indexOf(" ");
        String skillName;
        String skillSetName = "";
        if (firstSpace > 0) {
            skillName = originalRule.substring(0, firstSpace);
            skillSetName = originalRule.substring(firstSpace + 1);
        } else
            skillName = originalRule;

        RuleProduction.Catalog rpc = null;
        if (handler != null)
        	rpc = handler.controller.getRuleProductionCatalog();
        else {                                        // for debugging only
        	rpc = new RuleProduction.Catalog();
        	rpc.addRuleProduction(new RuleProduction(skillName, skillSetName));
        }

        RuleProduction thisRP = rpc.getRuleProduction(skillName, skillSetName);
        if (thisRP == null)
        	thisRP = new RuleProduction(skillName, skillSetName);

        setTitle("Edit skill name \"" + skillName + "\"");

        int rulesSize = rpc.size();

        skillSetComboBox = new JComboBox();
        skillSetComboBox.setName("skillSetComboBox");
        skillNamesComboBox.setName("skillNamesComboBox");
        skillLabelTextField.setName("skillLabelTextField");
        skillDescriptionTextArea.setName("skillDescriptionTextArea");

        if (handler != null && handler.controller.getCtatModeModel().isSimStudentMode()) {
            String setName = this.handler.controller.getMissController().getDefaultRuleSetName();
            skillSetComboBox.addItem(setName);
        }
        
        TreeSet<String> skillNameSet = new TreeSet<String>();
        TreeSet<String> skillSetSet = new TreeSet<String>();

        if (rulesSize == 0) {
            skillNameSet.add("No production Rules Defined");
            skillSetSet.add("No production Rule set Defined");
        } 
        else {
        	Collection<RuleProduction> ruleProductions =
        		rpc.values();
        	for (RuleProduction rp : ruleProductions) {
        		if (rp.isUnnamed())
        			continue;
        		skillNameSet.add(rp.getRuleName());
				if (rp.getProductionSet() != null && rp.getProductionSet().trim().length() > 0)      // it happened. Why?
					skillSetSet.add(rp.getProductionSet());
        	}
        }

        Iterator m = skillNameSet.iterator();
        while (m.hasNext())
            skillNamesComboBox.addItem(m.next());
        
        skillDescriptionTextArea.setFont(skillLabelTextField.getFont());
        skillDescriptionTextArea.setLineWrap(true);
        skillDescriptionTextArea.setWrapStyleWord(true);
        skillDescriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        skillDescriptionScrollPane.setPreferredSize(new Dimension(150,50));

        // int comboBoxSize = skillSetSet.size();
        m = skillSetSet.iterator();
        while (m.hasNext())
            skillSetComboBox.addItem(m.next());

        contentPanel.setLayout(new BorderLayout());
        ViewUtils.setStandardBorder(contentPanel);
        BoxLayout boxLayout = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);
        optionPanel.setLayout(boxLayout);
        skillNamesComboBox.addActionListener(this);
        skillNamesComboBox.addItemListener(this);

        optionPanel.add(skillNameLabel);
        optionPanel.add(skillNamesComboBox);

        skillSetComboBox.addActionListener(this);
        skillSetComboBox.addItemListener(this);
        optionPanel.add(skillSetLabel);
        optionPanel.add(skillSetComboBox);

        skillLabelTextField.setText(thisRP.getLabel());
	    skillDescriptionTextArea.setText(thisRP.getDescription());
        optionPanel.add(skillLabelLabel);
        optionPanel.add(skillLabelTextField);
        optionPanel.add(skillDescriptionLabel);
        optionPanel.add(skillDescriptionScrollPane);

        optionPanel.add(copyLinkHints);

        optionPanel.add(copyRuleHints);

        resetCopyHints(skillName, skillSetName);

        contentPanel.add(optionPanel, BorderLayout.CENTER);

        okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        okJButton.setSize(cancelJButton.getSize());
        okCancelPanel.add(okJButton);
        okCancelPanel.add(cancelJButton);

        contentPanel.add(okCancelPanel, BorderLayout.SOUTH);
        getContentPane().add(contentPanel);
        
        okJButton.addActionListener(this);
        cancelJButton.addActionListener(this);

        getRootPane().setDefaultButton(okJButton);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });

        skillNamesComboBox.setEditable(true);
        skillSetComboBox.setEditable(true);

        skillNamesComboBox.setSelectedItem(skillName);

        if (skillSetName.length() > 0)
            skillSetComboBox.setSelectedItem(skillSetName);

        pack();

 //       Point p = this.handler.brPanel.getLocation();
 //       Dimension brSize = this.handler.brPanel.getSize();
        Dimension thisSize = getSize();

        //############################################################## DIALOG LOCATION
        //CHANGE THIS to remember the last location

        //Point newLocation =
        //        p.x + (brSize.width - thisSize.width) / 2, p.y
        //                + (brSize.height - thisSize.height) / 2);        
        
        //setLocation(newLocation);
        applyPreferences();
              
        setVisible(true);
    }

    
    
//    public getSavedLocation(){
//    	
//    }
        

    public void itemStateChanged(java.awt.event.ItemEvent e) {

        if (e.getSource() == skillSetComboBox
                || e.getSource() == skillNamesComboBox) {

            String newRuleName = (String) skillNamesComboBox.getSelectedItem();
            String newSetName = (String) skillSetComboBox.getSelectedItem();

            resetCopyHints(newRuleName, newSetName);
        }

        return;
    }

    void resetCopyHints(String newRuleName, String newSetName) {

        copyRuleHints.setEnabled(false);
        copyLinkHints.setEnabled(false);

        if (newRuleName == null)
            return;

        if (newRuleName.length() <= 0)
            return;

        if (newSetName == null)
            return;

        if (newSetName.length() <= 0)
            return;

        if (handler == null)
        	return;

        RuleProduction tempESE_RuleProduction =
        		handler.controller.getRuleProduction(newRuleName, newSetName);


        if (tempESE_RuleProduction != null) {
            if (tempESE_RuleProduction.getHints().size() > 0) {
                copyRuleHints.setEnabled(true);
            } else
                copyRuleHints.setEnabled(false);

            if (this.handler.edgeData.getHints().size() > 0) {
                copyLinkHints.setEnabled(true);
                if (tempESE_RuleProduction.getHints().size() > 0)
                    copyLinkHints.setSelected(false);
            } else {
                copyLinkHints.setSelected(false);
                copyLinkHints.setEnabled(false);
            }
        } else if (handler.edgeData.getHints().size() > 0) {
            copyLinkHints.setEnabled(true);
            // copyLinkHints.setSelected(true);
        }
        return;
    }
    

        
        
    public void actionPerformed(ActionEvent ae) {
//        trace.out("wmefacts", "entered actionPerformed: ");
//        this.handler.controller.getMissController().getSimSt().showJessFacts();
              
    	// JButton selectedButton = ae.getSource();

        if (ae.getSource() == skillSetComboBox
                || ae.getSource() == skillNamesComboBox) {
            String newRuleName = (String) skillNamesComboBox.getSelectedItem();
            String newSetName = (String) skillSetComboBox.getSelectedItem();

            resetCopyHints(newRuleName, newSetName);

        } else if (ae.getSource() == okJButton) {
            String newRuleName = (String) skillNamesComboBox.getSelectedItem();
            
            
            if (newRuleName.length() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Skill name may not be empty.", "",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newRuleName.indexOf(" ") >= 0) {
                JOptionPane.showMessageDialog(this,
                        "Skill names may not have spaces", "",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newRuleName.equalsIgnoreCase("unnamed")) {
            	JOptionPane.showMessageDialog(this,
                        "Skill names can't use unnamed", "",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String newProductionSet = (String) skillSetComboBox
                    .getSelectedItem();

            if (newProductionSet == null || newProductionSet.length() <= 0) {

                JOptionPane.showMessageDialog(this,
                        "Please provide a skill set name.", "",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newProductionSet.indexOf(" ") >= 0) {
                JOptionPane.showMessageDialog(this,
                        "Skill set names may not have spaces", "",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            RuleProduction rp =
            	handler.controller.checkAddRuleName(newRuleName, newProductionSet);
            handler.ruleLabel.setText(rp.getDisplayName());
            handler.edgeData.replaceRuleName(originalRule, rp.getDisplayName());
            
            //set production set name into SimSt
            if (handler.controller.getCtatModeModel().isSimStudentMode())
            	handler.controller.getMissController().getSimSt().setProductionSetName(newProductionSet);
            
            if (rp != null) {
                // copy this edge hints to new defined RuleProductionSet
                if (copyLinkHints.isSelected())
                    rp.setHints(this.handler.edgeData.getAllHints());
                            
                // copy rule hints to this edge hints
                if (copyRuleHints.isSelected())
                    this.handler.edgeData.setHints(rp.getHints());
            }
            
            String s = skillLabelTextField.getText();
            if (s != null && s.length() > 0)
            	rp.setLabel(s);
            s = skillDescriptionTextArea.getText();
            if (s != null && s.length() > 0)
            	rp.setDescription(s);
            if (trace.getDebugCode("skills")) trace.out("skills", "RuleProduction "+rp+", label "+rp.getLabel()+
            		", description "+rp.getDescription());
            
            // Fri May 27 17:15:03 2005: Noboru
            // This must be the place to let SimSt know about
            // the change....
            BR_Controller brController = this.handler.controller; 
            if (brController.getCtatModeModel().isSimStudentMode()) {
            	// Wed Jun 14 22:43:28 2006 Noboru
            	// Update the production rules only when the skill name has been changed
            	int spacePos = originalRule.indexOf(' ');
            	String oldRuleName = spacePos > 0 ? originalRule.substring(0,spacePos) : originalRule;
            	if (!oldRuleName.equals(newRuleName)) {
            		if (trace.getDebugCode("miss")) trace.out("miss", "EditSkillName.actionPerformed:  new rule name set to " + newRuleName);
            		if (!AskHintHumanOracle.isWaitingForSkillName) { //InteractiveLearning suppresses this call 
            			ProblemNode actionState = this.handler.edgeData.getEndProblemNode();
            			brController.getMissController().skillNameSet(newRuleName, actionState);
            		}
            		else {
            			AskHintHumanOracle.hereIsTheSkillName(newRuleName);
            		}
            	} else {
            		if (trace.getDebugCode("miss")) trace.out("miss", "EditSkillName: skill name has not been changed. Action ignored");
            	}
            }

            setVisible(false);
            dispose();

            // upon Vincent request when copy rule hints to the actionlabel
            // display HelpSuccessEditor
            if (copyRuleHints.isSelected()) {
                HelpSuccessPanel helpSuccessPanel = new HelpSuccessPanel(this.handler.controller,
                		this.handler.edgeData, true);

                helpSuccessPanel.setVisible(true);
            }

            brController.getJGraphWindow().getJGraph().repaint(); // chc added for JGraph Layout Pro-1.4  
            
//            trace.out("wmefacts", "before returning");
//            this.handler.controller.getMissController().getSimSt().showJessFacts();
            
            
          //Undo checkpoint for Editing Skill Names ID: 1337
            String serializedAfterEdit = edgeData.getEdge().toXMLString();
            if (!(serializedBeforeEdit.equals(serializedAfterEdit))) {
            	if (trace.getDebugCode("undo"))
					trace.out("undo", "EditSkillNameDialog.close() XML before:\n"+
            				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);
            			
				//Undo checkpoint for Editing Skill Names ID: 1337
				ActionEvent evt = new ActionEvent(this, 0, "Edit skill name");
				controller.getUndoPacket().getCheckpointAction().actionPerformed(evt);
				if (trace.getDebugCode("undo"))
					trace.out("undo", "Checkpoint: Editing Skill Names (1)");
            }

            return;
        }

        if (ae.getSource() == cancelJButton) {
            setVisible(false);
            dispose();
            return;
        }

//        trace.out("wmefacts", "before returning");
//        this.handler.controller.getMissController().getSimSt().showJessFacts();

        return;
    }

        
    
    
	public void applyPreferences() {
		if (handler == null)
			return;
		PreferencesModel model = handler.controller.getPreferencesModel();
		
		// position
		
		String loc_x = "EditSkillNameDialogLocationX";
		String loc_y = "EditSkillNameDialogLocationY";
		//String width = getName() + " Width";
		//String height = getName() + " Height";
		
		Integer X = model.getIntegerValue(loc_x);
		Integer Y = model.getIntegerValue(loc_y);
		
		//X, Y are returning null. WHY?
		if (X==null){
			model.setIntegerValue(loc_x, 100);
			X = new Integer(100);
		}
		if (Y==null){
			model.setIntegerValue(loc_y, 100);
			Y = new Integer(100);
		}
		//X and Y are being stored correctly
		this.setLocation(X.intValue(),Y.intValue());//x.intValue(),y.intValue());

		
		//size

		/*int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().width;

		Integer Width = model.getIntegerValue(width);
		Integer Height = model.getIntegerValue(height);

		trace.out ("wh", "height = " + Height + " width = " + Width);
		
		if (Height != null && Width != null)
			this.setSize(new Dimension(Width.intValue(), Height.intValue()));
		*/
		
		
		trace.out ("wh", "size = " + this.getSize() + " location = " + this.getLocation());

	}
    
	
	public void storeLocation() {
		if (handler == null)
			return;

		String loc_x = "EditSkillNameDialogLocationX";
		String loc_y = "EditSkillNameDialogLocationY";

	    Point p = this.getLocation();

		handler.controller.getPreferencesModel().setIntegerValue(loc_x, p.x);
		handler.controller.getPreferencesModel().setIntegerValue(loc_y, p.y);
	}
    
    
}
