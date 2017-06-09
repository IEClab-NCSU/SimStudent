/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATStringUtil.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATStringUtil');

/**
*
*/
CTATStringUtil = function()
{
	/**
	* Apparently AS3 expects a direct conversion between a string and a boolean to only
	* happen when the string is '0' or '1' for every other case we have to test manually.
	* So this method pretty much catches all varieties.
	*/
	this.String2Boolean=function String2Boolean (aString)
	{
		switch(aString)
		{
			case "1":
			case "true":
			case "yes":
			case "TRUE":
			case "YES":
			case "Yes":
						return true;
			case "0":
			case "false":
			case "no":
			case "FALSE":
			case "NO":
			case "No":
						return false;
		}

		return (true);
	};
	/**
	*
	*/
	this.replaceString=function replaceString (thisStr,searchStr,replaceStr)
	{
		return (thisStr.replace(searchStr,replaceStr));
	};
};


if(typeof module !== 'undefined')
    module.exports = CTATStringUtil;
