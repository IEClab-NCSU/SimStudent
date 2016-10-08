goog.provide('SupplementaryLog');

goog.require('OLILog');

var SupplementaryLog = function SupplementaryLog(action, infoType, info) {
	OLILog.call(this, action, info, infoType);


	this.toXML = function toXML() {
		var Node = this.xmlCreator.Node;
		var TextNode = this.xmlCreator.TextNode;

		var xml = Node("log_supplement", {"source_id":OLILog.source,
										  "action_id":action,
										  "info_type":infoType},
										  TextNode(info));
		return xml;
	}
}

SupplementaryLog.prototype = Object.create(OLILog.prototype);
SupplementaryLog.prototype.constructor = SupplementaryLog;
