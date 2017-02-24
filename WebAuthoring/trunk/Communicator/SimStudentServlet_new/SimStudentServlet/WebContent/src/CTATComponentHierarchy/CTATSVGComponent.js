/**
 * @fileoverview Uses svg graphics instead of a canvas for drawing.
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/*
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATSVGComponent.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTAT.Component.Hierarchy.SVG');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');

/**
 * Creates a new component using svg for drawing.
 * @class
 * Uses SVG instead of Canvas. See:
 * http://documentup.com/wout/svg.js
 * https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Basic_Shapes
 * https://developer.mozilla.org/en-US/docs/Web/SVG/Element#Basic_shapes
 *
 * Prototype for all complex components. Overrides all appropriate methods.
 * Every component has a border (what the user will see) and a base (the actual
 * rectangular area covered by the component). Both are the same width and
 * height as the surrounding SVG, which is the same width and height as the
 * surrounding div. Also contains methods to name elements when creating them.
 * @augments CTATTutorableComponent
 */
CTAT.Component.Hierarchy.SVG = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTATTutorableComponent.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var compDoc;//SVG document
	var baseElem;//rectangular base
	var maskElem;
	var map = {};//name to elements map
	var textSet;//contains text elements to make changing, e.g. font, easier
	var initialized;
	var labelText = '';
	var textAlign = 'left';
	var textElem;//label text

	var transparency = 1;
	var precision = 3;//number of places to round decimals to
	var isBase = false;
	var pointer = this;

	//initialize the SVG to place this component in. must always come first
	this.createSVG = function(){
		compDoc = SVG(pointer.getDivWrap().id).size('100%','100%');
		textSet = compDoc.set();
		pointer.setComponent(compDoc);
	};

	/**
	 * Creates a base
	 */
	//create the border and base rects as well as the label text.
	this.createBorderBase = function(){
		//border/base
		baseElem = compDoc.rect('100%', '100%');
		baseElem.fill({color: pointer.getBackgroundColor(), opacity: isBase?transparency:0});
		baseElem.stroke({color: pointer.getBorderColor(), width: isBase?1:0, opacity: 1});
		baseElem.radius(pointer.getBorderRoundness());

		maskElem = compDoc.clip();
		maskElem.add(compDoc.rect('100%','100%').radius(pointer.getBorderRoundness()));
		//baseElem.clipWith(maskElem);
		//var d = compDoc.defs();

		//write text
		textElem = pointer.text(labelText,'label');
		pointer.alignText();
	};

	/* typical getters and setters */
	this.getSVG = function(){
		return compDoc;
	};
	this.getElem = function(name){
		return map[name];
	};
	this.removeElem = function(name){
		map[name].remove();
		map[name] = null;
	};
	this.getAttr = function(name,attr){
		if(!attr) return map[name].attr();
		else return map[name].attr(attr);
	};
	/*this.getBorderElem = function(){
		return borderElem;
	}*/
	this.getTextElem = function(){
		return textElem;
	};
	this.getPrecision = function(){
		return precision;
	};
	/*this.setBorderElem = function(aElement){
		borderElem = aElement;
	}*/
	this.setPrecision = function(aPrec){
		precision = aPrec;
	};
	this.setAlign=function(aTextAlign){
		textAlign=aTextAlign;
		pointer.alignText();
	};
	this.setLabelText=function(newText){
		labelText=newText;
		textElem.text(labelText);
	};

	//added so that label isn't buried underneath other component
	this.textForward=function(){
		textElem.front();
	};
	//places label in appropriate place
	this.alignText = function(){
		var len;
		if(textAlign == 'left'){
			textElem.x(3);
		}else if(textAlign == 'right'){
			len = textElem.length();
			textElem.x(pointer.getWidth()-len-3);
		}
		else if(textAlign == 'center'){
			len = textElem.length();
			textElem.x(pointer.getWidth()/2-len/2);
		}
	};
	//clears document
	this.clear = function(){
		compDoc.clear();
		map = {};
	};

	/* convenience methods to create SVG elements and also to name them */
	this.rect = function(width,height,name){
		//var clp = "url(#"+maskElem.parent.attr('id')+")";
		var rect = compDoc.rect(width,height);
		//rect.attr('clip-path',clp);
		rect.clipWith(maskElem);
		if(!name) name = "rect"+compDoc.index(rect);
		map[name] = rect;
		return map[name];
	};

	this.ellipse = function(rx,ry,name){
		var ellipse = compDoc.ellipse(rx,ry);
		if(!name) name = "ellipse"+compDoc.index(ellipse);
		map[name]=ellipse;
		return map[name];
	};

	this.circle = function(r,name){
		var circ = compDoc.circle(r);
		if(!name) name = "circle"+compDoc.index(circ);
		map[name]=circ;
		return map[name];
	};

	this.line = function(x1,y1,x2,y2,name){
		var attribs = {};
		attribs.stroke = "black";
		attribs["stroke-width"]=1;
		var line = compDoc.line(x1,y1,x2,y2).attr(attribs);
		if(!name) name = "line"+compDoc.index(line);
		map[name] = line;
		return map[name];
	};

	this.polyline = function(points,name){
		var polyline = compDoc.polyline(points);
		if(!name) name = "polyline"+compDoc.index(polyline);
		map[name] = polyline;
		return map[name];
	};

	this.polygon = function(points,name){
		//if(!attribs) attribs = new Object();
		var polygon = compDoc.polygon(points);
		if(!name) name = "polygon"+compDoc.index(polygon);
		map[name] = polygon;
		return map[name];
	};

	this.path = function(str, name){
		var path = compDoc.path(str);
		if(!name) name = "path"+compDoc.index(path);
		map[name] = path;
		return map[name];
	};

	this.image = function(src,name){
		var image = compDoc.image(src);
		if(!name) name = "image"+compDoc.index(image);
		map[name] = image;
		return map[name];
	};

	this.text = function(str,name){
		var txt = compDoc.text(str).font({size: pointer.getFontSize(), family: pointer.getFontFamily()}).fill({color: pointer.getFontColor()});
		textSet.add(txt);
		if(!name) name = "text"+compDoc.index(txt);
		map[name] = txt;
		return map[name];
	};

	this.group = function(name){
		var group = compDoc.group();
		if(!name) name = "group"+compDoc.index(group);
		map[name] = group;
		return map[name];
	};
	/**
	 * Constructs a fraction by adding text elements to a group. Handles whole numbers
	 * by redirecting to text.
	 * @param {CTAT.Math.Fraction} frac
	 * @param {String} name
	 */
	this.fraction = function(frac,name){
		if (frac.equals(frac.whole_part))
			return pointer.text(frac.toString(),name);
		var font = pointer.getFontSize();
		var num = frac.numerator;
		var den = frac.denominator;

		//use 1 font = 4/3 px conversion
		var scale = 4/3;
		var pixels = font*scale;
		var offset1 = Math.floor(pixels - 1);
		var offset2 = Math.floor(pixels + 1);

		//properly place and style each text element
		var fontObj = {size:font,family: pointer.getFontFamily()};
		var fillObj = {color: pointer.getFontColor()};
		var d = compDoc.text(String(den)).font(fontObj).fill(fillObj);
		var b = compDoc.text("_").font(fontObj).fill(fillObj);
		var n = compDoc.text(String(num)).font(fontObj).fill(fillObj);
		b.dy(-offset1);
		n.dy(-offset2);

		//indicate that they are fractions
		d.isFraction = true;
		b.isFraction = true;
		n.isFraction = true;

		textSet.add(d,b,n);

		var group = pointer.group();
		group.add(d);
		group.add(b);
		group.add(n);
		return group;
	};

	/* Override wrapComponent in CTATCompBase to not include canvas */
	this.wrapComponent=function wrapComponent(topDiv){
		pointer.ctatdebug ("wrapComponent ()");

		pointer.makeDivWrapper(topDiv);
		/*var divWrapper=document.createElement('div');
		divWrapper.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
		divWrapper.setAttribute('name', pointer.getGrDescription().name);
		divWrapper.setAttribute('onkeypress', 'return noenter(event)');
		divWrapper.style.position = 'absolute';
		divWrapper.style.left = pointer.getX()+'px';
		divWrapper.style.top = pointer.getY()+'px';
		divWrapper.style.
		divWrapper.setAttribute('style', 'position: absolute;left:'+pointer.getX()+'px; top:'+pointer.getY()+'px; z-index: '+pointer.getTopDivZIndex()+';width: '+pointer.getWidth()+'px;height: '+pointer.getHeight()+'px;');
		topDiv.appendChild(divWrapper);
		pointer.setdivWrapper(divWrapper);*/

		pointer.ctatdebug ("wrapComponent () done");
	};

	/* Modify methods in compbase so that they affect the correct components (border/base/textSet) */
	var oldSetBGC = pointer.setBackgroundColor;
	this.setBackgroundColor = function(aColor){
		oldSetBGC(aColor,true);
		console.log("nooooo");
		if(baseElem)
			baseElem.fill({color:aColor});
	};

	var oldSetBDC = pointer.setBorderColor;
	this.setBorderColor = function(aColor){
		oldSetBDC(aColor,true);
		console.log("nooooo border color");
		if(baseElem)
			baseElem.stroke({color:aColor});
	};

	var oldSetFC = pointer.setFontColor;
	this.setFontColor = function(aColor){
		oldSetFC(aColor,true);
		textSet.fill({color:aColor});
	};

	var oldSetFF = pointer.setFontFamily;
	this.setFontFamily = function(aFont){
		oldSetFF(aFont,true);
		textSet.each(function(i){
			this.font({family: aFont});//this refers to element i in textSet
		});
	};

	var oldSetFS = pointer.setFontSize;
	this.setFontSize = function(aSize){
		oldSetFS(aSize,true);
		textSet.each(function(i){
			if(pointer.isFraction)
				//pointer.font({size:aSize-2});
				this.font({size: aSize});//this refers to element i in textSet
			else
				this.font({size: aSize});
		});
	};

	var oldSetWidth = pointer.setWidth;
	this.setWidth = function(aWidth){
		oldSetWidth(aWidth,true);
		//compDoc.width(aWidth);
		pointer.render();
	};

	var oldSetHeight = pointer.setHeight;
	this.setHeight = function(aHeight){
		oldSetHeight(aHeight,true);
		//compDoc.height(aHeight);
		pointer.render();
	};

	var oldSetB = pointer.setBolded;
	this.setBolded = function(aBold){
		oldSetB(aBold,true);
		if(aBold == "true"){
			textSet.each(function(i){
				this.style('font-weight','bold');
			});
		}
	};

	var oldSetI = pointer.setItalicized;
	this.setItalicized = function(aItalicized){
		oldSetI(aItalicized,true);
		if(aItalicized == "true"){
			textSet.each(function(i){
				this.style('font-style','italic');
			});
		}
	};

	var oldSetU = pointer.setUnderlined;
	this.setUnderlined = function(aUnderlined){
		oldSetU(aUnderlined,true);
		if(aUnderlined == "true"){
			textSet.each(function(i){
				this.style('text-decoration','underline');
			});
		}
	};

	var oldSetSB = pointer.setShowBorder;
	this.setShowBorder = function(aValue){
		oldSetSB(aValue,true);
		if(!isBase) return;
		if(aValue){
			baseElem.stroke({width:1});
		}else{
			baseElem.stroke({width:0});
		}
	};

	var oldSetBR = pointer.setBorderRoundness;
	this.setBorderRoundness = function(roundness){
		oldSetBR(roundness,true);
		baseElem.radius(roundness);
		//maskElem.radius(roundness);
	};

	this.setTransparency = function(transparencyVal){
		transparency = transparencyVal;
		baseElem.fill({opacity: transparencyVal});
	};

	this.drawBase = function(aBase){
		console.log("drawBase("+aBase+")");
		isBase = aBase;
		if(isBase){
			baseElem.fill({opacity: transparency});
			baseElem.stroke({width:pointer.getShowBorder()?1:0});
		}else{
			baseElem.fill({opacity: 0});
			baseElem.stroke({width:0});
		}
	};

	//modified to affect the div itself
	this.addSafeEventListener = function(aType,aFunction){
		$("#"+pointer.getDivWrap().id).on(aType,aFunction);
	};

	//highlights the div itself instead of any individual element
	var oldSetHintHighlight = pointer.setHintHighlight;
	this.setHintHighlight = function(newValue){
		oldSetHintHighlight(newValue,true);
		/*if(!pointer.getShowHintHighlight())
			return;*/
		var elem = compDoc; //baseElem; //
		if(pointer.getShowHintHighlight() && newValue===true){
			var glow_string = CTATGlobals.Visual.hint_glow;
			/*pointer.getDivWrap().style['-webkit-box-shadow']=globalGlowStringContent;
			pointer.getDivWrap().style['-moz-box-shadow']=globalGlowStringContent;
			pointer.getDivWrap().style['box-shadow']=globalGlowStringContent;*/
			elem.style('-webkit-box-shadow',glow_string);
			elem.style('-moz-box-shadow',glow_string);
			elem.style('box-shadow',glow_string);
		}else{
			/*pointer.getDivWrap().style['-webkit-box-shadow']='';
			pointer.getDivWrap().style['-moz-box-shadow']='';
			pointer.getDivWrap().style['box-shadow']='';*/
			elem.style('-webkit-box-shadow',null);
			elem.style('-moz-box-shadow',null);
			elem.style('box-shadow',null);
		}
		/*alert(pointer.getDivWrap().id + " "+ newValue+" "+globalGlowStringContent+" "+pointer.getDivWrap().style['box-shadow']);*/
	};

	var oldShowCorrect = pointer.showCorrect;
	this.showCorrect = function(input){
		//implement in children
		pointer.ctatdebug("showCorrect("+input+")");

		pointer.setHintHighlight(false);
		if (suppressStudentFeedback===false) {
			var glow = CTATGlobals.Visual.correct_glow;
			var elem = compDoc; //baseElem; //
			elem.style('-webkit-box-shadow',glow);
			elem.style('-moz-box-shadow',glow);
			elem.style('box-shadow',glow);

			//alert(pointer.getDisableOnCorrect());
			if(pointer.getDisableOnCorrect()) {
				pointer.setEnabled(false);
				compDoc.disabled = true;
			}
			//alert(pointer.getComponent().disabled)
		}
	};

	var oldShowInCorrect = pointer.showIncorect;
	this.showInCorrect = function(input){
		//implement in children
		pointer.ctatdebug("showInCorrect("+input+")");
		pointer.setHintHighlight(false);
		if (suppressStudentFeedback===false) {
			var glow = CTATGlobals.Visual.incorrect_glow;
			var elem = compDoc; // baseElem; //
			elem.style('-webkit-box-shadow',glow);
			elem.style('-moz-box-shadow',glow);
			elem.style('box-shadow',glow);
		}
	};

	this.render = function(){
		//implement in children
		//pointer.ctatdebug("render()");
	};
};

CTAT.Component.Hierarchy.SVG.prototype = Object.create(CTATTutorableComponent.prototype);
CTAT.Component.Hierarchy.SVG.prototype.constructor = CTAT.Component.Hierarchy.SVG;
