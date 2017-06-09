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
goog.provide('silex.controller.EditMenuController');

goog.require('silex.controller.ControllerBase');
goog.require('silex.service.SilexTasks');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.EditMenuController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
  // init clipboard
  this.clipboard = [];
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.EditMenuController, silex.controller.ControllerBase);


/**
 * storage for the clipboard
 * @type Array.<silex.types.ClipboardItem>
 */
silex.controller.EditMenuController.prototype.clipboard = null;


/**
 * storage for the clipboard
 * @type {?Element}
 */
//silex.controller.EditMenuController.prototype.clipboardParent = null;

silex.controller.EditMenuController.prototype.focus = function()
{
	var selected = this.view.stage.selectedElements[0];
	if (selected)
	{
		var stageWindow = this.model.file.getContentWindow();
		stageWindow.CTATTutor.callComponentFunction(selected, function(component)
			{
				var compElement = component.getComponent();
				compElement.focus();
			});
	}
}


/**
 * undo the last action
 */
silex.controller.EditMenuController.prototype.undo = function() {
  if (silex.controller.ControllerBase.getStatePending === 0 &&
    silex.controller.ControllerBase.undoHistory.length > 0) {
    var state = this.getState();
    silex.controller.ControllerBase.redoHistory.push(state);
    var prevState = silex.controller.ControllerBase.undoHistory.pop();
    this.restoreState(prevState);
  }
  else if (silex.controller.ControllerBase.getStatePending > 0)
  {
	  console.log('Undo state pending, ignoring undo...');
  }
  else
  {
	  console.log('Undo history length = 0, ignoring undo...');
  }
};


/**
 * redo the last action
 */
silex.controller.EditMenuController.prototype.redo = function() {
  if (silex.controller.ControllerBase.redoHistory.length > 0) {
    var state = this.getState();
    silex.controller.ControllerBase.undoHistory.push(state);
    var prevState = silex.controller.ControllerBase.redoHistory.pop();
    this.restoreState(prevState);
  }
};


/**
 * copy the selection for later paste
 */
silex.controller.EditMenuController.prototype.copySelection = function() {
  this.tracker.trackAction('controller-events', 'info', 'copy', 0);
  // default is selected element
  var elements = this.model.body.getSelection();
  if (elements.length > 0) {
    // reset clipboard
    silex.controller.ControllerBase.clipboard = [];
    // add each selected element to the clipboard
    goog.array.forEach(elements, function(element) {
      if ((this.model.body.getBodyElement() !== element)
	  &&  (element.getAttribute('data-silex-id') !== 'background-initial'))
  	  {
		// disable editable
        this.model.body.setEditable(element, false);
        // copy the element and its children
        silex.controller.ControllerBase.clipboard.push(this.recursiveCopy(element));
        // re-enable editable
        this.model.body.setEditable(element, true);
        // update the views
        this.model.body.setSelection(this.model.body.getSelection());
      }
      else {
        console.error('could not copy this element (', element, ') because it is the stage element');
      }
    }, this);
  }
};



/**
 * make a copy of a Silex element and its sub-elements (for containers)
 * @param {Element} element
 */
silex.controller.EditMenuController.prototype.recursiveCopy = function(element) {
  // duplicate the node
  var res = {
    element: element.cloneNode(true),
    style: this.model.property.getStyleObject(element),
    children: []
  };
  // remove dynamically generated ctat content
  silex.utils.DomCleaner.cleanupCTAT(res.element);
  // case of a container, handle its children
  if (this.model.element.getType(res.element) === silex.model.Element.TYPE_CONTAINER) {
    let toBeRemoved = [];
    let len = res.element.childNodes.length;
    for (let idx = 0; idx < len; idx++) {
      let el = res.element.childNodes[idx];
      if (el.nodeType === 1 && this.model.element.getType(el) !== null) {
        res.children.push(this.recursiveCopy(el));
        toBeRemoved.push(el);
      }
    }
    toBeRemoved.forEach((el) => res.element.removeChild(el));
  }
  return res;
};


/**
 * paste the previously copied element
 */
silex.controller.EditMenuController.prototype.pasteSelection = function() {
  this.tracker.trackAction('controller-events', 'info', 'paste', 0);
  // default is selected element
  if (silex.controller.ControllerBase.clipboard) {
    // undo checkpoint
    this.undoCheckPoint();
    // find the container: original container, main background container or the stage
    var container;
    container = goog.dom.getElementByClass(silex.view.Stage.BACKGROUND_CLASS_NAME, this.model.body.getBodyElement());
    if (!container) {
      container = this.model.body.getBodyElement();
    }
    // take the scroll into account (drop at (100, 100) from top left corner of the window, not the stage)
    var doc = this.model.file.getContentDocument();
    var elements = silex.controller.ControllerBase.clipboard.map(function(item) {return item.element;});
    var selection = [];
    // duplicate and add to the container
    goog.array.forEach(silex.controller.ControllerBase.clipboard, function(clipboardItem) {
      var element = this.recursivePaste(clipboardItem);
      // add to the selection
      selection.push(element);
      // reset editable option
      this.doAddElement(element);
      // add to stage and set the "silex-just-added" css class
      this.model.element.addElement(/** @type {Element} */(container), element);
	  //add element-specific listeners
	  if (element.getAttribute('data-silex-type') === 'text')
	  {
		  element.addEventListener('dblclick', this.model.element.textEditListener.bind(this.model.element, element));
	  }
	}, this);
    // apply the offset to the elements, according to the scroll position
    var offsetX, offsetY;
	var bb = this.model.property.getBoundingBox(selection);
	if (($('.silex-stage:hover').length != 0)
	||	($('#silex-rightclick-context-menu:hover').length != 0)) //mouse is over stage, use mouse to position
	{
		var mousePos = this.view.stage.getMousePos();
		offsetX = mousePos.x; 
		offsetY = mousePos.y; 
	}
	else
	{
		offsetX = bb.left;
		offsetY = bb.top;
		var regex = new RegExp("{[^}]*top: *"+offsetY+"px[^}]*left: *"+offsetX+"px[^}]*}");
		var inlineStyles = this.model.property.updateSilexStyleTag(this.model.file.getContentDocument(), false);
		while (regex.test(inlineStyles))
		{
			offsetX += 20;
			offsetY += 20;
			regex = new RegExp("{[^}]*top: *"+offsetY+"px[^}]*left: *"+offsetX+"px[^}]*}");
		}
	}
    this.view.stage.followElementPosition(
      selection,
      offsetX - bb.left,
      offsetY - bb.top
    );
    // reset selection
    this.model.body.setSelection(selection);
	this.model.file.getContentWindow().CTATTutor.initializeHTMLComponents();
	selection.forEach(function(el)
	{
		el.classList.add(silex.model.Element.JUST_ADDED_CLASS_NAME);
	});
  }
};


/**
 * paste a Silex element and its sub-elements (for containers)
 * @param {silex.types.ClipboardItem} clipboardItem
 * @return {Element}
 */
silex.controller.EditMenuController.prototype.recursivePaste = function(clipboardItem) {
  var element = clipboardItem.element.cloneNode(true);
  // reset the ID
  this.model.property.initSilexId(element);
  // gen new ctat ID
  if (element.className.includes('CTAT'))
  {
	  // generate new id for copy
	  var stageFrame = this.model.file.getContentWindow();
	  var newId = stageFrame.CTATGlobalFunctions.gensym.div_id();
	  element.setAttribute('id', newId);
  }
  // keep the original style
  let selector = '.' + this.model.property.getSilexId(element);
  this.model.property.setStyle(selector, clipboardItem.style);
  // add its children
  goog.array.forEach(clipboardItem.children, function(childItem) {
    var childElement = this.recursivePaste(childItem);
    // add to stage
    this.model.element.addElement(element, childElement);
    }, this);

  if (element.className.includes('multchoice-element'))
	{
		  console.log('pasting multchoice question');
		  element.addEventListener('dblclick', this.view.stage.showMultChoiceWindow.bind(this.view.stage, element));
		  //gen new id
		  let oldId = element.getAttribute('id');
		  let suffix = 1;
		  let doc = this.model.file.getContentDocument();
		  while (doc.getElementById(oldId + suffix))
		  {
			  suffix++;
		  }
		  element.setAttribute('id', oldId+suffix);
		  let options = $(element).find('.ctat-multchoice-option');
		  console.log('found '+options.length+' options');
		  for (let i = 0; i < options.length; i++)
		  {
			options[i].setAttribute('name', oldId+suffix);  
		  }
	}
  
  return element;
};


/**
 * remove selected elements from the stage
 */
silex.controller.EditMenuController.prototype.removeSelectedElements = function() {
  this.undoCheckPoint();
  var elements = this.model.body.getSelection();
  // confirm and delete
  var success;
  goog.array.forEach(elements, function(element)
	{
		success = this.model.element.removeElement(element) || success;   
	}, this);
  if (success)
	this.model.body.setSelection([this.model.body.getBackground()]);
};


/**
 * edit an {Element} element
 * take its type into account and open the corresponding editor
 * @param {?HTMLElement=} opt_element
 */
silex.controller.EditMenuController.prototype.editElement = function(opt_element) {
  console.log('edit element');
  // undo checkpoint
  this.undoCheckPoint();
  // default is selected element
  var element = opt_element || this.model.body.getSelection()[0];
  switch (this.model.element.getType(element)) {
    case silex.model.Element.TYPE_TEXT:
      this.model.element.textEditListener(element);
    break;
    case silex.model.Element.TYPE_CONTAINER:
	  if (element.className.includes('multchoice-element'))
	  {
		var parent = element.parentNode;
  	    this.view.stage.showMultChoiceWindow(element, function(questionInfo)
			{
				//set undo checkpoint
				this.undoCheckPoint();
				//create the element
				questionInfo.parent = parent;
				element = this.model.element.createElement('question.multchoice', questionInfo);
			}.bind(this)
		);
	  }
	break;
  }
};


/**
 * get the previous element in the DOM, which is a Silex element
 * @see   {silex.model.element}
 * @param {Element} element
 * @return {?Element}
 */
silex.controller.EditMenuController.prototype.getPreviousElement = function(element) {
  let len = element.parentNode.childNodes.length;
  let res = null;
  for (let idx = 0; idx < len; idx++) {
    let el = element.parentNode.childNodes[idx];
    if (el.nodeType === 1 && this.model.element.getType(el) !== null) {
      if (el === element) {
        return res;
      }
      res = el;
    }
  }
  return null;
};


/**
 * get the previous element in the DOM, which is a Silex element
 * @see   {silex.model.element}
 * @param {Element} element
 * @return {?Element}
 */
silex.controller.EditMenuController.prototype.getNextElement = function(element) {
  let prev = null;
  for (let idx = element.parentNode.childNodes.length - 1; idx >= 0; idx--) {
    let el = element.parentNode.childNodes[idx];
    if (el.nodeType === 1 && this.model.element.getType(el) !== null) {
      if (el === element) {
        return prev;
      }
      prev = el;
    }
  }
  return null;
};


/**
 * get the index of the element in the DOM
 * @param {Element} element
 * @return {number}
 */
silex.controller.EditMenuController.prototype.indexOfElement = function(element) {
  let len = element.parentNode.childNodes.length;
  for (let idx = 0; idx < len; idx++) {
    if (element.parentNode.childNodes[idx] === element) {
      return idx;
    }
  }
  return -1;
};


/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 * @param  {silex.model.DomDirection} direction
 */
silex.controller.EditMenuController.prototype.move = function(direction) {
  // undo checkpoint
  this.undoCheckPoint();
  // get the selected elements
  var elements = this.model.body.getSelection();
  // sort the array
  elements.sort((a, b) => {
    return this.indexOfElement(a) - this.indexOfElement(b);
  });
  // move up
  elements.forEach((element) => {
    let reverse = this.model.element.getStyle(element, 'position', true) !== 'absolute';
    this.model.element.move(element, direction);
  });
};


/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 */
silex.controller.EditMenuController.prototype.moveUp = function() {
  this.move(silex.model.DomDirection.UP);
};


/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 */
silex.controller.EditMenuController.prototype.moveDown = function() {
  this.move(silex.model.DomDirection.DOWN);
};


/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 */
silex.controller.EditMenuController.prototype.moveToTop = function() {
  this.move(silex.model.DomDirection.TOP);
};


/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 */
silex.controller.EditMenuController.prototype.moveToBottom = function() {
  this.move(silex.model.DomDirection.BOTTOM);
};

/**
 * Move the selected elements in the DOM
 * This will move its over or under other elements if the z-index CSS properties are not set
 */
silex.controller.EditMenuController.prototype.demonstrate = function() 
{
	console.log ("Here");
	var demonstrateWindow=new CTATTutorPlayer ();
	demonstrateWindow.showOpenDialog ();  
};

/**
*	Toggle snap to grid behavior when dragging/resizing elements on the stage
*/
silex.controller.EditMenuController.prototype.toggleSnapToGrid = function(event)
{
	let enabled = this.view.stage.getSnapToGrid();
	var newLabel;
	if (enabled)
		newLabel = 'Enable Snap to Grid';
	else
		newLabel = 'Disable Snap to Grid';
	var menuItem = goog.dom.getElement('edit.snap.to.grid');
	var menuItemContent = goog.dom.getElementByClass('goog-menuitem-content', menuItem);
	menuItemContent.textContent = newLabel;
	let cntxtMenuBtn = event ? event.target : document.querySelector('#context-menu-grid');
	if (cntxtMenuBtn)
	{
		cntxtMenuBtn.setAttribute('title', newLabel);
		if (enabled) cntxtMenuBtn.classList.add('off');
		else cntxtMenuBtn.classList.remove('off');
	}
	this.view.stage.setSnapToGrid(!enabled);
}

/**
*	Toggle whether the lasso tool is enabled
*	@param event the click event on the lasso button (if any)
*/
silex.controller.EditMenuController.prototype.toggleLasso = function(event)
{
	let wasSelected = this.view.stage.getLassoSelected();
	//let the stage know
	this.view.stage.setLassoSelected(!wasSelected);
	//toggle highlight on button
	let button = event ? event.target : document.querySelector('#context-menu-lasso');
	if (wasSelected)
		button.classList.add('off');
	else
		button.classList.remove('off');
}

/**
*	Handle all 'arrange' submenu options
*	@param mode which option was clicked
*/
silex.controller.EditMenuController.prototype.arrange = function(mode)
{
	let selected = this.model.body.getSelection();
	let parents = [];
	//should affect parents only
	for (let i = 0; i < selected.length; i++)
	{
		if (!goog.dom.getAncestorByClass(selected[i].parentNode, silex.model.Element.SELECTED_CLASS_NAME))
		{
			parents.push(selected[i]);
		}
	}
	if (mode.includes('align.'))
	{
		this.align(mode.substr(6), parents);
	}
	else if (mode.includes('distribute.'))
	{
		this.distribute(mode.substr(11), parents);
	}
}

/**
*	Align all selected elements to one side of their bounding box
*	@param which which side to align to
*	@param selected all top level selected elements
*/
silex.controller.EditMenuController.prototype.align = function(which, selected)
{
	//get bounds of selection
	let bb = this.model.property.getBoundingBox(selected, true)
	var posVar, sizeVar, sizeFunc, size;
	//set up variables based on vertical or horizontal alignment
	if (which === 'left' || which ==='right' || which === 'center.x')
	{
		posVar = 'left';
		sizeVar = 'width';
		sizeFunc = silex.utils.Style.getTotalWidth;
	}
	else
	{
		posVar = 'top';
		sizeVar = 'height';
		sizeFunc = silex.utils.Style.getTotalHeight;
	}
	var totSize = which.includes('center') ? bb[sizeVar]/2 : bb[sizeVar];
	var boundary = bb[posVar];
	//align em
	if (which === 'left' || which === 'top') //the easy ones
	{
		this.styleChanged(posVar, bb[posVar]+'px', selected);
	}
	else //the less easy ones
	{
		for (let i = 0; i < selected.length; i++)
		{
			//get size
			size = sizeFunc(selected[i]);
			if (which.includes('center'))
				size /= 2;

			//apply new position (only first is undoable)
			this.styleChanged(posVar, ((boundary+totSize) - size) + 'px', [selected[i]], (i > 0 ? false : true));
		}
	}
}

/**
*	Reposition selected elements such that there is equal space between all of them
*	@param which which direction to distribute in, either 'x' or 'y'
*	@param selected all top level selected elements
*/
silex.controller.EditMenuController.prototype.distribute = function(which, selected)
{
	let bb = this.model.property.getBoundingBox(selected, true);
	let sum = 0;
	var posVar, sizeVar, sizeFunc;
	if (which === 'x')
	{
		posVar = 'left';
		sizeVar = 'width';
		sizeFunc = silex.utils.Style.getTotalWidth;
	}
	else
	{
		posVar = 'top';
		sizeVar = 'height';
		sizeFunc = silex.utils.Style.getTotalHeight;		
	}
	//get sum of dimensions and sort elements
	for (let i = 0; i < selected.length; i++)
	{
		//get element size (incl. border widths)
		sum += sizeFunc(selected[i]);
		//sort by least to greatest val of posVar
		let val = $(selected[i]).position()[posVar];
		let j = i;
		while ((j > 0 ) && (val < $(selected[j-1]).position()[posVar]))
		{
			let tmp = selected[j-1];
			selected[j-1] = selected[j];
			selected[j] = tmp;
			j--;
		}
	}
	//dist b/w each element
	let gap = (bb[sizeVar] - sum)/(selected.length-1);
	//apply gap to all elements
	for (let i = 1; i < selected.length; i++)
	{
		let last = selected[i-1];
		//new position = previous el position + previous el size + gap
		let newPos = $(last).position()[posVar] + 
					 sizeFunc(last) +
					 gap;
					 
		this.styleChanged(posVar, newPos+'px', [selected[i]], (i > 1 ? false : true));
	}
}