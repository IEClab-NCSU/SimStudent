/* This file overrides methods that exist only in a NodeJS environment with no-ops. Include this file when running the example tracer in a web browser environment. */
//goog.provide('NopeJS');

if (typeof(exports) === 'undefined')
{
	var exports = {};//FRANCESKA: It was new Object() I did {}
};

if (typeof(require) === 'undefined')
{
	require = function require(path)
	{
		return {};
	};
};



