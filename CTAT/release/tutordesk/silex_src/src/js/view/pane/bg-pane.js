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
 * @fileoverview Property pane, displayed in the property tool box.
 * Controls the background params
 *
 */


goog.provide('silex.view.pane.BgPane');
goog.require('goog.array');
goog.require('goog.object');
goog.require('goog.ui.Checkbox');
goog.require('goog.ui.ColorButton');
goog.require('goog.ui.CustomButton');
goog.require('goog.ui.HsvaPalette');
goog.require('goog.ui.TabBar');
goog.require('silex.utils.Style');
goog.require('silex.view.pane.PaneBase');



/**
 * on of Silex Editors class
 * let user edit style of components
 * @constructor
 * @extends {silex.view.pane.PaneBase}
 * @param {Element} element   container to render the UI
 * @param  {!silex.types.Model} model  model class which holds
 *                                  the model instances - views use it for read operation only
 * @param  {!silex.types.Controller} controller  structure which holds
 *                                  the controller instances
 */
silex.view.pane.BgPane = function(element, model, controller) {
  // call super
  goog.base(this, element, model, controller);
  // init the component
  this.buildUi();
};

// inherit from silex.view.PaneBase
goog.inherits(silex.view.pane.BgPane, silex.view.pane.PaneBase);


/**
 * build the UI
 */
silex.view.pane.BgPane.prototype.buildUi = function() {
  // BG color
  this.buildColors();

  // init palette
  this.buildPalette();

  // init bg image
  this.buildBgImage();

  // bg image properties
  this.buildBgImageProperties();

};


/**
 * build the UI
 */
silex.view.pane.BgPane.prototype.buildPalette = function() {
  var bgHsvPaletteElement = goog.dom.getElementByClass(
      'color-bg-palette',
      this.element);
  var fgHsvPaletteElement = goog.dom.getElementByClass(
	  'color-fg-palette',
	  this.element);

  //BG palette
  this.bgHsvPalette = new goog.ui.HsvaPalette(undefined,
                                            undefined,
                                            undefined,
                                            'goog-hsva-palette-sm');

  // render the element
  this.bgHsvPalette.render(bgHsvPaletteElement);

  // init palette
  this.bgHsvPalette.setColorRgbaHex('#FFFFFFFF');
  this.setColorPaletteVisibility(this.bgHsvPalette, false);

  // User has selected a color
  var bgColorChangeCbk = this.onColorChanged.bind(this, 'bg');
  goog.events.listen(this.bgHsvPalette,
                     goog.ui.Component.EventType.ACTION,
                     bgColorChangeCbk,
                     false,
                     this);
	
  //FG palette
  this.fgHsvPalette = new goog.ui.HsvaPalette(undefined,
                                            undefined,
                                            undefined,
                                            'goog-hsva-palette-sm');

  // render the element
  this.fgHsvPalette.render(fgHsvPaletteElement);

  // init palette
  this.fgHsvPalette.setColorRgbaHex('#FFFFFFFF');
  this.setColorPaletteVisibility(this.fgHsvPalette, false);

  // User has selected a color
  var fgColorChangeCbk = this.onColorChanged.bind(this, 'fg');
  goog.events.listen(this.fgHsvPalette,
                     goog.ui.Component.EventType.ACTION,
                     fgColorChangeCbk,
                     false,
                     this);  
};


/**
 * build the UI
 */
silex.view.pane.BgPane.prototype.buildColors = function() {
  // BG color
  // init button which shows/hides the palete
  this.bgColorPicker = new goog.ui.ColorButton('');
  this.bgColorPicker.setTooltip('Click to select color');
  this.bgColorPicker.render(goog.dom.getElementByClass(
      'color-bg-button',
      this.element));

  // init the button to choose if there is a color or not
  this.transparentBgCheckbox = new goog.ui.Checkbox();
  this.transparentBgCheckbox.decorate(
      goog.dom.getElementByClass(
          'enable-color-bg-button',
          this.element)
  );

  // the user opens/closes the palete
  var bgCbk = this.onColorButton.bind(this, 'bg');
  goog.events.listen(this.bgColorPicker,
                     goog.ui.Component.EventType.ACTION,
                     bgCbk,
                     false,
                     this);

  // user set transparent bg
  goog.events.listen(this.transparentBgCheckbox,
                     goog.ui.Component.EventType.CHANGE,
                     this.onTransparentChanged,
                     false,
                     this);
					 
  //FG color
  this.fgColorPicker = new goog.ui.ColorButton('');
  this.fgColorPicker.setTooltip('Click to select color');
  this.fgColorPicker.render(goog.dom.getElementByClass(
	'color-fg-button', this.element));
	
  //init button event
  var fgCbk = this.onColorButton.bind(this, 'fg');
  goog.events.listen(this.fgColorPicker,
	   			     goog.ui.Component.EventType.ACTION,
					 fgCbk,
					 false,
					 this);
};


/**
 * build the UI
 */
silex.view.pane.BgPane.prototype.buildBgImage = function() {
  // add bg image button
  this.bgSelectBgImage = goog.dom.getElement('bg-image-btn',this.element);

  // event user wants to update the bg image
  goog.events.listen(this.bgSelectBgImage,
      goog.events.EventType.CLICK,
      this.onSelectImageButton,
      false,
      this);
};


/**
 * build the UI
 */
silex.view.pane.BgPane.prototype.buildBgImageProperties = function() {
  // bg image properties
  this.positionComboBox = this.createComboBox('bg-image-position',
      goog.bind(function(event) {
        var position = this.positionComboBox.getSelectedItem().getId();
        this.styleChanged('backgroundPosition', position.replace('-', ' '));
      }, this));
  
  this.repeatComboBox = this.createComboBox('bg-image-repeat',
      goog.bind(function(event) {
        this.styleChanged(
            'backgroundRepeat',
            event.target.getSelectedItem().getId());
      }, this));
  
  this.sizeComboBox = this.createComboBox('bg-image-size',
      goog.bind(function(event) {
        this.styleChanged(
            'backgroundSize',
            event.target.getSelectedItem().getId());
      }, this));
};


/**
 * redraw the properties
 * @param   {Array.<Element>} selectedElements the elements currently selected
 * @param   {Array.<string>} pageNames   the names of the pages which appear in the current HTML file
 * @param   {string}  currentPageName   the name of the current page
 * @param	{Array.<string>} disable	the names of tools to disable for this group of elements
 */
silex.view.pane.BgPane.prototype.redraw = function(selectedElements, pageNames, currentPageName, disable) {
  
  if (this.iAmSettingValue) {
    return;
  }
  this.iAmRedrawing = true;
  // call super
  goog.base(this, 'redraw', selectedElements, pageNames, currentPageName);

  // remember selection
  this.selectedElements = selectedElements;
  this.pageNames = pageNames;
  this.currentPageName = currentPageName;
  
  //BG color
  if (!disable.includes('bgColor'))
  {
	  this.transparentBgCheckbox.setEnabled(true);
	  var color = this.getCommonProperty(selectedElements, goog.bind(function(element) {
			return this.model.element.getStyle(element, 'backgroundColor');
		}, this));
	  if (!color || color === 'transparent' || color === 'rgba(0, 0, 0, 0)') 
	  {
		this.transparentBgCheckbox.setChecked(true);
		this.bgColorPicker.setEnabled(false);
		this.setColorPaletteVisibility(this.bgHsvPalette, false);
	  }
	  else {
		// handle all colors, including the named colors
		color = silex.utils.Style.rgbaToHex(color);
		this.transparentBgCheckbox.setChecked(false);
		this.bgColorPicker.setEnabled(true);
		this.bgHsvPalette.setColorRgbaHex(color);
		this.bgColorPicker.setValue(this.bgHsvPalette.getColor());
	  }
  }
  else
  {
	  this.bgColorPicker.setEnabled(false);
	  this.setColorPaletteVisibility(this.bgHsvPalette, false);
	  this.transparentBgCheckbox.setEnabled(false);
  }
  //FG color
  if (!disable.includes('fgColor'))
  {
	  color = this.getCommonProperty(selectedElements, goog.bind(function(element) 
		{ return this.model.element.getStyle(element, 'color'); },
		this));
		
	  color = color || 'rgba(0, 0, 0, 1)'; //default to black
	  color = silex.utils.Style.rgbaToHex(color);
	  this.fgColorPicker.setEnabled(true);
	  if (color.length == 7)
		  color = color + "FF"; //fill in alpha val if not given
	  this.fgHsvPalette.setColorRgbaHex(color);
	  this.fgColorPicker.setValue(this.fgHsvPalette.getColor());
  }
  else
  {
	  this.fgColorPicker.setEnabled(false);
	  this.setColorPaletteVisibility(this.fgHsvPalette, false);
  } 
  // BG image
  var enableBgImg = false;
  if (!disable.includes('bgImage'))
  {
	  if (selectedElements.length === 1)
	  {
		  var selectedElement = selectedElements[0];
		  if (!selectedElement.className.includes('CTAT'))
		  {
			enableBgImg = true;
		  }
		  else if (selectedElement.className.includes('Button')
			   && !selectedElement.className.includes('RadioButton'))
		  {
			enableBgImg = true;
		  }
	  }
  }
  if (enableBgImg)
  {  
	document.getElementById('bg-image-container').removeAttribute('disabled');
	let imgStylesEnabled = !!this.model.element.getStyle(selectedElements[0], 'backgroundImage');
	
	this.repeatComboBox.setEnabled(imgStylesEnabled);
	this.sizeComboBox.setEnabled(imgStylesEnabled);
	this.positionComboBox.setEnabled(imgStylesEnabled);
  }
  else
  {
	document.getElementById('bg-image-container').setAttribute('disabled', 'yep');
  }
  
  this.iAmRedrawing = false;
};


/**
 * User has selected a color
 */
silex.view.pane.BgPane.prototype.onColorChanged = function(whichClr) {
  if (this.iAmRedrawing) {
    return;
  }
  var palette = (whichClr === 'fg') ? this.fgHsvPalette : this.bgHsvPalette;
  var colorPicker = (whichClr === 'fg') ? this.fgColorPicker : this.bgColorPicker;
  var color = silex.utils.Style.hexToRgba(palette.getColorRgbaHex());
  // update the button
  colorPicker.setValue(palette.getColor());

  // notify the toolbox
  if (whichClr === 'fg')
  {
	this.styleChanged('color', color, this.selectedElements);
  }
  else
  {
	this.styleChanged('backgroundColor', color, this.selectedElements);
	this.styleChanged('backgroundImage', '', this.selectedElements, false);
  }
};


/**
 * User has clicked on the color button
 * open or close the palete
 */
silex.view.pane.BgPane.prototype.onColorButton = function(whichBtn) {
  var palette = (whichBtn === 'fg') ? this.fgHsvPalette : this.bgHsvPalette; 
  var otherPalette = (whichBtn === 'fg') ? this.bgHsvPalette : this.fgHsvPalette;
  var defaultColor = (whichBtn === 'fg') ? 'rgba(0, 0, 0, 1)' : 'rgba(255, 255, 255, 1)';
  var element = this.selectedElements[0];
  if (silex.utils.CTAT.getCTATClassName(element) === 'CTATFractionBar'
  ||  silex.utils.CTAT.getCTATClassName(element) === 'CTATPieChart')
  {
	  defaultColor = 'rgba(128, 0, 128, 1)';
  }
  // show the palette
  if (this.getColorPaletteVisibility(palette) === false) {
    var color = (whichBtn === 'fg') ? this.model.element.getStyle(element, 'color') : this.model.element.getStyle(element, 'backgroundColor');
	color = color || defaultColor;
	if (color.length == 7)
		color = color + "FF";
	palette.setColorRgbaHex(silex.utils.Style.rgbaToHex(color));
    this.setColorPaletteVisibility(palette, true);
	this.setColorPaletteVisibility(otherPalette, false); //only allow one expanded at a time
	this.setColorPaletteVisibility(this.controller.propertyToolController.view.propertyTool.borderPane.hsvPalette, false);
  }
  else {
    this.setColorPaletteVisibility(palette, false);
  }
};


/**
 * Create a combo box
 */
silex.view.pane.BgPane.prototype.createComboBox = function(className, onChange) {
  // create the combo box
  var comboBox = goog.ui.decorate(
      goog.dom.getElementByClass(className, this.element)
      );
  // attach event
  goog.events.listen(comboBox, goog.ui.Component.EventType.CHANGE,
      goog.bind(function(event) {
        if (onChange && !this.iAmRedrawing) {
          onChange(event);
        }
      }, this));
  // return the google closure object
  return comboBox;
};

silex.view.pane.BgPane.prototype.applyImageStyles = function (element)
{
	let pos = this.positionComboBox.getSelectedItem().getId();
	let size = this.sizeComboBox.getSelectedItem().getId();
	let repeat = this.repeatComboBox.getSelectedItem().getId();
	
	this.styleChanged('backgroundPosition', pos.replace('-', ' '), [element], false);
	this.styleChanged('backgroundSize', size, [element], false);
	this.styleChanged('backgroundRepeat', repeat, [element], false);
}

/**
 * User has clicked the transparent checkbox
 */
silex.view.pane.BgPane.prototype.onTransparentChanged = function() {
  if (this.iAmRedrawing) {
    return;
  }
  var color = 'transparent';
  if (this.transparentBgCheckbox.getChecked() === false) {
    color = silex.utils.Style.hexToRgba(this.bgHsvPalette.getColorRgbaHex());
    if (!color) {
      color = 'rgba(255, 255, 255, 1)';
    }
  }
  // notify the toolbox
  this.styleChanged('backgroundColor', color);
  this.styleChanged('backgroundImage', '');
  var disabled = this.getDisabled(this.selectedElements);
  // redraw myself (styleChange prevent myself to redraw)
  this.redraw(this.selectedElements, this.pageNames, this.currentPageName, disabled);
};


/**
 * User has clicked the select image button
 */
silex.view.pane.BgPane.prototype.onSelectImageButton = function() {
  this.controller.propertyToolController.browseBgImage();
};


/**
 * User has clicked the clear image button
 */
silex.view.pane.BgPane.prototype.onClearImageButton = function() {
  this.styleChanged('backgroundImage', '');
};
