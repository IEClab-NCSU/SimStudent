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
 
	http://quocity.com/colresizable/
 
*/


/**
 *  
 */
function CTATTable (aDescription,aX,aY,aWidth,aHeight)
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
	var table=null;
	var nrRows=2;
	var nrColumns=2;
	var nameCheck="";
	var headerHeight=25;
	
	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");
		
	    pointer.addCSSAttribute("z-index", currentZIndex);
		
		nameCheck=('ctatdiv' + currentIDIndex);
		
		var body=document.body;
		table=document.createElement('table');
		table.style.width='100%';		
	    table.setAttribute('id', nameCheck);
		table.setAttribute('class', 'resizable');
	    
	    pointer.setComponent(table);
	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
	    pointer.setInitialized(true);	    
	    pointer.addComponentReference(pointer, table);
	    pointer.getDivWrap().appendChild(table);

		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");
		
		pointer.modifyCSSAttribute("border", "0px");
		pointer.modifyCSSAttribute("border-spacing", "0px");
		pointer.modifyCSSAttribute("border-collapse", "separate");
		
	    pointer.render();
		
	    currentZIndex++;
	    currentIDIndex++;
	};
	/**
	 * 
	 */
	this.configFromDescription=function configFromDescription ()
	{
		pointer.debug ("configFromDescription ()");
		
	};
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		//useDebugging=true;
	
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		
		
		// Process component custom styles ...		

		this.styles=pointer.getGrDescription().styles;
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if(aStyle.styleName=="num_rows")
			{
				nrRows=aStyle.styleValue;
				
				pointer.debug ("Setting number of rows to: " + aStyle.styleValue);
			}			
			
			if(aStyle.styleName=="num_cols")
			{
				nrColumns=aStyle.styleValue;
				
				pointer.debug ("Setting number of columns to: " + aStyle.styleValue);
			}	

			if(aStyle.styleName=="HeaderHeight")
			{
				headerHeight=aStyle.styleValue;
				
				pointer.debug ("Setting the header height to: " + aStyle.styleValue);
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
					
		pointer.debug ("adjustTableContents()");
		
		//listComponentReferences ();
				
		var head=null;
		var tr=null;		

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
				if (i==0)
				{
					td=document.createElement('th');
					tr.appendChild(td);
				}
				else	
					td=tr.insertCell();
										
				td.setAttribute('style', 'border: 1px solid black; padding: 0px; margin: 0px;');														
			}
		}
		
		//>-----------------------------------------------------------------------------
		// Make the table resizable
		
		//pointer.debug ('Making table colums (#'+nameCheck+') resizable ...');
		//$('#'+nameCheck).colResizable();
		//$('table').colResizable();
				
		//>-----------------------------------------------------------------------------
		// Get the cell size	

		var row = table.rows[0];
		var cell = row.cells[0];
		
		var cellWidth=cell.offsetWidth;
		var cellHeight=cell.offsetHeight;

		pointer.debug ("Determined cell size to be: " + cellWidth + "x" + cellHeight);

		var missingCells="";
		
		//>-----------------------------------------------------------------------------
		// Relocate the text components into the table cells ...
		
		for (var i = 0, row; row = table.rows[i]; i++) 
		{
			for (var j = 0, cell; cell = row.cells[j]; j++) 
			{		
				var formatted=(pointer.getName()+".R"+i+"C"+j);
								
				var comp=findComponent (formatted);
				
				if (comp!=null)
				{				
					comp.setCellContainer (cell);
				
					var divver=comp.getDivWrap();
					
					if (divver!=null)
					{						
						divver.parentNode.removeChild (divver);
						
						cell.appendChild (divver);

						//debug ("Resizing to: " + cellWidth + "," + cellWidth);
						
						comp.move (0,0);						
						comp.setStyleAll ("position","relative");
						comp.setSize (cellWidth-4,cellHeight-4);
					}
					else
					{
						pointer.debug ("Error: unable to get div wrapper from component");
					}	
				}
				else
				{
					missingCells+=(' ,'+formatted);
				}
			}  
		}
		
		if (missingCells!="")
		{
			pointer.debug ("The following cells could not be mapped to components: " + missingCells);
		}
		
		//>-----------------------------------------------------------------------------
		
		// All done
		
		//useDebugging=false;
	};
}

CTATTable.prototype = Object.create(CTATCompBase.prototype);
CTATTable.prototype.constructor = CTATTable;
