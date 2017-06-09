/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATUtil/CTATNameTranslator.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATNameTranslator');

goog.require('CTATBase');
goog.require('CTATGlobalFunctions');
/**
 *
 */
CTATNameTranslator = function()
{
	CTATBase.call(this, "CTATNameTranslator","translator");

	var toUpperCase=false;
	var passthrough=false;
	var pointer=this;

	/**
	*
	*/
	this.setPassthrough=function setPassthrough (aValue)
	{
		passthrough=CTATGlobalFunctions.toBoolean(aValue);
	};

	/**
	*
	*/
	this.getPassthrough=function getPassthrough ()
	{
		return (passthrough);
	};

	/**
	* This method needs to be able to generate a JSON string for calls such
	* as: resetInternal (E27:K41), where we manufactor a JSON array with empty
	* string values
	*/
	this.manufactorJSON=function manufactorJSON (aRange,aValue)
	{
		pointer.ctatdebug ("manufactorJSON ("+aRange+","+aValue+")");

		var range=aRange.split (":");

		if (range.length==1)
		{
			pointer.ctatdebug ("No range in aRange, returning basic JSON ...");
			return ("");
		}

		var a1RowStart=this.getA1Row (range [0]);
		var a1ColStart=this.getA1Col (range [0]);

		var a1RowEnd=this.getA1Row (range [1]);
		var a1ColEnd=this.getA1Col (range [1]);

		pointer.ctatdebug ("Manufacturing json from: [" + a1RowStart + "," + a1ColStart +"] to: [" + a1RowEnd + "," + a1ColEnd +"]");

		var jsonString="[";

		var xRange=parseInt(a1RowEnd)-parseInt(a1RowStart);
		var yRange=parseInt(a1ColEnd)-parseInt(a1ColStart);

		xRange++;
		yRange++;

		pointer.ctatdebug ("X range: " + xRange + ", Y range: " + yRange);

		for (var i=0;i<xRange;i++)
		{
			if (i>0)
			{
				jsonString+=",";
			}

			jsonString+="[";

			for (var j=0;j<yRange;j++)
			{
				if (j>0)
				{
					jsonString+=",";
				}

				jsonString+="\"";
				jsonString+=aValue;
				jsonString+="\"";
			}

			jsonString+="]";
		}

		jsonString+="]";

		//pointer.ctatdebug ("Resulting JSON string: " + jsonString);

		return (jsonString);
	};

	/**
	* Input for this method would for example be: spreadsheet.R14C8
	* The resulting output would be: spreadsheet.H14 or with the
	* noNamespace flag you would get just H14
	*/
	this.translateFromCTAT=function translateFromCTAT (aName, noNamespace)
	{
		if (passthrough===true)
		{
			return (aName);
		}

		var inName=aName.toLowerCase();

		pointer.ctatdebug ("translateFromCTAT ("+inName+")");

		var rowOriginal=0;
		var colOriginal=0;

		var outName="";

		if (inName.indexOf (".r")!=-1)
		{
			var pieces=inName.split (".");

			var RC=pieces [1].split ("r");

			if (RC.length<=0)
			{
				RC=pieces [1].split ("c");
			}

			if (RC.length<=0)
			{
				pointer.ctatdebug ("Info: incoming name does not need translation");
			}
			else
			{
				colOriginal=RC[1];
				rowOriginal=RC[0].substr (1);

				pointer.ctatdebug ("Original row (1st format): " + rowOriginal + " original col: " + colOriginal);

				if (noNamespace===true)
					outName=colName (colOriginal,toUpperCase)+rowOriginal;
				else
					outName=pieces [0]+"."+colName (colOriginal,toUpperCase)+rowOriginal;

				pointer.ctatdebug ("Translated: " + outName);

				return (outName);
			}
		}
		else
		{
			if (inName.indexOf ("r")===0) // In other words if it is the first character
			{
				var columnIndex=inName.indexOf ("c");

				var rowRaw=parseInt (inName.substr (1,columnIndex-1));
				var colRaw=parseInt (inName.substr (columnIndex+1));

				rowOriginal=rowRaw+1;
				colOriginal=colRaw;

				pointer.ctatdebug ("Original row (2nd format) (columnindex: "+columnIndex+"): " +rowRaw +" -> "+ rowOriginal + ", original col: " + colRaw +" -> "+colOriginal);

				outName=colName (colOriginal,toUpperCase)+rowOriginal;

				pointer.ctatdebug ("Translated: " + outName);

				return (outName);
			}
			else
			{
				pointer.ctatdebug ("Info: incoming name does not need translation");
			}
		}

		return (inName);
	};
	/**
	*
	*/
	this.translateToCTAT=function translateToCTAT (inName)
	{
		if (passthrough===true)
		{
			return (inName);
		}

		var colRaw=(this.letterToColumn (inName.substr (0,1))-1);
		var rowRaw=(parseInt (inName.substr (1))-1);

		return ('R'+rowRaw+'C'+colRaw);
	};

	/**
	*
	*/
	this.columnToLetter=function columnToLetter(column)
	{
		var temp, letter = '';
		while (column > 0)
		{
			temp = (column - 1) % 26;
			letter = String.fromCharCode(temp + 65) + letter;
			column = (column - temp - 1) / 26;
		}

		return letter;
	};

	/**
	*
	*/
	this.letterToColumn=function letterToColumn(letter)
	{
		var column = 0, length = letter.length;

		for (var i = 0; i < length; i++)
		{
			column += (letter.charCodeAt(i) - 64) * Math.pow(26, length - i - 1);
		}

		return column;
	};

	/**
	*
	*/
	this.getA1Row=function getA1Row (inName)
	{
		var rowRaw=(parseInt (inName.substr (1))-1);

		return (rowRaw);
	};

	/**
	*
	*/
	this.getA1Col=function getA1Col (inName)
	{
		var colRaw=(this.letterToColumn (inName.substr (0,1))-1);

		return (colRaw);
	};
};

CTATNameTranslator.prototype = Object.create(CTATBase.prototype);
CTATNameTranslator.prototype.constructor = CTATNameTranslator;
