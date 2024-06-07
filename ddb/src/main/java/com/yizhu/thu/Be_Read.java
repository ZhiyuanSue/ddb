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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Be_Read extends Table{
	static String table_name = "be_read";
	static String table_sql = "be_read (  "
    	+"id CHAR(7) DEFAULT NULL,  "
    	+"timestamp CHAR(14) DEFAULT NULL,  "
    	+"aid CHAR(7) DEFAULT NULL,  "
    	+"readNum INT NOT NULL DEFAULT 0,  "
    	+"readUidList TEXT,  "
    	+"commentNum INT NOT NULL DEFAULT 0,  "
    	+"commentUidList TEXT,  "
    	+"agreeNum INT NOT NULL DEFAULT 0,  "
    	+"agreeUidList TEXT,  "
    	+"shareNum INT NOT NULL DEFAULT 0,  "
    	+"shareUidList TEXT  "
		+") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
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
		bulk(2);
		insert_new_table(conn_user_3,table_name,table_sql);
		bulk(3);
		close(2);
		close(3);
	}
	public void bulk(int dbms_num){
		Map<String, String> aid_categories = new HashMap<>();  
		Map<String, String> aid_timestamp = new HashMap<>();

		Map<String, Integer>	aid_readnum = new HashMap<>();
		Map<String, List<String>> aid_uidlist = new HashMap<>();  

		Map<String, Integer>	aid_commentnum = new HashMap<>();
		Map<String, List<String>> aid_commentlist = new HashMap<>(); 

		Map<String, Integer>	aid_agreenum = new HashMap<>();
		Map<String, List<String>> aid_agreelist = new HashMap<>(); 

		Map<String, Integer>	aid_sharenum = new HashMap<>();
		Map<String, List<String>> aid_sharelist = new HashMap<>(); 

		Connection conn = null;
		if(dbms_num==1){
			conn=conn_user_1;
		} else if(dbms_num==2){
			conn=conn_user_2;
		} else if(dbms_num==3){
			conn=conn_user_3;
		} else {
			System.out.println("error dbms num\n");
			return;
		}

		// query dbms2 (hadoop3) for article (no fragment)
		try (Statement stmt = conn_user_3.createStatement();  
             ResultSet rs = stmt.executeQuery("SELECT aid, category, timestamp FROM article")) {  
  
            while (rs.next()) {  
                String aid = rs.getString("aid");  
                String category = rs.getString("category");  
				String timestamp = rs.getString("timestamp");
                aid_categories.put(aid, category);  
				aid_timestamp.put(aid, timestamp);
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  

		String sql = "SELECT "
			 	+"uid, "
				+"aid, "
				+"agreeOrNot, "
				+"commentOrNot,"
				+"shareOrNot "
				+"FROM user_read";
		// should query dbms1(hadoop2) and dbms2(hadoop3) for user_read
		// dbms1
		try (Statement stmt = conn_user_2.createStatement();  
            ResultSet rs = stmt.executeQuery(sql)) {  
  
            while (rs.next()) {  
                String aid = rs.getString("aid");  
                String uid = rs.getString("uid");  
				String agreeOrNot = rs.getString("agreeOrNot");  
				String commentOrNot = rs.getString("commentOrNot");
				String shareOrNot = rs.getString("shareOrNot");
				if(!aid_categories.containsKey(aid)){
					// System.out.println("error aid "+aid+" in user_read table which is not find in article table");
				}
				int readnum = 0;
				if(aid_readnum.containsKey(aid)){
					readnum = aid_readnum.get(aid);
				}
				if(!aid_uidlist.containsKey(aid)){
					List<String> uidlist=new ArrayList<>();
					aid_uidlist.put(aid,uidlist);
				}
				aid_readnum.put(aid,readnum+1);
				aid_uidlist.get(aid).add(uid);

				int commentnum = 0;
				if(aid_commentnum.containsKey(aid))
					commentnum = aid_commentnum.get(aid);
				if(!aid_commentlist.containsKey(aid)){
					List<String> commentlist=new ArrayList<>();
					aid_commentlist.put(aid,commentlist);
				}
				if (commentOrNot.equals("1")){
					commentnum++;
					aid_commentlist.get(aid).add(uid);
				}
				aid_commentnum.put(aid,commentnum);
					

				int agreenum = 0;
				if(aid_agreenum.containsKey(aid))
					agreenum = aid_agreenum.get(aid);
				if(!aid_agreelist.containsKey(aid)){
					List<String> agreelist=new ArrayList<>();
					aid_agreelist.put(aid,agreelist);
				}
				if (agreeOrNot.equals("1")){
					agreenum++;
					aid_agreelist.get(aid).add(uid);
				}
				aid_agreenum.put(aid,agreenum);
				

				int sharenum = 0;
				if(aid_sharenum.containsKey(aid))
					sharenum = aid_sharenum.get(aid);
				if(!aid_sharelist.containsKey(aid)){
					List<String> sharelist=new ArrayList<>();
					aid_sharelist.put(aid,sharelist);
				}
				if (shareOrNot.equals("1")){
					sharenum++;
					aid_sharelist.get(aid).add(uid);
				}
				aid_sharenum.put(aid,sharenum+1);
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  

		// dbms2
		try (Statement stmt = conn_user_3.createStatement();  
             ResultSet rs = stmt.executeQuery(sql)) {  
  
            while (rs.next()) {  
                String aid = rs.getString("aid");  
                String uid = rs.getString("uid");  
				String agreeOrNot = rs.getString("agreeOrNot");  
				String commentOrNot = rs.getString("commentOrNot");
				String shareOrNot = rs.getString("shareOrNot");
				if(!aid_categories.containsKey(aid)){
					// System.out.println("error aid "+aid+" in user_read table which is not find in article table");
				}
				int readnum = 0;
				if(aid_readnum.containsKey(aid)){
					readnum = aid_readnum.get(aid);
				}
				if(!aid_uidlist.containsKey(aid)){
					List<String> uidlist=new ArrayList<>();
					aid_uidlist.put(aid,uidlist);
				}
				aid_readnum.put(aid,readnum+1);
				aid_uidlist.get(aid).add(uid);

				int commentnum = 0;
				if(aid_commentnum.containsKey(aid))
					commentnum = aid_commentnum.get(aid);
				if(!aid_commentlist.containsKey(aid)){
					List<String> commentlist=new ArrayList<>();
					aid_commentlist.put(aid,commentlist);
				}
				if (commentOrNot.equals("1")){
					commentnum++;
					aid_commentlist.get(aid).add(uid);
				}
				aid_commentnum.put(aid,commentnum);
					

				int agreenum = 0;
				if(aid_agreenum.containsKey(aid))
					agreenum = aid_agreenum.get(aid);
				if(!aid_agreelist.containsKey(aid)){
					List<String> agreelist=new ArrayList<>();
					aid_agreelist.put(aid,agreelist);
				}
				if (agreeOrNot.equals("1")){
					agreenum++;
					aid_agreelist.get(aid).add(uid);
				}
				aid_agreenum.put(aid,agreenum);
				

				int sharenum = 0;
				if(aid_sharenum.containsKey(aid))
					sharenum = aid_sharenum.get(aid);
				if(!aid_sharelist.containsKey(aid)){
					List<String> sharelist=new ArrayList<>();
					aid_sharelist.put(aid,sharelist);
				}
				if (shareOrNot.equals("1")){
					sharenum++;
					aid_sharelist.get(aid).add(uid);
				}
				aid_sharenum.put(aid,sharenum+1);
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }
		

		Set<String> aids = aid_categories.keySet();
		int id=0;
		for(String aid:aids){
			String category	=	aid_categories.get(aid);
			String timestamp	=	aid_timestamp.get(aid);

			int readnum		=	aid_readnum.get(aid);
			int commentnum	=	aid_commentnum.get(aid);
			int agreenum	=	aid_agreenum.get(aid);
			int sharenum	=	aid_sharenum.get(aid);

			List<String> uidlist		=	aid_uidlist.get(aid);
			List<String> commentlist	=	aid_commentlist.get(aid);
			List<String> agreelist		=	aid_agreelist.get(aid);
			List<String> sharelist		=	aid_sharelist.get(aid);

			String uidlist_str	= "";
			String commentlist_str	=	"";
			String agreelist_str	=	"";
			String sharelist_str	=	"";

			for(String tmp:uidlist){
				uidlist_str+=tmp+",";
			}
			for(String tmp:commentlist){
				commentlist_str+=tmp+",";
			}
			for(String tmp:agreelist){
				agreelist_str+=tmp+",";
			}
			for(String tmp:sharelist){
				sharelist_str+=tmp+",";
			}

			// System.out.printf("| aid:%s | readnum:%d | uidlist:%s | commentnum:%d | commentlist:%s | agreenum:%d | agreelist:%s | sharenum:%d | sharelist:%s |\n",
			// 	aid,readnum,uidlist_str,commentnum,commentlist_str,agreenum,agreelist_str,sharenum,sharelist_str);
			// System.out.printf("insert %s %d\n",aid,id);
			StringBuilder sb = new StringBuilder();  
			sb.append(id);  
			String id_str=sb.toString();
			if ( dbms_num==3 || (dbms_num==2 && category.equals("science")) ){
				insert(conn,id_str,timestamp,aid,
					readnum,uidlist_str,
					commentnum,commentlist_str,
					agreenum,agreelist_str,
					sharenum,sharelist_str);
				id++;
			}
		}
		System.out.printf("table be_read have been inserted %d rows data to site %d\n",id,dbms_num);
	}
	public void insert(
		Connection conn,
		String id,
		String timestamp,
		String aid,
		int readNum,
		String readUidList,
		int commentNum,
		String commentUidList,
		int agreeNum,
		String agreeUidList,
		int shareNum,
		String shareUidList
	){
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
		
			pstmt.setString(1, id);
			pstmt.setString(2, timestamp);  
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
			if (rowsAffected <= 0) {  
				System.out.println("Data inserted failed!");  
			}  
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}
}