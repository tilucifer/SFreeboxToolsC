package fr.scaron.sfreeboxtools.model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadTracker {

	public static Logger log = LoggerFactory.getLogger(DownloadTracker.class);
	Double nseeders;
	Double nleechers;
	Double reannounce_in;
    Boolean is_backup;
    Double interval;
    Double min_interval;
    String announce;
    String status;
    Boolean is_enabled;
    
    
    public DownloadTracker(JSONObject json){
    	/*
    	"nseeders": 0,
        "nleechers": 0,
        "reannounce_in": 790,
        "is_backup": false,
        "interval": 900,
        "min_interval": 60,
        "announce": "http://bttracker.debian.org:6969/announce",
        "status": "announced"
    	*/
        nseeders = json.optDouble("nseeders");
        nleechers = json.optDouble("nleechers");
        reannounce_in = json.optDouble("reannounce_in");
        is_backup = json.optBoolean("is_backup");
        interval = json.optDouble("interval");
        min_interval = json.optDouble("min_interval");
        announce = json.optString("announce");
        status = json.optString("status");
        is_enabled = json.optBoolean("is_enabled");
    }
    

	public Double getNseeders() {
		return nseeders;
	}


	public void setNseeders(Double nseeders) {
		this.nseeders = nseeders;
	}


	public Double getNleechers() {
		return nleechers;
	}


	public void setNleechers(Double nleechers) {
		this.nleechers = nleechers;
	}


	public Double getReannounce_in() {
		return reannounce_in;
	}


	public void setReannounce_in(Double reannounce_in) {
		this.reannounce_in = reannounce_in;
	}


	public Boolean getIs_backup() {
		return is_backup;
	}


	public void setIs_backup(Boolean is_backup) {
		this.is_backup = is_backup;
	}


	public Double getInterval() {
		return interval;
	}


	public void setInterval(Double interval) {
		this.interval = interval;
	}


	public Double getMin_interval() {
		return min_interval;
	}


	public void setMin_interval(Double min_interval) {
		this.min_interval = min_interval;
	}


	public String getAnnounce() {
		return announce;
	}


	public void setAnnounce(String announce) {
		this.announce = announce;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Boolean getIs_enabled() {
		return is_enabled;
	}


	public void setIs_enabled(Boolean is_enabled) {
		this.is_enabled = is_enabled;
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
		
		if ("announcing".equals(status)){
			return "connexion en cours";
		}
		if ("announced".equals(status)){
			return "connexion établie";
		}
		if ("unannounced".equals(status)){
			return "connexion terminée";
		}
		if ("announce_failed".equals(status)){
			return "connexion impossible";
		}
		return "pas de connexion";
	}
	
	public static List<DownloadTracker> getDownloadTrackers(JSONArray jsonArray){
		List<DownloadTracker> downloadTrackers = new ArrayList<DownloadTracker>();
		for (int indexDwl = 0; indexDwl < jsonArray.length(); indexDwl++) {
			try {
				Object dwl = jsonArray.get(indexDwl);
				if (dwl instanceof JSONObject) {
					downloadTrackers.add(new DownloadTracker((JSONObject) dwl));
				}
			} catch (JSONException e) {
				log.trace("Erreur : " + e.getMessage());
			}
		}
		return downloadTrackers;
	}
}
