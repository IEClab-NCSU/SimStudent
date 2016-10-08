/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSkillWindow.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATSkillWindow');

goog.require('CTATCompBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATGraphicsTools');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATSkillWindow = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATCompBase.call(this,
					  "CTATSkillWindow",
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	this.setDefaultWidth (240);
	this.setDefaultHeight (140);

	var hints=new Array ();
	var alpha=0.0;
	var pointer=this;
	pointer.isTabIndexable=false;
	var graphicsTools=null;

	// Skill window specific variables

	var marginX		   =6;
	var marginY		   =6;
	var barDistance	   =4;
	var barHeight	   =15;
	var barMaxX		   =150;
	var borderRoundness =5;

	var spThreshold	=0.95;

	var inspSkillBarBorderColor   ="#cccccc";
	var inspSkillBarColor         ="#ffffcc";
	var inspSkillBarThresholdColor="#66cc33";
	var inspSkillVerticalStroke   =true;
	var outerBorderColor="#408080";

	var skillwindow=null;

	this.getAlpha=function getAlpha()
	{
		return (alpha);
	};

	this.setAlpha=function setAlpha(aAlpha)
	{
		alpha=aAlpha;
	};

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription ();

	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.setCanvasVisibility("visible");

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

		graphicsTools=new CTATGraphicsTools(pointer.getSubCanvasCtx());

	    //currentZIndex++;
	    //currentIDIndex++;

		this.drawComponent ();
	};

	/**
	 *
	 */
	this.assignSkillSet=function assignSkillSet (aSkillSet)
	{
		skillSet=aSkillSet;

		this.drawComponent ();
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...

		pointer.setText (this.label);

		// Process component custom styles ...
		this.styles=aDescription.styles;

		this.styles=pointer.getGrDescription().styles;

		if (this.styles!=null)
		{
			pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

			for (var i=0;i<this.styles.length;i++)
			{
				var aStyle=this.styles [i]; // CTATStyle

				if ((aStyle.styleName=="SkillBarBorderColor") || (aStyle.styleName=="inspSkillBarBorderColor"))
				{
					console.log ("SkillBarBorderColor: "+ aStyle.styleValue);

					inspSkillBarBorderColor=formatColor (aStyle.styleValue);
				}

				if((aStyle.styleName=="SkillBarColor") || (aStyle.styleName=="inspSkillBarColor"))
				{
					console.log ("SkillBarColor: "+ aStyle.styleValue);

					inspSkillBarColor=formatColor (aStyle.styleValue);
				}

				if((aStyle.styleName=="SkillMasteryColor") || (aStyle.styleName=="inspSkillBarThresholdColor"))
				{
					console.log ("SkillMasteryColor: "+ aStyle.styleValue);

					inspSkillBarThresholdColor=formatColor (aStyle.styleValue);
				}

				if ((aStyle.styleName=="SkillBarWidth") || (aStyle.styleName=="inspSkillBarWidth"))
				{
					console.log ("SkillBarWidth: "+ aStyle.styleValue);

					barMaxX=parseInt (aStyle.styleValue);
				}

				if ((aStyle.styleName=="backgroundColor") || (aStyle.styleName=="inspBackgroundColor"))
				{
					console.log ("backgroundColor: "+ aStyle.styleValue);

					pointer.setBackgroundColor(formatColor(aStyle.styleValue));
				}

				if(aStyle.styleName=="borderRoundness")
				{
					borderRoundness=aStyle.styleValue;
				}

				if(aStyle.styleName=="OuterBorderColor")
				{
					outerBorderColor=formatColor (aStyle.styleValue);
				}

				// Styles normally used for the main label now assigned to the skill bar labels

				if (aStyle.styleName=="FontFace")
				{
					pointer.setFontFamily(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontSize")
				{
					pointer.setFontSize(parseInt(aStyle.styleValue));
				}

				if (aStyle.styleName=="TextColor")
				{
					pointer.setFontColor(formatColor(aStyle.styleValue));
				}

				if (aStyle.styleName=="FontBold")
				{
					pointer.setBolded(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontItalic")
				{
					pointer.setItalicized(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontUnderlined")
				{
					pointer.setUnderlined(aStyle.styleValue);
				}

				if (aStyle.styleName=="TextAlign")
				{
					pointer.setAlign(aStyle.styleValue);
				}
			}
		}
	};

	/**
	 * Override from CTATComponent.js
	 */
	this.drawComponent=function drawComponent ()
	{
		pointer.ctatdebug ("drawComponent ()");

		if (skillSet==null)
		{
			ctatdebug ("Info: no skillSet object available, bumping out");
			return;
		}

		graphicsTools.save();

		graphicsTools.setLineColor(outerBorderColor);

		//For some reason, using borderRoundness will mess this function up...
		graphicsTools.drawRoundedRectFilled (1,1,
									   		pointer.getWidth(),
									   		pointer.getHeight(),
									  		5);

		graphicsTools.clip();

		var top=marginY;

		// Draw each skill, one at a time ...

		var skillList=skillSet.getSkillSet ();

		if (skillList==null)
		{
			pointer.ctatdebug ("Error: list of skills is null in skills object");
			return;
		}

		if (skillList.length==0)
		{
			pointer.ctatdebug ("Error: list of skills is 0 length");
			return;
		}

		for (var i=0;i<skillList.length;i++)
		{
			var skill=skillList [i];

			pointer.ctatdebug ("Drawing skill "+i+" "+skill.getDisplayName () + " level: " + skill.getLevel ()+" ...");

			graphicsTools.setLineColor(inspSkillBarBorderColor);
			graphicsTools.drawRectangle (marginX,top,barMaxX,barHeight);

			if (skill.getDisplayName() == null)
			{
				graphicsTools.setLineColor("#000000");
				graphicsTools.drawTextFormatted (marginX+1+barMaxX+2,top+(barHeight/2),
												 "no-name",
												 pointer.getFontFamily (),
												 pointer.getFontSize (),
												 pointer.getFontColor (),
												 pointer.getBolded (),
												 pointer.getItalicized (),
												 pointer.getUnderlined (),
												 pointer.getAlign ());
			}
			else
			{
				graphicsTools.setLineColor("#000000");
				graphicsTools.drawTextFormatted (marginX+1+barMaxX+2,top+(barHeight/2),
												 skill.getDisplayName(),
												 pointer.getFontFamily (),
												 pointer.getFontSize (),
												 pointer.getFontColor (),
												 pointer.getBolded (),
												 pointer.getItalicized (),
												 pointer.getUnderlined (),
												 pointer.getAlign ());
			}

			if (skill.getLevel ()>spThreshold)
			{
				graphicsTools.setFillColor(inspSkillBarThresholdColor);
				graphicsTools.drawRectangleFilled (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2);
				//this.drawRect (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2,'#000000');
			}
			else
			{
				graphicsTools.setFillColor(inspSkillBarColor);
				graphicsTools.drawRectangleFilled (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2);
				//this.drawRect (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2,'#000000');
			}

			/*
			if (inspSkillVerticalStroke==true)
			{
				//sTools.lineColor=inspSkillBarThresholdColor;
				//this.drawLine (marginX+1+calcWidth (skill.getLevel (),barMaxX-2),top+1,marginX+1+calcWidth (skill.getLevel (),barMaxX-2),top+1+barHeight-2);
			}
			*/

			top+=(barHeight+barDistance);
		}

		graphicsTools.restore();
	};
}

CTATSkillWindow.prototype = Object.create(CTATCompBase.prototype);
CTATSkillWindow.prototype.constructor = CTATSkillWindow;

CTAT.ComponentRegistry.addComponentType('CTATSkillWindow',CTATSkillWindow);