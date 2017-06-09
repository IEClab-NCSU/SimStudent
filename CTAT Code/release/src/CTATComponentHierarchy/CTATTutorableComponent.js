/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2016-11-08 12:58:28 -0600 (週二, 08 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponentHierarchy/CTATTutorableComponent.js $
 $Revision: 24364 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTAT.Component.Base.Tutorable');

goog.require('CTAT.Component.Base.SAIHandler');
goog.require('CTATCommShell');
goog.require('CTATGlobalFunctions');
//goog.require('CTATGlobals');

/**
 *
 */
CTAT.Component.Base.Tutorable = function(aClassName,
								 aName,
					 			 aDescription,
					 			 aX,
					 			 aY,
					 			 aWidth,
					 			 aHeight) {
	CTAT.Component.Base.SAIHandler.call(this,
								aClassName,
								aName,
								aDescription,
								aX,
								aY,
								aWidth,
								aHeight);

	var defaultTutorMe=true;
	var defaultRecordMe=true;
	var showFeedback=true;

	var _tutorComponent=CTAT.Component.Base.Tutorable.Options.TutorComponent.TUTOR;
	var disableOnCorrect=true;
	var pointer=this;

	var _highlighted = false;

	/**************** Tutor Component ****************/
	/**
	 * Set the type of tutoring to perform on the component.
	 * @param {String} theValue	One of the values in CTAT.Component.Base.Tutorable.Options.TutorComponent.
	 */
	this.setTutorComponent=function setTutorComponent (theValue)
	{
		_tutorComponent=theValue;
		var opt = CTAT.Component.Base.Tutorable.Options.TutorComponent;

		switch (_tutorComponent) {
		case opt.TUTOR :
			defaultTutorMe=defaultRecordMe=true;
			showFeedback=true;
			break;
		case opt.TUTOR_NO_FEEDBACK :
			defaultTutorMe=defaultRecordMe=true;
			showFeedback=false;
			break;
		case opt.DO_NOT_TUTOR :
			defaultTutorMe=false;
			defaultRecordMe=true;
			break;
		}
	};
	this.setParameterHandler('tutorComponent',this.setTutorComponent);
	this.data_ctat_handlers['tutor'] = function (val) { defaultTutorMe = CTATGlobalFunctions.toBoolean(val); };
	this.data_ctat_handlers['show-feedback'] = function (val) { showFeedback = CTATGlobalFunctions.toBoolean(val); };

	/**
	 * Return the type of tutoring.
	 * @returns {String} one of the options in CTAT.Component.Base.Tutorable.Options.TutorComponent.
	 */
	this.getTutorComponent=function getTutorComponent ()
	{
		var opt = CTAT.Component.Base.Tutorable.Options.TutorComponent;
		if (defaultTutorMe && defaultRecordMe && showFeedback) {
			return opt.TUTOR;
		} else if (defaultTutorMe && defaultRecordMe && !showFeedback) {
			return opt.TUTOR_NO_FEEDBACK;
		} else if (!defaultTutorMe && defaultRecordMe) {
			return opt.DO_NOT_TUTOR;
		} else { // fall back to whatever is the stored setting
			return (_tutorComponent);
		}
	};

	/*********** Disable on Correct ************/
	/**
	 * Get the disable on correct parameter.
	 * @returns {Boolean} true if the component should get disabled if it is
	 *  marked as correct.
	 */
	this.getDisableOnCorrect = function() {
		var doc = $(this.getDivWrap()).attr('data-ctat-disable-on-correct');
		if (doc)
			return CTATGlobalFunctions.toBoolean(doc);
		return true;
		//return disableOnCorrect;
	};
	/**
	 * Sets the disable on correct parameter.
	 * @param p_disable: some true value (true, "true", 1, etc.).
	 */
	this.setDisableOnCorrect = function(p_disable) {
		$(this.getDivWrap()).attr('data-ctat-disable-on-correct',p_disable);
		//disableOnCorrect = CTATGlobalFunctions.toBoolean(p_disable);
	};
	this.setParameterHandler('DisableOnCorrect', this.setDisableOnCorrect);
	//this.data_ctat_handlers['disable-on-correct'] = function (val) { disableOnCorrect = CTATGlobalFunctions.toBoolean(val); };

	/************ Show Hint Highlight ***********/
	var showHintHighlight = true; // TODO: rename so there is no confusion with this.showHintHightlight
	this.getShowHintHighlight=function(){
		return showHintHighlight;
	};
	this.setShowHintHighlight=function(p_show){
		showHintHighlight = CTATGlobalFunctions.toBoolean(p_show);
	};
	this.setParameterHandler('ShowHintHighlight',this.setShowHintHighlight);
	this.data_ctat_handlers['show-hint-highlight'] = function (val) { showHintHighlight = CTATGlobalFunctions.toBoolean(val); };

	/******** Grading Status ***********/
	var componentStatus = CTAT.Component.Base.Tutorable.Options.Status.NOTGRADED;
	this.getComponentStatus = function () { return componentStatus; };
	this.setComponentStatus = function (status) {
		// TODO: add storage of the status to the node so that it might be noticed by screen readers
		componentStatus = status;
	};
	this.isCorrect = function() { return componentStatus === CTAT.Component.Base.Tutorable.Options.Status.CORRECT; };
	this.isIncorrect = function() { return componentStatus === CTAT.Component.Base.Tutorable.Options.Status.INCORRECT; };
	this.isNotGraded = function() { return componentStatus === CTAT.Component.Base.Tutorable.Options.Status.NOTGRADED; };

	/**
	*
	*/
	this.setCorrect = function(aSAI) {
		//this.ctatdebug('setCorrect('+aSAI?[aSAI.getSelection(),aSAI.getAction(),aSAI.getInput()].join(','):''+')');
		this.setNotGraded();

		// The check below needs to be handled in a more general way for components that should
		// not change their content on a correct or incorrect
		if (this.getClassName ()!="CTATTableGoogle")
		{
			this.executeSAI(aSAI); // necessary for replace and collaboration to work
		}

		//this.ctatdebug('setCorrect() post executeSAI');
		componentStatus = CTAT.Component.Base.Tutorable.Options.Status.CORRECT;
		if(this.getDisableOnCorrect()===true) { // in old AS3 code, locking was independent of showing feedback
			this.setEnabled (false);
		}
		//this.setHintHighlight(false);
		if (CTATGlobals.suppressStudentFeedback===false && showFeedback) {
			this.showCorrect(aSAI);
		}
		this.ctatdebug('setCorrect() post showCorrect');
		//ctatdebug('trying to create correct event');
		//ctatdebug(this.getComponent());
		if (this.component) { // TODO: check for google environment?
			//ctatdebug('trying to trigger correct event');
			var correct_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.correct,
					{detail: {sai: aSAI, component:this}, bubbles:true, cancelable: true});
			//document.dispatchEvent(correct_event); // change to document because in Firefox if the component is locked, it will not fire the event
			this.getDivWrap().dispatchEvent(correct_event);// change to div because in Firefox if the component is locked, it will not fire the event
			//ctatdebug('correct dispatched');
		} /*else {
			//document.dispatchEvent(correct_event); // in case of abstract component
			//this.ctatdebug('setCorrect() no component for event');
		}*/
		//this.ctatdebug('setCorrect() post dispatchEvent');
	};
	/**
	*
	*/
	this.setIncorrect = function(aSAI)
	{
		this.ctatdebug('setIncorrect()');

		this.setNotGraded();

		// The check below needs to be handled in a more general way for components that should
		// not change their content on a correct or incorrect
		if (this.getClassName ()!="CTATTableGoogle")
		{
			this.executeSAI(aSAI);
		}

		componentStatus = CTAT.Component.Base.Tutorable.Options.Status.INCORRECT;

		if (CTATGlobals.suppressStudentFeedback===false && showFeedback) {
			this.showInCorrect(aSAI);
		}

		if (this.component) { // TODO: check for google environment?
			var incorrect_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.incorrect,
					{detail: {sai: aSAI, component:this}, bubbles:true, cancelable:true});
			this.getDivWrap().dispatchEvent(incorrect_event);
			//ctatdebug('incorrect dispatched');
		}
	};
	/**
	*
	*/
	this.setNotGraded = function()
	{
		this.ctatdebug("setNotGraded ()");

		this.setHintHighlight(false); // already had condition logic
		if (!this.isNotGraded()) { // only modify if state is changing to reduce dom modifications.
			componentStatus = CTAT.Component.Base.Tutorable.Options.Status.NOTGRADED;
			this.removeCorrect();
			this.removeInCorrect();
			if (this.component) { // TODO: check for google environment?
				var notGraded_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.ungrade,
						{detail: {component: this}, bubbles: true, cancelable: true});
				this.getDivWrap().dispatchEvent(notGraded_event);
			} /*else
				document.dispatchEvent(notGraded_event); // try removing for google? */
		}
	};
	/******** Show Grading *************/
	/*** Hint Highlighting ***/
	this.showHintHighlight = function(p_show,aSAI) {
		//ctatdebug(this.getName()+".showHintHighlight("+p_show+")");
		this.component.classList.remove('CTAT--correct');
		this.component.classList.remove('CTAT--incorrect');
		if (p_show) {
			this.component.classList.add('CTAT--hint');
		} else {
			this.component.classList.remove('CTAT--hint');
		}
	};
	/**
	 * This method should not be clobbered as it handles the logic of highlighting.
	 * To change how highlights are drawn, clobber showHintHighlight()
	 */
	this.setHintHighlight=function setHintHighlight(newValue,aSAI)
	{
		this.ctatdebug("setHintHighlight (" + newValue + ")");

		/*
		if (newValue==false)
		{
			//this.removeInCorrect ();
		}
		else
		{
			CTATGlobals.Tab.previousFocus=CTATGlobals.Tab.Focus;
			CTATGlobals.Tab.Focus=pointer;
			if (pointer.component.focus) // check if focus exists because it does not for svg tags in Firefox
				CTATGlobals.Tab.Focus.getComponent().focus();
			else // alternate is to try the div wrapper, this might have to change if we go to less encapsulated components.
				CTATGlobals.Tab.Focus.getDivWrap().focus();
		}
		*/

		if(showHintHighlight) {
			var highlight = CTATGlobalFunctions.toBoolean(newValue);
			if (this.component) { // TODO: check for google environment?
				var hint_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.highlight,
						{detail: {isHighlighted: highlight, component: this},
						 bubbles: true, cancelable: true});
				this.getDivWrap().dispatchEvent(hint_event);
			}/* else
				document.dispatchEvent(hint_event);*/
			if (highlight!==_highlighted) { // only draw on a change to reduce the number of dom edits.
				_highlighted = highlight;
				this.showHintHighlight(highlight,aSAI);
			}
		}
	};
	/**
	 * This method should not be clobbered as it handles the logic of highlighting.
	 * To change how highlights are drawn, clobber showHintHighlight()
	 */
	this.moveHintHighlight=function moveHintHighlight(newValue,aSAI)
	{
		this.ctatdebug("moveHintHighlight (" + newValue + ")");

		CTATGlobals.Tab.previousFocus=CTATGlobals.Tab.Focus;
		CTATGlobals.Tab.Focus=pointer;
		if (pointer.component.focus) // check if focus exists because it does not for svg tags in Firefox
			CTATGlobals.Tab.Focus.getComponent().focus();
		else // alternate is to try the div wrapper, this might have to change if we go to less encapsulated components.
			CTATGlobals.Tab.Focus.getDivWrap().focus();

		if(showHintHighlight)
		{
			var highlight = CTATGlobalFunctions.toBoolean(newValue);
			if (this.component) { // TODO: check for google environment?
				var hint_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.highlight,
						{detail: {isHighlighted: highlight, component: this},
						 bubbles: true, cancelable: true});
				this.getDivWrap().dispatchEvent(hint_event);
			}/* else
				document.dispatchEvent(hint_event);*/
			if (highlight!==_highlighted) { // only draw on a change to reduce the number of dom edits.
				_highlighted = highlight;
				this.showHintHighlight(highlight,aSAI);
			}
		}
	};
	/**
	 * @function highlight
	 * An Interface Action for highlighting the component.
	 * @see CTATCompBase.setHingHighlight
	 */
	//this.highlight = this.setHintHighlight.bind(this,true);
	this.highlight = function highlight(dummy)
	{
		ctatdebug ("highlight ()");

		this.component.classList.add('CTAT--highlight');
	};
	/**
	 * @function unhighlight
	 * An Interface Action for removing highlighting.
	 * @see CTATCompBase.setHingHighlight
	 */
	//this.unhighlight = this.setHintHighlight.bind(this,false);
	this.unhighlight = function unhighlight(dummy)
	{
		ctatdebug ("unhighlight ()");

		this.component.classList.remove('CTAT--highlight');
	};

	/**
	 *  Ported from AS3
	 *
	 * Does not work correctly for IE because disabled components have their own font color
	 */
	this.showCorrect=function (aMessage)
	{
		this.ctatdebug("showCorrect("+aMessage+")");
		//ctatdebug("showCorrect",aMessage);
		this.getComponent().classList.remove('CTAT--incorrect');
		this.getComponent().classList.remove('CTAT--hint');
		this.getComponent().classList.add('CTAT--correct');
	};
	this.removeCorrect=function() {
		if (this.getComponent())
			this.getComponent().classList.remove('CTAT--correct');
	};
	/**
	 * Ported from AS3
	 */
	this.showInCorrect=function showInCorrect(aMessage)
	{
		this.ctatdebug("showInCorrect("+aMessage+")");
		//ctatdebug("showInCorrect",aMessage.toString());

		this.getComponent().classList.remove('CTAT--correct');
		this.getComponent().classList.remove('CTAT--hint');
		this.getComponent().classList.add('CTAT--incorrect');
	};
	this.removeInCorrect = function() {
		//this.modifyCSSAttribute("color", this.getFontColor());
		if (this.getComponent())
			this.getComponent().classList.remove('CTAT--incorrect');
	};

	this.resetTutoring = function() {
		this.showHintHighlight(false);
		this.removeCorrect();
		this.removeInCorrect();
		componentStatus = CTAT.Component.Base.Tutorable.Options.Status.NOTGRADED;
	};

	/**
	 *
	 */
	this.grade = function () {
		this.updateSAI();
		this.processAction(true);
	};

	/**
	*
	*/
	this.processAction = function(force_grade, force_record)
	{
		this.ctatdebug('processAction('+force_grade+','+force_record+')');

		pointer.unHighlightAll ();

		var doneButton = CTATShellTools.findComponentByClass('CTATDoneButton'); // TODO: extend to handle all done buttons

		if (doneButton && doneButton instanceof CTAT.Component.Base.Tutorable && doneButton!=this) {
			//ctatdebug('set done button to not graded');
			doneButton.setNotGraded();
		}

		this.ctatdebug('processAction() finished checking doneButton');

		// Clear hint window, as per trac ticket #745
		ctatdebug ("Clearing hint window ...");
		
		if (CTATCommShell.commShell)
		{
			//CTATCommShell.commShell.showFeedback (" ");
			CTATCommShell.commShell.clearFeedbackComponents ();
		}

		force_grade = force_grade===undefined || force_grade===null?false:force_grade;
		force_record = force_record===undefined || force_record===null?false:force_record;
		//componentStatus = CTAT.Component.Base.Tutorable.Options.Status.NOTGRADED;
		if (this.getComponentGroup()!=='') {
			var group = CTATShellTools.findComponent(this.getComponentGroup());
			group.forEach(function(g) { g.setNotGraded(); });
		} else {
			this.setNotGraded();
		}
		this.ctatdebug('processAction() finished setNotGraded');

		//ctatdebug(this.getSAI().toLSxmlString());
		//console.trace(this.getSAI().toLSxmlString());
		//ctatdebug('processAction',force_grade,force_record,commShell);
		if (CTATCommShell.commShell) {
			if (force_grade)
				CTATCommShell.commShell.processComponentAction(this.getSAI(),true,true);
			else if (force_record)
				CTATCommShell.commShell.processComponentAction(this.getSAI(),false,true);
			else
				CTATCommShell.commShell.processComponentAction(this.getSAI(),defaultTutorMe,defaultRecordMe);
		}
		this.ctatdebug('processAction() finished call to commShell.processComponentAction');

		// FIXME: Should updateSAI be called in processAction? No
		if (this.component) { // TODO: check for google environment?
			var SAI_event = new CustomEvent(CTAT.Component.Base.Tutorable.EventType.action,
					{detail: {sai: this.getSAI(), component: this},
					 bubbles: true, cancelable: true});
			this.component.dispatchEvent(SAI_event);
		} /*else
			document.dispatchEvent(SAI_event);*/
	};
};

CTAT.Component.Base.Tutorable.Options = {
		TutorComponent: {
			TUTOR: "Tutor",
			TUTOR_NO_FEEDBACK:"Tutor but no visual feedback",
			DO_NOT_TUTOR:"Do not tutor"
		},
		Status: {
			CORRECT: "CORRECT",
			INCORRECT: "INCORRECT",
			NOTGRADED: "NOTGRADED"
		}
};

CTAT.Component.Base.Tutorable.EventType = {
		correct: 'CTAT_CORRECT',
		incorrect: 'CTAT_INCORRECT',
		highlight: 'CTAT_HIGHLIGHT',
		ungrade: 'CTAT_NOTGRADED',
		action: 'CTAT_ACTION'
};

CTAT.Component.Base.Tutorable.prototype = Object.create(CTAT.Component.Base.SAIHandler.prototype);
CTAT.Component.Base.Tutorable.prototype.constructor = CTAT.Component.Base.Tutorable;
