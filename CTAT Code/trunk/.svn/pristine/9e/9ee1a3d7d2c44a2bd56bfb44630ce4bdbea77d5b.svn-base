/**
*	fileoverview a controller class for the component palette in the CTAT HTML editor
*/

goog.provide('silex.controller.ComponentPaletteController');
goog.require('silex.controller.ControllerBase');

/**
*	Constructor
*	@param model the main silex model object
*	@param view the main silex view object
*/
silex.controller.ComponentPaletteController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
  this.currentComponent = null;
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.ComponentPaletteController, silex.controller.ControllerBase);

/**
*	A component in the palette was selected
*	@param componentObj an object storing data about the component clicked
*/
silex.controller.ComponentPaletteController.prototype.componentClicked = function(componentObj)
{
	this.currentComponent = componentObj;
	//let stage know
	this.view.stage.paletteItemSelected = true;
};

/**
*	Palette has fired a mouseover event
*/
silex.controller.ComponentPaletteController.prototype.mouseOverPalette = function()
{
	//remove 'phantom' element
	this.view.stage.removePhantomNode();
};

/**
*	Palette has fired a mouseout event
*/
silex.controller.ComponentPaletteController.prototype.mouseOutPalette = function()
{
	//add 'phantom' element
	if (this.currentComponent)
	{
		this.view.stage.addPhantomNode(this.currentComponent.size);
	}
};

/**
*	Add a component to the stage
*	@param position the position of the component
*	@param parentNode the parent of the new component
*/
silex.controller.ComponentPaletteController.prototype.addComponent = function(position, parentNode)
{
	var element;
	var component = this.currentComponent;
	this.unsetSelection();
	if (component.type === 'image')
	{
		this.view.stage.showFileSourceWindow(
			'image',
			function(url)
			{
				element = silexApp.controller.propertyToolController.setImgUrl(url, position);
				silexApp.controller.stageController.newContainer(parentNode, element);
			},
			function(fileData)
			{
				element = this.setBlobImgUrl(fileData, position);
				silexApp.controller.stageController.newContainer(parentNode, element);
			}.bind(silexApp.controller.propertyToolController)
		);
	}
	else if (component.type === 'question.multchoice')
	{
		this.view.stage.showMultChoiceWindow(null, function(questionInfo)
			{
				element = silexApp.model.element.createElement('question.multchoice', questionInfo, position);
				silexApp.controller.stageController.newContainer(parentNode, element);
			}
		);
	}
	else
	{
		//call insertMenuController
		element = silexApp.controller.insertMenuController.addElement(component.type, position);
		silexApp.controller.stageController.newContainer(parentNode, element);
	}
};

/**
*	Clear palette selection, resets all metadata associated with palette state
*/
silex.controller.ComponentPaletteController.prototype.unsetSelection = function()
{
	this.view.stage.paletteItemSelected = false;
	this.view.stage.removePhantomNode();
	this.currentComponent = null;
	this.view.componentPalette.clearSelected();
	this.model.body.getBodyElement().classList.remove(silex.model.Body.DRAGGING_CLASS_NAME);
	this.view.stage.markAsDropZone(null);
};
