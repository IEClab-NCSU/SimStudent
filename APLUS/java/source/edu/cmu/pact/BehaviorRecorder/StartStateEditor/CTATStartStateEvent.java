/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-23 10:36:09 -0400 (Tue, 23 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.1  2011/06/13 12:47:56  vvelsen
 Added missing start state event file.

 
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;

/**
 * 
 */
public class CTATStartStateEvent extends ProblemModelEvent
{    
	private String state="undefined";
	private Object target=null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     	
    /**
     *
     * @param source:        The object from which this event originates.
     */
    public CTATStartStateEvent (Object source, String aState,Object aTarget) 
    {
    	super(source, "", null, null);
    	setState (aState);
    	setTarget(aTarget);
    }
	/**
	 * 
	 */    
	public void setState(String state) 
	{
		this.state = state;
	}
	/**
	 * 
	 */	
	public String getState() 
	{
		return state;
	}
	/**
	 * 
	 */	
	public Object getTarget() 
	{
		return target;
	}
	/**
	 * 
	 */	
	public void setTarget(Object target) 
	{
		this.target = target;
	}     
}
