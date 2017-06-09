/**
 * @fileoverview Defines CTAT.Geom.Point, a utility class that extends the
 * functionality of DOMPoint @link{http://dev.w3.org/fxtf/geometry/}.
 * This should be reviewed as the draft is updated.  Also, shims should be
 * removed when it becomes standard.
 *
 * @author $Author: vvelsen $
 * @version $Revision: 24406 $
 */
/*
 * $Date: 2016-12-07 13:35:28 -0600 (週三, 07 十二月 2016) $
 * $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATMath/Point.js $
 */
goog.provide('CTAT.Geom.Point');
goog.require('CTAT.Math');

try { // test to see if DOMPoint is defined
	new DOMPoint();
} catch (e) {
	console.log("WARNING: new DOMPoint():", e, typeof(e));
	if (e instanceof ReferenceError || e instanceof TypeError) {
		console.log("   Using shim!");
		// DOMPoint shim
		var DOMPoint = function (x,y,z,w) {
			this.x = 0;
			this.y = 0;
			this.z = 0;
			this.w = 1;
			if (x instanceof DOMPoint) {
				for (var d in x) {
					this[d] = x[d];
				}
			} else {
				this.x = x || 0;
				this.y = y || 0;
				this.z = z || 0;
				this.w = w || 1;
			}
		};
	}
}
CTAT.Geom.Point.useDOMMatrix = true;
try { new DOMMatrix(); } catch (e) { // Test to see if DOMMatrix is defined
	// as we would preferentially like to use it as it is implemented in C++ and
	// therefore be quicker and more accurate.
	if (e instanceof ReferenceError) CTAT.Geom.Point.useDOMMatrix = false; }

/**
 * Add a number of points together (sequence of translations).
 * @param {DOMPoint...}
 * @returns {DOMPoint}
 * @example
 *  // returns new DOM.Point(3,3)
 * DOMPoint.add(new DOMPoint(1,1), new DOMPoint(2,2));
 */
CTAT.Geom.Point.add = function() { // TODO: optimize so that different versions are set at load time so that the check does not have to be done on each call.
	if (CTAT.Geom.Point.useDOMMatrix) { // if DOMMatrix is available, use it for speed.
		var matrix = new DOMMatrix();
		var p;
		for (var i=0; i<arguments.length; i++) {
			p = arguments[i];
			matrix.translateSelf(p.x/p.w,p.y/p.w,p.z/p.w);
		}
		return matrix.transformPoint(new DOMPoint());
	} else {
		return Array.prototype.reduce.call(arguments, function (sum, pnt) {
			for (var d in sum) { // go through all dimensions
				if (d!='w' && pnt[d]!==0) {
					sum[d] += pnt[d]/pnt.w; // as we are starting with a new DOMPoint(), we are sure that w starts with a value of 1
				}
			}
			return sum;
		}, new DOMPoint());
	}
};
//DOMPoint.add = CTAT.Geom.Point.add;
/**
 * Adds a point to this point (translate)
 */
if (!DOMPoint.prototype.add)
{	
	Object.defineProperty(DOMPoint.prototype,'add',{
		enumerable: false,
		/**
		* Adds a point to this point (translate)
		* @name add
		* @methodof DOMPoint.prototype
		* @param {DOMPoint} a
		* @returns {DOMPoint}
		*/
		value: function(a) {
			//console.trace();
			if (CTAT.Geom.Point.useDOMMatrix)
				return (new DOMMatrix()).translate(a.x/a.w,a.y/a.w,a.z/a.w).transformPoint(this);
			else	
				return CTAT.Geom.Point.add(this,a);
		}
	});
}
/**
 * Calculates the angle between two points as if they were vectors starting at the origin.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @returns {Number} radians
 */
CTAT.Geom.Point.angle = function(a,b) {
	return Math.acos(DOMPoint.dot(a,b)/(a.magnitude*b.magnitude));
};
//DOMPoint.angle = CTAT.Geom.Point.angle;
/**
 * Calculates the angle between two points as if they were vectors starting at the origin.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @returns {Number} degrees
 */
CTAT.Geom.Point.angle_degrees = function(a,b) { return CTAT.Math.rad2deg(CTAT.Geom.Point.angle(a,b)); };
//DOMPoint = CTAT.Geom.Point.angle_degrees;
/**
 * Calculates the sweep angle between two points as if they were vectors starting at the origin.
 * The sweep angle considers direction so unlike DOMPoint.angle_degrees it is not limited
 * to [0, 180].  This only operates of the x,y part of the point.
 * @param a
 * @param b
 * @returns {Number} degrees
 */
CTAT.Geom.Point.angle_between_2d = function(a,b) {
	var a1 = Math.atan2(a.y,a.x); // angle to x axis
	var a2 = Math.atan2(b.y,b.x); // angle to x axis
	var angle = a2-a1; // difference between angles.
	return CTAT.Math.rad2deg(angle);
};
//DOMPoint.angle_between_2d = CTAT.Geom.Point.angle_between_2d;
if (!DOMPoint.prototype.clone)
{
	Object.defineProperty(DOMPoint.prototype, 'clone', {
		/**
		* Clones the point.
		* @name clone
		* @methodof DOMPoint
		* @returns {DOMPoint}
		*/
		value: function() { return new DOMPoint(this);}
	});
}
/**
 * Calculate the Euclidian distance between two points.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @returns {Number}
 */
CTAT.Geom.Point.distance = function(a,b) {
	var d = CTAT.Geom.Point.add(a,CTAT.Geom.Point.scale(b,-1));
	return Math.sqrt(CTAT.Geom.Point.dot(d,d));
};
//DOMPoint.distance = CTAT.Geom.Point.distance;
//DOMPoint.prototype['distance'] = function(){}; // export
if (!DOMPoint.prototype.distance)
{
	Object.defineProperty(DOMPoint.prototype, 'distance', {
		/**
		* Calculate the Euclidian distance between this point and the given point.
		* @name distance
		* @methodof DOMPoint
		* @param {DOMPoint} a
		* @return {Number}
		*/
		value: function(a) { return CTAT.Geom.Point.distance(this, a); }
	});
}
/**
 * Calculate the dot product of two points.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @returns {Number}
 */
CTAT.Geom.Point.dot = function(a,b) {
	return (a.x*b.x + a.y*b.y + a.z*b.z)/(a.w*b.w);
};
//DOMPoint.dot = CTAT.Geom.Point.dot;
/**
 * Tests for equality of two points
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @returns {Boolean}
 */
CTAT.Geom.Point.equals = function(a,b) {
	var eq = true;
	for (var d in a) { // loop through all dimensions.
		if (d in b) {
			if (d != 'w') {
				eq = eq & a[d]/a.w == b[d]/b.w;
			}
		} else { eq = false; } // dimensionality mismatch.
		if (!eq) break;
	}
	return eq;
};
//DOMPoint.equals = CTAT.Geom.Point.equals;
/**
 * Determines a point between two specified points.
 * As f approcahes 0, the resulting point will be closer to b.
 * As f approaches 1, the resulting point will be closer to a.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @param {Number} f: (0,1)
 * @returns {DOMPoint}
 */
CTAT.Geom.Point.interpolate = function(a,b,f) {
	return CTAT.Geom.Point.add(a.scale(f), b.scale(1-f));
};
//DOMPoint.interpolate = CTAT.Geom.Point.interpolate;
if (!('magnitude' in DOMPoint.prototype))
{
	Object.defineProperty(DOMPoint.prototype, 'magnitude', {enumerable: false,
		/**
		* Gets the magnitude of the point (Euclidian distance from the origin)
		* @methodof DOMPoint
		* @returns {Number}
		*/
		get: function() { return CTAT.Math.round(Math.sqrt(CTAT.Geom.Point.dot(this,this))); }
	});
}

if (!DOMPoint.prototype.normalize)
{
	Object.defineProperty(DOMPoint.prototype, 'normalize', {
		/**
		* Scales the length of the point to the given length.
		* @name normalize
		* @methodof DOMPoint
		* @param {Number} [unit=1]
		* @returns {DOMPoint}
		*/
		value: function(unit) {
		unit=unit?unit:1;
		return this.scale(unit/this.magnitude);
		}
	});
}
/**
 * Scale the given point by the given value.
 * @param {DOMPoint} p
 * @param {Number} v
 * @returns {DOMPoint}
 */
CTAT.Geom.Point.scale = function(p,v) {
	if (CTAT.Geom.Point.useDOMMatrix) {
		return new DOMMatrix().scale(v).transformPoint(p);
	} else {
		var ret = new DOMPoint(p); // initialize with p to get w set
		for (var d in ret) {
			if (d!='w') {
				ret[d] = CTAT.Math.round(p[d] * v); // DOMMatrix does not need rounding
			}
		}
		return ret;
	}
};
//DOMPoint.scale = CTAT.Geom.Point;
if (!DOMPoint.prototype.scale)
{
	Object.defineProperty(DOMPoint.prototype, 'scale', {
		/**
		* Scale this point by the given value.
		* @name scale
		* @methodof DOMPoint
		* @param {Number} v
		* @returns {DOMPoint}
		*/
		value: function(v) { return CTAT.Geom.Point.scale(this,v); }
	});
}

/**
 * Produces a x,y point from polar notation.
 * @param {Number} len	The length of the vector.
 * @param {Number} angle	The angle of the vector in radians.
 */
CTAT.Geom.Point.polar = function(len, angle) {
	return new DOMPoint(
			CTAT.Math.round(len*Math.cos(angle)),
			CTAT.Math.round(len*Math.sin(angle)));
};
//DOMPoint.polar = CTAT.Geom.Point.polar;

/**
 * Determine if point c is on the line defined by a and b.
 * @param {DOMPoint} a
 * @param {DOMPoint} b
 * @param {DOMPoint} c
 * @returns {Boolean}
 */
CTAT.Geom.Point.is_on_line = function(a,b,c) {
	return Math.abs(a.distance(c) + b.distance(c) - a.distance(b)) < 0.0001;
};
/**
 * Calculates the points on the line defined by start and end that intersects
 * with the circle defined by center and radius.
 * @param {DOMPoint} start
 * @param {DOMPoint} end
 * @param {DOMPoint} center
 * @param {Number} radius
 * @returns {Array} of points.
 */
CTAT.Geom.Point.circle_intersection = function(start,end,center,radius) {
	//console.log('circle_intersection('+[start,end,center,radius].join(', ')+')');
	var unit = end.add(start.scale(-1)).normalize();
	var t = CTAT.Geom.Point.dot(unit, center.add(start.scale(-1)));
	var e = start.add(unit.scale(t));
	var dist = CTAT.Geom.Point.distance(center, e);
	if (dist < radius) { // a secant line
		var dt = Math.sqrt(radius*radius - dist*dist);
		//console.log([dist,e,t,dt,t-dt,t+dt].join(' '));
		var intercepts = [start.add(unit.scale(t-dt)), start.add(unit.scale(t+dt))];
		return intercepts;
		/*return intercepts.filter(function (i) {
			console.log(' filter: '+[start,end,i].join(', ')+' -> '+CTAT.Geom.Point.is_on(start,end,i));
			return CTAT.Geom.Point.is_on_line(start,end,i);
		});*/
	} else if (dist == radius) { // a tangent line
		return [e];
	} else { // no intersection
		return [];
	}
};

/**
 * Produce a string representation of the x and y dimensions of the point.
 * @param {DOMPoint} p
 * @param {Boolean} [paren=false]	if there should be () around the point.
 * @returns {String} format: x,y
 * @example
 *  // returns "1,2"
 *  (new CTAT.Geom.Point(1,2)).toString();
 */
CTAT.Geom.Point.to2DString = function(p,paren) {
	var s = [(p.x/p.w), (p.y/p.w)].join(',');
	if (paren) { s = '('+s+')'; }
	return s;
};
/**
 * Rounds each coordinate using Math.round().
 * @methodOf CTAT.Geom.Point.prototype
 * @returns {CTAT.Geom.Point}
 * @example
 * // returns "1,2"
 * (new CTAT.Geom.Point(1.1,2.2)).toString();
 */
//round: { value: function() { return new CTAT.Geom.Point(Math.round(this.x), Math.round(this.y)); }}
