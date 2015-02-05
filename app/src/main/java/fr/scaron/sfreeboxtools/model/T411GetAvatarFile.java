package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.activity.AbstractActivity;

public class T411GetAvatarFile extends HttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(T411GetAvatarFile.class);

	private String url;
	public void setUrl(String url) {
		this.url = url;
	}

	public T411GetAvatarFile(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		setUrl(url);
	}
	
	@Override
	public String getParamsString()
	{
		StringBuilder sb = new StringBuilder("");
		return sb.toString();
	}


	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\n-----");
		return sb.toString();
	}

	public String getUrl() {
		return url;
	}
}
