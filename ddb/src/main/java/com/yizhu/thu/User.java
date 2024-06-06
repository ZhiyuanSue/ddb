package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  

public class User extends Table{
	static String table_name = "user_table";
	static String user_file_path = "../db-generation/user.sql";
	
	public void init(){
		connect(2);
		connect(3);
		// try to create the table
		bulk(2);
		bulk(3);

		close(2);
		close(3);
	}
	
	public void bulk(int dbms_num){
		connect(dbms_num);
		Connection conn = null;
		if(dbms_num==1){
			conn=conn_user_1;
		} else if(dbms_num==2){
			conn=conn_user_2;
		} else if(dbms_num==3){
			conn=conn_user_3;
		} else {
			close(dbms_num); 
			System.out.println("error dbms num\n");
			return;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(user_file_path))) {  
            String line;  
            StringBuilder sqlScript = new StringBuilder();  
  
            // 连接到数据库  
            try (Statement stmt = conn.createStatement()) {  
  
                // 读取SQL文件并分割语句  
                while ((line = reader.readLine()) != null) {  
                    // 假设每个分号结尾的都是一个完整的SQL语句（注意：这可能不适用于所有情况）  
                    if (line.trim().endsWith(";")) {  
						boolean can_append=true;
						if(line.startsWith("  (\"")){
							String str = line.toString();
							String[] parts = str.split(",");
							if(parts[10].equals(" \"Hong Kong\"") && dbms_num!=3){
								can_append=false;
							}
							if(parts[10].equals(" \"Beijing\"") && dbms_num!=2){
								can_append=false;
							}
						}
						if (can_append){
							sqlScript.append(line).append("\n");  
						}else{
							sqlScript.append("(\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\");\n");  
						} 
                        String sql = sqlScript.toString();  
						// System.out.printf(sql);
                        if (!sql.trim().isEmpty()) {  
                            // 执行SQL语句  
                            stmt.execute(sql);  
                            // 重置StringBuilder以便读取下一个语句  
                            sqlScript.setLength(0);  
                        }  
                    } else {  
						boolean can_append=true;
						if(line.startsWith("  (\"")){
							String str = line.toString();
							String[] parts = str.split(",");
							if(parts[10].equals(" \"Hong Kong\"") && dbms_num!=3){
								can_append=false;
							}
							if(parts[10].equals(" \"Beijing\"") && dbms_num!=2){
								can_append=false;
							}
						}
						if (can_append){
							sqlScript.append(line).append("\n");  
						}
                    }  
                }  
                // 如果SQL文件最后一个语句没有分号，也需要执行  
                if (sqlScript.toString().trim().length() > 0) {  
                    stmt.execute(sqlScript.toString());  
                }  
  
                System.out.printf("SQL file %s imported to site %d successfully!\n",user_file_path,dbms_num);  
  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		close(dbms_num); 
	}
}