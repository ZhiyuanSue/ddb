package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DB {
    static String user = "root";
    static String pwd = "root";

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url1_mysql = "jdbc:mysql://hadoop1:3306/";
    static String url2_mysql = "jdbc:mysql://hadoop2:3306/";
    static String url3_mysql = "jdbc:mysql://hadoop3:3306/";

    static String ddb_name = "ddb";
    static String suffix = "?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    Connection conn_user_1 = null;
    Connection conn_user_2 = null;
    Connection conn_user_3 = null;
    public void init(){
        connect(1);
        connect(2);
        connect(3);
        // try to create the ddb database for two nodes, and if it exist ,just ignore it
        try{
            System.out.println("try to generate a new database at "+ url2_mysql);
            createDatabase(conn_user_2,"ddb");
            // listDatabase(2);
            System.out.println("try to generate a new database at "+ url3_mysql);
            createDatabase(conn_user_3,"ddb");
            // listDatabase(3);

        } catch (SQLException e){
            e.printStackTrace();
        }

        close(1);
        close(2);
        close(3);
    }
    public void connect(int dbms_num){
        try{
            Class.forName(driver);
            if(dbms_num==1){
                conn_user_1 = DriverManager.getConnection(url1_mysql,user,pwd);
            } else if(dbms_num==2){
                conn_user_2 = DriverManager.getConnection(url2_mysql,user,pwd);
            } else if(dbms_num==3){
                conn_user_3 = DriverManager.getConnection(url3_mysql,user,pwd);
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
    private static boolean existsDB(Connection conn,String dbname) throws SQLException {
        ResultSet res = conn.getMetaData().getCatalogs();
        while(res.next()){
            if(dbname.equals(res.getString(1))){
                return true;
            }
        }
        return false;
    }
    private static void createDatabase(Connection conn, String dbname) throws SQLException {
        if(!existsDB(conn,dbname)){
            Statement stmt = conn.createStatement();
            String sql= "CREATE DATABASE " + dbname;
            stmt.executeUpdate(sql);
            System.out.println("no database " + dbname + "" + " and we have created a new one");
            stmt.close();
        }else{
            System.out.println("database "+dbname+" has already exist,we just ignore it");
        }
    }
    private void listDatabase(int dbms_num){
        if(dbms_num!=1&&dbms_num!=2&&dbms_num!=3){
            System.out.println("wrong dbms_num");
            return;
        }
        try{
            Statement stmt=null;
            if(dbms_num==1)
                stmt = conn_user_1.createStatement();
            else if (dbms_num==2) {
                stmt = conn_user_2.createStatement();
            } else if (dbms_num==3) {
                stmt = conn_user_3.createStatement();
            }
            ResultSet rs = stmt.executeQuery("Show Databases");
            System.out.printf("show database at this mysql site\n");
            while(rs.next()){
                System.out.printf("- ");
                System.out.print(rs.getString(1));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void listDatabaseTable(int dbms_num,String ddbname){
        List<String> tablenames= new ArrayList<>();
        if(dbms_num!=1&&dbms_num!=2&&dbms_num!=3){
            System.out.println("wrong dbms_num");
            return;
        }
        try{
            Connection conn = null;
            if(dbms_num==1) {
                conn = DriverManager.getConnection(url1_mysql+ddbname+suffix,user,pwd);
            } else if (dbms_num==2) {
                conn = DriverManager.getConnection(url2_mysql+ddbname+suffix,user,pwd);
            } else if (dbms_num==3) {
                conn = DriverManager.getConnection(url3_mysql+ddbname+suffix,user,pwd);
            }
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet res =dbMetaData.getTables(null,null,"%",new String[] {"TABLE"});

            System.out.printf("show database %s\n",ddbname);
            while(res.next()) {
                String tableName = res.getString("TABLE_NAME");
                System.out.println("Table: " + tableName);
                ResultSet columns = dbMetaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.printf(" | " + columnName  );
                }
				System.out.printf(" | \n");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}