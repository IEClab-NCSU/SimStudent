package entity;

import java.sql.Connection;

import com.jcraft.jsch.Session;

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
