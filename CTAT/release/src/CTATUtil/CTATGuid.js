/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-06-28 15:07:11 -0500 (週二, 28 六月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATUtil/CTATGuid.js $
 $Revision: 23782 $

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
CTATGuid = {
	/**
	 *
	 * @returns
	 */
	s4: function s4()
	{
		return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
	},

	/**
	 *
	 * @returns {String}
	 */
	guid: function guid()
	{
		return this.s4() + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + this.s4() + this.s4();
	}
};

