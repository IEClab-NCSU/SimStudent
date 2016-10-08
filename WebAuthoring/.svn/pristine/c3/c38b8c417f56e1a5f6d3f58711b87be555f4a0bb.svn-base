/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2015-01-07 14:13:03 -0500 (Wed, 07 Jan 2015) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATTextInput.js $
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
goog.provide('CTATTextInput');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTextBasedComponent');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATTextInput = function(aDescription,
						aX,
						aY,
						aWidth,
						aHeight)
{
	CTATTextBasedComponent.call(this,
					  			"CTATTextInput",
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	this.setDefaultWidth (100);
	this.setDefaultHeight (22);

	var pointer=this;
	var textinput=null;
	var cellContainer=null;

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	/**
	 *
	 */
	this.init=function init()
	{
	    this.ctatdebug("init (" + pointer.getName() + ")");

	    this.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");
		pointer.addCSSAttribute("resize", "none");
		pointer.render ();

	    textinput=document.createElement("input");
	    textinput.type="text";
	    textinput.name=aDescription.name;
	    textinput.setAttribute('maxlength', pointer.getMaxCharacters());
	    textinput.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    textinput.setAttribute('onkeypress', 'return noenter(event)');
		textinput.style.resize= 'none';

	    pointer.setComponent(textinput);

		/*
	    if (pointer.getEnabled() == true)
	        pointer.getComponent().contentEditable='true';
	    else
	        pointer.getComponent().contentEditable='false';

	    pointer.getComponent().contentEditable='true';
		*/

	    this.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    textinput.value=this.getText();

	    pointer.setInitialized(true);

	    pointer.addComponentReference(pointer, textinput);

	    pointer.getDivWrap().appendChild(textinput);

		//this.addCSSAttribute("width", pointer.getWidth()+"px");
		//this.addCSSAttribute("height", pointer.getHeight()+"px");

	    pointer.render();

	    //currentZIndex++;
	    //currentIDIndex++;

	    pointer.addSafeEventListener ('keypress', pointer.processKeypress,textinput);
	    pointer.addSafeEventListener ('focus', pointer.processFocus,textinput);
	    pointer.addSafeEventListener ('click',pointer.processClick,textinput);
	    pointer.addSafeEventListener ('dblclick',pointer.processDblClick,textinput);
	};

	this.setCellContainer=function setCellContainer(aContainer)
	{
		cellContainer=aContainer;
	};

	this.getCellContainer=function getCellContainer()
	{
		return (cellContainer);
	};

	/**
	 *
	 */
	this.setText = function setText(aText)
	{
	    pointer.ctatdebug("setText (" + aText + ")");

 		pointer.assignText(aText);
 		textinput.value=aText;

 		// this.redraw (); // Very very experimental
	};


	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		textinput.value="";
	}

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...

		this.styles=pointer.getGrDescription().styles;

		// Process component custom styles ...

		if (this.styles==null)
		{
			pointer.ctatdebug ("Error: styles structure is null");
			return;
		}

		/*
		<CTATStyleProperty>
			<name>DrawBorder</name>
			<value fmt="text" name="Draw Border" type="Boolean" includein="full">true</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>borderRoundness</name>
			<value fmt="text" name="Border Roundness" type="Number" includein="full">0</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>BorderColor</name>
			<value fmt="text" name="Border Color" type="Color" includein="full">999999</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>FontFace</name>
			<value fmt="text" name="Font" type="String" includein="full">Arial</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>FontSize</name>
			<value fmt="text" name="Font Size" type="Number" includein="full">12</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>TextColor</name>
			<value fmt="text" name="Font Color" type="Color" includein="full">0</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>FontBold</name>
			<value fmt="text" name="Font Bold" type="Boolean" includein="full">false</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>FontItalic</name>
			<value fmt="text" name="Font Italic" type="Boolean" includein="full">false</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>FontUnderlined</name>
			<value fmt="text" name="Font Underline" type="Boolean" includein="full">false</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>TextAlign</name>
			<value fmt="text" name="Text Align" type="String" includein="full">left</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>transparencyValue</name>
			<value fmt="text" name="Background Transparency" type="Number" includein="full">1</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>BackgroundColor</name>
			<value fmt="text" name="Background Color" type="Color" includein="full">00ffff</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>disabledBackgroundColor</name>
			<value fmt="text" name="Disabled Background Color" type="Color" includein="full">00ff00</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>disabledTextColor</name>
			<value fmt="text" name="Disabled Text Color" type="Color" includein="full">0</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>padding</name>
			<value fmt="text" name="Text Pixel Padding" type="Number" includein="full">0</value>
		</CTATStyleProperty>
		<CTATStyleProperty>
			<name>showBorder</name>
			<value fmt="text" name="Show Border" type="Boolean" includein="full">true</value>
		</CTATStyleProperty>
		*/

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if(aStyle.styleName=="MaxCharacters")
			{
				pointer.setMaxCharacters(aStyle.styleValue);
			}

			if(aStyle.styleName=="Enabled")
			{
				pointer.setEditable(aStyle.styleValue);
			}

			if(aStyle.styleName=="DrawBorder")
			{
				if (aStyle.styleValue=="true")
				{
					pointer.addCSSAttribute("border-width",1);
				}
				else
				{
					pointer.addCSSAttribute("border-width",0);
				}
			}

			if(aStyle.styleName=="borderRoundness")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="BorderColor")
			{
				// pointer.addCSSAttribute("border-color","#"+aStyle.styleValue);
			}

			if(aStyle.styleName=="FontFace")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="FontSize")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="TextColor")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="FontBold")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="FontItalic")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="FontUnderlined")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="TextAlign")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="transparencyValue")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="BackgroundColor")
			{
				// pointer.addCSSAttribute('background-color', '#'+aStyle.styleValue);
			}

			if(aStyle.styleName=="disabledBackgroundColor")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="disabledTextColor")
			{
				//pointer.setPadding(parseInt(aStyle.styleValue));
			}

			if(aStyle.styleName=="padding")
			{
				pointer.setPadding(parseInt(aStyle.styleValue));
			}
		}
	};

	/**
	 * TPA
	 */
	this.UpdateTextArea=function UpdateTextArea (aText)
	{
		pointer.ctatdebug ("UpdateTextArea ("+aText+")");
		this.setText(aText);
	}

	//Trying out.. (dhruv, 07/21/2014.)
	this.UpdateTextField=function UpdateTextField (aText)
	{
		pointer.ctatdebug ("UpdateTextField ("+aText+")");
		this.setText(aText);
	}
}

CTATTextInput.prototype = Object.create(CTATTextBasedComponent.prototype);
CTATTextInput.prototype.constructor = CTATTextInput;

CTAT.ComponentRegistry.addComponentType('CTATTextInput', CTATTextInput);
