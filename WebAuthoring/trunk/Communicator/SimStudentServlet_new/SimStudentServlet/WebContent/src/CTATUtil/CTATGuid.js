/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATUtil/CTATGuid.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */


goog.provide('CTATGuid');

/**
 *
 */
CTATGuid = function()
{
	/**
	 *
	 * @returns
	 */
	this.s4=function s4()
	{
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	};

	/**
	 *
	 * @returns {String}
	 */
	this.guid=function guid()
	{
		return this.s4() + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + this.s4() + this.s4();
	};
}


if(typeof module !== 'undefined')
{
    module.exports = CTATGuid;
}
