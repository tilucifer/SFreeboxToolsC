package fr.scaron.sfreeboxtools.contexte;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.scaron.sfreeboxtools.model.IPUtils;

public class Params {
	public static Logger log = LoggerFactory.getLogger(Params.class);

	public static String FREEBOX_IP;
	public static String FREEBOX_PORT;
	
	public static String T411_LOGIN;
	public static String T411_PASSWORD;
	
	public static String FBX_GET_LOGIN;
	public static String FBX_GET_VERSION;
	public static String FBX_GET_CONFIG;
	public static String FBX_POST_LOGIN;
	public static String FBX_POST_AUTHORIZE;
	public static String FBX_POST_GET_SESSION;
	public static String FBX_GET_DOWNLOADS;
	public static String FBX_POST_ADD;
	public static String FBX_POST_LOGOUT;
	
	

	
	public final static int FBX_REQ_VERSION = 10 ;
	public final static int FBX_REQ_AUTHORIZE = 11;
	public final static int FBX_REQ_WAIT_AUTHORIZE = 12;
	public final static int FBX_REQ_LOGIN = 13;
	public final static int FBX_REQ_SESSION = 14;
	public final static int FBX_REQ_DOWNLOADS = 15;
	public final static int FBX_REQ_ADD_URL = 16;
	public final static int FBX_REQ_ADD_FILE = 17;
	public final static int FBX_REQ_LOGOUT = 18;
	public final static int FBX_REQ_DOWNLOAD = 19;
	public final static int FBX_REQ_DOWNLOAD_FILES = 20;
	public final static int FBX_REQ_DOWNLOAD_TRACKERS = 21;
	public final static int FBX_REQ_CONFIG = 22;
	
	public static boolean LOGGED_T411;
	public static boolean T411_SERVER_IS_IP = false;
	public static boolean T411_TRACKER_IS_IP = false;
	public static String T411_TRACKER_IP = "t411.download"; //http://t411.download/passkey/announce
	public static String T411_DNS_COURT = "t411.xx";
	public static String T411_DNS_WWW = "www."+T411_DNS_COURT;
	public static String T411_URL = "http://"+T411_DNS_WWW;
	//public static String T411_URL = "http://www.t411.me:8080";
    //public static String T411_URL_LOGIN = T411_URL+"/users/login/?returnto=/users/profile/";
	public static String T411_PATH_LOGIN = "/users/login/?returnto=/users/login/";//"/users/auth/";
    public static String T411_URL_LOGIN = T411_URL+T411_PATH_LOGIN;//"/users/login/?returnto=/users/login/";
    public static String T411_URL_GET_PREZ = T411_URL+"/torrents/torrents/"; //+?id= ID
    public static String T411_URL_SEARCH = T411_URL+"/torrents/search/?search="; // + terms
    public static String T411_URL_BOOKMARKS = T411_URL+"/my/bookmarks/";
    public static String T411_URL_GET_TORRENT = T411_URL+"/torrents/download/"; //?=id+ ID
    public static String T411_URL_GET_PROFILE = T411_URL+"/users/profile/";
    public static String T411_URL_TOP100 = T411_URL+"/top/100/";
    public static String T411_URL_TOPTODAY = T411_URL+"/top/today/";
    public static String T411_URL_TOPWEEK = T411_URL+"/top/week/";
    public static String T411_URL_TOPMONTH = T411_URL+"/top/month/";
    public static final String T411_URL_SUGGEST = "http://api"+T411_DNS_COURT+"/suggest";//?term=tot
    //
    //reponse : 
    //	["the walking dead","the walking dead s04","walking dead","walking dead s04","the walking dead saison 4","the walking dead saison 3","the walking dead saison 2","walking","the walking dead s04e09","The Walking Dead"]

    public static final int T411_TOP100 = 1;
    public static final int T411_TOPTODAY = 2;
    public static final int T411_TOPWEEK = 3;
    public static final int T411_TOPMONTH = 4;

    public static String T411_URL_INDEX = T411_URL+"";
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36";;


    public static String UpdateFreq = "60";
    public static String timeoutDelay = "20";
    public static String T411_DIR_TORRENT = "/sdcard/Android/data/fr.scaron.sfreeboxtools/torrents";
	
	public static final int RET_OK = 0;
	public static final int RET_KO = 1;
	
	public static final int GET = 0;
	public static final int POST = 1;
	public static final int ADD = 2;
	public static final int PUT = 3;
	public static final int DELETE = 4;
	
	
	public static final int MSG_ERR = 0;
	public static final int MSG_CNF = 1;
	public static final int MSG_IND = 2;
	public static final int MSG_TOAST = 3;
	
	
	//Fbx Downloads error
	public static final String task_not_found	= "task_not_found";//No task was found with the given id
	public static final String invalid_operation	= "invalid_operation";	//Attempt to perform an invalid operation
	public static final String invalid_file	= "invalid_file";//	Error with the download file (invalid format ?)
	public static final String invalid_url	= "invalid_url";//	URL is invalid
	public static final String not_implemented	= "not_implemented";//	Method not implemented
	public static final String out_of_memory	= "out_of_memory";//	No more memory available to perform the requested action
	public static final String invalid_task_type	= "invalid_task_type";//	The task type is invalid
	public static final String hibernating	= "hibernating";//	The downloader is hibernating
	public static final String need_bt_stopped_done	= "need_bt_stopped_done";//	This action is only valid for Bittorrent task in stopped or done state
	public static final String bt_tracker_not_found	= "bt_tracker_not_found";//	Attempt to access an invalid tracker object
	public static final String too_many_tasks	= "too_many_tasks";//	Too many tasks
	public static final String invalid_priority	= "invalid_priority";//	Invalid priority
	public static final String internal_error	= "internal_error";//	Internal error
	
	//Fbx Download error
	public static final String none	= "none";//		No error
	public static final String internal	= "internal";//		Internal error
	public static final String disk_full	= "disk_full";//		The disk is full
	public static final String unknown	= "unknown";//		Unknown error
	public static final String parse_error	= "parse_error";//		Parse error
	public static final String http_301	= "http_301";//		HTTP 301 error
	public static final String http_400	= "http_400";//		HTTP 400 error
	public static final String http_401	= "http_401";//		 
	public static final String http_402	= "http_402";//		 
	public static final String http_403	= "http_403";//		 
	public static final String http_404	= "http_404";//		 
	public static final String http_405	= "http_405";//		 
	public static final String http_406	= "http_406";//		 
	public static final String http_407	= "http_407";//		 
	public static final String http_408	= "http_408";//		 
	public static final String http_409	= "http_409";//		 
	public static final String http_410	= "http_410";//		 
	public static final String http_411	= "http_411";//		 
	public static final String http_412	= "http_412";//		[ ... ]
	public static final String http_413	= "http_413";//		 
	public static final String http_414	= "http_414";//		 
	public static final String http_415	= "http_415";//		 
	public static final String http_416	= "http_416";//		 
	public static final String http_417	= "http_417";//		 
	public static final String http_422	= "http_422";//		 
	public static final String http_423	= "http_423";//		 
	public static final String http_424	= "http_424";//		 
	public static final String http_425	= "http_425";//		 
	public static final String http_426	= "http_426";//		 
	public static final String http_427	= "http_427";//		 
	public static final String http_428	= "http_428";//		 
	public static final String http_429	= "http_429";//		 
	public static final String http_430	= "http_430";//		 
	public static final String http_431	= "http_431";//		 
	public static final String http_4xx	= "http_4xx";//		Other 4xx HTTP errors
	public static final String http_500	= "http_500";//		HTTP 500 error
	public static final String http_501	= "http_501";//		 
	public static final String http_502	= "http_502";//		 
	public static final String http_503	= "http_503";//		 
	public static final String http_504	= "http_504";//		 
	public static final String http_505	= "http_505";//		 
	public static final String http_506	= "http_506";//		[ ... ]
	public static final String http_507	= "http_507";//		 
	public static final String http_508	= "http_508";//		 
	public static final String http_509	= "http_509";//		 
	public static final String http_510	= "http_510";//		 
	public static final String http_511	= "http_511";//		= "";//		 
	public static final String http_5xx	= "http_5xx";//		Other 5xx HTTP errors
	public static final String http_redirections_exceeded	= "http_redirections_exceeded";//		Too many HTTP redirections
	public static final String nzb_no_group	= "nzb_no_group";//		Cannot find the requested group on server
	public static final String nzb_not_found	= "nzb_not_found";//		Article not fount on the server
	public static final String nzb_invalid_crc	= "nzb_invalid_crc";//		Invalid article CRC
	public static final String nzb_invalid_size	= "nzb_invalid_size";//		Invalid article size
	public static final String nzb_invalid_filename	= "nzb_invalid_filename";//		Invalid filename
	public static final String nzb_open_failed	= "nzb_open_failed";//		Error opening
	public static final String nzb_write_failed	= "nzb_write_failed";//		Error writing
	public static final String nzb_missing_size	= "nzb_missing_size";//		Missing article size
	public static final String nzb_decode_error	= "nzb_decode_error";//		Article decoding error
	public static final String nzb_missing_segments	= "nzb_missing_segments";//		Missing article segments
	public static final String nzb_error	= "nzb_error";//		Other nzb error
	public static final String unknown_host	= "unknown_host";//		Unknown host
	public static final String timeout	= "timeout";//		Timeout
	public static final String bad_authentication	= "bad_authentication";//		Invalid credentials
	public static final String connection_refused	= "connection_refused";//		Remote host refused connection
	public static final String nzb_authentication_required	= "nzb_authentication_required";//		Nzb server need authentication
	public static final String bt_tracker_error	= "bt_tracker_error";//		Unable to announce on tracker
	public static final String bt_missing_files	= "bt_missing_files";//		Missing torrent files
	public static final String bt_file_error	= "bt_file_error";//		Error accessing torrent files
	
	public static final String auth_required	= "auth_required";//	Invalid session token, or not session token sent
	public static final String invalid_token	= "invalid_token";//	The app token you are trying to use is invalid or has been revoked
	public static final String pending_token	= "pending_token";//	The app token you are trying to use has not been validated by user yet
	public static final String insufficient_rights	= "insufficient_rights";//	Your app permissions does not allow accessing this API
	public static final String denied_from_external_ip	= "denied_from_external_ip";//	You are trying to get an app_token from a remote IP
	public static final String invalid_request	= "invalid_request";//	Your request is invalid
	public static final String ratelimited	= "ratelimited";//	Too many auth error have been made from your IP
	public static final String new_apps_denied	= "new_apps_denied";//	New application token request has been disabled
	public static final String apps_denied	= "apps_denied";//	API access from apps has been disabled
//	public static final String internal_error	= "";//	Internal error
	
	public static String FBX_SESSION_PWD = null;
	
	public static void reinitFreebox(){
		FBX_GET_LOGIN = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/login/";
		FBX_GET_VERSION = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api_version";
		FBX_POST_LOGIN = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/login/";
		FBX_POST_AUTHORIZE = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/login/authorize/";
		FBX_POST_GET_SESSION = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/login/session/";
		FBX_GET_DOWNLOADS = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/downloads/";
		FBX_POST_ADD = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/downloads/add";
		FBX_POST_LOGOUT = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/login/logout/";
		FBX_GET_CONFIG = "http://"+FREEBOX_IP+":"+FREEBOX_PORT+"/api/v1/connection/config/";
	}

	public static void reinitT411(){
		if (T411_SERVER_IS_IP){
			T411_DNS_WWW = T411_DNS_COURT;
		}else{
			T411_DNS_WWW = "www."+T411_DNS_COURT;
		}
		T411_URL = "http://"+T411_DNS_WWW;
		T411_URL_LOGIN = T411_URL+T411_PATH_LOGIN;//"/users/login/?returnto=/users/login/";
		T411_URL_GET_PREZ = T411_URL+"/torrents/torrents/"; //+?id= ID
		T411_URL_SEARCH = T411_URL+"/torrents/search/?search="; // + terms
		T411_URL_BOOKMARKS = T411_URL+"/my/bookmarks/";
		T411_URL_GET_TORRENT = T411_URL+"/torrents/download/"; //?=id+ ID
		T411_URL_GET_PROFILE = T411_URL+"/users/profile/";
		T411_URL_TOP100 = T411_URL+"/top/100/";
		T411_URL_TOPTODAY = T411_URL+"/top/today/";
		T411_URL_TOPWEEK = T411_URL+"/top/week/";
		T411_URL_TOPMONTH = T411_URL+"/top/month/";
	}
	
	public static String hmacSha1(String value, String key) {

		log.debug("Generation d'une clé hmacSha1 pour la valeur '"+value+"' avec la clé '"+key+"'");
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            //  Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static String getMessageAttente(final int pId){
		switch(pId){
		case FBX_REQ_AUTHORIZE :
			return "Demande d'association";
		case FBX_REQ_WAIT_AUTHORIZE :
			return "Attente de l'authorisation";
		case FBX_REQ_LOGIN :
			return "Identification";
		case FBX_REQ_SESSION :
			return "Ouverture de session";
		case FBX_REQ_ADD_URL :
			return "Ajout d'un téléchargement url";
		case FBX_REQ_ADD_FILE :
			return "Ajout d'un téléchargement fichier";
		case FBX_REQ_DOWNLOADS :
			return "Liste des téléchargements";
		case FBX_REQ_DOWNLOAD :
			return "Détail du téléchargement";
		case FBX_REQ_LOGOUT :
			return "Logout";
		case FBX_REQ_CONFIG :
			return "Informations freebox";
		default : 
			return "Opération invalide";
				
		}
	}
	
}
