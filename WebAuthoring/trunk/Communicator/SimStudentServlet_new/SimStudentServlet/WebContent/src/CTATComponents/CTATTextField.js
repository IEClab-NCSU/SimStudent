/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2015-01-07 14:13:03 -0500 (Wed, 07 Jan 2015) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATTextField.js $
 $Revision: 21730 $

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
goog.provide('CTATTextField');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTextBasedComponent');
/**
 *
 */
CTATTextField = function(aDescription,
						aX,
						aY,
						aWidth,
						aHeight)
{
	CTATTextBasedComponent.call(this,
					  			"CTATTextBasedComponent",
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	this.setDefaultWidth (100);
	this.setDefaultHeight (22);

	var editable=true;
	var alpha=0.0;
	var pointer=this;
	var textfield=null;

	pointer.ctatdebug ("Base class initialized, continuing ...");

	this.getAlpha=function getAlpha()
	{
		return (this.alpha);
	};

	this.setAlpha=function setAlpha(anAlpha)
	{
		this.alpha=anAlpha;
	};

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription ();

	/**
	*
	*/
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("resize", "none");

	    textfield=document.createElement('div');

	    textfield.title=pointer.getName();
	    textfield.name=pointer.getName();
	    textfield.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    textfield.setAttribute('onkeypress', 'return noenter(event)');
		textfield.style.resize= 'none';

	    pointer.setComponent(textfield);

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    textfield.innerHTML=pointer.getText();

	    pointer.setInitialized(true);

	    pointer.addComponentReference(pointer, textfield);

		if (pointer.getDivWrap()==null)
		{
			pointer.ctatdebug ("Internal error: no div wrapper avaialble yet to add component to");
			return;
		}
		else
		{
			pointer.ctatdebug ("We have a div wrapper, using ..");
		}

	    pointer.getDivWrap().appendChild(textfield);

		pointer.ctatdebug ("We're wrapped now");

		var bgColor=pointer.getBackgroundColor();
		var backgroundColorString="rgba(" + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + pointer.getAlpha() + ")";

		pointer.modifyCSSAttribute("background-color", backgroundColorString);
		//pointer.addStringCSS("filter: alpha(opacity=1);");

		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");

		pointer.ctatdebug ("Pre-render ...");

	    pointer.render();

		pointer.ctatdebug ("Render done");

		//pointer.ctatdebug ("Adding keypress listener ...");
	    //pointer.addSafeEventListener ('keypress', pointer.processKeyPress,textfield);
		//pointer.ctatdebug ("Adding focus listener ...");

	    //pointer.addSafeEventListener ('focus', pointer.processFocus,textfield);

		textfield.style.resize= 'none';

		pointer.ctatdebug ("init () done");
	};

	/**
	 *
	 * @param aValue
	 */
	this.assignEditable=function assignEditable(aValue)
	{
		editable=aValue;
	};

	/**
	 *
	 * @param aText
	 */
	this.setText=function setText(aText)
	{
		pointer.ctatdebug("setText (" + aText + ")");

		pointer.assignText(aText);

		if (textfield!=null)
		{
			textfield.innerHTML=aText;
	    }
	};

	/**
	 * Override from CTATCompBase because for text based components
	 * we also have to set them non-editable
	 *
	 * @param aValue
	 */
	this.setEnabled=function setEnabled(aValue)
	{
		pointer.assignEnabled(aValue);

		if (textfield==null)
			return;

		textfield.disabled=!aValue;

		this.setEditable (aValue);
	};
	/**
	 *
	 * @param aValue
	 */
	this.setEditable=function setEditable(aValue)
	{
		pointer.assignEditable(aValue);

		if (textfield==null)
			return;

		if (pointer.getEditable()==true)
			textfield.contentEditable='true';
		else
		{
			textfield.contentEditable='false';
			pointer.setFontColor(pointer.getDisabledTextColor());
			pointer.setBackgroundColor(pointer.getDisabledBGColor());
		}
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		useDebugging=true;
	
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...



		// Process component custom styles ...

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if(aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}

			if(aStyle.styleName=="Enabled")
			{
				pointer.setEditable(aStyle.styleValue);
			}

			if(aStyle.styleName=="ShowScrollbars")
			{
				if(aStyle.styleValue=="true")
				{
					pointer.modifyCSSAttribute("overflow", "scroll");
				}

				else
				{
					pointer.modifyCSSAttribute("overflow", "hidden");
				}
			}

			if(aStyle.styleName=="BorderStyle")
			{
				pointer.setBorderStyle(aStyle.styleValue);
			}

			if(aStyle.styleName=="TabOnEnter")
			{
				pointer.setTabOnEnter(aStyle.styleValue);
			}

			if(aStyle.styleName=="MaxCharacters")
			{
				pointer.setMaxCharacters(aStyle.styleValue);
			}
		}
		
		useDebugging=false;
	};
}

CTATTextField.prototype = Object.create(CTATTextBasedComponent.prototype);
CTATTextField.prototype.constructor = CTATTextField;