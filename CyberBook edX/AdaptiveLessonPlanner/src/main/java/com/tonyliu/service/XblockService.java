package com.tonyliu.service;

import com.jcraft.jsch.Session;
import com.tonyliu.Configuration.MysqlSSHConnector;
import com.tonyliu.Configuration.SimpleBKT;
import com.tonyliu.entity.ConnectionAndSession;
import com.tonyliu.entity.StudentProbability;
import com.tonyliu.entity.Xblock;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.lang.model.util.SimpleElementVisitor6;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by tao on 8/3/17.
 */

@Service
public class XblockService {

    private ConnectionAndSession cs;
    private Connection conn = null;
    private Session session = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    // This method help us to save student data from the table: edxapp_csmh.temporary_probability
    public List<StudentProbability> saveStudentData(String studentId, String skillname, String correctness) {

        ArrayList<StudentProbability> lists = new ArrayList<>();

        try {
            cs = MysqlSSHConnector.getConnection();
            conn = cs.getConn();
            session = cs.getSession();

            String sql = "Insert into edxapp_csmh.temporary_probability(student_id, skillname, correctness, timestamp) values(?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, skillname);
            ps.setString(3, correctness);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            ps.setString(4, timeStamp);
            ps.executeUpdate();



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



    // This method help us to save student data from the table: edxapp_csmh.temporary_probability
    public Double saveStudentProbability(String studentId, String skillname, String correctness, String questionId) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // get opportunity_counts from DB then add 1 to it:
        try {
            cs = MysqlSSHConnector.getConnection();
            conn = cs.getConn();
            session = cs.getSession();

            // how to make sure that ALP only accept the first attempt
            String sql0 = "select count(*) from edxapp_csmh.temporary_probability where problem_name = ? and student_id = ?";
            ps = conn.prepareStatement(sql0);
            ps.setString(1, questionId);
            ps.setString(2, studentId);
            rs = ps.executeQuery();
            while(rs.next()) {
                Integer rowCount = rs.getInt(1);
                System.out.println("rowCount: " + rowCount);
                if (rowCount != 0) {
                    return null;
                }
            }

            String sql = "select * from edxapp_csmh.temporary_probability where student_id = ? and skillname = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, skillname);
            rs = ps.executeQuery();

            int count = 0;
            double prevL = 0;
            while(rs.next()) {
                count = rs.getInt("opportunity_counts");
                prevL = rs.getDouble("probability");
            }

            double L = 0;
            double S = 0;
            double T = 0;
            double G = 0;
            // which means this is the first time that the student answer the question
            if(count == 0) {
                String bkt_sql = "select * from edxapp_csmh.init_parameters_bkt where skillname = ?";
                ps = conn.prepareStatement(bkt_sql);
                ps.setString(1, skillname);
                rs = ps.executeQuery();

                while(rs.next()) {
                    L = rs.getDouble("l");
                    S = rs.getDouble("s");
                    T = rs.getDouble("t");
                    G = rs.getDouble("g");
                }


                SimpleBKT bkt = new SimpleBKT(L, G, S, T, Integer.parseInt(correctness));
                double firstL = bkt.computeL();
                // then we assume Lzero: 0.001, G: 0.111, S:0.001, T:0.409 -> got these assumed initial data from Neal's test.py file
                count ++;
                String sql1 = "insert into edxapp_csmh.temporary_probability(student_id, skillname, correctness, timestamp, probability, opportunity_counts, problem_name) values(?,?,?,?,?,?,?) ";
                ps = conn.prepareStatement(sql1);
                ps.setString(1, studentId);
                ps.setString(2, skillname);
                ps.setString(3, correctness);
                ps.setString(4, timeStamp);
                ps.setString(5, firstL + "");
                ps.setString(6, count + "");
                ps.setString(7, questionId);
                ps.executeUpdate();

                return firstL;
            } else {
                // we found the previous student data, so we will update the P(L) value and the opportunity_counts after that
                SimpleBKT bkt = new SimpleBKT(prevL, 0.111, 0.001, 0.409, Integer.parseInt(correctness));
                double currentL = bkt.computeL();

                count++;
                String sql1 = "insert into edxapp_csmh.temporary_probability(student_id, skillname, correctness, timestamp, probability, opportunity_counts, problem_name) values(?,?,?,?,?,?,?) ";
                ps = conn.prepareStatement(sql1);
                ps.setString(1, studentId);
                ps.setString(2, skillname);
                ps.setString(3, correctness);
                ps.setString(4, timeStamp);
                ps.setString(5, currentL + "");
                ps.setString(6, count + "");
                ps.setString(7, questionId);
                ps.executeUpdate();
                return currentL;
            }




        }catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);

        } finally {
            try {
                MysqlSSHConnector.closeConnection(conn, ps);
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MysqlSSHConnector.closeSession(session);
        }

        return null;
    }

}
