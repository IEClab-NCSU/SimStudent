/*
 * Created on Jun 22, 2006
 *
 */
package edu.cmu.pact.ctatview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.AuthorLauncherServer;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetModeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.model.CtatModeModel;

public class CtatModePanel extends JComponent implements ChangeListener
{
	private static final long serialVersionUID = 6550476531267520998L;
	
	/** Tooltip for {@link #sicsLabel}. */
	private static final String SICS_LABEL_TOOL_TIP =
			"<html>CTAT can be launched to connect to student interfaces<br/>"+
					"running either in <b>Flash</b> or in <b>Java</b>.</html>";
	
	private Color startColor;
	private Color endColor;

    /** Show the tutor type: Ex-tracing, Jess, .... */
    private JComboBox tutorTypeComboBox;
    
    /** Show the author mode: Start state, demonstrate, .... */
    private JComboBox authorModeComboBox;

    private final CTAT_Launcher ctatLauncher;

    public static final String TUTOR_TYPE_TOOL_TIP_TEXT = "" +
    "<html>&nbsp;" +
    "<b>Example-tracing Tutors</b> use demonstrated examples of problem-solving " +
    "steps to evaluate student actions." +
    "<br>&nbsp;<b>Cognitive Tutors</b> employ a general cognitive model, " +
    "composed of production rules, to evaluate or suggest student actions." +
    (VersionInformation.isRunningSimSt() ? 
    		"<br>&nbsp;<b>Simulated Student</b> induces production rules " +
    		"from demonstrated examples of problem-solving steps." : "")+
    "</html>";
    
	public static final String AUTHOR_MODE_TOOL_TIP_TEXT = "" +
	"<html>&nbsp;" +
	"<b>Set Start State mode:</b> enter the initial state of a problem.<br>" +
    "&nbsp;<b>Demonstrate mode:</b> record problem-solving steps in a behavior graph.<br>" +
    "&nbsp;<b>Test Tutor mode:</b> test the behavior of your tutor." +
    "</html>";
	
	/** For {@link ActionEvent#getActionCommand()} in events from {@link #authorModeComboBox}. */
	public static final String AUTHOR_MODE_COMBO_BOX_EVENT_CMD = "AuthorModeComboBox changed";
    
	/*
	public static final String FILE_CONNECTED_TOOL_TIP_TEXT = "" +
	"<html>&nbsp;" +
	"<b>None:</b> Don't connect any open problem to a student interface.<br>" +
    "&nbsp;<b>Filename.brd:</b> connected the problem Filename to the open student interface.<br>" +
    "</html>";
	*/

	Icon logo=null;
	
	/** Text for connection icon: varies by student interface platform. */
	private JLabel sicsLabel = null;

	private Box collabPanel = null;

	private JLabel collabCount = null;

    public CtatModePanel (CTAT_Launcher server) 
    {
    	this.ctatLauncher = server;

        init();
        
        //this.startColor = Color.GRAY ;
        this.startColor = new Color (200,200,200);
        //this.endColor = Color.WHITE;
        this.endColor = new Color (240,240,240);
        
        if(server.getAuthorLauncherServer() != null)
        	server.getAuthorLauncherServer().addChangeListener(this);
    }

    protected void update(SetModeEvent event) 
    {
        trace.out ("event = " + event);
    }
    
    @Override
    protected void paintComponent (Graphics g)
    {
    	super.paintComponent( g );
    	int panelHeight = getHeight();
    	int panelWidth = getWidth();
    	
    	GradientPaint gradientPaint = new GradientPaint( 0 , 0 , startColor , panelWidth , panelHeight , endColor );
    	
    	if (g instanceof Graphics2D)
    	{
    		Graphics2D graphics2D=(Graphics2D)g;
    		graphics2D.setPaint (gradientPaint);
    		graphics2D.fillRect (0,0,panelWidth ,panelHeight);
    		
    		graphics2D.setColor(new Color (0,0,0));
    		graphics2D.drawLine(0,panelHeight-1,panelWidth,panelHeight-1);
    	}
    }
    //}    

    private void init() 
    {
        
        trace.out ("INIT CTAT MODE PANEL");
        
        BorderLayout frameBox=new BorderLayout();
        this.setLayout (frameBox);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setOpaque(false);
        panel.setMinimumSize(new Dimension (100,28));
        panel.setMaximumSize(new Dimension (50000,28));
                
        final JLabel tutorTypeLabel = new JLabel("Tutor Type:");        
        tutorTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        tutorTypeLabel.setToolTipText(CtatModePanel.TUTOR_TYPE_TOOL_TIP_TEXT);
        CtatModeModel ctatModeModel = getCtatLauncher().getFocusedController().getCtatModeModel();
        
        tutorTypeComboBox = new JComboBox(ctatModeModel.getModeComboBoxModel());
        tutorTypeComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
        tutorTypeComboBox.setName("tutorTypeComboBox");
        tutorTypeComboBox.setToolTipText(CtatModePanel.TUTOR_TYPE_TOOL_TIP_TEXT);
        tutorTypeComboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		getCtatLauncher().getFocusedController().getCtatModeModel().userSetMode((String) tutorTypeComboBox.getSelectedItem());
        		getCtatLauncher().getLoggingSupport().authorActionLog(
        				AuthorActionLog.BEHAVIOR_RECORDER, BR_Controller.SWITCH_MODE,
        				tutorTypeComboBox.getSelectedItem().toString() + ", " +
        				authorModeComboBox.getSelectedItem().toString());
        	}
        });
        
        
        final JLabel authorModeLabel = new JLabel("Author Mode:");
        authorModeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        authorModeLabel.setToolTipText(CtatModePanel.AUTHOR_MODE_TOOL_TIP_TEXT);
        
        final ComboBoxModel authorModeModel =
        		getCtatLauncher().getFocusedController().getCtatModeModel().getAuthorModeComboBoxModel();
                
        authorModeComboBox = new JComboBox(authorModeModel);
        authorModeComboBox.setFont(new Font("Dialog", Font.PLAIN, 12));
        authorModeComboBox.setName("authorModeComboBox");
        authorModeComboBox.setActionCommand(AUTHOR_MODE_COMBO_BOX_EVENT_CMD);
        authorModeComboBox.setToolTipText(CtatModePanel.AUTHOR_MODE_TOOL_TIP_TEXT);
        authorModeComboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		getCtatLauncher().getLoggingSupport().authorActionLog(
        				AuthorActionLog.BEHAVIOR_RECORDER, BR_Controller.SWITCH_MODE,
        				tutorTypeComboBox.getSelectedItem().toString() + ", " +
        				authorModeComboBox.getSelectedItem().toString());
        	}
        });

        c.gridy = 0;
        c.insets = new Insets(5, 10, 5, 0);
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        panel.add(tutorTypeLabel, c);

        c.insets = new Insets(5, 5, 5, 0);		
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(tutorTypeComboBox, c);

        c.insets = new Insets(5, 15, 5, 0);
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        panel.add(authorModeLabel, c);

        c.insets = new Insets(5, 5, 5, 0);
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(authorModeComboBox, c);
        
        JLabel collabLabel = new JLabel("Collaborators");
        collabLabel.setName("collabLabel");
        collabLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
        collabLabel.setAlignmentX(CENTER_ALIGNMENT);
        collabLabel.setAlignmentY(CENTER_ALIGNMENT);

        collabCount = new JLabel("0 of 0");
        collabCount.setName("collabCount");
        collabCount.setFont(new Font("Dialog", Font.PLAIN, 12));
        collabCount.setOpaque(true);
        collabCount.setBackground(Color.green.brighter().brighter());
        collabCount.setAlignmentX(CENTER_ALIGNMENT);
        collabCount.setAlignmentY(TOP_ALIGNMENT);
        Dimension dim = collabCount.getPreferredSize();
        collabCount.setMaximumSize(new Dimension(Integer.MAX_VALUE, dim.height));
        collabCount.setHorizontalAlignment(SwingConstants.CENTER);
        // would like to set rounded corners but that's not in Java 1.6
        collabCount.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

        collabPanel  = new Box(BoxLayout.Y_AXIS);
        collabPanel.add(collabLabel);
        collabPanel.add(collabCount);
        collabPanel.setVisible(false);
        
        c.insets = new Insets(1, 5, 1, 0);
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(collabPanel, c);

        sicsLabel = new JLabel(getSicsLabelText());
        sicsLabel.setName("studentInterfaceConnectionStatusLabel");
        sicsLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        sicsLabel.setToolTipText(SICS_LABEL_TOOL_TIP);

        c.insets = new Insets(5, 0, 6, 5);
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0;
        panel.add(sicsLabel, c);

        this.add(panel);
    }

    /**
     * Generate text for the {@link #sicsLabel}. Decides Flash or Java according to
     * {@link UniversalToolProxy#getStudentInterfacePlatform()}.
     * @return complete label text
     */
	private String getSicsLabelText() {
        String result = "No Student Interface Connected";
        UniversalToolProxy utp =
        		((TutorController) getCtatLauncher().getFocusedController()).getUniversalToolProxy();
        if (utp != null && !Utils.isRuntime())
        	result = String.format("For %s Student Interfaces", utp.getStudentInterfacePlatform());
        return result;
	}

	/**
     * 
     */
    public ComboBoxModel getComboBoxModel() 
    {
        return tutorTypeComboBox.getModel();
    }

	public void stateChanged(ChangeEvent e) {
		if (trace.getDebugCode("inter"))
			trace.out("inter", "CtatModePanel.stateChanged() source "+e.getSource());
		if(e.getSource() instanceof UniversalToolProxy)
			updateStudentConnectionStatus();
		else if(e.getSource() instanceof AuthorLauncherServer)
			updateCollabCounts((AuthorLauncherServer) e.getSource());
	}

	/**
	 * Revise {@link #collabCount} and {@link #collabPanel} to show the current collaboration
	 * state from {@link AuthorLauncherServer#getCollaborationCounts()}.
	 * @param als server to query
	 */
	private void updateCollabCounts(AuthorLauncherServer als) {
		int[] counts = als.getCollaborationCounts();
		final int nCollabs = counts[0];
		final int teamSize = counts[1];
		Runnable collabUpdater = new Runnable() {
			public void run() {
				if(teamSize < 2) {                           // no collaboration
					collabPanel.setToolTipText(null);
					collabPanel.setVisible(false);
				} else {
					if(nCollabs < teamSize) {
						collabCount.setBackground(Color.pink);
						collabPanel.setToolTipText(String.format("<html>The collaborators' team size"+
								" is %d, but %sstudent interface%s connected so far.</html>",
								teamSize,
								(nCollabs < 1 ? "no<br />" : "only <br />"+nCollabs+" "),
								(nCollabs < 2 ? " is" : "s are")));
					} else {
						collabCount.setBackground(Color.green.brighter().brighter());
						collabPanel.setToolTipText("All collaborators are now active.");
					}
					collabCount.setText(String.format("%d of %d", nCollabs, teamSize));
					collabPanel.setVisible(true);
				}
			}
		};
		SwingUtilities.invokeLater(collabUpdater);
	}

	/**
	 * Update the icon in the behavior recorder window tab.
	 */
	private void updateStudentConnectionStatus() {
        sicsLabel.setText(getSicsLabelText());    // in case UTP wasn't defined when initially displayed
	}
	
	private CTAT_Launcher getCtatLauncher() {
		return this.ctatLauncher;
	}
	
	public void changeModes(BR_Controller controller) {
		ComboBoxModel modeModel = controller.getCtatModeModel().getModeComboBoxModel();
		ComboBoxModel authorModel = controller.getCtatModeModel().getAuthorModeComboBoxModel();
		this.tutorTypeComboBox.setModel(modeModel);
		this.authorModeComboBox.setModel(authorModel);
		this.tutorTypeComboBox.setSelectedItem(modeModel.getSelectedItem());
		this.authorModeComboBox.setSelectedItem(authorModel.getSelectedItem());
	}
	
	/**
	 * Access for {@link CtatMenuBar#createStartStateMenuActionPerformed()}, 
	 * {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelManager#pasteLinks()}.
	 * @param newAuthorMode
	 */
	public void createStartState() {
		authorModeComboBox.setSelectedItem(CtatModeModel.DEMONSTRATING_SOLUTION);		
	}

    /**
     * Prompt the user for a problem name. Validates user entry with
     * {@link ProblemModel#checkForValidProblemName(String)}
     * @return user entry
     */
    public String queryForProblemName() {

    	if(trace.getDebugCode("startstate"))
    		trace.printStack("startstate", "*! BR_Ctlr.queryForProblemName()");
        while (true) {
            String problemName = JOptionPane.showInputDialog(getCtatLauncher().getActiveWindow(),
                    "Please enter the problem name for this start state.");

            if (problemName == null)
                return null;

            boolean goodName = ProblemModel
                    .checkForValidProblemName(problemName);

            if (!goodName) {
                String[] messages = {
                        "The problem name must be non-empty can only contain ",
                        "alphabetic characters, digits, +, - and _." };

                JOptionPane.showMessageDialog(getCtatLauncher().getActiveWindow(),
                        messages, "Invalid Name",
                        JOptionPane.INFORMATION_MESSAGE);

                continue;

            }

            return problemName;
        }
    }

    /**
     * @param listener to pass to
     * {@link #authorModeComboBox}.{@link JComboBox#addActionListener(ActionListener) addActionListener()}
     */
	public void addAuthorModeListener(ActionListener listener) {
		authorModeComboBox.addActionListener(listener);
	}
}
