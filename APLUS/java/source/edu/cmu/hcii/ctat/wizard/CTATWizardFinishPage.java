package edu.cmu.hcii.ctat.wizard;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import edu.cmu.hcii.ctat.CTATBase;

/** 
 * @author vvelsen
 *
 */
public class CTATWizardFinishPage extends JPanel
{
	private static final long serialVersionUID = -4193982654107790562L;
	
	JProgressBar progress=null;
	
	/**
	 *
	 */
    public CTATWizardFinishPage () 
    {
    	debug ("CTATWizardFinishPage ()");
    	
		this.setLayout (new BoxLayout (this,BoxLayout.Y_AXIS));
		this.setBorder(new EmptyBorder(5,5,5,5));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);    	
    	
    	JLabel explanationMessage=new JLabel ();
    	explanationMessage.setText ("<html>Please click Finish to complete the wizard.<br><br><br></html>");
    	
    	progress=new JProgressBar ();
    	
    	CTATWizardBase.progress=this.progress;
    	
    	this.add(explanationMessage);
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		this.add(progress);
    }
    /**
     * 
     * @return
     */
    public JProgressBar getProgressBar ()
    {
    	return (progress);
    }
    /**
     * 
     */
    private void debug (String aMessage)
    {
    	CTATBase.debug("CTATWizardFinishPage",aMessage);
    }
}
