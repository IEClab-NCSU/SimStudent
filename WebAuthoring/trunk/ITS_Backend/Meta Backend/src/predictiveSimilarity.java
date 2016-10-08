package interaction;

//this policy maker looks at the difference in %correct over the next few problems and if the student
//shows only a small change in %correct then the policy will stop the student

public class predictiveSimilarity {

	public static double similarity_threshold = 0.01;  //originally 0.01
	public static double confidence_threshold = 0.95;     //originally 0.95
	public static int NUM_LOOK_AHEAD = 1;

	public static double predict_correct(double L, double G, double S, double T) {
		return ((L*(1.0-S)) + ((1.0-L)*G));
	}
	
	
	public static boolean stop(double L, double G, double S, double T) {
		//return (predict_mastery(L,G,S,T) >= mastery_threshold);
		double currentPC = predict_correct(L,G,S,T);
		double currentPW = 1.0 - currentPC;
		double tooClose = 0.0;
		if (currentPC > 0.0) {
			for (int i = 0; i < NUM_LOOK_AHEAD; i++) {
				L = update(L,G,S,T,1);
			}
			if (Math.abs(currentPC - predict_correct(L,G,S,T)) < similarity_threshold) {
					tooClose += currentPC;
			}
		}
		if (currentPW > 0.0) {
			for (int i = 0; i < NUM_LOOK_AHEAD; i++) {
				L = update(L,G,S,T,0);
			}
			if (Math.abs(currentPC - predict_correct(L,G,S,T)) < similarity_threshold) {
					tooClose += currentPW;
			}
		}
		return tooClose > confidence_threshold;
	}
	
	public static double update(double L, double G, double S, double T, int right) {
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		if (right == 1) {
			 return ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
		}
		else {
			 return (probL * S) / ((compL * (1.0-G)) + probL * S);
		}
	}
	
	public static double predict_mastery(double L, double G, double S, double T) {
		return L + ((1.0-L)*T);
	}

	
}
