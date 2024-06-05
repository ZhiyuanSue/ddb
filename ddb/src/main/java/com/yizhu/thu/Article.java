package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Article extends Table {
	static String table_name = "article";
	static String table_sql = "article ( " 
    	+"id INT AUTO_INCREMENT PRIMARY KEY, " 
    	+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  "
    	+"aid VARCHAR(255) NOT NULL UNIQUE, "  
    	+"title VARCHAR(255) NOT NULL,  "
    	+"category VARCHAR(255) NOT NULL,  "
    	+"abstract TEXT,  "
    	+"articleTags TEXT, "
    	+"authors TEXT,  "
    	+"language VARCHAR(10) NOT NULL, "
    	+"text TEXT NOT NULL,  "
    	+"image VARCHAR(255),  " 
    	+"video VARCHAR(255)   "  
		+");";
	static String sql = "INSERT INTO article ("
		+"id, "
		+"timestamp, "
		+"aid, "
		+"title," 
		+"category, "
		+"abstract, "
		+"articleTags, "
		+"authors, "
		+"language, "
		+"text, "
		+"image, "
		+"video) "
		+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";  
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
		String aid,
		String title,
		String category,
		String abstractText,
		String articleTags,
		String authors,
		String language,
		String text,
		String image,
		String video
	){
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  

			pstmt.setTimestamp(2, timestamp);  
			pstmt.setString(3, aid);  
			pstmt.setString(4, title);  
			pstmt.setString(5, category);  
			pstmt.setString(6, abstractText);  
			pstmt.setString(7, articleTags);
			pstmt.setString(8, authors);  
			pstmt.setString(9, language);  
			pstmt.setString(10, text);  
			pstmt.setString(11, image);  
			pstmt.setString(12, video);  
		
			int rowsAffected = pstmt.executeUpdate();  
			if (rowsAffected > 0) {  
				System.out.println("Data inserted successfully!");  
			}  
		
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}
}