package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxGetDownloadTrackers extends FbxHttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(FbxGetDownloadTrackers.class);

	private Integer dwl_id;
	
	public FbxGetDownloadTrackers(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
	}
	public FbxGetDownloadTrackers(String url, int method, AbstractTask task) {
		super(url, method, task);
	}
	public FbxGetDownloadTrackers(String url, int method, Follower follower) {
		super(url, method, follower);
	}
	
	

	public Integer getDwl_id() {
		return dwl_id;
	}



	public void setDwl_id(Integer dwl_id) {
		this.dwl_id = dwl_id;
	}


	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\ndwl_id:"+dwl_id);
		sb.append("\n-----");
		return sb.toString();
	}
	
}
