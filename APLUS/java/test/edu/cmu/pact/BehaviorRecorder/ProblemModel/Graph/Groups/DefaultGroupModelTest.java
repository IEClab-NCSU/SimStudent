/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Tests for {@link DefaultGroupModel}.
 */
public class DefaultGroupModelTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(DefaultGroupModelTest.class);
        return suite;
    }
    
    /**
     * A diagnostic for reading groups from BRD files. Reads stdin.
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
    	if (args.length > 0 && args[0].toLowerCase().contains("-h")) {
    		System.err.println("Reads BRD file from stdin.");
    		return;
    	}
		class Group {
			final String Indent = "    ";
			final String name;
			Set<Group> subgroups = new LinkedHashSet<Group>();
			Set<Integer> links = new LinkedHashSet<Integer>();
			Group(Element groupElt) {
				name = groupElt.getAttributeValue("name");
				for (Element linkElt : ((Iterable<Element>) groupElt.getChildren("link")))
					links.add(Integer.valueOf(linkElt.getAttributeValue("id")));
				for (Element subgroupElt : ((Iterable<Element>) groupElt.getChildren("group")))
					subgroups.add(new Group(subgroupElt));
			}
			void linkCheck(Set<Integer> allLinks, String indent) {
				System.out.printf("linkCheck%s%s:\n", indent, name);
				for (Integer linkID : links) {
					if (!allLinks.add(linkID))  // returns false if item already in set
						System.out.printf("linkCheck%s  Duplicate entry for link %d in group %s.\n", indent, linkID, name);
				}
				for (Group subgroup : subgroups)
					subgroup.linkCheck(allLinks, indent+Indent);				
			}
			public String toString() { return toString(Indent); }
			public String toString(String indent) {
				StringBuilder sb = new StringBuilder(indent);
				sb.append(name).append(": ");
				for (Integer linkID : links)
					sb.append(linkID).append(", ");
				sb.append('\n');
				for (Group subgroup : subgroups)
					sb.append(subgroup.toString(indent+Indent));
				return sb.toString();
			}
		}
    	SAXBuilder builder = new SAXBuilder();	
		Document doc = builder.build(System.in);
		Element root = doc.getRootElement();
		Element edgesGroups = root.getChild("EdgesGroups");
		Set<Group> groups = new LinkedHashSet<Group>();
		for (Element groupElt : ((Iterable<Element>) edgesGroups.getChildren("group")))
			groups.add(new Group(groupElt));
		System.out.printf("\n%d topLevel groups\n", groups.size());
		for (Group group : groups)
			trace.out(String.valueOf(group));
		Set<Integer> allLinks = new LinkedHashSet<Integer>();
		for (Group group : groups)
			group.linkCheck(allLinks, group.Indent);
    }

	private static final String subgroupsEdgesGroups =
		"<EdgesGroups ordered=\"true\">\r\n"+
		"    <group name=\"Group1\" ordered=\"false\" reenterable=\"true\">\r\n"+
		"        <group name=\"convert1\" ordered=\"true\" reenterable=\"true\">\r\n"+
		"            <link id=\"3\" />\r\n"+
		"            <link id=\"1\" />\r\n"+
		"        </group>\r\n"+
		"        <group name=\"convert2\" ordered=\"true\" reenterable=\"true\">\r\n"+
		"            <link id=\"5\" />\r\n"+
		"            <link id=\"7\" />\r\n"+
		"        </group>\r\n"+
		"    </group>\r\n"+
		"    <group name=\"Group2\" ordered=\"false\" reenterable=\"true\">\r\n"+
		"        <link id=\"11\" />\r\n"+
		"        <link id=\"9\" />\r\n"+
		"    </group>\r\n"+
		"</EdgesGroups>";

	private BR_Controller controller;

	public void testToXMLString() {
    	String problemFileLocation = "edu/cmu/pact/BehaviorRecorder/ProblemModel/Graph/subgroups.brd";
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("mt" + "problemFileLocation str = "
                + problemFileLocation + ", url = " + url);
        controller.openBRFromURL(url.toString());
		DefaultGroupModel gm = (DefaultGroupModel) 
			controller.getProblemModel().getExampleTracerGraph().getGroupModel();
		assertEquals("<EdgesGroups> from subgroups.brd",
				subgroupsEdgesGroups, gm.toXMLString());
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();
		CTAT_Launcher launcher = new CTAT_Launcher(new String[0]);
		controller = launcher.getFocusedController();
	}

}
