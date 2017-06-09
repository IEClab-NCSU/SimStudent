/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-06-28 15:07:11 -0500 (週二, 28 六月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATSkillWindow.js $
 $Revision: 23782 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATSkillWindow');

goog.require('CTAT.Component.Base.Graphical');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTAT.ComponentRegistry');
goog.require('CTATSkillSet');
/**
 *
 */
CTATSkillWindow = function(aDescription,aX,aY,aWidth,aHeight) {
	CTAT.Component.Base.Graphical.call(this,
			"CTATSkillWindow",
			"__undefined__",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);
	var pointer=this;
	pointer.isTabIndexable=false;

	this.getThreshold = function() {
		var threshold = parseFloat($(this.getDivWrap()).attr('data-ctat-threshold'));
		return isNaN(threshold) ? 0.95 : threshold;
	};
	this.setThreshold = function(threshold) {
		$(this.getDivWrap()).attr('data-ctat-threshold',threshold);
	};

	var skill_window=null;

	/**
	 *
	 */
	this.init=function init()
	{
		pointer.ctatdebug("init (" + pointer.getName() + ")");
		pointer.setIsAbstractComponent (true);

		pointer.setInitialized(true);
		skill_window = this.getDivWrap();
		this.setComponent(skill_window);

		this.drawComponent ();
		this.component.addEventListener('focus', this.processFocus);
	};

	/**
	 *
	 */
	this.assignSkillSet=function assignSkillSet (aSkillSet)
	{
		CTATSkillSet.skills=aSkillSet;

		this.drawComponent ();
	};

	/**
	 *
	 */
	this.updateSkillSet=function updateSkillSet (aSkillSet)
	{
		if (!CTATSkillSet.skills)
		{
			pointer.ctatdebug ("Error: updateSkillSet () no initial skill set given, can't update");
			return;
		}

		pointer.ctatdebug ("updateSkillSet ("+aSkillSet.getSkillSet ().length+")");

		var internalSkillList=aSkillSet.getSkillSet ();

		for (var i=0;i<internalSkillList.length;i++)
		{
			var skill=internalSkillList [i];

			pointer.ctatdebug ("Updating skill " +skill.getSkillName () + " to level: " + skill.getLevel ()+" ...");

			CTATSkillSet.skills.setSkillLevel (skill.getSkillName (),skill.getLevel (),1);
		}

		this.drawComponent ();
	};

	/**
	 * Override from CTATComponent.js
	 */
	this.drawComponent=function drawComponent ()
	{
		pointer.ctatdebug ("drawComponent ()");

		if (CTATSkillSet.skills===null)
		{
			ctatdebug ("Info: no skillSet object available, bumping out");
			return;
		}

		// Draw each skill, one at a time ...

		var skillList=CTATSkillSet.skills.getSkillSet ();

		if (skillList===null)
		{
			pointer.ctatdebug ("Error: list of skills is null in skills object");
			return;
		}

		if (skillList.length<=0)
		{
			pointer.ctatdebug ("Error: list of skills is 0 length");
			return;
		}

		var fragment = document.createDocumentFragment();
		var spThreshold = this.getThreshold();

		for (var i=0;i<skillList.length;i++)
		{
			var skill=skillList [i];

			pointer.ctatdebug ("Drawing skill "+i+" "+skill.getDisplayName () + " level: " + skill.getLevel ()+" ...");

			var skill_line = document.createElement('div');
			skill_line.classList.add('CTATSkillWindow--skill');
			var sbar = document.createElement('div');

			sbar.classList.add('CTATSkillWindow--bar');

			var mbar = document.createElement('div');
			var slvl = skill.getLevel();
			if (slvl<spThreshold)
			{
				mbar.classList.add('CTATSkillWindow--bar--nonmastered');
			}
			else
			{
				mbar.classList.add('CTATSkillWindow--bar--mastery');
			}

			mbar.style.width = slvl>spThreshold ? '100%' : (slvl * 100)+'%';
			sbar.appendChild(mbar);
			skill_line.appendChild(sbar);
			fragment.appendChild(skill_line);

			var sbar_label = document.createElement('div');
			sbar_label.textContent = skill.getDisplayName() || "no-name";
			sbar_label.classList.add('CTATSkillWindow--label');
			skill_line.appendChild(sbar_label);
		}

		skill_window.innerHTML = '';
		skill_window.appendChild(fragment);
	};
};

CTATSkillWindow.prototype = Object.create(CTAT.Component.Base.Graphical.prototype);
CTATSkillWindow.prototype.constructor = CTATSkillWindow;

CTAT.ComponentRegistry.addComponentType('CTATSkillWindow',CTATSkillWindow);
