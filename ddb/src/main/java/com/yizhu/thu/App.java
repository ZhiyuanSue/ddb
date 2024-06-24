package com.yizhu.thu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Jedis;  
import java.util.Scanner;  
/**
 */
public class App 
{
	static String local_data_dir="/home/ddb/db-generation/";
	static String articles="articles";
	
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Start test Hadoop" );
		Scanner scanner = new Scanner(System.in); 
		HDFS fs=new HDFS();
		DB db=new DB();
		// tables
		User usr					=	new User();
		Article art					=	new Article();
		Read read 					=	new Read();
		Be_Read be_read 			=	new Be_Read();
		Popular_Rank popular_rank 	= 	new Popular_Rank();
		fs.init();
		bulk_hadoop(fs);

		db.init();
		System.out.print("===> ");
		String input ="";
		input = scanner.nextLine(); 
		if(input.equals("bulk user ../db-generation/user.sql"))
			usr.init();

		System.out.print("===> ");
		input = scanner.nextLine(); 
		if(input.equals("bulk article ../db-generation/article.sql"))
			art.init();

		System.out.print("===> ");
		input = scanner.nextLine(); 
		if(input.equals("bulk user_read ../db-generation/user_read.sql"))
			read.init();

		System.out.print("===> ");
		input = scanner.nextLine(); 
		if(input.equals("populate be_read"))
			be_read.init();

		System.out.print("===> ");
		input = scanner.nextLine(); 
		if(input.equals("populate popular_rank"))
			popular_rank.init();

		// use site2(hadoop2) as dbms1 and use site3(hadoop3) as dbms 2
		// System.out.println("-------------------------[ site2 tables ]-------------------------------");
		// db.listDatabaseTable(2,"ddb");
		// System.out.println("-------------------------[ site3 tables ]-------------------------------");
		// db.listDatabaseTable(3,"ddb");

		System.out.print("===> ");
		input = scanner.nextLine(); 
		if(!input.equals("query 5 popular_rank"))
			return;

		System.out.println("-------------------------[ start  query ]-------------------------------");	
		Jedis jedis = new Jedis("cache", 6379);  
		String redis_password = "root";	
		jedis.auth(redis_password); 
		List<String> top_five_string =popular_rank.Query_five("1506000000000","daily");
		String[] aidArray = top_five_string.toArray(new String[0]);
		art.connect(3);
		for (int i=0;i<5;i++){
			String aid = aidArray[i];
			
			System.out.println("=========== top"+i+" is "+aid);
			
			String art_text=art.Query_Article_text(aid);
			System.out.println("article text is "+art_text);

			System.out.println("-------------------------[ byte content ]-------------------------------");	
			byte[] art_text_content=null;
			if(jedis.exists(art_text)){
				art_text_content=jedis.get(art_text.getBytes());
			}else{
				art_text_content=fs.readFileBytes("/",art_text);
				jedis.set(art_text.getBytes(),art_text_content);
			}
			// art_text_content=fs.readFileBytes("/",art_text);
			if(i==0)
				System.out.println(bytesToHexString(art_text_content));
			System.out.println("------------------------------[ end ]-----------------------------------");	

			String art_image=art.Query_Article_images(aid);
			String[] art_image_part = art_image.split(",");
			for(String art_image_name:art_image_part){
				if(!art_image_name.equals("")){
					System.out.println("article image is "+art_image_name);

					System.out.println("-------------------------[ byte content ]-------------------------------");	
					byte[] art_image_content=null;
					if(jedis.exists(art_image_name)){
						art_image_content=jedis.get(art_image_name.getBytes());
					}else{
						art_image_content=fs.readFileBytes("/",art_image_name);
						jedis.set(art_image_name.getBytes(),art_image_content);
					}
					// art_image_content=fs.readFileBytes("/",art_image_name);
					// System.out.println(bytesToHexString(art_image_content));
					System.out.println("------------------------------[ end ]-----------------------------------");	
				}
			}
			
			String art_video=art.Query_Article_videos(aid);
			if(!art_video.equals("")){
				System.out.println("article video is "+art_video);

				System.out.println("-------------------------[ byte content ]-------------------------------");	
				byte[] art_video_content=null;
				if(jedis.exists(art_video)){
					art_video_content=jedis.get(art_video.getBytes());
				}else{
					art_video_content=fs.readFileBytes("/",art_video);
					jedis.set(art_video.getBytes(),art_video_content);
				}
				// art_video_content=fs.readFileBytes("/",art_video);
				// System.out.println(bytesToHexString(art_video_content));
				System.out.println("------------------------------[ end ]-----------------------------------");	
			}
		}
		art.close(3);
		jedis.close();  
		fs.close();

    }
	public static void bulk_hadoop(HDFS fs) throws Exception{
		//upload all the files and data to the hadoop
		File p = new File(local_data_dir+articles);
		List<File> file_list = new ArrayList<>();
		if (!p.exists()){
			System.out.printf("path %s is not exist\n",local_data_dir+articles);
			return;
		}
		get_all_files(p,file_list);
		int id=0;
		for (File file : file_list){
			String src_dirname=file.getParent();
			String filename=file.getName();
			fs.uploadFile(src_dirname,"/",filename,id);
			id++;
		}
		System.out.printf("total have %d files\n",file_list.size());
		fs.listFiles("/");
	}
	public static void get_all_files(File dir,List<File> file_list){
		File[] filelist=dir.listFiles();
		for (File file : filelist) {
			if(file.isDirectory()){
				get_all_files(file,file_list);
			} else {
				file_list.add(file);
			}
		} 
	}

	public static String bytesToHexString(byte[] bytes) {  
		StringBuilder sb = new StringBuilder();  
		for (byte b : bytes) {  
			sb.append(String.format("%02X ", b));  
		}  
		return sb.toString().trim();
	}  
}