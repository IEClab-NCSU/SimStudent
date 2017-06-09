/**-----------------------------------------------------------------------------
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
*/

/**
*
*/
function typeOf(obj) 
{
  var type = typeof obj;
  return type === "object" && !obj ? "null" : type;
}

/**
*
*/
function exists (obj, name, type) 
{
  type = type || "function";
  return (obj ? this.typeOf(obj[name]) : "null") === type;
}

/**
 Introspects an object.

 @param name the object name.
 @param obj the object to introspect.
 @param indent the indentation (optional, defaults to "").
 @param levels the introspection nesting level (defaults to 1).
 @returns a plain text analysis of the object.
*/
function introspect(name, obj, indent, levels) 
{
	//console.log ("introspect ()");

	indent = indent || "";
  
	if (typeOf(levels) !== "number") 
	{
		levels = 1;
	}	
  
	var objType = typeOf(obj);
  
	var result = [indent, name, " ", objType, " :"].join('');
  
	if (objType === "object") 
	{
		if (levels > 0) 
		{
			indent = [indent, "  "].join('');
			
			var prop = null;
			
			for (prop in obj) 
			{
				if (obj.hasOwnProperty(prop)) 
				{
					var propString = introspect(prop, obj[prop], indent, levels - 1);
					result = [result, "\n", propString].join('');
				}	
			}
			
			return result;
		}
		else 
		{
			return [result, " ..."].join('');
		}
	}
	else if (objType === "null") 
	{
		return [result, " null"].join('');
	}
  
	return [result, " ", obj].join('');
}

