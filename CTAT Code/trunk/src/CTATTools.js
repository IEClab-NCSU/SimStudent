/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATTools.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATTools');

goog.require('CTATGlobalFunctions');
/**
 *
 */
function s4()
{
  return Math.floor((1 + Math.random()) * 0x10000)
             .toString(16)
             .substring(1);
}

/**
 * @returns {String}
 */
function guid()
{
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
         s4() + '-' + s4() + s4() + s4();
}

/**
*
*/
/*
Object.size = function(obj)
{
    var size = 0, key;

    for (key in obj)
    {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};
*/

/**
*
*/
function htmlEscape(str)
{
    return String(str)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
}

/**
*
*/
function htmlUnescape(value)
{
    return String(value)
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&');
}

/**
*
*/
//function isBlank(str) //in CTATGlobalFunctions.js
//{
//	return (!str || /^\s*$/.test(str));
//}

/**
* http://quocity.com/colresizable/
*/
function tableCreate()
{
    var body = document.body;
	var tbl  = document.createElement('table');
    tbl.style.width='100%';
    tbl.style.border = "1px solid black";

    for(var i = 0; i < 3; i++)
	{
        var tr = tbl.insertRow();

        for(var j = 0; j < 2; j++)
		{
            if(i==2 && j==1)
			{
                    break;
            }
			else
			{
                var td = tr.insertCell();

                td.appendChild(document.createTextNode('\u0020'));

                if(i==1&&j==1)
				{
                    td.setAttribute('rowSpan','2');
                }
            }
        }
    }

    body.appendChild(tbl);

	return (tbl);
}
