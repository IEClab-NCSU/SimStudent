/** For graphical representation. */

goog.provide('CTATExampleTracerNodeVisualData');
goog.require('CTATBase');

//goog.require('CTATExampleTracerNode');//

/**
*
*/
CTATExampleTracerNodeVisualData = function ()
{
	CTATBase.call(this, "ExampleTracerNodeVisualData","visualdata");

	var id="****";
	var label="undefined";
	var x=-1; // negative to indicate it's undefined
	var y=-1; // negative to indicate it's undefined

	var vizReference=null;

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

	this.setX=function setX (anX)
	{
		x=anX;
	};

	this.getX=function getX ()
	{
		return (x);
	};

	this.setY=function setY (aY)
	{
		y=aY;
	};

	this.getY=function getY ()
	{
		return (y);
	};

	this.setVizReference=function setVizReference (aRef)
	{
		vizReference=aRef;
	};

	this.getVizReference=function getVizReference ()
	{
		return (vizReference);
	};
};

CTATExampleTracerNodeVisualData.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerNodeVisualData.prototype.constructor = CTATExampleTracerNodeVisualData;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerNodeVisualData;
}