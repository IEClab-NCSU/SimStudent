/* This object represents a CTATRangeMatcher */

goog.provide('CTATRangeMatcher');
goog.require('CTATExampleTracerException');
goog.require('CTATMatcher');

/* LastModify: Dhruv Chand 07/31*/

/**
 * @param vector (of type integer)
 * @param value (of type string)
 */
CTATRangeMatcher = function(vector, value)
{
    //calling the constructor fo the super class from where we are inheriting
    CTATMatcher.call(this, true, vector, true);

    /**************************** PUBLIC INSTACE VARIABLES ******************************************************/

    /**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    var that = this; // used to make the object available to the private methods

    /***************************** PRIVATE METHODS *****************************************************/


    /***************************** PRIVILEDGED METHODS *****************************************************/

    /**
     * @param selection (of type array)
     * @param action (of type array)
     * @param input (of type array)
     * @param actor (of type string)
     * @return boolean
     */
    this.match = function(selection, action, input, actor)
    {
        
    };


    this.toString = function()
    {
            return "this is CTATRangeMatcher";
    };

    /**
     * This function can be used to initialize the matcher with a range string of the format [min,max]
     * @param {String} range
     * @returns {}
     */
    this.setMinMax = function(range)
    {
        if (range === null || range === undefined || range.length === 0)
        {
            minText = "0";
            maxText = "0";
        }
        else
        {
            var minIndex, commaIndex, maxIndex;
            minIndex = range.indexOf("[");
            commaIndex = range.indexOf(",", minIndex + 1);
            maxIndex = range.indexOf("]", commaIndex + 1);

            if (minIndex >= 0 && commaIndex >= 0 && maxIndex >= 0)
            {
                minText = range.substring(minIndex + 1, commaIndex);
                maxText = range.substring(commaIndex + 1, maxIndex);
            }

        }
    };

    this.setMin = function(minVal)
    {
        try
        {
            this.minimum = parseFloat(minVal);
        }
        catch (e)
        {
            throw("Number format exception while parsing minimum val: " + minVal);
        }

    };

    this.setMax = function(maxVal)
    {
        try
        {
            that.maximum = parseFloat(maxVal);
        }
        catch (e)
        {
            throw new CTATExampleTracerException("Number format exception while parsing minimum val.");
        }

    };


    this.matchSingle = function(s)
    {
	var answer;
        try
        {
           answer = parseFloat(s);
        }
        catch (e)
        {
            throw new CTATExampleTracerException("Number format exception while parsing .");
        }

        if (answer >= that.minimum && answer <= that.maximum)
            return true;
        else
            return false;
    };


/****************************** CONSTRUCTOR CALLS ****************************************************/
//constructor calls
this.setMinMax(value);

/****************************** PUBLIC METHODS ****************************************************/
};

//we are inheriting from the CTATMatcher object
CTATRangeMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATRangeMatcher.prototype.constructor = CTATRangeMatcher;

if(typeof module !== 'undefined')
{
    module.exports = CTATRangeMatcher;
}
