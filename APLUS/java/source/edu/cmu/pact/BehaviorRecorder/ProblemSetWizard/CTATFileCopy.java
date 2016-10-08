/**
 ------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.1  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 $RCSfile$ 
 $Revision: 13734 $ 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.hcii.ctat.CTATBase;

/**
*
*/
public class CTATFileCopy extends CTATBase
{
	/**
	 *
	 */	
	public static Boolean copyfile(String dtFile, String srFile)
	{
		try
		{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
  
			//For Append the file.
			//OutputStream out = new FileOutputStream(f2,true);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
		  
			in.close();
			out.close();
			System.out.println("File copied.");
		}	  
		catch(FileNotFoundException ex)
		{
			debug ("CTATFileCopy",ex.getMessage() + " in the specified directory.");
			//System.exit(0);
			return (false);
		}
		catch(IOException e)
		{
			debug ("CTATFileCopy",e.getMessage());
			return (false);
		}
		
		return (true);
	}
	/**
	 *
	 */  
	public static void main(String[] args)
	{
		switch(args.length)
		{
	  		case 0:	System.out.println("File has not mentioned.");
	  				System.exit(0);
	  		case 1: System.out.println("Destination file has not mentioned.");
	  				System.exit(0);
	  		case 2: copyfile(args[0],args[1]);
	  				System.exit(0);
	  		default : System.out.println("Multiple files are not allow.");
	 				System.exit(0);
		}
	}
}
