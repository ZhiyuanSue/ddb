package com.yizhu.thu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 */
public class App 
{
	static String local_data_dir="/home/ddb/db-generation/";
	static String articles="articles";
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Start test Hadoop" );
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

		usr.init();
		art.init();
		read.init();
		be_read.init();
		popular_rank.init();

		// use site2(hadoop2) as dbms1 and use site3(hadoop3) as dbms 2
		System.out.println("-------------------------[ site2 tables ]-------------------------------");
		db.listDatabaseTable(2,"ddb");
		System.out.println("-------------------------[ site3 tables ]-------------------------------");
		db.listDatabaseTable(3,"ddb");

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
		for (File file : file_list){
			String src_dirname=file.getParent();
			String filename=file.getName();
			// fs.uploadFile(src_dirname,"/",filename);
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
}