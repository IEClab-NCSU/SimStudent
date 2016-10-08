/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSubmitButton.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATSubmitButton');

goog.require('CTATButtonBasedComponent');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');

CTATSubmitButton = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight){
	CTATButtonBasedComponent.call(this,
			"CTATSubmitButton",
			"aSubmitButton",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer=this;
	var borderRoundness=5;
	var buttonText="";
	var targets;
	var status;

	this.ctatdebug ("CTATSubmitButton" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	this.init=function init()
	{
		pointer.ctatdebug("init (" + pointer.getName() + ")");
		pointer.setSAI ("this","ButtonPressed","-1");

		pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");
		pointer.render ();

		var button=document.createElement('button');
		button.value=pointer.getName();
		button.name=pointer.getName(); // might be wrong
		button.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
		button.setAttribute('onkeypress', 'return noenter(event)');

		pointer.setInitialized(true);

		pointer.setComponent(button);
		pointer.addComponentReference(pointer, button);
		pointer.getDivWrap().appendChild(button);

		//useDebgging=true;
		pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
		//useDebugging=false;

		//currentZIndex++;
		//currentIDIndex++;

		pointer.addSafeEventListener ('click',pointer.processClick,button);
		pointer.addSafeEventListener ('focus', pointer.processFocus,button);

		pointer.readTargets();
	};

	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		pointer.setEnabled(true);
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

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if (aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}

			if(aStyle.styleName=="borderRoundness")
			{
				borderRoundness=aStyle.styleValue;
				pointer.addCSSAttribute("border-radius", borderRoundness+"px");
			}
		}
	};

	/**
	 * TPA
	 */
	this.ButtonPressed=function ButtonPressed ()
	{
		pointer.ctatdebug ("ButtonPressed ()");
	};

	this.readTargets = function(){
		//get target components
		if(pointer.getGrDescription() == null){
			pointer.ctatdebug ("Error: no deserialized component description available");
			return;
		}
		pointer.parameters = pointer.getGrDescription().params;
		if(!pointer.parameters) return;

		for(var i = 0; i < pointer.parameters.length; i++){
			var aParam = pointer.parameters[i];
			if(aParam.paramName == 'target_components'){
				targets = aParam.paramValue.split(";");
			}
		}
	}

	this.getStatus = function(){
		return status;
	}
	this.getTargets = function(){
		return targets;
	}

	this.oldShowCorrect = pointer.showCorrect;
	this.showCorrect = function(aMessage){
		if(status === false) return;
		status = true;
		pointer.oldShowCorrect(aMessage);
	}

	this.oldShowInCorrect = pointer.showInCorrect;
	this.showInCorrect = function(aMessage){
		status = false;
		this.oldShowInCorrect(aMessage);
	}
}
//TODO: missing prototype inheritance?

CTAT.ComponentRegistry.addComponentType('CTATSubmitButton', CTATSubmitButton);