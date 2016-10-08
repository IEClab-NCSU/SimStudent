goog.provide('ActionLog');

goog.require('OLILog');

var ActionLog = function ActionLog(action, infoType, info, user_guid) {
	OLILog.call(this, action, infoType, info);

	this.sessionId = "sessionId";
	this.userGuid = "userGuid";
	this.supplements = [];

	this.addSupplement = function(supplement) {
		this.supplements.push(supplement);
	};

	this.removeSupplement = function(supplement) {
		var i = this.supplements.indexOf(supplement);
		if (i != -1) {
			this.supplements.splice(i,1);
		}
	};

	this.popSupplement = function() {
		return this.supplments.pop();
	};

	this.addSupplements = function(supplements) {
		for (var i = 0; i < supplements.length; i++) {
			this.addSupplement(supplements[i]);
		}
	};


	this.toXML = function() {
		var Node = this.xmlCreator.Node;
		var TextNode = this.xmlCreator.TextNode;

		var xml = Node("log_session", {"session_id": this.sessionId,
									   "user_guid": this.userGuid},
									   Node("log_action", {"source_id":OLILog.source,
		 												   "date_time":this.getTimeStamp(),
		 												   "timezone":this.getTimeZone(),
		 												   "action_id":action,
		 												   "container":"null",
		 												   "info_type":infoType},
		 												   this.supplements.map(function(s) {
		 												     return s.toXML();
		 												   }),
		 												   TextNode(info)
														   )
									   );
		return xml;
	};
};

ActionLog.prototype = Object.create(OLILog.prototype);
ActionLog.prototype.constructor = ActionLog;
