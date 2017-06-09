/**
 * Silex, live web creation
 * http://projects.silexlabs.org/?/silex/
 *
 * Copyright (c) 2012 Silex Labs
 * http://www.silexlabs.org/
 *
 * Silex is available under the GPL license
 * http://www.silexlabs.org/silex/silex-licensing/
 */

/**
 * @fileoverview This class handles the property panes,
 * Property panes displayed in the property tool box.
 * Controls the params of the selected component.
 *
 */


goog.provide('silex.view.PropertyTool');

goog.require('goog.array');
goog.require('goog.cssom');
goog.require('goog.editor.Field');
goog.require('goog.object');
goog.require('goog.ui.Checkbox');
goog.require('goog.ui.TabBar');
goog.require('silex.view.pane.BgPane');
goog.require('silex.view.pane.BorderPane');
goog.require('silex.view.pane.AttributePane');
goog.require('silex.view.pane.PositionPane');
goog.require('silex.view.pane.GeneralStylePane');
goog.require('silex.view.pane.PagePane');
goog.require('silex.view.pane.PropertyPane');
goog.require('silex.view.pane.FontPane');
goog.require('silex.view.pane.MarginPane');
goog.require('silex.view.pane.StylePane');
goog.require("CTATComponentAttributes");



//////////////////////////////////////////////////////////////////
// PropertyTool class
//////////////////////////////////////////////////////////////////
/**
 * the Silex PropertyTool class handles the panes actually displaying the properties
 * @constructor
 *
 * @param {Element} element   container to render the UI
 * @param  {!silex.types.Model} model  model class which holds
 *                                  the model instances - views use it for read operation only
 * @param  {!silex.types.Controller} controller  structure which holds
 *                                  the controller instances
 */
silex.view.PropertyTool = function(element, model, controller) {
  // store references
  /**
   * @type {Element}
   */
  this.element = element;
  /**
   * @type {!silex.types.Model}
   */
  this.model = model;
  /**
   * @type {!silex.types.Controller}
   */
  this.controller = controller;

  /**
   * invalidation mechanism
   * @type {InvalidationManager}
   */
  this.invalidationManager = new InvalidationManager(500);
  
  this.selectedId = null;
  
  this.classlistElement = document.querySelector('#property-tool-classlist');

};


/**
 * base url for relative/absolute urls
 */
silex.view.PropertyTool.prototype.baseUrl = null;


/**
 * bg editor
 * @see     silex.view.pane.BgPane
 */
silex.view.PropertyTool.prototype.bgPane = null;


/**
 * property editor
 * @see     silex.view.pane.PropertyPane
 */
silex.view.PropertyTool.prototype.propertyPane = null;

/**
 * attribute editor
 * @see silex.view.pane.AttributePane
 */
silex.view.PropertyTool.prototype.attributePane = null;

/**
 * position editor
 * @see silex.view.pane.PositionPane
 */
silex.view.PropertyTool.prototype.positionPane = null;

/**
 * editor
 * @see     silex.view.pane.BorderPane
 */
silex.view.PropertyTool.prototype.borderPane = null;


/**
 * property editor
 * @see     silex.view.pane.PagePane
 */
silex.view.PropertyTool.prototype.pagePane = null;


/**
 * property editor
 * @see     silex.view.pane.GeneralStylePane
 */
silex.view.PropertyTool.prototype.generalStylePane = null;


/**
 * property editor
 * @see     silex.view.pane.StylePane
 */
silex.view.PropertyTool.prototype.stylePane = null;

silex.view.PropertyTool.prototype.fontPane = null;

silex.view.PropertyTool.prototype.marginPane = null;

/**
 * build the UI
 */
silex.view.PropertyTool.prototype.buildUi = function() {
  // background  
  console.log("Building bg-pane...");
  this.bgPane = new silex.view.pane.BgPane(
    goog.dom.getElementByClass('background-editor', this.element),
    this.model, this.controller);

  // border
  console.log("Building border pane...");
  this.borderPane = new silex.view.pane.BorderPane(
    goog.dom.getElementByClass('border-editor', this.element),
    this.model, this.controller);
	
  console.log("Building attribute pane...");
  this.attributePane = new silex.view.pane.AttributePane(this, 
		this.model,
		goog.dom.getElementByClass('attr-editor', this.element),
		this.controller);
  
  console.log("Building position pane...");
  this.positionPane = new silex.view.pane.PositionPane(this, 
		this.model, 
		goog.dom.getElementByClass('position-editor', this.element), 
		this.controller);
		
  this.fontPane = new silex.view.pane.FontPane(this, 
		this.model,
		goog.dom.getElementByClass('font-editor', this.element),
		this.controller);
		
  this.marginPane = new silex.view.pane.MarginPane(this,
		this.model,
		goog.dom.getElementByClass('margin-editor', this.element),
		this.controller);
};

silex.view.PropertyTool.prototype.setComponentType = function(selectedElements)
{
	var typeStr = '';
	var getType = function(element)
	{
		var type = '';
		if (element)
		{
			type = silex.utils.CTAT.getCTATClassName(element);
			if (!type)
			{			
				type = silex.utils.Dom.getSilexType(element);
			}
			else type = silex.utils.CTAT.splitCTATClassName(type);
		}
		return type;
	};
	
	typeStr += getType(selectedElements[0]);
	for (let i = 1; i < selectedElements.length; i++)
	{
		let thisType = getType(selectedElements[i]);
		if (thisType)
			typeStr += ', '+thisType;
	}
	let typeField = document.querySelector('.element-type-field');
	typeField.textContent = typeStr;
}

/**
 * redraw all panes
* @param   {Array.<HTMLElement>} selectedElements the elements currently selected
* @param   {Array.<string>} pageNames   the names of the pages which appear in the current HTML file
* @param   {string}  currentPageName   the name of the current page
 */
silex.view.PropertyTool.prototype.redraw = function(selectedElements, pageNames, currentPageName) {
  this.invalidationManager.callWhenReady(() => {
    //get list of comps to disable
	var disable = this.borderPane.getDisabled(selectedElements);
	this.setComponentType(selectedElements);
	// refresh panes
    this.borderPane.redraw(selectedElements, pageNames, currentPageName, disable);
	this.bgPane.redraw(selectedElements, pageNames, currentPageName, disable);
	this.attributePane.redraw(selectedElements);
	this.positionPane.redraw(selectedElements, disable); 
	this.fontPane.redraw(selectedElements, disable);
	this.marginPane.redraw(selectedElements);
  });
};

silex.view.PropertyTool.prototype.addToClasslist = function(toAdd, innerText)
{
	//check if already there
	let option = document.querySelector('#property-tool-classlist > option[id="property-tool-classlist-option-'+toAdd);
	if (!option)
	{
		if (!silex.Config.RestrictedClasses[toAdd])
		{
			option = document.createElement('option');
			option.setAttribute('value', toAdd);
			option.setAttribute('id', 'property-tool-classlist-option-'+toAdd);
			option.textContent = innerText;
			this.classlistElement.appendChild(option);
			return true;
		}	
	}
	return false;
}

silex.view.PropertyTool.prototype.setClasslistItemText = function(className, text)
{
	let option = document.querySelector('#property-tool-classlist > option[id="property-tool-classlist-option-'+className);
	if (option)
	{
		option.textContent = text;
	}
}

silex.view.PropertyTool.prototype.resetClasslist = function()
{
	$('#property-tool-classlist > option').text('add');
}

silex.view.PropertyTool.prototype.checkClasslist = function(toCheck)
{
	return !!document.querySelector('#property-tool-classlist > option[id="property-tool-classlist-option-'+toCheck);
}

silex.view.PropertyTool.prototype.setLastKeyPressed = function(keyVal)
{
	this.lastKeyPressed = keyVal;
}

silex.view.PropertyTool.prototype.getLastKeyPressed = function()
{
	return this.lastKeyPressed;
}

silex.view.PropertyTool.prototype.setLastInput = function(val)
{
	this.lastInput = val;
}

silex.view.PropertyTool.prototype.getLastInput = function()
{
	return this.lastInput;
}
