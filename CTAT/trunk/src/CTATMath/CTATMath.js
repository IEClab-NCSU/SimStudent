/**
 * @fileoverview Defines the CTAT.Math namespace which defines several useful
 *   math functions.
 * @author $Author: mringenb $
 * @version $Revision: 23157 $
 */
/*
 * $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 * $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATMath/CTATMath.js $
 */
goog.provide('CTAT.Math');

/**
 * Library of math functions used by CTAT
 * @namespace
 */
CTAT.Math = {
		/**
		 * Calculate the least common multiple of two numbers.
		 * @param {Number} a	the first integer.
		 * @param {Number} b	the second integer.
		 * @returns {Number}	minimum of a*i where a*i == b*j for integers i and j>0
		 */
		LeastCommonMultiple: function(a,b) {
			return a * b / CTAT.Math.GreatestCommonDivisor(a,b);
		},
		/**
		 * Calculate the greatest common divisor of two numbers.
		 * @param {Number} a	the first number.
		 * @param {Number} b	the second number.
		 * @returns {Number} 	the greatest common divisor of a and b.
		 */
		GreatestCommonDivisor: function(a,b) {
			if ((a<0) || (b<0)) {
				return CTAT.Math.GreatestCommonDivisor(Math.abs(a),Math.abs(b));
			}
			if (a===1 || b===1) return 1;
			if (a===0) return b;
			if (b===0) return a;
			var gcd = function(x,y) { return !y ? x: gcd(y, x % y);}; // faster with less checking
			return gcd(a, b);
		},

		/**
		 * Rounding to a specific decimal place.
		 * @param {Number} value	The value to round.
		 * @param {Number} exponent	The place to round to 10^exponent
		 * @returns {Number}	The rounded number to the closest 10^exponent value.
		 */
		round10: function (value, exponent) {
			// inspired by https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/round
			// as a more accurate rounding function than
			// Math.round(value*10^exponent)/10^exponent; which tends to suffer
			// more from binary rounding problems.
			if (typeof exponent === 'undefined' || Number(exponent) === 0) {
				return Math.round(value);
			}
			value = Number(value);
			exponent = Number(exponent);
			if (isNaN(value) || !(typeof exponent === 'number' && exponent % 1 === 0)) {
				return NaN;
			}
			value = value.toString().split('e');
			value = Math.round(Number(value[0]) +'e'+(value[1]?Number(value[1])-exponent:-exponent));
			value = value.toString().split('e');
			return Number(value[0]+'e'+(value[1]?Number(value[1])+exponent:exponent));
		},

		precision: 4,
		round: function (value) { return CTAT.Math.round10(value, -CTAT.Math.precision); },

		rad2deg: function (radians) { return CTAT.Math.round(radians * 180 / Math.PI); },
		deg2rad: function (degrees) { return degrees * Math.PI / 180; }
};