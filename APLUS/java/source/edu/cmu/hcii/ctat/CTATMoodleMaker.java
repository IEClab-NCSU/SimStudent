package edu.cmu.hcii.ctat;

import javax.swing.*;        
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import edu.cmu.hcii.ctat.wizard.CTATWizardFinishPage;
import edu.cmu.hcii.ctat.wizard.CTATWizardPanelDescription;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/** 
 * @author vvelsen
 *
 * This application generates Moodle compatible SCORM packages (zip files)
 * according to this specification:
 * 
 * http://www.imsglobal.org/content/packaging/cpv1p1p4/imscp_infov1p1p4.html
 */
public class CTATMoodleMaker extends CTATMoodleTools implements ActionListener
{			
	private JPanel welcomePanel=null;
		
	private JRadioButton goSingle=null;
	private JRadioButton goMultiple=null;
	
	private JRadioButton flashButtonSingle=null;
	private JRadioButton html5ButtonSingle=null;
	
	private JRadioButton flashButtonMultiple=null;
	private JRadioButton html5ButtonMultiple=null;
	
	private JButton aBrowseSWF=null;
	private JButton aBrowseBRD=null;
	private JButton aBrowse=null;
	
	private JTextField swfLocation=null;
	private JTextField brdLocation=null;
	
	private JTextField anInput=null;
	
	private JTextField tsHostSingle=null;
	private JTextField tsPortSingle=null;
	
	private JTextField tsHostMultiple=null;
	private JTextField tsPortMultiple=null;	
	
	private JTextField datashopURLSingle=null;
	private JTextField datashopURLMultiple=null;
	
	private File tempIndex=null;
	private File tempManifest=null;
	
	private CTATCurriculum curriculum=null;
	private CTATProblemSet fProblemSet=null;
	
	//private CTATWizardPanelDescription choicePoint=null;	
	private CTATWizardPanelDescription targetPoint=null;
	private JPanel singlePanel=null;    	
	private JPanel multiplePanel=null;	
	
	private int choice=0; // 0 = single tutor, 1 = problem sets

	private ArrayList<File> tempFiles=new ArrayList<File> ();
	
    private Task task;

    class Task extends SwingWorker<Void, Void> 
    {
        @Override
        public Void doInBackground() 
        {
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            
            while (progress < 100) 
            {
                //Sleep for up to one second.
                try 
                {
                    Thread.sleep(random.nextInt(1000));
                } 
                catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() 
        {
        	/*
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
            */
        }
    }	
	
	/**
	 * 
	 */
    public CTATMoodleMaker () 
    {
    	setClassName ("CTATMoodleMaker");
    	debug ("CTATMoodleMaker ()");     
    }
    /**
     * 
     */
    public void init ()
    {
    	debug ("init ()");
    	
    	welcomePanel=this.createWizardJPanel();    
    	generateWelcomePanel (welcomePanel);    	
    	addPage ("Start",welcomePanel);
    	
    	JPanel second=this.createWizardJPanel();    
    	generateChoicePanel (second);
    	addPage ("Package Type",second);

    	singlePanel=this.createWizardJPanel();    
    	generateSingleConfigPanel (singlePanel);

    	multiplePanel=this.createWizardJPanel();    
    	generateMultipleConfigPanel (multiplePanel);
    	
    	targetPoint=addPage ("Configure",singlePanel);
    	targetPoint.setPanelContent(multiplePanel);
    	
    	CTATWizardFinishPage finishPage=new CTATWizardFinishPage ();
    	addPage ("Generate",finishPage);
    }        
    /**
     * 
     */
    private void generateWelcomePanel (JPanel aPanel)
    {
    	debug ("generateWelcomePanel ()");
    	
    	JLabel welcomeMessage=new JLabel ();
    	welcomeMessage.setText ("<html>This wizard will help you generate a package from your existing tutors<br> that can be used by any platform that supports SCORM.<br><br></html>");
    	
    	JLabel explanationMessage=new JLabel ();
    	explanationMessage.setText ("<html>To use this wizard you will need:<br><ul><li>One or more Flash files (if you want to show Flash tutors)<li>One or more BRD files</ul></html>");
    	
    	aPanel.add(welcomeMessage);
		aPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		aPanel.add(explanationMessage);
    }
    /**
     * 
     */
    private void generateChoicePanel (JPanel aPanel)
    {
    	debug ("generateChoicePanel ()");
    	
	    goSingle = new JRadioButton("Create an Activity with one CTAT tutor");
	    goSingle.setFont(new Font("Dialog", 1, 9));
	    goSingle.setSelected(true);
	    goSingle.setActionCommand("GOSINGLE");
	    goSingle.addActionListener(this);

	    goMultiple = new JRadioButton("Create an Activity from a sequence of Problem Sets");
	    goMultiple.setFont(new Font("Dialog", 1, 9));
	    goMultiple.setActionCommand("GOMULTIPLE");
	    goMultiple.addActionListener(this);

	    ButtonGroup group = new ButtonGroup();
	    group.add(goSingle);
	    group.add(goMultiple);    
	    
    	JLabel explanationMessage=new JLabel ();
    	explanationMessage.setText ("<html>You can generate two types of SCORM packages depending on how you want to integrate Tutors into your LMS (let's say Moodle). Most LMSs allow you to create an Activity and then import a SCORM package to represent that Activity. In that case you might want to use just one tutor. However you could also use the CTAT problem set approach to upload an entire course. Choose the type of package you would like to generate below:<br><br><br></html>");
    	
    	aPanel.add(explanationMessage);
		aPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		aPanel.add(goSingle);
		aPanel.add(goMultiple);
    }	    
    /**
     * 
     */
    private void generateSingleConfigPanel (JPanel aPanel)
    {
    	debug ("generateSingleConfigPanel ()");
    	
    	//>---------------------------------------------------------------
    	
		Box swfButtonBox = new Box (BoxLayout.X_AXIS);
		swfButtonBox.setBorder(new EmptyBorder(3,3,3,3));
    	
		JLabel aLabel=new JLabel ();
		aLabel.setText("Choose a SWF file: ");
		aLabel.setFont(new Font("Dialog", 1, 9));
		
		swfButtonBox.add(aLabel);
		
		swfLocation=new JTextField ();		
		swfLocation.setFont(new Font("Dialog", 1, 9));
		swfLocation.setMinimumSize (new Dimension (50,20));
		swfLocation.setMaximumSize (new Dimension (600,20));
		
		swfButtonBox.add(swfLocation);
		
		aBrowseSWF=new JButton ();
		aBrowseSWF.setText("Browse...");
		aBrowseSWF.setFont(new Font("Dialog", 1, 9));
		aBrowseSWF.setPreferredSize(new Dimension (85,20));
		aBrowseSWF.setActionCommand("aBrowseSWF");
		aBrowseSWF.addActionListener(this);
		
		swfButtonBox.add(aBrowseSWF);
		
		//swfButtonBox.add(Box.createHorizontalGlue());
								
    	aPanel.add(swfButtonBox);
    	
    	//>---------------------------------------------------------------
    	
		Box brdButtonBox = new Box (BoxLayout.X_AXIS);
		swfButtonBox.setBorder(new EmptyBorder(3,3,3,3));
    	
		JLabel aLabelBRD=new JLabel ();
		aLabelBRD.setText("Choose a BRD file: ");
		aLabelBRD.setFont(new Font("Dialog", 1, 9));
		
		brdButtonBox.add(aLabelBRD);
		
		brdLocation=new JTextField ();		
		brdLocation.setFont(new Font("Dialog", 1, 9));
		brdLocation.setMinimumSize (new Dimension (50,20));
		brdLocation.setMaximumSize (new Dimension (600,20));
		
		brdButtonBox.add(brdLocation);
		
		aBrowseBRD=new JButton ();
		aBrowseBRD.setText("Browse...");
		aBrowseBRD.setFont(new Font("Dialog", 1, 9));
		aBrowseBRD.setPreferredSize(new Dimension (85,20));
		aBrowseBRD.setActionCommand("aBrowseBRD");
		aBrowseBRD.addActionListener(this);
		
		brdButtonBox.add(aBrowseBRD);
		
		//brdButtonBox.add(Box.createHorizontalGlue());
								
    	aPanel.add(brdButtonBox);    	
    	
    	//>----------------------------------------------------------------
    	
	    flashButtonSingle = new JRadioButton("Flash Tutor");
	    flashButtonSingle.setFont(new Font("Dialog", 1, 9));
	    flashButtonSingle.setSelected(true);
	    flashButtonSingle.setActionCommand("flashButtonSingle");
	    flashButtonSingle.addActionListener(this);

	    html5ButtonSingle = new JRadioButton("HTML5 Tutor");
	    html5ButtonSingle.setFont(new Font("Dialog", 1, 9));
	    html5ButtonSingle.setActionCommand("html5ButtonSingle");
	    html5ButtonSingle.addActionListener(this);

	    ButtonGroup group = new ButtonGroup();
	    group.add(flashButtonSingle);
	    group.add(html5ButtonSingle);

		Box radioBox = new Box (BoxLayout.X_AXIS);
		radioBox.setBorder(new EmptyBorder(3,3,3,3));

		JLabel radioLabel=new JLabel ();
		radioLabel.setText("Tutor Type: ");
		radioLabel.setFont(new Font("Dialog", 1, 9));
		
		radioBox.add(radioLabel);
		radioBox.add(flashButtonSingle);
		radioBox.add(html5ButtonSingle);    	
	    
		radioBox.add (Box.createHorizontalGlue());
		
	    aPanel.add(radioBox);
	    
    	//>----------------------------------------------------------------
	    
	    Box aBox=generateBox ();
	    
		JLabel tLabel=new JLabel ();
		tLabel.setText("Tutoring Service Host:");
		tLabel.setFont(new Font("Dialog", 1, 9));
	    
		tsHostSingle=new JTextField ();
		tsHostSingle.setFont(new Font("Dialog", 1, 9));
		tsHostSingle.setText("tutorshop.org");
		
		aBox.add (tLabel);
		aBox.add (tsHostSingle);
		aBox.add (Box.createHorizontalGlue());
		
		aPanel.add(aBox);
		
    	//>----------------------------------------------------------------		
		
		Box bBox=generateBox ();
		
		JLabel wLabel=new JLabel ();
		wLabel.setText("Tutoring Service Port:");
		wLabel.setFont(new Font("Dialog", 1, 9));		
		
		tsPortSingle=new JTextField ();
		tsPortSingle.setFont(new Font("Dialog", 1, 9));
		tsPortSingle.setText("1502");
		
		bBox.add (wLabel);
		bBox.add (tsPortSingle);
		bBox.add (Box.createHorizontalGlue());
		
		aPanel.add(bBox);		
		
    	//>----------------------------------------------------------------		
		
		Box cBox=generateBox ();
		
		JLabel yLabel=new JLabel ();
		yLabel.setText("DataShop URL:");
		yLabel.setFont(new Font("Dialog", 1, 9));		
		
		datashopURLSingle=new JTextField ();	    
		datashopURLSingle.setFont(new Font("Dialog", 1, 9));
		datashopURLSingle.setText("http://augustus.pslc.cs.cmu.edu/log/server/sandboxlogger.php");
		
		cBox.add (yLabel);
		cBox.add (datashopURLSingle);
		cBox.add (Box.createHorizontalGlue());
		
		aPanel.add(cBox);		
	    		
    	//>----------------------------------------------------------------
    	
    	//aPanel.add(Box.createVerticalGlue());    	
    }
    /**
     * 
     */
    private void generateMultipleConfigPanel (JPanel aPanel)
    {
    	debug ("generateMultipleConfigPanel ()");
    	
    	//>---------------------------------------------------------------
    	
		Box buttonBox = new Box (BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(3,3,3,3));
    	
		JLabel aLabel=new JLabel ();
		aLabel.setText("FlashTutors Directory: ");
		aLabel.setFont(new Font("Dialog", 1, 9));
		
		buttonBox.add(aLabel);
		
		anInput=new JTextField ();		
		anInput.setFont(new Font("Dialog", 1, 9));
		anInput.setMinimumSize (new Dimension (50,20));
		anInput.setMaximumSize (new Dimension (600,20));
		
		buttonBox.add(anInput);
		
		aBrowse=new JButton ();
		aBrowse.setText("Browse...");
		aBrowse.setFont(new Font("Dialog", 1, 9));
		aBrowse.setPreferredSize(new Dimension (85,20));
		aBrowse.addActionListener(this);
		
		buttonBox.add(aBrowse);
		
		//buttonBox.add (Box.createHorizontalGlue());
						
    	aPanel.add(buttonBox);
    	
    	//>----------------------------------------------------------------
    	
	    flashButtonMultiple = new JRadioButton("Flash Tutor");
	    flashButtonMultiple.setFont(new Font("Dialog", 1, 9));
	    flashButtonMultiple.setSelected(true);
	    flashButtonMultiple.addActionListener(this);

	    html5ButtonMultiple = new JRadioButton("HTML5 Tutor");
	    html5ButtonMultiple.setFont(new Font("Dialog", 1, 9));
	    html5ButtonMultiple.addActionListener(this);

	    ButtonGroup group = new ButtonGroup();
	    group.add(flashButtonMultiple);
	    group.add(html5ButtonMultiple);

		Box radioBox = new Box (BoxLayout.X_AXIS);
		radioBox.setBorder(new EmptyBorder(3,3,3,3));

		JLabel radioLabel=new JLabel ();
		radioLabel.setText("Tutor Type: ");
		radioLabel.setFont(new Font("Dialog", 1, 9));
		
		radioBox.add(radioLabel);
		radioBox.add(flashButtonMultiple);
		radioBox.add(html5ButtonMultiple);    	
	    
		radioBox.add (Box.createHorizontalGlue());
		
	    aPanel.add(radioBox);
	    
    	//>----------------------------------------------------------------
	    
	    Box aBox=generateBox ();
	    
		JLabel tLabel=new JLabel ();
		tLabel.setText("Tutoring Service Host:");
		tLabel.setFont(new Font("Dialog", 1, 9));
	    
		tsHostMultiple=new JTextField ();
		tsHostMultiple.setFont(new Font("Dialog", 1, 9));
		tsHostMultiple.setText("tutorshop.org");
		
		aBox.add (tLabel);
		aBox.add (tsHostMultiple);
		aBox.add (Box.createHorizontalGlue());
		
		aPanel.add(aBox);
		
    	//>----------------------------------------------------------------		
		
		Box bBox=generateBox ();
		
		JLabel wLabel=new JLabel ();
		wLabel.setText("Tutoring Service Port:");
		wLabel.setFont(new Font("Dialog", 1, 9));		
		
		tsPortMultiple=new JTextField ();
		tsPortMultiple.setFont(new Font("Dialog", 1, 9));
		tsPortMultiple.setText("1502");
		
		bBox.add (wLabel);
		bBox.add (tsPortMultiple);
		bBox.add (Box.createHorizontalGlue());
		
		aPanel.add(bBox);		
		
    	//>----------------------------------------------------------------		
		
		Box cBox=generateBox ();
		
		JLabel yLabel=new JLabel ();
		yLabel.setText("DataShop URL:");
		yLabel.setFont(new Font("Dialog", 1, 9));		
		
		datashopURLMultiple=new JTextField ();	    
		datashopURLMultiple.setFont(new Font("Dialog", 1, 9));
		datashopURLMultiple.setText("http://augustus.pslc.cs.cmu.edu/log/server/sandboxlogger.php");
		
		cBox.add (yLabel);
		cBox.add (datashopURLMultiple);
		cBox.add (Box.createHorizontalGlue());
		
		aPanel.add(cBox);		
	    		
    	//>----------------------------------------------------------------
    	
    	//aPanel.add(Box.createVerticalGlue());
    }
    /**
     * 
     */
    private Box generateBox ()
    {
		Box radioBox = new Box (BoxLayout.X_AXIS);
		radioBox.setBorder(new EmptyBorder(3,3,3,3));
		
		return (radioBox);
    }
    /**
     * 
     */
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		debug ("actionPerformed ()");
				
		super.actionPerformed(arg0);
				
		Component comp=(Component) arg0.getSource();
		
		debug ("comp: " + comp.getClass());
				
		if (arg0.getActionCommand().equalsIgnoreCase("GOSINGLE")==true)
		{
			debug ("(comp==goSingle)");
			
			choice=0;
			
			if (targetPoint!=null)
			{
				targetPoint.setPanelIndex(0);
			}
			else
				debug ("Internal error: targetPoint object is null");
		}
		
		if (arg0.getActionCommand().equalsIgnoreCase("GOMULTIPLE")==true)
		{
			debug ("(comp==goMultiple)");
			
			choice=1;
			
			if (targetPoint!=null)
			{
				targetPoint.setPanelIndex(1);
			}
			else
				debug ("Internal error: targetPoint object is null");			
		}
		
		if (arg0.getActionCommand().equalsIgnoreCase("flashButtonSingle")==true)
		{
			debug ("(comp==flashButtonSingle)");
			
			swfLocation.setEditable(true);
			swfLocation.setEnabled(true);
		}
		
		if (arg0.getActionCommand().equalsIgnoreCase("html5ButtonSingle")==true)
		{
			debug ("(comp==html5ButtonSingle)");
			
			swfLocation.setEditable(false);
			swfLocation.setEnabled(false);
		}
				
		if (arg0.getActionCommand().equalsIgnoreCase("aBrowseSWF")==true)
		{
			debug ("(comp==aBrowseSWF)");
			
			JFileChooser fc = new JFileChooser();
		    FileFilter filter1 = new CTATFileFilter ("SWF", new String[] { "SWF" });
		    fc.setFileFilter(filter1);			
			Integer opt = fc.showOpenDialog(frame);
						
			switch (opt)
			{
				case JFileChooser.CANCEL_OPTION:
													break;
				case JFileChooser.APPROVE_OPTION:
													File f = fc.getSelectedFile();

													swfLocation.setText(f.getAbsolutePath());
													
													break;
				case JFileChooser.ERROR_OPTION:
													break; 				
			}			
		}
		
		if (arg0.getActionCommand().equalsIgnoreCase("aBrowseBRD")==true)
		{
			debug ("(comp==aBrowseBRD)");
			
			JFileChooser fc = new JFileChooser();
		    FileFilter filter2 = new CTATFileFilter ("BRD", new String[] { "BRD" });
		    fc.setFileFilter(filter2);			
			Integer opt = fc.showOpenDialog(frame);
			
			switch (opt)
			{
				case JFileChooser.CANCEL_OPTION:
													break;
				case JFileChooser.APPROVE_OPTION:
													File f = fc.getSelectedFile();

													brdLocation.setText(f.getAbsolutePath());
													
													break;
				case JFileChooser.ERROR_OPTION:
													break; 				
			}			
		}		
						
		if (arg0.getActionCommand().equalsIgnoreCase("aBrowse")==true)
		{
			debug ("(comp==aBrowse)");
			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			Integer opt = fc.showSaveDialog(frame);	
			
			switch (opt)
			{
				case JFileChooser.CANCEL_OPTION:
													break;
				case JFileChooser.APPROVE_OPTION:
													File f = fc.getSelectedFile();

													anInput.setText(f.getAbsolutePath());
													
													break;
				case JFileChooser.ERROR_OPTION:
													break; 				
			}
		}		
	}
    /**
     * 
     */
    protected Boolean processFinish ()
    {
    	debug ("processFinish ()");
    	
    	tempFiles=new ArrayList<File> ();
    	
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File("CTATMoodlePackage.zip"));
	
		//In response to a button click:
		int returnVal = fc.showSaveDialog(frame);
	
		switch (returnVal)
		{
			case JFileChooser.CANCEL_OPTION:
												break;
			case JFileChooser.APPROVE_OPTION:
												File f = fc.getSelectedFile();
												String filePath = f.getPath();
												if(!filePath.toLowerCase().endsWith(".zip"))
												{
													f = new File(filePath + ".zip");
												}
												
												if (f.exists()==true)
												{
										           	Object[] options = {"Yes",
							           	                    			"No",
							           	                    			"Cancel"};													
													
										           	int n = JOptionPane.showOptionDialog(null,
										               	    "The file " + f.getName() + " already exists, would you like to overwrite it?",
										               	    "CTAT Info Panel",
										               	    JOptionPane.YES_NO_CANCEL_OPTION,
										               	    JOptionPane.QUESTION_MESSAGE,
										               	    null,
										               	    options,
										               	    options[2]);
											           	    
											        if (n==0)
											        { 
														if (generatePackage (f)==false)
														{
															alert ("Error generating SCORM package " + f.getName());
														}
														else
														{
															alert ("Successfully generated SCORM package " + f.getName());
														}													
													} 
												}
												else
												{											
													if (generatePackage (f)==false)
													{
														alert ("Error generating SCORM package " + f.getName());
													}
													else
													{
														alert ("Successfully generated SCORM package " + f.getName());
													}
												}	
												
												return (true);												
			case JFileChooser.ERROR_OPTION:
												break; 			
		}
		
		return (false);
    }	
	/**
	 * 
	 */
	private CTATFileEntry addFileEntry (String anEntryBase,String anEntry)
	{
		debug ("addFileEntry ("+anEntryBase+","+anEntry+")");
		
		CTATFileEntry newEntry=new CTATFileEntry ();
		newEntry.basePath=anEntryBase;
		newEntry.filePath=anEntry;
		
		fileList.add(newEntry);
		
		return (newEntry);
	}	
	/**
	 * 
	 */
	private Boolean generatePackage (File outputZip)
	{
		debug ("generatePackage ()");
		
		// General prep 
		
		CTATLink.deployMobileAPI=CTATLink.MOBILEAPIDISABLED;
		
		// Run package generation
		
		if (choice==0)
		{						
			if (propagateValuesSingle ()==false)
			{
				return (false);
			}
			
			fileList=new ArrayList<CTATFileEntry> ();
																
			addFileEntry("moodletemplates/","adlcp_rootv1p2.xsd");
			addFileEntry("moodletemplates/","APIWrapper.js");
			addFileEntry("moodletemplates/","ims_xml.xsd");
			addFileEntry("moodletemplates/","imscp_root1p1p2.xsd");
			addFileEntry("moodletemplates/","imsmd_rootv1p2p1.xsd");
			addFileEntry("moodletemplates/","scormfs.js");
			addFileEntry("moodletemplates/","writeFlash.js");
			
			CTATFileEntry manifestEntry=addFileEntry(tempManifest.getParent(),"imsmanifest.xml");
			manifestEntry.fullPath=tempManifest.getAbsolutePath();
			
			tempFiles.add(tempManifest);
			
			CTATFileEntry indexEntry=addFileEntry(tempIndex.getParent(),"index.html");
			indexEntry.fullPath=tempIndex.getAbsolutePath();
			
			tempFiles.add(tempIndex);
			
			if (generateIndexFileSingle ()==false)
				return (false);
			
	    	// We can only generate the manifest after we know all the files that will go into the package
	    	
			if (generateManifest ()==false)
				return (false);
	    	
	    	zipIt(outputZip.getAbsolutePath());			
		}
		
		if (choice==1)
		{
			if (propagateValuesMultiple ()==false)
			{
				return (false);
			}
								
			fileList=new ArrayList<CTATFileEntry> ();
			
			flashTutorBasePath=anInput.getText();
			
			if (generateIndexFileMultiple ()==false)
				return (false);
								
			addFileEntry("moodletemplates/","adlcp_rootv1p2.xsd");
			addFileEntry("moodletemplates/","APIWrapper.js");
			addFileEntry("moodletemplates/","ims_xml.xsd");
			addFileEntry("moodletemplates/","imscp_root1p1p2.xsd");
			addFileEntry("moodletemplates/","imsmd_rootv1p2p1.xsd");
			addFileEntry("moodletemplates/","scormfs.js");
			addFileEntry("moodletemplates/","writeFlash.js");
			
			CTATFileEntry manifestEntry=addFileEntry(tempManifest.getParent(),"imsmanifest.xml");
			manifestEntry.fullPath=tempManifest.getAbsolutePath();
			
			tempFiles.add(tempManifest);
			
			CTATFileEntry indexEntry=addFileEntry(tempIndex.getParent(),"index.html");
			indexEntry.fullPath=tempIndex.getAbsolutePath();
			
			tempFiles.add(tempIndex);
							
	    	generateFileList(new File(flashTutorBasePath));
	    	
	    	// We can only generate the manifest after we know all the files that will go into the package
	    	
			if (generateManifest ()==false)
				return (false);
	    	
	    	zipIt(outputZip.getAbsolutePath());			
		}
		
		try 
		{
			Thread.sleep(1000);
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CTATWizardFinishPage finishPage=(CTATWizardFinishPage) this.getCurrentPanel();
		
		if (finishPage!=null)
		{
			JProgressBar progress=finishPage.getProgressBar();
			
			if (progress!=null)
			{
				progress.setMaximum(100);
				progress.setValue(100);
			}
		}
		
		return (true);
	}	
	/**
	 * 
	 */
	private Boolean generateManifest ()
	{
		debug ("generateManifest ()");
		
		StringBuffer manifestBuffer=new StringBuffer ();
		
		manifestBuffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		manifestBuffer.append("<manifest identifier=\"CQFaceism\" version=\"1.0\" xmlns=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\" xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_rootv1p2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2 imscp_rootv1p1p2.xsd http://www.imsglobal.org/xsd/imsmd_rootv1p2p1 imsmd_rootv1p2p1.xsd http://www.adlnet.org/xsd/adlcp_rootv1p2 adlcp_rootv1p2.xsd\">\n");
		
		manifestBuffer.append("<organizations default=\"CMU\">");
		manifestBuffer.append("<organization identifier=\"CMU\">");
		manifestBuffer.append("<title>CTAT Example Moodle Tutors</title>");
		manifestBuffer.append("<item identifier=\"problemset\" identifierref=\"ctat\" isvisible=\"true\">");
		manifestBuffer.append("<title>CTAT Example Moodle Tutors</title>");
		manifestBuffer.append("</item>");
		manifestBuffer.append("</organization>");
		manifestBuffer.append("</organizations>");	
		
		manifestBuffer.append("<metadata>\n");
		manifestBuffer.append("<schema>ADL SCORM</schema>\n");
		manifestBuffer.append("<schemaversion>1.2</schemaversion>\n");
		manifestBuffer.append("<lom xmlns=\"http://www.imsglobal.org/xsd/imsmd_rootv1p2p1\">\n");
		manifestBuffer.append("</lom>\n");
		manifestBuffer.append("</metadata>\n");
				
		manifestBuffer.append("<resources>\n");
					
		int index=0;
		
		for (CTATFileEntry file : this.fileList)
		{
			debug ("Registering: " + file.filePath + " in the manifest");

			if (file.filePath.equalsIgnoreCase("index.html")==true)
			{
				manifestBuffer.append("<resource adlcp:scormtype=\"sco\" href=\""+file.filePath+"\" identifier=\"ctat\" type=\"webcontent\">\n");
			}
			else
			{
				manifestBuffer.append("<resource adlcp:scormtype=\"sco\" href=\""+file.filePath+"\" identifier=\"ctat-"+index+"\" type=\"webcontent\">\n");
				index++;
			}	
			
			manifestBuffer.append("<file href=\""+file.filePath+"\"/>\n");
			manifestBuffer.append("</resource>\n");
		}
		
		manifestBuffer.append("</resources>\n");

		manifestBuffer.append("</manifest>\n");

		return (CTATLink.fManager.setContents(tempManifest.getAbsolutePath(),manifestBuffer.toString()));	
	}
	/**
	 * 
	 */
	private Boolean generateIndexFileSingle ()
	{
		debug ("generateIndexFileSingle ()");
				
		CTATObjectTagDriver driver=null;
		String indexTemplate=null;
		
		if (flashButtonSingle.isSelected()==true)
		{
			driver=new CTATFlashDriver ();
			indexTemplate=CTATLink.fManager.getContents ("moodletemplates/index-flash.html");
		}
		else
		{
			driver=new CTATHTML5Driver();
			indexTemplate=CTATLink.fManager.getContents ("moodletemplates/index-html5.html");
		}

		driver.setSCORMCompliant (true);
				
		if (indexTemplate==null)
		{
			debug ("Error: can't process root template");
			return (false);
		}
							
		File swfTranslator=new File (swfLocation.getText());
		File brdTranslator=new File (brdLocation.getText());
				
		addFileEntry(swfTranslator.getParent(),swfTranslator.getName());
		addFileEntry(brdTranslator.getParent(),brdTranslator.getName());
		
		String flashTags=driver.generateObjectTags (swfTranslator,brdTranslator);
		
		String composite=indexTemplate.replaceFirst("flashtags",flashTags);
					
		CTATLink.fManager.setContents(tempIndex.getAbsolutePath(),composite);

		return (true);
	}	
	/**
	 * 
	 */
	private Boolean generateIndexFileMultiple ()
	{
		debug ("generateIndexFileMultiple ()");
				
		CTATObjectTagDriver driver=null;
		String indexTemplate=null;
		
		if (flashButtonMultiple.isSelected()==true)
		{
			driver=new CTATFlashDriver ();
			indexTemplate=CTATLink.fManager.getContents ("moodletemplates/index-flash.html");
		}
		else
		{
			driver=new CTATHTML5Driver();
			indexTemplate=CTATLink.fManager.getContents ("moodletemplates/index-html5.html");
		}
				
		if (indexTemplate==null)
		{
			debug ("Error: can't process root template");
			return (false);
		}
		
		ArrayList<CTATProblemSet> problemSets=null;
		
		if (curriculum!=null)
		{
			debug ("We have a curriculum, loading first problem set in assignment ...");
			
			problemSets=curriculum.getProblemSets(curriculum.getFirstAssignment ());
		}
		else
		{
			debug ("We don't have a valid curriculum, using directory.txt ...");
			problemSets=CTATLink.fDirectoryTXT.getEntries();
		}		
		
		String composite="";
		
		fProblemSet=problemSets.get(0);
				
		if (fProblemSet!=null)
		{							
			fProblemSet.reset();
							
			CTATProblem startProblem=fProblemSet.getNextProblem ();
														
			String flashTags=driver.generateObjectTags (startProblem,fProblemSet);
			
			composite=indexTemplate.replaceFirst ("flashtags",flashTags);
		}
		
		CTATLink.fManager.setContents(tempIndex.getAbsolutePath(),composite);

		return (true);
	}
	/**
	 *  
	 */
	private Boolean propagateValuesSingle ()
	{
		debug ("propagateValuesSingle ()");

		CTATLink.hostName=tsHostSingle.getText();
		CTATLink.tsPort=Integer.parseInt(tsPortSingle.getText());
		CTATLink.datashopURL=datashopURLSingle.getText();
		
		if (swfLocation.getText().isEmpty()==true)
		{
			alert ("Please provide the name of a SWF file");
			return (false);
		}
		
		if (brdLocation.getText().isEmpty()==true)
		{
			alert ("Please provide the name of a BRD file");
			return (false);
		}		
		
		try 
		{
			tempManifest=File.createTempFile ("imsmanifest",".xml");
		} 
		catch (IOException e) 
		{
			alert ("Unable to create temporary manifest file: " + e.getMessage());
		}		
		
		try 
		{
			tempIndex=File.createTempFile ("index",".html");
		} 
		catch (IOException e) 
		{
			alert ("Unable to create temporary index file: " + e.getMessage());
			return (false);
		}			

		return (true);
	}
	/**
	 *  
	 */
	private Boolean propagateValuesMultiple ()
	{
		debug ("propagateValuesMultiple ()");
		
		if (anInput.getText().isEmpty()==true)
		{
			alert ("Please provide the name of a location that has a directory.txt file");
			return (false);
		}
		
		try 
		{
			tempManifest=File.createTempFile ("imsmanifest",".xml");
		} 
		catch (IOException e) 
		{
			alert ("Unable to create temporary manifest file: " + e.getMessage());
		}		
		
		try 
		{
			tempIndex=File.createTempFile ("index",".html");
		} 
		catch (IOException e) 
		{
			alert ("Unable to create temporary index file: " + e.getMessage());
			return (false);
		}			
		
		CTATLink.htdocs=(anInput.getText()+"/");		
		CTATLink.hostName=tsHostMultiple.getText();
		CTATLink.tsPort=Integer.parseInt(tsPortMultiple.getText());
		CTATLink.datashopURL=datashopURLMultiple.getText();
	
		if (CTATLink.fManager.doesFileExist(CTATLink.htdocs + "/curriculum.xml")==true)
		{
			debug ("Curriculum file exists, loading ...");
		    		
			String currContents=CTATLink.fManager.getContents(CTATLink.htdocs + "/curriculum.xml");
		
			try 
			{
				curriculum = new CTATCurriculum (currContents);
			} 
			catch (Exception e) 
			{
				e.printStackTrace(System.out);
				JOptionPane.showMessageDialog (null, "An error occurred when reading the curriculum description ("+CTATLink.htdocs+"curriculum.xml):\n"+e.getMessage()); 
				curriculum = null; 
			}
		
			if (curriculum!=null)
			{
				debug ("Curriculum check: " + curriculum.toString());

				curriculum.loadAllProblemSets ();
			}
		}
		else
			debug ("Info: curriculum.xml does not exist, attemting to load directory.txt instead ...");

		if (curriculum==null)
		{
			debug ("No curriculum object, attempting to load from directory.txt ...");
				
			CTATLink.fDirectoryTXT=new CTATDirectoryTXT ();
			
			if (CTATLink.fDirectoryTXT.isLoaded ()==false)
				CTATLink.fDirectoryTXT.loadDirectoryTXT (CTATLink.htdocs+"FlashTutors/directory.txt");				
		}
		else
			debug ("We have a curriculum, no need to load directory.txt");    					
		
		return (true);
	}
    /**
     * 
     */
    protected Boolean checkReadyToFinish ()
    {
    	debug ("checkReadyToFinish ()");
    	
    	
    	return (true);
    }
    /**
     * Remove any temp files, etc
     */
    protected Boolean cleanup ()
    {
    	debug ("cleanup ()");
    	
    	for (int i=0;i<tempFiles.size();i++)
    	{
    		File tempFile=tempFiles.get(i);
    		
    		debug ("Removing : " + tempFile.getName());
    		
    		if (tempFile.delete()==false)
    			debug ("Unable to remove temp file");
    	}
    	
    	return (false);
    }     
    /**
     * 
     * @param args
     */
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		CTATMoodleMaker t = new CTATMoodleMaker();
	}
}
