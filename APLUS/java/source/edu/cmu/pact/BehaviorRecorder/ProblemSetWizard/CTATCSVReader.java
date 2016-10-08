/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/07/07 14:47:03  keiser
 Check in for Martin to combat cvs failure to update issue.

 Revision 1.1  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 $RCSfile$ 
 $Revision: 13014 $ 
 $Source$ 
 $State$ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.ProblemSetWizard;

import java.util.ArrayList;

import edu.cmu.hcii.ctat.CTATBase;

public class CTATCSVReader extends CTATBase 
{
	private ArrayList <ArrayList <String>>data=null;
	public String mode="TAB"; // TAB,COMMA,DASH
	
	private int rows=0;
	private int columns=0;
	
	/**
	 *
	 */ 
	public CTATCSVReader () 
	{		
		setClassName ("CTATCSVReader");
		debug ("CTATCSVReader ()");
		
		data=new ArrayList<ArrayList<String>> ();
	}
	/**
	 *
	 */	
	public void setRows(int rows) 
	{
		this.rows = rows;
	}
	/**
	 *
	 */	
	public int getRows() 
	{
		return rows;
	}
	/**
	 *
	 */	
	public void setColumns(int columns) 
	{
		this.columns = columns;
	}
	/**
	 *
	 */	
	public int getColumns() 
	{
		return columns;
	}	
	/**
	 *
	 */
	public ArrayList<ArrayList<String>> getData ()
	{
		return (data);
	}
	/**
	 *
	 */
	public ArrayList<String> addInstanceStringTab (String entries[],int rowCounter)
	{
		//debug ("addInstanceStringTab ()");
				
		ArrayList<String> row=new ArrayList <String> ();
		
		for (int i=0;i<entries.length;i++)
		{
			//debug ("Adding instance: " + entries [i]);
			row.add (entries [i]);
		}
		
		return row;
	}		
	/**
	 *
	 */
	public boolean processInputTab (String a_file)
	{
		//debug ("processInput ()");
												
		String split[]=a_file.toString ().split("\\n");
				
		int index=0;
		
		for (int i=0;i<split.length;i++)
		{
			//debug ("Looking at line: " + i + ": "+ split [i]);
					
			String entries[]=split [i].split("\\t");
		 
			setColumns (entries.length);
			
			ArrayList <String> row=addInstanceStringTab (entries,index);
			data.add(row);
					 
			index++;
		}
		
		setRows (index);
		
		return (true);
	}		
}
