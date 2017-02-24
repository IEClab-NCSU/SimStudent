/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATStringUtil.js $
 $Revision: 21689 $

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
	}
	/**
	*
	*/
	this.replaceString=function replaceString (thisStr,searchStr,replaceStr)
	{
		return (thisStr.replace(searchStr,replaceStr));
	};
}


if(typeof module !== 'undefined')
    module.exports = CTATStringUtil;
