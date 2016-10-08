/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPHandlerInterface.java,v 1.4 2012/09/28 13:37:56 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATHTTPHandlerInterface.java,v $
 Revision 1.4  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.3  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.2  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.1  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 $RCSfile: CTATHTTPHandlerInterface.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPHandlerInterface.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;


public interface CTATHTTPHandlerInterface
{		
	/**
	 * Call this method with each new client request.
	 */	
	public boolean handle (CTATHTTPExchange arg0);

}
