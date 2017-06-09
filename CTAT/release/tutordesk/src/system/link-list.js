
var LinkedList = function()
{
	var linkListNode = function(data)
	{
		this.data = data;
		this.prev = null;
		this.next = null;
	}
	var head = null;
	var tail = null;
	
	this.addToFront = function(data)
	{
		var newNode = new linkListNode(data);
		if (head)
		{
			newNode.next = head;
			head.prev = newNode;
		}
		head = newNode;
		if (!tail)
			tail = head;
	};
	
	this.addToBack = function(data)
	{
		var newNode = new linkListNode(data);
		if (tail)
		{
			tail.next = newNode;
			newNode.prev = tail;
		}
		tail = newNode;
		if (!head)
			head = tail;
	};
	
	this.getHead = function()
	{
		if (head)
		{
			var retVal = head.data;
			head = head.next;
			if (!head) 
				tail = head;
			return retVal;
		}
		return null;
	};
	
	this.getTail = function()
	{
		if (tail)
		{
			var retVal = tail.data;
			tail = tail.prev;
			if (!tail)
				head = tail;
			return retVal;
		}
		return null;
	};
}