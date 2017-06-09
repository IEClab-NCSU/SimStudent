/**
 * @fileoverview Unit test for CTAT/CTATConfig.html.
 * @requires //code.jquery.com/qunit
 * @requires third-party/google/closure-library/closure/goog/base.js
 *
 * @author $Author: mringenb $
 * @version $Revision: 21879 $
 */
goog.require('CTATConfig');

QUnit.module('CTAT/CTATConfig');
QUnit.test('CTATConfig.platform', function(assert) {
	assert.ok(CTATConfig.hasOwnProperty('platform'));
	assert.deepEqual(CTATConfig.platform,'ctat','Testing that default is CTAT');
	assert.ok(CTATConfig.platform_is_CTAT(),'Testing generated test for CTAT');
	assert.ok(!CTATConfig.platform_is_Google(),'Testing geneerated test for Google');
	assert.ok(!CTATConfig.platform_is_Undefined(),'Testing generated test for Undefined');
	CTATConfig.platform = 'Google';
	assert.deepEqual(CTATConfig.platform,'ctat','Testing the immutability of a constant.');
	// tests for CTATConfig.Options which stores possible values
	assert.ok(CTATConfig.Options.hasOwnProperty('platform'),'Checking that options for platform exists.');
	assert.ok(CTATConfig.Options.platform.hasOwnProperty('CTAT'),'Checking for existance of CTAT option.');
	assert.deepEqual(CTATConfig.Options.platform.CTAT,'ctat','Checking that the value of CTAT:ctat');
	assert.ok(CTATConfig.Options.platform.hasOwnProperty('Google','Checking for existance of Google option.'));
	assert.deepEqual(CTATConfig.Options.platform.Google,'google','Checking value of Google option.');
	assert.ok(CTATConfig.Options.platform.hasOwnProperty('Undefined','Checking for existance of Undefined option.'));
	assert.deepEqual(CTATConfig.Options.platform.Undefined,'undefined','Checking the value of Undefined option.');
});
QUnit.test('CTATConfig.external', function(assert) {
	assert.ok(CTATConfig.hasOwnProperty('external'),'Test for existence of external.');
	assert.deepEqual(CTATConfig.external,'none','Testing that it is set properly.');
	assert.ok(!CTATConfig.external_is_Google(),'Test that generated Google test works.');
	assert.ok(!CTATConfig.external_is_LTI(),'Test that generated LTI test works.');
	assert.ok(!CTATConfig.external_is_SCORM(),'Test that the generated SCORM test works.');
	assert.ok(CTATConfig.external_is_None(),'Test that the generated None test works.');
});
QUnit.test('CTATConfig.parserType', function(assert) {
	assert.ok(CTATConfig.hasOwnProperty('parserType'),'Test for existence of parserType.');
	assert.deepEqual(CTATConfig.parserType,'xml','Test that default is set correctly.');
	assert.ok(CTATConfig.parserType_is_XML(),'Test generated test for XML.');
	assert.ok(!CTATConfig.parserType_is_JSON(),'Test generated test for JSON.');
	CTATConfig.parserType = 'JSON'; // change value
	assert.deepEqual(CTATConfig.parserType,'JSON','Test that value changed.');
	assert.ok(!CTATConfig.parserType_is_XML(),'Test generated test for XML works on changed value.');
	assert.ok(CTATConfig.parserType_is_JSON(),'Test generated test for JSON works on changed value.');
	CTATConfig.parserType = 'json'; // change value
	// checks to see that generated queries are not case sensitive.
	assert.deepEqual(CTATConfig.parserType,'json','Test change to lower case.');
	assert.ok(!CTATConfig.parserType_is_XML(),'Test generated test for XML working on lower case.');
	assert.ok(CTATConfig.parserType_is_JSON(),'Test generated test for JSON working on lower case.');
});
// TODO: add unit test for generating a new property using addProperty