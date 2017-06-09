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
goog.provide('CTATSequencer');

goog.require('CTATBase');
goog.require('CTATPackage');
goog.require('CTATCommLibrary');

/**
 *
 */
CTATSequencer = function()
{
	CTATBase.call (this, "CTATSequencer", "sequencer");

	var pointer=this;
	var packageManager=new CTATPackage ();
	var parser=new CTATXML ();
	var retriever=null;
	var algorithm="sequential"; // One of: sequential, random, adaptive
	var sequenceReadyHandler=null;

	/**
	 *
	 */
	this.setAlgorithm=function setAlgorithm (anAlgorithm)
	{
		algorithm=anAlgorithm;
	};

	/**
	 *
	 */
	this.getAlgorithm=function getAlgorithm ()
	{
		return (algorithm);
	};

	/**
	 *
	 */
	this.handlePackageRetrieval=function handlePackageRetrieval (aData)
	{
		pointer.ctatdebug ("handlePackageRetrieval ()");

		packageManager.init (aData);

		if (sequenceReadyHandler!=null)
		{
			sequenceReadyHandler ();
		}
	};

	/**
	 *
	 */
	this.processXML=function processXML (aRoot)
	{
		pointer.ctatdebug ("parseXML ()");

		packageManager.init (aRoot);

		if (sequenceReadyHandler!=null)
		{
			sequenceReadyHandler ();
		}
	};

	/**
	 *
	 */
	this.init=function init (aPackageURL,aHandler)
	{
		pointer.ctatdebug ("init ()");

		sequenceReadyHandler=aHandler;

		retriever=new CTATCommLibrary (null,false);
		retriever.retrieveXMLFile (aPackageURL,parser,this);
	};

	/**
	 *
	 */
	this.getProblemSetSize=function getProblemSetSize ()
	{
		pointer.ctatdebug ("getProblemSetSize ()");

		var pSets=packageManager.getProblemSets ();

		if (pSets.length==0)
		{
			pointer.ctatdebug ("Error: no problem sets available, trying list of problems directly ...");

			var pList=packageManager.getProblems ();

			return (pList.getProblemSize ());
		}
		else
		{
			var pSet=pSets [0];

			return (pSet.getProblemSize ());
		}

		return (0);
	};
	/**
	 *
	 */
	this.getFirstProblem=function getFirstProblem()
	{
		pointer.ctatdebug ("getFirstProblem ()");

		var pSets=packageManager.getProblemSets ();

		if (pSets.length==0)
		{
			pointer.ctatdebug ("Error: no problem sets available, trying list of problems directly ...");

			var pList=packageManager.getProblems ();

			return (pList [0]);
		}
		else
		{
			return (pSets [0].getFirstProblem ());
		}

		return (null);
	};

	/**
	 *
	 */
	this.getProblemList=function getProblemList ()
	{
		pointer.ctatdebug ("getProblemList ()");

		var pSets=packageManager.getProblemSets ();

		if (pSets.length==0)
		{
			pointer.ctatdebug ("Error: no problem sets available, trying list of problems directly ...");

			var pList=packageManager.getProblems ();

			return (pList);
		}
		else
		{
			return (pList);
		}

		return (null);
	};

	/**
	 *
	 */
	this.getNextProblem=function getNextProblem(currentIndex)
	{
		pointer.ctatdebug ("getNextProblem ()");
	};
};

CTATSequencer.prototype = Object.create(CTATBase.prototype);
CTATSequencer.prototype.constructor = CTATSequencer;
