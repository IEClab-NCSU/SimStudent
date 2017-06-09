/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATContextData.js $
 $Revision: 23157 $

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

};

CTATContextData.prototype = Object.create(CTATBase.prototype);
CTATContextData.prototype.constructor = CTATContextData;
