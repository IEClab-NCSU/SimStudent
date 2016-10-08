/**
 * @fileoverview CTAT's number line component.
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/*
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATNumberLine.js $
 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATNumberLine');

goog.require('CTAT.Math.Fraction');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSAI');
goog.require('CTAT.Component.Hierarchy.SVG');
goog.require('CTAT.ComponentRegistry');
/**
 * Generates a number line component
 * @class
 * Class that implements CommNumberline. Consists of a bunch of lines, tick marks,
 * and points (circles). Each point has a "name", which is basically its position
 * on the number line with appropriate rounding and in decimal form. The points
 * are graded individually instead of the number line as a whole, so we don't pass
 * this to the commShell to grade but directly send to the tutoring service ourselves.
 * @augments CTAT.Component.Hierarchy.SVG
 */
CTATNumberLine = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Hierarchy.SVG.call(this,
			"CTATNumberLine",
			"aNumberLine",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	var Fraction = CTAT.Math.Fraction;
	var tickStepL=new Fraction(1);
	var tickStepS=new Fraction(1,2);
	var max=new Fraction(3);
	var min=new Fraction(1);
	var maxPoints=1;
	var pointSize=1;

	var pendingPointElem;

	var placedPoints = []; //{name,grade}
	var correctPoints = [];//name only
	var incorrectPoints = [];//name only

	var pointer = this;

	/**
	 * Initialization function.
	 */
	this.init=function(){
		pointer.setInitialized(true);
		pointer.createSVG();
		pointer.render();
		pointer.configFromDescription();
		pointer.processParams();
		pointer.addComponentReference(pointer,pointer.getDivWrap());

		pointer.addSafeEventListener('mousemove',mousemoveHandler);
		pointer.addSafeEventListener('mouseleave',mouseleaveHandler);
		pointer.addSafeEventListener('click',clickHandler);
	};
	/**
	 * renders a pending point when mouse moves
	 * @param event	a mouse event
	 */
	var mousemoveHandler = function(event){
		var xCoor = event.offsetX;
		if(!pendingPointElem){
			pendingPointElem = pointer.circle(pointSize,'pendingPoint');
			pendingPointElem.fill({color: '#000000',opacity: 0.5});
		}
		//pendingPointElem.center(xCoor,pointer.getHeight()/2); // TODO: Throws an error
	};

	/**
	 * Removes the pending point when mouse leaves the component.
	 * @param event	a mouse event.
	 */
	var mouseleaveHandler = function(event){
		if(pendingPointElem){
			pendingPointElem.remove();
			pendingPointElem = null;
		}
	};
	/**
	 * Places a point on the number line and grades it if necessary.
	 * @param event	A mouse click event.
	 */
	var clickHandler = function(event){
		var xCoor = event.offsetX;
		//remove all incorrect points first
		var copy = placedPoints.slice(0);
		for(var i = 0; i < copy.length; i++){
			if(copy[i].grade=="incorrect"){
				pointer.removePointByNum(copy[i].name);
			}
		}
		//possibly add point and possibly grade point
		if(placedPoints.length < maxPoints){
			var num = pointer.placePointByCoor(xCoor);//name of point is its value
			if (pointer.getTutorComponent ()!="Do not tutor"){
				var sai = new CTATSAI(pointer.getName(),"AddPoint",num);
				commShell.processComponentAction(sai);
			}
		}
	};

	/**
	 * Renders the number line by drawing the axis, tick marks, and points.
	 */
	this.render = function(){
		pointer.clear();

		pointer.createBorderBase();

		//draw horizontal axis
		var axisY = pointer.getHeight()/2;//y coordinate of axis
		var axisXL = 10;//x coordinate of left point
		var axisXR = pointer.getWidth()-10;//x coordinate of right point
		pointer.line(axisXL,axisY,axisXR,axisY,"axis");


		//draw arrows
		pointer.line(axisXL,axisY,axisXL+10,axisY-5,"arrowLeftUp");
		pointer.line(axisXL,axisY,axisXL+10,axisY+5,"arrowLeftDown");
		pointer.line(axisXR,axisY,axisXR-10,axisY-5,"arrowRightUp");
		pointer.line(axisXR,axisY,axisXR-10,axisY+5,"arrowRightDown");

		//draw tick marks
		var start = axisXL+20;
		var end = axisXR-20;
		var font = 12;
		var drawTicks=function(tickStep,larges,height){
			var numIntervals = ((max.subtract(min)).divide(tickStep));//(max-min)/tickStep
			var stepSize = Math.abs(end-start)/numIntervals; //(end-start)/tickStep
			var frac = min.clone();

			//TODO: fix this so that it counts from origin
			var offset = Math.floor((font*2/3)+1);
			for(var i = 0; i <= numIntervals; i++){
				frac.reduce();
				if(!larges[frac.toString()]){
					var x1 = start + i*stepSize;
					pointer.line(x1, axisY-height/2, x1, axisY+height/2);
					pointer.fraction(frac).center(x1,axisY-height/2-offset);
					larges[frac.toString()] = frac;
				}
				frac = frac.add(tickStep);
			}
		};
		var larges = [];
		drawTicks(tickStepL,larges,30);
		drawTicks(tickStepS,larges,20);

		//draw placed points
		for(var i = 0; i < placedPoints.length; i++){
			var x = pointer.calcXCoordinate(placedPoints[i].name);
			var y = pointer.getHeight()/2;
			console.log(x + ' ' + y);
			pointer.circle(pointSize,placedPoints[i].name).center(x,y);
		}
	};

	/**
	 * places point on number line and adds to placedPoints
	 * @param {Number} xCoor	the x coordinate where the point is placed.
	 * @returns {String}
	 */
	this.placePointByCoor = function(xCoor){
		var name = pointer.calcNum(xCoor).valueOf().toFixed(pointer.getPrecision());
		var circ = pointer.circle(pointSize,name).center(xCoor,pointer.getHeight()/2);
		var obj = {name:name,grade:"ungraded"};
		placedPoints.push(obj);
		return name;
	};
	/**
	 * Places point on the number line by its value.
	 * @param {CTAT.Math.Fraction|String|Number} num
	 * @returns {String}
	 */
	this.placePointByNum = function(num){
		var x = pointer.calcXCoordinate(num);
		return pointer.placePointByCoor(x);
	};
	/**
	 * removes point from numberline and removes from placedPoints and possibly
	 * correctPoints/incorrectPoints
	 * @param {Number} xCoor	the x coordinate of the point.
	 * @returns {String}
	 */
	this.removePointByCoor = function(xCoor){
		var name  = pointer.calcNum(xCoor).valueOf().toFixed(pointer.getPrecision());
		var point = arrayRemove(placedPoints,name,function(a,b){return a.name==b;});
		if(!point) {
			this.ctatdebug(xCoor + " does not contain a placed point.");
			console.log(name + " does not contain a placed point.");
			return;
		}
		pointer.removeElem(name);

		var grade = point.grade;
		if(grade === 'ungraded') return;
		var arr = grade === 'correct' ? correctPoints : incorrectPoints;
		arrayRemove(arr,name,function(a,b){return a.name==b;});
		pointer.removeElem(name+"_grade");
		return name;
	};
	/**
	 * Removes the point specified by value.
	 * @param {CTAT.Math.Fraction|Number|String} num
	 * @returns {String}
	 */
	this.removePointByNum = function(num){
		var x = pointer.calcXCoordinate(num);
		return pointer.removePointByCoor(x);
	};

	/**
	 * Given a value on the number line, return the component coordinate
	 * @param {CTAT.Math.Fraction|String|Number} num
	 * @returns {Number}
	 */
	this.calcXCoordinate = function(num){
		var fraction = new Fraction(num);
		var start = 30;
		var end = pointer.getWidth() - 30;
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
		var end = pointer.getWidth() - 30;
		var rangePix = Math.abs(end - start);
		var range = max.subtract(min);
		var offset = range.multiply((xCoor - start)/rangePix);
		return min.add(offset);
	};
	/**
	 * shows an individual point as correct
	 * @param {CTATSAI} aMessage
	 */
	this.showCorrect = function(aMessage) {
		var name=aMessage.getInput ();

		correctPoints.push(name);
		var num = new Fraction(name);
		var x = pointer.calcXCoordinate(num);
		var gradient = pointer.getSVG().gradient('radial',function(stop){
			stop.at(0.5,'#00ee00');
			stop.at(1,'#ffffff');
		});
		pointer.circle(pointSize * 2,name+"_grade").center(x,pointer.getHeight()/2).attr({fill: gradient}).backward();
		arrayFind(placedPoints,function(a){return a.name==name;}).grade="correct";
	};
	/**
	 * shows an individal point incorrect
	 * @param {CTATSAI} aMessage
	 */
	this.showInCorrect = function(aMessage)
	{
		var name=aMessage.getInput ();

		incorrectPoints.push(name);
		var num = new Fraction(name);
		var x = pointer.calcXCoordinate(num);
		var gradient = pointer.getSVG().gradient('radial',function(stop){
			stop.at(0.5,'#ee0000');
			stop.at(1,'#ffffff');
		});
		pointer.circle(pointSize*2,name+"_grade").center(x,pointer.getHeight()/2).attr({fill: gradient}).backward();
		arrayFind(placedPoints,function(a){return a.name==name;}).grade="incorrect";
	};

	/* the typical getters and setters */
	/**
	 * Set the step size of the large tick marks.
	 * @param {CTAT.Math.Fraction|String|Number} step
	 */
	this.setTickStepL=function(step){
		tickStepL = new Fraction(step);
	};
	/**
	 * Set the step size of the small tick marks.
	 * @param {CTAT.Math.Fraction|String|Number} step
	 */
	this.setTickStepS=function(step){
		tickStepS = new Fraction(step);
	};
	/**
	 * Set the maximum value available on the number line.
	 * @param {CTAT.Math.Fraction|String|Number} newMax
	 */
	this.setMax=function(newMax){
		max = new Fraction(newMax);
	};
	/**
	 * Set the minimum value available on the number line.
	 * @param {CTAT.Math.Fraction|String|Number} newMin
	 */
	this.setMin=function(newMin){
		min = new Fraction(newMin);
	};
	/**
	 * Sets the size of the point marker.
	 * @param {Number} newPointSize	The size of the point marker in pixels.
	 */
	this.setPointSize=function(newPointSize){
		pointSize=Number(newPointSize);
	};
	/**
	 * Sets the maximum number of points that the user can enter.
	 * @param {Number} aMaxPoints
	 */
	this.setMaxPoints=function(aMaxPoints){
		maxPoints=aMaxPoints;
	};
	this.getTickStepL=function(){
		return tickStepL;
	};
	this.getTickStepS=function(){
		return tickStepS;
	};
	this.getMax=function(){
		return max;
	};
	this.getMin=function(){
		return min;
	};
	this.getPointSize=function(){
		return pointSize;
	};
	this.getLabelText=function(){
		return pointer.labelText;
	};
	this.getMaxPoints=function(){
		return maxPoints;
	};

	/* methods that read info from the brd specific to this component */

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
			}else if(aStyle.styleName == "PointSize"){
				pointer.setPointSize(parseInt(aStyle.styleValue));
			}else if(aStyle.styleName == "backgroundColor"){
				pointer.setBackgroundColor('#'+aStyle.styleValue);
			}else if(aStyle.styleName == "transparencyValue"){
				pointer.setTransparency(aStyle.styleValue);
			}else if(aStyle.styleName == "labelText"){
				pointer.setLabelText(aStyle.styleValue);
			}else if(aStyle.styleName == "DrawBase"){
				pointer.drawBase(aStyle.styleValue);
			}
		}
	};

	this.processParams = function(){
		if(!pointer.getGrDescription()) {
			pointer.ctatdebug ("Error: no deserialized component description available");
			return;
		}
		pointer.parameters = pointer.getGrDescription().params;
		if(!pointer.parameters) return;

		for(var i = 0; i < pointer.parameters.length; i++){
			var aParam = pointer.parameters[i];
			if(aParam.paramName == "LargeTickmarkStep"){
				pointer.setTickStepL(aParam.paramValue);
			}else if(aParam.paramName == 'SmallTickmarkStep'){
				pointer.setTickStepS(aParam.paramValue);
			}else if(aParam.paramName == 'MaxValueControllers'){
				//TODO
			}else if(aParam.paramName == 'MinValueControllers'){
				//TODO
			}else if(aParam.paramName == 'LargeTickmarkControllers'){
				//TODO
			}else if(aParam.paramName == 'SmallTickmarkControllers'){
				//TODO
			}else if(aParam.paramName == 'Max_Points'){
				pointer.setMaxPoints(parseInt(aParam.paramValue));
			}else if(aParam.paramName == 'Minimum'){
				pointer.setMin(aParam.paramValue);
			}else if(aParam.paramName == 'Maximum'){
				pointer.setMax(aParam.paramValue);
			}
		}
	};

	//interface actions
	this.set_minimum = function(str){
		pointer.setMin(str);
		pointer.render();
	};
	this.set_maximum = function(str){
		pointer.setMax(str);
		pointer.render();
	};
	this.set_max_user_entries = function(str){
		pointer.setMaxPoints(parseInt(str));
		pointer.render();
	};
	this.set_large_step = function(str){
		pointer.setTickStepL(str);
		pointer.render();
	};
	this.set_small_step = function(str){
		pointer.setTickStepS(str);
		pointer.render();
	};
	this.set_precision = function(str){
		pointer.setPrecision(parseInt(str));
		pointer.render();
	};
	this.Points = function(str){
		placedPoints = [];
		correctPoints = [];
		incorrectPoints = [];
		var arr = str.split(";");
		for(var i = 0; i < arr.length; i++){
			pointer.placePointByNum(arr[i]);
		}
	};
	this.set_denominator = function(aDenominator) {
		// TODO
		return;
	};
};
CTATNumberLine.prototype = Object.create(CTAT.Component.Hierarchy.SVG.prototype);
CTATNumberLine.prototype.constructor = CTATNumberLine;

CTAT.ComponentRegistry.addComponentType('CTATNumberLine',CTATNumberLine);