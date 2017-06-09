/**------------------------------------------------------------------------------------
 $Author$
 $Date$
 $Header$
 $Name$
 $Locker$
 $Log$

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 All the methods in this file correspond to functions made available by
 tutorshop. When placing a tutor on TS please remove this file otherwise
 the code will call the wrong functions.
*/
goog.provide('TutorShopAPIProxy');

goog.require('CTATSandboxDriver');
/**
 *
 */
function javaScriptInfo (aString)
{
	var tutorCanvas=getSafeElementById("info");

	if (tutorCanvas!=null)
	{
		tutorCanvas.innerHTML=aString;
	}
}
