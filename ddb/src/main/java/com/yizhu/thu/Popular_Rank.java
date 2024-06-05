package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Popular_Rank extends Table {
	static String table_name = "popular_rank";
	static String table_sql = "popular_rank (  "
    	+"id INT AUTO_INCREMENT PRIMARY KEY,  "
    	+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  "
    	+"temporalGranularity VARCHAR(50) NOT NULL,  "
    	+"articleAidList TEXT NOT NULL  "
		+");";
	static String sql = "INSERT INTO popular_rank ("
		+"id, "
		+"timestamp, "
		+"temporalGranularity, "
		+"articleAidList) "
		+"VALUES (?, ?, ?, ?)";  

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
		String temporalGranularity,
		String articleAidList
	){
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
		
			pstmt.setTimestamp(2, timestamp);  
			pstmt.setString(3, temporalGranularity);  
			pstmt.setString(4, articleAidList);  
		
			int rowsAffected = pstmt.executeUpdate();  
			if (rowsAffected > 0) {  
				System.out.println("Data inserted successfully!");  
			}  
		
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}
}