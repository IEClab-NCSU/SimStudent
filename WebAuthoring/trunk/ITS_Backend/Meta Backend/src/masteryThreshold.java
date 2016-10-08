package interaction;

public class masteryThreshold {
	
	public static double mastery_threshold = 0.95; //MASTERY THRESHOLD VALUE 
	
	public static boolean stop(double L, double G, double S, double T) {
		return L >= mastery_threshold;
	}

}
