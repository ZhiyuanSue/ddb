package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class User{
	static String user = "root";
	static String pwd = "root";

	static String driver1 = "com.mysql.cj.jdbc.Driver";
	static String driver2 = "com.mysql.cj.jdbc.Driver";
	static String driver3 = "com.mysql.cj.jdbc.Driver";

	static String url1="jdbc:mysql://hadoop1:3306/mysql?characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
	static String url2="jdbc:mysql://hadoop2:3306/mysql?characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
	static String url3="jdbc:mysql://hadoop3:3306/mysql?characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
	Connection conn = null;
	public void connect(){
		try{
			Class.forName(driver1);
			conn = DriverManager.getConnection(url1,user,pwd);
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
}