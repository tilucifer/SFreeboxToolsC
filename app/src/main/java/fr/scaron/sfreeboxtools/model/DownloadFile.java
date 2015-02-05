package fr.scaron.sfreeboxtools.model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadFile {

	public static Logger log = LoggerFactory.getLogger(DownloadFile.class);
	Double rx;
    String path;
    String errmsg;
    String status;
    Integer id;
    String priority;
    Integer task_id;
    Double size;
    
    
    public DownloadFile(JSONObject json){
    	rx = json.optDouble("rx");
        path = json.optString("path");
        errmsg = json.optString("errmsg");
        status = json.optString("status");
        id = json.optInt("id");
        priority = json.optString("priority");
        task_id = json.optInt("task_id");
        size = json.optDouble("size");
    }
    
	public Double getRx_bytes() {
		return rx;
	}
	public void setRx_bytes(Double rx) {
		this.rx = rx;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTask_id() {
		return task_id;
	}
	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}
	
	public static List<DownloadFile> getDownloadFiles(JSONArray jsonArray){
		List<DownloadFile> downloadFiles = new ArrayList<DownloadFile>();
		for (int indexDwl = 0; indexDwl < jsonArray.length(); indexDwl++) {
			try {
				Object dwl = jsonArray.get(indexDwl);
				if (dwl instanceof JSONObject) {
					downloadFiles.add(new DownloadFile((JSONObject) dwl));
				}
			} catch (JSONException e) {
				log.trace("Erreur : " + e.getMessage());
			}
		}
		return downloadFiles;
	}

	public String getOctetValue(double octet){
		int puissanceOctet = 0;
		double resteOctet = octet/1024;
		while(resteOctet>=1){
			puissanceOctet++;
			resteOctet = resteOctet/1024;
		}
		double xOctet = octet/Math.pow(1024,puissanceOctet);
			NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);            
		nf.setGroupingUsed(false); 
		String formattedvalue = nf.format(xOctet);
		String sizeOctetStr=""+formattedvalue;
		switch(puissanceOctet){
			case 1 :
				return sizeOctetStr+ " ko";
			case 2 :
				return sizeOctetStr+ " Mo";
			case 3 :
				return sizeOctetStr+ " Go";
			case 4 :
				return sizeOctetStr+ " To";
			case 5 :
				return sizeOctetStr+ " Po";
			case 6 :
				return sizeOctetStr+ " Eo";
			case 7 :
				return sizeOctetStr+ " Zo";
			default : 
				return sizeOctetStr + " o";
		}
		
	}
	@Override
	public String toString(){

		if ("downloading".equals(status)){
			return "t\351l\351charge ("+getOctetValue(rx)+"/"+getOctetValue(size)+")";
		}
		if ("done".equals(status)){
			return "t\351l\351chargement termin\351.";
		}
		if ("seeding".equals(status)){
			return "distribution en cours";
		}
		if ("error".equals(status)){
			return "en erreur ("+getOctetValue(rx)+"/"+getOctetValue(size)+")";
		}

		return "en pause ("+getOctetValue(rx)+"/"+getOctetValue(size)+")";
	}
}
