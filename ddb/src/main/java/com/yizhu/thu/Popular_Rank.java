package com.yizhu.thu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import java.time.Instant;  
import java.time.LocalDateTime;  
import java.time.ZoneId;  
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;  
import java.util.Date;  
import java.time.DayOfWeek;  
import java.time.LocalDate;  
import java.time.temporal.TemporalAdjusters;  
import java.util.PriorityQueue;  
import java.util.Collections;
import java.util.stream.Collectors;  
import java.util.Arrays;  

public class Popular_Rank extends Table {
	static String table_name = "popular_rank";
	static String table_sql = "popular_rank (  "
    	+"id CHAR(7) DEFAULT NULL,  "
    	+"timestamp CHAR(14) NOT NULL,  "
    	+"temporalGranularity ENUM('daily', 'weekly', 'monthly') NOT NULL,  "
    	+"articleAidList TEXT NOT NULL  "
		+") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	static String sql = "INSERT INTO popular_rank ("
		+"id, "
		+"timestamp, "
		+"temporalGranularity, "
		+"articleAidList) "
		+"VALUES (?, ?, ?, ?)";  

	public void init(){
		connect(2);
		connect(3);
		// try to create the table
		// if(!table_exist(conn_user_2,table_name)){
			insert_new_table(conn_user_2,table_name,table_sql);
			bulk(2);
		// }
		// if(!table_exist(conn_user_3,table_name)){
			insert_new_table(conn_user_3,table_name,table_sql);
			bulk(3);
		// }
		
		close(2);
		close(3);
	}
	public void bulk(int dbms_num){
		String sql = "SELECT "
				+"timestamp, "
				+"aid, "
				+"readNum "
				+"FROM be_read";
		
		Map<Long,List<String>> timestamp_aid_daily=new HashMap<>();
		Map<Long,List<String>> timestamp_aid_weekly=new HashMap<>();
		Map<Long,List<String>> timestamp_aid_monthly=new HashMap<>();
		Map<String,Integer> aid_readnum=new HashMap<>();
		//read the be_read table
		try (Statement stmt = conn_user_2.createStatement();  
            ResultSet rs = stmt.executeQuery(sql)) {  
  
            while (rs.next()) {
				String aid = rs.getString("aid");
				int readNum = rs.getInt("readNum");
				String timestamp = rs.getString("timestamp");
				long timestampMillis = Long.parseLong(timestamp);  

				long timestamp_daily_start = getDayStartTimestamp(timestampMillis);
				long timestamp_weekly_start = getWeekStartTimestamp(timestampMillis);
				long timestamp_monthly_start = getMonthStartTimestamp(timestampMillis);
				// daily
				if (!timestamp_aid_daily.containsKey(timestamp_daily_start)){
					List<String> aidlist=new ArrayList<>();
					timestamp_aid_daily.put(timestamp_daily_start,aidlist);
				}
				timestamp_aid_daily.get(timestamp_daily_start).add(aid);
				// weekly
				if (!timestamp_aid_weekly.containsKey(timestamp_weekly_start)){
					List<String> aidlist=new ArrayList<>();
					timestamp_aid_weekly.put(timestamp_weekly_start,aidlist);
				}
				timestamp_aid_weekly.get(timestamp_weekly_start).add(aid);
				//monthly
				if (!timestamp_aid_monthly.containsKey(timestamp_monthly_start)){
					List<String> aidlist=new ArrayList<>();
					timestamp_aid_monthly.put(timestamp_monthly_start,aidlist);
				}
				timestamp_aid_monthly.get(timestamp_monthly_start).add(aid);

				aid_readnum.put(aid,readNum);
			}

		}catch (SQLException e) {  
            e.printStackTrace();  
        } 
		
		if(dbms_num == 2){	// dbms1, hadoop2, store the daily data
			String temporalGranularity = "daily";
			int id=0;
			for(long ts: timestamp_aid_daily.keySet()){
				List<String> aids= timestamp_aid_daily.get(ts);

				List<String> maxfive = findFiveLargest(aids,aid_readnum);

				String aidlist="";
				for (String aid:maxfive){
					aidlist+=aid+",";
				}
				StringBuilder sb = new StringBuilder();  
				sb.append(id);  
				String id_str=sb.toString();
				insert(
					conn_user_2,
					id_str,
					String.valueOf(ts),
					temporalGranularity,
					aidlist
				);
				id++;
			}
		} else if(dbms_num==3){	// dbms2, hadoop3, store the weekly and monthly data
			String temporalGranularity1 = "weekly";
			String temporalGranularity2 = "monthly";
			int id=0;
			for(long ts: timestamp_aid_weekly.keySet()){
				List<String> aids= timestamp_aid_weekly.get(ts);
				
				List<String> maxfive = findFiveLargest(aids,aid_readnum);

				String aidlist="";
				for (String aid:maxfive){
					aidlist+=aid+",";
				}
				StringBuilder sb = new StringBuilder();  
				sb.append(id);  
				String id_str=sb.toString();
				insert(
					conn_user_3,
					id_str,
					String.valueOf(ts),
					temporalGranularity1,
					aidlist
				);
				id++;
			}
			for(long ts: timestamp_aid_monthly.keySet()){
				List<String> aids= timestamp_aid_monthly.get(ts);
				
				List<String> maxfive = findFiveLargest(aids,aid_readnum);

				String aidlist="";
				for (String aid:maxfive){
					aidlist+=aid+",";
				}
				StringBuilder sb = new StringBuilder();  
				sb.append(id);  
				String id_str=sb.toString();
				insert(
					conn_user_3,
					id_str,
					String.valueOf(ts),
					temporalGranularity2,
					aidlist
				);
				id++;
			}
		}
		
	}
	public void insert(
		Connection conn,
		String id,
		String timestamp,
		String temporalGranularity,
		String articleAidList
	){
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
			pstmt.setString(1, id);
			pstmt.setString(2, timestamp);  
			pstmt.setString(3, temporalGranularity);  
			pstmt.setString(4, articleAidList);  
		
			int rowsAffected = pstmt.executeUpdate();  
			if (rowsAffected > 0) {  
				System.out.println("Data inserted successfully!");  
			}  
		
		} catch (SQLException e) {  
			e.printStackTrace();  
		}
	}

	public static boolean areTimestampsOnSameDay(long timestamp1, long timestamp2) {  
        Calendar calendar1 = Calendar.getInstance();  
        Calendar calendar2 = Calendar.getInstance();  
  
        calendar1.setTimeInMillis(timestamp1);  
        calendar2.setTimeInMillis(timestamp2);  
  
        calendar1.set(Calendar.HOUR_OF_DAY, 0);  
        calendar1.set(Calendar.MINUTE, 0);  
        calendar1.set(Calendar.SECOND, 0);  
        calendar1.set(Calendar.MILLISECOND, 0);  
  
        calendar2.set(Calendar.HOUR_OF_DAY, 0);  
        calendar2.set(Calendar.MINUTE, 0);  
        calendar2.set(Calendar.SECOND, 0);  
        calendar2.set(Calendar.MILLISECOND, 0);  
  
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)  
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)  
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);  
    }  
	public static boolean isInSameWeek(long timestamp1, long timestamp2) {  
        LocalDate date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate();  
        LocalDate date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate();  
  
        LocalDate startOfWeek1 = date1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));  
        LocalDate startOfWeek2 = date2.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));  
  
        return startOfWeek1.equals(startOfWeek2);  
    }  
	public static boolean areTimestampsInSameMonth(long timestamp1, long timestamp2) {  
        Calendar calendar1 = Calendar.getInstance();  
        Calendar calendar2 = Calendar.getInstance();  
  
        calendar1.setTimeInMillis(timestamp1);  
        calendar2.setTimeInMillis(timestamp2);  
  
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)  
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);  
    }  
	public static long getDayStartTimestamp(long timestamp) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTimeInMillis(timestamp);  
  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
  
        return calendar.getTimeInMillis();  
    } 
	public static long getWeekStartTimestamp(long timestamp) {  
        Instant instant = Instant.ofEpochMilli(timestamp);  
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();  
    } 
	public static long getMonthStartTimestamp(long timestamp) {  
		Instant instant = Instant.ofEpochMilli(timestamp);  
		LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate monthStart = localDate.withDayOfMonth(1);  
		return monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();  
	}  

	public static List<String> findFiveLargest(List<String> aidlist,Map<String,Integer> aid_readnum) {  
		String[] aidarray = new String[5]; 
		for (int i=0;i<5;i++){
			aidarray[i]="";
		}

        for (String aid : aidlist) {  
			for(int i=0;i<5;i++){
				if(aidarray[i]==""){
					aidarray[i]=aid;
					break;
				}
				else{
					int cur_num=aid_readnum.get(aid);
					int tmp_array_num=aid_readnum.get(aidarray[i]);
					if(cur_num>tmp_array_num){
						aidarray[i]=aid;
						break;
					}
				}
			}
        }  
  
        return Arrays.asList(aidarray);
    }  

	public List<String> Query_five(String timestamp,String temporalGranularity){
		connect(2);
		connect(3);
		
		String sql="";
		Connection conn=null;
		String[] aidarray = new String[5]; 
		if (temporalGranularity=="daily"){
			conn=conn_user_2;
			sql = "SELECT timestamp, articleAidList FROM popular_rank WHERE temporalGranularity = 'daily'";
		}else if (temporalGranularity=="weekly"){
			conn=conn_user_3;
			sql = "SELECT timestamp, articleAidList FROM popular_rank WHERE temporalGranularity = 'weekly'";
		}else if (temporalGranularity=="monthly"){
			conn=conn_user_3;
			sql = "SELECT timestamp, articleAidList FROM popular_rank WHERE temporalGranularity = 'monthly'";
		}else{
			System.out.printf("wrong temporalGranularity\n");
			return null;
		}

		try (Statement stmt = conn.createStatement();  
            ResultSet rs = stmt.executeQuery(sql)) {  
  
            while (rs.next()) {  
                String tmp_timestamp = rs.getString("timestamp");  
                String articleAidList = rs.getString("articleAidList");
				System.out.println(articleAidList);
  
                String[] parts = articleAidList.split(",");
				for(int i = 0;i < 5;i++){
					aidarray[i]=parts[i];
				}
            }  
  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
		
		close(2);
		close(3);

		return Arrays.asList(aidarray);
	}
}