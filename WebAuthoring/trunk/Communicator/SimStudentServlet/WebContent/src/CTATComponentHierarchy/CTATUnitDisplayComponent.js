/**
 * @fileoverview Defines CTAT.Component.Hierarchy.UnitDisplay which is the base of graphical
 * components that deal with portions of a unit, for example a pie chart.
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/*
 * $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 * $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATUnitDisplayComponent.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTAT.Component.Hierarchy.UnitDisplay');

goog.require('CTAT.Math');
goog.require('CTAT.Math.Fraction');
goog.require('CTAT.Component.Hierarchy.SVG');
/**
 * Creates a new component that displays some graphical representation of pieces
 * of a unit.
 * @class Parent class of components that consist of data elements that sum to
 *   one (hence unit). Has a sense of "value" which is the sum of all selected
 *   pieces.
 * @augments CTAT.Component.Hierarchy.SVG
 */
CTAT.Component.Hierarchy.UnitDisplay = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Hierarchy.SVG.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	var value = "1/2+1/4+(0*1/8)";
	//all elements are such that pieceElems.data("deselected") returns true or false
	var pieceElems = [];

	var color = "#FF0";
	var colorAlpha = 1;
	var deselectedColor = '#FF0';
	var deselectedColorAlpha = 0.2;
	var piece_border_color = "black";
	var piece_border_thickness = 1;
	this.getPieceBorderColor = function() { return piece_border_color; };
	this.getPieceBorderThickness = function() { return piece_border_thickness; };

	/**
	 * The main active color for pieces.
	 * @returns a color specification
	 */
	this.getColor = function(){
		return color;
	};
	/**
	 * The alpha value for active/selected pieces.
	 * @returns {Number} a value from [0, 1]
	 */
	this.getColorAlpha = function(){
		return colorAlpha;
	};
	/**
	 * The color for inactive/unselected pieces.
	 * @returns a color specification
	 */
	this.getDeselectedColor = function(){
		return deselectedColor;
	};
	/**
	 * The alpha value for inactive/unselected pieces.
	 * @returns {Number} an alpha value from [0, 1]
	 */
	this.getDeselectedColorAlpha = function(){
		return deselectedColorAlpha;
	};
	/**
	 * Sets the color of active/selected pieces.
	 * @argument {Color} aColor
	 */
	this.setColor = function(aColor){
		color = aColor;
		this.render();
	};
	/**
	 * Sets the alpha value of active/selected pieces.
	 * @argument {Number} anAlpha 	A numeric value [0, 1]
	 */
	this.setColorAlpha = function(anAlpha){
		colorAlpha = anAlpha;
	};
	this.setDeselectedColor = function(aColor){
		deselectedColor = aColor;
	};
	this.setDeselectedColorAlpha = function(aAlpha){
		deselectedColorAlpha = aAlpha;
	};

	this.setActionInput('SetPieces',value);

	var pointer = this;

	this.valueOf = function() {
		return this.evaluate();
	};
	this.getValue = function(){
		return value;
	};

	this.setValue = function(aVal){
		value = aVal;
		pieceElems = [];
		pointer.render();
	};
	//evaluates the component by summing all selected pieces
	this.evaluate = function(){
		//check that value is ok before using eval, since eval is evil
		var regex = /\d+\/\d+|\(0\*\d+\/\d+\)/; //regex for single fraction
			//check each fraction individually since the code is easier to read than a single regex
			for(var i = 0; i < value.split("+"); i++){
				if(!regex.text(value.split("+"))){//uh oh
					alert("Invalid value string. Aborting eval.");
					return;
				}
			}
		return eval(value);
	};
	//gets the number of active (selected) pieces
	this.numActive = function(){
		var arr = value.split("+");
		var count = 0;
		for(var i = 0; i < arr.length; i++){
			if(arr[i].indexOf("(0*")<0){
				count++;
			}
		}
		return count;
	};
	//returns an array of CTAT.Math.Fractions corresponding to the value string, along
	//with information inside the objects about whether or not they are selected
	this.parseValue = function(){
		var arr0 = value.split("+");
		var arr1 = [];
		for(var i = 0; i < arr0.length; i++){
			var str = arr0[i];
			var sel = true;
			if(str.indexOf("(0*")>-1){
				str = str.substring(3,str.length-1);
				sel = false;
			}
			var frac = new CTAT.Math.Fraction(str);
			frac.selected=sel;
			if(frac > 0){
				arr1.push(frac);
			}
		}
		return arr1;
	};
	//update the value string based on the selected or deselected pieces
	this.updateValue = function(){
		value = "";
		for(var i = 0; i < pieceElems.length; i++){
			if(pieceElems[i].data('selected')){
				value += "+" + pieceElems[i].data('value');
			}else{
				value += "+(0*" + pieceElems[i].data('value') + ")";
			}
		}
		value = value.substring(1);
		pointer.setActionInput('SetPieces',value);

		//commShell.gradeComponent (pointer);
	};
	//add a piece element
	this.addPieceElem = function(piece,selected){
		piece.data('selected',selected);
		piece.click(pointer.clickHandler);
		pieceElems.push(piece);
		pointer.updateValue();
	};

	//when clearing, also clear pieceElems
	var oldClear = pointer.clear;
	this.clear = function(){
		oldClear();
		pieceElems = [];
	};

	//to show correct/incorrect we highlight the entire div
	/*this.showCorrect = function(){
		var str = "0px 0px 15px 5px rgba(0, 255,0, 1.0)";
		pointer.getDivWrap().style['-webkit-box-shadow'] = str;
		pointer.getDivWrap().style['-moz-box-shadow'] = str;
		pointer.getDivWrap().style['box-shadow'] = str;
	}
	this.showInCorrect = function(){
		var str = "0px 0px 15px 5px rgba(255, 0, 0, 1.0)";
		pointer.getDivWrap().style['-webkit-box-shadow'] = str;
		pointer.getDivWrap().style['-moz-box-shadow'] = str;
		pointer.getDivWrap().style['box-shadow'] = str;
	}*/

	/* method that reads info from the brd specific to this type of component */
	this.processParams = function(){
		if(pointer.getGrDescription() === null){
			pointer.ctatdebug ("Error: no deserialized component description available");
			return;
		}
		pointer.parameters = pointer.getGrDescription().params;
		if(!pointer.parameters) return;

		for(var i = 0; i < pointer.parameters.length; i++){
			var aParam = pointer.parameters[i];

			if(aParam.paramName == 'NumeratorControllers'){
				//TODO
			}else if(aParam.paramName == 'DenominatorControllers'){
				//TODO
			}else if(aParam.paramName == 'PartitionControllers'){
				//TODO
			}else if(aParam.paramName == 'Value'){
				pointer.setValue(aParam.paramValue);
			}
		}
	};

	/**
	 * An Interface Action to add a piece of the given size to the component.
	 * @param {string|number|CTAT.Math.Fraction} str
	 * @param {boolean|null} [selected=true]
	 *
	 */
	this.AddPiece = function(str,selected) {
		if (str) {
			var out = pointer.getValue()+'+';
			if (selected === false) out+='(0*';//if selected is not passed as an arg, we default to true
			var val = new CTAT.Math.Fraction(str); // constructor handles most types
			out+=val.toString();
			if (selected === false) out+=')';
			if (CTAT.Math.round(val.valueOf()) > 0) { // do not add empty or unparsable values
				pointer.setValue(out);
			}
		}
	};

	/**
	 * An Interface Action for setting the denominator.  This will preserve the
	 * value displayed while making 1/denominator sized pieces except for any
	 * remainder pieces needed to preserve the value.
	 * @param {string|number} aDenominator
	 */
	this.set_denominator = function(aDenominator) {
		//Ported from AS3.
		//Note that fraction.subtract/add/multiply/divide will modify the fraction
		//in place here but not in AS3
		pointer.setHintHighlight(false);
		var currentValue = new CTAT.Math.Fraction(pointer.evaluate());
		var denominator = (typeof(aDenominator)==='string')?parseInt(aDenominator):aDenominator;
		if (isNaN(denominator)) return;
		pointer.clear();
		pointer.setValue("");
		currentValue.set_denominator(denominator);
		var pieceValue = new CTAT.Math.Fraction(1,denominator);
		var fullPieces = Math.floor(currentValue.numerator);
		var emptyPieces = denominator - fullPieces;
		for(var i = 0; i < fullPieces; i++){
			pointer.AddPiece(pieceValue,true);
		}
		var remainder = new CTAT.Math.Fraction(currentValue.numerator - fullPieces, denominator);
		if (remainder.equals(0)) {
			pointer.AddPiece(remainder,true);
			pointer.AddPiece(pieceValue.subtract(remainder),false);
			emptyPieces--;
		}
		for(var j = 0; j < emptyPieces; j++){
			pointer.AddPiece(pieceValue,false);
		}
	};
	/**
	 * An Interface Action for setting the number of equally sized pieces while
	 * preserving the number of selected pieces.
	 * @param {string|number} numPieces
	 */
	this.set_number_pieces=function(numPieces){
		var num = (typeof(numPieces)==='string')?parseInt(numPieces):numPieces;
		if (isNaN(num)) return;
		pointer.setHintHighlight(false);
		var selected = pointer.numActive();
		pointer.clear();
		pointer.setValue("");
		var pieceValue = new CTAT.Math.Fraction(1,num);
		for(var i = 0; i < num; i++){
			pointer.AddPiece(pieceValue,selected>0);
			selected--;
		}
	};
	/**
	 * An Interface Action for setting the values of the pieces.
	 * @param {string} str 	A string representation of a series of fractions
	 * @example foo.SetPieces('1/2+1/4+(0*1/8)');
	 */
	this.SetPieces = function(str){
		pointer.setValue(str);
	};
	//selects/deselects fraction piece when clicked
	this.clickHandler = function(){
		//"this" refers to a piece
		if (pointer.getEnabled()){
			var selected = !this.data('selected');
			this.data('selected',selected);
			var c = selected ? color : deselectedColor;
			var a = selected ? colorAlpha : deselectedColorAlpha;
			this.fill({color: c, opacity: a});
			pointer.updateValue();
			pointer.grade();
		}
	};
};

CTAT.Component.Hierarchy.UnitDisplay.prototype = Object.create(CTAT.Component.Hierarchy.SVG.prototype);
CTAT.Component.Hierarchy.UnitDisplay.prototype.constructor = CTAT.Component.Hierarchy.UnitDisplay;