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
 * @fileoverview A controller listens to a view element,
 *      and call the main {silex.controller.Controller} controller's methods
 *
 */
goog.provide('silex.controller.InsertMenuController');

goog.require('silex.controller.ControllerBase');
goog.require('silex.service.SilexTasks');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.InsertMenuController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.InsertMenuController, silex.controller.ControllerBase);


/**
 * create an element and add it to the stage
 * @param {string} type the desired type for the new element
 * @return {Element} the new element
 */
silex.controller.InsertMenuController.prototype.addElement = function(type, optStyles) {
  console.log("insertMenu.addElement: "+ type);
  this.tracker.trackAction('controller-events', 'request', 'insert.' + type, 0);
  // undo checkpoint
  var element = null;
  if (type.includes('question'))
  {
	  this.view.stage.showQuestionDialog(type.substring(9));
  }
  else
  {
	this.undoCheckPoint();
	// create the element and add it to the stage
	element = this.model.element.createElement(type, null, optStyles);
	this.doAddElement(element);
	if (type.includes('ctat.table'))
		this.model.element.resizeTable(element);
	this.tracker.trackAction('controller-events', 'success', 'insert.' + type, 1);
	return element;
  }
};


/**
 * create a page
 * @param {?function()=} successCbk
 * @param {?function()=} cancelCbk
 */
silex.controller.InsertMenuController.prototype.createPage = function(successCbk, cancelCbk) {
  this.tracker.trackAction('controller-events', 'request', 'insert.page', 0);
  this.getUserInputPageName('Your new page name', goog.bind(function(name, displayName) {
    if (name) {
      // undo checkpoint
      this.undoCheckPoint();
      // create the page model
      this.model.page.createPage(name, displayName);
      // update view
      if (successCbk) {
        successCbk();
      }
      this.tracker.trackAction('controller-events', 'success', 'insert.page', 1);
    }
    else {
      if (cancelCbk) {
        cancelCbk();
      }
      this.tracker.trackAction('controller-events', 'cancel', 'insert.page', 0);
    }
  }, this));
};

/**
 *	Open the fchooser dialog to pick a stylesheet to insert into the Stage DOM
 */
silex.controller.InsertMenuController.prototype.pickFile = function(filetype)
{
	if (!this.fileChooser)
	{
		if (!window.ctatFileChooser)
			window.ctatFileChooser = new CTATFileChooser();
		this.fileChooser = window.ctatFileChooser;
	}
	
	let mode = (filetype === 'stylesheet') ? 'OPENSTYLESHEET' : 'OPENSCRIPT';
	
	this.fileChooser.show(mode);
}

/**
 *	Given the name of a stylesheet and its contents, insert it into the Stage DOM
 *	@param name the name of the sheet, will also be the id of the <style> tag
 *	@param content the content of the stylesheet
 */
silex.controller.InsertMenuController.prototype.addAsset = function(name, content, fileId, assetType)
{
	console.log('addAsset( '+name+' )');
	let doc = this.model.file.getContentDocument();
	let tagName = assetType === 'stylesheet' ? 'style' : 'script';
	//check if already loaded
	let asset = doc.querySelector(tagName+'[id="'+name+'"]');
	var node;
	if (asset)
		node = asset;
	else
	{
		node = document.createElement(tagName);
		doc.head.appendChild(node);
		this.view.menu.addAsset(name, assetType);
	}

	if (assetType === 'script')
	{
		//prevent from running in editor
		node.setAttribute('type', 'text/notjavascript');
	}
	node.innerHTML = content;
	node.setAttribute('id', name);
	node.setAttribute('data-file-id', fileId);
	node.setAttribute('class', 'user-'+assetType);
	
	if (assetType === 'stylesheet')
	{
		//load all classes into property-tool-classlist
		let regex = /\.([^ \n\cr,>:{]{1,})[\n\cr ]*{/g;
		var match;
		do
		{
			match = regex.exec(content);
			if (match)
			{
				console.log('found class: '+match[1]);
				this.view.propertyTool.addToClasslist(match[1], 'add');
			}
		}while(match);
	}

	this.view.stage.setStatus(name+' loaded');
}

/**
*	Open the 'create group' dialog window
*/
silex.controller.InsertMenuController.prototype.addGroup = function()
{
	var selected = this.model.body.getSelection();
	this.view.stage.showGroupWindow('create', selected)
}

