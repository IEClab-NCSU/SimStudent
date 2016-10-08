/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/env/CTATEnvironment.java,v 1.1 2012/05/07 19:08:15 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATEnvironment.java,v $
 Revision 1.1  2012/05/07 19:08:15  vvelsen
 Started some refactoring of our Java tree (with permission) First we'll do a bunch of small utilities that almost nobody uses, which seems to be the majority of our code

 $RCSfile: CTATEnvironment.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/env/CTATEnvironment.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
*/

package edu.cmu.hcii.ctat.env;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATEnvironment extends CTATBase 
{
	/**
	*
	*/		
	public static boolean isLocalWindows() 
	{		
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}
	/**
	* java.vendor.url=http://www.android.com/
	* java.vm.vendor.url=http://www.android.com/
	* java.home=/system
	* java.vm.name=Dalvik
	* java.runtime.name=Android Runtime
	* java.specification.vendor=The Android Project
	* java.vm.specification.vendor=The Android Project
	* java.vm.vendor=The Android Project
	* android.vm.dexfile=true
	* java.specification.name=Dalvik Core Library
	* java.vendor=The Android Project
	* java.vm.specification.name=Dalvik Virtual Machine Specification
	* 
	*/	
	public static boolean isLocalAndroid() 
	{
		String os = System.getProperty("java.vendor").toLowerCase();
		// Android
		return (os.indexOf("android") >= 0);
	}	
	/**
	*
	*/	
	public static boolean isLocalMac() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}
	/**
	*
	*/	 
	public static boolean isLocalUnix() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
	/**
	*
	*/	 
	public static boolean isLocalSolaris() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}			
}
