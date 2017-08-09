package com.tonyliu.service;

import com.jcraft.jsch.Session;
import com.opencsv.CSVWriter;
import com.tonyliu.Configuration.MysqlSSHConnector;
import com.tonyliu.entity.ConnectionAndSession;
import com.tonyliu.entity.Course;
import com.tonyliu.entity.Xblock;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tao on 8/3/17.
 */

@Service
public class XblockService {


    public List<Xblock> getXblocks() {
        ConnectionAndSession cs;
        Connection conn = null;
        Session session = null;
        PreparedStatement ps = null;
        ArrayList<Xblock> xblocks = new ArrayList<Xblock>();

        try {
            cs = MysqlSSHConnector.getConnection();
            conn = cs.getConn();
            session = cs.getSession();

            String sql = "SELECT * FROM edxapp_csmh.export_course_content;";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Xblock xblock = new Xblock();
                xblock.setId(rs.getInt("id"));
                xblock.setXblockId(rs.getString("xblock_id"));
                xblock.setTypeOfXblock(rs.getString("type_of_xblock"));
                xblock.setTitle(rs.getString("title"));
                xblock.setSubTitle(rs.getString("sub_title"));
                xblock.setText(rs.getString("text"));
                xblock.setQuestion(rs.getString("question"));
                xblock.setChoices(rs.getString("choices"));
                xblock.setImageUrl(rs.getString("image_url"));
                xblock.setCorrectAnswer(rs.getString("correct_answer"));
                xblock.setHint(rs.getString("hint"));
                xblock.setProblemName(rs.getString("problem_name"));
                xblock.setSkillName(rs.getString("skillname"));
                xblock.setHtmlUrl(rs.getString("html_url"));
                xblock.setBrdUrl(rs.getString("brd_url"));

                xblocks.add(xblock);
            }

            return xblocks;

        }catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);

        } finally {
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);
        }

        return null;
    }


    public List<Course> getCourses() {
        ConnectionAndSession cs;
        Connection conn = null;
        Session session = null;
        PreparedStatement ps = null;
        ArrayList<Course> courses = new ArrayList<Course>();

        try {
            cs = MysqlSSHConnector.getConnection();
            conn = cs.getConn();
            session = cs.getSession();

            String sql = "SELECT distinct(course_id),module_type FROM edxapp.courseware_studentmodule where module_type='course';";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Course course = new Course();
                course.setCourse_id(rs.getString("course_id"));
                course.setModule_type(rs.getString("module_type"));
                courses.add(course);
            }

            return courses;

        }catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);

        } finally {
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);
        }

        return null;

    }

    public List<Xblock> getXblocksByCourseId(String courseId) {
        ConnectionAndSession cs;
        Connection conn = null;
        Session session = null;
        PreparedStatement ps = null;
        ArrayList<Xblock> xblocks = new ArrayList<Xblock>();
        CSVWriter writer = null;
        try {
            cs = MysqlSSHConnector.getConnection();
            conn = cs.getConn();
            session = cs.getSession();

            String sql = "SELECT * FROM edxapp_csmh.export_course_content where xblock_id like '%" + courseId + "%';";

            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();


            String newCsv = "src/main/webapp/CSVFiles/" + courseId + ".csv";


            writer = new CSVWriter(new FileWriter(newCsv));
            String[] firstLine = new String[] {"xblock_id", "type_of_xblock", "title", "sub_title",
                    "text", "question", "choices",
                    "image_url", "correct_answer", "hint", "problem_name", "skillname",
                    "html_url", "brd_url"};
            writer.writeNext(firstLine);

            while(rs.next()) {
                Xblock xblock = new Xblock();
                xblock.setId(rs.getInt("id"));
                xblock.setXblockId(rs.getString("xblock_id"));
                xblock.setTypeOfXblock(rs.getString("type_of_xblock"));
                xblock.setTitle(rs.getString("title"));
                xblock.setSubTitle(rs.getString("sub_title"));
                xblock.setText(rs.getString("text"));
                xblock.setQuestion(rs.getString("question"));
                xblock.setChoices(rs.getString("choices"));
                xblock.setImageUrl(rs.getString("image_url"));
                xblock.setCorrectAnswer(rs.getString("correct_answer"));
                xblock.setHint(rs.getString("hint"));
                xblock.setProblemName(rs.getString("problem_name"));
                xblock.setSkillName(rs.getString("skillname"));
                xblock.setHtmlUrl(rs.getString("html_url"));
                xblock.setBrdUrl(rs.getString("brd_url"));

                xblocks.add(xblock);

                writer.writeNext(new String[] {xblock.getXblockId(), xblock.getTypeOfXblock(), xblock.getTitle(), xblock.getSubTitle(), xblock.getText(), xblock.getQuestion(),
                xblock.getChoices(), xblock.getImageUrl(), xblock.getCorrectAnswer(), xblock.getHint(), xblock.getProblemName(), xblock.getSkillName(), xblock.getHtmlUrl(), xblock.getBrdUrl()});
            }


            return xblocks;

        }catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
                writer.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            MysqlSSHConnector.closeSession(session);

        } finally {
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);
        }

        return null;
    }



}
