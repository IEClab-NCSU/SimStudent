/**
 * @fileoverview CTAT's pie chart component.  It shows fractional portions of
 * a circle.
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 *
 */
/*
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATPieChart.js $


 -
 License:
 -
 ChangeLog:
 -
 Notes:
  Adds the following attributes for div.CTATPieChart:
    data-ctat-explode="<number>" to control the explode distance.
    data-ctat-shadow-distance="<number>" to control the drop shadow size (<= 0 turns off shadow)

 */
goog.provide('CTATPieChart');

goog.require('CTAT.Geom.Point');
goog.require('CTAT.Math.Fraction');
goog.require('CTAT.Component.Base.UnitDisplay');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATGlobals');

/**
 * Creates a pie chart that can be manipulated and tutored by CTAT.
 * @param aDescription
 * @param {number} aX x position of the component in the tutor
 * @param {number} aY y position of the component in the tutor
 * @param {number} aWidth width of the component
 * @param {number} aHeight height of the component
 * @returns
 * @constructor
 * @extends {CTAT.Component.Base.UnitDisplay}
 */
CTATPieChart = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.UnitDisplay.call(this,
			"CTATPieChart",
			"aPieChart",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var explode = 3;
	//var borderPadding = 3; // unused
	var dropDistance = 3;

	var pointer = this;
	var dropShadow;
	var dropShadowRef;
	var dropShadowUrl;
	var offset = null;
	var svgNS = CTATGlobals.NameSpace.svg; /** @private convenient reference to svg url */

	this.init=function(){
		this.initSVG();
		this.baseGroup = document.createElementNS(svgNS, 'g');
		//this.baseGroup.style.overflow = 'visible';
		this.getComponent().appendChild(this.baseGroup);
		this.getComponent().classList.add('CTATPieChart--container');
		dropShadow = document.createElementNS(svgNS,'filter');
		dropShadow.id = this.genName('Shadow');
		dropShadowRef = '#'+dropShadow.id;
		dropShadowUrl = "url("+dropShadowRef+")";
		dropShadow.setAttributeNS(null,'width','200%');
		dropShadow.setAttributeNS(null,'height','200%');
		offset = document.createElementNS(svgNS,'feOffset');
		offset.setAttributeNS(null,'in','SourceAlpha');
		offset.setAttributeNS(null,'result','offOut');
		offset.setAttributeNS(null,'dx',dropDistance);
		offset.setAttributeNS(null,'dy',dropDistance);
		dropShadow.appendChild(offset);
		var blur = document.createElementNS(svgNS,'feGaussianBlur');
		blur.setAttributeNS(null,'in','offOut');
		blur.setAttributeNS(null,'result','blurOut');
		blur.setAttributeNS(null,'stdDeviation',4);
		dropShadow.appendChild(blur);
		var blend = document.createElementNS(svgNS,'feBlend');
		blend.setAttributeNS(null,'in','SourceGraphic');
		blend.setAttributeNS(null,'in2','blurOut');
		blend.setAttributeNS(null,'mode','normal');
		dropShadow.appendChild(blend);

		this.getComponent().getElementsByTagName("defs")[0].appendChild(dropShadow); // Need svg addon to get jquery-svg support
		if (dropDistance>0)
			this.baseGroup.setAttributeNS(null,'filter',dropShadowUrl);
		//this.baseGroup.setAttributeNS(null,'stroke',this.getPieceBorderColor());
		//this.baseGroup.setAttributeNS(null,'stroke-weight',this.getPieceBorderThickness());
		this.drawPieces();
		this.addComponentReference(this,this.getDivWrap());
	    this.component.addEventListener('focus', this.processFocus);
	};

	//patch to allow for redraw when component modified by editor
	this.render = function(){this.drawPieces();};
	
	this.drawPieces = function(){
		if (this.baseGroup) {
			this.clear();
			//var bbox = new DOMRect(0,0,);
			var cstyle = window.getComputedStyle(this.getComponent());
			//var padl = parseInt(cstyle.getPropertyValue('padding-left'));
			//var padt = parseInt(cstyle.getPropertyValue('padding-top'));
			//var padr = parseInt(cstyle.getPropertyValue('padding-right'));
			//var padb = parseInt(cstyle.getPropertyValue('padding-bottom'));
			var stroke_width = parseFloat(cstyle.getPropertyValue('stroke-width'));
			//var border_width = parseInt(cstyle.getPropertyValue('border-width'));
			//var bcr = this.getComponent().getBoundingClientRect();
			//console.log(bcr);
			//console.log(cstyle.getPropertyValue('width')+' '+this.getComponent().clientWidth+' '+this.getWidth());
			//console.log(cstyle.getPropertyValue('height')+' '+this.getComponent().clientHeight+' '+this.getHeight());
			//bbox.width = this.getComponent().clientWidth - (padl + padr);
			//bbox.height = this.getComponent().clientHeight - (padt + padb);
			var bbox = this.getBoundingBox();
			var radius = Math.min(bbox.width/2,bbox.height/2)-(stroke_width+dropDistance+4); // 4: standard deviation
			//console.trace('CTATPieChart radius: '+[bbox.width, bbox.height].join(',') + ' '+ radius);
			var center = new DOMPoint(bbox.width/2, bbox.height/2);
			var arr = this.parseValue();
			//console.log(arr);
			//console.log(window.getComputedStyle(this.getComponent()).getPropertyValue('--explode'));

			var fragment = document.createDocumentFragment();
			arr.reduce(function (sum,frac,i,a) {
				if (frac.valueOf()>=1) {
					var fc = document.createElementNS(svgNS,'circle');
					fc.classList.add('CTATPieChart--piece');
					fc.cx.baseVal.value = center.x;
					fc.cy.baseVal.value = center.y;
					fc.r.baseVal.value = radius;
					this.addPieceElem(fc,frac,frac.selected);
					fragment.appendChild(fc);
				} else {
					var p = document.createElementNS(svgNS,'path');
					p.classList.add('CTATPieChart--piece');
					fragment.appendChild(p);
					var pstyle = window.getComputedStyle(p);
					//console.log(pstyle.getPropertyValue('padding-left'));
					//console.log(pstyle.getPropertyValue('fill'));

					var startAngle = 2*Math.PI*sum.valueOf();
					var start = center.add(CTAT.Geom.Point.polar(radius,startAngle));
					frac.reduce();
					var theta = 2 * Math.PI * frac.valueOf();
					var end = center.add(CTAT.Geom.Point.polar(radius,theta+startAngle));
					var tip = center.clone();

					//explodes
					if(true && explode > 0 && explode < radius){
						var exp = CTAT.Geom.Point.polar(explode,startAngle+Math.PI*frac.valueOf());
						tip = tip.add(exp); // 1/2 theta
						start = start.add(exp);
						var sint = CTAT.Geom.Point.circle_intersection(tip,start,center,radius);
						var find_closest = function(check,closest,point) {
							return check['distance'](closest)<check['distance'](point)?closest:point;
						};
						start = sint.reduce(find_closest.bind(this,start));

						end = end.add(exp);
						var eint = CTAT.Geom.Point.circle_intersection(tip,end,center,radius);
						end = eint.reduce(find_closest.bind(this,end));
					}

					//draws slice using a path
					var toS = CTAT.Geom.Point.to2DString;
					var pathStr = "M "+toS(tip); //M: moveto
					var r = new DOMPoint(radius, radius);
					pathStr+=" L "+toS(start); //L: lineto
					pathStr+=" A "+toS(r)+" 0 "+(frac.valueOf()>0.5?1:0)+",1 "+toS(end); //A: arcto (rx,ry x-axis-rotation large-arc-flag,sweep-flag x,y)+
					pathStr+=" Z"; //Z: closepath
					p.setAttributeNS(null,'d',pathStr);
					//p.style.stroke = this.getPieceBorderColor();
					//p.style.strokeWeight = this.getPieceBorderThickness();
					//p.style.fill = 'purple';
					//p.setAttributeNS(null,'stroke',this.getPieceBorderColor());
					//p.setAttributeNS(null,'stroke-width',this.getPieceBorderThickness());

					//records necessary data
					pointer.addPieceElem(p,frac,frac.selected);
					//drop shadow
					//p.setAttributeNS(null,'filter',dropShadowUrl);
					//fragment.appendChild(p);
				}
				return sum.add(frac);
				//x0 = x2; y0 = y2;//current start position
			}, new CTAT.Math.Fraction());
			this.baseGroup.innerHTML = "";
			this.baseGroup.appendChild(fragment);
			//this.textForward();
			/*
			this.setColor(this.color);
			this.setDeselectedColor(this.deselectedColor);
			this.setDeselectedColorAlpha(this.deselectedColorAlpha);
			*/
		}
		//console.log(this.parseValue());
	};


	/* typical getters and setters */

	this.getExplode = function(){
		return explode;
	};
	/*this.getBorderPadding = function(){
		return borderPadding;
	};*/ // unused
	this.getDropShadowDistance = function(){
		return dropDistance;
	};
	this.setExplode = function(aExplode){
		var ex = Number(aExplode);
		if (!isNaN(ex)) {
			var repaint = (ex!==explode);
			explode = ex;
			if (repaint) this.drawPieces();
		}
		return this;
	};
	this.setStyleHandler('explode',this.setExplode);
	this.data_ctat_handlers['explode'] = function(val) { this.setExplode(val); };

	/*this.setBorderPadding = function(aPad){
		var padding = Number(aPad);
		if (!isNaN(padding)) {
			var repaint = (padding!=borderPadding);
			borderPadding = aPad;
			if (repaint) this.drawPieces();
		}
	};*/ // unused
	//this.setStyleHandler('border_padding',this.setBorderPadding);

	this.setDropShadowDistance = function(aDrop){
		dropDistance = parseInt(aDrop);
		dropDistance = isNaN(dropDistance)?0:dropDistance;
		if (offset) {
			offset.setAttributeNS(null,'dx',dropDistance);
			offset.setAttributeNS(null,'dy',dropDistance);
			if (dropDistance<=0 && this.baseGroup) {
				this.baseGroup.removeAttributeNS(null,'filter');
			} else {
				if (!this.baseGroup.hasAttributeNS(null,'filter'))
					this.baseGroup.setAttributeNS(null,'filter',dropShadowUrl);
			}
		}
	};
	//this.setStyleHandler('DropShadowDistance',this.setDropShadowDistance);
	this.data_ctat_handlers['shadow-distance'] = function (val) { this.setDropShadowDistance(val); };
};

CTATPieChart.prototype = Object.create(CTAT.Component.Base.UnitDisplay.prototype);
CTATPieChart.prototype.constructor = CTATPieChart;

CTAT.ComponentRegistry.addComponentType('CTATPieChart',CTATPieChart);