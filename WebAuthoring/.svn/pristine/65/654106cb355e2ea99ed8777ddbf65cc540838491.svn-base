/**
 * Javascript implementation of Sets.
 * Method names used are the same as the ones used in the upcmoming ECMAScript Standard.
 * 
 * dhruv
 */
Set = function(obj) {                                 
    this._values = {};                         
    this.size = 0;
    
    if (obj !== undefined)
    {
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                this.add(obj[key]);
            }
        }
    }
};
Set.prototype = Object.create(Object.prototype);

/**
 * Add each of the arguments to the set.
 * @param element... new element(s) to add
 * @return this Set object, for chained method calls
 */
Set.prototype.add = function() {
    for(var i = 0; i < arguments.length; i++) { 
        var val = arguments[i];                  
        var str = Set._stringify(val);                 
        if (!this._values.hasOwnProperty(str)) {  
            this._values[str] = val;              
            this.size++;                           
        }
    }
    return this;                                
};

/**
 * Remove each of the arguments from the set.
 * @param element... element(s) to delete
 * @return this Set object, for chained method calls
 */
Set.prototype.delete = function() {
    for(var i = 0; i < arguments.length; i++) {  
        var str = Set._stringify(arguments[i]);        
        if (this._values.hasOwnProperty(str)) {   
            delete this._values[str];             
            this.size--;                            
        }
    }
    return this;                                 
};

/**
 * Check whether the set contains the given value.
 * @param value 
 * @return true if the set contains value; false otherwise.
 */
Set.prototype.has = function(value) {
    return this._values.hasOwnProperty(Set._stringify(value));
};



/**
 * Call function f on the specified context for each element of the set.
 * @param f function to call
 * @param context object (this) pointer for function calls
 */
Set.prototype.forEach = function(f, context) {
    for(var s in this._values)                    
        if (this._values.hasOwnProperty(s))       
            f.call(context, this._values[s]);     
};

/**
 * Returns an Iterator with all the elements of the set. 
 * @return An object that implements the Iterator interface.
 */

Set.prototype.values  = function(){
    return new Iterator(this._values);
};

Set.prototype.keys = function(){
    return new Iterator(this._values);
};

/**
 * This internal function maps any JavaScript value to a unique string.
 * @param val value to map
 * @return unique string, effectively an object serial number
 */
Set._stringify = function(val) {
    switch(val) {
        case undefined: return 'u';             
        case null: return 'n';                   
        case true: return 't';                   
        case false: return 'f';
        default: switch(typeof val) {
            case 'number': return '#' + val;     
            case 'string': return '"' + val;     
            default: return '@' + objectId(val); 
        }
    }
   
    function objectId(o) {
        var prop = "CTAT_SET_OBJECT_ID";             // Private property name for storing ids on the object itself
        if (!o.hasOwnProperty(prop))             
            o[prop] = Set._stringify.next++;           
        return o[prop];                          
    }
};
Set._stringify.next = 100;                             //Start assigning IDs starting from this number

Set.prototype.constructor = Set;
