package fr.scaron.sfreeboxtools.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxSession extends FbxHttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(FbxSession.class);

	private String passwordsha;
	private HttpRaster request;
	
	/*
	 "app_id": "fr.freebox.testapp",
	 "app_name": "Test App",
	 "app_version": "0.0.7",
	 "device_name": "Pc de Xavier"
	*/
	public FbxSession(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
	}
	public FbxSession(String url, int method, AbstractTask task) {
		super(url, method, task);
	}
	public FbxSession(String url, int method, Follower follower) {
		super(url, method, follower);
	}
	
	private void initJson(){
		JSONObject jsonSession = new JSONObject();
		super.setParamsJson(null);
		super.setFlagParamsJson(false);
		try {
			jsonSession.put("app_id", "fr.scaron.sfreeboxtools");
			jsonSession.put("password", passwordsha);
			super.setParamsJson(jsonSession);
			super.setFlagParamsJson(true);
		} catch (JSONException e) {}
	}


	public String getJsonString(){
		initJson();
		return super.getParamsJson().toString();
	}

	

	public String getPasswordsha() {
		return passwordsha;
	}

	public void setPasswordsha(String passwordsha) {
		this.passwordsha = passwordsha;
		initJson();
	}

	public HttpRaster getRequest() {
		return request;
	}


	public void setRequest(HttpRaster request) {
		this.request = request;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\npasswordsha:"+passwordsha);
		sb.append("\n-----");
		return sb.toString();
	}	
}
