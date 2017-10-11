package com.tonyliu.controller;

import com.tonyliu.Configuration.computeKTparamsAll;
import com.tonyliu.entity.Course;
import com.tonyliu.entity.StudentProbability;
import com.tonyliu.entity.Xblock;
import com.tonyliu.service.CreateTxtService;
import com.tonyliu.service.XblockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tao on 8/3/17.
 */

@RestController
public class TesterController {

    @Autowired
    private XblockService xblockService;

    @Autowired
    private CreateTxtService txtService;


    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    private Integer count = 0;

    @RequestMapping(value = "/createFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentProbability createFile(@RequestParam(value="student_id") String studentId, @RequestParam(value = "skillname") String skillname, @RequestParam(value = "question_id") String questionId, @RequestParam(value = "correctness") String correctness) {
        System.out.println("I am getting skillname from edx: " + skillname);
        System.out.println("I am getting student_id from edx: " + studentId);
        System.out.println("I am getting question_id from edx: " + questionId);
        System.out.println("I am getting correctness from edx: " + correctness);
        computeKTparamsAll m = new computeKTparamsAll();
        double probability = 0;
        synchronized (this) {
            boolean success = txtService.createFile("StudentData/" + studentId + "_" + skillname + ".txt");
            // success return true means just create a new file, return false means the file already created.
            if(success) {
                // insert the first line and then, append the content
                FileWriter fw = null;
                try {
                    fw = new FileWriter("StudentData/" + studentId + "_" + skillname + ".txt", true);
                    String title = "num lesson student skill cell right eol\r\n";
                    fw.write(title);

                    String content = "1 " + questionId + " " + studentId + " " + skillname + " cell " + correctness + " eol\r\n";
                    fw.write(content);
                    fw.close();

                    //probability = m.computelzerot("StudentData/" + studentId + "_" + skillname + ".txt");
                    System.out.println("The probability is:" + probability);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // which means we found the txt file, then we append content only

                FileWriter fw = null;
                try {
                    fw = new FileWriter("StudentData/" + studentId + "_" + skillname + ".txt", true);
                    String content = "1 " + questionId + " " + studentId + " " + skillname + " cell " + correctness + " eol\r\n";
                    fw.write(content);
                    fw.close();

                    //probability = m.computelzerot("StudentData/" + studentId + "_" + skillname + ".txt");
                    System.out.println("The probability is:" + probability);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        StudentProbability sp = new StudentProbability();
        sp.setStudentId(studentId);
        sp.setProblemName(questionId);
        sp.setProbability(probability + "");
        sp.setSkillname(skillname);
        return sp;
    }



    @RequestMapping(value = "/saveProbability", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentProbability saveProbability(@RequestParam(value="student_id") String studentId, @RequestParam(value = "skillname") String skillname, @RequestParam(value = "correctness") String correctness, @RequestParam(value = "question_id") String questionId) {
        System.out.println("I am getting skillname from edx: " + skillname);
        System.out.println("I am getting student_id from edx: " + studentId);
        System.out.println("I am getting correctness from edx: " + correctness);
        count++;
        computeKTparamsAll m = new computeKTparamsAll();
        double probability = 0;
        synchronized (this) {
            boolean success = txtService.createFile("StudentData/" + studentId + "_" + skillname + ".txt");
            // success return true means just create a new file, return false means the file already created.
            if(success) {
                // insert the first line and then, append the content
                FileWriter fw = null;
                try {
                    fw = new FileWriter("StudentData/" + studentId + "_" + skillname + ".txt", true);
                    String title = "num\tlesson\tstudent\tskill\tcell\tright\teol\r\n";
                    fw.write(title);

                    String content = 1 + "   Z1." + questionId + "   student" + studentId + "   " + skillname + "    cell   " + correctness + "   eol\r\n";
                    fw.write(content);
                    fw.close();

                    probability = m.computelzerot("StudentData/" + studentId + "_" + skillname + ".txt");
                    System.out.println("The probability is:" + probability);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // which means we found the txt file, then we append content only

                FileWriter fw = null;
                try {
                    fw = new FileWriter("StudentData/" + studentId + "_" + skillname + ".txt", true);
                    String content = 1 + "   Z1." + questionId + "   student" + studentId + "   " + skillname + "   cell   " + correctness + "   eol\r\n";
                    fw.write(content);
                    fw.close();

                    probability = m.computelzerot("StudentData/" + studentId + "_" + skillname + ".txt");
                    System.out.println("The probability is:" + probability);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        // save the student data in database.
        xblockService.saveStudentData(studentId, skillname, correctness);

        return null;
    }


    @RequestMapping(value = "/callandsaveBKT", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentProbability callandsaveBKT(@RequestParam(value="student_id") String studentId, @RequestParam(value = "skillname") String skillname, @RequestParam(value = "correctness") String correctness, @RequestParam(value = "question_id") String questionId, @RequestParam(value = "course") String course) {
        Double currentL = xblockService.saveStudentProbability(studentId, skillname, correctness, questionId, course);
        StudentProbability sp = new StudentProbability();
        sp.setStudentId(studentId);
        sp.setSkillname(skillname);
        sp.setCorrectness(correctness);
        sp.setProbability(currentL + "");
        sp.setProblemName(questionId);
        return sp;
    }

}
