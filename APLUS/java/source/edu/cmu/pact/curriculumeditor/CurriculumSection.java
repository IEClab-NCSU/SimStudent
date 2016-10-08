/*
 * Created on Jan 25, 2006
 *
 */
package edu.cmu.pact.curriculumeditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CurriculumSection {

    private String title;
    private ArrayList problemList = new ArrayList();
    private CurriculumModel model; 

    public CurriculumSection(String title, CurriculumModel model) {
        this.title = title;
        this.model = model;
    }

    public void writeCurriculumFiles(File curriculumFolder) throws IOException {
        for (Iterator problems = problemList.iterator(); problems.hasNext();) {
            CurriculumProblem problem = (CurriculumProblem) problems.next();
            problem.writeCurriculumFiles (curriculumFolder);
        }
    }

    public CurriculumProblem createProblem(String problemName) {
        CurriculumProblem problem = new CurriculumProblem (problemName, model);
        problemList.add(problem);
        return problem;
    }

}
