package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxLogout extends FbxHttpRaster{

	private Boolean loggedOut = false;
	public static Logger log = LoggerFactory.getLogger(FbxLogout.class);
	

	public FbxLogout(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		super.setParamsString("");
	}
	public FbxLogout(String url, int method, AbstractTask task) {
		super(url, method, task);
		super.setParamsString("");
	}
	public FbxLogout(String url, int method, Follower follower) {
		super(url, method, follower);
		super.setParamsString("");
	}

	public Boolean isLoggedOut() {
		return loggedOut;
	}

	public void setLoggedOut(Boolean loggedOut) {
		this.loggedOut = loggedOut;
	}

	public String toString(){
		return super.toString();
	}
}
