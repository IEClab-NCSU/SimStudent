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
 * @fileoverview Helper class for common tasks
 *
 */


goog.provide('silex.utils.Dom');



/**
 * @constructor
 * @struct
 */
silex.utils.Dom = function() {
  throw ('this is a static class and it canot be instanciated');
};


/**
 * name of the get param used to store the timestamp (cache control)
 * @constant
 */
silex.utils.Dom.CACHE_CONTROL_PARAM_NAME = 'silex-cache-control';


/**
 * tag used by Silex, which are mandatory in all Silex webstes
 * @constant
 */
silex.utils.Dom.MANDATORY_TAGS = [
  {
    'type': 'script',
    'fileName': 'js/jquery.js'
  },
  {
    'type': 'script',
    'fileName': 'js/jquery-ui.js'
  },
  {
    'type': 'script',
    'fileName': 'js/silex/pageable.js'
  },
  {
    'type': 'script',
    'fileName': 'js/silex/front-end.js'
  },
  {
    'type': 'link',
    'fileName': 'css/silex/normalize.css'
  },
  {
    'type': 'link',
    'fileName': 'css/silex/front-end.css'
  }
].reverse();


/**
 * refresh an image with its latest version on the server
 * @param  {Element} img
 * @param  {function()} cbk
 */
silex.utils.Dom.refreshImage = function(img, cbk) {
  var initialUrl = img.src;
  img.onload = function(e) {
    // stop the process
    img.onload = null;
    cbk();
  };
  img.src = silex.utils.Dom.addCacheControl(initialUrl);
};


/**
 * add cache control to an URL
 * handle the cases where there is a ? or & or an existing cache control
 * example: silex.utils.Dom.addCacheControl('aaaaaaaa.com') returns 'aaaaaaaa.com?silex-cache-control=09238734099890'
 * @param {string} url
 * @return {string}
 */
silex.utils.Dom.addCacheControl = function(url) {
  // remove existing cache control if any
  url = silex.utils.Dom.removeCacheControl(url);
  // add an url separator
  if (url.indexOf('?') > 0) {
    url += '&';
  }
  else {
    url += '?';
  }
  // add the timestamp
  url += silex.utils.Dom.CACHE_CONTROL_PARAM_NAME + '=' + Date.now();
  // return the new url
  return url;
};


/**
 * remove cache control from an URL
 * handle the cases where there are other params in get
 * works fine when url contains several URLs with cache control or other text (like a full image tag with src='')
 * example: silex.utils.Dom.addCacheControl('aaaaaaaa.com?silex-cache-control=09238734099890') returns 'aaaaaaaa.com'
 * @param {string} url
 * @return {string}
 */
silex.utils.Dom.removeCacheControl = function(url) {
  // only when there is an existing cache control
  if (url.indexOf(silex.utils.Dom.CACHE_CONTROL_PARAM_NAME) > 0) {
    var re = new RegExp('([\?|&|&amp;]' + silex.utils.Dom.CACHE_CONTROL_PARAM_NAME + '=[0-9]*[&*]?)', 'gi');
    url = url.replace(re, function(match, group1, group2) {
      // if there is a ? or & then return ?
      // aaaaaaaa.com?silex-cache-control=09238734&ccsqcqsc&
      // aaaaaaaa.com?silex-cache-control=09238734099890
      // aaaaaaaa.com?silex-cache-control=09238734&ccsqcqsc&
      // aaaaaaaa.com?xxx&silex-cache-control=09238734&ccsqcqsc&
      if (group1.charAt(0) === '?' && group1.charAt(group1.length - 1) === '&') {
        return '?';
      }
      else if (group1.charAt(group1.length - 1) === '&' || group1.charAt(0) === '&') {
        return '&';
      }
      else {
        return '';
      }
    });
  }
  // return the new url
  return url;
};


/**
 * render a template by duplicating the itemTemplateString and inserting the data in it
 * @param {string} itemTemplateString   the template containing \{\{markers\}\}
 * @param {Array.<string>}  data                 the array of strings conaining the data
 * @return {string} the template string with the data in it
 */
silex.utils.Dom.renderList = function(itemTemplateString, data) {
  var res = '';
  // for each item in data, e.g. each page in the list
  for (let itemIdx in data) {
    // build an item
    var item = itemTemplateString;
    // replace each key by its value
    for (let key in data[itemIdx]) {
      var value = data[itemIdx][key];
      item = item.replace(new RegExp('{{' + key + '}}', 'g'), value);
    }
    // add the item to the rendered template
    res += item;
  }
  return res;
};


/**
 * prepare a file to be published
 * get all the files included in the website, and put them into assets/ or js/ or css/
 * the HTML file must be saved somewhere because all URLs are made relative
 * This method uses a temporary iframe to manage the temporary dom
 * @param {string} publicationUrl    the url where to publish to, e.g. /api/1.0/dropbox/exec/put/.../...
 * @param {string} fileUrl    the url of the file being published
 * @param {string} html    the html data of the website
 * @param {function({success: boolean})} statusCallback callback to be notified when operation is done, with the json response
 * @param {function(string)=} opt_errCbk    callback to be notified of server side errors
 */
silex.utils.Dom.getCleanFile = function(html, saveCallback, cleanupInline) {
  // create the iframe used to compute temporary dom
  var iframe = goog.dom.iframe.createBlank(goog.dom.getDomHelper(), 'position: absolute; left: -99999px; ');
  goog.dom.appendChild(document.body, iframe);
  // wait untill iframe is ready
  goog.events.listenOnce(iframe, 'load', function(e) {
    // clean up the DOM
    var contentDocument = goog.dom.getFrameContentDocument(iframe);
    var cleanedObj = silex.utils.DomCleaner.cleanup(contentDocument, cleanupInline);
    // store the files to download and copy to assets, scripts...
    var htmlString = cleanedObj['htmlString'];
    var cssString = cleanedObj['cssString'];
    var jsString = cleanedObj['jsString'];
    var files = cleanedObj['files'];
    
	saveCallback(cleanedObj);
  }, false);
  // prevent scripts from executing
  html = html.replace(/type ?= ?(['"])text\/javascript\1/gi, 'type="text/notjavascript"');
  html = html.replace(/onload ?= ?"[^"]*"/gi, '');
  // write the content (leave this after "listen")
  goog.dom.iframe.writeContent(iframe, html);
};


/**
 * add the mandatory Silex scripts and styles in <HEAD>
 * @param {Document} doc
 */
silex.utils.Dom.addMandatoryTags = function(doc) {
  silex.utils.Dom.MANDATORY_TAGS.forEach((tagObj) => {
	let query = tagObj['type']+'['+(tagObj['type'] === 'script' ? 'src=' : 'href=') + '"' + tagObj['fileName'] + '"]';
	let element = doc.head.querySelector(query);
	if(!element) {
	  console.log('couldn\'t find '+tagObj['fileName']);
	  element = doc.createElement(tagObj['type']);
	  if(tagObj['type'] === 'script') {
		element.setAttribute('type', 'text/javascript');
		element.setAttribute('src', tagObj['fileName']);
	  }
	  else {
		element.setAttribute('rel', 'stylesheet');
		element.setAttribute('href', tagObj['fileName']);
	  }
	  doc.head.insertBefore(element, doc.head.firstChild);
	}
  });
};

silex.utils.Dom.getSilexType = function(element)
{
	let silexId = element.getAttribute('data-silex-id');
	if (silexId === 'background-initial')
		type = 'Background';
	else if (silexId === 'body-initial')
		type = 'Body';
	else
	{
		let silexType = element.getAttribute('data-silex-type');
		switch (silexType)
		{
			case 'text':
				type = 'Text Box';
			break;
			case 'container':
				if (element.className.includes('multchoice-element'))
					type = 'Multiple Choice';
				else
					type = 'Container';
			break;
			case 'image':
				type = 'Image';				
			break;
			default:
				type = '';
		}
	}
	return type;
}
