/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-06-28 15:07:11 -0500 (週二, 28 六月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATSerialization/CTATArgument.js $
 $Revision: 23782 $

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
	var name="";
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
	this.setName=function setName(aValue)
	{
		name=aValue;
	};
	/**
	 *
	 */
	this.getName=function getName()
	{
		return name;
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
};

/**
 * @return {CTATArgument} new object with same property values
 */
CTATArgument.prototype.clone = function()
{
	var result = new CTATArgument();
	result.setValue(this.getValue());
	result.setType(this.getType());
	result.setFormat(this.getFormat());
	return result;
};

if(typeof module !== 'undefined')
{
	module.exports = CTATArgument;
}
