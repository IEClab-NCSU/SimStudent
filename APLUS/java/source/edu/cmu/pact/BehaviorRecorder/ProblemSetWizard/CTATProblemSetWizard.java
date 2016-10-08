/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 Revision 1.1  2011/07/05 19:06:10  vvelsen
 Added a shell for the problem set wizard.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.ProblemSetWizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;

import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATFileManager;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATJPanel;

/**
 * 
 */
public class CTATProblemSetWizard extends CTATJPanel
{		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CTATFileManager fManager=null;	
	
	JTabbedPane tabbedPane=null;
	
	/**
	 * 
	 */		
	public CTATProblemSetWizard () 
    {		
    	//setClassName ("CTATStartStateEditor");
    	debug ("CTATStartStateEditor ()"); 
    	
		fManager=new CTATFileManager ();
    	
    	/*
		link=new CTATLink ();
		*/

        BorderLayout frameBox=new BorderLayout();
        this.setLayout (frameBox);
		        
	    //>-----------------------------------------------        
        
	    tabbedPane=new JTabbedPane();
	    tabbedPane.setFont(new Font("Dialog",1,10));	    	
	    
        this.add(tabbedPane,BorderLayout.CENTER);		
        
        CTATProblemSetPanel experimenter=new CTATProblemSetPanel ();
        experimenter.setLayout (new BoxLayout (experimenter,BoxLayout.X_AXIS));
        experimenter.setMinimumSize(new Dimension (20,20));
        experimenter.setMaximumSize(new Dimension (5000,2000));        
        experimenter.setBorder(BorderFactory.createMatteBorder(3,3,3,3,new Color (180,180,180)));
        experimenter.setBackground (new Color (180,180,180));
        experimenter.setFont(new Font("Dialog", 1, 10));
                                                                        
        tabbedPane.addTab ("Configuration",null,experimenter,"tbd");
        
        CTATDeploymentPanel deployer=new CTATDeploymentPanel ();
        deployer.setLayout (new BoxLayout (deployer,BoxLayout.X_AXIS));
        deployer.setMinimumSize(new Dimension (20,20));
        deployer.setMaximumSize(new Dimension (5000,2000));        
        deployer.setBorder(BorderFactory.createMatteBorder(3,3,3,3,new Color (180,180,180)));
        deployer.setBackground (new Color (180,180,180));
        deployer.setFont(new Font("Dialog", 1, 10));
                                                                        
        tabbedPane.addTab ("Configuration",null,deployer,"tbd");        
    }	
}
