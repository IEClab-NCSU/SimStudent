goog.provide('WatsonLogger');

goog.require('OLILogger');
goog.require('CTATCommLibrary');
goog.require('CTATGlobals');

var WatsonLogger = function(url) {

	var loadedLogger = false;

	this.loadOLILogger = function(authToken, sessionId, resourceId) {
		if (loadedLogger) {
			return;
		}
		
		
		OLILogger.call(this, authToken, sessionId, resourceId);
		loadedLogger = true;
	};

	this.postLog = function(xmlString) {
		if (!loadedLogger) {
			return;
		}
		
		//		commLibrary.setHandler(oliMessageHandler);
		//console.log("OLI logging: " + xmlString);
		//commLibrary.sendXMLURL(xmlString, url);

	};

	function formatXML(xmlString) {
		// this.prolog should be defined within each logger
		return this.prolog + "\n" + xmlString;
	}

};
