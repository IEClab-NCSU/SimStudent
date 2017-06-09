/**
 * @fileoverview Defines a base class for CTAT components that adds methods for
 * updating and executing SAI's on the component.  SAI's are the general
 * mechanism used to communicate in CTAT and stands for
 * Selection, Action, Input.
 *
 * @author $Author: sewall $
 * @version $Revision: 24430 $
 */
goog.provide('CTAT.Component.Base.SAIHandler');

goog.require('CTAT.Component.Base.Graphical');
goog.require('CTATGlobals');
goog.require('CTATMessage');
goog.require('CTATSAI');

/**
 * A class that handles SAI's.
 * @class
 * @classdesc An intermediate class in the CTAT component hierarchy that stores,
 * maintains, and otherwise handles CTATSAI's for the given component.
 * @extends CTATCompBase
 */
CTAT.Component.Base.SAIHandler = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.Graphical.call(this, aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var _sai = new CTATSAI (this.getName(),
			CTAT.Component.Base.SAIHandler.DefaultAction,
			CTAT.Component.Base.SAIHandler.DefaultInput,
			CTAT.Component.Base.SAIHandler.DefaultPrompt);

	var super_setName = this.setName;
	this.setName = function (newName) {
		super_setName(newName);
		_sai.setSelection(this.getName());
	};
	/**
	 * Returns the current SAI for the component.
	 * @returns {CTATSAI} the Selection-Action-Input for the last action.
	 */
	this.getSAI = function() {
		return _sai;
	};
	/**
	 * Set the Selection, Action, and Input for the current SAI.
	 * @param {string|CTATSAI} aSelection
	 *  	If a CTATSAI, set the component's sai to aSelection.
	 *  	If anything else, it is ignored and replaced with this.getName().
	 *  @param {string=} anAction	the action (eg) the name of a method of the component.
	 *  @param {string=} anInput	the input (eg) the parameter of the action.
	 */
	this.setSAI = function(aSelection,anAction,anInput) {
		if (aSelection instanceof CTATSAI) {
			_sai = aSelection;
		} else {
			aSelection = aSelection===null||aSelection===undefined?this.getName():aSelection;
			_sai = new CTATSAI (aSelection, anAction, anInput,
					CTAT.Component.Base.SAIHandler.DefaultPrompt);
		}
		return this;
	};
	/**
	 * Sets the input of the current SAI.
	 * @see {@link CTAT.Component.Base.SAIHandler#setSAI}
	 * @param {String} anInput	the input.
	 * @return {CTAT.Component.Base.SAIHandler}
	 */
	this.setInput = function(anInput) {
		if (_sai instanceof CTATSAI)
			_sai.setInput(anInput);
		else
			_sai = new CTATSAI(this.getName(),
					CTAT.Component.Base.SAIHandler.DefaultAction,
					anInput, CTAT.Component.Base.SAIHandler.DefaultPrompt);
		return this;
	};
	/**
	 * Sets the action of the current SAI.
	 * @see {@link CTAT.Component.Base.SAIHandler#setSAI}
	 * @param {String} anAction 	The action (eg) the name of the method.
	 */
	this.setAction = function(anAction) {
		if (_sai instanceof CTATSAI)
			_sai.setAction(anAction);
		else
			_sai = new CTATSAI(this.getName(), anAction,
					CTAT.Component.Base.SAIHandler.DefaultInput,
					CTAT.Component.Base.SAIHandler.DefaultPrompt);
		return this;
	};
	/**
	 * Convenience function for setting both the action and the input.
	 * @see {@link CTAT.Component.Base.SAIHandler#setAction} and {@link CTAT.Component.Base.SAIHandler#setInput}
	 */
	this.setActionInput = function(anAction,anInput) {
		this.setAction(anAction);
		this.setInput(anInput);
		return this;
	};
	/**
	 * Sets the selection of the current SAI.
	 * @see {@link CTAT.Component.Base.SAIHandler#setSAI}
	 * @param {String} aSelection 	The name of the component (or group if a radio button or check box).
	 */
	this.setSelection = function(aSelection) {
		if (_sai instanceof CTATSAI)
			_sai.setSelection(aSelection);
		else
			_sai = new CTATSAI(aSelection,
					CTAT.Component.Base.SAIHandler.DefaultAction,
					CTAT.Component.Base.SAIHandler.DefaultInput,
					CTAT.Component.Base.SAIHandler.DefaultPrompt);
	};
	/**
	 * Called before grading for components that do not maintain state on
	 * every user action.  This should be overwritten in components that need
	 * it (eg) Text based, check boxes, radio buttons, ...
	 */
	this.updateSAI = function() {
		return;
	};

	/**
	 * Takes a SAI and executes it on the component.
	 * @param {CTATSAI|CTATMessage} aSAI 	A Selection-Action-Input to be executed.
	 * @returns {Boolean} true if the execution succeeded,
	 * 	false if it failed for any reason
	 */
	this.executeSAI = function(aSAI)
	{
		this.ctatdebug("executeSAI ()");

		var sai=null;

		if (aSAI instanceof CTATMessage)
		{
			sai = aSAI.getSAI();
		}
		else
		{
			sai = aSAI;
		}

		if (sai instanceof CTATSAI)
		{
			//TODO: Check if right component? probably redundant and unnecessary
			var action = sai.getAction();

			this.ctatdebug("Processing "+action+"("+sai.getInput ()+") on: " + sai.getSelection ());

			if (typeof(this[action]) == 'function')
			{
				var args = sai.getArgumentsTyped();

				this.ctatdebug ("JSON args: " + JSON.stringify(args));

				//this.ctatdebug("Trying to execute: "+action+" ("+args+") on: " + sai.getSelection ());

				try
				{
					this.ctatdebug("Executing "+action+"("+args+","+typeof aSAI+")...");
					args.push(aSAI);
					//console.log(this.getDivWrap().id,'executeSAI',action, args);
					this[action].apply(this,args); // need to use apply because args is an array.
					//this[action](args,aSAI);
				}
				catch(err)
				{
					//this.ctatdebug('ERROR: failed to execute action '+sai.toLSxmlString()+'\n'+err.message);
					this.ctatdebug('ERROR: failed to execute action: '+err.message);

					return false;
				}

				if (this.component)
				{
					var SAI_event = new CustomEvent("CTAT_EXECUTE_SAI",
							{detail: {sai: aSAI, component: this},
							 bubbles: true, cancelable: true});
					this.component.dispatchEvent(SAI_event);
				}

				return true;
			}
			else
			{
				this.ctatdebug("ERROR: Unsupported action: "+action+" from "+sai.toLSxmlString());
				return false;
			}
		}
		else
		{
			this.ctatdebug("ERROR: Non-SAI sent to executeSAI("+typeof(aSAI)+")");
			return false;
		}
	};
};
CTAT.Component.Base.SAIHandler.DefaultAction = "ButtonPressed";
CTAT.Component.Base.SAIHandler.DefaultInput = '-1';
CTAT.Component.Base.SAIHandler.DefaultPrompt = '';

CTAT.Component.Base.SAIHandler.prototype = Object.create(CTAT.Component.Base.Graphical.prototype);
CTAT.Component.Base.SAIHandler.prototype.constructor = CTAT.Component.Base.SAIHandler;
