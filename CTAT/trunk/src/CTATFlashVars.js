/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATFlashVars.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATFlashVars');

goog.require('CTATConfiguration');

/**
 * The name of this class is obsolete because it refers to technology we're deprecating.
 * Instead please use the CTATConfiguration class.
 */
CTATFlashVars = function()
{
	CTATConfiguration.call (this, "CTATFlashVars", "flashvars");

	var pointer=this;

};

CTATFlashVars.prototype = Object.create(CTATConfiguration.prototype);
CTATFlashVars.prototype.constructor = CTATFlashVars;
