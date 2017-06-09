/**
 * @fileoverview Unit tests for CTATGlobalFunctions using qUnit.
 * @requires //code.jquery.com/qunit/qunit-1.17.1.js
 * @requires third-party/google/closure-library/closure/goog/base.js
 *
 * @author $Author: mringenb $
 * @version $Revision: 22658 $
 */
goog.require('CTATGlobalFunctions');

QUnit.module('CTATGlobalFunctions');
QUnit.test("CTATGlobalFunctions.gensym", function(assert) {
	var gen = CTATGlobalFunctions.gensym;
	assert.deepEqual(typeof(gen),'object','Testing if an object');
	/* if more types are added, add more tests */
	// Tests for z_index
	assert.ok(gen.hasOwnProperty('z_index'),'Has z_index');
	assert.deepEqual(typeof(gen.z_index),'function','Test if z_index is a function');
	for (var i=2;i<20;i++) { // z_index starts at 2
		assert.equal(gen.z_index(),i,"z_index "+i+'='+i);
	}
	// Tests for div_id
	assert.ok(gen.hasOwnProperty('div_id'),'Has div_id');
	assert.deepEqual(typeof(gen.div_id),'function','Test if div_id is a function');
	for (var j=1;j<20;j++) { // div_id starts at 1
		assert.equal(gen.div_id(),'ctatdiv'+j,"div_id ctatdiv"+j);
	} // if div_id passes, it shows that the counters are independent.
});
QUnit.test("stringToBoolean", function(assert) {
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('True'),true);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('TRUE'),true);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('true'),true);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('yes'),true);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('1'),true);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('False'),false);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('FALSE'),false);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('false'),false);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('no'),false);
	assert.deepEqual(CTATGlobalFunctions.stringToBoolean('0'),false);
});
QUnit.test("componentToHex", function(assert) {
	assert.deepEqual(componentToHex(0),'00');
	assert.deepEqual(componentToHex(1),'01');
	assert.deepEqual(componentToHex(2),'02');
	assert.deepEqual(componentToHex(3),'03');
	assert.deepEqual(componentToHex(4),'04');
	assert.deepEqual(componentToHex(5),'05');
	assert.deepEqual(componentToHex(6),'06');
	assert.deepEqual(componentToHex(7),'07');
	assert.deepEqual(componentToHex(8),'08');
	assert.deepEqual(componentToHex(9),'09');
	assert.deepEqual(componentToHex(10),'0a');
	assert.deepEqual(componentToHex(11),'0b');
	assert.deepEqual(componentToHex(12),'0c');
	assert.deepEqual(componentToHex(13),'0d');
	assert.deepEqual(componentToHex(14),'0e');
	assert.deepEqual(componentToHex(15),'0f');
	assert.deepEqual(componentToHex(16),'10');
	assert.deepEqual(componentToHex(255),'ff');
});
QUnit.test("rgbToHex", function(assert) {
	assert.deepEqual(rgbToHex(0,0,0),'#000000');
	assert.deepEqual(rgbToHex(255,0,0),'#ff0000');
	assert.deepEqual(rgbToHex(0,255,0),'#00ff00');
	assert.deepEqual(rgbToHex(0,0,255),'#0000ff');
});
QUnit.test("CTATGlobalFunctions.formatColor", function(assert) {
	var fc = CTATGlobalFunctions.formatColor;
	// identity tests
	assert.deepEqual(fc('#FF00FF'),'#FF00FF','Identity Test');
	assert.deepEqual(fc('#ff00FF'),'#ff00FF','Identity Test');
	assert.deepEqual(fc('#0000FF'),'#0000FF','Identity Test');
	assert.deepEqual(fc('#aabbcc'),'#aabbcc','Identity Test');
	assert.deepEqual(fc('#FF0000'),'#FF0000','Identity Test');
	assert.deepEqual(fc('#888888'),'#888888','Identity Test');
	assert.deepEqual(fc('#000000'),'#000000','Identity Test');
	assert.deepEqual(fc('#FFFFFF'),'#FFFFFF','Identity Test');

	assert.deepEqual(fc('rgb(255,255,255)'),'#ffffff','RGB test');
	assert.deepEqual(fc('rgb (255 , 255 , 255)'),'#ffffff','RGB test');
	assert.deepEqual(fc('rgb(0,0,0)'),'#000000','RGB test');

	assert.deepEqual(fc('0'),'#000000','Short Hex test');
	assert.deepEqual(fc('ff'),'#0000ff','Short Hex test');
	assert.deepEqual(fc('a0ff'),'#00a0ff','Short Hex test');
	assert.deepEqual(fc('#a0ff'),'#00a0ff','Short Hex test');

	assert.deepEqual(fc('pink'),'pink','Fallthrough Test');
	assert.deepEqual(fc('black'),'black','Fallthrough Test');
	assert.deepEqual(fc('red'),'red','Fallthrough Test');
	assert.deepEqual(fc('Aquamarine'),'Aquamarine','Fallthrough Test');
});

