/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Class for analyzing and comparing BRD files. Initial features include<ol>
 * <li>counting edges in one graph but not in another</li> 
 * <li>printing link traversal counts</li> 
 * </ol>
 */
public class ScanBRD {
	
	/**
	 * Holds the data we need from an <edge> element. Elaborate as needed.
	 */
	private class Link {
		
		/** Link identifier, from <uniqueID> element in <actionLabel>. */
		private final String name;
		
		/** Selection,action,input[,actor] from <matcher> element. */
		private String sai = "";

		/** Link traversal count from <traversalCount> element. */
		private int traversalCount;

		/**
		 * Create a Link
		 * @param elt DOM Element
		 * @param serialNo for debugging
		 */
		Link(Element elt, int serialNo) {
			Element actionLabel = elt.getChild("actionLabel");
			if (actionLabel == null)
				throw new IllegalArgumentException("actionLabel missing on link "+serialNo);
			name = actionLabel.getChildText("uniqueID");
			if (name == null)
				throw new IllegalArgumentException("uniqueID missing on link "+serialNo);
			Element matcher = actionLabel.getChild("matcher");
			StringBuffer sb = new StringBuffer();
			for (Object mpElt: matcher.getChildren("matcherParameter"))
				sb.append(", ").append(((Element) mpElt).getTextTrim());
			sai = sb.substring(2);
			String traversalCount = elt.getChildText("traversalCount");
			if (traversalCount == null)
				this.traversalCount = 0;
			else
				this.traversalCount = Integer.parseInt(traversalCount);
//			trace.out(this);
		}
		/**
		 * @return {@link Link#name}
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the {@link #traversalCount}
		 */
		private int getTraversalCount() {
			return traversalCount;
		}
		/**
		 * @return "[ {@link #name}: {@link #traversalCount} ]"
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "["+name+": traversalCount "+traversalCount+"]";
		}
		/**
		 * @return the {@link #sai}
		 */
		private String getSai() {
			return sai;
		}
	}
	
	/** Map of the permanent or pre-existing links. */
	private Map<String, Link> fixedLinkMap;
	
	/** Map of the current links. */
	private Map<String, Link> linkMap;
	
	/** Whether to show column headings in output. */
	private boolean showHeadings = false;
	
	/** Optional label for each line of output. */
	private String label = null;

	/**
	 * Builds the link maps from the given BRDs.
	 * @param brd BRD file for current graph to analyze
	 * @param brd0 reference BRD file for fixed or pre-existing graph
	 * @param showHeadings whether to show headings in print output
	 * @throws Exception
	 */
	public ScanBRD(File brd0, File brd, boolean showHeadings) throws Exception {
		fixedLinkMap = buildLinkMap(brd0);
		linkMap = buildLinkMap(brd);
		this.showHeadings = showHeadings;
	}

	/**
	 * @param traversalThreshold
	 * @return String of formated output
	 */
	public String countLinksOverTraversalThreshold(int traversalThreshold) {
		int totalLinks = 0;
		int addedLinks = 0;
		int traversedLinks = 0;
		StringWriter sResult = new StringWriter();
		PrintWriter result = new PrintWriter(sResult);
		if (showHeadings) {
			printLabelLabel(result);
			result.printf("%5s\t%5s\t%9s\n", "total", "added", "traversed");
		}
		for (Link link: linkMap.values()) {
			totalLinks++;
			String name = link.getName();
			if (fixedLinkMap.get(name) != null)
				continue;
			addedLinks++;
			if (link.getTraversalCount() < traversalThreshold)
				continue;
			traversedLinks++;
		}
		printLabel(result);
		result.printf("%5d\t%5d\t%9d\n", totalLinks, addedLinks, traversedLinks);
		return sResult.toString();
	}

	/**
	 * Print the label if there is one.
	 * @param pw
	 */
	private void printLabel(PrintWriter pw) {
		if (label != null)
			pw.printf("%s\t", label);
	}

	/**
	 * Print a label for the label if there is a label.
	 * @param pw
	 */
	private void printLabelLabel(PrintWriter pw) {
		if (label != null)
			pw.printf("%."+label.length()+"s\t", "label");
	}

	/**
	 * Print the traversal counts.
	 * @return String of formated output
	 */
	public String traversalCounts() {
		StringWriter sResult = new StringWriter();
		PrintWriter result = new PrintWriter(sResult);
		if (showHeadings) {
			printLabelLabel(result);
			result.printf("%5s\t%-50s\t%10s\n", "ID", "Selection,Action,Input", "traversals");
		}
		for (Link link: linkMap.values()) {
			printLabel(result);
			result.printf("%5s\t%-50.50s\t%10d\n", link.getName(), link.getSai(), link.getTraversalCount());
		}
		return sResult.toString();
	}

	/**
	 * Create a set of links from a BRD file.
	 * @param brd
	 * @return map with key=link id (as string), value=Link
	 * @throws IOException
	 * @throws JDOMException
	 */
	private Map<String, Link> buildLinkMap(File brd0) throws IOException, JDOMException {
		Document doc = getDoc(brd0);
		Element root = doc.getRootElement();
		Map<String, Link> result = new LinkedHashMap<String, Link>();
		int i = 0;
		for (Object obj: root.getChildren("edge") ) {
			Element elt = (Element) obj;
			Link link = new Link(elt, i);
			result.put(link.getName(), link);
			++i;
		}
		return result;
	}

	/**
	 * Read a file.
	 * @param brd File on brd
	 * @return DOM document object
	 * @throws IOException
	 * @throws JDOMException
	 */
	private Document getDoc(File brd) throws IOException, JDOMException {
    	Document doc;
        SAXBuilder builder = new SAXBuilder();	
		doc = builder.build(brd);	
    	return doc;
	}

	/**
	 * @param args See {@link #usageExit(String, int)} for arguments.
	 */
	public static void main(String[] args) {
		boolean showHeadings= false;
		int traversalThreshold = -1;
		boolean traversalCounts = false;
		String label = null;
		int i = 0;
		for (; i < args.length && args[i].charAt(0) == '-'; ++i) {
			if ("showHeadings".equalsIgnoreCase(args[i].substring(1))) {
				showHeadings = true;
			} else if ("traversalCounts".equalsIgnoreCase(args[i].substring(1))) {
				traversalCounts = true;
			} else if ("traversalThreshold".equalsIgnoreCase(args[i].substring(1))) {
				if (++i >= args.length)
					usageExit("Missing traversalThreshold", 2);
				try {
					traversalThreshold = Integer.parseInt(args[i]);
				} catch (NumberFormatException nfe) {
					usageExit("Non-numeric traversalThreshold: "+nfe, 2);
				}
			} else if ("label".equalsIgnoreCase(args[i].substring(1))) {
				if (++i >= args.length)
					usageExit("Missing label", 2);
				label = args[i];
			}
		}

		if (args.length < i+2 || args[i].length() < 1 || args[i+1].length() < 1)
			usageExit("Too few filenames", 2);
		try
		{
			File brd0 = new File(args[i++]);
			File brd = new File(args[i++]);
			ScanBRD s = new ScanBRD(brd0, brd, showHeadings);
			if (label != null)
				s.setLabel(label);
			if (traversalThreshold > -1)
				trace.out(s.countLinksOverTraversalThreshold(traversalThreshold));
			if (traversalCounts)
				trace.out(s.traversalCounts());
		} catch (Exception e) {
			e.printStackTrace();
			usageExit(e.toString(), 2);
		}
	}

	/**
	 * Print a help message explaining the command-line arguments and
	 * exit the Java VM.
	 * @param errMsg Message to print before usage (this method will append ". "; null if none.
	 * @param exitStatus argument to {@link System#exit(int)}
	 * @return never returns: calls {@link System#exit(int)}
	 */
	private static int usageExit(String errMsg, int exitStatus) {
		if (errMsg != null && errMsg.length() > 0)
			System.err.print(errMsg+". ");
		System.err.println("\n"+
				"Usage: java ScanBRD [-showHeadings] [-label \"label\"] [-traversalThreshold T] [-traversalCounts] brd0 brd\n"+
				"where\n"+
				"   showHeadings means print column headings\n"+
				"   traversalCounds means print traversal count for each link\n"+
				"   label is a string to prefix to each line of output\n"+
				"   T = is the minimum number of traversals to retain;\n"+
				"   brd0 = initial BRD with content to ignore;\n"+
				"   brd  = current BRD to analyze.\n");
		System.exit(exitStatus);
		return exitStatus; // not reached
	}

	/**
	 * @return the {@link #label}
	 */
	private String getLabel() {
		return label;
	}

	/**
	 * @param label new value for {@link #label}
	 */
	private void setLabel(String label) {
		this.label = label;
	}

}
