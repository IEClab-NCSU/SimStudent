/**
 * @fileoverview Defines CTAT.Component.Hierarchy.UnitDisplay which is the base of graphical
 * components that deal with portions of a unit, for example a pie chart.
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/*
 * $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 * $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATUnitDisplayComponent.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:
  Adds html attribute:
    data-ctat-value="<string>" where string specifies the value (default="1/2+1/4+(0*1/8)").
 */
goog.provide('CTAT.Component.Base.UnitDisplay');

goog.require('CTATGlobalFunctions');
goog.require('CTAT.Math');
goog.require('CTAT.Math.Fraction');
goog.require('CTAT.Component.Base.SVG');
goog.require('CTAT.Component.Base.Tutorable');
/**
 * Creates a new component that displays some graphical representation of pieces
 * of a unit.
 * @class Parent class of components that consist of data elements that sum to
 *   one (hence unit). Has a sense of "value" which is the sum of all selected
 *   pieces.
 * @augments CTAT.Component.Hierarchy.SVG
 */
CTAT.Component.Base.UnitDisplay = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.SVG.call(this,
			aClassName,
			aName,
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	/** {String} */
	var _value = "1/2+1/4+(0*1/8)";
	this.setActionInput('SetPieces',_value);

	var color = "#800080";
	this.color = color;
	
	var colorAlpha = 1;
	this.colorAlpha = colorAlpha;
	
	var deselectedColor = '#800080';
	this.deselectedColor = deselectedColor;
	
	var deselectedColorAlpha = 0.3;
	this.deselectedColorAlpha = deselectedColorAlpha;
	
	var piece_border_color = "black";
	this.piece_border_color = piece_border_color;
	
	var piece_border_thickness = 1;
	this.piece_border_thickness = piece_border_thickness;
	
	this.getPieceBorderColor = function() { return piece_border_color; };
	this.setPieceBorderColor = function(aColor) {
		piece_border_color = CTATGlobalFunctions.formatColor(aColor);
		//if (this.baseGroup) {
			//this.baseGroup.setAttributeNS(null,'stroke',piece_border_color);
		//}
		return this;
	};

	//this.setStyleHandler('piece_border_color',this.setPieceBorderColor);
	//this.setStyleHandler('piece_border_thickness',this.setPieceBorderThickness);

	var _pieces = []; // maintain internal reference list for speed.
	var pieceClass = "UnitPart";

	var pointer = this;

	this.generateClassname = function() {
		return this.getName() + ' ' + pieceClass;
	};
	this.getPieces = function() {
		return _pieces; //document.getElementsByClassName(this.generateClassname());
	};
	var is_selected = function(piece) {
		if (piece.hasAttributeNS(null,'data-selected')) {
			return CTATGlobalFunctions.toBoolean(piece.getAttributeNS(null,'data-selected'));
		} else {
			piece.setAttributeNS(null,'data-selected',true);
			return true;
		}
	};
	this.getPiecesBySelected = function(status) {
		var pieces = this.getPieces();
		var selected = [];
		var piece;
		for (var i=0; i<pieces.length; i++) {
			piece = pieces[i];
			if (is_selected(piece)===status) {
				selected.push(piece);
			}
		}
		return selected;
	};
	this.getSelectedPieces = this.getPiecesBySelected.bind(this,true);
	this.getDeselectedPieces = this.getPiecesBySelected.bind(this,false);
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
		color = CTATGlobalFunctions.formatColor(aColor);
		this.color = color;
		var pieces = this.getSelectedPieces();
		for (var i = 0; i<pieces.length; i++) {
			//pieces[i].setAttributeNS(null,'fill',color);
			$(pieces[i]).css('fill', color);
		}
	};
	//this.setStyleHandler('color',this.setColor);
	/**
	 * Sets the alpha value of active/selected pieces.
	 * @argument {Number} anAlpha 	A numeric value [0, 1]
	 */
	this.setColorAlpha = function(anAlpha){
		colorAlpha = Number(anAlpha);
		colorAlpha = isNaN(colorAlpha)?1:colorAlpha;
		this.colorAlpha = colorAlpha;
		var pieces = this.getSelectedPieces();
		for (var i = 0; i<pieces.length; i++) {
			//pieces[i].setAttributeNS(null,'fill-opacity',colorAlpha);
			$(pieces[i]).css('fill-opacity', colorAlpha);
		}
	};
	//this.setStyleHandler('color_alpha',this.setColorAlpha);

	this.setDeselectedColor = function(aColor){
		deselectedColor = CTATGlobalFunctions.formatColor(aColor);
		this.deselectedColor = deselectedColor;
		var pieces = this.getDeselectedPieces();
		for (var i = 0; i<pieces.length; i++) {
			//pieces[i].setAttributeNS(null,'fill',deselectedColor);
			$(pieces[i]).css('fill', color);
		}
	};
	//this.setStyleHandler('deselected_color',this.setDeselectedColor);

	this.setDeselectedColorAlpha = function(aAlpha){
		deselectedColorAlpha = Number(aAlpha);
		if (isNaN(deselectedColorAlpha)) deselectedColorAlpha = 0.3;
		this.deselectedColorAlpha = deselectedColorAlpha;
		var pieces = this.getDeselectedPieces();
		for (var i = 0; i<pieces.length; i++) {
			//pieces[i].setAttributeNS(null,'fill-opacity',deselectedColorAlpha);
			$(pieces[i]).css('fill-opacity', deselectedColorAlpha);
		}
	};
	//this.setStyleHandler('unselected_color_alpha',this.setDeselectedColorAlpha);

	this.valueOf = function() {
		return this.evaluate();
	};
	this.getValue = function(){
		return _value;
	};

	this.setValue = function(aVal) {
		_value = String(aVal); // in case of number conversion
		this.setInput(_value);
		this.clear();
		this.drawPieces();
		return _value;
	};
	this.setParameterHandler('Value',this.setValue);
	this.data_ctat_handlers['value'] = function (val) { this.setValue(val); };

	this.drawPieces = function(){};
	//evaluates the component by summing all selected pieces
	this.evaluate = function(){
		var values = this.parseValue();
		var val = values.reduce(function(sum,frac) {
			return frac.selected?sum.add(frac):sum;
		},new CTAT.Math.Fraction());
		return val;
	};
	//gets the number of active (selected) pieces
	this.numActive = function(){
		return this.getSelectedPieces().length;
	};
	//returns an array of CTAT.Math.Fractions corresponding to the value string, along
	//with information inside the objects about whether or not they are selected
	this.parseValue = function() {
		//console.log(this.getName()+".parseValue() "+value);
		var arr0 = _value.split("+");
		var arr1 = [];
		arr0.forEach(function(str) {
			var sel = true;
			if (str.search(/(\(\s*0\s*\*)|(\*\s*0\s*\))/) != -1) {
				sel = false;
				str = str.replace(/(\(\s*0\s*\*)|(\*\s*0\s*\))/,''); // remove deselected markers
				str = str.replace(/[() ]/g,''); // remove parentheses and space
			}
			var frac = new CTAT.Math.Fraction(str);
			frac.selected = sel;
			if(frac>0) arr1.push(frac); // check for garbage values.
			return frac;
		});
		return arr1;
	};
	/**
	 * Update the value string based on the data-selected attribute of each pieces
	 * @returns {String}
	 */
	this.updateValue = function() {
		var pieces = this.getPieces();
		var values = pieces.map(function(piece) {
			return genOutValue(piece.getAttributeNS(null,'data-value'),
					CTATGlobalFunctions.toBoolean(piece.getAttributeNS(null,'data-selected')));
		});
		_value = values.join('+');
		return _value;
	};
	var set_selected = function(target, p_selected) {
		var sel = CTATGlobalFunctions.toBoolean(p_selected);
		var tsel = CTATGlobalFunctions.toBoolean(target.getAttributeNS(null,'data-selected'));
		/*if (sel===tsel) {
			return; // no change
		} else*/ 
		if(sel===true) 
		{ // TODO: change to class using css
			target.setAttributeNS(null,'data-selected',true);
			$(target).css('fill', pointer.color);
			$(target).css('fill-opacity', pointer.colorAlpha);
		} 
		else 
		{
			$(target).css('fill', pointer.deselectedColor);
			$(target).css('fill-opacity', pointer.deselectedColorAlpha);
			target.setAttributeNS(null,'data-selected',false);
		}
	};
	this.clickHandler = function (event) {
		if (pointer.getEnabled()===true) {
			var target = this;//event.getTarget();
			var selected = CTATGlobalFunctions.toBoolean(target.getAttributeNS(null,'data-selected'));
			set_selected(target,!selected);
			pointer.setActionInput('SetPieces',pointer.updateValue());
			pointer.processAction();
		}
	};
	this.updateSAI = function() {
		pointer.setActionInput('SetPieces',pointer.updateValue());
	};
	//add a piece element
	this.addPieceElem = function(piece,frac,selected){
		//piece.setAttributeNS(null,'class',this.generateClassname()); // internal array used instead, no name maintenance needed
		piece.setAttributeNS(null,'data-selected',selected);
		piece.setAttributeNS(null,'data-value',frac.toString()); // FIXME: this should probably be removed to avoid abuse
		piece.dataValue = frac;
		if (this.getEnabled())
			piece.style.cursor = 'pointer';

		piece.addEventListener("click",this.clickHandler);
		_pieces.push(piece);
	};
	var super_setEnabled = this.setEnabled;
	this.setEnabled = function(pEnabled) {
		super_setEnabled(pEnabled);
		_pieces.forEach(function(piece) {
			piece.style.cursor = pEnabled?'pointer':'default';
		});
	};

	this.clear = function(){
		_pieces = [];
	};

	var genOutValue = function(val,selected) {
		var out = val.toString();
		if (selected === false) {
			out = '(0*'+out+')';
		}
		return out;
	};
	/**
	 * An Interface Action to add a piece of the given size to the component.
	 * @param {string|number|CTAT.Math.Fraction} str
	 * @param {boolean|null} [selected=true]
	 *
	 */
	this.AddPiece = function(str,selected) {
		if (str) {
			var val = new CTAT.Math.Fraction(str); // constructor handles most types
			if (CTAT.Math.round(val.valueOf()) > 0) { // do not add empty or unparsable values
				var va = _value.split('+');
				va.push(genOutValue(val,selected));
				pointer.setValue(va.join('+'));
			}
		}
	};
	/**
	 * Adds a piece of the given value to the display.
	 * @param {String|Number|CTAT.Math.Fraction} val
	 * @param {Boolean} selected
	 * @returns {String} the new string representation of the value of the component.
	 */
	this.AddToValue = function(val,selected) {
		if (val) {
			var frac = new CTAT.Math.Fraction(val);
			if (CTAT.Math.round(frac.valueOf()) > 0) {
				var va = _value.split('+');
				va.push(genOutValue(frac,selected));
				_value = va.join('+');
			}
		}
		return _value;
	};

	var _max_denominator = -1;
	var check_bounds = function(denominator) {
		if (isNaN(denominator)) this.get_denominator();
		denominator = Math.max(denominator,1);
		if (_max_denominator > 1)
			denominator = Math.min(denominator,_max_denominator);
		return denominator;
	}.bind(this);
	/**
	 * Sets the maximum possible denominator
	 * @param {Number} max_denom
	 */
	this.set_max_denominator = function(max_denom) {
		max_denom = parseInt(max_denom);
		if (!isNaN(max_denom)) {
			_max_denominator = max_denom;
		}
	};
	/**
	 * An Interface Action for setting the denominator.  This will preserve the
	 * value displayed while making 1/denominator sized pieces except for any
	 * remainder pieces needed to preserve the value.
	 * @param {string|number} aDenominator
	 * @returns {String} the resulting string representation of the value of
	 *  the component after the denominator has been changed.
	 */
	this.set_denominator = function(aDenominator) {
		//Ported from AS3.
		//Note that fraction.subtract/add/multiply/divide will modify the fraction
		//in place here but not in AS3
		pointer.setHintHighlight(false);
		var denominator = (typeof(aDenominator)==='string')?parseInt(aDenominator):aDenominator;
		if (isNaN(denominator)) return; // abort if invalid
		// bounds checking
		denominator = check_bounds(denominator);
		/*denominator = Math.max(denominator,1);
		if (_max_denominator > 1)
			denominator = Math.min(d,_max_denominator);*/
		if (this.get_denominator() == aDenominator) return; // abort if no change

		var currentValue = pointer.evaluate();
		pointer.clear();
		//pointer.setValue("");
		var values = [];
		currentValue.set_denominator(denominator);
		var pieceValue = new CTAT.Math.Fraction(1,denominator);
		var fullPieces = Math.floor(currentValue.numerator);
		var emptyPieces = denominator - fullPieces;
		for(var i = 0; i < fullPieces; i++){
			values.push(genOutValue(pieceValue,true));
		}
		var remainder = new CTAT.Math.Fraction(currentValue.numerator - fullPieces, denominator);
		if (!remainder.equals(0)) {
			values.push(genOutValue(remainder,true));
			values.push(genOutValue(pieceValue.subtract(remainder),false));
			emptyPieces--;
		}
		for(var j = 0; j < emptyPieces; j++){
			values.push(genOutValue(pieceValue,false));
		}
		this.setValue(values.join('+'));
		pointer.setActionInput('SetPieces',_value);
		pointer.processAction();
		return _value;
	};
	this.get_denominator = function() { // FIXME: lower border condition problem
		return _pieces.reduce(function(min, piece, index, array) {
			return Math.min(min,piece.dataValue.denominator);
		},Infinity);
	};
	this.change_denominator = function(delta) {
		return this.set_denominator(this.get_denominator() + Number(delta));
	};

	/**
	 * An Interface Action for setting the number of equally sized pieces while
	 * preserving the number of selected pieces.
	 * @param {string|number} numPieces
	 * @returns {String} the string representation of the value of the component
	 *  after the number of pieces has been set to the given value.
	 */
	this.set_number_pieces=function(numPieces){
		pointer.setHintHighlight(false);
		var num = (typeof(numPieces)==='string')?parseInt(numPieces):numPieces;
		if (isNaN(num)) return;
		num = check_bounds(num);
		var selected = pointer.numActive();
		pointer.clear();
		var values = [];
		var pieceValue = new CTAT.Math.Fraction(1,num);
		for(var i = 0; i < num; i++){
			values.push(genOutValue(pieceValue,selected>0));
			selected--;
		}
		this.setValue(values.join('+'));
		pointer.setActionInput('SetPieces',_value);
		pointer.processAction();
		return _value;
	};
	this.change_number_pieces = function(delta) {
		this.set_number_pieces(_pieces.length+delta);
	};
	/**
	 * Interface action for setting the number of active pieces.
	 */
	this.set_numerator = function(aNum) {
		return this.change_numerator(aNum - this.getSelectedPieces().length);
	};
	this.change_numerator = function change_numerator(delta) {
		//console.log('change_numerator '+delta);
		if (delta === 0) {
			return; // no change
		} else if (delta>0) {
			// need to increase the number of active
			_pieces.every(function(piece) {
				if (is_selected(piece)===false) {
					set_selected(piece,true);
					delta--;
				}
				return delta>0;
			});
		} else {
			// reduce the number of selected pieces
			_pieces.reverse();
			_pieces.every(function(piece) {
				if (is_selected(piece)===true) {
					set_selected(piece,false);
					delta++;
				}
				return delta<0;
			});
			_pieces.reverse();
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

	var get_ctrl = function(type) {
		var ctrls = $(this.getDivWrap()).attr(type);
		if (ctrls) return ctrls.split(/\s*;\s*/).map(i=>i.trim());
		else return [];
	};
	var get_ctrl_denom = get_ctrl.bind(this,'data-ctat-ctrl-denominator');
	var get_ctrl_part = get_ctrl.bind(this,'data-ctat-ctrl-partition');
	var get_ctrl_numer = get_ctrl.bind(this,'data-ctat-ctrl-numerator');
	//var get_ctrl_value = get_ctrl.bind(this,'data-ctat-ctrl-value');

	var isa = function(getctrl, id) {
		return getctrl().indexOf(id)>=0;
	};
	var isa_denom = isa.bind(null,get_ctrl_denom);
	var isa_part = isa.bind(null,get_ctrl_part);
	var isa_numer = isa.bind(null,get_ctrl_numer);
	//var isa_value = isa.bind(null,get_ctrl_value);

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
						this.set_denominator);
			} else if (isa_numer(ctrl_name)) {
				return controller_update.bind(this,
						this.change_numerator,
						this.set_numerator);
			} else if (isa_part(ctrl_name)) {
				return controller_update.bind(this,
						this.change_number_pieces,
						this.set_number_pieces);
			}
		}
		//console.log('isController fallthrough');
		return null;
	}.bind(this);
	var event_type = CTAT.Component.Base.Tutorable.EventType;
	if (!CTATConfiguration.get('previewMode'))
	{
		document.addEventListener(event_type.action, function(e) {
			//if (e.detail.hasOwnProperty('sai')) { // assume correct type
			//if (e.detail.hasOwnProperty('component')) { // assume correct type
			var sai = e.detail.sai;
			var ctrl = isController(e.detail.component);
			//console.log('CTATUnitDisplay.onAction: '+e.detail.component.getName());
			//console.log(sai.getSelection()+", "+sai.getAction()+", "+sai.getInput());
			if (sai && ctrl!==null) {
				//console.log(sai.getSelection()+", "+sai.getAction()+", "+sai.getInput()+' '+ctrl);
				ctrl(sai);
			}
		}, false);
	}
	var controller_update = function(change_callback,set_callback,sai) {
		var input = parseInt(sai.getInput());
		if (!isNaN(input)) {
			switch (sai.getAction()) {
			case "ButtonPressed":
				change_callback.call(this,input);
				break;
			case "Update":
			case "UpdateTextField":
			case "UpdateTextArea":
				set_callback.call(this,input);
				break;
			default:
				break;
			}
			this.updateSAI();
			this.processAction();
		}
	};
	var update_value = function(sai) { // TODO: figure out if needed
		switch (sai.getAction()) {
		case "SetPieces":
		case "UpdateTextField":
		case "UpdateTextArea":
			pointer.setValue(sai.getInput());
			break;
		default:
			break;
		}
	};

	this.setNumeratorControllers = function(controllers) {
		this.getDivWrap().setAttribute('data-ctat-ctrl-numerator',controllers);
		return this;
	};
	this.setParameterHandler('NumeratorControllers',this.setNumeratorControllers);
	//this.data_ctat_handlers['ctrl-numerator'] = this.setNumeratorControllers;

	this.setDenominatorControllers = function(controllers) {
		this.getDivWrap().setAttribute('data-ctat-ctrl-denominator',controllers);
		return this;
	};
	this.setParameterHandler('DenominatorControllers',this.setDenominatorControllers);
	//this.data_ctat_handlers['ctrl-denominator'] = this.setDenominatorControllers;

	this.setPartitionControllers = function(controllers) {
		this.getDivWrap().setAttribute('data-ctat-ctrl-partition',controllers);
		return this;
	};
	this.setParameterHandler('PartitionControllers',this.setPartitionControllers);
	//this.data_ctat_handlers['ctrl-partition'] = this.setPartitionControllers;

	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var val = this.getDivWrap().getAttribute('data-ctat-value');
		if (val) { // if explicitly set by author
			var sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('SetPieces');
			sai.setInput(val);
			return [sai];
		} else { // get current internal value
			this.updateSAI();
			return [this.getSAI()];
		}
	};
};

CTAT.Component.Base.UnitDisplay.prototype = Object.create(CTAT.Component.Base.SVG.prototype);
CTAT.Component.Base.UnitDisplay.prototype.constructor = CTAT.Component.Base.UnitDisplay;