goog.provide('silex.view.pane.FontPane');
goog.require('silex.view.pane.PaneBase');

silex.view.pane.FontPane = function(propertyTool, model, element, controller)
{
    goog.base(this, element, model, controller);
	this.propertyTool = propertyTool;
	this.model = model;
	this.fontSizeInput = document.querySelector('#font-size-input');
	this.fontFamilySelect = document.querySelector('#property-tool-font-select');
	this.iAmRedrawing = false;
	this.initEvents();
}

goog.inherits(silex.view.pane.FontPane, silex.view.pane.PaneBase);

silex.view.pane.FontPane.prototype.initEvents = function()
{
	var pointer = this;
	
	//font family
	$(this.fontFamilySelect).fontSelector({
		'hide_fallbacks' : true,
		'initial' : 'Courier New,Courier New,Courier,monospace',
		'selected' : function(style) 
			{ 
				pointer.setFontFamily(style); 
			},
		'opened' : function(style) { console.log('font selector opened'); },
		'closed' : function(style) { console.log('font selector closed'); },
		'fonts' : [
			'Arial,Arial,Helvetica,sans-serif',
			'Arial Black,Arial Black,Gadget,sans-serif',
			'Comic Sans MS,Comic Sans MS,cursive',
			'Courier New,Courier New,Courier,monospace',
			'Georgia,Georgia,serif',
			'Impact,Charcoal,sans-serif',
			'Lucida Console,Monaco,monospace',
			'Lucida Sans Unicode,Lucida Grande,sans-serif',
			'Palatino Linotype,Book Antiqua,Palatino,serif',
			'Tahoma,Geneva,sans-serif',
			'Times New Roman,Times,serif',
			'Trebuchet MS,Helvetica,sans-serif',
			'Verdana,Geneva,sans-serif',
			'Gill Sans,Geneva,sans-serif'
		]
	});
	//font size
	$(this.fontSizeInput).on('change', function()
		{
			var size = this.fontSizeInput.value;
			pointer.setFontSize(size);
	}.bind(pointer));
	
	//font style
	this.italicCheck = goog.ui.decorate(document.querySelector('#font-style-italic-check'));
	this.boldCheck = goog.ui.decorate(document.querySelector('#font-style-bold-check'));
	$(this.boldCheck).on('change', pointer.toggleBold.bind(pointer));
	$(this.italicCheck).on('change', pointer.toggleItalic.bind(pointer));
	this.boldCheck.setChecked(true);
	this.italicCheck.setChecked(true);
	this.boldCheck.setChecked(false);
	this.italicCheck.setChecked(false);
	
	//alignment
	this.alignSelect = document.querySelector('#font-align-select');
	$(this.alignSelect).on('change', pointer.setAlignment.bind(pointer));
};

silex.view.pane.FontPane.prototype.redraw = function(selectedElements, disable)
{
	this.selected = selectedElements;
	this.iAmRedrawing = true;
	if (disable.includes('font'))
	{
		this.setDisabled(true);
		return;
	}	
	else
		this.setDisabled(false);
	
	if (selectedElements.length == 1)
	{
		//get font size
		var size = this.model.element.getStyle(selectedElements[0], 'fontSize') || '16px';
		this.fontSizeInput.value = size;
		//get font weight (bold)
		var weight = this.model.element.getStyle(selectedElements[0], 'fontWeight') || 'normal';
		var weightInt = parseInt(weight, 10);
		if (!isNaN(weightInt))
		{
			if (weightInt > 500 && weightInt < 1000)
				weight = 'bold';
			else if (weightInt > 0)
				weight = 'normal'
		}
		if (weight === 'bold')
			this.boldCheck.setChecked(true);
		else
			this.boldCheck.setChecked(false);
		//get font style (italic)
		var style = this.model.element.getStyle(selectedElements[0], 'fontStyle') || 'normal';
		if (style === 'italic')
			this.italicCheck.setChecked(true);
		else
			this.italicCheck.setChecked(false);
		//get font family
		var fontFamily = this.model.element.getStyle(selectedElements[0], 'fontFamily') ||
			'Arial, Helvetica, sans-serif';
		$(this.fontFamily).fontSelector('select', fontFamily);
		//alignment
		var alignment = this.model.element.getStyle(selectedElements[0], 'textAlign') || 'left';
		this.alignSelect.value = alignment;
	}

	this.iAmRedrawing = false;
};

silex.view.pane.FontPane.prototype.setFontFamily = function(family)
{
	if (this.iAmRedrawing)
		return;
	
	family && this.styleChanged('fontFamily', family);
}

silex.view.pane.FontPane.prototype.setFontSize = function(size)
{
	if (this.iAmRedrawing)
		return;
	
	if (size)
	{
		if (size.indexOf('px') < 0
		&&	size.indexOf('%') < 0
		&& 	size.indexOf('em') < 0
		) 
			size += 'px';
		
		this.styleChanged('fontSize', size);
	}	
}

silex.view.pane.FontPane.prototype.toggleBold = function()
{
	if (this.iAmRedrawing)
		return;
	
	var weight = this.boldCheck.getChecked() ? 'bold' : 'normal';
	this.styleChanged('fontWeight', weight);
}

silex.view.pane.FontPane.prototype.toggleItalic = function()
{
	if (this.iAmRedrawing)
		return;
	
	var style = this.italicCheck.getChecked() ? 'italic' : 'normal';
	this.styleChanged('fontStyle', style);
}

silex.view.pane.FontPane.prototype.setAlignment = function()
{
	if (this.iAmRedrawing)
		return;
	
	var alignment = this.alignSelect.value;
	this.styleChanged('textAlign', alignment);
}

silex.view.pane.FontPane.prototype.setDisabled = function(disabled)
{
	if (disabled)
	{
		this.boldCheck.setEnabled(false);
		this.italicCheck.setEnabled(false);
		this.fontSizeInput.setAttribute('disabled', 'true');
	}
	else
	{
		this.boldCheck.setEnabled(true);
		this.italicCheck.setEnabled(true);
		this.fontSizeInput.removeAttribute('disabled');
	}
}