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
 * @fileoverview Helper class used to cleanup the DOM when publishing a website
 *
 */


goog.provide('silex.utils.DomCleaner');
goog.require('silex.utils.Url');



/**
 * @constructor
 * @struct
 */
silex.utils.DomCleaner = function() {
  throw ('this is a static class and it canot be instanciated');
};


/**
 * List of URLs from which we are allowed to download the content locally
 * during the process of publishing the file
 * This is made to prevent trying to download locally fonts from google fonts
 * or scripts from an embed code
 */
silex.utils.DomCleaner.DOWNLOAD_LOCALLY_FROM = [
  '//editor.silex.me',
  silex.utils.Url.getRootUrl()
];


/**
 * remove the javascript and css files which firefox inlines
 * the inlined tags are script type="text/javascript" style="display:none"
 * @param {Document} doc
 */
silex.utils.DomCleaner.cleanupFirefoxInlines = function(doc) {
  // remove inlined scripts
  let elements = doc.querySelectorAll('script[style="display:none"]');
  for (let idx in elements) {
    goog.dom.removeNode(elements[idx]);
  }
  elements = doc.querySelectorAll('style[style="display:none"]');
  for (let idx in elements) {
    goog.dom.removeNode(elements[idx]);
  }
  silex.utils.Dom.addMandatoryTags(doc);
};


/**
 * remove dynamically generated CTAT content from a given node
 * @param node the DOM node to be cleaned up
**/
silex.utils.DomCleaner.cleanupCTAT = function(node)
{
	var genComponents = goog.dom.getElementsByClass('ctat-gen-component', node);
	var numComponents = genComponents.length;
	var parentNode;
	for (var i = 0; i < numComponents; i++)
	{
		parentNode = genComponents[i].parentNode;
		parentNode.removeChild(genComponents[i]);
	}
}


/**
 * cleanup html page
 * remove Silex specific data from HTML
 * create an external CSS file
 * generates a list of js scripts and assets to be eported with the file
 * @param {Document} contentDocument
 * @param {string} baseUrl
 * @return {{htmlString: string, cssString: string, jsString: string, files: Array.<Object>}} an object with
 *      html: the cleaned up raw HTML {string} or null if an error occured
 *      css: list of css files
 *      jsString: a script included in the html
 *      files: list of assets files
 */
silex.utils.DomCleaner.cleanup = function(contentDocument, removeInline) {
  var headElement = contentDocument.head;
  var bodyElement = contentDocument.body;
  let proto = window.location.protocol;
  let domain = window.location.href.split('/');
  if (domain[domain.length-1].includes('.'))
	  domain.pop();
  domain.shift();
  domain = domain.join('/');
  var baseUrl = 'https:/'+domain+'/';
  // remove publication path
  var metaNode = contentDocument.querySelector('meta[name="publicationPath"]');
  if (metaNode) {
    goog.dom.removeNode(metaNode);
  }
  var parentIdNode = contentDocument.querySelector('meta[name="parent-data"]');
  !parentIdNode || goog.dom.removeNode(parentIdNode);
  //--- Remove inner scroll containers ---//
  silex.utils.DomCleaner.flattenContainers(bodyElement);
  //---Convert all static links from relative to absolute---//
  var relToAbsolute = function(tagName)
  {
	var nodes = headElement.getElementsByTagName(tagName);
	var src;
	if (tagName === 'script') src = 'src';
	else src = 'href';
	for (var i = 0; i < nodes.length; i++)
	{
		if (nodes[i].getAttribute('data-silex-static') === 'true')
		{
			let relSrc = nodes[i].getAttribute(src);
			if (relSrc.includes('ctat_editor.min.js'))
			{
				relSrc = relSrc.replace('ctat_editor.min.js', 'ctat.min.js');
				nodes[i].setAttribute(src, relSrc);
			}
			if (!relSrc.includes('https'))
			{
				nodes[i].setAttribute(src, baseUrl + relSrc); 
			}
			if (nodes[i].getAttribute('type'))
				nodes[i].setAttribute('type', 'text/javascript');
		}
		else if (!(nodes[i].className.includes('user-stylesheet')
				 || nodes[i].className.includes('user-script'))) 
		{
			headElement.removeChild(nodes[i]);
			i--;
		}
	}
  };
  relToAbsolute('script');
  relToAbsolute('link');
  //------------------------------------------------//
  
  //-----Remove asset-map meta tag------//
  let assetMapTag = contentDocument.querySelector('meta[name="asset-map"]');
  assetMapTag && headElement.removeChild(assetMapTag);
  
  //-----Remove temp styles tag---------//
  let tempStyles = contentDocument.querySelector('.silex-temp-styles');
  tempStyles && headElement.removeChild(tempStyles);
  
  //---		JS assets		---//
  // final js script to store in js/script.js
  var jsString = '';
  var scriptTag = goog.dom.getElementByClass(
      silex.model.Head.SILEX_SCRIPT_ELEMENT_CSS_CLASS,
      headElement);
  if (scriptTag) {
    jsString = scriptTag.innerHTML;
    goog.dom.removeNode(scriptTag);
  }
  else {
    console.warn('no silex script found');
  }
  //---------------------------//

  var bodyStr = bodyElement.innerHTML;
  
  //---		CSS and image assets		---//
  if (removeInline)
  {
	  console.log('removing inline styles...');
	  //-- get paths to asset directories --//
	  //css/js
	  var cssFolderPath = 'Assets/';
	  var jsFolderPath = 'Assets/';
	  //images
	  var imgFolderPath = 'Assets/';
	  
	  //----EDITOR GENERATED CSS-----//
	  // final css to store in css/<interface-id>-styles.css
	  var cssStr = '';
	  
	  //ctat component styles
	  let styleNode = headElement.querySelector('style[id="ctat-styles"]');
	  if (styleNode)
	  {
		cssStr += styleNode.innerHTML;
		headElement.removeChild(styleNode);
	  }
	  
	  //silex styles
	  styleNode = goog.dom.getElementByClass('silex-inline-styles', headElement);
	  if (styleNode)
	  {
		cssStr += ('\n' + styleNode.innerHTML);
		headElement.removeChild(styleNode);
	  }
	  
	  styleNode = document.createElement('link');
	  styleNode.setAttribute('rel', 'stylesheet');
	  let interfaceName = headElement.querySelector('meta[name="interface-id"]').getAttribute('content');
	  let len = interfaceName.indexOf('.html');
	  interfaceName = (len > -1) ? interfaceName.substr(0, len) : interfaceName;
	  styleNode.setAttribute('href', cssFolderPath + interfaceName + '-styles.css');
	  headElement.appendChild(styleNode);
	  
	  var files = {};
	  //-----USER STYLESHEETS / SCRIPTS-----------//
	  var convertAssets = function(assetNodes, assetExt, tagName)
	  {
		  for (let i = 0; i < assetNodes.length; i++)
		  {
			  let name = assetNodes[i].getAttribute('id');
			  let fileId = assetNodes[i].getAttribute('data-file-id');
			  if (name.indexOf(assetExt) < 0) 
				  name += assetExt;
			  if (tagName === 'style')
			  {
				  //convert inline <style> tags to <link> tags
				  let sheetLink = document.createElement('link');
				  sheetLink.setAttribute('rel', 'stylesheet');
				  sheetLink.setAttribute('href', cssFolderPath+name);
				  headElement.appendChild(sheetLink);
				  headElement.removeChild(assetNodes[i]);
			  }
			  else if (tagName === 'script')
			  {
				  assetNodes[i].setAttribute('src', jsFolderPath+name);
				  assetNodes[i].innerHTML = '';
			  }
			  if (!files[fileId])
			  {
				files[fileId] = name;
			  }
		  }
	  }
	  let userSheets = goog.dom.getElementsByClass('user-stylesheet', headElement);
	  let userScripts = goog.dom.getElementsByClass('user-script', headElement);
	  convertAssets(userSheets, '.css', 'style');
	  convertAssets(userScripts, '.js', 'script');
	  
	  //convert img urls from google drive-based to local
	  let imgUrlMap = window.silexApp.model.file.imgUrlMap;
	  for (let url in imgUrlMap)
	  {
		  if (imgUrlMap.hasOwnProperty(url))
		  {
			  let fileData = imgUrlMap[url];
			  let toFind = url.replace('&', '((&amp;)|&)');
			  toFind = toFind.replace('?', '\\?');
			  toFind = new RegExp(toFind);
			  cssStr = cssStr.replace(toFind, fileData.name);
			  bodyStr = bodyStr.replace(toFind, imgFolderPath+fileData.name);
			  if (!files[fileData.id])
			  {
				files[fileData.id] = fileData.name;
			  }
		  }
	  }
  }
  //---------------------------------------//

  
  // cleanup classes used by Silex during edition
  silex.utils.Style.removeInternalClasses(bodyElement, false, true);

  // keep the body css classes
  var bodyClass = bodyElement.getAttribute('class');
	
  // convert to strings
  var headStr = headElement.innerHTML;
  headStr = headStr.replace(/type ?= ?(['"])text\/notjavascript\1/gi, 'type="text/javascript"');

  // final html page
  var html = '';
  html += '<!DOCTYPE html><html>';
  html += '<head>';
  html += '    ' + headStr + '';
  html += '</head>';
  html += '<body class="' + bodyClass + ' silex-published">' + bodyStr + '</body>';
  html += '</html>';

  return {
    'htmlString': html,
    'cssString': cssStr,
    'jsString': jsString,
    'files': files
  };
};

silex.utils.DomCleaner.flattenContainers = function(docBody)
{
	let containers = docBody.querySelectorAll('.scrollcontainer-inner');
	var parent, child;
	for (let i = 0; i < containers.length; i++)
	{
		child = containers[i];
		parent = child.parentNode;
		parent.removeChild(child);
		while(child.hasChildNodes())
		{
			let content = child.firstChild;
			child.removeChild(content);
			parent.appendChild(content);
		}
		child.className.includes('silex-container-scrollx') && parent.classList.add('silex-container-scrollx');
		child.className.includes('silex-container-scrolly') && parent.classList.add('silex-container-scrolly');
	}
}
/**
 * takes a matching pattern "url(...)"" and convert the absolute URLs to relative once,
 * take into account that these will be referenced in css/style.css,
 * so they must be relative to "css/"
 * FIXME: also changes the input param files, and this is dirty
 * @param {string} baseUrl
 * @param {Array.<{url:string, destPath:string, srcPath:string}>} files
 * @param {string} match
 * @param {string} group1
 * @param {string} group2
 * @return {string}
 */
silex.utils.DomCleaner.filterBgImage = function(baseUrl, files, match, group1, group2) {
  // get the url
  var url = silex.utils.Url.removeUrlKeyword(group2);
  // only if we are supposed to download this url locally
  var absolute = silex.utils.Url.getAbsolutePath(url, baseUrl);
  var relative = silex.utils.Url.getRelativePath(absolute, silex.utils.Url.getBaseUrl());
  // replace the '../' by '/', e.g. ../api/v1.0/www/exec/get/silex.png becomes /api/v1.0/www/exec/get/silex.png
  if (!silex.utils.Url.isAbsoluteUrl(relative)) {
    relative = '/' + relative;
  }
  if (silex.utils.DomCleaner.isDownloadable(absolute)) {
    var fileName = absolute.substr(absolute.lastIndexOf('/') + 1);
    var newRelativePath = 'assets/' + fileName;
    files.push({
      'url': absolute,
      'destPath': newRelativePath,
      'srcPath': relative
    });
    return silex.utils.Url.addUrlKeyword('../' + newRelativePath);
  }
  return match;
};


/**
 * det if a given URL is supposed to be downloaded locally
 * @param {string} url
 * @return {boolean} true if the url is relative or it is a known domain (sttic.silex.me)
 */
silex.utils.DomCleaner.isDownloadable = function(url) {
  // do not download files with ? or & since it is probably dynamic
  if (url.indexOf('?') >= 0 || url.indexOf('&') >= 0) {
    return false;
  }
  // download relative paths
  if (!silex.utils.Url.isAbsoluteUrl(url)) {
    return true;
  }
  // make protocol agnostic
  let agnosticUrl = url.substr(url.indexOf('//'));
  // download files from a known domain (sttic.silex.me)
  var found = false;
  goog.array.forEach(silex.utils.DomCleaner.DOWNLOAD_LOCALLY_FROM, function(baseUrl) {
    // make protocol agnostic
    baseUrl = baseUrl.substr(baseUrl.indexOf('//'));
    // check if we can download it
    if (agnosticUrl.indexOf(baseUrl) >= 0) {
      // url starts by the base url, so it is downloadable
      found = true;
    }
  });
  return found;
};
