
/**
*
*/
var RequestQueue = function()
{
	//linked list queues requests
	var requests = new LinkedList();
	//interval timer which triggers request sends
	var execTimer;
	//time now, time last req sent, and wait time
	var thisReq, lastReq, gap; 
	//whether currently suspended due to rate limit exceed
	var iAmBackingOff = false;
	
	/**
	*	Add a request to the queue
	*	@param request the request object
	*	@param callback a function to execute on success
	*/
	this.add = function(request, callback, isBatch)
	{
		requests.addToBack({'request': request,
							'callback': callback,
							'isBatch': isBatch});
							
		if (!execTimer)
		{
			resetTimer();
		}
	};
	
	this.setGap = function(g)
	{
		gap = g;
	}
	
	/**
	*	Private, executes the next request from the head of the queue
	*/
	function execute()
	{
		//time 'now'
		thisReq += 20;
		
		if (thisReq - lastReq >= gap)
		{
			var toExecute = requests.getHead();
			if (toExecute)
			{
				toExecute.request.execute(handleResponse.bind(this, toExecute));
				lastReq = thisReq;
			}
			else
			{
				//no requests queued, clear timer
				window.clearInterval(execTimer);
				execTimer = null;
				gap = 20;
			}
		}
	}
	
	/**
	*	Private, handle a response to an executed request
	*	@param request the original request
	*	@param response the response
	*/
	function handleResponse (request, response)
	{
		var err = false;
		if (request.isBatch)
		{//batch requests signal errors differently
			for (var key in response)
			{
				if (response[key].error 
				&&  response[key].error.message.toLowerCase().includes('user rate limit exceeded'))
				{
					err = true;
					break;
				}
			}
		}
		else if (response.error 
			 &&  response.error.message.toLowerCase().includes('user rate limit exceeded'))
		{
			err = true;
		}
		if (err)
		{
			//add to front of queue and increase gap
			requests.addToFront(request);
			backoff();
		}
		else
		{
			request.callback(response);
		}
	}
	
	/**
	*	Private, set gap to default of 20 ms and init interval timer
	*/
	function resetTimer()
	{
		execTimer = window.setInterval(execute, 20);
		lastReq = 0;
		thisReq = 0;
		gap = 20;
	}
	
	/**
	*	Called when rate limit is exceeded
	*	Doubles interval between requests and waits 500ms before resuming
	*/
	function backoff()
	{
		if (iAmBackingOff) //don't stack
			return;
		
		iAmBackingOff = true;
		gap *= 2;
		console.log('RequestQueue hit rate limit, increasing gap to '+gap);	
		//suspend for 500 ms
		thisReq = lastReq + (gap-20);
		window.clearInterval(execTimer);
		window.setTimeout(function() {
			iAmBackingOff = false;
			execTimer = window.setInterval(execute, 20);
		}, 500);
	}
};