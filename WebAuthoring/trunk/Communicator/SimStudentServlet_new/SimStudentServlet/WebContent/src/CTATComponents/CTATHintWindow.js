/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATHintWindow.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

  Events: click, mousemove, mouseover, mouseout, keyup, keydown,
  		  focus, blur, select, load

  CSS: http://tutobx.com/post/24806696944/raised-and-pressed-div-using-css
       http://stackoverflow.com/questions/5662178/opacity-of-divs-background-without-affecting-contained-element-in-ie-8

  Js:  http://www.quirksmode.org/js/this.html
       http://unschooled.org/2012/03/understanding-javascript-this/

  CTAT:

 		[48] [07:14:14] [CTATTextField] Processing style labelTextValue,
		[49] [07:14:14] [CTATTextField] Processing style inspBackgroundColor,ffffff
		[50] [07:14:14] [CTATTextField] Processing style inspBorderColor,999999
		[51] [07:14:14] [CTATTextField] Processing style inspFontName,Arial
		[52] [07:14:14] [CTATTextField] Processing style inspFontSize,20
		[53] [07:14:14] [CTATTextField] Processing style inspFontColor,0
		[54] [07:14:14] [CTATTextField] Processing style inspBold,FALSE
		[55] [07:14:14] [CTATTextField] Processing style inspItalic,FALSE
		[56] [07:14:14] [CTATTextField] Processing style inspUnderline,FALSE
		[57] [07:14:14] [CTATTextField] Processing style inspAlignment,left
		[58] [07:14:14] [CTATTextField] Processing style inspShowHintHighlight,true
		[59] [07:14:14] [CTATTextField] Processing style blockOnCorrect,true
		[60] [07:14:14] [CTATTextField] Processing style _tutorComponent,Tutor
		[61] [07:14:14] [CTATTextField] Processing style disabledBackgroundColor,ffffff
		[62] [07:14:14] [CTATTextField] Processing style disabledTextColor,0
		[63] [07:14:14] [CTATTextField] Processing style tutorComponent,Tutor
 */
goog.provide('CTATHintWindow');

goog.require('CTATBinaryImages');
goog.require('CTATCompBase');
goog.require('CTATComponentDescription');
goog.require('CTATConfig');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATGraphicsTools');
goog.require('CTATImageButton');
goog.require('CTATStyle');
goog.require('CTATTextField');
goog.require('CTAT.ComponentRegistry');

//Holds the components local to the Hint Window.
var localComponents=new Array();

//A hard-coded ComponentDescription that will be used for components with no description
var genDesc=new CTATComponentDescription();

genDesc.name="Generic name";
genDesc.styles.push(new CTATStyle("inspBackgroundColor", "#ffffff"));
genDesc.styles.push(new CTATStyle("BorderColor", "#999999"));
genDesc.styles.push(new CTATStyle("FontName", CTATGlobals.Font.family));
genDesc.styles.push(new CTATStyle("FontSize", CTATGlobals.Font.size));
genDesc.styles.push(new CTATStyle("FontColor", CTATGlobals.Font.color));
genDesc.styles.push(new CTATStyle("FontBold", false));
genDesc.styles.push(new CTATStyle("FontItalic", false));
genDesc.styles.push(new CTATStyle("FontUnderlined", false));
genDesc.styles.push(new CTATStyle("TextAlign", globalAlign));
genDesc.styles.push(new CTATStyle("ShowHintHighlight", false));
genDesc.styles.push(new CTATStyle("blockOnCorrect", false));
genDesc.styles.push(new CTATStyle("disabledBackgroundColor", "#999999"));
genDesc.styles.push(new CTATStyle("disabledTextColor", "#999999"));
genDesc.styles.push(new CTATStyle("tutorComponent", null));

/**
 *
 */
CTATHintWindow = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATCompBase.call(this,
					  "CTATHintWindow",
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	this.setDefaultWidth (240);
	this.setDefaultHeight (140);

	var alpha=0.0;
	var pointer=this;
	pointer.isTabIndexable=false;
	var hintContent=null;
	var previous=null;
	var next=null;
	var hintwindow=null;
	var outerBorderColor="#408080";
	var borderRoundness=5;
	var graphicsTools=null;

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription ();

	/**
	*
	*/
	this.getAlpha=function getAlpha()
	{
		return (alpha);
	};
	/**
	*
	*/
	this.setAlpha=function setAlpha(aAlpha)
	{
		alpha=aAlpha;
	};
	/**
	*
	*/
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

		pointer.setCanvasVisibility("visible");
	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

	    hintwindow=document.createElement('div');

	    hintwindow.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    hintwindow.setAttribute('onkeypress', 'return noenter(event)');
	    pointer.setComponent(hintwindow);

	    pointer.addComponentReference(pointer, hintwindow);

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

	    graphicsTools=new CTATGraphicsTools(pointer.getSubCanvasCtx());

	    pointer.getDivWrap().appendChild(hintwindow);

	    //currentZIndex++;
	    //currentIDIndex++;

	    //> ------------- Hintwindow specific code ---------------------------------------

		genDesc.name="hintcontent";
		genDesc.type="CTATTextField";

		if (CTATConfig.platform=="google")
			hintContent=new CTATTextField (genDesc,4,4,aWidth-8-4,aHeight-21-12);
		else
			hintContent=new CTATTextField (genDesc,6,6,aWidth-10,aHeight-21-12);

		hintContent.setName("hintcontent");
		//hintContent.setInitialized (true);

		hintContent.wrapComponent(hintwindow);
		hintContent.setCanvasVisibility("hidden");
		hintContent.setAlpha(1.0);
		hintContent.setShowBorder (true);
		hintContent.setBackgroundColor ('#ffffff');
		hintContent.addCSSAttribute("overflow", "auto");
		pointer.addComponent (hintContent);

		pointer.ctatdebug ("Jumping out of init ()");

		//> ------------- Previous button specific code ----------------------------------

		genDesc.name="previous";
		genDesc.type="CTATImageButton";

		if (CTATConfig.platform=="google")
			previous=new CTATImageButton (genDesc,4,aHeight-24,82,21);
		else
			previous=new CTATImageButton (genDesc,5,aHeight-24,82,21);

		previous.setName("previous");
		//previous.setInitialized (true);  // Don't call this
		previous.wrapComponent(hintwindow);
		previous.setCanvasVisibility("hidden");

		if (CTATConfig.embedImages=="true")
		{
			previous.assignImages (hintPreviousDefault,
								   hintPreviousHover,
								   hintPreviousHover,
								   hintPreviousDisabled);
		}
		else
		{
			previous.assignImages ("skindata/Hint-Previous.png",
					   "skindata/Hint-Previous-Hover.png",
					   "skindata/Hint-Previous-Hover.png",
					   "skindata/Hint-Previous-Disabled.png");
		}

		pointer.addComponent (previous);
		//previous.addSafeEventListener ('click',previous.processPreviousButton);

		//> ------------- Next specific code ---------------------------------------

		genDesc.name="next";
		genDesc.type="CTATImageButton";

		if (CTATConfig.platform=="google")
			next=new CTATImageButton (genDesc,aWidth-82-windowPadding,aHeight-24,82,21);
		else
			next=new CTATImageButton (genDesc,aWidth-82-windowPadding,aHeight-24,82,21);

		next.setName("next");
		//next.setInitialized (true); // Don't call this
		next.wrapComponent(hintwindow);
		next.setCanvasVisibility("hidden");

		if (CTATConfig.embedImages=="true")
		{
			next.assignImages (hintNextDefault,
								hintNextHover,
								hintNextHover,
								hintNextDisabled);
		}
		else
		{
			next.assignImages ("skindata/Hint-Next.png",
					   "skindata/Hint-Next-Hover.png",
					   "skindata/Hint-Next-Hover.png",
					   "skindata/Hint-Next-Disabled.png");
		}

		pointer.addComponent (next);

		//next.addSafeEventListener ('click',next.processNextButton);

	    //> ------------------------------------------------------------------------

		feedbackComponents.push(this);

		pointer.ctatdebug("Disabling previous and next ...");

		previous.setEnabled (false);
		next.setEnabled (false);
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

				if(aStyle.styleName=="borderRoundness")
				{
					borderRoundness=aStyle.styleValue;
				}

				if(aStyle.styleName=="OuterBorderColor")
				{
					outerBorderColor=aStyle.styleValue;
				}

				// Styles normally used for the main label now assigned to the content
				// of the hint window

				if (aStyle.styleName=="FontFace")
				{
					pointer.setFontFamily(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontSize")
				{
					//pointer.setFontSize(parseInt(aStyle.styleValue));

					hintContent.setFontSize(parseInt(aStyle.styleValue));
				}

				if (aStyle.styleName=="TextColor")
				{
					pointer.setFontColor(formatColor(aStyle.styleValue));

					hintContent.setFontColor(formatColor(aStyle.styleValue));
				}

				if (aStyle.styleName=="FontBold")
				{
					pointer.setBolded(aStyle.styleValue);

					hintContent.setBolded(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontItalic")
				{
					pointer.setItalicized(aStyle.styleValue);

					hintContent.setItalicized(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontUnderlined")
				{
					pointer.setUnderlined(aStyle.styleValue);

					hintContent.setUnderlined(aStyle.styleValue);
				}

				if (aStyle.styleName=="TextAlign")
				{
					pointer.setAlign(aStyle.styleValue);

					hintContent.setAlign(aStyle.styleValue);
				}
			}
		}
	};

	/**
	 *
	 */
	this.showFeedback=function showFeedback (aMessage)
	{
		pointer.ctatdebug("showFeedback ("+aMessage+")");

		hintContent.setText (aMessage);

		previous.setEnabled (false);
		next.setEnabled (false);
	};
	/**
	 * An Interface Action for setting what is displayed in the hint window.
	 * @param {string} message
	 * @see CTATHintWindow.showFeedback
	 */
	this.SetText = this.showFeedback;
	/**
	 *
	 * @param hintList
	 */
	this.showHint=function showHint (hintList)
	{
		pointer.ctatdebug("showHint ()");

		if (hintList==null)
		{
			hints=hintList;
			hintIndex=0;
			//hintContent.setText ("");
			previous.setEnabled (false);
			next.setEnabled (false);
			return;
		}

		hints=hintList;
		hintIndex=0;

		this.setEnabled (true);

		previous.setEnabled (false);
		next.setEnabled (false);

		if (!hints)
		{
			pointer.ctatdebug("Null hint in list, bump");
			return;
		}

		if (hints [hintIndex]=="")
		{
			pointer.ctatdebug("Empty hint in list, bump");
			return;
		}

		hintContent.setText (hints [hintIndex]); // Show first hint

		if (hints.length>1)
		{
			next.setEnabled (true);
		}
	};

	/**
	 * Not sure if we need to override this
	 * @param aValue
	 */
	this.setEnabled=function setEnabled(aValue)
	{
		pointer.assignEnabled(aValue);

		if (pointer.getComponent()==null)
		{
			pointer.ctatdebug ("Error pointer.getComponent()==null");
			return;
		}

		pointer.getComponent ().disabled=!aValue;

		/*
		if (pointer.getEnabled()==true)
			pointer.getComponent().contentEditable='true';
		else
			pointer.getComponent().contentEditable='false';
		*/
	};

	/**
	 *
	 */
	this.addComponent=function addComponent (aComponent)
	{
		pointer.ctatdebug ("addComponent ()");

		localComponents.push (aComponent);

		aComponent.processSerialization ();
		aComponent.init ();

		pointer.ctatdebug ("addComponent () done");
	};

	/**
	 *
	 */
	this.goPrevious=function goPrevious ()
	{
		pointer.ctatdebug ("addComponent ()");

		hintIndex--;

		previous.setEnabled (true);
		next.setEnabled (true);

		if (hintIndex<=0)
		{
			hintIndex=0;

			previous.setEnabled (false);
		}

		hintContent.setText (hints [hintIndex]);
	};

	/**
	 *
	 */
	this.goNext=function goNext ()
	{
		pointer.ctatdebug ("goNext ()");

		hintIndex++;

		previous.setEnabled (true);
		next.setEnabled (true);

		if (hintIndex>(hints.length-1))
		{
			hintIndex=(hints.length-1);
		}

		if (hintIndex>(hints.length-2))
		{
			next.setEnabled (false);
		}

		hintContent.setText (hints [hintIndex]);
	};

	/**
	*
	*/
	this.drawComponent=function drawComponent()
	{
		//pointer.ctatdebug ("drawComponent + ("+pointer.getX()+","+pointer.getY()+","+pointer.getWidth()+","+pointer.getHeight()+")");

		//pointer.ctatdebug ("drawComponent - ("+xOffset+","+yOffset+","+pointer.getWidth()+","+pointer.getHeight()+")");

		graphicsTools.setLineColor(outerBorderColor);

		graphicsTools.drawRoundedRectFilled (1,1,
									   		 pointer.getWidth(),
									   		 pointer.getHeight(),
									  		 5);
	};
}

CTATHintWindow.prototype = Object.create(CTATCompBase.prototype);
CTATHintWindow.prototype.constructor = CTATHintWindow;

CTAT.ComponentRegistry.addComponentType('CTATHintWindow',CTATHintWindow);