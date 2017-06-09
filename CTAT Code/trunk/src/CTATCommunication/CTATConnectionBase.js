/**-----------------------------------------------------------------------------
 $Author$
 $Date$
 $HeadURL$
 $Revision$

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATConnectionBase');

goog.require('CTATBase');

/**
 *
 */
CTATConnectionBase = function(aClassName, aName)
{
	CTATBase.call(this, "CTATConnectionBase","ctatconnection");

	var id=-1;
	var url="";
	var socketType="http";

	/**
	*
	*/
	this.setURL=function setURL (aURL)
	{
		url=aURL;
	};
	/**
	*
	*/
	this.getURL=function getURL ()
	{
		return (url);
	};
	/**
	*
	*/
	this.setID=function setID (anID)
	{
		id=anID;
	};
	/**
	*
	*/
	this.getID=function getID ()
	{
		return (id);
	};
	/**
	*
	*/
	this.setSocketType=function setSocketType (aType)
	{
		socketType=aType;
	};
	/**
	*
	*/
	this.getSocketType=function getSocketType ()
	{
		return (socketType);
	};
};

CTATConnectionBase.prototype = Object.create(CTATBase.prototype);
CTATConnectionBase.prototype.constructor = CTATConnectionBase;
