package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxLogin extends FbxHttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(FbxLogin.class);

	private String app_id;
	private String app_name;
	private String app_version;
	private String device_name;
	private String app_token;
	private String challenge;
	private Boolean logged_in;
	private String passwordsha;
	private HttpRaster request;

	public FbxLogin(String url, int method, Follower follower) {
		super(url, method, follower);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = "1.0.0";
		device_name = "Nexus5 de tilucifer";
	}
	public FbxLogin(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = "1.0.0";
		device_name = "Nexus5 de tilucifer";
	}
	public FbxLogin(String url, int method, AbstractTask task) {
		super(url, method, task);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = "1.0.0";
		device_name = "Nexus5 de tilucifer";
	}

	public String getApp_id() {
		return app_id;
	}



	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}



	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}


	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getDevice_name() {
		return device_name;
	}



	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}



	public String getApp_token() {
		return app_token;
	}



	public void setApp_token(String app_token) {
		this.app_token = app_token;
	}


	public String getChallenge() {
		return challenge;
	}



	public void setChallenge(String challenge) {
		this.challenge = challenge;
		setPasswordsha(Params.hmacSha1(this.challenge, app_token));
	}

	public String getPasswordsha() {
		return passwordsha;
	}



	public Boolean getLogged_in() {
		return logged_in;
	}

	public void setLogged_in(Boolean logged_in) {
		this.logged_in = logged_in;
	}

	public HttpRaster getRequest() {
		return request;
	}

	public void setRequest(HttpRaster request) {
		this.request = request;
	}

	public void setPasswordsha(String passwordsha) {
		this.passwordsha = passwordsha;
	}



	public String getJsonString(){
		return super.getParamsJson().toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\napp_id:"+app_id);
		sb.append("\napp_name:"+app_name);
		sb.append("\napp_version:"+app_version);
		sb.append("\ndevice_name:"+device_name);
		sb.append("\napp_token:"+app_token);
		sb.append("\nchallenge:"+challenge);
		sb.append("\nlogged_in:"+logged_in);
		sb.append("\npasswordsha:"+passwordsha);
		sb.append("\n-----");
		return sb.toString();
	}
	
	
}
