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
goog.provide('silex.controller.PropertyToolController');

goog.require('silex.controller.ControllerBase');



/**
 * @constructor
 * @extends {silex.controller.ControllerBase}
 * listen to the view events and call the main controller's methods}
 * @param {silex.types.Model} model
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.controller.PropertyToolController = function(model, view) {
  // call super
  silex.controller.ControllerBase.call(this, model, view);
};

// inherit from silex.controller.ControllerBase
goog.inherits(silex.controller.PropertyToolController, silex.controller.ControllerBase);


/**
 * add the provided elements to a given page
 * @param {Array.<Element>} elements
 * @param {string} name
 */
silex.controller.PropertyToolController.prototype.addToPage = function(elements, name) {
  // undo checkpoint
  this.undoCheckPoint();
  goog.array.forEach(elements, function(element) {
    this.model.page.addToPage(element, name);
  }, this);
};


/**
 * remove the provided elements from a given page
 * @param {Array.<Element>} elements
 * @param {string} name
 */
silex.controller.PropertyToolController.prototype.removeFromPage = function(elements, name) {
  // undo checkpoint
  this.undoCheckPoint();
  goog.array.forEach(elements, function(element) {
    this.model.page.removeFromPage(element, name);
  }, this);
};


/**
 * add link to the provided elements
 * @param {Array.<Element>} elements
 * @param {string} name
 */
silex.controller.PropertyToolController.prototype.addLink = function(elements, name) {
  // undo checkpoint
  this.undoCheckPoint();
  goog.array.forEach(elements, function(element) {
    this.model.element.setLink(element, name);
  }, this);
};


/**
 * remove link from the provided elements
 * @param {Array.<Element>} elements
 */
silex.controller.PropertyToolController.prototype.removeLink = function(elements) {
  // undo checkpoint
  this.undoCheckPoint();
  goog.array.forEach(elements, function(element) {
    this.model.element.setLink(element);
  }, this);
};

/**
 * open file explorer, choose an image and set it as the background image of the current selection
 */
silex.controller.PropertyToolController.prototype.browseBgImage = function() {
  this.tracker.trackAction('controller-events', 'request', 'selectBgImage', 0);

  this.imgElement = this.model.body.getSelection()[0];
  this.settingBgImg = true;
  let currVal = this.model.element.getBgImage(this.imgElement);
  if (!(currVal.includes('http://') || currVal.includes('https://')))
  {
	  currVal = '';
  }
  this.view.stage.showFileSourceWindow('image', 
										this.setImgUrl.bind(silexApp.controller.propertyToolController), 
										function(fileData) 
									    {
										  this.setBlobImgUrl(fileData);
									    }.bind(silexApp.controller.propertyToolController),
										currVal);
};

/**
*	Open the Drive file explorer
*/
silex.controller.PropertyToolController.prototype.pickFile = function(filetype, optCbk)
{
	var callback;
	if (!this.fileChooser)
	{
		if (!window.ctatFileChooser)
			window.ctatFileChooser = new CTATFileChooser();
		this.fileChooser = window.ctatFileChooser;
	}
	switch(filetype)
	{
		case 'image':
			callback = optCbk || function(fileData)
				{
					this.setBlobImgUrl(fileData);
				}.bind(this); 
			
			this.fileChooser.show('OPENIMG', null, callback);
		break;
		case 'audio':
			callback = optCbk || function(fileData)
				{
					this.setBlobAudioUrl(fileData);
				}.bind(this);
				
			this.fileChooser.show('OPENAUDIO', null, callback);
		break;
		case 'video':
			callback = optCbk || function(fileData)
				{
					this.setBlobVideoUrl(fileData);
				}.bind(this);
			
			this.fileChooser.show('OPENVIDEO', null, callback);
		break;
	}
};

/**
 *
 */
silex.controller.PropertyToolController.prototype.setImgUrl = function(url, optElPosition, optEl)
{
	this.undoCheckPoint();
	let data = {
		url: url,
		name: url ? url.split('/').pop().split('?')[0] : 'N/A',
		path: url || ''
	};
	return this.doSetImgUrl(data, optElPosition, optEl);
};

/**
*	Given raw image file data, generates a blob url and sets that URL as image tag or background.
*	@param fileId the cloud storage ID of the file
*	@param blobData the raw file data
*	@param filename the canonical name of the file
*/
silex.controller.PropertyToolController.prototype.setBlobImgUrl = function(fileData, optElPosition, optEl)
{
	if (!fileData.data)
	{
		console.warn('setBlobImgUrl(), bad blobdata!');
		this.view.stage.setStatus('Error loading image file');
		return;
	}
	this.view.stage.setStatus(fileData.name + ' loaded');
	this.undoCheckPoint();
	let url = silex.utils.Url.genBlobUrl(fileData.data, 'image/jpeg');
	this.model.file.imgUrlMap[url] = {'name': fileData.name, 'id': fileData.id, 'path': fileData.path};
	this.model.file.updateAssetMap();
	let data = {
		url: url,
		name: fileData.name,
		path: fileData.path
	};
	return this.doSetImgUrl(data, optElPosition, optEl);
};

silex.controller.PropertyToolController.prototype.doSetImgUrl = function(data, optPos, optEl)
{
	if (this.settingBgImg)
	{
		this.model.element.setBgImage(this.imgElement, data.url);
		this.view.propertyTool.bgPane.applyImageStyles(this.imgElement);
	}
	else
	{
		this.imgElement = optEl || this.model.element.createElement(silex.model.Element.TYPE_IMAGE, null, optPos);
		this.model.element.setImageUrl(this.imgElement, data.url);
		if (!optEl)
			this.doAddElement(this.imgElement);
		else
			this.view.propertyTool.attributePane.setAttrValue('data-silex-img-src', data);
	}
	
	this.settingBgImg = false;
	return this.imgElement;
};

/**
*	Sets a given URL as the source for a CTATAudioButton.  Only handles absolute URLs
*	@param url the absolute url of the audio file
*/
silex.controller.PropertyToolController.prototype.setUrl = function(element, url, property)
{
	this.undoCheckPoint();
	var data = {
		url: url,
		name: url ? url.split('/').pop().split('?')[0] : 'N/A',
		path: url || ''
	};
	this.doSetUrlProperty(element, property, data);
};

/**
*	Given raw audio file data, generates a blob url and sets that URL as source for CTATAudioButton.
*	@param fileId the cloud storage ID of the file
*	@param blobData the raw file data
*	@param filename the canonical name of the file
*/
silex.controller.PropertyToolController.prototype.setBlobUrl = function(element, fileData, property) 
{
	if (!fileData.data)
	{
		console.warn('setBlobImgUrl(), bad blobdata!');
		this.view.stage.setStatus('Error loading file');
		return;
	}

	this.undoCheckPoint();
	let url = silex.utils.Url.genBlobUrl(fileData.data, null);
	this.model.file.imgUrlMap[url] = {'name': fileData.name, 'id': fileData.id, 'path': fileData.path};
	this.model.file.updateAssetMap();
	this.view.stage.setStatus(fileData.name + ' loaded');
	var data = {
		url: url,
		name: fileData.name,
		path: fileData.path
	}
	this.doSetUrlProperty(element, property, data);
};

/**
*	Do the actual attribute setting (called by previous two functions) on CTATImageButtons
*	@param imgButtonElement the image button element
*	@param property the attribute to set
*	@param value the value to set the attribute to
*	@param optPath optional file path 
*/	
silex.controller.PropertyToolController.prototype.doSetUrlProperty = function(element, property, data)
{
	let oldUrl = element.getAttribute(property);
	if (oldUrl && oldUrl.includes('blob:'))
	{
		URL.revokeObjectURL(oldUrl);
		delete this.model.file.imgUrlMap[oldUrl];
		this.model.file.updateAssetMap();
	}
	let value = data.url;
	element.setAttribute(property, value)
	if (property === 'data-ctat-image-default')
	{
		if (value) value = "url('"+value+"')";
		else value = '';
		element.style.backgroundImage = value;
	}
	//update value in attribute pane
	this.view.propertyTool.attributePane.setAttrValue(property, data);
};
