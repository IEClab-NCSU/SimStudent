/**
 * @fileoverview CTAT's fraction bar.  It shows fractional pieces of a rectangle.
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/*
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATFractionBar.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATFractionBar');

goog.require('CTAT.Math.Fraction');
goog.require('CTAT.Component.Base.UnitDisplay');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATGlobals');
/**
 * Class that implements CommFractionBar. Each element is a rectangle with
 * appropriate size that can be clicked. Upon clicking, the rectangle's color
 * and transparency changes and the value of the fraction bar updated.
 * @param aDescription
 * @param {number} aX x position of the component in the tutor
 * @param {number} aY y position of the component in the tutor
 * @param {number} aWidth width of the component
 * @param {number} aHeight height of the component
 * @returns
 * @constructor
 * @extends CTAT.Component.Hierarchy.UnitDisplay
 */
CTATFractionBar = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.UnitDisplay.call(this,
			"CTATFractionBar",
			"aFractionBar",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var svgNS = CTATGlobals.NameSpace.svg;

	this.init=function() {
		this.initSVG();
		this.baseGroup = document.createElementNS(svgNS, 'g');
		this.getComponent().appendChild(this.baseGroup);
		this.getComponent().classList.add('CTATFractionBar--container');
		// TODO: fix how spacing and piece borders works.
		this.drawPieces();
		this.addComponentReference(this,this.getDivWrap());
	    this.component.addEventListener('focus', this.processFocus);
	};

	//patch to make resizing in the editor redraw the component
	this.render = function() {this.drawPieces();};  
	
	this.drawPieces = function() {
		if (this.baseGroup) {
			this.clear();
			var arr = this.parseValue();
			var sum = 0;

			//render fraction pieces
			var fragment = document.createDocumentFragment();
			//var dividers = [];
			for(var i = 0; i < arr.length; i++){
				var frac = arr[i];
				var pix = frac * 100;
				var r = document.createElementNS(svgNS,'rect');
				r.classList.add('CTATFractionBar--piece');
				r.setAttributeNS(null,'height','100%');
				//r.style.width = pix+'%';
				r.setAttributeNS(null,'width',pix+'%');
				r.setAttributeNS(null,'y',0);
				r.setAttributeNS(null,'x',sum+'%');
				this.addPieceElem(r,frac,frac.selected);//record selected/deselected information
				fragment.appendChild(r);
				var fl = document.createElementNS(svgNS,'text');
				fl.classList.add('CTATFractionBar--label');
				fl.setAttributeNS(null,'text-anchor','middle');
				fl.setAttributeNS(null,'dominant-baseline','middle');
				fragment.appendChild(fl);
				var num = ''+String(frac.numerator);
				var den = ''+String(frac.denominator);
				//fl.setAttributeNS(null,'text-align','center');
				var num_lbl = document.createElementNS(svgNS, 'tspan');
				var den_lbl = document.createElementNS(svgNS, 'tspan');
				num_lbl.appendChild(document.createTextNode(num));
				den_lbl.appendChild(document.createTextNode(den));
				fl.appendChild(num_lbl);
				fl.appendChild(den_lbl);
				//num_lbl.setAttributeNS(null,'text-anchor','middle');
				var lbl_x = sum+(pix/2);
				num_lbl.setAttributeNS(null,'x',lbl_x+'%');
				num_lbl.setAttributeNS(null,'y','60%');
				num_lbl.setAttributeNS(null,'dy','-1em');
				//den_lbl.setAttributeNS(null,'text-anchor','middle');
				//den_lbl.setAttributeNS(null,'x',0);
				den_lbl.setAttributeNS(null,'x',lbl_x+'%');
				den_lbl.setAttributeNS(null,'y','60%');
				//den_lbl.setAttributeNS(null,'dy','1em');
				if (num.length>den.length) {
					num_lbl.style.textDecoration = 'underline';
					} else {
					den_lbl.style.textDecoration = 'overline';
				}
				//dividers.push(sum);
				sum += pix;
			}
			this.baseGroup.innerHTML = "";
			this.baseGroup.appendChild(fragment);

			// adjust rectangles based on style properties
			//console.log(this.getComponent().getBBox());
			//console.log(this.baseGroup.getBBox());
			var base_width = this.baseGroup.getBBox().width -1;
			//console.log(this.baseGroup.firstChild.getBBox());

			// Adjust sizes based on style parameters
			Array.prototype.slice.call(this.baseGroup.childNodes).forEach(function (rect) {
				if (rect.nodeName == 'rect') {
					var cstyle = window.getComputedStyle(rect);
					// use 'padding-left' because padding is not always available even
					// if 'padding' is used in the css (browser dependent).
					var padding = parseFloat(cstyle.getPropertyValue('padding-left')); // expect <number>px
					//console.log(cstyle.getPropertyValue('padding-left'));
					var swidth = parseFloat(cstyle.getPropertyValue('stroke-width'));
					var bbox = rect.getBBox(); // SVGRect
					rect.setAttributeNS(null,'y',bbox.y+(swidth/2));
					rect.setAttributeNS(null,'height',bbox.height-swidth);
					var adjx = bbox.x + swidth/2;
					var adjw = bbox.width - swidth;
					if (bbox.x < 1) {
						adjw -= padding/2;
					} else {
						adjx += padding/2;
						if (bbox.x+bbox.width >= base_width)
							adjw -= padding/2;
						else
							adjw -= padding;
					}
					rect.setAttributeNS(null,'x',adjx);
					rect.setAttributeNS(null,'width',adjw);
				}
			});
			
			/*
			this.setColor(this.color);
			this.setDeselectedColor(this.deselectedColor);
			this.setDeselectedColorAlpha(this.deselectedColorAlpha);
			*/
		}
	};

	this.setShowFractionLabel = function(aShow){
		return; // control this through CSS.
	};
	this.setStyleHandler('showFractionLabel',this.setShowFractionLabel);
};
CTATFractionBar.prototype = Object.create(CTAT.Component.Base.UnitDisplay.prototype);
CTATFractionBar.prototype.constructor = CTATFractionBar;

CTAT.ComponentRegistry.addComponentType('CTATFractionBar', CTATFractionBar);