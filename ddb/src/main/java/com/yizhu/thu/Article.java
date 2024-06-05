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