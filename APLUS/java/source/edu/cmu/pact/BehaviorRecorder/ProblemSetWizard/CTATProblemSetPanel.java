/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.5  2012/03/09 19:48:29  sewall
 Cleanups on Skill Matrix. Fixes for MergeMassProduction & Preference file choosers.

 Revision 1.4  2012/02/29 04:59:43  sewall
 Revise file dialog.

 Revision 1.3  2011/09/01 18:04:52  vvelsen
 Upgraded the problem set generation wizard. Much easier to use, more options and more stable.

 Revision 1.2  2011/08/31 16:31:42  sewall
 Add undo for text areas and text fields.

 Revision 1.1  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.ProblemSetWizard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DirectoryFilter;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATFileManager;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATJPanel;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;

/**
 * 
 */
public class CTATProblemSetPanel extends CTATJPanel implements ActionListener
{		
	private static final long serialVersionUID = 1L;

	private CTATFileManager fManager=null;
	
	private JButton inputPathButton=null;
	private JTextField inputPath=null;
	
	private JButton outputPathButton=null;
	private JTextField outputPath=null;
	
	private JButton buildButton=null;
	
	private JTextArea console=null;
		
	private ArrayList <CTATProblemSet> model=null;
	
	private JCheckBox compDirTXT=null;
	private JCheckBox pSetXML=null;
	
	/**
	 * 
	 */		
	public CTATProblemSetPanel () 
    {		
    	//setClassName ("CTATProblemSetPanel");
    	debug ("CTATProblemSetPanel ()"); 
    	
		fManager=new CTATFileManager ();
            		
		this.setLayout (new BoxLayout (this,BoxLayout.X_AXIS));		
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		
        Box panelBox=new Box (BoxLayout.Y_AXIS);
        this.add (panelBox);
        
        //>-------------------------------------------------

        JLabel inLabel=new JLabel ();
        inLabel.setText("Choose a tab delimited text file");
        inLabel.setHorizontalAlignment(JLabel.LEFT);
        panelBox.add(inLabel);
        
        Box inputBox=new Box (BoxLayout.X_AXIS);
        panelBox.add (inputBox);
        
        inputPath=new JTextField ();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputPath); }
        inputPath.setFont(new Font("Dialog", 1, 10));
        inputPath.setMinimumSize(new Dimension (20,20));
        inputPath.setMaximumSize(new Dimension (5000,20));
        
        inputBox.add (inputPath);
        
        inputPathButton=new JButton ();
        inputPathButton.setText("...");
        inputPathButton.setFont(new Font("Dialog", 1, 10));
        inputPathButton.setMinimumSize(new Dimension (20,20));
        inputPathButton.setMaximumSize(new Dimension (75,20));
        //inputPathButton.setHorizontalAlignment(SwingConstants.LEFT);
        inputPathButton.addActionListener (this);
        
        inputBox.add(inputPathButton);
        
        //>-------------------------------------------------
        
        JLabel outLabel=new JLabel ();
        outLabel.setText("Choose an output directory");
        outLabel.setHorizontalAlignment(JLabel.LEFT);
        panelBox.add(outLabel);
        
        Box outputBox=new Box (BoxLayout.X_AXIS);
        panelBox.add (outputBox);
        
        outputPath=new JTextField ();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(outputPath); }
        outputPath.setFont(new Font("Dialog", 1, 10));
        outputPath.setMinimumSize(new Dimension (20,20));
        outputPath.setMaximumSize(new Dimension (5000,20));
        
        outputBox.add (outputPath);
        
        outputPathButton=new JButton ();
        outputPathButton.setText("...");
        outputPathButton.setFont(new Font("Dialog", 1, 10));
        outputPathButton.setMinimumSize(new Dimension (20,20));
        outputPathButton.setMaximumSize(new Dimension (75,20));
        //inputPathButton.setHorizontalAlignment(SwingConstants.LEFT);
        outputPathButton.addActionListener (this);
        
        outputBox.add (outputPathButton);
                
        //>-------------------------------------------------
                       
        console=new JTextArea ();
        console.setFont(new Font("Courier", 1, 11));
        console.setMinimumSize(new Dimension (20,20));
        console.setMaximumSize(new Dimension (5000,5000));
        console.setEditable(false);
        console.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setMinimumSize(new Dimension (20,20));
        scrollPane.setMaximumSize(new Dimension (5000,5000));        
        
        panelBox.add (scrollPane);
        
        //>-------------------------------------------------
        
        compDirTXT=new JCheckBox ();
        compDirTXT.setText("Compile and write directory.txt");
        compDirTXT.setFont(new Font("Dialog", 1, 10));
        compDirTXT.setMinimumSize(new Dimension (20,20));
        compDirTXT.setMaximumSize(new Dimension (5000,20));
        //inputPathButton.setHorizontalAlignment(SwingConstants.LEFT);
        //buildButton.addActionListener (this);

        panelBox.add (addInHorizontalLayout (compDirTXT,5000,20));
        
        //>-------------------------------------------------
        
        pSetXML=new JCheckBox ();
        pSetXML.setText("Compile and write problem_set.xml");
        pSetXML.setFont(new Font("Dialog", 1, 10));
        pSetXML.setMinimumSize(new Dimension (20,20));
        pSetXML.setMaximumSize(new Dimension (5000,20));
        //inputPathButton.setHorizontalAlignment(SwingConstants.LEFT);
        //buildButton.addActionListener (this);
        pSetXML.setSelected(true);

        panelBox.add (addInHorizontalLayout (pSetXML,5000,20));        
        
        //>-------------------------------------------------
        
        buildButton=new JButton ();
        buildButton.setText("Build");
        buildButton.setFont(new Font("Dialog", 1, 10));
        buildButton.setMinimumSize(new Dimension (20,20));
        buildButton.setMaximumSize(new Dimension (5000,20));
        //inputPathButton.setHorizontalAlignment(SwingConstants.LEFT);
        buildButton.addActionListener (this);

        panelBox.add (addInHorizontalLayout (buildButton,5000,20));        
        
        //>-------------------------------------------------        
    }
	/**
	 * 
	 */
	private String stripFileFromPath (String aFile)
	{
		return (new File (aFile).getParent());
	}
	/**
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		debug ("actionPerformed ()");
		   
		//>------------------------------------------------------
	  	
		if (e.getSource ()==buildButton)
		{	
			build ();
		}
		
		//>------------------------------------------------------
	  	
		if (e.getSource ()==inputPathButton)
		{
			File file = DialogUtilities.chooseFile(null, null, null,
					"Choose an input file", "Open", this);
			if (file != null)
			{
	         	debug ("Input: " + file.getAbsolutePath());
	               	           	
	         	inputPath.setText(file.getAbsolutePath());
			}		
		}
		
		//>------------------------------------------------------
	  	
		if (e.getSource ()==outputPathButton)
		{	
			File file = DialogUtilities.chooseFile(null, null,
					new DirectoryFilter(), "Choose an output directory", "Open", this);
			if (file != null)
			{	        	   
	         	debug ("Input: " + file.getAbsolutePath());
	               	           	
	         	outputPath.setText(file.getAbsolutePath());
			}
		}		
		
		//>------------------------------------------------------						
	}
	/**
	 * 
	 */
	public void consoleWrite (String aLine) 
	{
		console.append(aLine+"\n");
	}
	/**
	 * 
	 */
	public void consoleReset () 
	{
		console.setText("");
	}	
	/**
	 * 
	 */
	public CTATProblemSet addProblemSet (String aSet) 
	{				
		for (int i=0;i<model.size();i++)
		{
			CTATProblemSet compare=model.get(i);
			if (compare.getName().equals(aSet)==true)
			{
				//consoleWrite ("Problem set " + aSet + " already exists, augmenting...");
				return (compare);
			}	
		}
		
		consoleWrite ("Adding new problem set: " + aSet);
		
		CTATProblemSet newSet=new CTATProblemSet ();
		newSet.setName(aSet);
		model.add(newSet);
		
		return (newSet);
	}
	/**
	 * 
	 */
	public void build () 
	{
		debug ("build ()");
		
		consoleReset ();
		
		consoleWrite ("Starting build ...");
		
		File inp =new File (inputPath.getText());
		if (inp.exists()==false)
		{
			consoleWrite ("Error: input file ["+inputPath.getText()+"] does not exist or isn't specified");
			return;
		}
		
		File outp=new File (outputPath.getText());
		if (outp.exists()==false)
		{
			consoleWrite ("Error: output path ["+outputPath.getText()+"] does not exist or isn't specified");
			return;
		}		
		
		String inputStream=fManager.loadContents (inputPath.getText());
		
		model=new ArrayList<CTATProblemSet> ();
		
		CTATCSVReader reader=new CTATCSVReader ();
		reader.processInputTab (inputStream);
		
		ArrayList<ArrayList<String>> data=reader.getData ();
		
		StringBuffer dirTXT=new StringBuffer ();
		
		for (int i=0;i<data.size();i++)
		{
			ArrayList <String> row=data.get(i);
			
			if (i==0)
			{
				consoleWrite ("Skipping header ...");
			}
			else
			{
				//consoleWrite ("Processing row: " + i);
				
				CTATProblemSet testSet=addProblemSet(row.get(0));
				testSet.setCondition(row.get(1));
				if (row.get (5)!=null)
					testSet.setDescription (row.get (5));
				ArrayList<CTATProblem> problems=testSet.getProblems();
				CTATProblem problem=new CTATProblem ();
				problem.setCondition(row.get (1));
				problem.setSwf(row.get (2));
				problem.setBrd(row.get (3));
				problem.setProblemType(row.get (4));
				
				problems.add(problem);
			}	
		}
		
		consoleWrite ("Generating output ...");
		
		for (int j=0;j<model.size();j++)
		{
			Boolean ready=false;
			StringBuffer pSetBuffer=new StringBuffer ();
						
			CTATProblemSet converter=model.get(j);
			
			pSetBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ProblemSet max_repeat=\"1\" initial_sequence=\"0\" selection_algorithm=\"sequential\" description=\""+converter.getName()+"-"+converter.getDescription()+"\" name=\""+converter.getName()+"\" max_count=\"1000\"><Problems>");
						
			consoleWrite ("Converting: " + converter.getName() + " ...");
			
			if (j>0)
				dirTXT.append ("\n");
			
			dirTXT.append(converter.getCondition()+"|"+converter.getName()+"-"+converter.getCondition ()+"|"+converter.getDescription());
			
			StringBuffer pSetPath=new StringBuffer ();
			pSetPath.append(outputPath.getText()+"/"+converter.getName()+"-"+converter.getCondition ());
			
			StringBuffer swfPath=new StringBuffer ();
			swfPath.append(outputPath.getText()+"/"+converter.getName()+"-"+converter.getCondition ()+"/Flash/");
			
			StringBuffer brdPath=new StringBuffer ();
			brdPath.append(outputPath.getText()+"/"+converter.getName()+"-"+converter.getCondition ()+"/FinalBRDs/");			
			
			consoleWrite ("Creating problem set directory: " + pSetPath.toString());
			
			boolean success=(new File(pSetPath.toString())).mkdir();
			
			if (success==false) 
			{
				consoleWrite ("Error creating problem set directory");				 
			}
			else
			{
				consoleWrite ("Creating problem set directory: " + pSetPath.toString());
				
				success=(new File(swfPath.toString())).mkdir();
				
				if (success==false)
				{
					consoleWrite ("Error creating Flash directory: " + swfPath.toString());					
				}
				else
				{
					success=(new File(brdPath.toString())).mkdir();					
					
					if (success=false)
					{
						consoleWrite ("Error creating BRD directory: " + brdPath.toString());						
					}
					else
						ready=true;
				}
			}
			
			if (ready==true)
			{
				consoleWrite ("Copying files ...");
				
				ArrayList <CTATProblem> problems=converter.getProblems();
				
				for (int k=0;k<problems.size();k++)
				{
					CTATProblem problem=problems.get(k);
					
					String swfString=problem.getSwf();
					String brdString=problem.getBrd();
					
					pSetBuffer.append("<Problem tutor_flag=\"tutor\" problem_file=\""+problem.getBrd()+"\" description=\""+converter.getDescription()+"\" name=\""+converter.getName()+"-"+k+"-"+converter.getCondition ()+"\" student_interface=\""+problem.getSwf()+"\"><Skills></Skills></Problem>");
				    					
					//consoleWrite ("Copying "+swfPath.toString()+"/"+swfString + " from: " + stripFileFromPath (inputPath.getText())+"/Flash/"+swfString);
					
					CTATFileCopy.copyfile (swfPath.toString()+"/"+swfString,stripFileFromPath (inputPath.getText())+"/Flash/"+swfString);
					
					//consoleWrite ("Copying "+brdPath.toString()+"/"+brdString + " from: " + stripFileFromPath (inputPath.getText())+"/FinalBRDs/"+brdString);
					
					CTATFileCopy.copyfile (brdPath.toString()+"/"+brdString,stripFileFromPath (inputPath.getText())+"/FinalBRDs/"+brdString);
				}
			}
			
			pSetBuffer.append("</Problems><Categories></Categories><Skills></Skills><Assets></Assets></ProblemSet>");
			
			if (pSetXML.isSelected()==true)
				fManager.saveContents(pSetPath.toString()+"/problem_set.xml",pSetBuffer.toString());
		}
		
		if (compDirTXT.isSelected()==true)
			fManager.saveContents(outputPath.getText()+"/directory.txt",dirTXT.toString());
		
		consoleWrite ("Build complete");
	}
}
