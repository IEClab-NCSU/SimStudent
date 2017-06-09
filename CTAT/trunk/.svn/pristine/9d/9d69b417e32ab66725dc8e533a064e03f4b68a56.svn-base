
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
 * @fileoverview
 *   This class represents a File opened by Silex,
 *   which is rendered by the Stage class
 *   It has methods to manipulate the File
 */

goog.provide('silex.model.File');
goog.require('silex.Config');
goog.require('silex.service.SilexTasks');
//goog.require("SilexStyleSheet");
goog.require('silex.utils.CTAT');



/**
 * @constructor
 * @param  {silex.types.Model} model  model class which holds the other models
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.model.File = function(model, view) {
  // store the model and the view
  /**
   * @type {silex.types.Model}
   */
  this.model = model;
  /**
   * @type {silex.types.View}
   */
  this.view = view;
  // store the iframe window and document
  /**
   * the iframe element
   * @type {!HTMLIFrameElement}
   * @private
   */
  this.iFrameElement_ = /** @type {!HTMLIFrameElement} */ (goog.dom.getElementByClass(silex.view.Stage.STAGE_CLASS_NAME));

  /**
   * iframe document
   * @type {Document}
   * @private
   */
  this.contentDocument_ = goog.dom.getFrameContentDocument(this.iFrameElement_);

  /**
   * iframe window
   * @type {Window}
   * @private
   */
  this.contentWindow_ = goog.dom.getFrameContentWindow(this.iFrameElement_);  
};


/**
 * name of the new file template
 * @const
 */
silex.model.File.CREATION_TEMPLATE = 'creation-template.html';

silex.model.File.WINDOW_TEMPLATE_DIR = 'template';


/**
 * loading css class
 * @const
 */
silex.model.File.LOADING_CSS_CLASS = 'loading-website';


/**
 * loading css class
 * @const
 */
silex.model.File.LOADING_LIGHT_CSS_CLASS = 'loading-website-light';


/**
 * current file url
 * if the current file is a new file, it has no url
 * if set, this is an absolute URL, use silex.model.File::getUrl to get the relatvie URL
 * @type {?string}
 */
silex.model.File.prototype.url = null;

/**
*	maps urls used to link images in the editor to the actual
*	filenames that will be linked in the published version
*/
silex.model.File.prototype.imgUrlMap = {};

/**
 * the get the iframe element
 * @return {HTMLIFrameElement}
 */
silex.model.File.prototype.getIFrameElement = function() {
  return this.iFrameElement_;
};


/**
 * get the iframe document
 * @return {Document}
 */
silex.model.File.prototype.getContentDocument = function() {
  return this.contentDocument_;
};


/**
 * get the iframe window
 * @return {Window}
 */
silex.model.File.prototype.getContentWindow = function() {
  return this.contentWindow_;
};

/**
 * build the html content
 * Parse the raw html and fill the bodyElement and headElement
 * @param {string} rawHtml
 * @param {?function()=} opt_cbk
 * @param {?boolean=} opt_showLoader
 * @param {?boolean} readStyles true if CTATStyleSheet should
		be populated w/ styles from html string
 */
silex.model.File.prototype.setHtml = function(rawHtml, opt_cbk, opt_showLoader, isStateRestore) 
{
  // cleanup
  this.model.body.setEditable(this.contentDocument_.body, false);
  this.view.stage.removeEvents(this.contentDocument_.body);
  // add base tag from the beginning
  // should not be needed since we change all  the URLs to absolute
  // but just in case abs/rel conversion bugs
  if (this.url) {
    rawHtml = rawHtml.replace('<head>', '<head><base class="' + silex.model.Head.SILEX_TEMP_TAGS_CSS_CLASS + '" href="' + this.url + '" target="_blank">');
  }
  // remove user's head tag before it is interprated by the browser
  // - in case it has bad HTML tags, it could break the whole site, insert tags into the body instead of the head...
  rawHtml = this.model.head.extractUserHeadTag(rawHtml);
  // prepare HTML
  rawHtml = this.model.element.prepareHtmlForEdit(rawHtml);
  
  // make everything protocol agnostic to avoid problems with silex being https
  // rawHtml = rawHtml.replace('http://', '//', 'g');
  
  // detect non-silex websites
  if(rawHtml.indexOf('silex-runtime') < 0) 
  {
	alert('This is not a website editable in Silex.');
    return;
  }
  else if(rawHtml.indexOf('silex-published') >= 0) {
    console.error('This is a published website.');
    silex.utils.Notification.alert('I can not be open this website. It is a published version of a Silex website. <a target="_blank" href="https://github.com/silexlabs/Silex/issues/282">More info here</a>.', function() {});
    return;
  }
  // remove the "silex-runtime" css class from the body while editing
  // this must be done before rendering the dom, because it is used in front-end.js
  rawHtml = rawHtml.replace(/<body(.*)(silex-runtime).*>/, function(match, p1, p2) {
    if (p1.indexOf('>') >= 0) {
      return match;
    }
    return match.replace('silex-runtime', '');
  }, 'g');
  
  //load assets
  let assetTagRegex = /<meta name ?= ?"asset-map" ?content ?= ?"(.*)"/;
  let result = rawHtml.match(assetTagRegex);
  let oldImgUrlMap = {};
  if (result && result[1])
  {
	console.log('found asset map = '+result[1]);
	oldImgUrlMap = JSON.parse(result[1].replace(/&quot;/g, '"'));
  }
  let counter = Object.keys(oldImgUrlMap).length;
  let pointer = this;
  let doFetch = true;
  
  //callback called when all assets have been downloaded
  var assetsLoaded = function()
  {
	  // write the content
	  goog.dom.iframe.writeContent(pointer.iFrameElement_, rawHtml);
	  
	  pointer.view.stage.setStatus('');
	  pointer.contentChanged(opt_cbk);
  };
  
  if (isStateRestore)
  {
	//if we're undoing or redoing...
	//clear unnecessary url map entries
	for (let url in pointer.imgUrlMap)
	{
		if (pointer.imgUrlMap.hasOwnProperty(url)
		&&  !oldImgUrlMap.hasOwnProperty(url))
		{
			delete pointer.imgUrlMap[url];
		}
	}
  }
  
  if (counter > 0)
  {
	  //load all external assets (audio, images, scripts, stylesheets)
	  pointer.view.stage.setStatus('Loading Assets...');
	  for (let oldUrl in oldImgUrlMap)
	  {
		  doFetch = true;
		  if (oldImgUrlMap.hasOwnProperty(oldUrl))
		  {
			  if (isStateRestore && this.imgUrlMap[oldUrl])
			  {
				  //Already loaded, skip it
				  doFetch = false;
				  counter--;
				  if (counter === 0)
				  {
					  assetsLoaded();
				  }
			  }
			  if (doFetch)
			  {
				  let entry = oldImgUrlMap[oldUrl];
				  console.log('fetching asset '+entry.name+', id: '+entry.id);
				  //download asset
				  cloudUtils.openBlobById(entry.id, function(blobData)
					{
						if (!blobData)
						{
							pointer.view.stage.setStatus('Error retrieving asset '+entry.name);
						}
						let url = silex.utils.Url.genBlobUrl(blobData);
						
						let toFind = oldUrl.replace('&', '((&amp;)|&)');
						toFind = toFind.replace('?', '\\?');
						toFind = new RegExp(toFind, 'g');
						rawHtml = rawHtml.replace(toFind, url);
						pointer.imgUrlMap[url] = entry;
						
						counter--;
						if (counter == 0) //all assets downloaded
						{
							assetsLoaded();
						}
					});
			  }
		  }
	  }
  }
  else
  {
	 assetsLoaded();
  }
};



/**
 * the content of the iframe changed
 * @param {?function()=} opt_cbk
 */
silex.model.File.prototype.contentChanged = function(opt_cbk) {
  // wait for the webste to be loaded
  // can not rely on the load event of the iframe because there may be missing assets
  this.contentDocument_ = goog.dom.getFrameContentDocument(this.iFrameElement_);
  this.contentWindow_ = goog.dom.getFrameContentWindow(this.iFrameElement_);
  if (this.contentDocument_.body === null ||
    this.contentWindow_ === null ||
    this.contentWindow_['$'] === null ) {
    setTimeout(goog.bind(function() {
      this.contentChanged(opt_cbk);
    }, this), 0);
    return;
  }

  //reset right click listener
  silexApp.setRightClickListener();
  
  // include edition tags and call onContentLoaded
  // the first time, it takes time to load the scripts
  // the second time, no load event, and jquery is already loaded

  // first time in chrome, and always in firefox
  // load scripts for edition in the iframe
  this.includeEditionTags(goog.bind(function() 
  {
    this.onContentLoaded(opt_cbk);
  }, this), goog.bind(function()
  {
    // error loading editable script
    console.error('error loading editable script');
    throw new Error('error loading editable script');
  }, this));
}


/**
 * copntent successfully changed in the iframe
 * @param {?function()=} opt_cbk
 */
silex.model.File.prototype.onContentLoaded = function(opt_cbk) {
  // handle retrocompatibility issues
  silex.utils.BackwardCompat.process(this.contentDocument_, this.model, (hasUpgraded) => {
   // check the integrity and store silex style sheet which holds silex elements styles
    this.model.property.initSilexStyleTag(this.contentDocument_);
	//set lasso cursor style if necessary
	if (this.view.stage.lassoSelected)
	  this.model.head.setTempStyle('*{cursor: crosshair !important;}');
	else
	  this.model.head.removeTempStyle('*{cursor: crosshair !important;}')
	this.model.property.setCurrentSilexStyleSheet(this.model.property.getSilexStyleSheet(this.contentDocument_));
    // select the body
    this.model.body.setSelection([this.contentDocument_.body]);
    // make editable again
    this.model.body.setEditable(this.contentDocument_.body, true);
    // reset body ref in model.body
	this.model.body.setBackground(null);
	// update text editor with the website custom styles and script
    this.model.head.setHeadStyle(this.model.head.getHeadStyle());
    this.model.head.setHeadScript(this.model.head.getHeadScript());
    // update the settings
    this.model.head.updateFromDom();
    // restore event listeners
    this.view.stage.initEvents(this.contentWindow_);
    // set node outline if palette selected
	if (this.view.stage.paletteItemSelected)
	{
		silexApp.controller.componentPaletteController.mouseOutPalette();
	}
	// notify the caller
    if (opt_cbk) {
      opt_cbk();
    }
    // loading
    setTimeout(goog.bind(function() {
      goog.dom.classlist.remove(this.view.stage.element, silex.model.File.LOADING_CSS_CLASS);
      goog.dom.classlist.remove(this.view.stage.element, silex.model.File.LOADING_LIGHT_CSS_CLASS);
      // refresh the view (workaround for a bug where no page is opened after open a website or undo)
      var page = this.model.page.getCurrentPage();
      this.model.page.setCurrentPage(page);
      setTimeout(goog.bind(function() {
        this.model.page.setCurrentPage(page);
      }, this), 300);
    }, this), 100);
  });
};


/**
 * load all scripts needed for edit and display
 * in the iframe
 * WARNING:
 *    this is not used when the scripts are cached by the browser (see how this method is called, only the 1st time the website is loaded)
 * @param {?function()=} opt_onSuccess
 * @param {?function()=} opt_onError
 */
silex.model.File.prototype.includeEditionTags = function(opt_onSuccess, opt_onError) {
  var tags = [];
  // css tags
  var styles = [
    'css/silex/editable.css'
  ];
  var scripts = [
  ];
  goog.array.forEach(styles, function(url) {
    var tag = this.contentDocument_.createElement('link');
    tag.rel = 'stylesheet';
    tag.href = silex.utils.Url.getAbsolutePath(url, window.location.href);
    tags.push(tag);
  }, this);
  goog.array.forEach(scripts, function(url) {
	var tag = this.contentDocument_.createElement('script');
	tag.src = url;
	tag.type = 'text/javascript';
	tags.push(tag);
  }, this);
  // load all tags
  this.model.head.addTempTag(tags, opt_onSuccess, opt_onError);
};


/**
 * build a string of the raw html content
 * remove all internal objects and attributes
 * @param optID : optional interface-id value to put in meta tag 
 */
silex.model.File.prototype.getHtml = function(optID) {
  console.log('getHtml ('+optID+')');
  if (this.contentDocument_.readyState === 'loading')
  {
    console.log('doc still loading, skipping getHTML...');
    return null;
  }
  
  silex.utils.DomCleaner.cleanupFirefoxInlines(this.contentDocument_);
  // clone
  var cleanFile = /** @type {Node} */ (this.contentDocument_.cloneNode(true));
  
  // update style tag (the dom do not update automatically when we change document.styleSheets)
  this.model.property.updateSilexStyleTag(/** @type {Document} */ (cleanFile));
  
  if (optID)
  {
	  let idTag = cleanFile.querySelector('meta[name="interface-id"]');
	  if (!idTag)
	  {
		  idTag = document.createElement('meta');
		  idTag.setAttribute('name', 'interface-id');
		  cleanFile.head.appendChild(idTag);
	  }
	  idTag.setAttribute('content', optID);
  }
  
  // cleanup
  this.model.head.removeTempTags(/** @type {Document} */ (cleanFile).head);
  this.model.body.removeEditableClasses(/** @type {!Element} */ (cleanFile));
  silex.utils.Style.removeInternalClasses(/** @type {!Element} */ (cleanFile), false, true);
  silex.utils.DomCleaner.cleanupCTAT(cleanFile.body);
  let phantomNode = cleanFile.body.querySelector('.silex-phantom-node');
  phantomNode && phantomNode.parentNode.removeChild(phantomNode);
  // reset the style set by stage on the body
  goog.style.setStyle(/** @type {Document} */ (cleanFile).body, 'minWidth', '');
  goog.style.setStyle(/** @type {Document} */ (cleanFile).body, 'minHeight', '');
  
  // put back the "silex-runtime" css class after editing
  goog.dom.classlist.add(/** @type {Document} */ (cleanFile).body, 'silex-runtime');
  
  // get html
  var rawHtml = /** @type {Document} */ (cleanFile).documentElement.innerHTML;
  
  // add the outer html (html tag)
  rawHtml = '<html>' + rawHtml + '</html>';
  
  // add doctype
  rawHtml = '<!DOCTYPE html>' + rawHtml;
  
  // cleanup HTML
  rawHtml = this.model.element.unprepareHtmlForEdit(rawHtml);
  
  // add the user's head tag
  rawHtml = this.model.head.insertUserHeadTag(rawHtml);
  
  // beutify html
  rawHtml = window['html_beautify'](rawHtml);
  return rawHtml;
};


/**
 * async verion of getHtml
 * this is an optimisation needed to speedup drag start (which creates an undo point)
 * it uses generator to lower the load induced by these operations
 */
silex.model.File.prototype.getHtmlAsync = function (cbk) {
  var generator = this.getHtmlGenerator();
  this.getHtmlNextStep(cbk, generator);
};


/**
 * does one more step of the async getHtml process
 */
silex.model.File.prototype.getHtmlNextStep = function (cbk, generator) {
  let res = generator.next();
  if(res.done) {
    setTimeout(() => cbk(res.value), 50);
  }
  else {
    setTimeout(() => this.getHtmlNextStep(cbk, generator), 50);
  }
};


/**
 * the async getHtml process
 * yield after each step
 */
silex.model.File.prototype.getHtmlGenerator = function* () {
	if (this.contentDocument_.readyState === 'loading' 
	|| !this.contentDocument_.body.innerHTML.trim()
	|| !this.contentDocument_.head.innerHTML.trim())
	{
		console.log('doc still loading, skipping getHTML...');
		return null;
	}
	silex.utils.DomCleaner.cleanupFirefoxInlines(this.contentDocument_);
	// update style tag (the dom do not update automatically when we change document.styleSheets)
	let updatedStyles = this.model.property.updateSilexStyleTag(this.contentDocument_, false);
	// clone
	var cleanFile = /** @type {Node} */ (this.contentDocument_.cloneNode(true));
  yield;
	var styleTag = cleanFile.querySelector('.' + silex.model.Property.INLINE_STYLE_TAG_CLASS_NAME);
	if (styleTag)
		styleTag.innerHTML = updatedStyles;
  yield;
	// cleanup
	this.model.head.removeTempTags(/** @type {Document} */ (cleanFile).head);
  yield;
	this.model.body.removeEditableClasses(/** @type {!Element} */ (cleanFile));
  yield;
	silex.utils.Style.removeInternalClasses(/** @type {!Element} */ (cleanFile), false, true);
  yield;
	silex.utils.DomCleaner.cleanupCTAT(cleanFile.body);
  yield;
	// reset the style set by stage on the body
	goog.style.setStyle(/** @type {Document} */ (cleanFile).body, 'minWidth', '');
	goog.style.setStyle(/** @type {Document} */ (cleanFile).body, 'minHeight', '');
  yield;
	// put back the "silex-runtime" css class after editing
	goog.dom.classlist.add(/** @type {Document} */ (cleanFile).body, 'silex-runtime');
	// remove component outline
	let phantomNode = cleanFile.body.querySelector('.silex-phantom-node');
    phantomNode && phantomNode.parentNode.removeChild(phantomNode);
  yield;
	// get html
	var rawHtml = /** @type {Document} */ (cleanFile).documentElement.innerHTML;
  yield;
	// add the outer html (html tag)
	rawHtml = '<html>' + rawHtml + '</html>';
	// add doctype
	rawHtml = '<!DOCTYPE html>' + rawHtml;
  yield;
	// cleanup HTML
	rawHtml = this.model.element.unprepareHtmlForEdit(rawHtml);
  yield;
	// add the user's head tag
	rawHtml = this.model.head.insertUserHeadTag(rawHtml);
  yield;
	// beutify html
	rawHtml = window['html_beautify'](rawHtml);
	return rawHtml;
};


/**
 * load an empty new file
 */
silex.model.File.prototype.newFile = function(cbk, opt_errCbk) {
  this.openFromUrl(silex.model.File.CREATION_TEMPLATE, cbk, opt_errCbk);
};


/**
 * load an arbitrary url as a silex html file
 * will not be able to save
 */
silex.model.File.prototype.openFromUrl = function(url, cbk, opt_errCbk) {
  silex.service.CloudStorage.getInstance().loadLocal(url,
      goog.bind(function(rawHtml) {
        this.setUrl(null);
        if (cbk) {
          cbk(rawHtml);
        }
      }, this), opt_errCbk);
};


/**
 * save a file with a new name
 */
silex.model.File.prototype.saveAs = function(url, rawHtml, cbk, opt_errCbk) {
  // save the data
  this.setUrl(url);
  this.save(rawHtml, cbk, opt_errCbk);
};


/**
 * reset data, close file
 */
silex.model.File.prototype.close = function() {
  this.url = null;
};


/**
 * get the url of the file
 */

silex.model.File.prototype.getUrl = function() {
  // revert to relative URL
  if (this.url){
    var baseUrl = silex.utils.Url.getBaseUrl();
    return silex.utils.Url.getRelativePath(this.url, baseUrl);
  }
  return this.url;
};


/**
 * store url of this file
 * @param {?string} url
 */
silex.model.File.prototype.setUrl = function(url) {
  if (url) {
    var baseUrl = silex.utils.Url.getBaseUrl();
    url = silex.utils.Url.getAbsolutePath(url, baseUrl);
  }
  this.url = url;
};

/**
 *	Return the interface-id stored in meta tag in the working document.
 *	The interface id is used to identify the interface to the tutoring
 *	service
 */
silex.model.File.prototype.getMeta = function(name)
{
	var idTag = this.contentDocument_.querySelector('meta[name="'+name+'"]');
	if (idTag)
	{
		if (idTag.getAttribute('content'))
			return idTag.getAttribute('content');
		else
			return '';
	}
	return null;
};

/**
 *	Set the interface-id meta tag in the working document.
 *	The interface id is used to identify the interface to the tutoring
 *	service.
 *	@param anId the new id.
 */
silex.model.File.prototype.setMeta = function(name, content)
{
	var idTag = this.contentDocument_.querySelector('meta[name="'+name+'"]');
	if (!idTag)
	{
		idTag = document.createElement('meta');
		this.contentDocument_.head.appendChild(idTag);
	}
	idTag.setAttribute('name', name);
	idTag.setAttribute('content', content);
};

/**
*	Reset the imgUrlMap, which tracks cloud storage assets associated w/ the open file
*/
silex.model.File.prototype.resetUrlMap = function()
{
	this.imgUrlMap = {};
};

/**
*	Write the imgUrlMap object out to the actual interface
*/
silex.model.File.prototype.updateAssetMap = function()
{
	this.setMeta('asset-map', JSON.stringify(this.imgUrlMap));
}

/**
*	Convert CSS casing to camel case
*/
function camelizeCase(str)
{
	var pieces = str.split('-');
	var whole = pieces[0];
	for (let i = 1; i < pieces.length; i++)
	{
		whole += pieces[i].charAt(0).toUpperCase() + pieces[i].slice(1);
	}
	return whole;
}

