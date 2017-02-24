/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATRadioButton.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATRadioButton');

goog.require('CTATGlobals');
goog.require('CTATGlobalFunctions');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');

CTATRadioButton = function(aDescription, aX, aY, aWidth, aHeight)
{
	CTATTutorableComponent.call(this,
			"CTATRadioButton",
			"__undefined__",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	this.setDefaultWidth (100);
	this.setDefaultHeight (22);

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	var pointer=this;
	var radioContainer=null;
	var radiobutton=null;
	var label;
	var radioGroup;
	var radioText="";
	var labelStyle = "display: inline-block; float: left;position: absolute;top: 0px; left: 0px;width:auto; text-decoration: inherit;";
	var radiobuttonStyle = "position: absolute;top: 0px; left: 0px;";
	var labelPlacement;

	this.init=function init()
	{
		pointer.ctatdebug ("init ("+pointer.getName ()+")");

		pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");

		radioContainer=document.createElement('div');

		radiobutton=document.createElement('input');
		radiobutton.type='radio';
		radiobutton.value=pointer.getName();
		radiobutton.name=pointer.getComponentGroup();
		radiobutton.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
		radiobutton.setAttribute('style',radiobuttonStyle);
		pointer.assignEnabled(true);

		if (pointer.getEnabled()==true)
			radiobutton.disabled=false;
		else
			radiobutton.disabled=true;

		pointer.ctatdebug ("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

		pointer.setInitialized(true);

		pointer.addComponentReference(pointer, radioContainer);

		pointer.getDivWrap().appendChild(radioContainer);

		label = document.createElement('label');
		label.htmlFor = "id";
		label.innerHTML=pointer.getText ();
		label.setAttribute('style',labelStyle);

		pointer.setComponent(radioContainer);
		pointer.setLabel(label);

		radioContainer.appendChild (radiobutton);
		radioContainer.appendChild (label);

		pointer.render ();

		pointer.addSafeEventListener ('click',pointer.processClick,radiobutton);
		pointer.addSafeEventListener ('focus', pointer.processFocus,radiobutton);
	};

	this.setText=function setText (aText)
	{
		pointer.ctatdebug ("setText ("+aText+")");

		radioText=aText;

		if (radioContainer!=null)
		{
			radiobutton.value=aText;
			label.innerHTML=aText;
		}
	};

	this.getText=function getText ()
	{
		return (radioText);
	}

	this.showCorrect=function showCorrect(aMessage)
	{
		pointer.ctatdebug("showCorrect("+correctColor+")");

		pointer.setFontColor(correctColor);
		pointer.setEnabled (false);
		pointer.setLabelPlacement(labelPlacement);
		console.log(pointer.getDivWrap());
		console.log(label);
	};

	/**
	 * Radiobutton groups are disabled when they are correct. We need to
	 * disable them when we received a CorrectAction. The action parameter
	 * is needed for this case, and is otherwise optional.
	 */
	this.setEnabled=function setEnabled(aValue, action)
	{
		if (action==undefined)
		{
			if (radiobutton==null)
				return;

			radiobutton.disabled=!aValue;
		}

		else if (action=="CorrectAction")
		{
			for (var i=0;i<components.length;i++)
			{
				var aDesc=components [i];

				if(aDesc.groupName=="")
				{
					pointer.setEnabled(false);
				}
			}
		}
	};

	this.setChecked=function setChecked(aValue)
	{
		if (radiobutton==null)
			return;

		radiobutton.checked=aValue;
	};

	this.getChecked=function getChecked()
	{
		return (radiobutton.checked);
	};

	//SAI Input
	this.getRadioInput=function getRadioInput ()
	{
		return (pointer.getName()+": "+label.innerHTML);
	};
	this.getLabelPlacement=function()
	{
		return labelPlacement;
	}

	//places label in relation to radiobutton
	//called in CTATTutor because of timing shenanigans
	this.setLabelPlacement=function(aPlacement)
	{
		labelPlacement=aPlacement;
		label.style.position='absolute';
		radiobutton.style.position='absolute'
			if(labelPlacement == "right"){
				label.style.left = radiobutton.clientWidth+10 + "px";
				radiobutton.style.left = '0px';
				label.style.top = '0px';
				radiobutton.style.top = '0px';
			}else if(labelPlacement == "left"){
				label.style.left = '0px';
				radiobutton.style.left = label.clientWidth+5 + "px"
				radiobutton.style.top = '0px';
				label.style.top = '0px';
			}
			else if(labelPlacement == "bottom"){
				var h = radiobutton.clientHeight+3;
				var w1 = label.clientWidth/2;
				var w2 = radiobutton.clientWidth/2;

				label.style.left = '0px';
				radiobutton.style.left=(w1-w2)/2+'px';
				label.style.top=h+"px";
				radiobutton.style.top='0px'
			}
			else if(labelPlacement == "top"){
				var h = label.clientHeight+3;
				var w1 = label.clientWidth/2;
				var w2 = radiobutton.clientWidth/2;

				label.style.left='0px';
				radiobutton.style.left=(w1-w2)+'px';
				label.style.top='0px';
				radiobutton.style.top=h+"px";
			}
	}

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...
		// Process component custom styles ...

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if(aStyle.styleName=="buttonLabel")
			{
				pointer.setText(aStyle.styleValue);
			}

			if(aStyle.styleName=="labelPlacement")
			{
				pointer.setLabelPlacement(aStyle.styleValue);
			}
		}
	};

	this.reset=function reset ()
	{
		pointer.ctatdebug (" reset ( " + pointer.getName () + ")");

		radiobutton.checked=false;
		pointer.setEnabled(true);
		label.setAttribute("style", " ");
	};

	/**
	 * TPA
	 */
	this.UpdateRadioButton=function UpdateRadioButton (selection)
	{
		if (selection.indexOf(pointer.getName())>=0) {
			pointer.setChecked(true);
		}
		pointer.ctatdebug ("UpdateRadioButton ()");
	};
}

CTATRadioButton.prototype = Object.create(CTATTutorableComponent.prototype);
CTATRadioButton.prototype.constructor = CTATRadioButton;

CTAT.ComponentRegistry.addComponentType('CTATRadioButton',CTATRadioButton);