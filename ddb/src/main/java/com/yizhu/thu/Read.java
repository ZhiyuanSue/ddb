package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.HashSet;
import java.util.Set;

public class Read extends Table{
	static String table_name = "user_read";
	static String user_read_file_path = "../db-generation/user_read.sql";
	public void init(){
		connect(2);
		connect(3);
		// try to create the table
		// if(!table_exist(conn_user_2,table_name))
			bulk(2);
		// if(!table_exist(conn_user_3,table_name))
			bulk(3);

		close(2);
		close(3);
	}
	public void bulk(int dbms_num){

		//查询user table的uid
		Set<String> uid_set=new HashSet<>();
		ResultSet dbms_uid=null;
		if(dbms_num == 2){
			try(PreparedStatement statement = conn_user_2.prepareStatement("SELECT uid FROM user_table")){
				dbms_uid=statement.executeQuery();
				while(dbms_uid.next()){
					String user_table_uid = dbms_uid.getString("uid");
					uid_set.add(user_table_uid);
				}
			} catch (SQLException e) {  
				e.printStackTrace();  
			}  
		}else if(dbms_num == 3){
			try(PreparedStatement statement = conn_user_3.prepareStatement("SELECT uid FROM user_table")){
				dbms_uid=statement.executeQuery();
				while(dbms_uid.next()){
					String user_table_uid = dbms_uid.getString("uid");
					uid_set.add(user_table_uid);
				}
			} catch (SQLException e) {  
				e.printStackTrace();  
			}  
		}

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
		try (BufferedReader reader = new BufferedReader(new FileReader(user_read_file_path))) {  
            String line;  
            StringBuilder sqlScript = new StringBuilder();  
			
  
            // 连接到数据库  
            try (Statement stmt = conn.createStatement()) {  
  
                // 读取SQL文件并分割语句  
                while ((line = reader.readLine()) != null) {  
                    // 假设每个分号结尾的都是一个完整的SQL语句（注意：这可能不适用于所有情况）  
                    if (line.trim().endsWith(";")) {  
                        boolean can_append=false;
						if(line.startsWith("  (\"")){
							String str = line.toString();
							String[] parts = str.split(",");
							String uid=parts[2].trim().split("\"")[1];

							if(uid_set.contains(uid)){
								can_append=true;
							}
						} else{
							can_append = true;
						}
						if (can_append){
							sqlScript.append(line).append("\n");  
						} else {
							sqlScript.append("(\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\");\n");
						}  
                        String sql = sqlScript.toString();  
                        if (!sql.trim().isEmpty()) {  
                            // 执行SQL语句  
                            stmt.execute(sql);  
                            // 重置StringBuilder以便读取下一个语句  
                            sqlScript.setLength(0);  
                        }  
                    } else {  
						boolean can_append=false;
						if(line.startsWith("  (\"")){
							String str = line.toString();
							String[] parts = str.split(",");
							String uid=parts[2].trim().split("\"")[1];
							
							if(uid_set.contains(uid)){
								can_append=true;
							}
						}else{
							can_append = true;
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
  
                System.out.printf("SQL file %s imported to site %d successfully!\n",user_read_file_path,dbms_num);  
  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		close(dbms_num); 
	}
}