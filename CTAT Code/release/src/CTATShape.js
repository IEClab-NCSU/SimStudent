/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATShape.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 This is best used for dynamic objects. If you are simply painting something "static" (rounded rect for
 the Skill and Hint Windows for example), just use the CTATGraphicsTools code to paint.

 Use this with CTATCanvasComponent to create aggregates of shapes, or a "whole component" made
 of shapes. For instance, a CTATNumberLine would consist of many CTATShapes (many lines
 drawn), that will represent tick marks.

 The constructor takes six arguments:
 	1. anID    - ID of the div you want to add the canvas to.
 	2. aName   - The name of this shape.
 	3. startX  - The x coordinate of the canvas to be drawn on.
 	4. startY  - The y coordinate of the canvas to be drawn on.
 	5. aWidth  - The width of the canvas and the shape.
 	6. aHeight - The height of the canvas and the shape.

The startX and startY coordinates are absolutely positioned. Once set, you must then call
addPoints which will set an (x, y) coordinate local to the shape's canvas.

*/
goog.provide('CTATShape');

goog.require('CTATCSS');
goog.require('CTATGraphicsTools');
goog.require('CTATSandboxDriver');

CTATShape = function(anID, aName, startX, startY, aWidth, aHeight)
{
	var pointer=this;

	if((anID==undefined) || (anID==null))
	{
		pointer.ctatdebug("--- Error: Must pass a valid ID. ---");
		return null;
	}

	//Holds an array of x or y coordinates to support draw methods.
	var x=[];
	var y=[];

	var width=aWidth;
	var height=aHeight;
	var shapeName=aName;

	var canvasX=startX;
	var canvasY=startY;

	//If you want to pair up some data with a particular shape.
	//*note: this is used to hold the text for draw text
	var data=undefined;

	var lineColor="black";
	var fillColor="black";

	var textFont="Arial";
	var textSize=12;

	var eventHandlerList=[];

	//This is the width of the line to be drawn. Not applicable to fill methods.
	var shapeLineWidth=1;

	var radius=5;

	var canvasObject=document.createElement('canvas');
	canvasObject.id=shapeName;
	canvasObject.width=width;
	canvasObject.height=height;

	var canvasStyles=new CTATCSS();

	canvasStyles.addCSSAttribute("position", "absolute");
	canvasStyles.addCSSAttribute("left", canvasX+"px");
	canvasStyles.addCSSAttribute("top", canvasY+"px");

	//Lets us see what we draw on the canvas...
	canvasStyles.addCSSAttribute("visibility", "visible");

	//...without making us see the canvas itself *needs IE support
	canvasStyles.addCSSAttribute("background-color", "rgba(0, 0, 0, 0)");
	canvasStyles.addCSSAttribute("border-style", "none");

	//Set the style of the canvas object to the CSS attributes set thus far
	canvasObject.setAttribute('style', canvasStyles.toCSSString());

	if(anID==ctatcontainer)
	{
		getSafeElementById(anID).appendChild(canvasObject);
	}

	else
	{
		//getSafeElementById('ctatdiv'+anID).appendChild(canvasObject);
    getSafeElementById(anID).appendChild(canvasObject);
	}

	var graphicTools=new CTATGraphicsTools(canvasObject.getContext("2d"));
	graphicTools.setGraphicBorderWidth(shapeLineWidth);
	graphicTools.setLineColor(lineColor);
	graphicTools.setFillColor(fillColor);

	/*
	this.addListener=function addListener(eventName, handler)
	{
		eventHandlerList.push([eventName, handler]);
		canvasObject.addEventListener(eventName, handler);
	};
	*/

	/**
	 * The get offset methods are primarily used in situations where you want to place
	 * one shape's canvas over another. Since a shape's coordinates are represented locally
	 * to its own canvas, this is needed to get the absolute positioning of its canvas.
	 */
	this.getXOffset=function getXOffset()
	{
		return (startX + x[0]);
	};


	this.getYOffset=function getYOffset()
	{
		return (startY + y[0]);
	};

	this.addPoint=function addPoint(addX, addY)
	{
		x.push(addX);
		y.push(addY);
	};

	this.getName=function getName()
	{
		return (shapeName);
	};

	this.getXPoint=function getXPoint(index)
	{
		return (x[index]);
	};

	this.getYPoint=function getYPoint(index)
	{
		return (y[index]);
	};

	this.getWidth=function getWidth()
	{
		return (width);
	};

	this.getHeight=function getHeight()
	{
		return (height);
	};

	this.getName=function getName()
	{
		return (shapeName);
	};

	this.getData=function getData()
	{
		return (data);
	};

	this.getColor=function getColor()
	{
		return (fillColor);
	};

	this.getRadius=function getRadius()
	{
		return (radius);
	};

	this.getTextFont=function getTextFont()
	{
		return (textFont);
	};

	this.getTextSize=function getTextSize()
	{
		return (textSize);
	};

	this.getLineColor=function getLineColor()
	{
		return (lineColor);
	};

	this.getFillColor=function getFillColor()
	{
		return (fillColor);
	};

	this.setXPoints=function setXPoints(xPoints)
	{
		x=xPoints;
		canvasStyles.modifyCSSAttribute("left", x[0]+"px");
		canvasObject.setAttribute('style', canvasStyles.toCSSString());
	};

	this.setYPoints=function setYPoints(yPoints)
	{
		y=yPoints;
		canvasStyles.modifyCSSAttribute("top", y[0]+"px");
		canvasObject.setAttribute('style', canvasStyles.toCSSString());
	};

	this.setWidth=function setWidth(aWidth)
	{
		width=aWidth;

		if(anID != "main-canvas")
		{
			canvasObject.width=width;
		}
	};

	this.setHeight=function setHeight(aHeight)
	{
		height=aHeight;

		if(anID != "main-canvas")
		{
			canvasObject.height=height;
		}
	};

	this.setName=function setName(aName)
	{
		shapeName=aName;
	};

	this.setData=function setData(aData)
	{
		data=aData;
	};

	this.setFillColor=function setFillColor(aColor)
	{
		fillColor=aColor;
		graphicTools.setFillColor(aColor);
	};

	this.setLineColor=function setLineColor(aColor)
	{
		lineColor=aColor;
		graphicTools.setLineColor(aColor);
	};

	this.setDrawWidth=function setDrawWidth(aDrawWidth)
	{
		shapeLineWidth=aDrawWidth;
		graphicTools.setGraphicBorderWidth(shapeLineWidth);
	};

	this.setRadius=function setRadius(aRadius)
	{
		radius=aRadius;
	};

	this.setColor=function setColor(aColor)
	{
		lineColor=aColor;
		fillColor=aColor;

		graphicTools.setLineColor(lineColor);
		graphicTools.setFillColor(fillColor);
	};

	this.setTextFont=function setTextFont(aFont)
	{
		textFont=aFont;
		graphicTools.setFont(textFont);
	};

	this.setTextSize=function setTextSize(aSize)
	{
		textSize=aSize;
		graphicTools.setFontSize(textSize);
	};

	this.modifyCanvasCSS=function modifyCanvasCSS(attrib, value)
	{
		canvasStyles.modifyCSSAttribute(attrib, value);
		canvasObject.setAttribute('style', canvasStyles.toCSSString());
	};

	this.detatchCanvas=function detatchCanvas()
	{
		if(anID==ctatcontainer)
		{
			getSafeElementById(anID).removeChild(canvasObject);
		}

		else
		{
			getSafeElementById('ctatdiv'+anID).removeChild(canvasObject);
		}
	};

	this.shapeMagicTrigFunctionX=function shapeMagicTrigFunctionX(pointRatio)
	{
		graphicTools.magicTrigFunctionX(pointRatio);
	};

	this.shapeMagicTrigFunctionY=function shapeMagicTrigFunctionY(pointRatio)
	{
		graphicTools.magicTrigFunctionY(pointRatio);
	};

	this.drawLine=function drawLine()
	{
		graphicTools.drawLine(x[0]+shapeLineWidth, y[0]+shapeLineWidth, x[1], y[1], lineColor, shapeLineWidth);
	};

	/**
	 * The draw circle methods require us to begin at (initial+radius) because HTML5 assumes
	 * that (0, 0) is at the center of the circle, rather than at the corner.
	 */
	this.drawCircle=function drawCircle()
	{
		graphicTools.drawCircle(x[0]+radius, y[0]+radius, radius);
	};

	this.drawCircleFilled=function drawCircleFilled()
	{
		graphicTools.drawCircleFilled(x[0]+radius, y[0]+radius, radius-1);
	};

	this.drawRectangle=function drawRectangle(x, y, aWidth, aHeight)
	{
		graphicTools.drawRectangle(x[0]+shapeLineWidth, y[0]+shapeLineWidth, width-shapeLineWidth, height-shapeLineWidth);
	};

	this.drawRectangleFilled=function drawRectangleFilled()
	{
		graphicTools.drawRectangleFilled(x[0], y[0], width, height);
	};

	this.drawRoundedRect=function drawRoundedRect ()
	{
		graphicTools.drawRoundedRect(x[0]+shapeLineWidth, y[0]+shapeLineWidth, width-radius-shapeLineWidth, height-radius-shapeLineWidth, radius);
	};

	this.drawRoundedRectFilled=function drawRoundedRectFilled ()
	{
		graphicTools.drawRoundedRectFilled(x[0]+shapeLineWidth, y[0]+shapeLineWidth, width-radius-shapeLineWidth, height-radius-shapeLineWidth, radius);
	};

	this.drawText=function drawText ()
	{
		graphicTools.drawText((textSize/2), (textSize/2), data);
	};

	this.drawTriangle=function drawTriangle()
	{
		graphicTools.drawTriangle(x[0]+shapeLineWidth, y[0]+shapeLineWidth, x[1], y[1], x[2], y[2]);
	};

	this.drawTriangleFilled=function drawTriangleFilled()
	{
		graphicTools.drawTriangleFilled(x[0], y[0], x[1], y[1], x[2], y[2]);
	};

	this.save=function save()
	{
		graphicTools.save();
	};

	this.restore=function restore()
	{
		graphicTools.restore();
	};

	this.clip=function clip()
	{
		graphicTools.clip();
	};

	/**
	 * Gives you a new canvas to draw on, but is situated in the same
	 * spot as the last one. Doing so effectively "erases" the painted object.
	 */
	this.erase=function erase()
	{
		pointer.detatchCanvas();
		x=[];
		y=[];

		canvasObject=document.createElement('canvas');
		canvasObject.id=shapeName;
		canvasObject.width=width;
		canvasObject.height=height;

		canvasStyles=new CTATCSS();

		canvasStyles.addCSSAttribute("position", "absolute");
		canvasStyles.addCSSAttribute("left", canvasX+"px");
		canvasStyles.addCSSAttribute("top", canvasY+"px");

		canvasStyles.addCSSAttribute("visibility", "visible");
		canvasStyles.addCSSAttribute("background-color", "rgba(0, 0, 0, 0)");
		canvasStyles.addCSSAttribute("border-style", "none");

		canvasObject.setAttribute('style', canvasStyles.toCSSString());

		if(anID==ctatcontainer)
		{
			getSafeElementById(anID).appendChild(canvasObject);
		}

		else
		{
			getSafeElementById('ctatdiv'+anID).appendChild(canvasObject);
		}

		//Add the event listeners from the last canvas
		for(var i=0; i < eventHandlerList.length; i++)
		{
			canvasObject.addEventListener(eventHandlerList[i][0], eventHandlerList[i][1]);
		}
	};
};
