/*
*	TutoringServiceLoadTest
*	Simple class for testing the TutoringService
* 	Created 2007
*	Updated: July 2007
*	Author:  Michael Weber
*/


// import a library for generating unique IDs.
//import CommShell.Util;
import edu.cmu.hcii.ctat.CTATUtil;
import mx.events.EventDispatcher;
import it.sephiroth.XML2Object;

class TutoringServiceLoadTest extends MovieClip {
	// dispatching events
	
	private var myXML:XML;
	/**
	* Declare this as a class member.  This lets the class know that the methods exist.
	* See http://www.person13.com/articles/components/creatingcompontents.html
	*/
	private var dispatchEvent:Function;
	
	/**
	* Declare this as a class member.  This lets the class know that the methods exist.
	* See http://www.person13.com/articles/components/creatingcompontents.html
	*/
	public var addEventListener:Function;

	/**
	* Declare this as a class member.  This lets the class know that the methods exist.
	* See http://www.person13.com/articles/components/creatingcompontents.html
	*/
	public var removeEventListener:Function;
	
	private var dumpInterval:Number;
	private var messageQueue:Array;
	private var msgID:Number;
	private var gotStartStateEnd:Boolean;
	private var gotMessageQueue:Boolean;
	private var theTSExchange:String;
	private var finalXMLSocket:XMLSocket;
	private var hasSentTSExchange:Boolean;
	private var _logFileName:String;
	private var _testLogTag:String;

	function callMe(evt_obj:Object) {
		this.gotStartStateEnd = true;
		trace(targetPath(this));
		checkForStart(evt_obj);	
	}
	
	function sendNextMessage(evt_obj:Object, correctAction:Boolean) {
		trace("sendNextMessage : MsgID = " + this.msgID + " Previous Result = " + correctAction);
		
		if (this.msgID < this.messageQueue.length)
		{
			//trace("sendNextMessage : sending message : " + this.messageQueue[this.msgID]);
			theTSExchange = theTSExchange + this.messageQueue[this.msgID] + "\n";
			_global.theShell.myXMLSocket.send(this.messageQueue[this.msgID]);			
		}
		else 
		{ 
 			finalXMLSocket = new XMLSocket();
			finalXMLSocket.onConnect = com.dynamicflash.utils.DelegateSource.Delegate.create(this, sendTSExchange);
			trace("connectToTSLTListener: connection status = " + finalXMLSocket.connect("localhost", 1515));
			trace(theTSExchange);
		}
		this.msgID++;
	}
	
	function forcedSendTSExchange() {
 			finalXMLSocket = new XMLSocket();
			finalXMLSocket.onConnect = com.dynamicflash.utils.DelegateSource.Delegate.create(this, sendTSExchange);
			trace("connectToTSLTListener: connection status = " + finalXMLSocket.connect("localhost", 1515));
			trace(theTSExchange);		
	}
	
	function sendTSExchange() {
		clearInterval(this.dumpInterval);
		trace("sendTSExchange: logFileName=" + _root.logFileName + " hasSentTSExchange=" + this.hasSentTSExchange);
		finalXMLSocket.send(_root.logFileName + "#" + this.theTSExchange);
		finalXMLSocket.close();
		//this.hasSentTSExchange = true;
	}
	
	function findProperty(root:XMLNode, prop:String):String {
		var returnString = "";
		var cNode:XMLNode = root.firstChild;
		for (var i = 0; i < root.childNodes.length; i++)
		{			
			if (cNode.hasChildNodes)
			{
				returnString = returnString + findProperty(cNode,prop);
			}
			if (cNode.nodeName.toString().toLowerCase() == prop.toLowerCase())
			{			
				return cNode.firstChild.nodeValue;
			}
			if (eval("cNode.attributes." + prop) != null)
			{
				return eval("cNode.attributes." + prop);
			}
			cNode = cNode.nextSibling;
		}
		return returnString;
	}
	
	function gotMessage(evt_obj:Object) {
		trace("gotMessage: recieved a message within parseXML");
		theTSExchange = theTSExchange + evt_obj.message + "\n";
	}
	
	
	function loadedXML(evt_obj:Object) {
		trace("Loaded XML: " + targetPath(this));
		
		for (var i = 0; i < myXML.lastChild.childNodes.length; i++)
		{
			var myNode:XMLNode = myXML.lastChild.childNodes[i];
			var msg = CTATUtil.makeInteractionMessage(findProperty(myNode,"selection"),
											  findProperty(myNode,"action"),
											  findProperty(myNode,"input"),
											  findProperty(myNode,"transaction_id"));
			this.messageQueue.push(msg);
		}
		this.gotMessageQueue = true;
		checkForStart(evt_obj);			
	}
	
	function checkForStart(evt_obj:Object):Void {
		trace("checkForStart: gotMessageQueue = " + gotMessageQueue + " gotStartStateEnd = " + gotStartStateEnd);
		if (gotStartStateEnd && gotMessageQueue)
		{
			this.msgID = 0;
			this.sendNextMessage(evt_obj);
		}
	}
	
	function TutoringServiceLoadTest() {
		this.gotMessageQueue = false;
		this.gotStartStateEnd = false;
		this.messageQueue = new Array();
		this.theTSExchange = "";
		this.hasSentTSExchange = false;
		this.dumpInterval = setInterval(this, "forcedSendTSExchange", 100000);			
		init();
	}
	
	private function init():Void {
		trace(targetPath(this));
		
		trace("I EXIST");
		myXML  = new XML();
		myXML.ignoreWhite = true;		
		myXML.onLoad = com.dynamicflash.utils.DelegateSource.Delegate.create(this, loadedXML);
		//eval("_level0." + flashVarName)
		myXML.load("http://localhost:8080/log" + _level0.sessionID + "/input.xml");
		//myXML.load("POOP and lots of it");		
		_global.theShell.addEventListener("GotXMLMessage", com.dynamicflash.utils.DelegateSource.Delegate.create(this, gotMessage));
		_global.theShell.addEventListener("onStartStateEndEvent", com.dynamicflash.utils.DelegateSource.Delegate.create(this, callMe));
		_global.theShell.addEventListener("inCorrectActionDetected", com.dynamicflash.utils.DelegateSource.Delegate.create(this,sendNextMessage,false));
		_global.theShell.addEventListener("correctActionDetected", com.dynamicflash.utils.DelegateSource.Delegate.create(this,sendNextMessage,true));
	}
	
	// ----------------------------------------------- END Utility --------------------------------
}
