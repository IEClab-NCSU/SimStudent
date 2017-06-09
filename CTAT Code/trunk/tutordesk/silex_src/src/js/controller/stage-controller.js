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
goog.provide('silex.controller.StageController');

goog.require('silex.controller.ControllerBase');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.StageController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.StageController, silex.controller.ControllerBase);


/**
 * the user has selected an element
 * @param {Element} target selected element
 */
silex.controller.StageController.prototype.select = function(target) {
  this.model.body.setSelection([target]);
};


/**
 * the user has selected an element with shift
 * @param {Element} target selected element
 */
silex.controller.StageController.prototype.selectMultiple = function(target) {
  var selection = this.model.body.getSelection();
  selection.push(target);
  this.model.body.setSelection(selection);
};


/**
 * the user has clicked on an element
 * which was already selected,
 * with the shift key down
 * @param {Element} target selected element
 */
silex.controller.StageController.prototype.deselect = function(target) {
  var selection = this.model.body.getSelection();
  goog.array.remove(selection, target);
  this.model.body.setSelection(selection);
};


/**
 * callback for the stage to notify a component has been moved or resized
 */
silex.controller.StageController.prototype.updateView = function(redrawStage) {
  // refresh the toolboxes
  var selection = this.model.body.getSelection();
  this.model.body.setSelection(selection, redrawStage); 
};


/**
 * mark the state for undo/redo
 */
silex.controller.StageController.prototype.markAsUndoable = function() {
  // undo checkpoint
  this.undoCheckPoint();
};


/**
 * an element is dropped in a new container
 * @param {Element} container the container
 * @param {Element} element the dropped element
 */
silex.controller.StageController.prototype.newContainer = function(container, element) {
  if (element.parentNode !== container) {
    // initial positions
    var elementPos = goog.style.getPageOffset(element);
    var newContainerPos = goog.style.getPageOffset(container);
    // move to the new container
    this.model.element.removeElement(element, true);
    this.model.element.addElement(container, element);
    // restore position
    this.styleChanged('left', Math.round((elementPos.x - newContainerPos.x)+container.scrollLeft) + 'px', [element], false);
    this.styleChanged('top', Math.round((elementPos.y - newContainerPos.y)+container.scrollTop) + 'px', [element], false);
	if (container.className.includes('CTATDragNDrop') && !element.getAttribute('id'))
	{
		//elements inside a dragndrop need id's to work
		let stage = this.model.file.getContentWindow();
		var newId = stage.CTATGlobalFunctions.gensym.div_id();
		element.setAttribute('id', newId);
	}
  }
  // check if a parent is visible only on some pages,
  // then element should be visible everywhere
  this.checkElementVisibility(element);
};

/**
*	Add element showing prospective position of an added component
*	@param sizeStr a string describing the dimensions of the component
*		(of the form widthxheight)
*/
silex.controller.StageController.prototype.addPhantomNode = function(sizeStr)
{
	let sizeRegex = /([0-9]*)x([0-9]*)/;
	let result = sizeRegex.exec(sizeStr);
	let width = result[1]+'px', height = result[2]+'px';
	
	this.phantomNode = { 
		domNode: document.createElement('div'),
		width: parseInt(result[1], 10),
		height: parseInt(result[2], 10)
	};
	this.phantomNode.domNode.style.width = width;
	this.phantomNode.domNode.style.height = height;
	this.phantomNode.domNode.classList.add('silex-phantom-node');
	this.phantomNode.domNode.addEventListener('click', this.handlePhantomNodeClick.bind(this));
	this.model.file.getContentDocument().body.appendChild(this.phantomNode.domNode);
};

/**
*	Update phantom node position to follow mouse
*	@param x the x coordinate of the mouse
*	@param y the y coordinate of the mouse
*/
silex.controller.StageController.prototype.setPhantomNodePos = function(x, y)
{
	if (this.phantomNode)
	{
		let xOffset = this.phantomNode.width/2;
		let yOffset = this.phantomNode.height/2;
		//top/left of where component should be based on mouse position
		var top = (y-yOffset)+this.view.stage.getScrollY(), 
			left = (x-xOffset)+this.view.stage.getScrollX();
		
		if (this.view.stage.snapToGrid)
		{
			let bgOffsetLeft = this.model.body.getBackground().offsetLeft; 
			let bgOffsetTop = this.model.body.getBackground().offsetTop;
			//dist b/w background element and phantom node
			let leftDist = left - bgOffsetLeft;
			let topDist = top - bgOffsetTop;
			//nearest multiple of 20
			leftDist = Math.round(leftDist/20) * 20;
			topDist = Math.round(topDist/20) * 20;
			//apply offset
			top = bgOffsetTop + topDist;
			left = bgOffsetLeft + leftDist;
		}
		this.phantomNode.top = top;
		this.phantomNode.left = left;
		this.phantomNode.domNode.style.top = top+'px';
		this.phantomNode.domNode.style.left = left+'px';
	}
};

/**
*	Take positioning node off screen
*/
silex.controller.StageController.prototype.removePhantomNode = function()
{
	if (this.phantomNode && this.phantomNode.domNode.parentNode)
	{
		this.phantomNode.domNode.parentNode.removeChild(this.phantomNode.domNode);
		this.phantomNode = null;
	}
};

/**
*	Click on phantom node, trigger element creation
*	@param e the click event
*/
silex.controller.StageController.prototype.handlePhantomNodeClick = function(e)
{
	//Find parent element based on click position
	let mouseX = e.clientX, mouseY = e.clientY;
	let parent = this.view.stage.getDropZone(mouseX, mouseY).element;
	//get position
	var top, left;
	if (this.view.stage.snapToGrid)
	{
		let bgOffsetLeft = this.model.body.getBackground().offsetLeft; 
		let bgOffsetTop = this.model.body.getBackground().offsetTop;
		top = (this.phantomNode.top - bgOffsetTop) + 'px';
		left = this.phantomNode.left - bgOffsetLeft + 'px';
	}
	else
	{
		let pos = this.view.stage.getMousePos();
		let xOffset = this.phantomNode.width/2;
		let yOffset = this.phantomNode.height/2;
		top = (parseInt(pos.y, 10)-yOffset)+'px';
		left = (parseInt(pos.x, 10)-xOffset)+'px';
	}
	let compPosition = {top: top, left:left};
	//alert palette
	this.view.componentPalette.addComponent(compPosition, parent);
};

silex.controller.StageController.prototype.initLassoBox = function(posX, posY, e)
{
	posX += this.view.stage.getScrollX();
	posY += this.view.stage.getScrollY();
	this.lassoBox = {
		domNode: document.createElement('div'),
		top: posY,
		left: posX,
		width: 0,
		height: 0,
		origin: {x: posX, y: posY}
	};
	this.lassoBox.domNode.style.top = posY+'px';
	this.lassoBox.domNode.style.left = posX+'px';
	this.lassoBox.domNode.style.width = '0px';
	this.lassoBox.domNode.style.height = '0px';
	this.lassoBox.domNode.classList.add('silex-lasso-box');
	this.model.file.getContentDocument().body.appendChild(this.lassoBox.domNode)
}

silex.controller.StageController.prototype.updateLassoBox = function(posX, posY)
{
	posX += this.view.stage.getScrollX();
	posY += this.view.stage.getScrollY();	
	let offsetX = this.lassoBox.left - posX;
	let offsetY = this.lassoBox.top - posY;
	let bottom = this.lassoBox.top + this.lassoBox.height;
	let right = this.lassoBox.left + this.lassoBox.width;
	var width, height;
	if (posX < this.lassoBox.origin.x)
	{
		this.lassoBox.left = posX;
		this.lassoBox.domNode.style.left = posX+'px';
		width = this.lassoBox.width + offsetX;
	}
	else
	{
		width = Math.abs(offsetX);
	}
	if (posY < this.lassoBox.origin.y)
	{
		this.lassoBox.top = posY;
		this.lassoBox.domNode.style.top = posY+'px';
		height = this.lassoBox.height + offsetY;
	}
	else
	{
		height = Math.abs(offsetY);
	}
	this.lassoBox.domNode.style.width = width + 'px';
	this.lassoBox.domNode.style.height = height + 'px';
	this.lassoBox.width = width;
	this.lassoBox.height = height;
};

silex.controller.StageController.prototype.handleLassoSelect = function()
{
	//get all elements (completely) inside bounds
	var els = this.model.file.getContentDocument().querySelectorAll('.editable-style');
	var inside = [];
	var boundary = this.lassoBox.domNode.getBoundingClientRect();
	var thisEl;
	for (let i = 0; i < els.length; i++)
	{
		thisEl = els[i].getBoundingClientRect();
		if ((thisEl.top > boundary.top)
		&&  (thisEl.left > boundary.left)
		&&  (thisEl.right < boundary.right)
		&&  (thisEl.bottom < boundary.bottom))
		{
			inside.push(els[i]);
		}
	}
	//select all of them
	this.model.body.setSelection(inside);
	//unselect lasso tool
	silexApp.controller.editMenuController.toggleLasso();
	this.lassoBox.domNode.parentNode.removeChild(this.lassoBox.domNode);
	this.lassoBox = null;
};

silex.controller.StageController.prototype.showMsg = function(msgText, timeout, isGood)
{
	let msgContainer = document.querySelector('.silex-popup-msg');
	if (msgContainer)
	{
		if (!isGood)
			msgContainer.classList.add('bad');
		else 
			msgContainer.classList.remove('bad');
		msgContainer.classList.remove('hidden');
		msgContainer.innerHTML = msgText;
		var top=60, opacity=1;
		msgContainer.style.top = '60px';
		msgContainer.style.opacity = 1;
		var downInt, fadeInt;
		downInt = setInterval(function(){
			top++;
			msgContainer.style.top = top+'px';
			if (top >= 75)
			{
				clearInterval(downInt);
				setTimeout(function()
				{
					fadeInt = setInterval(function()
					{
						opacity -= 0.05;
						msgContainer.style.opacity = opacity;
						if (opacity <= 0)
						{
							clearInterval(fadeInt);
							msgContainer.classList.add('hidden');
						}
					}, 25)
				}, timeout);
			}
		}, 15);
	}
}