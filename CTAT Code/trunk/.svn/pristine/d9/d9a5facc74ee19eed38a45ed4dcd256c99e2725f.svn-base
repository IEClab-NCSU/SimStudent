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
 * @fileoverview Service used to interact with the unifile server.
 *     This class is a singleton.
 *
 */


goog.provide('silex.service.CloudStorage');

goog.require('goog.net.XhrIo');



/**
 * the Silex CloudStorage singleton
 * @constructor
 * based on http://www.inkfilepicker.com/
 * load and save data to and from the cloud storage services
 */
silex.service.CloudStorage = function() {
  this.filePicker = ce.api.CloudExplorer.get('silex-file-explorer');
};
goog.addSingletonGetter(silex.service.CloudStorage);


/**
 * reference to the filepicker instance
 * @type {Object}
 */
silex.service.CloudStorage.prototype.filePicker = null;


/**
 * create a blob out of an url
 * @param {string} url
 * @return {{url:string}}
 */
silex.service.CloudStorage.prototype.createBlob = function(url) {
  // cloud explorer expects relative path
  if (silex.utils.Url.isAbsoluteUrl(url)) {
    console.error('cloud explorer expects relative path');
    throw new Error('cloud explorer expects relative path');
  }
  // create the blob
  var relBlob = {
    'url': url
  };
  return relBlob;
};

/**
 * load data
 * @param  {string} url
 * @param  {function(string)} cbk
 * @param  {function(Object)} opt_errCbk
 */
silex.service.CloudStorage.prototype.loadLocal = function(url, cbk, opt_errCbk) {
  goog.net.XhrIo.send(url, function(e) {
    // success of the request
    var xhr = e.target;
    var rawHtml = xhr.getResponse();
    if (xhr.isSuccess()) {
      if (cbk) {
        cbk(rawHtml);
      }
    }
    else {
      var message = xhr.getLastError();
      console.error(message, xhr, xhr.isSuccess(), xhr.getStatus(), xhr.headers.toString());
      if (opt_errCbk) {
        opt_errCbk(message);
      }
    }
  });
};
