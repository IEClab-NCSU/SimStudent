package edu.cmu.pact.ctatview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Dialogs.SkillMatrixDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReaderJDom;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.Utilities.trace;


/**
 * The Parse class contains implementation to parse a hierarchy.txt or search from
 * a main folder to populate maps from unit to section, section to problem, problems
 * to skills, etc.
 * 
 * To implement your own Parse class, see the setup() method and the Parse interface
 * 
 * Note that we expect at most a single curriculum to be created with this program
 * 
 * After parsing, CTATtoLMS can create an Excel file (with Problem, Unit, and Skill Libraries),
 * a .UNIT file, .BAS files, and .XCUR files.  These operations are done pairwise independent
 * 
 * See comments in each class for usage instructions/options
 * 
 * @author Ko
 */

public class CTATtoLMS{
	
	private static final String HIERARCHY = "hierarchy.txt";
	
	private String curDir = null; //points to the root with a File.separator on the end
	private File root = null;
	private String studentInterface;
	private String platform;
	
	private String history = ""; //keeps track of warnings and errors to be returned to the dialog
	private boolean[] msgOption; //debug, errors, warnings
	private boolean fromMenu;
	
	//these three maps are to be used only in creating the Excel file and .BAS files
	private HashMap<String, File> brdFileMap = new HashMap<String, File>();
	private HashMap<String, ArrayList<File>> skillToProblemMap = new HashMap<String, ArrayList<File>>();//for the excel doc
	private HashMap<File, HashSet<String>> problemToSkillMap = new HashMap<File, HashSet<String>>();//for the .BAS files and the excel doc
	
	//NOTE: be aware that ProblemName Strings in the maps are DIFFERENT objects than those in the Section object
	//on the other note, SkillName Strings in the maps are the SAME objects as those in the Section object
	
	private String date; //constant for the date
	private ArrayList<String> omitted;
	private ArrayList<String> unitNames;
	
	private static final String RANDOM = "random"; /*keyword put after section to indicate randomized ordering*/
	
	//NOTE: there is no check on platform or stuInt flags until the Section level
	
	//this represents the entire hierarchy
	private ArrayList<ArrayList<Section>> curriculum; //not actually used as such unless hierarchy requires
	private String curricName;
	
	public CTATtoLMS(File root, String platform, String studentInterface, String msgOption, boolean fromMenu)
	{
		this.curDir = root.getAbsolutePath() + File.separator;
		this.root = root;
		this.studentInterface = studentInterface;
		this.platform = platform;
		
		this.msgOption = new boolean[4];
		for(int i = 0; i < msgOption.length(); i ++)
			if(msgOption.charAt(0) == 'y' || msgOption.charAt(0) == 'Y')
				this.msgOption[i] = true;
		
		this.fromMenu = fromMenu;
		
		this.date = getDate();
		this.curriculum = new ArrayList<ArrayList<Section>>();
		this.unitNames = new ArrayList<String>();
		
		this.omitted = new ArrayList<String>();
	}
	
	//NOTE: since the problemLibrary is itself a list of .brds, if you wanted
	//a cvs txt file with all of the problems in it, could write a macro from the Excel or cut and paste
	//seems redundant to have in excel and .txt
	
	/* filepath - argument is file path for a folder
	 * platform name - name of the platform for all of the .brds
	 * studentInterface name - name of the student interface for all of the .brds
	 * options (optional) - a 4-tuple of Ys and Ns, will default to NYYY if missing (case insensitive) 
	 * 1st whether to create Excel document
	 * 2nd whether to create XCur file(s)
	 * 3rd whether to create Unit file(s)
	 * 4th whether to create .BAS file(s)
	 * 
	 * debugging (optional) - a 3-tuple of Ys and Ns
	 * 1st - errors //default on
	 * 2nd - warnings
	 * 3rd - debugging
	 */
	//usage: java CTATtoLMS (filepath) (platformName) (stuInterfaceName) (options[xml, xcur, unit, bas])* (debugging)* [see comments for more]
	//* arguments are optional
	//technically, they're all optional, but the first 3 will be asked for if missing

	public static void main(String[] args)
    {
		String status = run(args, false);
    	if(status != null && status != "") //should be empty if options want it so
    		trace.out(status);
    }
    
	/**
	 * 
	 * @param args
	 * @param fromMenu - whether this is being run from the CreateLMSFilesDialog
	 * @return
	 */
    public static String run(String[] args, boolean fromMenu)
    {
    	String filename = args == null || args.length == 0 ? JOptionPane.showInputDialog(null, "Filepath: ") : args[0];
    	if(filename == null)
    		return "Error: Cancelled by user";
    	
    	File d = new File(filename);
    	
    	if(!d.isDirectory() && !d.getAbsolutePath().endsWith(".brd") && !d.getName().contains(CTATtoLMS.HIERARCHY)) //so I suppose you could have a "blaghhierarchymorerandomstuff1234.xml" file, so long as it can be parsed
    		return "Error: Not a folder, .brd, or hierarchy.txt";
    	
    	if(d.getName().equals(CTATtoLMS.HIERARCHY))
    	{
    		if (d.isFile())
    			d = d.getAbsoluteFile().getParentFile();
    		else
    			d = new File(System.getProperty("user.dir"));
    	}
    	
    	String platform = args == null || args.length <= 1 ? JOptionPane.showInputDialog(null, "Platform: ") : args[1]; //may be null, will check later
    	
    	String studentInterface = args == null || args.length <= 2 ? JOptionPane.showInputDialog(null, "StudentInterface: ") : args[2]; //may be null, will check later
    	
    	String options = "NYYY"; //default options
    	if(args.length > 3)
    		options = args[3];
    	
    	String msgOption = "YYN"; //default prints errors and warnings
    	if(args.length > 4)
    		msgOption = args[4];
    	
    	CTATtoLMS t = new CTATtoLMS(d, platform, studentInterface, msgOption, fromMenu);
    	
    	t.setup();
    	if(!t.doOptions(options))
    		return t.history;
    	
    	t.debugging("Success!");
    	return t.history;
    }
    
    private boolean doOptions(String options)
    {
    	if(options.length() != 4)
    	{
    		error("Invalid options (needs 4 y/n's), usage: java CTATtoLMS (filepath) (platformName) (stuInterfaceName) (options[xml, xcur, unit, bas])* (debugging)* [see comments for more]");
    		return false;
    	}
    	
    	if(options.charAt(0) == 'y' || options.charAt(0) == 'Y') //creates Problem, Unit, and Skill Libraries
    		if(!createExcelFile(problemToSkillMap.keySet(), skillToProblemMap, problemToSkillMap))
    			error("Could not create Excel file");
    	
    	if(options.charAt(1) == 'y' || options.charAt(1) == 'Y') //creates .UNIT files (and .xcur if wide enough scope)
    		if(!createXCURFile())
    			error("Could not create Unit (or Xcur) file(s)");;
    	
    	if(options.charAt(2) == 'y' || options.charAt(2) == 'Y') //creates .UNIT files (and .xcur if wide enough scope)
    		if(!createUnitFiles())
    			error("Could not create Unit (or Xcur) file(s)");;
    	
		if(options.charAt(3) == 'y' || options.charAt(3) == 'Y') //creates the .bas files
    	{
    		new File(curDir + "bas").mkdir();
    		for(File f : brdFileMap.values())//so we traverse each file
				if(!createBAS(f, new File(curDir + "bas" + File.separator + f.getName().replace(".brd", ".bas")), problemToSkillMap.get(f)))
					error("Could not create BAS file");
    	}
		
		return true;
    }
    
    /**
     * For now, this will look into the given directory and search for a hierarchy.txt
     * If it is found, we use it to setup the maps
     * If it is not found, we use the root directory to generate the maps
     * 
     * Future implementations may parse from an Excel file or 
     */
    
    public void setup()
    {
    	setupMaps(root); //organizes all problems and skills into maps
    	
    	//checks if a "hierarchy.txt" file is found
    	boolean hierarchyExists = false;
		File[] files = root.listFiles();
		for(int i = 0; i < files.length; i ++)
			if(files[i].getName().equals(CTATtoLMS.HIERARCHY))
				hierarchyExists = true; //hehe guess this wasn't really necessary =p i'm lazy
    	
		Parse p = hierarchyExists ? new FromTxt(root) : new FromFolders(root);
		
		//creates the hierarchy.txt from folder directory
		if(!hierarchyExists)
		{
			debugging("Parsing from directory");
			if(!p.setup()) //if no hierarchy.txt found, creates one and fills curriculum
				error("Could not create hierarchy from folders");
		}
		else
		{
			debugging("Parsing from hierarchy.txt file");
			if(!p.setup()) //if not, then makes curriculum
				error("Could not create hierarchy from txt");
		}
		//by now, hierarchy determined, maps set up, ready to go, curriculum filled appropriately
    }
    
    public interface Parse
    {
    	//package private for a reason ...
    	boolean setup(); //this method will attempt to setup all maps, returns success    	
    }
    
    //this includes some utility methods for parsing through directories
    public class ParseUtil
    {
    	/**
	     * Find the depth of the .brd file with the longest file path.
	     * Recursively searches directories inside the given file. Depth is
	     * the number of directory components in longest path from parent to
	     * any .brd file in its directory subtree.
	     * @param parent file or directory to start
	     * @return depth of path from parent to .brd; 0 if parent is a .brd 
	     */
	    protected int getDepth(File parent)
	    {
	    	trace.out("skills", "CTATtoLMS.getDepth("+parent+")");
	    	if(!parent.getName().endsWith(".brd"))
	    	{	 //don't want to try looking just through folders since no guarantee there's a brd at the end
	    		int maxDepth = Integer.MIN_VALUE;
				File[] files = filter(parent, 0);
				for(int i = 0; i < files.length; i ++)
				{
					int curDepth = getDepth(files[i]) + 1;
					if(curDepth > maxDepth)
						maxDepth = curDepth;
				}
				return maxDepth;
	    	}
	    	return 0; //only gets here if parent is a .brd
	    }
    
	    //populates omittedFiles with misplaced .brd files
	    protected void checkFiles(File parent, int curDepth, int depth)
	    {
	    	if(curDepth >= depth - 1) //checks only if we are at a directory above depth
	    		return;
	    	File[] brds = filter(parent, -1);
	    	for(int i = 0; i < brds.length; i ++)
	    		omitted.add(brds[i].getName());
	    	
	    	File[] folders = filter(parent, 1);
	    	for(int i = 0; i < folders.length; i ++)
	    		checkFiles(folders[i], curDepth + 1, depth);
	    }
	    
	    /*
	     * This is the same as the one in FromTxt class ...
	     * should be nothing but .brd files in the parent (which is a folder) here
	     * Again, since we come from a folder, nonrandomization is assumed
	     */
	    protected Section getSection(File parent, String studentInterface, String platform, int sectionNum)
	    {
	    	Section section = new Section(parent.getName(), studentInterface, platform, false, sectionNum);
	    	File[] brds = filter(parent, -1);
	    	HashSet<String> skills = new HashSet<String>();
	    	for(int i = 0; i < brds.length; i ++)
	    	{
	    		section.addProblem(brds[i]);
	    		//emm when problem files for WebStart are placed in their own .jar,
	    		// should use the other addProblem() method
	    		HashSet<String> problemSkills = problemToSkillMap.get(brds[i]);
	    		if(problemSkills != null)
	    			for(String skill : problemSkills)
	    				skills.add(skill); //gets rid of repeats
	    	}
	    	for(String skill : skills) //places all the skills into the section
	    		section.addSkill(skill);
	    	
	    	return section;
	    }
    }
    
    private class FromTxt extends ParseUtil implements Parse
    {
    	private int depth; //field var for how deep folder given goes, used to determine hierarchy to build
    	
    	public FromTxt(File root)
    	{
    		this.depth = getDepth(root); //sets depth
	    	trace.out("cu", "FromTxt("+root+"): depth="+depth);
    		checkFiles(root, 0, depth); //should check for erroneous brds
        	if(omitted.size() > 0)
        	{
        		String omit = "";
        		omit += "These files to be omitted:\r\n";
        		for(String file : omitted)
        			omit += file + "\r\n";
        		warning(omit);
        	}
    	}
    
	   /**
		takes in a tab-delimited txt with optional options on the side
		* (curriculum name)
		*		(unit name)
		*			(section name)
		* 				(problem name)
		*/
	    
	    /*
	     * Sets up hierarchy from a .txt file (tab-delimited, more detail at top) into the curriculum object
	     * Note: To randomize a section, ex. "Section sec1	random"
	     */
	    public boolean setup()
	    {
	    	String contents = inputFile(new File(curDir + CTATtoLMS.HIERARCHY));
	    	contents = contents.replaceAll("\r\n\r\n", "\r\n"); //replaces double enters with single
	    	
	    	final String[] tabs = {"\t\t\t", "\t\t", "\t"}; //should never find 4 tabs ... hehe
	    	
	    	int sectionNum = 1; //iterates through to track section num in a unit
	    	
	    	//tests whether file has appropriate tabs (like a single-tab Unit followed by 3-tab Problem will fail)
	    	//also sets depth
	    	boolean found = true; //boolean for whether file is still good
	    	boolean began = false; //boolean for whether reached a tab that doesn't exist
	    	depth = 0; //sets to 0 first, if txt has only problems will not enter loop
	    	for(int i = 0; i < tabs.length; i ++)
	    	{
	    		found = (contents.indexOf(tabs[i]) != -1); //so okay is true as long as current tab is found
	    		if(began && !found) //so if a 3-tab is found and no 2-tabs are found ... returns an error
	    			return false;
	    		if(began || found)
	    		{
	    			depth = !began ? 3 - i : depth; //only sets depth on first bad tab
	    			began = true;
	    		}
	    	}
	    	trace.out("cu", "setup(): depth="+depth);
	    	String[] blocks = contents.split("\r\n");
	    	
	    	ArrayList<Section> curUnit = null;
	    	Section curSection = null;
	
	    	if(depth <= 1) //unit was not specified, i know this seems out of order with its section equivalent above, but it works this way to allow unit creation without specification
	    	{
	    		curUnit = new ArrayList<Section>();
	    		curriculum.add(curUnit);
	    		unitNames.add(root.getName());
	    	}
	    	if(depth == 0) //section not specified, not randomized
	    	{
	    		curSection = new Section(root.getName(), platform, studentInterface, false, 1); //default section # in unit is 1
	    		curUnit.add(curSection);
	    	}
	    	
	    	String blockInfo;
	    	Integer relDepth = null; //relative depth
	    	for(int i = 0; i < blocks.length; i ++) //iterates through each line
	    	{
	    		blockInfo = blocks[i];
	    		
	    		//determines relDepth
	    		if(!blockInfo.startsWith("\t"))
	    			relDepth = depth;
	    		else //so not starting over, then next should be at same level or one below, else throws error
	    		{
	    			int tabIndex = 0;
	    			for(; tabIndex < tabs.length && !blockInfo.startsWith(tabs[tabIndex]); tabIndex ++)
	    			{} //so basically, loop through array until we find the tab it starts with
					if(relDepth == 0 || relDepth - (depth - (3 - tabIndex)) == 1 || relDepth - (depth - (3 - tabIndex)) == -1) //if not a problem file or 0th level, checks if next entry is 1 tab before or beyond last
						relDepth = depth - (3 - tabIndex); //if so, sets correct
					else //note: 3 - tabIndex should be the number of tabs out there ...
					{
						error("Parsing error, hierarchy in file incorrect");
						return false;
					}
	    		}
		    	trace.out("cu", "setup(): relDepth="+relDepth+"; curUnit="+curUnit);
	    		
	    		blockInfo = blockInfo.trim(); //gets rid of beginning tabs
	    		
	    		if(relDepth == 0) //at problem level
	    		{
	    			if(blockInfo.indexOf("\t") != -1) //this is a webstart tutor with specific pathing information
	    				curSection.addProblem(blockInfo.split("\t")[0], blockInfo.split("\t")[1], null);
	    			else
	    			{ //this is a generic tutor whose name is all we want
	    				if(blockInfo.contains("/"))
	    					blockInfo = blockInfo.substring(blockInfo.lastIndexOf("/") + 1);
	    				if(blockInfo.contains("\\"))
	    					blockInfo = blockInfo.substring(blockInfo.lastIndexOf("\\") + 1);
	    				curSection.addProblem(blockInfo, blockInfo, null);
	    			}
	    		}
	    		else if(relDepth == 1) //section level
	    		{
	    			String[] sectionInfo = blockInfo.split("\t");
	    			/*if(sectionInfo.length > 1) //note: SectionName (StuInt Platform) (Random)
	    				curSection = new Section(sectionInfo);
	    			else //defaults to main arguments if these are not found
	    				curSection = new Section(sectionInfo[0], studentInterface, platform, randomized);*/
	    			curSection = new Section(sectionInfo, sectionNum ++); //everything is now parsed in here, errors are thrown in here, etc
					curUnit.add(curSection);
				}
	    		else if(relDepth == 2) //unit level
				{
					curUnit = new ArrayList<Section>();
					curriculum.add(curUnit);
					unitNames.add(blockInfo);
					sectionNum = 1; //reset sectionNum
				}
	    	}
	    	
	    	return true;
	    }
    }
    
    private class FromFolders extends ParseUtil implements Parse
	{
    	private int depth; //field var for how deep folder given is, used to determine hierarchy to build
    	
    	public FromFolders(File root)
    	{
    		this.depth = getDepth(root); //sets depth
    		checkFiles(root, 0, depth); //should check for erroneous brds
        	if(omitted.size() > 0)
        	{
        		String omit = "";
        		omit += "These files to be omitted:\r\n";
        		for(String file : omitted)
        			omit += file + "\r\n";
        		warning(omit);
        	}
    	}
	    
	    /*
	     * Sets up the hierarchy from folders into the curriculum object
	     * Note: No option for randomization is provided here
	     */
	    public boolean setup()
	    {
	    	ArrayList<Section> unit;
	    	//omitted files will be printed when determining depth (called from constructor)
			
	    	//means root given is simply a .brd file
			//creates a .UNIT file with a single section including the sole .brd problem
			if(depth == 0)
			{
				unit = new ArrayList<Section>();
				Section section = new Section(root.getName(), studentInterface, platform, false, 1); //default section # in unit is 1
				section.stuIntFlag = checkBadString(studentInterface);
				section.platformFlag = checkBadString(platform);
				
				section.addProblem(root);
				for(String skill : skillToProblemMap.keySet())
					section.addSkill(skill);
				unit.add(section);
				curriculum.add(unit);
				unitNames.add(root.getName());
			}
			//here root given is a folder (section) of .brd files
			//creates a single .unit file with a single section including all problems in that section
			if(depth == 1)
			{
				unit = new ArrayList<Section>();
				Section section = getSection(root, studentInterface, platform, 1); //default sectionNum since only 1 section in this .unit file
				unit.add(section);
				curriculum.add(unit);
				unitNames.add(root.getName());
			}
			//root given is a folder (unit) of folders (sections) of .brd files
			//creates a .UNIT file with arranged sections, with problems as organized in section folders
			else if(depth == 2)
			{
				unit = new ArrayList<Section>();
				File[] sections = filter(root, 1);
				for(int i = 1; i <= sections.length; i ++)
				{
					unit.add(getSection((sections[i]), studentInterface, platform, i));
				}
				curriculum.add(unit);
				unitNames.add(root.getName());
			}
			//root given is a folder (curriculum) of folders (units) of folders (sections) of .brd files
			//creates a group of .UNIT files as before, but also with a .XCUR file with organized units
			else if(depth == 3)
			{
				File[] unitFiles = filter(root, 1);
				for(int i = 0; i < unitFiles.length; i ++)
				{
					unit = new ArrayList<Section>();
					File[] sectionFiles = filter(unitFiles[i], 1);
					for(int j = 1; j <= sectionFiles.length; j ++)
						unit.add(getSection(sectionFiles[j], studentInterface, platform, j));
					curriculum.add(unit);
					unitNames.add(unitFiles[i].getName());
				}
				curricName = root.getName();
			}
			//too many nested folders!
			else
			{
				error("Unrecognized hierarchy");
				return false;
			}
			return true;
		}
	}

    //prepares the skill maps and file map for all .BRDs, all .BAS's files are now in the same directory
    private void setupMaps(File parent)
    {
    	if(!parent.getName().endsWith(".brd"))
     	{
     		File[] files = filter(parent, 0);
         	for(int i = 0; i < files.length; i ++)
         		setupMaps(files[i]);
     	}
     	else
     	{
 			brdFileMap.put(parent.getName(), parent); 			
 			//gets all of the production rules from a .BRD file
 			String brdContents = inputFile(parent);
 			String[] prodRules = brdContents.split("<productionRule>");
 			for(int j = 1; j < prodRules.length; j ++)
 			{
 				String ruleName = prodRules[j].split("<ruleName>")[1].split("</ruleName")[0];
 				String prodSet = prodRules[j].split("<productionSet>")[1].split("</productionSet")[0];
 				String skillName = ruleName + ' ' + prodSet;
 				
 				ArrayList<File> fileSet;
 				if((fileSet = skillToProblemMap.get(skillName)) == null)
 					fileSet = new ArrayList<File>();
 				fileSet.add(parent);
 				skillToProblemMap.put(skillName, fileSet);
 				
 				HashSet<String> skillNames;
 				if((skillNames = problemToSkillMap.get(parent)) == null)
 					skillNames = new HashSet<String>();
 				skillNames.add(skillName);
 				problemToSkillMap.put(parent, skillNames);
 			}
 		}
    }
    
    
    /*
     * The following methods are specifically for creating Unit, XCur, and .brd files
     * from the populated maps
     */
    private boolean createUnitFiles()
    {
    	boolean success = true;;
    	for(int i = 0; i < curriculum.size(); i ++)
    	{
    		ArrayList<Section> unit = curriculum.get(i);
    		success = success && createUnitFile(unitNames.get(i), unit);
    	}

    	return success;
    }
    
    //gets the Skills matrix for the unit
    private HashMap<String, ArrayList> getMatrix(ArrayList<Section> unit)
    {
    	Vector unitRuleProductionList = new Vector(); //this is supposed to be empty at start, will get filled in by ProblemModel.checkAddRules()
    	HashMap<String, ArrayList> unitSkillsFrequency = new HashMap<String, ArrayList>(); //each element in this vector is a vector with first element problemName, following are the skill counts corresponding to the skills in the RuleProdList passed in hopefully
    	
    	for(Section s : unit)
    	{
    		//if we have problems with ruleProd lists ... can set them more locally here
    		for(File problem : s.getProblemFiles())
	    	{
	    		//get problem files from fileMap.keySet()
				//perhaps this should be done from elsewhere ...
    	    	RuleProduction.Catalog rpc = new RuleProduction.Catalog();
				ProblemStateReaderJDom psrj = new ProblemStateReaderJDom(null);
		        ProblemModel pm = psrj.loadBRDFileIntoProblemModel(problem.getAbsolutePath(), rpc);
		        ArrayList problemFrequency;
		        List<Integer> tempVector;
		        ProblemGraph problemGraph;
		        if (pm != null) {
		            tempVector = SkillMatrixDialog.problemSkillsMatrix(pm, unitRuleProductionList, rpc); //emm in SkillsMatrixDialog, looks like the whole thing's just passed down anyway
		
		            problemFrequency = new ArrayList(tempVector);
		            // 1st element is the problem name
//		            problemFrequency.add(problem);
		            //i'll be storing this in hashmap ... with name as key
		
		            unitSkillsFrequency.put(problem.getName(), problemFrequency);
		        }
	    	}
    	}
    	
    	//okay so by now unitRuleProductionList should be an ordered Vector with all
    	//the skills corresponding to the unitSkillsFrequency's arraylists ...
    	
    	//so now we'll put an extra value into the hashmap will all of the production rule names as strings
    	ArrayList productionRules = new ArrayList(); 
    	for(int i = 0; i < unitRuleProductionList.size(); i ++)
    		productionRules.add(unitRuleProductionList.get(i));
    	unitSkillsFrequency.put("skills", productionRules);
    	
    	return unitSkillsFrequency;
    }
    
    //creates the .BAS files in a "bas" subfolder of root
    //only creates if a .BAS file is not present ...
    private boolean createBAS(File input, File output, Set<String> skillNames)
    {
    	debugging("Parsing " + input.getName());
    	
    	//may be some problem with the problem description ...
    	final String problemHeader = "<?xml version=\"1.0\" encoding=\"US-ASCII\"   ?>\n<LMS:Problem id=\"_PROBLEM_NAME_\" name=\"_PROBLEM_NAME_\" problem_type=\"PROBLEM\" description=\"filler\">\r\n";
    	final String skillInfo = "<LMS:ProblemSkill subskill_id=\"_SKILL_\" skill_count=\"1\"/>\r\n";
		final String problemFooter = "</LMS:Problem>";
    	
    	String problemName = input.getName().replaceAll(".brd", "");
		
		String contents = "";
		contents += problemHeader.replaceAll("_PROBLEM_NAME_", problemName);
		
		if(skillNames != null) //possible that there are no skills for this problem ...
			for(String skillName : skillNames)
				contents += skillInfo.replaceAll("_SKILL_", skillName);
		
		contents += problemFooter;

		return outputFile(output, contents);
    }
    
    //notes: The row in the worksheet header seems to be accurate, however
    //the active row in the worksheet footer seems about half of the "row" attribute
    //currently we are ignoring those in the footer, if bad things happen ... look there
    /**
     * d The directory containing .BRD files
     * files a Set of the .BRD files
     */
    private boolean createExcelFile(Set<File> files, HashMap<String, ArrayList<File>> skillToProblemMap,
    		HashMap<File, HashSet<String>> problemToSkillMap)
    {
    	debugging("Creating xml document " + root.getName());
    	
    	ExcelFormatter x = new ExcelFormatter(files, skillToProblemMap, problemToSkillMap);
    	
    	String xmlContents = "";
    	xmlContents += x.xmlHeader(); 
    	xmlContents += x.createProblemLibrary();
    	
    	//creates a Unit Library for each unit specified
    	for(int i = 0; i < curriculum.size(); i ++)
    		xmlContents += x.createUnitLibrary(unitNames.get(i), curriculum.get(i));
    	
    	xmlContents += x.createSkillLibrary();
    	xmlContents += x.xmlFooter();
    	
    	return outputFile(new File(curDir + root.getName() + ".xml"), xmlContents); //outputs
    }
    
    /**
     * Create a CL Unit file with appropriate skills, and all problems in a single section
     * arranged as listed in the folder
     */
    //NOTES: fill in the blanks!
    private boolean createUnitFile(String unitName, List<Section> unit)
    {
    	debugging("Creating unit file " + unitName);
    	
    	/*
    	 * This may seem a bit complicated, especially the problemSetMember thing ... the problemSetMembers and problemSets are nested (always at least 2 deep)
    	 * */
    	
    	final String unitHeader = "<?xml version=\"1.0\" encoding=\"US-ASCII\"  ?>\r\n<!-- $Id: CTATtoLMS.java 18476 2012-10-29 23:07:25Z sewall $\r\n     $Name$ -->\r\n \r\n  <LMS:Unit  xmlns:LMS=\"http://www.carnegielearning.com/LMS\" id=\"_UNIT_NAME_\"  name=\"_UNIT_NAME_\" >\r\n";
    	//_DATE_;_UNIT_NAME_
    	final String sectionHeader = "    <LMS:Section id=\"_UNIT_NAME_-_SECTION_NUM_\" name=\"_SECTION_NAME_\" completion_code_freq=\"PROBLEM_COMPLETION\">\r\n";
    	//_UNIT_NAME_, _SECTION_NUM_, _SECTION_NAME_
    	final String skillInfo = "       <LMS:SectionSkill skill_id=\"_SKILL_\" skill_name=\"_SKILL_\" p_guess=\"0.4\" p_learn=\"0.1\" p_slip=\"0.2\" initial_p_known=\"0.66\" displayed=\"true\" remediated=\"true\">\r\n          <LMS:SectionSubSkill subskill_id=\"_SKILL_\" subskill_name=\"_SKILL_\"/>\r\n       </LMS:SectionSkill>\r\n";
    	//_SKILL_
    	final String outerProblemSetHeader = "       <LMS:ProblemSet id=\"_PROBLEM_SET_NAME_-_PROBLEM_SET_NUM_\" problem_count=\"2\" repeat_problems=\"2\">\r\n          <LMS:SelectionInfo>\r\n             <LMS:SelectionAlgo id=\"Ordered-1\" name=\"Ordered\" execution_point=\"precompile\"/>\r\n          </LMS:SelectionInfo>\r\n";
    	//_PROBLEM_SET_NAME_, _PROBLEM_SET_NUM_
    	final String executableInfo = "       <LMS:Executable id=\"_PLATFORM_\" name=\"_PLATFORM_\" type=\"LISP\" platform=\"ANY\" entry_point=\"cl.tutors.tre.LMS_Session\"/>\r\n";
    	//_PLATFORM_
    	final String toolInfo = "          <LMS:ToolSpec id=\"_STU_INT_\" >\r\n		<LMS:Tool id=\"_STU_INT_\" name=\"_STU_INT_\" type=\"_STUD_INT_PATH_\" subtype=\"\" />\r\n             <LMS:SetOptions>\r\n                <LMS:AttValue attribute=\"ToolIsRequired\" value=\"true\" />\r\n                <LMS:AttValue attribute=\"layout_template\" value=\"3\"/>\r\n                <LMS:AttValue attribute=\"layout_region\" value=\"2\"/>\r\n                <LMS:AttValue attribute=\"layout_tab\" value=\"1\"/>\r\n             </LMS:SetOptions>\r\n             <LMS:DefaultOptions>\r\n             </LMS:DefaultOptions>\r\n          </LMS:ToolSpec>\r\n";
    	//_STU_INT_;_STUD_INT_PATH_
    	final String outerProblemSetMemberHeader = "          <LMS:ProblemSetMember>\r\n";
    	final String innerProblemSetHeader = "             <LMS:ProblemSet id=\"_INNER_PROBLEM_SET_NAME_-_INNER_PROBLEM_SET_NUM_-PS-1\" problem_count=\"10\" repeat_problems=\"2\">\r\n";
    	//_INNER_PROBLEM_SET_NAME_, _INNER_PROBLEM_SET_NUM_
    	final String innerProblemSetMemberHeader = "          <LMS:ProblemSetMember>\r\n";
    	final String randomSelectionAlgo = "                <LMS:SelectionInfo>\r\n                   <LMS:SelectionAlgo id=\"Random-1\" name=\"Random\" execution_point=\"precompile\"/>\r\n                </LMS:SelectionInfo>\r\n";
        final String orderedSelectionAlgo = "                <LMS:SelectionInfo>\r\n                   <LMS:SelectionAlgo id=\"Ordered-1\" name=\"Ordered\" execution_point=\"precompile\"/>\r\n                </LMS:SelectionInfo>\r\n";
        final String problemInfo = "                   <LMS:Problem id=\"_PROBLEM_ID_\" name=\"_PROBLEM_NAME_\"/>\r\n";
    	//_PROBLEM_NAME_;_PROBLEM_ID_
    	final String innerProblemSetMemberFooter = "                </LMS:ProblemSetMember>\r\n";
    	final String innerProblemSetFooter = "             </LMS:ProblemSet>\r\n";
    	final String outerProblemSetMemberFooter = "          </LMS:ProblemSetMember>\r\n";    	
    	final String outerProblemSetFooter = "       </LMS:ProblemSet>\r\n";
    	final String sectionFooter = "    </LMS:Section>\r\n";
    	final String unitFooter = " </LMS:Unit>\r\n";
    	
    	String date = getDate();
    	
    	String unitContents = "";
    	unitContents += unitHeader.replaceAll("_UNIT_NAME_", unitName).replaceAll("_DATE_", date); //adds the unit file header
    	
    	for(Section s : unit)
	    {
    		unitContents += sectionHeader.replaceAll("_UNIT_NAME_", unitName).replaceAll("_SECTION_NUM_", s.getNum()).replaceAll("_SECTION_NAME_", s.getName()); //adds the section header, NOTE: section's are (unitName)-(num) for CL's purposes
	    	for(String skill : s.getSkills()) //puts in a Section skill and subskill for each production rule
	    		unitContents += skillInfo.replaceAll("_SKILL_", skill);
	    	unitContents += executableInfo.replaceAll("_PLATFORM_", s.getPlatform()); //adds platform
	    	if(s.platformFlagged())
	    		warning("Null or empty platform used in " + unitName + ".UNIT file");
	    	unitContents += outerProblemSetHeader.replaceAll("_PROBLEM_SET_NAME_", unitName).replaceAll("_PROBLEM_SET_NUM_", s.getNum());
	    	unitContents += toolInfo.replaceAll("_STU_INT_", s.getStudentInterface()).replaceAll("_STUD_INT_PATH_", s.getStudentInterface()); //eventually may want different path and stu_int for webstart
	    	if(s.stuIntFlagged())
	    		warning("Null or empty student interface used in " + unitName + ".UNIT file");
	    	unitContents += outerProblemSetMemberHeader;
	    	unitContents += innerProblemSetHeader.replaceAll("_INNER_PROBLEM_SET_NAME_", unitName).replaceAll("_INNER_PROBLEM_SET_NUM_", s.getNum());
	    	unitContents += innerProblemSetMemberHeader;
	    	if(s.randomized())
	    		unitContents += randomSelectionAlgo;
	    	else
	    		unitContents += orderedSelectionAlgo;
	    	for(String problem : s.getProblems()) //adds each problem in
	    	{
	    		problem = problem.replaceAll(".brd", ""); //takes away any .brd endings for unit file use (hopefully the problem jar name won't randomly have a .brd)
	    		unitContents += problemInfo.replaceAll("_PROBLEM_NAME_", problem.split(";")[0]).replaceAll("_PROBLEM_ID_", problem.split(";")[1]);
	    	}
	    	
	    	unitContents += innerProblemSetMemberFooter;
	    	unitContents += innerProblemSetFooter;
	    	unitContents += outerProblemSetMemberFooter;
	    	unitContents += outerProblemSetFooter;
	    	unitContents += sectionFooter;
	    }
    	unitContents += unitFooter;
    	return outputFile(new File(curDir + unitName + ".unit"), unitContents);
    }
	
    //should only be called if depth is high enough
    private boolean createXCURFile()
    {
    	if(curricName == null) //hierarchy not nested deep enough
    		curricName = JOptionPane.showInputDialog(null, "Curriculum name: ");
    	
    	if(curricName == null) //user hit cancel
    		return false;
    	
    	debugging("Creating xcur file " + curricName + ".xcur");
    	
    	final String xcurHeader = "<?xml version=\"1.0\" encoding=\"US-ASCII\"  ?>\r\n<!-- $Id: CTATtoLMS.java 18476 2012-10-29 23:07:25Z sewall $\r\n     $Name$ -->\r\n\r\n<LMS:Curriculum xmlns:LMS=\"http://www.carnegielearning.com/LMS\"\r\n                id=\"_CURRICULUM_NAME_\" name=\"_CURRICULUM_NAME_\">\r\n ";
    	//_CURRICULUM_NAME_
    	final String unitInfo = "<LMS:Unit id=\"_UNIT_NAME_\"  unit_introduction=\"_UNIT_NAME_\" unit_summary=\"_UNIT_NAME_\" />\r\n <!-- review sections -->\r\n";
    	//_UNIT_NAME_
    	final String xcurFooter = " </LMS:Curriculum>";
    	
    	String xcurContents = "";
    	xcurContents += xcurHeader.replaceAll("_CURRICULUM_NAME_", curricName);
    	for(int i = 0; i < unitNames.size(); i ++)
    		xcurContents += unitInfo.replaceAll("_UNIT_NAME_", unitNames.get(i));
    	xcurContents += xcurFooter;
    	return outputFile(new File(curDir + curricName + ".xcur"), xcurContents);
    }
    
    private String getDate()
    {
    	Calendar c = Calendar.getInstance();
    	return "" + c.get(Calendar.YEAR) + '-' + c.get(Calendar.MONTH) + '-' + c.get(Calendar.DAY_OF_MONTH) + 'T' + c.get(Calendar.HOUR_OF_DAY) + ':' + c.get(Calendar.MINUTE) + ':' + c.get(Calendar.SECOND);
    }
    
    private boolean outputFile(File target, String contents)
	{
    	debugging("Writing to " + target.getName());
    	
    	PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(target);
			pw.print(contents);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return false;
		}
		finally
		{
			pw.flush();
			pw.close();
		}
		return true;
	}
    
    /**
     * Takes a file and returns its contents as a string, will return null if it is a folder
     * @param input a file (not a folder)
     * @return the contents as a string
     */
    private String inputFile(File input)
    {
    	if(input.isDirectory())
    		return null;
    	debugging("Parsing: " + input.getAbsolutePath());
    	StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(input));
			String line = null;
			while((line = br.readLine()) != null)
				sb.append(line).append("\r\n");
		}
		catch(IOException e)
		{
			error("IOException, could not read file: " + input.getAbsolutePath());
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(IOException e)
			{
				error("Could not close buffered reader, while reading file: " + input.getAbsolutePath());
			}
		}
		return sb.toString();
    }
    
    private void debugging(String message)
    {
    	if(msgOption[0])
    		if(fromMenu)
    			history += "Debug: " + message + "\r\n";
    		else
    			trace.out(message);
    }
    
    private void error(String error)
    {
    	if(msgOption[1])
    		if(fromMenu)
    			history += "Error: " + error + "\r\n";
    		else
    			System.err.println("Error: " + error);
    }
    
    //used for warnings, ie. null/empty platform used in a unit file, .BRD file not found though listed in a hierarchy.txt
    private void warning(String warning)
    {
    	if(msgOption[2])
    		if(fromMenu)
    			history += "Warning: " + warning + "\r\n";
    		else
    			trace.out("Warning: " + warning);
    }
    
    private boolean checkBadString(String toCheck)
    {
    	return toCheck == null || toCheck.trim().equals("");
    }
    
    /**
     * This filter retrieves only brds and folders (excluding the bas folder if found)
     * @param parent directory
     * @param filter -1 only brds, 0 brds and folders, 1 only folders
     * @return list of files that pass the filter
     */
    private File[] filter(File parent, int filter)
    {
    	File[] result = null;
    	if(filter == -1)
    		result = parent.listFiles( new FileFilter(){
					public boolean accept(File filename)
					{return filename.getAbsolutePath().endsWith(".brd");}
				} );
    	else if(filter == 0)
    		result =  parent.listFiles( new FileFilter(){
				public boolean accept(File filename)
				{return filename.getAbsolutePath().endsWith(".brd") || (filename.isDirectory() && !filename.getName().equals("bas"));}
			} );
    	else  	//should be 1 if not ...
    		result = parent.listFiles( new FileFilter(){
    			public boolean accept(File filename)
    			{return (!filename.getAbsolutePath().contains(".") && !filename.getName().equals("bas"));}
    		} );
    	return ( result == null ? new File[0] : result );
    }
    
    //would make a unit class, but a unit is just a name, associated with an ordered list of sections
    private class Section
    {
    	private String sectionName;
    	private ArrayList<String> problemList = new ArrayList<String>(); //ordered list of problems (with any exts)
    	private ArrayList<File> problemFileList = new ArrayList<File>(); //ordered list of problem files (only .brds!) see notes on which addProblem method to use
    	private ArrayList<String> skillList = new ArrayList<String>();
    	private String stu_int; //student interface
    	private String platform_; //platform type
    	private String sectionNum; //increments for each section in a unit (specifically used to track section IDs in the .unit file)
    	
    	//flags for whether they are null or empty Strings
    	//warning will be given if used as such in a .UNIT file
    	private boolean platformFlag = false;
    	private boolean stuIntFlag = false;
    	
    	private boolean randomize = false;
    	
    	//randomize boolean is not used here, since all hierarchies from .txt go to the other constructor
    	public Section(String sectionName, String stu_int, String platform, boolean randomize, int sectionNum)
    	{
    		this.sectionName = sectionName;
    		this.stu_int = stu_int;
    		this.platform_ = platform;
    		if(checkBadString(platform))
    			platformFlag = true;
    		if(checkBadString(studentInterface))
    			stuIntFlag = true;
    		this.randomize = randomize;
    		this.sectionNum = "" + sectionNum;
    	}
    	
    	public Section(String[] sectionInfo, int sectionNum)
    	{
    		if(sectionInfo.length == 0)
    			error("Section name missing");
    		this.sectionName = sectionInfo[0];
    		this.sectionNum = "" + sectionNum;
    		if(sectionInfo.length == 2) //"name random" expected
    			this.randomize = sectionInfo[1].contains(RANDOM);
    		
    		if(sectionInfo.length >= 3) //"name stuInt platform" expected
    		{
	    		this.stu_int = sectionInfo[1];
	    		this.platform_ = sectionInfo[2];
	    		if(sectionInfo.length >= 4) //"name stuInt platform random" expected
	    			this.randomize = sectionInfo[3].contains(RANDOM);
    		}
    		else //here the stuInt and platform were not stored
    		{
        		this.stu_int = studentInterface;
        		this.platform_ = platform;    			
    		}
    		
    		if(checkBadString(platform_))
				platformFlag = true;
			if(checkBadString(studentInterface))
				stuIntFlag = true;
    	}
    	
    	//sets the name and ID specifically for not normal pathing
    	public void addProblem(String problemID, String problemName, File problem)
    	{
    		if(problem == null)
        		problem = brdFileMap.get(problemID); //maybe name, but as of now, ID
    		if(problem == null)
    			error("Could not find problem: " + problemID);
    		problemFileList.add(problem);
    		problemList.add(problemID + ";" + problemName);
    	}
    	
    	//sets a name and problemFile, since a .brd will probably be put in this way
    	//use above for problem.jars as wanted for WebStart, note that this jar will not be in the problemFileList though
    	public void addProblem(File problem)
    	{
    		String problemName = problem.getName();
    		addProblem(problemName, problemName, problem); //should be the same if not different =p
    	}
    	
    	public void addSkill(String skillName)
    	{
    		skillList.add(skillName);
    	}
    	
    	//just the names (as put into the hierarchy.txt)
    	public ArrayList<String> getProblems()
    	{
    		return problemList;
    	}
    	
    	public ArrayList<File> getProblemFiles()
    	{
    		return problemFileList;
    	}
    	
    	public ArrayList<String> getSkills()
    	{
    		return skillList;
    	}
    	
    	public String getStudentInterface()
    	{
    		return stu_int;
    	}
    	
    	public String getPlatform()
    	{
    		return platform_;
    	}
    	
    	public String getName()
    	{
    		return sectionName;
    	}
    	
    	//this is used for section ID in the .unit file
    	public String getNum()
    	{
    		return sectionNum;
    	}
    	
    	public boolean platformFlagged()
    	{
    		return platformFlag;
    	}
    	
    	public boolean stuIntFlagged()
    	{
    		return stuIntFlag;
    	}
    	
    	public boolean randomized()
    	{
    		return randomize;
    	}
    }
    
    private class ExcelFormatter
    {
    	private final int horizTextWidth = 6;
    	private final int rotatedTextWidth = 3;
    	
    	//xml formatting stuff, feel free to change if errors occur ... there may be stuff missing here, its filled in by Excel
    	private final String xmlHeader = "<?xml version=\"1.0\"?>\r\n<?mso-application progid=\"Excel.Sheet\"?>\r\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\r\n xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\r\n xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\r\n xmlns:html=\"http://www.w3.org/TR/REC-html40\">\r\n <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\r\n  <Author>Somebody</Author>\r\n  <LastAuthor>Somebody</LastAuthor>\r\n  <Created>_DATE_</Created>\r\n  <LastSaved>_DATE_</LastSaved>\r\n  <Version>11.6360</Version>\r\n </DocumentProperties>\r\n <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\r\n  <WindowHeight>7425</WindowHeight>\r\n  <WindowWidth>15195</WindowWidth>\r\n  <WindowTopX>0</WindowTopX>\r\n  <WindowTopY>75</WindowTopY>\r\n  <ProtectStructure>False</ProtectStructure>\r\n  <ProtectWindows>False</ProtectWindows>\r\n </ExcelWorkbook>\r\n";
    	//
    	private final String cellStyleOptions = " <Styles>\r\n  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\r\n   <Alignment ss:Vertical=\"Bottom\"/>\r\n  </Style>\r\n  <Style ss:ID=\"s23\">\r\n   <Alignment ss:Vertical=\"Bottom\" ss:Rotate=\"60\"/>\r\n  </Style>\r\n </Styles>\r\n";
    	//
    	private final String worksheetHeader = " <Worksheet ss:Name=\"_WORKSHEET_NAME_\">\r\n  <Table>\r\n";
    	//_WORKSHEET_NAME_;
    	private final String columnInfo = "   <Column ss:Index=\"_INDEX_\" ss:Hidden=\"_HIDDEN_\" ss:Width=\"_WIDTH_\"/>\r\n";
    	//_INDEX_;_HIDDEN_VALUE_(0 or 1);_WIDTH_
    	private final String rowHeader = "   <Row>\r\n";
    	//
    	private final String regCellHeader = "    <Cell>";
    	//
    	private final String rotatedCellHeader = "    <Cell ss:StyleID=\"s23\">";
    	//
    	private final String specificColCellHeader = "    <Cell ss:Index=\"_COL_\"";
    	//_COL_
    	private final String regCellFooter = "</Cell>\r\n";
    	//
    	private final String stringData = "<Data ss:Type=\"String\">_DATA_</Data>";
    	//_DATA_
    	private final String integerData = "<Data ss:Type=\"Number\">_DATA_</Data>";
    	//_DATA_
    	//this one is deprecated
    	private final String skillCountCellInfo = "   <Cell ss:Formula=\"=COUNTIF('Skills Library'!C[_COL_INDEX_],&quot;=&quot;&amp;RC1)\"><Data ss:Type=\"Number\"></Data></Cell>\r\n";
    	//_COL_INDEX_ (corresponds to column from skill library)
    	private final String rowFooter = "   </Row>\r\n";
    	//
    	private final String worksheetFooter = "  </Table>  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\r\n   <Selected/>\r\n   <Panes>\r\n    <Pane>\r\n     <Number>3</Number>\r\n     <ActiveRow>7</ActiveRow>\r\n     <ActiveCol>4</ActiveCol>\r\n    </Pane>\r\n   </Panes>\r\n   <ProtectObjects>False</ProtectObjects>\r\n   <ProtectScenarios>False</ProtectScenarios>\r\n  </WorksheetOptions>\r\n </Worksheet>\r\n ";
    	//
    	private final String xmlFooter = "</Workbook>";
    	//
    	
    	private Set<File> files;
    	private HashMap<String, ArrayList<File>> skillToProblemMap;
		private HashMap<File, HashSet<String>> problemToSkillMap;
		private ArrayList<String> skillList;
		private ArrayList<String> problemList;
		
    	public ExcelFormatter(Set<File> files, HashMap<String, ArrayList<File>> skillToProblemMap,
    			HashMap<File, HashSet<String>> problemToSkillMap)
    	{
    		this.files = files;
    		this.skillToProblemMap = skillToProblemMap;
    		this.problemToSkillMap = problemToSkillMap;
    		
    		//setup for libraries
        	//these lists are used to maintain link between the different libraries
        	skillList = new ArrayList<String>(); //arrayList to keep track of which skills are done
        	for(String skill : skillToProblemMap.keySet())
        		skillList.add(skill); //adds each skill in the hashmap to the arrayList
        	problemList = new ArrayList<String>();
        	for(File f : problemToSkillMap.keySet())
        		problemList.add(f.getName());
    	}
    	
    	//header
    	public String xmlHeader()
    	{
    		String xmlContents = "";
    		xmlContents += xmlHeader.replaceAll("_DATE_", date); //sticks in the header
    		xmlContents += cellStyleOptions;
    		return xmlContents;
    	}
    	
    	//footer
    	public String xmlFooter()
    	{
    		return xmlFooter;
    	}

    	//creates the Problem Library
    	//a list of all .BRD files found
    	public String createProblemLibrary()
    	{
    		String xmlContents = "";
        	xmlContents += worksheetHeader.replaceAll("_WORKSHEET_NAME_", "Problem Library").replaceAll("_ROW_", "" + files.size()).replaceAll("_COL_", "" + 3);
        	
        	//makes columns pretty
        	int max = 0, cur;
        	for(File f : files)
        		if(max < (cur = f.getName().length()))
        			max = cur;
        	
        	xmlContents += columnInfo.replaceAll("_INDEX_", "" + 1).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + (horizTextWidth * max));
        	xmlContents += columnInfo.replaceAll("_INDEX_", "" + 2).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + (horizTextWidth * (studentInterface.length() > 17 ? studentInterface.length() : 17))); //17 is for "Student Interface"
        	xmlContents += columnInfo.replaceAll("_INDEX_", "" + 3).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + (horizTextWidth * (platform.length() > 8 ? platform.length() : 8))); //8 is for "Platform"
        	
        	xmlContents += rowHeader;
        	xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "Problem Name") + regCellFooter;
        	xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "Student Interface") + regCellFooter;
        	xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "Platform") + regCellFooter;
        	xmlContents += rowFooter;
        	
        	for(File f : files)
        	{
        		xmlContents += rowHeader;
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", f.getName()) + regCellFooter;
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", studentInterface) + regCellFooter;
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", platform) + regCellFooter;
        		xmlContents += rowFooter;
        	}
        	xmlContents += worksheetFooter;
        	return xmlContents;
    	}
    	
    	//tries to create a Unit library using skill counts for each problem from skillsMatrixDialog implementation
    	public String createUnitLibrary(String unitName, ArrayList<Section> unit)
    	{
    		//gets the proper skill counts for each problem (source code is taken fro SkillMatrixDialog)
    		HashMap<String, ArrayList> unitSkillsFrequency = getMatrix(unit);
    		ArrayList skills = unitSkillsFrequency.get("skills");
    		
    		String xmlContents = "";
        	xmlContents += worksheetHeader.replaceAll("_WORKSHEET_NAME_", unitName).replaceAll("_ROW_", "" + unitSkillsFrequency.keySet().size()).replaceAll("_COL_", "" + (skills.size() + 1));

        	//makes pretty (the excel file)
        	int max = 0, cur;
        	for(Section s : unit)
        	{
        		for(String problemData : s.getProblems())
        			if(max < (cur = problemData.split(";")[0].length()))
        				max = cur;
        		if(max < (cur = s.getName().length()))
        			max = cur;
        	}
        	xmlContents += columnInfo.replaceAll("_INDEX_", "" + 1).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + (horizTextWidth * (max > 12 ? max : 12))); //12 is for "Skill Counts"
        	
        	int i = 2; //counter for column, starts at 2 for skills
        	for(Object skillNameObj : skills)
        	{
        		xmlContents += columnInfo.replaceAll("_INDEX_", "" + i).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + rotatedTextWidth *((String)skillNameObj).length());
        		i ++;
        	}
        	
        	xmlContents += rowHeader;
        	xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "Skill Counts") + regCellFooter;
        	for(Object skillNameObj : skills)
        		xmlContents += rotatedCellHeader + stringData.replaceAll("_DATA_", (String)skillNameObj) + regCellFooter;
        	xmlContents += rowFooter;
	        
        	for(Section s : unit)
        	{
        		xmlContents += rowHeader;
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", s.getName()) + regCellFooter;
        		xmlContents += rowFooter;
	        	for(String problemData : s.getProblems()) //if we want this organized by section ... change it here
	        	{
	        		String problemName = problemData.split(";")[0];
	        		if(problemName != "skills")
	        		{
	        			xmlContents += rowHeader;
	        			xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "" + problemName) + regCellFooter;
	        			for(Object integerVal : unitSkillsFrequency.get(problemName))
	        				if((Integer) integerVal == 0)
	        					xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "") + regCellFooter;
	        				else
	        					xmlContents += regCellHeader + integerData.replaceAll("_DATA_", "" + (Integer)integerVal) + regCellFooter;
	        			xmlContents += rowFooter;
	        		}
	        	}
        	}
        	
        	xmlContents += worksheetFooter;
    		return xmlContents;
    	}
    	
    	/**
    	 * creates a Unit Library from a Section object
    	 * NOTE: With existing implementation, this program does not keep track of multiple skill counts for a single skill and problem
    	 * it instead relies on the Skill Library through functions to count occurrences of skills
    	 * NOTE: this probably won't work anymore since the function call points to the wrong cell now :)
    	 * ahh this is probably deprecated ... but i spent so much time on it ... =p
    	 */ 
    	public String createOldUnitLibrary(String unitName, ArrayList<Section> unit)
    	{
    		String xmlContents = "";

    		HashSet<String> unitProblems = new HashSet<String>();
    		HashMap<String, Integer> unitSkills = new HashMap<String, Integer>();
    		
    		for(Section s : unit)
    		{
    			for(String sectionProblem : s.getProblems())
    				unitProblems.add(sectionProblem.split(";")[0]);
    			for(String sectionSkill : s.getSkills())
    				unitSkills.put(sectionSkill, new Integer(skillList.indexOf(sectionSkill)));
    		}
    		
        	xmlContents += worksheetHeader.replaceAll("_WORKSHEET_NAME_", unitName).replaceAll("_ROW_", "" + (unitProblems.size() + 1)).replaceAll("_COL_", "" + (unitSkills.size() + 1));
        	
        	xmlContents += rowHeader;
        	xmlContents += regCellHeader + stringData.replaceAll("_DATA_", "Skill Counts") + regCellFooter;
        	for(String skill : unitSkills.keySet())
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", skill) + regCellFooter;
        	xmlContents += rowFooter;
        	
        	for(String problemName : unitProblems)
        	{
        		xmlContents += rowHeader;
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", problemName) + regCellFooter;
        		int i = 0;
        		for(String skill : unitSkills.keySet())
        		{
        			xmlContents += skillCountCellInfo.replaceAll("_COL_INDEX_", "" + (unitSkills.get(skill) - i));
        			i ++;
        		}
        		xmlContents += rowFooter;
        	}
        	xmlContents += worksheetFooter;
    		return xmlContents;
    	}
    	
		//creates the Skills Library
    	public String createSkillLibrary()
    	{
    		String xmlContents = "";
        	xmlContents += worksheetHeader.replaceAll("_WORKSHEET_NAME_", "Skills Library").replaceAll("_ROW_", "" + (problemList.size() + 1)).replaceAll("_COL_",	"" + (skillList.size() + 1)); //adds in the worksheet's header

        	int i = 1;
        	for(String skill : skillList)
        	{
        		xmlContents += columnInfo.replaceAll("_INDEX_", "" + i).replaceAll("_HIDDEN_", "" + 0).replaceAll("_WIDTH_", "" + horizTextWidth * skill.length());
        		i ++;
        	}
        	
        	xmlContents += rowHeader; //sets a new row
        	ArrayList<String> tempSkillList = new ArrayList<String>(); //for removing stuff
        	for(String skill : skillList)
        	{
        		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", skill) + regCellFooter; //adds a new column for each skill
        		tempSkillList.add(skill);
        	}
        	xmlContents += rowFooter; //closes this row

        	//loops through while there are either still problems corresponding to a list or problems unlisted
        	for(int problemIndex = 0; tempSkillList.size() > 0; problemIndex ++) //loops through while there are still skills with more problems
        	{
        		xmlContents += rowHeader; //sets up a new row
        		for(String skillName : skillList) //loops through each skill in the skillList (needs to be each so the columns don't get messed up)
    	    	{
    	    		if(skillToProblemMap.get(skillName).size() <= problemIndex) //removes skill from list if no more problems
    	    			tempSkillList.remove(skillName);
    	    		String problemName = ""; //cell value should be empty if there's no more problems
    	    		if(tempSkillList.contains(skillName)) //else sets to the next problem in the skill's arrayList
    	    			problemName = skillToProblemMap.get(skillName).get(problemIndex).getName();
    	    		xmlContents += regCellHeader + stringData.replaceAll("_DATA_", problemName) + regCellFooter; //puts in cell's value 
    	    	}
        		xmlContents += rowFooter; //closes the row
        	}
        	xmlContents += worksheetFooter;
    		return xmlContents;
    	}
    }
}
