/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
 http://stackoverflow.com/questions/6486307/default-argument-values-in-javascript-functions
 
*/

/**
*
*/
function CTATComplexSAI (aSelection,anAction,anInput,aPrompt) 
{
	CTATBase.call(this, "CTATComplexSAI","complexsai");
	
		
}

CTATComplexSAI.prototype = Object.create(CTATBase.prototype);
CTATComplexSAI.prototype.constructor = CTATComplexSAI;
