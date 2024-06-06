package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;

public class Table {
    static String user = "root";
    static String pwd = "root";

    static String driver = "com.mysql.cj.jdbc.Driver";

    static String url1="jdbc:mysql://hadoop1:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    static String url2="jdbc:mysql://hadoop2:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    static String url3="jdbc:mysql://hadoop3:3306/ddb?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    Connection conn_user_1 = null;
    Connection conn_user_2 = null;
    Connection conn_user_3 = null;
    static String table_name = "";
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
    public boolean table_exist(Connection conn,String table_name) {
        try{
            ResultSet res = conn.getMetaData().getTables(null,null,table_name,null);
            if(res.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void del_table(Connection conn,String table_name){
        try{
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE " + table_name;
            stmt.executeUpdate(sql);
            System.out.println("del table :"+table_name);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
	public void insert_new_table(Connection conn,String table_name,String table_sql){
		if(!table_exist(conn,table_name)){
			System.out.printf("db don't have table: %s, create it\n",table_name);
		}
		else{
			System.out.printf("db have %s table, delete it\n",table_name);
			try{
				String sql="DROP TABLE "+table_name;
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			}catch (SQLException e){
				e.printStackTrace();
			}
		}
		try{
			String sql="CREATE TABLE "+table_sql;
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}catch (SQLException e){
			e.printStackTrace();
		}

		System.out.println("insert table :"+table_name);
	}
}