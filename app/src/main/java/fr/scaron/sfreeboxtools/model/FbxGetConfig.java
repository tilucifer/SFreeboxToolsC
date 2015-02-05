package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxGetConfig extends FbxHttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(FbxGetConfig.class);

	public FbxGetConfig(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
	}
	public FbxGetConfig(String url, int method, AbstractTask task) {
		super(url, method, task);
	}
	public FbxGetConfig(String url, int method, Follower follower) {
		super(url, method, follower);
	}
	
	

	public String getJsonString(){
		return super.getParamsJson().toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\n-----");
		return sb.toString();
	}
	
	
}
