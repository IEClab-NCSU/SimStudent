goog.provide('XMLCreator');

var XMLCreator = function XMLCreator() {
	this.wrapInCData = false;

	var doc = document.implementation.createDocument("", "", null);
	var pointer = this;

	/* Example of how to construct an xml document with this api, see
	 * ActionLog.js and SupplementaryLog.js for practical examples:

	   Node("log_message", {"session_id": 111, "time": 2015/03/01},
	   		Node("action",
				TextNode("student_input")
				)
			Node("input",
				TextNode("5")
				)
			)

	   Corresponds to the xml document:

	   <log_message session_id="111" time="2015/03/01">
	   		<action>student_input</action>
			<input>5</input>
	   </log_message>
	  */


	this.Node = function Node(nodeName, attributes) {
		var node = doc.createElement(nodeName);
		addAttributes(node, attributes);

		var args = arguments;
		for (var i = 2; i < args.length; i++) {
			var child = args[i];
			if (child instanceof Array) {
				for (var j = 0; j < child.length; j++) {
					node.appendChild(child[j]);
				}
			} else {
				node.appendChild(child);
			}
		}
		return node;
	};

	this.TextNode = function TextNode(text) {
		if (text == "") {
			return doc.createTextNode(text);
		}
		if (pointer.wrapInCData) {
			return doc.createCDATASection(text);
		}
		return doc.createTextNode(text);
	};


	function addAttributes(node, attributes) {
		for (var name in attributes) {
			addAttribute(node, name, attributes[name]);
		}
	}

	function addAttribute(node,attribute,value) {
		node.setAttribute(attribute,value);
	}

	this.toXMLString = function (xml) {
		var serializer = new XMLSerializer();
		return serializer.serializeToString(xml);
	}

	function CData(text) {
		return "<![CDATA[" + text + "]]>";
	}
}
