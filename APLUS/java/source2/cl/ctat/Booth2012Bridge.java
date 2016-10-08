/**
 * Copyright 2007 Carnegie Mellon University.
 */
package cl.ctat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cl.utilities.Logging.Logger;

import edu.cmu.hcii.ctat.CTATAppletFileManager;
import edu.cmu.hcii.ctat.CTATCLBridge;
import edu.cmu.hcii.ctat.CTATLink;

/**
 * Implementation of the interface to Carnegie Learning for Julie Booth's
 * Fall 2012 study.  
 */
public class Booth2012Bridge extends CTATCLBridge {

	/** Score for class "Correct Conventional". */
	private double cc = 0;
	/** Score for class "Incorrect Only". */
	private double io = 0;
	/** Score for class "Correct Only". */
	private double co = 0;
	/** Score for class "Both". */
	private double b  = 0;

	/**
	 * Pair each score with its proper Carnegie Learning class name.
	 */
	public class ScoreClass implements Comparable<ScoreClass> {
		Double score; String className;
		ScoreClass(double score, String className) {
			this.score = new Double(score); this.className = className;
		}
		public int compareTo(ScoreClass other) {
			return this.score.compareTo(other.score);
		}
		public String toString() {
			return "["+className+": "+score.toString()+"]";
		}
	}
	
	/**
	 * Associate a problem name with the score changes to effect due to the student's
	 * performance on that problem.
	 */
	abstract class ScoreDelta {
		final String problemName;
		ScoreDelta(String problemName) { this.problemName = problemName; }
		abstract void delta(int c);
	}

	/**
	 * Map to associate each problem name with its proper score change method.
	 */
	private Map<String, ScoreDelta> scoreDeltaMap = new HashMap<String, ScoreDelta>();
	{
		String pn;  // problem name
		pn = "8c"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { cc += 0.100; io -= 0.091; co -= 0.125; } }
		});
		pn = "8e"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { cc += 0.100; io -= 0.091; } }
		});
		pn = "9a"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io -= 0.091; } }
		});
		pn = "9c"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; cc += 0.100; co -= 0.125; } }
		});
		pn = "10d"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io -= 0.091; } }
		});
		pn = "11a"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { io += 0.091; cc += 0.100; co -= 0.125; } }
		});
		pn = "12a"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io -= 0.091; } }
		});
		pn = "12c"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { co += 0.125; cc += 0.100; io -= 0.091; } }
		});
		pn = "13a"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io += 0.091; cc += 0.100; co -= 0.125; } }
		});
		pn = "13b"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io += 0.091; cc += 0.100; co -= 0.125; } }
		});
		pn = "15a"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; co -= 0.125; io -= 0.091; cc -= 0.100; } }
		});
		pn = "15d"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { b  += 0.111; io -= 0.091; cc -= 0.100; } }
		});
		pn = "15e"; scoreDeltaMap.put(pn, new ScoreDelta(pn) {
			void delta(int c) { if (c < 2) { co += 0.125; cc += 0.100; io -= 0.091; } }
		});
	}

	/** Count calls to {@link #problemEnd(String)}. */
	private int problemCount = 0; 
	
	/** List of problem summaries for {@link #calculateProperClass(List)}. */
	private List<String> problemSummaries = new LinkedList<String>();
	
	/**
	 * Factory to mimic action of {@link CTATCLBridge#main(String[])}
	 * @return new instance
	 */
	public static Booth2012Bridge create() {
		CTATLink link =          // necessary to allow for decryption of the config file
				new CTATLink(new CTATAppletFileManager());

		Booth2012Bridge service=new Booth2012Bridge();
    	return service;
	}

	/** Result of {@link #problemSetEnd(List)}. */
	private volatile String targetClass = null;
	
	/**
	 * A thread wanting this result from the local TutorShop should {@link #wait()}
	 * on this object until this method returns non-null. 
	 * @return the {@link #targetClass}
	 */
	public synchronized String getTargetClass() {
		return targetClass;
	}

	/**
	 * Call {@link #calculateProperClass(List<Element>)} to calculate the desired class
	 * from ProblemSummary results. Sets {@link #targetClass}. Calls {@link #notify()}
	 * to awake any thread waiting for {@link #getTargetClass()} to be available.
	 * @param problemSummaries
	 * @return true because the caller can now exit, for here we want only one problem set
	 */
	public synchronized boolean problemSetEnd(List<String> problemSummaries)
	{
		Logger.log("launcher", "problemSetEnd() nProblemSummaries: "+problemSummaries.size());
		List<Element> psElts = null;
		try {
			psElts = psListToElements(problemSummaries);
			targetClass = calculateProperClass(psElts);
		} catch (Exception e) {
			Logger.log("launcher", "B2B.problemSetEnd() error from psListToElements(): "+e+
					";\n  cause "+e.getCause()+"; problemSummaries:\n  "+problemSummaries);
			e.printStackTrace(System.out);
			targetClass = (String) JOptionPane.showInputDialog(null,
					"Error getting results from prescreen problems: "+e+
					". Please ask your teacher to choose your proper class: ",
					"Error getting results from prescreen problems",
					JOptionPane.QUESTION_MESSAGE, null,
					targetClassNames, targetClassNames[0]);
			Logger.log("launcher", "B2B.problemSetEnd() choice after error"+targetClass);
		}
		notify();  // create() caller is waiting for targetClass to be available 
		return true;
	}

	/**
	 * Parse a list of XML strings into a list of ProblemSummary XML elements.
	 * @param problemSummaries list of XML strings, each an individual ProblemSummary element
	 * @return list &lt;ProblemSummary&gt; Elements 
	 * @throws Exception
	 */
	private List<Element> psListToElements(List<String> problemSummaries)
			throws Exception {
		StringBuffer problemSummariesDoc =
				new StringBuffer("<?xml version=\"1.0\"?>\n<root>\n");
		for (String s : problemSummaries)
			problemSummariesDoc.append("  ").append(s).append("\n");
		problemSummariesDoc.append("</root>\n");

		StringReader rdr = new StringReader(problemSummariesDoc.toString());
		SAXBuilder builder = new SAXBuilder();	
		Document doc = builder.build(rdr);
		Element root = doc.getRootElement();

		List<Element> psElts = (List<Element>) root.getChildren("ProblemSummary");
		return psElts;
	}

	/** Target class names for {@link #calculateProperClass(List)} to choose from. */
	private static final String[] targetClassNames = {"A1", "A2", "G"};
	
	/** List of score-class pairs, the result of the student's performance calculations. */
	private List<ScoreClass> scoreClasses = new ArrayList<ScoreClass>();

	/**
	 * Get the score-class pairs, the result of the student's performance calculations.
	 * The student will be enrolled in the class whose score is largest.
	 * @return the {@link #scoreClasses}
	 */
	public List<ScoreClass> getScoreClasses() {
		return scoreClasses;
	}

	/**
	 * Calculate the proper class from the student's performance on a problem set.
	 * @param problemSummaries details of student performance on each problem
	 * @return entry from {@link #targetClassNames}
	 */
	private String calculateProperClass(List<Element> psElts)
			throws Exception {
		for (Element elt : psElts) {
			String problemName = elt.getAttributeValue("ProblemName");
			String uniqueCorrect = elt.getAttributeValue("UniqueCorrect");
			int correct = 0;
			try {
				correct = Integer.parseInt(uniqueCorrect);
			} catch(Exception e) {
				throw new IllegalArgumentException("Error converting problem "+problemName+
						" UniqueCorrect \""+uniqueCorrect+"\" to integer");
			}
			ScoreDelta sd = scoreDeltaMap.get(problemName);
			if (sd == null)
				throw new IllegalArgumentException("Unexpected problem name \""+problemName+"\"");
			sd.delta(correct);
		}

		scoreClasses.add(new ScoreClass(cc, "Correct Conventional"));
		scoreClasses.add(new ScoreClass(io, "Incorrect Only"));
		scoreClasses.add(new ScoreClass(co, "Correct Only"));
		scoreClasses.add(new ScoreClass(b,  "Both"));

		Collections.sort(scoreClasses, Collections.reverseOrder());  // reverse: want largest 1st
		Logger.log("launcher", "B2B.calculateProperClass() scoreClasses "+scoreClasses);
		
		if (scoreClasses.get(0).score.doubleValue() - scoreClasses.get(1).score.doubleValue() <= 2*Double.MIN_NORMAL)
			return "Incorrect Only";     // default in case of tie scores
		else
			return scoreClasses.get(0).className;
	}
	
	/**
	 * Number of problems in the problem set, for {@link #problemEnd(String)}.
	 * @return number of entries in {@link #scoreDeltaMap}
	 */
	private int totalProblemCount() {
		return scoreDeltaMap.size();
	}
	
	/**
	 * Increment {@link #problemCount} and append to {@link #problemSummaries}.
	 * Temporary: Call {@link #problemSetEnd(List)} when problemCount reaches
	 * {@link #totalProblemCount} ({@value #totalProblemCount}). 
	 * @param problemSummary
	 * @return true if the caller can now exit (for its work is done); else false
	 */
	public boolean problemEnd(String problemSummary) 
	{
		Logger.log("launcher", "problemEnd() problemSummary["+problemCount+"]:\n"+problemSummary);
		problemSummaries.add(problemSummary);
		if (++problemCount >= totalProblemCount())
			return problemSetEnd(problemSummaries);  // FIXME remove when problem set notice ok
		return false;
	}
}
