package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Be_Read extends Table{
	static String table_name = "be_read";
	static String table_sql = "be_read (  "
    	+"id INT AUTO_INCREMENT PRIMARY KEY,  "
    	+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  "
    	+"aid VARCHAR(255) NOT NULL,  "
    	+"readNum INT NOT NULL DEFAULT 0,  "
    	+"readUidList TEXT,  "
    	+"commentNum INT NOT NULL DEFAULT 0,  "
    	+"commentUidList TEXT,  "
    	+"agreeNum INT NOT NULL DEFAULT 0,  "
    	+"agreeUidList TEXT,  "
    	+"shareNum INT NOT NULL DEFAULT 0,  "
    	+"shareUidList TEXT  "
		+");";
	static String sql = "INSERT INTO be_read ("
		+"id, "
		+"timestamp, "
		+"aid, "
		+"readNum, "
		+"readUidList, "
		+"commentNum, "
		+"commentUidList, "
		+"agreeNum, "
		+"agreeUidList, "
		+"shareNum, "
		+"shareUidList) "
		+"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";  
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
		Integer readNum,
		String readUidList,
		Integer commentNum,
		String commentUidList,
		Integer agreeNum,
		String agreeUidList,
		Integer shareNum,
		String shareUidList
	){
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
		
			pstmt.setTimestamp(2, timestamp);  
			pstmt.setString(3, aid);  
			pstmt.setInt(4, readNum);  
			pstmt.setString(5, readUidList);  
			pstmt.setInt(6, commentNum);  
			pstmt.setString(7, commentUidList);  
			pstmt.setInt(8, agreeNum);  
			pstmt.setString(9, agreeUidList);  
			pstmt.setInt(10, shareNum);  
			pstmt.setString(11, shareUidList);  
		
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