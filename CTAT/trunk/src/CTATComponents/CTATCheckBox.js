/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2017-01-27 13:53:03 -0600 (週五, 27 一月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATCheckBox.js $
 $Revision: 24563 $

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

 */
goog.provide('CTATCheckBox');

//goog.require('CTATGlobals');
goog.require('CTATGlobalFunctions');
goog.require('CTAT.Component.Base.Clickable');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATSAI');
//goog.require('CTATShellTools');
/**
 *
 */
CTATCheckBox = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTAT.Component.Base.Clickable.call(this,
			"CTATCheckBox",
			"__undefined__",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	var checkbox=null;
	var label=null;
	var previewMode = CTATConfiguration.get('previewMode');
	// The following SUI styles are ignored because they were in AS3 or do
	// not work with the current way that css styles are handled.
	this.setStyleHandler("inspBackgroundColor",null); // handle old style name, but don't do anything
	this.setStyleHandler('BackgroundColor',null); // do not respect background color SUI setting
	this.setStyleHandler('DrawBorder',null);
	this.setStyleHandler('showBorder',null);
	this.setStyleHandler('BorderColor',null);
	this.setStyleHandler('TextAlign',null);

	var pointer=this;

	this.setSelection(this.getComponentGroup());
	this.setAction('UpdateCheckBox');
	/**
	 *
	 */
	this.getCheckBox=function getCheckBox()
	{
		return (checkbox);
	};

	this.setStyleHandler('labelPlacement', null); // use CSS
	//this.data_ctat_handlers['label-placement'] = this.setLabelPlacement;

	var handle_selection = function(e) {
		this.updateSAI();
		//console.log(pointer.getSAI().toLSxmlString());
		this.processClick(e);
	};
	/**
	 *
	 */
	this.init=function init ()
	{
		pointer.setInitialized(true);

		checkbox=document.createElement('input');
		checkbox.type='checkbox';
		checkbox.setAttribute ('id', this.getName()+'_check');
		checkbox.classList.add('CTATCheckBox--button');
		if (this.getText())
			checkbox.value=pointer.getText();
		else if (this.getDivWrap() && this.getDivWrap().textContent)
			checkbox.value=this.getDivWrap().textContent;
		else
			checkbox.value=checkbox.id; // default, use generated id

		if (this.getComponentGroup())
			checkbox.name=pointer.getComponentGroup ();
		else if (this.getDivWrap() && $(this.getDivWrap()).attr('name'))
			checkbox.name=$(this.getDivWrap()).attr('name'); // TODO: remove div wraps and use substitution //FIXME: remove name attribute from gen components
		else
			checkbox.name='checkBoxGroup'; // default.

		if (pointer.getEnabled()===true)
			checkbox.disabled=false;
		else
			checkbox.disabled=true;

		pointer.ctatdebug ("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

		pointer.addComponentReference (pointer,checkbox);
		if (!previewMode)
		{
			var content = this.getDivWrap().innerHTML;
			this.getDivWrap().innerHTML = '';
		}
		$(pointer.getDivWrap()).append(checkbox);

		label = document.createElement('label');
		label.htmlFor = checkbox.id;
		label.classList.add('CTATCheckBox--label');
		if (this.getText()) {
			label.textContent=this.getText();
		} else if (content && !previewMode) {
			label.innerHTML=content;
		}
		else if (this.getDivWrap().getAttribute('data-ctat-label'))
		{
			this.setText(this.getDivWrap().getAttribute('data-ctat-label'));
		}
		$(this.getDivWrap()).append(label);
		pointer.setComponent(checkbox);

		checkbox.addEventListener('click',handle_selection.bind(this));
		checkbox.addEventListener('focus',pointer.processFocus);
		checkbox.onfocus = this.processOnFocus; // as the div is the component, need to add the onfocus handler which is normally done by processTabOrder.
		this.setSelection(this.getComponentGroup()); //CTATTutor does a setName after creation but before init.
	};

	this.resize = function()
	{
		var height = $(this.getDivWrap()).height();
		$(label).css('font-size', (height-5)+'px');
		$(this.getComponent()).css('height', (height-10)+'px');
		$(this.getComponent()).css('width', (height-10)+'px');
	}

	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var actions = [];
		var sai;
		if (label.innerHTML.trim().length>0) {
			sai = new CTATSAI();
			sai.setSelection(this.getName()); // make sure it is id and not group
			sai.setInput(label.innerHTML.toString());
			sai.setAction('setText');
			actions.push(sai);
		}
	    return actions;
	};

	/**
	 *
	 */
	var super_setText = this.setText;
	this.setText=function (aText)
	{
		pointer.ctatdebug ("setText ("+aText+")");
		super_setText(aText);
		if (checkbox)
		{
			checkbox.value=aText;
			label.innerHTML=aText;
		}
	};
	this.setStyleHandler('labelText',this.setText);

	/**
	 *
	 */
	var super_setEnabled = this.setEnabled;
	this.setEnabled=function setEnabled(aValue)
	{
		super_setEnabled(aValue);
		if (!checkbox)
			return;

		checkbox.disabled=!this.getEnabled();
	};

	/**
	 *
	 */
	this.getCheckBoxInput=function getCheckBoxInput ()
	{
		return (label.innerHTML+": "+checkbox.checked);
	};

	/**
	 *
	 */
	this.reset=function reset ()
	{
		checkbox.checked=false;
		pointer.setEnabled(true);
	};

	/**
	 * An Interface Action for updating the checkbox selection.
	 * @param {string} aLabel	A list of ; separated checkbox labels
	 */
	this.UpdateCheckBox = function (aLabel) {
		// if the label of this checkbox is in the list, then check it.
		//pointer.ctatdebug('UpdateCheckBox('+aLabel+') mylabel='+label.innerHTML);
		var search_string = new RegExp("(^|;)"+CTATCheckBox.escape(checkbox.value)+"\\s*:\\s*true");
		//pointer.ctatdebug('UpdateCheckBox: cur='+checkbox.checked+' incoming='+
		//		(aLabel.search(search_string)));
		checkbox.checked = (aLabel.search(search_string)>=0);
	};
	/**
	 * An Interface Action for setting selection of this checkbox.
	 * @param {boolean|string|number} isChecked	Some representation of truth
	 */
	this.SetSelected = function (isChecked) {
		var sel = CTATGlobalFunctions.toBoolean(isChecked);
		checkbox.checked=sel;
	};

	this.updateSAI = function () {
		//var checkboxes = CTATShellTools.findComponent(this.getComponentGroup());
		var checkboxes = $('div[data-ctat-component]:has(input[type="checkbox"][name="'+checkbox.name+'"])');

		var cbs_sorted = checkboxes.sort(function (a, b) {
			var an = a.id;
			var bn = b.id; //getName();
			if (an>bn) return 1;
			if (an<bn) return -1;
			return 0;
		});
		var cbinputs = $.map(cbs_sorted, function(cb) {
			var comp = $(cb).data('CTATComponent').getCheckBox(); // TODO: add checks for checkbox existence
			return comp.value+': '+comp.checked;
		}); //getCheckBoxInput();});

		this.setSelection(checkbox.name); //this.getComponentGroup());
		this.setAction('UpdateCheckBox');
		this.setInput(cbinputs.join(';'));
		//console.log(this.getComponentGroup()+':'+cbinputs.join(';'));
	};
	var add_highlighting = function(h) {
		label.classList.add(h);
		checkbox.classList.add(h);
	};
	var remove_highlighting = function(h) {
		label.classList.remove(h);
		checkbox.classList.remove(h);
	};
	this.showCorrect = function(aSAI) {
		remove_highlighting('CTAT--incorrect');
		remove_highlighting('CTAT--hint');
		add_highlighting('CTAT--correct');
	};//setGlow.bind(this,CTATGlobals.Visual.correct_color);
	this.removeCorrect = remove_highlighting.bind(this,'CTAT--correct');
	this.showInCorrect = function(aSAI) {
		remove_highlighting('CTAT--correct');
		remove_highlighting('CTAT--hint');
		add_highlighting('CTAT--incorrect');
	};//setGlow.bind(this,CTATGlobals.Visual.incorrect_color);
	this.removeInCorrect = remove_highlighting.bind(this,'CTAT--incorrect');
	this.showHintHighlight = function(pHint) {
		remove_highlighting('CTAT--incorrect');
		remove_highlighting('CTAT--correct');
		if (pHint)
			add_highlighting('CTAT--hint');
		else
			remove_highlighting('CTAT--hint');
	};
};

CTATCheckBox.prototype = Object.create(CTAT.Component.Base.Clickable.prototype);
CTATCheckBox.prototype.constructor = CTATCheckBox;

CTATCheckBox.escape = function(text) {
	// es7 looks like it will have RegExp.escape() that will do this.
	return text.replace(/[-[\]{}()*+?.,\\^$|#\s\/]/g, "\\$&");
};

CTAT.ComponentRegistry.addComponentType('CTATCheckBox',CTATCheckBox);
