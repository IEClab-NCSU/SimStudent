package edu.cmu.pact.miss.PeerLearning.GameShow;

public class Competitor {

		String name;
		String img;
		int rating;
		int wins;
		int losses;
		int ties;
		String userid;
		boolean stat = false;
		String classroom;
		
		public Competitor(String name, String img, String user)
		{

			this.name = name;
			this.img = img;
			userid = user;
			losses = 0;
			wins = 0;
			ties = 0;
			rating = 25;
		}
	
		@Override
		public String toString()
		{
			return name+","+img+","+rating+","+wins+","+losses+","+ties+","+userid+","+classroom;
		}
		
		public String stats()
		{
			return rating+"("+wins+"/"+losses+"/"+ties+")";
		}

}
