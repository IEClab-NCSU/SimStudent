/**
 * @fileoverview CTATSubmitButton is for implementing grading on demand features
 * in CTAT.  It is a CTATButton that calls grade on a list of associated
 * components.
 *
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/*
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSubmitButton.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATSubmitButton');

goog.require('CTATButton');
goog.require('CTAT.Component.Base.Tutorable');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSAI');

CTATSubmitButton = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight){
	CTATButton.call(this,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	this.setClassName("CTATSubmitButton");
	var previewMode = CTATConfiguration.get('previewMode');
	var pointer=this;
	this.setParameterHandler('target_components',function(slist){
		this.getDivWrap().setAttribute('data-ctat-target',slist);
		return this;
	});
	//this.data_ctat_handlers['target'] = function (val) {
	//	targets = {};
	//	var a_targets = String(val).split(/\s*[;,]\s*/);
	//	a_targets.forEach(function(target) { targets[target] = 1; });
	//};

	var super_init = this.init;
	this.init = function() {
		super_init();
		// force this component to DO_NOT_TUTOR so custom grading based on children can be done.
		this.setParameter('tutorComponent',CTAT.Component.Base.Tutorable.Options.DO_NOT_TUTOR);
	    this.component.addEventListener('focus', this.processFocus);
	};

	/**
	 * Tests if the given component is in the list of targets.
	 * @param aComponent
	 * @returns {Boolean} true if it is in the list.
	 */
	var isTarget = function(aComponent) {
		var targets = pointer.getTargets();
		if (Object.keys(targets).length === 0) {
			return false; // shortcut if there are no specified targets
		} else if (aComponent === pointer) {
			return false; // do not self reference
		} else if (aComponent instanceof CTAT.Component.Base.Tutorable) {
			// check for component name and group name as authors might use either.
			//console.log('Submit Checking CTATComponent');
			return (targets.hasOwnProperty(aComponent.getName()) ||
					targets.hasOwnProperty(aComponent.getComponentGroup()));
		} else if (aComponent instanceof CTATSAI) {
			return aComponent.getSelection() != pointer.getName() &&
			(targets.hasOwnProperty(aComponent.getSelection()));
		} else if (aComponent instanceof String) {
			return aComponent!=pointer.getName() && (targets.hasOwnProperty(aComponent));
		} else if (aComponent instanceof Element) {
			return aComponent!=pointer.getComponent() &&
			(targets.hasOwnProperty(aComponent.id) ||
					targets.hasOwnProperty(aComponent.getAttribute('name')));
		} else { // null, undefined, etc.
			return false;
		}
	};

	var event_type = CTAT.Component.Base.Tutorable.EventType;
	document.addEventListener(event_type.correct, function(e) {
		var sai = e.detail.sai;
		if (sai && isTarget(e.detail.component)) {
			if (pointer.isNotGraded())
				// if already correct, don't need to set it again.
				// If incorrect, don't clobber it
				pointer.setCorrect();
		}
	}, false);
	document.addEventListener(event_type.incorrect, function(e) {
		var sai = e.detail.sai;
		if (sai && isTarget(e.detail.component)) {
			pointer.setIncorrect();
			pointer.setEnabled(true); // in case a previous correct locked this
		}
	}, false);
	document.addEventListener(event_type.ungrade, function(e) {
		var comp = e.detail.component;
		if (comp && isTarget(comp)) {
			pointer.setNotGraded();
		}
	}, false);
	document.addEventListener(event_type.highlight, function(e) {
		var comp = e.detail.component;
		if (comp && isTarget(comp)) {
			pointer.setHintHighlight(e.detail.isHighlighted);
		}
	}, false);
	/**
	 * Returns an object of target names.
	 * @returns {Object} a map of component names.
	 */
	this.getTargets = function(){
		var targets = {};
		var div_wrap = this.getDivWrap();
		var target_string = div_wrap.getAttribute('data-ctat-target');
		if (target_string) {
			String(target_string).split(/\s*[;,]\s*/).forEach(t=>targets[t.trim()]=1);
		}
		return targets;
	};
	this.processClick = function(e) {
		if (pointer.getEnabled()===true) {
			var targets = pointer.getTargets();
			for (var targetName in targets) {
				var targetArr = CTATShellTools.findComponent(targetName);
				if (targetArr!==null && targetArr.length>0) {
					//console.log('Grading '+targetArr[0].getName());
					var target = targetArr[0];  // if grouped component, calling grade on just one should work
					if (target instanceof CTAT.Component.Base.Tutorable) {
						if (target.getEnabled()) {
							// do not resubmit entries that users can not modify
							// (usually because they are correct)
							target.grade(pointer);
						}
					} else
						pointer.ctatdebug('Invalid target component: '+target);
				}
			}
		}
	};
};
CTATSubmitButton.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATSubmitButton.prototype.constructor = CTATSubmitButton;
CTAT.ComponentRegistry.addComponentType('CTATSubmitButton', CTATSubmitButton);
