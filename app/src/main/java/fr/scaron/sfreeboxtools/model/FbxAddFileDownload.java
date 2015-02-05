package fr.scaron.sfreeboxtools.model;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxAddFileDownload extends FbxHttpRaster {
	public static Logger log = LoggerFactory.getLogger(FbxAddFileDownload.class);

	private String download_file_name;
	private byte[] download_file_bytes;
	private File download_file; //The File as multipart
	private String download_dir; //The download destination directory (optional: will use the configuration download_dir by default)

	public FbxAddFileDownload(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
	}

	public FbxAddFileDownload(String url, int method, AbstractTask task) {
		super(url, method, task);
	}

	public FbxAddFileDownload(String url, int method, Follower follower) {
		super(url, method, follower);
	}
	
	
	public String getDownload_file_name() {
		return download_file_name;
	}



	public void setDownload_file_name(String download_file_name) {
		this.download_file_name = download_file_name;
	}



	public byte[] getDownload_file_bytes() {
		return download_file_bytes;
	}



	public void setDownload_file_bytes(byte[] download_file_byes) {
		this.download_file_bytes = download_file_byes;
	}


	public File getDownload_file() {
		return download_file;
	}

	public void setDownload_file(File download_file) {
		this.download_file = download_file;
	}

	public String getDownload_dir() {
		return download_dir;
	}

	public void setDownload_dir(String download_dir) {
		this.download_dir = download_dir;
	}
	
	
	
	
	@Override
	public String getParamsString(){
		StringBuilder sb = new StringBuilder();
		if (download_dir!=null && !download_dir.isEmpty()){
			if (sb.length()>0){
				sb.append("&");
			}
			sb.append("download_dir="+download_dir);
		}
		return sb.toString();
	}
	

	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\ndownload_dir:"+download_dir);
		sb.append("\ndownload_file_name:"+download_file_name);
		sb.append("\n-----");
		return sb.toString();
	}
	
	

}
