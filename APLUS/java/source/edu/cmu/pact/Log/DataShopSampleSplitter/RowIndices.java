package edu.cmu.pact.Log.DataShopSampleSplitter;

/** Class used to save the column numbers of the needed columns from the
 * tab-delimited data shop sample. */
class RowIndices{
	/** The index of the session id column in the DataShop sample */
	private int sessionID;

	/** The index of the user id column in the DataShop sample */
	private int userGuid;

	/** The index of the date and time column in the DataShop sample */
	private int dateTime;

	/** The index of the timezone column in the DataShop sample */
	private int timezone;

	/** The index of the problem name column in the DataShop sample */
	private int problemName;

	/** The index of the transaction name column in the DataShop sample */
	private int transactionName;

	/** The index of the action column in the DataShop sample */
	private int action;

	/** The index of the selection column in the DataShop sample */
	private int selection;

	/** The index of the input column in the DataShop sample */
	private int input;

	/** Constructor. Creates a RowIndices object. It initializes the private
	 * data members. */
	public RowIndices()
	{
		sessionID = userGuid = dateTime = timezone = -1;
		problemName = transactionName = action = selection = input = -1;
	}

	/** Returns the index of the session id column in the DataShop sample.
	 * @return the index of the session id column in the DataShop sample */
	public int getSessionID()
	{
		return sessionID;
	}

	/** Returns the index of the user id column in the DataShop sample.
	 * @return the index of the user id column in the DataShop sample */
	public int getUserGuid()
	{
		return userGuid;
	}

	/** Returns the index of the date and time column in the DataShop sample.
	 * @return the index of the date and time column in the DataShop sample */
	public int getDateTime()
	{
		return dateTime;
	}

	/** Returns the index of the timezone column in the DataShop sample.
	 * @return the index of the timezone column in the DataShop sample */
	public int getTimezone()
	{
		return timezone;
	}

	/** Returns the index of the problem name column in the DataShop sample.
	 * @return the index of the problem name column in the DataShop sample */
	public int getProblemName()
	{
		return problemName;
	}

	/** Returns the index of the transaction name column in the DataShop sample.
	 * @return the index of the transaction name column in the DataShop sample */
	public int getTransactionName()
	{
		return transactionName;
	}

	/** Returns the index of the action column in the DataShop sample.
	 * @return the index of the action column in the DataShop sample */
	public int getAction()
	{
		return action;
	}

	/** Returns the index of the selection column in the DataShop sample.
	 * @return the index of the selection column in the DataShop sample */
	public int getSelection()
	{
		return selection;
	}

	/** Returns the index of the input column in the DataShop sample.
	 * @return the index of the input column in the DataShop sample */
	public int getInput()
	{
		return input;
	}

	/** Sets the index of the session id column in the DataShop sample.
	 * @param inSessionID the index of the session id column in the DataShop sample */
	public void setSessionID( int inSessionID )
	{
		sessionID = inSessionID;
	}

	/** Sets the index of the user id column in the DataShop sample.
	 * @param inUserGuid the index of the user id column in the DataShop sample */
	public void setUserGuid(int inUserGuid)
	{
		userGuid = inUserGuid;
	}

	/** Sets the index of the date and time column in the DataShop sample.
	 * @param inDateTime the index of the date and time column in the DataShop sample */
	public void setDateTime( int inDateTime )
	{
		dateTime = inDateTime;
	}

	/** Sets the index of the timezone column in the DataShop sample.
	 * @param inTimezone the index of the timezone column in the DataShop sample */
	public void setTimezone( int inTimezone)
	{
		timezone = inTimezone;
	}

	/** Sets the index of the problem name column in the DataShop sample.
	 * @param inProblemName the index of the problem name column in the DataShop sample */
	public void setProblemName( int inProblemName )
	{
		problemName = inProblemName;
	}

	/** Sets the index of the transaction name column in the DataShop sample.
	 * @param inTransactionName the index of the transaction name column in the
	 * DataShop sample */
	public void setTransactionName( int inTransactionName )
	{
		transactionName = inTransactionName;
	}

	/** Sets the index of the action column in the DataShop sample.
	 * @param inAction the index of the action column in the DataShop sample */
	public void setAction( int inAction )
	{
		action = inAction;
	}

	/** Sets the index of the selection column in the DataShop sample.
	 * @param inSelection the index of the selection column in the DataShop sample */
	public void setSelection( int inSelection )
	{
		selection = inSelection;
	}

	/** Sets the index of the input column in the DataShop sample.
	 * @param inInput the index of the input column in the DataShop sample */
	public void setInput( int inInput)
	{
		input = inInput;
	}
}