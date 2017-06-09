goog.provide('CTATNoolsSessionManager');

CTATNoolsSessionManager = function()
{
	var sessions = {};
	var sessionCnt = 0;
	
	this.createSession = function(flow)
	{
		var sId = null;
		var newSession = flow.getSession();
		if (newSession)
		{
			sId = ++sessionCnt;
			sessions[sId] = newSession; 
		}
		return {session: newSession, id: sId};
	}
	
	this.getSession = function(id)
	{
		if (id && sessions[id])
		{
			return sessions[id];
		}
		return false;
	}
}