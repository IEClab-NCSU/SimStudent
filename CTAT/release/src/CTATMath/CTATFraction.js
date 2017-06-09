/**
 * @fileoverview Defines CTAT.Math.Fraction, a utility class for dealing with
 *   fractions.
 *
 * @author $Author: mringenb $
 * @version $Revision: 23157 $
 */
/*
 * $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 * $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATMath/CTATFraction.js $
 */
goog.provide('CTAT.Math.Fraction');
goog.require('CTAT.Math');


/**
 * @class
 * @classdesc A class representing fractions and for performing fraction based math.
 * @param {Number|String|CTAT.Math.Fraction|null} [num=0]	The numerator.
 * @param {Number|String|null} [den=1]	The denominator.
 * @see CTAT.Math.Fraction#set
 * @requires CTAT.Math
 * @constructor
 */
CTAT.Math.Fraction = function(num, den) {
	/**
	 * @private
	 */
	this._numerator = 0;
	/**
	 * @private
	 */
	this._denominator = 1;
	//this.set(this._numerator,this._denominator);
	this.set(num,den); // using set is about twice as complex, but still
	// multiple times faster than defining methods here.
};

CTAT.Math.Fraction.prototype = Object.create(CTAT.Math.Fraction.prototype, {
	/**
	 * The numerator of the fraction.
	 * @var
	 * @memberof CTAT.Math.Fraction.prototype
	 * @fieldOf CTAT.Math.Fraction.prototype
	 * @type Number
	 */
	numerator: {
		get: function() { return this._numerator; },
		set: function(value) { this._numerator=value; }
	},
	/**
	 * The denominator of the fraction.
	 * @fieldOf CTAT.Math.Fraction.prototype
	 * @type Number
	 */
	denominator: {
		get: function() { return this._denominator; },
		set: function(value) { this._denominator=value; }
	},

	/**
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @description Produces a string representation of the fraction.
	 * @returns {String}
	 * @example
	 * // returns "1/2"
	 * (new CTAT.Math.Fraction(1,2)).toString();
	 */
	toString: {
		value: function() {
			if (this.denominator === 1) {
				return String(this._numerator);
			}
			return String(this._numerator)+"/"+String(this._denominator);
		}
	},
	/**
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @description Produce a numerical value (float) of the fraction.
	 * @returns {Number}
	 * @example
	 * // returns 0.5
	 * (new CTAT.Math.Fraction(1,2)).valueOf();
	 */
	valueOf: {
		value: function() {
			return this.numerator/this.denominator;
		}
	},
	/**
	 * Sets this fraction given the supplied string representation.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {String} str	The string to parse.
	 * @example
	 * // Sets frac.numerator==1, frac.denominator==2
	 * var frac = new CTAT.Math.Fraction();
	 * frac.fromString('1/2');
	 */
	fromString: {
		value: function(str) {
			// TODO? add mixed fraction support
			var fraction_regEx = /(\d*\.?\d*)\s*\/\s*(\d*\.?\d*)/;
			var fraction_array = str.match(fraction_regEx);
			if (fraction_array) {
				this._numerator = Number(fraction_array[1]);
				this._denominator = Number(fraction_array[2]);
			} else {
				this.fromValue(str);
			}
		}
	},
	/**
	 * Sets the value of the fraction based on a number
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number} num	the numeric value to set the fraction to
	 * @example // sets frac.numerator==3, frac.denominator==1
	 * var frac = new CTAT.Math.Fraction();
	 * frac.fromValue(3);
	 */
	fromValue: {
		value: function(num) {
			this._numerator = Number(num.valueOf());
			this._denominator = 1;
		}
	},
	//TODO: Add support for reading from mathml

	/**
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {Element} a string representing the MathML representation of the
	 *   fraction.
	 * @example
	 * (new CTAT.Math.Fraction(1,2)).toMathML().outerHTML
	 * // returns <math><mfrac><mrow><mn>1</mn></mrow><mrow><mn>2</mn></mrow></mfrac></math>
	 */
	toMathML: {
		value: function() {
			var mathml = 'http://www.w3.org/1998/Math/MathML';
			var math = document.createElementNS(mathml, 'math');
			var num = document.createElementNS(mathml, 'mn');
			num.textContent = this.numerator;
			if (this.denominator == 1) {
				math.appendChild(num);
			} else {
				var frac = document.createElementNS(mathml, 'mfrac');
				var nrow = document.createElementNS(mathml, 'mrow');
				nrow.appendChild(num);
				frac.appendChild(nrow);
				var drow = document.createElementNS(mathml, 'mrow');
				var den = document.createElementNS(mathml, 'mn');
				den.textContent = this.denominator;
				drow.appendChild(den);
				frac.appendChild(drow);
				math.appendChild(frac);
			}
			return math;
		}
	},

	/**
	 * Boolean check to see if the fraction is a proper fraction.
	 * @returns {Boolean} true if the fraction is a proper fraction (eg) fraction < 1
	 * @fieldOf CTAT.Math.Fraction.prototype
	 * @readonly
	 * @example
	 * // returns true
	 * (new CTAT.Math.Fraction(1,2)).is_proper;
	 * @example
	 * // returns false
	 * (new CTAT.Math.Fraction(3,2)).is_proper;
	 */
	is_proper: {
		get: function() { return Math.abs(this._numerator)<Math.abs(this._denominator);}
	},

	/**
	 * Accesses the whole number (integer) part of the fraction as if it were a
	 *  mixed fraction.
	 * @returns {Number} the integer part of the mixed fraction representation
	 *   of the fraction.
	 * @fieldOf CTAT.Math.Fraction.prototype
	 * @readonly
	 * @example
	 * // returns 1
	 * (new CTAT.Math.Fraction(3,2)).whole_part;
	 */
	whole_part: {
		get: function() { return Math.floor(this.valueOf()); }
	},
	/**
	 * Accesses the remainder part of the fraction as if it were a mixed fraction.
	 * @returns {CTAT.Math.Fraction} the fraction part of the mixed fraction
	 *   representation of the fraction.
	 * @fieldOf CTAT.Math.Fraction.prototype
	 * @readonly
	 * @example
	 * // returns CTAT.Math.Fraction(1,2)
	 * (new CTAT.Math.Fraction(3,2)).remainder_part;
	 */
	remainder_part: {
		get: function() { return new CTAT.Math.Fraction(this._numerator%this._denominator,this._denominator); }
	},

	/**
	 * Modifies the fraction so that the denominator is set to the given value
	 * but the value of the fraction remains the same.  Note: careful using
	 * this as it is destructive and the resulting numerator is not checked to
	 * see if it is an integer.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {number} denominator	The new value of the denominator.
	 */
	set_denominator: {
		value: function(denominator) {
			//var val = this.numerator/this.denominator;
			this._numerator = this.numerator * denominator/this.denominator;
			if (Math.abs(this.numerator - Math.round(this.numerator)) < 1e-9) {
				// this should help address small binary math errors.
				this.numerator = Math.round(this.numerator);
			}
			this._denominator = denominator;
			return this;
		}
	},
	/**
	 * Destructively multiplies the fractions numerator and denominator by the
	 * given value, which does not change its value.  Equivalent to multiplying
	 * the fraction by factor/factor.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number} factor	The value to multiply the parts of the fraction.
	 * @example // results true
	 * var frac = new CTAT.Math.Fraction(1,2);
	 * frac.scale(3);
	 * frac.numerator==3 && frac.denominator==6;
	 */
	scale: {
		value: function(factor) {
			this._numerator *= factor;
			if (factor === 0)
				this._denominator = 1;
			else
				this._denominator = Math.abs(this._denominator * factor);
		}
	},
	/**
	 * Sets the fraction based on what parameters are passed to it.
	 * @constructs CTAT.Math.Fraction
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number|String|CTAT.Math.Fraction} [numerator=0]
	 *                  	If {Number}, it will set the numerator to the value
	 *                      If {String}, it will try to set the numerator by
	 *                        trying to parse it as a number if there is a
	 *                        denominator specified, otherwise it will try to
	 *                        parse it as a fraction with @see CTAT.Math.Fraction#fromString
	 *                      If {CTAT.Math.Fraction}, the numerator will be the
	 *                        value of the fraction if a denominator is specified,
	 *                        otherwise, it will clone the given fraction.
	 * @param {Number|String} [denominator=1] If {Number}, it will set the denominator to the value
	 *                       If {String} it will set the denominator by trying
	 *                        to parse it as a number.
	 * @returns {CTAT.Math.Fraction} a CTAT.Math.Fraction generated from the inputs.
	 */
	set: {
		value: function(numerator,denominator) {
			this._numerator = 0;
			this._denominator = 1;
			if (typeof numerator !== 'undefined' && denominator) {
				if (typeof(numerator) === 'number') { // if a number
					this.numerator = numerator;
				} else if (typeof(numerator) === "string") { // if a string
					this.numerator = Number(numerator); // coerce into number
				} else if (numerator) { // if exists
					this.numerator = numerator.valueOf(); // try getting its value
				} else {
					this.numerator = 0; // otherwise default to 0
				}

				if (typeof(denominator) === 'number') {
					this.denominator = denominator;
				} else if (typeof(denominator) === 'string') {
					this.denominator = Number(denominator);
				} else {
					this.denominator = denominator.valueOf();
				}
			} else if (numerator) { // numerator but no denominator
				if (numerator instanceof CTAT.Math.Fraction) {
					this.numerator = numerator.numerator;
					this.denominator = numerator.denominator;
				} else if (typeof(numerator) === 'number') {
					this.numerator = numerator;
					this.denominator = 1;
				} else if (typeof(numerator) === 'string') {
					this.fromString(numerator);
				} else if (numerator) {
					this.numerator = numerator.valueOf();
					this.denominator = 1;
				} else {
					this.numerator = 0;
					this.denominator = 1;
				}
			}
			// Final checks to make sure we have numbers
			if (isNaN(this._numerator)) {
				this._numerator = 0;
			}
			if (isNaN(this._denominator)) {
				this._denominator = 1;
			}
			return this;
		}
	},

	/**
	 * Destructively reduce the fraction.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {CTAT.Math.Fraction} this after reducing.
	 * @example // frac.numerator==1 && frac.denominator==2
	 * var frac = new CTAT.Math.Fraction(5,10);
	 * frac.reduce();
	 */
	reduce: {
		value: function() {
			var divisor = CTAT.Math.GreatestCommonDivisor(this.numerator,this.denominator);
			this._numerator/=divisor;
			this._denominator/=divisor;
			return this;
		}
	},
	/**
	 * Returns an equivalent value but reduced fraction to this one.  This is
	 * the non-destructive version of @see CTAT.Math.Fraction#reduce
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {CTAT.Math.Fraction} a new fraction of equivalent value but reduced.
	 * @example // frac.numerator==5 && frac.denominator==10 && rfrac.numerator==1 && rfrac.denominator==2
	 * var frac = new CTAT.Math.Fraction(5,10);
	 * var rfrac = frac.reduce();
	 */
	reduced: {
		value: function() {
			var fraction = new CTAT.Math.Fraction(this);
			fraction.reduce();
			return fraction;
		}
	},
	/**
	 * Returns a new fraction that is the reciprocal of this fraction.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {CTAT.Math.Fraction} denominator/numerator
	 * @example // rfrac.numerator==3 && rfrac.denominator==2
	 * var frac = new CTAT.Math.Fraction(2,3);
	 * var rfrac = frac.reciprocal();
	 */
	reciprocal: {
		value: function() { return new CTAT.Math.Fraction(this.denominator, this.numerator); }
	},
	/**
	 * Returns a new fraction that is the negative of this fraction.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {CTAT.Math.Fraction} -numerator/denominator
	 * @example // frac.numerator==-1 && frac.denominator==2
	 * var frac = (new CTAT.Math.Fraction(1,2)).negative();
	 */
	negative: {
		value: function() { return new CTAT.Math.Fraction(-this.numerator,this.denominator); }
	},
	/**
	 * Returns a new fraction that is the sum of this fraction and the addend.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number|String|CTAT.Math.Fraction} addend
	 * 					if {Number}, add the value of the addend/1 to the fraction.
	 *                  if {String} try to convert to a fraction and add it to this.
	 *                  if {CTAT.Math.Fraction} add the two fractions together.
	 * @returns {CTAT.Math.Fraction} The sum of this and the provided fraction.
	 */
	add: {
		value: function(addend) {
			var result = new CTAT.Math.Fraction();
			if (addend instanceof CTAT.Math.Fraction) {
				if (this.numerator===0 || isNaN(this.numerator)) {
					result.set(addend.numerator,addend.denominator);
				} else if (addend.numerator===0 || isNaN(addend.numerator)) {
					result.set(this.numerator,this.denominator);
				} else if (this.denominator == addend.denominator) {
					result.set(this.numerator+addend.numerator,this.denominator);
					// do not reduce if denominator is already shared.
					// needed for producing labels like 1/4,2/4,3/4,4/4,...
				} else {
					// avoid using valueOf() because floating point math is not exact
					result.set(this.numerator*addend.denominator+addend.numerator*this.denominator,this.denominator*addend.denominator);
					result.reduce();
				}
			} else if (typeof(addend) === 'string') {
				var adder = new CTAT.Math.Fraction(addend);
				result = this.add(adder);
			} else {
				result.set(addend.valueOf()*this.denominator + this.numerator, this.denominator);
			}
			return result;
		}
	},
	/**
	 * Returns a new fraction that is the result of subtracting the addend from this.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number|String|CTAT.Math.Fraction} addend
	 * 					if {Number}, subtracts the value of the addend from this.
	 *                 	if {String}, parses the string as a fraction and performs
	 *                               the subtraction.
	 *                  if {CTAT.Math.Fraction}, adds the negative of the addend to this.
	 */
	subtract: {
		value: function(addend) {
			if (addend instanceof CTAT.Math.Fraction) {
				return this.add(addend.negative());
			} else if (typeof(addend) === 'string') {
				var subend = new CTAT.Math.Fraction(addend);
				return this.add(subend.negative());
			} else {
				return this.add(-(addend.valueOf()));
			}
		}
	},
	/**
	 * Returns the reduced result of multiplying this fraction with the given value.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number|String|CTAT.Math.Fraction} multican
	 * 					if {Number}, multiply this fraction by the number.
	 *                  if {String}, multiply this fraction by the fraction
	 *                    represented by the given string.
	 *                  if {CTAT.Math.Fraction}, multiply this fraction with
	 *                    the given fraction.
	 * @returns {CTAT.Math.Fraction} the result of multiplying this times multican.
	 */
	multiply: {
		value: function(multican) {
			var result;
			if (multican instanceof CTAT.Math.Fraction) {
				result = new CTAT.Math.Fraction(this.numerator*multican.numerator,
						this.denominator*multican.denominator);
				result.reduce();
			} else if (typeof(multican) === 'string') {
				var mult = new CTAT.Math.Fraction(multican);
				result = this.multiply(mult); // already reduced
			} else {
				result = new CTAT.Math.Fraction(multican.valueOf()*this.numerator,this.denominator);
				result.reduce();
			}
			return result;
		}
	},
	/**
	 * Returns the result of dividing this fraction by the given fraction.
	 * This basically performs @see CTAT.Math.Fraction#multiply on this fraction
	 * by the reciprocal of the provided value.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param {Number|String|CTAT.Math.Fraction} multican
	 * 					if {Number}, multiply this fraction by the number.
	 *                  if {String}, multiply this fraction by the fraction
	 *                    represented by the given string.
	 *                  if {CTAT.Math.Fraction}, multiply this fraction with
	 *                    the given fraction.
	 * @returns {CTAT.Math.Fraction} the result of dividing this by multican.
	 */
	divide: {
		value: function(multican) {
			if (multican instanceof CTAT.Math.Fraction)
				return this.multiply(multican.reciprocal());
			else if (typeof(multican)==='string') {
				var divisor = new CTAT.Math.Fraction(multican);
				return this.multiply(divisor.reciprocal());
			} else {
				return this.multiply(new CTAT.Math.Fraction(1,multican.valueOf()));
			}
		}
	},

	/**
	 * Tests for the numeric equivalence.  Note: this only checks equivalence
	 * the the nearest 0.0001 to avoid problems with binary floating point error.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param cmp	Takes the value of cmp and compares it to the value of this.
	 * @returns {Boolean}
	 */
	equals: {
		value: function(cmp) {
			// As CTAT is mostly focused on easily displayable fractions, rounding
			// to the (1e-4)th decimal place should be exact enough.
			return (CTAT.Math.round(this.valueOf()) === CTAT.Math.round(cmp.valueOf()));
		}
	},
	/**
	 * Stricter test for equivalence that checks for equality of the numerator
	 * and denominator of two fractions.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @param cmp
	 * @returns {Boolean}
	 */
	deepEquals: {
		value: function(cmp) {
			return (cmp instanceof CTAT.Math.Fraction) &&
			       (this.numerator===cmp.numerator) &&
			       (this.denominator===cmp.denominator);
		}
	},
	// other comparisons are not necessary as they will coerce using valueOf()

	/**
	 * Creates an equivalent fraction to this.
	 * @methodOf CTAT.Math.Fraction.prototype
	 * @returns {CTAT.Math.Fraction}
	 */
	clone: {
		value: function() { return new CTAT.Math.Fraction(this.numerator,this.denominator); }
	},
});

/**
 * Generate a fraction from a string.
 * @param {String} str	a string representation of a fraction
 * @example // returns CTAT.Math.Fraction(1,2)
 * CTAT.Math.Fraction.FromString('1/2');
 * @deprecated use new CTAT.Math.Fraction(str);
 */
CTAT.Math.Fraction.FromString = function(str) {
	var frac = new CTAT.Math.Fraction();
	frac.setFromString(String(str));
	return frac;
};

/**
 * Generates a fraction from a given number.
 * @param {Number} dec a decimal/number representation to be converted to a fraction.
 * @returns {CTAT.Math.Fraction} a fraction representation of the given number.
 * @deprecated use new CTAT.Math.Fraction(dec);
 */
CTAT.Math.Fraction.FromNumber = function(dec) {
	var frac = new CTAT.Math.Fraction(dec);
	frac.reduce();
	return frac;
};

/**
 * Sums all of the provided fractions.
 * @returns {CTAT.Math.Fraction}
 *
 */
CTAT.Math.Fraction.Sum = function () {
	var result = new CTAT.Math.Fraction();
	for(var i = 0; i < arguments.length; i++)
		result = result.add(arguments[i]);
	return result;
};

/**
 * Multiplies all of the provided fractions together.
 * @returns {CTAT.Math.Fraction}
 */
CTAT.Math.Fraction.Product = function () {
	var result = new CTAT.Math.Fraction(1,1);
	for(var i = 0; i < arguments.length; i++)
		result = result.multiply(arguments[i]);
	return result;
};

