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
 * @fileoverview The stage is the area where the user drag/drops elements
 *   This class is in charge of listening to the DOM of the loaded publication
 *   and retrieve information about it
 *
 */


goog.provide('silex.view.Stage');

goog.require('goog.events');
goog.require('goog.events.MouseWheelHandler');



/**
 * the Silex stage class
 * @constructor
 * load the template and render to the given html element
 * @param  {Element}  element  DOM element to wich I render the UI
 *  has been changed by the user
 * @param  {!silex.types.Model} model  model class which holds
 *                                  the model instances - views use it for read operation only
 * @param  {!silex.types.Controller} controller
 *                      structure which holds the controller classes
 */
silex.view.Stage = function(element, model, controller) {
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
  this.invalidationManagerScroll = new InvalidationManager(500);


  /**
   * invalidation mechanism
   * @type {InvalidationManager}
   */
  this.invalidationManagerFocus = new InvalidationManager(500);
  
  this.snapToGrid = true;
  this.paletteItemSelected = false;
  this.lassoSelected = false;
};

/**
 * class name for the stage element
 */
silex.view.Stage.STAGE_CLASS_NAME = 'silex-stage-iframe';


/**
 * input element to get the focus
 */
silex.view.Stage.BACKGROUND_CLASS_NAME = 'background';


/**
 * the document of the iframe which contains the website
 */
silex.view.Stage.prototype.documentElement = null;


/**
 * the element which contains the body of the website
 */
silex.view.Stage.prototype.bodyElement = null;



/**
 * current selection
 * @type {Array.<Element>}
 */
silex.view.Stage.prototype.selectedElements = null;


/**
 * input element to get the focus
 */
silex.view.Stage.prototype.focusInput = null;


/**
 * flag to store the state
 */
silex.view.Stage.prototype.isResizing = false;


/**
 * flag to store the state
 */
silex.view.Stage.prototype.isDragging = false;


/**
 * flag to store the state
 */
silex.view.Stage.prototype.isDown = false;

silex.view.Stage.prototype.lassoSelected = false;

/**
 * build the UI
 * called by the app constructor
 */
silex.view.Stage.prototype.buildUi = function() {
  // create an input element to get the focus
  this.focusInput = goog.dom.createElement('input');
  goog.style.setStyle(this.focusInput, 'left', '-1000px');
  goog.style.setStyle(this.focusInput, 'position', 'absolute');
  var silexBody = goog.dom.getElementByClass('silex-body');
  silexBody.appendChild(this.focusInput);


  // Disable horizontal scrolling for Back page on Mac OS, over Silex UI
  goog.events.listen(new goog.events.MouseWheelHandler(silexBody),
      goog.events.MouseWheelHandler.EventType.MOUSEWHEEL,
      this.onPreventBackSwipe,
      false,
      this);

  // Disable horizontal scrolling for Back page on Mac OS
  // on the iframe
  goog.events.listen(new goog.events.MouseWheelHandler(this.element),
      goog.events.MouseWheelHandler.EventType.MOUSEWHEEL,
      this.onPreventBackSwipe,
      false,
      this);

  // listen on body too because user can release the mouse over the tool boxes
  goog.events.listen(silexBody,
      'mouseup',
      this.onMouseUpOverUi,
      false,
      this);

  // listen on body too because user can release
  // on the tool boxes
  goog.events.listen(silexBody,
      'mousemove',
      this.onMouseMoveOverUi,
      false,
      this);

  // keyboard
  let keyHandler = new goog.events.KeyHandler(document);
  goog.events.listen(keyHandler, 'key', goog.bind(this.handleKey, this));
};


/**
 * Forward mouse release from the tool boxes to the stage
 * Because user can drag something and move the mouse over the tool boxes
 * @param {Event} event
 */
silex.view.Stage.prototype.onMouseMoveOverUi = function(event) {
  let pos = goog.style.getRelativePosition(event, this.element);
  this.onMouseMove(/** @type {Element} */ (event.target), pos.x, pos.y, event.shiftKey);
  event.preventDefault();
};


/**
 * Forward mouse release from the tool boxes to the stage
 * Because user can drag something and release the mouse over the tool boxes
 * @param {Event} event
 */
silex.view.Stage.prototype.onMouseUpOverUi = function(event) {
  if (this.bodyElement !== null) {
    // if out of stage, release from drag of the plugin
    // simulate the mouse up on the iframe body
    var pos =  goog.style.getRelativePosition(event, this.element);
    var newEvObj = document.createEvent('MouseEvent');
    newEvObj.initEvent('mouseup', true, true);
    newEvObj.clientX = pos.x;
    newEvObj.clientY = pos.y;
    this.iAmClicking = true;
    this.bodyElement.dispatchEvent(newEvObj);
    this.iAmClicking = false;
  }
};


/**
 * Disable horizontal scrolling for back page on Mac OS,
 * Over Silex UI and over the stage
 * @param {Event} event
 */
silex.view.Stage.prototype.onPreventBackSwipe = function(event) {
  if (event.deltaX < 0 && this.getScrollX() <= 0) {
    event.preventDefault();
  }
};


/**
 * Resize the iframe body to the size of its content
 * This is to always keep space between the elements (main container etc)
 * and the stage border
 * @param {?Event=} event
 */
silex.view.Stage.prototype.bodyElementSizeToContent = function(event) {
  if (this.bodyElement) {
    let containers = [];
    goog.array.forEach(this.bodyElement.children, (element) => {
      if(element.classList.contains(silex.model.Body.EDITABLE_CLASS_NAME)) {
        containers.push(element);
      }
    });
    if (containers && containers.length > 0) {
      let bb = this.model.property.getBoundingBox(containers);
      let viewportSize = this.viewport.getSize();
      let desiredBodyWidth = bb.width + 100;
      if (desiredBodyWidth < viewportSize.width) {
        // let the css handle a body of the size of the stage
        goog.style.setStyle(this.bodyElement, 'minWidth', '');
      }
      else {
        // we want the body to be this size
        // we use minWidth/minHeight in order to leave width/height to the user
        goog.style.setStyle(
            this.bodyElement,
            'minWidth',
            desiredBodyWidth + 'px');
      }
      let desiredBodyHeight = bb.height + 100;
      if (desiredBodyHeight < viewportSize.height) {
        // let the css handle a body of the size of the stage
        goog.style.setStyle(this.bodyElement, 'minHeight', '');
      }
      else {
        // we want the body to be this size
        // we use minWidth/minHeight in order to leave width/height to the user
        goog.style.setStyle(
            this.bodyElement,
            'minHeight',
            desiredBodyHeight + 'px');
      }
    }
  }
  // else {
    // could not resize body to match content because
    // this.bodyElement is undefined
    // this happens at startup
  //}
};


/**
 * remove stage event listeners
 * @param {Element} bodyElement the element which contains the body of the site
 */
silex.view.Stage.prototype.removeEvents = function(bodyElement) {
  goog.events.removeAll(bodyElement);
};


/**
 * init stage events
 * handle mouse events for selection,
 * events of the jquery editable plugin,
 * double click to edit,
 * and disable horizontal scrolling for back page on Mac OS
 * @param {Window} contentWindow the window instance of the iframe which contains the site
 */
silex.view.Stage.prototype.initEvents = function(contentWindow) {
  this.bodyElement = contentWindow.document.body;
  this.documentElement = contentWindow.document;

  // handle resize and the iframe body size
  if (this.viewport) {
    goog.events.removeAll(this.viewport);
  }
  this.viewport = new goog.dom.ViewportSizeMonitor(contentWindow);
  goog.events.listen(this.viewport, goog.events.EventType.RESIZE,
      this.bodyElementSizeToContent, false, this);
  // init iframe body size
  this.bodyElementSizeToContent();

  // listen on body instead of element because user can release
  // on the tool boxes
  goog.events.listen(this.bodyElement, 'mouseup', function(event) {
    let x = event.clientX;
    let y = event.clientY;
    this.handleMouseUp(event.target, x, y, event.shiftKey);
  }, false, this);

  // move in the iframe
  goog.events.listen(this.bodyElement, 'mousemove', function(event) {
    let x = event.clientX;
    let y = event.clientY;
	let xPosElement = goog.dom.getElement('editor-xpos');
	xPosElement.textContent = (x + this.getScrollX()) - goog.dom.getElementByClass(silex.view.Stage.BACKGROUND_CLASS_NAME, this.bodyElement).offsetLeft;
	let yPosElement = goog.dom.getElement('editor-ypos');
	yPosElement.textContent = (y + this.getScrollY()) - goog.dom.getElementByClass(silex.view.Stage.BACKGROUND_CLASS_NAME, this.bodyElement).offsetTop;
    this.onMouseMove(/** @type {Element} */ (event.target), x, y, event.shiftKey, event.ctrlKey);
    event.preventDefault();
  }, false, this);

  // detect mouse down
  goog.events.listen(this.bodyElement, 'mousedown', function(event) {
    this.lastClickWasResize = goog.dom.classlist.contains(
        event.target,
        'ui-resizable-handle');
    this.resizeDirection = this.getResizeDirection(event.target);
    let x = event.clientX;
    let y = event.clientY;
    // get the first parent node which is editable (silex-editable css class)
    let editableElement = goog.dom.getAncestorByClass(
        event.target,
        silex.model.Body.EDITABLE_CLASS_NAME) || this.bodyElement;
    this.handleMouseDown(editableElement, x, y, event.shiftKey);
    // necessary in firefox to prevent default image drag
    event.preventDefault();
  }, false, this);

  // detect double click
  
  goog.events.listen(
    this.bodyElement,
    goog.events.EventType.DBLCLICK,
    function(event) {
      this.controller.editMenuController.editElement();
    }, false, this);
  
};


/**
 * redraw the properties
 * @param   {Array.<HTMLElement>} selectedElements the selected elements
 * @param   {Array.<string>} pageNames   the names of the pages
 * @param   {string}  currentPageName   the name of the current page
 */
silex.view.Stage.prototype.redraw =
    function(selectedElements, pageNames, currentPageName) {
  // reset focus out of the text inputs,
  // this also prevents a bug when the page is loaded and the user presses a key,
  // the body is replaced by the keys chars
  this.resetFocus();
  // remember selection
  this.selectedElements = selectedElements;
  this.bodyElementSizeToContent();
  this.currentPageName = currentPageName;
};


/**
 * handle key strikes, look for arrow keys to move selection
 * @param {Event} event
 */
silex.view.Stage.prototype.handleKey = function(event) {
  // not in text inputs
  if (event.target.tagName.toUpperCase() !== 'INPUT' &&
      event.target.tagName.toUpperCase() !== 'TEXTAREA') {
    // compute the number of pixels to move
    let amount = 10;
    if (event.ctrlKey) {
      amount = 1;
    }
    if (event.altKey === true) {
      // this is the bring forward/backward shortcut
      return;
    }
    // compute the direction
    let offsetX = 0;
    let offsetY = 0;
    switch (event.keyCode) {
      case goog.events.KeyCodes.LEFT:
        offsetX = -amount;
      break;
      case goog.events.KeyCodes.RIGHT:
        offsetX = amount;
      break;
      case goog.events.KeyCodes.UP:
        offsetY = -amount;
      break;
      case goog.events.KeyCodes.DOWN:
        offsetY = amount;
      break;
    }
    // if there is something to move
    if (offsetX !== 0 || offsetY !== 0) {
      // mark as undoable
      this.controller.stageController.markAsUndoable();
      // apply the offset
      this.followElementPosition(this.selectedElements, offsetX, offsetY, event);
      // notify the controller
      this.propertyChanged();
      // prevent default behavior for this key
      event.preventDefault();
    }
  }
};


/**
 * handle mouse up
 * notify the controller to select/deselect the element (multiple or single)
 * reset state:
 * - clicked DOM element
 * - mouse position
 * - scroll position
 * - isDown
 * @param   {Element} target a DOM element clicked by the user
 * @param   {number} x position of the mouse, relatively to the screen
 * @param   {number} y position of the mouse, relatively to the screen
 * @param   {boolean} shiftKey state of the shift key
 */
silex.view.Stage.prototype.handleMouseUp = function(target, x, y, shiftKey) {
  // update state
  this.isDown = false;
  // if it is not a click on the UI
  if (this.iAmClicking !== true) {
    this.resetFocus();
  }
  // handle the mouse up
  if (this.isDragging) {
    // new container
    let dropZone = this.getDropZone(x, y) || {'element': this.bodyElement, 'zIndex': 0};
    // move all selected elements to the new container
    goog.array.forEach(this.selectedElements, function(element) {
      if(!goog.dom.getAncestorByClass(element.parentNode, silex.model.Element.SELECTED_CLASS_NAME) &&
         !goog.dom.classlist.contains(element, silex.model.Body.PREVENT_DRAGGABLE_CLASS_NAME)) {
        this.controller.stageController.newContainer(dropZone.element, element);
      }
    }, this);
    // change z order
    //this.bringSelectionForward();
    // reset dropzone marker
    this.markAsDropZone(null);
  }
  // handle selection
  if (this.isDragging || this.isResizing) {
    // update property tool box
    this.propertyChanged();
    // keep flags up to date
    this.isDragging = false;
    this.isResizing = false;
    // style the body
    goog.dom.classlist.remove(this.bodyElement, silex.model.Body.DRAGGING_CLASS_NAME);
  }
  // if not dragging, and on stage, then change selection
  else if (this.iAmClicking !== true) {
	if (this.lassoSelected)
	{
		this.controller.stageController.handleLassoSelect();
	}
	else
	{
		// get the first parent node which is editable (silex-editable css class)
		let editableElement = goog.dom.getAncestorByClass(
			target,
			silex.model.Body.EDITABLE_CLASS_NAME) || this.bodyElement;

		// single or multiple selection
		if (shiftKey === true) {
		  // if the element is selected, then unselect it
		  if (this.lastSelected !== editableElement) {
			this.controller.stageController.deselect(editableElement);
		  }
		}
		else {
		  // if the user did not move the element,
		  // select it in case other elements were selected
		  // check if selection has changed
		  // ?? do not check if selection has changed,
		  // because it causes refresh bugs
		  // (apply border to the next selected element)
		  let hasChanged = (this.selectedElements.length === 1 &&
			  this.selectedElements[0] === editableElement);
		  if (!hasChanged) {
			// update selection
			this.controller.stageController.select(editableElement);
		  }
		}
	}
  }
};


/**
 * remove the focus from text fields
 */
silex.view.Stage.prototype.resetFocus = function() {
  this.invalidationManagerFocus.callWhenReady(() => {
    this.focusInput.focus();
    this.focusInput.blur();
  });
};


/**
 * bring the selection forward
 */
silex.view.Stage.prototype.bringSelectionForward = function() {
  goog.array.forEach(this.selectedElements, function(element) {
    let container = element.parentNode;
    goog.dom.removeNode(element);
    goog.dom.appendChild(container, element);
  }, this);
};


/**
 * Handle mouse move
 * If the mouse button isDown, then
 * - compute the offset of the mouse from the last known position
 * - handle the scroll position changes
 *       (while dragging an element near the border of the stage, it may scroll)
 * - apply the ofset to the dragged or resized element(s)
 * @param   {Element} target a DOM element clicked by the user
 * @param   {number} x position of the mouse, relatively to the screen
 * @param   {number} y position of the mouse, relatively to the screen
 * @param   {boolean} shiftKey true if shift is down
 */
silex.view.Stage.prototype.onMouseMove = function(target, x, y, shiftKey, ctrlKey) {
  if (!this.paletteItemSelected)
  {
	  var editableElement = goog.dom.getAncestorByClass(target, silex.model.Body.EDITABLE_CLASS_NAME) || this.bodyElement;
	  let compType = silex.utils.CTAT.getCTATClassName(editableElement);
	  if (compType)
	  {
		compType = silex.utils.CTAT.splitCTATClassName(compType);  
	  }
	  else
	  {
		compType = silex.utils.Dom.getSilexType(editableElement);
	  }
	  this.setCompType(compType);  
	  // update states
	  if (this.isDown) 
	  {
		if (this.lassoSelected)
		{
			this.controller.stageController.updateLassoBox(x, y);
		}	
		else
		{
			// det the drop zone under the cursor
			let dropZone = this.getDropZone(x, y) || {'element': this.bodyElement, 'zIndex': 0};
			// handle the css class applyed to the dropzone
			this.markAsDropZone(dropZone.element);
			// update property tool box
			this.propertyChanged();
			// case of a drag directly after mouse down (select + drag)
			if (this.lastSelected === null) {
			  this.lastSelected = editableElement;
			}
			// update states
			if (!this.isDragging && !this.isResizing) {
			  // notify controller that a change is about to take place
			  // marker for undo/redo
			  this.controller.stageController.markAsUndoable();
			  // store the state for later use
			  if (this.lastClickWasResize) {
				this.isResizing = true;
			  }
			  else {
				console.trace();
				this.isDragging = true;
				// dragging style
				goog.dom.classlist.add(this.bodyElement, silex.model.Body.DRAGGING_CLASS_NAME);
			  }
			}
			else {
			  // keep the body size while dragging or resizing
			  this.bodyElementSizeToContent();
			}

			// update multiple selection according the the dragged element
			this.multipleDragged(x, y, shiftKey, ctrlKey);
		}

		// update scroll when mouse is near the border
		this.updateScroll(x, y);
	  }
  }
  else
  {
	  //update position of phantom element
	  this.controller.stageController.setPhantomNodePos(x, y);
	  // get the drop zone under the cursor
	  let dropZone = this.getDropZone(x, y) || {'element': this.bodyElement, 'zIndex': 0};
	  // handle the css class applyed to the dropzone
	  this.markAsDropZone(dropZone.element);
  }
};


/**
 * add a css class to the drop zone
 * and remove from non dropzones
 * @param {?Element=} opt_element to be marked
 */
silex.view.Stage.prototype.markAsDropZone = function(opt_element) {
  let els = goog.dom.getElementsByClass(silex.model.Body.DROP_CANDIDATE_CLASS_NAME, /** @type {Element|null} */ (this.bodyElement.parentNode));
  goog.array.forEach(els, (event) => goog.dom.classlist.remove(/** @type {Element} */ (event), silex.model.Body.DROP_CANDIDATE_CLASS_NAME));
  if (opt_element) {
    opt_element.classList.add(silex.model.Body.DROP_CANDIDATE_CLASS_NAME);
  }
};


/**
 * recursively get the top most element which is under the mouse cursor
 * excludes the selected elements
 * takes the zIndex into account, or the order in the DOM
 *
 * @param {number} x    mouse position
 * @param {number} y    mouse position
 * @param {?Element=} opt_container   element into which to seach for the dropzone, by default the body
 * @return {{element: ?Element, zIndex: number}}  if not null this is the drop zone under the mouse cursor
 *                                              zIndex being the highest z-index encountered while browsing children
 */
silex.view.Stage.prototype.getDropZone = function(x, y, opt_container) {
  // default value
  let container = opt_container || this.bodyElement;
  let children = goog.dom.getChildren(container);
  let topMost = null;
  let zTopMost = 0;
  // find the best drop zone
  for (let idx = 0; idx < children.length; idx++) {
    let element = children[idx];
    if (element.className.includes('container-element') &&
      !goog.dom.classlist.contains(element, silex.model.Body.PREVENT_DROPPABLE_CLASS_NAME) &&
      !goog.dom.classlist.contains(element, 'silex-selected') &&
      this.getVisibility(element)
      ) {
        let bb = goog.style.getBounds(element);
        let scrollX = this.getScrollX();
        let scrollY = this.getScrollY();
        if (bb.left < x + scrollX && bb.left + bb.width > x + scrollX &&
            bb.top < y + scrollY && bb.top + bb.height > y + scrollY) {
              let candidate = this.getDropZone(x, y, element);
              // if zIndex is 0 then there is no value to css zIndex, considere the DOM order
              if (candidate.element) {
                let zIndex = goog.style.getComputedZIndex(element);
                if (zIndex === 'auto') {
                  zIndex = 0;
                }
                if (zIndex >= zTopMost) {
                  topMost = candidate;
                  zTopMost = zIndex;
                  // keep track of the highest z-index in for the given result
                  if (zIndex > candidate.zIndex) {
                    candidate.zIndex = /** @type {number} */ (zIndex);
                  }
                }
              }
            }
      }
  }
  return topMost || {'element': container, 'zIndex': 0};
};


/**
 * compute the page visibility of the element
 * @param {Element} element     the element to check
 * @return {boolean} true if the element is in the current page or not in any page
 */
silex.view.Stage.prototype.getVisibility = function(element) {
  /** @type {?Element} */
  let parent = /** @type {?Element} */ (element);
  while (parent &&
         (!goog.dom.classlist.contains(/** @type {Element} */ (parent), silex.model.Page.PAGED_CLASS_NAME) ||
          goog.dom.classlist.contains(/** @type {Element} */ (parent), this.currentPageName))) {
    parent = /** @type {?Element} */ (parent.parentNode);
  }
  return parent === null;
};

/**
 * Handle the case where mouse is near a border of the stage
 * and an element is being dragged
 * Then scroll accordingly
 * @param   {number} x position of the mouse, relatively to the screen
 * @param   {number} y position of the mouse, relatively to the screen
 */
silex.view.Stage.prototype.updateScroll = function(x, y) {
  this.invalidationManagerScroll.callWhenReady(() => {
    let iframeSize = goog.style.getSize(this.element);
    let scrollX = this.getScrollX();
    let scrollY = this.getScrollY();
    if (x < 30) {
      this.setScrollX(scrollX - 25);
    }
    else if (x > iframeSize.width - 30) {
      this.setScrollX(scrollX + 25);
    }
    if (y < 30) {
      this.setScrollY(scrollY - 25);
    }
    else if (y > iframeSize.height - 30) {
      this.setScrollY(scrollY + 25);
    }
  });
};


/**
 * Make selected elements move as the dragged element is moving
 * Compute the offset compared to the last mouse move
 * Take the scroll delta into account (changes when dragging outside the stage)
 * @param   {number} x position of the mouse, relatively to the screen
 * @param   {number} y position of the mouse, relatively to the screen
 * @param   {boolean} shiftKey state of the shift key
 */
silex.view.Stage.prototype.multipleDragged = function(x, y, shiftKey, ctrlKey) {
  let scrollX = this.getScrollX();
  let scrollY = this.getScrollY();
  let doUpdateX = true, doUpdateY = true;
  // follow the mouse (this means that the element dragged by the editable plugin
  // is handled here, which overrides the behavior of the plugin
  // (this is because we take the body scroll into account, and the parent's scroll too)
  let followers = this.selectedElements;
  // drag or resize
  if (this.isDragging || this.resizeDirection === null) {
    // handle shift key to move on one axis or preserve ratio
    if (shiftKey === true) 
	{
      if (Math.abs((this.initialPos.x + this.initialScroll.x) - (x + scrollX)) < Math.abs((this.initialPos.y + this.initialScroll.y) - (y + scrollY)))
	  {
        x = this.initialPos.x + this.initialScroll.x - scrollX; //keep x constant
      }
      else 
	  {
        y = this.initialPos.y + this.initialScroll.y - scrollY; //keep y constant
      }
    }
    let offsetX = x - this.lastPosX + (scrollX - this.lastScrollLeft);
    let offsetY = y - this.lastPosY + (scrollY - this.lastScrollTop);
	if (this.snapToGrid && !ctrlKey)
	{
		if (Math.abs(this.lastPosX - x) < 20)
		{	
			offsetX = 0;
			doUpdateX = false;
		}
		if (Math.abs(this.lastPosY - y) < 20)
		{
			offsetY = 0;
			doUpdateY = false;
		}
    }
	if (doUpdateY || doUpdateX)
		this.followElementPosition(followers, offsetX, offsetY, {ctrlKey: ctrlKey});
  }
  else if (this.isResizing) {
    // handle shift key to move on one axis or preserve ratio
    if (shiftKey === true &&
      (this.resizeDirection === 'sw' ||
          this.resizeDirection === 'se' ||
          this.resizeDirection === 'nw' ||
          this.resizeDirection === 'ne'
    )) {
      let width = x - this.initialPos.x;
      if (this.resizeDirection === 'ne' || this.resizeDirection === 'sw') {
        width = -width;
      }
      y = (this.initialPos.y) + (width * this.initialRatio);
    }
    let offsetX = x - this.lastPosX + (scrollX - this.lastScrollLeft);
    let offsetY = y - this.lastPosY + (scrollY - this.lastScrollTop);
    if (this.snapToGrid && !ctrlKey)
	{
		if (Math.abs(this.lastPosX - x) < 20)
		{	
			offsetX = 0;
			doUpdateX = false;
		}
		if (Math.abs(this.lastPosY - y) < 20)
		{
			offsetY = 0;
			doUpdateY = false;
		}
    }
	if (doUpdateX || doUpdateY)
		this.followElementSize(followers, this.resizeDirection, offsetX, offsetY);
  }

  // update the latest position and scroll
  if (doUpdateX)
	this.lastPosX = x;
  if (doUpdateY)
	this.lastPosY = y;
  this.lastScrollLeft = scrollX;
  this.lastScrollTop = scrollY;
};


/**
 * make the followers follow the element's position
 * @param   {Array.<HTMLElement>} followers which will follow the elements
 * @param   {number} offsetX the delta to be applied
 * @param   {number} offsetY the delta to be applied
 */
silex.view.Stage.prototype.followElementPosition =
  function(followers, offsetX, offsetY, e) 
  {
	if (this.snapToGrid && !(e && (e.ctrlKey || e.keyCode)))
	{
		if (offsetX !== 0)
			offsetX = (Math.abs(offsetX) > 20 ) ? Math.round(offsetX/20) * 20 : ((offsetX < 0) ? -20 : 20);
		if (offsetY !== 0)
			offsetY = (Math.abs(offsetY) > 20 ) ? Math.round(offsetY/20) * 20 : ((offsetY < 0) ? -20 : 20);
	}
    // apply offset to other selected element
    goog.array.forEach(followers, function(follower) {
      // do not move an element if one of its parent is already being moved
      // or if it is the stage
      // or if it has been marked as not draggable
  	  if (follower.tagName.toUpperCase() !== 'BODY' &&
		!goog.dom.getAncestorByClass(follower.parentNode, silex.model.Element.SELECTED_CLASS_NAME) &&
		!goog.dom.classlist.contains(follower, silex.model.Body.PREVENT_DRAGGABLE_CLASS_NAME)) {
		 let pos = {};
		 pos.x = this.model.element.getStyle(follower, 'left');
		 pos.y = this.model.element.getStyle(follower, 'top');
		 pos.x = parseInt(pos.x.substring(0, pos.x.length-2), 10);
		 pos.y = parseInt(pos.y.substring(0, pos.y.length-2), 10);
		 this.controller.stageController.styleChanged('top', Math.round(pos.y + offsetY) + 'px', [follower], false);
		 this.controller.stageController.styleChanged('left', Math.round(pos.x + offsetX) + 'px', [follower], false);
		}
	}, this);
};


/**
 * make the followers follow the element's size
 * @param   {Array.<HTMLElement>} followers which will follow the elements
 * @param   {string} resizeDirection the direction n, s, e, o, ne, no, se, so
 * @param   {number} offsetX the delta to be applied
 * @param   {number} offsetY the delta to be applied
 */
silex.view.Stage.prototype.followElementSize =
  function(followers, resizeDirection, offsetX, offsetY) {
  // apply offset to other selected element
  goog.array.forEach(followers, function(follower) {
    // do not resize the stage or the un-resizeable elements
    if (follower.tagName.toUpperCase() !== 'BODY'
      && !goog.dom.classlist.contains(follower, silex.model.Body.PREVENT_RESIZABLE_CLASS_NAME)) {
      var offsetPosX = this.model.element.getStyle(follower, 'left');
      var offsetPosY = this.model.element.getStyle(follower, 'top');
	  offsetPosX = parseInt(offsetPosX.substring(0, offsetPosX.length-2), 10);
	  offsetPosY = parseInt(offsetPosY.substring(0, offsetPosY.length-2), 10);
	  var offsetSizeX = offsetX;
      var offsetSizeY = offsetY;
      // depending on the handle which is dragged,
      // only width and/or height should be set
      switch (resizeDirection) {
      case 's':
        offsetSizeX = 0;
		offsetPosY = null;
		offsetPosX = null;
        break;
      case 'n':
        offsetPosY += offsetSizeY;
        offsetSizeY = -offsetSizeY;
        offsetSizeX = 0;
        break;
      case 'w':
        offsetPosX += offsetSizeX;
        offsetSizeX = -offsetSizeX;
        offsetSizeY = 0;
        break;
      case 'e':
        offsetSizeY = 0;
        offsetPosY = null;
		offsetPosX = null;
		break;
      case 'se':
		offsetPosY = null;
		offsetPosX = null;
        break;
      case 'sw':
        offsetPosX += offsetSizeX;
        offsetSizeX = -offsetSizeX;
        break;
      case 'ne':
        offsetPosY += offsetSizeY;
        offsetSizeY = -offsetSizeY;
        break;
      case 'nw':
        offsetPosX += offsetSizeX;
        offsetPosY += offsetSizeY;
        offsetSizeY = -offsetSizeY;
        offsetSizeX = -offsetSizeX;
        break;
      }
      // handle .background element which is forced centered
      if(goog.dom.classlist.contains(follower, 'background')) {
        offsetSizeX *= 2;
      }
      // compute new size
	  var size = {};
	  size.width = $(follower).width();
	  size.height = $(follower).height();
	  var newSizeW = (offsetSizeX !== 0) ? size.width + offsetSizeX : null;
      var newSizeH = (offsetSizeY !== 0) ? size.height + offsetSizeY: null;
      // handle min size
      if (newSizeW && (newSizeW < silex.model.Element.MIN_WIDTH)) {
        if (resizeDirection === 'w' || resizeDirection === 'sw' || resizeDirection === 'nw') {
          offsetPosX -= silex.model.Element.MIN_WIDTH - newSizeW;
        }
        newSizeW = silex.model.Element.MIN_WIDTH;
      }
      if (newSizeH && (newSizeH < silex.model.Element.MIN_HEIGHT)) {
        if (resizeDirection === 'n' || resizeDirection === 'ne' || resizeDirection === 'nw') {
          offsetPosY -= silex.model.Element.MIN_HEIGHT - newSizeH;
        }
        newSizeH = silex.model.Element.MIN_HEIGHT;
      }
      // set position in case we are resizing up or left
      offsetPosY && this.controller.stageController.styleChanged('top', Math.round(offsetPosY) + 'px', [follower], false);
      offsetPosX && this.controller.stageController.styleChanged('left', Math.round(offsetPosX) + 'px', [follower], false);
      // apply the new size
      newSizeW && this.controller.stageController.styleChanged('width', Math.round(newSizeW) + 'px', [follower], false);
      newSizeH && this.controller.stageController.styleChanged('height', Math.round(newSizeH) + 'px', [follower], false);
    }
  }, this);
};


/**
 * handle mouse down
 * notify the controller to select the element (multiple or single)
 * store state:
 * - clicked DOM element
 * - mouse position
 * - scroll position
 * - isDown
 * @param   {Element} element Silex element currently selected (text, image...)
 * @param   {number} x position of the mouse, relatively to the screen
 * @param   {number} y position of the mouse, relatively to the screen
 * @param   {boolean} shiftKey state of the shift key
 */
silex.view.Stage.prototype.handleMouseDown = function(element, x, y, shiftKey) {
  this.lastSelected = null;
  // if the element was not already selected
  if (!this.lassoSelected)
  {  
	if (!goog.dom.classlist.contains(element, silex.model.Element.SELECTED_CLASS_NAME)) 
	{
		this.lastSelected = element;
		// notify the controller
		if (shiftKey) {
		  this.controller.stageController.selectMultiple(element);
		}
		else {
		  this.controller.stageController.select(element);
		}
	}
  }
  else
  {
	  this.controller.stageController.initLassoBox(x, y, element)
  }
  // keep track of the last mouse position and body scroll
  this.lastPosX = x;
  this.lastPosY = y;
  this.lastScrollLeft = this.getScrollX();
  this.lastScrollTop = this.getScrollY();
  let initialSize = goog.style.getSize(element);
  this.initialRatio = initialSize.height / initialSize.width;
  this.initialPos = {x: x, y: y};
  this.initialScroll = {x: this.getScrollX(), y: this.getScrollY()};
  // update state
  this.isDown = true;
};


/**
 * check if the target is a UI handle to resize or move -draggable jquery plugin
 * @param   {Element} target a DOM element clicked by the user,
 *                    which may be a handle to resize or move
 * @return {?string}
 */
silex.view.Stage.prototype.getResizeDirection = function(target) {
  var direction = null;
  if (goog.dom.classlist.contains(target, 'ui-resizable-s')) {
    direction = 's';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-n')) {
    direction = 'n';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-e')) {
    direction = 'e';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-w')) {
    direction = 'w';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-se')) {
    direction = 'se';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-sw')) {
    direction = 'sw';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-ne')) {
    direction = 'ne';
  } else if (goog.dom.classlist.contains(target, 'ui-resizable-nw')) {
    direction = 'nw';
  }
  return direction;
};


/**
 * get the scroll property, working around cross browser issues
 * @param {number} value to be set
 */
silex.view.Stage.prototype.setScrollX = function(value) {
  let dh = new goog.dom.DomHelper(this.documentElement);
  dh.getDocumentScrollElement().scrollLeft = value;
};


/**
 * get the scroll property, working around cross browser issues
 * @param {number} value to be set
 */
silex.view.Stage.prototype.setScrollY = function(value) {
  let dh = new goog.dom.DomHelper(this.documentElement);
  dh.getDocumentScrollElement().scrollTop = value;
};


/**
 * get the scroll property, working around cross browser issues
 * FIXME: no need for getScrollX and getScrollY, should be getScroll which returns coords
 * @return {number} the value
 */
silex.view.Stage.prototype.getScrollX = function() {
  let dh = new goog.dom.DomHelper(this.documentElement);
  return dh.getDocumentScroll().x;
};


/**
 * get the scroll property, working around cross browser issues
 * FIXME: no need for getScrollX and getScrollY, should be getScroll which returns coords
 * @return {number} the value
 */
silex.view.Stage.prototype.getScrollY = function() {
  let dh = new goog.dom.DomHelper(this.documentElement);
  return dh.getDocumentScroll().y;
};


/**
 * get the scroll property, working around cross browser issues
 * @return {number} the value
 */
silex.view.Stage.prototype.getScrollMaxX = function() {
  let dh = new goog.dom.DomHelper(this.documentElement);
  return goog.style.getSize(dh.getDocumentScrollElement()).width;
};


/**
 * get the scroll property, working around cross browser issues
 * @return {number} the value
 */
silex.view.Stage.prototype.getScrollMaxY = function() {
  let dh = new goog.dom.DomHelper(this.documentElement);
  return goog.style.getSize(dh.getDocumentScrollElement()).height;
};


/**
 * notify the controller that the properties of the selection have changed
 */
silex.view.Stage.prototype.propertyChanged = function() {
  // update property tool box
  this.controller.stageController.updateView();
};

/**
 *	Initialize dialog to edit text boxes
 *	@param doShow if true will display the window
 *	@param selected the currently selected element on the stage
 */
silex.view.Stage.prototype.initTextEditWindow = function(doShow, selected)
{
	this.textEditWindow = new CTATTextEdit('#text-editor');
	if (doShow) this.showTextEditWindow(selected);
};

/**
 *	Display the dialog used to edit text boxes
 */
silex.view.Stage.prototype.showTextEditWindow = function(selected)
{
	this.textEditWindow.show(selected);
	toggleBlocker(true);
};

/**
 *	Display the dialog used to create/edit questions
 *	@param type the type of question (multiple choice, etc)
 */
silex.view.Stage.prototype.showQuestionDialog = function(type)
{
	switch(type)
	{
		case 'multchoice':
			this.showMultChoiceWindow();
		break;
	}
};

/**
*	Display the multiple choice question editor window
*	@param selected the currently selected element on the stage
*/
silex.view.Stage.prototype.showMultChoiceWindow = function(selected, cbk)
{
	if (!this.multChoiceWindow)
	{
		this.multChoiceWindow = new CTATMultChoice('#mult-choice-dialog');
	}
	this.multChoiceWindow.show(selected, cbk);
	toggleBlocker(true);
};

/**
*	Display the CSS editor window
*	@param content the contents of the <style> tag being edited
*	@param cbk a function to which the edited content is passed
*/
silex.view.Stage.prototype.showCSSWindow = function(content, cbk)
{
	if (!this.cssWindow)
	{
		this.cssWindow = new CTATCSSEdit('#css-editor');
	}
	this.cssWindow.show(content, cbk);
}

/**
*	Display the file import dialog window
*	@param filetype the type of file to import
*	@param callbackOne function to call if the URL given is absolute
*	@param callbackTwo function to call if using a file from the cloud
*/
silex.view.Stage.prototype.showFileSourceWindow = function(filetype, callbackOne, callbackTwo)
{
	if (!this.fileSourceWindow)
	{
		this.fileSourceWindow = new CTATImageSource('#img-source-dialog');
	}
	this.fileSourceWindow.show(callbackOne, callbackTwo);
	this.fileSourceWindow.setMode(filetype);
	toggleBlocker(true);
};

/**
*	Display group editor window
*	@param mode either 'create' (new group) or 'edit' (existing group)
*	@param selected the currently selected elements on the stage
*	@param opt_groupName optional name for the group
*/
silex.view.Stage.prototype.showGroupWindow = function(mode, selected, opt_groupName)
{
	if (!this.groupWindow)
	{
		this.groupWindow = new CTATGroupDialog('#ctat-group-dialog');
	}
	this.groupWindow.show(mode, selected, opt_groupName);
}

/**
*	Enables or disables snap-to-grid behavior when dragging and resizing
*	@param enable (boolean) whether or not to enable snap behavior
*/
silex.view.Stage.prototype.setSnapToGrid = function(enable)
{
	this.snapToGrid = enable;
};

/**
*	Returns whether snap-to-grid behavior is currently enabled (boolean)
*/
silex.view.Stage.prototype.getSnapToGrid = function()
{
	return this.snapToGrid;
}

/**
*	Highlights or removes highlight from all members of a given group on the stage
*	@param groupId the ID of the group whose members should be highlighted
*	@param doHighlight whether or not to highlight the elements
*/
silex.view.Stage.prototype.toggleGroupHighlight = function(groupId, doHighlight)
{
	if (doHighlight && this.highlighted === groupId)
		return;
	
	var doc = this.model.file.getContentDocument();
	//get ctatgroupingcomponent
	var group = this.model.file.getContentDocument().getElementById(groupId);
	if (group)
	{
		//get members
		var memberIds = group.getAttribute('data-ctat-componentlist').split(',');
		if (doHighlight) //add highlight elements
		{
			for (let i = 0; i < memberIds.length; i++)
			{
				let member = doc.getElementById(memberIds[i]);
				if (member)
				{
					let highlight = doc.createElement('div');
					highlight.classList.add('silex-highlight-screen');
					member.appendChild(highlight);
				}
			}
			this.highlighted = groupId;
		}
		else //remove highlight elements
		{
			for (let i = 0; i < memberIds.length; i++)
			{
				let highlight = doc.querySelector('#'+memberIds[i]+' > .silex-highlight-screen');
				highlight && highlight.parentElement.removeChild(highlight);
			}
			this.highlighted = null;
		}
	}
};

/**
*	Update component type field of status bar
*	@param type the type of the component the mouse is currently over
*/
silex.view.Stage.prototype.setCompType = function(type)
{
	var typeField = document.getElementById('silex-statusbar-type-field');
	if (typeField)
	{
		typeField.textContent = type;
	}
	else
	{
		console.warn('Couldn\'t find type field!');
	}
};

/**
*	Update the status field of the status bar
*	@param status the status to set
*/
silex.view.Stage.prototype.setStatus = function(status)
{
	console.log('stage.setStatus ( '+status+' )');
	var statusField = document.getElementById('silex-statusbar-status-field');
	if (statusField)
	{
		console.log('found status field');
		statusField.textContent = status;
	}
	else
		console.warn("Silex couldn't find the statusbar!");
};

/**
*	Update the filename field of the status bar
*	@param filename the filename to set
*/
silex.view.Stage.prototype.setFilename = function(filename)
{
	console.log('stage.setFilename ('+filename+')');
	var nameField = document.getElementById('silex-statusbar-filename-field');
	if (nameField)
	{
		nameField.textContent = filename;
	}
}

/**
*	Return the value of the x,y mouse coordinate fields in the status bar
*	@returns object with properties 'x' and 'y'
*/
silex.view.Stage.prototype.getMousePos = function()
{
	let xPosElement = goog.dom.getElement('editor-xpos');
	let yPosElement = goog.dom.getElement('editor-ypos');
	return {
		x: xPosElement.textContent,
		y: yPosElement.textContent
	};
};

silex.view.Stage.prototype.addPhantomNode = function(sizeStr)
{
	this.controller.stageController.addPhantomNode(sizeStr);
};

silex.view.Stage.prototype.removePhantomNode = function()
{
	this.controller.stageController.removePhantomNode();
};

silex.view.Stage.prototype.getLassoSelected = function()
{
	return this.lassoSelected;
}

silex.view.Stage.prototype.setLassoSelected = function(isSelected)
{
	this.lassoSelected = isSelected;
}