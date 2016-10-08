/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATGraphicsTools.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATGraphicsTools');

goog.require('CTATBase');
goog.require('CTATConfig');
goog.require('CTATGlobals');

CTATGraphicsTools = function(aCanvasCtx, aBorderWidth, aLineColor, aFillColor, aFillShape, allowAA)
{

	CTATBase.call(this, "CTATGraphicsTools", "__undefined__");

	var borderWidth=aBorderWidth || 1;
	var lineColor=aLineColor || "#000000";
	var fillColor=aFillColor || "#EEEEEE";
	var fillShape=aFillShape || false;
	var textFont="Arial";
	var textSize=12;
	var canvasCtx=aCanvasCtx || null;
	var allowAntiAliasing=allowAA || false;
	var aliasingOffset=0;

	var pointer=this;

	if(canvasCtx == null)
	{
		pointer.ctatdebug("--- Error: Must pass a canvas context. ---");
		return -1;
	}

	if(allowAntiAliasing==false)
	{
		aliasingOffset=0.5;
	}

	/**
	 *
	 */
	this.getGraphicBorderWidth=function getGraphicBorderWidth()
	{
		return (borderWidth);
	};

	/**
	 *
	 */
	this.setGraphicBorderWidth=function setGraphicBorderWidth(aWidth)
	{
		borderWidth=aWidth;
	};

	this.setFont=function setFont(aFont)
	{
		textFont=aFont;
	};

	this.setFontSize=function setFontSize(aSize)
	{
		pointer.fontSize=aSize;
	};

	/**
	 *
	 */
	this.getLineColor=function getLineColor()
	{
		return (lineColor);
	};

	/**
	 *
	 */
	this.getFillColor=function getFillColor()
	{
		return (fillColor);
	};

	/**
	 *
	 */
	this.getFillShape=function getFillShape()
	{
		return (fillShape);
	};

	/**
	 *
	 */
	this.setLineColor=function setLineColor(aColor)
	{
		lineColor=aColor;
	};

	/**
	 *
	 */
	this.setFillColor=function setFillColor(aColor)
	{
		fillColor=aColor;
	};

	/**
	 *
	 */
	this.setFillShape=function setFillShape(aValue)
	{
		fillShape=aValue;
	};

	/**
	 *
	 */
	this.getFont=function getFont()
	{
		return (textFont);
	};

	/**
	 *
	 */
	this.getFontSize=function getFontSize()
	{
		return (pointer.fontSize);
	};

	/**
	 *
	 */
	this.magicTrigFunctionX=function magicTrigFunctionX(pointRatio)
	{
		return Math.cos(pointRatio*2*Math.PI);
	};

	/**
	 *
	 */
	this.magicTrigFunctionY=function magicTrigFunctionY(pointRatio)
	{
		return Math.sin(pointRatio*2*Math.PI);
	};

	/**------------------------------------------------------------------------------------
	* drawLine(x1,y1,x2,y2,c,w):void
	* @param x1: the x-coordinate of the first point
	* @param y1: the y-coordinate of the first point
	* @param x2: the x-coordinate of the second point
	* @param y2: the y-coordinate of the second point
	* @param c: color of the line (May not be correct comment)
	* @param w: width of the line (May not be correct comment)
	* Draws a line
	**------------------------------------------------------------------------------------*/
	this.drawLine=function drawLine(x1, y1, x2, y2, aColor, aWidth)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawLine");
		}

		var finalX1=x1+aliasingOffset;
		var finalX2=x2+aliasingOffset;
		var finalY1=y1+aliasingOffset;
		var finalY2=y2+aliasingOffset;

		canvasCtx.lineWidth=aWidth;
		canvasCtx.strokeStyle=aColor;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX1, finalY1);
		canvasCtx.lineTo(finalX2, finalY2);
		canvasCtx.stroke();
	};

	/**------------------------------------------------------------------------------------
	* drawCircle(centerX, centerY, radius, sides):void
	* @param centerX: the x-coordinate of the center
	* @param centerY: the y-coordinate of the center
	* @param radius: the radius of the circle
	* @param sides: the number of sides
	* NOTE: Due to restrictions, drawCircle is drawing an n-gon with 'sides' sides
	* Draws a circle
	**------------------------------------------------------------------------------------*/
	this.drawCircle=function drawCircle(x, y, radius)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawCircle");
		}

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		canvasCtx.strokeStyle=lineColor;
		canvasCtx.lineWidth=borderWidth;

		canvasCtx.beginPath();
		canvasCtx.arc(finalX, finalY, radius, 0, 2*Math.PI);
		canvasCtx.stroke();
	};

	/**------------------------------------------------------------------------------------
	* drawCircleFilled(centerX:Number, centerY:Number, radius:Number, sides:Number):void
	* @param centerX: the x-coordinate of the center
	* @param centerY: the y-coordinate of the center
	* @param radius: the radius of the circle
	* @param sides: the number of sides
	* NOTE: Due to restrictions, drawCircle is drawing an n-gon with 'sides' sides
	* Draws a filled circle
	**------------------------------------------------------------------------------------*/
	this.drawCircleFilled=function drawCircleFilled(x, y, radius)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawCircleFilled");
		}

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		canvasCtx.fillStyle=fillColor;

		canvasCtx.beginPath();
		canvasCtx.arc(finalX, finalY, radius, 0, 2*Math.PI);
		canvasCtx.fill();
	};

	/**------------------------------------------------------------------------------------
	* drawRectangle (x:Number,y:Number,w:Number,h:Number)
	* @param x: the x-coordinate of the top left corner of the rectangle
	* @param y: the y-coordinate of the top left corner of the rectangle
	* @param w: the width of th rectangle
	* @param h: the height of the rectangle
	* Draws a rectangle
	**------------------------------------------------------------------------------------*/
	this.drawRectangle=function drawRectangle(x, y, aWidth, aHeight)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawRectangle");
		}

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		canvasCtx.lineWidth=borderWidth;

		if(fillShape==true)
		{
			canvasCtx.fillStyle=fillColor;
			canvasCtx.fillRect(finalX, finalY, aWidth, aHeight);
		}

		else
		{
			canvasCtx.strokeStyle=lineColor;
			canvasCtx.beginPath();
			canvasCtx.rect(finalX, finalY, aWidth, aHeight);
			canvasCtx.stroke();
		}
	};


	/**------------------------------------------------------------------------------------
	* drawRectangleFilled(x:Number,y:Number,w:Number,h:Number)
	* @param x: the x-coordinate of the top left corner of the rectangle
	* @param y: the y-coordinate of the top left corner of the rectangle
	* @param w: the width of th rectangle
	* @param h: the height of the rectangle
	* Draws a filled rectangle
	**------------------------------------------------------------------------------------*/
	this.drawRectangleFilled=function drawRectangleFilled(x, y, aWidth, aHeight)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawRectangleFilled");
		}

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		canvasCtx.fillStyle=fillColor;
		canvasCtx.fillRect(finalX, finalY, aWidth, aHeight);
	};

	/**
	 *
	 * @param canvasCtx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 */
	this.drawRoundedRect=function drawRoundedRect (x,y,aWidth,aHeight,radius)
	{
		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		//pointer.ctatdebug ("drawRoundedRect ("+finalX+","+finalY+","+aWidth+","+aHeight+","+radius+")");

		if (canvasCtx==null)
			return;

		canvasCtx.strokeStyle=lineColor;
		canvasCtx.lineWidth=borderWidth;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX,finalY+radius);
		canvasCtx.lineTo(finalX,finalY+aHeight-radius);
		canvasCtx.quadraticCurveTo(finalX,finalY+aHeight,finalX+radius,finalY+aHeight);
		canvasCtx.lineTo(finalX+aWidth-radius,finalY+aHeight);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY+aHeight,finalX+aWidth,finalY+aHeight-radius);
		canvasCtx.lineTo(finalX+aWidth,finalY+radius);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY,finalX+aWidth-radius,finalY);
		canvasCtx.lineTo(finalX+radius,finalY);
		canvasCtx.quadraticCurveTo(finalX,finalY,finalX,finalY+radius);
		canvasCtx.stroke();
	};

	/**
	 *
	 * @param grcanvasCtx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 */
	this.drawRoundedRectFilled=function drawRoundedRectFilled (x,y,aWidth,aHeight,radius)
	{
		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		//pointer.ctatdebug ("drawRoundedRect ("+finalX+","+finalY+","+aWidth+","+aHeight+","+radius+")");

		if (canvasCtx==null)
			return;

		// Draw outline ...

		canvasCtx.strokeStyle=lineColor;
		canvasCtx.lineWidth=borderWidth;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX,finalY+radius);
		canvasCtx.lineTo(finalX,finalY+aHeight-radius);
		canvasCtx.quadraticCurveTo(finalX,finalY+aHeight,finalX+radius,finalY+aHeight);
		canvasCtx.lineTo(finalX+aWidth-radius,finalY+aHeight);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY+aHeight,finalX+aWidth,finalY+aHeight-radius);
		canvasCtx.lineTo(finalX+aWidth,finalY+radius);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY,finalX+aWidth-radius,finalY);
		canvasCtx.lineTo(finalX+radius,finalY);
		canvasCtx.quadraticCurveTo(finalX,finalY,finalX,finalY+radius);
		canvasCtx.stroke();

		//fill ...
		canvasCtx.fillStyle=fillColor;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX,finalY+radius);
		canvasCtx.lineTo(finalX,finalY+aHeight-radius);
		canvasCtx.quadraticCurveTo(finalX,finalY+aHeight,finalX+radius,finalY+aHeight);
		canvasCtx.lineTo(finalX+aWidth-radius,finalY+aHeight);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY+aHeight,finalX+aWidth,finalY+aHeight-radius);
		canvasCtx.lineTo(finalX+aWidth,finalY+radius);
		canvasCtx.quadraticCurveTo(finalX+aWidth,finalY,finalX+aWidth-radius,finalY);
		canvasCtx.lineTo(finalX+radius,finalY);
		canvasCtx.quadraticCurveTo(finalX,finalY,finalX,finalY+radius);
		canvasCtx.fill();
	};

	/**
	 * Text is placed from a top left anchor.
	 * @param x
	 * @param y
	 * @param aText
	 */
	this.drawText=function drawText (x,y,aText)
	{
		//pointer.ctatdebug ("drawText ("+anX+","+anY+","+aText+")");

		if (canvasCtx==null)
			return;

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;

		try
		{
			canvasCtx.fillStyle=lineColor;
			canvasCtx.font=(textFont+" "+textSize+"pt");
			canvasCtx.fillText (aText,finalX,finalY);
		}

		catch (e)
		{
			alert (incompatibleBrowserMessage);
			throw new Error ("Browser does not support fillText");
		}
	};

	/**
	 * Text is placed from a top left anchor. Note: according to the spec the anAlign and the
	 * anUnderlined parameter are not used yet. We included it here for future use.
	 * @param x
	 * @param y
	 * @param aText
	 */
	this.drawTextFormatted=function drawTextFormatted (x,y,aText,aFont,aSize,aColor,aBold,anItalic,anUnderlined,anAlign)
	{
		//pointer.ctatdebug ("drawText ("+anX+","+anY+","+aText+")");

		if (canvasCtx==null)
			return;

		var finalX=x+aliasingOffset;
		var finalY=y+aliasingOffset;
		var formatted="";

		if (anItalic==true)
		{
			formatted+="italic ";
		}

		if (aBold==true)
		{
			formatted+="bold ";
		}

		formatted+=(textSize+"pt " + textFont);

		try
		{
			canvasCtx.fillStyle=aColor;
			canvasCtx.font=(formatted);
			canvasCtx.fillText (aText,finalX,finalY);
		}

		catch (e)
		{
			alert (incompatibleBrowserMessage);
			throw new Error ("Browser does not support fillText");
		}
	};

	/**------------------------------------------------------------------------------------
	* drawTriangle(x1,y1,x2,y2,x3,y3)
	* @param x1: the x-coordinate of the point1
	* @param y1: the y-coordinate of the point1
	* @param x2: the x-coordinate of the point2
	* @param y2: the y-coordinate of the point2
	* @param x3: the x-coordinate of the point3
	* @param y3: the y-coordinate of the point3
	* Draws a triangle (Three points define a triangle)
	**------------------------------------------------------------------------------------*/
	this.drawTriangle=function drawTriangle(x1, y1, x2, y2, x3, y3)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawTriangle");
		}

		var finalX1=x1+aliasingOffset;
		var finalX2=x2+aliasingOffset;
		var finalX3=x3+aliasingOffset;
		var finalY1=y1+aliasingOffset;
		var finalY2=y2+aliasingOffset;
		var finalY3=y3+aliasingOffset;

		canvasCtx.fillStyle=lineColor;
		canvasCtx.lineWidth=borderWidth;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX1, finalY1);
		canvasCtx.lineTo(finalX2, finalY2);
		canvasCtx.lineTo(finalX3, finalY3);
		canvasCtx.lineTo(finalX1, finalY1);
		canvasCtx.stroke();
	};

	/**------------------------------------------------------------------------------------
	* drawTriangleFilled(x1:Number,y1:Number,x2:Number,y2:Number,x3:Number,y3:Number)
	* @param x1: the x-coordinate of the point1
	* @param y1: the y-coordinate of the point1
	* @param x2: the x-coordinate of the point2
	* @param y2: the y-coordinate of the point2
	* @param x3: the x-coordinate of the point3
	* @param y3: the y-coordinate of the point3
	* Draws a filled triangle (Three points define a triangle)
	**------------------------------------------------------------------------------------*/

	this.drawTriangleFilled=function drawTriangleFilled(x1, y1, x2, y2, x3, y3)
	{
		if(canvasCtx==null)
		{
			pointer.ctatdebug("Error: canvas canvasCtx is NULL in drawTriangleFilled");
		}

		var finalX1=x1+aliasingOffset;
		var finalX2=x2+aliasingOffset;
		var finalX3=x3+aliasingOffset;
		var finalY1=y1+aliasingOffset;
		var finalY2=y2+aliasingOffset;
		var finalY3=y3+aliasingOffset;

		canvasCtx.fillStyle=fillColor;
		canvasCtx.lineWidth=borderWidth;

		canvasCtx.beginPath();
		canvasCtx.moveTo(finalX1, finalY1);
		canvasCtx.lineTo(finalX2, finalY2);
		canvasCtx.lineTo(finalX3, finalY3);
		canvasCtx.lineTo(finalX1, finalY1);
		canvasCtx.fill();
	};


	//Wrapper function for save
	this.save=function save()
	{
		canvasCtx.save();
	};


	//Wrapper function for restore
	this.restore=function restore()
	{
		canvasCtx.restore();
	};


	//Wrapper function for clip
	this.clip=function clip()
	{
		canvasCtx.clip();
	};

	/**
	 * Paints a white rectangle over the given region.
	 */
	this.clearCanvas=function clearCanvas(x, y, width, height)
	{
		if (CTATConfig.platform=="ctat")
		{
			canvasCtx.clearRect(x, y, width, height);
		}
	};
}

CTATGraphicsTools.prototype = Object.create(CTATBase.prototype);
CTATGraphicsTools.prototype.constructor = CTATGraphicsTools;