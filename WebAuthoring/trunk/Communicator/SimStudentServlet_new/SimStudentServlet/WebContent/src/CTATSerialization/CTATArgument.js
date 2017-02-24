/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATSerialization/CTATArgument.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATArgument');
/**
 *
 */
CTATArgument = function()
{
	var value="Undefined";
	var type="String";
	var format="text";

	/**
	 *
	 */
	this.setValue=function setValue(aValue)
	{
		value=aValue;
	};
	/**
	 *
	 */
	this.getValue=function getValue()
	{
		return value;
	};
	/**
	 *
	 */
	this.setType=function setType(aType)
	{
		type=aType;
	};
	/**
	 *
	 */
	this.getType=function getType()
	{
		return type;
	};
	/**
	 *
	 */
	this.setFormat=function setFormat(aFormat)
	{
		format=aFormat;
	};
	/**
	 *
	 */
	this.getFormat=function getFormat()
	{
		return format;
	};
}

if(typeof module !== 'undefined')
{
	module.exports = CTATArgument;
}
