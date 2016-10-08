/*
 * Created on Jan 25, 2006
 *
 */
package edu.cmu.pact.curriculumeditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.cmu.pact.Utilities.trace;

public class CurriculumUnit {

    private ArrayList sectionList = new ArrayList();

    private String title;

    private CurriculumModel model;

    /**
     * CurricumUnits should be constructed using the 
     * CurriculumModel.createUnit () factory method.
     * 
     * @param title
     */
    CurriculumUnit(String title, CurriculumModel model) {
        this.title = title;
        this.model = model;
    }

    public void writeCurriculumFiles(File testFolder) throws IOException {
        
        File unitFile = new File(model.getUnitFolder(), title + ".unit");
        trace.out("name = " + unitFile);
        unitFile.createNewFile();
        
        for (Iterator sections = sectionList.iterator(); sections.hasNext();) {
            CurriculumSection section = (CurriculumSection) sections.next();
            section.writeCurriculumFiles (testFolder);
        }

        
    }

    public CurriculumSection createSection(String sectionTitle) {
        CurriculumSection section = new CurriculumSection (sectionTitle, model);
        sectionList.add(section);
        return section;
    }

}
