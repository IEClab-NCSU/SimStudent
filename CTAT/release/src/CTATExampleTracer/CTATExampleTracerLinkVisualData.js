/** For graphical representation. */

goog.provide('CTATExampleTracerLinkVisualData');
goog.require('CTATBase');

//goog.require('CTATExampleTracerLink');//

/**
*
*/
CTATExampleTracerLinkVisualData = function ()
{
	CTATBase.call(this, "CTATExampleTracerLinkVisualData","visualdata");

	var id="****";
	var label="undefined";
	var source="****";
	var destination="****";

	this.setID=function setID (anID)
	{
		id=anID;
	};

	this.getID=function getID ()
	{
		return (id);
	};

	this.setLabel=function setLabel (aLabel)
	{
		label=aLabel;
	};

	this.getLabel=function getLabel ()
	{
		return (label);
	};


	this.setSource=function setSource (aSource)
	{
		source=aSource;
	};

	this.getSource=function getSource ()
	{
		return (source);
	};


	this.setDestination=function setDestination (anID)
	{
		destination=anID;
	};

	this.getDestination=function getDestination ()
	{
		return (destination);
	};
};

CTATExampleTracerLinkVisualData.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerLinkVisualData.prototype.constructor = CTATExampleTracerLinkVisualData;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerLinkVisualData;
}