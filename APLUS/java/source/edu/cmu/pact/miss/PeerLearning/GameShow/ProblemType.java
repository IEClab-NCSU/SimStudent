package edu.cmu.pact.miss.PeerLearning.GameShow;

/*
 * A class to track the statistics of correctness for a problem type
 */
public class ProblemType
{
	public String type;
	int attemptCount;
	int successCount;
	
	public ProblemType(String type)
	{
		this.type = type;
	}
	
	public ProblemType(String type, int attempts,int successes)
	{
		this.type = type;
		attemptCount = attempts;
		successCount = successes;
	}
	
	public int getNumAttempts()
	{
		return attemptCount;
	}
	
	public void addAttempts(int attempts, int successes)
	{
		attemptCount += attempts;
		successCount += successes;
	}
	
	@Override
	public String toString()
	{
		return type+","+attemptCount+","+successCount;
	}
}