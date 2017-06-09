/**
 * @fileoverview Unit tests for CTAT.Geom.Point functions using qUnit.
 * @requires unit_test_util.js
 * @requires //code.jquery.com/qunit/qunit-1.15.0.js
 * @requires third-party/google/closure-library/closure/goog/base.js
 *
 * @author $Author: mringenb $
 * @version $Revision: 22197 $
 */
goog.require('CTAT.Geom.Point');

QUnit.module("CTAT.Geom.Point");
QUnit.test("new", function(assert) {
	var p = new DOMPoint();
	assert.deepEqual(p.x,0,"Default x");
	assert.deepEqual(p.y,0,"Default y");
	/*p = new Point("1","foo");
	assert.deepEqual(p.x,0,"Default x with string");
	assert.deepEqual(p.y,0,"Default y with string");*/

	var a,b;
	for (var i=0; i<1000; i++) {
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		p = new DOMPoint(a,b);
		assert.deepEqual(p.x,a);
		assert.deepEqual(p.y,b);
	}
});
QUnit.test("add", function(assert) {
	var p1,p2,p;
	var a,b,c,d;
	for (var i=0; i<1000; i++) {
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		c = unit_test_util.gen100();
		d = unit_test_util.gen100();
		p1 = new DOMPoint(a,b);
		p2 = new DOMPoint(c,d);
		p = p1.add(p2);
		assert.deepEqual(p1,new DOMPoint(a,b));
		assert.deepEqual(p2,new DOMPoint(c,d));
		assert.deepEqual(p,new DOMPoint(a+c,b+d));
		assert.deepEqual(CTAT.Geom.Point.add(p1,p2),new DOMPoint(a+c,b+d));
	}
});
QUnit.test("<DOMPoint>.clone", function(assert) {
	var Point = DOMPoint;
	var p = new Point(1,2);
	var pc = p.clone();
	assert.deepEqual(p,pc,"Test that clone is equal to original.");
	pc.x = 5;
	assert.notEqual(pc.x,p.x,"Test that they are independent.");
});
QUnit.test("<DOMPoint>.distance", function(assert) {
	var Point = DOMPoint;
	var p;
	var i,a,b;
	var o = new Point(0,0);
	for (i=0; i<100; i++) {
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		p = new Point(a,0);
		assert.deepEqual(p.distance(o),a,'(a,0)->(0,0)===a');
		assert.deepEqual(p.distance(new Point(-a,0)),2*a);
		p = new Point(0,b);
		assert.deepEqual(p.distance(o),b,'(0,b)->(0,0)===b');
		assert.deepEqual(p.distance(new Point(0,-b)),2*b);
		p = new Point(a,b);
		assert.deepEqual(p.distance(p),0,'0 distance');
		assert.deepEqual(p.distance(new Point(b,b)),Math.abs(b-a));
		assert.deepEqual(p.distance(new Point(a,a)),Math.abs(b-a));
	}
	p = new Point(1,1);
	assert.deepEqual(p.distance(new Point(4,5)),5,'3,4,5');
});
QUnit.test('dot', function(assert) { // TODO: add more cases
	var Point = DOMPoint;
	var v = new Point(2,4);
	var w = new Point(1,5);
	assert.deepEqual(CTAT.Geom.Point.dot(v,w),22);
});
QUnit.test('angle_between_2d', function(assert) { // TODO: add more cases
	var Point = DOMPoint;
	var px = new Point(1,0);
	var py = new Point(0,1);
	var pnx = new Point(-1,0);
	var pny = new Point(0,-1);
	var pu = new Point(1,1);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(px, px),0);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(px, py),90);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(py, px),-90);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(px, pnx),180);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(px, pny),-90);
	assert.deepEqual(CTAT.Geom.Point.angle_between_2d(px, pu),45);
});
QUnit.test('equals', function(assert) {
	//var Point = CTAT.Geom.Point;
	var a,b,p1,p2;
	for (var i=0; i<100; i++) {
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		p1 = new DOMPoint(a,b);
		p2 = new DOMPoint(a,b);
		assert.deepEqual(p1,p2);
		assert.ok(CTAT.Geom.Point.equals(p1,p2));
		p2.x = p2.x+3;
		assert.ok(!CTAT.Geom.Point.equals(p1,p2));
	}
});
QUnit.test('interpolate', function(assert) { // TODO: add more cases
	//var Point = DOMPoint; //CTAT.Geom.Point;
	var p = new DOMPoint(1,1);
	assert.deepEqual(CTAT.Geom.Point.interpolate(p, new DOMPoint(1,2),1), new DOMPoint(1,1));
	assert.deepEqual(CTAT.Geom.Point.interpolate(p, new DOMPoint(1,3),0.5), new DOMPoint(1,2));
	assert.deepEqual(CTAT.Geom.Point.interpolate(p, new DOMPoint(0,0),0), new DOMPoint(0,0));
});
QUnit.test('<DOMPoint>.magnitude', function(assert) { // TODO: add more cases
	//var Point = CTAT.Geom.Point;
	assert.deepEqual((new DOMPoint(1,0)).magnitude,1);
	assert.deepEqual((new DOMPoint(0,1)).magnitude,1);
	assert.deepEqual((new DOMPoint(3,4)).magnitude,5);
});
QUnit.test('<DOMPoint>.normalize', function(assert) { // TODO: add more cases
	//var Point = CTAT.Geom.Point;
	var p = new DOMPoint(3,4);
	assert.deepEqual(p.magnitude,5,'Test origional length.');
	//p.normalize(1);
	assert.deepEqual(p.normalize(1).magnitude,1,'Test that it normalized the length to 1.');
	//p.normalize(2);
	assert.deepEqual(p.normalize(2).magnitude,2,'Test scaling.');
	//p.normalize(5);
	assert.deepEqual(p.normalize(5).magnitude,5,'Test scaling.');
	assert.deepEqual(p.normalize(1).normalize(5),new DOMPoint(3,4),'Testing reversability.');

	//p.normalize();
	assert.deepEqual(p.normalize().magnitude,1,'Testing empty parameter.');
});
/*QUnit.test('offset', function(assert) { // TODO: add more cases
	var Point = CTAT.Geom.Point;
	var p = new Point(1,2);
	p.offset(1,2);
	assert.deepEqual(p,new Point(2,4));
});*/
/*QUnit.test('subtract', function(assert) { // TODO: add more cases
	var Point = CTAT.Geom.Point;
	var p = new Point(1,2);
	var s = new Point(1,1);
	assert.deepEqual(p.subtract(s),new Point(0,1));
});*/
QUnit.test('to2DString', function(assert) {
	var tos = CTAT.Geom.Point.to2DString;
	var p,a,b;
	for (var i=0; i<100; i++){
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		p = new DOMPoint(a,b);
		assert.deepEqual(tos(p),a+','+b);
		assert.deepEqual(tos(p,true),'('+a+','+b+')');
	}
});
QUnit.test('polar', function(assert) { // TODO: more test cases.
	//var Point = CTAT.Geom.Point;
	var polar = CTAT.Geom.Point.polar;
	assert.deepEqual(polar(1,0),new DOMPoint(1,0));
	assert.deepEqual(polar(1,Math.PI),new DOMPoint(-1,0));
	assert.deepEqual(polar(2,Math.PI/2),new DOMPoint(0,2));
	assert.deepEqual(polar(Math.sqrt(2),Math.PI/4), new DOMPoint(1,1));
});
QUnit.test("distance", function(assert) {
	var Point = DOMPoint;
	var p;
	var i,a,b;
	var o = new Point(0,0);
	for (i=0; i<100; i++) {
		a = unit_test_util.gen100();
		b = unit_test_util.gen100();
		p = new Point(a,0);
		assert.deepEqual(CTAT.Geom.Point.distance(p,o),a,'(a,0)->(0,0)===a');
		assert.deepEqual(CTAT.Geom.Point.distance(p,new Point(-a,0)),2*a);
		p = new Point(0,b);
		assert.deepEqual(CTAT.Geom.Point.distance(p,o),b,'(0,b)->(0,0)===b');
		assert.deepEqual(CTAT.Geom.Point.distance(p,new Point(0,-b)),2*b);
		p = new Point(a,b);
		assert.deepEqual(CTAT.Geom.Point.distance(p,p),0,'0 distance');
		assert.deepEqual(CTAT.Geom.Point.distance(p,new Point(b,b)),Math.abs(b-a));
		assert.deepEqual(CTAT.Geom.Point.distance(p,new Point(a,a)),Math.abs(b-a));
	}
	p = new Point(1,1);
	assert.deepEqual(CTAT.Geom.Point.distance(p,new Point(4,5)),5,'3,4,5');
});
QUnit.test("circle_intersection", function(assert) {
	//var Point = CTAT.Geom.Point;
	assert.deepEqual(CTAT.Geom.Point.circle_intersection(
			new DOMPoint(0,0),new DOMPoint(10,0),new DOMPoint(0,0),5),
			[new DOMPoint(-5,0), new DOMPoint(5,0)],'Test secant line.');
	assert.deepEqual(CTAT.Geom.Point.circle_intersection(
			new DOMPoint(-5,5),new DOMPoint(5,5),new DOMPoint(0,0),5),
			[new DOMPoint(0,5)],'Test tangent line.');
	assert.deepEqual(CTAT.Geom.Point.circle_intersection(
			new DOMPoint(-5,10),new DOMPoint(5,10),new DOMPoint(0,0),5), [],'Test non-intersecting.');
});
