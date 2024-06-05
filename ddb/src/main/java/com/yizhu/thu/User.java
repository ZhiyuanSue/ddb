package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;


public class User{
	static String user = "root";
	static String pwd = "root";

	static String driver = "com.mysql.cj.jdbc.Driver";

	static String url1="jdbc:mysql://hadoop1:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
	static String url2="jdbc:mysql://hadoop2:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
	static String url3="jdbc:mysql://hadoop3:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
	Connection conn_user_1 = null;
	Connection conn_user_2 = null;
	Connection conn_user_3 = null;
	public void connect(int dbms_num){
		try{
			Class.forName(driver);
			if(dbms_num==1){
				conn_user_1 = DriverManager.getConnection(url1,user,pwd);
			} else if(dbms_num==2){
				conn_user_2 = DriverManager.getConnection(url2,user,pwd);
			} else if(dbms_num==3){
				conn_user_3 = DriverManager.getConnection(url3,user,pwd);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public void init(){
		connect(1);
		// try to create the
		try{
			Statement stmt = conn_user_1.createStatement();
			ResultSet rs = stmt.executeQuery("Show Databases");
			while(rs.next()){
				System.out.print(rs.getString(1));
				System.out.println();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		close(1);
	}
	public void close(int dbms_num){
		try{
			if(dbms_num==1){
				conn_user_1.close();
			}else if(dbms_num==2){
				conn_user_2.close();
			}else if(dbms_num==3){
				conn_user_3.close();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}


}