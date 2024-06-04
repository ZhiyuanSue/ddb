package com.yizhu.thu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.util.ToolRunner;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFS{
	private static final Logger log = LoggerFactory.getLogger(HDFS.class);
	private static String HDFS_PATH = "hdfs://hadoop1:8020";
	static FileSystem hdfs;
	static Configuration conf;
	public void init() throws IOException{
		conf = new Configuration();
		conf.set("fs.defaultFS",HDFS_PATH);
		conf.set("fs.hdfs.impl",DistributedFileSystem.class.getName());
		hdfs = FileSystem.get(conf);
		System.out.println(hdfs.toString());
	}
	public void uploadFile(String src_dirname,String dst_dirname,String filename) throws IOException{
		Path src_p = new Path(src_dirname + "/" + filename);
		Path dst_p = new Path(HDFS_PATH + dst_dirname + "/" + filename);
		if(hdfs.exists(dst_p)){
			System.out.printf("at %s hava a file with same name,we ignore it\n",HDFS_PATH + dst_dirname + "/"+filename);
			return;
		}
		hdfs.copyFromLocalFile(false,src_p,dst_p);
		System.out.printf("successfully upload %s\n",filename);
	}
	public void listFiles(String dirname) throws IOException {
		Path p = new Path(dirname);
		FileStatus[] status = hdfs.listStatus(p);
		System.out.printf("start list hadoop files:under path %s hava %d files\n",dirname,status.length);
		for (int i=0; i < status.length; i++){
			System.out.println(status[i].getPath().toString());
		}
	}
	public boolean deleteFile(String dirname,String filename) throws IOException {
		Path p = new Path(HDFS_PATH + dirname + "/" + filename);
		if (hdfs.exists(p)){
			return hdfs.delete(p,true);
		} else {
			return false;
		}
	}
	public void createFile(String dirname, String filename,String content){
		Path p = new Path(HDFS_PATH + dirname + "/" + filename);
		byte[] bytes = content.getBytes();
		FSDataOutputStream output;
		try{
			output=hdfs.create(p);
			output.write(bytes);
		} catch (IOException e) {
			if (log.isDebugEnabled())
				log.debug("create file error:"+HDFS_PATH + dirname + "/" + filename,e);
		}
	}
	public void downloadFile(String hdfs_dirname,String local_dirname,String filename){
		Path hdfs_p = new Path(HDFS_PATH + hdfs_dirname + "/" + filename);
		Path local_p = new Path(local_dirname + "/" + filename);
		
		try{
			hdfs.copyToLocalFile(false,hdfs_p,local_p,true);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public void close(){
		try{
			hdfs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}