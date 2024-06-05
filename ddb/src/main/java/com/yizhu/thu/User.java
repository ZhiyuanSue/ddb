package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;


public class User extends Table{
	static String table_name = "user_table";
	static String table_sql = "user_table ("
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
	static String insert_sql = "INSERT INTO user_table ("
			+"id, "
			+"timestamp, "
			+"uid, "
			+"name, "
			+"gender, "
			+"email, "
			+"phone, "
			+"dept, "
			+"grade, "
			+"language, "
			+"region, "
			+"role, "
			+"preferTags, "
			+"obtainedCredits"
			+") " 
			+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";  
	
	public void init(){
		connect(2);
		connect(3);
		// try to create the table
		insert_new_table(conn_user_2,table_name,table_sql);
		insert_new_table(conn_user_3,table_name,table_sql);
		close(2);
		close(3);
	}
	
	public void bulk(String dat_file_path){

	}
	public void insert(
		Connection conn,
		Timestamp timestamp,
		String uid,
		String name,
		String gender,
		String email,
		String phone,
		String dept,
		String grade,
		String language,
		String region,
		String role,
		String preferTags,
		int obtainedCredits
	){
		try (PreparedStatement pstmt = conn.prepareStatement(insert_sql)) {   
			pstmt.setTimestamp(2, timestamp);  
			pstmt.setString(3, uid);  
			pstmt.setString(4, name);  
			pstmt.setString(5, gender);  
			pstmt.setString(6, email);  
			pstmt.setString(7, phone);  
			pstmt.setString(8, dept);  
			pstmt.setString(9, grade);  
			pstmt.setString(10, language);  
			pstmt.setString(11, region);  
			pstmt.setString(12, role);  
			pstmt.setString(13, preferTags);  
			pstmt.setInt(14, obtainedCredits);  
		
			// 执行插入操作  
			int rowsAffected = pstmt.executeUpdate();  
			if (rowsAffected > 0) {  
				System.out.println("Data inserted successfully!");  
			}  
		
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}
}