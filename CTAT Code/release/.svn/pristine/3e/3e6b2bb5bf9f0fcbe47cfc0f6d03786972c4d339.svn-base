goog.provide('silex.view.pane.PositionPane');
goog.require('silex.view.pane.PaneBase');


silex.view.pane.PositionPane = function(propertyTool, model, element, controller)
{
    goog.base(this, element, model, controller);
	this.propertyTool = propertyTool;
	this.model = model;
	this.posContainer = goog.dom.getElement('pos-container');
	this.iAmRedrawing = false;
	this.initEvents();
}

goog.inherits(silex.view.pane.PositionPane, silex.view.pane.PaneBase);

silex.view.pane.PositionPane.prototype.propertyTool = null;
silex.view.pane.PositionPane.prototype.model = null;
silex.view.pane.PositionPane.prototype.posContainer = null;
silex.view.pane.PositionPane.prototype.selected = null;
silex.view.pane.PositionPane.prototype.leftMost = null;
silex.view.pane.PositionPane.prototype.topMost = null;
silex.view.pane.PositionPane.prototype.xInput = null;
silex.view.pane.PositionPane.prototype.yInput = null;

silex.view.pane.PositionPane.prototype.initEvents = function()
{
	this.xInput = goog.dom.getElement('pos-x-input', this.posContainer);
	this.yInput = goog.dom.getElement('pos-y-input', this.posContainer);
	this.widthInput = goog.dom.getElement('width-input', this.posContainer);
	this.heightInput = goog.dom.getElement('height-input', this.posContainer);
	var onchange = function(input)
		{
			if (this.iAmRedrawing)
				return;
			
			var newValStr = input.value;
			var newVal = this.convertToInt(input.value);
			if (isNaN(newVal)) return;
			if (newValStr.indexOf('px') < 0)
			{	
				newValStr = newVal+'px';
			}
			var attr;
			switch(input.getAttribute('id'))
			{
				case 'pos-x-input':
					attr = 'left';
				break;
				case 'pos-y-input':
					attr = 'top';
				break;
				case 'width-input':
					attr = 'width';
				break;
				case 'height-input':
					attr = 'height';
				break;
			}
			if ((attr == 'top' || attr == 'left') && this.selected.length > 1)
			{
				var boundaryVal;
				if (attr === 'top')
					boundaryVal = this.convertToInt(this.model.element.getStyle(this.topMost, attr));
				else
					boundaryVal = this.convertToInt(this.model.element.getStyle(this.leftMost, attr));
				if (isNaN(boundaryVal)) return;
				for (let i = 0; i < this.selected.length; i++)
				{
					if (!(this.selected[i] == this.leftMost && attr == 'left') &&
						!(this.selected[i] == this.topMost && attr == 'top'))
					{
						let elementVal = this.convertToInt(this.model.element.getStyle(this.selected[i], attr));
						let relativeVal = elementVal - boundaryVal;
						let newElementVal = newVal + relativeVal;
						if (isNaN(newElementVal)) return;
						this.styleChanged(attr, newElementVal+'px', [this.selected[i]], true);
					}
				}
			}
			if (attr == 'top')
				this.styleChanged(attr, newValStr, [this.topMost], true);
			else if (attr == 'left')
				this.styleChanged(attr, newValStr, [this.leftMost], true);
			else if (attr == 'width')
				this.styleChanged(attr, newValStr, [this.selected[0]], true);
			else
				this.styleChanged(attr, newValStr, [this.selected[0]], true);
		};
	
	this.xInput.addEventListener('change', onchange.bind(this, this.xInput));
	this.yInput.addEventListener('change', onchange.bind(this, this.yInput));
	this.widthInput.addEventListener('change', onchange.bind(this, this.widthInput));
	this.heightInput.addEventListener('change', onchange.bind(this, this.heightInput));
};

silex.view.pane.PositionPane.prototype.redraw = function(selectedElements, disable)
{
	this.iAmRedrawing = true;
	var width = '-', height = '-';
	selectedElements = this.model.body.getSelection();
	this.selected = selectedElements;
	if (selectedElements.length == 1)
	{
		this.leftMost = selectedElements[0];
		this.topMost = selectedElements[0];
		this.widthInput.removeAttribute('disabled');
		this.heightInput.removeAttribute('disabled');
		width = this.convertToInt(this.model.element.getStyle(selectedElements[0], 'width', true));
		height = this.convertToInt(this.model.element.getStyle(selectedElements[0], 'height', true));
	}
	else
	{
		this.leftMost = this.findLeftMost(selectedElements);
		this.topMost = this.findTopMost(selectedElements);
		this.widthInput.setAttribute('disabled', 'true');
		this.heightInput.setAttribute('disabled', 'true');
	}
	var x = this.convertToInt(this.model.element.getStyle(this.leftMost, 'left', true));
	var y = this.convertToInt(this.model.element.getStyle(this.topMost, 'top', true));
	if (x) this.xInput.value = x;
	else this.xInput.value = '-';
	if (y) this.yInput.value = y;
	else this.yInput.value = '-';
	this.widthInput.value = width;
	this.heightInput.value = height;
	if (disable)
	{
		if (!disable.includes('position'))
		{
			this.xInput.removeAttribute('disabled');
			this.yInput.removeAttribute('disabled');
		}
		else
		{
			this.xInput.setAttribute('disabled', 'indeed');
			this.yInput.setAttribute('disabled', 'totally');
		}
	}
	
	this.iAmRedrawing = false;
}

silex.view.pane.PositionPane.prototype.redrawX = function(newX)
{
	this.iAmRedrawing = true;
	var x = this.convertToInt(newX);
	this.xInput.value = x;
	this.iAmRedrawing = false;
};

silex.view.pane.PositionPane.prototype.redrawY = function(newY)
{
	this.iAmRedrawing = true;
	var y = this.convertToInt(newY);
	this.yInput.value = y;
	this.iAmRedrawing = false;
};

silex.view.pane.PositionPane.prototype.redrawWidth = function(newWidth)
{
	this.iAmRedrawing = true;
	var w = this.convertToInt(newWidth);
	this.widthInput.value = w;
	this.iAmRedrawing = false;
}

silex.view.pane.PositionPane.prototype.redrawHeight = function(newHeight)
{
	this.iAmRedrawing = true;
	var h = this.convertToInt(newHeight);
	this.heightInput.value = h;
	this.iAmRedrawing = false;
}

silex.view.pane.PositionPane.prototype.findLeftMost = function(elements)
{
	var winnerIndex = 0;
	var winnerLeft = this.convertToInt(this.model.element.getStyle(elements[0], 'left'));
	for (var i = 1; i < elements.length; i++)
	{
		var thisLeft = this.convertToInt(this.model.element.getStyle(elements[i], 'left'));
		if (thisLeft < winnerLeft)
		{
			winnerLeft = thisLeft;
			winnerIndex = i;
		}			
	}
	return elements[winnerIndex];
}

silex.view.pane.PositionPane.prototype.findTopMost = function(elements)
{
	var winnerIndex = 0;
	var winnerTop = this.convertToInt(this.model.element.getStyle(elements[0], 'top'));
	for (var i = 1; i < elements.length; i++)
	{
		var thisTop = this.convertToInt(this.model.element.getStyle(elements[i], 'top'));
		if (thisTop < winnerTop)
		{
			winnerTop = thisTop;
			winnerIndex = i;
		}			
	}
	return elements[winnerIndex];
}