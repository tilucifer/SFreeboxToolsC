package fr.scaron.sfreeboxtools.model;

import java.io.Serializable;
import java.text.NumberFormat;

import org.json.JSONObject;

import android.util.Base64;

public class Download implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2523482230535806799L;
	Double rx_bytes=Double.valueOf(0);
    Double tx_bytes=Double.valueOf(0);
    String download_dir;
    String archive_password;
    Double eta=Double.valueOf(0);
    String status;
    String io_priority;
    Double size=Double.valueOf(0);
    String type;
    String error;
    Double queue_pos=Double.valueOf(0);
    Integer id;
    Double created_ts=Double.valueOf(0);
    Double tx_rate=Double.valueOf(0);
    String name;
    Double rx_pct=Double.valueOf(0);
    Double rx_rate=Double.valueOf(0);
    Double tx_pct=Double.valueOf(0);
	Double stop_ratio=Double.valueOf(0);
	
	 public Download(){
	 }
    
    public Download(JSONObject json){
    	rx_bytes = json.optDouble("rx_bytes");
        tx_bytes = json.optDouble("tx_bytes");
        download_dir = new String(Base64.decode(json.optString("download_dir"),Base64.URL_SAFE));
        archive_password = json.optString("archive_password");
        eta = json.optDouble("eta");
        status = json.optString("status");
        io_priority = json.optString("io_priority");
        size = json.optDouble("size");
        type = json.optString("type");
        error = json.optString("error");
        queue_pos = json.optDouble("queue_pos");
        id = json.optInt("id");
        created_ts = json.optDouble("created_ts");
        stop_ratio = json.optDouble("stop_ratio");
        tx_rate = json.optDouble("tx_rate");
        name = json.optString("name");
        rx_pct = json.optDouble("rx_pct");
        rx_rate = json.optDouble("rx_rate");
        tx_pct = json.optDouble("tx_pct");
    }
    
	public Double getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(Double rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public Double getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(Double tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
	public String getDownload_dir() {
		return download_dir;
	}
	public void setDownload_dir(String download_dir) {
		this.download_dir = download_dir;
	}
	public String getArchive_password() {
		return archive_password;
	}
	public void setArchive_password(String archive_password) {
		this.archive_password = archive_password;
	}
	public Double getEta() {
		return eta;
	}
	public void setEta(Double eta) {
		this.eta = eta;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIo_priority() {
		return io_priority;
	}
	public void setIo_priority(String io_priority) {
		this.io_priority = io_priority;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Double getQueue_pos() {
		return queue_pos;
	}
	public void setQueue_pos(Double queue_pos) {
		this.queue_pos = queue_pos;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getCreated_ts() {
		return created_ts;
	}
	public void setCreated_ts(Double created_ts) {
		this.created_ts = created_ts;
	}
	public Double getTx_rate() {
		return tx_rate;
	}
	public void setTx_rate(Double tx_rate) {
		this.tx_rate = tx_rate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getRx_pct() {
		return rx_pct;
	}
	public void setRx_pct(Double rx_pct) {
		this.rx_pct = rx_pct;
	}
	public Double getRx_rate() {
		return rx_rate;
	}
	public void setRx_rate(Double rx_rate) {
		this.rx_rate = rx_rate;
	}
	public Double getTx_pct() {
		return tx_pct;
	}
	public void setTx_pct(Double tx_pct) {
		this.tx_pct = tx_pct;
	}
	public Double getStop_ratio() {
		return stop_ratio;
	}

	public void setStop_ratio(Double stop_ratio) {
		this.stop_ratio = stop_ratio;
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
			return "t\351l\351charge \340 "+getOctetValue(rx_rate)+"/s ("+getOctetValue(rx_bytes)+"/"+getOctetValue(size)+")";
		}
		if ("done".equals(status)){
			return "t\351l\351chargement termin\351.";
		}
		if ("seeding".equals(status)){
			if (tx_rate<=0){
				return "t\351l\351chargement termin\351.";
			}
			return "distribution \340 "+getOctetValue(tx_rate)+"/s";
		}
		if ("error".equals(status)){
			return "en erreur ("+getOctetValue(rx_bytes)+"/"+getOctetValue(size)+")";
		}

		return "en pause ("+getOctetValue(rx_bytes)+"/"+getOctetValue(size)+")";
	}
}
