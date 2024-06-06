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
		insert_new_table(conn_user_2,table_name,table_sql);
		insert_new_table(conn_user_3,table_name,table_sql);
		bulk(2);
		close(2);
		close(3);
	}
	public void bulk(int dbms_num){
		String sql = "SELECT "
				+"timestamp, "
				+"aid, "
				+"readNum "
				+"FROM be_read";
		
		Map<Integer,List<String>> timestamp_aid=new HashMap<>();
		Map<String,Integer> aid_readnum=new HashMap<>();
		//read the be_read table
		try (Statement stmt = conn_user_2.createStatement();  
            ResultSet rs = stmt.executeQuery(sql)) {  
  
            while (rs.next()) {
				String aid = rs.getString("aid");
				String readNum = rs.getString("readNum");
				String timestamp = rs.getString("timestamp");
				long timestampMillis = Long.parseLong(timestamp);  
				Instant instantMillis = Instant.ofEpochMilli(timestampMillis);  
				LocalDateTime dateTimeMillis = instantMillis.atZone(ZoneId.systemDefault()).toLocalDateTime();  
				
			}
		}catch (SQLException e) {  
            e.printStackTrace();  
        } 
		
	}
	public void insert(
		Connection conn,
		Timestamp timestamp,
		String temporalGranularity,
		String articleAidList
	){
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  
		
			pstmt.setTimestamp(2, timestamp);  
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
}