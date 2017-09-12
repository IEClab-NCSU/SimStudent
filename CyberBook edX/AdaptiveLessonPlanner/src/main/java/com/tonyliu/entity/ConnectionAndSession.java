package com.tonyliu.entity;

import com.jcraft.jsch.Session;

import java.sql.Connection;

/**
 * Created by tao on 8/3/17.
 */
public class ConnectionAndSession {

    private Connection conn;
    private Session session;
    public Connection getConn() {
        return conn;
    }
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }



}
