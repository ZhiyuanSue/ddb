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
	public void init(){
		connect(2);
		connect(3);
		// try to create the table
		insert_new_table(conn_user_2,table_name,table_sql);
		insert_new_table(conn_user_3,table_name,table_sql);
		close(2);
		close(3);
	}
}