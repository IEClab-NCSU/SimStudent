/**
 * Copyright 2007-2012 Carnegie Mellon University.
 */
package edu.cmu.hcii.login;

/**
 * Categorize the possible results of a login attempt. 
 */
public enum LoginResult {
	Success("Success"),
	InvalidUserid("Your userid is invalid"),
	DisabledUserid("Your userid has been disabled"),
	InvalidPassword("Your password did not match the recorded one"),
	MissingPassword("You need to enter a password"),
	ServerErrorRetry("There was a system error; please retry your login"),
	ServerErrorNoRetry("There is a system error; please tell an administrator"),
	ClientErrorNoRetry("There was an error with your program; please tell an administrator"),
	ServiceNotAvailable("The system is not available now; please retry later"),
	AlreadyLoggedIn("You are already logged in; please logout first");
	
	/** Text to display to the user describing this result. */
	private String userText;

	/**
	 * Same as {@link #LoginResult(String) LoginResult(null)}.
	 */
	private LoginResult() {
		this(null);
	}

	/**
	 * Record a user text with the enum value.
	 * @param userText if null, uses {@link #toString()}
	 */
	private LoginResult(String userText) {
		this.userText = (userText == null ? toString() : userText); 
	}

	/**
	 * @return the {@link #userText}
	 */
	public String getUserText() {
		return userText;
	}
}
