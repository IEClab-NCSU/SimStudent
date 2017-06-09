/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATActionEvaluationData.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATActionEvaluationData');

goog.require('CTATBase');
/**
 * CTATActionEvaluation contains data relevant to Action Evalutation fields in
 * log messages.<p>This class mainly serves the purpose of internal message
 * management. All of the externally published logging methods that deal with
 * action evaluations will condense their arguments down to one of these objects
 * to be used with the internal logging methods.</p>
 *
 * @see http://pslcdatashop.web.cmu.edu/dtd/guide/tutor_message.html#element.action_evaluation Action Evaluation specification
 */
CTATActionEvaluationData = function(anEval)
{
	CTATBase.call (this,"CTATActionEvaluationData","actionevaluation");

	var classification="";
	var currentHintNumber=0;
	var totalHintsAvailable=0;
	var hintID= "";
	var evaluation=anEval;

	/**
	 * Sets the classification of the ActionEvaluation.
	 * @param	classification	A string that classifies the evaluation.
	 */
	this.setClassification=function setClassification(classification)
	{
		this.classification = classification;
	};

	/**
	 * Returns whether or not that ActionEvaluation is of type "HINT"
	 * @return <code>true</code> if the ActionEvaluation describes a hint, <code>false</code> otherwise.
	 */
	this.isHint=function isHint()
	{
		return (evaluation == "HINT");
	};

	/**
	 * Returns whether of not the ActionEvaluation has a classification.
	 * @return	<code>true</code> if the ActionEvaluation has a clasification, <code>false</code> otherwise.
	 */
	this.hasClassification=function hasClassification()
	{
		return (classification != null);
	};

	/**
	 * Sets the current hint number value of the Action Evaluation.
	 * @param	hintNumber	The index of the current hint.
	 */
	this.setCurrentHintNumber=function setCurrentHintNumber(hintNumber)
	{
		currentHintNumber = hintNumber;
	};

	/**
	 * Sets the total number of hints available.
	 * @param	numHints	The total number of hints.
	 */
	this.setTotalHintsAvailable=function setTotalHintsAvailable(numHints)
	{
		totalHintsAvailable = numHints;
	};

	/**
	 * Sets the id of the current hint
	 * @param	theID	A Hint ID
	 */
	this.setHintID=function setHintID(theID)
	{
		hintID = theID;
	};

	/**
	 * Returns the classification of the ActionEvaluation.
	 * @return	A classification of the action evaluation
	 */
	this.getClassification=function getClassification()
	{
		return classification;
	};

	/**
	 * Sets the evaluation field of the ActionEvaluation.
	 * @see		http://pslcdatashop.web.cmu.edu/dtd/guide/tutor_message.html#table.action_evaluation Reccomended Values for Action Evaluation
	 * @param	theEvaluation	The evaluation field of the ActionEvaluation
	 */
	this.setEvaluation=function setEvaluation(theEvluation)
	{
		evaluation = theEvluation;
	};

	/**
	 * Returns the evaluation field of the ActionEvaluation
	 * @return	The evaluation of the ActionEvaluation
	 */
	this.getEvaluation=function getEvaluation()
	{
		return evaluation;
	};

	/**
	 * Returns the ActionEvalution in its DataShop specified XML format.
	 * @return	An XMLString representation of the ActionEvaluation.
	 */
	this.getAttributeString=function getAttributeString()
	{
		var retString="";

		if (classification!== "")
		{
			retString += 'classification="' + classification + '" ';
		}

		if (!this.isHint())
		{
			return retString;
		}

		retString += 'current_hint_number="' + currentHintNumber + '" ';
		retString += 'total_hints_available="' + totalHintsAvailable + '" ';

		if(hintID !== "")
		{
			retString += 'hint_id="' + hintID + '" ';
		}

		return retString;
	};
};

CTATActionEvaluationData.prototype = Object.create(CTATBase.prototype);
CTATActionEvaluationData.prototype.constructor = CTATActionEvaluationData;
