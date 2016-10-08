/**
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 $Log: CTATVisualProgressTask.java,v $
 Revision 1.5  2012/08/30 15:25:33  sewall
 Fix-ups after Alvaro's 2012/08/17 merge.

 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**  
 *
 */    
public class CTATVisualProgressTask extends SwingWorker<Void, Void> 
{
	private JProgressBar pb=null;
    	
	public CTATVisualProgressTask (JPanel aContainer)
	{
		CTATBase.debug ("CTATVisualProgressTask","CTATVisualProgressTask ()");
    		
		pb=new JProgressBar(0,100);
		pb.setMinimumSize(new Dimension(320,20));
		pb.setPreferredSize(new Dimension(320,20));
		pb.setMaximumSize(new Dimension(5000,20));
		pb.setString("Working");
		pb.setStringPainted(true);
		pb.setValue(0);
    		
		aContainer.add (pb);    		
	}
	/*
	 * Main task. Executed in background thread.
     */
	@Override
	public Void doInBackground() 
	{
		CTATBase.debug ("CTATVisualProgressTask","doInBackground()");
        				
		return null;
	}
	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() 
	{
		CTATBase.debug ("CTATVisualProgressTask","done ()");
        	
		//setProgress (-1);
	}
	/**
	 * 
	 */
	public void setValue (int aValue)
	{
		if (pb!=null)
		{
			pb.setValue(aValue);
			setProgress (aValue);
		}
	}
	/**
	 * 
	 */
	public void repaint()
	{
		if(pb!=null)
		{
			pb.repaint();
		}
	}
}
