/**-----------------------------------------------------------------------------
 $Author: mdb91 $
 $Date: 2017-01-10 17:37:04 -0600 (週二, 10 一月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponents/CTATTextInput.js $
 $Revision: 24499 $

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
goog.provide('CTATTextInput');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSAI');
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
	var pointer=this;
	var textinput=null;
	var cellContainer=null;
	var previewMode = CTATConfiguration.get('previewMode');
	/**
	 *
	 */
	this.init=function init()
	{
	    this.ctatdebug("init (" + pointer.getName() + ")");

	    textinput=document.createElement("input");
	    textinput.type="text";
	    if (aDescription)
	    	textinput.name=aDescription.name;
	    textinput.setAttribute('maxlength', pointer.getMaxCharacters());
	    textinput.setAttribute('id', CTATGlobalFunctions.gensym.div_id());

	    pointer.setComponent(textinput);

	    this.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    var $div=$(this.getDivWrap());
	    if ($div.attr('value')) {
	    	this.setText($div.attr('value'));
	    }
		
	    //textinput.value=this.getText();
	    var $txt=$(textinput);
		['autofocus','defaultValue','maxLength','pattern','placeholder','readOnly','size','title'].forEach(
				function(attr) {
					var av = $div.attr(attr);
					if (av) {
						$txt.attr(attr,av);
					}
		});

	    pointer.setInitialized(true);

	    pointer.addComponentReference(pointer, textinput);

	    pointer.getDivWrap().appendChild(textinput);

	    //pointer.render();

	    pointer.addSafeEventListener ('keypress', pointer.processKeypress,textinput);
	    pointer.addSafeEventListener ('focus', pointer.processFocus,textinput);
	    //pointer.addSafeEventListener ('click',pointer.processClick,textinput);
	    //$(textinput).blur(function (e) { pointer.processAction(); });
	    $(textinput).on('input', function (e) { pointer.setNotGraded(); });
		
		if (previewMode)
			this.addEventScreen(false);
		
	};
	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var actions = [];
		var $div = $(this.getDivWrap());
		if ($div.attr('value')) {
			var sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('UpdateTextField');
			sai.setInput($div.attr('value'));
			actions.push(sai);
		}
	    return actions;
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
	    //console.trace(this.getName(),'setText',aText);

 		pointer.assignText(aText);
 		textinput.value=aText;

 		// this.redraw (); // Very very experimental
	};

	this.getValue = function () {
		return textinput.value;
	};

	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		textinput.value="";
	};

	this.setStyleHandler("DrawBorder", null);
};

CTATTextInput.prototype = Object.create(CTATTextBasedComponent.prototype);
CTATTextInput.prototype.constructor = CTATTextInput;

CTAT.ComponentRegistry.addComponentType('CTATTextInput', CTATTextInput);