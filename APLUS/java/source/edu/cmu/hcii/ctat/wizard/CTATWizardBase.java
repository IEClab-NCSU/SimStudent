package edu.cmu.hcii.ctat.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import edu.cmu.hcii.ctat.CTATBase;

/** 
 * @author vvelsen
 *
 */
public class CTATWizardBase extends CTATBase implements ActionListener
{
	// Settings

	private int stepListWidth=125;

	// Panels, Frames and Controls ...

	protected JFrame frame; //will hold the mainPane, and any message dialogs
	
	private JLabel titleLabel=null;
	
	private JButton previousButton=null;
	private JButton nextButton=null;
	
	private JButton quitButton=null;
	private JButton cancelButton=null;
	private JButton restoreButton=null;
	
	private Box rightBox=null;
	
	private JPanel substitute=null;
	
	// Data models and tracking variables
	
	private JList pageList=null;
	private ArrayList<CTATWizardPanelDescription> pages=new ArrayList<CTATWizardPanelDescription> ();
	
	private DefaultListModel listModel = new DefaultListModel();
	
	private int pageIndex=0;
	
	public static JProgressBar progress=null;
	
	/**
	 *
	 */
    public CTATWizardBase () 
    {
    	setClassName ("CTATWizardBase");
    	debug ("CTATWizardBase ()");
    	
	    JPanel mainPane = new JPanel();
    	mainPane.setLayout(new BoxLayout (mainPane,BoxLayout.Y_AXIS));
    	mainPane.setBorder(new EmptyBorder(5,5,5,5));
    	
		Box centerBox = new Box (BoxLayout.X_AXIS);
		centerBox.setBorder(new EmptyBorder(5,5,5,5));
		
		mainPane.add(centerBox);
		
		pageList=new JList ();
		pageList.setBorder(new EmptyBorder(5,5,5,5));
		pageList.setCellRenderer(new CTATWizardPageListRenderer());
		pageList.setEnabled(false);
		pageList.setMinimumSize (new Dimension (stepListWidth,50));
		pageList.setMaximumSize (new Dimension (stepListWidth,700));
		
		JScrollPane listScroller = new JScrollPane(pageList);
		
		listScroller.setMinimumSize (new Dimension (stepListWidth,50));
		listScroller.setPreferredSize (new Dimension (stepListWidth,300));
		listScroller.setMaximumSize (new Dimension (stepListWidth,700));
		listScroller.setBorder(BorderFactory.createLineBorder(new Color (100,100,100)));
		    	
    	centerBox.add(listScroller);
		
		rightBox = new Box (BoxLayout.Y_AXIS);
		rightBox.setBorder(new EmptyBorder(5,5,5,5));
	            
        titleLabel=new JLabel ("CTAT SCORM Package Generator");
        titleLabel.setHorizontalAlignment (SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", 1, 12));
        //titleLabel.setBorder(BorderFactory.createLineBorder(new Color (0,0,0)));
        titleLabel.setMinimumSize(new Dimension (50,22));
        //titleLabel.setMaximumSize(new Dimension (500,22));
        
        rightBox.add(titleLabel);
        rightBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        
        centerBox.add(rightBox);
        
        substitute=createWizardJPanel ();
           			
		rightBox.add(substitute);
		rightBox.add(Box.createVerticalGlue()); 
		
		mainPane.add (centerBox);
		
		Box buttonBox = new Box (BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(5,5,5,0));
		
		restoreButton = new JButton("Restore default settings");
				
		buttonBox.add(restoreButton);
		
		buttonBox.add(Box.createHorizontalGlue());
		
		previousButton = new JButton("< Prev");
		previousButton.addActionListener(this);
		previousButton.setEnabled(false);
		
		buttonBox.add(previousButton);
		
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		
		nextButton = new JButton("Next >");
		nextButton.addActionListener(this);
		
		buttonBox.add(nextButton);		
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));

		quitButton = new JButton("Finish");
		quitButton.setEnabled(false);
		quitButton.addActionListener(this);
		
		buttonBox.add(quitButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		buttonBox.add(cancelButton);
		
		mainPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		mainPane.add(buttonBox);
		
		frame = new JFrame("Moodle Package Generator");
		frame.setSize(582,347);
		frame.setContentPane(mainPane);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null); //umm should this be relative to something else??
		//frame.pack();
		frame.setResizable(false);
    }
    /**
     * 
     */
    public void init ()
    {
    	debug ("init ()");
    	
    	// Implement in child class
    }
    /**
     * 
     */
    public void show ()
    {
		frame.setVisible(true);
    }
    /**
     * 
     */    
	public int getStepListWidth() 
	{
		return stepListWidth;
	}
    /**
     * 
     */	
	public void setStepListWidth(int stepListWidth) 
	{
		this.stepListWidth = stepListWidth;
	}        
    /**
     * 
     */
    protected void populateWizard ()
    {
    	debug ("populateWizard ()");

    	// Implement in child class
    }
    /**
     * 
     */
    protected void addPageLabel (String aLabel)
    {
    	debug ("addPageLabel ()");
    	    	
    	listModel.addElement (aLabel);
    	
    	pageList.setModel(listModel);
    }
    /**
     * 
     */
    protected CTATWizardPanelDescription addPage (String aLabel,JPanel aPanel)
    {    	    	
    	debug ("addPage ()");
    	
    	CTATWizardPanelDescription newPage=new CTATWizardPanelDescription ();
    	newPage.setPanelLabel(aLabel);
    	newPage.setPanelContent(aPanel);
    	
    	pages.add(newPage);
    	
    	addPageLabel (aLabel);
    	
    	Component test=rightBox.getComponent(2);
    	
    	debug ("Test: " + test);
    	
    	if (test==substitute)
    	{
    		debug ("We have a start page, substituting ...");
    		rightBox.remove(substitute);
    		rightBox.add(aPanel,2);
    	}
    	else
    		debug ("Info: component at index 2 is not our substitute page");
    	
    	updateState ();
    	
    	return (newPage);
    }
    /**
     * 
     */
    private void updateState ()
    {
    	debug ("updateState ()");
    	
    	Component test=rightBox.getComponent(2);
    	
    	if (test!=null)
    	{
    		for (int i=0;i<pages.size();i++)
    		{
    			CTATWizardPanelDescription testPanel=pages.get(i);
    			
    			if (testPanel.getPanelContent()==test)
    			{
    				titleLabel.setText(testPanel.getPanelLabel()+" - Page ( "+(i+1)+" of " + pages.size() + ")");
    				
    				return;
    			}
    		}
    	}
    	else
    		titleLabel.setText("No wizard pages defined");
    	
    	debug ("updateState () Done");
    }
    /**
     * 
     */
    protected void generateStartPage (JPanel aContainer)
    {
    	debug ("generateStartPage ()");
    	
    	// Implement in child class
    }
    /**
     * 
     */
    protected Boolean checkReadyToFinish ()
    {
    	debug ("checkReadyToFinish ()");
    	
    	// Implement in child class
    	
    	return (false);
    }
    /**
     * 
     */
    protected Boolean processFinish ()
    {
    	debug ("processFinish ()");
    	
    	// Implement in child class
    	
    	return (false);
    }
    /**
     * Remove any temp files, etc
     */
    protected Boolean cleanup ()
    {
    	debug ("cleanup ()");
    	
    	// Implement in child class
    	
    	return (false);
    }    
    /**
     * 
     */
    protected JPanel createWizardJPanel ()
    {
    	debug ("createWizardJPanel ()");
    	
		JPanel wizardPane=new JPanel ();
		wizardPane.setLayout (new BoxLayout (wizardPane,BoxLayout.Y_AXIS));
		wizardPane.setBorder(new EmptyBorder(5,5,5,5));
		wizardPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		return (wizardPane);
    }
	/**
	 * 
	 */
	protected void alert (String aMessage)
	{
		JOptionPane.showMessageDialog (null,aMessage);		
	}
	/**
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		debug ("actionPerformed ()");
		
		Object comp=arg0.getSource();
		
		if (comp==cancelButton)
		{
			frame.setVisible(false);
			frame.dispose();
		}
		
		if (comp==restoreButton)
		{
			
		}
		
		if (comp==quitButton)
		{
			if (processFinish ()==true)
			{
				cleanup ();
				
				frame.setVisible(false);
				frame.dispose();
			}	
		}
		
		if (comp==previousButton)
		{
			pageIndex--;
			
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);			
			
			if (pageIndex<=0)
			{
				pageIndex=0;
				
				previousButton.setEnabled(false);
			}
									
			updatePageContent ();
			
			updateState ();			
		}
		
		if (comp==nextButton)
		{
			pageIndex++;
			
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);
			
			if (pageIndex>(pages.size()-1))
			{
				pageIndex=(pages.size()-1);
			}
			
			if (pageIndex==(pages.size ()-1))
			{
				nextButton.setEnabled(false);
								
				if (checkReadyToFinish ()==true)
				{
					quitButton.setEnabled(true);
					
					/*
			    	Component test=rightBox.getComponent(2);
			    	
			    	if (test!=null)
			    	{
			    		CTATWizardFinishPage finishPage=new CTATWizardFinishPage ();
			    		
			    		rightBox.remove(test);
			    		rightBox.add(finishPage,2);			    	
			    	}			    	
			    	
			    	rightBox.repaint();
			    	*/					
				}
			}
			else
				quitButton.setEnabled(false);
			
			updatePageContent ();
			
			updateState ();
		}
	}	
	/**
	 * 
	 */
	private void updatePageContent ()
	{
		debug ("updatePageContent ()");
		
    	Component test=rightBox.getComponent(2);
    	
    	if (test!=null)
    	{
    		CTATWizardPanelDescription targetPage=pages.get(pageIndex);
    		if (targetPage!=null)
    		{	    		
    			rightBox.remove(test);
    			rightBox.add(targetPage.getPanelContent(),2);
    		}	
    	}			
    	
    	rightBox.repaint();
	}
	/**
	 * 
	 */
	public JPanel getCurrentPanel ()
	{
		return (pages.get(pageIndex).getPanelContent());
	}
    /**
     * 
     * @param args
     */
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		CTATWizardBase t = new CTATWizardBase();
	}
}
