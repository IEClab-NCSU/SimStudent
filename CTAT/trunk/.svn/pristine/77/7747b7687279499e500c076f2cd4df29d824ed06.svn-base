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
goog.provide('CTATProblem');

goog.require('CTATBase');

/**
 *
 */
CTATProblem = function()
{
	CTATBase.call (this, "CTATProblem", "problem");

	// var name=""; // use setName and getName in CTATBase
	var	label="";
	var	description="";
	var	tutor_flag="tutor";
	var problem_file="";
	var student_interface="";

	var state="notstarted"; // one of: notstarted, started, complete

	/**
	*
	*/
	this.setState=function setState (aValue)
	{
		state=aValue;
	};

	/**
	*
	*/
	this.getState=function getState ()
	{
		return (state);
	};

	/**
	*
	*/
	this.setLabel=function setLabel (aValue)
	{
		label=aValue;
	};

	/**
	*
	*/
	this.getLabel=function getLabel ()
	{
		return (label);
	};

	/**
	*
	*/
	this.setDescription=function setDescription (aValue)
	{
		description=aValue;
	};

	/**
	*
	*/
	this.getDescription=function getDescription ()
	{
		return (description);
	};

	/**
	*
	*/
	this.setTutorFlag=function setTutorFlag (aValue)
	{
		tutor_flag=aValue;
	};

	/**
	*
	*/
	this.getTutorFlag=function getTutorFlag ()
	{
		return (tutor_flag);
	};

	/**
	*
	*/
	this.setProblemFile=function setProblemFile (aValue)
	{
		problem_file=aValue;
	};

	/**
	*
	*/
	this.getProblemFile=function getProblemFile ()
	{
		return (problem_file);
	};

	/**
	*
	*/
	this.setStudentInterface=function setStudentInterface (aValue)
	{
		student_interface=aValue;
	};

	/**
	*
	*/
	this.getStudentInterface=function getStudentInterface ()
	{
		return (student_interface);
	};
};

CTATProblem.prototype = Object.create(CTATBase.prototype);
CTATProblem.prototype.constructor = CTATProblem;

