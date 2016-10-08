/**
 * @fileoverview Defines a base class for CTAT components that adds methods for
 * updating and executing SAI's on the component.  SAI's are the general
 * mechanism used to communicate in CTAT and stands for
 * Selection, Action, Input.
 *
 * @author $Author: vvelsen $
 * @version $Revision: 21845 $
 */
goog.provide('CTAT.Component.Hierarchy.SAIHandler');

goog.require('CTATCompBase');
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
CTAT.Component.Hierarchy.SAIHandler = function(aClassName,
					   aName,
					   aDescription,
					   aX,
					   aY,
					   aWidth,
					   aHeight) {
	CTATCompBase.call(this, aClassName,
					   aName,
					   aDescription,
					   aX,
					   aY,
					   aWidth,
					   aHeight);

	// set a default SAI.
	var _default_action = CTAT.Component.Hierarchy.SAIHandler.DefaultAction;
	var _default_input = CTAT.Component.Hierarchy.SAIHandler.DefaultInput;
	var _default_prompt = CTAT.Component.Hierarchy.SAIHandler.DefaultPrompt;

	var _sai = new CTATSAI (aName,_default_action,_default_input,_default_prompt);

	/**
	 * Returns the current SAI for the component.
	 * @returns {CTATSAI} the Selection-Action-Input for the last action.
	 */
	this.getSAI = function() {
		_sai.setSelection(this.getName());
		return (_sai);
	};
	/**
	 * Set the Selection, Action, and Input for the current SAI.
	 * @param {String|CTATSAI} aSelection
	 *  	If a CTATSAI, set the component's sai to aSelection.
	 *  	If anything else, it is ignored and replaced with this.getName().
	 *  @param {String=} anAction	the action (eg) the name of a method of the component.
	 *  @param {String=} anInput	the input (eg) the parameter of the action.
	 */
	this.setSAI = function(aSelection,anAction,anInput) {
		if (aSelection instanceof CTATSAI) {
			_sai = aSelection;
		} else {
			// override selection as it gets overriddeon on output anyway
			_sai = new CTATSAI (this.getName(),anAction,anInput,_default_prompt);
		}
	};
	/**
	 * Sets the input of the current SAI.
	 * @see {@link CTAT.Component.Hierarchy.SAIHandler#setSAI}
	 * @param {String} anInput	the input.
	 */
	this.setInput = function(anInput) {
		if (_sai instanceof CTATSAI)
			_sai.setInput(anInput);
		else
			_sai = new CTATSAI(this.getName(),_default_action,anInput,_default_prompt);
	};
	/**
	 * Sets the action of the current SAI.
	 * @see {@link CTAT.Component.Hierarchy.SAIHandler#setSAI}
	 * @param {String} anAction 	The action (eg) the name of the method.
	 */
	this.setAction = function(anAction) {
		if (_sai instanceof CTATSAI)
			_sai.setAction(anAction);
		else
			_sai = new CTATSAI(this.getName(),anAction,_default_input,_default_prompt);
	};
	/**
	 * Convenience function for setting both the action and the input.
	 * @see {@link CTAT.Component.Hierarchy.SAIHandler#setAction} and {@link CTAT.Component.Hierarchy.SAIHandler#setInput}
	 */
	this.setActionInput = function(anAction,anInput) {
		this.setAction(anAction);
		this.setInput(anInput);
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
		
		if (sai instanceof CTATSAI) {
			//TODO: Check if right component? probably redundant and unnecessary
			var action = sai.getAction();
			
			this.ctatdebug("Processing "+action+"("+sai.getInput ()+") on: " + sai.getSelection ());
			
			if (typeof(this[action]) == 'function') 
			{
				var args = sai.getArgumentsTyped();
				
				this.ctatdebug("Trying to execute: "+action+" ("+args+") on: " + sai.getSelection ());
				
				try 
				{
					//this[action].apply(this,args);
					
					this [action](args,aSAI);
				} 
				catch(err) 
				{
					//this.ctatdebug('ERROR: failed to execute action '+sai.toLSxmlString()+'\n'+err.message);
					this.ctatdebug('ERROR: failed to execute action: '+err.message);
					return false;
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

	/**
	*
	*/
	this.grade = function () {
		if (commShell)
			commShell.gradeComponent(this);
	};
};
CTAT.Component.Hierarchy.SAIHandler.DefaultAction = "ButtonPressed";
CTAT.Component.Hierarchy.SAIHandler.DefaultInput = '-1';
CTAT.Component.Hierarchy.SAIHandler.DefaultPrompt = '';

CTAT.Component.Hierarchy.SAIHandler.prototype = Object.create(CTATCompBase.prototype);
CTAT.Component.Hierarchy.SAIHandler.prototype.constructor = CTAT.Component.Hierarchy.SAIHandler;
