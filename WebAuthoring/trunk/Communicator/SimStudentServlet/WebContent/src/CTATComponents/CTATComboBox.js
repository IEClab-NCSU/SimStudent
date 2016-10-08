/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATComboBox.js $
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
goog.provide('CTATComboBox');

goog.require('CTATGlobalFunctions');
//goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');
/**
 * Currently the styles DropDownsize and DropDownWidth are not supported
 * DropDownSize can't be implemented in HTML
 * DropDownWidth can be done using CSS style sheets but setting it from
 * js seems to not work
 */
CTATComboBox = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATTutorableComponent.call(this,
					  			"CTATComboBox",
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	this.setDefaultWidth (100);
	this.setDefaultHeight (22);

	var pointer=this;
	var combobox=null;

	this.setAction('UpdateComboBox');

	this.ctatdebug (pointer.getClassName() + " ("+pointer.getX()+","+pointer.getY()+","+pointer.getWidth()+","+pointer.getHeight()+")");

	this.configFromDescription();

	/**
	 *
	 */
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

	    combobox=document.createElement("select");
	    combobox.name=pointer.getName(); // might be wrong
	    combobox.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    combobox.setAttribute('onkeypress', 'return noenter(event)');
	    //combobox.setAttribute('onchange','processComboSelection();');
	    combobox.onchange=this.processComboSelection;

		pointer.assignEnabled(true);

	    if (pointer.getEnabled()==true)
	        combobox.disabled=false;
	    else
	        combobox.disabled=true;

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

	    pointer.addComponentReference(pointer, combobox);

	    pointer.setComponent(combobox);

	    pointer.getDivWrap().appendChild(combobox);

		pointer.addCSSAttribute("width", pointer.getWidth());
		pointer.addCSSAttribute("height", pointer.getHeight());

	    pointer.render();

	    //currentZIndex++;
	    //currentIDIndex++;
	};

	/**
	 *
	 */
	this.getHTMLComponent=function getHTMLComponent ()
	{
		return (combobox);
	}

	/**
	 *
	 * @param aValue
	 */
	this.addItem=function addItem(aValue)
	{
	    pointer.ctatdebug("addItem (" + aValue + ")");

	    var option=document.createElement("option");
	    option.setAttribute("value", aValue);
	    option.innerHTML=aValue;

	    combobox.appendChild(option);
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		//useDebugging=true;

		pointer.ctatdebug ("processSerialization()");

		if (combobox==null)
		{
			pointer.ctatdebug ("Error: Internal weirdness: combobox object is null in serialization method");
			return;
		}

		// Process component specific pre-defined styles ...



		// Process component custom styles ...

		if (this.styles==null)
		{
			pointer.ctatdebug ("Error: styles structure is null");
			return;
		}

		var i=0;
		var aStyle=null;
		var splitCharacter=',';

		pointer.ctatdebug ("PRE Processing " + this.styles.length + " styles ...");

		for (i=0;i<this.styles.length;i++)
		{
			aStyle=this.styles [i]; // CTATStyle

			pointer.ctatdebug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);

			if (aStyle.styleName=="SplitCharacter")
			{
				pointer.ctatdebug ("Setting SplitCharacter to: " + aStyle.styleValue);

				splitCharacter=aStyle.styleValue;
			}
		}

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (i=0;i<this.styles.length;i++)
		{
			aStyle=this.styles [i]; // CTATStyle

			pointer.ctatdebug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);

			if (aStyle.styleName=="Labels")
			{
				pointer.ctatdebug ("Setting Labels to: " + aStyle.styleValue + " using split character: " + splitCharacter);

				var n=aStyle.styleValue.split(splitCharacter);

				for (var j=0;j<n.length;j++)
				{
					try
					{
						var aLabel=n [j];

						pointer.ctatdebug ("label: " + aLabel);

						this.addItem (aLabel);
					}
					catch (err)
					{
						pointer.ctatdebug ("Exception: " + err.message);
					}
				}
			}

			  /*
			  if(aStyle.styleName=='DropDownSize')
			  {
				//not possible to port from AS3
			  }
			  */

			  /*
			  if(aStyle.styleName=='DropDownWidth')
			  {
				//very difficult to implement with pure js

				//pointer.setDropDownWidth(parseInt(aStyle.styleValue));
			  }
			  */
		}

		//useDebugging=false;
	};

  //doesn't work
	this.setDropDownWidth=function(aWidth)
	{
		for(var i = 0; i < combobox.options.length; i++)
		{
			if(aWidth != -1)
			{
				combobox.options[i].setAttribute('style','width: '+aWidth+'px;');
			}
			else
			{
				combobox.options[i].setAttribute('width',null);
			}
		}
	}
	this.valid_selection = function () {
		return combobox.selectedIndex>=0 && !isBlank(combobox.value);
	}
	/**
	 *
	 */
	this.processComboSelection=function processComboSelection ()
	{
		pointer.ctatdebug ("processComboSelection ()");

		var selected = combobox.options[combobox.selectedIndex].value;
		pointer.setInput(selected);
		//useDebugging=true;
		pointer.grade();
		//useDebugging=false;
	};

	this.UpdateComboBox = function (item) {
		// Need method for valid Action
		// TODO: check if this works.
		combobox.value = item;
	};
};
CTATComboBox.prototype = Object.create(CTATTutorableComponent.prototype);
CTATComboBox.prototype.constructor = CTATComboBox;

CTAT.ComponentRegistry.addComponentType('CTATComboBox',CTATComboBox);