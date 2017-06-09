goog.provide('silex.view.pane.MarginPane');
goog.require('silex.view.pane.PaneBase');

silex.view.pane.MarginPane = function(propertyTool, model, element, controller)
{
    goog.base(this, element, model, controller);
	this.propertyTool = propertyTool;
	this.model = model;
	this.marginRadio = document.querySelector('#margin-radio-btn');
	this.paddingRadio = document.querySelector('#padding-radio-btn');
	this.valueInput = document.querySelector('#margin-padding-value-input');
	this.initInputs();
	this.iAmRedrawing = false;
	this.initEvents();
}

goog.inherits(silex.view.pane.MarginPane, silex.view.pane.PaneBase);

silex.view.pane.MarginPane.prototype.initInputs = function()
{
	this.inputs = {};
	this.inputs.left = document.querySelector('#margin-left-val');
	this.inputs.top = document.querySelector('#margin-top-val');
	this.inputs.right = document.querySelector('#margin-right-val');
	this.inputs.bottom = document.querySelector('#margin-bottom-val');
}

silex.view.pane.MarginPane.prototype.initEvents = function()
{
	var pointer = this;
	this.marginRadio.checked = true;
	this.marginRadio.addEventListener("change", function(){pointer.redraw()});
	this.paddingRadio.addEventListener("change", function(){pointer.redraw()});
	['left','top','bottom','right'].forEach(function(side)
	{
		pointer.inputs[side].addEventListener('change', pointer.valueChanged.bind(pointer, side));
	});
}

silex.view.pane.MarginPane.prototype.redraw = function(selectedElements)
{
	this.iAmRedrawing = true;
	if (!selectedElements) 
		selectedElements = this.model.body.getSelection();
	var property = this.paddingRadio.checked ? 'padding' : 'margin';
	if (selectedElements.length === 1)
	{
		['top','right','bottom','left'].forEach(function(side)
		{
			var val = this.model.element.getStyle(selectedElements[0], property+'-'+side);
			this.inputs[side].value = val || '';
		}, this);
	}
	this.iAmRedrawing = false;
};

silex.view.pane.MarginPane.prototype.valueChanged = function(which)
{
	if (this.iAmRedrawing)
		return;
	var property = this.paddingRadio.checked ? 'padding' : 'margin';
	var val = this.inputs[which].value;
	val = val.trim();
	if ((val.indexOf('px')<0)
	&&	(val.indexOf('%')<0))
	{
		val += 'px';
	}
	this.styleChanged(property+'-'+which, val);
};