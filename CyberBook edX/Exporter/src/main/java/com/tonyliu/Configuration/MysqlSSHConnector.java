package com.tonyliu.Configuration;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.PreparedStatement;
import com.tonyliu.entity.ConnectionAndSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by tao on 8/3/17.
 */
public class MysqlSSHConnector {

    public static ConnectionAndSession getConnection() throws Exception {
        Connection conn;
        conn = null;

        String strSshUser = "vagrant";                  // SSH loging username
        String strSshPassword = "vagrant";                   // SSH login password
        String strSshHost = "192.168.33.10";          // hostname or ip or SSH server
        //int nSshPort = 2222;                                    // remote SSH host port number
        String strRemoteHost = "127.0.0.1";  // hostname or ip of your database server
        int nLocalPort = 3307;                                // local port number use to bind SSH tunnel
        int nRemotePort = 3306;                               // remote port number of your database
        String strDbUser = "root";                    // database loging username
        String strDbPassword = "";                    // database login password
        Session session = null;

        try {
            //SSH loging username

            final JSch jsch = new JSch();
            session = jsch.getSession(strSshUser,  strSshHost, 22);

            session.setPassword(strSshPassword);

            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);

            session.connect();

            session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:" + nLocalPort, strDbUser, strDbPassword);
            System.out.println("Database connected correctly...");



        } catch(Exception e) {
            MysqlSSHConnector.closeConnection(conn, null);
            MysqlSSHConnector.closeSession(session);
            e.printStackTrace();

        }

        ConnectionAndSession cs = new ConnectionAndSession();
        cs.setConn(conn);
        cs.setSession(session);

        return cs;

    }

    public static void closeConnection(Connection conn, PreparedStatement ps) throws Exception {


        if(conn != null) {
            try {
                conn.close();
            } catch(SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        if(ps != null) {
            ps.close();
        }

        System.out.println("Database has been closed..");
    }

    public static void closeSession(Session session) {
        if(session != null) {
            session.disconnect();
        }
        System.out.println("Session has been closed...");
    }




}
