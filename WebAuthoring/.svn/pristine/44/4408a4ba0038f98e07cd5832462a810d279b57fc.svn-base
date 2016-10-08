goog.provide("OLIMessageHandler")

goog.require("CTATGlobals");

OLIMessageHandler = function() {

	var handler = null;
	var messageParser = new CTATXML();
	var vars = flashVars.getRawFlashVars();

	this.assignHandler = function(aHandler) {
		handler = aHandler;
	};

	function escapeXml(unsafe) {
		return unsafe.replace(/[<>&'"]/g, function (c) {
			switch (c) {
				case '<': return '&lt;';
				case '>': return '&gt;';
				case '&': return '&amp;';
				case '\'': return '&apos;';
				case '"': return '&quot;';
			}
		});
	}


	this.processMessage = function (message) {
		console.log("Recieved OLI Message:");
		console.log(message);
		var docRoot = messageParser.parse(message);
		if (docRoot == null) {
			this.ctatdebug("Error parsing oli message");
			return;
		}

		var name = messageParser.getElementName(docRoot);

		if (name == "super_activity_client") {
			this.parseClientConfig(docRoot);
		} else if (name == "super_activity_session") {
			this.parseBeginSession(docRoot);
		} else if (name == "watson_activity") {
			this.parseResourceFile(docRoot);
		} else if (name == "stateGraph") {
			handler.oliRecievedBrd(escapeXml(message));
		} else {
			console.log("unknwon xml recieved with root " + name);
		}
	}

	// these parsing methods store global info to flashvars, otherwise
	// send info to the handler (currently CTATCommShell)

	/** Example message
		<?xml version="1.0" encoding="UTF-8"?>
		<super_activity_client server_time_zone="America/New_York">
			<resource_type id="x-cmu-idea-watson" name="Watson" />
			<base href="https://dev-02.oli.cmu.edu/superactivity/watson/" />
			<authentication user_guid="axiao" />
			<logging session_id="7c0737b80aaa1c8c02c1474c85a26d1a" source_id="WatsonTutor">
				<url>https://oli.cmu.edu/jcourse/dashboard/log</url>
			</logging>
		</super_activity_client>
	**/
	this.parseClientConfig = function(root) {
		var children = messageParser.getElementChildren(root);

		var logUrl = null;
		for (var i = 0; i < children.length; i++) {
			var element = children[i];
			var name = messageParser.getElementName(element);
			if (name == "resource_type") {
				// do nothing for now
			} else if (name == "base") {
				// do nothing for now
			} else if (name == "authentication") {
				vars["user_guid"] = messageParser.getElementAttr(element,"user_guid");
			} else if (name == "logging") {
				vars["session_id"] = messageParser.getElementAttr(element,"session_id");
				vars["source_id"] = messageParser.getElementAttr(element,"source_id");

				var logChildren = messageParser.getElementChildren(element);
				logUrl = messageParser.getNodeTextValue(logChildren[0])
			}
		}

		handler.oliClientConfig(logUrl);
	}

	this.parseBeginSession = function(root) {
		console.log("parsing begin session");

		var file = root.getElementsByTagName("file")[0];

		if (file == undefined) {
			console.log("can't find resource file");
			return;
		}

		var vars = flashVars.getRawFlashVars();
		var webContent = root.getElementsByTagName("web_content")[0];
		vars["web_content_url"] = messageParser.getElementAttr(webContent,"href");

		var resourceFileId = messageParser.getElementAttr(file,"guid");
		handler.oliBeganSession(resourceFileId);
	}

	this.parseResourceFile = function(root) {
		console.log("parsing resource file!");
		var children = messageParser.getElementChildren(root);
		var brdUrls = [];

		for (var i = 0; i < children.length; i++) {
			var element = children[i];
			var name = messageParser.getElementName(element);
			if (name == "interface") {
				var url = messageParser.getNodeTextValue(element);
				var fullUrl = vars["web_content_url"] + url.substring(3);
				brdUrls.push(fullUrl);
			} else if (name == "title") {
				vars["problem_name"] = messageParser.getNodeTextValue(element);
			} /*added by nbarba to support new activity*/
			else if (name== "tutor_arguments"){
				vars["Argument"]=messageParser.getNodeTextValue(element);
			}
		}
		handler.oliLoadedContentFile(brdUrls);
	}
}
