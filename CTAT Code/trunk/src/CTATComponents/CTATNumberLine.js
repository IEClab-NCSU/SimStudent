/**
 * @fileoverview CTAT's number line component.
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/*
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATNumberLine.js $
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 */
goog.provide('CTATNumberLine');

goog.require('CTAT.Math');
goog.require('CTAT.Math.Fraction');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSAI');
goog.require('CTAT.Component.Base.SVG');
goog.require('CTAT.Component.Base.Tutorable');
goog.require('CTAT.ComponentRegistry');
goog.require('CTAT.Geom.Point');
goog.require('CTAT.Geom.Rectangle');

/**
 * Generates a number line component
 * @class
 * Class that implements CTATNumberline. Consists of a bunch of lines, tick marks,
 * and points (circles).
 * @augments CTAT.Component.Hierarchy.SVG
 */
CTATNumberLine = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.SVG.call(this,
			"CTATNumberLine",
			"aNumberLine",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	var Fraction = CTAT.Math.Fraction; // local convenience reference
	var svgNS = CTATGlobals.NameSpace.svg; // local convenience reference

	/*************** Tick Mark Settings ***************/
	/**
	 * Private object for managing a set of tick marks.
	 * Assumes a linear step function.
	 * @param {CTATNumberLine} pnumberline	The numberline that contains these tickmarks.
	 * @param {CTAT.Math.Fraction|Number} pstep	Step size in axis units.
	 * @param {Number} psize	Size of the tick marks in pixels.
	 * @param {String} [tclass]	The class/type of tickmarks (the css selector)
	 * @param {String} [lclass]	The class/type of tickmark labels (the css selector)
	 */
	var Ticks = function(pnumberline,pstep,psize,tick_class,label_class) {
		/** @member {CTATNumberLine} */
		this.numberline = pnumberline;
		/** @member {Fraction} Scalar in axis units for the distance between tick marks */
		this.step = new Fraction(pstep);
		/** {Number} Size of the tick mark, initial setting */
		var default_size = psize;
		/** @member {Number} Size of the tick mark */
		this.size = psize;
		/** @member {CTAT.Math.Fraction} origin	The starting point for the tick marks */
		this.origin = new Fraction(0);
		/** {String} The class selector for the tick marks */
		tick_class = tick_class?tick_class:null;
		/** {String} The class selector for the tick mark labels */
		label_class = label_class?label_class:null;
		/** @member {element} The svg group associated with these tick marks */
		this.g = null;

		/**
		 * @param {CTAT.Math.Fraction|String|Number} val
		 */
		this.setStep = function(val) { this.step = new Fraction(val); };
		/** @param {Number} val */
		this.setSize = function(val) {
			var v = Number(val);
			if (isNaN(v)) v = default_size;
			this.size = v;
		};
		
		/**
		 * @param {CTAT.Math.Fraction|String|Number} val
		 */
		this.setOrigin = function(val) { this.origin = new Fraction(val); };
		/**
		 * Generate the specified tick marks.
		 */
		this.generate = function () {
			if (!this.numberline) return;
			var curVal = new Fraction();
			var fragment = document.createDocumentFragment();
			var group = document.createElementNS(svgNS, 'g');
			group.setAttributeNS(null, 'data-step-size', this.step.toString());
			group.setAttributeNS(null, 'pointer-events', 'none');
			if (tick_class)	group.classList.add(tick_class);
			fragment.appendChild(group);
			//console.log(this.step,this.step>0);
			if (this.step>0) {
				curVal.set(this.origin).reduce();
				//console.log('curVal='+curVal+' fstep='+fstep+" max="+max);
				//console.log(curVal,this.numberline.Maximum,this.numberline.Minimum);
				while (curVal<=this.numberline.Maximum) {
					if (curVal>=this.numberline.Minimum) {
						//console.log('curVal='+curVal);
						group.appendChild(this.numberline.genTickmark(curVal, null, this.size));
					}
					curVal = curVal.add(this.step);
				}
				curVal.set(this.origin).reduce();
				curVal = curVal.subtract(this.step);
				while (curVal>=this.numberline.Minimum) {
					if (curVal<=this.numberline.Maximum) {
						group.appendChild(this.numberline.genTickmark(curVal, null, this.size));
					}
					curVal = curVal.subtract(this.step);
				}
			}
			if (label_class) {
				$(group).find("text.CTATNumberLine--tickmark-label").each(function() {
					this.classList.add(label_class);
				});
			}
			return fragment;
		};
	};

	/*********** Large Tick Marks ************/
	var ticksLarge = new Ticks(this, new Fraction(1), 30,
			'CTATNumberLine--large-tickmark',
			'CTATNumberLine--large-tickmark-label');
	this.setLargeTickSize = function(size) {
		ticksLarge.setSize(s);
		this.render();
		return this;
	};
	this.setParameterHandler('LargeTickmarkSize', this.setLargeTickSize);
	/*this.setParameterHandler('LargeTickmarkColor', function(aColor) {
		ticksLarge.setColor(aColor);
		this.render();
		return this;
	});*/ // use css
	/*this.setParameterHandler('ShowLargeTickLabels', function(bool) {
		ticksLarge.setShowLabels(bool);
		this.render();
		return this;
	});*/ // use css (display:none)
	this.setParameterHandler('LargeTickmarkStep', function(step) {
		ticksLarge.setStep(step);
		this.render();
		return this;
	});
	this.data_ctat_handlers['large-tick-step'] = function(step) {
		ticksLarge.setStep(step);
		this.render();
	};

	/************ Small Tick Marks ************/
	var ticksSmall = new Ticks(this, new Fraction(1,2), 20,
			'CTATNumberLine--small-tickmark',
			'CTATNumberLine--small-tickmark-label');
	this.setSmallTickSize = function(size) {
		ticksSmall.setSize(s);
		this.render();
		return this;
	};
	this.setParameterHandler('SmallTickmarkSize',this.setSmallTickSize);
	/*this.setSmallTickColor = function(aColor) {
		ticksSmall.setColor(aColor);
		this.render();
		return this;
	};
	this.setParameterHandler('SmallTickmarkColor', this.setSmallTickColor);
	this.setParameterHandler('ShowSmallTickLabels', function(bool){
		ticksSmall.setShowLabels(bool);
		this.render();
		return this;
	});*/ // use css
	this.setParameterHandler('SmallTickmarkStep', function(step){
		ticksSmall.setStep(step);
		this.render();
		return this;
	});
	this.data_ctat_handlers['small-tick-step'] = function (step) {
		ticksSmall.setStep(step);
		this.render();
	};

	/************* Denominator Tick Marks **************/
	var ticksDenominator = new Ticks(this, new Fraction(0), 25,
			'CTATNumberLine--denominator-tickmark',
			'CTATNumberLine--denominator-tickmark-label');
	this.setParameterHandler('DenominatorTickmarkSize', function(size){
		ticksDenominator.setSize(size);
		this.render();
		return this;
	});
	this.setParameterHandler('DenominatorTickmarkStep', function (denom) {
		ticksDenominator.setStep((new Fraction(denom)).reciprocal());
	});
	this.data_ctat_handlers['denominator'] = function (denom) {
		var d = new Fraction(denom);
		ticksDenominator.setStep(d.reciprocal());
	};
	/*this.setParameterHandler('DenominatorTickmarkColor', function(aColor){
		ticksDenominator.setColor(aColor);
		this.render();
		return this;
	});*/
	
	this.setColor = function(aColor)
	{
		console.log('numberline.setColor');
		let color = CTATGlobalFunctions.formatColor(aColor);
		this.color = color;
		var gNodes = $(this.getDivWrap()).find('g');
		for (let i = 0; i < gNodes.length; i++)
		{
			$(gNodes[i]).css('stroke', color)
		}
	};
	
	this.getColor = function()
	{
		console.log('numberline.getColor');
		return this.color;
	};

	/**
	 * Creates an axis object that is used to perform calculations based on it.
	 * @param {DOMPoint} start	The starting point of the axis in the
	 *  host coordinate system.
	 * @param {DOMPoint} end	The ending point of the axis in the host
	 *  coordinate system.
	 * @param {CTAT.Math.Fraction} axis_min	The minimum value on the axis.
	 * @param {CTAT.Math.Fraction} axis_max	The maximum value on the axis.
	 */
	var Axis = function(start_point,end_point,axis_min,axis_max) {
		var _memo = {};
		var _decoration_padding = 10;
		this.svg_group = null;
		var start = start_point;
		/** The starting point of the axis in host coordinates */
		Object.defineProperty(this,'start', {
			get: function() { return start; },
			set: function(value) { _memo = {}; start=value; }
		});
		Object.defineProperty(this,'min_loc', {
			get: function() {
				if (!_memo.hasOwnProperty('min_loc')) {
					var p = this.start.clone();
					var decoration = _decoration_padding;
					decoration += 10; // if (_start_decoration) += _start_decoration.width
					var dv = CTAT.Geom.Point.polar(decoration,this.angle());
					p = p.add(dv);
					p.x = CTAT.Math.round(p.x);
					p.y = CTAT.Math.round(p.y);
					_memo.min_loc = p;
				}
				return _memo.min_loc;
			}
		});
		/** The end point of the axis in host coordinates */
		var end = end_point;
		Object.defineProperty(this,'end', {
			get: function() { return end; },
			set: function(value) { _memo = {}; end=value; }
		});
		Object.defineProperty(this,'max_loc', {
			get: function() {
				if (!_memo.hasOwnProperty('max_loc')) {
					var p = this.end.clone();
					var decoration = _decoration_padding;
					decoration += 10; // if (_end_decoration) += _end_decoration.width
					var dv = CTAT.Geom.Point.polar(decoration,this.angle());
					p = p.add(dv.scale(-1));
					p.x = CTAT.Math.round(p.x);
					p.y = CTAT.Math.round(p.y);
					_memo.max_loc = p;
				}
				return _memo.max_loc;
			}
		});
		/** The minimum value on the axis at this.start */
		var min = axis_min;
		Object.defineProperty(this,'min', {
			get: function() { return min; },
			set: function(value) { _memo = {}; min=value; }
		});
		/** The maximum value on the axis at this.end */
		var max = axis_max;
		Object.defineProperty(this,'max', {
			get: function() { return max; },
			set: function(value) { _memo = {}; max = value; }
		});
		/** The length of the axis in host coordinate units */
		Object.defineProperty(this,'length', {
			get: function() {
				if (!_memo.hasOwnProperty('length'))
					_memo.length = CTAT.Math.round(this.min_loc.distance(this.max_loc));
				return _memo.length;
			}
		});
		this.isHorizontal = function() { return this.start.y === this.end.y; };
		this.isVertical = function() { return this.start.x === this.end.x; };
		Object.defineProperty(this,'range', {
			get: function() { return this.max.subtract(this.min); }
		});
		Object.defineProperty(this,'step_size', {
			get: function() {
				if (CTAT.Geom.Point.equals(this.min_loc,this.max_loc)) return this.length;
				return CTAT.Math.round(this.length/this.range);
			}
		});
		this.s_vec = function() { return this.max_loc.add(this.min_loc.scale(-1)); };
		this.slope = function() { var m = this.s_vec(); return m.y/m.x; };
		this.step = function() {
			if (!_memo.hasOwnProperty('step')) {
				_memo.step = this.s_vec().normalize(this.step_size);
			}
			return _memo.step;
		};
		this.scalar = function() {
			if (!_memo.hasOwnProperty('scalar')) {
				_memo.scalar = this.range/this.length;
			}
			return _memo.scalar;
		};
		this.zero = function() {
			if (!_memo.hasOwnProperty('zero')) {
				_memo.zero = this.min_loc.add(this.step().scale(-this.min));
				_memo.zero.x = CTAT.Math.round(_memo.zero.x);
				_memo.zero.y = CTAT.Math.round(_memo.zero.y);
			}
			return _memo.zero;
		};
		this.angle = function() {
			if (!_memo.hasOwnProperty('angle')) {
				var m = this.end.add(this.start.scale(-1));
				_memo.angle = Math.atan2(m.y,m.x);
			}
			return _memo.angle;
		};
		this.angle_deg = function() { return CTAT.Math.rad2deg(this.angle()); };
		this.getPosition = function(value) {
			var p = this.zero().add(this.step().scale(1.0*value));
			p.x = CTAT.Math.round(p.x);
			p.y = CTAT.Math.round(p.y);
			return p;
		};
		this.projected_point = function(point) {
			var s = this.s_vec();
			var dps = point.add(this.min_loc.scale(-1)); //console.log(point,this.min_loc,'dps',dps);
			var c = 0.0 + ((s.y*dps.x)-(s.x*dps.y))/((s.y*s.y)+(s.x*s.x)); //console.log('c',c);
			return point.add(new DOMPoint(-c*s.y,c*s.x));
		};
		this.getProjectedPoint = function(point) {
			var pp = this.projected_point(point);
			//console.log("gpp",pp,pp.distance(this.max_loc),pp.distance(this.min_loc),this.length);
			if (pp.distance(this.max_loc)>this.length)
				return this.min_loc;
			if (pp.distance(this.min_loc)>this.length)
				return this.max_loc;
			return pp;
		};
		this.getAxisValue = function(point) {
			return CTAT.Math.round(min+this.scalar()*this.min_loc.distance(this.getProjectedPoint(point)));
		};
	};
	/** {Axis} */
	this.X_Axis = new Axis(new DOMPoint(0,0), new DOMPoint(0,0), new Fraction(0), new Fraction(3));

	/************** Maximum Value **************/
	/** {CTAT.Math.Fraction} The maximum value */
	Object.defineProperty(this,'Maximum', {
		/**
		 * Get the maximum value available on the number line.
		 * @return {CTAT.Math.Fraction}
		 */
		get: function() { return this.X_Axis.max; },
		/**
		 * Set the maximum value available on the number line.
		 * @param {CTAT.Math.Fraction|String|Number} value
		 */
		set: function(value) {
			this.X_Axis.max = new Fraction(value);
			//$(this.getDivWrap()).attr('data-ctat-maximum',this.X_Axis.max.toString()); // Causes part of #910
		}
	});
	this.setMaximum = function(newMax) { this.Maximum = newMax; };
	this.setParameterHandler('Maximum', this.setMaximum);
	this.data_ctat_handlers['maximum'] = this.setMaximum;

	/************ Minimum Value *************/
	/** {CTAT.Math.Fraction} the minimum value. */
	//var _min=new Fraction(0);
	Object.defineProperty(this,'Minimum', {
		/**
		 * Get the minimum value available on the number line.
		 * @return {CTAT.Math.Fraction}
		 */
		get: function() { return this.X_Axis.min; },
		/**
		 * Set the minimum value available on the number line.
		 * @param {CTAT.Math.Fraction|String|Number} value
		 */
		set: function(value) { this.X_Axis.min = new Fraction(value); }
	});
	this.setMinimum = function(m){ this.Minimum = m; };
	this.setParameterHandler('Minimum', this.setMinimum);
	this.data_ctat_handlers['minimum'] = this.setMinimum;

	/************ Maximum Number of Points *************/
	/** {Number} The maximum number of user entered points */
	var _max_points=1;
	Object.defineProperty(this, 'Max_Points', {
		get: function() { return _max_points; },
		set: function(m) {
			var im = parseInt(m);
			if (!isNaN(im)) _max_points=im;
		}
	});
	/**
	 * Sets the maximum number of points that the user can enter.
	 * @param {Number} aMaxPoints
	 */
	this.setMaxPoints=function(aMaxPoints){
		this.Max_Points = aMaxPoints;
	};
	this.setParameterHandler('Max_Points', this.setMaxPoints);
	this.data_ctat_handlers['max-points'] = this.setMaxPoints;

	/************ Point Size ***************/
	var _point_size=7; // can not set using css as only Chrome will recognize it.
	/**
	 * Sets the size of the point marker.
	 * @param {Number} newPointSize	The size of the point marker in pixels.
	 */
	this.setPointSize=function(newPointSize){
		_point_size=Number(newPointSize);
		if (cursor) {
			cursor.setAttributeNS(null, 'r',_point_size);
			$(this._point_group).find('circle').each(function () {
				$(this).attr('r',_point_size);
			});
		}
	};
	this.setStyleHandler('PointSize',this.setPointSize);
	this.data_ctat_handlers['point-size'] = this.setPointSize;

	/************ Snap to Tick Marks ************/
	var _snap = false;
	this.setSnapToTickMark = function(bool){ _snap = CTATGlobalFunctions.toBoolean(bool); };
	this.setParameterHandler('SnapToTickmark',this.setSnapToTickMark);
	this.data_ctat_handlers['snap'] = this.setSnapToTickMark;

	//var pendingPointElem;

	/************** Orientation *******************/
	/** {Number} Rotation of the axis around the midpoint in radians */
	this.theta = 0; // radians  // TODO: this should probably be moved to X_Axis
	Object.defineProperty(this,'Orientation', {
		/**
		 * Get the rotation of the axis around the midpoint in degrees.
		 * @return {Number} degrees
		 */
		get: function() { return 180 * this.theta / Math.PI; },
		/**
		 * Set the rotation of the axis around the midpoint in degrees.
		 * @param {Number} value	degrees
		 */
		set: function(value) {
			var d = Number(value);
			if (isNaN(d)) d = 0;
			this.theta = Math.PI * d / 180;
		}
	});
	this.setOrientation = function(deg) { this.Orientation = deg; this.render(); };
	this.setParameterHandler('Orientation', this.setOrientation);
	this.data_ctat_handlers['rotation'] = this.setOrientation;


	this._tickmarks = [];
	this.genTickmark = function(value, label, size) {
		var fragment = document.createDocumentFragment();
		if (!this._tickmarks.some(function(elem) { return elem.data.equals(value); })) {
			var tick = document.createElementNS(svgNS,'line');
			var origin = this.X_Axis.getPosition(value);
			var top = (CTAT.Geom.Point.polar(size/2,this.theta+Math.PI/2)).add(origin);
			var bot = (CTAT.Geom.Point.polar(-size/2,this.theta+Math.PI/2)).add(origin);
			var lloc = bot;//(CTAT.Geom.Point.polar(-((size/2)+2),this.theta+Math.PI/2)).add(origin);
			tick.setAttributeNS(null,'x1',top.x);
			tick.setAttributeNS(null,'y1',top.y);
			tick.setAttributeNS(null,'x2',bot.x);
			tick.setAttributeNS(null,'y2',bot.y);
			tick.setAttributeNS(null,'data-value',value.toString());
			tick.data = value;
			tick.location = origin;
			tick.label = null;
			this._tickmarks.push(tick);
			fragment.appendChild(tick);
			//console.log('nllabel: '+label);
			if (label!=='') {
				var tick_label = document.createElementNS(svgNS,'text');
				tick_label.classList.add('CTATNumberLine--tickmark-label');
				tick_label.setAttributeNS(null,'text-anchor','middle');
				fragment.appendChild(tick_label);
				if (label===null || label===undefined) { // generate the label from its value
					// TODO: add .data-? attribute.
					if (value.denominator == 1) {
						tick_label.appendChild(document.createTextNode(String(value.numerator.toPrecision(_precision))));
						tick_label.setAttributeNS(null,'x',lloc.x);
						tick_label.setAttributeNS(null,'y',lloc.y);
					} else {
						var num = ''+String(value.numerator);
						var den = ''+String(value.denominator);
						tick_label.style.fontSize = '0.6em';
						tick_label.setAttributeNS(null,'text-align', 'center');
						var num_lbl = document.createElementNS(svgNS,'tspan');
						var den_lbl = document.createElementNS(svgNS, 'tspan');
						num_lbl.appendChild(document.createTextNode(num));
						den_lbl.appendChild(document.createTextNode(den));
						tick_label.appendChild(num_lbl);
						tick_label.appendChild(den_lbl);
						//var nbb = num_lbl.getBBox();
						num_lbl.setAttributeNS(null,'text-anchor','middle');
						//num_lbl.setAttributeNS(null,'x',0);
						num_lbl.setAttributeNS(null,'x',lloc.x);
						num_lbl.setAttributeNS(null,'y',lloc.y);
						num_lbl.setAttributeNS(null,'dy','-1em');
						den_lbl.setAttributeNS(null,'text-anchor','middle');
						//den_lbl.setAttributeNS(null,'x',0);
						den_lbl.setAttributeNS(null,'x',lloc.x);
						den_lbl.setAttributeNS(null,'y',lloc.y);
						//den_lbl.setAttributeNS(null,'dy','1em');
						if (num.length>den.length) {
							num_lbl.style.textDecoration = 'underline';
						} else {
							den_lbl.style.textDecoration = 'overline';
						}
						//tick_label.setAttributeNS(null,'x',lloc.x);
						//tick_label.setAttributeNS(null,'y',lloc.y);
						//tick_label.setAttributeNS(null,'transform','translate'+CTAT.Geom.Point.to2DString(lloc,true));
					}
				} else {
					tick_label.appendChild(document.createTextNode(label.toString()));
					fragment.appendChild(tlabel);
					tick_label.setAttributeNS(null,'x',lloc.x);
					tick_label.setAttributeNS(null,'y',lloc.y);
				}
				tick.label = tick_label;
				//console.log($(tick_label).width(),$(tick_label).height());
			}
		}
		return fragment;
	};
	/**
	 * Draw the number line.
	 */
	var drawNumberLine = function() {
		// abort if drawing before this.init() is called
		if (this.X_Axis === null || this.X_Axis.svg_group === null) return;
		var bbox = this.getBoundingBox();
		var cstyle = window.getComputedStyle(this._axis_line_group);
		var stroke_width = parseInt(cstyle.getPropertyValue('stroke-width'));
		//console.log('stroke-width',stroke_width);
		bbox = CTAT.Geom.Rectangle.inflate(bbox,-stroke_width,-stroke_width);
		//console.log(bbox);
		var center = new DOMPoint(bbox.width/2, bbox.height/2);
		var d = Math.min(40,bbox.height,bbox.width)/4;
		var /** DOMPoint */ ref = center.add(CTAT.Geom.Point.polar(d,this.theta));
		var A = Math.sin(this.theta);
		var B = -Math.cos(this.theta);
		var C = A*center.x + B*center.y;

		var det_lr = -bbox.height*B;
		var det_tb = -A*bbox.width;
		var start = new DOMPoint(bbox.left, center.y);
		var end = new DOMPoint(bbox.right, center.y);
		if (Math.abs(CTAT.Math.round(det_lr))>0) {
			var test_l = new DOMPoint(bbox.left, (C - A*bbox.left)/B);
			var test_r = new DOMPoint(bbox.right, (C - A*bbox.right)/B);
			if (bbox.contains(test_l) || bbox.contains(test_r)) {
				if (ref.distance(test_l) < ref.distance(test_r)) {
					start = test_r;
					end = test_l;
				} else {
					start = test_l;
					end = test_r;
				}
			}
		}
		if (Math.abs(CTAT.Math.round(det_tb))>0) {
			var test_t = new DOMPoint((C-B*bbox.top)/A,bbox.top);
			var test_b = new DOMPoint((C-B*bbox.bottom)/A,bbox.bottom);
			if (bbox.contains(test_t) || bbox.contains(test_b)) {
				if (ref.distance(test_t) < ref.distance(test_b)) {
					start = test_b;
					end = test_t;
				} else {
					start = test_t;
					end = test_b;
				}
			}
		}

		// only redraw the line if it is invalidated.
		// which should only occur on a resize or a change in theta.
		if (this.X_Axis.start.x != start.x || this.X_Axis.start.y != start.y ||
				this.X_Axis.end.x != end.x || this.X_Axis.end.y != end.y) {
			this.X_Axis.start = start;
			this.X_Axis.end = end;
			this.X_Axis.svg_group.innerHTML = '';
			var axis_fragment = document.createDocumentFragment();
			var axis = document.createElementNS(svgNS, 'line');
			axis.setAttributeNS(null,'x1',start.x);
			axis.setAttributeNS(null,'y1',start.y);
			axis.setAttributeNS(null,'x2',end.x);
			axis.setAttributeNS(null,'y2',end.y);
			axis_fragment.appendChild(axis);

			// draw angle left  TODO: Move to <def><marker/></def>
			var end_l = document.createElementNS(svgNS, 'path');
			//end_l.setAttributeNS(null,'fill-opacity',0);
			end_l.setAttributeNS(null,'d','M 10,6 L 0,0 L 10,-6');
			end_l.setAttributeNS(null,'transform','translate('+CTAT.Geom.Point.to2DString(start)+') rotate('+this.Orientation+')');
			axis_fragment.appendChild(end_l);

			var end_r = document.createElementNS(svgNS, 'path'); // TODO: Move to <dev><marker/></def>
			//end_r.setAttributeNS(null,'fill-opacity',0);
			end_r.setAttributeNS(null,'d','M -10,6 L 0,0 L -10,-6');
			end_r.setAttributeNS(null,'transform','translate('+CTAT.Geom.Point.to2DString(end)+') rotate('+this.Orientation+')');
			axis_fragment.appendChild(end_r);

			this.X_Axis.svg_group.appendChild(axis_fragment);
		}

		// regenerate tick marks.  This is currently easier than
		// checking for validity.
		this._tickmarks = [];
		var tick_fragment = document.createDocumentFragment();
		tick_fragment.appendChild(ticksLarge.generate());
		tick_fragment.appendChild(ticksSmall.generate());
		tick_fragment.appendChild(ticksDenominator.generate());
		this._tick_mark_group.innerHTML = '';
		this._tick_mark_group.appendChild(tick_fragment);
		//console.log(this._tickmarks.length);

		// Move the points if the scale on the number line changed.
		var points = this._point_group.children;
		for (var i = 0; i < points.length; i++) {
			var p = points[i];
			var ploc = this.X_Axis.getPosition(p.value);
			//console.log(p,p.value,p.cx.baseVal.value,p.cy.baseVal.value);
			var cloc = new DOMPoint(p.cx.baseVal.value,p.cy.baseVal.value);
			if (ploc.distance(cloc)>1) {
				//console.log('Moving point',p);
				p.cx.baseVal.value = ploc.x;
				p.cy.baseVal.value = ploc.y;
			}
		}
	}.bind(this);
	//var drawTicks = function() {};

	var point_group = null;
	var rAFIndex = 0;

	var cursor = null;
	var cursorPosition = new DOMPoint(-30, -30);
	var inframe=false;
	/**
	 * renders a pending point when mouse moves
	 * @param event	a mouse event
	 */
	var mousemoveHandler = function(event){
		cursorPosition.x = event.clientX;
		cursorPosition.y = event.clientY;
		//console.log(pp+' '+mouse_loc);
	};
	/**
	 * Removes the pending point when mouse leaves the component.
	 * @param event	a mouse event.
	 */
	var mouseleaveHandler = function(event){
		cancelAnimationFrame(rAFIndex);
		cursor.style.visibility = 'hidden';
		inframe=false;
	};
	/**
	 * Places a point on the number line and grades it if necessary.
	 * @param event	A mouse click event.
	 */
	var clickHandler = function(event){
		if (this.getEnabled()===true && _max_points>0) {
			this.setHintHighlight(false);

			var pp, value;
			if (_snap) {
				var tick = closest_tick(event.clientX,event.clientY);
				pp = tick.location;
				value = tick.data;
			} else {
				var loc = client2local(event.clientX,event.clientY);
				pp = this.X_Axis.getProjectedPoint(loc);
				value = this.X_Axis.getAxisValue(loc);
			}

			//var fragment = document.createDocumentFragment();
			var point = document.createElementNS(svgNS, 'circle');
			//point.setAttributeNS(null, 'r', _point_size); // does not inherit from group
			point.classList.add('CTATNumberLine--point');
			point.setAttributeNS(null, 'cx', pp.x);
			point.setAttributeNS(null, 'cy', pp.y);
			point.setAttributeNS(null, 'r', _point_size);
			point.value = value;
			//fragment.appendChild(point);
			if (this._point_group.children.length>=this.Max_Points) {
				// remove one
				// prefer oldest non-correct
				var children = [].slice.call(this._point_group.children);
				//console.log("click",children,children.map(c=>c.classList.contains('CTAT--incorrect')));
				// FIXME: currently using classList as status is not getting set.
				var incorrects = children.filter(function (point, index, array) {
					return point.classList.contains('CTAT--incorrect');
					//return point.status == CTAT.Component.Base.Tutorable.Options.Status.INCORRECT;
				});
				if (incorrects.length>0) {
					this._point_group.removeChild(incorrects[0]);
				} else {
					var not_corrects = children.filter(function (point, index, array) {
						return !point.classList.contains('CTAT--correct');
						//return point.status != CTAT.Component.Base.Tutorable.Options.Status.CORRECT;
					});
					if (not_corrects.length>0) {
						this._point_group.removeChild(not_corrects[0]);
					}
					// TODO: what to do when the max is already there and not locked.
					// probably the right thing to do is just add the new point,
					// which is what is currently done.
				}
			}
			this._point_group.appendChild(point);
			this.setActionInput('AddPoint',value.toString());
			this.processAction();
		}
	}.bind(this);

	/**
	 * Initialization function.
	 */
	this.init=function(){
		//this.configFromDescription();
		this.initSVG();
		//$(this.component).wrap('<div class="CTAT-gen-component CTATNumberLine CTAT-svg"></div>')
		this.component.classList.add('CTATNumberLine--container');

		this._axis_line_group = document.createElementNS(svgNS, 'g');
		this._axis_line_group.classList.add('CTATNumberLine--axis');
		this._tick_mark_group = document.createElementNS(svgNS, 'g');
		this._tick_mark_group.classList.add('CTATNumberLine--tickmark');
		this._point_group = document.createElementNS(svgNS, 'g');
		cursor = document.createElementNS(svgNS,'circle');
		cursor.setAttributeNS(null, 'r',_point_size);
		//cursor = document.createElementNS(svgNS,'line');
		//cursor.x1.baseVal.value = 0;
		//cursor.x2.baseVal.value = 0;
		//cursor.y1.baseVal.value = 0;
		//cursor.y2.baseVal.value = 0;
		cursor.classList.add('CTATNumberLine--cursor');
		cursor.setAttributeNS(null, 'transform','translate(-30,-30)'); // initially out of view
		this.component.appendChild(this._axis_line_group);
		this.component.appendChild(this._tick_mark_group);
		this.component.appendChild(this._point_group);
		this.component.appendChild(cursor);
		//$(cursor).css('r');
		this.X_Axis.svg_group = this._axis_line_group;

		drawNumberLine();

		//this.processParams();
		this.addComponentReference(this,this.getDivWrap());

		this.component.addEventListener('mousemove',mousemoveHandler);
		//this.addSafeEventListener('mousemove',mousemoveHandler); // move to svg?
		this.component.addEventListener('mouseleave',mouseleaveHandler);
		//this.addSafeEventListener('mouseleave',mouseleaveHandler);
		var enabled = this.getEnabled;
		this.component.addEventListener('mouseenter',function() {
			if (enabled()===true && _max_points>0) {
				inframe=true;
				//cursor.setAttributeNS(null, 'fill-opacity', 0.5);
				cursor.style.visibility = null;
				rAFIndex = requestAnimationFrame(moveCursor);
			}});
		this.component.addEventListener('click', clickHandler);
	    this.component.addEventListener('focus', this.processFocus);
	};
	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var actions = [];
		var sai;
		var $div = $(this.getDivWrap());
		if ($div.attr('data-ctat-minimum')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_minimum');
			sai.setInput($div.attr('data-ctat-minimum'));
			actions.push(sai);
		}
		if ($div.attr('data-ctat-maximum')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_maximum');
			sai.setInput($div.attr('data-ctat-maximum'));
			actions.push(sai);
		}
		if ($div.attr('data-ctat-max-points')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_max_user_entries');
			sai.setInput($div.attr('data-ctat-max-points'));
			actions.push(sai);
		}
		if ($div.attr('data-ctat-large-tick-step')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_large_step');
			sai.setInput($div.attr('data-ctat-large-tick-step'));
			actions.push(sai);
		}
		if ($div.attr('data-ctat-small-tick-step')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_small_step');
			sai.setInput($div.attr('data-ctat-small-tick-step'));
			actions.push(sai);
		}
		if ($div.attr('data-ctat-denominator')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('set_denominator');
			sai.setInput($div.attr('data-ctat-denominator'));
			actions.push(sai);
		}
	    return actions;
	};
	var _in_firefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
	var client2local = function(ex,ey) {
		// use SVG's point and matrix as those seem to be better supported than DOMPoint
		// There is a bug in Firefox https://bugzilla.mozilla.org/show_bug.cgi?id=972041
		// that causes getScreenCTM to ignore CSS transformations.
		this.svgPoint = this.svgPoint?this.svgPoint:this.component.createSVGPoint(); // create once
		this.svgPoint.x = ex;
		this.svgPoint.y = ey;
		var matrix = this._axis_line_group.getScreenCTM().inverse(); // needs to be fairly local to account for any transforms
		this.svgPoint = this.svgPoint.matrixTransform(matrix);
		//console.log(this.component.getScreenCTM(),this._axis_line_group.getScreenCTM());
		var p = new DOMPoint(this.svgPoint.x,this.svgPoint.y);
		if (_in_firefox) { // handle firefox bug.
			var $c = $(this.component);
			var x_offset = parseInt($c.css('borderLeftWidth')) + parseInt($c.css('paddingLeft'))+parseInt($c.css('marginLeft'));
			var y_offset = parseInt($c.css('borderTopWidth')) + parseInt($c.css('paddingTop'))+parseInt($c.css('marginTop'));
			p.x -= x_offset;
			p.y -= y_offset;
		}
		return p;
	}.bind(this);
	var project_point = function(ecx,ecy) {
		return this.getProjectedPoint(client2local(ecx, ecy));
	}.bind(this.X_Axis);
	var closest_tick = function(ecx,ecy) { // maybe try with value?
		var pp = project_point(ecx,ecy);
		var min_tick = null;
		var min_dist = Number.POSITIVE_INFINITY;
		var tick,dist;
		for (var i=0; i<this._tickmarks.length; i++) {
			tick = this._tickmarks[i];
			dist = pp.distance(tick.location);
			if (dist<min_dist) {
				min_tick = tick;
				min_dist = dist;
			}
		}
		return min_tick;
	}.bind(this);
	var moveCursor = function() { // speed improved using rAF, but still a bit slow, particularly on some platforms.
		if (inframe) {
			if (this.getEnabled()===true) {
				if (_snap) {
					var tick = closest_tick(cursorPosition.x,cursorPosition.y);
					cursor.setAttributeNS(null,'transform',
							'translate'+CTAT.Geom.Point.to2DString(tick.location,true));
				} else {
					// much faster than cx,cy
					cursor.setAttributeNS(null,'transform',
							'translate'+CTAT.Geom.Point.to2DString(
									project_point(cursorPosition.x,cursorPosition.y),true));
				}
				rAFIndex = requestAnimationFrame(moveCursor);
			}
		}
	}.bind(this);

	/**
	 * Renders the number line by drawing the axis, tick marks, and points.
	 */
	this.render = function(){
		//if (this.baseGroup)
			drawNumberLine();
		//this.clear();
	};

	/**
	 * places point on number line and adds to placedPoints
	 * @param {Number} xCoor	the x coordinate where the point is placed.
	 * @returns {String}
	 */
	/*this.placePointByCoor = function(xCoor){
		var name = this.calcNum(xCoor).valueOf().toFixed(this.getPrecision());
		var circ = this.circle(pointSize,name).center(xCoor,this.getHeight()/2);
		var obj = {name:name,grade:"ungraded"};
		placedPoints.push(obj);
		return name;
	};*/
	/**
	 * Places point on the number line by its value.
	 * @param {CTAT.Math.Fraction|String|Number} num
	 * @returns {String}
	 */
	/*this.placePointByNum = function(num){
		var x = this.calcXCoordinate(num);
		return this.placePointByCoor(x);
	};*/
	/**
	 * removes point from numberline and removes from placedPoints and possibly
	 * correctPoints/incorrectPoints
	 * @param {Number} xCoor	the x coordinate of the point.
	 * @returns {String}
	 */
	/*this.removePointByCoor = function(xCoor){
		var name  = this.calcNum(xCoor).valueOf().toFixed(this.getPrecision());
		var point = arrayRemove(placedPoints,name,function(a,b){return a.name==b;});
		if(!point) {
			this.ctatdebug(xCoor + " does not contain a placed point.");
			console.log(name + " does not contain a placed point.");
			return;
		}
		this.removeElem(name);

		var grade = point.grade;
		if(grade === 'ungraded') return;
		var arr = grade === 'correct' ? correctPoints : incorrectPoints;
		arrayRemove(arr,name,function(a,b){return a.name==b;});
		this.removeElem(name+"_grade");
		return name;
	};*/
	/**
	 * Removes the point specified by value.
	 * @param {CTAT.Math.Fraction|Number|String} num
	 * @returns {String}
	 */
	/*this.removePointByNum = function(num){
		var x = this.calcXCoordinate(num);
		return this.removePointByCoor(x);
	};*/

	/**
	 * Given a value on the number line, return the component coordinate
	 * @param {CTAT.Math.Fraction|String|Number} num
	 * @returns {Number}
	 */
	this.calcXCoordinate = function(num){
		var fraction = new Fraction(num);
		var start = 30;
		var end = this.getWidth() - 30;
		var rangePix = Math.abs(end - start);
		var range = max.subtract(min);
		var offset = fraction.subtract(min).divide(range);
		return start+(offset/rangePix);
	};

	/**
	 * given a pixel coordinate, returns the corresponding value on the number line
	 * @param {Number} xCoor
	 * @returns {CTAT.Math.Fraction}
	 */
	this.calcNum = function(xCoor) {
		var start = 30;
		var end = this.getWidth() - 30;
		var rangePix = Math.abs(end - start);
		var range = max.subtract(min);
		var offset = range.multiply((xCoor - start)/rangePix);
		return min.add(offset);
	};

	var fix_sai = function(comp,sai) {
		switch (sai.getClassName()) {
		case "CTATMessage":
			sai = sai.getSAI();
			break;
		default:
			break;
		}
		var s = sai.clone();
		s.setAction(comp.getSAI().getAction()); // assumes single main action.
		s.setSelection(comp.getName());
		return s;
	};
	//var super_showCorrect = this.showCorrect.bind(this);
	/**
	 * shows an individual point as correct
	 * @param {CTATSAI} aMessage
	 */
	this.showCorrect = function(aSAI) {
		// TODO: add filter to make glow effect instead of fill to indicate correct.
		var name=aSAI.getInput();
		var action=aSAI.getAction();
		var call_correct = function(c) {
			c.setCorrect(fix_sai(c,aSAI));
		};

		switch (action) {
		case "AddPoint":
			if (this._point_group.children.length>0) {
				var children = [].slice.call(this._point_group.children);
				var last_point = children[children.length-1];
				last_point.classList.remove('CTAT--incorrect');
				last_point.classList.remove('CTAT--hint');
				last_point.classList.add('CTAT--correct');
				if (this.getDisableOnCorrect()) {
					// if max points are all correct
					if (children.length >= this.Max_Points &&
							children.every(function(c) {
								return c.classList.contains('CTAT--correct'); }))
						this.setEnabled(false); // make sure it is locked
					else
						this.setEnabled(true); // undo any locking on correct
				}
			}
			break;
		case "Points":
			var points = this._point_group.children;
			for (var p = 0; p < points.length; p++) {
				var point = points[p];
				point.classList.remove('CTAT--incorrect');
				point.classList.remove('CTAT--hint');
				point.classList.add('CTAT--correct');
			}
			break;
		case "set_minimum":
			// FIXME: need to modify action on aSAI to get this to work correctly
			ctrl_component(get_ctrl_min()).forEach(call_correct);
			break;
		case "set_maximum":
			ctrl_component(get_ctrl_max()).forEach(call_correct);
			break;
		case "set_large_step":
			ctrl_component(get_ctrl_large()).forEach(call_correct);
			break;
		case "set_small_step":
			ctrl_component(get_ctrl_small()).forEach(call_correct);
			break;
		case "set_denominator":
			ctrl_component(get_ctrl_denom()).forEach(call_correct);
			break;
		default:
			//super_showCorrect(aSAI);
			console.log('Unhandled Action in',this.getName(),action);
		}
	};
	/**
	 * shows an individal point incorrect
	 * @param {CTATSAI} aSAI
	 */
	this.showInCorrect = function(aSAI)
	{
		var name=aSAI.getInput();
		var action=aSAI.getAction();
		var call_incorrect = function(c) {
			c.setIncorrect(fix_sai(c,aSAI));
		};

		switch (action) {
		case "AddPoint":
			if (this._point_group.children.length>0) {
				var children = [].slice.call(this._point_group.children);
				var last_point = children[children.length-1];
				//last_point.setAttributeNS(null,'filter','url(#incorrect-glow)');
				last_point.classList.remove('CTAT--correct');
				last_point.classList.remove('CTAT--hint');
				last_point.classList.add('CTAT--incorrect');
			}
			break;
		case "Points":
			var points = this._point_group.children;
			for (var p = 0; p < points.length; p++) {
				var point = points[p];
				point.classList.remove('CTAT--correct');
				point.classList.remove('CTAT--hint');
				point.classList.add('CTAT--incorrect');
			}
			break;
		case "set_minimum":
			// FIXME: need to modify action on aSAI to get this to work correctly
			ctrl_component(get_ctrl_min()).forEach(call_incorrect);
			break;
		case "set_maximum":
			ctrl_component(get_ctrl_max()).forEach(call_incorrect);
			break;
		case "set_large_step":
			ctrl_component(get_ctrl_large()).forEach(call_incorrect);
			break;
		case "set_small_step":
			ctrl_component(get_ctrl_small()).forEach(call_incorrect);
			break;
		case "set_denominator":
			ctrl_component(get_ctrl_denom()).forEach(call_incorrect);
			break;
		default:
			console.log('Unhandled Action in',this.getName(),action);
		}
		/*var name=aMessage.getInput ();

		incorrectPoints.push(name);
		var num = new Fraction(name);
		var x = this.calcXCoordinate(num);
		var gradient = this.getSVG().gradient('radial',function(stop){
			stop.at(0.5,'#ee0000');
			stop.at(1,'#ffffff');
		});
		this.circle(pointSize*2,name+"_grade").center(x,this.getHeight()/2).attr({fill: gradient}).backward();
		arrayFind(placedPoints,function(a){return a.name==name;}).grade="incorrect";*/
	};

	var super_setEnabled = this.setEnabled;
	this.setEnabled = function(bool) {
		super_setEnabled(bool);
		if (cursor && inframe) {
			cursor.style.visibility = this.getEnabled()?null:'hidden';
		}
	};

	//interface actions
	this.set_minimum = function(str){
		this.Minimum = str;
		this.render();
	};

	this.change_minimum = function(delta) {
		var d = new Fraction(delta);
		if (d.valueOf()!==0) {
			this.Minimum = this.Minimum.add(d);
			this.render();
		}
	};
	var updateSAI_minimum = function() {
		this.setActionInput('set_minimum',this.Minimum);
	}.bind(this);

	this.set_maximum = function(str){
		this.Maximum = str;
		this.render();
	};
	this.change_maximum = function(delta) {
		var d = new Fraction(delta);
		if (d.valueOf()!==0) {
			this.Maximum = this.Maximum.add(d);
			this.render();
		}
	};
	var updateSAI_maximum = function() {
		this.setActionInput('set_maximum',this.Maximum);
	}.bind(this);

	this.set_max_user_entries = function(str){
		this.setMaxPoints(parseInt(str));
		//this.render();
		//this.setDisableOnCorrect(false);
	};
	var set_step = function(ticks,str) {
		ticks.setStep(str);
		this.render();
	};
	this.set_large_step = set_step.bind(this,ticksLarge);
	var updateSAI_large_step = function() {
		this.setActionInput('set_large_step',ticksLarge.step.toString());
	}.bind(this);
	this.set_small_step = set_step.bind(this,ticksSmall);
	var updateSAI_small_step = function() {
		this.setActionInput('set_small_step',ticksSmall.step.toString());
	}.bind(this);
	this.set_denominator = function (str) {
		if (parseInt(str)===0)
			ticksDenominator.setStep(0);
		else
			ticksDenominator.setStep((new Fraction(str)).reciprocal());
		this.render();
	};
	var updateSAI_denominator = function() {
		var denom = ticksDenominator.step.valueOf() === 0? 0: ticksDenominator.step.reciprocal();
		this.setActionInput('set_denominator', denom.toString());
	}.bind(this);

	var change_step = function(ticks,delta) {
		var d = new Fraction(delta);
		if (d.valueOf()!==0) {
			var step = ticks.step.add(delta);
			if (step.valueOf()<0) { step.set(0,1); }
			ticks.setStep(step);
			this.render();
		}
	};
	this.change_large_step = change_step.bind(this,ticksLarge);
	this.change_small_step = change_step.bind(this,ticksSmall);
	this.change_denominator = function (delta) {
		var d = new Fraction(delta);
		if (d.valueOf()!==0) {
			var s = d.add(ticksDenominator.step.denominator);
			ticksDenominator.setStep(s.reciprocal());
			this.render();
		}
	};

	var _precision = undefined;
	/**
	 * Set the precision of the displayed numbers.
	 * An Interface Action.
	 * @param {String|Integer} str	set the precision of the numbers.
	 */
	this.set_precision = function(str){
		_precision = parseInt(str);
		//this.setPrecision(parseInt(str));
		this.render();
	};

	/**
	 * Add a point to the numberline, but only if a point does not already
	 * exist at that value.
	 * An Interface Action.
	 * @param {String} val	A number or fraction.
	 */
	this.AddPoint = function(val,sai) {
		//console.log(this.getDivWrap().id,'AddPoint',val,sai);
		var value = new Fraction(val);
		var pp = this.X_Axis.getPosition(value);
		var point = document.createElementNS(svgNS, 'circle');
		//point.setAttributeNS(null, 'r', _point_size); // does not inherit from group
		point.classList.add('CTATNumberLine--point');
		point.setAttributeNS(null, 'cx', pp.x);
		point.setAttributeNS(null, 'cy', pp.y);
		point.setAttributeNS(null, 'r', _point_size);
		point.value = value;
		var children = [].slice.call(this._point_group.children);
		if (!children.some(c => value.equals(c.value))) {
			//console.log('adding',point,point.value)
			this._point_group.appendChild(point);
		}
	};
	/**
	 * Add a list of points to the numberline, clearing out any points entered
	 * previous to calling this.
	 * An Interface Action.
	 * @param {String} str	a ; separated list of values (numbers or fractions)
	 */
	this.Points = function(str,sai) {
		//console.log(this.getName(),'Points',str,sai.toString());
		this._point_group.innerHTML = ''; // remove old points
		var points = str.split(';');
		for(var i in points) {
			this.AddPoint(points[i],sai);
		}
	};

	var controller_update = function(change_callback,set_callback,update_sai,sai) {
		switch (sai.getAction()) {
		case "ButtonPressed":
			change_callback.call(this,sai.getInput());
			break;
		case "Update":
		case "UpdateTextField":
		case "UpdateTextArea":
			set_callback.call(this,sai.getInput());
			break;
		default:
			break;
		}
		update_sai.call();
		this.processAction(false,true); // do not tutor controlled action, but update tutor
		// tutoring controls without submit is done on the component (unadvised)
	};

	var get_ctrl = function(type) {
		var ctrls = $(this.getDivWrap()).attr(type);
		if (ctrls) return ctrls.split(/\s*;\s*/).map(i=>i.trim());
		else return [];
	};
	var get_ctrl_max = get_ctrl.bind(this,'data-ctat-ctrl-max');
	var get_ctrl_min = get_ctrl.bind(this,'data-ctat-ctrl-min');
	var get_ctrl_large = get_ctrl.bind(this,'data-ctat-ctrl-large-tick');
	var get_ctrl_small = get_ctrl.bind(this,'data-ctat-ctrl-small-tick');
	var get_ctrl_denom = get_ctrl.bind(this,'data-ctat-ctrl-denominator');

	var ctrl_component = function(names) {
		return names.map(function(id) { return $('#'+id).data('CTATComponent'); });
	};

	var isController = function(aComponent) {
		var ctrl_name = null;
		if (aComponent instanceof CTAT.Component.Base.Tutorable) {
			// check for component name and group name as authors might use either.
			//console.log('Checking CTATComponent');
			if (aComponent != this) {
				ctrl_name = aComponent.getName();
				//ctrl_name.push(aComponent.getComponentGroup()); // in case author uses group name
			}
		} else if (aComponent instanceof CTATSAI) {
			//console.log('Checking SAI');
			if (aComponent.getSelection() != pointer.getName()) {
				ctrl_name = aComponent.getSelection();
			}
		} else if (aComponent instanceof String) {
			if (aComponent!=pointer.getName()) {
				ctrl_name = aComponent;
			}
		} else if (aComponent instanceof Element) {
			if (aComponent!=pointer.getComponent()) {
				ctrl_name=aComponent.id;
			}
		} else { // null, undefined, etc.
			//console.log('isController no type match');
			return null;
		}

		if (ctrl_name) {
			if (isa_denom(ctrl_name)) {
				return controller_update.bind(this,
						this.change_denominator,
						this.set_denominator,
						updateSAI_denominator);
			} else if (isa_small(ctrl_name)) {
				return controller_update.bind(this,
						this.change_small_step,
						this.set_small_step,
						updateSAI_small_step);
			} else if (isa_large(ctrl_name)) {
				return controller_update.bind(this,
						this.change_large_step,
						this.set_large_step,
						updateSAI_large_step);
			} else if (isa_min(ctrl_name)) {
				return controller_update.bind(this,
						this.change_minimum,
						this.set_minimum,
						updateSAI_minimum);
			} else if (isa_max(ctrl_name)) {
				return controller_update.bind(this,
						this.change_maximum,
						this.set_maximum,
						updateSAI_maximum);
			}
		}
		//console.log('isController fallthrough');
		return null;
	}.bind(this);
	if (!CTATConfiguration.get('previewMode'))
	{
		document.addEventListener(CTAT.Component.Base.Tutorable.EventType.action, function (e) {
			var sai = e.detail.sai;
			var ctrl = isController(e.detail.component);
			if (sai && ctrl!==null) {
				ctrl(sai);
			}
		}, false);
	}
	this.setMaxControllers = function(controllers) {
		$(this.getDivWrap()).attr('data-ctat-ctrl-max',controllers);
	};
	this.setParameterHandler('MaxValueControllers',this.setMaxControllers);
	this.setMinControllers = function(controllers) {
		$(this.getDivWrap()).attr('data-ctat-ctrl-min',controllers);
	};
	this.setParameterHandler('MinValueControllers',this.setMinControllers);
	this.setLargeTickControllers = function(controllers) {
		$(this.getDivWrap()).attr('data-ctat-ctrl-large-tick',controllers);
	};
	this.setParameterHandler('LargeTickmarkControllers',
			this.setLargeTickControllers);
	this.setSmallTickControllers = function(controllers) {
		$(this.getDivWrap()).attr('data-ctat-ctrl-small-tick',controllers);
	};
	this.setParameterHandler('SmallTickmarkControllers',
			this.setSmallTickControllers);
	this.setDenominatorControllers = function(controllers) {
		$(this.getDivWrap()).attr('data-ctat-ctrl-denominator',controllers);
	};
	this.setParameterHandler('DenominatorTickmarkControllers',
			this.setDenominatorControllers);

	var isa = function(gctrl,id) {
		return gctrl().indexOf(id)>=0;
	};
	var isa_max = isa.bind(null,get_ctrl_max);
	var isa_min = isa.bind(null,get_ctrl_min);
	var isa_large = isa.bind(null,get_ctrl_large);
	var isa_small = isa.bind(null,get_ctrl_small);
	var isa_denom = isa.bind(null,get_ctrl_denom);

	this.grade = function (submit_button) {
		var sb_name = submit_button.getDivWrap().id;
		if (isa_max(sb_name)) {
			this.setAction('set_maximum');
			this.setInput(this.Maximum.toString());
		} else if (isa_min(sb_name)) {
			this.setAction('set_minimum');
			this.setInput(this.Minimum.toString());
		} else if (isa_large(sb_name)) {
			this.setAction('set_large_step');
			this.setInput(ticksLarge.step.toString());
		} else if (isa_small(sb_name)) {
			this.setAction('set_small_step');
			this.setInput(ticksSmall.step.toString());
		} else if (isa_denom(sb_name)) {
			this.setAction('set_denominator');
			this.setInput(ticksDenominator.step.reciprocal().toString());
		} else { // not in controller, therefore points.
			this.updateSAI();
		}
		this.processAction(true);
	};
	this.updateSAI = function() {
		var points = this._point_group.children;
		var pvals = [];
		for (var i = 0; i < points.length; i++) {
			pvals.push(points[i].value);
		}
		pvals.sort((a,b)=>a-b); // sort in numeric order.
		this.setActionInput('Points',pvals.join(';'));
	};
};
CTATNumberLine.prototype = Object.create(CTAT.Component.Base.SVG.prototype);
CTATNumberLine.prototype.constructor = CTATNumberLine;

CTAT.ComponentRegistry.addComponentType('CTATNumberLine',CTATNumberLine);
