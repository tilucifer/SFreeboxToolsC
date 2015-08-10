package fr.scaron.sfreeboxtools.control;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.DownloadFile;
import fr.scaron.sfreeboxtools.model.DownloadTracker;
import fr.scaron.sfreeboxtools.model.FbxAddFileDownload;
import fr.scaron.sfreeboxtools.model.FbxAddUrlDownload;
import fr.scaron.sfreeboxtools.model.FbxGetConfig;
import fr.scaron.sfreeboxtools.model.FbxGetDownload;
import fr.scaron.sfreeboxtools.model.FbxGetDownloadFiles;
import fr.scaron.sfreeboxtools.model.FbxGetDownloadTrackers;
import fr.scaron.sfreeboxtools.model.FbxGetDownloads;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;
import fr.scaron.sfreeboxtools.model.FbxInitAuthorized;
import fr.scaron.sfreeboxtools.model.FbxLogin;
import fr.scaron.sfreeboxtools.model.FbxLogout;
import fr.scaron.sfreeboxtools.model.FbxSession;
import fr.scaron.sfreeboxtools.model.FbxWaitAuthorized;
import fr.scaron.sfreeboxtools.model.FreeboxBox;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import fr.scaron.sfreeboxtools.model.TinyDB;

public class FreeboxControler {

	public static Logger log = LoggerFactory.getLogger(FreeboxControler.class);

	public static Object processResponse(final FbxHttpRaster fbxHttpRaster,
			Follower follower) {

		Context context = follower.getContext();
        TinyDB myDB = new TinyDB(context);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();

		Integer track_id = null;
		if (settings.contains("track_id")) {
			track_id = settings.getInt("track_id", -1);
		}
		String status = settings.getString("status", null);
		String app_token = settings.getString("app_token", null);
		String challenge = settings.getString("challenge", null);
		String logged_in = settings.getString("logged_in", null);
		String session_token = settings.getString("session_token", null);
		String password_sha = settings.getString("password_sha", null);
		String add_dwl_url = settings.getString("add_dwl_url", null);
		Integer add_dwl_urlid = null;
		if (settings.contains("add_dwl_urlid")) {
			add_dwl_urlid = settings.getInt("add_dwl_urlid", -1);
		}
		String add_dwl_file = settings.getString("add_dwl_file", null);
		String add_dwl_filename = settings.getString("add_dwl_filename", null);
		Integer add_dwl_fileid = null;
		if (settings.contains("add_dwl_fileid")) {
			add_dwl_fileid = settings.getInt("add_dwl_fileid", -1);
		}
		byte[] add_dwl_file_bytes = null;
		if (add_dwl_file != null) {
			try {
				add_dwl_file_bytes = add_dwl_file.getBytes("iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				add_dwl_file_bytes = add_dwl_file.getBytes();
				e.printStackTrace();
			}
		}
		Integer dwl_id = null;
		if (settings.contains("dwl_id")) {
			dwl_id = settings.getInt("dwl_id", -1);
		}

		log.trace("SharedPreferences processResponse: \n-------------\n"
				+ fbxHttpRaster.getClass().getName() + "\n" + "track_id : "
				+ track_id + "\n" + "app_token : " + app_token + "\n"
				+ "challenge : " + challenge + "\n" + "logged_in : "
				+ logged_in + "\n" + "session_token : " + session_token + "\n"
				+ "password_sha : " + password_sha + "\n" + "add_dwl_url : "
				+ add_dwl_url + "\n" + "add_dwl_urlid : " + add_dwl_urlid
				+ "\n" + "add_dwl_file : is null ? " + (add_dwl_file == null)
				+ "\n" + "add_dwl_filename : " + add_dwl_filename + "\n"
				+ "add_dwl_fileid : " + add_dwl_fileid +"\n" +
				 "dwl_id : "+dwl_id+"\n" +
				 "\n" + "-------------");

		HttpConnect connection = HttpConnect.getInstance();
		int id = -1;
		if (fbxHttpRaster instanceof FbxInitAuthorized) {
			id = Params.FBX_REQ_AUTHORIZE;
		} else if (fbxHttpRaster instanceof FbxWaitAuthorized) {
			id = Params.FBX_REQ_WAIT_AUTHORIZE;
		} else if (fbxHttpRaster instanceof FbxLogin) {
			id = Params.FBX_REQ_LOGIN;
		} else if (fbxHttpRaster instanceof FbxSession) {
			id = Params.FBX_REQ_SESSION;
		} else if (fbxHttpRaster instanceof FbxGetDownloads) {
			id = Params.FBX_REQ_DOWNLOADS;
		} else if (fbxHttpRaster instanceof FbxAddUrlDownload) {
			id = Params.FBX_REQ_ADD_URL;
		} else if (fbxHttpRaster instanceof FbxAddFileDownload) {
			id = Params.FBX_REQ_ADD_FILE;
		} else if (fbxHttpRaster instanceof FbxLogout) {
			id = Params.FBX_REQ_LOGOUT;
		} else if (fbxHttpRaster instanceof FbxGetDownload) {
			id = Params.FBX_REQ_DOWNLOAD;
		} else if (fbxHttpRaster instanceof FbxGetDownloadFiles){
			id = Params.FBX_REQ_DOWNLOAD_FILES;
		} else if (fbxHttpRaster instanceof FbxGetDownloadTrackers){
			id = Params.FBX_REQ_DOWNLOAD_TRACKERS;
		} else if (fbxHttpRaster instanceof FbxGetConfig){
			id = Params.FBX_REQ_CONFIG;
		}
		JSONObject json = null;
		switch (id) {
		case Params.FBX_REQ_AUTHORIZE:
			if (fbxHttpRaster.isFlagResponseError()) {
				return fbxHttpRaster.getResponse() + "\n(code : "
						+ fbxHttpRaster.getErrorCode() + ")";
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							app_token = result.getString("app_token");
							track_id = result.getInt("track_id");
							editor.putString("app_token", app_token);
							editor.putInt("track_id", track_id);
							editor.apply();

							// Next step Wait Init
							FbxWaitAuthorized fbxWaitAuthorized = (FbxWaitAuthorized) createRequest(
									Params.FBX_REQ_WAIT_AUTHORIZE, follower);
							if (fbxHttpRaster.getFinalRequestLabel() != null) {
								fbxWaitAuthorized
										.setFinalRequestLabel(fbxHttpRaster
												.getFinalRequestLabel());
							}
							follower.sendMessage(
									Params.MSG_IND,
									Params.getMessageAttente(fbxWaitAuthorized.requestLabel));
							connection.execute(fbxWaitAuthorized);
							return "Execution de la requette WaitAuthorize";
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "";
		case Params.FBX_REQ_WAIT_AUTHORIZE:
			if (fbxHttpRaster.isFlagResponseError()) {
				return fbxHttpRaster.getResponse() + "\n(code : "
						+ fbxHttpRaster.getErrorCode() + ")";
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							status = result.getString("status");
							challenge = result.getString("challenge");
							editor.putString("status", status);

							if ("granted".equals(status)) {
								editor.putString("challenge", challenge);
								password_sha = Params.hmacSha1(challenge,
										app_token);
								editor.putString("password_sha", password_sha);
								editor.apply();
								// Next step Session Init
								FbxSession fbxSession = (FbxSession) createRequest(
										Params.FBX_REQ_SESSION, follower);
								if (fbxHttpRaster.getFinalRequestLabel() != null) {
									fbxSession
											.setFinalRequestLabel(fbxHttpRaster
													.getFinalRequestLabel());
								}
								follower.sendMessage(
										Params.MSG_IND,
										Params.getMessageAttente(fbxSession.requestLabel));
								connection.execute(fbxSession);
								return "Execution de la requette Session";
							} else {
								connection.execute(fbxHttpRaster);
								return "On reste en attente de l'association";
							}
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
		case Params.FBX_REQ_LOGIN:
			if (fbxHttpRaster.isFlagResponseError()) {
				return fbxHttpRaster.getResponse() + "\n(code : "
						+ fbxHttpRaster.getErrorCode() + ")";
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							logged_in = result.getString("logged_in");
							if ("true".equals(logged_in)) {
								if (fbxHttpRaster.getFinalRequestLabel() != null) {
									log.debug("Vous êtes déjà authentifiés donc on passe directement à la requete finale");
									follower.sendMessage(Params.MSG_IND, Params
											.getMessageAttente(fbxHttpRaster
													.getFinalRequestLabel()));
									connection
											.execute((HttpRaster) createRequest(
													fbxHttpRaster
															.getFinalRequestLabel(),
													follower));
									return "Execution de la requette finale";
								}
								follower.sendMessage(Params.MSG_CNF,
										"Vous êtes déjà identifié");
								log.debug("Vous êtes déjà authentifiés mais aucune requete finale est définie");
								return "Fin des requetes";
							}
							delOldPrefs(follower);
							challenge = result.getString("challenge");
							password_sha = Params
									.hmacSha1(challenge, app_token);
							editor.putString("logged_in", "true");
							editor.putString("challenge", challenge);
							editor.putString("password_sha", password_sha);
							editor.apply();
							// Next step Session Init
							FbxSession fbxSession = (FbxSession) createRequest(
									Params.FBX_REQ_SESSION, follower);
							if (fbxHttpRaster.getFinalRequestLabel() != null) {
								fbxSession.setFinalRequestLabel(fbxHttpRaster
										.getFinalRequestLabel());
							}
							follower.sendMessage(Params.MSG_IND, Params
									.getMessageAttente(fbxSession.requestLabel));
							connection.execute(fbxSession);
							return "Execution de la requette Session";
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "";
		case Params.FBX_REQ_SESSION:
			if (fbxHttpRaster.isFlagResponseError()) {
				return fbxHttpRaster.getResponse() + "\n(code : "
						+ fbxHttpRaster.getErrorCode() + ")";
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							session_token = result.getString("session_token");
							challenge = result.getString("challenge");
							editor.putString("logged_in", "true");
							editor.putString("session_token", session_token);
							editor.putString("challenge", challenge);
							editor.apply();
							if (fbxHttpRaster.getFinalRequestLabel() != null) {
								follower.sendMessage(Params.MSG_IND, Params
										.getMessageAttente(fbxHttpRaster
												.getFinalRequestLabel()));
								connection.execute((HttpRaster) createRequest(
										fbxHttpRaster.getFinalRequestLabel(),
										follower));
								return "Execution de la requette finale";
							}
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "";
		case Params.FBX_REQ_DOWNLOADS:
			if (fbxHttpRaster.isFlagResponseError()) {
				if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
					FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
							Params.FBX_REQ_LOGIN, Params.FBX_REQ_DOWNLOADS,
							follower);
					delOldPrefs(follower);
					follower.sendMessage(Params.MSG_IND,
							Params.getMessageAttente(fbxlogin.requestLabel));
					connection.execute(fbxlogin);
					log.debug("Erreur d'authentification, on tente le logout");
					return null;//"Erreur d'authentification, on tente le logout";
				} else {
					return fbxHttpRaster.getResponse() + "\n(code : "
							+ fbxHttpRaster.getErrorCode() + ")";
				}
			}
			FbxGetDownloads fbxGetDownloads = (FbxGetDownloads) fbxHttpRaster;
			if (fbxGetDownloads.isFlagResponseJson()) {
				json = fbxGetDownloads.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONArray downloads = json.optJSONArray("result");
							if (downloads != null) {
								return mapDownloads(downloads);
							}
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "Un probleme est survenu lors de la récupération des téléchargements";
		case Params.FBX_REQ_ADD_FILE:
			if (fbxHttpRaster.isFlagResponseError()) {
				if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
					FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
							Params.FBX_REQ_LOGIN, Params.FBX_REQ_ADD_FILE,
							follower);
					delOldPrefs(follower);
					follower.sendMessage(Params.MSG_IND,
							Params.getMessageAttente(fbxlogin.requestLabel));

					connection.execute(fbxlogin);
					log.debug("Erreur d'authentification, on tente le logout");
					return null;//"Erreur d'authentification, on tente le logout";
				} else {
					return fbxHttpRaster.getResponse() + "\n(code : "
							+ fbxHttpRaster.getErrorCode() + ")";
				}
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							add_dwl_fileid = result.getInt("id");
							editor.putInt("add_dwl_fileid", add_dwl_fileid);
							editor.apply();
							return add_dwl_fileid;
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "Impossible d'ajouter le torrent Fichier";
		case Params.FBX_REQ_ADD_URL:
			if (fbxHttpRaster.isFlagResponseError()) {
				if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
					FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
							Params.FBX_REQ_LOGIN, Params.FBX_REQ_ADD_URL,
							follower);
					delOldPrefs(follower);
					follower.sendMessage(Params.MSG_IND,
							Params.getMessageAttente(fbxlogin.requestLabel));

					connection.execute(fbxlogin);
					log.debug("Erreur d'authentification, on tente le logout");
					return null;//"Erreur d'authentification, on tente le logout";
				} else {
					return fbxHttpRaster.getResponse() + "\n(code : "
							+ fbxHttpRaster.getErrorCode() + ")";
				}
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");
							add_dwl_urlid = result.getInt("id");
							editor.putInt("add_dwl_urlid", add_dwl_urlid);
							editor.apply();
							return add_dwl_urlid;
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							return msg + "\n(code : " + error_code + ")";
						}
					} catch (JSONException e) {
					}
				}
			}
			return "Impossible d'ajouter le torrent via Url";
		case Params.FBX_REQ_DOWNLOAD:

			if (fbxHttpRaster.isFlagResponseError()) {
				if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
					FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
							Params.FBX_REQ_LOGIN, Params.FBX_REQ_DOWNLOAD,
							follower);
					delOldPrefs(follower);
					follower.sendMessage(Params.MSG_IND,
							Params.getMessageAttente(fbxlogin.requestLabel));

					connection.execute(fbxlogin);
					log.debug("Erreur d'authentification, on tente le logout");
					return null;//"Erreur d'authentification, on tente le logout";
				} else {
					return fbxHttpRaster.getResponse() + "\n(code : "
							+ fbxHttpRaster.getErrorCode() + ")";
				}
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						boolean success = json.getBoolean("success");
						if (success) {
							JSONObject result = json.getJSONObject("result");

							return new Download(result);
						}
					} catch (JSONException e) {
					}
				}
			}
			case Params.FBX_REQ_DOWNLOAD_FILES:
				if (fbxHttpRaster.isFlagResponseError()) {
					if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
						FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
								Params.FBX_REQ_LOGIN, Params.FBX_REQ_DOWNLOAD_FILES,
								follower);
						delOldPrefs(follower);
						follower.sendMessage(Params.MSG_IND,
								Params.getMessageAttente(fbxlogin.requestLabel));

						connection.execute(fbxlogin);
						log.debug("Erreur d'authentification pour FBX_REQ_DOWNLOAD_FILES, on tente le logout");
						return null;//"Erreur d'authentification pour FBX_REQ_DOWNLOAD_FILES, on tente le logout";
					} else {
						log.debug("Reponse erreur pour FBX_REQ_DOWNLOAD_FILES (code : "
								+ fbxHttpRaster.getErrorCode() + ")\n"+fbxHttpRaster.getResponse());
						return fbxHttpRaster.getResponse() + "\n(code : "
								+ fbxHttpRaster.getErrorCode() + ")";
					}
				}
				FbxGetDownloadFiles fbxGetDownloadFiles = (FbxGetDownloadFiles) fbxHttpRaster;
				if (fbxGetDownloadFiles.isFlagResponseJson()) {
					json = fbxGetDownloadFiles.getResponseJson();
					log.debug("Reponse Json pour FBX_REQ_DOWNLOAD_FILES\n"+json);
					if (json != null && json.has("success")) {
						try {
							boolean success = json.getBoolean("success");
							if (success) {
								JSONArray downloadFiles = json.optJSONArray("result");
								if (downloadFiles != null) {
									return DownloadFile.getDownloadFiles(downloadFiles);
								}
							} else {
								String msg = json.getString("msg");
								String error_code = json.getString("error_code");
								fbxHttpRaster.setErrorCode(error_code);
								fbxHttpRaster.setResponse(msg);
								fbxHttpRaster.setFlagResponseError(true);
								fbxHttpRaster.setFlagResponseFile(false);
								fbxHttpRaster.setFlagResponseJson(false);
								return msg + "\n(code : " + error_code + ")";
							}
						} catch (JSONException e) {
						}
					}
				}
				log.debug("Reponse pour FBX_REQ_DOWNLOAD_FILES\n"+fbxHttpRaster.getResponse());
				return "Un probleme est survenu lors de la récupération des téléchargements";

			case Params.FBX_REQ_DOWNLOAD_TRACKERS:
				if (fbxHttpRaster.isFlagResponseError()) {
					if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
						FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
								Params.FBX_REQ_LOGIN, Params.FBX_REQ_DOWNLOAD_TRACKERS,
								follower);
						delOldPrefs(follower);
						follower.sendMessage(Params.MSG_IND,
								Params.getMessageAttente(fbxlogin.requestLabel));

						connection.execute(fbxlogin);
						log.debug("Erreur d'authentification pour FBX_REQ_DOWNLOAD_TRACKERS, on tente le logout");
					} else {
						log.debug("Reponse erreur pour FBX_REQ_DOWNLOAD_TRACKERS (code : "
								+ fbxHttpRaster.getErrorCode() + ")\n"+fbxHttpRaster.getResponse());
						return fbxHttpRaster.getResponse() + "\n(code : "
								+ fbxHttpRaster.getErrorCode() + ")";
					}
				}
				FbxGetDownloadTrackers fbxGetDownloadTrackers = (FbxGetDownloadTrackers) fbxHttpRaster;
				if (fbxGetDownloadTrackers.isFlagResponseJson()) {
					json = fbxGetDownloadTrackers.getResponseJson();
					log.debug("Reponse Json pour FBX_REQ_DOWNLOAD_TRACKERS\n"+json);
					if (json != null && json.has("success")) {
						try {
							boolean success = json.getBoolean("success");
							if (success) {
								JSONArray downloadTrackers = json.optJSONArray("result");
								if (downloadTrackers != null) {
									return DownloadTracker.getDownloadTrackers(downloadTrackers);
								}
							} else {
								String msg = json.getString("msg");
								String error_code = json.getString("error_code");
								fbxHttpRaster.setErrorCode(error_code);
								fbxHttpRaster.setResponse(msg);
								fbxHttpRaster.setFlagResponseError(true);
								fbxHttpRaster.setFlagResponseFile(false);
								fbxHttpRaster.setFlagResponseJson(false);
								return msg + "\n(code : " + error_code + ")";
							}
						} catch (JSONException e) {
						}
					}
				}
				log.debug("Reponse pour FBX_REQ_DOWNLOAD_FILES\n"+fbxHttpRaster.getResponse());
				return "Un probleme est survenu lors de la récupération des téléchargements";

			case Params.FBX_REQ_CONFIG:
				if (fbxHttpRaster.isFlagResponseError()) {
					if (Params.http_403.equals(fbxHttpRaster.getErrorCode())) {
						FbxHttpRaster fbxlogin = (FbxHttpRaster) createRequest(
								Params.FBX_REQ_LOGIN, Params.FBX_REQ_CONFIG,
								follower);
						delOldPrefs(follower);
						follower.sendMessage(Params.MSG_IND,
								Params.getMessageAttente(fbxlogin.requestLabel));
						connection.execute(fbxlogin);
						log.debug("Erreur d'authentification, on tente le logout");
						return null;//"Erreur d'authentification, on tente le logout";
					} else {
						return fbxHttpRaster.getResponse() + "\n(code : "
								+ fbxHttpRaster.getErrorCode() + ")";
					}
				}
				FbxGetConfig fbxGetConfig = (FbxGetConfig) fbxHttpRaster;
				if (fbxGetConfig.isFlagResponseJson()) {
					json = fbxGetConfig.getResponseJson();
					if (json != null && json.has("success")) {
						try {
							boolean success = json.getBoolean("success");
							if (success) {
								JSONObject config = json.optJSONObject("result");
                                log.debug("Config json : "+config.toString());
                                Log.d(FreeboxControler.class.getName(), "Config json : " + config.toString());
								if (config != null) {
									FreeboxBox freeboxBox = new FreeboxBox();
									freeboxBox.setIp_public(config.getString("remote_access_ip"));
									freeboxBox.setPort(""+config.getInt("remote_access_port"));//TODO FINIR TEST JU
									

									myDB.putString("pref_ip_public_freebox", freeboxBox.getIp_public());
									myDB.putString("pref_port_public_freebox", freeboxBox.getPort());
                                    log.debug("Add Freebox " + freeboxBox.getIp_public() + ":" + freeboxBox.getPort());
									return freeboxBox;
								}else{

									return "Impossible d'accéder aux informations freebox\n(Réponse vide)";
								}
							} else {
								String msg = json.getString("msg");
								String error_code = json.getString("error_code");
								fbxHttpRaster.setErrorCode(error_code);
								fbxHttpRaster.setResponse(msg);
								fbxHttpRaster.setFlagResponseError(true);
								fbxHttpRaster.setFlagResponseFile(false);
								fbxHttpRaster.setFlagResponseJson(false);
								return msg + "\n(code : " + error_code + ")";
							}
						} catch (JSONException e) {
						}
					}
				}
				return "Un probleme est survenu lors de la récupération de la configuration Freebox";
		case Params.FBX_REQ_LOGOUT:
			if (fbxHttpRaster.isFlagResponseError()) {
				return false;
			}
			if (fbxHttpRaster.isFlagResponseJson()) {
				json = fbxHttpRaster.getResponseJson();
				if (json != null && json.has("success")) {
					try {
						Boolean success = json.getBoolean("success");
						if (success) {
							delOldPrefs(follower);
							log.debug("Successfully logout");
							return Boolean.valueOf(true);
						} else {
							String msg = json.getString("msg");
							String error_code = json.getString("error_code");
							fbxHttpRaster.setErrorCode(error_code);
							fbxHttpRaster.setResponse(msg);
							fbxHttpRaster.setFlagResponseError(true);
							fbxHttpRaster.setFlagResponseFile(false);
							fbxHttpRaster.setFlagResponseJson(false);
							log.debug("Erreur rencontrée lors du logout : "
									+ msg + " (code : " + error_code + ")");
							delOldPrefs(follower);
						}
						return success;
					} catch (JSONException e) {
					}
				}
			}
			return Boolean.valueOf(false);
		default:
			return fbxHttpRaster.getResponse() + "\n(requête inconnue)";
		}

	}

	public static Object createRequest(final int pId, final int pFinalId,
			Follower follower) {
		Object created = createRequest(pId, follower);
		if (created instanceof FbxHttpRaster) {
			FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) created;
			fbxHttpRaster.setFinalRequestLabel(pFinalId);
			return fbxHttpRaster;
		}
		return null;
	}

	public static Object createRequest(final int pId, Follower follower) {
		log.trace("Demande de création de la requete : " + pId);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(follower.getContext());
		Integer track_id = null;
		if (settings.contains("track_id")) {
			track_id = settings.getInt("track_id", -1);
		}
		String app_token = settings.getString("app_token", null);
		String challenge = settings.getString("challenge", null);
		String logged_in = settings.getString("logged_in", null);
		String session_token = settings.getString("session_token", null);
		String password_sha = settings.getString("password_sha", null);
		String add_dwl_url = settings.getString("add_dwl_url", null);
		Integer add_dwl_urlid = null;
		if (settings.contains("add_dwl_urlid")) {
			add_dwl_urlid = settings.getInt("add_dwl_urlid", -1);
		}
		String add_dwl_file = settings.getString("add_dwl_file", null);
		String add_dwl_filename = settings.getString("add_dwl_filename", null);
		Integer add_dwl_fileid = null;
		if (settings.contains("add_dwl_fileid")) {
			add_dwl_fileid = settings.getInt("add_dwl_fileid", -1);
		}
		Integer dwl_id = null;
		if (settings.contains("dwl_id")) {
			dwl_id = settings.getInt("dwl_id", -1);
		}
		byte[] add_dwl_file_bytes = null;
		if (add_dwl_file != null) {
			try {
				add_dwl_file_bytes = add_dwl_file.getBytes("iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				add_dwl_file_bytes = add_dwl_file.getBytes();
				e.printStackTrace();
			}
		}

		log.trace("SharedPreferences createRequest\n-------------\n"+
				 Params.getMessageAttente(pId)+"\n" + 
				"track_id : "+track_id+"\n" +
				 "app_token : "+app_token+"\n" + "challenge : "+challenge+"\n" +
				 "logged_in : "+logged_in+"\n" + "session_token : "+session_token+"\n" +
				 "password_sha : "+password_sha+"\n" + "add_dwl_url : "+add_dwl_url+"\n" +
				 "add_dwl_urlid : "+add_dwl_urlid+"\n" +
				 "add_dwl_file : is null ? "+(add_dwl_file==null)+"\n" +
				 "add_dwl_filename : "+add_dwl_filename+"\n" +
				 "add_dwl_fileid : "+add_dwl_fileid+"\n" +
				 "dwl_id : "+dwl_id+"\n" +
				 "\n" + "-------------");

		switch (pId) {
		case Params.FBX_REQ_AUTHORIZE:
			log.debug("Demande de création de la requete FBX_REQ_AUTHORIZE");
			FbxInitAuthorized fbxInitAuthorized = new FbxInitAuthorized(
					Params.FBX_POST_AUTHORIZE, Params.POST, follower);
			fbxInitAuthorized.requestLabel = Params.FBX_REQ_AUTHORIZE;
			return fbxInitAuthorized;
		case Params.FBX_REQ_WAIT_AUTHORIZE:
			log.debug("Demande de création de la requete FBX_REQ_WAIT_AUTHORIZE");
			FbxWaitAuthorized httpRaster = new FbxWaitAuthorized(
					Params.FBX_POST_AUTHORIZE + String.valueOf(track_id),
					Params.GET, follower);
			httpRaster.setApp_token(app_token);
			httpRaster.setTrack_id(track_id);
			httpRaster.requestLabel = Params.FBX_REQ_WAIT_AUTHORIZE;
			return httpRaster;
		case Params.FBX_REQ_LOGIN:
			log.debug("Demande de création de la requete FBX_REQ_LOGIN");
			FbxLogin fbxLogin = new FbxLogin(Params.FBX_GET_LOGIN, Params.GET,
					follower);
			fbxLogin.requestLabel = Params.FBX_REQ_LOGIN;
			return fbxLogin;
		case Params.FBX_REQ_SESSION:
			log.debug("Demande de création de la requete FBX_REQ_SESSION");
			FbxSession fbxSession = new FbxSession(Params.FBX_POST_GET_SESSION,
					Params.POST, follower);
			fbxSession.setPasswordsha(password_sha);
			fbxSession.requestLabel = Params.FBX_REQ_SESSION;
			return fbxSession;
		case Params.FBX_REQ_DOWNLOADS:
			log.debug("Demande de création de la requete FBX_REQ_DOWNLOADS");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_DOWNLOADS, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_DOWNLOADS, follower);
			}
			FbxGetDownloads fbxGetDownloads = new FbxGetDownloads(
					Params.FBX_GET_DOWNLOADS, Params.GET, follower);
			fbxGetDownloads.addHeader("X-Fbx-App-Auth", session_token);
			fbxGetDownloads.requestLabel = Params.FBX_REQ_DOWNLOADS;
			return fbxGetDownloads;
		case Params.FBX_REQ_ADD_FILE:
			log.debug("Demande de création de la requete FBX_REQ_ADD_FILE");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_ADD_FILE, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_ADD_FILE, follower);
			}
			FbxAddFileDownload fbxAddFileDownload = new FbxAddFileDownload(
					Params.FBX_POST_ADD, Params.POST, follower);
			fbxAddFileDownload.setFlagParamFile(true);
			fbxAddFileDownload.setDownload_file_bytes(add_dwl_file_bytes);
			fbxAddFileDownload.setDownload_file_name(add_dwl_filename);
			fbxAddFileDownload.addHeader("X-Fbx-App-Auth", session_token);
			fbxAddFileDownload.requestLabel = Params.FBX_REQ_ADD_FILE;
			return fbxAddFileDownload;
		case Params.FBX_REQ_ADD_URL:
			log.debug("Demande de création de la requete FBX_REQ_ADD_URL");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_ADD_URL, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_ADD_URL, follower);
			}
			FbxAddUrlDownload fbxAddUrlDownload = new FbxAddUrlDownload(
					Params.FBX_POST_ADD, Params.POST, follower);
			fbxAddUrlDownload.setDownload_url(add_dwl_url);
			fbxAddUrlDownload.setFlagParamsString(true);
			fbxAddUrlDownload.addHeader("X-Fbx-App-Auth", session_token);
			fbxAddUrlDownload.requestLabel = Params.FBX_REQ_ADD_URL;
			return fbxAddUrlDownload;
		case Params.FBX_REQ_DOWNLOAD:
			log.debug("Demande de création de la requete FBX_REQ_DOWNLOAD");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_DOWNLOAD, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_DOWNLOAD, follower);
			}
			FbxGetDownload fbxGetDownnload = new FbxGetDownload(
					Params.FBX_GET_DOWNLOADS + String.valueOf(dwl_id),
					Params.GET, follower);
			fbxGetDownnload.addHeader("X-Fbx-App-Auth", session_token);
			fbxGetDownnload.setDwl_id(dwl_id);
			fbxGetDownnload.requestLabel = Params.FBX_REQ_DOWNLOAD;
			return fbxGetDownnload;
		case Params.FBX_REQ_DOWNLOAD_FILES:
			log.debug("Demande de création de la requete FBX_REQ_DOWNLOAD_FILES");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_DOWNLOAD_FILES, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_DOWNLOAD_FILES, follower);
			}
			FbxGetDownloadFiles fbxGetDownloadFiles = new FbxGetDownloadFiles(
					Params.FBX_GET_DOWNLOADS + String.valueOf(dwl_id) + "/files",
					Params.GET, follower);
			fbxGetDownloadFiles.addHeader("X-Fbx-App-Auth", session_token);
			fbxGetDownloadFiles.setDwl_id(dwl_id);
			fbxGetDownloadFiles.requestLabel = Params.FBX_REQ_DOWNLOAD_FILES;
			return fbxGetDownloadFiles;
		case Params.FBX_REQ_DOWNLOAD_TRACKERS:
			log.debug("Demande de création de la requete FBX_REQ_DOWNLOAD_TRACKERS");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_DOWNLOAD_TRACKERS, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_DOWNLOAD_TRACKERS, follower);
			}
			FbxGetDownloadTrackers fbxGetDownloadTrackers = new FbxGetDownloadTrackers(
					Params.FBX_GET_DOWNLOADS + String.valueOf(dwl_id) + "/trackers",
					Params.GET, follower);
			fbxGetDownloadTrackers.addHeader("X-Fbx-App-Auth", session_token);
			fbxGetDownloadTrackers.setDwl_id(dwl_id);
			fbxGetDownloadTrackers.requestLabel = Params.FBX_REQ_DOWNLOAD_TRACKERS;
			return fbxGetDownloadTrackers;
		case Params.FBX_REQ_CONFIG:
			log.debug("Demande de création de la requete FBX_REQ_CONFIG");
			if (!"true".equals(logged_in)) {
				if (app_token == null || app_token.isEmpty()) {
					log.debug("Remplacement pour la requete FBX_REQ_AUTHORIZE");
					return createRequest(Params.FBX_REQ_AUTHORIZE,
							Params.FBX_REQ_CONFIG, follower);
				}
				log.debug("Remplacement pour la requete FBX_REQ_LOGIN");
				return createRequest(Params.FBX_REQ_LOGIN,
						Params.FBX_REQ_CONFIG, follower);
			}
			FbxGetConfig fbxGetConfig = new FbxGetConfig(
					Params.FBX_GET_CONFIG, Params.GET, follower);
			fbxGetConfig.addHeader("X-Fbx-App-Auth", session_token);
			fbxGetConfig.requestLabel = Params.FBX_REQ_CONFIG;
			return fbxGetConfig;
		case Params.FBX_REQ_LOGOUT:
			FbxLogout fbxLogout = new FbxLogout(Params.FBX_POST_LOGOUT,
					Params.POST, follower);
			fbxLogout.addHeader("X-Fbx-App-Auth", session_token);
			fbxLogout.requestLabel = Params.FBX_REQ_LOGOUT;
			return fbxLogout;
		default:
			return null;
		}
	}

	private static List<Download> mapDownloads(JSONArray jsonDownloads) {
		List<Download> downloads = new ArrayList<Download>();
		for (int indexDwl = 0; indexDwl < jsonDownloads.length(); indexDwl++) {
			try {
				Object dwl = jsonDownloads.get(indexDwl);
				if (dwl instanceof JSONObject) {
					downloads.add(new Download((JSONObject) dwl));
				}
			} catch (JSONException e) {
				log.trace("Erreur : " + e.getMessage());
			}
		}
		return downloads;
	}

	public static boolean checkGlobalPrefs(AbstractActivity follower) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(follower.getBaseContext());
		boolean errorParams = false;

        TinyDB myDB = new TinyDB(follower);
		String ip = "";
		String port = "unkwnown";

		if (!settings.contains("pref_ip_public_freebox") && settings.contains("pref_port_public_freebox"))
			errorParams = true;
		if (!errorParams) {
            ip = myDB.getString("pref_ip_public_freebox");
//			ip = settings.getString("pref_ip_public_freebox", "");
			if (ip.isEmpty())
				errorParams = true;
			Params.FREEBOX_IP = ip;
			if (settings.contains("pref_port_public_freebox")) {
				port = settings.getString("pref_port_public_freebox", "80");
				Params.FREEBOX_PORT = port;
			}
			Params.reinitFreebox();
			if (Params.FBX_GET_LOGIN == null)
				errorParams = true;
		}
		return errorParams;
	}

	public static void delOldPrefs(Follower follower) {
		log.info("delOldPrefs");
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(follower.getContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("track_id");
		editor.remove("logged_in");
		editor.remove("session_token");
		editor.remove("challenge");
		editor.remove("password_sha");
//		editor.remove("add_dwl_url");
//		editor.remove("add_dwl_urlid");
//		editor.remove("add_dwl_file");
//		editor.remove("add_dwl_fileid");
//		editor.remove("add_dwl_filename");
		editor.apply();
	}
}
