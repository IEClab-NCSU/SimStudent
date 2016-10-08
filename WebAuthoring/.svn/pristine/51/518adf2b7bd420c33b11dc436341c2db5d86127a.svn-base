/**
 * @fileoverview CTAT's fraction bar.  It shows fractional pieces of a rectangle.
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/*
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
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
goog.require('CTAT.Component.Hierarchy.UnitDisplay');
goog.require('CTAT.ComponentRegistry');
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
	CTAT.Component.Hierarchy.UnitDisplay.call(this,
			"CTATFractionBar",
			"aFractionBar",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);


	var spacing = 1;
	var showFractionLabel = true;
	var pointer = this;

	this.init=function() {
		pointer.setInitialized(true);
		pointer.createSVG();
		pointer.render();
		pointer.configFromDescription();
		pointer.processParams();
		pointer.addComponentReference(pointer,pointer.getDivWrap());
	};

	this.render = function() { //TODO: rewrite using createDocumentFragment()
		pointer.clear();
		pointer.createBorderBase();

		var arr = pointer.parseValue();
		var cwidth = pointer.getWidth();
		//var widthFrac = new CTATFraction(pointer.getWidth());
		var sum = 0;

		//render fraction pieces
		for(var i = 0; i < arr.length; i++){
			var frac = arr[i];
			var pix = frac * 100; //prodOfFracs(frac,widthFrac).getValue();
			var c = frac.selected ? pointer.getColor() : pointer.getDeselectedColor();
			var a = frac.selected ? pointer.getColorAlpha() : pointer.getDeselectedColorAlpha();
			var r = pointer.rect(pix+'%','100%').move(sum,0)
			           .stroke({width:pointer.getPieceBorderThickness(),
			        	        color:pointer.getPieceBorderColor()})
			           .fill({color: c, opacity: a});
			r.data('value',frac.toString());//record the value of the fraction piece
			pointer.addPieceElem(r,frac.selected);//record selected/deselected information
			if(showFractionLabel){
				pointer.fraction(frac,20).move(sum+pix/2,pointer.getHeight()/2);
			}
			sum += (frac.valueOf() * cwidth) + spacing;
		}
		pointer.textForward();//bring text label forward
	};

	/* the usual getters and setters */
	this.getSpacing = function(){
		return spacing;
	};
	this.getShowFractionLabel = function(){
		return showFractionLabel;
	};
	this.setSpacing = function(aSpacing){
		spacing=aSpacing;
	};
	this.setShowFractionLabel = function(aShow){
		showFractionLabel = aShow;
	};

	/* method that reads info from the brd specific to this component */

	this.processSerialization=function(){
		pointer.ctatdebug ("processSerialization()");
		pointer.styles=pointer.getGrDescription().styles;
		if(pointer.styles === null){
			pointer.ctatdebug ("Error: styles structure is null");
			return;
		}
		pointer.ctatdebug ("Processing " + pointer.styles.length + " styles ...");

		for(var i=0; i<pointer.styles.length; i++){
			var aStyle = pointer.styles[i];
			// console.log(aStyle.styleName + " " + aStyle.styleValue);
			if(aStyle.styleName == "DrawBorder"){
				pointer.setShowBorder(aStyle.styleName == 'true');
			}if(aStyle.styleName == 'color'){
				pointer.setColor('#'+aStyle.styleValue);
			}else if(aStyle.styleName == 'spacing'){
				pointer.setSpacing(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == 'showFractionLabel'){
				pointer.setShowFractionLabel(aStyle.styleValue == 'true');
			}else if(aStyle.styleName == 'color_alpha'){
				pointer.setColorAlpha(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == 'deselected_color'){
				pointer.setDeselectedColor('#'+aStyle.styleValue);
			}else if(aStyle.styleName == 'unselected_color_alpha'){
				pointer.setDeselectedColorAlpha(aStyle.styleValue);
			}else if(aStyle.styleName == 'backgroundColor'){
				pointer.setBackgroundColor('#'+aStyle.styleValue);
			}else if(aStyle.styleName == 'transparencyValue'){
				pointer.setTransparency(aStyle.styleValue);
			}else if(aStyle.styleName == 'labelText'){
				pointer.setLabelText(aStyle.styleValue);
			}else if(aStyle.styleName == 'DrawBase'){
				pointer.drawBase(aStyle.styleValue);
			}
		}
	};
};
CTATFractionBar.prototype = Object.create(CTAT.Component.Hierarchy.UnitDisplay.prototype);
CTATFractionBar.prototype.constructor = CTATFractionBar;

CTAT.ComponentRegistry.addComponentType('CTATFractionBar', CTATFractionBar);