goog.provide("CTATLogger");

CTATLogger = function() {
	
	var flags = {};
	
	this.isSet = function(flag) {
		return !!flags[flag];
	};
	
	this.set = function(flag) {
		flags[flag] = true;
	};
	
	this.unset = function(flag) {
		flags[flag] = false;
	};
	
	this.log = function(flag, msg) {
		if (flags[flag]) {
			console.log(msg);
		}
	}
}
