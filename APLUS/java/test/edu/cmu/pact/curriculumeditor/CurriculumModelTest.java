package edu.cmu.pact.curriculumeditor;

/*
 * Created on May 30, 2005
 *
 */

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class CurriculumModelTest extends TestCase {

    final String CURRICULUM_NAME = "TestCurriculum";

    public void testCurriculumModel() throws IOException {
        CurriculumModel model = new CurriculumModel(CURRICULUM_NAME);
        CurriculumUnit unit = model.createUnit("Unit 1");
        
        CurriculumSection section = unit.createSection("Section 1");
        
        CurriculumProblem problem1 = section.createProblem("Problem 1");
        CurriculumProblem problem2 = section.createProblem("Problem 2");
        
        final String testFolderName = "curriculum test folder" + File.separator;
        File testFolder = new File (testFolderName);
        deleteRecursive (testFolder);
        testFolder.mkdir();
        
        model.writeCurriculumFiles (testFolder);

        assertTrue (new File (testFolder, CurriculumConstants.INITIAL_STATE_FOLDER_NAME).isDirectory());
        assertTrue (new File (testFolder, CurriculumConstants.UNIT_DATA_FOLDER_NAME).isDirectory());
        
        assertTrue (new File (CURRICULUM_NAME + ".xcur").exists());
        assertTrue (new File (CURRICULUM_NAME + ".interfaces").exists());
        assertTrue (new File (testFolder, CurriculumConstants.UNIT_DATA_FOLDER_NAME + "/Unit 1.unit").exists());
        assertTrue (new File (testFolder, CurriculumConstants.PROBLEM_DATA_FOLDER_NAME + "/Problem 1.bas").exists());
        assertTrue (new File (testFolder, CurriculumConstants.PROBLEM_DATA_FOLDER_NAME + "/Problem 2.bas").exists());
        assertTrue (new File (testFolder, CurriculumConstants.INITIAL_STATE_FOLDER_NAME).exists());
        

    }

    public static boolean deleteRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteRecursive(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    protected void setUp() {
    }

    public static Test suite() {
        return new TestSuite(CurriculumModelTest.class);
    }

}
