/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-06-22 09:51:52 -0400 (Wed, 22 Jun 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

public class CTATSAITableEntry extends Object
{	
	private CTATSAI SAI=null;
	private CTATComponent component=null;
	private String value="";
	
	/**
	 *
	 */
    public CTATSAITableEntry (String aString) 
    {
    	value=aString;
    }
	/**
	 *
	 */    
    public String toString ()
    {
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
	public void setSAI(CTATSAI sAI) 
	{
		SAI = sAI;
	}
	/**
	 *
	 */
	public CTATSAI getSAI() 
	{
		return SAI;
	}
	/**
	 *
	 */
	public void setComponent(CTATComponent component) 
	{
		this.component = component;
	}
	/**
	 *
	 */
	public CTATComponent getComponent() 
	{
		return component;
	}
}
