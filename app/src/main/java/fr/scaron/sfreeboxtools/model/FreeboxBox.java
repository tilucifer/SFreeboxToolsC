package fr.scaron.sfreeboxtools.model;

import java.io.Serializable;

public class FreeboxBox implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7288532493826464660L;
	/**
	 *
	 */
    private String name;
    private String ip_public;
    private String port;
    private String ip_local="mafreebox.free.fr";
    private boolean selected=false;
    private int index;
    //Freebox tokens
    private String app_token;



	public FreeboxBox(){
	}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp_public() {
        return ip_public;
    }

    public void setIp_public(String ip_public) {
        this.ip_public = ip_public;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getApp_token() {
        return app_token;
    }

    public void setApp_token(String app_token) {
        this.app_token = app_token;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
