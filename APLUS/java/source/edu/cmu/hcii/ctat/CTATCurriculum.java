package edu.cmu.hcii.ctat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.cmu.pact.Utilities.Logger;

/**
 * 
 * This class represents a "curriculum", which is a set of assignments, each of which
 * includes one or more problem sets. A curriculum description can be specified in XML.
 * 
 * example curriculum.xml:
 * 
 * <curriculum>
 *   <assignment id="14" name="2012 JN4 Challenge">
 *     <problemset id="44" name="propchallenge1" />
 *     <problemset id="45" name="propchallenge2" />
 *   </assignment>
 *   <assignment id="89" name="Proportional Speed Scale">
 *     <problemset id="167" name="propspeed-scale" />
 *   </assignment>
 * </curriculum>
 * 
 * The id and name attributes of each problemset are used to find the problem_set.xml file.
 * Example: tutors/problem_sets/44/propchallenge1/problem_set.xml is the path to the XML
 * that describes the first problem set in the assignment with id "14" in the example above.
 * 
 * The curriculum description is used when running tutors in "local" or "offline" mode, for FIRE, etc.
 * 
 * @author Kevin Jeffries
 *
 */
public class CTATCurriculum extends CTATBase 
{	
	/** Attribute name in root element. */
	private static final String INSTRUCTOR_NAME = "instructor_name";

	/** Alternate attribute name in root element. Cf. {@link #INSTRUCTOR_NAME}. */
	private static final String TEACHER_NAME = "teacher_name";
	
	public String problemSetsPath = CTATLink.htdocs + "/remoteBRDs/tutors/problem_sets/"; // path to base directory for hierarchy of folders containing problem_set.xml files
	public String relativePathPrefix = "/tutors/problem_sets/"; // for paths to BRD, SWF, and problem_set XML files.
	private String assignedClass="";
	
	private Hashtable<String, CTATCourseAssignment> assignments = new Hashtable<String, CTATCourseAssignment>(); // map from assignment name to assignment

	/** The {@value #INSTRUCTOR_NAME} or {@value #TEACHER_NAME} attribute from the root element. */
	private String instructorName = null;
	
	/** The {@value #Logger.SCHOOL_NAME_PROPERTY} attribute from the root element. */
	private String schoolName = null;
	
	/**
	 * 
	 */
	public CTATCurriculum ()
	{
		setClassName ("CTATCurriculum");
		debug ("CTATCurriculum (String)");	
	}
	
	/** 
	 * @param curriculumXML
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	public CTATCurriculum (String curriculumXML) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
	{
		setClassName ("CTATCurriculum");
		debug ("CTATCurriculum (): curriculumXML is\n"+curriculumXML+"\n");		
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(curriculumXML.getBytes("utf-8"))));
		
		parse(doc);
	}	
	/** 
	 * @param curriculumXML
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	public CTATCurriculum (File curriculumXML) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
	{
		setClassName ("CTATCurriculum");
		debug ("CTATCurriculum (File)");		
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(curriculumXML);

		parse(doc);
	}
	
	/**
	 * 
	 */
	public String getAssignedClass() 
	{
		return assignedClass;
	}

	/**
	 * 
	 */	
	public void setAssignedClass(String assignedClass) 
	{
		this.assignedClass = assignedClass;
	}	
	
	// TODO: check for duplicate problem set names if these are to be disallowed
	public void parse(Document doc) throws IOException
	{
		debug ("parse ()");
		
		Element root = doc.getDocumentElement();
		
		if (root.getNodeName().equalsIgnoreCase("curriculum"))
		{
			debug ("Detected old format curriculum");
			parseTheOldWay(doc);
			return;
		}
		else
			debug ("Parsing new format curriculum");

		instructorName = root.getAttribute(INSTRUCTOR_NAME);
		if(instructorName == null || instructorName.length() < 1)
			instructorName = root.getAttribute(TEACHER_NAME);  // previous name in XML
		schoolName = root.getAttribute(Logger.SCHOOL_NAME_PROPERTY);
			
		// parse the new, course.xml, way
		NodeList docChildren = root.getChildNodes();
		
		int numDocChildren = docChildren.getLength();
		
		for(int i = 0; i < numDocChildren; ++i)
		{
			Node docChild = docChildren.item(i);
			
			if(docChild.getNodeType() == Node.ELEMENT_NODE && docChild.getNodeName().equalsIgnoreCase("Assignments"))
			{
				debug ("Processing 'Assignments' node ...");
				
				NodeList assignmentNodes = docChild.getChildNodes();
				int numAssignmentNodes = assignmentNodes.getLength();
				for(int j = 0; j < numAssignmentNodes; ++j)
				{
					Node assignmentNode = assignmentNodes.item(j);
					if(assignmentNode.getNodeType() == Node.ELEMENT_NODE && assignmentNode.getNodeName().equalsIgnoreCase("Assignment"))
					{
						debug ("Processing 'Assignment' node ...");
						
						Element assignmentElement = (Element) assignmentNode;
						String assignmentName = assignmentElement.getAttribute("name");
						String assignmentStatus = assignmentElement.getAttribute("status");
						String assignmentPosition = assignmentElement.getAttribute("position");
						String assignmentAssignType = assignmentElement.getAttribute("assign_type");
						
						debug("Assignment name "+assignmentName+", status "+assignmentStatus+
								", position "+assignmentPosition);
						
						if(assignmentName.length() > 0)
						{
							CTATCourseAssignment thisAssignment = new CTATCourseAssignment();
							thisAssignment.name = assignmentName;
							thisAssignment.status = assignmentStatus;
							try { thisAssignment.position = Integer.valueOf(assignmentPosition); } catch(NumberFormatException e) { thisAssignment.position = -1; }
							thisAssignment.assign_type = assignmentAssignType;
							
							Object prev = assignments.put(assignmentName, thisAssignment);
							if(prev != null)
							{
								throw new IOException("invalid XML -- duplicate assingment names");
							}
							
							NodeList assignmentChildren = assignmentNode.getChildNodes();
							int numAssignmentChildren = assignmentChildren.getLength();
							
							debug("Assignment name "+thisAssignment.name+", numAssignmentChildren "+numAssignmentChildren);

							for(int k = 0; k < numAssignmentChildren; ++k)
							{
								Node assignmentChild = assignmentChildren.item(k);
								if(assignmentChild.getNodeType() == Node.ELEMENT_NODE && assignmentChild.getNodeName().equalsIgnoreCase("ProblemSets"))
								{
									NodeList problemSetNodes = assignmentChild.getChildNodes();
									int numProblemSetNodes = problemSetNodes.getLength();
									
									debug("Assignment name "+thisAssignment.name+", nProblemSetNodes "+numProblemSetNodes);

									for(int m = 0; m < numProblemSetNodes; ++m)
									{
										Node problemSetNode = problemSetNodes.item(m);
										if(problemSetNode.getNodeType() == Node.ELEMENT_NODE && problemSetNode.getNodeName().equalsIgnoreCase("ProblemSet"))
										{
											Element problemSetElement = (Element) problemSetNode;
											String probsetName = problemSetElement.getAttribute("name");
											String probsetPosition = problemSetElement.getAttribute("position");
											String probsetSubdirectory = problemSetElement.getAttribute("subdirectory");
											String probsetActivationStatus = problemSetElement.getAttribute("activation_status");
											
											if(probsetName.length() > 0)
											{
												CTATCourseProbset thisProbset = new CTATCourseProbset();
												thisProbset.name = probsetName;
												try { thisProbset.position = Integer.valueOf(probsetPosition); } catch(NumberFormatException e) { thisProbset.position = -1; }
												thisProbset.subdirectory = probsetSubdirectory;
												thisProbset.activation_status = probsetActivationStatus;
												
												thisAssignment.addProbset(thisProbset);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(docChild.getNodeType() == Node.ELEMENT_NODE && docChild.getNodeName().equalsIgnoreCase("Students"))
			{
				NodeList studentNodes = docChild.getChildNodes();
				int numStudentNodes = studentNodes.getLength();
				for(int m = 0; m < numStudentNodes; ++m)
				{
					Node studentNode = studentNodes.item(m);
					if(studentNode.getNodeType() == Node.ELEMENT_NODE && studentNode.getNodeName().equalsIgnoreCase("Student"))
					{
						Element studentElement = (Element) studentNode;
						String username = studentElement.getAttribute("username");
						usernames.add(username); 
					}
				}
			}								
		}
	}
	
	/** Usernames found in the top-level &lt;Students&gt; element. */
	private Set<String> usernames = new HashSet<String>();
	
	/**
	 * @param username
	 * @return true if arg is in {@link #usernames}
	 */
	public boolean isUserRegistered(String username) {
		return usernames.contains(username);
	}
	
	/**
	 * Parse according to the old (August 2012) curriculum.xml format. This will be called by the constructor if old format is detected.
	 * @param doc
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	private void parseTheOldWay(Document doc) throws IOException
	{
		Node root = doc.getDocumentElement();
		NodeList assignmentNodes = root.getChildNodes();
		int numAssignmentNodes = assignmentNodes.getLength();
		
		Set<String> probsetNames = new HashSet<String>(); // use this to check for duplicate problem set names, which are currently not allowed, even in different assignments. 
		
		for(int i = 0; i < numAssignmentNodes; ++i)
		{
			Node assignmentNode = assignmentNodes.item(i);
			
			if (assignmentNode.getNodeType() == Node.ELEMENT_NODE && assignmentNode.getNodeName().equalsIgnoreCase("assignment"))
			{
				Element assignmentElement = (Element) assignmentNode;
				String assignmentID = assignmentElement.getAttribute("id");
				String assignmentName = assignmentElement.getAttribute("name");
				String assignmentStatus = assignmentElement.getAttribute("status"); // optional attribute. returns "", not null, if attribute is not present
				
				if(assignmentID.length() > 0 && assignmentName.length() > 0)
				{
					CTATCourseAssignment thisAssignment = new CTATCourseAssignment();
					//thisAssignment.id = assignmentID;
					thisAssignment.name = assignmentName;
					thisAssignment.status = assignmentStatus;
					
					Object prev = assignments.put(assignmentID, thisAssignment);
					
					if (prev != null)
					{
						throw new IOException("Curriculum XML invalid -- duplicate assignment IDs \""+assignmentID+"\"");
					}
					
					NodeList probsetNodes = assignmentElement.getChildNodes();
					int numProbsetNodes = probsetNodes.getLength();
					
					for(int j = 0; j < numProbsetNodes; ++j)
					{
						Node probsetNode = probsetNodes.item(j);
						
						if(probsetNode.getNodeType() == Node.ELEMENT_NODE && probsetNode.getNodeName().equals("problemset"))
						{
							Element probsetElement = (Element) probsetNode;
							String probsetID = probsetElement.getAttribute("id");
							String probsetName = probsetElement.getAttribute("name");
							//String probsetStatus = probsetElement.getAttribute("status"); // optional attribute. returns "", not null, if attribute is not present
							
							boolean noDuplicate = probsetNames.add(probsetName);
							if(!noDuplicate)
							{
								throw new IOException("Curriculum XML invalid -- duplicate problem set names \""+probsetName+"\"");
							}
							
							if(probsetName.length() > 0)
							{
								CTATCourseProbset thisProbset = new CTATCourseProbset();
								thisProbset.activation_status = probsetID; // because id was originally used as a directory name, and now activation_status is
								thisProbset.name = probsetName;
								//thisProbset.status = probsetStatus;
								thisAssignment.addProbset(thisProbset);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public String getFirstAssignment ()
	{
		debug ("getFirstAssignment ()");
		
		if (assignments.size()==0)
		{
			debug ("Error: no assignments yet");
			return ("");
		}
		
		CTATCourseAssignment ass=assignments.entrySet().iterator().next().getValue();

		if (ass==null)
		{
			debug ("Error getting first assignment");
			return ("");
		}
		
		return (/*ass.id*/ass.name);
	}
	
	/**
	 * Get the name of the first problem set of this assignment
	 * @param assignmentID
	 * @return name of the problem set, or null
	 */
	public String getFirstProblemSet(String assignmentID)
	{
		debug ("getFirstProblemSet ("+assignmentID+")");
		
		debug ("assignments.size (): " + assignments.size());
		
		String firstProblemSet = null;
		
		if(assignmentID != null)
		{
			CTATCourseAssignment assignment = assignments.get(assignmentID);
			if(assignment != null && assignment.numProbsets() > 0)
			{
				firstProblemSet = assignment.getProbsets().get(0).name;
			}
		}
		
		return firstProblemSet;
	}
	
	/**
	 * Get the name of the first problem set of this assignment
	 * @param assignmentID
	 * @return a problem set, or null
	 */
	public CTATCourseProbset getFirstProblemSetInstance (String assignmentID)
	{
		debug ("getFirstProblemSetInstance ("+assignmentID+")");
				
		if (assignmentID != null)
		{
			CTATCourseAssignment assignment = assignments.get(assignmentID);
			
			if(assignment != null && assignment.numProbsets() > 0)
			{
				return (assignment.getProbsets().get(0));
			}
		}
		
		return (null);
	}
	
	/**
	 * Get the name of the first problem set of this assignment
	 * @param assignmentID
	 * @return 
	 */
	public ArrayList<CTATProblemSet> getProblemSets (String assignmentID)
	{
		debug ("getProblemSets ("+assignmentID+")");
				
		if (assignmentID.isEmpty()==false)
		{
			CTATCourseAssignment assignment = assignments.get (assignmentID);
			
			if (assignment!=null)
			{
				debug ("We have an assignment object");
				
				ArrayList<CTATProblemSet> pSets=new ArrayList<CTATProblemSet> ();
			
				if (assignment.getProbsets()!=null)
				{
					for (int i=0;i<assignment.numProbsets();i++)
					{
						CTATCourseProbset testSet=assignment.getProbsets().get(i);
				
						if (testSet!=null)
						{										
							if (testSet.ctatProblemSet!=null)
							{
								pSets.add (testSet.ctatProblemSet);
							}
							else
								debug ("Oops, problem set not loaded yet!");
						}
						else
							debug ("Error getting problem set " + i);
					}
				}	
				else
					debug ("Error: assignment.probsets==null");
			
				return (pSets);
			}
			else
			{
				debug ("Error: unable to get assignment object for id: " + assignmentID);
				return (null);
			}
		}
		
		return (null);
	}	
	
	/**
	 * Find the corresponding activation status for a problem set by name
	 * @param problemSetName
	 * @return the problem set's activation status as a string, or null if the problem set name is not recognized.
	 */
	public String getProblemSetActivationStatus(String problemSetName)
	{
		debug ("getProblemSetActivationStatus ("+problemSetName+")");
		
		String problemSetActivationStatus = null;
		
		Collection<CTATCourseAssignment> collection = assignments.values();
		for(CTATCourseAssignment assignment : collection)
		{
			for(CTATCourseProbset probset : assignment.getProbsets())
			{
				if(problemSetName.equals(probset.name))
				{
					problemSetActivationStatus = probset.activation_status;
					break;
				}
			}
		}
		
		return problemSetActivationStatus;
	}
	
	/**
	 * Get the name of the assignment that contains the given problem set. Note: In general, a problem set may exist in more than one
	 * assignment, but for the offline curriculum this is not true. A "student assignment" is the unique name for an assignment-problemset pair.
	 * @param studentAssignmentName
	 * @return assignment name, or null for unknown student assignments
	 */
	public String getAssignment(String studentAssignmentName)
	{
		CTATCourseAssignment answer = getAssignmentInternal(studentAssignmentName);
		
		return answer == null ? null : answer.name;
	}
	
	/**
	 * Get the next student assignment (i.e. problem set) in sequence. A "student assignment" is a problem set within a certain assignment.
	 * @param currentStudentAssignment
	 * @return name of next student assignment in the assignment. Null if either the parameter is unrecognized or is the final student assignment within an assignment.
	 */
	public String getNextStudentAssignment(String currentStudentAssignment)
	{
		debug ("getNextStudentAssignment ("+currentStudentAssignment+")");
		
		String answer = null;
		
		CTATCourseAssignment assignment = getAssignmentInternal(currentStudentAssignment);
		
		if(assignment != null)
		{
			List<CTATCourseProbset> probsets = assignment.getProbsets();
			Iterator<CTATCourseProbset> iter = probsets.iterator();
			while(iter.hasNext())
			{
				CTATCourseProbset thisProbset = iter.next();
				if(currentStudentAssignment.equals(thisProbset.name))
				{
					// found currentStudentAssignment. Return the next student assignment
					if(iter.hasNext())
					{
						answer = iter.next().name;
					}
					
					break;
				}
			}
		}
		
		return answer;
	}
	
	/**
	 * Get the number of problems in the problem set specified by student assignment name (e.g. "measdist1")
	 * @param studentAssignmentName
	 * @return number of problems in set, or 0 if the student assignment name is null or unrecognized
	 */
	public int getNumberOfProblemsInProbset(String studentAssignmentName)
	{
		debug ("getNumberOfProblemsInProbset ("+studentAssignmentName+")");
		
		CTATProblemSet ctatProblemSet = getProblemSet(studentAssignmentName);
		if(ctatProblemSet != null)
		{
			return ctatProblemSet.getNumProblems();
		}
		else return 0;
	}
	
	/**
	 * Get an object that represents a problem set that is specified by name. The object is
	 * pulled from the cache if possible, or read from problem_set.xml on disk otherwise.
	 * @param problemSetName name of the problem set / student assignment (e.g. "measdist1" )
	 * @return the problem set object, with the problem set loaded, or null if the problem set does not exist
	 */
	public CTATProblemSet getProblemSet(String problemSetName)
	{
		debug ("getProblemSet ("+problemSetName+")");
		
		CTATProblemSet answer = null;
		
		CTATCourseAssignment assignment = getAssignmentInternal(problemSetName);
		
		if(assignment != null)
		{
			List<CTATCourseProbset> probsets = assignment.getProbsets();
			for(CTATCourseProbset probset : probsets)
			{
				if(problemSetName.equals(probset.name))
				{
					if(probset.ctatProblemSet == null)
					{
						loadProblemSet(probset);
					}
					
					answer = probset.ctatProblemSet;
					break;
				}
			}
		}
		
		return answer;
	}
	
	/**
	 * Get the value of the status attribute for the indicated assignment
	 * @param assignmentName name of assignment
	 * @return value of status attribute. Empty string is returned if attribute has no value; null is returned if assignment could not be found.
	 */
	public String getAssignmentStatus(String assignmentName)
	{
		debug ("getAssignmentStatus ("+assignmentName+")");
		
		String status = null;
		
		if(assignmentName != null)
		{
			CTATCourseAssignment assignment = assignments.get(assignmentName);
			if(assignment != null)
			{
				status = ((assignment.status == null) ? "" : assignment.status);  
			}
		}
		
		return status;
	}
	
	/**
	 * Get the value of the status attribute for the indicated problem set
	 * @param problemSetName name of problem set
	 * @return value of status attribute. Empty string is returned if attribute has no value; null is returned if problem set could not be found.
	 */
	public String getProblemSetStatus(String problemSetName)
	{
		debug ("getProblemSetStatus ("+problemSetName+")");
		
		String status = null;
		
		CTATCourseAssignment assignment = getAssignmentInternal(problemSetName);
		
		if(assignment != null)
		{
			List<CTATCourseProbset> probsets = assignment.getProbsets();
			for(CTATCourseProbset probset : probsets)
			{
				if(problemSetName.equals(probset.name))
				{
					status = ((probset.status == null) ? "" : probset.status);
					break;
				}
			}
		}
		
		return status;
	}
	
	/**
	 * Set the status attribute for the indicated assignment. This does not alter the actual XML file. To make a persistent change,
	 * call this class's toString() method and write the result to the XML file.
	 * @param assignmentName name of assignment
	 * @param status the new status (null is interpreted as "")
	 * @return true if assignment was found and status attribute was altered; false otherwise
	 */
	public boolean setAssignmentStatus(String assignmentName, String status)
	{
		debug ("setAssignmentStatus ("+assignmentName+","+status+")");
		
		boolean success = false;
		
		if(assignmentName != null)
		{
			CTATCourseAssignment assignment = assignments.get(assignmentName);
			if(assignment != null)
			{
				assignment.status = ((status == null) ? "" : status);
				success = true;
			}
		}
		
		return success;
	}
	
	/**
	 * Set the status attribute for the indicated problem set. This does not alter the actual XML file. To make a persistent change,
	 * call this class's toString() method and write the result to the XML file.
	 * @param problemSetName name of problem set
	 * @param status the new status (null is interpreted as "")
	 * @return true if problem set was found and status attribute was altered; false otherwise
	 */
	public boolean setProblemSetStatus(String problemSetName, String status)
	{
		debug ("setProblemSetStatus ("+problemSetName+","+status+")");
		
		boolean success = false;
		
		CTATCourseAssignment assignment = getAssignmentInternal(problemSetName);
		if(assignment != null)
		{
			List<CTATCourseProbset> probsets = assignment.getProbsets();
			for(CTATCourseProbset probset : probsets)
			{
				if(problemSetName.equals(probset.name))
				{
					probset.status = ((status == null) ? "" : status);
					success = true;
					break;
				}
			}
		}
		
		return success;
	}
	
	/**
	 * Get the assignment that contains the given problem set. Note: In general, a problem set may exist in more than one
	 * assignment, but for the offline curriculum this is not true. A "student assignment" is the unique name for an assignment-problemset pair.
	 * @param studentAssignmentName problem set name
	 * @return assignment, or null for unknown student assignments
	 */
	private CTATCourseAssignment getAssignmentInternal(String studentAssignmentName)
	{
		debug ("getAssignmentInternal ("+studentAssignmentName+")");
		
		CTATCourseAssignment answer = null;
		
		Collection<CTATCourseAssignment> collection = assignments.values();
		outerloop: for(CTATCourseAssignment thisAssignment : collection)
		{
			for(CTATCourseProbset probset : thisAssignment.getProbsets())
			{
				if(studentAssignmentName.equals(probset.name))
				{
					answer = thisAssignment;
					break outerloop;
				}
			}
		}
		
		return answer;
	}
	
	/**
	 * Load the CTATProblemSet object for the Probset
	 * @param probset
	 */
	private void loadProblemSet(CTATCourseProbset probset)
	{
		debug ("loadProblemSet (Probset)");
		
		CTATProblemSet ctatProblemSet = new CTATProblemSet();
		
		debug ("problemSetsPath: " + problemSetsPath + ", probset.activation_status: " + probset.activation_status + ", probset.name: " + probset.name);
		
		String path = problemSetsPath + probset.activation_status + "/" + probset.subdirectory + "/problem_set.xml";
		
		debug ("Attempting to load file: " + path);
		
		if (CTATLink.allowWriting==true)
		{
			File f=new File(path);
		
			if (!f.exists())
			{
				try
				{
					f.getParentFile().mkdirs();
					f.createNewFile();
					String xml = (new CTATURLFetch()).fetchURL("http://"+CTATLink.remoteHost+"/tutors/problem_sets/"+probset.activation_status+"/"+probset.subdirectory+"/problem_set.xml");
					CTATLink.fManager.setContents(path, xml);
				}
				catch(Exception e) { return; }
			}
		}
		
		boolean success = ctatProblemSet.loadProblemSet(path);
		
		if (success==true)
			probset.ctatProblemSet = ctatProblemSet;
		
		if ((success==true) && (CTATLink.allowWriting==true))
		{			
			// make sure this problem set's BRDs are stored locally
			List<CTATProblem> problems = ctatProblemSet.getAllProblems();
			
			for(CTATProblem problem : problems)
			{
				if(problem != null)
				{
					String brdName = problem.problem_file;
					String brdPath = problemSetsPath + probset.activation_status + "/" + probset.subdirectory + "/FinalBRDs/" + brdName;
					
					File brdFile = new File(brdPath);
					
					if(!brdFile.exists())
					{
						try
						{
							brdFile.getParentFile().mkdirs();
							brdFile.createNewFile();
							
							String brdContents = (new CTATURLFetch()).fetchURL("http://"+CTATLink.remoteHost+"/tutors/problem_sets/"+probset.activation_status+"/"+probset.subdirectory+"/FinalBRDs/"+brdName);
							
							CTATLink.fManager.setContents(brdPath, brdContents);
							
						} catch (Exception e) { e.printStackTrace(); }
					}
				}
			}
		}
	}
	
	/** returns some XML representing this object
	 *  Note: this is the OLD format for the XML (curriculum.xml, not course.xml) */
	public String toString()
	{
		StringBuilder sb = new StringBuilder("<curriculum>");
		
		Collection<CTATCourseAssignment> collection = assignments.values();
		for(CTATCourseAssignment assignment : collection)
		{
			sb.append("<assignment");
			//if(assignment.id != null) sb.append(" id=\""+assignment.id+"\"");
			if(assignment.name != null) sb.append(" name=\""+assignment.name+"\"");
			if(assignment.status != null) sb.append(" status=\""+assignment.status+"\"");
			sb.append(">");
			
			for(CTATCourseProbset probset : assignment.getProbsets())
			{
				sb.append("<problemset");
				//if(probset.id != null) sb.append(" id=\""+probset.id+"\"");
				if(probset.name != null) sb.append(" name=\""+probset.name+"\"");
				if(probset.status != null) sb.append(" status=\""+probset.status+"\"");
				sb.append(" />");
			}
			
			sb.append("</assignment>");
		}
		
		sb.append("</curriculum>");
		
		return sb.toString();
	}
	
	/**
	 * Get the names of the problem_set.xml files for all the problem sets in this curriculum.
	 * @return a collection containing paths to all required problem_set.xml files. Paths are relative to the remoteBRDs folder locally, and can be used directly in a HTTP request to the server.
	 */
	public Collection<String> getRequiredProblemSets()
	{
		debug ("getRequiredProblemSets ()");
		
		Collection<String> result = new HashSet<String>();
		
		Collection<CTATCourseAssignment> assignmentCollection = assignments.values();
		
		debug ("Found " + assignmentCollection.size() + " eligible course assignments");
		
		for(CTATCourseAssignment assignment : assignmentCollection)
		{
			Collection<CTATCourseProbset> probsetCollection = assignment.getProbsets();
			
			debug ("Processing assignment "+assignment.name+" with "+probsetCollection.size()+" problem sets...");
			
			for(CTATCourseProbset probset : probsetCollection)
			{
				result.add(relativePathPrefix + probset.activation_status + "/" + probset.subdirectory + "/problem_set.xml");
			}
		}
		
		return result;
	}
	
	/**
	 * Get the paths to the BRDs (problem files) of all problems in this curriculum.
	 * @return collection of paths, which are relative to the remoteBRDs directory locally and which form the path portion of a URL for a request to the server 
	 */
	public Collection<String> getRequiredAssets (String server)
	{
		debug ("getRequiredAssets ()");
		
		Collection<String> result = new HashSet<String>();
		
		Collection<CTATCourseProbset> probsets = loadAllProblemSets();
		
		for(CTATCourseProbset probset : probsets)
		{
			if (probset.ctatProblemSet!=null)
			{
				int num = probset.ctatProblemSet.getNumProblems();
			
				for(int i = 0; i < num; i++)
				{
					probset.ctatProblemSet.setCurrentIndex(i);
					String pathSearch=relativePathPrefix+probset.activation_status+"/"+probset.subdirectory+
							"/Flash/Assets.xml";
				
					CTATAssetManager assetManager=new CTATAssetManager ();
					
					ArrayList<String> assetList=assetManager.downloadAssets (pathSearch,server);
					
					for (int t=0;t<assetList.size();t++)
					{
						result.add (assetList.get(t));	
					}
				}			
			}			
			else
				debug ("Internal error: ctatProblemSet attribute in problem set container is null");				
		}
		
		return result;
	}	
	
	/**
	 * Get the paths to the BRDs (problem files) of all problems in this curriculum.
	 * @return collection of paths, which are relative to the remoteBRDs directory locally and which form the path portion of a URL for a request to the server 
	 */
	public Collection<String> getRequiredBRDs()
	{
		debug ("getRequiredBRDs ()");
		
		Collection<String> result = new HashSet<String>();
		
		Collection<CTATCourseProbset> probsets = loadAllProblemSets();
		
		for(CTATCourseProbset probset : probsets)
		{
			if (probset.ctatProblemSet!=null)
			{
				int num = probset.ctatProblemSet.getNumProblems();
			
			//	for(int i = 0; i < num; ++i)
				for(int i = 0; i < num; i++)
				{
					probset.ctatProblemSet.setCurrentIndex(i);
					CTATProblem ctatProblem = probset.ctatProblemSet.getNextProblem();
					result.add(relativePathPrefix+probset.activation_status+"/"+probset.subdirectory+
							"/FinalBRDs/"+ctatProblem.problem_file);
				}
			}			
			else
				debug ("Internal error: ctatProblemSet attribute in problem set container is null");				
		}
		
		return result;
	}
	
	/**
	 * Get the paths to the student interface files for all problems in this curriculum.
	 * @return collection of paths which form the path portion of a URL for a request to the server
	 */
	public Collection<String> getRequiredSWFs()
	{
		debug ("getRequiredSWFs ()");
		
		Collection<String> result = new HashSet<String>();
		
		Collection<CTATCourseProbset> probsets = loadAllProblemSets();
		
		for (CTATCourseProbset probset : probsets)
		{
			if (probset.ctatProblemSet!=null)
			{
				int num = probset.ctatProblemSet.getNumProblems();
			
				//for(int i = 0; i < num; ++i)
				for(int i = 0; i < num; i++)
				{
					probset.ctatProblemSet.setCurrentIndex(i);
				
					CTATProblem ctatProblem = probset.ctatProblemSet.getNextProblem();
				
					result.add (relativePathPrefix+probset.activation_status+"/"+probset.subdirectory+"/Flash/"+ctatProblem.student_interface);
				}
			}
			else
				debug ("Internal error: ctatProblemSet attribute in problem set container is null");
		}
		
		return result;
	}
	
	/**
	 * Load the CTATProblemSet object for each problem set in the curriculum.
	 * @return a collection of all probsets, with their ctatProblemSet loaded
	 */
	public Collection<CTATCourseProbset> loadAllProblemSets()
	{
		debug ("loadAllProblemSets ()");
		
		Collection<CTATCourseProbset> result = new HashSet<CTATCourseProbset>();
		
		Collection<CTATCourseAssignment> collection = assignments.values();
		for(CTATCourseAssignment assignment : collection)
		{
			Collection<CTATCourseProbset> probsets = assignment.getProbsets();
			for(CTATCourseProbset probset : probsets)
			{
				if(probset.ctatProblemSet == null)
				{
					loadProblemSet(probset);
				}
				
				result.add(probset);
			}
		}
		
		return result;
	}

	/**
	 * @return #instructorName; returns "" if null
	 */
	public String getInstructorName() {
		return (instructorName == null ? "" : instructorName);
	}

	/**
	 * @return "" FIXME Need school in course.xml's root element
	 */
	public String getSchoolName() {
		return schoolName;
	}

	/**
	 * @param problemSetName
	 * @return CTATCourseAssignment#getConditionName() for the assignment having the given problem set;
	 *         returns empty string "" if problemSetName or assignment not found
	 */
	public String getConditionName(String problemSetName) {
		if(problemSetName == null)
			return "";
		CTATCourseAssignment assignment = getAssignmentInternal(problemSetName);
		if(assignment == null)
			return "";
		else
			return assignment.getConditionName();
	}
}
