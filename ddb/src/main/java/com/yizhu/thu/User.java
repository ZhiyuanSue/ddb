package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;


public class User extends Table{
	static String table_name = "USER";
	static String user_table = "user ("
			+"id INT AUTO_INCREMENT PRIMARY KEY,"	//字符
			+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"	//
			+"uid VARCHAR(255) UNIQUE NOT NULL,"	//
			+"name VARCHAR(255) NOT NULL,"
			+"gender ENUM('male', 'female', 'Other', 'Unknown') DEFAULT 'Unknown',"
			+"email VARCHAR(255) UNIQUE NOT NULL,"
			+"phone VARCHAR(20) NOT NULL,"
			+"dept VARCHAR(255) NOT NULL,"
			+"grade VARCHAR(10) NOT NULL,"
			+"language VARCHAR(50) NOT NULL,"
			+"region VARCHAR(255) NOT NULL,"
			+"role VARCHAR(50) NOT NULL,"
			+"preferTags TEXT,"
			+"obtainedCredits DECIMAL(10, 2) DEFAULT 0.0"
			+");";
	public void init(){
		connect(1);
		// try to create the table
		insert_new_table(conn_user_1);
		close(1);
	}
	public void insert_new_table(Connection conn){
		if(!table_exist(conn,table_name)){
			System.out.printf("db don't have table: %s, create it\n",table_name);
			try{
				String sql="CREATE TABLE "+user_table;
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			}catch (SQLException e){
				e.printStackTrace();
			}

			System.out.println("insert table :"+table_name);
		}
		else{
			System.out.printf("db have user table\n");
		}
	}
}