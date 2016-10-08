/**
 * This class provides a graphical interface that allows the user to specify
 * configuration options for the standalone Tutorshop.
 * 
 * @author kjeffries
 */

package edu.cmu.hcii.ctat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import edu.cmu.pact.Utilities.trace;

public class CTATConfigPanel extends JPanel implements ActionListener, ChangeListener
{	
	private static final long serialVersionUID = -4206289219634303096L;

    private JRadioButton flashButton = null;
    private JRadioButton html5Button = null;
    
    private JRadioButton deployDisabled = null;
    private JRadioButton deployOnButton = null;    
    private JRadioButton deployAutoButton = null;
	
	private JTextField htdocs;
	private JTextField hostName;
	private JTextField wwwPort;
	private JTextField tsPort;
	private JTextField tsMonitorPort;
	private JTextField remoteHost;
	private JTextField etc;
	private JTextField datashopURL;
	private JTextField logdir;
	private JTextField datasetName;
	private JTextArea crossDomainPolicy;
	private JTextField adminPasswordFilename;
	private JTextField maxCachedFiles;
	private JCheckBox noNetwork;
	private JCheckBox allowWriting;
	private JCheckBox inMemoryOnly;
	private JCheckBox useLocalTutoringService;
	private JCheckBox printDebugMessages;
	private JCheckBox showNavButtons;
	@SuppressWarnings("unused")
	private JCheckBox remoteTutoringService;
	
	private JCheckBox generateIndex;
	
	private JButton restoreButton;
	private JButton okButton;
	private JButton cancelButton;
	
	private String path = null;
	
	private JButton updateButton=null;
	private JTextArea console=null;
	private JTextField className=null;
	
	private JFrame frame=null;
	
	private JTabbedPane tabbedPane =null;
	
	private JProgressBar progressBar=null;
	
	@SuppressWarnings("rawtypes")
	private JComboBox configList =null;
	private JLabel configHelp=null;
	
	/**
	 * 
	 * @param aContainer
	 */
	public CTATConfigPanel(JFrame aContainer)
	{
		frame=aContainer;
		
		this.setBorder(new EmptyBorder(2,2,2,2));		
		this.setLayout(new BoxLayout (this,BoxLayout.Y_AXIS));
		
		tabbedPane=new JTabbedPane();
		tabbedPane.addChangeListener(this);

		JPanel configPanel=createConfigPanel ();
		
		JScrollPane aConfigScroller=new JScrollPane (configPanel);
		
		tabbedPane.addTab("Configuration",null,aConfigScroller,"Change global program settings");
		
		JPanel updatePanel=createUpdatePanel ();
		tabbedPane.addTab("Update Content",null,updatePanel,"Download the latest live content");
		
		JPanel milestonesPanel = createMilestonesPanel();
		tabbedPane.addTab("Milestones",null,milestonesPanel,"View milestone set");
				
		Box buttonBox = new Box (BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(3,3,3,3));
		
		restoreButton = new JButton("Restore default settings");
		restoreButton.addActionListener(new RestoreButtonListener());
				
		buttonBox.add(restoreButton);
		
		buttonBox.add(Box.createHorizontalGlue());

		okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonListener());
		
		buttonBox.add(okButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		
		buttonBox.add(cancelButton);
						
		add(tabbedPane, BorderLayout.NORTH);
		add (buttonBox, BorderLayout.SOUTH);
		
		if (flashButton.isSelected()==true)
		{
			tsPort.setEnabled(true);
			tsMonitorPort.setEnabled(true);
		}	
		else
		{		
			tsPort.setEnabled(false);
			tsMonitorPort.setEnabled(false);			
		}		
		
		evaluateInMemory ();
	}
	/**
	 * 
	 */
	public CTATConfigPanel (JFrame aContainer,String path)
	{
		this (aContainer);
		this.path = path;
	}
	/**
	 * 
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug("CTATConfigPanel",aMessage);
	}
	/**
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createConfigPanel ()
	{
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel tfp = new JPanel();		
		
		tfp.setLayout(new GridBagLayout());
		
		tfp.setMinimumSize(new Dimension (200,100));
		//tfp.setBorder (BorderFactory.createLineBorder(Color.red));

		//>---------------------------------------------------------------
		
	    flashButton = new JRadioButton("Flash Tutor");
	    flashButton.setFont(new Font("Dialog", 1, 9));
	    flashButton.addActionListener(this);

	    html5Button = new JRadioButton("HTML5 Tutor");
	    html5Button.setFont(new Font("Dialog", 1, 9));
	    html5Button.addActionListener(this);

	    if (CTATLink.deployType==CTATLink.DEPLOYFLASH)
	    {
		    flashButton.setSelected(true);
	    }

	    if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
	    {
		    html5Button.setSelected(true);
	    }

	    ButtonGroup group = new ButtonGroup();
	    group.add(flashButton);
	    group.add(html5Button);

	    JPanel radioPanel=new JPanel ();
	    radioPanel.setLayout(new BoxLayout (radioPanel,BoxLayout.X_AXIS));
	    radioPanel.setMinimumSize(new Dimension (50,10));
	    radioPanel.setMaximumSize(new Dimension (50,22));

	    radioPanel.add(flashButton);
	    radioPanel.add(html5Button);
	    	    
	    addLabeledComponent(tfp, "Choose tutor type: ", radioPanel);
	    
		//>---------------------------------------------------------------

	    deployDisabled = new JRadioButton("Disabled");
	    deployDisabled.setFont(new Font("Dialog", 1, 9));	    
	    
	    deployOnButton = new JRadioButton("On");
	    deployOnButton.setFont(new Font("Dialog", 1, 9));
	    
	    deployAutoButton = new JRadioButton("Auto");
	    deployAutoButton.setFont(new Font("Dialog", 1, 9));	    

	    if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIDISABLED)
	    {
	    	deployDisabled.setSelected(true);
	    }

	    if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPION)
	    {
	    	deployOnButton.setSelected(true);
	    }
	    
	    if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIAUTO)
	    {
	    	deployAutoButton.setSelected(true);
	    }	    

	    ButtonGroup groupB = new ButtonGroup();
	    groupB.add(deployDisabled);
	    groupB.add(deployOnButton);
	    groupB.add(deployAutoButton);	    

	    JPanel radioBPanel=new JPanel ();
	    radioBPanel.setLayout(new BoxLayout (radioBPanel,BoxLayout.X_AXIS));
	    radioBPanel.setMinimumSize(new Dimension (50,10));
	    radioBPanel.setMaximumSize(new Dimension (50,22));

	    radioBPanel.add(deployDisabled);
	    radioBPanel.add(deployOnButton);
	    radioBPanel.add(deployAutoButton);
	    	    
	    addLabeledComponent(tfp, "Use Mobile Math Keyboard: ", radioBPanel);
	    
		//>---------------------------------------------------------------
	    
	    String[] configStrings = {"DEFAULT" , "LOCAL" , "OFFLINE" , "CS2N" , "DEMO"};

	    configList = new JComboBox(configStrings);	    
	    configList.addActionListener(this);
	    
	    addLabeledComponent(tfp, "Install HTTP Handler: ", configList);
	    
	    configList.setSelectedItem(CTATLink.handlerConfig);
	    
	    configHelp=new JLabel ();
	    configHelp.setFont(new Font("Dialog", 1, 9));
	    configHelp.setMinimumSize(new Dimension (20,30));
	    configHelp.setPreferredSize(new Dimension (120,30));
	    configHelp.setBorder (BorderFactory.createLineBorder(Color.black));
	    	    
	    addLabeledComponent(tfp, " ", configHelp);	    
	    	  
		addLabeledComponent(tfp, "Location of htdocs folder: ", htdocs = new JTextField(CTATLink.htdocs, 50));

		addLabeledComponent(tfp, "Host name: ",	hostName = new JTextField(CTATLink.hostName, 50));

		addLabeledComponent(tfp, "Port number: ", wwwPort = new JTextField(String.valueOf(CTATLink.wwwPort), 50));

		addLabeledComponent(tfp, "Tutoring service port number: ", tsPort = new JTextField(String.valueOf(CTATLink.tsPort), 50));

		addLabeledComponent(tfp, "Tutoring service monitor port number: ", tsMonitorPort = new JTextField(String.valueOf(CTATLink.tsMonitorPort), 50));

		addLabeledComponent(tfp, "Remote host name -- leave empty to run local tutors: ", remoteHost = new JTextField(CTATLink.remoteHost, 50));

		addLabeledComponent(tfp, "Location of etc folder: ", etc = new JTextField(CTATLink.etc, 50));

		addLabeledComponent(tfp, "DataShop URL: ", datashopURL = new JTextField(CTATLink.datashopURL, 50));

		addLabeledComponent(tfp, "Location of local DataShop log: ", logdir = new JTextField(CTATLink.logdir, 50));

		addLabeledComponent(tfp, "Name of dataset within DataShop: ", datasetName = new JTextField(CTATLink.datasetName, 50));

		addLabeledComponent(tfp, "Location of administrator password file: ", adminPasswordFilename = new JTextField(CTATLink.adminPasswordFilename, 50));

		addLabeledComponent(tfp, "Maximum number of files to cache in memory: ", maxCachedFiles = new JTextField(String.valueOf(CTATLink.maxCachedFiles), 50));

		if (trace.getDebugCode("tsu"))
			trace.out("tsu", "adminPasswordFilename border "+adminPasswordFilename.getBorder());

		addLabeledComponent(tfp, "No network: ", noNetwork = new JCheckBox("No network access allowed", CTATLink.noNetwork));		
		
		addLabeledComponent(tfp, "Disk access (cache, logs): ", allowWriting = new JCheckBox("Allow writing to disk (cache, logging)", CTATLink.allowWriting));
		
		allowWriting.addActionListener(this);
		
		addLabeledComponent(tfp, "In Memory User DB: ", inMemoryOnly = new JCheckBox("In-memory user database)", CTATLink.inMemoryOnly));

		addLabeledComponent(tfp, "Tutoring service: ", useLocalTutoringService = new JCheckBox("Use local tutoring service", CTATLink.useLocalTutoringService));

		addLabeledComponent(tfp, "Debug messages: ", printDebugMessages = new JCheckBox("Print debug messsages to console", CTATLink.printDebugMessages));

		addLabeledComponent(tfp, "Navigation buttons: ", showNavButtons = new JCheckBox("Show navigation buttons in tutor frame", CTATLink.showNavButtons));

		addLabeledComponent(tfp, "Cross-domain policy: ", crossDomainPolicy = new JTextArea(CTATLink.crossDomainPolicy, 5, 50));
				
	    c.gridwidth = GridBagConstraints.REMAINDER;  //end of row
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.weighty =1.0;
	    c.anchor =GridBagConstraints.NORTHWEST;
	    
	    tfp.add(new JLabel (), c);
		
		crossDomainPolicy.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		return (tfp);
	}
	/**
	 * 
	 */
	private JPanel createUpdatePanel ()
	{
		JPanel updatePanel = new JPanel();
				
		updatePanel.setLayout(new BoxLayout (updatePanel,BoxLayout.Y_AXIS));
		
	    Box buttonBox = new Box (BoxLayout.X_AXIS);
	    
	    updateButton=new JButton ();
	    updateButton.setFont(new Font("Dialog", 1, 9));
	    updateButton.setPreferredSize(new Dimension (75,20));
	    updateButton.setText("Update");
	    updateButton.addActionListener(this);

	    JLabel aLabel=new JLabel ();
	    aLabel.setText("Name of the Class to download:");
	    aLabel.setFont(new Font("Dialog", 1, 9));
	    aLabel.setPreferredSize(new Dimension (200,20));
	    
	    className=new JTextField ();
	    className.setFont(new Font("Dialog", 1, 9));
	    className.setPreferredSize(new Dimension (150,20));
	    className.setMaximumSize(new Dimension (150,20));
	    
	    progressBar=new JProgressBar();
	    progressBar.setValue(0);
	    progressBar.setFont(new Font("Dialog", 1, 9));
	    progressBar.setStringPainted(true);
	    progressBar.setPreferredSize(new Dimension (200,20));
	    progressBar.setMaximumSize(new Dimension (200,20));
	    	    
	    buttonBox.add (updateButton);
	    buttonBox.add (Box.createRigidArea(new Dimension(2,0)));
	    buttonBox.add (aLabel);
	    buttonBox.add (Box.createRigidArea(new Dimension(2,0)));
	    buttonBox.add (className);	    
	    buttonBox.add (Box.createRigidArea(new Dimension(2,0)));
	    buttonBox.add (progressBar);	    
	    buttonBox.add (Box.createRigidArea(new Dimension(2,0)));
	    	    
	    generateIndex=new JCheckBox ();
	    generateIndex.setText("Generate index html file");
	    generateIndex.setFont(new Font("Dialog", 1, 9));
	    generateIndex.setSelected(CTATLink.generateHTMLIndex);
	    
	    buttonBox.add (generateIndex);	    
	    buttonBox.add (Box.createRigidArea(new Dimension(2,0)));
	    
	    buttonBox.add(Box.createHorizontalGlue());
					    
	    updatePanel.add(buttonBox);
	    	   	    
		console=new JTextArea ();
		console.setEditable (false);
	    console.setFont(new Font("Courier",1,10));
		
		JScrollPane consoleContainer = new JScrollPane (console);
		consoleContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		updatePanel.add (Box.createRigidArea(new Dimension(0,2)));
		
		updatePanel.add (consoleContainer);
		
		return (updatePanel);
	}	
	
	/**
	 * 
	 * @return
	 */
	private JPanel createMilestonesPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout (panel,BoxLayout.Y_AXIS));
		JTextArea text = new JTextArea();
		
		JScrollPane aConsoleScroller=new JScrollPane (text);
		
		panel.add(aConsoleScroller);
		
		CTATMilestoneManager milestones = new CTATMilestoneManager();
		milestones.initialize();
		text.setText(milestones.getDebugString());
		
		return panel;
	}
	
	/**
	 * 
	 * @param tfp
	 * @param labelText
	 * @param comp
	 */
	private void addLabeledComponent (JPanel tfp, String labelText,JComponent comp) 
	{
		GridBagConstraints c = new GridBagConstraints();
		
		comp.setFont(new Font("Dialog", 1, 9));
		
		if (comp instanceof JTextComponent)
			c.insets = new Insets(1,1,1,1);
				
	    c.anchor = GridBagConstraints.EAST;
	    JLabel label = new JLabel(labelText);
	    label.setFont(new Font("Dialog", 1, 9));
	    c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
	    c.fill = GridBagConstraints.NONE;      //reset to default
	    c.weightx = 0.0;                       //reset to default
	    c.weighty =0.0;
	    c.anchor = GridBagConstraints.NORTHWEST;
	    tfp.add(label, c);

	    c.gridwidth = GridBagConstraints.REMAINDER;  //end of row
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.weighty =0.0;
	    c.anchor =GridBagConstraints.NORTHWEST;
	    
		//tfp.add(Box.createRigidArea(new Dimension(20,2)),c);
	    
	    tfp.add(comp, c);
	}	
	/**
	 * 
	 */
	@Override
	public void actionPerformed (ActionEvent event) 
	{
		Object comp=event.getSource();
		
		//>-------------------------------------------
		
		if ((comp==flashButton) || (comp==html5Button))
		{
			if (flashButton.isSelected()==true)
			{
				tsPort.setEnabled(true);
				tsMonitorPort.setEnabled(true);
			}	
			else
			{		
				tsPort.setEnabled(false);
				tsMonitorPort.setEnabled(false);			
			}
		}	
		
		//>-------------------------------------------
		
		if (comp==allowWriting)
		{
			evaluateInMemory ();
		}
		
		//>-------------------------------------------
		
		if (comp==updateButton)
		{
			restoreButton.setEnabled(false);
			okButton.setEnabled(false);
			cancelButton.setEnabled(false);
			updateButton.setEnabled(false);
			className.setEnabled(false);			
			tabbedPane.setEnabledAt(0,false);
			
			String targetClass=className.getText();
			
			if (targetClass.isEmpty()==true)
			{
				alert ("Please provide a class name");
				restoreButton.setEnabled(true);
				okButton.setEnabled(true);
				cancelButton.setEnabled(true);				
				return;
			}
			
			CTATLink.FIREClass=targetClass;
			
			if (propagateValues ()==false)
			{
				restoreButton.setEnabled(true);
				okButton.setEnabled(true);
				cancelButton.setEnabled(true);				
				return;
			}
			
			CTATLink.generateHTMLIndex=generateIndex.isSelected();
			
			CTATFlashTutorShop.downloadContent (console,progressBar,false,true,new Runnable() 
			{
				public void run() 
				{					

				}
			});
			
			restoreButton.setEnabled(true);
			okButton.setEnabled(true);
			cancelButton.setEnabled(true);
			updateButton.setEnabled(true);
			className.setEnabled(true);			
			tabbedPane.setEnabledAt(0,true);			
		}
		
		//>-------------------------------------------
		
		if (configHelp!=null)
		{
			String selConfig=(String) configList.getSelectedItem();
		
			if (selConfig.equalsIgnoreCase("DEFAULT")==true)
			{
				configHelp.setText("DEFAULT: CTATHTTPHandler ()");
			}
		
			if (selConfig.equalsIgnoreCase("LOCAL")==true)
			{
				configHelp.setText("LOCAL: CTATHTTPLocalHandler ()");
			}
		
			if (selConfig.equalsIgnoreCase("OFFLINE")==true)
			{
				configHelp.setText("OFFLINE: CTATOfflineHTTPHandler ()");
			}
		
			if (selConfig.equalsIgnoreCase("CS2N")==true)
			{
				configHelp.setText("CS2N: CTATCS2NHandler ()");
			}		
		}	
		
		//>-------------------------------------------		
	}
	/**
	 * 
	 */
	private void alert (String aMessage)
	{
		JOptionPane.showMessageDialog(frame,aMessage);
	}
	/**
	 * 
	 */
	private boolean propagateValues ()
	{
		boolean inputIsValid = true;
		
		//>----------------------------------------------------------
		
		if (flashButton.isSelected()==true)
		{
			CTATLink.deployType=CTATLink.DEPLOYFLASH;
		}
		
		if (html5Button.isSelected()==true)
		{
			CTATLink.deployType=CTATLink.DEPLOYHTML5;
		}
		
		//>----------------------------------------------------------		
			    
		if (deployDisabled.isSelected()==true)
		{
			CTATLink.deployMobileAPI=CTATLink.MOBILEAPIDISABLED;
		}
		
		if (deployOnButton.isSelected()==true)
		{
			CTATLink.deployMobileAPI=CTATLink.MOBILEAPION;
		}
		
		if (deployAutoButton.isSelected()==true)
		{
			CTATLink.deployMobileAPI=CTATLink.MOBILEAPIAUTO;
		}		
	    
		//>----------------------------------------------------------
		
		CTATLink.htdocs = htdocs.getText();
		CTATLink.hostName = hostName.getText();
		
		try 
		{
			CTATLink.wwwPort = Integer.valueOf(wwwPort.getText());
		} 
		catch (NumberFormatException ex) 
		{
			JOptionPane.showMessageDialog(null, "The port number must be an integer.");
			inputIsValid = false;
		}
		
		try 
		{
			int i = Integer.valueOf(tsPort.getText());
			if (i <= 0)
				throw new NumberFormatException("value "+i+" must be positive");
			CTATLink.tsPort = i;
		} 
		catch (NumberFormatException ex) 
		{
			JOptionPane.showMessageDialog(null, "The TutorShop port number must be a positive integer: "+ex);
			inputIsValid = false;
		}
		
		try 
		{
			CTATLink.tsMonitorPort = Integer.valueOf(tsMonitorPort.getText());
		} 
		catch (NumberFormatException ex) 
		{
			JOptionPane.showMessageDialog(null, "The TutorShop monitor port number must be an integer.");
			inputIsValid = false;
		}
		
		CTATLink.remoteHost = remoteHost.getText();
		CTATLink.etc = etc.getText();
		CTATLink.datashopURL = datashopURL.getText();
		CTATLink.logdir = logdir.getText();
		CTATLink.datasetName = datasetName.getText();
		CTATLink.crossDomainPolicy = crossDomainPolicy.getText();
		CTATLink.adminPasswordFilename = adminPasswordFilename.getText();
		
		try 
		{
			CTATLink.maxCachedFiles = Integer.valueOf(maxCachedFiles.getText());
		} 
		catch (NumberFormatException ex) 
		{
			JOptionPane.showMessageDialog(null, "The number of cached files must be an integer: "+ex);
			inputIsValid = false;
		}
		
		CTATLink.noNetwork=noNetwork.isSelected();
		CTATLink.allowWriting = allowWriting.isSelected();
		CTATLink.useLocalTutoringService = useLocalTutoringService.isSelected();
		CTATLink.printDebugMessages = printDebugMessages.isSelected();
		CTATLink.showNavButtons = showNavButtons.isSelected();
		
		CTATLink.handlerConfig=String.valueOf (configList.getSelectedItem());
		
		if (!inputIsValid)
		{
			return (false);
		}		
		
		return (true);
	}
	
	/**
	 * 
	 */
	private class RestoreButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Restore all the input fields to the default values of the corresponding CTATLink fields.
			// The hard-coded values here should match the defaults specified in CTATLink.
			if (trace.getDebugCode("tsu"))
				trace.out("tsu", "actionPerformed() size "+getSize());
			
			wwwPort.setText("8080");
			tsPort.setText("4000");
			tsMonitorPort.setText("4001");
			remoteHost.setText("");
			datashopURL.setText("http://digger.pslc.cs.cmu.edu/log/server/sandboxlogger.php");
			logdir.setText("logs/");
			datasetName.setText("FIRE Preview");
			maxCachedFiles.setText(Integer.toString(CTATLink.maxCachedFiles));
			allowWriting.setSelected(true);
			useLocalTutoringService.setSelected(true);
			printDebugMessages.setSelected(false);
		}
	}
	
	
	/**
	 * 
	 */
	private class OKButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (tabbedPane.getSelectedIndex()==1)
			{
				System.exit(0); // Process as a regular OK button
			}
			
			// Confirm that the user wants to make a config file
			int answer=JOptionPane.showConfirmDialog(null,
					"Are you sure that you want to create a configuration file with the specified settings? " +
							"This will overwrite any existing configuration file.", "Please confirm", JOptionPane.YES_NO_OPTION);
			
			// If yes, update CTATLink according to the specified settings and make a config file, then exit
			if (answer==JOptionPane.YES_OPTION)
			{
				if (propagateValues ()==false)
				{
					return;
				}
				
				boolean success=false;
				
				if(path == null)
				{
					success = (new CTATDesktopFileManager()).saveConfigData();
				}
				else
				{
					success = (new CTATDesktopFileManager()).saveConfigData(path);
				}
				if(success)
				{
					JOptionPane.showMessageDialog(null, "The configuration file has been saved.");
					System.exit(0);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "An exception occurred which prevented the configuration file from being saved.");
				}
			}
		}
	}
		
	/**
	 * 
	 */
	private class CancelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0); // exit on cancel
		}
	}
	/**
	 * 
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) 
	{
		debug ("stateChanged ()");
		
		// Config panel
		if (tabbedPane.getSelectedIndex()==0)
		{
			if (cancelButton!=null)
				cancelButton.setVisible(true);
			
			if (restoreButton!=null)
				restoreButton.setVisible(true);
		}
		
		// Download panel
		if (tabbedPane.getSelectedIndex()==1)
		{
			if (cancelButton!=null)
				cancelButton.setVisible(false);
			
			if (restoreButton!=null)
				restoreButton.setVisible(false);			
		}		
	}
	/**
	 * 
	 */
	public void evaluateInMemory ()
	{
		debug ("evaluateInMemory ()");
		
		if (allowWriting.isSelected()==false)
		{
			inMemoryOnly.setEnabled(true);
		}
		else
		{
			inMemoryOnly.setEnabled(false);
		}
	}
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) 
    {
    	String file = "/edu/cmu/pact/hcii/ctat"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	
    	return new ImageIcon(url);    
    }	
}
