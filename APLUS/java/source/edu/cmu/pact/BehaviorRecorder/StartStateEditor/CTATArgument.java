/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.1  2011/05/26 16:12:06  vvelsen
 Added first version of the start state editor. There's a test rig under the test directory.

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

public class CTATArgument extends CTATBase
{    		    
	private String value="";
	private String type="String";
	private String format="text";
	
	/**
	 *
	 */
    public CTATArgument () 
    {
		setClassName ("CTATArgument");
		debug ("CTATArgument ()");	
		setName ("Undefined");
    }
	/**
	 * 
	 */	
	public void setValue(String aValue) 
	{
		this.value=aValue;
	}
	/**
	 * 
	 */	
	public String getValue() 
	{
		return value;
	}        
	/**
	 * 
	 */	
	public void setType(String aType) 
	{
		this.type=aType;
	}
	/**
	 * 
	 */	
	public String getType() 
	{
		return type;
	}
	/**
	 * 
	 */	
	public void setFormat(String aFormat) 
	{
		this.format=aFormat;
	}
	/**
	 * 
	 */	
	public String getFormat() 
	{
		return format;
	}	
}
