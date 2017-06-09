function ClassB() 
{

	var myVar = 'test';
	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	function _get ()
	{
		return myVar;
	};

	this.get = function ()
	{
		return _get();
	};


}

module.exports = ClassB;