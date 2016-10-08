/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.1  2011/07/19 19:54:33  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import edu.cmu.hcii.ctat.CTATBase;

public class CTATSerializableTableEntry extends CTATBase
{	
	private CTATSerializable entry=null;
	private CTATSAI          sai=null;
	private CTATArgument     arg=null;
	
	private CTATComponent component=null;
	private String value="";
	
	/**
	 *
	 */
    public CTATSerializableTableEntry (String aString) 
    {
    	setClassName ("CTATSerializableTableEntry");
    	debug ("CTATSerializableTableEntry ()");
    	
    	value=aString;
    }
	/**
	 *
	 */    
    public String toString ()
    {
    	if (entry!=null)
    		return (entry.toString());
    	
    	return (value);
    }
	/**
	 *
	 */    
    public void fromString (String aString)
    {
    	value=aString;
    }
	/**
	 *
	 */
	public void setEntry(CTATSerializable anEntry) 
	{
		entry=anEntry;
	}
	/**
	 *
	 */
	public CTATSerializable getEntry() 
	{
		return entry;
	}
	/**
	 *
	 */
	public void setSAI(CTATSAI anSAI) 
	{
		sai=anSAI;
	}
	/**
	 *
	 */
	public CTATSAI getSAI() 
	{
		return sai;
	}	
	/**
	 *
	 */
	public void setArgument(CTATArgument anArg) 
	{
		arg=anArg;
	}
	/**
	 *
	 */
	public CTATArgument getArgument() 
	{
		return arg;
	}		
	/**
	 *
	 */
	public void setComponent(CTATComponent component) 
	{
		this.component=component;
	}
	/**
	 *
	 */
	public CTATComponent getComponent() 
	{
		return component;
	}
}
