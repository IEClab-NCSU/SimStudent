/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATJumble.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATJumble');

goog.require('CTAT.Component.Hierarchy.SVG');
goog.require('CTAT.ComponentRegistry');
/*
 * Class that implements CommJumble. Inherits from CTAT.Component.Hierarchy.SVG so
 * it has the usual border and base. Each jumble element is a text element
 * wrapped in a group. This is because moving a text element gives strange behavior,
 * so this is a work around to make animation smoother. They're also made draggable
 * within the bounds of the border.
 */
CTATJumble = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Hierarchy.SVG.call(this,
			"CTATJumble",
			"aJumble",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var textItems = '';
	var alignment = 'center';
	var orientation = CTATJumble.HORIZONTAL;
	var animationSpeed = 0;
	var borderPadding = 3;
	var cellPadding = 10;
	var showMarker = false;

	var pos = [];//array of positions of elements
	var perm = [];//array holding logical order of elements
	var markerElem;//insertion marker element

	var pointer = this;

	pointer.setActionInput("SetOrder",textItems);

	this.init=function(){
		pointer.setInitialized(true);
		pointer.createSVG();
		pointer.render();
		pointer.configFromDescription();
		pointer.processParams();
		pointer.addComponentReference(pointer,pointer.getDivWrap());
	};

	/* Methods used when alignment is horizontal. */
	//returns y coordinate depending on alignment
	var positionH = function(elem){
		if(alignment == 'center'){
			return pointer.getHeight()/2-10;
		}else if(alignment == 'top'){
			return 0;
		}else if(alignment == 'bottom'){
			return pointer.getHeight() - elem.bbox().height;
		}
	};
	//handler for when item is starting to be dragged
	//creates an insertion marker if it needs to
	var onstartH = function(delta,event){
		var i;//get index of this
		for(i = 0; i < perm.length; i++){
			if(perm[i] == this){//this refers to the text item
				break;
			}
		}
		if(showMarker){
			markerElem = pointer.line(this.x(),0,this.x(),pointer.getHeight()).stroke('blue');
			markerElem.data('index0',i);
		}
	};
	//handler for while an item is being dragged
	//moves the insertion marker if it needs to
	var onmoveH = function(delta,event){
		if(!showMarker) return;

		var index0 = markerElem.data('index0');
		var a;
		if(this.cx() < pos[0].cx){//marker before first element
			a = pos[0].x;
		}else if(pos.length > 1 && this.cx() > pos[pos.length-1].cx){//marker after last element
			a = pos[pos.length-1].cx + perm[pos.length-1].length() + cellPadding;
		}else{//marker in between
			for(var i = 0; i < pos.length-1; i++){
				if(pos[i].cx < this.cx() && this.cx() < pos[i+1].cx){
					if(i == index0)
						a = pos[i].x;
					else
						a = pos[i+1].x;
				}
			}
		}
		markerElem.plot(a,0,a,pointer.getHeight());
	};

	//handler for after an item has been dragged
	//moves the items into the correct permutation and removes marker
	var onendH = function(delta,event){
		//remove this (the text item) from perm
		var i;
		for(i = 0; i < perm.length; i++){
			if(perm[i] == this){
				perm.splice(i,1);
				break;
			}
		}
		//add back in to appropriate index
		i = 0;
		while(i < perm.length && perm[i].cx() < this.cx()){
			i++;
		}
		perm.splice(i,0,this);

		//update textItems
		var a = "";
		for(i = 0; i < perm.length; i++){
			a+=perm[i].text()+";";
		}
		textItems = a;
		pointer.setInput(textItems);

		//recalculate positions
		var x = borderPadding;
		pos = [];
		for(i = 0; i < perm.length; i++){
			pos.push({x:x,cx:x+perm[i].length()/2});
			x+=perm[i].length()+cellPadding;
		}

		//move perm[i] to pos[i]'s x coordinate
		for(i = 0; i < pos.length;i++){
			if(perm[i].x() != pos[i].x || perm[i].y() != positionH(perm[i])){
				perm[i].animate(animationSpeed * 1000).move(pos[i].x,positionH(perm[i]));
			}
		}
		if(showMarker){
			markerElem.remove();
			markerElem = null;
		}

		pointer.grade();
	};

	/* methods used when alignment is vertical. Similar to horizontal. */
	//returns x coordinate depending on alignment
	var positionV = function(elem){
		if(alignment == 'center'){
			return pointer.getWidth()/2-3;
		}else if(alignment == 'top'){//interpret as left for now
			return 5;//some padding
		}else if(alignment == 'bottom'){//interpret as right for now
			return pointer.getWidth() - elem.length()-5;
		}
	};

	//handler for when item is starting to be dragged
	//creates an insertion marker if it needs to
	var onstartV = function(delta,event){
		var i;//get index of this
		for(i = 0; i < perm.length; i++){
			if(perm[i] == this){//this refers to the text item
				break;
			}
		}
		if(showMarker){
			markerElem = pointer.line(0,this.y(),pointer.getWidth(),this.y()).stroke('blue');
			markerElem.data('index0',i);
		}
	};
	//handler for while an item is being dragged
	//moves the insertion marker if it needs to
	var onmoveV = function(delta,event){
		if(!showMarker) return;

		var index0 = markerElem.data('index0');
		var a;
		if(this.cy() < pos[0].cy){//marker before first element
			a = pos[0].y;
		}else if(pos.length > 1 && this.cy() > pos[pos.length-1].cy){//marker after last element
			a = pos[pos.length-1].cx + perm[pos.length-1].length() + cellPadding;
		}else{//marker in between
			for(var i = 0; i < pos.length-1; i++){
				if(pos[i].cy < this.cy() && this.cy() < pos[i+1].cy){
					if(i == index0)//original space
						a = pos[i].y;
					else
						a = pos[i+1].y;
				}
			}
		}
		markerElem.plot(0,a,pointer.getWidth(),a);
	};
	//handler for after an item has been dragged
	//moves the items into the correct permutation and removes marker
	var onendV = function(delta,event){
		//remove this (the text item) from perm
		var i;
		for(i = 0; i < perm.length; i++){
			if(perm[i] == this){
				perm.splice(i,1);
				break;
			}
		}
		//add back in to appropriate index
		i = 0;
		while(i < perm.length && perm[i].cy() < this.cy()){
			i++;
		}
		perm.splice(i,0,this);

		//update textItems
		var a = "";
		for(i = 0; i < perm.length; i++){
			a+=perm[i].text()+";";
		}
		textItems = a;
		pointer.setInput(textItems);

		//recalculate positions
		var y = borderPadding;
		pos = [];
		for(i = 0; i < perm.length; i++){
			pos.push({y:y,cy:y+perm[i].bbox().height/2});
			y+=perm[i].bbox().height+cellPadding;
		}

		//move perm[i] to pos[i]'s x coordinate
		for(i = 0; i < pos.length;i++){
			if(perm[i].y() != pos[i].y || perm[i].x() != positionV(perm[i])){
				perm[i].animate(animationSpeed * 1000).move(positionV(perm[i]),pos[i].y);
			}
		}
		if(showMarker){
			markerElem.remove();
			markerElem = null;
		}
	};

	this.render = function(){
		pointer.clear();
		pointer.createBorderBase();

		pos = [];
		perm = [];

		var arr = textItems.split(";");
		var x = borderPadding;
		var horizontal = (this.orientation != CTATJumble.VERTICAL);

		//render each text item
		for(var i = 0; i < arr.length; i++){
			//create text element and then wrap inside group
			var t = pointer.text(arr[i]);
			var txt = pointer.group().style("cursor:pointer");
			//.draggable({minX:0, maxX:pointer.getWidth(),minY:0,maxY:pointer.getHeight()});
			txt.add(t);
			//give group the necessary functions from text element
			txt.length=function(){return this.get(0).length();};
			txt.text=function(){return this.get(0).text();};
			perm.push(txt);

			//add handlers
			txt.dragstart = horizontal?onstartH:onstartV;
			txt.dragmove = horizontal?onmoveH:onmoveV;
			txt.dragend = horizontal?onendH:onendV;

			//move to correct position and such
			if(horizontal){
				txt.move(x,positionH(txt));
				pos.push({x:x,cx:x+txt.length()/2});
				x+=txt.length()+cellPadding;
			}else{
				txt.move(positionV(txt),x);
				pos.push({y:x,cy:x+txt.bbox().height/2});
				x+=perm[i].bbox().height+cellPadding;
			}
		}

		pointer.textForward();//bring text label forward
	};

	//show correct/incorrect by highlighting div
	this.showCorrect = function(aMessage){
		var str = "0px 0px 15px 5px rgba(0, 255,0, 1.0)";
		pointer.getDivWrap().style['-webkit-box-shadow'] = str;
		pointer.getDivWrap().style['-moz-box-shadow'] = str;
		pointer.getDivWrap().style['box-shadow'] = str;
	};
	this.showInCorrect = function(aMessage){
		var str = "0px 0px 15px 5px rgba(255, 0, 0, 1.0)";
		pointer.getDivWrap().style['-webkit-box-shadow'] = str;
		pointer.getDivWrap().style['-moz-box-shadow'] = str;
		pointer.getDivWrap().style['box-shadow'] = str;
	};

	/* typical getters and setters */
	this.getTextItems = function(){
		return textItems;
	};
	this.getAlignment = function(){
		return alignment;
	};
	this.getOrientation = function(){
		return this.orientation;
	};
	this.getAnimationSpeed = function(){
		return animationSpeed;
	};
	this.getBorderPadding = function(){
		return borderPadding;
	};
	this.getCellPadding = function(){
		return cellPadding;
	};
	this.getShowInsertionMarker = function(){
		return showMarker;
	};
	this.setTextItems = function(aItems){
		textItems = aItems;
	};
	this.setAlignment = function(aAlignment){
		alignment = aAlignment;
	};
	this.setOrientation = function(aOrientation){
		this.orientation = aOrientation;
	};
	this.setAnimationSpeed = function(aAnimationSpeed){
		animationSpeed = aAnimationSpeed;
	};
	this.setBorderPadding = function(aPadding){
		borderPadding = aPadding;
	};
	this.setCellPadding = function(aPadding){
		cellPadding = aPadding;
	};
	this.setShowInsertionMarker = function(aShow){
		showMarker = aShow;
	};

	/* methods that read info from the brd specific to this component */

	this.processParams = function(){
		if(!pointer.getGrDescription()){
			pointer.ctatdebug ("Error: no deserialized component description available");
			return;
		}
		pointer.parameters = pointer.getGrDescription().params;
		if(!pointer.parameters) return;

		for(var i = 0; i < pointer.parameters.length; i++){
			var aParam = pointer.parameters[i];
			if(aParam.paramName == 'textItems'){
				pointer.setTextItems(aParam.paramValue);
			}
		}
	};

	this.processSerialization=function(){
		pointer.ctatdebug ("processSerialization()");
		pointer.styles=pointer.getGrDescription().styles;
		if(!pointer.styles){
			pointer.ctatdebug ("Error: styles structure is null");
			return;
		}
		pointer.ctatdebug ("Processing " + pointer.styles.length + " styles ...");

		for(var i=0; i<pointer.styles.length; i++){
			var aStyle = pointer.styles[i];
			if(aStyle.styleName == "DrawBorder"){
				pointer.setShowBorder(true);
			}else if(aStyle.styleName == "backgroundColor"){
				pointer.setBackgroundColor('#'+aStyle.styleValue);
			}else if(aStyle.styleName == "AnimationSpeed"){
				pointer.setAnimationSpeed(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == "alignment"){
				pointer.setAlignment(aStyle.styleValue);
			}else if(aStyle.styleName == "borderPadding"){
				pointer.setBorderPadding(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == "cellPadding"){
				pointer.setCellPadding(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == "transparencyValue"){
				pointer.setTransparency(aStyle.styleValue);
			}else if(aStyle.styleName == "labelText"){
				pointer.setLabelText(aStyle.styleValue);
			}else if(aStyle.styleName == "DrawBase"){
				pointer.drawBase(aStyle.styleValue);
			}
		}
	};

	/**
	 * An Interface Action for setting the order of the items in the jumble.
	 * @param {string} str	A ; seperated list of instance names.
	 */
	this.SetOrder = function(str){
		pointer.setTextItems(str);
		pointer.render();
	};
};
/**
 * @constant
 * @type {string}
 * @default
 */
CTATJumble.HORIZONTAL = 'Horizontal';
/**
 * @constant
 * @type {string}
 * @default
 */
CTATJumble.VERTICAL = 'Vertical';

CTATJumble.prototype = Object.create(CTAT.Component.Hierarchy.SVG.prototype);
CTATJumble.prototype.constructor = CTATJumble;

CTAT.ComponentRegistry.addComponentType('CTATJumble', CTATJumble);