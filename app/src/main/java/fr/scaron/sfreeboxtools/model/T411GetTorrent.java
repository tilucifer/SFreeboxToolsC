package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.activity.AbstractActivity;

public class T411GetTorrent extends HttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(T411GetTorrent.class);

	private String id;
	private String name;
	private Integer icon;
	private String uploader;
	public T411GetTorrent(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		super.setCharset("ISO-8859-1");
	}
	
	
	
	public String getUploader() {
		return uploader;
	}



	public void setUploader(String uploader) {
		this.uploader = uploader;
	}



	public Integer getIcon() {
		return icon;
	}



	public void setIcon(Integer icon) {
		this.icon = icon;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	@Override
	public String getParamsString()
	{
		StringBuilder sb = new StringBuilder("");
		sb.append("id="+id);
		return sb.toString();
	}


	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\nid:"+id);
		sb.append("\nname:"+name);
		sb.append("\n-----");
		return sb.toString();
	}
}
