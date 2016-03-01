package fr.scaron.sfreeboxtools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.activity.AbstractActivity;

public class T411Login extends HttpRaster implements Cloneable{
	public static Logger log = LoggerFactory.getLogger(T411Login.class);

	private String login;
	private String password;
	private String remember;
	private HttpRaster request;
	
	public T411Login(String url, int method, AbstractActivity activity) {
		super(url, method, activity);
		login = "";
		password = "";
		remember = "1";
	}
	
	
	
	public String getLogin() {
		return login;
	}



	public void setLogin(String login) {
		this.login = login;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getRemember() {
		return remember;
	}



	public void setRemember(String remember) {
		this.remember = remember;
	}
	
	

	public HttpRaster getRequest() {
		return request;
	}



	public void setRequest(HttpRaster request) {
		this.request = request;
	}



	@Override
	public String getParamsString()
	{
		StringBuilder sb = new StringBuilder("");
		sb.append("url=/");
		sb.append("remember="+remember);
		sb.append("&login="+login);
		sb.append("&password="+password);
		return sb.toString();
	}


	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");
		sb.append(super.toString());
		sb.append("\nurl=/");
		sb.append("\nlogin:"+login);
		sb.append("\npassword:"+password);
		sb.append("\nremember:"+remember);
		sb.append("\n-----");
		return sb.toString();
	}
}
