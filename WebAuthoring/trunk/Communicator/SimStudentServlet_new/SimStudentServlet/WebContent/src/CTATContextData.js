/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATContextData.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATContextData');

goog.require('CTATBase');
/**
 *
 */
CTATContextData = function()
{
	CTATBase.call(this, "CTATContextData", "__undefined__");

	var pointer=this;

}

CTATContextData.prototype = Object.create(CTATBase.prototype);
CTATContextData.prototype.constructor = CTATContextData;
