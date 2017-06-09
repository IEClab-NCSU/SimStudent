/**
 * @fileoverview Defines CTATJumble component, a CTAT component that lets
 * users order things linearly.
 *
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/**-----------------------------------------------------------------------------
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATJumble.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATJumble');

goog.require('CTAT.Component.Base.Tutorable');
goog.require('CTAT.ComponentRegistry');
/*
 * Class that implements CommJumble. Each jumble element is a text element
 * wrapped in a group. This is because moving a text element gives strange behavior,
 * so this is a work around to make animation smoother. They're also made draggable
 * within the bounds of the border.
 */
CTATJumble = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTAT.Component.Base.Tutorable.call(this,
			"CTATJumble",
			"aJumble",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer = this;

	this.setActionInput("SetOrder",'');

	var drag_area = null;

	this.init=function() {
		this.setInitialized(true);
		drag_area = this.getDivWrap();
		this.setComponent(drag_area);
		this.addComponentReference(this,drag_area);
		if (!CTATConfiguration.get('previewMode'))
		{
			$(drag_area).children().addClass('CTATJumble--item').attr({
				unselectable:'on',
				draggable: true,
			}).each(function () {
				this.addEventListener('dragstart',handleDragStart,false);
				this.addEventListener('dragenter',handleDragEnter,false);
				this.addEventListener('dragover',handleDragOver,false);
				this.addEventListener('dragleave',handleDragLeave,false);
				this.addEventListener('drop',handleDrop,false);
				this.addEventListener('dragend',handleDragEnd,false);
			});
		}
		this.component.addEventListener('focus', this.processFocus);
	};
	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		this.updateSAI();
	    return [this.getSAI()];
	};

	var _drag_source = null;
	var handleDragStart = function(e) {
		e.target.classList.add('CTATJumble--item--home');
			_drag_source = e.target;
			e.dataTransfer.effectAllowed = 'move';
			e.dataTransfer.setData('text/html', e.target.innerHTML);
	};
	var handleDragOver = function(e) {
		if (e.preventDefault) {
			e.preventDefault();
		}
		e.dataTransfer.dropEffect = 'move';
		return false;
	};
	var handleDragEnter = function(e) {
		// this is the node that is under the dragged node
		this.classList.add('CTATJumble--item--over');
	};
	var handleDragLeave = function(e) {
		// this is the node that is under the dragged node
		this.classList.remove('CTATJumble--item--over');
	};
	var handleDrop = function(e) {
		if (e.stopPropagation) {
			e.stopPropagation();
		}
		$(drag_area).children().removeClass('CTATJumble--item--over');
		if (_drag_source != this) {
			_drag_source.innerHTML = this.innerHTML;
			var id = _drag_source.id;
			_drag_source.id = this.id;
			this.innerHTML = e.dataTransfer.getData('text/html');
			this.id = id;

			pointer.setInput(pointer.getOrder());
			//console.log(pointer.getSAI().toString());
			pointer.processAction();
		}
		return false;
	};

	var handleDragEnd = function(e) {
		this.classList.remove('CTATJumble--item--home');
		$(drag_area).children().removeClass('CTATJumble--item--over');
	};

	var super_setEnabled = this.setEnabled;
	this.setEnabled = function (bool) {
		super_setEnabled(bool);
		if (drag_area) {
			$(drag_area).children().attr('draggable',bool);
		}
	};

	this.init_items = function(items) {
		if (drag_area) {
			var fragment = document.createDocumentFragment();

			var itms = items.split(';');
			var l = itms.length;
			for (var i=0;i<l;i++) {
				var label = document.createElement('div');
				label.textContent = itms[i];
				label.id = this.getName()+i;
				label.setAttribute('unselectable','on');
				label.setAttribute('draggable',true);
				label.classList.add('CTATJumble--item');
				label.addEventListener('dragstart',handleDragStart,false);
				label.addEventListener('dragenter',handleDragEnter,false);
				label.addEventListener('dragover',handleDragOver,false);
				label.addEventListener('dragleave',handleDragLeave,false);
				label.addEventListener('drop',handleDrop,false);
				label.addEventListener('dragend',handleDragEnd,false);
				fragment.appendChild(label);
			}

			drag_area.innerHTML = "";
			drag_area.appendChild(fragment);
		}
	};

	// Use CSS for alignment
	// Use CSS for orientation
	// Use CSS for BorderPadding
	// AnimationSpeed is no longer valid
	// Use CSS for cellPadding
	// No insertion marker, use CSS to help indicate replacement.

	/* methods that read info from the brd specific to this component */
	this.setParameterHandler('textItems',this.init_items);

	/**
	 * An Interface Action for setting the order of the items in the jumble.
	 * @param {string} str	A ; seperated list of instance names.
	 */
	this.SetOrder = function(str) {
		str.split(';').forEach(function (id) {
			$(drag_area).append($('#'+id));
		});
	};
	/**
	 * Get a list of item ids in the current order.
	 * @returns {String} a list of ids separated with ;'s
	 */
	this.getOrder = function() {
		var lbs = [];
		$(drag_area).children().each(function() {
			if (this.draggable) {
				if (this.id) { lbs.push(this.id); }
				else {
					console.log("ERROR: The CTATJumble "+pointer.getName()+
							" has a child with no id attribute: "+this+"!!!");
				}
			}
		});
		return lbs.join(';');
	};
	/**
	 *
	 */
	this.updateSAI = function() {
		pointer.setInput(pointer.getOrder());
	};
};

CTATJumble.prototype = Object.create(CTAT.Component.Base.Tutorable.prototype);
CTATJumble.prototype.constructor = CTATJumble;

CTAT.ComponentRegistry.addComponentType('CTATJumble', CTATJumble);
