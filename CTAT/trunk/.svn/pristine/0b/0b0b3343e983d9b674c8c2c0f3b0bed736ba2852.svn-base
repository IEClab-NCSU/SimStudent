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
goog.provide('silex.controller.ViewMenuController');

goog.require('silex.controller.ControllerBase');
goog.require('silex.service.SilexTasks');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.ViewMenuController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.ViewMenuController, silex.controller.ControllerBase);

silex.controller.ViewMenuController.prototype.tabOrderVisible = false;

/**
 * edit Silex editable css styles
 */
silex.controller.ViewMenuController.prototype.openCssEditor = function() {
  // undo checkpoint
  this.undoCheckPoint();
  var userStyleTag = this.model.file.getContentDocument().head.querySelector('style[class="silex-style"]');
  var css = userStyleTag.innerHTML.trim();;
  this.view.stage.showCSSWindow(css, function(newCSS)
  {
	userStyleTag.innerHTML = newCSS;  
  });
};


/**
 * edit HTML head tag
 */
silex.controller.ViewMenuController.prototype.openHtmlHeadEditor = function() {
  // undo checkpoint
  this.undoCheckPoint();
  // deselect all elements
  this.model.body.setSelection([]);
  // open the editor
  this.view.htmlEditor.openEditor();
  this.view.htmlEditor.setValue(this.model.head.getUserHeadTag());
};


/**
 * edit Silex editable js scripts
 */
silex.controller.ViewMenuController.prototype.openJsEditor = function() {
  // undo checkpoint
  this.undoCheckPoint();
  // open the editor
  this.view.jsEditor.openEditor();
  this.view.jsEditor.setValue(this.model.head.getHeadScript());
};

/**
 * preview the website in a new window or in responsize
 * ask the user to save the file if needed
 * @param {boolean} inResponsize if true this will open the preview in responsize
 *                               if false it will open the website in a new window
 */
silex.controller.ViewMenuController.prototype.doPreview = function(inResponsize) {
  this.tracker.trackAction('controller-events', 'request', 'view.file', 0);
  var doOpenPreview = function() {
    if (inResponsize) {
      this.view.workspace.setPreviewWindowLocation('http://www.responsize.org/?url=' +
        silex.utils.Url.getBaseUrl() +
        this.model.file.getUrl() +
        '#!' + this.model.page.getCurrentPage());
    }
    else {
      this.view.workspace.setPreviewWindowLocation(this.model.file.getUrl() + '#!' + this.model.page.getCurrentPage());
    }
    this.tracker.trackAction('controller-events', 'success', 'view.file', 1);
  }.bind(this);
  // save before preview
  var doSaveTheFile = function() {
    this.save(
      this.model.file.getUrl(),
      goog.bind(function(url) {
        //doOpenPreview();
      }, this),
      goog.bind(function(err) {
        this.tracker.trackAction('controller-events', 'error', 'view.file', -1);
      }, this));
  }.bind(this);
  if(this.model.file.getUrl()) {
    // open the preview window
    // it is important to do it now, on the user click so that it is not blocked
    // it will be refreshed after save
    doOpenPreview();
    // also save
    if(this.isDirty()) {
      doSaveTheFile();
    }
  }
  else {
    silex.utils.Notification.alert('You need to save the website before I can show a preview', goog.bind(function () {
      doSaveTheFile();
    }, this));
  }
};

/**
*	Show or hide labels displaying the tabindex of elements on the stage
*/
silex.controller.ViewMenuController.prototype.displayTabOrder = function()
{
	var stage = this.model.file.getContentDocument();
	var menuItem = goog.dom.getElement('view.taborder');
	var menuItemContent = goog.dom.getElementByClass('goog-menuitem-content', menuItem);
	if (this.tabOrderVisible)
	{
		//hide tab order
		var tabLabels = goog.dom.getElementsByClass('taborder-label', stage);		
		for (let i = 0; i < tabLabels.length; i++)
		{
			tabLabels[i].parentNode.removeChild(tabLabels[i]);
		}
		menuItemContent.textContent = "Show Tab Order";
		this.tabOrderVisible = false;
	}
	
	else
	{
		//show tab order
		this.tabOrderVisible = true;
		var stageElements = this.model.file.getContentDocument().querySelectorAll('div[data-ctat-tabindex]');
		stageElements = [].slice.call(stageElements);
		for (let i = 0; i < stageElements.length; i++)
		{
			this.model.element.setTabOrderLabel(stageElements[i]);
		}
		menuItemContent.textContent = "Hide Tab Order";
	}
};

/**
*	Open the group editor window to edit a given group
*	@param groupName the name of the group to edit
*/
silex.controller.ViewMenuController.prototype.editGroups = function(groupName)
{
	console.log('viewMenuController.editGroups '+groupName);
	var selected;
	if (groupName === 'newgrp')
	{
		//create new group, pass current selection
		let selected = this.model.body.getSelection();
		this.view.stage.showGroupWindow('create', selected)
	}
	else
	{
		//get ctatgroupingcomponent
		let group = this.model.file.getContentDocument().getElementById(groupName);
		if (group)
		{
			let listAttr = group.getAttribute('data-ctat-componentlist');
			let idList = listAttr.split(',');
			this.view.stage.showGroupWindow('edit', idList, groupName);
		}
		else
		{
			console.warn('group '+groupName+' not found');
		}
	}
};
