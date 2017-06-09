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
 * @fileoverview In Silex, each UI of the view package,
 *      has a controller in the UI controller package which listens to its events,
 *      and call the main {silex.controller.Controller} controller's methods
 *      These UI controllers override the
 *      {silex.controller.ControllerBase} UiControllerBase class
 *
 */
goog.provide('silex.controller.ControllerBase');

goog.require('silex.utils.Notification');

 

/**
 * base class for all UI controllers of the controller package
 * @constructor
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.ControllerBase = function(model, view) {
  // store the model
  // store the model and the view
  /**
   * @type {silex.types.Model}
   */
  this.model = model;
  /**
   * @type {silex.types.View}
   */
  this.view = view;

  // init undo/redo
  this.undoReset();

  // tracker
  this.tracker = silex.service.Tracker.getInstance();
  window.onerror = goog.bind(function(msg, url, line) {
    this.tracker.trackAction('controller-events', 'uncaught.error', msg + '- ' + url + ' - line: ' + line, -1);
  }, this);
  
  //init file chooser
  if (!window.ctatFileChooser)
  {
	window.ctatFileChooser = new CTATFileChooser();
	window.ctatFileChooser.close();
  }
  this.fileChooser = window.ctatFileChooser;
  
  //init create pkg dialog
  if (!window.ctatPublishDialog)
  {  
	window.ctatPublishDialog = new CTATCreatePackage('#create-package-dialog');
	window.ctatPublishDialog.initEvents();
	window.ctatPublishDialog.close();
  }
  this.publishDialog = window.ctatPublishDialog;
};

/**
 * {silex.service.Tracker} tracker used to pull statistics on the user actions
 * @see     silex.service.Tracker
 */
silex.controller.ControllerBase.prototype.tracker = null;


/**
 * @type {number} index of the undoHistory when last saved
 * this is useful in order to know if the website is "dirty", i.e. if it was modified since last save
 * it has a default value of -1
 * @see isDirty
 * @static because it is shared by all controllers
 */
silex.controller.ControllerBase.lastSaveUndoIdx = -1;


/**
 * @type {Array.<silex.types.UndoItem>} array of the states of the website
 * @static because it is shared by all controllers
 */
silex.controller.ControllerBase.undoHistory = [];


/**
 * @type {Array.<silex.types.UndoItem>} array of the states of the website
 * @static because it is shared by all controllers
 */
silex.controller.ControllerBase.redoHistory = [];


/**
 * @type {Array.<silex.types.ClipboardItem>}
 * @static because it is shared by all controllers
 */
silex.controller.ControllerBase.clipboard = null;


/**
 * flag to indicate that a getState ation is pending
 * will be 0 unless an undo check point is being created
 * @type {number}
 */
silex.controller.ControllerBase.getStatePending = 0;


/**
 * use lastSaveUndoIdx to determine if the website is dirty
 * @return {boolean} true if the website has unsaved changes
 */
silex.controller.ControllerBase.prototype.isDirty = function() {
  return silex.controller.ControllerBase.lastSaveUndoIdx !== (silex.controller.ControllerBase.undoHistory.length - 1);
};

silex.controller.ControllerBase.prototype.fileChooser = null;


/**
 * store the model state in order to undo/redo
 */
silex.controller.ControllerBase.prototype.undoCheckPoint = function(optCbk) {
  silex.controller.ControllerBase.redoHistory = [];
  silex.controller.ControllerBase.getStatePending++;
  this.getState((state) => {
    silex.controller.ControllerBase.getStatePending--;
    // if the previous state was different
    if (state.html && (
	    silex.controller.ControllerBase.undoHistory.length === 0 ||
        silex.controller.ControllerBase.undoHistory[silex.controller.ControllerBase.undoHistory.length - 1].html !== state.html)) 
	{
	  silex.controller.ControllerBase.undoHistory.push(state);
	  if (optCbk && typeof(optCbk) === 'function')
		optCbk();
	}
  });
};



/**
 * build a state object for undo/redo
 * asyn operation if opt_cbk is provided
 * @param {?function(silex.types.UndoItem)=} opt_cbk
 * @return {silex.types.UndoItem|null}
 */
silex.controller.ControllerBase.prototype.getState = function(opt_cbk) {
  if(opt_cbk) {
    this.model.file.getHtmlAsync((html) => {
      opt_cbk({
        html:html,
        page: this.model.page.getCurrentPage(),
        scrollX: this.view.stage.getScrollX(),
        scrollY: this.view.stage.getScrollY()
      });
    });
  }
  else {
    return {
      html: this.model.file.getHtml(),
      page: this.model.page.getCurrentPage(),
      scrollX: this.view.stage.getScrollX(),
      scrollY: this.view.stage.getScrollY()
    };
  }
  return null;
};


/**
 * build a state object for undo/redo
 * @param {silex.types.UndoItem} state
 */
silex.controller.ControllerBase.prototype.restoreState = function(state) {
  this.model.file.setHtml(state.html, goog.bind(function() {
    this.model.page.setCurrentPage(state.page);
    this.view.stage.setScrollX(state.scrollX);
    this.view.stage.setScrollY(state.scrollY);
  }, this), false, true);
};


/**
 * reset the undo/redo history
 */
silex.controller.ControllerBase.prototype.undoReset = function() {
  silex.controller.ControllerBase.undoHistory = [];
  silex.controller.ControllerBase.redoHistory = [];
  silex.controller.ControllerBase.lastSaveUndoIdx = -1;
};

/**
 * set a given style to the current selection
 * @param  {string} name
 * @param  {?string=} value
 * @param {?Array.<Element>=} opt_elements
 * @param  {?boolean=} opt_isUndoable default is true
 */
silex.controller.ControllerBase.prototype.styleChanged = function(name, value, opt_elements, opt_isUndoable) {
  if (!opt_elements) {
    opt_elements = this.model.body.getSelection();
  }
  if (opt_isUndoable !== false) {
    // undo checkpoint
	this.undoCheckPoint();
  }
  // apply the change to all elements
  goog.array.forEach(opt_elements, function(element) 
  {
    // update the model
	var iFrameWindow = this.model.file.getContentWindow();
	this.model.element.setStyle(element, name, value);
  }, this);
};


/**
 * set a set of styles to the current selection
 * @param  {string|Object|CSSStyleDeclaration} style
 * @param {?Array.<Element>=} opt_elements
 */
silex.controller.ControllerBase.prototype.multipleStylesChanged = function(style, opt_elements) {
  if (!opt_elements) {
    opt_elements = this.model.body.getSelection();
  }
  // undo checkpoint
  this.undoCheckPoint();
  // apply the change to all elements
  goog.array.forEach(opt_elements, function(element) {
    // update the model
	let selector = '.' + this.model.property.getSilexId(element);
    this.model.property.setStyle(selector, style);
  }, this);
};


/**
 * set a given property to the current selection
 * @param  {string} name
 * @param  {?string=} value
 * @param {?Array.<Element>=} opt_elements
 * @param {?boolean=} opt_applyToContent
 */
silex.controller.ControllerBase.prototype.propertyChanged = function(name, value, opt_elements, opt_applyToContent) {
  if (!opt_elements) {
    opt_elements = this.model.body.getSelection();
  }
  // undo checkpoint
  this.undoCheckPoint();
  // apply the change to all elements
  goog.array.forEach(opt_elements, function(element) {
    // update the model
    this.model.element.setProperty(element, name, value, opt_applyToContent);
  }, this);
};


/**
 * set css class names
 * @param   {string} name
 */
silex.controller.ControllerBase.prototype.setClassName = function(name) {
  // undo checkpoint
  this.undoCheckPoint();
  // apply the change to all elements
  var elements = this.model.body.getSelection();
  goog.array.forEach(elements, function(element) {
    // update the model
    this.model.element.setClassName(element, name);
  }, this);
};

/**
 * called after an element has been created
 * add the element to the current page (only if it has not a container which is in a page)
 * redraw the tools and set the element as editable
 * @param {Element} element the element to add
 */
silex.controller.ControllerBase.prototype.doAddElement = function(element) {
  if (!element)
  {
	return;
  }
  // only visible on the current page
  var currentPageName = this.model.page.getCurrentPage();
  this.model.page.removeFromAllPages(element);
  if (!element.className.includes('CTATHintWindow'))
  {
	  this.model.page.addToPage(element, currentPageName);
  }
  // unless one of its parents is in a page already
  this.checkElementVisibility(element);
  // set element editable
  this.model.body.setEditable(element, true);
  if (element.className.includes('CTAT') || element.className.includes('question'))
  {
	  console.log('init CTAT comps');
	  var stage = this.model.file.getContentWindow();
	  stage.CTATTutor.initializeHTMLComponents();
  }
  // select the component
  this.model.body.setSelection([element]);
};


/**
 * check if the element's parents belong to a page, and if one of them do,
 * remove the element from the other pages
 *
 * if the element is in a container which is visible only on some pages,
 * then the element should be visible everywhere, i.e. in the same pages as its parent
 * @param   {Element} element
 */
silex.controller.ControllerBase.prototype.checkElementVisibility = function(element) {
  var parentPage = this.model.page.getParentPage(element);
  if (parentPage !== null) {
    // get all the pages
    var pages = this.model.page.getPagesForElement(element);
    for (let idx in pages) {
      // remove the component from the page
      var pageName = pages[idx];
      this.model.page.removeFromPage(element, pageName);
    }
  }
};


/**
 * ask the user for a new file title
 * @param {string} title
 */
silex.controller.ControllerBase.prototype.setTitle = function(title) {
  // undo checkpoint
  this.undoCheckPoint();
  this.model.head.setTitle(title);
};


/**
 * refresh font list in the text editor, and in the head tag
 */
silex.controller.ControllerBase.prototype.refreshFonts = function() {
  //update loaded font list, as user might have choose a new one
  var neededFonts = this.model.body.getNeededFonts();
  // refresh the font list in the text editor
  var customFontsForTextEditor = this.model.head.refreshFontList(neededFonts);
};

/**
 * save or save-as
 * @param {?string=} opt_url
 * @param {?function()=} opt_cbk
 * @param {?function(Object)=} opt_errorCbk
 */
silex.controller.ControllerBase.prototype.save = function(saveAs) 
{
  this.tracker.trackAction('controller-events', 'request', 'file.save', 0);
  
  let filename = this.model.file.getMeta('interface-id');
  let parent = this.model.file.getMeta('parent-id');
  if (!saveAs && (parent || parent === ''))
  {
	  var fileData = {
		type: 'text/html',
		data: this.model.file.getHtml()
	  };
	  cloudUtils.saveFile(
			filename, 
			false, 
			parent, 
			fileData, 
			() => {
				silex.controller.ControllerBase.lastSaveUndoIdx = silex.controller.ControllerBase.undoHistory.length - 1;
				this.view.contextMenu.redraw();
			}
	  );
  }
  else 
  {
	this.publishDialog.show('save');
  }
};

/**
 * success of an operation involving changing the file model
 * @param {?string=} opt_message
 * @param {?boolean=} opt_updateTools
 */
silex.controller.ControllerBase.prototype.fileOperationSuccess = function(opt_message, opt_updateTools) {
  // update tools
  if (opt_updateTools) {
    // update fonts
    this.refreshFonts();
	this.view.contextMenu.redraw();
  }
  if (opt_message) {
    // notify user
    silex.utils.Notification.notifySuccess(opt_message);
  }
};

/*
silex.controller.ControllerBase.prototype.addWindowNode = function(id, templateName, callback, width, height)
{
	width = width || 400;
	height = height || 200;
	var windowDiv = goog.dom.getElement(id);
	var cbk = function(html)
	{
		windowDiv = document.createElement('div');
		windowDiv.setAttribute('id', id);
		windowDiv.classList.add('ctatdialog');
		windowDiv.style.width = width+'px';
		windowDiv.style.height = height+'px';
		windowDiv.innerHTML = html;
		document.body.appendChild(windowDiv);
		
		callback(windowDiv);
	};
	if (windowDiv)
	{
		callback(windowDiv)
	}
	else
	{
		let url = silex.model.File.WINDOW_TEMPLATE_DIR+'/'+templateName;
		console.log('loading dialog contents from '+url);
		silex.service.CloudStorage.getInstance().loadLocal(url, cbk);
	}
}
*/