package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Read extends Table{
	static String table_name = "read_table";
	static String table_sql = "read_table ("
    	+"id INT AUTO_INCREMENT PRIMARY KEY,"
    	+"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
    	+"uid VARCHAR(255) NOT NULL,"
    	+"aid VARCHAR(255) NOT NULL,"
    	+"readTimeLength INT,"
    	+"aggreeOrNot BOOLEAN DEFAULT FALSE,"
    	+"commentOrNot BOOLEAN DEFAULT FALSE,"
    	+"commentDetail TEXT,"
    	+"shareOrNot BOOLEAN DEFAULT FALSE"
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