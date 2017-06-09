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
 *   This class is used to manage Silex elements
 *   It has methods to manipulate the DOM elements
 *      created by new silex.model.Element().createElement
 */

goog.provide('silex.model.Element');

goog.require('goog.net.EventType');
goog.require('goog.net.ImageLoader');
goog.require('silex.types.Model');


/**
 * direction in the dom
 * @enum {string}
 */
silex.model.DomDirection = {
  UP: "UP",
  DOWN: "DOWN",
  TOP: "TOP",
  BOTTOM: "BOTTOM"
};


/**
 * @constructor
 * @param  {silex.types.Model} model  model class which holds the other models
 * @param  {silex.types.View} view  view class which holds the other views
 */
silex.model.Element = function(model, view) {
  // store the model and the view
  /**
   * @type {silex.types.Model}
   */
  this.model = model;
  /**
   * @type {silex.types.View}
   */
  this.view = view;
};


/**
 * constant for minimum elements size
 * @const
 * @type {number}
 */
silex.model.Element.MIN_HEIGHT = 20;


/**
 * constant for minimum elements size
 * @const
 * @type {number}
 */
silex.model.Element.MIN_WIDTH = 20;


/**
 * constant for loader on elements
 * @const
 * @type {string}
 */
silex.model.Element.LOADING_ELEMENT_CSS_CLASS = 'loading-image';


/**
 * constant for silex element type
 * @const
 * @type {string}
 */
silex.model.Element.TYPE_CONTAINER = 'container';

silex.model.Element.TYPE_SCROLLCONTAINER = 'scrollcontainer';

/**
 * constant for silex element type
 * @const
 * @type {string}
 */
silex.model.Element.TYPE_IMAGE = 'image';


/**
 * constant for silex element type
 * @const
 * @type {string}
 */
silex.model.Element.TYPE_TEXT = 'text';


/**
 * constant for silex element type
 * @const
 * @type {string}
 */
silex.model.Element.TYPE_HTML = 'html';


/**
 * constant for silex element type
 * @const
 * @type {string}
 */
silex.model.Element.TYPE_ATTR = 'data-silex-type';


/**
 * constant for the class name of the element content
 * @const
 * @type {string}
 */
silex.model.Element.ELEMENT_CONTENT_CLASS_NAME = 'silex-element-content';


/**
 * constant for the attribute name of the links
 * @const
 * @type {string}
 */
silex.model.Element.LINK_ATTR = 'data-silex-href';


/**
 * constant for the class name of selected components
 * @const
 * @type {string}
 */
silex.model.Element.SELECTED_CLASS_NAME = 'silex-selected';


/**
 * constant for the class name of pasted components
 * this will be removed from the component as soon as it is dragged
 * @const
 * @type {string}
 */
silex.model.Element.JUST_ADDED_CLASS_NAME = 'silex-just-added';


/**
 * prepare element for edition
 * @param  {string} rawHtml   raw HTML of the element to prepare
 * @return {string} the processed HTML
 */
silex.model.Element.prototype.prepareHtmlForEdit = function(rawHtml) {
  // prevent the user scripts from executing while editing
  rawHtml = rawHtml.replace(/<script.*class=\"silex-script\".*?>/gi, '<script type="text/notjavascript" class="silex-script">');
  // convert to absolute urls
  if (this.model.file.getUrl()) {
    rawHtml = silex.utils.Url.relative2Absolute(rawHtml, silex.utils.Url.getBaseUrl() + this.model.file.getUrl());
  }
  return rawHtml;
};



/**
 * unprepare element for edition
 * @param  {string} rawHtml   raw HTML of the element to prepare
 * @return {string} the processed HTML
 */
silex.model.Element.prototype.unprepareHtmlForEdit = function(rawHtml) {
  // put back the user script
  // rawHtml = rawHtml.replace(/type=\"text\/notjavascript\"/gi, 'type="text/javascript"');
  // remove cache control used to refresh images after editing by pixlr
  rawHtml = silex.utils.Dom.removeCacheControl(rawHtml);
  if (this.model.file.getUrl()) {
    // convert to relative urls
    let baseUrl = silex.utils.Url.getBaseUrl();
    rawHtml = silex.utils.Url.absolute2Relative(rawHtml, baseUrl + this.model.file.getUrl());
    // put back the static scripts (protocol agnostic)
    let staticUrl = baseUrl+'static/'; //baseUrl.substr(baseUrl.indexOf('//')) + 'static/';
    rawHtml = rawHtml.replace(new RegExp('\.\./\.\./\.\./\.\./\.\./[\.\./]*static/', 'g'), staticUrl);
  }
  return rawHtml;
};


/**
 * get num tabs
 * example: getTabs(2) returns '        '
 * @param {number} num
 * @return {string}
 */
silex.model.Element.prototype.getTabs = function(num) {
  var tabs = '';
  for (var n = 0; n < num; n++) {
    tabs += '    ';
  }
  return tabs;
};


/**
 * get/set type of the element
 * @param  {Element} element   created by silex, either a text box, image, ...
 * @return  {string|null}           the style of the element
 * example: for a container this will return "container"
 */
silex.model.Element.prototype.getType = function(element) {
  //return goog.style.getStyle(element, styleName);
  return element.getAttribute(silex.model.Element.TYPE_ATTR);
};


/**
 * get all the element's styles
 * @param  {Element} element   created by silex, either a text box, image, ...
 * @param {?boolean=} opt_computed use window.getComputedStyle instead of the element's stylesheet
 * @return  {string}           the styles of the element
 */
silex.model.Element.prototype.getAllStyles = function(element, opt_computed) {
  var styleObject = this.model.property.getStyleObject(element, opt_computed);
  var styleStr = silex.utils.Style.styleToString(styleObject);
  return this.unprepareHtmlForEdit(styleStr);
};


/**
 * get/set style of the element
 * @param  {Element} element   created by silex, either a text box, image, ...
 * @param  {string} styleName  the style name
 * @param {?boolean=} opt_computed use window.getComputedStyle instead of the element's stylesheet
 * @return  {string|null}           the style of the element
 */
silex.model.Element.prototype.getStyle = function(element, styleName, opt_computed) 
{
  var styleObject;
  if (element.className.includes("CTAT") 
  && !element.className.includes("CTATHintWidget") 
  && !(styleName == 'top' || styleName == 'left' || 
	   styleName == 'height' || styleName == 'width'))
  {
	let styleData = silex.utils.CTAT.getCTATCSSSelector(element, camelizeCase(styleName));
	styleName = styleData['style'];
	let selector = styleData['selector'];
	styleObject = this.model.property.getStyleObject(element, false, selector); 
  }
  else
  {
	styleObject = this.model.property.getStyleObject(element, opt_computed);
  }
  var cssName = goog.string.toSelectorCase(styleName);
  if (styleObject && styleObject[cssName]) 
  {
 	return this.unprepareHtmlForEdit(styleObject[cssName]);
  }
  else if (!styleObject)
  {
	  return null;
  }
  return null;
};

/**
 * get/set style of element from a container created by silex
 * @param  {Element} element            created by silex, either a text box, image, ...
 * @param  {string}  styleName          the style name, camel case, not css with dashes
 * @param  {?string=}  opt_styleValue     the value for this styleName
 */
silex.model.Element.prototype.setStyle = function(element, styleName, opt_styleValue) 
{
	if (styleName === 'backgroundColor' || styleName === 'backgroundImage')
	{
		//clear old background img url
		let oldVal = this.getStyle(element, 'backgroundImage');
		if (oldVal && oldVal.includes('blob:'))
		{
			let blobUrl = /url\((['"])(.*)\1\)/.exec(oldVal)[2];
			URL.revokeObjectURL(blobUrl);
			delete this.model.file.imgUrlMap[blobUrl];
			this.model.file.updateAssetMap();
		}
	}
	var selector, styleObject;
	//ctat elements have different selector format
	if (element.className.includes('CTAT') 
	&& !element.className.includes('CTATHintWidget')
	&& !(styleName == 'top' || styleName == 'left' 
	|| styleName == 'height' || styleName == 'width'))
	{
		let styleData = silex.utils.CTAT.getCTATCSSSelector(element, camelizeCase(styleName));
		styleName = styleData['style'];
		selector = styleData['selector'];
		styleObject = this.model.property.getStyleObject(element, false, selector);
	}
	else
	{
		selector = '.' + this.model.property.getSilexId(element);
		styleObject = this.model.property.getStyleObject(element);
	}
	// convert to css case
	styleName = goog.string.toSelectorCase(styleName);
	if (!styleObject) 
	{
		styleObject = {};
	}
	if (styleObject[styleName] !== opt_styleValue) 
	{
		if (goog.isDefAndNotNull(opt_styleValue)) 
		{
			styleObject[styleName] = this.prepareHtmlForEdit(opt_styleValue);
		}
		else 
		{
			styleObject[styleName] = '';
		}
		this.model.property.setStyle(selector, styleObject);
	}
	// remove the 'just pasted' class
	element.classList.remove(silex.model.Element.JUST_ADDED_CLASS_NAME);
	// re-render property tool
	if (styleName === 'top')
	{
		this.view.propertyTool.positionPane.redrawY(opt_styleValue);
	}
	else if (styleName === 'left')
	{
		this.view.propertyTool.positionPane.redrawX(opt_styleValue);
	}
	//resize/redraw ctat components depending on type
	else if (styleName == 'width' 
		 ||  styleName == 'height' 
		 || (styleName.includes('border') && styleName.toLowerCase().includes('width')))
	{
		if (element.className.includes('CTATTable'))
		{
			this.resizeTable(element, styleName);
		}
		//redraw ctat component when div resized
		this.model.file.getContentWindow().CTATTutor.callComponentFunction(element, function(component)
			{
				if (component.render) component.render();
			});	
	}
};

/**
 * get/set a property of an element from a container created by silex
 * @param  {Element} element            created by silex, either a text box, image, ...
 * @param  {string}  propertyName          the property name
 * @param  {?string=}  opt_propertyValue     the value for this propertyName
 * @param  {?boolean=}  opt_applyToContent    apply to the element or to its ".silex-element-content" element
 * example: element.setProperty(imgElement, 'style', 'top: 5px; left: 30px;')
 */
silex.model.Element.prototype.setProperty = function(element, propertyName, opt_propertyValue, opt_applyToContent) {
  if (opt_applyToContent) {
    element = this.getContentNode(element);
  }
  if (goog.isDefAndNotNull(opt_propertyValue)) {
    element.setAttribute(propertyName, /** @type {!string} */ (opt_propertyValue));
  }
  else {
    element.removeAttribute(propertyName);
  }
};


/**
 * @param {Element} element
 * @param {string} url    URL of the image chosen by the user
 */
silex.model.Element.prototype.setBgImage = function(element, url, filename) {
  console.log("set bg image()");
  if (url) 
  {
	this.setStyle(element, 'backgroundImage', silex.utils.Url.addUrlKeyword(url));
  }
  else 
  {
	this.setStyle(element, 'backgroundImage');
  }
  
  // redraw tools
  this.model.body.setSelection(this.model.body.getSelection());
};


/**
 * get/set html from a container created by silex
 * @param  {Element} element  created by silex, either a text box, image, ...
 * @return  {string}  the html content
 */
silex.model.Element.prototype.getInnerHtml = function(element) {
  // disable editable
  this.model.body.setEditable(element, false);
  var innerHTML = this.getContentNode(element).innerHTML;
  // remove absolute urls and not executable scripts
  innerHTML = this.unprepareHtmlForEdit(innerHTML);
  // re-enable editable
  this.model.body.setEditable(element, true);
  return innerHTML;
};


/**
 * get/set element from a container created by silex
 * @param  {Element} element  created by silex, either a text box, image, ...
 * @param  {string} innerHTML the html content
 */
silex.model.Element.prototype.setInnerHtml = function(element, innerHTML) {
  // get the container of the html content of the element
  var contentNode = this.getContentNode(element);
  // cleanup
  this.model.body.setEditable(element, false);
  // remove absolute urls and not executable scripts
  innerHTML = this.prepareHtmlForEdit(innerHTML);
  // set html
  contentNode.innerHTML = innerHTML;
  // make editable again
  this.model.body.setEditable(element, true);
};


/**
 * get/set element from a container created by silex
 * @param  {Element} element  created by silex, either a text box, image, ...
 * @return  {Element}  the element which holds the content, i.e. a div, an image, ...
 */
silex.model.Element.prototype.getContentNode = function(element) {
  var content;
  // find the content elements
  var contentElements = goog.dom.getElementsByClass(
      silex.model.Element.ELEMENT_CONTENT_CLASS_NAME,
      element);
  if (contentElements && contentElements.length === 1) {
    // image or html box case
    content = contentElements[0];
  }
  else {
    // text box or container case
    content = element;
  }
  return content;
};


/**
 * move the element up/down the DOM
 * @param  {Element} element
 * @param  {silex.model.DomDirection} direction
 */
silex.model.Element.prototype.move = function(element, direction) {
  switch(direction) {
    case silex.model.DomDirection.UP:
      let sibling = this.getNextElement(element);
      if(sibling) {
        // insert after
        element.parentNode.insertBefore(sibling, element);
      }
      break;
    case silex.model.DomDirection.DOWN:
      let sibling = this.getPreviousElement(element);
      if(sibling) {
        // insert before
        element.parentNode.insertBefore(sibling, element.nextSibling);
      }
      break;
    case silex.model.DomDirection.TOP:
      element.parentNode.appendChild(element);
      break;
    case silex.model.DomDirection.BOTTOM:
      element.parentNode.insertBefore(element, element.parentNode.childNodes[0]);
      break;
  }
  // remove the 'just pasted' class
  element.classList.remove(silex.model.Element.JUST_ADDED_CLASS_NAME);
};


/**
 * get the previous element in the DOM, which is a Silex element
 * @param {Element} element
 * @return {Element|null}
 */
silex.model.Element.prototype.getPreviousElement = function(element) {
  let len = element.parentNode.childNodes.length;
  let res = null;
  for (let idx=0; idx < len; idx++) 
  {
    let el = element.parentNode.childNodes[idx];
    if (el.nodeType === 1 && this.getType(el) !== null) 
	{
      if(el === element) 
	  {
        return res;
      }
      res = el;
    }
  }
  console.warn('no prev element');
  return null;
};


/**
 * get the previous element in the DOM, which is a Silex element
 * @param {Element} element
 * @return {Element|null}
 */
silex.model.Element.prototype.getNextElement = function(element) {
  let len = element.parentNode.childNodes.length;
  let res = null;
  for (let idx=len - 1; idx >= 0; idx--) 
  {
    let el = element.parentNode.childNodes[idx];
    if (el.nodeType === 1 && this.getType(el) !== null) 
	{
      if(el === element) 
	  {
        return res;
      }
      res = el; 
    }
  }
  console.warn('no next element');  
  return null;
};


/**
 * set/get the image URL of an image element
 * @param  {Element} element  container created by silex which contains an image
 * @return  {string}  the url of the image
 */
silex.model.Element.prototype.getImageUrl = function(element) {
  var url = '';
  if (element.getAttribute(silex.model.Element.TYPE_ATTR) === silex.model.Element.TYPE_IMAGE) {
    // get the image tag
    let img = this.getContentNode(element);
    if (img) {
      url = img.getAttribute('src');
    }
    else {
      console.error('The image could not be retrieved from the element.', element);
    }
  }
  else {
    console.error('The element is not an image.', element);
  }
  return url;
};


/**
 * set/get the image URL of an image element
 * @param  {Element} element  container created by silex which contains an image
 * @param  {string} url  the url of the image
 * @param  {?function(Element, Element)=} opt_callback the callback to be notified when the image is loaded
 * @param  {?function(Element, string)=} opt_errorCallback the callback to be notified of errors
 */
silex.model.Element.prototype.setImageUrl = function(element, url, opt_callback, opt_errorCallback) {
  if (element.getAttribute(silex.model.Element.TYPE_ATTR) === silex.model.Element.TYPE_IMAGE) {
    // get the image tag
    let img = this.getContentNode(element);
    if (img) {
		
		let imgTag = document.createElement('img');
		imgTag.setAttribute('src', url);
		imgTag.classList.add(silex.model.Element.ELEMENT_CONTENT_CLASS_NAME);
		img.appendChild(imgTag);
		img.classList.remove(silex.model.Element.LOADING_ELEMENT_CSS_CLASS);
		if (opt_callback)
		{
			opt_callback(element, imgTag);
		}
	}
    else {
      console.error('The image could not be retrieved from the element.', element);
      if (opt_errorCallback) {
        opt_errorCallback(element, 'The image could not be retrieved from the element.');
      }
    }
  }
  else {
    console.error('The element is not an image.', element);
    if (opt_errorCallback) {
      opt_errorCallback(element, 'The element is not an image.');
    }
  }
};


/**
 * remove a DOM element
 * @param  {Element} element   the element to remove
 */
silex.model.Element.prototype.removeElement = function(element, keepStyles) {
  console.log('removeElement()');
  // check this is allowed, i.e. an element inside the stage container
  if (this.model.body.getBodyElement() !== element
  &&  element.getAttribute('data-silex-id') !== 'background-initial' 
  &&  goog.dom.contains(this.model.body.getBodyElement(), element)) 
  {
	if (!keepStyles)
	{
		//find any blob URLs associated w/ element
		var assets = [];
		if (element.getAttribute('data-silex-type') === 'image')
		{
			let imgTag = goog.dom.getElementByClass('silex-element-content', element);
			let blobUrl = imgTag.getAttribute('src');
			if (blobUrl && blobUrl.includes('blob:'))
			{
				assets.push(blobUrl);
			}
		}
		else if (element.className.includes('CTATImageButton'))
		{
			['data-ctat-image-default', 
			 'data-ctat-image-clicked',
			 'data-ctat-image-disabled',
			 'data-ctat-image-hover'].forEach(function(prop)
			 {
				 let blobUrl = element.getAttribute(prop);
				 if (blobUrl && blobUrl.includes('blob:'))
				 {
					 assets.push(blobUrl);
				 }
			 });
		}
		else
		{
			let bgImg = this.model.element.getStyle(element, 'backgroundImage');
			if (bgImg && bgImg.includes('blob:') && !keepStyles)
			{
				let blobUrl = /url\((['"])(.*)\1\)/.exec(bgImg)[2];
				assets.push(blobUrl);
			}
			if (element.className.includes('CTATAudioButton'))
			{
				let blobUrl = element.getAttribute('data-ctat-src');
				if (blobUrl)
				{
					assets.push(blobUrl);
				}
			}
		}
		//get rid of styles associated w/ element
		this.model.property.removeStylesBySubstring(this.model.property.getSilexId(element));
		//delete images/audio blobs associated w/ element
		assets.forEach(function(asset)
		{
			URL.revokeObjectURL(asset);
			delete this.model.file.imgUrlMap[asset];
		}, this);
		this.model.file.updateAssetMap();
	}
	goog.dom.removeNode(element);
	return true;
  }
  else 
  {
    console.warn('could not delete', element, 'because it is not in the stage element');
	return false;
  }
};


/**
 * append an element to the stage
 * handles undo/redo
 * @param {Element} container
 * @param {Element} element
 */
silex.model.Element.prototype.addElement = function(container, element) {
  goog.dom.appendChild(container, element);
  // add the class to keep the element above all others
  element.classList.add(silex.model.Element.JUST_ADDED_CLASS_NAME);
 };


/**
 * element creation
 * create a DOM element, attach it to this container
 * and returns a new component for the element
 * @param  {string} type  the type of the element to create,
 *    see TYPE_* constants of the class @see silex.model.Element
 * @return  {Element}   the newly created element
 */
silex.model.Element.prototype.createElement = function(type, data, optStyles) {
  // find the container (main background container or the stage)
  var bodyElement = this.model.body.getBodyElement();
  var container = (data && data.parent) ? data.parent : goog.dom.getElementByClass(silex.view.Stage.BACKGROUND_CLASS_NAME, bodyElement);
  if (!container) {
    container = bodyElement;
  }
  
  var styleObject = optStyles || {};
  //calculate default position
  if (!(styleObject.top || styleObject.left))
  {
	  // take the scroll into account (drop at (100, 100) from top left corner of the window, not the stage)
	  var offsetX = 100 + this.view.stage.getScrollX();
	  var offsetY = 100 + this.view.stage.getScrollY();
	  // check if any elements in the way
	  var regex = new RegExp("{[^}]*top: *"+offsetY+"px[^}]*left: *"+offsetX+"px[^}]*}");
	  var inlineStyles = this.model.property.updateSilexStyleTag(this.model.file.getContentDocument(), false);
	  while (regex.test(inlineStyles))
	  {
		offsetX += 20;
		offsetY += 20;
		regex = new RegExp("{[^}]*top: *"+offsetY+"px[^}]*left: *"+offsetX+"px[^}]*}");
	  }
	  styleObject.top = offsetY+'px';
	  styleObject.left = offsetX+'px';
  }
  else if (!styleObject.top)
	  styleObject.top = '100px';
  else if (!styleObject.left)
	  styleObject.left = '100px';
  
  // default dimensions
  if (!styleObject.height)
	  styleObject.height = '100px';
  if (!styleObject.width)
	  styleObject.width = '100px';

  // create the element
  var element = null;
  if (type.includes('ctat'))
  {
	element = this.createCtatElement(type.substr(5), styleObject);
  }
  else if (type.includes('question'))
  {
	element = this.createQuestion(type.substr(9), data, styleObject);
	//type = type.substr(9); 
  }
  else
  {
	switch (type) {

    // container
    case silex.model.Element.TYPE_CONTAINER:
      element = this.createContainerElement();
      // add a default style
      styleObject.backgroundColor = '#FFFFFF';
      break;
	  
    // text
    case silex.model.Element.TYPE_TEXT:
      element = this.createTextElement();
      break;

    // HTML box
    case silex.model.Element.TYPE_HTML:
      element = this.createHtmlElement();
      // add a default style
      styleObject.backgroundColor = '#FFFFFF';
      break;

    // Image
    case silex.model.Element.TYPE_IMAGE:
      element = this.createImageElement();
      break;
	
	case 'group':
	  element = this.createCTATGroup(data);
	  styleObject.display = 'none';
	  styleObject.width = '0px';
	  styleObject.height = '0px';
	  break;
    }
	
  }
  
  if (!element)
  {	  
	  return;
  }
  // init the element
  if (type !== 'group')
  {
	  goog.dom.classlist.add(element, silex.model.Body.EDITABLE_CLASS_NAME);
  }
  this.model.property.initSilexId(element, this.model.file.getContentDocument());
  // apply the style
  let selector = '.' + this.model.property.getSilexId(element);
  this.model.property.setStyle(selector, styleObject);
  // make it editable
  this.model.body.setEditable(element, true);
  // add css class for Silex styles
  if (!type.includes('ctat'))
  {
	goog.dom.classlist.add(element, type + '-element');
  }
  // add to stage
  this.addElement(container, element);
  if (type.includes('question') || type === 'group')
  {
	this.view.stage.controller.insertMenuController.doAddElement(element);
	if (type.includes('group'))
	{
		//deselect groups
		this.model.body.setSelection([]);
		return null;
	}
  }
  return element;
}

/**
*	Create a CTATGroupingComponent
*	@param componentData an object containing a list of member IDs and an ID for the group itself
*/
silex.model.Element.prototype.createCTATGroup = function(componentData)
{
	var groupElement = document.createElement('div');
	groupElement.classList.add('CTATGroupingComponent');
	groupElement.setAttribute('data-ctat-componentlist', componentData.componentList);
	groupElement.setAttribute('id', componentData.id);
	
	return groupElement;
};

/**
*	Create a question element
*	@param type the type of question (only multchoice implemented currently)
*	@param questionInfo an object containing data about the question
*	@param styleObject an object containing styles for the question
*/
silex.model.Element.prototype.createQuestion = function (type, questionInfo, styleObject)
{
	switch(type)
	{
		case 'multchoice':
			return this.createMultChoice(questionInfo, styleObject);
	}
};

/**
*	Create a multiple choice question element
*	@param questionInfo an object containing data about the question
*	@param styleObject an object containing styles for the question
*/
silex.model.Element.prototype.createMultChoice = function(questionInfo, styleObject)
{	
	var top, left, width, height;
	var wrapper = questionInfo['wrapper']; 
	if (wrapper)
	{
		//remove old contents
		let toRemove = [];
		for (let i = 0; i < wrapper.childNodes.length; i++)
		{
			//take out everything but resize handles
			if (wrapper.childNodes[i].className 
			&& !wrapper.childNodes[i].className.includes('ui-resizable-handle'))
			{
				toRemove.push(wrapper.childNodes[i]);
			}
			else if (wrapper.childNodes[i].nodeName.toLowerCase() == 'br')
			{
				toRemove.push(wrapper.childNodes[i]);
			}
		}
		for (let i in toRemove)
		{
			wrapper.removeChild(toRemove[i]);
		}
		
		top = this.getStyle(wrapper, 'top');
		left = this.getStyle(wrapper, 'left');
		width = this.getStyle(wrapper, 'width');
		height = this.getStyle(wrapper, 'height');
	}
	else
	{
		wrapper = this.createContainerElement();
		width = '200px';
		height = '100px';
	}
	//question content
	var questionText = document.createElement('div');
	questionText.innerHTML = questionInfo['question'];
	questionText.style.fontSize = questionInfo['qFontSize'];
	questionText.classList.add('mult-choice-question');
	wrapper.appendChild(questionText);
	//answers (radio buttons or checkboxes)
	for (var i = 0; i < questionInfo['answers'].length; i++)
	{
		var option = questionInfo['allowMultAnswer'] ? this.createCtatElement('checkbox', styleObject) :
			this.createCtatElement('radiobutton', styleObject);
		option.setAttribute('name', questionInfo['name']);
		option.setAttribute('data-ctat-label', questionInfo['answers'][i]);
		option.style.fontSize = questionInfo['aFontSize'];
		option.style.padding = '3px';
		option.classList.add('ctat-multchoice-option');
		option.setAttribute('id', questionInfo['name']+'-option-'+i);
		//grade when clicked vs use submit button
		if (!questionInfo['gradeOnInput'])
			option.setAttribute('data-ctat-tutor', "false");
		
		wrapper.appendChild(option);
	}
	if (questionInfo['includeSubmit'])
	{
		let submitBtn = this.createCtatElement('submitbutton', {});
		submitBtn.setAttribute('data-ctat-target', questionInfo['name']);
		submitBtn.setAttribute('data-ctat-label', 'Submit');
		submitBtn.style.marginTop = '10px';
		submitBtn.classList.add('mult-choice-gen-submit-btn');
		wrapper.appendChild(submitBtn);
	}
	//font info
	styleObject.fontFamily = questionInfo['fontFamily'];
	//positioning
	if (top) styleObject.top = top;
	if (left) styleObject.left = left;
	//size
	styleObject.width = width;
	styleObject.height = height;
	styleObject.padding = '10px';
	//set id
	wrapper.setAttribute('id', questionInfo['name']);
	
	return wrapper;
};

/**
 * element creation method for a given type
 * called from createElement
 * @return {Element}
 */
silex.model.Element.prototype.createContainerElement = function() {
  // create the conatiner
  var element = goog.dom.createElement('div');
  element.setAttribute(silex.model.Element.TYPE_ATTR, silex.model.Element.TYPE_CONTAINER);
  return element;
};


/**
 * element creation method for a given type
 * called from createElement
 * @return {Element}
 */
silex.model.Element.prototype.createTextElement = function() {
  // create the element
  var element = goog.dom.createElement('div');
  element.setAttribute(silex.model.Element.TYPE_ATTR, silex.model.Element.TYPE_TEXT);
  element.classList.add('CTATTextField');
  // create the container for text content
  var textContent = goog.dom.createElement('div');
  // add empty content
  textContent.innerHTML = 'Double click to edit';
  goog.dom.appendChild(element, textContent);
  // add a marker to find the inner content afterwards, with getContent
  goog.dom.classlist.add(textContent, silex.model.Element.ELEMENT_CONTENT_CLASS_NAME);
  // add normal class for default text formatting
  // sometimes there is only in text node in textContent
  // e.g. whe select all + remove formatting
  goog.dom.classlist.add(textContent, 'normal');
  
  element.addEventListener('dblclick', this.textEditListener.bind(this, element));

  return element;
};

/**
*	Function to handle double clicks on text blocks: shows text editor dialog
*	@param element the text block element
*/
silex.model.Element.prototype.textEditListener = function(element)
{
	if (!this.view.stage.textEditWindow)
	{
		this.view.stage.initTextEditWindow(true, element);
	}
	else
	{
		this.view.stage.showTextEditWindow(element);
	}
};

/**
*	Function to handle double click on question elements: shows question editor dialog
*	@param element the question element
*/
silex.model.Element.prototype.questionEditListener = function(element)
{
	this.view.stage.showMultChoiceWindow(element);
}

/**
 * element creation method for a given type
 * called from createElement
 * @return {Element}
 */
silex.model.Element.prototype.createHtmlElement = function() {
  // create the element
  var element = goog.dom.createElement('div');
  element.setAttribute(silex.model.Element.TYPE_ATTR, silex.model.Element.TYPE_HTML);
  // create the container for html content
  var htmlContent = goog.dom.createElement('div');
  htmlContent.innerHTML = '<p>New HTML box</p>';
  goog.dom.appendChild(element, htmlContent);
  // add a marker to find the inner content afterwards, with getContent
  goog.dom.classlist.add(htmlContent, silex.model.Element.ELEMENT_CONTENT_CLASS_NAME);

  return element;
};


/**
 * element creation method for a given type
 * called from createElement
 * @return {Element}
 */
silex.model.Element.prototype.createImageElement = function() {
  // create the element
  var element = goog.dom.createElement('div');
  element.setAttribute(silex.model.Element.TYPE_ATTR, silex.model.Element.TYPE_IMAGE);
  return element;
};


/**
 * element creation method for ctat components
**/
silex.model.Element.prototype.createCtatElement = function(type, defaultStyle)
{
	console.log("createCtatElement( "+type+" )");
	var element = goog.dom.createElement('div');
	element.setAttribute(silex.model.Element.TYPE_ATTR,'ctat');
	var ctatClass;
	
	switch(type)
	{
		case 'textinput':
			ctatClass = 'CTATTextInput';
			defaultStyle.width = '100px'; 
			defaultStyle.height='1.4em'; 
		break;
		case 'textarea':
			ctatClass = 'CTATTextArea';
			defaultStyle.width='100px';
			defaultStyle.height='2em';
		break;
		case 'textfield':
			ctatClass = 'CTATTextField';
			defaultStyle.width='150px';
			defaultStyle.height='3em';
			break;
		case 'hintwidget': //<div> <table> <tr> <td> <hint window> </td> <td> hint button, done button </td> </tr> </table> </div>
			if (this.model.file.getContentDocument().querySelector('.CTATHintButton')
			||  this.model.file.getContentDocument().querySelector('.CTATDoneButton')
			||	this.model.file.getContentDocument().querySelector('.CTATHintWindow'))
			{
				alert('A tutor can only have one hint button, one done button, and one hint window');
				return null;
			};
			//class added by createCtatHintWidget()
			this.createCtatHintWidget(element);
			defaultStyle.width = '306px';
			defaultStyle.height= '140px';
		break;
		case 'button':
			ctatClass = 'CTATButton';
			defaultStyle.width = '60px';
			defaultStyle.height = '30px';
		break;
		case 'submitbutton':
			ctatClass = 'CTATSubmitButton';
			defaultStyle.width = '60px';
			defaultStyle.height = '30px';
		break;
		case 'audiobutton':
			ctatClass = 'CTATAudioButton';
			defaultStyle.width = '155px'; 
			defaultStyle.height= '30px';
		break;
		case 'imagebutton':
			ctatClass = 'CTATImageButton';
			defaultStyle.width='64px';
			defaultStyle.height='64px';
		break;
		case 'combobox':
			ctatClass = 'CTATComboBox';
			defaultStyle.width = '100px';
			defaultStyle.height= '1.4em';
		break;
		case 'radiobutton':
			ctatClass = 'CTATRadioButton';
			defaultStyle.width = '100px';
			defaultStyle.height= '1.4em';
		break;
		case 'checkbox':
			ctatClass = 'CTATCheckBox';
			defaultStyle.width = '100px';
			defaultStyle.height= '1.4em';
		break;
		case 'fractionbar':
			ctatClass = 'CTATFractionBar';
			defaultStyle.width = '240px';  
			defaultStyle.height= '70px';
		break;
		case 'numberline':
			ctatClass = 'CTATNumberLine';
			defaultStyle.width ='360px';
			defaultStyle.height='90px';
		break;
		case 'piechart':
			ctatClass = 'CTATPieChart';
			defaultStyle.width ='100px';
			defaultStyle.height='100px';
		break;
		case 'jumble':
			ctatClass = 'CTATJumble';
			defaultStyle.width ='150px';
			defaultStyle.height='40px';
			element.setAttribute('data-silex-type', 'container');
			element.classList.add('container-element');
		break;
		case 'table':
			ctatClass = 'CTATTable';
			defaultStyle.width='132px';
			defaultStyle.height='42px';
		break;
		case 'dragndrop':
			ctatClass = 'CTATDragNDrop';
			defaultStyle.width = '200px';
			defaultStyle.height = '110px';
			element.setAttribute('data-silex-type', 'container');
			element.classList.add('container-element');
		break;
		case 'skillwindow':
			ctatClass = 'CTATSkillWindow';
			defaultStyle.width = '240px';
			defaultStyle.height= '140px';
			defaultStyle.boxSizing='content-box';
			defaultStyle.padding='0px';
			defaultStyle.overflow='visible';
		break;
		case 'hintbutton':
			if (this.model.file.getContentDocument().querySelector('.CTATHintButton'))
			{
				alert('A tutor can only have one hint button');
				return null;
			};
			ctatClass = 'CTATHintButton';
			defaultStyle.width = '64px';
			defaultStyle.height = '64px';
			element.setAttribute('id', 'hint');
		break;
		case 'donebutton':
			if (this.model.file.getContentDocument().querySelector('.CTATDoneButton'))
			{
				alert('A tutor can only have one done button');
				return null;
			};
			ctatClass = 'CTATDoneButton';
			defaultStyle.width = '64px';
			defaultStyle.height = '64px';
			element.setAttribute('id', 'done');
		break;
		case 'hintwindow':
			if (this.model.file.getContentDocument().querySelector('.CTATHintWindow'))
			{
				alert('A tutor can only have one hint window');
				return null;
			};
			ctatClass = 'CTATHintWindow';
			defaultStyle.width = '240px';
			defaultStyle.height = '140px';
			defaultStyle.display = 'flex';
		break;
	}
	goog.dom.classlist.add(element, ctatClass);
	return element;
}


/**
 * helper method to create hint window and button group of components
 * takes a handle to a <div> node and fills it in
**/
silex.model.Element.prototype.createCtatHintWidget = function(mainDiv)
{
	console.log("createCtatHintWidget( )");
	//table wrapper for group of components
	var tableWrapper = goog.dom.createElement('table');
	tableWrapper.setAttribute('style', 'width: 100%; height: 100%;');
	var tableRow = tableWrapper.insertRow();
	tableRow.setAttribute('height', '125');
	//hint window
	var hintWindowCell = tableRow.insertCell();
	var hintWindowDiv = goog.dom.createElement('div');
	goog.dom.classlist.add(hintWindowDiv, 'CTATHintWindow');
	goog.dom.appendChild(hintWindowCell, hintWindowDiv);
	//buttons
	//cell for both of em
	var buttonCell = tableRow.insertCell();
	buttonCell.setAttribute('width', '64');
	buttonCell.setAttribute('style', 'padding-top: 0px; padding-bottom: 0px;');
	//hint button
	var hintButtonDiv = goog.dom.createElement('div');
	hintButtonDiv.setAttribute('id', 'hint');
	hintButtonDiv.setAttribute('style', 'margin-bottom: 2px');
	goog.dom.classlist.add(hintButtonDiv, 'CTATHintButton');
	//done button
	var doneButtonDiv = goog.dom.createElement('div');
	doneButtonDiv.setAttribute('id', 'done');
	goog.dom.classlist.add(doneButtonDiv, 'CTATDoneButton');
	//add buttons to cell
	goog.dom.appendChild(buttonCell, hintButtonDiv);
	goog.dom.appendChild(buttonCell, doneButtonDiv);
	//add table to main div wrapper
	goog.dom.appendChild(mainDiv, tableWrapper);
	mainDiv.classList.add("CTATHintWidget"); //handle for attr editor
};

/**
*	Create a CTATSkillWindow component
*	@param mainDiv the <div> tag for the component to inhabit
*/
silex.model.Element.prototype.createCtatSkillWindow = function(mainDiv)
{
	console.log('createCtatSkillWindow()');
	var skillWindow = document.createElement('div');
	skillWindow.classList.add('CTATSkillWindow');
	skillWindow.style.height = '100%';
	skillWindow.style.width = '100%';
	mainDiv.appendChild(skillWindow);
	mainDiv.classList.add('CTATSkillWindowContainer');
};

/**
 * set/get a "silex style link" on an element
 * @param  {Element} element
 * @param  {?string=} opt_link  a link (absolute or relative)
 *         or an internal link (beginning with #!)
 *         or null to remove the link
 */
silex.model.Element.prototype.setLink = function(element, opt_link) {
  if (opt_link) {
    element.setAttribute(silex.model.Element.LINK_ATTR, opt_link);
  }
  else {
    element.removeAttribute(silex.model.Element.LINK_ATTR);
  }
};


/**
 * set/get a "silex style link" on an element
 * @param  {Element} element
 * @return {string}
 */
silex.model.Element.prototype.getLink = function(element) {
  return element.getAttribute(silex.model.Element.LINK_ATTR);
};


/**
 * get/set class name of the element of a container created by silex
 * remove all silex internal classes
 * @param  {Element} element   created by silex, either a text box, image, ...
 * @return  {?string}           the value for this styleName
 */
silex.model.Element.prototype.getClassName = function(element) {
  var pages = this.model.page.getPages();
  return goog.array.map(element.className.split(' '), function(name) {
    if (goog.array.contains(silex.utils.Style.SILEX_CLASS_NAMES, name) ||
        goog.array.contains(pages, name) ||
        this.model.property.getSilexId(element) === name) {
      return;
    }
    return name;
  }, this).join(' ').trim();
};


/**
 * get/set class name of the element of a container created by silex
 * remove all silex internal classes
 * @param  {Element} element   created by silex, either a text box, image, ...
 * @param  {string=} opt_className  the class names, or null to reset
 */
silex.model.Element.prototype.setClassName = function(element, opt_className) {
  // compute class names to keep, no matter what
  // i.e. the one which are in element.className + in Silex internal classes
  var pages = this.model.page.getPages();
  var classNamesToKeep = goog.array.map(element.className.split(' '), function(name) {
    if (goog.array.contains(silex.utils.Style.SILEX_CLASS_NAMES, name) ||
        goog.array.contains(pages, name) ||
        this.model.property.getSilexId(element) === name) {
      return name;
    }
  }, this);

  // reset element class name
  element.className = classNamesToKeep.join(' ');
  if (opt_className) {
    // apply classes from opt_className
    goog.array.forEach(opt_className.split(' '), function(name) {
      name = name.trim();
      if (name && name !== '') {
        goog.dom.classlist.add(element, name);
      }
    });
  }
};

/**
*	Add a label displaying a given element's tab index to that element
*	@param element the element to label
*	@param optVal optional tabindex value
*/
silex.model.Element.prototype.setTabOrderLabel = function(element, optVal)
{
	let val = optVal || element.getAttribute('data-ctat-tabindex');
	if (this.view.propertyTool.controller.viewMenuController.tabOrderVisible && val)
	{
		let div = goog.dom.getElementByClass('taborder-label', element);
		if (!div)
		{	
			div = document.createElement('div');
			div.classList.add('taborder-label');
			element.appendChild(div);
		}
		div.textContent = val;
	}
};

/**
*	Resize a CTATTable component's cells based on overall width and height
*	@param tableElement the DOM node of the CTATTable
*	@param dimension the direction to resize, either 'width', 'height', or 'both'
*/
silex.model.Element.prototype.resizeTable = function(tableElement, dimension)
{
	if (dimension === 'width')
		this.resizeTableWidth(tableElement);
	else if (dimension === 'height')
		this.resizeTableHeight(tableElement);
	else
	{
		this.resizeTableWidth(tableElement);
		this.resizeTableHeight(tableElement);
	}
};

/**
*	Set a CTATTable element's cell width based on the table's overall width
*	@param tableElement the DOM node of the CTATTable 
*/
silex.model.Element.prototype.resizeTableWidth = function(tableElement)
{
	var rows = $(tableElement).find('.CTATTable--row');
	if (rows)
	{
		var cells = $(rows[0]).find('.CTATTable--cell');
		if (cells)
		{
			let borderLeftWidth=$(cells[0]).css('border-left-width');
			let borderRightWidth=$(cells[0]).css('border-right-width');
			borderLeftWidth = parseInt(borderLeftWidth.substring(0, borderLeftWidth.length-2), 10);
			borderRightWidth = parseInt(borderRightWidth.substring(0, borderRightWidth.length-2), 10);
			let selector = '.'+this.model.property.getSilexId(tableElement)+' > .CTATTable--row > .CTATTable--cell';
			let numCols = cells.length;
			let width = ((($(tableElement).width()/numCols)-borderLeftWidth)-borderRightWidth);
			let styleValue = width+'px';
			let styleObject = this.model.property.getStyleObject(tableElement, false, selector);
			if (!styleObject) 
		    {
				styleObject = {};
		    }
		    if (styleObject['width'] !== styleValue) 
		    {
				if (goog.isDefAndNotNull(styleValue)) 
				{
					styleObject['width'] = this.prepareHtmlForEdit(styleValue);
			    }
			    else 
				{
					styleObject['width'] = '';
				}
				this.model.property.setStyle(selector, styleObject);
			}
		}
	}
};

/**
*	Set CTATTable component cell height based on num rows and overall table height
*	@param tableElement the DOM node of the CTATTable
*/
silex.model.Element.prototype.resizeTableHeight = function(tableElement)
{
	var rows = $(tableElement).find('.CTATTable--row');
	if (rows)
	{
		var cells = $(rows[0]).find('.CTATTable--cell');
		let rowSelector = '.'+this.model.property.getSilexId(tableElement)+' > .CTATTable--row';
		let cellSelector = '.'+this.model.property.getSilexId(tableElement)+' > .CTATTable--row > .CTATTable--cell';
		let numRows = rows.length;
		let tableHeight = $(tableElement).height();
		let borderTopWidth=$(cells[0]).css('border-top-width');
		let borderBtmWidth=$(cells[0]).css('border-bottom-width');
		borderTopWidth = parseInt(borderTopWidth.substring(0, borderTopWidth.length-2), 10);
		borderBtmWidth = parseInt(borderBtmWidth.substring(0, borderBtmWidth.length-2), 10);
		let height = (((tableHeight/numRows)-borderTopWidth)-borderBtmWidth);
		let styleValue = height+'px';
		let cellStyleObject = this.model.property.getStyleObject(tableElement, false, cellSelector) || {};
		if (cellStyleObject['height'] !== styleValue)
		{
			cellStyleObject['height'] = this.prepareHtmlForEdit(styleValue);
			this.model.property.setStyle(cellSelector, cellStyleObject);
		}
	}
};

/**
*	Set or unset scrolling behavior on a container element
*	@param element the container element
*	@param direction the direction of scroll to toggle, either 'x' or 'y'
*	@param doScroll whether or not the container should scroll (boolean)
*/
silex.model.Element.prototype.setScroll = function(element, direction, doScroll)
{
	let inner = $(element).find('.scrollcontainer-inner')[0];
	let classVal = (direction === 'x') ? 'silex-container-scrollx' : 'silex-container-scrolly';
	let otherClassVal = (direction === 'x') ? 'silex-container-scrolly' : 'silex-container-scrollx';
	if (doScroll)
	{
		if (!inner)
		{
			//create inner container
			inner = silexApp.model.element.createContainerElement();
			inner.classList.add('scrollcontainer-inner');
			inner.classList.add('container-element');
			let contents = [];
			for (let i = 0; i < element.childNodes.length; i++)
			{
				let childClass = element.childNodes[i].className;
				if (childClass && !childClass.includes('ui-resizable-handle'))
				{
					contents.push(element.childNodes[i]);
				}
			}
			element.appendChild(inner);
			for (let i = 0; i < contents.length; i++)
			{
				element.removeChild(contents[i]);
				inner.appendChild(contents[i]);
			}
		}
		inner.classList.add(classVal);
	}
	else 
	{
		if (inner)
		{
			if (!inner.className.includes(otherClassVal))
			{
				//remove inner container
				element.removeChild(inner);
				let contents = [];
				while(inner.hasChildNodes())
				{
					let child = inner.firstChild;
					inner.removeChild(child);
					element.appendChild(child);
				}
			}
			else
			{
				//set style
				inner.classList.remove(classVal);
			}
		}
	}
};
