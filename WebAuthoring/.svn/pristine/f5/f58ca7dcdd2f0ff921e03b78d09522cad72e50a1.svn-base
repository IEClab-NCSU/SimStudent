/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

/**
 *
 */
function CTATNameTranslator () 
{	
	CTATBase.call(this, "CTATNameTranslator","translator");
	
	/**
	* Input for this method would for example be: spreadsheet.R14C8
	* The resulting output would be: spreadsheet.H14 or with the
	* noNamespace flag you would get just H14
	*/
	this.translateFromCTAT=function translateFromCTAT (inName, noNamespace)
	{
		//useDebugging=true;
	
		debug ("translateFromCTAT ("+inName+")");
		
		if (inName.indexOf (".R")!=-1)
		{
			var outName="";
			
			var pieces=inName.split (".");
			
			var RC=pieces [1].split ("C");
			
			if (RC.length==0)
			{
				RC=pieces [1].split ("c");
			}
			
			if (RC.length==0)
			{
				debug ("Info: incoming name does not need translation");
			}
			else
			{
				var colOriginal=RC[1];
				var rowOriginal=RC[0].substr (1);
					
				debug ("Original row: " + rowOriginal + " original col: " + colOriginal);
					
				if (noNamespace==true)
					outName=colName (colOriginal).toUpperCase ()+rowOriginal;
				else
					outName=pieces [0]+"."+colName (colOriginal).toUpperCase()+rowOriginal;
					
				debug ("Translated: " + outName);
			}
		}
		else
			debug ("Info: incoming name does not need translation");		
		
		//useDebugging=false;		
		
		return (inName);
	}
	/**
	* use: var colNumber = colOrignal.match(/(\d+)/)[1];
	*/
	this.translateToCTAT=function translateToCTAT (inName)
	{

		return (inName);
	};
}

CTATNameTranslator.prototype = Object.create(CTATBase.prototype);
CTATNameTranslator.prototype.constructor = CTATNameTranslator;