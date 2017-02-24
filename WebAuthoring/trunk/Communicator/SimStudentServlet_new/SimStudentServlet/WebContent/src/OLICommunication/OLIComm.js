goog.provide("OLIComm");

goog.require("CTATGlobals");
goog.require("CTATCommLibrary");


OLIComm = function(url) {

	this.url = url;

	function makeVar(name, value)
	{
		return {"name":name, "value":value};
	}


	var pointer = this;
	function sendVariables(variables)
	{
		commLibrary.setHandler(oliMessageHandler);
		commLibrary.sendURLVariables(pointer.url,variables);
	}

	this.loadClientConfig = function() {
		var vars = flashVars.getRawFlashVars();
		var variables = [makeVar("commandName", "loadClientConfig"),
						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"])];
		sendVariables(variables);
	}

	this.beginSession = function() {
		var vars = flashVars.getRawFlashVars();

		var variables = [makeVar("commandName", "beginSession"),
						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("activityGuid", "undefined"),
						 makeVar("userGuid", vars["user_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"])];
		sendVariables(variables);
	};

	this.loadContentFile = function(contentFileGuid) {
		var vars = flashVars.getRawFlashVars();

		var variables = [makeVar("commandName", "loadContentFile"),
						 makeVar("contentFileGuid", contentFileGuid),
  						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("activityGuid", "undefined"),
						 makeVar("userGuid", vars["user_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"])];

		sendVariables(variables);
	};

	this.loadFileRecord = function(fileName, attemptNumber) {
		var vars = flashVars.getRawFlashVars();

		var variables = [makeVar("commandName", "loadFileRecord"),
  						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("activityGuid", "undefined"),
						 makeVar("userGuid", vars["user_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"]),
						 makeVar("attemptNumber", attemptNumber),
						 makeVar("fileName", fileName)];
		sendVariables(variables);
	};

	this.writeFileRecord = function(filename, attemptNumber, mimeType, byteEncoding, fileRecordData) {
		var vars = flashVars.getRawFlashVars();

		var variables = [makeVar("commandName", "loadContentFile"),
  						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("activityGuid", "undefined"),
						 makeVar("userGuid", vars["user_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"]),
						 makeVar("attemptNumber", attemptNumber),
						 makeVar("fileName", fileName),
						 makeVar("mimeType", mimeType),
						 makeVar("byteEncoding", byteEncoding),
						 makeVar("fileRecordData", fileRecordData)];

		sendVariables(variables);
	};

	this.scoreAttempt = function(scoreId, scoreValue) {
		var vars = flashVars.getRawFlashVars();

		var variables = [makeVar("commandName", "loadContentFile"),
  						 makeVar("activityContextGuid", vars["activity_context_guid"]),
						 makeVar("activityGuid", "undefined"),
						 makeVar("userGuid", vars["user_guid"]),
						 makeVar("authenticationToken", vars["oli_auth_token"]),
						 makeVar("resourceTypeID", vars["resource_type_id"]),
						 makeVar("activityMode", vars["activity_mode"]),
						 makeVar("scoreId", scoreId),
						 makeVar("scoreValue", scoreValue)];

		sendVariables(variables);
	};

}
