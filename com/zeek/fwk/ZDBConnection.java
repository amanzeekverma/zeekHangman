package com.zeek.fwk;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ZDBConnection {

	//private static Properties properties = ZConfig.getProperties();
	private java.sql.Connection con;
	private static ZDBConnection zcon = null;
	
	private ZDBConnection(){	
			String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
			String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
			String username = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
			String password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

			String url = String.format("jdbc:mysql://%s:%s/java", host, port);
			 try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
			con = DriverManager.getConnection(url, username, password);
			 }catch (Exception e){
				 e.printStackTrace();
				System.out.println("Error : "+e.getMessage()); 
			 }
	}
	
	public static ZDBConnection getInstance() {
			zcon = new ZDBConnection();
			return zcon;
	}
	
	public ResultSet executeQuery(String query) throws SQLException{
		ResultSet rs = null;
		Statement s = con.createStatement();
		rs = s.executeQuery(query);
		return rs;
	}
	
	public int executeUpdate(String query) throws SQLException{
		Statement s = con.createStatement();
		return s.executeUpdate(query);
	}
	
	public void close() {
		try {
			con.close();
		}catch(Exception e){
		 e.printStackTrace();	
		}
	}
	
}
