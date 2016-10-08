/*
 * Created on Jan 25, 2006
 *
 */
package edu.cmu.pact.curriculumeditor;

import java.io.File;
import java.io.IOException;

import edu.cmu.pact.Utilities.trace;

public class CurriculumProblem {

    private String title;
    private CurriculumModel model;

    public CurriculumProblem(String title, CurriculumModel model) {
        this.title = title;
        this.model = model;
    }

    public void writeCurriculumFiles(File curriculumFolder) throws IOException {
        File problemFile = new File(model.getProblemsFolder(), title + ".bas");
        trace.out("problem file = " + problemFile);
        problemFile.createNewFile();
    }

}
