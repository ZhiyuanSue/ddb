package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Read extends Table{
	static String table_name = "read_table";
	static String user_file_path = "../db-generation/user_read.sql";
	static String table_sql = "read_table ("
    	+"id INT AUTO_INCREMENT PRIMARY KEY,"
    	+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
    	+"uid VARCHAR(255) NOT NULL,"
    	+"aid VARCHAR(255) NOT NULL,"
    	+"readTimeLength INT,"
    	+"aggreeOrNot BOOLEAN,"
    	+"commentOrNot BOOLEAN,"
    	+"commentDetail TEXT,"
    	+"shareOrNot BOOLEAN"
		+");";
	static String sql = "INSERT INTO read_table ("
		+"id, "
		+"timestamp, "
		+"uid, "
		+"aid, "
		+"readTimeLength, "
		+"aggreeOrNot, "
		+"commentOrNot, "
		+"commentDetail, "
		+"shareOrNot) " 
		+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";  
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
		String aid, 
		Integer readTimeLength,
		Boolean aggreeOrNot,
		Boolean commentOrNot, 
		String commentDetail,
		Boolean shareOrNot
	){
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
			pstmt.setTimestamp(2, timestamp);  
			pstmt.setString(3, uid);  
			pstmt.setString(4, aid);  
			pstmt.setInt(5, readTimeLength);  
			pstmt.setBoolean(6, aggreeOrNot);  
			pstmt.setBoolean(7, commentOrNot);  
			pstmt.setString(8, commentDetail);
			pstmt.setString(8, null);
			pstmt.setBoolean(9, shareOrNot);  
 
			int rowsAffected = pstmt.executeUpdate();  
			if (rowsAffected > 0) {  
				System.out.println("Data inserted successfully!");  
			}  
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}
}