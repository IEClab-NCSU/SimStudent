/** This object represents a CTATMatcherComparator. */

goog.provide('CTATMatcherComparator');
goog.require('CTATBase');
goog.require('CTATAnyMatcher');
goog.require('CTATExactMatcher');
goog.require('CTATExpressionMatcher');
goog.require('CTATRangeMatcher');
goog.require('CTATRegexMatcher');
goog.require('CTATVectorMatcher');
goog.require('CTATWildcardMatcher');

CTATMatcherComparator = function()
{
    CTATBase.call(this, "CTATMatcherComparator", "");

    /****************************** STATIC METHODS ****************************************************/

	/**
	 * Compare CTATMatcher instances for specificity. When comparing 2 CTATVectorMatchers,
	 * compare the individual element matchers in the order input, selection, action.
	 * @param m1 one CTATMatcher to compare
	 * @param m2 other CTATMatcher to compare
	 * @return 1, -1, 0 as m1 is more specific, less specific or neither, relative to m2
	 */
	CTATMatcherComparator.compare = function(m1, m2)
	{
		ctatdebug("CTATMatcherComparator--> in compare");

		var top = CTATMatcherComparator.compareBasedOnType(m1, m2);

		if(top !== 0) //compare main matchers
		{
			return top;
		}

		if((m1 instanceof CTATVectorMatcher) === false && (m2 instanceof CTATVectorMatcher) === false)
		{
			return top;
		}

		var input = CTATMatcherComparator.compareBasedOnType(m1.getSingleMatcher("input"), m2.getSingleMatcher("input"));

		if(input !== 0)
		{
			return input;
		}

		var selection = CTATMatcherComparator.compareBasedOnType(m1.getSingleMatcher("selection"), m2.getSingleMatcher("selection"));

		if(selection !== 0)
		{
			return selection;
		}

		return CTATMatcherComparator.compareBasedOnType(m1.getSingleMatcher("action"), m2.getSingleMatcher("action"));
	};

	/**
	 * @param object1 of type Matcher or one of it's subclasses
	 * @param object2 of type Matcher or one of it's subclasses
	 * @return integer
	 */
	CTATMatcherComparator.compareBasedOnType = function(object1, object2)
	{
		ctatdebug("CTATMatcherComparator --> in compareBasedOnType");

		for(var i in CTATMatcherComparator.matcherPrecedenceOrder)
		{
			if(CTATMatcherComparator.matcherPrecedenceOrder.hasOwnProperty(i))
			{
				if(object1 instanceof CTATMatcherComparator.matcherPrecedenceOrder[i])
				{
					break;
				}
			}
		}

		for(var j in CTATMatcherComparator.matcherPrecedenceOrder)
		{
			if(CTATMatcherComparator.matcherPrecedenceOrder.hasOwnProperty(j))
			{
				if(object2 instanceof CTATMatcherComparator.matcherPrecedenceOrder[j])
				{
					break;
				}
			}
		}

		if(i < j)
		{
			return 1;
		}
		else if (i > j)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	};
};

    /****************************** CONSTANTS ****************************************************/

    Object.defineProperty(CTATMatcherComparator, "matcherPrecedenceOrder", {enumerable: false, configurable: false, writable: false, value: {
        0 : CTATExactMatcher,
        1 : CTATRangeMatcher,
        2 : CTATExpressionMatcher,
        3 : CTATWildcardMatcher,
        4 : CTATRegexMatcher,
        5 : CTATAnyMatcher
    }});

CTATMatcherComparator.prototype = Object.create(CTATBase.prototype);
CTATMatcherComparator.prototype.constructor = CTATMatcherComparator;

if(typeof module !== 'undefined')
{
	module.exports = CTATMatcherComparator;
}

new CTATMatcherComparator();  // this step actually defines the static functions
