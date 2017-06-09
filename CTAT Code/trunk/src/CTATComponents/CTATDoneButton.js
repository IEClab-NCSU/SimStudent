/**
 * @fileoverview CTAT's default done button component.  Uses SVG to render the
 * button.  In this implementation, the image should scale to the specified
 * dimensions.
 * @author $Author: mringenb $
 * @version $Revision: 23782 $
 */
goog.provide('CTATDoneButton');

goog.require('CTATCommShell');
goog.require('CTAT.Component.Base.Clickable');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATLanguageManager');

/**
 * Creates a done button
 * @param {CTATComponentDescription} aDescription
 * @param {Number} aX
 * @param {Number} aY
 * @param {Number} aWidth
 * @param {Number} aHeight
 * @returns
 * @constructor
 * @extends {CTAT.Component.Base.SVGButton}
 */
CTATDoneButton = function(aDescription,aX,aY,aWidth,aHeight) {
	CTAT.Component.Base.Clickable.call(this,'done','CTATDoneButton',aDescription,aX,aY,aWidth,aHeight);
	this.setName('done');
	this.setClassName('CTATDoneButton');
	this.setText(CTATGlobals.languageManager.getString ("DONE")); // add default label, override in BRD by setting labelText style property

	this.setStyleHandler('BackgroundColor',null); // do not respect SUI background setting
	this.setStyleHandler('TextAlign',null); // do not respect SUI text alignment setting
	var pointer = this;

	this.init = function() {
		this.setInitialized(true);
		var comp = document.createElement('button');
		comp.classList.add('unselectable');
		comp.classList.add('CTAT-done-button');
		this.setComponent(comp);
		var button_content = document.createElement('div');
		button_content.classList.add('CTAT-done-button--content');
		comp.appendChild(button_content);
		var checkmark = document.createElement('div');
		checkmark.textContent = '\u2714'; //#\u2713 = checkmark, \u2714 = heavy checkmark
		checkmark.classList.add('CTAT-done-button--icon');

		button_content.appendChild(checkmark);
		if (this.getText()) {
			var button_text = document.createElement('div');
			button_text.classList.add('CTAT-done-button--text');
			button_text.textContent = this.getText();
			button_content.appendChild(button_text);
		}
		this.getDivWrap().appendChild(comp);
		comp.addEventListener('mouseenter',function(e) {
			e.target.classList.add('CTAT-done-button--hover');
		});
		comp.addEventListener('mouseleave',function(e) {
			e.target.classList.remove('CTAT-done-button--hover');
			e.target.classList.remove('CTAT-done-button--clicked');
		});
		comp.addEventListener('mousedown', function(e) {
			e.target.classList.add('CTAT-done-button--clicked');
		});
		comp.addEventListener('mouseup', function(e) {
			e.target.classList.remove('CTAT-done-button--clicked');
		});
		comp.addEventListener('click', function(e) {
			e.target.classList.remove('CTAT-done-button--clicked');
		});
		comp.addEventListener('click', this.processClick);
	    comp.addEventListener('focus', this.processFocus);
	};
	this.processClick = function(e) {
		if (pointer.getEnabled() && CTATCommShell.commShell)
			CTATCommShell.commShell.processDone();
	};
};

CTATDoneButton.prototype = Object.create(CTAT.Component.Base.Clickable.prototype);
CTATDoneButton.prototype.constructor = CTATDoneButton;
CTAT.ComponentRegistry.addComponentType('CTATDoneButton', CTATDoneButton);
