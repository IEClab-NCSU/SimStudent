/**
	fileoverview: a class representing the component palette in the CTAT HTML editor. 
*/

goog.provide('silex.view.ComponentPalette');

/**
*	Constructor
*	@param model the main silex model object
*	@param controller the main silex controller object
*/
silex.view.ComponentPalette = function(model, controller)
{
	this.model = model;
	this.controller = controller;
	
	//whether the body of the palette is visible
	this.isExpanded = true;
	
	//the name of the currently selected component in the palette
	this.currentComp = null;
};

/**
*	Build the palette from a list of the components and initialize events
*/
silex.view.ComponentPalette.prototype.buildUi = function()
{
	var pointer = this;
	//container for interactive components
	var intCompList = document.querySelector('#silex-component-palette-list-ctat-std');
	//container for non-interactive components
	var nonIntCompList = document.querySelector('#silex-component-palette-list-html');
	//container for composite components
	var compositeList = document.querySelector('#silex-component-palette-list-ctat-comp');
	//the main body of the palette
	var paletteContainer = document.querySelector('#silex-component-palette-container');
	this.paletteContainer = paletteContainer;
	//the minimize button
	var minBtn = document.querySelector('#palette-minimize');
	this.minBtn = minBtn;
	//array of interactive components
	var intComponents = silex.Config.componentPaletteList.ctatStandard;
	//array of non-interactive components
	var nonIntComponents = silex.Config.componentPaletteList.html;
	//array of composite components
	var compositeComponents = silex.Config.componentPaletteList.ctatComposite;
	//populate component lists
	var popList = function(which, list)
	{
		list.forEach(function(component)
			{
				//create div for display name
				var child = document.createElement('div')
				if (component === null)
				{
					child.classList.add('little-separator');
				}
				else
				{
					child.classList.add('component-container');
					child.innerHTML = component.name;
					child.setAttribute('data-silex-comp-type', component.type);
					child.setAttribute('data-silex-default-size', component.size);
					child.setAttribute('title', component.description);
					child.addEventListener('click', pointer.processComponentClick.bind(pointer, component, child));
				}
				//add to list
				which.appendChild(child);
			});
	};
	popList(nonIntCompList, nonIntComponents);
	popList(intCompList, intComponents);
	popList(compositeList, compositeComponents);
	//position lower lists
	intCompList.style.top = $(nonIntCompList).height() + 'px';
	compositeList.style.top = ($(intCompList).height()+$(nonIntCompList).height())+'px';
	//minimize button
	minBtn.addEventListener('click', this.toggleVisible.bind(this));	
	//mouse over palette
	paletteContainer.addEventListener('mouseover', pointer.handleMouseOver.bind(pointer));
	//mouse out of palette
	paletteContainer.addEventListener('mouseout', pointer.handleMouseOut.bind(pointer));
};

/**
*	Handle click events on component names in the palette
*	@param componentObj the object storing data about the component clicked
*	@param listItem the HTML DOM node clicked
*/
silex.view.ComponentPalette.prototype.processComponentClick = function(componentObj, listItem)
{
	//check if already selected
	if (this.currentComp === componentObj.name)
	{
		//cancel selection
		this.controller.componentPaletteController.unsetSelection();
		return;
	}
	else if (componentObj.name === 'Grouping List')
	{
		this.clearSelected();
		this.controller.insertMenuController.addGroup();
		return;
	}
	else if (!this.currentComp)
	{
		this.model.body.getBodyElement().classList.add(silex.model.Body.DRAGGING_CLASS_NAME);
		this.model.body.setSelection([]);
	}
	//remove selected attr from previous selection
	this.clearSelected();
	//add selected attr to selection
	this.currentComp = componentObj.name;
	listItem.setAttribute('data-silex-palette-selected', 'true');
	
	//alert controller
	this.controller.componentPaletteController.componentClicked(componentObj);
};

/**
*	Remove selected attr from currently selected list item
*/
silex.view.ComponentPalette.prototype.clearSelected = function()
{
	let oldComp = document.querySelector('.component-container[data-silex-palette-selected="true"]');
	oldComp && oldComp.removeAttribute('data-silex-palette-selected');
	this.currentComp = null;
};

/**
*	Handle mouseover events on the palette
*/
silex.view.ComponentPalette.prototype.handleMouseOver = function()
{
	//alert controller
	this.controller.componentPaletteController.mouseOverPalette();
};

/**
*	Handle mouseout events on the palette
*/
silex.view.ComponentPalette.prototype.handleMouseOut = function()
{
	//alert controller
	this.controller.componentPaletteController.mouseOutPalette();
};

/**
*	Add a component to the stage
*	@param position an object containing x and y coordinates for the component
*	@param parentNode the parent of the new component
*/
silex.view.ComponentPalette.prototype.addComponent = function(position, parentNode)
{
	this.controller.componentPaletteController.addComponent(position, parentNode);
};

/**
*	Toggle whether or not the body of the palette is visible
*/
silex.view.ComponentPalette.prototype.toggleVisible = function()
{
	if (this.isExpanded)
	{
		this.paletteContainer.classList.add('hidden');
		this.isExpanded = false;
		this.minBtn.innerHTML = '+';
	}
	else
	{
		this.paletteContainer.classList.remove('hidden');
		this.isExpanded = true;
		this.minBtn.innerHTML = '-';
	}
};