package fr.scaron.sfreeboxtools.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ImageButton;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.model.BSize;
import fr.scaron.sfreeboxtools.model.Base64;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.model.IPUtils;
import fr.scaron.sfreeboxtools.model.T411GetAvatarFile;
import fr.scaron.sfreeboxtools.model.T411GetProfile;
import fr.scaron.sfreeboxtools.model.T411GetTorrent;
import fr.scaron.sfreeboxtools.model.T411GetTorrentFile;
import fr.scaron.sfreeboxtools.model.T411Login;
import fr.scaron.sfreeboxtools.model.T411Search;
import fr.scaron.sfreeboxtools.model.T411Torrent;
import fr.scaron.sfreeboxtools.model.T411Torrrents;
import fr.scaron.sfreeboxtools.model.TinyDB;
import fr.scaron.sfreeboxtools.view.CategoryIcon;

public class T411Controler {

    static ArrayList<HashMap<String, String>> listItem, pageListItem;

	static ArrayList<HashMap<String, String>> catListItem;
	
	static String strNext, strPrev;
	
	static ImageButton prev, next;
	
	static SharedPreferences prefs;
	
	public static Logger log = LoggerFactory.getLogger(T411Controler.class);
	
	
	public static boolean checkGlobalPrefs(AbstractActivity follower) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(follower.getBaseContext());

		TinyDB myDB = new TinyDB(follower);
		String ip = "";
		String ipTracker = "";
		boolean errorParams=false;		
		if (!settings.contains("pref_user_public_torrent")) errorParams=true;	
		if (!settings.contains("pref_user_public_torrent")) errorParams=true;
		if (!settings.contains("pref_urlaccess_public_torrent"))
			errorParams = true;
		if (!errorParams) {
			ip = myDB.getString("pref_urlaccess_public_torrent");
			if (ip.isEmpty())
				errorParams = true;
			Params.T411_DNS_COURT = ip;
			Params.T411_SERVER_IS_IP = IPUtils.isIpAddress(Params.T411_DNS_COURT);

			if (Params.T411_SERVER_IS_IP && settings.contains("pref_urlaccess_public_torrent")) {
				ipTracker = myDB.getString("pref_urlaccess_public_torrent");
				if (ipTracker.isEmpty())
					errorParams = true;
				Params.T411_TRACKER_IP = ipTracker;
			}
			Params.reinitT411();
		}
		
		return errorParams;
	}

	public static Object requestProcess(HttpRaster httpRaster, AbstractActivity follower){
		log.info("requestProcess : " + httpRaster.getClass().getName());
        prefs = PreferenceManager.getDefaultSharedPreferences(follower.getApplicationContext());
		HttpConnect connection = HttpConnect.getInstance();
		if (httpRaster instanceof T411Login){
			log.debug("Traitement particulier pour T411Login");
			follower.sendMessage(Params.MSG_IND, "Identification");
			T411Login t411Login = (T411Login) httpRaster;
			t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
			t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));		
			connection.execute(t411Login);
			return "Identification sur T411";
		}else if (httpRaster instanceof T411Search){
			T411Search t411Search = (T411Search)httpRaster;
			log.debug("Traitement particulier pour T411Search");
			follower.sendMessage(Params.MSG_IND, "Recherche");
			if (!Params.LOGGED_T411){
				log.debug("Vous n'êtes pas authentifié à votre compte T411 -> ");
				T411Login t411Login = new T411Login(
						Params.T411_URL_LOGIN, Params.POST, follower);
				t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
				t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));	
				t411Login.setRequest(t411Search);
				return requestProcess(t411Login, follower);
			}
			connection.execute(t411Search);
			return "Recherche sur T411";
		}else if (httpRaster instanceof T411GetTorrent){
			T411GetTorrent t411GetTorrent = (T411GetTorrent)httpRaster;
			log.debug("Traitement particulier pour T411GetTorrent");
			follower.sendMessage(Params.MSG_IND, "Récupération de la fiche");
			if (!Params.LOGGED_T411){
				T411Login t411Login = new T411Login(
						Params.T411_URL_LOGIN, Params.POST, follower);
				t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
				t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));	
				t411Login.setRequest(t411GetTorrent);
				return requestProcess(t411Login, follower);
			}
			connection.execute(t411GetTorrent);
			return null;
			//return "Récupération de la fiche Torrent sur T411";
		}else if (httpRaster instanceof T411GetTorrentFile){
			T411GetTorrentFile t411GetTorrentFile = (T411GetTorrentFile)httpRaster;
			log.debug("Traitement particulier pour T411GetTorrentFile");
			follower.sendMessage(Params.MSG_IND, "Récupération du fichier");
			if (!Params.LOGGED_T411){
				T411Login t411Login = new T411Login(
						Params.T411_URL_LOGIN, Params.POST, follower);
				t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
				t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));	
				t411Login.setRequest(t411GetTorrentFile);
				return requestProcess(t411Login, follower);
			}
			connection.execute(t411GetTorrentFile);
			return "Récupération du fichier Torrent sur T411";
		}else if (httpRaster instanceof T411GetAvatarFile){
			T411GetAvatarFile t411GetAvatarFile = (T411GetAvatarFile)httpRaster;
			log.debug("Traitement particulier pour T411GetAvatarFile");
			follower.sendMessage(Params.MSG_IND, "Récupération du fichier");
			if (!Params.LOGGED_T411){
				T411Login t411Login = new T411Login(
						Params.T411_URL_LOGIN, Params.POST, follower);
				t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
				t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));	
				t411Login.setRequest(t411GetAvatarFile);
				return requestProcess(t411Login, follower);
			}
			connection.execute(t411GetAvatarFile);
			return "Récupération de l'avatar T411";
		}else if (httpRaster instanceof T411GetProfile){
			T411GetProfile t411GetProfile = (T411GetProfile)httpRaster;
			log.debug("Traitement particulier pour T411GetProfile");
			follower.sendMessage(Params.MSG_IND, "Récupération du fichier");
			if (!Params.LOGGED_T411){
				T411Login t411Login = new T411Login(
						Params.T411_URL_LOGIN, Params.POST, follower);
				t411Login.setLogin(prefs.getString("pref_user_public_torrent",""));
				t411Login.setPassword(prefs.getString("pref_pwd_public_torrent",""));	
				t411Login.setRequest(t411GetProfile);
				return requestProcess(t411Login, follower);
			}
			connection.execute(t411GetProfile);
			return "Récupération du profil T411";
		}

		follower.sendMessage(Params.MSG_CNF,
				"Opération inconnue sur T411");
		log.debug("Aucun traitement à faire pour cette requete");
		connection.execute(httpRaster);
		return "Requête sur T411";
	}
	
	public static Object responseProcess(HttpRaster httpRaster, AbstractActivity follower){

		log.info("responseProcess : " + httpRaster.getClass().getName());
		if (httpRaster instanceof T411Login){
			String reponse = "LOGIN_T411_KO";
			T411Login t411Login = (T411Login)httpRaster;
			String html = t411Login.getResponse();
			if(!html.equals("OK")) {
                Document doc = Jsoup.parse(html);
                Elements messages = doc.select("#messages > p");
                if (messages!=null){
                	log.trace("messages\n------------\n"+messages.text());
                	Element firstMessage = messages.first();
                	if (firstMessage!=null){
                    	log.trace("first message\n------------\n"+firstMessage.text());
                		reponse=firstMessage.text();
	                	if ("Vous &#234;tes d&#233;j&#224; identifi&#233;.".equals(reponse)
	                			|| "Vous êtes déjà identifié.".equals(reponse)){
	                		reponse = "LOGIN_T411_OK";
	                    	Params.LOGGED_T411 = true;
	                    	try {
								t411Login(t411Login.getLogin(),t411Login.getPassword(),doc, follower);
							} catch (IOException e) {
								log.debug("Impossible de récupérer les informations utilisateurs T411");
							}
	                	}
                	}
                }
            }else{
            	reponse = "LOGIN_T411_OK";
            	Params.LOGGED_T411 = true;

                Document doc = Jsoup.parse(html);
                try {
					t411Login(t411Login.getLogin(),t411Login.getPassword(),doc, follower);
				} catch (IOException e) {
					log.debug("Impossible de récupérer les informations utilisateurs T411");
				}
            	// else reponse="Connexion réussie !";
            }
			if ("LOGIN_T411_OK".equals(reponse)&&t411Login.getRequest()!=null){
				return requestProcess(t411Login.getRequest(), follower);
			}
			return reponse;
		}else if (httpRaster instanceof T411Search){
			return t411GetTorrents((T411Search)httpRaster, follower);
		}else if (httpRaster instanceof T411GetTorrent){
			return t411GetTorrent((T411GetTorrent)httpRaster, follower);
		}else if (httpRaster instanceof T411GetTorrentFile){
			return t411GetTorrentFile((T411GetTorrentFile)httpRaster, follower);
		}else if (httpRaster instanceof T411GetAvatarFile){
			return t411GetAvatarFile((T411GetAvatarFile)httpRaster, follower);
		}else if (httpRaster instanceof T411GetProfile){
			return t411GetProfile((T411GetProfile)httpRaster, follower);
		}
		return "Une erreur c'est produite lors de la communication de l'application avec t411 : "
		+ httpRaster.getResponse();
	}
	
	

    public static Object t411Login(String login, String password, Document doc, AbstractActivity follower) throws IOException {

    	log.info("t411Login");
    	 Integer mails=null, oldmails;
    	 double ratio;
    	 String upload, download, username, conError = "", usernumber = null;
    	 String avatar = null;
    	 String pagecontent;
    	
        if (doc.select("title").contains("503")){
            return "Maintenance -> t411 est actuellement indisponible.";
        }
        try {
            ratio = Math.round(Float.valueOf(doc.select(".rate").first().text().replace(',', '.')) * 100.0) / 100.0;
            log.trace("Ratio : '"+String.valueOf(ratio)+"'");
            username = doc.select(".avatar-big").attr("alt");
            log.trace("username : '"+username+"'");
            // r?cup?ration de l'avatar
            String avatarPath = doc.select(".avatar-big").attr("src");
            
            // fin
            // on r?cup?re la chaine de l'upload sans la traiter, avec la fl?che
            // et l'unit?
            upload = doc.select(".up").first().text();
            log.trace("upload :", upload);

            // idem pour le download
            download = doc.select(".down").first().text();
            log.trace("download :", download);

            // et enfin le nombre de mails, sous forme d'entier
            oldmails = (mails != null) ? mails : prefs.getInt("lastMails", 0);
            log.trace("mails (avant check) :", oldmails.toString());

            String rawMail = null;
            try {
                rawMail = doc.select(".mail  > strong").first().text();
                log.trace("MAILS .newpm", rawMail);
            } catch (Exception ex) {
                rawMail = doc.select(".newpm > strong").first().text();
                log.trace("MAILS .mail", rawMail);
            }

            mails = Integer.valueOf(rawMail);

            log.trace("mails (apr?s check) :", mails.toString());

            // On r?cup?re aussi le N? utilisateur pour les statistiques
            try{
				String[] tmp = doc.select(".ajax").attr("href").split("=");
           		usernumber = tmp[1];
			}catch(Exception e){
				
			}
            // t?l?chargements (24h)
            T411GetProfile t411GetProfile = new T411GetProfile(Params.T411_URL_GET_PROFILE, Params.GET, follower);
            T411Controler.requestProcess(t411GetProfile, follower);

            log.trace("avatarPath : '"+Params.T411_URL_INDEX + avatarPath+"'");
            if (avatarPath!=null && !avatarPath.isEmpty()){
            	T411GetAvatarFile t411GetAvatarFile = new T411GetAvatarFile(Params.T411_URL + avatarPath, Params.GET, follower);

            	t411GetAvatarFile.setFlagResponseFile(true);
            	T411Controler.requestProcess(t411GetAvatarFile,
											 follower);

            }

            // Calcul du restant possible t?l?chargeable avant d'atteindre la
            // limite de ratio fix?e
            double beforeLimit = 0;
            try {
                //double upData = getGigaOctetData(prefs.getString("lastUpload", "U 0 GB"));
                double upData = new BSize(upload).getInGB();
                //double dlData = getGigaOctetData(prefs.getString( "lastDownload", "D 0 GB"));
                double dlData = new BSize(download).getInGB();

                double lowRatio = Double.valueOf(prefs.getString("ratioMinimum", "1"));
                log.trace("Current Ratio :", String.valueOf(lowRatio));

                beforeLimit = (upData - dlData * lowRatio) / lowRatio;
                log.trace("beforeLimit :", String.valueOf(beforeLimit));
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("RATIORATIO", ex.toString());
            }
            String GoLeft = null;

            // Calcul de l'upload e faire avant d'atteindre la limite de ratio
            double toLimit = 0;
            try {
                //double upData = getGigaOctetData(prefs.getString("lastUpload", "U 0 GB"));
                //double dlData = getGigaOctetData(prefs.getString("lastDownload", "D 0 GB"));
                double upData = new BSize(upload).getInGB();
                double dlData = new BSize(download).getInGB();

                double curRatio = upData / dlData;
                log.trace("Current Ratio :", String.valueOf(curRatio));

                double targetRatio = Double.valueOf(prefs.getString("ratioCible", "1"));
                log.trace("Target Ratio :", String.valueOf(targetRatio));

                toLimit = (targetRatio * upData / curRatio) - upData;
                log.trace("toRatio :", String.valueOf(toLimit));
            } catch (Exception ex) {
            }

            String UpLeft = null;

            // Prise en compte des quantit?s restantes en Tera-octets
            GoLeft = (beforeLimit > 500) ?
                    String.format("%.2f", beforeLimit / 1024) + " TB" :
                    String.format("%.2f", beforeLimit) + " GB";

            UpLeft = (toLimit > 500) ?
                    String.format("%.2f", toLimit / 1024) + " TB" :
                    String.format("%.2f", toLimit) + " GB";

            log.trace("left2DL : ", UpLeft);
            // on stocke tout ce petit monde (si non nul) dans les pr?f?rences
            Editor editor = prefs.edit();

            editor.putString("GoLeft", (beforeLimit > 0) ? GoLeft : "0.00 GB");
            editor.putString("UpLeft", (toLimit > 0) ? UpLeft : "0.00 GB");

            if (mails != null)
                editor.putInt("lastMails", mails);
            if (upload != null)
                editor.putString("lastUpload", upload);
            if (download != null)
                editor.putString("lastDownload", download);
            if (ratio != Double.NaN)
                editor.putString("lastRatio", String.valueOf(ratio));
            editor.putString("usernumber", usernumber);

            if (mails < prefs.getInt("lastMails", 0))
                editor.putBoolean("mailsNeedRefresh", false);


            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();

            editor.putString("lastDate", today.format("%d/%m/%Y %k:%M:%S"));
            editor.putString("lastUsername", username);

            editor.commit();

            //updateMails();

            log.trace("t411 Error :", conError);
            log.trace("INFOS T411 :", "Mails (" + String.valueOf(mails) + ") " + upload + " " + download + " " + String.valueOf(ratio));

            
        } catch (Exception ex) {
        	log.debug("Une erreur est survenue lors de la récupération des informations\n"+ex.getMessage());
        }
        return "Informations T411";
    }



	
	public static T411Torrent t411GetTorrent(T411GetTorrent t411GetTorrent, AbstractActivity follower){
		String html_filelist = "";
	    String torrent_NFO ="";
	    String torrent_URL = t411GetTorrent.getUrl()+t411GetTorrent.getParamsString();
        String torrent_Name = t411GetTorrent.getName();
        String torrent_ID = t411GetTorrent.getId();
        Integer icon = t411GetTorrent.getIcon();
        String tdt_seeders="";
        String tdt_leechers="";
        String tdt_note="";
        String tdt_votes="";
        String tdt_complets="";
        String tdt_taille="";
        double note = 0;
        String hadopi = "";
        String qualite = "";
        String tduploader = t411GetTorrent.getUploader();
       

        String prez = "<meta name=\"viewport\" content=\"width=320; user-scalable=no\" />Erreur lors de la récupération des données...";
        int nbHadopi = 0;
        log.trace("Detail (string) du torrent à analyser :\n-----------------\n"+ t411GetTorrent.getResponse() +"\n---------------");
		
		String html=t411GetTorrent.getResponse();

		Document doc = Jsoup.parse(html);
	
		log.trace("Detail du torrent à analyser :\n-----------------\n"+doc.html()+"\n---------------");
		try{ html_filelist = doc.select(".accordion div").get(1).outerHtml(); }catch(Exception e){}
		try{ tduploader = doc.select(".profile").first().text(); }catch(Exception e){}
		Element comments = null;
		Element commentsA = null;
		/*
		 try {
        	 */
			 //TODO
			comments = doc.select(".comment").last();
			commentsA = comments;
		 //}catch(Exception e){}
		try{ qualite = "<center><span class='qualite'>" + doc.select(".terms-type-7").first().text() + "</span></center>"; }catch(Exception e){}
         

		if (prefs.getBoolean("hadopi", false)) {
        	 nbHadopi = commentsA.text().split("[Hh][Aa][Dd][Oo][Pp][Ii]").length - 1;
             if (nbHadopi > 0) {
                 hadopi = "<div onclick=\"this.style.display = 'none';\" style='position: fixed; top: 0px; right: 0px; left: 0px; background: red; opacity: 0.666; color: white; padding: 10px 7px 13px 7px; border-bottom: 1px solid darkred;'><img src='file:///android_asset/picts/hadopi_red.png' style='vertical-align: bottom;' /> <b>Hadopi</b> <small style='font-size: 0.5em;'>( mentionnée " + nbHadopi + " fois dans les commentaires de cette page)</small></div>";
             }
         }
         Elements objects;
         
		 try {
        	 
//TODO
             
             String customCSS = "<meta name=\"viewport\" content=\"width=320; user-scalable=no\" /><style>body {width: 100%; overflow: none; margin: 0px; padding: 0px;} * {font-size: 1em; text-wrap: unrestricted; word-wrap:break-word;} h1,h2,h3,h4 {font-size: 1.5em;} img, * {max-width: 360px; max-width: 100%;} .up {color: green;} .down {color: red;} .data {font-weight: normal; color: grey; font-size: 0.7em;} .qualite {background: #008A00; color: white; padding: 4px 20px 4px 20px; margin-top: 50px; font-weight: bold; border: 1px solid #007700; border-radius: 25px;} .verify{position: absolute; top: 32px; right: 6px; width:128px; height: 128px; background: url('file:///android_asset/picts/verify.png')}</style>";
             prez = customCSS + "<body>" + hadopi + "<br/>" + qualite + doc.select(".description").first().html() + "<br/><table width=100%>";// + comments+"</body></html>";
             torrent_NFO = doc.select("pre").first().text();

             prez += "<img src=\"file:///android_asset/picts/top.png\" onclick=\"scroll(0,0);\" style='z-index: 99999; position: fixed; top: 2px; right: 2px;' />";
             prez += "<img src=\"file:///android_asset/picts/bottom.png\" onclick=\"scroll(0,document.body.scrollHeight);\" style='z-index: 99999; position: fixed; bottom: 2px; right: 2px;' />";

        	 
             //commentaires
             objects = comments.select("tr");

             for (Element object : objects) {
                 String cusername = object.select("th").first().select("a").first().text();

                 String colorPseudo = "darkblue";
                 String arrowPict = "arrow.png";
                 String bubbleStyle = "border: 1px solid #dfdfdf; padding: 3px; border-radius: 5px; background: #f6f6f6; font-size: 0.8em";

                 if (cusername.equals(tduploader) && !cusername.equals("")) {
                     colorPseudo = "darkgrey";
                     cusername = cusername + " (uploader)";
                     arrowPict = "arrowBlack.png";
                     bubbleStyle = "border: 1px solid #000000; padding: 3px; border-radius: 5px; background: #303030; color: #EFEFEF; font-size: 0.8em";
                 }

                 String comm_username = "<b style='color: " + colorPseudo + ";'>" + cusername + "</b>";
                 String comm_avatar = "<img width=50 src=\"http://www.t411.me/" + object.select("th").first().select("img.avatar").first().attr("src") + "\" />";
                 String comm_up = object.select("th").first().select("span").get(1).outerHtml();
                 String comm_down = object.select("th").first().select("span").get(2).outerHtml();
                 String comm_ratio = object.select("th").first().select("span").get(3).outerHtml();

                 String comm_comm = "<img src=\'file:///android_asset/picts/" + arrowPict + "\' width=10 style='position: relative; left: -13px; top: 3px;' />";
                 comm_comm += object.select("td").first().select("p").first().html();

                 String comm_date = "<div style='text-align: right;'>" + object.select("td").first().select("div").first().html() + "</div>";


                 try {
                     String comment = ""
                             + "<tr><td colspan=2><br/>" + comm_username + "</td></tr>"
                             + "<tr valign='top' style='margin-top: -5px;'>"
                             + "<td style='font-size: 0.5em;'>" + comm_avatar + "<br/>" + comm_up + "<br/>" + comm_down + "<br/>" + comm_ratio + "<br/></td>"
                             + "<td><div style='" + bubbleStyle + " word-wrap:break-word; overflow-wrap: break-word; max-width: 300px; font-size: 0.8em;'>" + comm_comm + "<br/>" + comm_date + "</div></td>"
                             + "</tr>";
                     prez += comment;

                 } catch (Exception e) {
                     e.printStackTrace();
                 }

             }
         } catch (Exception e) {
             e.printStackTrace();
         }             
		 //TODO
		 prez += "</table></body></html>";

         prez = prez.replaceAll("_____", "");
         try {
        	 if (comments.select("tr").size() > 0)
        		 prez += "<a href=\"" + torrent_URL.replace("/torrents/torrents", "/torrents") + "\"><center><br/>La suite sur t411.me...<br/><br/></center></a>";
         } catch (Exception e) {}
        	 
         try {
         //vidéos youtube
         objects = doc.select("object");
         for (Element object : objects) {
             try {
                 String youtube_link = object.select("embed").first().attr("src");
                 prez = prez.replace(object.outerHtml(), "<a href=\"" + youtube_link + "\"><img src=\"file:///android_asset/picts/yt_play_vid.png\"/></a>");
             } catch (Exception e) {
                 prez = prez.replace(object.outerHtml(), "");
             }
         }
         } catch (Exception e) {
             e.printStackTrace();
             Log.e("Youtube", "Error");
         }

         //vidéos youtube 2

         objects = doc.select("iframe[src~=youtube]");
         for (Element object : objects) {
             Log.e("iframe", object.toString());
             try {
                 String youtube_src = object.attr("src");

                 //String[] youtube_array = youtube_src.split("/");
                 //String youtube_id = youtube_array[youtube_array.length-1];
                 String youtube_id = youtube_src.substring(youtube_src.lastIndexOf("/")+1);
                 Log.e(youtube_src, youtube_id);

                 String youtube_thumb = "http://img.youtube.com/vi/"+youtube_id+"/0.jpg";
                 String youtube_link = "http://www.youtube.com/watch?v="+youtube_id;
                 //file:///android_asset/picts/yt_play_vid.png
                 prez = prez.replace(object.outerHtml(),
                         "<a href=\"" + youtube_link + "\" style='position: relative;'>" +
                                 "<span style='position: absolute; bottom: 20px; right: 0px; color: white; background: red; border-top-left-radius: 6px; border-bottom-left-radius: 6px;  padding: 6px 24px 6px 6px;'> ▶  Voir sur youtube</span>"+
                                 "<img src=\""+youtube_thumb+"\"/>" +
                                 "</a>");
             } catch (Exception e) {
                 e.printStackTrace();
                 prez = prez.replace(object.outerHtml(), "");
             }
         }

         //liens
         objects = doc.select("a");
         for (Element object : objects) {
             try {
                 prez = prez.replace(object.outerHtml(), object.text());
             } catch (Exception e) {
                 e.printStackTrace();
             }

         }

         prez = prez.replaceAll("src=\"/", "src=\"http://www.t411.me/");
         
         try {tdt_seeders = doc.select(".details table tr td.up").first().text();} catch (Exception e) {}
         try {tdt_leechers = doc.select(".details table tr td.down").first().text();} catch (Exception e) {}
         try {tdt_note = doc.select("div.accordion div table tr").get(8).select("td").first().text().split(" ", 2)[0];} catch (Exception e) {}
         try {note = Double.valueOf(tdt_note.split("/")[0].replace(",", "."));} catch (Exception e) {}
         try {tdt_votes = doc.select("div.accordion div table tr").get(8).select("td").first().text().split(" ", 2)[1];} catch (Exception e) {}
         try {tdt_complets = doc.select(".details table tr td.down").first().parent().select("td").last().text();} catch (Exception e) {}
         try {tdt_taille = doc.select("div.accordion table tr").get(3).select("td").first().text();} catch (Exception e) {}

         if (icon==null){icon=0;}//FIXEME TODO BIG T411 ?
         T411Torrent t411Torrent = new T411Torrent(html_filelist, torrent_NFO, torrent_URL, 
        		 torrent_Name, torrent_ID, tdt_seeders, tdt_leechers, tdt_note, 
        		 tdt_votes, tdt_complets, tdt_taille, note, prez, tduploader,
        		 nbHadopi, icon);
 		follower.sendMessage(Params.MSG_CNF, "Fiche du Torrent");
         return t411Torrent;
	}
	

	public static T411Torrrents t411GetTorrents(T411Search t411Search, AbstractActivity follower){
				

		T411Torrrents t411Torrents = new T411Torrrents();
		listItem = new ArrayList<HashMap<String, String>>();
		t411Torrents.setMap(listItem);
		String html=t411Search.getResponse();//"";
		
		Document doc = Jsoup.parse(html);
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		int base = t411Search.getUrl().equals(Params.T411_URL_BOOKMARKS) ? 1 : 0;

		Element elmtPrev = doc.select(".pagebar a").first();
        strPrev = null;
        if (elmtPrev!=null && elmtPrev.hasAttr("rel")) {
            strPrev = elmtPrev.attr("href").substring(elmtPrev.attr("href").lastIndexOf("=") + 1);
        }

        Element elmtNext = doc.select(".pagebar a").last();
        strNext = null;
        if (elmtNext!=null && elmtNext.hasAttr("rel")) {
            strNext = elmtNext.attr("href").substring(elmtNext.attr("href").lastIndexOf("=") + 1);
        }
        t411Torrents.setStrNext(strNext);
        t411Torrents.setStrPrev(strPrev);
		
        for (Element table : doc.select("table.results tbody")) {
            for (Element row : table.select("tr")) {
                Elements tds = row.select("td");
				map = new HashMap<String, String>();
                if (tds!=null){
                	try{
//	                	String catCode = tds.get(base + 0).select(".category img").first().attr("class").replace("cat-", "");
//	                	
	                	String catCode = tds.get(base + 0).select("a").first().attr("href").split("=", 2)[1];
	                	//log.info("Category code : '"+catCode+"'");
	                    map.put("icon", String.valueOf(new CategoryIcon(catCode).getIcon()));
	                    map.put("cat", catCode);
                    	//map.put("nomComplet", tds.get(base + 1).select("a").first().attr("title").toString());
						map.put("nomComplet", tds.get(base + 1).select("a").first().text());// correctif 12/11/2014
                    	map.put("ID", tds.get(base + 2).select("a").attr("href").split("=")[1]);
                    	map.put("age", tds.get(base + 4).text());
                    	map.put("taille", new BSize(tds.get(base + 5).text()).convert());
                    	map.put("avis", tds.get(base + 3).text());
                    	map.put("seeders", tds.get(base + 7).text());
                    	map.put("leechers", tds.get(base + 8).text());
                    	map.put("uploader", tds.get(base + 1).select("dd > a.profile").text());
                    	map.put("completed", tds.get(base + 6).text());

                    	String tSize = tds.get(base + 5).text();
					
                    	double estimatedDl = new BSize(prefs.getString("lastDownload", "? 0.00 GB")).getInMB() + new BSize(tSize).getInMB();
                    	String estimatedRatio = String.format("%.2f", (new BSize(prefs.getString("lastUpload", "? 0.00 GB")).getInMB() / estimatedDl) - 0.01);

                    	map.put("ratio", estimatedRatio);
                    	map.put("ratioBase", String.format("%.2f", Float.valueOf(prefs.getString("lastRatio", ""))));
					
					}catch(Throwable thr){
						follower.sendMessage(Params.MSG_ERR, "Le site t411 ne semble pas répondre correctement\nVeuillez actualiser, ou faire une nouvelle recherche.");
					}//FIXME TODO
                    listItem.add(map);
                }
            }
        }
//		follower.sendMessage(Params.MSG_CNF, "Liste des Torrents");
        return t411Torrents;
	}
	public static String t411GetTorrentFile(T411GetTorrentFile t411GetTorrentFile, AbstractActivity follower){
		String reponse="Le fichier ("+t411GetTorrentFile.getId()+") "+t411GetTorrentFile.getName()+"n'a pas été téléchargé !";
				
		byte[] byteOS = t411GetTorrentFile.getResponseFile();
		boolean isErreur = false;
		if (byteOS==null){
			reponse="Le fichier ("+t411GetTorrentFile.getId()+") "+t411GetTorrentFile.getName()+"n'a pas été téléchargé !\nErreur inconnue";
			isErreur = true;
		}else{
			File fileDir = new File(Params.T411_DIR_TORRENT);
	        fileDir.mkdirs();
	        String filename = t411GetTorrentFile.getName().replaceAll("[\\s\\p{Punct}]","_").replaceAll("\\W","")+ ".torrent";//.replaceAll("/", "-").replaceAll(" ", "_").replaceAll("'", "-").replaceAll("&", "-") + ".torrent";//.replaceAll("[","-").replaceAll("]","-")
			File file = new File(Params.T411_DIR_TORRENT, filename);
	        file.setWritable(true, false);
	        try { file.createNewFile(); } catch (Exception e) {e.printStackTrace();}
			FileOutputStream fo;
			try {
				fo = new FileOutputStream(file);
		        fo.write(byteOS);
		        fo.close();
		        reponse = "Le fichier ("+t411GetTorrentFile.getId()+") "+filename+" est envoyé à la freebox !";;
		        if (Params.FBX_POST_ADD==null){
					FreeboxControler.checkGlobalPrefs(follower);
		        	Params.reinitFreebox();
		        }
		        

				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(follower);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("add_dwl_file", new String(byteOS,"iso-8859-1"));
				editor.putString("add_dwl_filename", filename);
				editor.apply();
				FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_ADD_FILE, follower);
		        HttpConnect.getInstance().execute(fbxHttpRaster);
			} catch (FileNotFoundException e) {
				reponse="Le fichier ("+t411GetTorrentFile.getId()+") "+t411GetTorrentFile.getName()+"n'a pas été téléchargé !\nErreur FileNotFoundException : "+e.getMessage();
				e.printStackTrace();
				isErreur = true;
			} catch (IOException e) {
				reponse="Le fichier ("+t411GetTorrentFile.getId()+") "+t411GetTorrentFile.getName()+"n'a pas été téléchargé !\nErreur IOException : "+e.getMessage();
				e.printStackTrace();
				isErreur = true;
			}
		}
		if (isErreur){
			follower.sendMessage(Params.MSG_ERR, "Téléchargement du Torrent impossible");
//		}else{
//			follower.sendMessage(Params.MSG_IND, "Téléchargement du Torrent");
		}
		return reponse;
		
	}
	
	
	public static String t411GetAvatarFile(T411GetAvatarFile t411GetAvatarFile, AbstractActivity follower){
		String reponse="Le fichier du lien "+t411GetAvatarFile.getUrl()+" n'a pas été téléchargé !";
				
		byte[] byteOS = t411GetAvatarFile.getResponseFile();
		boolean isErreur = false;
		if (byteOS==null){
			reponse="Le fichier du lien "+t411GetAvatarFile.getUrl()+" n'a pas été téléchargé !\nErreur inconnue";
			isErreur = true;
		}else{

			String avatar = Base64.encodeBytes(byteOS);
			log.trace("avatar", avatar);
            Editor editor = prefs.edit();
            editor.putString("avatar", avatar);
            editor.commit();
		}
		if (isErreur){
			follower.sendMessage(Params.MSG_ERR, "Téléchargement de l'Avatar impossible");
//		}else{
//			follower.sendMessage(Params.MSG_IND, "Téléchargement du Torrent");
		}
		return reponse;
		
	}
	

	public static String t411GetProfile(T411GetProfile t411GetProfile, AbstractActivity follower){
		String reponse=t411GetProfile.getResponse();//"";
		boolean isErreur=false;
		try{
		Document doc = Jsoup.parse(reponse);
		
		
		String classe = "";
        String up24 = "";
        String dl24 = "";
        String titre = "";
        String val = "";
        String seedbox = "";
        log.info(".block > div > dl > dt : "+doc.select(".block > div > dl > dt"));
        for (int iterator = 0; iterator < doc.select(".block > div > dl > dt").size(); iterator++) {
            val = doc.select(".block > div > dl > dt").get(iterator).text();

            log.info("val : " + val);
            if (val.contains("Classe:"))
                classe = doc.select(".block > div > dl > dd").get(iterator).text();
            if (val.contains("Titre personnalis"))
                titre = doc.select(".block > div > dl > dd").get(iterator)
                        .text();
            if (val.contains("Total") && val.contains("(24h")
                    && val.contains("charg"))
                dl24 = doc.select(".block > div > dl > dd").get(iterator)
                        .text();
            if (val.contains("Total") && val.contains("(24h") && val.contains("Upload"))
                up24 = doc.select(".block > div > dl > dd").get(iterator).text();

            if (val.contains("Seedbox"))
                seedbox = doc.select(".block > div > dl > dd").get(iterator).text();
        }
        log.info("Classe : ", classe);
        log.info("Titre : ", titre);
        log.info("DT : "+up24);
        log.info("DD : "+ dl24);
        log.info("Seedbox : "+seedbox);

        Editor editor = prefs.edit();
        editor.putString("classe", classe);
        editor.putString("up24", up24);
        editor.putString("dl24", dl24);
        editor.putString("titre", titre);
        if (seedbox != null)
            editor.putBoolean("seedbox", seedbox.contains("ui") ? true : false);
        editor.commit();
		}catch(Throwable e){
			isErreur=true;
		}
		
		if (isErreur){
			follower.sendMessage(Params.MSG_ERR, "Téléchargement du profil impossible");
		}
		return reponse;
		
	}
}
