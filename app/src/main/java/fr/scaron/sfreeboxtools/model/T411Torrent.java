package fr.scaron.sfreeboxtools.model;

import java.io.Serializable;

import fr.scaron.sfreeboxtools.R;

public class T411Torrent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4160013732243561422L;
	String html_filelist = "";
    String torrent_NFO;
    String torrent_URL;
    String torrent_Name;
    String torrent_ID;
    String tdt_seeders;
    String tdt_leechers;
    String tdt_note;
    String tdt_votes;
    String tdt_complets;
    String tdt_taille;
    double note = 0;
    String prez = "<meta name=\"viewport\" content=\"width=320; user-scalable=no\" />Erreur lors de la récupération des données...";
    String tduploader = "";
    int nbHadopi = 0;
    int icon = R.drawable.ic_new_t411;
    
    public T411Torrent(String html_filelist, String torrent_NFO,String torrent_URL,
    		String torrent_Name,String torrent_ID,String tdt_seeders,
    		String tdt_leechers,String tdt_note,String tdt_votes,
    		String tdt_complets,String tdt_taille,double note,String prez,
    		String tduploader,int nbHadopi, int icon){
    	this.html_filelist = html_filelist;
    	this.torrent_NFO = torrent_NFO;
    	this.torrent_URL = torrent_URL;
    	this.torrent_Name = torrent_Name;
    	this.torrent_ID = torrent_ID;
    	this.tdt_seeders = tdt_seeders;
    	this.tdt_leechers = tdt_leechers;
    	this.tdt_note = tdt_note;
    	this.tdt_votes = tdt_votes;
    	this.tdt_complets = tdt_complets;
    	this.tdt_taille = tdt_taille;
    	this.note = note;
    	this.prez = prez;
    	this.tduploader = tduploader;
    	this.nbHadopi = nbHadopi;
    	this.icon = icon;;
    }




	public int getIcon() {
		return icon;
	}




	public void setIcon(int icon) {
		this.icon = icon;
	}




	public String getHtml_filelist() {
		return html_filelist;
	}




	public void setHtml_filelist(String html_filelist) {
		this.html_filelist = html_filelist;
	}




	public String getTorrent_NFO() {
		return torrent_NFO;
	}




	public void setTorrent_NFO(String torrent_NFO) {
		this.torrent_NFO = torrent_NFO;
	}




	public String getTorrent_URL() {
		return torrent_URL;
	}




	public void setTorrent_URL(String torrent_URL) {
		this.torrent_URL = torrent_URL;
	}




	public String getTorrent_Name() {
		return torrent_Name;
	}




	public void setTorrent_Name(String torrent_Name) {
		this.torrent_Name = torrent_Name;
	}




	public String getTorrent_ID() {
		return torrent_ID;
	}




	public void setTorrent_ID(String torrent_ID) {
		this.torrent_ID = torrent_ID;
	}




	public String getTdt_seeders() {
		return tdt_seeders;
	}




	public void setTdt_seeders(String tdt_seeders) {
		this.tdt_seeders = tdt_seeders;
	}




	public String getTdt_leechers() {
		return tdt_leechers;
	}




	public void setTdt_leechers(String tdt_leechers) {
		this.tdt_leechers = tdt_leechers;
	}




	public String getTdt_note() {
		return tdt_note;
	}




	public void setTdt_note(String tdt_note) {
		this.tdt_note = tdt_note;
	}




	public String getTdt_votes() {
		return tdt_votes;
	}




	public void setTdt_votes(String tdt_votes) {
		this.tdt_votes = tdt_votes;
	}




	public String getTdt_complets() {
		return tdt_complets;
	}




	public void setTdt_complets(String tdt_complets) {
		this.tdt_complets = tdt_complets;
	}




	public String getTdt_taille() {
		return tdt_taille;
	}




	public void setTdt_taille(String tdt_taille) {
		this.tdt_taille = tdt_taille;
	}




	public double getNote() {
		return note;
	}




	public void setNote(double note) {
		this.note = note;
	}




	public String getPrez() {
		return prez;
	}




	public void setPrez(String prez) {
		this.prez = prez;
	}




	public String getTduploader() {
		return tduploader;
	}




	public void setTduploader(String tduploader) {
		this.tduploader = tduploader;
	}




	public int getNbHadopi() {
		return nbHadopi;
	}




	public void setNbHadopi(int nbHadopi) {
		this.nbHadopi = nbHadopi;
	}




	@Override
    public String toString(){
    	StringBuilder sb=new StringBuilder("T411Torren\n--------\n");
    	sb.append("html_filelist = "+html_filelist);
    	sb.append("torrent_NFO = "+torrent_NFO);
    	sb.append("torrent_URL = "+torrent_URL);
    	sb.append("torrent_Name = "+torrent_Name);
    	sb.append("torrent_ID = "+torrent_ID);
    	sb.append("tdt_seeders = "+tdt_seeders);
    	sb.append("tdt_leechers = "+tdt_leechers);
    	sb.append("tdt_note = "+tdt_note);
    	sb.append("tdt_votes = "+tdt_votes);
    	sb.append("tdt_complets = "+tdt_complets);
    	sb.append("tdt_taille = "+tdt_taille);
    	sb.append("note = "+note);
    	sb.append("prez = "+prez);
    	sb.append("tduploader = "+tduploader);
    	sb.append("nbHadopi = "+nbHadopi);
    	sb.append("icon = "+icon);
    	sb.append("\n--------");
    	return sb.toString();
    }
}
