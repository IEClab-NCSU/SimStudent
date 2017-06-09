/**-----------------------------------------------------------------------------
 $Author$
 $Date$
 $HeadURL$
 $Revision$

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATProblemSet');

goog.require('CTATBase');
goog.require('CTATProblem');

/**
 *
 */
CTATProblemSet = function()
{
	CTATBase.call (this, "CTATProblemSet", "problemset");

	var pointer=this;
	var problems=new Array (); // Contains CTATProblem

	/**
	*
	*/
	this.addProblem=function addProblem (aProblem)
	{
		problems.push (aProblem);
	};
	/**
	*
	*/
	this.getProblems=function getProblems ()
	{
		return (problems);
	};
	/**
	*
	*/
	this.getProblemSize=function getProblemSize ()
	{
		return (problems.length);
	};	
	/**
	*
	*/
	this.getFirstProblem=function getFirstProblem()
	{
		pointer.ctatdebug ("getFirstProblem () problems.length "+problems.length);
		
		if (problems.length==0)
		{
			return (null);
		}
		
		return (problems [0]);
	};
};

CTATProblemSet.prototype = Object.create(CTATBase.prototype);
CTATProblemSet.prototype.constructor = CTATProblemSet;
