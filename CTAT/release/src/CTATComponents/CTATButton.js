/**-----------------------------------------------------------------------------
 $Author: mdb91 $
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponents/CTATButton.js $
 $Revision: 24393 $

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
goog.provide('CTATButton');

goog.require('CTATButtonBasedComponent');
goog.require('CTATGlobalFunctions');
goog.require('CTATSAI');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATButton = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTATButtonBasedComponent.call(this,
			"CTATButton",
			"aButton",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer=this;
	var button = null;
	var previewMode = CTATConfiguration.get('previewMode');
	/**
	 *
	 */
	this.init=function init()
	{
		pointer.ctatdebug("init (" + pointer.getName() + ")");
		pointer.setActionInput ("ButtonPressed","-1");

		button=document.createElement('button');
		button.type="button";
		button.name=pointer.getName();
		button.value=$(this.getDivWrap()).attr('value') || '-1';
		this.setInput(button.value);
		button.id = CTATGlobalFunctions.gensym.div_id();
		button.setAttribute('onkeypress', 'return noenter(event)');
		button.classList.add('CTAT-button');
		pointer.setInitialized(true);
		if (pointer.getDivWrap().getAttribute('data-ctat-label'))
			button.innerHTML = pointer.getDivWrap().getAttribute('data-ctat-label');
		else if (pointer.getText())
			button.innerHTML = pointer.getText();
		else if (pointer.getDivWrap() && pointer.getDivWrap().innerHTML && !previewMode) 
		{
			var insides = pointer.getDivWrap().innerHTML;
			pointer.getDivWrap().innerHTML = '';
			button.innerHTML = insides;
		}

		pointer.setComponent(button);
		pointer.addComponentReference(pointer, button);
		pointer.getDivWrap().appendChild(button);

		button.addEventListener ('click', pointer.processClick);
		button.addEventListener ('focus', pointer.processFocus);

		// The following events are processed in CTATButtonBasedComponent to ensure
		// we properly update the visual parts of any button based components in
		// case the author assigned images to it.

		button.addEventListener ('mousedown', this.processBaseMousedown);
		button.addEventListener ('mouseup', this.processBaseMouseup);
		button.addEventListener ('mouseover',pointer.processBaseMouseover);
		button.addEventListener ('mouseout',pointer.processBaseMouseout);

		//pointer.getImagesFromDiv (); // defined in CTATButtonBasedComponent
	}.bind(this);
	this.render=function(){return;};
	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var actions = [];
		var sai;
		if (button.innerHTML.trim().length>0) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setInput(button.innerHTML.toString());
			sai.setAction('setText');
			actions.push(sai);
		}
	    return actions;
	};
	/**
	 *
	 */
	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		pointer.setEnabled(true);
	};
};

CTATButton.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATButton.prototype.constructor = CTATButton;

CTAT.ComponentRegistry.addComponentType('CTATButton',CTATButton);
