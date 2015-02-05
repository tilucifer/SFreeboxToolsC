package fr.scaron.sfreeboxtools.connect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import fr.scaron.sfreeboxtools.contexte.*;
import fr.scaron.sfreeboxtools.model.FbxAddFileDownload;
import fr.scaron.sfreeboxtools.model.HttpRaster;

public class HttpConnect {

	public static Logger log = LoggerFactory.getLogger(HttpConnect.class);
	private static HttpConnect instance;

	private AsyncHttpClient client;
	private PersistentCookieStore myCookieStore;
	private Context context;

	public static HttpConnect getInstance() {
		return instance;
	}

	public static void setInstance(Context context) {
		if (instance == null) {
			instance = new HttpConnect(context);
		}
	}

	public HttpConnect(Context context) {
		client = new AsyncHttpClient();
		client.setMaxRetriesAndTimeout(2, 10);
		myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		this.context = context;

	}

	public void execute(final HttpRaster raster) {

		log.debug("Execute request of type  " + raster.getClass().getName());

		String[] allowedTypes = new String[] { "application/x-bittorrent",
				"image/jpeg", "image/gif", "image/png", "image/bmp",
				"image/tiff", "image/x-icon" };
		BinaryHttpResponseHandler handlerBin = new BinaryHttpResponseHandler(
				allowedTypes) {
			@Override
			public void onSuccess(byte[] bytes) {
				log.debug("File response is null ? : " + (bytes == null));
				log.trace("File response : " + bytes);
				raster.setFlagResponseFile(true);
				raster.setFlagResponseError(false);
				raster.setFlagResponseJson(false);
				raster.setResponseFile(bytes);
				raster.notifyFollowers();
			}

			@Override
			public void onFailure(int code, Header[] headers, byte[] bytes,
					Throwable error) {
				log.error("onfailure BinaryHttpResponseHandler : code = "
						+ code + " error = " + error.getMessage());
				gestionErreur(raster, code, headers, bytes, error);
			}
		};

		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int code, Header[] headers, byte[] bytes,
					Throwable error) {
				log.error("onfailure AsyncHttpResponseHandler : code = " + code
						+ " error = " + error.getMessage());
				gestionErreur(raster, code, headers, bytes, error);
			}

			@Override
			public void onSuccess(String response) {

				log.debug("Getting response of type  "
						+ raster.getClass().getName());
//				try {
					try {
						JSONObject jsonResponse = new JSONObject(response);
						raster.setResponseJson(jsonResponse);
						raster.setFlagResponseJson(true);
						if (response.length() > 500) {
							log.info("Reponse JSON tronquée: "
									+ response.substring(0, 499));
						} else {
							log.info("Reponse JSON : " + response);
						}
						log.trace("Reponse JSON complète: " + response);
					} catch (JSONException e) {
						log.debug("Reponse HTTP : string (Erreur json) "
								+ e.getMessage());
//						raster.setResponse("Erreur:" + e.getMessage());
//						raster.setFlagResponseError(true);
					} catch (Throwable e) {
						log.debug("Reponse HTTP : (Erreur) " + e.getMessage());
						raster.setResponse("Erreur:" + e.getMessage());
						raster.setFlagResponseError(true);
					}

					if (!raster.isFlagResponseJson()) {
						log.info("Reponse HTTP sous forme de page html");
						if (response.length() > 501) {
							// log.info("Reponse HTTP sous forme de page html");
							log.debug("Reponse HTTP tronquée: "
									+ response.substring(0, 500));
						} else {
							log.debug("Reponse HTTP : " + response);
						}
						raster.setResponse(response);
					}
//				} catch (Throwable e) {
//					log.debug("Reponse HTTP : (Erreur) " + e.getMessage());
//					raster.setResponse("Erreur:" + e.getMessage());
//					raster.setFlagResponseError(true);
//				}
				raster.notifyFollowers();
			}
		};

		Map<String, String> headers = raster.getHeaders();
		if (!headers.isEmpty()) {
			Set<String> headerKeys = headers.keySet();
			for (String key : headerKeys) {
				client.addHeader(key, headers.get(key));

				log.debug("addHeader HTTP : "+key+" = "+headers.get(key));
			}
		}
		switch (raster.getMethod()) {
		case Params.PUT:
			break;
		case Params.DELETE:
			break;
		case Params.ADD:

			break;
		case Params.GET:
			if (raster.getCharset() != null && !raster.getCharset().isEmpty()) {
				handler.setCharset(raster.getCharset());
			}
			if (raster.isFlagParamsString()) {
				String url = raster.getUrl();
				String params = raster.getParamsString();
				if (params != null && !params.isEmpty()) {
					url = url + "?" + raster.getParamsString();
				}
				if (raster.isFlagResponseFile()) {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", get file, params : "
							+ raster.getParamsString());
					client.get(url, handlerBin);
				} else {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", get, params : " + raster.getParamsString());
					client.get(url, handler);
				}
			} else {
				if (raster.isFlagResponseFile()) {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", get file, standard");
					client.get(raster.getUrl(), handlerBin);
				} else {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", get, standard");
					client.get(raster.getUrl(), handler);
				}
			}
			break;
		case Params.POST:
			if (raster.isFlagParamsJson()) {
				try {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", post, JSON : "
							+ raster.getParamsJson().toString());
					client.post(context, raster.getUrl(), new StringEntity(
							raster.getParamsJson().toString()),
							"application/json", handler);
				} catch (UnsupportedEncodingException e) {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", post, JSON : impossible à envoyer");
					client.post(raster.getUrl(), handler);
				}
			} else if (raster.isFlagParamFile()
					&& raster instanceof FbxAddFileDownload) {
				FbxAddFileDownload fbxAddFileDownload = (FbxAddFileDownload) raster;
				if (fbxAddFileDownload.getDownload_file_bytes() == null) {
					log.error("Demande d'ajout du fichier torrent impossible car le fichier est vide !");
					raster.setResponse("Erreur: ajout du fichier torrent impossible car le fichier est vide !");
					raster.setFlagResponseError(true);
				} else {
					log.debug("Appel HTTP, url : "
							+ fbxAddFileDownload.getUrl() + ", post, FILE : "
							+ fbxAddFileDownload.getDownload_file_name());
					RequestParams params = new RequestParams();
					params.put("download_file", new ByteArrayInputStream(
							fbxAddFileDownload.getDownload_file_bytes()),
							fbxAddFileDownload.getDownload_file_name());
					client.post(context, raster.getUrl(), params, handler);
				}
			} else {
				try {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", post, standard : " + raster.getParamsString());
					if (raster.getParamsString() == null
							|| raster.getParamsString().isEmpty()) {
						client.post(raster.getUrl(), handler);
					}
					client.post(context, raster.getUrl(), new StringEntity(
							raster.getParamsString()),
							"application/x-www-form-urlencoded", handler);
				} catch (UnsupportedEncodingException e) {
					log.debug("Appel HTTP, url : " + raster.getUrl()
							+ ", post, JSON : impossible à envoyer");
					client.post(raster.getUrl(), handler);
				}
			}
			break;
		default:
			log.debug("Appel HTTP (default), url : " + raster.getUrl()
					+ ", get, standard");
			client.get(raster.getUrl(), handler);
			break;
		}
	}

	private static void gestionErreur(HttpRaster raster, int code,
			Header[] headers, byte[] bytes, Throwable error) {
		log.debug("Gestion d'erreur pour " + raster.getClass().getName());
		raster.setFlagResponseError(true);
		raster.setErrorException(error);
		if (error instanceof HttpResponseException) {
			HttpResponseException ex = (HttpResponseException) error;
			int statusCode = ex.getStatusCode();
			String messageReponse = "Reponse HTTP : (Erreur) " + statusCode
					+ ":" + ex + "[" + ex.getMessage() + "]\nFlux:" + bytes
					+ "\nHeaders:" + headers;
			if (ex.getCause() != null) {
				messageReponse = messageReponse + "\nCause:"
						+ ex.getCause().getMessage();
			}
			log.error("HttpResponseException : (Erreur) " + statusCode + ":"
					+ messageReponse);
			raster.setFlagResponseError(true);
//			raster.setErrorCode("" + statusCode);
			raster.setErrorCode("http_" + statusCode);
			raster.setResponse(messageReponse);
			raster.notifyFollowers();
		} else if (error instanceof ConnectTimeoutException) {
			ConnectTimeoutException ex = (ConnectTimeoutException) error;
			int statusCode = 118;
			String messageReponse = "Reponse HTTP : (Erreur) " + statusCode
					+ ":" + ex + "[" + ex.getMessage() + "]\nFlux:" + bytes
					+ "\nHeaders:" + headers;
			if (ex.getCause() != null) {
				messageReponse = messageReponse + "\nCause:"
						+ ex.getCause().getMessage();
			}
			log.error("ConnectTimeoutException : (Erreur) " + statusCode + ":"
					+ messageReponse);
			raster.setFlagResponseError(true);
			raster.setErrorCode("" + statusCode);
			raster.setResponse(messageReponse);
			raster.notifyFollowers();
		} else if (error instanceof ClientProtocolException) {
			ClientProtocolException ex = (ClientProtocolException) error;
			int statusCode = 118;
			String messageReponse = "Reponse HTTP : (Erreur) " + statusCode
					+ ":" + ex + "[" + ex.getMessage() + "]\nFlux:" + bytes
					+ "\nHeaders:" + headers;
			if (ex.getCause() != null) {
				messageReponse = messageReponse + "\nCause:"
						+ ex.getCause().getMessage();
			}
			log.error("ClientProtocolException : (Erreur) " + statusCode + ":"
					+ messageReponse);
			raster.setFlagResponseError(true);
			raster.setErrorCode("" + statusCode);
			raster.setResponse(messageReponse);
			raster.notifyFollowers();
		} else {
			raster.setFlagResponseError(true);
			raster.setErrorCode("" + code);
			log.error("Reponse HTTP : (Erreur) " + code + ":" + error + "["
					+ error.getMessage() + "]\nFlux:" + bytes);
			String messageReponse = "Reponse HTTP : (Erreur) " + code + ":"
					+ error + "[" + error.getMessage() + "]\nFlux:" + bytes
					+ "\nHeaders:" + headers;
			if (error.getCause() != null) {
				messageReponse = messageReponse + "\nCause:"
						+ error.getCause().getMessage();
			}
			raster.setResponse(messageReponse);
			raster.notifyFollowers();
		}
	}

	public static File IStoFile(InputStream inputStream, String fileName) {
		try {
			File f = new File(fileName);
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			inputStream.close();
			return f;
		} catch (IOException e) {
			log.debug("Impossible de cr\351er le fichier torrent temporaire");
			log.error(Log.getStackTraceString(e));
			return null;
		}
	}

}
