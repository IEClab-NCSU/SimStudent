/* This source file creates the CTATHintPolicyEnum used by the Javascript Tracer. */

goog.provide('CTATHintPolicyEnum');

/* LastModified: sewall 2014/10/31 */
CTATHintPolicyEnum = function() {
};
Object.defineProperty(CTATHintPolicyEnum, "HINTS_UNBIASED", {enumerable: false, configurable: false, writable: false, value: "Always Follow Best Path"});
Object.defineProperty(CTATHintPolicyEnum, "HINTS_BIASED_BY_CURRENT_SELECTION_ONLY", {enumerable: false, configurable: false, writable: false, value: "Bias Hints by Current Selection Only"});
Object.defineProperty(CTATHintPolicyEnum, "HINTS_BIASED_BY_PRIOR_ERROR_ONLY", {enumerable: false, configurable: false, writable: false, value: "Bias Hints by Prior Error Only"});
Object.defineProperty(CTATHintPolicyEnum, "HINTS_BIASED_BY_ALL", {enumerable: false, configurable: false, writable: false, value: "Use Both Kinds of Bias"});

CTATHintPolicyEnum.prototype = Object.create(Object.prototype);
CTATHintPolicyEnum.prototype.constructor = CTATHintPolicyEnum;

if(typeof module !== 'undefined')
{
	module.exports = CTATHintPolicyEnum;
}
