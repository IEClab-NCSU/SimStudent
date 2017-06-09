/* This function implements the ECMAScript 6 Iterator protocol. Used by Set.
 * Author: dhruv
 */
Iterator = function(o) {
    console.log("Iterator(" + o + ") constructor1, typeof(o) " + typeof(o));
    
    if(!(this instanceof arguments.callee))
      return new arguments.callee(o);
  
    var index = 0, keys = [];
    console.log("Iterator(" + o + ") constructor1, typeof(o) " + typeof(o));
    
    if(!o || typeof o != "object") return; //If it is not an object (includes arrays)
    
    if(typeof o.splice !=='undefined' && typeof o.join !=='undefined') { //If the object is an array
        
        while(keys.length < o.length) keys.push(o[keys.length]); //index keys range from 0 to length-1
        
    } else {
        
        for(p in o) if(o.hasOwnProperty(p)) keys.push(p);        
    }
    console.log("Iterator(" + o + ") constructor2, keys.length " + keys.length);
    
    //Successively returns the next element until the end, when it returns a 
    this.next = function next() {
        if(index < keys.length) {
            var key = keys[index++];
            return {value: o[key], done:false};
        } else return { done:true };
    };
}

Iterator.prototype = Object.create(Object.prototype);
Iterator.prototype.constructor = Iterator;
