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
goog.provide('silex.controller.FileMenuController');

goog.require('silex.controller.ControllerBase');
goog.require('silex.service.SilexTasks');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.FileMenuController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.FileMenuController, silex.controller.ControllerBase);


/**
 * open a file
 * @param {?function()=} opt_cbk
 * @param {?function(Object)=} opt_errorCbk
 */
silex.controller.FileMenuController.prototype.newFile = function(opt_cbk, opt_errorCbk) {

  this.tracker.trackAction('controller-events', 'request', 'file.new', 0);
  var doNew = function(cbk, errCbk)
  {
	  cbk && (opt_cbk = cbk);
	  errCbk && (opt_errorCbk = errCbk);
	  this.model.file.newFile(goog.bind(function(rawHtml) {
		this.model.file.setHtml(rawHtml, goog.bind(function() {
		  // undo redo reset
		  this.undoReset();
		  this.fileOperationSuccess(null, true);
		  // QOS, track success
		  this.view.stage.setScrollX(20);
		  this.tracker.trackAction('controller-events', 'success', 'file.new', 1);
		  //use timestamp as temp name
		  let filename = 'Untitled-'+Date.now();
		  this.model.file.setMeta('interface-id',filename);
		  this.view.stage.setFilename(filename);
		  //clear dynamically populated menus
		  this.view.menu.clearMenus();
		  //reset asset map
		  this.model.file.resetUrlMap();
		  
		  if (opt_cbk) {
			opt_cbk();
		  }
		}, this));
	  }, this), goog.bind(function(error) {
		this.tracker.trackAction('controller-events', 'error', 'file.new', -1);
		if (opt_errorCbk) {
		  opt_errorCbk(error);
		}
	  }, this));
  }.bind(this);
  
  if (this.isDirty())
  {
	this.showUnsavedChangesDialog(doNew)
  }
  else
  {
	doNew();
  }
};


/**
 * open a file
 * @param {?function()=} opt_cbk
 * @param {?function(Object)=} opt_errorCbk
 */
silex.controller.FileMenuController.prototype.openFile = function(opt_cbk, opt_errorCbk) {
  // QOS, track success
  this.tracker.trackAction('controller-events', 'request', 'file.open', 0);
  var goAhead = true; 
  if (this.isDirty())
  {
	this.showUnsavedChangesDialog(this.fileChooser.show.bind(this.fileChooser, 'OPEN_SILEX'));
  }
  else
	this.fileChooser.show('OPEN_SILEX');
};

silex.controller.FileMenuController.prototype.showUnsavedChangesDialog = function(continueFunc)
{
	var msg = 'You have unsaved changes that will be lost if you open a new file.  Continue?';
	var options = [
		{
			label: 'Cancel',
			cbk: function() {return;}
		},
		{
			label: 'Save and Continue',
			cbk: function()
			{
				silexApp.controller.fileMenuController.save(false, function()
				{
					continueFunc(function(){
						silexApp.controller.stageController.showMsg('Save Successful', 2000, true);
					}, function() {
						silexApp.controller.stageController.showMsg('Error Saving', 2000, false);
					});
				});
			}
		},
		{
			label: 'Continue Without Saving',
			cbk: continueFunc
		}
	];

	this.view.stage.showConfirmDialog(msg, options);
};

/**
 *	Load a copy of the stage content into a new iframe window that will
 *	connect to the tutoring service.
 */
silex.controller.FileMenuController.prototype.demonstrate = function()
{
  var id = this.model.file.getMeta('interface-id');
  if (!id)
  {
	  id = window.prompt('Please enter a name for this interface', 'Untitled');
	  this.model.file.setMeta('interface-id', id);
  }
  var html = this.model.file.getHtml(true);
  var playerWindow = goog.dom.getElement('tutorplayer');
  //set dimensions of player window
  var bgElement = this.model.file.getContentDocument().querySelector('.background-initial');
  var w = $(bgElement).width(), h = $(bgElement).height();
  w = parseInt(w, 10) + 50;
  h = parseInt(h, 10) + 50;
  console.log('setting window size to '+w+' x '+h);
  playerWindow.style.width = w+'px'; 
  playerWindow.style.height= h+'px';
  //CTATConfiguration options
  var flashVars = {
	mode: 'auth',
	remoteSocketPort: 20080,
	remoteSocketSecurePort: 20443,
	session_id: id,
	editorMode: true,
	CTATTarget: 'AUTHORING'
  };
  
  var cbk = function(fileObj)
	{
		//create iframe
		var iframe = goog.dom.iframe.createBlank(goog.dom.getDomHelper(), 'width: 100%; height: 100%;');
		iframe.setAttribute('data-params', JSON.stringify(flashVars));
		iframe.setAttribute('id', 'demonstrate-iframe');
		var playerContent = goog.dom.getElementByClass('ctatcontent', playerWindow);
		playerContent.innerHTML = '';
		
		//add to DOM
		goog.dom.appendChild(playerContent, iframe);		
		
		//register/initialize with CTATIFrameManager
		if (!window.ctatIFrameManager)
		{
			window.ctatIFrameManager = new CTATIFrameManager();
		}
		window.ctatIFrameManager.initFrame(iframe, fileObj.htmlString, "AUTHORING");
		
		//open demonstrate window
		if (!this.demonstrateWindow)
		{
			this.demonstrateWindow = new CTATTutorPlayer ();
		}
		this.demonstrateWindow.showOpenDialog ();
	};
	
  silex.utils.Dom.getCleanFile(html, cbk, false);
};

/**
*	Display the file chooser dialog in download mode
*/
silex.controller.FileMenuController.prototype.downloadPkg = function()
{
	this.fileChooser.show('DOWNLOAD');
};

/**
*	Display the new folder/package creation dialog
*/
silex.controller.FileMenuController.prototype.newPackage = function()
{
	this.fileChooser.fileDialogNewFolder(null, 
										this.fileChooser.createPackage,
										'New Package',
										'Creating a package creates a new set of empty folders with the recommended structure for a new CTAT tutor.');
}

silex.controller.FileMenuController.prototype.initSaveTimer = function()
{
	// I'm turning this off for now because it's not clear at this point
	// under what conditions it should start auto-saving. For example it
	// already kicks in on a blank canvas when the user hasn't done anything
	// yet. There could be more conditions like this.
	
	// this.saveTimer = window.setInterval(this.autosave.bind(this), 300000);
}
