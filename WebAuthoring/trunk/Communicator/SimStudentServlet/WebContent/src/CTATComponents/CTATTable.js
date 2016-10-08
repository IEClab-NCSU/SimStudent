/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATTable.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

	http://quocity.com/colresizable/

 */
goog.provide('CTATTable');

goog.require('CTATCompBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATTable = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATCompBase.call(this,
					  "CTATTable",
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	var pointer=this;
	pointer.isTabIndexable=false;
	var table=null;
	var nrRows=2;
	var nrColumns=2;
	var nameCheck="";
	var headerHeight=25;

	this.init=function init()
	{
    pointer.ctatdebug("init (" + pointer.getName() + ")");

    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

		nameCheck=CTATGlobalFunctions.gensym.div_id();

		var body=document.body;
		table=document.createElement('table');
		table.style.width='100%';
	    table.setAttribute('id', nameCheck);
		table.setAttribute('class', 'resizable');

	    pointer.setComponent(table);
	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
	    pointer.setInitialized(true);
	    pointer.addComponentReference(pointer, table);
	    pointer.getDivWrap().appendChild(table);

		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");

		pointer.modifyCSSAttribute("border", "0px");
		pointer.modifyCSSAttribute("border-spacing", "0px");
		pointer.modifyCSSAttribute("border-collapse", "separate");

	    pointer.render();

	    //currentZIndex++;
	    //currentIDIndex++;
	};
	/**
	 *
	 */
	this.configFromDescription=function configFromDescription ()
	{
		pointer.ctatdebug ("configFromDescription ()");

	};
	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		//useDebugging=true;

		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...



		// Process component custom styles ...

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if(aStyle.styleName=="num_rows")
			{
				nrRows=parseInt(aStyle.styleValue);

				pointer.ctatdebug ("Setting number of rows to: " + aStyle.styleValue);
			}

			if(aStyle.styleName=="num_cols")
			{
				nrColumns=parseInt(aStyle.styleValue);

				pointer.ctatdebug ("Setting number of columns to: " + aStyle.styleValue);
			}

			if(aStyle.styleName=="HeaderHeight")
			{
				headerHeight=parseInt(aStyle.styleValue);
				pointer.ctatdebug ("Setting the header height to: " + aStyle.styleValue);
			}
		}

		//useDebugging=false;
	};
	/**
	* This method is called from CTATTutor in postProcess and will grab all
	* the separate text input components and reparents them in the appropriate
	* table cells.
	*
	* Uses http://quocity.com/colresizable/
	*/
	this.adjustTableContents=function adjustTableContents ()
	{
		//useDebugging=true;

		pointer.ctatdebug ("adjustTableContents()");

		//listComponentReferences ();

		var head=null;
		var tr=null;
		var td=null;
		var cellHeight = (pointer.getHeight() - headerHeight)/(nrRows-1);
		var cellWidth = Math.round(pointer.getWidth()/nrColumns);
		console.log(pointer.getWidth() + ' ' + nrRows + ' ' +cellWidth);

		//>-----------------------------------------------------------------------------
		// First create the table, this allows it to resize to a stable configuration...

		for (var i=0;i<nrRows;i++)
		{
			if (i==0)
			{
				head=table.createTHead();
				tr=head.insertRow();
			}
			else
			{	head=table.createTBody();
				tr=head.insertRow();
			}

			for (var j=0;j<nrColumns;j++)
			{
				/*
				var r = Math.floor(Math.random() * 256);
				var g = Math.floor(Math.random() * 256);
				var b = Math.floor(Math.random() * 256);
				*/
				var r=255;
				var g=255;
				var b=255;

				var rgb = "rgb("+r +','+g+','+b+')';
				var style = 'border: 1px solid black; padding: 0px; margin: 0px;background-color: '+rgb+';';

				if (i==0)
				{
					td=document.createElement('th');
					tr.appendChild(td);
					td.setAttribute('style',style+'height: '+headerHeight+'px;');
				}
				else
				{
					td=tr.insertCell();
					td.setAttribute('style',style+'height: '+cellHeight+'px');
				}
			}
		}

		//>-----------------------------------------------------------------------------
		// Make the table resizable

		//pointer.ctatdebug ('Making table colums (#'+nameCheck+') resizable ...');
		//$('#'+nameCheck).colResizable();
		//$('table').colResizable();

		//>-----------------------------------------------------------------------------
		// Get the cell size


		pointer.ctatdebug ("Determined cell size to be: " + cellWidth + "x" + cellHeight);

		var missingCells="";

		//>-----------------------------------------------------------------------------
		// Relocate the text components into the table cells ...

		var heightR=0;
		for (var i = 0, row; row = table.rows[i]; i++)
		{
			var row = table.rows[i];

			for (var j = 0, cell; cell = row.cells[j]; j++)
			{
				var cell = row.cells[j];
				cellHeight=cell.offsetHeight;

				var formatted=(pointer.getName()+".R"+i+"C"+j);

				var comp=findComponent (formatted);

				if (comp!=null)
				{
					var desc=pointer.getGrDescription ();
					desc.name=formatted; // overwrite the name to make sure we go from tablename to tablename.cellname

					comp.setCellContainer (cell);
					comp.setGrDescription (desc);
					comp.configFromDescription ();

					var divver=comp.getDivWrap();

					if (divver!=null)
					{
						divver.parentNode.removeChild (divver);

						cell.appendChild (divver);

						comp.setY(heightR);
						comp.setStyleAll ("position","absolute");

						//border size is always 1px
						comp.setSize(cellWidth-2-comp.getPadding()*2,cellHeight-2-comp.getPadding()*2);
						comp.render();
					}
					else
					{
						pointer.ctatdebug ("Error: unable to get div wrapper from component");
					}
				}
				else
				{
					missingCells+=(' ,'+formatted);
				}
			}

			heightR+=cellHeight;
		}

		if (missingCells!="")
		{
			pointer.ctatdebug ("The following cells could not be mapped to components: " + missingCells);
		}

		//>-----------------------------------------------------------------------------

		// All done

		//useDebugging=false;
	};
}

CTATTable.prototype = Object.create(CTATCompBase.prototype);
CTATTable.prototype.constructor = CTATTable;

CTAT.ComponentRegistry.addComponentType('CTATTable',CTATTable);