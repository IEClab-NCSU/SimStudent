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
goog.provide('CTATPackage');

goog.require('CTATBase');
goog.require('CTATXML');
goog.require('CTATProblem');
goog.require('CTATProblemSet');

/**
 *
 */
CTATPackage = function()
{
	CTATBase.call (this, "CTATPackage", "packagemanager");

	var pointer=this;
	var root=null;
	var parser=new CTATXML (); // Like the other cases where we have XML coming in we should be able to parse JSON
	//var problems=new Array (); // Contains CTATProblem
	var problems=new CTATProblemSet (); // Contains CTATProblem
	var problemSets=[problems]; // Contains CTATProblemSet

	/**
	*
	*/
	this.init=function init (aDocRoot)
	{
		pointer.ctatdebug ("init ()");

		if (aDocRoot==null)
		{
			pointer.ctatdebug ("Error: document root to be parsed is null.");
			return;
		}

		pointer.ctatdebug ("Root name: " + parser.getElementName (aDocRoot));

		var packageContent=parser.getElementChildren (aDocRoot);

		for (var i=0;i<packageContent.length;i++)
		{
			var rootEntity=packageContent [i];
			var rootEntityName=parser.getElementName (rootEntity);

			pointer.ctatdebug ("Examining element: " + parser.getElementName (rootEntity));

			//>--------------------------------------------------------

			if (rootEntityName=="Problems")
			{
				pointer.ctatdebug ("Processing Problems ...");

				var problemList=parser.getElementChildren (rootEntity);

				for (var j=0;j<problemList.length;j++)
				{
					var aProblemElement=problemList [j];

					pointer.ctatdebug ("Problem ["+j+"] desc: " + parser.getElementAttr (aProblemElement,"description") + ", interface: " + parser.getElementAttr (aProblemElement,"student_interface") + ", brd: " + parser.getElementAttr (aProblemElement,"problem_file"));

					var newProblem=new CTATProblem ();

					newProblem.setName (parser.getElementAttr (aProblemElement,"name"));
					newProblem.setLabel (parser.getElementAttr (aProblemElement,"label"));
					newProblem.setDescription (parser.getElementAttr (aProblemElement,"description"));
					newProblem.setTutorFlag (parser.getElementAttr (aProblemElement,"tutor_flag"));
					newProblem.setProblemFile (parser.getElementAttr (aProblemElement,"problem_file"));
					newProblem.setStudentInterface (parser.getElementAttr (aProblemElement,"student_interface"));

					pointer.ctatdebug ("Adding problem ...");

					//problems.push (newProblem);
					problems.addProblem (newProblem);
				}
			}

			//>--------------------------------------------------------

			if (rootEntityName=="ProblemSets")
			{
				pointer.ctatdebug ("Processing ProblemSets ...");
			}

			//>--------------------------------------------------------

			if (rootEntityName=="Assets")
			{
				pointer.ctatdebug ("Processing Assets ...");
			}

			//>--------------------------------------------------------
		}
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
	this.getProblemSets=function getProblemSets ()
	{
		return (problemSets);
	};
};

CTATPackage.prototype = Object.create(CTATBase.prototype);
CTATPackage.prototype.constructor = CTATPackage;
