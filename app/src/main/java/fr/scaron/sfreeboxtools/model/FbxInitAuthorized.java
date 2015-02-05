package fr.scaron.sfreeboxtools.model;

import android.bluetooth.BluetoothAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.BuildConfig;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.task.AbstractTask;

public class FbxInitAuthorized extends FbxHttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(FbxInitAuthorized.class);

	private String app_id;
	private String app_name;
	private String app_version;
	private String device_name;
	private HttpRaster request;
	public FbxInitAuthorized(String url, int method, AbstractTask task) {
		super(url, method, task);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = BuildConfig.VERSION_NAME;//"1.0.0";
		device_name = getPhoneName();
		JSONObject json = new JSONObject();
		try {
			json.put("app_id", "fr.scaron.sfreeboxtools");
			json.put("app_name", "SFreeboxTools");
			json.put("app_version", app_version);
			json.put("device_name", device_name);
			super.setParamsJson(json);
			super.setFlagParamsJson(true);
		} catch (JSONException e) {
			StringBuilder paramsSb = new StringBuilder("{");
			paramsSb.append("\"app_id\": \"fr.scaron.sfreeboxtools\"");
			paramsSb.append("\"app_name\": \"SFreeboxTools\"");
			paramsSb.append("\"app_version\": \""+app_version+"\"");
			paramsSb.append("\"device_name\": \""+device_name+"\"");
			super.setParamsString(paramsSb.toString());
			super.setFlagParamsString(true);
		}
	}
	
	public FbxInitAuthorized(String url, int method, Follower follower) {
		super(url, method, follower);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = BuildConfig.VERSION_NAME;
		device_name = getPhoneName();
		JSONObject json = new JSONObject();
		try {
			json.put("app_id", "fr.scaron.sfreeboxtools");
			json.put("app_name", "SFreeboxTools");
			json.put("app_version", "1.0.0");
			json.put("device_name", device_name);
			super.setParamsJson(json);
			super.setFlagParamsJson(true);
		} catch (JSONException e) {
			StringBuilder paramsSb = new StringBuilder("{");
			paramsSb.append("\"app_id\": \"fr.scaron.sfreeboxtools\"");
			paramsSb.append("\"app_name\": \"SFreeboxTools\"");
			paramsSb.append("\"app_version\": \"\"+app_version+\"\"");
			paramsSb.append("\"device_name\": \""+device_name+"\"");
			super.setParamsString(paramsSb.toString());
			super.setFlagParamsString(true);
		}
	}
	
	public FbxInitAuthorized(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		app_id = "fr.scaron.sfreeboxtools";
		app_name = "SFreeboxTools";
		app_version = BuildConfig.VERSION_NAME;
		device_name = getPhoneName();
		JSONObject json = new JSONObject();
		try {
			json.put("app_id", "fr.scaron.sfreeboxtools");
			json.put("app_name", "SFreeboxTools");
			json.put("app_version", "1.0.0");
			json.put("device_name", device_name);
			super.setParamsJson(json);
			super.setFlagParamsJson(true);
		} catch (JSONException e) {
			StringBuilder paramsSb = new StringBuilder("{");
			paramsSb.append("\"app_id\": \"fr.scaron.sfreeboxtools\"");
			paramsSb.append("\"app_name\": \"SFreeboxTools\"");
			paramsSb.append("\"app_version\": \"\"+app_version+\"\"");
			paramsSb.append("\"device_name\": \""+device_name+"\"");
			super.setParamsString(paramsSb.toString());
			super.setFlagParamsString(true);
		}
	}

    public String getPhoneName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        return deviceName;
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


	public String getJsonString(){
		return super.getParamsJson().toString();
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
		sb.append("\napp_id:"+app_id);
		sb.append("\napp_name:"+app_name);
		sb.append("\napp_version:"+app_version);
		sb.append("\ndevice_name:"+device_name);
		sb.append("\n-----");
		return sb.toString();
	}
	
}
