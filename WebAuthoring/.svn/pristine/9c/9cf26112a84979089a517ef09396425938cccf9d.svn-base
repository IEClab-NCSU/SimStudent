/**
 * @fileoverview CTAT's pie chart component.  It shows fractional portions of
 * a circle.
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/*
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATPieChart.js $


 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATPieChart');

goog.require('CTAT.Math.Fraction');
goog.require('CTAT.Component.Hierarchy.UnitDisplay');
goog.require('CTAT.ComponentRegistry');

/**
 * Creates a pie chart that can be manipulated and tutored by CTAT.
 * @param aDescription
 * @param {number} aX x position of the component in the tutor
 * @param {number} aY y position of the component in the tutor
 * @param {number} aWidth width of the component
 * @param {number} aHeight height of the component
 * @returns
 * @constructor
 * @extends {CTAT.Component.Hierarchy.UnitDisplay}
 */
CTATPieChart = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	var adjust = 5;
	CTAT.Component.Hierarchy.UnitDisplay.call(this,
			"CTATPieChart",
			"aPieChart",
			aDescription,
			aX,
			aY,
			aWidth + adjust,
			aHeight + adjust);

	var explode = 3;
	var borderPadding = 3;
	var dropDistance = 1;

	var pointer = this;

	this.init=function(){
		pointer.setInitialized(true);
		pointer.createSVG();
		pointer.render();
		pointer.configFromDescription();
		pointer.processParams();
		pointer.addComponentReference(pointer,pointer.getDivWrap());
	};

	this.render = function(){
		pointer.clear();
		pointer.createBorderBase();

		var radius = pointer.getHeight()/2 - borderPadding;
		var cx = radius + borderPadding, cy = radius + borderPadding;//center coordinates
		var arr = pointer.parseValue();
		var sum = new CTAT.Math.Fraction();//accumulates sum of pieces

		//uses ineffable trig to place pie slices
		//slices are rendered at (0,0) and later moved to make calculations easier
		//proof of this algorithm is left as an exercise to the reader
		var x0 = radius, y0 = 0;//start position of a pie slice
		for(var i = 0; i < arr.length; i++){
			//calculates pie slice coordinates without explosion
			var accumAngle = 2*Math.PI*sum.valueOf();
			var frac = arr[i];
			var theta = 2 * Math.PI * frac.valueOf();
			var x1 = radius * Math.cos(theta + accumAngle);//end position of pie slice on circle
			var y1 = radius * Math.sin(theta + accumAngle);
			var x2 = x1, y2 = y1;
			var xd = 0, yd = 0;//coordinate of the tip of pie slice

			//explodes
			if(explode > 0){
				var xM = (x0 + x1)/2;
				var yM = (y0 + y1)/2;
				var a = x0-xM,b = y0-yM;

				var alpha = Math.asin(Math.sqrt(a*a + b*b)/radius);
				alpha = Math.abs(alpha);
				var half_theta = theta/2;
				var phi = half_theta - Math.asin(explode/radius*Math.sin(Math.PI-half_theta));
				x0 = radius * Math.cos(alpha-phi+ accumAngle);
				y0 = radius * Math.sin((alpha-phi+ accumAngle));
				x1 = radius * Math.cos(alpha+phi+ accumAngle);
				y1 = radius * Math.sin((alpha+phi+ accumAngle));
				xd = explode * Math.cos(alpha+ accumAngle);
				yd = explode * Math.sin(alpha+ accumAngle);
			}
			xd = Math.round(xd); yd = Math.round(yd);
			x0 = Math.round(x0); y0 = Math.round(y0);
			x1 = Math.round(x1); y1 = Math.round(y1);

			//draws slice using a path
			var pathStr = "M "+xd+","+yd; //M: moveto
			pathStr+=" L "+x0+","+y0; //L: lineto
			pathStr+=" A "+radius+","+radius+" 0 0,1 "+x1+","+y1; //A: arcto
			pathStr+=" Z"; //Z: closepath
			var c = frac.selected ? pointer.getColor() : pointer.getDeselectedColor();
			var ca = frac.selected ? pointer.getColorAlpha() : pointer.getDeselectedColorAlpha();
			var p = pointer.path(pathStr)
			          .stroke({width:pointer.getPieceBorderThickness(),
			        	       color:pointer.getPieceBorderColor()})
			          .dx(cx).dy(cy)
					  .fill({color: c, opacity: ca});

			//records necessary data
			p.data('value',frac.toString());
			pointer.addPieceElem(p,frac.selected);

			//drop shadow
			/*if(dropDistance > 0){
        p.filter(function(add) {
          var adjust = 1;
          var dis = dropDistance - adjust < 0? 0 : dropDistance - adjust;
          var blur = add.offset(dis, dis).in(add.sourceAlpha).gaussianBlur(1);

          add.blend(add.source, blur);

          this.size('200%','200%').move('-50%', '-50%');
        });
      }*/
			sum = sum.add(frac);
			x0 = x2; y0 = y2;//current start position
		}
		pointer.textForward();
	};


	/* typical getters and setters */

	this.getExplode = function(){
		return explode;
	};
	this.getBorderPadding = function(){
		return borderPadding;
	};
	this.getDropShadowDistance = function(){
		return dropDistance;
	};
	this.setExplode = function(aExplode){
		explode = aExplode;
	};
	this.setBorderPadding = function(aPad){
		borderPadding = aPad;
	};
	this.setDropShadowDistance = function(aDrop){
		dropDistance = aDrop;
	};

	/* methods that read info from the brd specific to this component */

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
			}else if(aStyle.styleName == 'explode'){
				pointer.setExplode(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == 'border_padding'){
				pointer.setBorderPadding(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == 'DropShadowDistance'){
				pointer.setDropShadowDistance(parseInt(aStyle.styleValue));
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

CTATPieChart.prototype = Object.create(CTAT.Component.Hierarchy.UnitDisplay.prototype);
CTATPieChart.prototype.constructor = CTATPieChart;

CTAT.ComponentRegistry.addComponentType('CTATPieChart',CTATPieChart);