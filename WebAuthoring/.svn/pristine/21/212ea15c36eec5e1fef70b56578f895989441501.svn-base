goog.provide('OLILog');

goog.require('XMLCreator');
goog.require('TimeZone');

var OLILog = function OLILog(action, infoType, info) {
	this.xmlCreator = new XMLCreator();
	this.xmlCreator.wrapInCData = true
	this.source = OLILog.source;
	this.action = action;
	this.infoType = infoType;
	this.info = info;

	function addZero(num) {
		if (num < 10 && num > 0) {
			return '0' + num;
		}
		return num;
	}


	this.getTimeStamp = function getTimeStamp() {
		var d = new Date();
		var year = d.getFullYear();
		var month = addZero(d.getMonth() + 1);
		var day = addZero(d.getDate());
		var date = year + "/" + month + "/" + day;

		var timeString = d.toTimeString();
		var time = timeString.substring(0,timeString.indexOf(" "));

		return date + " " + time;
	};

	this.getTimeZone = function getTimeZone() {
		var tz = jstz.determine();
		return tz.name();
	};

	this.toXMLString = function toXMLString() {
		return this.xmlCreator.toXMLString(this.toXML())
	}
}

// constants
OLILog.source = "ASSESSMENT_ACTIVITY";
