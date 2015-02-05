package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxAddUrlDownload extends FbxHttpRaster {
	public static Logger log = LoggerFactory.getLogger(FbxAddUrlDownload.class);

	private String download_url; //The URL
	private String download_url_list; //A list of URL separated by a new line delimiter (use download_url or download_url_list)
	private String download_dir; //The download destination directory (optional: will use the configuration download_dir by default)

	public FbxAddUrlDownload(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
	}
	

	public FbxAddUrlDownload(String url, int method, AbstractTask task) {
		super(url, method, task);
	}

	public FbxAddUrlDownload(String url, int method, Follower follower) {
		super(url, method, follower);
	}
	
	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getDownload_url_list() {
		return download_url_list;
	}

	public void setDownload_url_list(String download_url_list) {
		this.download_url_list = download_url_list;
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
		if (download_url!=null && !download_url.isEmpty()){
			if (sb.length()>0){
				sb.append("&");
			}
			sb.append("download_url="+download_url);
		}
		if (download_url_list!=null && !download_url_list.isEmpty()){
			if (sb.length()>0){
				sb.append("&");
			}
			sb.append("download_url_list="+download_url_list);
		}
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
		sb.append("\ndownload_url:"+download_url);
		sb.append("\ndownload_url_list:"+download_url_list);
		sb.append("\n-----");
		return sb.toString();
	}
	
	

}
