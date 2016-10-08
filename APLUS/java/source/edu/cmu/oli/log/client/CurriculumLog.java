/*
 * Created on Apr 6, 2004
 * 
 */
package edu.cmu.oli.log.client;

/**
 * @author mfeng
 * 
 */
public class CurriculumLog extends Log {

	public CurriculumLog() {
		
		_session_id = new String("");
		_auth_token = new String("");
		_user_guid = new String("");
		_action = new String("");
		_name = new String("");
		_timestamp = new String("");
		_class_name = new String("");
		_brd_name = new String("");
		_parameters = "";
		
	}

	public Boolean setSessionId(String session_id) {
		_session_id = session_id;
		return Boolean.TRUE;
	}

	public String getSessionId() {
		return _session_id;
	}

	public Boolean setAuthToken(String authToken) {
		_auth_token = authToken;
		return Boolean.TRUE;
	}

	public String getAuthToken() {
		return _auth_token;
	}

	public Boolean setUserGuid(String userGuid) {
		_user_guid = userGuid;
		return Boolean.TRUE;
	}

	public String getUserGuid() {
		return _user_guid;
	}

	public Boolean setAction(String action) {
		_action = action;
		return Boolean.TRUE;
	}

	public String getAction() {
		return _action;
	}

	public Boolean setName(String name) {
		_name = name;
		return Boolean.TRUE;
	}

	public String getName() {
		return _name;
	}
	
	public Boolean setTimestamp(String timestamp) {
		_timestamp = timestamp;
		return Boolean.TRUE;
	}

	public String getTimestamp() {
		return _timestamp;
	}

	public Boolean setClassName(String class_name) {
		_class_name = class_name;
		return Boolean.TRUE;
	}

	public String getClassName() {
		return _class_name;
	}

	public Boolean setBrdName(String brd_name) {
		_brd_name = brd_name;
		return Boolean.TRUE;
	}

	public String getBrdName() {
		return _brd_name;
	}

	protected String _session_id;
	protected String _action_id;
	protected String _action;
	protected String _name;
	protected String _class_name;
	protected String _brd_name;
	protected String _timestamp;
	protected String _parameters;
	/**
	 * @param parameters
	 */
	public Boolean setParameters(String param) {
		// TODO Auto-generated method stub
		this._parameters = param;
		return Boolean.TRUE;
	}

	public String getParameters() {
		return _parameters;
	}
	
	
}
