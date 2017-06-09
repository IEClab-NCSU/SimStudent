// Stubs for goog.XX functions

if (typeof(goog) === 'undefined') {
	goog = function() {}
};

if (!goog.provide) {
	goog.provide = function(className) {
		if (console && console.log) {
			console.log("stub called: goog.provide(" + className + ")");
		}
	}
};

if (!goog.require) {
	goog.require = function(className) {
		if (console && console.log) {
			console.log("stub called: goog.require(" + className + ")");
		}
	}
};
