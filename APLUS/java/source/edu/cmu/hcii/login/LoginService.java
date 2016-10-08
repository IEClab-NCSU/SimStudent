/**
 * Copyright 2007-2012 Carnegie Mellon University.
 */
package edu.cmu.hcii.login;

/**
 * Classes that perform logins should implement this interface. The implementors
 * may authenticate the login credentials themselves or communicate with a remote
 * service. See {@link LoginResult} for the kinds of responses to expect.
 */
public interface LoginService {

	/**
	 * Models a student or user record for this login service.
	 */
	public interface User {
		
		/**
		 * Retrieve the user's unique identifier. 
		 * @return userid; null if there is no information about this user
		 */
		public String getUserid();
	}

	/**
	 * Attempt a login with the given userid and password.
	 * @param userid identifies the user
	 * @param password may be null if no password is needed
	 * @param specificReply if not null and not empty, return in the first element
	 *        any user-specific response; e.g., "Your password expires in 10 days"
	 * @return server's result from the login attempt
	 */
	public LoginResult login(String userid, String password, String[] specificReply);

	/**
	 * Terminate a login session.
	 * @param userid
	 * @param specificReply
	 * @return
	 */
	public LoginResult logout(String userid, String[] specificReply);
}
