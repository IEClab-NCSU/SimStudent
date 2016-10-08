/*
 * Created on Jan 25, 2006
 *
 */
package edu.cmu.pact.curriculumeditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CurriculumModel {
    
    ArrayList unitList = new ArrayList();
    private String title;
    private File problemDataFolder;
    private File initialStateFolder;
    private File unitFolder;
	private File problemFolder;
    public CurriculumModel(String title) {
        this.title = title;
    }

    public CurriculumUnit createUnit(String unitTitle) {
        CurriculumUnit unit = new CurriculumUnit(unitTitle, this);
        unitList.add(unit);
        return unit;
    }

    public void writeCurriculumFiles(File testFolder) throws IOException {
        
        makeFolders(testFolder);
        
        writeXCurFile();
        
        writeUnitFiles(testFolder);
    }

    /**
     * @throws IOException
     */
    private void writeXCurFile() throws IOException {
    	File f = new File (title + ".xcur");
        f.createNewFile();
        
        File g = new File (title + ".interfaces");
        g.createNewFile();
        
        
    }

    /**
     * @param testFolder
     */
    private void makeFolders(File testFolder) {
        problemDataFolder = new File (testFolder, CurriculumConstants.PROBLEM_DATA_FOLDER_NAME);
        problemDataFolder.mkdirs();
        
        initialStateFolder = new File (testFolder, CurriculumConstants.INITIAL_STATE_FOLDER_NAME);
        initialStateFolder.mkdirs();

        unitFolder = new File (testFolder, CurriculumConstants.UNIT_DATA_FOLDER_NAME);
        unitFolder.mkdirs();
        
        problemFolder = new File (testFolder, CurriculumConstants.PROBLEM_DATA_FOLDER_NAME);
        problemFolder.mkdirs();
    }

    /**
     * @param testFolder
     * @throws IOException
     */
    private void writeUnitFiles(File testFolder) throws IOException {
        for (Iterator units = unitList.iterator(); units.hasNext();) {
            CurriculumUnit unit = (CurriculumUnit) units.next();
            unit.writeCurriculumFiles (testFolder);
        }
    }

    public File getUnitFolder() {
        return unitFolder;
    }

	public File getProblemsFolder() {
		return problemFolder;
	}

}
