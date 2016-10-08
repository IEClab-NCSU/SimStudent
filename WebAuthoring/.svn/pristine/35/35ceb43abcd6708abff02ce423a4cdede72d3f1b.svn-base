/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-19 11:56:39 -0500 (Fri, 19 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATCheckBox.js $
 $Revision: 21695 $

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
goog.provide('CTATCheckBox');

goog.require('CTATGlobals');
goog.require('CTATGlobalFunctions');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATCheckBox = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATTutorableComponent.call(this,
			"CTATCheckBox",
			"__undefined__",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	this.setDefaultWidth (100);
	this.setDefaultHeight (22);

	var checkbox=null;
	var label=null;
	var checkboxContainer=null;
	var placement="top";
	var labelStyle = "display: inline-block; float: left;position: absolute;top: 0px; left: 0px; width:auto; text-decoration: inherit;";
	var checkboxStyle = "position: absolute;top: 0px; left: 0px;";

	var pointer=this;

	pointer.setAction('UpdateCheckBox');
	/**
	 *
	 */
	this.getCheckBox=function getCheckBox()
	{
		return (checkbox);
	};

	/**
	 *
	 */
	this.setCheckBox=function setCheckBox(aCheckBox)
	{
		checkbox=aCheckBox;
	};

	/**
	 * values are Top,Bottom,Right,Left
	 */
	this.getLabelPlacement=function()
	{
		return placement;
	};
	/**
	 * Places label in relation to checkbox called in CTATTutor because of
	 * timing shenanigans
	 */
	this.setLabelPlacement=function(aPlacement)
	{
		placement=aPlacement;

		var h,w1,w2;
		if(placement == "right")
		{
			label.style.left = checkbox.clientWidth+10 + "px";
			checkbox.style.left = '0px';
			label.style.top = '0px';
			checkbox.style.top = '0px';
		}
		else if(placement == "left")
		{
			label.style.left = '0px';
			checkbox.style.left = label.clientWidth+5 + "px";
			checkbox.style.top = '0px';
			label.style.top = '0px';
		}
		else if(placement == "bottom")
		{
			h = checkbox.clientHeight+3;
			w1 = label.clientWidth/2;
			w2 = checkbox.clientWidth/2;

			label.style.left = '0px';
			checkbox.style.left=(w1-w2)/2+'px';
			label.style.top=h+"px";
			checkbox.style.top='0px';
		}
		else if(placement == "top")
		{
			h = label.clientHeight+3;
			w1 = label.clientWidth/2;
			w2 = checkbox.clientWidth/2;

			label.style.left='0px';
			checkbox.style.left=(w1-w2)+'px';
			label.style.top='0px';
			checkbox.style.top=h+"px";
		}
	};

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription ();

	/**
	 *
	 */
	this.init=function init ()
	{
		pointer.ctatdebug ("init ("+pointer.getName ()+")");

		pointer.modifyCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");

		checkboxContainer=document.createElement('div');

		checkbox=document.createElement('input');
		checkbox.type='checkbox';
		checkbox.value=pointer.getName ();
		checkbox.name=pointer.getComponentGroup (); // might be wrong
		checkbox.setAttribute ('id', CTATGlobalFunctions.gensym.div_id());
		checkbox.setAttribute('style',checkboxStyle);

		pointer.assignEnabled(true);

		if (pointer.getEnabled()===true)
			checkbox.disabled=false;
		else
			checkbox.disabled=true;

		pointer.ctatdebug ("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

		pointer.setInitialized(true);

		pointer.addComponentReference (pointer,checkbox);

		pointer.getDivWrap().appendChild(checkboxContainer);

		label = document.createElement('label');
		label.htmlFor = checkbox.id;
		label.innerHTML=this.getName ();
		label.setAttribute('style',labelStyle);

		pointer.setComponent(checkboxContainer);
		pointer.setLabel(label);

		checkboxContainer.appendChild (checkbox);
		checkboxContainer.appendChild (label);

		checkboxContainer.setAttribute('style', pointer.getCSS());
		checkboxContainer.setAttribute('id', 'checkboxdiv');

		pointer.addSafeEventListener ('click', pointer.processClick,checkbox);
		pointer.addSafeEventListener ('focus', pointer.processFocus,checkbox);

	};

	/**
	 *
	 */
	this.setText=function setText (aText)
	{
		pointer.ctatdebug ("setText ("+aText+")");

		if (checkboxContainer)
		{
			checkbox.value=aText;
			label.innerHTML=aText;
		}
		else
			pointer.ctatdebug ("Error: component is null, can't set label!");
	};

	/**
	 *
	 */
	this.setEnabled=function setEnabled(aValue)
	{
		if (!checkbox)
			return;

		checkbox.disabled=!aValue;
	};

	/**
	 *
	 */
	this.getCheckBoxInput=function getCheckBoxInput ()
	{
		return (label.innerHTML+": "+checkbox.checked); // TODO: This should get all of the checkboxes in the group.
	};

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

			if(aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}

			if(aStyle.styleName=="labelPlacement")
			{
				pointer.setLabelPlacement(aStyle.styleValue);
			}
		}
	};

	/**
	 *
	 */
	this.reset=function reset ()
	{
		checkbox.checked=false;
		pointer.setEnabled(true);
		label.setAttribute("style", labelStyle);
		checkbox.setAttribute("style",checkboxStyle);
		pointer.setLabelPlacement(placement);
	};

	/**
	 * An Interface Action for updating the checkbox selection.
	 * @param {string} aLabel	A list of ; separated checkbox labels
	 */
	this.UpdateCheckBox = function (aLabel) {
		// if the label of this checkbox is in the list, then check it.
		//pointer.ctatdebug('UpdateCheckBox('+aLabel+') mylabel='+label.innerHTML);
		var search_string = new RegExp("(^|;)"+label.innerHTML+"\\s*:\\s*true");
		//pointer.ctatdebug('UpdateCheckBox: cur='+checkbox.checked+' incoming='+
		//		(aLabel.search(search_string)));
		checkbox.checked = (aLabel.search(search_string)>=0);
	};
	/**
	 * An Interface Action for setting selection of this checkbox.
	 * @param {boolean|string|number} isChecked	Some representation of truth
	 */
	this.SetSelected = function (isChecked) {
		var sel;
		if (typeof(isChecked)==='boolean') { sel=isChecked; }
		else if (typeof(isChecked)==='string') {
			if (isChecked.toLowerCase()=='true') {
				sel = true;
			} else {
				sel = false;
			}
		} else if (typeof(isChecked)==='number') {
			sel = isChecked>0;
		} else {
			sel = false;
		}
		checkbox.checked=sel;
	};
};

CTATCheckBox.prototype = Object.create(CTATTutorableComponent.prototype);
CTATCheckBox.prototype.constructor = CTATCheckBox;

CTAT.ComponentRegistry.addComponentType('CTATCheckBox',CTATCheckBox);