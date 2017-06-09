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
  this.view.stage.showFileSourceWindow('image', 
										this.setImgUrl.bind(silexApp.controller.propertyToolController), 
										function(fileData) 
									    {
										  this.setBlobImgUrl(fileData.id, fileData.data, fileData.name);
									    }.bind(silexApp.controller.propertyToolController));
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
		case 'IMG':
			callback = optCbk || function(fileData)
				{
					this.setBlobImgUrl(fileData.id, fileData.data, fileData.name);
				}.bind(this); 
			
			this.fileChooser.show('OPENIMG', null, callback);
		break;
		case 'AUDIO':
			callback = optCbk || function(fileData)
				{
					this.setBlobAudioUrl(fileData.id, fileData.data, fileData.name);
				}.bind(this);
				
			this.fileChooser.show('OPENAUDIO', null, callback);
		break;
	}
};

/**
 * Called by fsystem.js, which supplies a fileId to be used as a URL for an
 * img tag or background.  Only handles absolute URLs (full origin)
 */
silex.controller.PropertyToolController.prototype.setImgUrl = function(url, optElPosition)
{
	this.undoCheckPoint();
	if (this.settingBgImg)
	{
		this.model.element.setBgImage(this.imgElement, url);
	}
	else
	{
		this.imgElement = this.model.element.createElement(silex.model.Element.TYPE_IMAGE, null, optElPosition);
		this.doAddElement(this.imgElement);
		this.model.element.setImageUrl(this.imgElement, url);
	}
	
	this.view.propertyTool.bgPane.applyImageStyles(this.imgElement);
	this.settingBgImg = false;
	
	return this.imgElement;
};

/**
*	Given raw image file data, generates a blob url and sets that URL as image tag or background.
*	@param fileId the cloud storage ID of the file
*	@param blobData the raw file data
*	@param filename the canonical name of the file
*/
silex.controller.PropertyToolController.prototype.setBlobImgUrl = function(fileId, blobData, filename, optElPosition)
{
	if (!blobData)
	{
		console.warn('setBlobImgUrl(), bad blobdata!');
		this.view.stage.setStatus('Error loading '+filename);
		return;
	}
	this.undoCheckPoint();
	let url = silex.utils.Url.genBlobUrl(blobData, 'image/jpeg');
	this.model.file.imgUrlMap[url] = {'name': filename, 'id': fileId};
	this.model.file.updateAssetMap();
	if (this.settingBgImg)
	{
		this.model.element.setBgImage(this.imgElement, url);
		this.view.propertyTool.bgPane.applyImageStyles(this.imgElement);
	}
	else
	{
		this.imgElement = this.model.element.createElement(silex.model.Element.TYPE_IMAGE, null, optElPosition);
		this.doAddElement(this.imgElement);
		this.model.element.setImageUrl(this.imgElement, url);
	}
	
	this.settingBgImg = false;
	this.view.stage.setStatus(filename + ' loaded');
	return this.imgElement;
};

/**
*	Sets a given URL as the source for a CTATAudioButton.  Only handles absolute URLs
*	@param url the absolute url of the audio file
*/
silex.controller.PropertyToolController.prototype.setAudioUrl = function(url)
{
	this.undoCheckPoint();
	this.view.contextMenu.redraw();
	this.audioElement.setAttribute('data-ctat-src', url);
}

/**
*	Given raw audio file data, generates a blob url and sets that URL as source for CTATAudioButton.
*	@param fileId the cloud storage ID of the file
*	@param blobData the raw file data
*	@param filename the canonical name of the file
*/
silex.controller.PropertyToolController.prototype.setBlobAudioUrl = function(fileId, blobData, filename)
{
	if (!blobData)
	{
		console.warn('setBlobImgUrl(), bad blobdata!');
		this.view.stage.setStatus('Error loading '+filename);
		return;
	}

	this.undoCheckPoint();
	let url = silex.utils.Url.genBlobUrl(blobData, null);
	this.model.file.imgUrlMap[url] = {'name': filename, 'id': fileId};
	this.model.file.updateAssetMap();
	this.audioElement.setAttribute('data-ctat-src', url);
	this.view.stage.setStatus(filename + ' loaded');
	this.view.contextMenu.redraw();
};

/**
*	Set a given attribute on a CTATImageButton to a given URL value (Absolute urls only)
*	@param imgButtonElement the image button element
*	@param url the URL to set the attribute to
*	@param property the attribute to set
*/
silex.controller.PropertyToolController.prototype.setImgButtonPropertyUrl = function(imgButtonElement, url, property)
{
	this.undoCheckPoint();
	this.doSetImgButtonProperty(imgButtonElement, property, url);
};

/**
*	Construct a blob URL from raw file data, then set a given attribute on a CTATImageButton to that URL
*	@param imgButtonElement the image button element
*	@param fileData the raw data of the file
*	@param property the attribute to set 	
*/
silex.controller.PropertyToolController.prototype.setImgButtonPropertyBlob = function(imgButtonElement, fileData, property)
{
	this.undoCheckPoint();
	let url = silex.utils.Url.genBlobUrl(fileData.data, 'image/jpeg');
	this.model.file.imgUrlMap[url] = {'name': fileData.name, 'id': fileData.id};
	this.view.stage.setStatus(fileData.name + ' loaded');
	this.model.file.updateAssetMap();
	this.doSetImgButtonProperty(imgButtonElement, property, url);
};

/**
*	Do the actual attribute setting (called by previous two functions) on CTATImageButtons
*	@param imgButtonElement the image button element
*	@param property the attribute to set
*	@param value the value to set the attribute to
*/	
silex.controller.PropertyToolController.prototype.doSetImgButtonProperty = function(imgButtonElement, property, value)
{
	let oldUrl = imgButtonElement.getAttribute(property);
	if (oldUrl && oldUrl.includes('blob:'))
	{
		URL.revokeObjectURL(oldUrl);
		delete this.model.file.imgUrlMap[oldUrl];
		this.model.file.updateAssetMap();
	}
	imgButtonElement.setAttribute(property, value)
	if (property === 'data-ctat-image-default')
	{
		if (value) value = "url('"+value+"')";
		else value = '';
		imgButtonElement.style.backgroundImage = value;
	}
};