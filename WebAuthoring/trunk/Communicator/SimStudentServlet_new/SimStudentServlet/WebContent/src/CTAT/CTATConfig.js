/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/CTATConfig.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATConfig');

/**
 * platform values can be one of: 'ctat','google','undefined'.
 * parserType values can be one of: 'xml','json'.
 */
CTATConfig = function()
{

}

Object.defineProperty(CTATConfig, "platform", {enumerable: false, configurable: false, writable: false, value: "ctat"});

Object.defineProperty(CTATConfig, "external", {enumerable: false, configurable: false, writable: false, value: "google"}); // one of: none, google, lti, scorm

// Either one of json or xml, this only configures the format of the expected incoming message from the tracer. Outgoing
// messages will still be in xml format.
Object.defineProperty(CTATConfig, "parserType", {enumerable: false, configurable: false, writable: false, value: "xml"}); // either one of json or xml

// Either one of true or false. Use this if you want to disable the use of the baked-in images that can be found in
// CTATBinaryImages.js. Those images are used by the done button, hint button and hint window. If you set this to false
// then image urls pointing to qa.pslc will be used instead by default
Object.defineProperty(CTATConfig, "embedImages", {enumerable: false, configurable: false, writable: false, value: "false"});

if (typeof module !== 'undefined')
{
    module.exports = CTATConfig;
}
