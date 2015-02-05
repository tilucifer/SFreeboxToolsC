package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.activity.AbstractActivity;

public class T411Search extends HttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(T411Search.class);

	private String keywords;
	private String order="added";
	private String type="desc";
	private String paginator;
	
	public T411Search(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		super.setCharset("ISO-8859-1");
		super.setFlagParamsString(true);
	}
	
	
	
	public String getKeywords() {
		return keywords;
	}



	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}



	public String getOrder() {
		return order;
	}



	public void setOrder(String order) {
		this.order = order;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getPaginator() {
		return paginator;
	}



	public void setPaginator(String paginator) {
		this.paginator = paginator;
		if (paginator!=null){
			int ind = paginator.indexOf('#');
			if (ind<paginator.length()){
				this.paginator = paginator.substring(0, ind);
			}
		}
	}



	@Override
	public String getParamsString()
	{
		StringBuilder sb = new StringBuilder("");
		if (keywords!=null){
			sb.append(keywords);
		}
		sb.append("&order=");
		if (order!=null){
			sb.append(order);
		}
		sb.append("&type=");
		if (type !=null){
			sb.append(type);
		}
		sb.append("&page=");
		if (paginator !=null){
			sb.append(paginator);
		}
		return sb.toString();
	}


	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\nkeywords:"+keywords);
		sb.append("\norder:"+order);
		sb.append("\ntype:"+type);
		sb.append("\npaginator:"+paginator);
		sb.append("\n-----");
		return sb.toString();
	}
}
